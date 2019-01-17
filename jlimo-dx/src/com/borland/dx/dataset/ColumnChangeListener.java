//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnChangeListener.java,v 7.1 2003/09/18 00:47:24 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
    Column value editing events.
*/
/**
 * For events related to editing column values.
 */
public interface ColumnChangeListener extends EventListener
{
  /**
   * Called before column level validations like readOnly, min, or max are performed,
   * and before the new value is recorded in a ReadWriteRow. Note that if
   * values are set programmatically, EditMask constraints are not applied.
   * You can change the value stored in value, but it must still pass the column
   * level validations. To prevent the value from being set, throw an Exception.
   * If an Exception is constructed with a String parameter, this String is used
   * in the default error handling display, for example,
   * <p>
   * <code>throw new Exception("My error message");</code>
   *
   * @param dataSet           Which data set contains the column that has data that has changed.
   * @param columnWhich       Which column has data that has changed.
   * @param value             The new value of the data in the column that changed.
   * @throws Exception
   * @throws DataSetException
   */
  void validate(DataSet dataSet, Column column, Variant value) throws Exception, DataSetException;
  /** Called after a Column value has been successfully set inside a ReadWriteRow.
  */

  /**
   * Called after all column level validations have been performed and a column
   * value has been successfully posted inside a ReadWriteRow.
   *
   * @param dataSet   Which data set contains the column that has data that has
   *                  been accepted as a valid field value.
   * @param column    Which column has data that has been changed and verified as valid.
   * @param value     The new, valid value of the data in the column that changed.
   */
  void changed(DataSet dataSet, Column column, Variant value) /*-throws DataSetException-*/;
}
