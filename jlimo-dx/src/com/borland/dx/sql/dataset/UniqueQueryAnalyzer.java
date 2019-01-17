//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/UniqueQueryAnalyzer.java,v 7.0 2002/08/08 18:39:59 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! UniqueQueryAnalyzer
//! This class will analyze the passed query string (and possibly ResultSetMetaData)
//! to determine if the query is updatable (has unique row identifiers).
//! It turns out that the ResultSetMetaData is not good enough for most JDBC drivers.
//! Here we:
//!    a) Parse the query to get additional information.
//!    b) Determines the table names.
//!    c) If this is a multi table query, we need to determine which table each column is from.
//!       This can generally be time consuming, however in single table queries we will assume
//!       all columns will be from that single table.
//!    d) Find a set of unique row identifiers by
//!          1) Investigating the columns returned from DatabaseMetaData.getBestRowIdentifier().
//!             (on Oracle try utilizing the ROWID column)
//!          2) Look at the primary key.
//!          3) Look at unique secondary keys.
//!    e) If none of the sets of row identifier columns are already in the query, a new query is generated.
//!       The generated query will then later be used in place of the original query to retrieve the data.
//!    f) If named parameters appears in the query, these will be replaced with question marks before running
//!       the query.
//! Notes:
//!    a) If the query is a summary query with aggregates, distinct query, etc.. the query is not
//!       updatable.
//!    b) The parser code is able to keep track of qualified column and table names with aliases.
//!       However none of the JDBC drivers gave correct metadata when these qualifiers were given
//!       i.e. schema and database.  If this start to work in the future, there will bew some
//!       additional work in the resolver part that needs to be done.
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Column;

import java.lang.*;
import java.util.*;
import java.sql.*;

/**
 * This interface is used internally by other com.borland classes.
 * You should never use this interface directly.
 */
public class UniqueQueryAnalyzer extends QueryAnalyzer
{
  public UniqueQueryAnalyzer(Database database, String queryString) {
    super( database, queryString );
  }

  UniqueQueryAnalyzer(Database database, String queryString, ResultSetMetaData metaData) {
    super( database, queryString );
    metaResult = metaData;
  }

  // This function assumes query has been executed.
  //
  public void analyze() throws SQLException, DataSetException {
    DiagnosticJLimo.trace(Trace.QueryAnalyze,"Analyzing query");
    updatable = true;

    try {
      analyzeTableName();
    }
    catch (Exception ex) {
      updatable = false;
    }
    if (!updatable)
      return;

    // See if query is a summary query: either a distinct query or a group by query:
    if (isSummaryQuery()) {
      updatable = false;
      return;
    }

    if (columnList == null)
      return;

    // Find RowIds for all the tables involved.
    bestRowId = new Vector(10,5);
    validRowId = new Vector(10,5);
    bestRowIdForTable = new Vector(10,5);
    validRowIdForTable = new Vector(10,5);
    for (int i=0; i<tableList.size(); i++) {
      SQLElement table = (SQLElement)tableList.elementAt(i);
      analyzeRowId(table);
    }
    bestRowIdForTable = null;
    validRowIdForTable = null;
  }

  private void initLocals() /*-throws DataSetException-*/ throws SQLException {
    if (metaResult != null) {
      columnCount = metaResult.getColumnCount();
      columnList = new Vector(columnCount);
    }
    else if (parse()) {
      columnList = getColumns();
      columnCount = columnList.size();
    }
    tableList  = new Vector(3);
  }

  private void calcUniqueTableName() {
    int count = tableList.size();
    uniqueTableName = new boolean[count];
    for (int i=0; i<count; i++)
      uniqueTableName[i] = true;
    for (int i=0; i<count; i++) {
      SQLElement table1 = (SQLElement)tableList.elementAt(i);
      for (int j=i+1; j<count; j++) {
        SQLElement table2 = (SQLElement)tableList.elementAt(j);
        if (table1.getName().equalsIgnoreCase(table2.getName())) {
          uniqueTableName[i] = false;
          uniqueTableName[j] = false;
        }
      }
    }
  }

