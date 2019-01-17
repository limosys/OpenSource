//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MatrixData.java,v 7.4.2.3 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.TriStateProperty;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.cons.*;
import java.util.Locale;


public abstract class MatrixData
{

  public abstract void    updateStoreRow(long internalRow, RowVariant[] values, Column[] updateColumns) /*-throws DataSetException-*/;

  public abstract long    getRowCount() /*-throws DataSetException-*/;

  public abstract void    openData(StorageDataSet dataSet, boolean replaceColumns) /*-throws DataSetException-*/;

  public abstract void    addColumn(Column column) /*-throws DataSetException-*/;
  public abstract void    dropColumn(int ordinal) /*-throws DataSetException-*/;
  public abstract void    changeColumn(int ordinal, Column oldColumn, Column newColumn) /*-throws DataSetException-*/;
  public abstract void    moveColumn(int oldOrdinal, int newOrdinal) /*-throws DataSetException-*/;

  public abstract void prepareRestructure(StorageDataSet dataSet) /*-throws DataSetException-*/;

  public abstract void commitRestructure(StorageDataSet dataSet) /*-throws DataSetException-*/;

  public abstract MatrixData
  restructure(  StorageDataSet        dataSet,
                CalcFieldsListener    calcListener,
                CalcAggFieldsListener calcAggFieldsListener
             ) /*-throws DataSetException-*/;

  public abstract boolean getNeedsRestructure() /*-throws DataSetException-*/;

//!  public abstract void setNeedsRecalc(boolean recalc) /*-throws DataSetException-*/;
//!  public abstract boolean getNeedsRecalc() /*-throws DataSetException-*/;
  public abstract boolean needsRecalc(StorageDataSet dataSet) /*-throws DataSetException-*/;
  public abstract void notifyRecalc(StorageDataSet dataSet) /*-throws DataSetException-*/;

  public abstract boolean copyStreams();

  public abstract boolean validColumnType(Column column) /*-throws DataSetException-*/;


  public abstract void    getVariant(long internalRow, int ordinal, Variant value) /*-throws DataSetException-*/;

  public abstract void    getRowData(long internalRow, Variant[] values) /*-throws DataSetException-*/;

  public abstract void    getOriginalRow(long internalRow, Variant[] values) /*-throws DataSetException-*/;

  public abstract void    getOriginalVariant(long internalRow, int ordinal, Variant value) /*-throws DataSetException-*/;

  public abstract int     getStatus(long internalRow) /*-throws DataSetException-*/;
  public abstract void    setStatus(long internalRow, int status) /*-throws DataSetException-*/;

  public abstract boolean  canCalc() /*-throws DataSetException-*/;

  public static final void initCalcs(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    dataSet.initCalcs();
  }
  public static final Variant[] getCalcFieldsValues(StorageDataSet dataSet) {
    return dataSet.calcFieldsValues;
  }

  public static final AggManager getAggManager(StorageDataSet dataSet) {
    return dataSet.aggManager;
  }



  public abstract DirectIndex openIndex(DataSet dataSet, boolean create) /*-throws DataSetException-*/;



  public abstract void dropAllIndexes() /*-throws DataSetException-*/;

  public abstract void dropIndex(DataSet dataSet) /*-throws DataSetException-*/;

  public abstract boolean dropIndex( SortDescriptor    descriptor,
                            Locale            locale,
                            RowFilterListener rowFilter,
                            int               visibleMask,
                            int               invisibleMask
                       )
    /*-throws DataSetException-*/;


  public abstract void freeFetchIndex() /*-throws DataSetException-*/;
//!/*
//!  public abstract DataIndex findIndex(SortDescriptor    descriptor,
//!                                        Locale            locale,
//!                                        RowFilterListener rowFilterListener,
//!                                        int               visibleMask,
//!                                        int               invisibleMask
//!                                       ) /*-throws DataSetException-*/;
//!
//!*/
  public abstract long insertRow(ReadRow row, RowVariant[] values, int status) /*-throws DataSetException-*/;

  public abstract void deleteRow(long internalRow) /*-throws DataSetException-*/;

  public abstract void emptyRow(long internalRow) /*-throws DataSetException-*/;

  public abstract void updateRow(long internalRow, Variant[] originalValues, RowVariant[] values, Column[] updateColumns) /*-throws DataSetException-*/;


