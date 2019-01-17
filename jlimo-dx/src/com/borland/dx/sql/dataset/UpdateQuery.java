//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/UpdateQuery.java,v 7.0 2002/08/08 18:40:00 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! UpdateQuery
//! This class is designed to generate UPDATE queries for the QueryResolver.
//! It is called from QueryResolverStateHolder.java.
//! Checkout ResolverQuery.java for more details.
//!-------------------------------------------------------------------------------------------------
package com.borland.dx.sql.dataset;

import java.sql.SQLException;
import com.borland.dx.dataset.Variant;
import com.borland.dx.dataset.*;
import com.borland.jb.util.FastStringBuffer;

class UpdateQuery extends ResolverQuery
{
  public UpdateQuery(Database db, int queryTimeout, Coercer coercer) {
    super(db, queryTimeout, coercer);
  }

  //! This method controls the query string generated.
  //! An UPDATE query with a comma separated list of column = value elements
  //! is generated.
  final void setParameters(String tableName, Variant values[], Variant oldValues[])
  throws SQLException, DataSetException {
    Column   column;
    Variant  value;
    if (rebuildNeeded()) {
      if (updateBuf == null) {
        updateBuf = new FastStringBuffer(128);
        updateBuf.append("UPDATE ");
        updateLength = updateBuf.getLength();
      }
      else
        updateBuf.setLength(updateLength);

      updateBuf.append(tableName);
      updateBuf.append(" SET ");

      boolean firstTime = true;

      for (int index = 0; index < currentColumn; index++) {
        if ((columnStatus[index] & ResolverQuery.changedValue) != 0) {
          column           = columns[index];

          if (!firstTime)
            updateBuf.append(',');

          firstTime = false;

          columnString(column, updateBuf);

          if (values[column.getOrdinal()].isNull())
            updateBuf.append(" = NULL");    //NORES
          else
            updateBuf.append(' ', '=', ' ', '?');

        }
      }

      whereClause(updateBuf);
      prepare(updateBuf.toString());
    }

    int paramIndex  = 0;

    for (int index = 0; index < currentColumn; index++) {
      if ((columnStatus[index] & ResolverQuery.changedValue) != 0) {
        column = columns[index];
        value  = values[column.getOrdinal()];
        if (value.isNull())
          continue;
        setParameter(paramIndex++, column, value);
      }
    }

    setWhereParameters(paramIndex, oldValues);
  }
  private FastStringBuffer updateBuf;
  private int updateLength;
}


