//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CalcAggFieldsAdapter.java,v 7.0 2002/08/08 18:39:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * Used for performing calculations on aggregated values.
 */

 /**
  * This class is an adapter class for {@link com.borland.dx.dataset.CalcAggFieldsListener}.
  * It is used for performing calculations on aggregated columns.
  */
public class CalcAggFieldsAdapter
  implements CalcAggFieldsListener
{
  public void calcAggAdd(ReadRow row, ReadWriteRow resultRow) /*-throws DataSetException-*/ {}
  public void calcAggDelete(ReadRow row, ReadWriteRow resultRow) /*-throws DataSetException-*/ {}

}
