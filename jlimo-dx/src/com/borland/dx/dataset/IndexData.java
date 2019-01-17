//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/IndexData.java,v 7.4.2.2 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.TriStateProperty;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.Locale;
import com.borland.dx.dataset.cons.ColumnConst;


public abstract class IndexData extends MatrixData
{
//! public abstract long    insertStoreRow(ReadRow row, RowVariant[] values, int status) /*-throws DataSetException-*/;
  public abstract void    deleteStoreRow(long internalRow) /*-throws DataSetException-*/;
  public abstract void    emptyStoreRow(long internalRow) /*-throws DataSetException-*/;


  public abstract DirectIndex   createIndex(  StorageDataSet    dataSet,
                                              SortDescriptor    descriptor,
                                              RowFilterListener rowFilterListener,
                                              DataRow           filterRow,
                                              RowVariant[]      filterValues,
                                              int               visibleMask,
                                              int               invisibleMask
                                     ) /*-throws DataSetException-*/;


  public void prepareRestructure(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
  }

  public void commitRestructure(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
  }

  public          void    restoreStoreRow(long internalRow) /*-throws DataSetException-*/
  {}
  public          void    replaceStoreRow(long internalRow, RowVariant[] values, int status) /*-throws DataSetException-*/ {
    DataSetException.missingReplaceRow();
  }

  public boolean  canCalc() {
    return true;
  }

  public final DirectIndex openIndex(DataSet dataSet, boolean create)
    /*-throws DataSetException-*/
  {

    RowFilterListener  rowFilterListener  = dataSet.getRowFilterListener();


    DirectIndex index = openIndex( dataSet.getStorageDataSet(),
                             dataSet.getSort(),
                             rowFilterListener,
                             dataSet.visibleMask,
                             dataSet.invisibleMask,
                             create
                           );

    DiagnosticJLimo.check(index != null || !create);
    return index;
  }

   public final DirectIndex openIndex(  StorageDataSet    dataSet,
                                        SortDescriptor    descriptor,
                                        RowFilterListener rowFilterListener,
                                        int               visibleMask,
                                        int               invisibleMask,
                                        boolean           create
                               )
    /*-throws DataSetException-*/
  {

    if (visibleMask == RowStatus.UPDATED && updateIndex != null)
      return updateIndex;

    if (visibleMask == RowStatus.DELETED && deleteIndex != null)
      return deleteIndex;

    if (visibleMask == RowStatus.INSERTED && insertIndex != null)
      return insertIndex;

    DirectIndex index = findIndex(descriptor, dataSet.getLocale(), rowFilterListener, visibleMask, invisibleMask);
    if (index == null /*&& openPersistentIndexes()*/)
      index = findIndex(descriptor, dataSet.getLocale(), rowFilterListener, visibleMask, invisibleMask);
    if (index == null && create) {
      if (descriptor != null && descriptor.isUnique() && dataSet.getDuplicates() != null) {
        DataSetException.deleteDuplicates();
      }
      DataRow       filterRow = null;
      RowVariant[]  filterValues = null;
      if (rowFilterListener != null && !isMemoryData()) {
        filterRow = new DataRow(dataSet);
        filterValues = filterRow.getRowValues(dataSet.getColumnList());
      }

      if (descriptor != null && descriptor.isPrimary()) {
        if (autoIncrementOrdinal > -1 && dataSet.getColumnList().cols[autoIncrementOrdinal].isPrimaryKey())
          DataSetException.duplicatePrimary();
        SortDescriptor indexSort;
        for (int i = 0; i < indexesLength; ++i) {
          indexSort = this.indexes[i].getSort();
          if (indexSort != null && indexSort.isPrimary())
            DataSetException.duplicatePrimary();
        }
        String[] keys = descriptor.getKeys();

        if (keys == null || keys.length < 1)
          DataSetException.noPrimaryKey();
      }

      if (descriptor != null) {
        if (descriptor.isSortAsInserted()) {
          //! This is comming in late, so we will have to resource the string
          //! in the next build.
          //!
          if (descriptor.isUnique())
            throw new DataSetException(Res.bundle.format(ResIndex.NoSortAsInserted, descriptor.getIndexName()));
        }
        else if (descriptor.getKeys().length < 1 && !descriptor.isSortAsInserted() && visibleMask != RowStatus.INSERTED)
          DataSetException.invalidSort(descriptor.getIndexName());
      }

      index = createIndex(  dataSet, descriptor,
                            rowFilterListener, filterRow, filterValues,
                            visibleMask, invisibleMask
                         );

      if (index.isMaintained()) {
//!        addIndex(/*dataSet,*/ index, visibleMask);
        addIndex(index, visibleMask);
        if (descriptor != null && descriptor.isPrimary())
          initRequiredOrdinals(dataSet);
      }
    }
    return index;
  }

/*
  public boolean openPersistentIndexes()
  {
    return false;
  }
*/

