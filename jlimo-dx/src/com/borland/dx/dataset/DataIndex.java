//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataIndex.java,v 7.1 2003/06/13 00:37:26 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

// DO NOT DOCUMENT.
//

public abstract class DataIndex extends DirectIndex
{
  public DataIndex(MatrixData data, int visibleMask, int invisibleMask) {
    this.data = data;
    this.invisibleMask  = invisibleMask;
    this.visibleMask    = visibleMask;
  }

  public long locate(long startRow, Column[] scopedColumns, RowVariant[] values, int locateOptions)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.check(startRow == 0 || (startRow > 0 && startRow <= lastRow()));

    if (lastRow() < 0) {
//!     Diagnostic.println("Locate on empty index.");
      return -1;
    }

    if ((locateOptions&Locate.DETAIL) == 0) {
      // startRow basically ignored if going to first or last.
      //
      if ((locateOptions&Locate.FIRST) != 0)
        startRow  = 0;
      else if ((locateOptions&Locate.LAST) != 0)
        startRow  = lastRow();
    }

    Column[]  newLocateColumns  = scopedColumns;
    boolean   fastLoad          = ((locateOptions & Locate.FAST)) != 0
                                  && locateColumns == newLocateColumns;
    locateColumns     = newLocateColumns;
    locateColumnCount = locateColumns.length;

    if (!fastLoad)
      loadSearchValues(locateColumns, values);

    int lastColumn    = locateColumnCount - 1;

    if ( (Locate.PARTIAL & locateOptions) != 0 ) {
      if (locateColumns[lastColumn].getDataType() != Variant.STRING)
        DataSetException.partialSearchForString();
      locatePartialIndex = lastColumn;
    }
    else
      locatePartialIndex = -1;

    locateCaseInsensitive = (Locate.CASE_INSENSITIVE & locateOptions) != 0;

    return locate(startRow, locateOptions);
  }

  // Meaningless without keys.
  //
  public void sort() /*-throws DataSetException-*/ {}

  public void markStatus(long row, int status, boolean on)
    /*-throws DataSetException-*/
  {
    long internalRow = internalRow(row);
//!   Diagnostic.println(row+" "+internalRow+" markStatus:  "+Integer.toHexString(status));
    if (on)
      data.setStatus(internalRow, data.getStatus(internalRow) | status);
    else
      data.setStatus(internalRow, data.getStatus(internalRow) & ~status);
//!   Diagnostic.println(row+" "+internalRow+" after markStatus:  "+Integer.toHexString(data.getStatus(internalRow)));
  }


  public boolean resetPending(long internalRow, boolean resolved)
    /*-throws DataSetException-*/
  {
    int status  = data.getStatus(internalRow);
//!     Diagnostic.println(row+" "+internalRow+" resolvePending:  "+Integer.toHexString(status)+" "+Integer.toHexString(newStatus));
    if ((status&RowStatus.PENDING_RESOLVED) != 0) {
      if (!resolved)
        data.setStatus(internalRow, (status & ~RowStatus.PENDING_RESOLVED));
      else {
        status  &= ~(RowStatus.PENDING_RESOLVED|RowStatus.UPDATED|RowStatus.INSERTED);
        status  |= RowStatus.LOADED;
        data.setStatus(internalRow, status);
        delete(internalRow);
        return true;
      }
    }
    return false;
  }

  // Use for insertes and updates.
  //
  public  void resetPending(boolean resolved)
    /*-throws DataSetException-*/
  {
    for (int row = 0; row <= lastRow(); ) {
      if (!resetPending(internalRow(row), resolved))
        ++row;
    }
  }

  public  boolean resetPendingDelete(long internalRow, boolean resolved)
    /*-throws DataSetException-*/
  {
    int   status = data.getStatus(internalRow);

    if ((status&RowStatus.PENDING_RESOLVED) != 0) {
      if (!resolved)
        data.setStatus(internalRow, (status & ~RowStatus.PENDING_RESOLVED));
      else {
        data.setStatus(internalRow, 0);
        delete(internalRow);
        return true;
      }
    }
    return false;
  }

  public  void resetPendingDeletes(boolean resolved)
    /*-throws DataSetException-*/
  {
    for (int row = 0; row <= lastRow(); ) {
      if (!resetPendingDelete(internalRow(row), resolved))
        ++row;
    }
  }


  public void prepareInsert()
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  public void prepareUpdate(long internalRow)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  public void prepareUpdate()
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  public void prepareDelete()
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  public void uniqueCheck(long internalRow, RowVariant[] values, boolean updating)
    /*-throws DataSetException-*/
  {
  }

  public boolean markForUpdate(RowVariant[] values) /*-throws DataSetException-*/ { return true;}

  public void close()
    /*-throws DataSetException-*/
  {
  }

  public boolean isMaintained() {return true;}

  // Used to avoid descending indexes.
  //
  public  boolean isIndexMaintained() { return true; }

  public void dropIndex()
    /*-throws DataSetException-*/
  {
  }


  public boolean hasRowFilterListener(RowFilterListener rowFilterListener) {
    return this.rowFilterListener == rowFilterListener;
  }

  public long getInternalRow() {
    return internalRow;
  }

  public final SortDescriptor getSort() { return descriptor;}
  public final int getVisibleMask() { return visibleMask; }
  public final int getInvisibleMask() { return invisibleMask; }
  public final RowFilterListener getRowFilterListener() { return rowFilterListener; }
  public final DirectIndex getIndex() { return this; }
  public final MatrixData getData() {
    return data;
  }

  public boolean isInverted() { return false; }

  // Set by findClosest() methods.  Only valid for duration of a synchronized block.
  //
  public long internalRow;

  protected   Column[]            locateColumns;
  protected   int                 locateColumnCount;
  protected   int                 locatePartialIndex;
  protected   boolean             locateCaseInsensitive;

  protected    int                   visibleMask;
  protected    int                   invisibleMask;
  protected    SortDescriptor        descriptor;
  protected    RowFilterListener     rowFilterListener;

  private    MatrixData            data;
}

