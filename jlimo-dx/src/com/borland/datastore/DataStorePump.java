//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/datastore/DataStorePump.java,v 7.3 2003/11/13 21:11:37 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.datastore;

import com.borland.dx.sql.dataset.*;
import com.borland.datastore.*;
import com.borland.datastore.cons.*;
import com.borland.sql.SQLAdapter;
import java.sql.*;
import java.util.*;
import com.borland.dx.dataset.*;
//import com.borland.dbtools.jdbcx.common.*;

/**
 * This component can be used to import table definitions and data from an external database
 * into a JDataStore database.  The external database can be any database that
 * has a JDBC driver.  There are options for filtering which tables are pumped
 * into the JDataStore database.  There are options for forcing column, index, and
 * table identifiers as upper case.  The connection information needed to connect
 * to the external database's JDBC driver is stored in the /SYS/CONNECTIONS table.
 * The queries used to pump data from the external database to the JDataStore database
 * are stored in the /SYS/QUERIES table.
*/

public class DataStorePump implements com.borland.dx.dataset.Designable {


  public DataStorePump() {
    tablePattern          = "%"; //NORES
//!JOAL:bug141373 Don't get the indexes up front, wait and get only the indexes for the selected tables
//!  importIndexes         = true;
    enableSave            = true;
    enableRefresh         = true;
  }

  /**
   * Call this to use the DataStorePump.  Must be called after
   * setting database and dataStore properties.
   */

  public void open() /*-throws DataSetException-*/  {

    if (!isOpen) {
      if ( database == null ) {
        throw new RuntimeException(Res.bundle.getString(ResIndex.DatabaseNotSet));
      }
      else if ( !database.isOpen() ) {
        throw new RuntimeException(Res.bundle.getString(ResIndex.DatabaseNotOpen));
      }
      else if ( con == null ) {
        throw new RuntimeException(Res.bundle.getString(ResIndex.DataStoreNotSet));
      }

      if (recordQueries) {
        queries     = getQueriesTable();
        queries.setStore(con);
        queries.open();
        connections = getConnectionsTable();
        connections.setStore(con);
        connections.open();
        if (connections.getNeedsRestructure())
          connections.restructure();
      }

      isOpen = true;
    }
  }

  /**
   * Call when DataStorePump is no longer needed.
   */
  public void close()
    /*-throws DataSetException-*/
  {
    if (isOpen) {
      isOpen = false;
      if (queries != null)
        queries.close();
      if (connections != null)
        connections.close();
    }
  }