  public void analyzeTableName() throws SQLException, DataSetException {
    if (!parse()) {
      updatable = false;
      return;
    }

    calcColumnNo = 0;
    columnList   = getColumns();
    tableList    = getTables();
    columnCount  = columnList.size();
    calcUniqueTableName();
    SQLElement singleTable = tableList.size() == 1 ? (SQLElement)tableList.elementAt(0) : null;
    boolean needExpensiveTableQuery = false;

    if (columnCount == 1) {
      tablesByColumn = new Hashtable(101);
      SQLElement column = (SQLElement)columnList.elementAt(0);
      if ("*".equals(column.getName()) && column.getPrefixName() == null) {
        columnList.removeElementAt(0);
        columnCount--;
        handleAsterisk();
        return;
      }
    }

    columnsByName  = new Hashtable(columnCount*3+1);
    tablesByColumn = new Hashtable(columnCount*3+1);
    for (int ordinal=0; ordinal<columnCount; ordinal++) {
      SQLElement column = (SQLElement)columnList.elementAt(ordinal);
      if (column != null) {  // Expressions are stored as a null column !
        if (column.getName() == null)
          continue;
        else if ("*".equals(column.getName())) {
          columnList.removeElementAt(ordinal);
          columnCount--;
          SQLElement table = seekTableFromColumn(column);
          if (table == null)
            table = (SQLElement)tableList.elementAt(0);
          ordinal += insertAllColumns(table, ordinal);
          ordinal--;
        }
        else if (singleTable != null) {
          column.mkColumnOfTable(singleTable);
          tablesByColumn.put(column,singleTable);
        }
        else if (column.getPrefixName() == null) {
          columnsByName.put(column.getName(),column);
          needExpensiveTableQuery = true;
        }
        else {
          SQLElement table = seekTableFromColumn(column);
          tablesByColumn.put(column,table);
        }
      }
    }

    if (needExpensiveTableQuery)
      loopThroughAllTables();
    columnsByName = null; // We dont need this anymore...
  }

  private String getTablePrefixName(SQLElement column) {
    String     tableAlias = column.getPrefixName();
    int        index = tableIndexFromAlias(tableAlias);
    SQLElement table;
    String     temp  = null;

    table  = index < 0 ? new SQLElement(tableAlias,null,null,null,null) : (SQLElement)tableList.elementAt(index);
    if (table != null) {
      temp = table.getName() + "." + column.getName();
      if (!uniqueTableName[index] && table.getPrefixName() != null)
        temp = table.getPrefixName() + "." + temp;
    }
    return temp;
  }

  private String getMetaDataName(int ordinal) {
    ordinal++;
    String name = null;
    try {
      if (metaResult == null)
        return null;
      name = metaResult.getColumnLabel(ordinal);
      if (name == null)
        name = metaResult.getColumnName(ordinal);
    }
    catch (SQLException ex) {
    }
    return name;
  }

  private int insertAllColumns(SQLElement table, int insertAt) throws SQLException, DataSetException {
    String tableName = table.getName();
    String schemaName = table.getPrefixName();
    int    inserted = insertAt;

    if (schemaName == null && database.isUseSchemaName())
      schemaName = userName;
    ResultSet columns = database.getMetaData().getColumns(null,schemaName,tableName,"%");  //NORES
    while (columns.next()) {
      String columnName = trimRight(columns.getString(4));
      if (adjustIdentifiers)
        columnName = adjustCase(columnName);
      SQLElement column = new SQLElement(columnName);
      column.mkColumnOfTable(table, true);
      columnList.insertElementAt(column,insertAt++);
      tablesByColumn.put(column,table);
    }
    columns.close();
    inserted = insertAt - inserted;
    columnCount += inserted;
    return inserted;
  }

  private void generateUniqueTableAliases() {
    aliasesAdded = true;
    int counter = 0;
    for (int tableIndex=0; tableIndex < tableList.size()-1; tableIndex++) {
      SQLElement table = (SQLElement)tableList.elementAt(tableIndex);
      boolean isUnique = false;
      while (!isUnique) {
        isUnique = true;
        String name = table.getLabelName();
        for (int otherTableIndex=0; otherTableIndex < tableList.size(); otherTableIndex++) {
          SQLElement otherTable = (SQLElement)tableList.elementAt(otherTableIndex);
          String otherName = otherTable.getLabelName();
          if (tableIndex != otherTableIndex && name.equals(otherName)) {
            table.setAlias("A"+(++counter));
            isUnique = false;
            break;
          }
        }
      }
    }
  }

