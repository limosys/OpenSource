//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/AggManager.java,v 7.0 2002/08/08 18:39:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;

public class AggManager {

  public static AggManager init(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    Column[]      cols       = dataSet.columnList.cols;
    int           count     = dataSet.columnList.count;
    Aggregator[]  aggregatorMap = null;
    Lookup[]      lookupMap     = null;
    boolean[]     calcs         = null;
    Column        column;

    int aggCount    = 0;

    for (int ordinal = 0; ordinal < count; ++ordinal) {
      column  = cols[ordinal];
      column.initLookup(dataSet);
      if (column.lookup != null && column.lookup.calcField) {
        if (calcs == null)
          calcs = new boolean[count];

        calcs[ordinal]  = true;

        if (lookupMap == null)
          lookupMap = new Lookup[count];
        lookupMap[ordinal] = column.lookup;
      }

      if (isAggregator(dataSet, column, ordinal)) {
        if (calcs == null)
          calcs = new boolean[count];
        calcs[ordinal]  = true;
        if (aggregatorMap == null)
          aggregatorMap = new Aggregator[count];
        if (addAggregator(aggregatorMap, dataSet, cols[ordinal], ordinal))
          ++aggCount;
      }
    }

    if (aggregatorMap != null || lookupMap != null)
      return new AggManager(dataSet, calcs, aggregatorMap, aggCount, lookupMap);
    return null;
  }

  private static boolean isAggregator(  StorageDataSet  dataSet,
                                        Column          column,
                                        int             ordinal
                                     )
    /*-throws DataSetException-*/
  {
    if (column.getCalcType() == CalcType.AGGREGATE) {
      AggDescriptor aggDescriptor;
      aggDescriptor = dataSet.getColumn(ordinal).getAgg();
      if (aggDescriptor == null)
        return false;

      String[]      groupColumns  = aggDescriptor.getGroupColumnNames();
      if (groupColumns != null) {
        for(int index = 0; index < groupColumns.length; ++index) {
          if (dataSet.hasColumn(groupColumns[index]) == null)
            DataSetException.invalidAggDescriptor();}

      }
      return true;
    }
    return false;
  }

  private static boolean addAggregator(  Aggregator[]   aggregatorMap,
                                         StorageDataSet dataSet,
                                         Column         column,
                                         int            ordinal
                                      )
    /*-throws DataSetException-*/
  {
    Aggregator aggregator;

    for (int index = 0; index < aggregatorMap.length; ++index) {
      aggregator  = aggregatorMap[index];
      if (aggregator != null) {
        if (aggregator.groupEquals(column)) {
          aggregator.addOperator(dataSet, ordinal);
          aggregatorMap[ordinal]  = aggregator;
          return false;
        }
      }
    }
    aggregatorMap[ordinal]  = new Aggregator(dataSet, ordinal);
    aggregatorMap[ordinal].addOperator(dataSet, ordinal);
    return true;
  }

  private boolean findAggregator(Aggregator aggregator) {
    for (int index = 0; index < aggregators.length; ++index)
      if (aggregators[index] == aggregator)
        return true;
    return false;
  }

  AggManager( StorageDataSet    dataSet,
              boolean[]         calcs,
              Aggregator[]      aggregatorMap,
              int               aggCount,
              Lookup[]          lookupMap
            )
    /*-throws DataSetException-*/
  {
    this.calcs            = calcs;
    DiagnosticJLimo.check(this.calcs != null);
    aggregators           = new Aggregator[aggCount];
    this.aggregatorMap    = aggregatorMap;
    this.lookupMap        = lookupMap;
    this.dataSet          = dataSet;
    this.internalReadRow  = dataSet.getInternalReadRow();
    if (aggregatorMap != null) {
      if (dataSet.calcAggFieldsListener != null)
        this.customRow  = new DataRow(dataSet, null, false);

      int aggIndex  = -1;
      for (int index = 0; index < aggregatorMap.length; ++index) {
        if (aggregatorMap[index] != null) {
          if (!findAggregator(aggregatorMap[index])) {
            aggregators[++aggIndex] = aggregatorMap[index];
            aggregators[aggIndex].open();
          }
        }
      }
    }
  }