  public void openPersistentIndex(int mask)
    /*-throws DataSetException-*/
  {
  }
//!/*
//!  public void addIndex(StorageDataSet dataSet, DirectIndex index, int visibleMask) {
//!    // MatrixData may have been reset due to a Restructure operation.
//!    //
//!    if (dataSet != null && dataSet.getMatrixData() != null) {
//!      dataSet.getMatrixData().addIndex(index, visibleMask);
//!    }
//!    else
//!      addIndex(index, visibleMask);
//!  }
//!*/

  //!Comment outdated?  Should only be called by addIndex that takes a dataSet.  This protects against
  //!recursion issues where DataSet may need to be restructured before an index can be built.
  //!
  //!OVERRIDDEN BY TableData!!!
  //!
  public void addIndex(DirectIndex index, int visibleMask) {
    if (visibleMask == RowStatus.UPDATED)
      updateIndex = index;
    else if (visibleMask == RowStatus.DELETED)
      deleteIndex = index;
    else if (visibleMask == RowStatus.INSERTED) {
      DiagnosticJLimo.check(insertIndex == null);
      insertIndex = index;
    }
    else {
      int oldLength = indexes == null ? 0 : indexesLength;
      DirectIndex[] newIndexes  = new DirectIndex[oldLength+1];
      if (oldLength > 0)
        System.arraycopy(indexes, 0, newIndexes, 0, oldLength);
      indexes             = newIndexes;
      indexes[oldLength]  = index;
      indexesLength       = indexes.length;
//!     Diagnostic.println("addIndex:  "+this);
    }
  }

  public final void dropAllIndexes()
    /*-throws DataSetException-*/
  {

//    openPersistentIndexes();
    for (int i = 0; i < indexesLength; ++i)
      indexes[i].dropIndex();

    indexesLength   = 0;
    indexes         = null;
//!    /*  Don't know why this was done.  Causes
//!        trouble with DataStore because the indexes
//!        have not been properly closed.  TableData also
//!        tracks when persistent indexes have been opened.
//!    insertIndex     = null;
//!    deleteIndex     = null;
//!    updateIndex     = null;
//!    */
  }

  public final void dropIndex(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    dropIndex(  dataSet.getSort(),
                dataSet.getStorageDataSet().getLocale(),
                dataSet.getRowFilterListener(),
                dataSet.visibleMask,
                dataSet.invisibleMask
             );
  }