  private void handleAsterisk() throws SQLException, DataSetException {
    Vector tables = tableList;
    int    tableCount = tables.size();
    int    ordinal = 0;
    if (tableCount > 1)
      generateUniqueTableAliases();
    if (tableCount == 1 && metaResult != null)
      generateColumnsFromResultSetMetaData();
    else {
      for (int index=0; index < tableCount; index++) {
        SQLElement table = (SQLElement)tables.elementAt(index);
        ordinal += insertAllColumns(table, ordinal);
      }
    }
  }

  private void generateColumnsFromResultSetMetaData() throws SQLException, DataSetException {
    SQLElement table = (SQLElement)tableList.elementAt(0);
    columnCount = metaResult.getColumnCount();
    for (int i=0; i<columnCount; i++) {
      String columnName = getMetaDataName(i);
      if (adjustIdentifiers)
        columnName = adjustCase(columnName);
      SQLElement column = new SQLElement(columnName);
      column.mkColumnOfTable(table,true);
      columnList.addElement(column);
      tablesByColumn.put(column,table);
    }
  }

  private void loopThroughAllTables() throws SQLException, DataSetException {
    Vector tables = tableList;
    int    tableCount = tables.size();
    for (int index=0; index < tableCount; index++) {
      SQLElement table = (SQLElement)tables.elementAt(index);
      String tableName = table.getName();
      String schemaName = table.getPrefixName();
      if (schemaName == null && database.isUseSchemaName())
        schemaName = userName;
      ResultSet columns = database.getMetaData().getColumns(null,schemaName,tableName,"%");  //NORES
//!      if (columns instanceof com.borland.sql.SQLAdapter)
//!        setRightTrimStrings(columns);
      while (columns.next()) {
        String columnName = trimRight(columns.getString(4));
        if (adjustIdentifiers)
          columnName = adjustCase(columnName);
//!BUGTEST        columnName = JdbcProvider.trimRight(columnName);
        SQLElement column = (SQLElement)columnsByName.get(columnName);
        if (column != null && column.getName() != null && column.getPrefixName() == null) {
          column.mkColumnOfTable(table);
          tablesByColumn.put(column,table);
        }
      }
      columns.close();
    }
  }

  // If the query is updatable with no modifications, return true.
  //
  boolean isUpdatable() { return updatable; }

  public String getTableName() { return tableList == null || tableList.size() != 1 ? null : ((SQLElement)tableList.elementAt(0)).getName(); }
  public String getSchemaName() { return tableList == null || tableList.size() != 1 ? null : ((SQLElement)tableList.elementAt(0)).getPrefixName(); }

  // Return an array of tablename strings.
  // These strings should include the schema name if present.
  //
  String[] getResolveOrder() {
    if (tableList == null)
      return null;
    int count = tableList.size();
    if (count <= 1)
      return null;
    String[] list = new String[count];
    for (int i=0; i<count; i++) {
      SQLElement table = (SQLElement)tableList.elementAt(i);
      list[i] = table.generateString(quoteCharacter,false,true);
    }
    return list;
  }

  // Return an vector of SQLElement describing <column,alias,tablealias>
  // indicating what columns in the query make it updatable.
  //
  public Vector getCurrentRowId() {
    return validRowId;
  }

  // Return an vector of SQLElement describing <column,alias,tablealias>
  // indicating what columns needs to be included to make the query updatable.
  //
  public Vector getBestRowId() { return bestRowId; }

