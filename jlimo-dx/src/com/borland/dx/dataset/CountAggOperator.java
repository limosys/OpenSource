//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CountAggOperator.java,v 7.1 2003/05/20 18:47:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

/**
 * The CountAggOperator class is instantiable subclass of the
 * {@link com.borland.dx.dataset.AggOperator} and defines an aggregation
 * operation of a count.
 * <p>
 * Set the aggOperator property of the {@link com.borland.dx.dataset.AggDescriptor}
 * object to this class to perform a count calculation using the property settings
 * stored in the AggDescriptor. Attach the AggDescriptor object to a
 * Column component's agg property to access the data that the aggregation uses.
 */
public class CountAggOperator extends AggOperator {

/**
 *  Initializes the CountAggOperator by calling the constructor of its superclass.
 * @param dataSet           The StorageDataSet that contains the aggColumn that is being aggregated on
 * @param groupColumnNames  A array of the Column names to perform grouping by.
 * @param aggDataSet        The internal StorageDataSet that contains and maintains aggregated values.
 * @param resultColumn      The Column in aggDataSet that contains the aggregated value.
 * @param aggColumn         The Column in the DataSet that is being aggregated on.
 */
  public void init( StorageDataSet  dataSet,
                    String[]        groupColumnNames,
                    StorageDataSet  aggDataSet,
                    Column          resultColumn,
                    Column          aggColumn
                  )
    /*-throws DataSetException-*/
  {
    int dataType        = resultColumn.getDataType();
    aggValue              = new Variant(Variant.INT);
    aggValue.setInt(1);
    resultValue              = new Variant(dataType);
    this.aggColumn      = aggColumn;
    this.resultColumn   = resultColumn;
  }

  /**
   * A row has been added or updated.
   * @param row             The row containing the values.
   * @param internalRow     The unique identifier for the row.
   * @param first            Returns true if this is the first row in the group, false otherwise.
   */
  public void add(ReadRow row, long internalRow, boolean first)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.check(row!=null);
    DiagnosticJLimo.check(aggColumn!=null);
    aggDataSet.getVariant(resultColumn.ordinal, resultValue);
    aggValue.add(resultValue, resultValue);
    aggDataSet.setVariant(resultColumn.ordinal, resultValue);
  }

  /**
   * A row has been deleted or updated.
   * @param row           The row containing the values.
   * @param internalRow   A unique identifier for the row.
   */
  public void delete(ReadRow row, long internalRow)
    /*-throws DataSetException-*/
  {
    aggDataSet.getVariant(resultColumn.ordinal, resultValue);
    resultValue.subtract(aggValue, resultValue);
    //! Diagnostic.println("countaggoperator delete:  "+resultValue);
    aggDataSet.setVariant(resultColumn.ordinal, resultValue);
  }

  private static final long serialVersionUID = 1L;
}
