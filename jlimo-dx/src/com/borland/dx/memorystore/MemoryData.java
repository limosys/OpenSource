//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/MemoryData.java,v 7.3 2003/09/16 21:26:24 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import java.math.*;
import java.util.Locale;

public class MemoryData extends com.borland.dx.dataset.IndexData {


  MemoryData(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    super();
//!    System.err.println("mds.new()");
    statusColumn    = new IntColumn(null);

    dataColumns     = new DataColumn[0];
    // Reserve row 0 for locate operations.
    //
    rowCount      = 1;
    MatrixData.setNeedsRecalc(dataSet, true);
    this.dataSet  = dataSet;
  }

  private final void checkType(Column column)
    /*-throws DataSetException-*/
  {
    if (!validColumnType(column))
      DataSetException.invalidColumnType(column);
  }

  public final void addColumn(Column column)
    /*-throws DataSetException-*/
  {
    checkType(column);

    MatrixData.setNeedsRecalc(dataSet, true);

    int           count       = (dataColumns == null) ? 1 : dataColumns.length + 1;

//!    System.err.println("mds.addColumn(" + column + ")");

    DataColumn[]  newColumns = new DataColumn[count];

    if (count > 1)
      System.arraycopy(dataColumns, 0, newColumns, 0, count-1);

//!    Diagnostic.println("column:  "+column.ordinal+" "+count);
    DiagnosticJLimo.check(column.getOrdinal() == -1 || column.getOrdinal() == (count-1));

    newColumns[count - 1]  = createColumnStorage(column, allocateNullState());

//!    if (rowCount > 1) {
    if (count > 1) {
      newColumns[count - 1].growTo(newColumns[0].vectorLength, rowCount);
    }
    else {
//!      Diagnostic.println("added column:  "+this);
//!      Diagnostic.printStackTrace();
      rowCount              = 1;
      statusColumn.lastRow  = 1;
    }

    dataColumns = newColumns;


    dropAllIndexes();
  }

  public void changeColumn(int ordinal, Column oldColumn, Column newColumn)
    /*-throws DataSetException-*/
  {
    if (oldColumn.getDataType() != newColumn.getDataType()) {
      checkType(newColumn);
      dataColumns[ordinal]  = createColumnStorage(newColumn, allocateNullState());

//!      newColumn.ordinal = dataColumns.length;
//!      addColumn(newColumn);
//!      dataColumns[oldColumn.ordinal]  = dataColumns[dataColumns.length-1];
//!      newColumn.ordinal = oldColumn.ordinal;
//!      dropColumn(dataColumns.length-1);

      dropAllIndexes();
    }
  }

  public void moveColumn(int oldOrdinal, int newOrdinal)
    /*-throws DataSetException-*/
  {
    // Fixes cliffhanger app.  StorageDataSet.calcFieldsRow gets out of synch
    // with StorageDataSet if columns moved, deleted, or added.
    //
    MatrixData.setNeedsRecalc(dataSet, true);

    DataColumn  column  = dataColumns[oldOrdinal];

    int ordinal = oldOrdinal;
    if (newOrdinal < oldOrdinal) {
      for (; ordinal > newOrdinal; --ordinal)
        dataColumns[ordinal]  = dataColumns[ordinal-1];
    }
    else {
      for (; newOrdinal > ordinal; ++ordinal)
        dataColumns[ordinal]  = dataColumns[ordinal+1];
    }
    dataColumns[ordinal]  = column;

    dropAllIndexes();
  }

  public void openData(StorageDataSet dataSet, boolean replaceColumns)
    /*-throws DataSetException-*/
  {
    updateProperties(dataSet);
  }

  public void updateProperties(StorageDataSet dataSet) {
    resolvable = dataSet.isResolvable();
    if (insertIndex == null && resolvable) {
      insertIndex = openIndex(dataSet, null, null, RowStatus.INSERTED, 0, true);
      deleteIndex = openIndex(dataSet, null, null, RowStatus.DELETED, 0, true);
      updateIndex = openIndex(dataSet, null, null, RowStatus.UPDATED, 0, true);
    }
  }


