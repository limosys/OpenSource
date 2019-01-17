//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Lookup.java,v 7.2 2003/06/13 00:37:27 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

class Lookup {

  Lookup(StorageDataSet dataSet, Column column, PickListDescriptor pickList)
    /*-throws DataSetException-*/
  {
    this.pickList = pickList;
    this.column   =  column;
    this.dataSet  =  dataSet;
    this.calcField               = (column.getCalcType() == CalcType.LOOKUP);
  }

  private final void init()
    /*-throws DataSetException-*/
  {
    if (destinationColumns == null) {
      destinationColumns      = pickList.getDestinationColumns();
      pickListColumns         = pickList.getPickListColumns();
      pickListDataSet         = pickList.getPickListDataSet();
      pickListDataSet.open();
      pickListRow             = new DataRow(pickListDataSet, pickListColumns);
      destRow                 = new DataRow(dataSet, destinationColumns);
      displayOrdinal          = pickListDataSet.getColumn(pickList.getLookupDisplayColumn()).getOrdinal();
    }
    if (!pickListRow.isCompatibleList(pickListDataSet))
      pickListRow = new DataRow(pickListDataSet, pickListColumns);
    if (!destRow.isCompatibleList(dataSet))
      destRow = new DataRow(dataSet, destinationColumns);
    pickListDataSet.dataSetStore.closeProvider(true);
  }

  void lookup(DataSet dataSet, long row, Variant value)
    /*-throws DataSetException-*/
  {
      init();

      dataSet.getDataRow(row, destRow);
      DiagnosticJLimo.check(pickListColumns != null);
      DiagnosticJLimo.check(destinationColumns != null);
      destRow.copyTo(destinationColumns, destRow, pickListColumns, pickListRow);

      value.setUnassignedNull();
      pickListDataSet.dataSetStore
      .lookup(  pickListDataSet,
                pickListRow.columnList.getScopedArray(),
                pickListRow,
                displayOrdinal,
                value,
                Locate.FIRST
             );
  }

  void lookup(ReadRow readRow, Variant value)
    /*-throws DataSetException-*/
  {
      init();

      destRow.copyTo(destinationColumns, readRow, pickListColumns, pickListRow);

      value.setUnassignedNull();
      pickListDataSet.dataSetStore
      .lookup(  pickListDataSet,
                pickListRow.columnList.getScopedArray(),
                pickListRow,
                displayOrdinal,
                value,
                Locate.FIRST
             );
  }

  final void fillIn(DataSet dataSet, Variant value)
    /*-throws DataSetException-*/
  {
    init();
    if (fillInRow == null || !fillInRow.isCompatibleList(dataSet)) {
      fillInRow       = new DataRow(pickListDataSet, pickListDataSet.getColumn(displayOrdinal).getColumnName());
      fillInDestRow   = new DataRow(pickListDataSet, pickListColumns);
    }

    fillInRow.setVariant(0, value);
    if (pickListDataSet.dataSetStore.lookup(fillInRow, fillInDestRow, Locate.FIRST))
      destRow.copyTo(pickListColumns, fillInDestRow, destinationColumns, dataSet);
  }

  private DataSet             dataSet;
  private PickListDescriptor  pickList;
  private Column              column;

  private DataSet         pickListDataSet;
  private String[]        destinationColumns;
  private String[]        pickListColumns;
  private int             displayOrdinal;
  private DataRow         fillInRow;
  private DataRow         fillInDestRow;
  private DataRow         destRow;
  private DataRow         pickListRow;
          boolean         calcField;
}
