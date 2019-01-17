//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/MetaDataImplementor.java,v 7.2 2003/05/04 00:18:41 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.dx.dataset.Column;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;
import java.sql.*;

/**
*/
public class MetaDataImplementor
{
  public MetaDataImplementor(MetaData metaData) {
    this.metaData = metaData;
  }

  final void unsupportedColumnType(Column column) {
    String productName = metaData.getDatabaseProductName();
    MetaDataException.throwUnsupportedColumnType(column.getDataType(),productName);
  }
  public String columnToSQLDataType(Column column) throws MetaDataException {
    String  type  = null;

    switch (column.getDataType()) {
      case Variant.TIME:
        type = "TIME"; //NORES
        break;
      case Variant.DATE:
        type = "DATE"; //NORES
        break;
      case Variant.TIMESTAMP:
        type = "TIMESTAMP"; //NORES
        break;
      case Variant.STRING:
        int precision = column.getPrecision();
        if (column.getSqlType() == java.sql.Types.CHAR) {
          type = charDataType(precision);
        }
        else {
//          if (precision < 1)
//            precision = 200;
//          type = "VARCHAR("+precision+")"; //NORES
          if (precision > 0)
            type = "VARCHAR("+precision+")"; //NORES
          else
            type = "VARCHAR"; //NORES
        }
        break;
      case Variant.BIGDECIMAL:
        precision = column.getPrecision();
        if (precision < 0)
          type = "NUMERIC"; //NORES
        else
          type = "NUMERIC("+precision+","+column.getScale()+")"; //NORES
        break;
      case Variant.INT:
        type = "INT"; //NORES
        break;
      case Variant.SHORT:
        type = "SMALLINT"; //NORES
        break;
      case Variant.BYTE:
        type = "TINYINT"; //NORES
        break;
      case Variant.LONG:
        type = "LONG"; //NORES
        break;
      case Variant.BOOLEAN:
        type = "BIT"; //NORES
        break;
      case Variant.FLOAT:
        type = "FLOAT"; //NORES
        break;
      case Variant.DOUBLE:
        type = "DOUBLE"; //NORES
        break;
      case Variant.BYTE_ARRAY:
      case Variant.INPUTSTREAM:
        type = "VARBINARY"; //NORES
        break;
      default:
        unsupportedColumnType(column);
        break;
    }
    return type;
  }

/*  public String columnToSQLDataType(Column column) throws MetaDataException
   {
    MetaDataException.throwNotImplemented();
    return null;
  }
*/

  String charDataType(int precision) {
    if (precision < 0)
      return "CHAR";
    else
      return "CHAR("+precision+")"; //NORES
  }

  public String getProcedureQueryString(String procedureName, boolean forResultSet, boolean[] warnings) {
    if (warnings != null && warnings.length >= 3) {
      warnings[0] = false;
      warnings[1] = false;
      warnings[2] = false;
    }
    return procedureName;

//!/*
//!//  This code is OK, but it is too early to activate: the drivers simply doesn't give
//!//  reliable metadata information incl: Oracle jdbc, Sybase thin, Datagateway, misc ODBC.
//!//
//!    String  query = "call " + procedureName; //NORES
//!    String  ret   = "";
//!    String  params= "";
//!    boolean errors       = false;
//!    boolean hasResultSet = false;
//!    boolean areTypesOK   = true;
//!    try {
//!      DatabaseMetaData meta = metaData.getJdbcMetaData();
//!      ResultSet columns = meta.getProcedureColumns(null,null,procedureName,"%");
//!
//!      while (columns.next()) {
//!        String param = columns.getString(4);  // "COLUMN_NAME"  //NORES
//!        short  ptype = columns.getShort(5);    // "COLUMN_TYPE"  //NORES
//!        short  type  = columns.getShort(6);    // "DATA_TYPE"    //NORES
//!
//!        if (ptype == ParameterType.RESULT) {
//!          hasResultSet = true;
//!          continue;
//!        }
//!
//!        if (!metaData.isSqlTypeSupported(type))
//!          areTypesOK = false;
//!        if (ptype == ParameterType.RETURN)
//!          ret = ":" + param + " = ";
//!        else {
//!          if (params.length() > 0)
//!            params += ", ";
//!          params += ":" + param;
//!        }
//!        if (params.length() > 0)
//!          params = "(" + params + ")";
//!      }
//!      columns.close();
//!    }
//!    catch (Exception ex) {
//!      Diagnostic.printStackTrace(ex);
//!      errors = true;
//!    }
//!    if (warnings != null && warnings.length >= 3) {
//!      warnings[0] = errors;
//!      warnings[1] = forResultSet ? !hasResultSet : hasResultSet;
//!      warnings[2] = !areTypesOK;
//!    }
//!    return "{" + ret + query + params + "}";
//!*/
  }

  protected MetaData metaData;
}