  public void setBestRowId(Vector columns) /*-throws DataSetException-*/ {
    DiagnosticJLimo.trace(Trace.QueryAnalyze,"Setting a best rowId");

    // Check if query has been parsed (could be here if Jdbc driver supported getTableName()).
    if (parser == null)
      parse();

    // Store any added columns that weren't in the original query (for
    // getAddedColumns()). This is used to determine what columns need
    // to be added to the query and may be used to indicate what columns
    // should be hidden from view.
    // Some of the bestRowId columns may already be in the query!
    int count = columns.size();
    newColumns = new Vector(count,1);
    for (int i=0; i<count; i++) {
      SQLElement column = (SQLElement)columns.elementAt(i);
      if (!tablesByColumn.containsKey(column)) {
        DiagnosticJLimo.trace(Trace.QueryAnalyze,"Adding row id column to query: " + column);
        newColumns.addElement(column);
        SQLElement table = seekTableFromColumn(column);
        tablesByColumn.put(column,table);
      }
    }

    // Get tokens from parser.
    QueryParseToken   tokens = parser.getParsedTokens();
    DiagnosticJLimo.check(tokens.isSelect());

    // optional DISTINCT...
    QueryParseToken tmpToken = tokens.getNextToken();
    if (tmpToken.getType() == SQLToken.OTHER) {
      tokens = tmpToken;
      tmpToken = tokens.getNextToken();
    }

    // Add the new columns in front:
    count = newColumns.size();
    for (int index = 0; index < count; index++) {
      SQLElement column = (SQLElement)newColumns.elementAt(index);

      // Insert row id columns into projection list.
      tokens.setNextToken(new QueryParseToken(SQLToken.FIELD,column));
      tokens = tokens.getNextToken();
      tokens.setAlias(null);
    }
    tokens.setNextToken(tmpToken);
    tokens = tmpToken;

    // If this is a "SELECT *" query, project '*' to a series of 'T1.*,T2.*,...' field specifications
    while (tokens.isField() && tokens.getName().equals("*") && tokens.getPrefixName() == null) {
      tmpToken = tokens.getNextToken();
      Vector tables = tableList;
      int tableCount = tables.size();
      for (int index=0; index < tableCount; index++) {
        SQLElement table = (SQLElement)tables.elementAt(index);
        if (index > 0) {
          tokens.setNextToken(new QueryParseToken(SQLToken.FIELD,"*",null));
          tokens = tokens.getNextToken();
        }
        tokens.mkColumnOfTable(table, false);
      }
      tokens.setNextToken(tmpToken);
      tokens = tmpToken;
    }

    // Modify the tables if aliases were added:
    if (aliasesAdded) {
      // Skip the FROM
      if (tokens.getType() == SQLToken.FROM)
        tokens = tokens.getNextToken();
      int tableIndex = -1;
      while (tokens != null && tokens.getType() == SQLToken.TABLE) {
        SQLElement table = (SQLElement)tableList.elementAt(++tableIndex);
        tokens.setAlias(table.getAlias());
        tokens = tokens.getNextToken();
      }
    }

    if (tokens == null) {
//! &&&&&
//! throw exception (FIELD tokens not found) -cjo
    }

    // Instantiate a new query string from token list.
    generatedQuery = parser.format(parser.getParsedTokens(),false);
    DiagnosticJLimo.trace(Trace.QueryAnalyze,"Generating a new query: " + generatedQuery);
  }

  // This returns the userName of the connection, which is modified to
  // identify the default schemaName
  //
  public String getDefaultSchemaName() {
    return userName;
  }

  // This returns a string to the query generated when setBestRowId is
  // called.
  String getGeneratedQuery() {
    return generatedQuery;
  }

  // The returns a vector of <table.column> Strings indicating what columns
  // were added to the query when it was regenerated by setBestRowId. This
  // is probably most useful for determining what columns should be hidden
  // from view.
  public Vector getAddedColumns() {
    return newColumns;
  }
/*
  // Parse the query. If problems parsing the query, return false
  //
  boolean parse() {
    Diagnostic.trace(Trace.QueryAnalyze,"Invoking parser");

    try {
      return super.parse();
    }
    catch (DataSetException ex)
    {
      Diagnostic.printStackTrace(ex);
//! &&&&&
//! do work here to catch exceptions for:
//!  - not a SELECT query
//!  - malformed query (could mean don't understand how to parse it)

//! placeholder: return that query isn't updatable.
      return false;
    }

    return true;
  }
*/
  // The tableAlias is either a tablename or an alias for a tablename.
  // Find the tablename association and return a TABLE token for that table.
  // Linear list should be OK, since 10 tables is an unusual large number of
  // tables in a query.
  //
  private int tableIndexFromAlias(String tableAlias) {
    if (tableAlias == null)
      return 0;
    int    tableCount = tableList.size();
    for (int index=0; index<tableCount; index++) {
      SQLElement table = (SQLElement)tableList.elementAt(index);
      String labelName = table.getLabelName();
      if (tableAlias.equalsIgnoreCase(labelName))
        return index;
    }
    return -1;
  }

