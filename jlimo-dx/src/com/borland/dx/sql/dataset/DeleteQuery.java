//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/DeleteQuery.java,v 7.0 2002/08/08 18:39:51 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! DeleteQuery
//! This class is designed to generate DELETE queries for the QueryResolver.
//! It is called from QueryResolverStateHolder.java.
//! Checkout ResolverQuery.java for more details.
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.sql.SQLException;
import com.borland.dx.dataset.Variant;
import com.borland.dx.dataset.*;
import com.borland.jb.util.FastStringBuffer;

class DeleteQuery extends ResolverQuery
{
  public DeleteQuery(Database db, int queryTimeout, Coercer coercer) {
    super(db, queryTimeout, coercer);
  }

  //! This method controls the query string generated.
  //! It's simply a searched DELETE statement.
  final void setParameters(String tableName, Variant values[])
  throws SQLException, DataSetException {
    Column   column;
    if (rebuildNeeded()) {
      if (deleteBuf == null) {
        deleteBuf = new FastStringBuffer(128);
        deleteBuf.append("DELETE FROM ");
        deleteLength  = deleteBuf.getLength();
      }
      else
        deleteBuf.setLength(deleteLength);
      deleteBuf.append(tableName);
      deleteBuf.append(' ');
      whereClause(deleteBuf);  //NORES
      prepare(deleteBuf.toString());
    }

    setWhereParameters(0, values);
  }
  private FastStringBuffer deleteBuf;
  private int deleteLength;
}

