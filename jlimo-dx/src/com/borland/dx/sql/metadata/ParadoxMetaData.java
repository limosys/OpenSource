//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/ParadoxMetaData.java,v 7.0 2002/08/08 18:40:08 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.*;

import com.borland.dx.dataset.Column;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;

/**
 *
 */
class ParadoxMetaData extends MetaDataImplementor
{
  ParadoxMetaData(MetaData metaData) {
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
      case Variant.SHORT:
      case Variant.FLOAT:
        return super.columnToSQLDataType(column);
      case Variant.STRING:
        if (column.getSqlType() == java.sql.Types.CHAR)
          return super.columnToSQLDataType(column);
        int precision = column.getPrecision();
        if (precision > 255)
          type = "BLOB(240,1)"; //NORES
        else {
          if (precision < 0)
            precision = 255;
          type = "CHAR("+precision+")"; //NORES
        }
        break;
      case Variant.BIGDECIMAL:
        type = "DECIMAL("+column.getPrecision()+","+column.getScale()+")"; //NORES
        break;
      case Variant.INT:
        type = "INTEGER"; //NORES
        break;
      case Variant.LONG:
        type = "NUMERIC"; //NORES   //NB: Actually not supported
        break;
      case Variant.BOOLEAN:
        type = "BOOLEAN"; //NORES
        break;
      case Variant.DOUBLE:
        type = "NUMERIC"; //NORES
        break;
      case Variant.BYTE_ARRAY:
      case Variant.INPUTSTREAM:
        type = "BLOB(240,2)"; //NORES
        break;
      default:
        unsupportedColumnType(column);
        break;
    }
    return type;
  }
}
