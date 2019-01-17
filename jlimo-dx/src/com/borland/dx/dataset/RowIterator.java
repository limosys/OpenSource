//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/RowIterator.java,v 7.2 2003/06/13 16:21:13 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

/**
    RowIterator provides lightweight (low memory usage, fast binding) iteration
    capabilities for any class that extends ReadRow.  RowIterators can also be
    used to ensure static type safe access to columns.
    <p>
    RowIterator capabilities are dependent on what class it is bound to:
    <ul>
      <li>Column level read operations for any class that extends ReadRow.            </li>
      <li>Column level read/write operations for and class that extends ReadWriteRow. </li>
      <li>Navigation for any class that extends DataSet.                              </li>
    </ul>
    <p>
    A Row iterator can be bound (using the bind method) to any class that extends
    ReadRow.
    <p>
    See the com.borland.samples.twotier sample project for an example of how
    RowIterator can be applied to provide type safe access.
    <p>
    <b>Using RowIterator bound to a DataSet.</b>
    <p>
    If RowIterator is bound to a DataSet, the post() method must be used to cause the changes to appear in the bound
    DataSet.  This allows multiple RowIterators to be simultaneously editing differnt
    rows in the same DataSet.  When bound to a ReadWriteRow, setting columns takes
    immediate affect.  You can force a DataSet to be bound as a ReadWriteRow by calling
    RowIterator.bind((ReadWriteRow)DataSet);
    <p>
    If you have bound an RowIterator to a DataSet, you must call RowIterator.unbind()
    to free up allocated memory resources used by the RowIterator.  Note that this
    does not have to be done if the DataSet was bound as a ReadWriteRow
    (ie RowIterator.bind((ReadWriteRow)DataSet);).
    <p>
    You can extend RowIterator or embed it in your own row object.  With your own
    row object, you can add type safe accessor methods with descriptive names.
    Once established, tools like JBuilder can provide nice accessor choices inside
    the code editor via code insights.  Take a customer table for example.  In
    this case an application may set up an entity Object that extends StorageDataSet.
    The entity Object contains the business logic for customers expressed as
    property and event settings with associated business logic.  The entity object
    has a private jbInit() method so it is designable using tools like JBuilder.
    The jbInit() method contains the persistent columns, property and event settings
    for the entity object.
*/

public class RowIterator extends ReadWriteRow {

  /**
      This must be called to free up allocated resources when bound to a DataSet.
      Note that if an iterator is bound to different Object that extends from
      ReadRow, an implicit unbind() call will be made for the previously bound
      object.
  */
  public final void unbind()
    /*-throws DataSetException-*/
  {
    unBind(true);
  }

  final void unBind(boolean permanently)
    /*-throws DataSetException-*/
  {
    if (dataSetListener != null) {
      if (permanently) {
        dataSetListener.removeIterator(this);
        dataSetListener = null;
      }
      closeEditView();
      dataSet = null;
    }
    columnList      = null;
    setCompatibleList((ColumnList)null);
  }

  private final void closeEditView()
    /*-throws DataSetException-*/
  {
    if (editView != null) {
      editView.close();
      editView = null;
    }
  }

  private final void init(ReadRow row)
    /*-throws DataSetException-*/
  {
    if (dataSet != null) {
      dataSet.removeIterator(this);
      // Try to keep the view around.
      //
      if (readRow != dataSet || !(editView != null && dataSet.dataSetStore == editView.dataSetStore))
        closeEditView();
    }
    this.readRow    = row;
    this.rowValues  = row.rowValues;
    this.editing    = false;
    setCompatibleList(row);
    columnList      = row.columnList;
  }

  /**
      Bind to another iterator.  If iterator is bound to a DataSet, then unbind()
      must be called when you are done with this iterator.
  */
  public void bind(RowIterator iterator)
    /*-throws DataSetException-*/
  {
    if (iterator.dataSet != null) {
      bind(iterator.dataSet);
      synchronized(dataSet.dataMonitor) {
        if (iterator.needsSynch)
          iterator._synchRow();
        internalRow = iterator.internalRow;
        currentRow  = iterator.currentRow;
      }
    }
    else if (iterator.writeRow != null)
      bind(iterator.writeRow);
    else
      bind(iterator.readRow);
  }

