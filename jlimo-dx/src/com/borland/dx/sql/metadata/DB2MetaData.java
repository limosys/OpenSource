//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/DB2MetaData.java,v 7.0 2002/08/08 18:40:05 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ParameterType;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.Variant;

import java.sql.*;

/**
 *
 */
class DB2MetaData extends MetaDataImplementor
{
  DB2MetaData(MetaData metaData) {
    super(metaData);
  }

  public String columnToSQLDataType(Column column) throws MetaDataException {
    String  type  = null;

    switch (column.getDataType()) {
      case Variant.TIME:
      case Variant.DATE:
      case Variant.TIMESTAMP:
      case Variant.BIGDECIMAL:
      case Variant.INT:
      case Variant.SHORT:
      case Variant.DOUBLE:
        return super.columnToSQLDataType(column);
      case Variant.STRING:
        if (column.getSqlType() == java.sql.Types.CHAR)
          return super.columnToSQLDataType(column);
        int precision = column.getPrecision();
        if (precision < 1)
//!        type = "DBCLOB(2G)";
          type = "VARCHAR(32700)"; //!NORES
        else if (precision <= 32700)
          type = "VARCHAR("+precision+")"; //!NORES
//!/*      else if ((precision = div1024(precision)) < 1024)
//!          type = "DBCLOB("+precision+"K)";
//!        else if ((precision = div1024(precision)) < 1024)
//!          type = "DBCLOB("+precision+"M)";
//!        else if ((precision = div1024(precision)) < 1024)
//!          type = "DBCLOB("+precision+"G)";
//!        else
//!          type = "DBCLOB(2G)";
//!*/      else
          type = "VARCHAR(32700)"; //!NORES
        break;
      case Variant.LONG:
        type = "NUMERIC(20,0)"; //NORES
        break;
      case Variant.BOOLEAN:
        type = "CHAR"; //NORES
        break;
      case Variant.FLOAT:
        type = "REAL"; //NORES
        break;
      case Variant.BYTE_ARRAY:
      case Variant.INPUTSTREAM:
        type = "LONG VARCHAR FOR BIT DATA"; //NORES
        break;
      default:
        String productName = metaData.getDatabaseProductName();
        MetaDataException.throwUnsupportedColumnType(column.getDataType(),productName);
        break;
    }
    return type;
  }

  private int div1024(int number) {
    int rest  = number & 1023;
    int value = number >> 10;
    if (rest > 0)
      value++;
    return value;
  }
}
