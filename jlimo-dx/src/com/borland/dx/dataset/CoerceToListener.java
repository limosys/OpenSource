//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CoerceToListener.java,v 7.0 2002/08/08 18:39:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * Used by implementors of Providers and Resolvers to coerce data from the
 * data type of the source to the data type of the Column.
 * It is used by QueryDataSet, ProcedureDataSet, QueryProvider and
 * ProcedureProvider components.
 */
public interface CoerceToListener extends EventListener
{

/**
 * Allows an application to control the coercion of a data value from the
 * data source to a column value in dataSet.
 *
 * @param dataSet     The DataSet in which the Column exists.
 * @param column      The name of the column in dataSet parameter.
 * @param from        The data value from the data source which needs to
 *                    be coerced to the to parameter.
 * @param to          The column value that the from parameter must be coerced to.
 */
  void coerceToColumn(StorageDataSet dataSet, Column column, Variant from, Variant to) /*-throws DataSetException-*/;
}
