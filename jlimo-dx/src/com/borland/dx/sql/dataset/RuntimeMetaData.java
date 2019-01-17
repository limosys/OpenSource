//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/RuntimeMetaData.java,v 7.0 2002/08/08 18:39:57 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.DiagnosticJLimo;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.borland.dx.dataset.*;

/**


This class is used internally by other com.borland classes.
You should never use this class directly.
*/

public class RuntimeMetaData
{
  public static RuntimeMetaData getRuntimeMetaData(Database database) {
    RuntimeMetaData metaData = database.getRuntimeMetaData();
    if (metaData == null)
      metaData = new RuntimeMetaData(database);
    return metaData;
  }

  protected RuntimeMetaData(Database database) {
    this.database = database;
    database.setRuntimeMetaData(this);
    bitSettings   = new byte[MAX_BITSETTINGS];
    intSettings   = new  int[MAX_INTSETTINGS-MAX_BOOLEANSETTINGS];
  }

  protected RuntimeMetaData(RuntimeMetaData metaData) {
    this.database      = metaData.database;
    this.jdbcMetaData  = metaData.jdbcMetaData;
    database.setRuntimeMetaData(this);
    bitSettings   = new byte[MAX_BITSETTINGS];
    intSettings   = new  int[MAX_INTSETTINGS-MAX_BOOLEANSETTINGS];
  }

  protected void connectionClosed() {
    jdbcMetaData          = null;
    userName              = defUserName;
    resetBitValues();
  }

  /** This method should reside somewhere in the dataset package and replace
   *  JdbcProvider.processMetaData().
   */
  static Column[] processMetaData(Database database, int metaDataUpdate, ResultSet result) /*-throws DataSetException-*/ throws SQLException {
    ResultSetMetaData metaResult = result.getMetaData();

    int dialect = database.getSQLDialect();
    boolean isInterclient = (dialect & MASK_DRIVER) == JDBC4_INTERCLIENT;
    int resultColumns = metaResult.getColumnCount();
    ColumnList columnList = new ColumnList(resultColumns);

    int sqlType;
    String name;
    int precision;
    int displaySize;
    int scale;
    String label;
    boolean search;

    for (int index=1; index<=resultColumns; index++) {
      search  = true;
      sqlType  = metaResult.getColumnType(index);
      name     = metaResult.getColumnName(index);
      label    = name;

      // Some drivers (ie. Oracle's 7.3 driver) throw an exception instead of failing quietly.
      try { label = metaResult.getColumnLabel(index); }
      catch (SQLException se) {};

//      if ((metaDataUpdate&MetaDataUpdate.PRECISION) != 0) {
        precision   = metaResult.getPrecision(index);
        displaySize = metaResult.getColumnDisplaySize(index);
//      }
//      else {
//        precision   = -1;
//        displaySize = -1;
//      }

//      if ((metaDataUpdate&MetaDataUpdate.SCALE) != 0)
        scale       = metaResult.getScale(index);
//      else
//        scale       = 0;

      if (sqlType == java.sql.Types.LONGVARBINARY || sqlType == java.sql.Types.LONGVARCHAR)
        search = false;  //08373 : Make blobs non searchable by default.
      else if ((metaDataUpdate&MetaDataUpdate.SEARCHABLE) != 0) {
        try { search = metaResult.isSearchable(index); }
        catch (SQLException se) { };
      }

      // Oracle thin special case:  (JB3.0)
      //   The Oracle numeric types: FLOAT, REAL, DOUBLE PRECISION,
      //   are being specified as BigDecimals with a scale of -127.
      //   They are really floating point types, that are not supported by
      //   Java. We choose to handle them as doubles (less precision) and
      //   dont include them in the where clause of updates.
      if (scale == -127 && sqlType == java.sql.Types.NUMERIC && ((dialect & MASK_DRIVER) == JDBC4_ORACLE)) {
        sqlType = java.sql.Types.DOUBLE;
        scale   = 0;
        search  = false;
      }

      // Interclient is special:
      //   The columnName of the Result "table" is returned in getColumnLabel() instead of getColumnName()
      //   Instead the name of a physical column from a real table projected in the result is returned in getColumnName()
      if (isInterclient)
        name = label;

      // Guard against null column names
      if (name == null)
        name = "";

      int count = 1;
      int initialLength = name.length();

      // BUG 15699, 17529
      //    Some drivers return empty ColumnNames, set these to "CALC" and
      //    rely on the logic below to make it unique.
      if (initialLength == 0) {
        name = "CALC_1";
        initialLength = 5;
        count = 2;
      }

      // Some query results can evidently return the same column name for more
      // than one column.  In this case, fabricate a name.
      //
      while(columnList.findOrdinal(name) != -1) {
        name  = name.substring(0,initialLength)+count;
        ++count;
      }

      columnList.addColumn( createColumn(name,label,sqlType,precision,displaySize,scale,search) );
    }
    return columnList.getColumnsArray();
  }

