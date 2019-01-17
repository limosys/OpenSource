//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/InternalRow.java,v 7.0 2002/08/08 18:39:26 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.sql.*;
import java.io.InputStream;




public class InternalRow extends ReadRow
{
  public InternalRow(StorageDataSet dataSet) {
    this.data         = dataSet.getMatrixData();
    this.columnList   = dataSet.getColumnList();
    // Use a different variant for each column.
    // Good for dirty reads - at least the type will never be dirty,
    // just the value.
    //
    int   count           = columnList.count;
    internalRowValues   = new RowVariant[count];
    for (int ordinal = 0; ordinal < count; ++ordinal)
      internalRowValues[ordinal] = new RowVariant(columnList.cols[ordinal].getDataType());
  }

  public void setInternalRow(long internalRow) {
    this.internalRow = internalRow;
  }

  public final RowVariant getVariantStorage(int ordinal)
    /*-throws DataSetException-*/
  {
    data.getVariant(internalRow, ordinal, internalRowValues[ordinal]);
    return internalRowValues[ordinal];
  }

  public final RowVariant getVariantStorage(String columnName)
  /*-throws DataSetException-*/
  {
    int ordinal = columnList.getOrdinal(columnName);
    data.getVariant(internalRow, ordinal, internalRowValues[ordinal]);
    return internalRowValues[ordinal];
  }

  private MatrixData    data;
  private long          internalRow;
  private RowVariant[]  internalRowValues;
}
