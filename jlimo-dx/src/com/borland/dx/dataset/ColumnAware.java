//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnAware.java,v 7.0 2002/08/08 18:39:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The ColumnAware interface is a way for a component to declare
 * to JBuilder that it knows how to bind DataSets and Columns.
 * This allows the Inspector to provide additional help when
 * editing these properties.  Note that all the interface methods
 * defined here are valid JavaBeans property getters/setters
 * by design to simplify the component's implementation.
 */
public interface ColumnAware extends DataSetAware
{
/**
 * Determines which Column is accessed by the control that
 * implements this interface in the DataSet.
 *
 * @param columnName  The Column that implements this interface in the DataSet.
 */
  public void setColumnName(String columnName);

  /**
   * Determines which Column is accessed by the control that
   * implements this interface in the DataSet.
   * @return    The Column accessed by the control that implements this
   *            interface in the DataSet.
   */
  public String getColumnName();
}