  /**
   * Creates SYS/QUERIES table used for refreshing table data from another database
   * and saving table data back to another database.
   * @see DataStoreSync component for more information on refreshing and saving
   * table data with another database.
   */
  public static TableDataSet getQueriesTable()
    /*-throws DataSetException-*/
  {
    Variant vBooleanTrue = new Variant(Variant.BOOLEAN);

    Column storeNameColumn = new Column();
    storeNameColumn.setCaption(Res.bundle.getString(ResIndex.TableName));
    storeNameColumn.setColumnName(SysQueries.STORE_NAME);
    storeNameColumn.setDataType(com.borland.dx.dataset.Variant.STRING);
//    storeNameColumn.setRequired(true);

    Column queryColumn = new Column();
    queryColumn.setCaption(Res.bundle.getString(ResIndex.SQLQuery));
    queryColumn.setColumnName(SysQueries.QUERY);
    queryColumn.setDataType(com.borland.dx.dataset.Variant.STRING);
//    queryColumn.setRequired(true);

    Column loadOptionColumn = new Column();
    loadOptionColumn.setCaption(Res.bundle.getString(ResIndex.LoadOption));
    loadOptionColumn.setColumnName(SysQueries.LOAD_OPTION);
    loadOptionColumn.setDataType(com.borland.dx.dataset.Variant.INT);
    loadOptionColumn.setDefault("0"); //NORES
//    loadOptionColumn.setRequired(true);

    Column connectionColumn = new Column();
    connectionColumn.setCaption(Res.bundle.getString(ResIndex.Connection));
    connectionColumn.setColumnName(SysQueries.CONNECTION);
    connectionColumn.setDataType(com.borland.dx.dataset.Variant.INT);

    Column enableRefreshColumn = new Column();
    enableRefreshColumn.setCaption(Res.bundle.getString(ResIndex.EnableRefreshOnMenu));
    enableRefreshColumn.setColumnName(SysQueries.ENABLE_REFRESH);
    enableRefreshColumn.setDataType(com.borland.dx.dataset.Variant.BOOLEAN);
    enableRefreshColumn.setDefaultValue(vBooleanTrue);

    Column enableSaveColumn = new Column();
    enableSaveColumn.setCaption(Res.bundle.getString(ResIndex.EnableSaveOnMenu));
    enableSaveColumn.setColumnName(SysQueries.ENABLE_SAVE);
    enableSaveColumn.setDataType(com.borland.dx.dataset.Variant.BOOLEAN);
    enableSaveColumn.setDefaultValue(vBooleanTrue);

    Column updateModeColumn = new Column();
    updateModeColumn.setCaption(Res.bundle.getString(ResIndex.UpdateMode));
    updateModeColumn.setColumnName(SysQueries.UPDATE_MODE);
    updateModeColumn.setDataType(com.borland.dx.dataset.Variant.INT);
    updateModeColumn.setDefault(Integer.toString(com.borland.dx.dataset.UpdateMode.ALL_COLUMNS));

    Column uppercaseColumnsColumn = new Column();
    uppercaseColumnsColumn.setCaption(Res.bundle.getString(ResIndex.UpperColsOnMenu));
    uppercaseColumnsColumn.setColumnName(SysQueries.UPPERCASE_COLUMNS);
    uppercaseColumnsColumn.setDataType(com.borland.dx.dataset.Variant.BOOLEAN);
    uppercaseColumnsColumn.setDefaultValue(vBooleanTrue);

//!    Column importIndicesColumn = new Column();
//!    importIndicesColumn.setCaption(Res.bundle.getString(ResIndex.ImportIndexOnMenu));
//!    importIndicesColumn.setColumnName(SysQueries.IMPORT_INDICES);
//!    importIndicesColumn.setDataType(com.borland.dx.dataset.Variant.BOOLEAN);
//!    importIndicesColumn.setDefaultValue(vBooleanTrue);
//!
//!    Column uppercaseIndicesColumn = new Column();
//!    uppercaseIndicesColumn.setCaption(Res.bundle.getString(ResIndex.UpperIndexOnMenu));
//!    uppercaseIndicesColumn.setColumnName(SysQueries.UPPERCASE_INDICES);
//!    uppercaseIndicesColumn.setDataType(com.borland.dx.dataset.Variant.BOOLEAN);
//!    uppercaseIndicesColumn.setDefaultValue(vBooleanTrue);

    TableDataSet queries = new TableDataSet();
    queries.setSort(new com.borland.dx.dataset.SortDescriptor(new String[] {SysQueries.STORE_NAME}, false, false));  //NORES
    queries.setStoreName(SysQueries.TABLE);
    queries.setColumns(new Column[] {storeNameColumn, queryColumn, loadOptionColumn, connectionColumn, enableRefreshColumn, enableSaveColumn, updateModeColumn, uppercaseColumnsColumn});
    queries.setResolvable(false);

    return queries;
  }

