//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/AggOperator.java,v 7.0 2002/08/08 18:39:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

/**
 *  Components that perform aggregate operations for aggregate columns.
 *  Specified through the aggDescriptor property of a Column.
 *
 * The <CODE>AggOperator</CODE> class is an abstract class that defines basic aggregator behavior of a calculated value or a calculated <CODE>Column</CODE>.  All aggregation operators must extend from this class, therefore, you should extend this class when creating custom <CODE>AggOperator</CODE> classes. The following aggregator operators are provided and can be specified as the <CODE>aggOperator</CODE> of the <A HREF="com.borland.dx.dataset.AggDescriptor.html"><CODE>AggDescriptor</CODE></A> object:
<UL>
<LI><A href="com.borland.dx.dataset.SumAggOperator.html"><CODE>SumAggOperator</CODE></A>
<LI><A href="com.borland.dx.dataset.CountAggOperator.html"><CODE>CountAggOperator</CODE></A>
<LI><A href="com.borland.dx.dataset.MinAggOperator.html"><CODE>MinAggOperator</CODE></A> (a subclass of <A href="com.borland.dx.dataset.BoundsAggOperator.html"><CODE>BoundsAggOperator</CODE></A>)
<LI><A href="com.borland.dx.dataset.MaxAggOperator.html"><CODE>MaxAggOperator</CODE></A> (a subclass of <CODE>BoundsAggOperator</CODE>)
</UL>

<!-- JDS start - remove entire paragraph -->
<P>For examples of applications that use aggregators, see the following projects in the specified directories of your JBuilder installation:

<UL>
<LI>Aggregating.jpr in the  /samples/DataExpress/Aggregating folder
<LI>IntlDemo.jpr in the /samples/dbSwing/MultiLingual folder
<LI>Source code for <CODE>SumAggOperator</CODE>, <CODE>CountAggOperator</CODE>, <CODE>MinAggOperator</CODE>, and <CODE>MaxAggOperator</CODE>
</UL>

<!-- JDS end -->

 */
public abstract class AggOperator implements Cloneable, java.io.Serializable {

  /**
   * Called when the AggOperator is being initialized.
   * If overridden by an extended class, super.init() should still be
   * called to ensure proper initialization.
   * Note that this method is called before the aggDataSet is opened.
   * This allows an AggOperator extension to add a column to the aggDataSet.
   * This can be useful for some types of maintained aggregations like
   * average that would need to accumulate count and total.
   * @param dataSet              The StorageDataSet that contains the aggColumn that is being aggregated on.
   * @param groupColumnNames     A array of the Column names to perform grouping by.
   * @param aggDataSet           The internal StorageDataSet that contains and maintains aggregated values.
   * @param resultColumn         The Column in aggDataSet that contains the aggregated value.
   * @param aggColumn            The Column in the DataSet that is being aggregated on.
   */
  public void init( StorageDataSet  dataSet,
                    String[]        groupColumnNames,
                    StorageDataSet  aggDataSet,
                    Column          resultColumn,
                    Column          aggColumn
                  )
    /*-throws DataSetException-*/
  {
    this.dataSet        = dataSet;
    int dataType        = resultColumn.getDataType();
    aggValue            = new Variant(aggColumn.getDataType());
    resultValue         = new Variant(dataType);
    this.aggColumn      = aggColumn;
    this.resultColumn   = resultColumn;
  }

  /**
   *  This method returns <b>true</b> and specifies whether the AggOperator
   *  subclass requires an aggDataSet (an internal StorageDataSet that
   *  contains and maintains aggregated values).
   *  The MinAggOperator and MaxAggOperator classes internally override
   *  this setting since they use a secondary index to track minimum
   *  and maximum values rather than an aggDataSet.
   * @return  true
   */
  public boolean needsAggDataSet()
  {
    return true;
  }
  /** Called when the aggDataSet is opened and prepared for usage.
  */
  public void open(DataSet aggDataSet){
    this.aggDataSet = aggDataSet;
    resultColumn    = aggDataSet.hasColumn(resultColumn.getColumnName());
  }

  /**
   * A row has been added or updated.
   * @param row                 The row containing the values.
   * @param internalRow         The unique identifier for the row.
   * @param first               Returns <b>true</b> if this is the first
   *                            row in the group, <b>false</b> otherwise.
   */
  public        abstract void         add(ReadRow row, long internalRow, boolean first) /*-throws DataSetException-*/;

  /**
   *  A row has been deleted or updated.
   * @param row             The value of the row
   * @param internalRow     A unique identifier for that row.
   */
  public        abstract void         delete(ReadRow row, long internalRow) /*-throws DataSetException-*/;
//! public                  boolean     isUpdatable() { return true; }

  void get(Variant value)
    /*-throws DataSetException-*/
  {
    //! Diagnostic.println("get:  "+aggDataSet.row()+" "+resultColumn.ordinal);
    aggDataSet.getVariant(resultColumn.ordinal, value);
    //! Diagnostic.println("get:  "+aggDataSet.row()+" "+value);
  }

  void set(Variant value)
    /*-throws DataSetException-*/
  {
//!   Diagnostic.println("set for:  "+aggColumn.getColumnName()+" "+resultColumn.getColumnName());
    aggDataSet.setVariant(resultColumn.ordinal, value);
  }

  /**
   * Implemented by subclasses {@link com.borland.dx.dataset.MinAggOperator}
   * and {@link com.borland.dx.dataset.MaxAggOperator}
   * to locate the minimum or maximum value for the grouping column
   * values specified in row.
   * @param row
   * @return
   */
  public boolean locate(ReadRow row)
    /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();
    return false;
  }

  /**
   * @return A clone (or copy) of this AggOperator.
   */
  public Object clone() {
    try {
      return super.clone();
    }
    catch (java.lang.CloneNotSupportedException ex) {
      DiagnosticJLimo.printStackTrace(ex);
      return null;
    }
  }

  /**
      dataSet is the the StorageDataSet that contains the aggColumn that is being
      aggregated on.
  */
  protected transient StorageDataSet  dataSet;
  /**
      aggDataSet is the internal StorageDataSet that contains and maintains aggregated values.
  */
  protected transient DataSet         aggDataSet;

  /**
   * The Column in the DataSet that is being aggregated on.
   */
  protected transient Column          aggColumn;
  /**
      The Column in aggDataSet that contains the aggregated value.
  */
  protected transient Column          resultColumn;
  /** Preallocated storage for an aggColumn value.  AggOperator Extension classes
      use this for storing and manipulating an aggColumn value.
  */
  protected transient Variant         aggValue;
  /**
   *  Pre-allocated storage for a resultColumn value. AggOperator extension
   *  classes use this for storing and manipulating a resultColumn value.
  */
  protected transient Variant         resultValue;

  private static final long serialVersionUID = 1L;
}