  static protected Column createColumn(String name, String label, int sqlType, int precision, int displaySize, int scale, boolean search)
    /*-throws DataSetException-*/
  {

    Column column = new Column();
    column.setColumnName(name);
    column.setServerColumnName(name);
    column.setCaption(label);
    column.setSearchable(search);
    getTypeInfo(column, sqlType, precision, displaySize, scale);
    return column;
  }

  /**
      @since JB3.00
      returns sqlType that variant type maps to.
  */
  static int variantTypeToSqlType(int variantType) /*-throws DataSetException-*/
  {
    switch(variantType)
    {
      case Variant.STRING:
        return java.sql.Types.VARCHAR;

      case Variant.BIGDECIMAL:
        return java.sql.Types.NUMERIC;

      case Variant.BOOLEAN:
        return java.sql.Types.BIT;

      case Variant.BYTE:
        return java.sql.Types.TINYINT;

      case Variant.SHORT:
        return java.sql.Types.SMALLINT;

      case Variant.INT:
        return java.sql.Types.INTEGER;

      case Variant.LONG:
        return java.sql.Types.BIGINT;

      case Variant.FLOAT:
        return java.sql.Types.REAL;

      case Variant.DOUBLE:
        return java.sql.Types.DOUBLE;

      case Variant.INPUTSTREAM:
        return java.sql.Types.BINARY;

      case Variant.TIMESTAMP:
        return java.sql.Types.TIMESTAMP;

      case Variant.DATE:
        return java.sql.Types.DATE;

      case Variant.TIME:
        return java.sql.Types.TIME;

      case Variant.OBJECT:
        return java.sql.Types.OTHER;

      default:
        DataSetException.unrecognizedDataType();
        break;
    }

    return 0;
  }

  /**
      @since JB2.01
      returns variant type that sqlType maps to.
  */
  public static int sqlTypeToVariantType(int sqlType)
    /*-throws DataSetException-*/
  {
    return getTypeInfo(null, sqlType, -1, -1, -1);
  }

  static int getTypeInfo(Column column, int sqlType, int precision, int displaySize, int scale)
    /*-throws DataSetException-*/
  {
    int type      = Variant.STRING;
    int alignment = Alignment.RIGHT | Alignment.MIDDLE;

    switch (sqlType) {
      // Don't set the precision for blob fields, they are usually open-ended.
      case java.sql.Types.LONGVARCHAR:
        type        = Variant.STRING;
        alignment   = Alignment.LEFT | Alignment.MIDDLE;
        precision   = -1;
        scale       = -1;
        break;

      case -8: // Oracle ROWID is a String:
        precision   = -1;
      case java.sql.Types.CHAR:
      case java.sql.Types.VARCHAR:
        type        = Variant.STRING;
        alignment   = Alignment.LEFT | Alignment.MIDDLE;
        scale       = -1;
        if (precision == 0 && displaySize > 0)
          precision = displaySize;
        break;

      case java.sql.Types.NUMERIC:
      case java.sql.Types.DECIMAL:
        type        = Variant.BIGDECIMAL;

        //!RC TODO test
        // If scale and precision are both zero, assume it means the driver
        // just doesn't know and propagate -1 (which means don't know)
        if (scale == 0 && precision == 0)
          scale = precision = -1;

        //

        //!JOAL
        // If scale is negative (Oracle allows the range -48..127)
        // set it to 0 (meaning no decimals)
        if (scale < 0)
          scale = 0;

        break;

      case java.sql.Types.BIT:
        type      = Variant.BOOLEAN;
        precision = scale = -1;
        break;

      case java.sql.Types.TINYINT:
        type      =  Variant.BYTE;
        precision = scale = -1;
        break;

      case java.sql.Types.SMALLINT:
        type      =  Variant.SHORT;
        precision = scale = -1;
        break;

      case java.sql.Types.INTEGER:
        type      =  Variant.INT;
        precision = scale = -1;
        break;

      case java.sql.Types.BIGINT:
        type      =  Variant.LONG;
        precision = scale = -1;
        break;

      case java.sql.Types.REAL:
        type      =  Variant.FLOAT;
        precision = scale = -1;
        break;
      case java.sql.Types.FLOAT:
      case java.sql.Types.DOUBLE:
        type      =  Variant.DOUBLE;
        precision = scale = -1;
        break;

      case java.sql.Types.VARBINARY:
      case java.sql.Types.LONGVARBINARY:
        precision   = -1;
      case java.sql.Types.BINARY:
        type        = Variant.INPUTSTREAM;
        alignment   = Alignment.LEFT | Alignment.MIDDLE;
        scale       = -1;
        break;

      case java.sql.Types.DATE:
        type        = Variant.DATE;
        precision   = scale = -1;
        break;

      case java.sql.Types.TIME:
        type        =  Variant.TIME;
        precision   = scale = -1;
        break;

      case java.sql.Types.TIMESTAMP:
        type        =  Variant.TIMESTAMP;
        precision   = scale = -1;
        break;

      case java.sql.Types.OTHER:
      case java.sql.Types.JAVA_OBJECT:
        type        =  Variant.OBJECT;
        precision   = scale = -1;
        break;

      default:
        DiagnosticJLimo.fail();
        break;
    }
    if (column != null) {
      column.setDataType(type);
      column.setScale(scale);
      column.setPrecision(precision);
      column.setAlignment(alignment);
      column.setSqlType(sqlType);
    }
    return type;
  }


