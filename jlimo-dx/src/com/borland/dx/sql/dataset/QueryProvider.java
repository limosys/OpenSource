//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryProvider.java,v 7.1.2.2 2004/05/05 00:28:41 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;

import java.util.Vector;
import java.sql.*;
import java.math.*;
import java.io.*;

/**
 * The <CODE>QueryProvider</CODE> component is used to provide data to
 * a {@link com.borland.dx.dataset.DataSet} <CODE>DataSet</CODE></A>
 *  by running a query through JDBC. This component is also a place holder
 * for static methods for executing statements with parameters
 *  (see {@link com.borland.dx.sql.dataset.Database}
 * <CODE>executeStatement()</CODE></A>).

 */
public class QueryProvider extends JdbcProvider {

  /**
   * Creates a QueryProvider object.
   */
  public QueryProvider() {
    query = new PreparedStmt();
  }

  // Simple Query execution:
  //
  /**
   * Executes the SQL statement specified as statement and passes values to the
   *  parameter markers in the SQL statement (all named or unnamed) with values
   * from the ReadWriteRow. Use this method to execute SQL statements with parameters
   *  that do not yield a ResultSet. <p>
   * For example: QueryProvider.executeStatement(db1,"INSERT INTO CUST VALUES (?,?)", paramRow);
   * @param database Database
   * @param statement String
   * @param parameters ReadWriteRow
   *
   */
  public static final int executeStatement(Database database, String statement, ReadWriteRow parameters)
    /*-throws DataSetException-*/
  {
    ReadWriteRow[] rows = parameters == null ? null : new ReadWriteRow[]{parameters};
    return executeStatement(database, statement, rows);
  }

  /**
   * Similar to executeStatement(com.borland.dx.sql.dataset.Database, java.lang.String,
   * com.borland.dx.dataset.ReadWriteRow), but allows for an array of query parameters.
   * @param database DataBase
   * @param statement String
   * @param parameters ReadWriteRow
   *
   */
  public static final int executeStatement(Database database, String statement, ReadWriteRow[] parameters)
    /*-throws DataSetException-*/
  {
    int updateCount = -1;
    try {
      if (database == null || statement == null)
        DataSetException.badQueryProperties();

      PreparedStmt query = new PreparedStmt();
      query.resetState(database,statement,null,parameters);
      updateCount = query.executeUpdate();
      query.closeStatement();
    }
    catch (SQLException ex) {
      DataSetException.SQLException(ex);
    }
    return updateCount;
  }

  public final void setQuery(QueryDescriptor queryDescriptor)
    /*-throws DataSetException-*/
  {
    if (dataSet != null)
      ProviderHelp.failIfOpen(dataSet);
    this.queryDescriptor = queryDescriptor;
    setPropertyChanged(true);
  }

  public final QueryDescriptor getQuery() {
    return queryDescriptor;
  }

  /**
   * Returns the query String associated with this QueryDataSet.
   *  This property is a short cut to the queryString property of the QueryDescriptor object.
   * @param sds StorageDataSet
   *
   */
  public final String getQueryString(StorageDataSet sds) {
    if (generatedQueryString != null)
      return generatedQueryString;
    if (queryDescriptor != null)
      return queryDescriptor.getQueryString();
    return null;
  }

  void cacheDataSet(StorageDataSet dataSet) /*-throws DataSetException-*/ {
    super.cacheDataSet(dataSet);
    if (queryDescriptor == null || queryDescriptor.getDatabase() == null || queryDescriptor.getStrippedQueryString() == null)
      DataSetException.badQueryProperties();
    setQueryDescriptor(queryDescriptor);
  }

  ResultSet provideResultSet() /*-throws DataSetException-*/ throws SQLException {
    if (queryDescriptor == null || queryDescriptor.getDatabase() == null || queryDescriptor.getStrippedQueryString() == null)
      DataSetException.badQueryProperties();
    ResultSet resultSet = query.executeQuery();
    if (resultSet == null)
      DataSetException.noResultSet();
    if (!queryAnalyzed)
      resultSet = analyzeQuery(resultSet);
    return resultSet;
  }

  void providerFailed(Exception ex) /*-throws DataSetException-*/ {
    DataSetException.queryFailed(ex);
  }

  void closeResultSet(ResultSet resultSet) /*-throws DataSetException-*/ throws SQLException {
    query.closeResultSet(resultSet);
  }

  void resetState() /*-throws DataSetException-*/ throws SQLException {
    super.resetState();
    if (queryDescriptor == null || dataSet == null)
      query.resetState(null, null, null, (ReadWriteRow[])null);
    else
      query.resetState(queryDescriptor.getDatabase(), queryDescriptor.getStrippedQueryString(), dataSet.getMasterLink(), queryDescriptor.getParameterRow() );
    queryAnalyzed = false;
    updatable = false;
    generatedQueryString = null;
  }

