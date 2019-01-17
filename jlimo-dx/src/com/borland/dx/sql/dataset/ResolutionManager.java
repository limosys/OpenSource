//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ResolutionManager.java,v 7.2 2003/07/08 23:41:57 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.ErrorResponse;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;
import com.borland.dx.dataset.cons.DataBits;

/**
 * This component is used internally by other com.borland classes.
 *  You should never use this class directly.
 */
public abstract class ResolutionManager
{
  public ResolutionManager() {
    resolverResponse        = new ResolverResponse();
    response                = new ErrorResponse();
    postEdits               = true;
    resetPendingStatus      = true;
  }

  public void saveChanges(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    DataSet  dsArray[] = new DataSet[1];
    dsArray[0] = dataSet;
    saveChanges(dsArray);
  }

  // Save a collection of DataSets within a Database. The method is synchronized as the Resolver
  // classes are not thread-safe.
  //
  public synchronized void saveChanges(DataSet[] dataSets)
    /*-throws DataSetException-*/
  {
    Exception ex   = null;
    boolean transactionStarted  = false;

    this.dataSets = dataSets;

    try {
      this.dataSets = startResolution(dataSets,postEdits);

      DataSet[] resOrder = findResolutionOrder(dataSets);

      transactionSupport.start();

      transactionStarted  = true;

      processDataSetDeletes(resOrder);

      if (insertsFirst) {
        processDataSetInserts(resOrder);
        processDataSetUpdates(resOrder);
      }
      else {
        processDataSetUpdates(resOrder);
        processDataSetInserts(resOrder);
      }

      transactionSupport.commit();

    }
    catch (Exception ex0) {
      ex = ex0;
    }

    try {
      if (resetPendingStatus)
        resetPendingStatus(dataSets, ex == null);
    }
    catch(Exception ex1) {
      if (ex == null)
        ex = ex1;
    }

    if (ex != null) {
      try {
        // Diagnostic.printStackTrace(ex);
        transactionSupport.rollback();
      }
      catch(Exception ex2) {
        // We want to surface the first error, so ignore this.
      }
    }

    try {
      endResolution(dataSets);
    }
    catch(Exception ex3) {
      if (ex == null)
        ex = ex3;
    }


    if (ex != null)
      DataSetException.throwException(DataSetException.EXCEPTION_CHAIN, ex);

    if (errorDataSets != null)
      throw new ResolutionException(ResolutionException.RESOLVE_PARTIAL, errorDataSets, Res.bundle.getString(ResIndex.ResolveFailed));

//!/*
//!    finally{
//!      try {
//!        if (resetPendingStatus)
//!          resetPendingStatus(dataSets, operationComplete);
//!      }
//!      finally{
//!        try {
//!          if (!operationComplete)
//!            transactionSupport.rollback();
//!        }
//!        finally{
//!          endResolution(dataSets);
//!        }
//!      }
//!    }
//!*/
  }


/*
  private DataSet[] startResolution(DataSet[] dataSets, boolean postEdits)
  {
    resolutionCalled = 0;
    StorageDataSet dataSet;

    for (int index = 0; index < dataSets.length; ++index) {
      dataSets[index].open();
      dataSet = dataSets[index].getStorageDataSet();
      for (int index2 = 0; index2 < index; ++index) {
        if (dataSets[index].getStorageDataSet() == dataSet) {
          dataSet = null;
          dataSets[index] = null;
          break;
        }
      }
      if (dataSet != null) {
        ProviderHelp.startResolution(dataSets[index].getStorageDataSet(), postEdits);
        resolutionCalled++;
      }
    }

    if (resolutionCalled < dataSets.length) {
      DataSet[] newDataSets = new DataSet[resolutionCalled];
      int destIndex = 0;
      for (int index = 0; index < dataSets.length; ++index) {
        if (dataSets[index] != null) {
          newDataSets[destIndex] = dataSets[index];
          ++destIndex;
        }
      }
      return newDataSets;
    }

    return dataSets;
  }
*/

  private DataSet[] startResolution(DataSet[] dataSets, boolean postEdits)
    /*-throws DataSetException-*/
  {
    resolutionCalled = 0;
    StorageDataSet dataSet;

    for (int index = 0; index < dataSets.length; ++index) {
      dataSets[index].open();
      dataSet = dataSets[index].getStorageDataSet();
      ProviderHelp.startResolution(dataSets[index].getStorageDataSet(), postEdits);
      resolutionCalled++;
    }

    return dataSets;
  }

