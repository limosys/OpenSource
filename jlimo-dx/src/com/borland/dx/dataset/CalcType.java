//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CalcType.java,v 7.0 2002/08/08 18:39:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

public interface CalcType {

   /**
    * Constant used to designate that a calculation is not used for this Column.
    */
  public static final int  NO_CALC = 0;

   /**
    *  Constant used to designate a basic calculated field that is updated
    *  by the calcFields event of a Column when rows are changed or added.
    */
  public static final int  CALC = 1;

   /**
    * Constant used to designate a calculated field that summarizes across
    * multiple rows. To work with an aggregation calculation, set the agg
    * property of the Column to the AggDescriptor object that contains the
    * properties associated with the aggregation.
    */
  public static final int  AGGREGATE = 2;

  /**
   * Constant used to designate that this column gets its value from a Column
   * in another DataSet. The com.borland.dx.dataset.Column.PickList
   * property must be set with the com.borland.dx.dataset.PickList.LookupDisplayColumn
   * set to a non-null value for the lookup to work.
   */
  public static final int  LOOKUP = 3;

}