  // WARNING:  method signature overridden by TableData.
  //
  public boolean dropIndex( SortDescriptor    descriptor,
                            Locale            locale,
                            RowFilterListener rowFilter,
                            int               visibleMask,
                            int               invisibleMask
                       )
    /*-throws DataSetException-*/
  {
    DirectIndex index = findIndex(  descriptor,
                                    locale,
                                    rowFilter,
                                    visibleMask,
                                    invisibleMask
                                 );
    if (index != null) {
      index.dropIndex();
      for (int i = 0; i < indexesLength; ++i) {
        if (indexes[i] == index) {
          //!This line was added when I tried to fix a bug,
          //!but did not fix the bug.  So if it causes problems,
          //!that's how it got here.  Steve.
          //
          index.close();
          --indexesLength;
          if (indexesLength > i)
            System.arraycopy(indexes, i+1, indexes, i, indexesLength-i);
          DirectIndex[] newIndexes  = new DirectIndex[indexesLength];
          System.arraycopy(indexes, 0, newIndexes, 0, indexesLength);
          indexes = newIndexes;
          return true;
        }
      }
    }
    return false;
  }


  public void freeFetchIndex()
    /*-throws DataSetException-*/
  {
  }

  private final boolean indexNameEquals(SortDescriptor s1, SortDescriptor s2) {
    if (s1 == null || s2 == null)
      return false;
    return s1.nameEquals(s2);
  }

  private final boolean sortEquals(SortDescriptor s1, SortDescriptor s2, Locale locale) {
    if (s1 == s2)
      return true;
//!     Diagnostic.println("  sortEquals:  "+s2);
    if (s1 == null || s2 == null)
      return false;
    return s1.equals(s2, locale);
  }

  //! OVERRIDDEN BY TABLEDATA.
  //
  public DirectIndex findIndex(   SortDescriptor    descriptor,
                                        Locale            locale,
                                        RowFilterListener rowFilterListener,
                                        int               visibleMask,
                                        int               invisibleMask
                                       )
  {

//!   Diagnostic.println("findIndex:  "+descriptor);
    for (int index = 0; index < indexesLength ; ++index) {
//!     Diagnostic.println("  test:  "+indexes+" "+indexes[index].descriptor);
      if (indexNameEquals(descriptor, indexes[index].getSort()))
        return indexes[index];
      if (sortEquals(indexes[index].getSort(), descriptor, locale)) {
        if (indexes[index].getVisibleMask() == visibleMask && indexes[index].getInvisibleMask() == invisibleMask) {
          if (indexes[index].hasRowFilterListener(rowFilterListener))
            return indexes[index];
        }
      }
    }
//!     Diagnostic.println("nomatch:  ");
    return null;
  }

  public final boolean indexExists(SortDescriptor descriptor, RowFilterListener listener)
    /*-throws DataSetException-*/
  {
//    openPersistentIndexes();
    return (findIndex(descriptor, descriptor.getLocale(), listener, RowStatus.DEFAULT, RowStatus.DEFAULT_HIDDEN) != null);
  }

  public DirectIndex[] getIndices() {
    return indexes;
  }

//!/*
//!  public final long insertRow(ReadWriteRow row, RowVariant[] values, int status)
//!    /*-throws DataSetException-*/
//!  {
//!    long internalRow = insertStoreRow(row, values, status);
//!    return internalRow;
//!  }
//!*/

  public final void deleteRow(long internalRow)
    /*-throws DataSetException-*/
  {

    if (resolvable) {
      int status = getStatus(internalRow);

      //!bug 12951
      // Delete an inserted row means forget that row al together !
      // This gives consistency and simplifies resolvers
      //
      if ((status & RowStatus.INSERTED) != 0)
        emptyStoreRow(internalRow);
      else {
        // Delete an updated row means...
        if ((status & RowStatus.UPDATED) != 0)
          restoreStoreRow(internalRow);

        // Now do the real delete work:
        deleteStoreRow(internalRow);

        if (deleteIndex != null && resolvable) {
          deleteIndex.addStore(internalRow);
        }

        // indexDelete() must come after deleteStoreRow for TableData because that
        // is when tableData initializes the keys of the secondary indexs.
        //
        indexDelete(internalRow);
      }
    }
    else {
      emptyStoreRow(internalRow);
    }
  }

