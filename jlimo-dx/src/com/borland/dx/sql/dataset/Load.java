//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/Load.java,v 7.0 2002/08/08 18:39:52 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

/**
  @since JB2.0
  LoadOption is used to control how the data is loaded into a dataset.
  The option appears in the QueryDescriptor and ProcedureDescriptor.
*/

public interface Load
{
/**
ALL: All data is loaded once.
The JDBC ResultSet is closed after use.
Note that if the MaxRows property of the dataSet is set, then only that
number of rows are loaded, no other records in the ResultSet will ever be loaded.
*/
  public static final int ALL        =  0;

/**
ASYNCHRONOUS: Asynchronous load.
A new thread is created for executing the query and fetching the results.
This can yield better performance. However, running the query in a separate
thread could introduce the possibility of dead-lock.
*/
  public static final int ASYNCHRONOUS  =  1;

/**
 *An initial number of rows is loaded. Then, whenever a navigation beyond the last
 *loaded row is attempted, another set of rows are loaded. Enlarging a JdbTable or
 *  reducing the height of its rows so that there is additional space to display data does not cause additional rows to be loaded nor will moving to the last row of the grid using the scrollbar or the PgUp or PgDown keys. Through the UI, the following actions cause an additional set of rows to be added: cursor movement down from the last row
 *clicking the JdbNavToolBar's Next button when positioned on the last row
 * clicking the JdbNavToolBar's Last button
 * Similarly, the following have the same effect programmatically
 *DataSet.next() when positioned on the last row
 *DataSet.last()
 *DataSet.goToRow(int) past the last row
 *The number of rows loaded at a time is controlled by the (StorageDataSet's) maxRows property at runtime and maxDesignRows property in the UI Designer. If the maxRows property is not set (its default value is -1) when using this constant, 25 rows are loaded at a time.
 *When using this constant, a call to getRowCount() returns the number of rows loaded; it doesn't return the number of rows in the ResultSet. Similarly, locates perform the search on loaded rows only.
 *The JDBC ResultSet is kept open until all the data is loaded. There is no notification that there are additional rows to be loaded
*/
  public static final int AS_NEEDED      =  2;



  /**
   * Initially, one row is loaded. Whenever a navigation beyond the loaded row is
   * attempted, another row is loaded that replaces the previously loaded row. The DataSet will keep changes as normal. The JDBC ResultSet is kept open until the last record is read. The description for AS_NEEDED applies to this constant as well, except that the number of rows loaded in this case,
   * is one.
   */
  public static final int UNCACHED         =  4;

}
