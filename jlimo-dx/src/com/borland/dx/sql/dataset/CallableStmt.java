//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/CallableStmt.java,v 7.1 2003/10/15 18:41:36 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! CallableStmt
//! This is a wrapper around JDBC CallableStatement, that given a query string will bind parameters,
//! execute the query, and retrieve the output values. Part of the functionality is implemented in
//! PreparedStatement.
//!
//! **** WARNING !!! ****
//!
//! This file is related to: PreparedStmt.java, InterbaseCallableStmt.java, and OracleCallableStmt.java.
//! Be careful when changing the signature of methods in this file and the related files. Many of
//! the methods are overridden, and the functionality can easily be broken if the derived methods
//! are not changed accordingly [it may compile, but...]
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;

import java.sql.*;
import java.math.BigDecimal;
import java.io.*;

class CallableStmt extends PreparedStmt {

  void resetState(Database database, String query, MasterLinkDescriptor masterDetail, ReadWriteRow parameterRow, boolean[] inputOnly) /*-throws DataSetException-*/ throws SQLException {
    super.resetState(database,query,masterDetail,parameterRow);
    this.readOnly = readOnly;
  }

  void resetState(Database database, String query, MasterLinkDescriptor masterDetail, ReadWriteRow[] parameterRows, boolean[] inputOnly) /*-throws DataSetException-*/ throws SQLException {
    super.resetState(database,query,masterDetail,parameterRows);
    this.readOnly = inputOnly;
  }

  //! Warning: Overloaded method from PreparedStmt
  //!
  //! Create a CallableStatement instead of a PreparedStatement.
  PreparedStatement createStatement(String queryString) /*-throws DataSetException-*/ {
    return statement = database.createCallableStatement(queryString);
  }

  //! Warning: Overloaded method from PreparedStmt
  //! Warning: This method is overloaded in InterbaseStmt and OracleCallableStmt
  //!
  //! Clear state and delegate to super.
  void closeStatement() throws SQLException, DataSetException {
    super.closeStatement();
    statement = null;
  }

  //! Called from the outside to get the output values.
  void setOutputValues() /*-throws DataSetException-*/ throws  SQLException {
    if (parameterRows == null)
      return;

    DiagnosticJLimo.trace(Trace.QueryProgress, "Procedure: getting OUT parameter values");

    for (int index=0; index < columnCount; index++) {
      int   paramNo = paramIndices[index];
      String  name  = paramColumns[index];
      if (paramNo >= 0) {
        ReadWriteRow parameterRow = parameterRows[paramNo];
        Column column = parameterRow.getColumn(name);

        if (bindOutParameter(paramValues[paramNo], column, index+1))
          parameterRow.setVariant(name, paramValues[paramNo]);
      }
    }
  }

  //! Warning: Overloaded method from PreparedStmt
  //! Warning: This method is overloaded in InterbaseStmt and OracleCallableStmt
  //!
  //! Force certain parameters to be input parameters only, overrider the
  //! parameterType from the parameter ReadWriteRow.
  boolean analyzeParameters(QueryParseToken tokens, int actualParamCount, char quoteChar) /*-throws DataSetException-*/ {
    boolean namedParameter = super.analyzeParameters(tokens,actualParamCount, quoteChar);
    inputOverride = new boolean[columnCount];
    for (int i=0; i<columnCount; i++) {
      int paramNo = paramIndices[i];
      if (paramNo < 0)
        inputOverride[i] = true;
      else if (readOnly != null && paramNo < readOnly.length && readOnly[paramNo])
        inputOverride[i] = true;
    }
    return namedParameter;
  }

  //! Warning: This method is overloaded in InterbaseStmt and OracleCallableStmt
  //!
  //! Need to be different for derived classes.
  boolean isInputOverride(int param) {
    return inputOverride[param-1];
  }

  //! Warning: Overloaded method from PreparedStmt
  //! Warning: This method is overloaded in InterbaseStmt and OracleCallableStmt
  //!
  //! The super class will set the input parameter value, here we add
  //! registration of output parameters as well.
  void bindParameter(Variant data, Column column, int param)
    /*-throws DataSetException-*/ throws SQLException
  {
    int type = isInputOverride(param) ? ParameterType.IN : column.getParameterType();

    switch(type) {
      case ParameterType.IN:
      case ParameterType.NONE:
        super.bindParameter(data, column, param);
        break;

      case ParameterType.IN_OUT:
        registerOutParameter(data, column, param);  // Must be first because of BDE bug: 8860
        super.bindParameter(data, column, param);
        break;

      case ParameterType.OUT:
      case ParameterType.RETURN:
        registerOutParameter(data, column, param);
        break;

      case ParameterType.RESULT:
        break;  // Nothing to do, until ResetSet type in introduced.

      default:
        DataSetException.unrecognizedDataType();
        break;
    }
  }

