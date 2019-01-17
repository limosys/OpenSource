//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataSetAware.java,v 7.0 2002/08/08 18:39:22 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.dx.dataset;

/**
 * The DataSetAware interface is a way for a component to declare
 * to JBuilder that it knows how to bind DataSets.
 * This allows the Inspector to provide additional help when
 * editing these properties.  Note that all the interface methods
 * defined here are valid JavaBeans property getters/setters
 * by design to simplify the component's implementation.
 *
 * The ColumnAware interface extends this to include the extra
 * granularity of a single column setting.
 */
public interface DataSetAware
{
/**
 * Determines the DataSet the control that implements this interface is linked to.
 * @param dataSet
 */
  public void setDataSet(DataSet dataSet);

  /**
   * Determines the DataSet the control that implements this interface is linked to.
   * @return
   */
  public DataSet getDataSet();
}