  private SQLElement seekTableFromColumn(SQLElement column) {
    if (column.getPrefixName() == null)
      return null;
    int tableCount = tableList.size();
    for (int index=0; index<tableCount; index++) {
      SQLElement table = (SQLElement)tableList.elementAt(index);
      if (column.isColumnFromTable(table,userName))
        return table;
    }
    DiagnosticJLimo.fail();
    return new SQLElement(column.getPrefixName(), null, column.getPrefix2Name(), column.getPrefix3Name(), null);
  }

  public SQLElement tableFromColumn(SQLElement column) {
    return (SQLElement)tablesByColumn.get(column);
  }

  private boolean isSummaryQuery() /*-throws DataSetException-*/ {
    if (parser == null)
      parse();
    return parser.isSummary();
  }

  private void analyzeRowId(SQLElement table) {
    String databaseName = table.getPrefix2Name();
    String schemaNameUsed = table.getPrefixName();
    if (schemaNameUsed == null && database.isUseSchemaName())
      schemaNameUsed = userName;
    String tableName = table.getName();

    if (!analyzeBestRowId(databaseName,tableName,schemaNameUsed,table)) {
      if (!analyzePrimaryKey(databaseName,tableName,schemaNameUsed,table)) {
        if (!analyzeUniqueIndex(databaseName,tableName,schemaNameUsed,table)) {
          updatable = false;
          addToList(bestRowId,bestRowIdForTable);
          bestRowIdForTable.removeAllElements();
          return;
        }
      }
    }
    addToList(validRowId,validRowIdForTable);
    validRowIdForTable.removeAllElements();
    bestRowIdForTable.removeAllElements();
  }

  private void addToList(Vector target, Vector source) {
    int count = source.size();
    for (int i=0; i<count; i++) {
      target.addElement(source.elementAt(i));
    }
  }

