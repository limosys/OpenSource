//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/SybaseMetaData.java,v 7.0 2002/08/08 18:40:08 jlaurids Exp $
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
class SybaseMetaData extends MetaDataImplementor
{
  SybaseMetaData(MetaData metaData) {
    super(metaData);
  }

  public String columnToSQLDataType(Column column) throws MetaDataException
  {
    String  type  = null;

    switch (column.getDataType()) {
      case Variant.TIME:
      case Variant.DATE:
      case Variant.TIMESTAMP:
        type = "datetime"; //NORES
        break;
      case Variant.STRING:
        if (column.getSqlType() == java.sql.Types.CHAR)
          return super.columnToSQLDataType(column);
        int precision = column.getPrecision();
        DiagnosticJLimo.trace(Trace.MetaData, "String precision: "+precision); //!NORES
        if (precision < 1)
          precision = 16000;  // Allocations in 2K intervals
        if (precision < 256)
          type = "nvarchar("+precision+")"; //NORES
        else
          type = "text"; //NORES
        break;
      case Variant.BIGDECIMAL:
      case Variant.INT:
      case Variant.SHORT:
      case Variant.FLOAT:
        return super.columnToSQLDataType(column);
      case Variant.LONG:
        type = "numeric(20,0)"; //NORES
        break;
      case Variant.BOOLEAN:
        type = "tinyint";   //NORES
        break;
      case Variant.DOUBLE:
        type = "double precision"; //NORES
        break;
      case Variant.BYTE_ARRAY:
      case Variant.INPUTSTREAM:
        type = "image";   //NORES
        break;
      default:
        String productName = metaData.getDatabaseProductName();
        MetaDataException.throwUnsupportedColumnType(column.getDataType(),productName);
        break;
    }
    return type;
  }
}