  ResultSet analyzeQuery(ResultSet resultSet) /*-throws DataSetException-*/ throws SQLException {
    queryAnalyzer = null;
    queryAnalyzed = true;
    int metaDataUpdate = dataSet.getMetaDataUpdate();
    ProviderHelp.setMetaDataMissing(dataSet, dataSet.hasRowIds());

    if ((metaDataUpdate & (MetaDataUpdate.TABLENAME + MetaDataUpdate.ROWID)) == 0)
      return resultSet;

    ResultSetMetaData metaData = resultSet.getMetaData();
    queryAnalyzer = new UniqueQueryAnalyzer(queryDescriptor.getDatabase(), queryDescriptor.getStrippedQueryString(), metaData);

    if ((metaDataUpdate & MetaDataUpdate.ROWID) == 0)
      queryAnalyzer.analyzeTableName();
    else {
      queryAnalyzer.analyze();

      updatable = queryAnalyzer.isUpdatable();
      if (!updatable) {
        Vector rowIdList = rowIdList = queryAnalyzer.getBestRowId();
        if ((rowIdList != null) && (rowIdList.size() > 0)) {
          try {
            // Send the recommended row id columns to the query object to regenerate the query.
            queryAnalyzer.setBestRowId(rowIdList);

            // Get the regenerated query (with row id columns added)
            generatedQueryString = queryAnalyzer.getGeneratedQuery();

            // Re execute the query with the added rowID columns
            query.closeResultSet(resultSet);
            query.resetState(queryDescriptor.getDatabase(), generatedQueryString, dataSet.getMasterLink(), queryDescriptor.getParameterRow() );
            resultSet = query.executeQuery();

            updatable = true;
          }
          catch (Exception ex) {
            // If there was an error trying to make the query updatable, re execute the original query.
            DiagnosticJLimo.printStackTrace(ex);
            query.resetState(queryDescriptor.getDatabase(), queryDescriptor.getStrippedQueryString(), dataSet.getMasterLink(), queryDescriptor.getParameterRow() );
            resultSet = query.executeQuery();
          }
        }
      }
      ProviderHelp.setMetaDataMissing(dataSet, updatable);
    }

    return resultSet;
  }
  /*
      Unfortunately Oracle drivers impose annoying restrictions when retrieving
      LONG data types.  There is a notion of "Stream" that is used to provide the
      data from these data types.  The Stream of the current row in the ResultSet
      is closed if another Statement is executed.  It appears that asking the Connection
      to provide best row id or primary key information must also execute a statement
      against the data dictionary because these operations also cause the stream
      for the current row to be closed.  So after best row id metadata discovery,
      reexecute the query.  Note that another code path will reexecute the query
      if row id columns needed to be added to the query.

      The following JDBC code shows the problem:

    java.sql.Statement state = con.createStatement();

    try {
      state.executeUpdate("DROP TABLE TABLE1");
    }
    catch(Exception ex) {
    }
    state.executeUpdate("CREATE TABLE Table1 (Fld1 varchar(20),Fld0  LONG,Fld2 DECIMAL(10,2), PRIMARY KEY (Fld1))");
    state.close();
    PreparedStatement pState = con.prepareStatement("insert into Table1 values(?,?,?)");
    for (int i=0; i<2; i++) {
     pState.setString(1,"data"+i);
     pState.setString(2,"Longdata"+i);
     pState.setInt(3,i);
     pState.executeUpdate();
   }
    pState.close();
    pState = bugDB.getJdbcConnection().prepareStatement("SELECT * FROM Table1");
    java.sql.ResultSet result = pState.executeQuery();
    ResultSetMetaData md = result.getMetaData();
//      ResultSet ri = bugDB.getMetaData().getBestRowIdentifier(null, null, "TABLE1",bugDB.getMetaData().bestRowTransaction,true);
    ResultSet pk = bugDB.getMetaData().getPrimaryKeys(null, null, "TABLE1");

    while (result.next()) {
      System.out.println(result.getString(1));
      result.getCharacterStream(2);
    }
    result.close();

  */
  private boolean needsExecute()
    throws SQLException
  {
    if (updatable && (queryDescriptor.getDatabase().getSQLDialect() & RuntimeMetaData.DB_ORACLE) != 0) {
      int count = dataSet.getColumnCount();
      int sqlType;
      for (int ordinal = 0; ordinal < count; ++ordinal) {
        sqlType = dataSet.getColumn(ordinal).getSqlType();
        if (sqlType == java.sql.Types.LONGVARCHAR || sqlType == java.sql.Types.LONGVARBINARY)
          return true;
      }
    }
    return false;
  }

