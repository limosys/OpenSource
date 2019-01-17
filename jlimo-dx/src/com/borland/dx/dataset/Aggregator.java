//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Aggregator.java,v 7.0 2002/08/08 18:39:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

class Aggregator {
  Aggregator(StorageDataSet dataSet, int ordinal)
    /*-throws DataSetException-*/
  {
    operatorCount         = 0;
    operatorMap           = new AggOperator[dataSet.getColumnCount()];
    String[] columnNames  = dataSet.getColumn(ordinal).getAgg().getGroupColumnNames();
    groupColumnNames      = new String[columnNames==null?0:columnNames.length];
  }

  void open()
    /*-throws DataSetException-*/
  {
    operators = new AggOperator[operatorCount];

    if (aggDataSet != null) {
      if (groupColumnNames != null && groupColumnNames.length > 0)
        aggDataSet.setSort(new SortDescriptor(groupColumnNames, false, false));

      aggDataSet.open();

      searchRow = new DataRow(aggDataSet, groupColumnNames);
    }


    int operatorIndex = -1;
    isUpdatable = false;
    for (int index = 0; index < operatorMap.length; ++index)
      if (operatorMap[index] != null) {
        operators[++operatorIndex]  = operatorMap[index];
        operators[operatorIndex].open(aggDataSet);
//!       if (operators[operatorIndex].isUpdatable())
          isUpdatable = true;
      }
  }

  final void reOpen()
    /*-throws DataSetException-*/
  {
    if (aggDataSet != null)
      aggDataSet.open();
  }

  final void close()
    /*-throws DataSetException-*/
  {
    if (aggDataSet != null)
      aggDataSet.close();
  }

  final boolean groupEquals(Column column)
    /*-throws DataSetException-*/
  {
    AggDescriptor descriptor  = column.getAgg();
    AggOperator   aggOperator = descriptor.getAggOperator();
    if (descriptor.groupEquals(groupColumnNames) && (aggOperator == null || aggOperator.needsAggDataSet())) {
      for (int index = 0; index < operatorMap.length; ++index)
        if (operatorMap[index] != null) {
          return operatorMap[index].needsAggDataSet();
        }
    }
    return false;
  }

  void addOperator(StorageDataSet dataSet, int ordinal)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.check(operatorMap[ordinal] == null);
    ++operatorCount;

    Column  calcColumn        = dataSet.getColumn(ordinal);
    AggDescriptor descriptor  = calcColumn.getAgg();

    operatorMap[ordinal]  = (AggOperator)descriptor.getAggOperator();
    if (operatorMap[ordinal] == null)
      operatorMap[ordinal]  = new CustomAggOperator();
    else
      operatorMap[ordinal]  = (AggOperator)operatorMap[ordinal].clone();

    String[] columnNames  = dataSet.getColumn(ordinal).getAgg().getGroupColumnNames();
    if (columnNames != null)
      System.arraycopy(columnNames, 0, groupColumnNames, 0, groupColumnNames.length);
    if (operatorCount == 1 && operatorMap[ordinal].needsAggDataSet()) {
      aggDataSet = dataSet.createAggDataSet(groupColumnNames);
      Column groupColumn;
      for (int index = 0; index < groupColumnNames.length; ++index) {
        groupColumn = dataSet.getColumn(groupColumnNames[index]);
        aggDataSet.addColumn( groupColumn.getColumnName(),
                              groupColumn.getDataType()
                            );
      }
    }

    Column  aggColumn;
    Column  resultColumn  = null;
    if (aggDataSet != null) {
      resultColumn      = new Column( calcColumn.getColumnName(),
                                      calcColumn.getCaption(),
                                      calcColumn.getDataType()
                                    );

      aggDataSet.addColumn(resultColumn);
    }

    if (descriptor.getAggColumnName() == null && operatorMap[ordinal] instanceof CustomAggOperator)
      aggColumn = calcColumn;  // Dummy place holder.
    else
      aggColumn = dataSet.getColumn(descriptor.getAggColumnName());

