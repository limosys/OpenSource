//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/MetaData.java,v 7.0 2002/08/08 18:40:07 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.*;
import java.util.*;

import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;
import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;

/**
  This class is intended to provide basic metadata information and some DDL functionality.
*/

public class MetaData extends RuntimeMetaData
{
  public static MetaData getMetaData(Database database) {
    RuntimeMetaData metaData = database.getRuntimeMetaData();
    if (metaData == null)
      metaData = new MetaData(database);
    else if (!(metaData instanceof MetaData))
      metaData = new MetaData(metaData);
    return (MetaData)metaData;
  }

  protected MetaData(Database database) {
    super(database);
  }

  protected MetaData(MetaData metaData) {
    super(metaData);
    this.implementor = metaData.implementor;
  }

  protected MetaData(RuntimeMetaData metaData) {
    super(metaData);
  }

  protected void connectionClosed() {
    implementor = null;
    super.connectionClosed();
  }

  /** Gets an Enumeration of String objects representing the existing ODBC Data Sources.
   *  Later the existing Velcro aliases will be added.
   */
  public static Enumeration getURLs() {
    Vector result = new Vector();
    try {
      NativeMetaData metadata = new NativeMetaData();
      metadata.getOdbcURLs(result);
    }
    catch (Throwable t) {
      DiagnosticJLimo.printStackTrace(t);
    }
    return (result.elements());
  }

  public static boolean BorlandRemoteEnabled() {
    if (datagateway_remote_enabled == 0)
      datagateway_remote_enabled = BorlandMetaData.getRemoteEnabled() ? 1 : -1;
    return (datagateway_remote_enabled > 0);
  }

  public static boolean BorlandLocalEnabled() {
    if (datagateway_local_enabled == 0)
      datagateway_local_enabled = BorlandMetaData.getLocalEnabled() ? 1 : -1;
    return (datagateway_local_enabled > 0);
  }

  public static Enumeration getBorlandLocalURLs() {
    Vector result = new Vector();
    BorlandMetaData.getBorlandLocalURLs(result);
    return (result.elements());
  }

  public static Enumeration getBorlandRemoteURLs(String serverName) {
    Vector result = new Vector();
    BorlandMetaData.getBorlandRemoteURLs(serverName, result);
    return (result.elements());
  }

