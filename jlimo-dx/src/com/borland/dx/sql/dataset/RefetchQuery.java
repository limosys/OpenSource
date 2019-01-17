//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/RefetchQuery.java,v 7.1 2003/06/13 16:21:28 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! RefetchQuery
//! This class is designed to regenerate a SELECT for a specific row in a dataset.
//! It is called from QueryDataSet.java.
//! Checkout ResolverQuery.java for more details.
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.sql.*;
import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.FastStringBuffer;
import com.borland.dx.dataset.*;

class RefetchQuery extends ResolverQuery
{

  public RefetchQuery(Database db, String query, int queryTimeout, Coercer coercer) {
    super(db, queryTimeout, coercer);
    selectAndFromBuf = new FastStringBuffer(128);
    selectAndFromBuf.append(extractSelectAndFrom(query));
    selectAndFromLength = selectAndFromBuf.getLength();
  }

  //! Extract the select list from the original query, by removing the
  //! WHERE clause from the query.
  private String extractSelectAndFrom(String query) {
    SimpleParser parser = new SimpleParser(query, quoteCharacter);
    QueryParseToken tokens = parser.getParsedTokens();
    QueryParseToken prev   = null;
    while (tokens != null) {
      if (tokens.getType() == SQLToken.WHERE) {
        prev.setNextToken(null);
        break;
      }
      prev   = tokens;
      tokens = tokens.getNextToken();
    }
    return parser.format(parser.getParsedTokens(),false);
  }

  //! Will generate a query string for the refetch (not used internally)
  //! it only returns the string for the QueryDataSet method.
  final String makeQueryString(ReadRow row)
    /*-throws DataSetException-*/
  {
    int columnCount = row.getColumnCount();
    boolean firstTime = true;
    Variant value = new Variant();

    if (queryBuf == null) {
      queryBuf = new FastStringBuffer(128);
      selectAndFromBuf.setLength(selectAndFromLength);
      queryBuf.append(selectAndFromBuf);
      queryBuf.append(" WHERE ");
      queryLength = queryBuf.getLength();
    }
    else
      queryBuf.setLength(queryLength);

    for (int index = 0; index < columnCount; ++index) {
      Column column = row.getColumn(index);
      if (column.isRowId()) {
        row.getVariant(index, value);
        int dataType = value.getType();
        if (!firstTime)
          queryBuf.append(" AND ");
        firstTime = false;
        columnString(column, queryBuf);
        if (value.isNull()) {
          queryBuf.append(" IS NULL"); //NORES
        }
        else {
          queryBuf.append(" = ");
          if (dataType >= Variant.DATE && dataType <= Variant.STRING)
            queryBuf.append('\'');
          queryBuf.append(value.toString());
          if (dataType >= Variant.DATE && dataType <= Variant.STRING)
            queryBuf.append('\'');
        }
      }
    }
    return queryBuf.toString();
  }

  //! Generates the query by adding a WHERE clause to the SELECT part
  //! we got from the original query.
  final void setParameters(DataSet dataSet, ReadRow row)
    throws SQLException, DataSetException
  {
    clearRebuildStatus();
    setDataSet(dataSet,1,null,0);
    int       columnCount = row.getColumnCount();
    Variant   values[]    = dataSet.allocateValues();//new Variant[columnCount];

    for (int index = 0; index < columnCount; ++index) {
      int status = 0;

      Column column = row.getColumn(index);
      if (column.isRowId()) {
        status |= ResolverQuery.rowId;

        row.getVariant(index, values[index]);
        if (values[index].isNull())
          status |= ResolverQuery.nullRowId;

        setColumnStatus(column, status);
        DiagnosticJLimo.trace(Trace.DataSetSave, "setColumnStatus " + index);
      }
    }

    rebuildNeeded();

    selectAndFromBuf.setLength(selectAndFromLength);
    whereClause(selectAndFromBuf);
    prepare(selectAndFromBuf.toString());
    setWhereParameters(0, values);
  }

  //! Executes the query, and returns the values of the row in the
  //! row passed into the call.
  final void executeQuery(ReadWriteRow row)
    throws SQLException, DataSetException
  {
    DiagnosticJLimo.trace(Trace.DataSetSave, "Executing query");
    StorageDataSet resultDataSet = null;
    ResultSet result = preparedStatement.executeQuery();
    resultDataSet = database.resultSetToDataSet(result);
    result.close();
    close();

    resultDataSet.open();
    if (resultDataSet.getLongRowCount() != 1) {
      if (resultDataSet.getLongRowCount() > 0)
        DataSetException.insufficientRowId();
      else
        DataSetException.nonExistentRowId();
    }
    String[] columns = resultDataSet.getColumnNames(resultDataSet.getColumnCount());
    ReadRow.copyTo(columns, resultDataSet, columns, row);
    resultDataSet.close();
  }

  private FastStringBuffer queryBuf;
  private FastStringBuffer selectAndFromBuf;
  private int selectAndFromLength;
  private int queryLength;
}