  /**
   * Creates SYS/CONNECTIONS table used for refreshing table data from another database
   * and saving table data back to another database.
   * @see DataStoreSync component for more information on refreshing and saving
   * table data with another database.
   */
  public static TableDataSet getConnectionsTable()
    /*-throws DataSetException-*/
  {
    Column connectionIdColumn = new Column();
    connectionIdColumn.setColumnName(SysConnections.ID);
    connectionIdColumn.setDataType(com.borland.dx.dataset.Variant.INT);
    connectionIdColumn.setVisible(com.borland.jb.util.TriStateProperty.FALSE);

    Column urlColumn = new Column();
    urlColumn.setCaption(Res.bundle.getString(ResIndex.URL));
    urlColumn.setColumnName(SysConnections.URL);
    urlColumn.setDataType(com.borland.dx.dataset.Variant.STRING);
//    urlColumn.setRequired(true);

    Column usernameColumn = new Column();
    usernameColumn.setCaption(Res.bundle.getString(ResIndex.UsernameCaption));
    usernameColumn.setColumnName(SysConnections.USER_NAME);
    usernameColumn.setDataType(com.borland.dx.dataset.Variant.STRING);
//    usernameColumn.setRequired(true);

    Column passwordColumn = new Column();
    passwordColumn.setCaption(Res.bundle.getString(ResIndex.PasswordCaption));  //RES Password
    passwordColumn.setColumnName(SysConnections.PASSWORD);
    //passwordColumn.setDisplayMask("'<" + "Password" + ">';0");
    //passwordColumn.setEditMask("*aaaaaaaaaaaaaaa*");
    passwordColumn.setDataType(com.borland.dx.dataset.Variant.STRING);
//    passwordColumn.setItemEditor(passwordFieldItemEditor);
//    passwordColumn.setRequired(true);

    Column driverColumn = new Column();
    driverColumn.setCaption(Res.bundle.getString(ResIndex.DriverName));
    driverColumn.setColumnName(SysConnections.DRIVER);
    driverColumn.setDataType(com.borland.dx.dataset.Variant.STRING);
//    driverColumn.setRequired(true);

    Column lastId = new Column();
    lastId.setAgg(new com.borland.dx.dataset.AggDescriptor((String[]) null, "ID", new com.borland.dx.dataset.MaxAggOperator())); //NORES
    lastId.setCalcType(com.borland.dx.dataset.CalcType.AGGREGATE);
    lastId.setColumnName(SysConnections.LAST_ID);
    lastId.setDataType(com.borland.dx.dataset.Variant.INT);
    lastId.setVisible(com.borland.jb.util.TriStateProperty.FALSE);

    Column propertiesColumn = new Column();
    propertiesColumn.setCaption(Res.bundle.getString(ResIndex.ExtendedPropsCaption));
    propertiesColumn.setColumnName(SysConnections.PROPERTIES);
    propertiesColumn.setDataType(com.borland.dx.dataset.Variant.STRING);

    TableDataSet connections  = new TableDataSet();
    connections.setColumns(new Column[] {connectionIdColumn, urlColumn, usernameColumn, passwordColumn, driverColumn, lastId, propertiesColumn});
    connections.setStoreName(SysConnections.TABLE);
    connections.setSort(new com.borland.dx.dataset.SortDescriptor(new String[] {SysConnections.URL, SysConnections.USER_NAME}, false, false));  //NORES
    connections.setResolvable(false);

    return connections;
  }


  /**
   * Builds a default select statement for a table with the given name from the
   * database specified by the Database property.
   */
  public String getSelectForTable(String catalogName, String schemaName, String tableName)
    /*-throws DataSetException-*/
  {
    open();
    tableName = database.makeTableIdentifier(catalogName, schemaName, tableName);
    return "select * from "+tableName;          //NORES
  }

  /**
   * Creates Array of TableDefs for all tables from Database whose name matches
   * the pattern specifications for catalog, schema and table names.
   */
  public TableDef [] getTableDefs()
    /*-throws DataSetException-*/
  {
    open();


    Vector v = new Vector();
    try {
      DatabaseMetaData metaData = database.getMetaData();
      ResultSet rs = metaData.getTables(catalogPattern, schemaPattern, tablePattern, new String[]{"TABLE"} ); //NORES
      if ( rs instanceof SQLAdapter ) {
        ((SQLAdapter) rs).adapt(SQLAdapter.RIGHT_TRIM_STRINGS, null);
      }
      TableDef td;
      while ( rs.next() ) {
        td = new TableDef();
        td.catalog              = rs.getString(1);
        td.schema               = rs.getString(2);
        td.table                = rs.getString(3);
        if (!td.table.equalsIgnoreCase(SysConnections.TABLE) && !td.table.equalsIgnoreCase(SysQueries.TABLE)) {
          td.storeName            = td.table;
          td.query                = getSelectForTable(td.catalog, td.schema, td.table);
          td.enableRefresh        = enableRefresh;
          td.enableSave           = enableSave;

          if (importIndexes)
            td.indexes = getIndexes(td);

          v.addElement(td);
        }
      }
    }
    catch (SQLException e) {
      DataStoreException.throwExceptionChain(e);
    }
    TableDef [] t = new TableDef[v.size()];
    v.copyInto(t);
    return t;
  }

