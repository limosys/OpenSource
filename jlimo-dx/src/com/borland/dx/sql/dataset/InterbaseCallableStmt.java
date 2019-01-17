//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/InterbaseCallableStmt.java,v 7.0 2002/08/08 18:39:51 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! InterbaseCallableStmt
//! This is a special version of CallableStmt.java, where some methods are overridden to retrieve
//! output values from an Interbase procedure call. Interbase doesn't support output values, instead
//! Interbase will give a ResultSet, where each column correspond to an output parameter.
//!   This class will capture this ResultSet in executeUpdate() and supply these values to the
//! output parameters when that call is made [CallableStmt.setOutputValues].
//!
//! **** WARNING !!! ****
//!
//! This file is related to: PreparedStmt.java, CallableStmt.java, and OracleCallableStmt.java.
//! Be careful when changing the signature of methods in this file and the related files. Many of
//! the methods are overridden, and the functionality can easily be broken if the derived methods
//! are not changed accordingly [it may compile, but...]
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;
import java.sql.*;
import java.math.BigDecimal;
import java.io.*;

class InterbaseCallableStmt extends CallableStmt {

  //! Warning: Overloaded method from PreparedStmt
  //!
  //! We extended the syntax for Interbase stored procedures specification.
  //! If the string ends with the word: "returning_values" and a list of output
  //! parameter markers, we will setup structures to capture the ResultSet for
  //! the output values. If the word is not present, we handle the procedure
  //! call as a non Interbase procedure.
  void prepareParameters() /*-throws DataSetException-*/ throws SQLException {
    String queryString = query.toLowerCase();
    int index = queryString.indexOf("returning_values");                //NORES
    hasReturningValues = (index >= 0);
    if (hasReturningValues)
      returning_values = query.substring(index,index+16);
    super.prepareParameters();
  }

  //! Warning: Overloaded method from CallableStmt
  //!
  //! Remove the "returning_values" from the procedure specification
  //! before running the procedure.
  boolean analyzeParameters(QueryParseToken tokens, int actualParamCount, char quoteChar) /*-throws DataSetException-*/ {
    boolean namedParameter = super.analyzeParameters(tokens,actualParamCount,quoteChar);
    if (hasReturningValues) {
      QueryParseToken prevTokens = null;

      inputParams = 0;
      outputParams = 0;
      while (tokens != null) {
        if (tokens.isParameter())
          inputParams++;
        else {
          String name = tokens.getName();
          if (name != null && name.indexOf(returning_values) >= 0) {
            if (name.trim().indexOf(returning_values) == 0) {
              if (prevTokens != null)
                prevTokens.setNextToken(null); // Remove sql after this token.
            } else {
              int index = name.indexOf(returning_values);
              name = JdbcProvider.trimRight(name.substring(0,index));
              tokens.setName(name);
              tokens.setNextToken(null);    // Remove sql after this token.
            }

            // Force regeneration of query string, to eliminate the returning_values token.
            namedParameter = true;
            break;
          }
        }
        prevTokens = tokens;
        tokens = tokens.getNextToken();
      }
      outputParams = columnCount - inputParams;
    }
    return namedParameter;
  }

  //! Warning: Overloaded method from CallableStmt
  //!
  //! If the "returning_values" was present in the procedure specification
  //! only bind the input parameters (which are always first in Interbase).
  void bindParameter(Variant data, Column column, int param)
    /*-throws DataSetException-*/ throws SQLException
  {
    if (!hasReturningValues || param <= inputParams)
      super.bindParameter(data,column,param);
  }

  //! Warning: Overloaded method from CallableStmt
  //!
  //! If the "returning_values" was present in the procedure specification
  //! only bind the input parameters (which are always first in Interbase).
  boolean isInputOverride(int param) {
    if (hasReturningValues)
      return (param <= inputParams);
    else
      return super.isInputOverride(param);
  }

  //! Warning: Overloaded method from PreparedStmt
  //!
  //! Override this method and execute this procedure as a query (returning a
  //! ResultSet) instead of an update.  Convert and store the ResultSet in a
  //! dataset.
  //! If the "returning_values" was present in the procedure specification call
  //! execute normally.
  int executeUpdate() /*-throws DataSetException-*/ throws SQLException {
    prepareParameters();
    if (!hasReturningValues) {
      statement.execute();
      return statement.getUpdateCount();
    }

    ResultSet resultSet = statement.executeQuery();
    dataSet = null;
    if (resultSet != null) {
      dataSet = database.resultSetToDataSet(resultSet);
      dataSet.open();
      resultSet.close();
    }
    int actualOutputParams = resultSet == null ? 0 : dataSet.getColumnCount();
    if (outputParams != actualOutputParams)
      DataSetException.mismatchParamResult();
    return 0;
  }

  //! Warning: Overloaded method from CallableStmt
  //!
  //! Get the output values from the dataSet from running the query.
  boolean bindOutParameter(Variant data, Column column, int param)
    /*-throws DataSetException-*/ throws SQLException
  {
    if (!hasReturningValues)
      return super.bindOutParameter(data,column,param);
    else {
      if (param > inputParams) {
        dataSet.getVariant(param-inputParams-1, value);
        data.setVariant(value);
        return true;
      }
    }
    return false;
  }

  //! Warning: Overloaded method from CallableStmt
  //!
  //! Close local resources as well.
  void closeStatement() throws SQLException, DataSetException {
    super.closeStatement();
    if (dataSet != null)
      dataSet.close();
    dataSet = null;
  }

  private Variant value = new Variant();
  private StorageDataSet dataSet;
  private int inputParams;
  private int outputParams;
  private boolean hasReturningValues;
  private String returning_values;
}