  public abstract long replaceLoadedRow(long internalRow, ReadWriteRow row, RowVariant[] values, int status) /*-throws DataSetException-*/;

  public abstract void resetPendingStatus(long internalRow, boolean resolved) /*-throws DataSetException-*/;

  public abstract void resetPendingStatus(boolean resolved) /*-throws DataSetException-*/;

  public abstract void clearInternalReadRow() /*-throws DataSetException-*/;

  public abstract InternalRow getInternalReadRow(StorageDataSet dataSet) /*-throws DataSetException-*/;

  public abstract void recalc(StorageDataSet storageDataSet, AggManager aggManager) /*-throws DataSetException-*/;

  public abstract void getInsertedRows(StorageDataSet dataSet, DataSetView insertedDataSet) /*-throws DataSetException-*/;

  public abstract void getDeletedRows(StorageDataSet dataSet, DataSetView deletedDataSet) /*-throws DataSetException-*/;

  public abstract void getUpdatedRows(StorageDataSet dataSet, DataSetView updatedDataSet) /*-throws DataSetException-*/;

  public abstract MatrixData   closeDataSet(  StorageDataSet  dataSet,
                                              int             matrixDataType,
                                              AggManager      aggManager,
                                              StorageDataSet  fetchDataSet,
                                              int             reason,
                                              boolean         closeData
                                           ) /*-throws DataSetException-*/;

  public abstract MatrixData   setColumns(StorageDataSet dataSet, Column[] columns) /*-throws DataSetException-*/;
//! public abstract void         deleteDataSet(StorageDataSet dataSet) /*-throws DataSetException-*/;


  // Overridden by ResolverData in DataStore so that it synchronizes
  // on its associated TableData.  This is important for blob optimizations
  // that reference both blob tables.
  //
  public abstract Object getDataMonitor();

  public abstract void cancelOperation() /*-throws DataSetException-*/;

  public abstract void setLoadCancel(StorageDataSet dataSet, LoadCancel loader) /*-throws DataSetException-*/;

  public abstract long getInsertedRowCount() /*-throws DataSetException-*/;

  public abstract long getDeletedRowCount() /*-throws DataSetException-*/;

  public abstract long getUpdatedRowCount() /*-throws DataSetException-*/;

  public abstract boolean indexExists(SortDescriptor descriptor, RowFilterListener listener) /*-throws DataSetException-*/;

  public abstract DirectIndex[] getIndices();

  public abstract boolean isEmpty() /*-throws DataSetException-*/;

  public abstract String getCalcFieldsName();
  public abstract String getCalcAggFieldsName();
  public abstract void setCalcNames(String calcFieldsName, String calcAggFieldsName);
  public abstract MatrixData getData();

  public abstract IndexData getIndexData();
  public abstract void addDataSet(DataSet listener) /*-throws DataSetException-*/;
  public abstract void removeDataSet(DataSet listener) /*-throws DataSetException-*/;
  public abstract void updateProperties(StorageDataSet listener) /*-throws DataSetException-*/;

  public static final void calcFields(StorageDataSet dataSet, ReadWriteRow row)
    /*-throws DataSetException-*/
  {
    dataSet.calcFields(row, true);
  }

  public static final void processDataChanged(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    dataSet.processDataChanged(0);
  }

//  public static final void closeData(StorageDataSet dataSet, int reason, boolean closeData)
//    /*-throws DataSetException-*/
//  {
//    dataSet.closeData(reason, closeData);
//  }

  public static final void closeStorage(StorageDataSet dataSet, int reason, boolean closeData)
    /*-throws DataSetException-*/
  {
    // StorageDataSet.closeStorage() does not deal with recursion that occurs when
    // AccessListeners notified of pending close (which causes recursion via DataSet.close())
    // So call DataSet.close which protects against recursion via the "open" state.
    //
    dataSet.close(true, reason, closeData);
  }
  /*
  public static boolean openStorage(StorageDataSet dataSet, DataSet view, AccessEvent event)
  {
    return dataSet.openStorage(view, event);
  }
  */

  //! JOAL: Used to match identifiers: StoreNames, IndexNames (caseSensitive);
  public static final boolean identifierEquals(String thisName, String name) {
    if (thisName == name)
      return true;
    if (name == null || thisName == null)
      return false;
    return name.equals(thisName);
  }