  /**
   * Copy a table in the DataStore based on the TableDef specification.
   * Note that TableDefs can be modified to change identifiers casing or
   * remove secondary or primary index specifications.
   */
  public void copyTable(TableDef  td)
    /*-throws DataSetException-*/
  {
    open();
    String storeName = td.storeName;

    if (upperCaseTableNames)
      storeName = storeName.toUpperCase();

    if ( storeName == null || storeName.length() == 0 ) {
      throw new RuntimeException(Res.bundle.getString(ResIndex.StoreNameNotSet));
    }
    if (td.query == null || td.query.length() == 0 ) {
      throw new RuntimeException(Res.bundle.getString(ResIndex.SqlNotSet));
    }
    if ( con.tableExists(storeName) ) {
      try {
        con.deleteStream(storeName);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    QueryDataSet queryDataSet = new QueryDataSet();
    queryDataSet.setMaxDesignRows(-1);
    queryDataSet.setStore(con);
    queryDataSet.setStoreName(storeName);
    queryDataSet.setQuery(new QueryDescriptor(database, td.query));
    queryDataSet.setMetaDataUpdate(td.enableSave ? MetaDataUpdate.ALL : MetaDataUpdate.NONE);
    queryDataSet.setResolvable(td.enableSave);
    queryDataSet.open();

    SortDescriptor[] indexes = td.indexes;

    if (indexes != null) {
      int count = indexes.length;
      for (int index = 0; index < count; ++index) {
        queryDataSet.setSort(indexes[index]);
      }
    }

    queryDataSet.close();
    queryDataSet.closeProvider(true);

    if (upperCaseColumnNames)
      upperCaseColumnNames(queryDataSet);

    if (recordQueries) {
      int connectionId = findConnection();

      if ( connectionId != -1 ) {
        queries.insertRow(false);
        queries.setString(SysQueries.STORE_NAME, storeName);
        queries.setString(SysQueries.QUERY, td.query);
        queries.setInt(SysQueries.LOAD_OPTION, Load.ALL);
        queries.setInt(SysQueries.CONNECTION, connectionId);
        queries.setBoolean(SysQueries.ENABLE_REFRESH, td.enableRefresh);
        queries.setBoolean(SysQueries.ENABLE_SAVE, td.enableSave);
        queries.setBoolean(SysQueries.UPPERCASE_COLUMNS, upperCaseColumnNames);
        queries.post();
      }
    }
  }

  static final void upperCaseColumnNames(StorageDataSet table)
    /*-throws DataSetException-*/
  {
    boolean restructure = false;
    Column[] columns = table.getColumns();
    Column column;
    String name;
    for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
      column = columns[ordinal];
      name = column.getColumnName();
      if (!name.toUpperCase().equals(name)) {
        column.setServerColumnName(name);
        column.setColumnName(name.toUpperCase());
        restructure = true;
      }
    }
    if (restructure) {
      table.open();
      table.close();
    }
  }

  private int findConnection()
    /*-throws DataSetException-*/
  {
    int ret = -1;
    int maxId = 0;

    open();
    if (connections != null && recordQueries) {

      connections.first();

      ConnectionDescriptor connection = database.getConnection();

      while ( connections.inBounds() ) {
        if (  connection.getConnectionURL().equals(connections.getString(SysConnections.URL)) &&
              connection.getDriver().equals(connections.getString(SysConnections.DRIVER)) &&
              connection.getUserName().equals(connections.getString(SysConnections.USER_NAME)) &&
              connection.getPassword().equals(connections.getString(SysConnections.PASSWORD)) ) {

          ret = connections.getInt(SysConnections.ID);
          break;

        }
        else {
          maxId = Math.max(maxId, connections.getInt(SysConnections.ID));
        }
        connections.next();
      }
      if ( ret == -1 ) {
        ret = ++maxId;
        connections.insertRow(false);
        connections.setInt(SysConnections.ID, maxId);
        connections.setString(SysConnections.URL, connection.getConnectionURL());
        connections.setString(SysConnections.DRIVER, connection.getDriver());
        connections.setString(SysConnections.USER_NAME, connection.getUserName());
        connections.setString(SysConnections.PASSWORD, connection.getPassword());
        connections.setString(SysConnections.PROPERTIES, getStringifiedProperties(connection.getProperties()));
        connections.post();
      }
    }
    return ret;
  }

  /**
   * We store the java.util.Properties of a Connection as a string.  This
   * function returns the string that we use to represent a Properties object.
   */

   static String getStringifiedProperties(Properties p) {
    String ret = p == null ? "" : p.toString(); //NORES
    // String comes back from Propeties.toString in curly braces, e.g., {One=1,Two=2}
    // Strip out those curly braces.  Ideally would just keep them, but
    // we didn't use to keep them, so to be backwards compatible, strip them.
    ret = ret.length() < 3 ? "" : ret.substring(1, ret.length() - 1);
    return ret;
  }


  /**
   * Get an Array of SortDescriptors that specify all secondary and primary indexes
   * for the table specfied by the TableDef.
   */
  public SortDescriptor[] getIndexes(TableDef td)
    /*-throws DataSetException-*/
  {
    open();
    DatabaseMetaData metaData = database.getMetaData();
    Vector indexes = new Vector();
    try {
      String catalog = td.catalog != null && td.catalog.length() > 0 ? td.catalog : null;
      String schema = td.schema != null && td.schema.length() > 0 ? td.schema : null;
      ResultSet rs = metaData.getIndexInfo(catalog, schema, td.table, false, true);
      if ( rs instanceof SQLAdapter ) {
        ((SQLAdapter) rs).adapt(SQLAdapter.RIGHT_TRIM_STRINGS, null);
      }
      Vector columns = new Vector();
      Vector orders = new Vector();
      String curIndexName = null;
      boolean curUnique = false;
      String indexName;
      boolean unique;
      String  order;
      String  column;
      short ordinal;
      short   type;
      while ( rs.next() ) {
        // Note that some drivers require you to read the columns
        // in sequential order, i.e., you must read column 4 before you read
        // column 6; hence this convoluted code.
        //
        unique      = !rs.getBoolean(4);
        indexName   = rs.getString(6);
        type        = rs.getShort(7);
        ordinal     = rs.getShort(8);
        column      = rs.getString(9);
        order       = rs.getString(10);

        if ( order == null )
          order = "A"; //NORES

        if ( curIndexName == null ) {
          curIndexName  = indexName;
          curUnique     = unique;
        }

        if ( type != DatabaseMetaData.tableIndexStatistic ) {
          if (ordinal == 1 && columns.size() > 0) {

            indexes.addElement(makeSort(curIndexName, columns, orders, curUnique));

            columns.removeAllElements();
            orders.removeAllElements();
            curUnique     = unique;
            curIndexName  = indexName;
          }
          columns.addElement(rs.getString(9));
          orders.addElement(order);
        }
      }

      if (columns.size() > 0)
        indexes.addElement(makeSort(curIndexName, columns, orders, curUnique));
      rs.close();
    }
    catch (SQLException e) {
//      Runtime.getRuntime().runFinalization();
//      Runtime.getRuntime().gc();
      com.borland.jb.util.DiagnosticJLimo.println("ex:  "+e.getMessage());
      // Changed 7/10/2000 by Charles to not throw Exception.
      // When connecting to a particular Oracle database, I was getting
      // "insufficient privileges" on a particular table.  The exception
      // interrupted everything.  So instead of throwing an exception, just
      // swallow it and say there is no index for the table.
      return new SortDescriptor[0];
//      DataSetException.throwExceptionChain(e);
    }
    SortDescriptor[] sorts = new SortDescriptor[indexes.size()];
    indexes.copyInto(sorts);
    return sorts;
  }


  private SortDescriptor makeSort(  String      indexName,
                                    Vector      columns,
                                    Vector      orders,
                                    boolean     unique
                                 )
    /*-throws DataSetException-*/
  {
    if (columns.size() > 0) {
      String [] columnsArray = new String[columns.size()];
      columns.copyInto(columnsArray);
      int size = orders.size();
      boolean [] descendingArray = new boolean[size];
      for ( int i = 0; i < size; i++ ) {
        descendingArray[i] = ((String)orders.elementAt(i)).equals("D"); //NORES
      }
      if (upperCaseIndexNames)
        indexName = indexName.toUpperCase();
      return new SortDescriptor(indexName, columnsArray, descendingArray, false, unique, null);
    }
    return null;
  }

  /**
   * Create an extended properties object that can be used by a JDBC driver from
   * a comma separated list of attribute value pairs e.g. "username=joe,password=joey54,charset=Western"
   */
  public static Properties getExtendedProperties(String s) {
    Properties p = null;
    if ( s != null ) {
      String trimOfS = s.trim();
      if ( trimOfS.length() > 0 ) {
        p = new Properties();
        while ( trimOfS.length() > 0 ) {
          int equalsIndex = trimOfS.indexOf('='); //NORES
          int commaIndex = trimOfS.indexOf(','); //NORES
          if ( equalsIndex > 0 && equalsIndex < (trimOfS.length() - 1)) { // Ensure there is something to the right and to the left of the equals sign
            while ( commaIndex != -1 && commaIndex < equalsIndex ) {
              commaIndex = trimOfS.indexOf(',', commaIndex + 1);
            }
            String key = trimOfS.substring(0, equalsIndex).trim();
            int endIndex = commaIndex == -1 ? trimOfS.length() : commaIndex;
            String value = trimOfS.substring(equalsIndex + 1, endIndex).trim();
            p.put(key, value);
            endIndex++;
            trimOfS = endIndex < trimOfS.length() ? trimOfS.substring(endIndex) : ""; //NORES
          }
          else {
            trimOfS = ""; //NORES
          }
        }
        if ( p.size() == 0 ) {
          p = null;
        }
      }
    }
    return p;
  }

  /**
   * Force column names of imported tables to upper case.
   */
  public boolean isUpperCaseColumnNames() {
    return upperCaseColumnNames;
  }
  public void setUpperCaseColumnNames(boolean upperCase) {
    this.upperCaseColumnNames = upperCase;
  }


  /**
   * Force table names of imported tables to upper case.
   */
  public boolean isUpperCaseTableNames() {
    return upperCaseTableNames;
  }
  public void setUpperCaseTableNames(boolean upperCase) {
    this.upperCaseTableNames = upperCase;
  }


  /**
   * Force index names of indexes from imported tables to upper case.
   */
  public boolean isUpperCaseIndexNames() {
    return upperCaseIndexNames;
  }
  public void setUpperCaseIndexNames(boolean upperCase) {
    this.upperCaseIndexNames = upperCase;
  }

  /**
   *  Catalog Pattern used by getTableDefs method when calling
   *  java.sql.DatabaseMetaData.getTables() against the external
   *  database to select tables for import.
   */
  public String getCatalogPattern() {
    return catalogPattern;
  }
  public void setCatalogPattern(String newCatalogPattern) {
    catalogPattern = newCatalogPattern;
  }


  /**
   *  Schema Pattern used by getTableDefs method when calling
   *  java.sql.DatabaseMetaData.getTables() against the external
   *  database to select tables for import.
   */

  public String getSchemaPattern() {
    return schemaPattern;
  }
  public void setSchemaPattern(String newSchemaPattern) {
    schemaPattern = newSchemaPattern;
  }

  /**
   *  Schema Pattern used by getTableDefs method when calling
   *  java.sql.DatabaseMetaData.getTables() against the external
   *  database to select tables for import.
   */
  public String getTablePattern() {
    return tablePattern;
  }
  public void setTablePattern(String newTablePattern) {
    tablePattern = newTablePattern;
  }


  /**
   * If true, index definitions from the external database will be created
   * for the same table inside the JDataStore database.
   */
  public boolean isImportIndexes() {
    return importIndexes;
  }
  public void setImportIndexes(boolean newimportIndexes) {
    importIndexes = newimportIndexes;
  }

  /**
   * If true, the queries used to import the table definitions will be recorded
   * in the /SYS/QUERIES and /SYS/CONNECTIONS tables.
   */
  public boolean isRecordQueries() {
    return recordQueries;
  }
  public void setRecordQueries(boolean newRecordQueries) {
    recordQueries = newRecordQueries;
  }


  /**
   * If true, the TableDef.enableSave property will be set to true for every
   * TableDef returned by getTableDefs() method.
   */
  public boolean isEnableSave() {
    return enableSave;
  }
  public void setEnableSave(boolean newEnableSave) {
    enableSave = newEnableSave;
  }


  /**
   * If true, the TableDef.enableRefresh property will be set to true for every
   * TableDef returned by getTableDefs() method.
   */
  public boolean isEnableRefresh() {
    return enableRefresh;
  }
  public void setEnableRefresh(boolean newEnableRefresh) {
    enableRefresh = newEnableRefresh;
  }

  /**
   * Database to copy tables from.
   */
  public Database getDatabaseSource() {
    return database;
  }
  public void setDatabaseSource(Database database) {
    this.database = database;
  }

  /**
   * DataStore to copy tables to.
   */

  public DataStoreConnection getDataStore() {
    return con;
  }
  public void setDataStore(DataStoreConnection con) {
    this.con = con;
  }


  private boolean         upperCaseColumnNames;
  private boolean         upperCaseTableNames;
  private boolean         upperCaseIndexNames;
  private boolean         isOpen;
  private Database        database;
  private DataStoreConnection       con;
  private TableDataSet    queries;
  private TableDataSet    connections;

  private String          catalogPattern;
  private String          schemaPattern;
  private String          tablePattern;
  private boolean         importIndexes;
  private boolean         recordQueries;
  private boolean         enableSave;
  private boolean         enableRefresh;
}

