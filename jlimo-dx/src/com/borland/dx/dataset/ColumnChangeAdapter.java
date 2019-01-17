//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnChangeAdapter.java,v 7.0 2002/08/08 18:39:18 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
    Column value editing events.
*/

/**
 * This class is an adapter class for
 * {@link com.borland.dx.dataset.ColumnChangeListener},
 * which is used for notification when a field value changes.
 */
public class ColumnChangeAdapter
  implements ColumnChangeListener
{
  public void validate(DataSet dataSet, Column column, Variant value)
    throws Exception, DataSetException
  {
  }
  public void changed(DataSet dataSet, Column column, Variant value)
    /*-throws DataSetException-*/
  {
  }
}