  public static final MatrixData getData(StorageDataSet dataSet) {
    return dataSet.getMatrixData();
  }

  public static final void setTableName(StorageDataSet dataSet, String schemaName, String tableName) {
    if (!MatrixData.propSet(dataSet, PropSet.TableName)) {
      dataSet._setSchemaName(schemaName);
      dataSet._setTableName(tableName);
    }
    else {
      if (!identifierEquals(tableName, dataSet.getTableName())) {
        dataSet.needsPropertyUpdate = true;
      }
    }
  }

  public static final void setResolveOrder(StorageDataSet dataSet, String[] resolveOrder) {
/*
          if (!MatrixData.propSet(dataSet, PropSet.Resolvable))
          if (!MatrixData.propSet(dataSet, PropSet.Locale))
*/
    if (!MatrixData.propSet(dataSet, PropSet.ResolveOrder))
      dataSet._setResolveOrder(resolveOrder);
    else {
      String[] current = dataSet.getResolveOrder();
      boolean equals = true;
      if ((resolveOrder == null) == (current == null)) {
        if (current != null) {
          if (resolveOrder.length == current.length) {
            for (int index = 0; index < current.length; ++index) {
              if (!resolveOrder[index].equals(current[index]))
                equals = false;
            }
          }
          else
            equals = false;
        }
      }
      else
        equals = false;
      if (!equals)
        dataSet.needsPropertyUpdate = true;
    }

  }


  public static final void setResolvable(StorageDataSet dataSet, int resolvable) {
    if (!MatrixData.propSet(dataSet, PropSet.Resolvable))
      dataSet._setResolvable(resolvable != TriStateProperty.FALSE);
    else if ((resolvable != TriStateProperty.FALSE) != dataSet.isResolvable())
      dataSet.needsPropertyUpdate = true;
  }


  public static final void storeClassNeedsUpdate(StorageDataSet dataSet, String name) {
    StoreClassFactory currentFactory = dataSet.storeClassFactory;
    String currentName = currentFactory == null ? null : currentFactory.getClass().getName();
    if (MatrixData.propSet(dataSet, PropSet.StoreClassFactory) && !identifierEquals(currentName, name)) {
      dataSet.needsPropertyUpdate = true;
    }
  }
  public static final void setStoreClassFactory(StorageDataSet dataSet, StoreClassFactory factory) {
    dataSet.storeClassFactory = factory;
  }


  public static final Locale getLocale(StorageDataSet dataSet)
  {
    return dataSet.locale;
  }

  public static final Locale getLocale(Column column)
  {
    return column.locale;
  }

  public static final void setLocale(StorageDataSet dataSet, Locale locale) {
    if (!MatrixData.propSet(dataSet, PropSet.Locale))
      dataSet._setLocale(locale);
    else {
      Locale current = dataSet.locale;
      boolean equals = true;
      if ((current == null) == (locale == null)) {
        if (current != null) {
          if (!current.equals(locale))
            equals = false;
        }
      }
      if (!equals)
        dataSet.needsPropertyUpdate = true;
    }
  }

//!  public static final void addChangeListener(StorageDataSet dataSet, DataSet listener)
//!     /*-throws DataSetException-*/
//!   {
//!     dataSet.addStorageDataChangeListener(listener);
//!   }

//!   public static final void removeChangeListener(StorageDataSet dataSet, DataSet listener) {
//!     dataSet.removeStorageDataChangeListener(listener);
//!   }

  public static final void addUniqueColumns(StorageDataSet dataSet, Column[] columns, Column[] orderColumns, boolean reconcile)
    /*-throws DataSetException-*/
  {
    dataSet.addUniqueColumns(columns, orderColumns, reconcile, false);
  }

/*
  public static final Column[] getColumns(StorageDataSet dataSet)
  {
    return dataSet.columnList.cols;
  }
*/


  public static final boolean getResolvable(StorageDataSet dataSet) {
    return dataSet.resolvable;
  }

  public static final int getResolvable(Column column) {
    return column.resolvable;
  }

  public static final StoreClassFactory getStoreClassFactory(StorageDataSet dataSet)
  {
    return dataSet.storeClassFactory;
  }