  private void registerOutParameter(Variant data, Column column, int param)
    /*-throws DataSetException-*/ throws SQLException
  {
    int sqlType = column.getSqlType();

    // If the sqlType isn't set, do a default mapping of the column's
    // variant type to a SQL type.
    if (sqlType == 0)
      sqlType = RuntimeMetaData.variantTypeToSqlType(column.getDataType());

    switch(data.getType()) {
      case Variant.BIGDECIMAL:
        statement.registerOutParameter(param, sqlType, column.getScale());
        break;

      default:
        statement.registerOutParameter(param, sqlType);
        break;
    }
  }

  //! Warning: This method is overloaded in InterbaseStmt and OracleCallableStmt
  //!
  //! Retrieve the output value of the out parameters.
  boolean bindOutParameter(Variant data, Column column, int param)
    /*-throws DataSetException-*/ throws SQLException
  {
    int type = isInputOverride(param) ? ParameterType.IN : column.getParameterType();
    boolean didSetDataValue = true;

    switch (type) {
      case ParameterType.RETURN:
      case ParameterType.OUT:
      case ParameterType.IN_OUT:

        switch(column.getDataType()) {
          case Variant.INPUTSTREAM:
            byte[] bytes = statement.getBytes(param);
            if (statement.wasNull() || bytes == null)
              data.setAssignedNull();
            else {
              InputStream tempStream = new InputStreamToByteArray(bytes);
              data.setInputStream(tempStream);
            }
            break;

          case Variant.STRING:
            String tempString = statement.getString(param);
            if (statement.wasNull() || tempString == null)
              data.setAssignedNull();
            else {
              if (column.getSqlType() == java.sql.Types.CHAR)
                tempString = JdbcProvider.trimRight(tempString);
              data.setString(tempString);
            }
            break;

          case Variant.BIGDECIMAL:
            BigDecimal tempBigDecimal = statement.getBigDecimal(param, column.getScale());
            if (statement.wasNull() || tempBigDecimal == null)
              data.setAssignedNull();
            else
              data.setBigDecimal(tempBigDecimal);
            break;

          case Variant.INT:
            int tempInt = statement.getInt(param);
            if (!statement.wasNull())
              data.setInt(tempInt);
            else
              data.setAssignedNull();
            break;

          case Variant.BOOLEAN:
            boolean tempBoolean = statement.getBoolean(param);
            if (!statement.wasNull())
              data.setBoolean(tempBoolean);
            else
              data.setAssignedNull();
            break;

          case Variant.BYTE:
            byte tempByte = statement.getByte(param);
            if (!statement.wasNull())
              data.setByte(tempByte);
            else
              data.setAssignedNull();
            break;

          case Variant.SHORT:
            short tempShort = statement.getShort(param);
            if (!statement.wasNull())
              data.setShort(tempShort);
            else
              data.setAssignedNull();
            break;

          case Variant.LONG:
            long tempLong = statement.getLong(param);
            if (!statement.wasNull())
              data.setLong(tempLong);
            else
              data.setAssignedNull();
            break;

          case Variant.FLOAT:
            float tempFloat = statement.getFloat(param);
            if (!statement.wasNull())
              data.setFloat(tempFloat);
            else
              data.setAssignedNull();
            break;

          case Variant.DOUBLE:
            double tempDouble = statement.getDouble(param);
            if (!statement.wasNull())
              data.setDouble(tempDouble);
            else
              data.setAssignedNull();
            break;

          case Variant.DATE:
            java.sql.Date tempDate = statement.getDate(param);
            if (!statement.wasNull())
              data.setDate(tempDate);
            else
              data.setAssignedNull();
            break;

          case Variant.TIME:
            java.sql.Time tempTime = statement.getTime(param);
            if (!statement.wasNull())
              data.setTime(tempTime);
            else
              data.setAssignedNull();
            break;

          case Variant.TIMESTAMP:
            java.sql.Timestamp tempTimestamp= statement.getTimestamp(param);
            if (!statement.wasNull())
              data.setTimestamp(tempTimestamp);
            else
              data.setAssignedNull();
            break;

          case Variant.OBJECT:
            Object tempObject = statement.getObject(param);
            if (!statement.wasNull())
              data.setObject(tempObject);
            else
              data.setAssignedNull();
            break;

          default:
            didSetDataValue = false;
            DiagnosticJLimo.fail();
            break;
        }
        break;

      default:
        didSetDataValue = false;
        // do nothing
        break;
    }
    return didSetDataValue;
  }

  private boolean[] readOnly;        // These are readonly parameters
  private boolean[] inputOverride;   // Which parameters are input only, (given in resetState)
  CallableStatement statement;       // Instance of CallableStatement, (copy of PreparedStatement instance in PreparedStmt.java)
}
