//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DetailIndex.java,v 7.8 2003/07/17 00:36:11 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.Trace;
import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DiagnosticJLimo;


class DetailIndex extends Index
  implements MasterUpdateListener
{

  public void masterUpdate(MasterUpdateEvent event) { }

  DetailIndex(DataSet detailDataSet)
    /*-throws DataSetException-*/
  {
    MasterLinkDescriptor masterLink = detailDataSet.getMasterLink();

    this.detailDataSet          = detailDataSet;
    this.detailDataSetStore     = detailDataSet.getStorageDataSet();
    this.masterDataSet          = masterLink.getMasterDataSet();
    this.masterLinkColumns      = masterLink.getMasterLinkColumns();
    this.detailLinkColumns      = masterLink.getDetailLinkColumns();
    this.fetchAsNeeded          = masterLink.isFetchAsNeeded();
    this.cascadeUpdates         = masterLink.isCascadeUpdates();
    this.cascadeDeletes         = masterLink.isCascadeDeletes();

    detailDataSetStore.checkMasterLink(masterLink);

    if (this.detailLinkColumns == null)
      this.detailLinkColumns = this.masterLinkColumns;


    DiagnosticJLimo.trace(   Trace.Detail,
                          "StorageDataSet.openDetailIndex "  //NORES
                        + masterDataSet.getStorageDataSet().getTableName()
                        +"->>"+detailDataSet.getTableName()  //NORES
                      );



//!    MasterLinkDescriptor masterLink = detailDataSet.getMasterLink();

    if (      masterDataSet == null || masterLinkColumns == null || masterLinkColumns.length < 1
          ||  (detailLinkColumns != null && masterLinkColumns.length != detailLinkColumns.length))
    {
      DataSetException.throwLinkColumnsError();
    }

    //! Need to force master open to do tests.  fix for 5518.
    //!
    masterDataSet.open();

    if (fetchAsNeeded) {
//!      Diagnostic.println("detail:  "+detailDataSet.getTableName()+" "+detailDataSet.dataSetStore.getStoreName());
      detailDataSetStore.initFetchDataSet(masterDataSet, masterLinkColumns, detailLinkColumns);
      if (canLoadDetails(masterDataSet, masterLinkColumns, true)) {
        loadDetails();

        // Must be done after loadDetails, since loadDetails can cause a restructure
        // to happen which would empty the fetchDataSet.
        //
        detailDataSetStore.recordDetailsFetched();
      }
    }
  }

  final void init() /*-throws DataSetException-*/ {

    //! fix for 14358.  TextDataFile load causes storageDataSet.fetchDataSet to
    //! be set to null in between DetailIndex() constructor and this init() method.
    //!
    if (fetchAsNeeded && detailDataSetStore.fetchDataSet == null)
      detailDataSetStore.initFetchDataSet(masterDataSet, masterLinkColumns, detailLinkColumns);

    if (detailDataSet.getColumnCount() < 1 && masterDataSet.getLongRowCount() < 1)
      DataSetException.dataSetHasNoRows(masterDataSet);
//!   Diagnostic.println("detail:  "+detailDataSet.columnCount());


//!   Diagnostic.println("detailDataSetStore:  "+detailDataSetStore.getColumnCount());
    for (int index = 0; index < detailLinkColumns.length; ++index) {
      if (detailDataSetStore.getColumn(detailLinkColumns[index]).getDataType() != masterDataSet.getColumn(masterLinkColumns[index]).getDataType()) {
        DataSetException.throwLinkColumnsError();
      }
    }

    SortDescriptor oldSort = detailDataSet.getSort();
    // Being the nice guy I am, I try to reconcile the sort descriptor with
    // the linking columns.  If things are too out of sync, I throw an exception.
    //
    String[]  sortKeys    = detailDataSet.getSortKeys();
    String[]  newSortKeys = null;
    boolean   descending  = false;
    if (sortKeys != null && sortKeys.length > 0) {
      int length  = sortKeys.length;
      if (length > detailLinkColumns.length)
        length  = detailLinkColumns.length;

      boolean columnsOverlap  = true;
      for (int index = 0; index < length; ++index) {
        if (!detailLinkColumns[index].equals(sortKeys[index]))
          columnsOverlap  = false;
      }

      if (!columnsOverlap) {
        for (int index = 0; index < sortKeys.length; ++index) {
          if (isDetailLinkColumn(sortKeys[index]))
            DataSetException.masterDetailViewError();
        }
        newSortKeys = new String[detailLinkColumns.length+sortKeys.length];
        System.arraycopy(detailLinkColumns, 0, newSortKeys, 0, detailLinkColumns.length);
        System.arraycopy(sortKeys, 0, newSortKeys, detailLinkColumns.length, sortKeys.length);
      }
      else if (detailLinkColumns.length > sortKeys.length) {
        newSortKeys = detailLinkColumns;
      }
      else
        newSortKeys = sortKeys;
      descending  = detailDataSet.getSort().isDescending();
    }
    else
      newSortKeys = detailLinkColumns;



    // Must always set the sort property because we don't support caseInsensitive
    // links and sortKeys may change.
    //
    detailDataSet.resetSort(new SortDescriptor( null, newSortKeys, oldSort == null ? null : oldSort.getDescending(),
                                                (oldSort == null ? null : oldSort.getLocaleName()), oldSort == null ? 0 : oldSort.getOptions()));


    detailDataSetStore.openIndex(detailDataSet);
    this.index              = detailDataSet.index;
    this.detailRow          = new DataRow(detailDataSet, detailLinkColumns);


    detailDataSetView = new DataSetView();
    detailDataSetView.setStorageDataSet(detailDataSetStore);
    detailDataSetView.setSort(detailDataSet.getSort());
    if (detailDataSet.getRowFilterListener() != null) {
      try {
        detailDataSetView.addRowFilterListener(detailDataSet.getRowFilterListener());
      }
      catch(java.util.TooManyListenersException ex) {
        //Diagnostic.fail();
        DataSetException.throwExceptionChain(ex);
      }
    }
    detailDataSetView.open();
//!   Diagnostic.println(" opened:  "+detailDataSetStore.getName());
    detailRow         = new DataRow(detailDataSet, detailLinkColumns);

    DiagnosticJLimo.trace(Trace.Detail, "initMasterLink");
    masterRow  = new DataRow(masterDataSet, masterLinkColumns);
    masterDataSet.addMasterUpdateListener(this);
    // Must remove first, because could get here by dependent close access event
    // and then a reopen of the dependent (detailDataSet).  ie sort master and detail
    // must be closed and opened again.
    //
    masterDataSet.removeAccessListener(detailDataSet);
    masterDataSet.addAccessListener(detailDataSet);
//!   Diagnostic.println("detailDataSet:  "+detailDataSet.getStorageDataSet().getName()+" "+detailDataSet.getTableName());
    masterDataSet.addMasterNavigateListener(detailDataSet);

    reLink(false);
  }


  private boolean isDetailLinkColumn(String columnName) {
    for (int index = 0; index < detailLinkColumns.length; ++index) {
      if (detailLinkColumns[index].equals(columnName))
        return true;
    }
    return false;
  }

  final void setDefaultValues(ReadWriteRow row)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.trace(Trace.Detail, "assign default link column values: "+masterDataSet.toString());
    copyMasterRowToDetailRow(masterDataSet, row);
  }

  final void close(DataSet detailDataSet, boolean preserveAccessListener) /*-throws DataSetException-*/ {
    if (!preserveAccessListener)
      masterDataSet.removeAccessListener(detailDataSet);
    masterDataSet.removeMasterUpdateListener(this);
    masterDataSet.removeMasterNavigateListener(detailDataSet);
    DiagnosticJLimo.check(detailDataSetView != null);
    if (detailDataSetView != null)
        detailDataSetView.close();
  }

  final void copyMasterRowToDetailRow(ReadRow masterRow, ReadWriteRow detailRow)
    /*-throws DataSetException-*/
  {
    masterRow.copyTo(masterLinkColumns, masterRow, detailLinkColumns, detailRow);
  }

  final void copyPostedMasterRowToDetailRow(ReadWriteRow detailRow)
    /*-throws DataSetException-*/
  {
    if (masterDataSet.isEditing() && !masterDataSet.isEditingNewRow()) {
      masterDataSet.getStorageDataSet().getRowData(masterDataSet, masterDataSet.getLongRow(), masterRow);
      masterDataSet.copyTo(masterLinkColumns, masterRow, detailLinkColumns, detailRow);
    }
    else {
      masterDataSet.copyTo(masterLinkColumns, masterDataSet, detailLinkColumns, detailRow);
    }
  }


  private long locateDetail(DataRow row, int options)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.check(detailDataSetView.getStorageDataSet() == detailDataSet.getStorageDataSet());

    if (detailDataSetView.locate(row, options)) {
      return detailDataSetView.getLongRow();
    }
    else
      return -1;
  }

  private final boolean isOkToRelink() {
    // Be careful about opening the master.  Detail may be linked to itself.
    // Potential for infinite recursion.
    //
    if (!masterDataSet.isOpen() || masterDataSet.getLongRowCount() < 1) {
        DiagnosticJLimo.trace(Trace.Detail, "relink on empty master");
    }
    else if (!detailDataSetView.isOpen()) {
      // Can get here inadvertently when all dependents of a StorageDataSet
      // are asked to close.  - detailDataSetView.close() happens before
      // detail.close() and detail has an unposted row, _post will ask to
      // relink the details.
      //
      DiagnosticJLimo.trace(Trace.Detail, "detail closed");
    }
    else
      return true;
    return false;
  }

  boolean needsRelink() {
    if (isOkToRelink() && rangeEnd > -1) {
      copyPostedMasterRowToDetailRow(detailRow);
      // Relink is sometimes unnecessary.  Note that when there are many
      // DataSet instances for a master, an edit to any of these masters will
      // cause all masters to consider relinking even if the linking columns
      // have not been modified.  Relinking details can be annoying to apps
      // since it causes edited rows to be posted and repositions to the start
      // of the group.
      //
      if (detailDataSetView.getLongRowCount() > 0 && detailDataSetView.findDifference(0, detailRow) < 0)
        return false;
    }
    return true;
  }

  void reLink(boolean doFetchAsNeeded)
    /*-throws DataSetException-*/
  {
    long rowStart;
    long rowEnd;

    rangeStart  = 0;
    rangeEnd    = -1;

    DiagnosticJLimo.trace(Trace.Detail, "DetailIndex.reLink()");

//    try {
      if (isOkToRelink()) {
        //detailRow.clearValues();
        copyPostedMasterRowToDetailRow(detailRow);
/*
  These slow down debug build because of the toString() operations.
        Diagnostic.trace(Trace.Detail, masterDataSet.getRow()+" masterRow:  " + masterDataSet.toString());
        Diagnostic.trace(Trace.Detail, "linking on master rowValue:  " + detailRow.toString());
*/


//!       Diagnostic.println("relink on:  "+detailRow);
        rowStart = locateDetail(detailRow, Locate.FIRST);
//!/*
//!        Variant[] rowValues = detailRow.getRowValues(detailRow.getColumnList());
//!
//!        rowStart = index.locate( 0, detailRow.getColumnList().getScopedArray(),
//!                                rowValues, Locate.FIRST
//!                              );
//!*/

        if (      canLoadDetails(detailRow, detailLinkColumns, true)
              &&  rowStart < 0 && doFetchAsNeeded && detailsLoaded)
        {
          loadDetails();
          if (fetchAsNeeded)
            detailDataSetStore.recordDetailsFetched();
          rowStart = locateDetail(detailRow, Locate.FIRST);
//!/*
//!rowStart = index.locate( 0, detailRow.getColumnList().getScopedArray(),
//!                                  rowValues, Locate.FIRST
//!                                );
//!*/
        }

        if (rowStart > -1) {
          rowEnd = locateDetail(detailRow, Locate.LAST);
//!/*
//!          rowEnd = index.locate( 0, detailRow.getColumnList().getScopedArray(),
//!                                detailRow.getRowValues(detailRow.getColumnList()),
//!                                Locate.LAST
//!                              );
//!*/

          if (rowEnd > -1) {
            rangeStart  = rowStart;
            rangeEnd    = rowEnd;
//            Diagnostic.trace(Trace.Detail, "reLink success rangeStart:  " + rangeStart + " rangeEnd: " +rangeEnd);
          }
          else {
            DiagnosticJLimo.println("detailRow:  "+detailRow);
            rowEnd = locateDetail(detailRow, Locate.LAST);
            DiagnosticJLimo.trace(Trace.Detail, "reLink FAIL rangeStart:  " + rangeStart + " rangeEnd: " +rangeEnd);
            DiagnosticJLimo.fail();
          }
        }
        else
          DiagnosticJLimo.trace(Trace.Detail, "reLink failed on locate first");
      }
//    }
//    catch(Exception ex) {
//      Diagnostic.printStackTrace(ex);
//    }
  }

  private boolean canLoadDetails(ReadRow row, String[] sourceLinkNames, boolean recordFetch)
    /*-throws DataSetException-*/
  {
    // Avoid recursion.
    //
    if (loadingRows)
      return false;

    if (!fetchAsNeeded)
      return false;

    masterDataSet.open();

    if (masterDataSet.getLongRowCount() < 1)
      return false;
//!/*
//!    if ((masterDataSet.getStatus()&RowStatus.DETAILS_FETCHED)!=0)
//!      return false;
//!*/

    // Some jdbc drivers (ie Visigenics) do not like the setting of null
    // paramatized queries, so just don't load if any of the linking values
    // are null - could add a property to the MasterLinkDescriptor to allow the
    // user to control this in the future.
    //
    for (int index = 0; index < masterLinkColumns.length; ++index) {
      if (masterDataSet.getVariantStorage(masterLinkColumns[index]).isNull())
        return false;
    }

    if (fetchAsNeeded && detailDataSetStore.detailsFetched(row, sourceLinkNames, detailLinkColumns))
      return false;

    return true;
  }

  final boolean canLoadDetails(boolean recordFetch)
    /*-throws DataSetException-*/
  {
    if (canLoadDetails(masterDataSet, masterLinkColumns, recordFetch)) {
      copyMasterRowToDetailRow(masterDataSet, detailRow);

      return locateDetail(detailRow, Locate.FIRST) < 0;
    }
    return false;
  }

  private final void loadDetails()
    /*-throws DataSetException-*/
  {
    //! Diagnostic.trace(Trace.Detail, "reLink.index.lastRow():  "+index.lastRow());
    //! Diagnostic.trace(Trace.Detail, "reLink.masterDataSet current row:  "+masterDataSet.row()+ " "+masterDataSet);
    try {
      loadingRows = true;
      //!Diagnostic.println("before loadDetailRows: "+detailDataSet.rowCount()+" "+detailDataSetView.rowCount()+" "+detailDataSet+" "+detailDataSetView.index);
//!     Diagnostic.println("before loadDetailRows: "+detailDataSet.columnCount());
      detailDataSetStore.loadDetailRows(detailDataSet, masterDataSet);
      detailsLoaded = true;
      //! Diagnostic.println("after loadDetailRows: "+detailDataSet.rowCount()+" "+detailDataSetView.rowCount());
//!     Diagnostic.println("after loadDetailRows: "+detailDataSet.columnCount());
    }
//    catch (DataSetException ex) {
//      Diagnostic.println("loadDetails encountered exception");//! Diagnostic.printStackTrace(ex);
//    }
    finally {
      loadingRows = false;
    }
  }

  public long lastRow() {
    if (rangeEnd > -1)
      return rangeEnd - rangeStart;
    return -1;
  }

  public long internalRow(long row)
    /*-throws DataSetException-*/
  {
    if(!((row > -1 && row <= rangeEnd - rangeStart) || (row == 0))) {
      DiagnosticJLimo.printStackTrace();
      DiagnosticJLimo.println("row:  "+row+" "+rangeStart+" "+rangeEnd);
//!     Diagnostic.exit(1);
    }

//!   Diagnostic.check((row > -1 && row <= rangeEnd - rangeStart) || (row == 0));
    return index.internalRow(rangeStart+row);
  }

  public long getInternalRow() {
    return internalRow;
  }

  public void markStatus(long row, int status, boolean on)
    /*-throws DataSetException-*/
  {
    detailDataSet.dataSetStore.markStatus(internalRow(row), status, on);
  }

  private final long adjustForBounds(long row)
    /*-throws DataSetException-*/
  {

    if (row > rangeEnd && rangeEnd > -1 && row > 0) {
//!     Diagnostic.println("end Going from "+row+" to "+rangeEnd);
      row = rangeEnd;
      internalRow = index.internalRow(row);
    }
    else if (row < rangeStart) {
//!     Diagnostic.println("start Going from "+row+" to "+rangeStart);
      row = rangeStart;
      internalRow = index.internalRow(row);
    }
    else {
      internalRow = index.getInternalRow();
    }


    return row - rangeStart;
  }

  public long findClosest(long internalRow, long row)
    /*-throws DataSetException-*/
  {
    return adjustForBounds(index.findClosest(internalRow, rangeStart+row));
  }

  public long findClosest(long internalRow)
    /*-throws DataSetException-*/
  {
    return adjustForBounds(index.findClosest(internalRow));
  }

  final boolean compareRow(int row) { DiagnosticJLimo.fail(); return false; }
  final void loadSearchValues(Variant[] values) {DiagnosticJLimo.fail(); }

  public long locate(  long           startRow,
                      Column[]      scopedColumns,
                      RowVariant[]  values,
                      int           locateOptions
            )
    /*-throws DataSetException-*/
  {
    long rowFound  = index.locate( startRow + rangeStart, scopedColumns,values, locateOptions|Locate.DETAIL);
    if (rowFound > -1 && rowFound <= rangeEnd)
      return rowFound - rangeStart;
    return -1;
  }

  public final void masterCanChange(MasterUpdateEvent event)
    throws Exception
  {
    if (event.getMaster() == masterDataSet) {
      if (!cascadeUpdates && lastRow() >= 0 && masterRow.hasColumn(event.getColumn().getColumnName()) != null)
        ValidationException.cannotOrphanDetails(detailDataSet.getTableName());
    }
  }

  public final void masterDeleting(MasterUpdateEvent event)
    throws Exception
  {
    if (event.getMaster() == masterDataSet) {
      DiagnosticJLimo.trace(Trace.Detail, "attempt to delete master row");

  //!   masterDataSet.getStorageDataSet().getRowData(masterDataSet, masterDataSet.getRow(), masterRow);
      masterDataSet.getDataRow(masterRow);

      copyPostedMasterRowToDetailRow(detailRow);

      if (cascadeDeletes) {
        // Must used detailDataSet instead of detailDataSetView, because
        // we must navigate the master of the next level detail.
        //
        while (detailDataSet.locate(detailRow, Locate.FIRST)) {
          detailDataSet.deleteRow();
        }
      }
      else {
        if (detailDataSetView.locate(detailRow, Locate.FIRST))
          ValidationException.cannotOrphanDetails(detailDataSetView.getTableName());
      }
    }
  }

  public final void masterChanging(MasterUpdateEvent event)
    throws Exception
  {
    if (event.getMaster() == masterDataSet) {
      if (detailDataSet != masterDataSet)
        detailDataSet.post();
      masterDataSet.getStorageDataSet().getRowData(masterDataSet, masterDataSet.getLongRow(), masterRow);

      ReadRow changingRow = event.getChangingRow();

      int linkCount;

      if (changingRow.columnList.hasScopedColumns()) {
        linkCount = 0;
        for (int index = 0; index < masterLinkColumns.length; ++index) {
          if (changingRow.hasColumn(masterLinkColumns[index]) != null)
            ++linkCount;
        }
      }
      else
        linkCount = masterLinkColumns.length;

      if (linkCount != 0) {
        if (linkCount != masterLinkColumns.length || !changingRow.equals(masterRow)) {
          copyMasterRowToDetailRow(masterRow, detailRow);
          if (cascadeUpdates) {
            while (detailDataSet.locate(detailRow, Locate.FIRST)) {
              detailRow.copyTo(masterLinkColumns, changingRow, detailLinkColumns, detailDataSet);
              detailDataSet.post();
              copyMasterRowToDetailRow(masterRow, detailRow);
            }
          }
          else if (detailDataSetView.locate(detailRow, Locate.FIRST))
            ValidationException.cannotOrphanDetails(detailDataSetView.getTableName());
        }
      }
    }
  }

  final boolean detailsLoaded() {
    return detailsLoaded;
  }

  final void emptyAllRows(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    StorageDataSet dataSetStore = dataSet.getStorageDataSet();
    dataSet.open();
    if (dataSetStore != null) {
      dataSetStore.deleteAllRows(dataSet, true);
      if (dataSetStore.getDeletedRowCount() > 0) {
        DataSetView view = new DataSetView();
        dataSetStore.getDeletedRows(view);
        DataRow locRow  = new DataRow(view, detailLinkColumns);
        copyMasterRowToDetailRow(masterDataSet, locRow);
        while(view.locate(locRow, Locate.FIRST)) {
          view.emptyRow();
        }
        view.close();
      }
    }
  }

  public void setInsertPos(long pos)
    /*-throws DataSetException-*/
  {
    index.setInsertPos(rangeStart+pos);
  }

  public long moveRow(long pos, long delta)
    /*-throws DataSetException-*/
  {
    long targetPos = rangeStart+pos+delta;
    if (targetPos < rangeStart)
      delta += (rangeStart-targetPos);
    else if (targetPos > rangeEnd)
      delta -= (targetPos-rangeEnd);

    return index.moveRow(rangeStart+pos, delta);
  }

  private long            internalRow;

  private boolean         cascadeDeletes;
  private boolean         cascadeUpdates;
  private boolean         detailsLoaded;
  private long             rangeStart;
  private long             rangeEnd;
  private DataRow         detailRow;
  private DataRow         masterRow;
  private DataSet         masterDataSet;
  private Index           index;
  private StorageDataSet  detailDataSetStore;
  private DataSet         detailDataSet;
  private DataSetView     detailDataSetView;
  private boolean         loadingRows;
  private boolean         fetchAsNeeded;
  private String[]        detailLinkColumns;
  private String[]        masterLinkColumns;
}