  public final boolean validColumnType(Column column) {
    switch (column.getDataType()) {
      case Variant.DATE:
      case Variant.TIME:
      case Variant.TIMESTAMP:
      case Variant.STRING:
      case Variant.BIGDECIMAL:
      case Variant.BYTE:
      case Variant.SHORT:
      case Variant.INT:
      case Variant.LONG:
      case Variant.BOOLEAN:
      case Variant.FLOAT:
      case Variant.DOUBLE:
      case Variant.INPUTSTREAM:
      case Variant.OBJECT:
        return true;
      default:
        return false;
      }
  }


  private final DataColumn createColumnStorage(Column column, NullState nullState) {
//!    System.err.println("mds.createColumnStorage()");
    switch (column.getCalcType()) {
      case CalcType.AGGREGATE:
      case CalcType.LOOKUP:
        return new CalcPlaceHolderColumn(nullState);
    }

    switch(column.getDataType()) {
      case Variant.STRING:
        Locale  locale = column.getLocale();//vs
        if(locale == null || locale.getLanguage().equals("en"))  //NORES
          return new StringColumn(nullState);
        else
          return new LocaleStringColumn(nullState, locale);
      case Variant.INPUTSTREAM: return new BinaryStreamColumn(nullState);
      case Variant.BYTE:        return new ByteColumn(nullState);
      case Variant.SHORT:       return new ShortColumn(nullState);
      case Variant.INT:         return new IntColumn(nullState);

      case Variant.BOOLEAN:     return new BooleanColumn(nullState);

      case Variant.FLOAT:       return new FloatColumn(nullState);
      case Variant.DOUBLE:      return new DoubleColumn(nullState);

      case Variant.LONG:        return new LongColumn(nullState);

      case Variant.BIGDECIMAL:  return new BigDecimalColumn(nullState);
      case Variant.TIME:        return new TimeColumn(nullState);
      case Variant.TIMESTAMP:   return new TimestampColumn(nullState);
      case Variant.DATE:        return new DateColumn(nullState);
      case Variant.OBJECT:      return new ObjectColumn(nullState);
      default: break;         // to make compiler happy
    }
    DiagnosticJLimo.fail();
    return null;
  }

  public void  dropColumn(int ordinal)
    /*-throws DataSetException-*/
  {
    DataColumn[] newList  = new DataColumn[dataColumns.length-1];

    MatrixData.setNeedsRecalc(dataSet, true);

    System.arraycopy(dataColumns, 0, newList, 0, ordinal);
    if ((ordinal+1) < dataColumns.length)
      System.arraycopy(dataColumns, ordinal+1, newList, ordinal, dataColumns.length-(ordinal+1));

    // Do this last in case there are exception.
    //
    dataColumns = newList;
  }

  NullState allocateNullState() {
    if (nullState == null || nullState.slot >= 6) {
      DiagnosticJLimo.check(nullState == null || nullState.slot == 6);
      return (nullState = new NullState());
    }
    nullState.slot  +=  2;
    DiagnosticJLimo.check(nullState.slot <= 6 && nullState.slot > 0);
    return nullState;
  }

  public final int getStatus(long internalRow) {
    return statusColumn.getInt((int)internalRow);
  }

  public final void setStatus(long internalRow, int status) {
    statusColumn.setInt((int)internalRow, status);
  }

  public final long getRowCount() { return rowCount; }

  public boolean isEmpty()
    /*-throws DataSetException-*/
  {
    return getRowCount() <= 1;
  }

  public final long insertRow(ReadRow row, RowVariant[] values, int status)
    /*-throws DataSetException-*/
  {

    if (hasUnique)
      uniqueCheck(0, values, false);
      //!    System.err.println("addStoreRow: values = " + values + "[" + values.length + "]");
//!    System.err.println("dataColumns = " + dataColumns + "[" + dataColumns.length + "]");
    int internalRow = appendRow();
    DiagnosticJLimo.check(status!= 0);
    statusColumn.setInt(internalRow, status);

    for (int ordinal = 0; ordinal < values.length; ++ordinal) {
//!      System.err.println("addStoreRow: ordinal = " + ordinal);
//!      System.err.println(" value is " + values[ordinal]);
      dataColumns[ordinal].setVariant(internalRow, values[ordinal]);
    }

    // Add quick status check for high speed loading.
    //
    if (insertIndex != null && (status&RowStatus.INSERTED) != 0 && resolvable)
      insertIndex.addStore(internalRow);

    indexAdd(internalRow);

    return internalRow;
  }

