//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Index.java,v 7.1 2003/06/13 00:37:27 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

// Not public.  Not used outside package.
//
public abstract class Index
{

  public abstract long  lastRow() /*-throws DataSetException-*/;
  public abstract long internalRow(long row) /*-throws DataSetException-*/;
  public abstract long  findClosest(long internalRow) /*-throws DataSetException-*/;
  public abstract long  findClosest(long internalRow, long row) /*-throws DataSetException-*/;
  public abstract long  locate(  long           startRow,
                                Column[]      scopedColumns,
                                RowVariant[]  values,
                                int           locateOptions
                      ) /*-throws DataSetException-*/;



  public void markStatus(long row, int status, boolean on)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  // Overrriden by DetailIndex.
  //
  void emptyAllRows(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    StorageDataSet dataSetStore = dataSet.getStorageDataSet();
    if (dataSetStore != null)
      dataSetStore.empty();
  }


  public void setInsertPos(long pos)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  public abstract long getInternalRow();

  public long moveRow(long pos, long delta)
    /*-throws DataSetException-*/
  {
    return 0;
  }

}


