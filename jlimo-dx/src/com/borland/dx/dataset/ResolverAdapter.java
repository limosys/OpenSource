//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ResolverAdapter.java,v 7.0 2002/08/08 18:39:33 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;
import com.borland.jb.util.ErrorResponse;

/**
 * This is an adapter class for {@link ResolverListener}, which is used as notification
 * before and after a StorageDataSet is resolved.
 */
public class ResolverAdapter implements
   ResolverListener
{
  public void insertingRow(ReadWriteRow row, ResolverResponse response) /*-throws DataSetException-*/ {}
  public void deletingRow(ReadWriteRow row, ResolverResponse response) throws  DataSetException {}
  public void updatingRow(ReadWriteRow row, ReadRow oldRow, ResolverResponse response)  /*-throws DataSetException-*/ {}
  public void insertedRow(ReadWriteRow row) /*-throws DataSetException-*/ {}
  public void deletedRow(ReadWriteRow row) /*-throws DataSetException-*/ {}
  public void updatedRow(ReadWriteRow row, ReadRow oldRow) /*-throws DataSetException-*/ {}
  public void insertError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/ {}
  public void deleteError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) throws  DataSetException {}
  public void updateError(DataSet dataSet, ReadWriteRow row, ReadRow oldRow, ReadWriteRow updRow, DataSetException ex, ErrorResponse response)  /*-throws DataSetException-*/ {}
}

