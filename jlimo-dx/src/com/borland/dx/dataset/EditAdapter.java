//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/EditAdapter.java,v 7.0 2002/08/08 18:39:24 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ErrorResponse;
import java.util.*;

/**
 * This is an adapter class for {@link com.borland.dx.dataset.EditListener},
 * which is used as a notification for row editing before and after edit-related
 * operations for DataSets are completed.
 */
public class EditAdapter
  implements EditListener
{
  public void canceling(DataSet dataSet) throws Exception {};

  public void updating(DataSet dataSet, ReadWriteRow newRow, ReadRow oldRow) throws Exception {}
  public void updated(DataSet dataSet) /*-throws DataSetException-*/ {}

  public void adding(DataSet dataSet, ReadWriteRow newRow) throws Exception {}
  public void added(DataSet dataSet) /*-throws DataSetException-*/{}

  public void deleting(DataSet dataSet) throws Exception {}
  public void deleted(DataSet dataSet) /*-throws DataSetException-*/{}

  public void modifying(DataSet dataSet) throws Exception {}

  public void inserting(DataSet dataSet) throws Exception {}
  public void inserted(DataSet dataSet) /*-throws DataSetException-*/{}

  public void editError(DataSet dataSet, Column column, Variant value, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/{}
  public void updateError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/{}
  public void addError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/{}
  public void deleteError(DataSet dataSet, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/{}

}
