//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/dBaseMetaData.java,v 7.0 2002/08/08 18:40:08 jlaurids Exp $
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
class dBaseMetaData extends MetaDataImplementor
{
  dBaseMetaData(MetaData metaData) {
    super(metaData);
  }

  public String columnToSQLDataType(Column column)
    throws MetaDataException
  {
    String  type  = null;

    switch (column.getDataType()) {
      case Variant.TIME:     //NB: Actually not supported
      case Variant.DATE:     //NB: Actually not supported
      case Variant.TIMESTAMP:
        type = "DATE"; //NORES
        break;
      case Variant.STRING:
        if (column.getSqlType() == java.sql.Types.CHAR)
          return super.columnToSQLDataType(column);
        int precision = column.getPrecision();
        if (precision > 254)
          type = "BLOB(10,1)"; //NORES
        else {
          if (precision < 0)
            precision = 254;
          type = "CHAR("+precision+")"; //NORES
        }
        break;
      case Variant.BIGDECIMAL:
        return super.columnToSQLDataType(column);
      case Variant.INT:
        type = "NUMERIC(10,0)"; //NORES   //NB: Actually not supported
        break;
      case Variant.SHORT:
        type = "NUMERIC(5,0)"; //NORES   //NB: Actually not supported
        break;
      case Variant.LONG:
        type = "NUMERIC(20,0)"; //NORES   //NB: Actually not supported
        break;
      case Variant.BOOLEAN:
        type = "BOOLEAN"; //NORES
        break;
      case Variant.BYTE_ARRAY:
      case Variant.INPUTSTREAM:
        type = "BLOB(10,2)"; //NORES
        break;
      case Variant.FLOAT:
      case Variant.DOUBLE:
      default:
        String productName = metaData.getDatabaseProductName();
        MetaDataException.throwUnsupportedColumnType(column.getDataType(),productName);
        break;
    }
    return type;
  }
}

