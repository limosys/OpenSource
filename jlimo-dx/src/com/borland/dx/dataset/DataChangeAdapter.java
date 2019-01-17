//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataChangeAdapter.java,v 7.0 2002/08/08 18:39:19 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * This is an adapter class for DataChangeListener, which is used as a notification
 * that changes to the data in a DataSet have occurred.
 */
public class DataChangeAdapter
  implements DataChangeListener
{
  /** Arbitrary data change to one or more rows.
  */
  public void dataChanged(DataChangeEvent event) {}
  public void postRow(DataChangeEvent event) throws Exception {}
}
