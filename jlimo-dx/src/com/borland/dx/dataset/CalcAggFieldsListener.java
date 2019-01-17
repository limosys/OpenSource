//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CalcAggFieldsListener.java,v 7.0 2002/08/08 18:39:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * for performing calculations on aggregated values.
 */
public interface CalcAggFieldsListener extends EventListener
{
/**
 * Called when a new or modified row is posted. In the case of a modified row,
 * both calcAggDelete() and calcAggAdd() are called.
 *
 * @param row           The contents of the newly added or modified row.
 * @param resultRow     The values you want posted to the row. When calcAggAdd() is called,
 *                      resultRow has the same values an row. Typically, you only change the
 *                      values in columns that are calculations on aggregate columns.
 */
  public void calcAggAdd(ReadRow row, ReadWriteRow resultRow) /*-throws DataSetException-*/;

  /**
   * Called when a row is deleted from the data set or a modified row is posted.
   * In the case of a modified row, calcAggAdd() is also called.
   *
   * @param row           The contents of the deleted or modified row.
   * @param resultRow     The contents of the deleted or modified row,
   *                      including any changes you make to the columns that are
   *                      calculations on aggregate columns. Values that you place in
   *                      these columns will be duplicated into other rows in the same
   *                      group before the row is deleted.
   */
  public void calcAggDelete(ReadRow row, ReadWriteRow resultRow) /*-throws DataSetException-*/;

}