  protected final char getIdentifierQuoteChar() {
    if (quoteCharacter == INVALID_QUOTE_CHAR) {
      // Get the quote character. If no support, default quote character to 0.
      quoteCharacter = '\0';
      try{
        String string = getJdbcMetaData().getIdentifierQuoteString();
        if (string.length() > 0)
          quoteCharacter = string.charAt(0);

        if (quoteCharacter == ' ')
          quoteCharacter = '\0';
      }
      catch (Throwable ex) {
        quoteCharacter  = '\0';
      }
    }
    return (char)quoteCharacter;
  }

  protected String getUserName() {
    if (userName == defUserName) {
      try {
        userName = getJdbcMetaData().getUserName();
      }
      catch (Throwable ex) {
        userName = null;
      }
    }
    return userName;
  }

  public synchronized final DatabaseMetaData getJdbcMetaData() /*-throws DataSetException-*/ {
    if (jdbcMetaData == null) {
      java.sql.Connection connection = database.getJdbcConnection();
      try {
        jdbcMetaData  = connection.getMetaData();
      }
      catch (SQLException sex) {
        DataSetException.SQLException(sex);
      }
    }
    return jdbcMetaData;
  }

  private synchronized void analyzeSqlDialect() {
    int sqlDriver, sqlDialect;
    try {
      String url = getJdbcMetaData().getURL();
      if (url.startsWith("jdbc:borland:ds"))   //NORES
        sqlDriver = JDBC4_JDATASTORE;
      else if (url.startsWith("jdbc:BorlandBridge:"))   //NORES
        sqlDriver = JDBC2_DATAGATEWAY;
      else if (url.startsWith("jdbc:BorlandBroker:"))   //NORES
        sqlDriver = JDBC4_DATAGATEWAY;
      else if (url.startsWith("jdbc:oracle:oci"))       //NORES
        sqlDriver = JDBC2_ORACLE;
      else if (url.startsWith("jdbc:oracle:thin:"))     //NORES
        sqlDriver = JDBC4_ORACLE;
      else if (url.startsWith("jdbc:db2://"))           //NORES
        sqlDriver = JDBC4_DB2;
      else if (url.startsWith("jdbc:db2:"))             //NORES
        sqlDriver = JDBC2_DB2;
      else if (url.startsWith("jdbc:interbase:"))       //NORES
        sqlDriver = JDBC4_INTERCLIENT;
      else if (url.startsWith("jdbc:sybase:Tds:"))      //NORES
        sqlDriver = JDBC4_JCONNECT;
      else if (url.startsWith("jdbc:odbc:")) {          //NORES
        String driverName = getJdbcMetaData().getDriverName();
        String part = driverName.substring(17);
        if (!driverName.substring(0,17).equalsIgnoreCase("JDBC-ODBC Bridge ")) //NORES
          sqlDriver = UNKNOWN_DRIVER;
        else if (part.equalsIgnoreCase("(iscdrv32.DLL)") ||  // Interbase      //NORES
                 part.equalsIgnoreCase("(VSORAC32.DLL)") ||  // Oracle         //NORES
                 part.equalsIgnoreCase("(vssyb32.DLL)"))     // Sybase         //NORES
          sqlDriver = ODBC_VISIGENIC;
        else if (part.equalsIgnoreCase("(SQO32_73.DLL)"))                      //NORES
          sqlDriver = ODBC_ORACLE;
        else
          sqlDriver = ODBC_UNKNOWN;
      }
      else
        sqlDriver = UNKNOWN_DRIVER;
    }
    catch (Throwable ex) {
      sqlDriver = UNKNOWN_DRIVER;
    }

    // Find the database product:
    try {
      String productName = getJdbcMetaData().getDatabaseProductName();
      if      (productName.equalsIgnoreCase("Interbase") ||   // Visigenic ODBC //NORES
//!            productName.equalsIgnoreCase("InterBase") ||   // InterClient    //NORES
               productName.equalsIgnoreCase("INTRBASE"))      // BorlandBridge  //NORES
        sqlDialect = DB_INTERBASE;
      else if (productName.equalsIgnoreCase("Oracle") ||      // Visigenic ODBC //NORES
//!             productName.equalsIgnoreCase("Oracle") ||      // Oracle JDBC    //NORES
               productName.equalsIgnoreCase("Oracle7"))       // Oracle ODBC    //NORES
        sqlDialect = DB_ORACLE;
      else if (productName.equalsIgnoreCase("DBASE"))         // BorlandBridge  //NORES
        sqlDialect = DB_DBASE;
      else if (productName.equalsIgnoreCase("PARADOX"))       // BorlandBridge  //NORES
        sqlDialect = DB_PARADOX;
      else if (productName.startsWith("DB2"))                 // DB2            //NORES
        sqlDialect = DB_DB2;
      else if (productName.length() >= 6 && productName.substring(0,6).equalsIgnoreCase("SYBASE"))
        sqlDialect = DB_SYBASE;
      else if (sqlDriver == JDBC4_JDATASTORE)
        sqlDialect = DB_JDATASTORE;
      else
        sqlDialect = DB_UNKNOWN;
    }
    catch (Throwable ex) {
      sqlDialect = DB_UNKNOWN;
    }
    intSettings[INT_SQLDIALECT-MAX_BOOLEANSETTINGS] = sqlDriver + sqlDialect;
  }