  /**
      Bind to a DataSet.  unbind() must be called when you are done with this iterator.
      set/get operations allowed on Columns.
      Row navigation allowed.
      Edited and inserted rows must call the post() method to make the changes
      visible to the bound dataSet.
  */
  public void bind(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    init(dataSet);
    this.dataSet          = dataSet;
    this.dataSetListener  = dataSetListener;
    this.index            = dataSet.index;
    this.writeRow         = dataSet;
    dataSet.addIterator(this);
  }

  /**
      Bind to a ReadWriteRow.  set/get operations allowed on Columns.
  */
  public void bind(ReadWriteRow writeRow)
    /*-throws DataSetException-*/
  {
    init(writeRow);
    this.dataSet  = null;
    this.writeRow = writeRow;
  }

  /*

      Bind to a ReadRow.  get operations allowed on Columns.
  */

  /**
   * Binds to a ReadRow.
   * Once the RowIterator is bound, get operations are allowed on Columns.
   *
   * @param readRow
   */
  public void bind(ReadRow readRow)
    /*-throws DataSetException-*/
  {
    init(readRow);
    this.dataSet  = null;
    this.writeRow = null;
  }

  private final void invalidUse()
    /*-throws DataSetException-*/
  {
    DataSetException.invalidIteratorUse();
  }

  void processColumnPost(RowVariant value) /*-throws DataSetException-*/ {
    if (writeRow != null)
      writeRow.processColumnPost(value);
    else
      invalidUse();
  }

  final void rowEdited()
    /*-throws DataSetException-*/
  {
    if (!editing) {
      if (writeRow == null)
        invalidUse();
      if (dataSet != null) {
        editView().goToInternalRow(internalRow);
        rowEdited(editView);
        editView.rowEdited();
      }
      else {
        writeRow.rowEdited();
        rowEdited(writeRow);
      }
      editing   = true;
    }
    if (!rowDirty) {
      if (dataSet != null)
        editView.rowEdited();
      rowDirty  = true;
    }
  }

  final void rowEdited(ReadWriteRow row)
    /*-throws DataSetException-*/
  {
      rowValues           = row.rowValues;
      row.copySetValuesTo(this);
      hasValidations      = row.hasValidations;
      notifyColumnPost    = row.notifyColumnPost;
  }

  /* Override of ReadWriteRow implementation.  Should not use externally
     because it returns internal Variant storage.
     Returns internal storage, do not make public.
  */
  final RowVariant getVariantStorage(String columnName)
    /*-throws DataSetException-*/
  {
    return getVariantStorage(columnList.getOrdinal(columnName));
  }