  public final void deleteStoreRow(long internalRow)
    /*-throws DataSetException-*/
  {
//!/*
//!    if ((status & (RowStatus.INSERTED|RowStatus.UPDATED)) != 0) {
//!      if ((status & RowStatus.INSERTED) != 0) {
//!        if (insertIndex != null)
//!          insertIndex.deleteStore(internalRow);
//!      }
//!      else if ((status & RowStatus.UPDATED) != 0) {
//!        if (updateIndex != null)
//!          updateIndex.deleteStore(internalRow);
//!      }
//!    }
//!    statusColumn.setInt((int)internalRow, status | RowStatus.DELETED);
//!*/
    int status = statusColumn.getInt((int)internalRow);
    statusColumn.setInt((int)internalRow, status | RowStatus.DELETED);
  }

  public final void emptyStoreRow(long internalRow)
    /*-throws DataSetException-*/
  {
    //!BUG:12381
    // Must do before emptyStoreRow, because values are wiped out.
    // TableData handles this differently.  indexDelete must come after.
    //
    indexDelete(internalRow);

    int status = statusColumn.getInt((int)internalRow);

    if ((status & (RowStatus.INSERTED|RowStatus.UPDATED)) != 0) {
      if ((status & RowStatus.INSERTED) != 0) {
        if (insertIndex != null)
          insertIndex.deleteStore(internalRow);
      }
      else if ((status & RowStatus.UPDATED) != 0) {
        if (updateIndex != null) {
          updateIndex.deleteStore(internalRow);
          setNullValues(originalColumn.getInt((int)internalRow));
        }
      }
    }

    if (deleteIndex != null)
      deleteIndex.deleteStore(internalRow);
    statusColumn.setInt((int)internalRow, 0);
    // Array entries still stay allocated, but space for values can be freed
    // up.
    //
    setNullValues((int)internalRow);
//!    if (emptyCount == 0)
//!      lastEmpty = (int) internalRow;
//!    ++emptyCount;
  }

  private final void setNullValues(int internalRow) {
    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal) {
      dataColumns[ordinal].setVariant(internalRow, RowVariant.getNullVariant());
    }
  }

  private void uniqueCheck(long internalRow, RowVariant[] values, boolean updating)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.check(hasUnique);
    for (int index = 0; index < indexesLength; ++index)
      indexes[index].uniqueCheck(internalRow, values, updating);
  }

  public final void updateStoreRow(long internalRow, RowVariant[] values, Column[] updateColumns)
    /*-throws DataSetException-*/
  {

    int status  = getStatus((int)internalRow);
    if (status != 0) {
      if (hasUnique)
        uniqueCheck(internalRow, values, true);
      status  = saveRow(status);

      indexPrepareUpdate((int)internalRow);

      if (saveOriginal) {

        DiagnosticJLimo.check(resolvable);  // saveOriginal should be false otherwise.

        saveStoreRow((int)internalRow, status);

        if ((status & RowStatus.UPDATED) == 0) {
          status  |= RowStatus.UPDATED;
          setStatus(internalRow, status);
          updateIndex.addStore(internalRow);
        }
        else
          setStatus(internalRow, status);

      }
      if (updateColumns != null) {
        int targetOrdinal;
        for (int ordinal = 0; ordinal < updateColumns.length; ++ordinal) {
          targetOrdinal = updateColumns[ordinal].getOrdinal();
          dataColumns[targetOrdinal].setVariant((int)internalRow, values[targetOrdinal]);
        }
      }
      else {
        for (int ordinal = 0; ordinal < values.length; ++ordinal)
          dataColumns[ordinal].setVariant((int)internalRow, values[ordinal]);
      }

      indexUpdate(internalRow);
    }
  }

  public void restoreStoreRow(long internalRow)
    /*-throws DataSetException-*/
  {
    int savedRow = originalColumn.getInt((int)internalRow);
    indexPrepareUpdate((int)internalRow);
    if (savedRow != 0) {
      copyRow(savedRow, (int)internalRow);
      setNullValues(savedRow);
    }
    indexUpdate(internalRow);
    if (updateIndex != null)
      updateIndex.deleteStore(internalRow);
    setStatus(internalRow, RowStatus.LOADED);
  }

  public void replaceStoreRow(long internalRow, RowVariant[] values, int status)
    /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < values.length; ++ordinal)
      dataColumns[ordinal].setVariant((int)internalRow, values[ordinal]);
    setStatus(internalRow, status);
  }

  private final void indexPrepareUpdate(int internalRow)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < indexesLength ; ++index)
      indexes[index].prepareUpdate(internalRow);
  }

