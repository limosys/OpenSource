//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/OpenListener.java,v 7.0 2002/08/08 18:39:30 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * This interface is used as a notification when a DataSet is opened or closed.
 * This is especially useful when your application uses many lookup tables.
 * You may verify whether or not a DataSet is open before performing a lookup,
 * open it as necessary to perform the lookup, then close it to improve performance.
 */
public interface OpenListener extends EventListener
{
/**
 * The name of the data set that was opened.
 * @param dataSet     The name of the data set to be opened.
 */
  public void opening(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   * Called to notify the listener that a data set has been opened.
   * @param dataSet    The name of the data set that was opened.
   */
  public void opened(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   * Called to notify the listener before a data set is closed.
   * @param dataSet   The name of the data set to be closed.
   */
  public void closing(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   * Called to notify the listener that a data set has been successfully closed.
   * @param dataSet   The name of the data set that was closed.
   */
  public void closed(DataSet dataSet) /*-throws DataSetException-*/;
}
