//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/StoreInternals.java,v 7.2 2003/08/05 01:48:34 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.Locale;

public interface StoreInternals {
  public void open() /*-throws DataSetException-*/;
  public MatrixData   open(  StorageDataSet  dataSet,
                             MatrixData      data,
                             int             matrixDataType,
                             int             aggGroupColumnCount,
                             AggManager      aggManager,
                             boolean         replaceColumns
                          ) /*-throws DataSetException-*/;
  public void             updateProperties(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public void             rename(String storeName, String newStoreName) /*-throws DataSetException-*/;
  public StorageDataSet[] empty(StorageDataSet dataSet) /*-throws DataSetException-*/;
//!  public Store            opening(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public void             attach(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public boolean          exists(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public boolean          isReadOnly(String storeName);
  public boolean          isDataStore();
  public Locale           getLocale();
  public StorageDataSet   getDuplicates(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public void             deleteDuplicates(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public Object           getOpenMonitor(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public boolean          isSortable(Column column);
  public Object           setSavepoint(String name);
  public boolean          rollback(Object savepoint);
  public String           getSchemaStoreName(String name);
  public String           getReadableTableName(String storeName);
}

