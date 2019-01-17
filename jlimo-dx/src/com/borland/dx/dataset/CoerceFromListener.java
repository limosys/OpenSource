//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CoerceFromListener.java,v 7.0 2002/08/08 18:39:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * Used by implementors of Providers and Resolvers to coerce data from the
 * type of the data source to the type of the Column. It is used by QueryDataSet,
 * ProcedureDataSet, QueryResolver, and ProcedureResolver components.
 */
public interface CoerceFromListener extends EventListener
{

  /**
   *  Allows an application to control the coercion of a data value from a
   *  column in the DataSet to the data source.
   *
   * @param dataSet   The DataSet in which the column exists.
   * @param column    The name of the column in dataSet.
   * @param from      The data value from the data source which needs to be coerced
   *                  to the to parameter.
   * @param to        The column value that the from parameter must be coerced to.
   */
  void coerceFromColumn(StorageDataSet dataSet, Column column, Variant from, Variant to) /*-throws DataSetException-*/;
}
