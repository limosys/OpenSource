//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/MemoryStore.java,v 7.2 2003/08/05 01:47:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import java.util.Locale;

public class MemoryStore
  implements  Store, StoreInternals
{
  public void open() /*-throws DataSetException-*/ {}

  public StoreInternals getStoreInternals() { return this; }

  public MatrixData  open( StorageDataSet   dataSet,
                           MatrixData       data,
                           int              matrixDataType,
                           int              aggGroupColumnCount,
                           AggManager       aggManager,
                           boolean          replaceColumns
                         )
    /*-throws DataSetException-*/
  {
    if (data == null) {
      MemoryData memoryData = new MemoryData(dataSet);
      data = memoryData;
      Column[] columns  = dataSet.getColumns();//dataSet.columnList.columns;
      if (columns != null) {
        Column column;
        for (int columnIndex = 0; columnIndex < columns.length; ++columnIndex) {
          column  = columns[columnIndex];
          if (!data.validColumnType(column))
            DataSetException.invalidColumnType(column);
          data.addColumn(column);
        }
      }
    }
    data.openData(dataSet, replaceColumns);
    ((MemoryData)data).initRequiredOrdinals(dataSet);
    return data;
  }

  public void updateProperties(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
  }
  public void rename(String storeName, String newStoreName)
    /*-throws DataSetException-*/
  {
  }

  public StorageDataSet[] empty(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    return null;
  }


  public void attach(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
  }

//!  public Store opening(StorageDataSet dataSet) { return this; }

  public boolean      isReadOnly(String storeName) { return false; }

  public boolean      exists(StorageDataSet dataSet) /*-throws DataSetException-*/  { return false; }

  public Locale       getLocale() { return null; }

  public boolean      isDataStore() { return false; }

  public StorageDataSet getDuplicates(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    MemoryData  data = (MemoryData)MatrixData.getData(dataSet);
    if (data != null)
      return data.duplicates;
    return null;
  }

  public void deleteDuplicates(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    MemoryData  data = (MemoryData)MatrixData.getData(dataSet);
    if (data != null)
      data.deleteDuplicates();
  }

  public Object getOpenMonitor(StorageDataSet dataSet)
  {
    return dataSet;
  }

  public final boolean isSortable(Column column) {
    int dataType = column.getDataType();
    return      dataType != Variant.INPUTSTREAM
            &&  dataType != Variant.OBJECT;
  }
  // Not supported.
  //
  public Object           setSavepoint(String name) {
    return null;
  }
  // Not supported.
  //
  public boolean          rollback(Object savepoint) {
    return false;
  }

  public String           getSchemaStoreName(String storeName) {
    return storeName;
  }

  public String           getReadableTableName(String storeName) {
    return storeName;
  }
}