  private void endResolution(DataSet[] dataSets) {
    StorageDataSet  dataSet;
    for (int index = 0; index < resolutionCalled; ++index) {
      if (dataSets[index] != null) {
        ProviderHelp.endResolution(dataSets[index].getStorageDataSet());
      }
    }
  }

  public void resetPendingStatus(DataSet[] dataSets, boolean markResolved)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < dataSets.length; ++index)
      dataSets[index].resetPendingStatus(markResolved);
  }

  private void processDataSetUpdates(DataSet[] resOrder)
    /*-throws DataSetException-*/
  {
    for (int index = resOrder.length-1; index >= 0; index--) {
      // Process the details first:
      DataSet     dataSet = resOrder[index];
      StorageDataSet sds = dataSet.getStorageDataSet();
      if (sds.getUpdatedRowCount() > 0) {
        DataSetView updateDataSet = new DataSetView();

        // Get the change DataSetViews from the DataSet.
        sds.getUpdatedRows(updateDataSet);

        ResolutionResolver resolver = (ResolutionResolver)defaultResolver.getResolver(dataSet);
        try {
          processUpdates(resolver, updateDataSet, dataSet);
        } finally {
          resolver.closeResources(sds);
          updateDataSet.close();
        }
      }
    }
  }

  private void processDataSetDeletes(DataSet[] resOrder)
    /*-throws DataSetException-*/
  {
    for (int index = resOrder.length-1; index >= 0; index--) {
      // Process the details first:
      DataSet     dataSet = resOrder[index];
      StorageDataSet sds = dataSet.getStorageDataSet();
      if (sds.getDeletedRowCount() > 0) {
        DataSetView deleteDataSet = new DataSetView();

        // Get the change DataSetViews from the DataSet.
        ResolutionResolver resolver = (ResolutionResolver)defaultResolver.getResolver(dataSet);
        sds.getDeletedRows(deleteDataSet);

        try {
          processDeletes(resolver, deleteDataSet, dataSet);
        } finally {
          resolver.closeResources(sds);
          deleteDataSet.close();
        }
      }
    }
  }

  private void processDataSetInserts(DataSet[] resOrder)
    /*-throws DataSetException-*/
  {
    for (int index=0; index < resOrder.length; index++) {
      // Process the details last:
      DataSet     dataSet = resOrder[index];
      StorageDataSet sds = dataSet.getStorageDataSet();
      if (saveAll || sds.getInsertedRowCount() > 0) {
        DataSetView insertDataSet = new DataSetView();

        sds.getInsertedRows(insertDataSet);

        ResolutionResolver resolver = (ResolutionResolver)defaultResolver.getResolver(dataSet);
        try {
          processInserts(resolver, saveAll?(DataSet)sds:insertDataSet, dataSet);
        } finally {
          resolver.close(sds);
          insertDataSet.close();
        }
      }
    }
  }

  private void processInserts(ResolutionResolver resolver, DataSet insertDataSet, DataSet dataSet)
    /*-throws DataSetException-*/
  {
    if (!insertDataSet.isEmpty()) {
      insertDataSet.first();
      int status;
      do {
        status  = insertDataSet.getStatus();
        if ((status& RowStatus.DELETED) == 0) {
          processInsertRow(resolver, insertDataSet, dataSet);
        }
      } while(insertDataSet.next());
    }
  }

  private void processDeletes(ResolutionResolver resolver, DataSet deleteDataSet, DataSet dataSet)
    /*-throws DataSetException-*/
  {
    if (!deleteDataSet.isEmpty()) {
      deleteDataSet.first();
      int status;
      do {
        status  = deleteDataSet.getStatus();
        if ((status & RowStatus.INSERTED) == 0) {
          processDeleteRow(resolver, deleteDataSet, dataSet);
        }

      } while (deleteDataSet.next());
    }
  }

  private void processUpdates(ResolutionResolver resolver, DataSet updateDataSet, DataSet dataSet)
    /*-throws DataSetException-*/
  {
    if (!updateDataSet.isEmpty()) {
      DataRow     oldDataRow        = new DataRow(dataSet);
      StorageDataSet  dataSetStore  = dataSet.getStorageDataSet();

      updateDataSet.first();
      int status;
      do {
        status  = updateDataSet.getStatus();
        if (   (status&RowStatus.DELETED) == 0) {

          dataSetStore.getOriginalRow(updateDataSet, oldDataRow);
          processUpdateRow(resolver, updateDataSet, oldDataRow, dataSet);
        }

      } while (updateDataSet.next());
    }
  }

  private void processInsertRow(ResolutionResolver resolver, DataSet insertDataSet, DataSet dataSet)
    /*-throws DataSetException-*/
  {
    // Event handling code is here instead of the resolver so that each implementation
    // of resolver does not have to implement this handling.
    //
    ResolverListener  resolverListener  = resolver.fetchResolverListener();
    if (resolverListener == null) {
      try{
//!        Diagnostic.println("\nInsert row:  "+insertDataSet);
        resolver.insertRow(insertDataSet);
        ProviderHelp.markPendingStatus(insertDataSet, true);
      }
      catch(Exception ex) {
        dataSet.goToRow(insertDataSet);
        logError(ResolutionException.INSERT_FAILED, dataSet, insertDataSet, ex, true, false);
      }
    }
    else {
      DataSetException abortEx = null;
      while(true) {
        boolean abort = false;

        try {
          resolverResponse.resolve();
          resolverListener.insertingRow(insertDataSet, resolverResponse);
          if (resolverResponse.isSkip())
            return;
          if (resolverResponse.isAbort())
            abort = true;
          else {
            resolver.insertRow(insertDataSet);
            ProviderHelp.markPendingStatus(insertDataSet, true);
          }
        }
        catch(DataSetException ex) {
          response.abort();
          resolverListener.insertError(dataSet, insertDataSet, ex, response);
          if (response.isRetry())
            continue;
          else if (response.isAbort()) {
            abortEx = ex;
            abort   = true;
          }
          else {
            logError(ResolutionException.INSERT_FAILED, dataSet, insertDataSet, ex, false, false);
            return;
          }
        }

        if (abort)
          logError(ResolutionException.INSERT_FAILED, dataSet, insertDataSet, abortEx, true, true);

        break;
      }
      resolverListener.insertedRow(insertDataSet);
    }
  }

  private void processDeleteRow(ResolutionResolver resolver, DataSet deleteDataSet, DataSet dataSet)
    /*-throws DataSetException-*/
  {
    // Event handling code is here instead of the resolver so that each implementation
    // of resolver does not have to implement this handling.
    //
    ResolverListener  resolverListener  = resolver.fetchResolverListener();
    if (resolverListener == null) {
      try{
        resolver.deleteRow(deleteDataSet);
        ProviderHelp.markPendingStatus(deleteDataSet, true);
      }
      catch(Exception ex) {
        dataSet.goToRow(deleteDataSet);
        logError(ResolutionException.DELETE_FAILED, dataSet, deleteDataSet, ex, true, false);
      }
    }
    else {
      DataSetException abortEx = null;
      while(true) {
        boolean abort = false;

        try {
          resolverResponse.resolve();
          resolverListener.deletingRow(deleteDataSet, resolverResponse);
          if (resolverResponse.isSkip())
            return;
          if (resolverResponse.isAbort())
            abort = true;
          else {
            resolver.deleteRow(deleteDataSet);
            ProviderHelp.markPendingStatus(deleteDataSet, true);
          }
        }
        catch(DataSetException ex) {
          response.abort();
          resolverListener.deleteError(dataSet, deleteDataSet, ex, response);
          if (response.isRetry())
            continue;
          else if (response.isAbort()) {
            abortEx = ex;
            abort   = true;
          }
          else {
            logError(ResolutionException.DELETE_FAILED, dataSet, deleteDataSet, ex, false, false);
            return;
          }
        }

        if (abort)
          logError(ResolutionException.DELETE_FAILED, dataSet, deleteDataSet, abortEx, true, true);

        break;
      }
      resolverListener.deletedRow(deleteDataSet);
    }
  }

  private void processUpdateRow(  ResolutionResolver      resolver,
                                  DataSet       updateDataSet,
                                  ReadWriteRow  oldRow,
                                  DataSet       dataSet
                               )
    /*-throws DataSetException-*/
  {
    // Event handling code is here instead of the resolver so that each implementation
    // of resolver does not have to implement this handling.
    //
    ResolverListener  resolverListener  = resolver.fetchResolverListener();
    if (resolverListener == null) {
      try{
        resolver.updateRow(updateDataSet, oldRow);
        ProviderHelp.markPendingStatus(updateDataSet, true);
      }
      catch(Exception ex) {
        dataSet.goToRow(updateDataSet);
        DiagnosticJLimo.println("dataSet.getRow:  "+dataSet.getLongRow());
        ResolutionException.updateFailed(dataSet, null, ex);
        logError(ResolutionException.UPDATE_FAILED, dataSet, updateDataSet, ex, true, false);
      }
    }
    else {
      ReadWriteRow updateRow = oldRow;
      DataSetException abortEx = null;

      while(true) {
        boolean abort = false;
        DataRow newData = null;

        try {
          resolverResponse.resolve();
          resolverListener.updatingRow(updateDataSet, updateRow, resolverResponse);
          if (resolverResponse.isSkip())
            return;
          if (resolverResponse.isAbort())
            abort = true;
          else {
            resolver.updateRow(updateDataSet, updateRow);
            ProviderHelp.markPendingStatus(updateDataSet, true);
          }
        }
        catch(DataSetException ex) {
          response.abort();
          if (newData == null) {
            //!JOAL Quick Hack:
            // We create a virtual original row here, which allows the user to change
            // the original row from which the update query is made.;
            newData = new DataRow(updateDataSet);
            oldRow.copyTo(newData);
            updateRow = newData;
          }
          resolverListener.updateError(dataSet, updateDataSet, oldRow, updateRow, ex, response);
          if (response.isRetry())
            continue;
          else if (response.isAbort()) {
            abort   = true;
            abortEx = ex;
          }
          else {
            logError(ResolutionException.UPDATE_FAILED, dataSet, updateDataSet, ex, false, false);
            return;
          }
        }

        if (abort)
          logError(ResolutionException.UPDATE_FAILED, dataSet, updateDataSet, abortEx, true, true);

        break;
      }
      resolverListener.updatedRow(updateDataSet, updateRow);
    }
  }

  // --------------------------------------------------------------------
  // findResolutionOrder:
  //   Keep resolution order the same as it was passed in, unless:
  //   If a detail dataSet appears before its master dataSet, then
  //   the order is rearranged such, that the masters appear before
  //   their detail dataSets.
  // --------------------------------------------------------------------
  //
  DataSet[] findResolutionOrder(DataSet[] dataSets) /*-throws DataSetException-*/ {
    DataSet[] order = new DataSet[dataSets.length];
    System.arraycopy(dataSets,0,order,0,dataSets.length);

    for (int index=0; index < dataSets.length; index++) {
      MasterLinkDescriptor link = order[index].getMasterLink();
      if (link == null) continue;
      DataSet masterDataSet = link.getMasterDataSet();
      DiagnosticJLimo.check(masterDataSet != null);
      int mIndex;
      for (mIndex=0; mIndex < order.length && order[mIndex] != masterDataSet; mIndex++);
      if (mIndex != order.length && mIndex > index) {  // Swap if neccessary
        DataSet temp  = order[index];
        order[index]  = order[mIndex];
        order[mIndex] = temp;
        index--;  // remember to check the master's master
      }
    }
    return order;
  }

  protected void setTransactionSupport(TransactionSupport transactionSupport) {
    this.transactionSupport = transactionSupport;
  }

  public TransactionSupport getTransactionSupport() {
    return transactionSupport;
  }

  protected void setDefaultResolver(DefaultResolver defaultResolver) {
    this.defaultResolver = defaultResolver;
  }

  // This property can be used to change the default order of actions for the resolution manager.
  // By default, we will resolve deletes, updates, then inserts. In some corner cases (specifically,
  // for master/detail DataSets where link fields are changed), it may be desirable to process
  // inserts before updates (ie. insert a new master row, change existing detail rows to point to
  // the new master row). Default behavior (as stated above) is to process updates before inserts.

  public boolean getInsertsBeforeUpdates() {
    return insertsFirst;
  }

  public void setInsertsBeforeUpdates(boolean insertsFirst) {
    this.insertsFirst = insertsFirst;
  }

  /**
   *  @since JB2.0
   *  This property controls whether edits should be posted before resolution starts.
   *  If this property is set to false, the changes of the current row is not posted
   *  and the changes are not resolved back to the data source.
   *  Default is true.
   */
  public void setPostEdits(boolean postEdits) {
    this.postEdits = postEdits;
  }
  public boolean getPostEdits() {
    return postEdits;
  }

  /**
   *  @since JB2.0
   *  This property controls whether the status of the datasets involved should be
   *  reset after the resolution is done.
   *  If this property is set to false, the status bits has to be reset by calling
   *  resetPendingStatus for an array of dataSets, or calling resetPendingStatus
   *  directly on the dataSet.
   *  Default is true.
   */
  public void setResetPendingStatus(boolean resetPendingStatus) {
    this.resetPendingStatus = resetPendingStatus;
  }

  public void setSaveAll(boolean saveAll) {
    this.saveAll = saveAll;
  }
  public boolean getResetPendingStatus() {
    return resetPendingStatus;
  }

  protected abstract void initError(int code, DataSet dataSet, DataSet view, ResolveError resolveError);


  final void logError(  int             code,
                        DataSet         dataSet,
                        DataSet         view,
                        Exception       ex,
                        boolean         failIfCantLog,
                        boolean         fail
                     )
    /*-throws DataSetException-*/
  {
    StorageDataSet errorDataSet = null;
    int maxErrors = dataSet.getStorageDataSet().getMaxResolveErrors();
    int index     = 0;

    if (maxErrors != 0) {
      for (index = 0; index < dataSets.length; ++index) {
        if (dataSets[index] == dataSet) {
          if (errorDataSets == null) {
            errorDataSets = new StorageDataSet[dataSets.length];
            errorCounts   = new int[dataSets.length];
          }
          ++errorCounts[index];
          if (errorDataSets[index] == null) {
            errorDataSet = new StorageDataSet();
            errorDataSet.setColumns(dataSet.getStorageDataSet().cloneColumns());
            errorDataSet.addColumn("RESOLVE_ERROR", Variant.OBJECT); //!NORES
            errorDataSet.setTableName(dataSet.getTableName());
            errorDataSet.open();
            errorDataSets[index] = errorDataSet;
          }
          errorDataSet = errorDataSets[index];
          break;
        }
      }
    }

    if (errorDataSet != null) {
      dataSet.goToInternalRow(view.getInternalRow());
      ResolveError error  = new ResolveError();
      error.row           = dataSet.getLongRow();
      Column column = dataSet.getColumn(dataSet.getColumnCount()-1);
      // If loaded by DataSetData, use the internalRow of the source
      // DataSet.
      //
      if (column.getColumnName().startsWith(DataBits.INTERNALROW))
        error.internalRow   = dataSet.getLong(column.getOrdinal());
      else
        error.internalRow   = dataSet.getInternalRow();

      error.response      = fail ? ResolverResponse.ABORT : ResolverResponse.IGNORE;
      if (ex != null)
      {
        error.message = ex.getMessage();
        error.ex = ex;
      }
      errorDataSet.insertRow(false);
      DataRow refetchRow = null;
      if (code == ResolutionException.UPDATE_FAILED) {
        refetchRow = new DataRow(dataSet);
        // Get the offending row.
        //
        dataSet.getStorageDataSet().getOriginalRow(dataSet, refetchRow);
      }
      if (code == ResolutionException.DELETE_FAILED) {
        refetchRow = new DataRow(dataSet);
        // Get the offending row.
        //
        dataSet.copyTo(refetchRow);
      }
      if (refetchRow != null) {
        // If it exists with modified columns, lets see if we can find it.
        //
        try {
          dataSet.refetchRow(view);

          Variant value = new Variant();
          int count = refetchRow.getColumnCount();
          Column[] columns = refetchRow.getColumns();
          for (int ordinal = 0; ordinal < count; ++ordinal) {
            if ( !columns[ ordinal ].isReadOnly() ) {
              refetchRow.getVariant(ordinal, value);
              errorDataSet.setVariant(ordinal, value);
            }
          }
        }
        catch(DataSetException ex2) {
        }
      }
      initError(code, dataSet, view, error);
      errorDataSet.setObject("RESOLVE_ERROR", error); //!NORES
      errorDataSet.post();
      if (errorCounts[index] > maxErrors && maxErrors > -1)
        throw new ResolutionException(ResolutionException.RESOLVE_FAILED, errorDataSets, Res.bundle.getString(ResIndex.ResolveFailed));
    }

    if (fail || (failIfCantLog && errorDataSet == null)) {
      switch(code) {
        case ResolutionException.INSERT_FAILED:
          ResolutionException.insertFailed(dataSet, null, ex);
          break;
        case ResolutionException.DELETE_FAILED:
          ResolutionException.deleteFailed(dataSet, null, ex);
          break;
        case ResolutionException.UPDATE_FAILED:
          ResolutionException.updateFailed(dataSet, null, ex);
          break;
      }
    }
  }

  private       DataSet[]             dataSets;
  private       StorageDataSet[]      errorDataSets;
  private       int[]                 errorCounts;
  private       boolean               insertsFirst;
  private       ResolverResponse      resolverResponse;
  private       ErrorResponse         response;
  private       TransactionSupport    transactionSupport;
  private       DefaultResolver       defaultResolver;
  private       int                   resolutionCalled;
  private       boolean               postEdits;
  private       boolean               resetPendingStatus;
  private       boolean               saveAll;
}
