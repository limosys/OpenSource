//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/FilterRow.java,v 7.0 2002/08/08 18:39:25 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.sql.*;
import java.io.InputStream;
import com.borland.jb.util.DiagnosticJLimo;


public abstract class FilterRow extends ReadRow
{
  protected FilterRow(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.check(columnList == null);
    if (columnList == null)
      columnList = new ColumnList(dataSet);
    // Use a different variant for each column.
    // Good for dirty reads - at least the type will never be dirty,
    // just the value.
    //
    Column[]   columns           = columnList.getColumnsArray();
    rowValues   = new RowVariant[columns.length];
    for (int ordinal = 0; ordinal < columns.length; ++ordinal)
      rowValues[ordinal] = new RowVariant(columns[ordinal].getDataType());
  }

  final RowVariant getVariantStorage(int ordinal)
    /*-throws DataSetException-*/
  {
    getFilterValue(ordinal, rowValues[ordinal]);
    return rowValues[ordinal];
  }

  final RowVariant getVariantStorage(String columnName)
    /*-throws DataSetException-*/
  {
    int ordinal = columnList.getOrdinal(columnName);
    getFilterValue(ordinal, rowValues[ordinal]);
    return rowValues[ordinal];
  }

  abstract protected void getFilterValue(int ordinal, Variant value) /*-throws DataSetException-*/;


  private RowVariant[]  rowValues;
}