  public static final Class getJavaClass(Column column)
    /*-throws DataSetException-*/
  {
    String name = column.getJavaClass();
    if (name != null) {
      try {
        return Class.forName(name);
      }
      catch(ClassNotFoundException ex) {
        DataSetException.throwExceptionChain(ex);
      }
    }
    return null;
  }

  public static final boolean displayError(DataSet dataSet, Throwable ex)
  {
    return dataSet.displayError(ex);
  }

  public static final void setNeedsRecalc(StorageDataSet dataSet, boolean recalc)
    /*-throws DataSetException-*/
  {
    dataSet.setNeedsRecalc(recalc);
  }

  public String getStoreClassName() {
    return null;
  }

  public static final void updateRow(StorageDataSet dataSet, long internalRow, DataRow row, Column[] updateColumns)
    /*-throws DataSetException-*/
  {
    dataSet.updateRow(internalRow, row, updateColumns);
  }

  public static final void deleteRow(StorageDataSet dataSet, long internalRow)
    /*-throws DataSetException-*/
  {
    dataSet.deleteRow(internalRow, false, false);
  }


  public static final boolean propSet(StorageDataSet dataSet, int prop)
    /*-throws DataSetException-*/
  {
    return (dataSet.propSet & prop) != 0;
  }

  public static final void setStorageDataSet(DataSetView view, StorageDataSet sds)
    /*-throws DataSetException-*/
  {
    view._setStorageDataSet(sds);
  }

  public int[] getRequiredOrdinals() { return null; }

  public static final void setPrimaryKey(Column column, boolean primary) {
    column.setPrimaryKey(primary);
  }

  public static final ForeignKeyDescriptor[] getForeignKeyReferences(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    return dataSet.foreignKeyReferenceDescs;
  }

//  public static final boolean hasRI(StorageDataSet table) {
//    return table.foreignKeyReferenceDescs != null || table.foreignKeyDescs != null;
//  }

  public static final void setForeignKeys(StorageDataSet dataSet, ForeignKeyDescriptor[] referenced, ForeignKeyDescriptor[] referencing)
    /*-throws DataSetException-*/
  {
    dataSet.checkUniqueReference(referenced);
    dataSet.foreignKeyDescs = referenced;
    dataSet.foreignKeyReferenceDescs = referencing;
  }

  public static final void setConstraintName(StorageDataSet dataSet, SortDescriptor sort) {
    dataSet.setConstraintName(sort);
  }

  public static final void dropConstraint(StorageDataSet dataSet, String name) {
    dataSet.dropConstraint(name);
  }

  public static final boolean is(Column column, int predicate) {
    return column.is(predicate);
  }

  public static final void renameStoreName(StorageDataSet dataSet, String oldName, String newName) {
    dataSet.updateForeignKeyStoreName(oldName, newName);
  }

  public static final String[] createReferencedColumns(StorageDataSet dataSet, ForeignKeyDescriptor desc) {
    return dataSet.createReferencedColumns(desc);
  }

  public static void forceAutoInc(Column column) {
    column.setPredicate(ColumnConst.FORCE_AUTO_INC, true);
  }
  public static boolean isForceAutoInc(Column column) {
    return column.is(ColumnConst.FORCE_AUTO_INC);

  }

  public static boolean hasEditListener(StorageDataSet dataSet) {
    return dataSet.editListeners != null;
  }

  public static String getAutoIncConstraint(StorageDataSet dataSet) {
    return dataSet.autoIncConstraint;
  }

  public static void setAutoIncConstraint(StorageDataSet dataSet, String name, boolean create) {
    dataSet.autoIncConstraint = name;
    if (create)
      dataSet.updateProperties();
  }

  public static void setClustered(Column column, boolean set) {
    column.setClustered(set);
  }


/*
  public static final void removeReferences(StorageDataSet dataSet) {
    dataSet.removeReferences();
  }

  public static final void addReferenceForeignKey(StorageDataSet dataSet, ForeignKeyDescriptor desc) {
    StorageDataSet table = desc.openReferenceTableData(dataSet, dataSet.getStore());
    table.addForeignKey(desc.invert(dataSet));
  }
*/

//!  // Set if initialized from StorageDataSet.initExisting
//!  //
//!  boolean       initExisting;
}



