//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CalcFieldsListener.java,v 7.0 2002/08/08 18:39:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * Used for performing calculations on row values.
 */
public interface CalcFieldsListener extends EventListener
{

  /**
   * Called whenever a field value is modified or added in the listener's data set.
   * The isPosted parameter indicates that the row is being posted.
   * You may not want to recalculate fields on rows that are not yet posted.
   *
   * @param changedRow    The current values in the row, including the field that
   *                      was just added or modified.
   * @param calcRow       The values to be left in the row when calcFields returns.
   *                      Typically, the code in calcFields only modifies values in the
   *                      calculated columns of calcRow.
   * @param isPosted      Indicates whether or not the row is being posted.
   */
  public void calcFields(ReadRow changedRow, DataRow calcRow, boolean isPosted) /*-throws DataSetException-*/;
}