  private boolean analyzeBestRowId(String databaseName, String tableName, String schemaName, SQLElement table) {
    // Ask the JDBC driver for the best row id for this table.
    boolean found = false;
    try {
      ResultSet resultSet = database.getMetaData().getBestRowIdentifier(databaseName,
                                                                        schemaName,
                                                                        tableName,
                                                                        database.getMetaData().bestRowTransaction,
                                                                        true);
      found = findRowIdInResultSet(resultSet, table, true);
    }
    catch (Exception ex) {
      DiagnosticJLimo.printStackTrace(ex);
      bestRowIdForTable.removeAllElements();
      validRowIdForTable.removeAllElements();
    }
    if (!found && bestRowIdForTable.size() == 0 && database.getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_ORACLEROWID)) {
      bestRowIdForTable.removeAllElements();
      SQLElement rowId = new SQLElement("ROWID");  //NORES
      rowId.mkColumnOfTable(table,false);
      bestRowIdForTable.addElement(rowId);
    }
    return found;
  }

  private boolean analyzePrimaryKey(String databaseName, String tableName, String schemaName, SQLElement table) {
    boolean storeBestRow = (bestRowIdForTable.size() == 0);
    // Get primary key information.
    try {
      ResultSet resultSet = database.getMetaData().getPrimaryKeys(databaseName, schemaName, tableName);
      return findRowIdInResultSet(resultSet, table, false);
    }
    catch (Exception se) {
      // Some drivers (ie. Oracle's 7.3 driver) throw an exception instead of failing quietly.
      validRowIdForTable.removeAllElements();
      if (storeBestRow)
        bestRowIdForTable.removeAllElements();
    }
    return false;
  }

  private boolean findRowIdInResultSet(ResultSet resultSet, SQLElement table, boolean bestRow) throws SQLException {

    boolean storeBestRow = (bestRowIdForTable.size() == 0);
    boolean match = true;
    int ordinal = bestRow ? 2 : 4;

//!    if (resultSet instanceof com.borland.sql.SQLAdapter)
//!      setRightTrimStrings(resultSet);

    while (resultSet.next()) {
      // Get column name from result set.
      // (note: right trimming string in case some driver returns meta-info as CHAR not VARCHAR)
      String  columnName = trimRight(resultSet.getString(ordinal));
//!BUGTEST      columnName = JdbcProvider.trimRight(columnName);
      DiagnosticJLimo.trace(Trace.QueryAnalyze,"(rowId)getBestRowIdentifier table,column: " + table.toString() + ", " + columnName);

      // Bugfix for certain drivers (Oracle through DataGateway):
      if (columnName == null)
        continue;

      if (adjustIdentifiers)
        columnName = adjustCase(columnName);
      SQLElement rowId = new SQLElement(columnName);
      boolean quotes = !bestRow ? true : resultSet.getShort(8) != java.sql.DatabaseMetaData.bestRowPseudo;
      rowId.mkColumnOfTable(table,quotes);

      if (storeBestRow)
        bestRowIdForTable.addElement(rowId);

      // Check columnList for a match.
      if (match) {
        if (tablesByColumn.containsKey(rowId))
          validRowIdForTable.addElement(rowId);
        else {
          match = false;
          validRowIdForTable.removeAllElements();
        }
      }
    }
    resultSet.close();

    return match && (validRowIdForTable.size() > 0);
  }

  private boolean analyzeUniqueIndex(String databaseName, String tableName, String schemaName, SQLElement table) {

    // Get unique index information.
    ResultSet resultSet;
    try {
      resultSet = database.getMetaData().getIndexInfo(databaseName, schemaName, tableName, true, true);
    }
    catch (Exception ex) {
      DiagnosticJLimo.printStackTrace(ex);
      return false;
    }

    boolean storeBestRow = (bestRowIdForTable.size() == 0);

    Vector indexColumns = new Vector(10,5);
    String indexName = null;

//!    if (resultSet instanceof com.borland.sql.SQLAdapter)
//!      setRightTrimStrings(resultSet);

    try {
      while (resultSet.next()) {
        // (note: right trimming string in case some driver returns meta-info as CHAR not VARCHAR)
        String tmpIndexName = trimRight(resultSet.getString("INDEX_NAME"));
        String columnName   = trimRight(resultSet.getString("COLUMN_NAME"));
//!BUGTEST        tmpIndexName = JdbcProvider.trimRight(tmpIndexName);
        DiagnosticJLimo.trace(Trace.QueryAnalyze,"Added table,column:" + tableName + ", " + columnName + " to Unique key: " + indexName);

        // Skip results that contain an empty column_name. Some
        // ODBC drivers (via the JDBC/ODBC Bridge) return these
        // values.
        if ((columnName == null) || (columnName.length() == 0))
          continue;

        if (adjustIdentifiers)
          columnName = adjustCase(columnName);

        if (indexName == null || !indexName.equals(tmpIndexName)) {
          if (analyzeUniqueIndex(indexColumns,storeBestRow)) {
            resultSet.close();
            return true;
          }
          indexName = tmpIndexName;
          indexColumns.removeAllElements();
        }

        SQLElement rowId = new SQLElement(columnName);
        rowId.mkColumnOfTable(table,true);
        indexColumns.addElement(rowId);
      }
      resultSet.close();
      return analyzeUniqueIndex(indexColumns,storeBestRow);
    }
    catch (SQLException ex) {
      validRowIdForTable.removeAllElements();
      if (storeBestRow)
        bestRowIdForTable.removeAllElements();
    }
    return false;
  }

  private boolean analyzeUniqueIndex(Vector indexColumns, boolean storeBestRowId) {
    int count = indexColumns.size();
    if (count == 0)
      return false;

    for (int i=0; i<count; i++) {
      SQLElement rowId  = (SQLElement)indexColumns.elementAt(i);
      if (tablesByColumn.containsKey(rowId))
        validRowIdForTable.addElement(rowId);
      else {
        validRowIdForTable.removeAllElements();
        break;
      }
    }

    if (validRowIdForTable.size() > 0)
      return true;

    boolean storeBestRow = (bestRowIdForTable.size() == 0);
    if (storeBestRowId) {
      if (bestRowIdForTable.size() == 0 || bestRowIdForTable.size() > indexColumns.size())
        bestRowIdForTable = indexColumns;
    }
    return false;
  }

  public Vector getColumnList() {
    return columnList;
  }

  public SQLElement[] getAllColumns(boolean updatable) {
    int nNew = updatable && newColumns != null ? newColumns.size() : 0;
    int nCol = columnList != null ? columnList.size() : 0;
    SQLElement[] result = new SQLElement[nNew+nCol];
    if (nNew > 0) {
      newColumns.copyInto(result);
      for (int ordinal=nNew; ordinal<result.length; ordinal++)
        result[ordinal] = (SQLElement)columnList.elementAt(ordinal-nNew);
    }
    else if (nCol > 0) {
      columnList.copyInto(result);
    }
    return result;
  }

