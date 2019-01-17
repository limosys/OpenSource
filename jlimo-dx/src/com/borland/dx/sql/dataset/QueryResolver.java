//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryResolver.java,v 7.0 2002/08/08 18:39:55 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;

/**
 *<P>
*The <CODE>QueryResolver</CODE> component is the default <CODE>Resolver</CODE>
* object of the <CODE>Database</CODE>
*component. It encapsulates {@link com.borland.dx.dataset.UpdateMode} <CODE>
* UpdateMode</CODE></A>
*behavior and uses SQL queries to resolve data changes to a server.
*
*<P>The <CODE>database</CODE> property of this component must be set to the <CODE>
* Database</CODE> component that this <CODE>QueryResolver</CODE> is associated with.
*  Otherwise, a <CODE>DataSetException</CODE> is generated.
*
*
*<P>To make a query updatable:
*<UL>
*<LI>If the <CODE>updateMode</CODE> property is set to <CODE>ALL_COLUMNS</CODE> (the default), set the
*<CODE>tableName</CODE> property of the <CODE>QueryDataSet</CODE>
*<LI>If a <CODE>QueryResolver</CODE> component has been specified and the
* <CODE>updateMode</CODE> property is
*set to <CODE>KEY_COLUMNS</CODE>, set the <CODE>rowId</CODE> property on the columns used as an index to the table.
*<LI>If you have a BLOB column, set the searchable property to false. This indicates that
*this column should never be used in a WHERE clause of an update query. Most databases do not support
*this for BLOBs.
*</UL>
*
*
*<P>When troubleshooting resolution problems, columns other than those with edited values may be causing
*problems. For instance, the problem might stem from columns which are included in the WHERE clause
*of the update query.
*
*
*<P>If you get a message that no rows were affected by the update query, check  whether someone
*meanwhile modified the original row. This would cause the WHERE clause to fail. Use the
*<CODE>DatSet.refetchRow()</CODE> method to get the original row from the server to determine if this is
*the case.
*
*<!-- JDS start - change JBuilder to JDataStore -->
*<P>JBuilder automatically recognizes read-only data such as calculated columns, and attempts to discover
*other non-updatable columns. Some JDBC drivers seem unaware that such columns are read-only.
*<!--The JDBC-ODBC
*bridge seems particularly affected by this.-->
*For this reason, you should set column properties to not
*resolvable. While this seems redundant, this  can prove crucial to your application.
*<!-- JDS end -->
*
*<P>Columns of certain data types and fields which are calculated on the server could cause the comparison
*in the WHERE clause of the update query to fail. Conditions which might cause this include:
*
*<UL>
*<LI>a <CODE>Column</CODE> is of imprecise data type (such as float or double)
*<LI>a <CODE>Column</CODE> contains String data that is of fixed length; some drivers do not pad strings
*with blanks, which leads  to failures in comparison
*<LI>a <CODE>Column</CODE> is calculated on the server, and successive calls to the <CODE>saveChanges</CODE>
*method are made without an intervening refresh.
*</UL>
*
*
*<P>Possible solutions to the above types of problems are:
*<UL>
*<LI>Set the <CODE>metaDataUpdate</CODE> property of the <CODE>QueryDataSet</CODE> to <CODE>MetaDataUpdate.NONE</CODE>
*and the <CODE>searchable</CODE> property of the column in question to false so that the column is not
*included in the WHERE clause of the update query. Note that you will have to set other properties as
*well to make the query updatable.
*<LI>Add a <CODE>QueryResolver</CODE> to your project and set the <CODE>updateMode</CODE> property of this
*component to <CODE>KEY_COLUMNS</CODE>.
*</UL>
*
*<P>If your server returns an error other than a constraint or integrity violation, check whether your
*driver supports prefixing field names with tables names, for example, testtable.column1. If not, set the
*<CODE>useTableName</CODE> property of the <CODE>Database</CODE> component to false.
*
*<P>In 1-1 relationships, the <CODE>QueryResolver</CODE> is able to resolve SQL queries that have more than one table reference. Metadata discovery detects which table each column belongs to and the default resolution order is set in
*the {@link com.borland.dx.dataset.StorageDataSet} <CODE>resolveOrder</CODE></A> of the <CODE>StorageDataSet</CODE>. When using 1-Many relationships, use a separate master detail <CODE>DataSet</CODE>. For Many-1 relationships, use lookups.
*
*<P>The <CODE>resolverQueryTimeout</CODE> property can help in situations where an application is trying to read a locked row.

 */
public class QueryResolver extends SQLResolver
{