  public final void emptyRow(long internalRow)
    /*-throws DataSetException-*/
  {
    emptyStoreRow(internalRow);

//!    /*
//!    if (deleteIndex != null) {
//!      deleteIndex.deleteStore(internalRow);
//!    }
//!    */
  }

  public final void updateRow(long internalRow, Variant[] originalValues, RowVariant[] values, Column[] updateColumns)
    /*-throws DataSetException-*/
  {

    int count = values.length;

    if (originalValues == null) {
      for (int ordinal = 0; ordinal < count; ++ordinal)
        values[ordinal].changed = false;
      count = updateColumns.length;
      for (int ordinal = 0; ordinal < count; ++ordinal)
        values[updateColumns[ordinal].getOrdinal()].changed = true;
    }
    else {
      for (int ordinal = 0; ordinal < count; ++ordinal) {
        values[ordinal].changed = (!values[ordinal].equalsInstance(originalValues[ordinal]));
    }

//!     Diagnostic.println("compare:  "+values[ordinal].changed+" "+values[ordinal]+" "+originalValues[ordinal]);
    }

    markIndexesForUpdate(values);

    updateStoreRow(internalRow, values, updateColumns);
  }


  public long replaceLoadedRow(long internalRow, ReadWriteRow row, RowVariant[] values, int status)
    /*-throws DataSetException-*/
  {
//! What does this do for dataStore ?
//!   markIndexesForUpdate(values);

    if (internalRow == -1 || getStatus(internalRow) != RowStatus.LOADED)
      return insertRow(row, values, status);
    replaceStoreRow(internalRow, values, status);
    return internalRow;
  }


  public final int saveRow(int status)
    /*-throws DataSetException-*/
  {
    saveOriginal  = false;
    if (updateIndex != null && resolvable) {
      DiagnosticJLimo.check((status & RowStatus.DELETED) == 0);
      DiagnosticJLimo.check((status & RowStatus.ORIGINAL) == 0);

      if ((status&(RowStatus.UPDATED|RowStatus.INSERTED)) == 0)
        saveOriginal  = true;
    }
    return status;
  }

  public final void resetPendingStatus(long internalRow, boolean resolved)
    /*-throws DataSetException-*/
  {
    int status = getStatus(internalRow);
    if ((status&RowStatus.PENDING_RESOLVED) != 0) {
      if ((status&RowStatus.DELETED) != 0)
        deleteIndex.resetPendingDelete(internalRow,resolved);
      if ((status&RowStatus.UPDATED) != 0)
        updateIndex.resetPending(internalRow,resolved);
      if ((status&RowStatus.INSERTED) != 0)
        insertIndex.resetPending(internalRow,resolved);
    }
  }

  public final void resetPendingStatus(boolean resolved)
    /*-throws DataSetException-*/
  {
    //! bug 5632
    /*  updates must be resolved first because if previous
        insert was done, the RowStatus.PENDING_RESOLVED bit will be cleared
        even though the insert was done by a previous resolve.
        - comment is probably obsolete now that resolved status no longer used.
    */

    if (updateIndex != null)
      updateIndex.resetPending(resolved);

    if (insertIndex != null)
      insertIndex.resetPending(resolved);

    if (deleteIndex != null)
      deleteIndex.resetPendingDeletes(resolved);
  }

