//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CustomAggOperator.java,v 7.0 2002/08/08 18:39:19 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

/**
    The CustomAggOperator class is used internally as a no-op place holder
    for columns values that are aggregated by using the StorageDataSet.CalcAggFieldsListener
    event.

    To aggregate Column values with the StorageDataSet.CalcAggFieldsListener event
    you must:

    1) The aggColumn in the Column.AggDescriptor property must be set to null
    2) The aggOperator in the Column.AggDescriptor property must be set to null.
    3) Add an aggregate Column to maintain the aggregate value.  Set its CalcType
    property to CalcType.Aggregate
    4) Register a CalcAggFieldsListener with the aggregate Column's StorageDataSet.

*/

public class CustomAggOperator extends AggOperator {

  public final boolean isUpdatable() { return false; }

  /**
   * This method is used internally by other com.borland classes.
   * You should never use this method directly.
   * This method is a "dummy" add request.
   * The StorageDataSet.calcAggFieldsListener maintains the aggregate.
   * @param row
   * @param internalRow
   * @param first
   */
  public void add(ReadRow row, long internalRow, boolean first)
    /*-throws DataSetException-*/
  {
  }

  /**
   *  This method is used internally by other com.borland classes.
   *  You should never use this method directly.
   *  This method is a "dummy" delete request.
   *  The StorageDataSet.calcAggFieldsListener maintains the aggregate.
   * @param row
   * @param internalRow
   */
  public void delete(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
  }
  private static final long serialVersionUID = 1L;
}