  /** Gets an Enumeration of String objects representing the existing tables in the
   *  current database.
   */
  public Enumeration getTables()
    throws MetaDataException
  {
    String userName = getUserName();

    Vector result = new Vector();
    try {
      ResultSet tables = getJdbcMetaData().getTables(null,null,"%",new String[]{"TABLE"});  //NORES
      while (tables.next()) {
        String owner = trimRight(tables.getString(2));       // "TABLE_SCHEM" //NORES
        String name  = trimRight(tables.getString(3));       // "TABLE_NAME"  //NORES
        if (userName == null || owner == null || userName.equalsIgnoreCase(owner))
          result.addElement(name);
        else if (!owner.startsWith("SYS") || userName.startsWith("SYS")) //!NORES
          result.addElement(owner + "." + name); //!NORES
      }
      tables.close();
    }
    catch(SQLException ex) {
      MetaDataException.rethrowSQLException(ex);
    }
    catch(DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    return result.elements();
  }

  /** Gets an Enumeration of String objects representing the existing procedure names in the
   *  current database.
   */
  public Enumeration getProcedures()
    throws MetaDataException
  {
    String userName = getUserName();

    Vector result = new Vector();
    try {
      ResultSet procedures = getJdbcMetaData().getProcedures(null,null,"%");  //NORES
      while (procedures.next()) {
        String owner = trimRight(procedures.getString(2));       // "PROCEDURE_SCHEM" //NORES
        String name  = trimRight(procedures.getString(3));       // "PROCEDURE_NAME"  //NORES
        if (name != null && name.indexOf((int)';') > 0)
          name = name.substring(0, name.indexOf((int)';'));  // Hack for the thin Sybase driver (JOAL)

        if (userName == null || owner == null || userName.equalsIgnoreCase(owner))
          result.addElement(name);
        else if (!owner.startsWith("SYS") || userName.startsWith("SYS")) //!NORES
          result.addElement(owner + "." + name); //!NORES
      }
      procedures.close();
    }
    catch(SQLException ex) {
      MetaDataException.rethrowSQLException(ex);
    }
    catch(DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    return result.elements();
  }

  /**
   * Get the columns of a procedure from the DatabaseMetaData information.
   * Store the columns in a hashtable for quick re-retrieval.
   *
   * @param   procedureName   the name of the procedure of interest.
   * @return  an array of columns.
   */
  public synchronized ParameterRow getProcedureColumns(String procedureName)
    throws MetaDataException
  {
    String[] names = splitName(procedureName);

    ParameterRow parameterRow = new ParameterRow();

    try {
      ResultSet procedures = getJdbcMetaData().getProcedureColumns(null,names[0],names[1],"%");  //NORES
      while (procedures.next()) {
        String name      = trimRight(procedures.getString(4));   // "COLUMN_NAME"  //NORES
        short  kind      = procedures.getShort(5);    // "COLUMN_TYPE"  //NORES
        short  sqlType   = procedures.getShort(6);    // "DATA_TYPE"    //NORES
        int    precision = procedures.getInt(8);      // "PRECISION"    //NORES
        short  scale     = procedures.getShort(10);   // "SCALE"        //NORES

        Column column = createColumn(name, name, sqlType, precision, 0, scale, false);
        column.setParameterType(kind);
        parameterRow.addColumn(column);
      }
      procedures.close();
    }
    catch(SQLException ex) {
      MetaDataException.rethrowSQLException(ex);
    }
    catch(DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    return parameterRow;
  }

  /** Gets an Enumeration of String objects representing the column names of
   *  a table.
   */
  public Enumeration getColumns(String tableName)
    throws MetaDataException
  {
    String[] names = splitName(tableName);

    Vector result = new Vector();
    try {
      ResultSet fields = getJdbcMetaData().getColumns(null,names[0],names[1],"%");  //NORES
      while (fields.next()) {
        result.addElement(trimRight(fields.getString(4)));  // "COLUMN_NAME"  //NORES
      }
      fields.close();
    }
    catch(SQLException ex) {
      MetaDataException.rethrowSQLException(ex);
    }
    catch(DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    return result.elements();
  }

  /**
   * Create a Table using TableDescriptor
   */
  public void createTable(TableDescriptor table)
  {
    database.executeStatement(createTableSql(table));
  }

  /**
   * Create a Table using TableDescriptor
   */
  public void createTables(TableDescriptor[] tables)
  {
    for (int index = 0; index < tables.length; ++index)
      createTable(tables[index]);
  }

  /**
   *  @deprecated  Use createTable(TableDescriptor) instead.
   *  Create a Table with the columns described by the given dataSet.
   */
  public void createTable(String tableName, DataSet dataSet)
    throws MetaDataException
  {
    createTable(tableName, dataSet, null);
  }

  /**
   *  @deprecated  Use createTable(TableDescriptor) instead.
   *
   * Create a Table with the columns described by the given dataSet.
   *  The primaryKey may be either of the 5 formats:
   *    1) String[]    With column names.
   *    2) Number[]    With indices into the columns of the passed dataSet.
   *    3) int[]       With indices into the columns of the passed dataSet.
   *    4) Object[]    A hybrid of column names and column indices.
   *    5) Enumeration A hybrid of column names and column indices.
   */
  public void createTable(String tableName, DataSet dataSet, Object primaryKey)
    throws MetaDataException
  {
    TableDescriptor table = new TableDescriptor();
    table.table = tableName;
    table.columns = dataSet.getColumns();
    if (primaryKey != null) {
      table.indexes = new SortDescriptor[1];
      table.indexes[0] = new SortDescriptor(  tableName+"_PK",
                                              makeFieldNameArray(primaryKey, dataSet),
                                              null,
                                              null,
                                              0
                                            );
    }
    createTable(table);
  }

  /**
   *  Drop the given table from the current database and ignore failures.
   */
  public void dropTable(TableDescriptor table)
    throws MetaDataException
  {
    try {
      dropTable(table.table);
    }
    catch (DataSetException ex) {
    }
  }

  /**
   *
   * Drop the given Table from the current database and ignore failures.
   */
  public void dropTableIfExist(String tableName)
    throws MetaDataException
  {
    try {
      dropTable(tableName);
    }
    catch (DataSetException ex) {
    }
  }

  /**
   *
   * Drop the given Table from the current database.
   */
  public void dropTable(String tableName)
    throws MetaDataException
  {
    database.executeStatement("DROP TABLE " + tableName); //NORES
  }

  /** Returns true if the tableName is either a user table or a view.
   */
  public boolean tableExists(String tableName)
    throws MetaDataException
  {
    try {
      Connection connection = database.getJdbcConnection();
      DatabaseMetaData metadata = connection.getMetaData();
      ResultSet tables = metadata.getTables(null,null,tableName,new String[]{"TABLE"}); //!NORES
      boolean result = tables.next();
      tables.close();
      return result;
    }
    catch (DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    catch(SQLException ex) {
      MetaDataException.rethrowSQLException(ex);
    }
    return false;
  }

  /**
   * @deprecated  Use CreateTableIndex(TableDescriptor, SortDescriptor)
   *
   * Create an index with the name 'indexName' on the table 'tableName'.
   *  unique if the index should be unique.
   *  The indexFields may be either of the 5 formats:
   *    1) String[]    With column names.
   *    2) Number[]    With indices into the columns of the passed dataSet.
   *    3) int[]       With indices into the columns of the passed dataSet.
   *    4) Object[]    A hybrid of column names and column indices.
   *    5) Enumeration A hybrid of column names and column indices.
   */
  public void createTableIndex(String indexName, String tableName, boolean unique, Object indexFields, DataSet dataSet)
  {
    SortDescriptor index = new SortDescriptor(indexName, makeFieldNameArray(indexFields, dataSet), null, null, 0);
    TableDescriptor table = new TableDescriptor();
    table.table = tableName;
    createTableIndex(table, index);
  }

  /**
   * Create a index.
   *
   * @param table description of table to create index for.
   * @param index description of of index to create.
   */

  public void createTableIndex(TableDescriptor table, SortDescriptor index) {
//    try {
      String sql = makeSqlForCreateIndex(table, index);
      DiagnosticJLimo.trace(Trace.MetaData, "SQL for Create Index:  " + sql ); //NORES
      database.executeStatement(sql);
      DiagnosticJLimo.trace(Trace.MetaData, "Index " + index.getIndexName() + " created!"); //NORES
//    }
//    catch(SQLException ex) {
//      MetaDataException.rethrowSQLException(ex);
//    }
  }

  public static DataSetAnalyzer getDataSetAnalyzer(DataSet dataSet) {
    StorageDataSet storageDataSet = dataSet.getStorageDataSet();
    Provider  provider = storageDataSet.getProvider();
    if (storageDataSet instanceof QueryDataSet || provider != null && provider instanceof QueryProvider)
      return new QueryDataSetAnalyzer(storageDataSet);
    if (storageDataSet instanceof TableDataSet)
      return new TableDataSetAnalyzer((TableDataSet)storageDataSet);
    return new DefaultDataSetAnalyzer(storageDataSet);
  }

  private String[] splitName(String name) {
    int point = name == null ? -1 : name.indexOf('.');
    String[] result = new String[2];
    if (point < 0)
      result[1] = name;
    else {
      result[0] = name.substring(0,point);
      result[1] = name.substring(point+1);
    }
    return result;
  }

  Column createColumn(String schemaName, String tableName, String columnName) {
    Column col = null;
    try {
      ResultSet fields = getJdbcMetaData().getColumns(null,schemaName,tableName,columnName);
      if (fields.next()) {
        int sqlType   = fields.getShort(5);       // "DATA_TYPE" //NORES
        int precision = fields.getInt(7);         // "PRECISION" //NORES
        int scale     = fields.getInt(9);         // "SCALE"     //NORES
        String label  = columnName;
        boolean search= true;
        col = createColumn(columnName,label,sqlType,precision,0,scale,search);
      }
      fields.close();
    }
    catch (Exception ex)
    {
      DiagnosticJLimo.printStackTrace(ex);
      col = new Column();
      try { col.setColumnName(columnName); }
      catch (DataSetException ex2) {};
    }
    return col;
  }

  public void setImplementor(MetaDataImplementor implementor) {
    this.implementor = implementor;
  }

  public MetaDataImplementor getImplementor() {
    if (implementor == null) {
      int sqlDriver = getIntValue(INT_SQLDIALECT) & MASK_DRIVER;
      switch (sqlDriver) {
        case JDBC2_DATAGATEWAY:
        case JDBC4_DATAGATEWAY:
          implementor = new BorlandMetaData(this);
          break;
        case JDBC2_ORACLE:
        case JDBC4_ORACLE:
          implementor = new OracleJdbcMetaData(this);
          break;
        default:
          implementor = getImplementorByDialect();
      }
    }
    return implementor;
  }

  public MetaDataImplementor getImplementorByDialect() {
    int sqlDialect = getIntValue(INT_SQLDIALECT) & MASK_DIALECT;
    switch (sqlDialect) {
      case DB_INTERBASE:
        return new InterbaseMetaData(this);
      case DB_ORACLE:
        return new Oracle7MetaData(this);
      case DB_DBASE:
        return new dBaseMetaData(this);
      case DB_PARADOX:
        return new ParadoxMetaData(this);
      case DB_SYBASE:
        return new SybaseMetaData(this);
      case DB_JDATASTORE:
        return new JDataStoreMetaData(this);
      case DB_DB2:
      default:
        return new MetaDataImplementor(this);
    }
  }

  private Enumeration getEnumeration(Object list)
    throws MetaDataException
  {
    if (list instanceof Enumeration)
      return (Enumeration)list;
    if (list instanceof Object[])
      return new ObjectArrayEnumerator((Object[])list);
    if (list instanceof int[])
      return new IntArrayEnumerator((int[])list);
    MetaDataException.throwBadFieldlist();
    return null;
  }

  private String makeFieldNameListString(String[] fieldNames)
    throws MetaDataException
  {
    StringBuffer buf = new StringBuffer(32);
    for (int index = 0; index < fieldNames.length; ++index) {
      if (buf.length() > 0)
        buf.append(',');
      buf.append(fieldNames);
    }
    return buf.toString();
  }

  private String[] makeFieldNameArray(Object fieldList, DataSet dataSet)
    throws MetaDataException
  {
    Vector list = new Vector();

      Enumeration fieldIterator = getEnumeration(fieldList);
      while (fieldIterator.hasMoreElements()) {
        Object obj = fieldIterator.nextElement();
        if (obj instanceof String)
          list.addElement(obj);
        else if (obj instanceof Number)
          list.addElement(dataSet.getColumn(((Number)obj).intValue()).getColumnName());
      }
    String[] fieldNames = new String[list.size()];
    list.copyInto(fieldNames);
    return fieldNames;
  }

  private String createTableSql(TableDescriptor table)
    throws MetaDataException
  {
    if (implementor == null)
      getImplementor();

    String sql = null;

    try {
      sql = "CREATE TABLE " + table.table + " ("; //NORES

      int columns = table.columns.length;
      int columnCount = 0;
      for (int i=0; i<columns; i++) {
        {//if (dataSet.getColumn(i).getCalcType() == CalcType.NO_CALC) {
          if (columnCount > 0)
            sql += ", "; //NORES
          ++columnCount;
          Column column = table.columns[i];
          sql += column.getColumnName() + " " + implementor.columnToSQLDataType(column); //NORES
          if (column.isRowId() || column.isRequired())
            sql += " NOT NULL"; //NORES
        }
      }
      if (table.primaryKey != null) {
        sql += ", PRIMARY KEY(" + makeFieldNameListString(table.primaryKey.getKeys()) + ")"; //NORES
      }
      sql += ")"; //NORES
    }
    catch (DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }

    return sql;
  }

  private String makeSqlForCreateIndex(TableDescriptor table, SortDescriptor index)
    throws MetaDataException
  {
    String sql = index.isUnique() ? "CREATE UNIQUE INDEX " : "CREATE INDEX "; //NORES
    sql += index.getIndexName() + " ON " + table.table + "(" + makeFieldNameListString(index.getKeys()) + ")"; //NORES
    return sql;
  }

  static String trimRight(String source) {
    if (source == null) return null;

    int len = source.length();

    while ((len > 0) && (source.charAt(len-1) == ' '))
      len--;

    return (len < source.length()) ? source.substring(0, len) : source;
  }

  public boolean isSqlTypeSupported(short sqlType) {
    switch (sqlType) {
      case java.sql.Types.CHAR:         // case statements from: JdbcProvider.processMetaData
      case java.sql.Types.VARCHAR:
      case java.sql.Types.LONGVARCHAR:
      case java.sql.Types.NUMERIC:
      case java.sql.Types.DECIMAL:
      case java.sql.Types.BIT:
      case java.sql.Types.TINYINT:
      case java.sql.Types.SMALLINT:
      case java.sql.Types.INTEGER:
      case java.sql.Types.BIGINT:
      case java.sql.Types.REAL:
      case java.sql.Types.FLOAT:
      case java.sql.Types.DOUBLE:
      case java.sql.Types.BINARY:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.LONGVARBINARY:
      case java.sql.Types.DATE:
      case java.sql.Types.TIME:
      case java.sql.Types.TIMESTAMP:
        return true;
      default:
        return false;
    }
  }

  public String getProcedureQueryString(String procedureName, boolean forResultSet, boolean[] warnings) {
    return getImplementor().getProcedureQueryString(procedureName, forResultSet, warnings);
  }

  String getDatabaseProductName() throws MetaDataException {
    try {
      return getJdbcMetaData().getDatabaseProductName();
    }
    catch (SQLException ex) {
      MetaDataException.rethrowSQLException(ex);
    }
    catch(DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    return null;
  }
  private MetaDataImplementor implementor;
  private static int datagateway_remote_enabled;
  private static int datagateway_local_enabled;
}

class ObjectArrayEnumerator implements Enumeration
{
  ObjectArrayEnumerator(Object objArray[]) {
    this.objArray = objArray;
    nextIndex = 0;
  }

  public boolean hasMoreElements() {
    while (nextIndex < objArray.length && objArray[nextIndex] == null)
      nextIndex++;
    return nextIndex < objArray.length;
  }

  public Object nextElement() throws NoSuchElementException {
    if (hasMoreElements())
      return objArray[nextIndex++];
    else
      throw new NoSuchElementException();
  }
  int nextIndex;
  Object objArray[];
}

class IntArrayEnumerator implements Enumeration
{
  IntArrayEnumerator(int intArray[]) {
    this.intArray = intArray;
    nextIndex = 0;
  }

  public boolean hasMoreElements() {
    return nextIndex < intArray.length;
  }

  public Object nextElement() throws NoSuchElementException {
    if (hasMoreElements())
      return new Integer(intArray[nextIndex++]);
    else
      throw new NoSuchElementException();
  }

  int nextIndex;
  int intArray[];
}