//!  public final void setNeedsRecalc(boolean recalc) {
//!    needsRecalc = recalc;
//!  }
//!  public  boolean getNeedsRecalc() {
//!    return needsRecalc;
//!  }

  public final boolean copyStreams() {
    return true;
  }

  public  boolean getNeedsRestructure() {
    return false;
  }

  public MatrixData restructure( StorageDataSet        dataSet,
                           CalcFieldsListener    calcListener,
                           CalcAggFieldsListener calcAggFieldsListener
                        )
    /*-throws DataSetException-*/
  {
    return this;
  }

  public final void getVariant(long internalRow, int ordinal, Variant value) {
     dataColumns[ordinal].getVariant((int)internalRow, value);
   }

  public final void getRowData(long internalRow, Variant[] values)
    /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal)
        dataColumns[ordinal].getVariant((int)internalRow, values[ordinal]);
  }



  // Keep private, doesn't do everything needed for adding rows!
  //
  private final int appendRow() {
    int pos;

//! Diagnostic..println("dataColumns:  "+dataColumns.length);
    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal) {
//! Diagnostic..println("ordinal "+ordinal+" rowCount "+rowCount+" ordinalCount "+dataColumns[ordinal].lastRow);
      DiagnosticJLimo.check(rowCount == dataColumns[ordinal].lastRow);
      dataColumns[ordinal].append();
    }
//!    Diagnostic.println(this+" rowCount:  "+rowCount+" "+statusColumn.lastRow);
    DiagnosticJLimo.check(rowCount == statusColumn.lastRow);
    statusColumn.append();
    return rowCount++;
  }

  private final void copyRow(int sourceRow, int destRow) {

    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal)
      dataColumns[ordinal].copy(sourceRow, destRow);

  }

  private final void copyValues(int destRow, Variant[] values) {

    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal)
      dataColumns[ordinal].setVariant(destRow, values[ordinal]);

  }

  private final void saveStoreRow(int internalRow, int status)
    /*-throws DataSetException-*/
  {
    int savedRow  = 0;

    if (originalColumn == null)
      originalColumn  = new IntColumn(null);
    growTo(originalColumn);

    savedRow  = originalColumn.getInt(internalRow);

    if (savedRow == 0)
      savedRow  = appendRow();

    copyRow(internalRow, savedRow);
    statusColumn.setInt(savedRow, RowStatus.ORIGINAL);
//! Diagnostic..println(Integer.toString(statusColumn.getInt(savedRow), 16)+" setting change "+internalRow);
    originalColumn.setInt(internalRow, savedRow);
  }

  final void growTo(IntColumn intColumn) {
    intColumn.growTo(dataColumns[0].vectorLength, rowCount);
  }

  public final DirectIndex createIndex( StorageDataSet    dataSet,
                                        SortDescriptor    descriptor,
                                        RowFilterListener rowFilterListener,
                                        DataRow           filterRowDummy,
                                        RowVariant[]      filterValues,
                                        int               visibleMask,
                                        int               invisibleMask
                                     )
    /*-throws DataSetException-*/
  {
    DataIndex      index;

    InternalRow filterRow = null;
    if (rowFilterListener != null)
      filterRow  = getInternalReadRow(dataSet);

    index  = null;
    boolean       sortAsInserted  = false;
    IntColumn     insertColumn    = null;
    if (descriptor != null) {
                    sortAsInserted  = descriptor.isSortAsInserted();
      int           dataKeyCount    = descriptor.keyCount();
      int           keyCount        = dataKeyCount+(sortAsInserted?1:0);
      DataColumn[]  keyColumns      = new DataColumn[keyCount];
      Column[]      columns         = new Column[keyCount];
      Column        column;

      int keyIndex;
      for (keyIndex = 0; keyIndex < dataKeyCount; ++keyIndex) {
        column  = dataSet.getColumn(descriptor.getKeys()[keyIndex]);
        if (!column.isSortable())
          DataSetException.notSortable();
        columns[keyIndex]     = column;
        keyColumns[keyIndex]  = dataColumns[column.getOrdinal()];
      }

      long rowCount  = getRowCount();
      if (sortAsInserted) {
        insertColumn           = new IntColumn(null);
        long minLength = keyColumns.length > 1 ? keyColumns[0].vectorLength : rowCount;
        if (minLength > insertColumn.vectorLength)
          insertColumn.grow((int)minLength);
        keyColumns[keyIndex] = insertColumn;
        ++keyIndex;
      }

      if (keyCount > 0 && keyIndex >= keyCount) {
        index  = new SortedMemoryIndex( descriptor, rowFilterListener, filterRow,
                                      this, dataColumns, visibleMask, invisibleMask, statusColumn,
                                      keyColumns, columns
                                    );
        if (descriptor.isUnique())
          hasUnique = true;
      }
    }

    if (index == null) {
      index  = new MemoryIndex( descriptor, rowFilterListener, filterRow,
                              this, visibleMask, invisibleMask, statusColumn
                            );
    }

    for (int internalRow = 0; internalRow < rowCount; ++internalRow)
      index.loadStore(internalRow);

    if (sortAsInserted) {
      long count = index.lastRow() + 1 +1;
      for (int i = 1; i < count; ++i) {
        insertColumn.setInt(i, i);
      }
    }

    index.sort();

    return index;
  }

  public final void getOriginalRow(long internalRow, Variant[] values)
    /*-throws DataSetException-*/
  {
    getRowData(originalColumn.getInt((int)internalRow), values);
  }

  public final void    getOriginalVariant(long internalRow, int ordinal, Variant value)
    /*-throws DataSetException-*/
  {
    getVariant(originalColumn.getInt((int)internalRow), ordinal, value);
  }