  private static final boolean isValidUpdateMode(int updateMode) {
    return (updateMode >= UpdateMode.ALL_COLUMNS && updateMode <= UpdateMode.CHANGED_COLUMNS);
  }

  public QueryResolver() {
    updateModeProperty  = UpdateMode.ALL_COLUMNS;
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    if (this.database != database) {
      close();
      this.database = database;
    }
  }

  public void setUpdateMode(int updateMode) {
    if (!isValidUpdateMode(updateMode))
      updateMode  = updateModeProperty;

    if (!isValidUpdateMode(updateMode))
      updateMode  = UpdateMode.ALL_COLUMNS;

    this.updateModeProperty = updateMode;
  }

  public int getUpdateMode() {
    return updateModeProperty;
  }

  // Delegate to StateHolder:
  /**
   * Inserts the current row of the DataSet into the
   * Database specified in this resolver's database property.
   * @param dataSet DataSet
   */
  public void insertRow(DataSet dataSet) /*-throws DataSetException-*/ {
    getStateHolder(dataSet).insertRow(dataSet);
  }

  // Delegate to StateHolder:
  /**
   * Updates the current row of the specified DataSet in the Database.
   * @param dataSet DataSet
   * @param oldDataRo ReadWriteroww
   */
  public void updateRow(DataSet dataSet, ReadWriteRow oldDataRow) /*-throws DataSetException-*/ {
    getStateHolder(dataSet).updateRow(dataSet,oldDataRow);
  }

  // Delegate to StateHolder:
  /**
   * Deletes the current row in the DataSet from the Database.
   * @param dataSet dataSet
   */
  public void deleteRow(DataSet dataSet) /*-throws DataSetException-*/ {
    getStateHolder(dataSet).deleteRow(dataSet);
  }

  /**
   * Frees any system resources used for statements associated with the
   * specified StorageDataSet.
   * @param dataSet DataSet
   */
  public void closeStatements(StorageDataSet dataSet) /*-throws DataSetException-*/ {
    getStateHolder(dataSet).close();
  }

  /**
   * Frees any system resources such as Databases, statements,
   *  and so on that are associated with the specified StorageDataSet.
   * @param dataSet StroagedataSet
   */
  public synchronized void close(StorageDataSet dataSet) /*-throws DataSetException-*/ {
    if (cache != null)
      cache.close(dataSet);
    if (state0 != null && state0.getDataSet() == dataSet) {
      state0.close();
      state0 = null;
    }
  }

  private synchronized void close() {
    if (state0 != null) {
      state0.close();
      state0 = null;
    }
    if (cache != null)
      cache.close();
    cache = null;
  }

  private synchronized QueryResolverStateHolder getStateHolder(DataSet dataSet) {
    StorageDataSet sds = ProviderHelp.getResolverDataSet(dataSet);

    // Optimization: Is it in our current state:
    if (state0 != null && state0.getDataSet() == sds)
      return state0;
    if (cache == null) {
      if (state0 == null)
        return state0 = new QueryResolverStateHolder(this,sds);
      cache = new StateHolderCache();
      cache.put(state0.getDataSet(),state0);
    }

    // Otherwise look in our cache:
    state0 = (QueryResolverStateHolder)cache.get(sds);
    if (state0 == null) {
      state0 = new QueryResolverStateHolder(this,sds);
      cache.put(sds,state0);
    }
    return state0;
  }
    /**
     * The resolverQueryTimeout limit is the number of seconds the driver will
     * wait for a Resolver insert/update/delete Statement to execute. If the limit
     * is exceeded, a DataSetException is thrown.
     *
     * @return the current query timeout limit in seconds; zero means unlimited
     * @exception SQLException if a database-access error occurs.
     */
    public final int getResolverQueryTimeout()
      /*-throws DataSetException-*/
    {
      return resolverQueryTimeOut;
    }


    /**
     * The resolverQueryTimeout limit is the number of seconds the driver will
     * wait for a Resolver insert/update/delete Statement to execute. If the limit
     * is exceeded, a DataSetException is thrown.
     *
     * @param seconds the new query timeout limit in seconds; zero means unlimited
     * @exception SQLException if a database-access error occurs.
     */
    public final void setResolverQueryTimeout(int seconds)
      /*-throws DataSetException-*/
    {
      this.resolverQueryTimeOut = seconds;
    }

  private int                         resolverQueryTimeOut;
  private           int               updateModeProperty;
  private           Database          database;
  private transient StateHolderCache  cache;
  private transient QueryResolverStateHolder state0;
  private static final long serialVersionUID = 1L;
}

