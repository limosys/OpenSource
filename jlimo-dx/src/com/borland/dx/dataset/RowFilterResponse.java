//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/RowFilterResponse.java,v 7.0 2002/08/08 18:39:34 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * This class includes or excludes the current row. Rows that are not displayed
 * in the current view are not removed from the DataSet, only from the current,
 * filtered view of a DataSet. If a newly inserted row contains a value that excludes
 * it from current filter criteria, it is stored in the DataSet, but is not displayed
 * in the current view when posted.
 * <p>
 * This class is usually called from the DataSet object's filterRow event. You
 * restrict the rows included in a view by adding a RowFilterListener and using
 * it to define which rows should be shown. The default action in a RowFilterListener
 * is to exclude the row. Your code should call the RowFilterResponse's add() method
 * for every row that should be included in the view.
 */
public class RowFilterResponse {

  /**
   * Call this method inside the filterRow() method to cause the row to be included
   * in the current DataSetView (i.e. in the filtered view of the DataSet).
   */
  public final void add() { response  = true; }

  /**
   * Call this method inside the filterRow() method to cause the row to be excluded
   * in the DataSetView (i.e. in the filtered view of the DataSet).
   */
  public final void ignore() { response = false; }

  /**
   * This method returns <b>true</b> if the row should be added to the DataSetView,
   * otherwise, it returns <b>false</b>.
   * @return  <b>true</b> if the row should be added to the DataSetView,
   *          otherwise, it returns <b>false</b>.
   */
  public final boolean canAdd() { return response; }

  boolean response;
}