  void writeMetaInfo(ObjectOutput stream) throws IOException {
    byte[] bits = new byte[MAX_BOOLEANSETTINGS_SAVED];
    System.arraycopy(bitSettings,0,bits,0,MAX_BOOLEANSETTINGS_SAVED);
    stream.writeObject(bits);
    stream.writeObject(null);  // reserved for future use
    stream.writeObject(null);  // reserved for future use
    stream.writeObject(null);  // reserved for future use
  }

  void readMetaInfo(ObjectInput stream) throws IOException, ClassNotFoundException {
    byte[] bits = (byte[])stream.readObject();
    int len = bits == null ? 0 : bits.length;
    len = Math.min(len,MAX_BOOLEANSETTINGS_SAVED);
    if (len > 0)
      System.arraycopy(bits,0,bitSettings,0,len);

    // Skip 3 objects for reserved for future use
    stream.readObject();
    stream.readObject();
    stream.readObject();
  }

  void setBooleanValue(int index, boolean value) {
    DiagnosticJLimo.check( 0 <= index && index < MAX_BOOLEANSETTINGS );
    bitSettings[index] = (byte)(value ? HAS_MEANING + WAS_SET + IS_TRUE : HAS_MEANING + WAS_SET);
  }

  boolean getBooleanValue(int index) {
    DiagnosticJLimo.check( 0 <= index && index < MAX_BOOLEANSETTINGS );
    if ((bitSettings[index] & HAS_MEANING) == 0)
      getDefaultValue(index);
    return (bitSettings[index] & IS_TRUE) != 0;
  }

  void setIntValue(int index, int value) {
    DiagnosticJLimo.check( MAX_BOOLEANSETTINGS <= index && index < MAX_BITSETTINGS );
    bitSettings[index] = HAS_MEANING + WAS_SET;
    intSettings[index-MAX_BOOLEANSETTINGS] = value;
  }