  // Returns internal variant storage - do not make public.
  //
  final RowVariant getVariantStorage(int ordinal)
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      return readRow.getVariantStorage(ordinal);
    else {

      if (editing && !columnList.cols[ordinal].isLookupOrAggregate()) {
        DiagnosticJLimo.check(rowValues == editView.rowValues);
        return rowValues[ordinal];
      }
      else {
        if (currentRow == 0 && dataSet.getLongRowCount() < 1)
          return RowVariant.nullVariant;

        if (needsSynch) {
          synchronized(dataSet.dataMonitor) {
            _synchRow();
          }
        }

        return dataSet.dataSetStore.getVariantStorage(this, internalRow, ordinal, value);
      }
    }
  }

  final long _synchRow()
    /*-throws DataSetException-*/
  {
    currentRow  = index.findClosest(internalRow, currentRow);
//!   Diagnostic.println("internalRow:  "+index.internalRow+" "+internalRow);
    if (index.getInternalRow() != internalRow) {
      // internal row is gone so position to closest logical row.
      //
      if (currentRow < 0) {
        currentRow  = 0;
        internalRow = 0;
        isInBounds = false;
      }
      else {
//!       Diagnostic.println("internalRow:  "+index.internalRow+" "+internalRow);
        internalRow = index.getInternalRow();
        isInBounds = true;
        if (internalRow < 0) {
          //! JOAL: BUG16898, BUG17157,  TODO: Steve please verify fix!
          //! No more rows exist, so reset the following fields:
          currentRow  = 0;
          internalRow = 0;
          isInBounds = false;

        }
      }
    }
    needsSynch = false;
    return internalRow;
  }

  /**
   * Is this iterator at the last row visible by this dataSet?
   */
  public final boolean  atLast()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      return true;
    return currentRow == (dataSet.getLongRowCount()-1) && !dataSet.dataSetStore.hasMoreData();
  }

  /**
   * Is this iterator at the first row visible by this dataSet?
   */
  public final boolean  atFirst()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      return true;
    return currentRow == 0;
  }

  /**
     Move to first row visible by this dataSet.  This may cause
     the inBounds() to return true if more than one row exists.
   */
  public final void first() /*-throws DataSetException-*/ {
    if (dataSet != null) {
      synchronized(dataSet.dataMonitor) {
        _goToRow(0);
      }
    }
  }

  /**
     Move to last row visible by this dataSet.  This may cause
     the inBounds() to return true if more than one row exists.
   */
  public final void last() /*-throws DataSetException-*/ {
    boolean moved = false;

    if (dataSet != null) {
      dataSet.dataSetStore.closeProvider(true);
      synchronized(dataSet.dataMonitor) {
        // Don't know what the last row really is if editing new row,
        // so stop editing before attempting to move
        //
        if (editing)
          _post();
        _goToRow(dataSet.getLongRowCount()-1);
      }
    }
  }

  // DO NOT MAKE PUBLIC!!!  Must go through synchronized StorageDataSet
  //
  final boolean _goToRow(long row)
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      invalidUse();
    if (editing)
      _post();

    // FETCH_AS_NEEDED and LOAD_ONE_ROW require us to ask for more data.
    // For LOAD_ONE_ROW we may need to adjust the row number (if row was replaced).
    if (row > index.lastRow() && dataSet.dataSetStore.provideMoreData())
      if (row > index.lastRow())
        row = index.lastRow();

    // Note that there is no pseudo record at this point.
    // index.lastRow() will be negative when no rows present.
    //
    if (row < 0 || (row > index.lastRow() && row != 0)) {
      isInBounds = false;
    }
    else {
      isInBounds      = true;
      this.currentRow = row;
      internalRow     = index.internalRow(row);
      return true;
    }
    return false;
  }

  /**
     Move to the next row visible by this iterator.  This will cause
     the inBounds() to return false if next is called when the iterator is positioned
     at the last visible row.
   */
  public final boolean next() /*-throws DataSetException-*/ {
    if (dataSet == null)
      return false;
    synchronized(dataSet.dataMonitor) {
      if (needsSynch)
        _synchRow();

      return _goToRow(currentRow+1);
    }
  }

  /**
     Move to the prior row visible by this iterator.  This will cause
     the inBounds() to return false if prior is called when the iterator is positioned
     at the first visible row.
   */
  public final boolean prior()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      return false;
    synchronized(dataSet.dataMonitor) {
      if (needsSynch)
        _synchRow();

      return _goToRow(currentRow-1);
    }
  }

  /** Returns true if the most recent navigation was in bounds.
  */
  public final boolean inBounds()
    /*-throws DataSetException-*/
  {
    return isInBounds && (index.lastRow() > -1 || newRow);
  }

  final void _post()
    /*-throws DataSetException-*/
  {
    if (editing) {
      if (dataSet != null) {
        editView._postIterator();
        internalRow = editView.internalRow;
        currentRow  = editView.currentRow;
        rowDirty  = false;
        newRow  =   false;
        editing =   false;
      }
    }
  }

  /**
      Will throw a DataSetException if not bound to a DataSet.
      Associates the Iterator with an unposted row with default
      values.  Columns that have no default values, are initialized
      with Variant.UNASSIGNED_NULL.
  */
  public final void insertRow()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      invalidUse();
    synchronized(dataSet.dataMonitor) {
      if (editing)
        _post();
      rowEdited(editView());
      editView.insertRow(true);
      editing   = true;
      rowDirty  = false;
      newRow    = true;
    }
  }

  /**
      Will throw a DataSetException if not bound to a DataSet.
      Deletes row that RowIterator is positioned at.
  */
  public final void deleteRow()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      invalidUse();
    synchronized(dataSet.dataMonitor) {
      if (editing)
        _post();
      editView().goToInternalRow(internalRow);
      editView.deleteRow();
      internalRow = editView.internalRow;
      currentRow  = editView.currentRow;
    }
  }

  private final DataSetView editView()
    /*-throws DataSetException-*/
  {
    if (editView == null) {
      editView = new DataSetView();
      synchronized(dataSet.dataMonitor) {
        editView.setStorageDataSet(dataSet.dataSetStore);
        editView.setSort(dataSet.getSort());
        editView.setMasterLink(dataSet.getMasterLink());
        if (dataSet.getRowFilterListener() != null) {
          try {
            editView.addRowFilterListener(dataSet.getRowFilterListener());
          }
          catch(java.util.TooManyListenersException ex) {
            DiagnosticJLimo.printStackTrace(ex);  // Should never happen.
          }
        }
        editView.open();
      }
    }
    return editView;
  }

  /**
      Terminates edit mode for RowIterator.  Most useful when bound to DataSets.
      Causes new or edited row changes to be posted to the bound DataSet.
  */
  public final void post()
    /*-throws DataSetException-*/
  {
    if (dataSet != null) {
      if (editing) {
        synchronized(dataSet.dataMonitor) {
          _post();
        }
      }
    }
    else {
      editing   = false;
      rowDirty  = false;
      newRow    = false;
    }
  }

  /**
      Will throw a DataSetException if not bound to a DataSet.
      Cancels edit mode and any edits made to the RowIterator.
  */
  public final void cancel()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      invalidUse();
    if (editing) {
      editView.cancel();
      editing   = false;
      rowDirty  = false;
      newRow    = false;
    }
  }

  /**
      Will throw a DataSetException if not bound to a DataSet.
      Positions iterator to row with values specified by rowLocate.  Behaves
      much the same as DataSet.locate().
  */
  public final boolean locate(ReadRow rowLocate, int locateOptions) /*-throws DataSetException-*/ {
    if (dataSet == null)
      invalidUse();
    synchronized(dataSet.dataMonitor) {
      if (editing)
        _post();
      long rowFound = dataSet.dataSetStore.find(index, currentRow, rowLocate.getColumnList().getScopedArray(), rowLocate, locateOptions);
      if (rowFound < 0)
        return false;
      _goToRow(rowFound);
      return true;
    }
  }

  RowVariant[] getLocateValues(ColumnList compatibleList)
    /*-throws DataSetException-*/
  {
    if (dataSet != null) {
      rowValues       = editView().rowValues;
      if (!editing)
        dataSet.dataSetStore.getRowData(internalRow, rowValues);
      return super.getLocateValues(compatibleList);
    }
    return readRow.getLocateValues(compatibleList);
  }

  /**
      Will throw a DataSetException if not bound to a DataSet.
      @returns the internalRow that RowIterator is positioned at.
  */
  public final long getInternalRow()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      invalidUse();
    if (needsSynch) {
      synchronized(dataSet.dataMonitor) {
        _synchRow();
      }
    }
    return internalRow;
  }

  /**
      Will throw a DataSetException if not bound to a DataSet.
      @returns the row position the RowIterator is positioned at.
  */
  public final int getRow() {
    return (int)getLongRow();
  }

  final long getLongRow()
    /*-throws DataSetException-*/
  {
    if (dataSet == null)
      invalidUse();
    if (needsSynch) {
      synchronized(dataSet.dataMonitor) {
        _synchRow();
      }
    }
    return currentRow;
  }

  /**
   * Returns the DataSet this RowIterator is bound to.
   * @return  the DataSet this RowIterator is bound to.
   */
  public final DataSet getDataSet()
  {
    return dataSet;
  }

  /**
   * Returns the {@link com.borland.dx.dataset.ReadWriteRow} this RowIterator is bound to.
   * @return  The {@link com.borland.dx.dataset.ReadWriteRow} this RowIterator is bound to.
   */
  public final ReadWriteRow getReadWriteRow()
  {
    return writeRow;
  }

  /**
   * Returns the {@link com.borland.dx.dataset.ReadWriteRow} this RowIterator is bound to.
   * @return  The {@link com.borland.dx.dataset.ReadWriteRow} this RowIterator is bound to.
   */
  public final ReadRow getReadRow()
  {
    return writeRow;
  }

  /**
   *  Returns whether the DataSet is being edited or not.
   * @return  <b>true</b> if the DataSet is being edited or not.
   */
  public final boolean isEditing() { return editing; }

  /**
   * Returns whether data is being added to a new row in the DataSet or not.
   * @return <b>true</b> if the data is being added to a new row in the DataSet or not.
   */
  public final boolean isEditingNewRow() { return editing && newRow;}


  int[] getRequiredOrdinals() {
    return dataSet.getRequiredOrdinals();
  }

  private Index           index;
  private RowVariant      value = new RowVariant();
  private DataSet         dataSet;
  private DataSet         dataSetListener;
  private ReadRow         readRow;
  private ReadWriteRow    writeRow;
  private DataSetView     editView;
          long            internalRow;
          long             currentRow;
  private boolean         editing;
  private boolean         rowDirty;
  private boolean         newRow;
          boolean         needsSynch;
          boolean         isInBounds;

          RowIterator        next;
}
