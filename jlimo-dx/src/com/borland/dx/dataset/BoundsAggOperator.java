//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/BoundsAggOperator.java,v 7.0 2002/08/08 18:39:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;


public abstract class BoundsAggOperator extends AggOperator {


  public void init( StorageDataSet  dataSet,
                    String[]        groupColumnNames,
                    StorageDataSet  aggDataSet,
                    Column          resultColumn,
                    Column          aggColumn
                  )
    /*-throws DataSetException-*/
  {
//!/*
//!    super.init(dataSet, groupColumnNames, aggDataSet, resultColumn, aggColumn);
//!    String minRow = "internalRow"+aggDataSet.getColumnCount();
//!    aggDataSet.addColumn(minRow, Variant.INT);
//!    rowOrdinal  = aggDataSet.getColumnCount()-1;
//!*/
    dataSetView = new DataSetView();
    dataSetView.setStorageDataSet(dataSet);

    String  sortKeys[]    = new String[groupColumnNames.length+1];
    System.arraycopy(groupColumnNames, 0, sortKeys, 0, groupColumnNames.length);
    DiagnosticJLimo.check(aggColumn.getColumnName() != null);
    sortKeys[sortKeys.length-1] = aggColumn.getColumnName();
    dataSetView.setSort(new SortDescriptor(sortKeys));

    // subtle.  calcs can be initialized when DataSet has been marked open, but StorageDataSet
    // is not open yet.  (QueryDataSet -> startLoading -> initCalcs.  By adding
    // in as a StorageAccessListener, this view will be asked to open when the
    // "Storage" becomes open.
    //
    if (dataSet.isStorageOpen())
      dataSetView.open();
    else
      dataSet.addStorageAccessListener(dataSetView);
    ordinal = dataSet.getColumn(aggColumn.getColumnName()).getOrdinal();

    // Optimization that signals first()/last() can be used instead of locate()
    // to find start/end of a group.
    //
    if (groupColumnNames.length > 0)
      searchRow = new DataRow(dataSet, groupColumnNames);
  }

  public void open(DataSet aggDataSet){
  }


  void get(Variant value)
    /*-throws DataSetException-*/
  {
//!  /*
//!    if (false && bugCount == 50) {
//!      Diagnostic.println(bugCount+" "+dataSetView.getRow()+" "+dataSetView.getColumn(ordinal).getColumnName()+" get:  "+dataSetView.getVariantStorage(ordinal));
//!      int row = dataSetView.getRow();
//!      dataSetView.first();
//!      Diagnostic.println(" DataType:  "+dataSetView.getVariantStorage(ordinal).getType());
//!      while (dataSetView.inBounds()) {
//!        Diagnostic.println(dataSetView.getRow()+" "+dataSetView.getVariantStorage(0)+":"
//!                                                   +dataSetView.getVariantStorage(1)+":"
//!                                                   +dataSetView.getVariantStorage(2)+":"
//!                                                   +dataSetView.getVariantStorage(ordinal)+":"
//!                          );
//!        dataSetView.next();
//!      }
//!    }
//!*/
    if (found)
      dataSetView.getVariant(ordinal, value);
    else
      value.setUnassignedNull();
    //! Diagnostic.println("get:  "+aggDataSet.row()+" "+value);
  }

  final void set(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
//!/*
//!    row.getVariant(aggColumn.ordinal, aggValue);
//!   Diagnostic.println("setting:  "+aggColumn.getColumnName()+" "+aggValue);
//!    aggDataSet.setVariant(resultColumn.ordinal, aggValue);
//!    aggDataSet.setInt(rowOrdinal, internalRow);
//!*/
  }

  /**
  */
  public boolean needsAggDataSet()
  {
    return false;
  }

  public void add(ReadRow row, long internalRow, boolean first)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

  public void delete(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
  }

//!/*
//!  public void add(ReadRow row, int internalRow, boolean first)
//!    /*-throws DataSetException-*/
//!  {
//!    Diagnostic.check(row!=null);
//!    Diagnostic.check(aggColumn!=null);
//!    row.getVariant(aggColumn.ordinal, aggValue);
//!    aggDataSet.getVariant(resultColumn.ordinal, resultValue);
//!    if (first || aggDataSet.getInt(rowOrdinal) < 0 || compare(aggValue, resultValue)) {
//!     Diagnostic.println("result:  "+aggDataSet.row()+" "+resultValue);
//!      aggDataSet.setVariant(resultColumn.ordinal, aggValue);
//!      aggDataSet.setInt(rowOrdinal, internalRow);
//!    }
//!  }
//!
//!  public void delete(ReadRow row, int internalRow)
//!    /*-throws DataSetException-*/
//!  {
//!    if (aggDataSet.getInt(rowOrdinal) == internalRow) {
//!      if (dataSetView == null) {
//!        dataSetView = new DataSetView();
//!        dataSetView.setStorageDataSet(dataSet);
//!
//!        String  aggKeys[]     = aggDataSet.getSortKeys();
//!        String  sortKeys[]    = new String[aggKeys.length+1];
//!        System.arraycopy(aggKeys, 0, sortKeys, 0, aggKeys.length);
//!        sortKeys[sortKeys.length-1] = aggColumn.getColumnName();
//!        dataSetView.setSort(new SortDescriptor(sortKeys));
//!        dataSetView.open();
//!
//!        if (aggKeys.length > 0)
//!          dataRow = new DataRow(dataSetView, aggKeys);
//!      }
//!
//!
//!      int setRow;
//!      if (dataRow != null)
//!        row.copyTo(dataRow);
//!
//!      if (!first())
//!        setRow  = -1;
//!      else if (dataSetView.getInternalRow() == internalRow && !next())
//!        setRow  = -1;
//!      else
//!        setRow  = dataSetView.getInternalRow();
//!
//!      set(dataSetView, setRow);
//!    }
//!  }
//!*/


//!/*
//!  abstract boolean compare(Variant aggValue, Variant resultValue);
//!
//!  abstract boolean first() /*-throws DataSetException-*/;
//!
//!  abstract boolean next() /*-throws DataSetException-*/;
//!*/

  boolean     found;
  DataRow     searchRow;
  DataSetView dataSetView;
  int         ordinal;
//! DataRow     dataRow;
//! int         rowOrdinal;

  private static final long serialVersionUID = 1L;
}
