//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/InterbaseMetaData.java,v 7.0 2002/08/08 18:40:06 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.*;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ParameterType;
import com.borland.dx.dataset.DataSetException;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.Variant;

/**
 *
 */
class InterbaseMetaData extends MetaDataImplementor
{
  InterbaseMetaData(MetaData metaData) {
    super(metaData);
  }

  public String columnToSQLDataType(Column column) throws MetaDataException {
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
        if (precision > 32765)
          type = "BLOB(1,1)"; //NORES
        else {
          if (precision < 1)
            precision = 200;
          type = "VARCHAR("+precision+")"; //NORES
        }
        break;
      case Variant.BIGDECIMAL:
      case Variant.INT:
      case Variant.FLOAT:
      case Variant.SHORT:
        return super.columnToSQLDataType(column);
      case Variant.LONG:
        type = "BIGINT"; //NORES
        break;
      case Variant.BOOLEAN:
        type = "CHAR(1)"; //NORES
        break;
      case Variant.DOUBLE:
        type = "DOUBLE PRECISION"; //NORES
        break;
      case Variant.BYTE_ARRAY:
      case Variant.INPUTSTREAM:
        type = "BLOB"; //NORES
        break;
      default:
        String productName = metaData.getDatabaseProductName();
        MetaDataException.throwUnsupportedColumnType(column.getDataType(),productName);
        break;
    }
    return type;
  }

  public String getProcedureQueryString(String procedureName, boolean forResultSet, boolean[] warnings) {
    String  inputParams  = "";
    String  outputParams = "";
    boolean errors       = false;
    boolean hasResultSet = false;
    boolean areTypesOK   = true;
    int     params       = 0;

    try {
      DatabaseMetaData meta = metaData.getJdbcMetaData();
      ResultSet columns = meta.getProcedureColumns(null,null,procedureName,"%");

      while (columns.next()) {
        String param = columns.getString(4);   // "COLUMN_NAME"  //NORES
        short  ptype = columns.getShort(5);    // "COLUMN_TYPE"  //NORES
        short  type  = columns.getShort(6);    // "DATA_TYPE"    //NORES

        // Calculation of hasResultSet:
        if (params == 0)
          hasResultSet = true;
        params++;
        if (ptype != ParameterType.OUT)
          hasResultSet = false;

        // Parameters for normal procedure calls:
        if (!forResultSet) {
          if (!metaData.isSqlTypeSupported(type))
            areTypesOK = false;

          if (ptype == ParameterType.IN) {
            if (inputParams.length() > 0)
              inputParams += ", ";
            inputParams += ":" + param;
          }
          else {
            if (outputParams.length() > 0)
              outputParams += ", ";
            outputParams += ":" + param;
          }
        }
      }
      columns.close();
    }
    catch (Exception ex) {
      DiagnosticJLimo.printStackTrace(ex);
      errors = true;
    }
    if (warnings != null && warnings.length >= 3) {
      warnings[0] = errors;
      warnings[1] = forResultSet ? !hasResultSet : hasResultSet;
      warnings[2] = !areTypesOK;
    }
    if (outputParams.length() > 0)
      outputParams = "\r\nreturning_values " + outputParams;            //NORES
    if (inputParams.length() > 0)
      inputParams = " " + inputParams;

    if (forResultSet)
      return "select * from " + procedureName;          //NORES
    else
      return "execute procedure " + procedureName + inputParams + outputParams;         //NORES
  }
}