  protected int getIntValue(int index) {
    DiagnosticJLimo.check( MAX_BOOLEANSETTINGS <= index && index < MAX_BITSETTINGS );
    if ((bitSettings[index] & HAS_MEANING) == 0)
      getDefaultValue(index);
    return intSettings[index-MAX_BOOLEANSETTINGS];
  }

  private void resetBitValues() {
    for (int i=0; i<MAX_BITSETTINGS; i++) {
      if ((bitSettings[i] & WAS_SET) == 0)
        bitSettings[i] = 0;
    }
  }

  private void getDefaultValue(int index) {
    int sqlDialect;
    bitSettings[index] = HAS_MEANING;
    switch (index) {
      // IBM requires the shema name.
      case USE_SCHEMANAME:
        sqlDialect = getIntValue(INT_SQLDIALECT);
        if ((sqlDialect & MASK_JDBC) == IS_DB2_JDBC)
          bitSettings[index] += IS_TRUE;
        break;

      // Local DataGateway i.e. Paradox and dBase needs the tableName to prefix
      // the quoted fieldnames, since quoted identifiers are not really supported
      // by the SQL parser in BDE.
      case USE_TABLENAME:
//!    case USE_DBASE_PARADOX:
        sqlDialect = getIntValue(INT_SQLDIALECT);
        if ((sqlDialect & MASK_JDBC) == IS_DATAGATEWAY) {
          sqlDialect &= MASK_DIALECT;
          if (sqlDialect == DB_DBASE || sqlDialect == DB_PARADOX)
            bitSettings[index] += IS_TRUE;
        }
        break;

      // The Oracle JDBC drivers does not accept CHAR(n) fields unless the
      // string has space padding up to the length of the field.
      case USE_SPACEPADDING:
        sqlDialect = getIntValue(INT_SQLDIALECT);
        if ((sqlDialect & MASK_JDBC) == IS_ORACLE_JDBC)
          bitSettings[index] += IS_TRUE;
        break;

      // JBuilder will by default use an optimistic statement caching scheme,
      // where statements are not being closed in order to reuse the resource
      // later. Certain database drivers don't handle this well.
      // The user can now override this default.
      case USE_STATEMENTCACHING:
        if (getIntValue(RuntimeMetaData.INT_MAXSTATEMENTS) > 9)
          bitSettings[index] += IS_TRUE;
        break;

      // Use the setObject call instead of setString if possible.
      // Some drivers need to know the sqlType to successfully store strings.
      // (VARCHAR or CHAR).
      case USE_SETOBJECTFORSTRINGS:
        bitSettings[index] += IS_TRUE;
        break;

      // Use the setObject call instead of setBinaryStream if possible.
      // Some drivers need to know the sqlType to successfully store blobs.
      // However the SUN ODBC driver has a bug in setObject for blobs, where
      // the blobs are chopped off to 2000 bytes.
      case USE_SETOBJECTFORSTREAMS:
        sqlDialect = getIntValue(INT_SQLDIALECT);
        if ((sqlDialect & MASK_ODBC) != IS_ODBC)
          bitSettings[index] += IS_TRUE;
        break;

      case USE_TRANSACTIONS:
        try {
          if (getJdbcMetaData().supportsTransactions())
            bitSettings[index] += IS_TRUE;
        }
        catch (Throwable ex) {
        }
        break;

      // Does the database treat SQL identifiers prefer case insensitive and store them in lowercase:
      case USE_LOWERCASEIDENTIFIERS:
        try {
          if (getJdbcMetaData().storesLowerCaseIdentifiers())
            bitSettings[index] += IS_TRUE;
        }
        catch (Throwable ex) {
        }
        break;

      // Does the database treat SQL identifiers prefer case insensitive and store them in uppercase:
      case USE_UPPERCASEIDENTIFIERS:
        try {
          if (getJdbcMetaData().storesUpperCaseIdentifiers())
            bitSettings[index] += IS_TRUE;
        }
        catch (Throwable ex) {
        }
        break;

      // Does the database treat SQL identifiers prefer case insensitive and store them in mixed case:
      case USE_CASESENSITIVEID:
        try {
          if (!(getBooleanValue(USE_UPPERCASEIDENTIFIERS) ||   //! if (!(metadata.storesUpperCaseIdentifiers() ||
                getBooleanValue(USE_LOWERCASEIDENTIFIERS) ||   //!       metadata.storesLowerCaseIdentifiers() ||
                getBooleanValue(USE_DBASE_PARADOX)))           //!       metadata.storesMixedCaseIdentifiers()))   note: Most drivers respond incorrectly to this metadata call including: DataGateway, FastForward
          {
            bitSettings[index] += IS_TRUE;
          }
        }
        catch (Throwable ex) {
        }
        break;

      case USE_CASESENSITIVEQUOTEDID:
        try {
          DatabaseMetaData metadata = getJdbcMetaData();
          if (!(metadata.storesUpperCaseQuotedIdentifiers() ||   //! if (!(metadata.storesUpperCaseQuotedIdentifiers() ||
                getBooleanValue(USE_LOWERCASEQUOTEDIDS)     ||   //!       metadata.storesLowerCaseQuotedIdentifiers() ||
                getBooleanValue(USE_DBASE_PARADOX)))             //!       metadata.storesMixedCaseQuotedIdentifiers()))   note: Most drivers respond incorrectly to this metadata call including: DataGateway, FastForward
          {
            bitSettings[index] += IS_TRUE;
          }
        }
        catch (Throwable ex) {
        }
        break;

      // Does the database treat quoted SQL identifiers prefer case insensitive and store them in lowercase:
      case USE_LOWERCASEQUOTEDIDS:
        try {
          if (getJdbcMetaData().storesLowerCaseQuotedIdentifiers())
            bitSettings[index] += IS_TRUE;
        }
        catch (Throwable ex) {
        }
        break;

      // The Oracle JDBC drivers and DataGateway are the only ones known today:
      case USE_ORACLERESULTSET:
        sqlDialect = getIntValue(INT_SQLDIALECT);
        if ((sqlDialect & MASK_DIALECT) == RuntimeMetaData.DB_ORACLE) {
          sqlDialect &= MASK_JDBC;
          if (sqlDialect == IS_ORACLE_JDBC || sqlDialect == IS_DATAGATEWAY)
            bitSettings[index] += IS_TRUE;
        }
        break;

      // Any Oracle driver should make use of the Oracle ROWID:
      case USE_ORACLEROWID:
        sqlDialect = getIntValue(INT_SQLDIALECT);
        if ((sqlDialect & MASK_DIALECT) == RuntimeMetaData.DB_ORACLE)
          bitSettings[index] += IS_TRUE;
        break;

      case REUSE_SAVE_STATEMENTS:
        bitSettings[index] += IS_TRUE;
        break;

      case USE_CLEAR_PARAMETERS:
        bitSettings[index] += IS_TRUE;
        break;

      // Cache the Maximal number of open statements:
      case INT_MAXSTATEMENTS:
        int maxStatements;
        try {
          maxStatements = getJdbcMetaData().getMaxStatements();
          if (maxStatements < 1)
            maxStatements = 1;
        }
        catch (Throwable ex) {
          maxStatements = 1;
        }
        intSettings[index-MAX_BOOLEANSETTINGS] = maxStatements;
        break;

      // Find the SQL Dialect:
      case INT_SQLDIALECT:
        analyzeSqlDialect();
        break;

      // Find the Identifier quite character:
      case INT_QUOTECHAR:
        //!TOJENS.  this looks like a repeat of the code in getIdentifierQuoteChar()
        //!        ***** JENS LOOK AT THIS ***** -Steve.
        //
        char quoteCharacter = '\0';
        try {
          String string = getJdbcMetaData().getIdentifierQuoteString();
          if (string.length() > 0)
            quoteCharacter = string.charAt(0);
          if (quoteCharacter == ' ')
            quoteCharacter = '\0';
        }
        catch (Throwable ex) {
          quoteCharacter  = '\0';
        }
        intSettings[index-MAX_BOOLEANSETTINGS] = (int)quoteCharacter;
        break;

      default:
        DiagnosticJLimo.check(false);
    }
  }

/******************************************************************************
  Explanation of bit fields:
  A. boolean values are kept in bitSettings with the following bitwise meaning:
      0 : Has meaningfull value
      1 : Was set by user
      2 : Value true/false
  B. int values are kept in intSettings, and have a byte in bitSettings where
     bit 0 and 1 are used with the same meaning as boolean values.
******************************************************************************/
            static final int    USE_SCHEMANAME           = 0;     //saved
  private   static final int    USE_DBASE_PARADOX           = 1;  //not saved
            static final int    USE_TABLENAME            = 1;     //saved
            static final int    USE_SPACEPADDING         = 2;     //saved
            static final int    USE_STATEMENTCACHING     = 3;     //saved
            static final int    USE_SETOBJECTFORSTRINGS  = 4;     //saved
            static final int    USE_SETOBJECTFORSTREAMS  = 5;     //saved
            static final int    USE_TRANSACTIONS         = 6;     //saved
  private   static final int    MAX_BOOLEANSETTINGS_SAVED   = 7;
            static final int    USE_LOWERCASEIDENTIFIERS = 7;     //not saved
            static final int    USE_UPPERCASEIDENTIFIERS = 8;     //not saved
            static final int    USE_LOWERCASEQUOTEDIDS   = 9;     //not saved
            static final int    USE_CASESENSITIVEID      = 10;    //not saved
            static final int    USE_CASESENSITIVEQUOTEDID= 11;    //not saved
            static final int    USE_ORACLERESULTSET      = 12;    //not saved
            static final int    USE_ORACLEROWID          = 13;    //not saved
            static final int    REUSE_SAVE_STATEMENTS    = 14;    //not saved
            static final int    USE_CLEAR_PARAMETERS     = 15;    //not saved