  public final void indexAdd(long internalRow)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength ; ++index)
      indexes[index].addStore(internalRow);
  }

  public  final void indexDelete(long internalRow)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].deleteStore(internalRow);
  }

  public final void markIndexesForUpdate(RowVariant[] values)
    /*-throws DataSetException-*/
  {
    indexUpdateCount  = 0;
    for (int index = 0; index < indexesLength ; ++index) {
      if (indexes[index].markForUpdate(values))
        ++indexUpdateCount;
    }
  }

  public final void clearInternalReadRow() {
    internalReadRow = null;
  }

  // OVERRIDDEN by TxData.
  //
  public final InternalRow getInternalReadRow(StorageDataSet dataSet) {
    if (internalReadRow == null) {
      internalReadRow = new InternalRow(dataSet);
    }
    return internalReadRow;
  }

  public final void recalc(StorageDataSet storageDataSet, AggManager aggManager)
    /*-throws DataSetException-*/
  {
    DataSetView dataSet = new DataSetView();
    dataSet.setStorageDataSet(storageDataSet);
    dataSet.open();
    while(dataSet.inBounds()) {
      dataSet.rowEdited();
      dataSet.next();
    }
    dataSet.post();
    dataSet.close();
  }

  public void getInsertedRows(StorageDataSet dataSet, DataSetView insertedDataSet)
    /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.INSERTED);
    if (insertIndex != null)
      MatrixData.setStorageDataSet(insertedDataSet, dataSet);
  }

  public void getDeletedRows(StorageDataSet dataSet, DataSetView deletedDataSet)
    /*-throws DataSetException-*/
  {
    MatrixData.setStorageDataSet(deletedDataSet, dataSet);
  }

  public void getUpdatedRows(StorageDataSet dataSet, DataSetView updatedDataSet)
    /*-throws DataSetException-*/
  {
    MatrixData.setStorageDataSet(updatedDataSet, dataSet);
  }

  public final void indexUpdate(long internalRow)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength ; ++index)
      indexes[index].updateStore(internalRow);
  }

  public final void closeIndexes()
    /*-throws DataSetException-*/ {
//!   Diagnostic.println("closeIndexes:  "+this+" "+indexesLength);
    for (int index = 0; index < indexesLength ; ++index) {
      indexes[index].close();
    }
    if (insertIndex != null)
      insertIndex.close();
    if (deleteIndex != null)
      deleteIndex.close();
    if (updateIndex != null)
      updateIndex.close();
  }



  // Overridden by ResolverData in DataStore so that it synchronizes
  // on its associated TableData.  This is important for blob optimizations
  // that reference both blob tables.
  // Also overridden by TableData.
  //
  public Object getDataMonitor() {
    return this;
  }

  public void cancelOperation() {
  }

  public final void setLoadCancel(StorageDataSet dataSet, LoadCancel loader) {
    dataSet.setLoadCancel(loader);
  }

  public final long getInsertedRowCount()
    /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.INSERTED);
    return insertIndex == null ? 0 : insertIndex.lastRow()+1;
  }
  public final long getDeletedRowCount()
    /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.DELETED);
    return deleteIndex == null ? 0 : deleteIndex.lastRow()+1;
  }
  public final long getUpdatedRowCount()
    /*-throws DataSetException-*/
  {
    openPersistentIndex(RowStatus.UPDATED);
    return updateIndex == null ? 0 : updateIndex.lastRow()+1;
  }

  public boolean isMemoryData() {
    return false;
  }

  public boolean needsRecalc(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    String name = getClassName(dataSet.getCalcFieldsListener());
    if ((name == null) != (calcFieldsName == null))
      return true;

    if (name != null && !name.equals(calcFieldsName))
      return true;


    name = getClassName(dataSet.getCalcAggFieldsListener());
    if ((name == null) != (calcAggFieldsName == null))
      return true;

    if (name != null && !name.equals(calcAggFieldsName))
      return true;


    return false;
  }

  private final String getClassName(Object object) {
    if (object == null)
      return null;
    return object.getClass().getName();
  }

  public void notifyRecalc(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    calcFieldsName    = getClassName(dataSet.getCalcFieldsListener());
    calcAggFieldsName = getClassName(dataSet.getCalcAggFieldsListener());
  }

  // OVERRIDDEN by MemoryData.
  //
  public boolean isEmpty()
    /*-throws DataSetException-*/
  {
    return getRowCount() == 0;
  }

  public String getCalcFieldsName() {
    return calcFieldsName;
  }
  public String getCalcAggFieldsName() {
    return calcAggFieldsName;
  }

  public void setCalcNames(String calcFieldsName, String calcAggFieldsName) {
    this.calcFieldsName     = calcFieldsName;
    this.calcAggFieldsName  = calcAggFieldsName;
  }

  public MatrixData getData() {
    return this;
  }

  public final void addDataSet(DataSet listener)
    /*-throws DataSetException-*/
  {
    dataChangeListeners = addDataSet(dataChangeListeners, listener);
  }

  public final void removeDataSet(DataSet listener)
    /*-throws DataSetException-*/
  {
    dataChangeListeners = removeDataSet(dataChangeListeners, listener);
  }

  static int findDataSet(DataSet[] listeners, DataSet listener) {
    if (listeners != null ) {
      for (int index = 0; index < listeners.length; ++index)
        if (listeners[index] == listener)
          return index;
    }
    return -1;
  }

  static final DataSet[] addDataSet(DataSet[] listeners, DataSet listener) {
    if (findDataSet(listeners, listener) < 0) {
      DataSet[] newListeners;

      if (listeners == null)
        newListeners = new DataSet[1];
      else {
        newListeners = new DataSet[listeners.length+1];
        System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
      }

      newListeners[newListeners.length-1] = listener;
//      Diagnostic.check(newListeners.length < 128);
      listeners = newListeners;
    }
    return listeners;
  }

  static final DataSet[] removeDataSet(DataSet[] listeners, DataSet listener) {
    int index = findDataSet(listeners, listener);
    if (index > -1) {
      // Important: hasListeners() expects listeners too be null if there are no listeners.
      if (listeners.length == 1)
        listeners = null;
      else {
        DataSet[] newListeners = new DataSet[listeners.length-1];
        System.arraycopy(listeners, 0, newListeners, 0, index);

        if (index < newListeners.length)
          System.arraycopy(listeners, index+1, newListeners, index, newListeners.length-index);

        listeners = newListeners;
      }
    }
    return listeners;
  }

  public IndexData getIndexData() {
    return this;
  }

  public void initRequiredOrdinals(StorageDataSet dataSet) {
    ColumnList columnList = dataSet.getColumnList();
    Column[] columns = columnList.cols;
    int count = columnList.count;
    Column column;
    java.util.Vector list = new java.util.Vector();
    String keys[];
    SortDescriptor descriptor;
    boolean foundPrimary = false;
    requiredOrdinals = null;
    for (int index = 0; index < count; ++index) {
      if (columns[index].isRequired())
        list.addElement(columns[index]);
    }
    for (int index = 0; index < indexesLength; ++index) {
      descriptor = indexes[index].getSort();
      if (descriptor != null && descriptor.isPrimary()) {

        keys = descriptor.getKeys();
        for (int keyIndex = 0; keyIndex < keys.length; ++keyIndex) {
          column = dataSet.getColumn(keys[keyIndex]);
          column.setPrimaryKey(true);
//          if (!column.isRequired())
          if (!column.is(ColumnConst.REQUIRED))
            list.addElement(column);
        }
      }
    }
    if (list.size() > 0) {
      requiredOrdinals = new int[list.size()];
      for (int index = 0; index < requiredOrdinals.length; ++index) {
        requiredOrdinals[index] = ((Column)list.elementAt(index)).getOrdinal();
      }
    }
  }

  public int[] getRequiredOrdinals() { return requiredOrdinals; }


            DataSet[]                 dataChangeListeners;

  protected DirectIndex       insertIndex;
  protected DirectIndex       updateIndex;
  protected DirectIndex       deleteIndex;
  protected boolean           resolvable;

  private   InternalRow       internalReadRow;
  protected DirectIndex[]     indexes;
  protected int               indexesLength;
  protected boolean           saveOriginal;
  protected int               indexUpdateCount;
  private    String           calcFieldsName;
  private    String           calcAggFieldsName;
  private    int[]            requiredOrdinals;
  public     int              autoIncrementOrdinal = -1;
}


