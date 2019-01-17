//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/InsertQuery.java,v 7.0 2002/08/08 18:39:51 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! InsertQuery
//! This class is designed to generate INSERT queries for the QueryResolver.
//! It is called from QueryResolverStateHolder.java.
//! Checkout ResolverQuery.java for more details.
//!-------------------------------------------------------------------------------------------------
package com.borland.dx.sql.dataset;

import java.sql.SQLException;
import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.FastStringBuffer;

class InsertQuery extends ResolverQuery
{
  public InsertQuery(Database db, int queryTimeout, Coercer coercer) {
    super(db, queryTimeout, coercer);
  }

  //! This method controls the query string generated.
  //! An INSERT with a column list and a value list is generated.
  final void setParameters(String tableName, Variant values[])
  throws SQLException, DataSetException {
    Column   column;
    if (rebuildNeeded()) {
      if (insertBuf == null) {
        insertBuf = new FastStringBuffer(128);
        paramBuf = new FastStringBuffer(128);
        insertBuf.append("INSERT INTO ");
        insertLength = insertBuf.getLength();
        paramBuf.append(") VALUES (");
        paramLength = paramBuf.getLength();
      }
      else
      {
        paramBuf.setLength(paramLength);
        insertBuf.setLength(insertLength);
      }

     insertBuf.append(tableName);
     insertBuf.append(' ');
     insertBuf.append('(');

     boolean first = true;

      for (int index = 0; index < currentColumn; index++) {
        column           = columns[index];
        if (!first) {
          insertBuf.append(',');
          paramBuf.append(',');
        }
        first = false;
        columnString(column, insertBuf);

        // Some JDBC drivers do not like the PreparedStatement.setNull(int) call,
        // so we explicitly generate the NULL inline.
        //
        if (values[column.getOrdinal()].isNull())
          paramBuf.append("NULL");
        else
          paramBuf.append('?');
      }

      insertBuf.append(paramBuf);
      insertBuf.append(')');

      prepare(insertBuf.toString());  //NORES

    }
    DiagnosticJLimo.check(preparedStatement != null);
    for (int index = 0; index < currentColumn; index++) {
      column = columns[index];
      setParameter(index, column, values[column.getOrdinal()]);
    }
  }

  private FastStringBuffer insertBuf;
  private int insertLength;
  private FastStringBuffer paramBuf;
  private int paramLength;
}