  private   static final int    MAX_BOOLEANSETTINGS      = 16;
  private   static final int    MAX_INTSETTINGS_SAVED    = 16;
            static final int    INT_MAXSTATEMENTS        = 16;    //not saved
  protected static final int    INT_SQLDIALECT           = 17;    //not saved
            static final int    INT_QUOTECHAR            = 19;    //not saved

  private   static final int    MAX_BITSETTINGS             = 20;
  private   static final int    MAX_INTSETTINGS             = MAX_BITSETTINGS;

  private   static final byte   HAS_MEANING = 1;
  private   static final byte   IS_TRUE     = 2;
  private   static final byte   WAS_SET     = 4;

  public    static final int    DB_UNKNOWN         = 0x0000;
  public    static final int    DB_INTERBASE       = 0x0001;
  public    static final int    DB_ORACLE          = 0x0002;
  public    static final int    DB_SYBASE          = 0x0003;
  public    static final int    DB_DB2             = 0x0004;
  public    static final int    DB_DBASE           = 0x0005;
  public    static final int    DB_PARADOX         = 0x0006;
  public    static final int    DB_JDATASTORE      = 0x0007;

  public    static final int    INVALID_DRIVER     = 0xffffFFFF;
  public    static final int    UNKNOWN_DRIVER     = 0x0000;
  public    static final int    ODBC_UNKNOWN       = 0x1000;
  public    static final int    ODBC_VISIGENIC     = 0x1100;
  public    static final int    ODBC_ORACLE        = 0x1200;