  ResultSet retryQuery()
    throws java.sql.SQLException
  {
    if (needsExecute()) {
      return provideResultSet();
    }
    return null;
  }

  void adjustColumnInfo(Column[] metaDataColumns) /*-throws DataSetException-*/ {
    if (queryAnalyzer != null && queryAnalyzer.couldParse()) {

      // Properties directly on the StorageDataSet:
      if ((dataSet.getMetaDataUpdate() & MetaDataUpdate.TABLENAME) != 0) {
        String uniqueTableName  = queryAnalyzer.getTableName();
        String uniqueSchemaName = queryAnalyzer.getSchemaName();
        ConnectionDescriptor cdesc = queryDescriptor.getDatabase().getConnection();
        String userName = (cdesc != null ? cdesc.getUserName() : null);

        dataSet.setTableName(uniqueTableName);
        if (uniqueSchemaName != null && !uniqueSchemaName.equals(userName))
          dataSet.setSchemaName(uniqueSchemaName);

        dataSet.setResolveOrder(queryAnalyzer.getResolveOrder());
      }

      if ((dataSet.getMetaDataUpdate() & MetaDataUpdate.ROWID) != 0) {
        dataSet.setAllRowIds(false);
        ProviderHelp.setMetaDataMissing(dataSet, updatable);
      }

      // Properties for adjusting the metaDataColumns:
      SQLElement[] columns = queryAnalyzer.getAllColumns(updatable);
      DiagnosticJLimo.check(columns.length == metaDataColumns.length);
      if (columns.length != metaDataColumns.length)
        return;  // Emergency exit !!!!!

      if ((dataSet.getMetaDataUpdate() & MetaDataUpdate.TABLENAME) != 0) {
        for (int ordinal=0; ordinal<columns.length; ordinal++) {
          SQLElement column  = columns[ordinal];
          Column     columnDS= metaDataColumns[ordinal];
          if (column == null) { // Expressions and Functions or aggregates
            columnDS.setSearchable(false);
            continue;
          }
          SQLElement table   = queryAnalyzer.tableFromColumn(column);
          columnDS.setServerColumnName(column.getName());
          if (column.isUnquotedInternelRow())
            columnDS.setResolvable(false);
          if (table != null) {
            columnDS.setTableName(table.getName());
            columnDS.setSchemaName(table.getPrefixName());
          }
        }
      }

      if (updatable) {
        int[] rowIds = queryAnalyzer.getOrdinalsOfRowIds();
        for (int index=0; index<rowIds.length; index++)
          metaDataColumns[rowIds[index]].setRowId(true);
        Vector added = queryAnalyzer.getAddedColumns();
        int   nAdded = added != null ? added.size() : 0;
        for (int index=0; index<nAdded; index++) {
          metaDataColumns[index].setHidden(true);
          metaDataColumns[index].setResolvable(false);
        }
      }
    }
  }

  /**
   * Validates the masterLink property. When the MasterLinkDescriptor's
   * fetchAsNeeded property is enabled (true), this method checks if there is a
   *  WHERE clause in the query. If no WHERE clause is specified, a DataSetException
   *  is thrown.
   * @param dataSet StorageDataSet
   * @param masterLink MasterLinkDescriptor
   */
  public void checkMasterLink(StorageDataSet dataSet, MasterLinkDescriptor masterLink)
    /*-throws DataSetException-*/
  {
    // Yes, this is a very lightweight check, but it can help catch the really
    // dumb mistake of no where clause when doing delayed fetching.  I am afraid to
    // do better checking since this is invoked at "runtime".  Don't want it to be slow
    // or bring in a bunch of query parsing classes.
    //
    if (masterLink != null && queryDescriptor != null && masterLink.isFetchAsNeeded()) {
      String query  = queryDescriptor.getStrippedQueryString();
      if (query != null && query.length() > 0 && dataSet != null) {
        if (query.indexOf("where") < 0 && query.indexOf("WHERE") < 0)           //NORES
          DataSetException.noWhereClause(dataSet);
      }
    }
  }

  public ReadWriteRow getParameterRow() {
    if (queryDescriptor != null)
      return queryDescriptor.getParameterRow();
    else
      return null;
  }

  public void setParameterRow(ReadWriteRow value) {
    if (queryDescriptor != null)
      queryDescriptor.setParameterRow(value);
  }

  private transient PreparedStmt query;
  private           QueryDescriptor queryDescriptor;
  private transient UniqueQueryAnalyzer queryAnalyzer;
  private transient String generatedQueryString;
  private transient boolean queryAnalyzed;
  private transient boolean updatable;
  private static final long serialVersionUID = 1L;
}