//!  // Fix for InterClient:
//!  void setRightTrimStrings(ResultSet rs) {
//!    try {
//!      com.borland.sql.SQLAdapter fast = (com.borland.sql.SQLAdapter)rs;
//!      fast.adapt(fast.RIGHT_TRIM_STRINGS, null);
//!    }
//!    catch (SQLException ex) {
//!    }
//!  }

  int[] getOrdinalsOfRowIds() {
    int nNew   = newColumns != null ? newColumns.size() : 0;
    int nValid = validRowId != null ? validRowId.size() : 0;
    int[] result = new int[nNew+nValid];
    for (int index=0; index<nNew; index++)
      result[index] = index;
    for (int index=0; index<nValid; index++)
      result[index+nNew] = nNew + columnList.indexOf(validRowId.elementAt(index));
    return result;
  }

  private final String trimRight(String str)
  {
    if (sqlDialect == RuntimeMetaData.INVALID_DRIVER) {
      sqlDialect = database.getSQLDialect();
    }
    if (sqlDialect == RuntimeMetaData.JDBC4_INTERCLIENT)
      return JdbcProvider.trimRight(str);
    return str;

  }

  // Method for internal use only!
  //! Used from DMdataSetView to strip the query string from hidden columns
  public static String removeHiddenColumns(QueryDataSet qds, Vector hiddenColumns) {
    QueryDescriptor desc = qds.getQuery();
    String original = desc.getQueryString();
    if (hiddenColumns == null || hiddenColumns.size() == 0)
      return null;
    Database db = desc.getDatabase();
    SimpleParser parser = new SimpleParser(original, db.getIdentifierQuoteChar());
    QueryParseToken tokens = parser.getParsedTokens();
    if (tokens == null)
      return null;

    boolean modified = false;
    String[] columns = new String[hiddenColumns.size()];
    hiddenColumns.copyInto(columns);

    for (int index=0; index<columns.length; index++) {
      String name = columns[index];

      QueryParseToken prev = null;
      tokens = parser.getParsedTokens();
      while (tokens != null) {
        if (tokens.getType() == SQLToken.FIELD && tokens.getLabelName().equalsIgnoreCase(name)) {
          prev.setNextToken(tokens.getNextToken());
          modified = true;
          break;
        }
        prev = tokens;
        tokens = tokens.getNextToken();
      }
    }

    return modified ? parser.format(parser.getParsedTokens(),false) : null;
  }


  private int               sqlDialect  = RuntimeMetaData.INVALID_DRIVER;
  // accessible via accessor methods
  private boolean           updatable;
  private Vector            bestRowIdForTable;  // Potential row id column name list for a single table.
  private Vector            bestRowId;          // Potential row id column name list for all tables in the query.
  private Vector            validRowIdForTable; // Active row id column name list for a table.
  private Vector            validRowId;         // Active row id column name list for all tables in the query.
  private String            generatedQuery;     // Query generated by setBestRowId.
  private Vector            newColumns;         // List of columns added by setBestRowId.

  // "internal" variables
  private ResultSetMetaData metaResult;         // setup in constructor
  private int               columnCount;        // Number of columns reported by metaResult.
  private Vector            columnList;         // Vector of type SQLElement of the columns in the query.
  private Vector            tableList;          // Vector of type SQLElement of the tables in the query
  private int               calcColumnNo;
  private Hashtable         tablesByColumn;     // Used to identify physical columns
  private Hashtable         columnsByName;      // Used during analyzeTableName
  private boolean           uniqueTableName[];  // Used during analyzeTableName
  private boolean           aliasesAdded;       // Were aliases added to the table names ?
}