    operatorMap[ordinal].init(dataSet, groupColumnNames, aggDataSet, resultColumn, aggColumn);
  }

  private boolean locateNewRow(ReadRow row)
    /*-throws DataSetException-*/
  {
    if (!locate(row)) {
      row.copyTo(searchRow);
      aggDataSet.addRow(searchRow);
      //! Diagnostic.println("rowCount:  "+aggDataSet.rowCount());
      locate(row);
      return true;
    }
    return false;
  }

  void add(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    if (isUpdatable && aggDataSet != null) {
//!     Diagnostic.println("add: "+internalRow);
      boolean first = locateNewRow(row);
      //! Diagnostic.println("aggDataSet row() before add:  "+aggDataSet.row());
      for (int index = 0; index < operators.length; ++index) {
        if (operators[index] != null)
          operators[index].add(row, internalRow, first);
      }
      //! Diagnostic.println("aggDataSet after add:  "+aggDataSet);
      aggDataSet.post();
    }
  }

  void delete(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    if (isUpdatable && aggDataSet != null) {
      locate(row);
      //! Diagnostic.println("aggDataSet before delete:  "+aggDataSet);
      for (int index = 0; index < operators.length; ++index) {
        if (operators[index] != null)
          operators[index].delete(row, internalRow);
      }
      //! Diagnostic.println("aggDataSet after delete:  "+aggDataSet);
      aggDataSet.post();
    }
  }
  void update(ReadRow oldRow, ReadRow newRow, long internalRow)
    /*-throws DataSetException-*/
  {
    if (isUpdatable && aggDataSet != null) {
      // Careful if you want to optimized this.  Grouping columns
      // and aggregating columns could have changed.  ie could be subtracting
      // from one group and adding to another.
      //
      delete(oldRow, internalRow);
      add(newRow, internalRow);
    }
  }

  void updateCustomAggs(ReadRow row, ReadWriteRow updatedRow)
    /*-throws DataSetException-*/
  {
    if (isUpdatable && aggDataSet != null) {
      locateNewRow(row);
      int     count = aggDataSet.getColumnCount();
      Variant value;
      Column  column;
      //! Diagnostic.println("aggDataSet before customUpdate:  "+aggDataSet);
      for (int ordinal = 0; ordinal < operatorMap.length; ++ordinal) {
        column  = row.getColumn(ordinal);
        if (operatorMap[ordinal] != null && !updatedRow.isUnassignedNull(column.getColumnName()))
          operatorMap[ordinal].set(updatedRow.getVariantStorage(column.getColumnName()));
      }
      //! Diagnostic.println("aggDataSet after customUpdate:  "+aggDataSet);
      aggDataSet.post();
    }
  }

  void getVariant(ReadRow row, int calcOrdinal, Variant value)
    /*-throws DataSetException-*/
  {
    if (operatorMap[calcOrdinal] != null) {
      locate(row);
      operatorMap[calcOrdinal].get(value);
    }
    //operatorMap[calcOrdinal].get(value);
  }

  void getRowData(ReadRow row, ReadWriteRow destRow)
    /*-throws DataSetException-*/
  {
    locate(row);
    Column[]  columns = destRow.columnList.getScopedArray();
    Column    column;
    for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
      column  = columns[ordinal];
      if (operatorMap[column.ordinal] != null)
        operatorMap[column.ordinal].get(destRow.getVariantStorage(ordinal));
    }
  }


  private boolean locate(ReadRow row)
    /*-throws DataSetException-*/
  {
    if (aggDataSet == null)
      return operators[0].locate(row);
    else {
      row.copyTo(searchRow);
//!     Diagnostic.println(" locating:  "+searchRow+" "+aggDataSet.locate(searchRow, Locate.FIRST));
      return aggDataSet.locate(searchRow, Locate.FIRST);
    }
  }


//! String[] getGroupColumnNames() { return groupColumnNames; }

  private String[]        groupColumnNames;
  private AggOperator[]   operators;
  private AggOperator[]   operatorMap;
  private int             operatorCount;
  private StorageDataSet  aggDataSet;
  private DataRow         searchRow;
  private boolean         isUpdatable;
}