  public void reOpen()
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < aggregators.length; ++index)
      aggregators[index].reOpen();
  }
  public void close()
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < aggregators.length; ++index)
      aggregators[index].close();
  }

  public void add(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    //! Diagnostic.println("================= aggManager.add start:  "+aggregators.length);
    for (int index = 0; index < aggregators.length; ++index)
      aggregators[index].add(row, internalRow);
    //! Diagnostic.println("================= aggManager.add end:");
    if (dataSet.calcAggFieldsListener != null) {
      initCustomAgg(row);
      dataSet.calcAggFieldsListener.calcAggAdd(customRow, dataSet.calcFieldsRow);
      updateCustomAggs(row);
    }

  }

  public void delete(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < aggregators.length; ++index)
      aggregators[index].delete(row, internalRow);

    if (dataSet.calcAggFieldsListener != null) {
      initCustomAgg(row);
      dataSet.calcAggFieldsListener.calcAggDelete(customRow, dataSet.calcFieldsRow);
      updateCustomAggs(row);
    }
  }

  public void update(ReadRow oldRow, ReadRow newRow, long internalRow)
    /*-throws DataSetException-*/
  {
    if (loading)
      add(newRow, internalRow);
    else {
      for (int index = 0; index < aggregators.length; ++index)
        aggregators[index].update(oldRow, newRow, internalRow);

      if (dataSet.calcAggFieldsListener != null) {
        initCustomAgg(oldRow);
        dataSet.calcAggFieldsListener.calcAggAdd(customRow, dataSet.calcFieldsRow);
        updateCustomAggs(oldRow);
        initCustomAgg(newRow);
        dataSet.calcAggFieldsListener.calcAggDelete(customRow, dataSet.calcFieldsRow);
        updateCustomAggs(newRow);
      }
    }
  }

  private final void initCustomAgg(ReadRow row)
    /*-throws DataSetException-*/
  {
    dataSet.calcFieldsRow._clearValues();
    row.copyTo(customRow);
    getRowData(customRow, customRow);
//!   Diagnostic.println("  customRow:  "+customRow);
  }


  private final void updateCustomAggs(ReadRow row)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < aggregators.length; ++index)
      aggregators[index].updateCustomAggs(row, dataSet.calcFieldsRow);
  }

  public void getVariant(ReadRow row, int calcOrdinal, Variant value)
    /*-throws DataSetException-*/
  {
    if (lookupMap != null && lookupMap[calcOrdinal] != null)
      lookupMap[calcOrdinal].lookup(row, value);
    else
      aggregatorMap[calcOrdinal].getVariant(row, calcOrdinal, value);
  }

  public void getVariant(long internalRow, int calcOrdinal, Variant value)
    /*-throws DataSetException-*/
  {
    internalReadRow.setInternalRow(internalRow);
    if (lookupMap != null && lookupMap[calcOrdinal] != null)
      lookupMap[calcOrdinal].lookup(internalReadRow, value);
    else
      aggregatorMap[calcOrdinal].getVariant(internalReadRow, calcOrdinal, value);
  }

  public void getRowData(long internalRow, ReadWriteRow destRow)
    /*-throws DataSetException-*/
  {
    internalReadRow.setInternalRow(internalRow);
    if (aggregators != null) {
      for (int index = 0; index < aggregators.length; ++index)
        aggregators[index].getRowData(internalReadRow, destRow);
    }
    if (lookupMap != null)
      getLookupData(internalReadRow, destRow);
  }

  public void getRowData(ReadRow row, ReadWriteRow destRow)
    /*-throws DataSetException-*/
  {
    if (aggregators != null) {
      for (int index = 0; index < aggregators.length; ++index)
        aggregators[index].getRowData(row, destRow);
    }
    if (lookupMap != null)
      getLookupData(row, destRow);
  }

  final void getLookupData(ReadRow row, ReadWriteRow destRow)
    /*-throws DataSetException-*/
  {
    if (lookupMap != null) {
      for (int ordinal = 0; ordinal < lookupMap.length; ++ordinal) {
        if (lookupMap[ordinal] != null)
          lookupMap[ordinal].lookup(row, destRow.getVariantStorage(ordinal));
      }
    }
  }

  public boolean isCalc(int ordinal) {
    return calcs[ordinal];
  }

  public void setLoading(boolean loading) {
    this.loading  =  loading;
  }

  private boolean         loading;
  private InternalRow     internalReadRow;
  private boolean[]       calcs;
  private Lookup[]        lookupMap;
  private Aggregator[]    aggregatorMap;
  private Aggregator[]    aggregators;
  private StorageDataSet  dataSet;
  private DataRow         customRow;
}