//!/*
//!  public void deleteDataSet(StorageDataSet dataSet)
//!    /*-throws DataSetException-*/
//!  {
//!    // No persistance, so nothing to do. (when pointer reset, memory
//!    // will be garbage collected.
//!  }
//!*/

  public MatrixData closeDataSet( StorageDataSet    dataSet,
                                  int               matrixDataType,
                                  AggManager        aggManager,
                                  StorageDataSet    fetchDataSet,
                                  int               reason,
                                  boolean           closeData
                                )
    /*-throws DataSetException-*/
  {
    return this;
  }

  public MatrixData setColumns(StorageDataSet dataSet, Column[] columns)
    /*-throws DataSetException-*/
  {
    return null;//emptyDataSet(dataSet);
  }

  final void deleteDuplicates()
    /*-throws DataSetException-*/
  {
    if (duplicates != null) {
      duplicates.close();
      duplicates  = null;
      dupValue    = null;
    }
  }

  final void copyDuplicate(int dupInternalRow)
    /*-throws DataSetException-*/
  {
    if (duplicates == null) {
      duplicates = new TableDataSet();
      duplicates.setColumns(dataSet.cloneColumns());
      duplicates.setResolvable(false);
      duplicates.open();
      dupValue  = new Variant();
    }
    duplicates.insertRow(false);
    for (int ordinal = 0; ordinal < dataColumns.length; ++ordinal) {
      getVariant(dupInternalRow, ordinal, dupValue);
      duplicates.setVariant(ordinal, dupValue);
    }
    duplicates.post();
    emptyStoreRow(dupInternalRow);
  }


  public boolean isMemoryData() {
    return true;
  }


  private IntColumn         statusColumn;
  private IntColumn         originalColumn;
          DataColumn[]      dataColumns;
  private int               rowCount;
  private boolean           hasUnique;

  private NullState         nullState;
  private boolean           needsRecalc;
//!  private int               emptyCount;
//!  private int               lastEmpty;

          TableDataSet      duplicates;
          Variant           dupValue;
          StorageDataSet    dataSet;
}