  public    static final int    JDBC4_DATAGATEWAY  = 0x2000;
  public    static final int    JDBC4_ORACLE       = 0x2100;
  public    static final int    JDBC4_DB2          = 0x2200;
  public    static final int    JDBC4_INTERCLIENT  = 0x2300;
  public    static final int    JDBC4_JCONNECT     = 0x2400;
  public    static final int    JDBC4_JDATASTORE   = 0x2500;

  public    static final int    JDBC2_DATAGATEWAY  = 0x3000;
  public    static final int    JDBC2_ORACLE       = 0x3100;
  public    static final int    JDBC2_DB2          = 0x3200;

  // sqlDialect masks:
  protected static final int    MASK_DRIVER        = 0xFF00;
  protected static final int    MASK_DIALECT       = 0x00FF;
  private   static final int    MASK_ODBC          = 0xF000;
  private   static final int    IS_ODBC            = 0x1000;
  private   static final int    MASK_JDBC          = 0xEF00;
  private   static final int    IS_DATAGATEWAY     = 0x2000;
  private   static final int    IS_ORACLE_JDBC     = 0x2100;
  private   static final int    IS_DB2_JDBC        = 0x2200;

  protected Database            database;
  protected DatabaseMetaData    jdbcMetaData;
  private   static final String defUserName           = "<DEF>";
  private   String              userName              = defUserName;
  private   static final int    INVALID_QUOTE_CHAR    = 0xffFFFF;
  private   int                 quoteCharacter        = INVALID_QUOTE_CHAR;
  private   byte[]              bitSettings;
  private   int[]               intSettings;
}

