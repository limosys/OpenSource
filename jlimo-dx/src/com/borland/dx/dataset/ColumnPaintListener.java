//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnPaintListener.java,v 7.0 2002/08/08 18:39:19 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * The ColumnPaintListener interface is used for notification when painting of a
 * value in a Column at a specific row location is occurring.
 */
public interface ColumnPaintListener extends EventListener
{
  /**
   * Use this method to set display properties on the incoming paintSite and/or
   * modify the incoming value. If value is modified, it does not affect the value
   * stored in the dataSet. It only affects the value to be displayed.
   *
   * @param dataSet
   * @param column
   * @param row
   * @param value
   * @param paintSite
   */
  void painting(DataSet dataSet, Column column, int row, Variant value, CustomPaintSite paintSite);

 /**
  * Use this method to set display properties for the editor on the incoming paintSite.
  *
  * @param dataSet
  * @param column
  * @param paintSite
  */
  void editing(DataSet dataSet, Column column, CustomPaintSite paintSite);
}
