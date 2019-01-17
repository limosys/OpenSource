//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/Oracle7MetaData.java,v 7.0 2002/08/08 18:40:07 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.*;

import com.borland.dx.dataset.Column;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.Variant;

/**
 *
 */
class Oracle7MetaData extends MetaDataImplementor
{
  Oracle7MetaData(MetaData metaData) {
    super(metaData);
  }

  public String columnToSQLDataType(Column column)
    throws MetaDataException
  {
    String  type  = null;

    switch (column.getDataType()) {
      case Variant.TIME:
      case Variant.DATE:
      case Variant.TIMESTAMP:
        type = "DATE"; //NORES
        break;
      case Variant.STRING:
        if (column.getSqlType() == java.sql.Types.CHAR)
          return super.columnToSQLDataType(column);
        int precision = column.getPrecision();
        if (precision < 1 || precision > 2000)
          precision = 2000; // Oracle7 limit of VARCHAR and VARCHAR2
        if (precision < 16)
          type = "CHAR("+precision+")"; //NORES
        else
          type = "VARCHAR2("+precision+")"; //NORES
        break;
      case Variant.BIGDECIMAL:
        super.columnToSQLDataType(column);
        break;
      case Variant.INT:
      case Variant.SHORT:
      case Variant.LONG:
        type = "NUMBER(38)"; //NORES
        break;
      case Variant.BOOLEAN:
        type = "CHAR(1)"; //NORES
        break;
      case Variant.FLOAT:
      case Variant.DOUBLE:
        type = "NUMBER"; //NORES
        break;
      case Variant.BYTE_ARRAY:
        type = "LONG";  // Possibly "LONG RAW" if no character conversion by Oracle //NORES
        break;
      case Variant.INPUTSTREAM:
        type = "LONG RAW"; //NORES
        break;
      default:
        unsupportedColumnType(column);
        break;
    }
    return type;
  }
}
