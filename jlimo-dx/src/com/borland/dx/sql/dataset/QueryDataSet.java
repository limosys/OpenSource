//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryDataSet.java,v 7.0 2002/08/08 18:39:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;
//!import java.beans.Beans;

import java.sql.*;
import java.io.*;
import java.util.*;

//!JENS TODO.  JENS, need to add event that is fired inside a transaction
//! before a query is provided for.  Allows user to set up temp tables or
//! other optimizations before a query is performed.

/**
 * The <CODE>QueryDataSet</CODE> class is an extension of its superclass
 *  ({@link com.borland.dx.dataset.StorageDataSet}
 * <CODE>StorageDataSet</CODE></A>) and provides functionality
 * to run a query statement (with or without parameters) against
 * a table in a SQL database.

<P>In any application that uses the <CODE>QueryDataSet</CODE>, the
* following components are also required:
<UL>
<LI>an instantiated {@link com.borland.dx.sql.dataset.Database}
<CODE>Database</CODE></A> component to handle
the JDBC connection to the SQL database
<LI>an instantiated  {@link com.borland.dx.sql.dataset.QueryDescriptor}
<CODE>QueryDescriptor</CODE></A> object
to store the query properties
</UL>

<P>The data contained in a <CODE>QueryDataSet</CODE> is the result of the
* most recent query. The "result set"
from the execution of the query is stored in the <CODE>QueryDataSet</CODE>,
*  which allows for much greater
flexibility in navigation of the resulting data. You specify how the data is loaded into the <CODE>QueryDataSet</CODE>
(asynchronously, as needed, etc.) by specifying its
{@link com.borland.dx.sql.dataset.QueryDescriptor} <CODE>loadOption</CODE></A>
*  property (stored in the associated <CODE>QueryDescriptor</CODE>).
* The <CODE>QueryDataSet</CODE> inherits the {@link com.borland.dx.dataset.StorageDataSet} <CODE>maxRows</CODE></A> property which allows
you to set the maximum number of rows that can be initially stored in the <CODE>QueryDataSet</CODE> from a query
execution.

<P>Once the data is stored in the <CODE>QueryDataSet</CODE>, you manipulate it and connect it to UI controls in
exactly the same way as you would other <CODE>StorageDataSet</CODE> components, without regard to which component is storing the data.


<P>The <CODE>QueryDataSet</CODE> component uses the  {@link com.borland.dx.sql.dataset.QueryProvider}
* <CODE>QueryProvider</CODE></A> and {@link com.borland.dx.sql.dataset.QueryResolver}
* <CODE>QueryResolver</CODE></A> to perform the data providing and resolving functions.

<P>
<A NAME="updatingqueries"></A>
<H3>Updatable versus read-only queries</H3>



<P>By default, an attempt is automatically made to make a <CODE>QueryDataSet</CODE> updatable
* so that changes made to the data it contains can be resolved back to its data source.
* The query is analyzed and row
identifiers are looked for: one or more columns that uniquely identify each row. This is required to save any changed data
back to the correct row of the original data. If you have not included these columns in your query's SELECT
statement, they are automatically added but the visibilty of such columns  is set to "default". This
hides them from any data aware controls that this <CODE>QueryDataSet</CODE> is connected to. You may change the
column's visibility to <STRONG>true</STRONG> if desired.

<P>
<A NAME="metadatadiscovery"></A>
<H3>Suppressing metadata discovery</H3>


<P>To prevent the addition of row ID columns and various metadata related
properties on <CODE>DataSet</CODE>
and <CODE>Column</CODE> components, set this component's
*  <CODE>metaDataUpdate</CODE> property to
<CODE>MetaDataUpdate.NONE</CODE>.

<P>If the rowId analysis fails or <CODE>metaDataUpdate</CODE> is set to
*  <CODE>NONE</CODE>, you can make a <CODE>QueryDataSet</CODE> updatable by setting one or more
 of these properties (as applicable):
<UL>
<LI>Setting the <CODE>tableName</CODE> property of the
*  <CODE>StorageDataSet</CODE> to the table name that is the
data source of the <CODE>QueryDataSet</CODE>.
<LI>Identifying a set of columns that can uniquely identify a row,
for example, columns of a primary or
secondary index. You set this using the <CODE>rowID</CODE> property
*  of the <CODE>Column</CODE> components in the
<CODE>StorageDataSet</CODE>. These row identifier columns are included in the query and the corresponding
columns in the <CODE>QueryDataSet</CODE> are marked hidden by default.
<LI>Setting the <CODE>readOnly</CODE> property to <STRONG>false</STRONG> (if not already).
</UL>


<P>
<A NAME="performancetuning"></A>
<H3>Fine-tuning query performance</H3>

<P>To improve <CODE>QueryDataSet</CODE> performance on data retrieval,
<P>
<UL>
<LI>For queries that return a small <CODE>ResultSet</CODE>, disabling the  metadata discovery mechanisms for fetch operations can make a big performance improvement. Specifically, set the
        <UL>
        <LI><CODE>StorageDataSet.MetaDataUpdate</CODE> property to
        * 	<CODE>MetaDataUpdate.NONE</CODE>.
        <LI><CODE>StorageDataSet.TableName</CODE> property to the table name.
        <LI><CODE>Column.RowId</CODE> property for the columns that uniquely
        * and efficiently identify a row.
        </UL>
Note that the <CODE>QueryDataSet</CODE> only performs the metadata discovery operations the first time a query is run.
<P>
 <LI>Set the <CODE>LoadOption</CODE> property on the <CODE>QueryDataSet</CODE>
 * or <CODE>ProcedureDataSet</CODE> to
<CODE>Load.ASYNCHRONOUS</CODE> or <CODE>Load.AS_NEEDED</CODE>.  You can also
* set this property to <CODE>Load.UNCACHED</CODE> if you
 will be reading the data one time in sequential order.
<LI><P>For large result sets, using a <CODE>DataStore</CODE>
* can improve performance and save
a lot of memory with its caching/persistence support.
<LI><P>Statement caching.  By default, DataExpress will cache prepared statements for both queries and stored procedures
if <CODE>java.sql.Connection.getMetaData().getMaxStatements()</CODE> returns a value &gt; 10.  You can force statement caching
* by calling <CODE>Database.setCacheStatements(true)</CODE>.
*  The prepared statements that are cached are not closed until
*  one of the following happens:
        <UL>
        <LI>Some provider related property (for example, the <CODE>query</CODE> property) is
changed.
        <LI>A <CODE>DataSet</CODE> component is garbage collected (the statement is closed
          in a <CODE>finalize()</CODE> method, <CODE>QueryDataSet.closeStatement()</CODE>,
<CODE>ProcedureDataSet.closeStatement()</CODE>, <CODE>QueryProvider.closeStatement()</CODE> or
<CODE>ProcedureProvider.closeStatement()</CODE>.
        </UL>
</UL>

<P>To improve performance when performing data inserts, deletes, and updates:
<P>
<UL>
<LI>For updates and deletes, set the <CODE>resolver</CODE> property to a
<CODE>QueryResolver</CODE> and set the <CODE>updateMode</CODE> property of this <CODE>QueryResolver</CODE> to
<CODE>UpdateMode.KEY_COLUMNS</CODE>. This weakens the optimistic concurrency used, but reduces the
number of parameters set for an update/delete operation.
<LI><P>For each call to <CODE>Database.saveChanges()</CODE>, calls are made to
disable/enable a JDBC drivers autocommit mode.  If your application calls
<CODE>Database.saveChanges()</CODE> with the <CODE>useTransactions</CODE> parameter set to
false, then these calls will not be made and the transaction will not be
commited.
<LI><P>By disabling the <CODE>resetPendingStatus</CODE> flag in the
<CODE>Database.saveChanges()</CODE> method, further performance benefits can be achieved.  With this
disabled, DataExpress will not clear the <CODE>RowStatus</CODE> state for all
inserted/deleted/updated rows. This is only desirable if you will not be calling <CODE>saveChanges()</CODE>
with new edits on the <CODE>DataSet</CODE> without calling <CODE>refresh()</CODE> first.
<P>Note that if transactions are disabled, your application must call
<CODE>Database.commit()</CODE> or <CODE>Connection.commit()</CODE>.
</UL>


<A NAME="master-detail"></A>
<H3>Master-detail relationships</H3>
<!

<P>In a master-detail relationship, if you set the
{@link com.borland.dx.dataset.MasterLinkDescriptor}
<CODE>fetchAsNeeded</CODE></A> property  to <STRONG>true</STRONG>,
* you must include a WHERE clause in the detail query that matches
* the detail link column values to the master link column values.

<!-- JDS start - remove second sentence -->
<A NAME="aliases"></A>
<H3>Alias support and column name conflicts</H3>
<!
<P>Column aliases that are specified in a SQL Select statement are supported.
The name specified as the alias is stored in the <CODE>columnName</CODE> property
* and is used to access the DataExpress API. The original data source name is stored
*  in the <CODE>serverColumnName</CODE> property for use when resolving changes back
* to the data source.
<P>When a query joins two or more tables, it may return several columns that
have the same name (often the columns used to link the tables have the same name).  Duplicate column names are handled by appending a number ("EMP_NO", "EMP_No1", etc.). The modified column name is stored in the <CODE>columnName</CODE> property and is used to access the DataExpress API. The original server name is stored in the <CODE>serverColumnName</CODE> property for (later) use when resolving data changes back to the data source.
<P>These properties are also used for column aliases that are specified in SQL statements.


<P><CODE>Table aliases</CODE> are also supported.
For example, in the following query:
<P><PRE><CODE>select e.emp_no, e.last_name  empl_last,  p.last_name,  phone_last
        from employee e, phone_list p
        where e.emp_no = p.emp_no and
        e.last_name &lt;&gt; p.last_name
</CODE></PRE>
The employee table is assigned the alias "e" and its last_name column is given the alias "empl_last".  In multi-table queries, aliases can be  useful.


<P>
<A NAME="oraclesynonyms"></A>
<H3>Oracle synonyms</H3>

<P>If a query is run against a synonym on an Oracle server, it is dependent on the support of synonyms in the JDBC driver to determine whether the query is updatable.


<A NAME="sql_views"></A>
<H3>SQL views</H3>

<P>Queries run against SQL views are supported, however, they may not be resolvable depending on what actions the  SQL view performed. For example, if the view simply filters out rows, the server may be able to handle resolving the edits. You should be aware that you risk making edits that cannot later be resolved back to the data source. More likely, you will need to write your own resolver logic to handle this situation.
 */

public class QueryDataSet extends StorageDataSet
{
  public final void setQuery(QueryDescriptor queryDescriptor)
    /*-throws DataSetException-*/
  {
    ProviderHelp.failIfOpen(this);
//!/*
//!    //!RC TODO leave in a hook to test exception handling from Inspector
//!    //!RC remove before ship!!!
//!    if (true || Diagnosing.on) {
//!      if (queryDescriptor != null &&
//!          queryDescriptor.getQueryString() != null &&
//!          queryDescriptor.getQueryString().startsWith("failJB"))
//!        DataSetException.badQueryProperties();
//!    }
//!*/
    this.queryDescriptor = queryDescriptor;
    ProviderHelp.setProviderPropertyChanged(this, true);
    if (currentProvider == null) {
      setQueryProvider(new QueryProvider());
      currentProvider.setAccumulateResults(accumulateResults);
    }
    currentProvider.setQuery(queryDescriptor);
  }

  public final QueryDescriptor getQuery() {
    return queryDescriptor;
  }

  /*<---------->*

   * For simple QueryDataSets:
   *
   * This method is used to execute the query and fetch the results.
   *
   * For master/detail QueryDataSets:
   *
   * There are two methods of using a QueryDataSet to provide the detail rows in a master/detail
   * relationship: delayed fetching and immediate fetching.
   *
   * Immediate fetching means that a single query is used to fetch the entire detail set in
   * one shot. After initial execution of the query and fetching of the results, the two
   * datasets linked by the MasterLinkDescriptor do all linkage based on the cached data.
   *
   * Delayed fetching is a less consistent view of the data. Each time a master row is visited
   * for the first time, the detail data will be fetched by re-executing the detail query. The
   * detail query must contain a WHERE clause with linkage to the master dataset via named
   * parameters. The detail QueryDataSet method loadDetailRows will be invoked each time a
   * master row is first encountered. This method will extract the link columns (as specified
   * in the MasterLinkDescriptor) data values and bind them to the query. Then the query
   * will be executed and the results of that query will be added to any already cached
   * rows in the detail DataSet. There must be a one-to-one match of the master DataSet
   * columns names to the named parameters in the WHERE clause of the detail QueryDataSet.
   * This is how the parameters are bound in the correct order. For example,
   *
   * Master table: Master
   * Contains fields: Field1, Field2, Field3
   *
   * Detail table: Detail
   * Contains fields: Column1, Column2, Column3
   *
   * Relationship is Master.Field1 to Detail.Column2
   *
   * Detail query: SELECT * FROM DETAIL WHERE Column2 = :Field1
   *
  */

  /**
   *
   * @param sds StorageDataSet
   *
   */
  public final String getQueryString(StorageDataSet sds) {
    if (currentProvider != null)
      return currentProvider.getQueryString(this);
    else
      return null;
  }

  /**
      If Database.isUseStatementCaching() returns true, JDBC statements
      can be cached.  By default these statements will be closed during
      garbage collection.  If resources are scarce, the statment can be
      forced closed by calling this method.
  */
  public void closeStatement()
    /*-throws DataSetException-*/
  {
    if (currentProvider != null)
      currentProvider.closeStatement();
  }

  public final String getOriginalQueryString() { return queryDescriptor != null ? queryDescriptor.getQueryString() : null; }
  public final Database getDatabase() { return queryDescriptor != null ? queryDescriptor.getDatabase() : null; }

  public ReadWriteRow getParameterRow() {
    if (queryDescriptor != null)
      return queryDescriptor.getParameterRow();
    else
      return null;
  }

  /**
   * Returns state of accumulateResults property.
   */
  public final boolean isAccumulateResults() { return accumulateResults; }

  /**
   * If this property is enabled, provided data will be accumulated over consecutive calls to
   * executeQuery. If the property is disabled, subsequent executeQuery calls will overwrite
   * the existing dataset.
   */
  public final void setAccumulateResults(boolean accumulate) {
    accumulateResults = accumulate;
    //propertiesChanged = true;
    ProviderHelp.setProviderPropertyChanged(this, true);
    if (currentProvider != null)
      currentProvider.setAccumulateResults(accumulate);
  }

  /**
   * Given that the database and query properties have been set, executes
   * the query and populates the dataset.
   */
  public final void executeQuery() /*-throws DataSetException-*/ {
    refresh();
  }

  /**
   * Given that the database and query properties have been set,
   * executes the query and populates the DataSet.
   */
  public void refresh() /*-throws DataSetException-*/ {
    if (currentProvider == null)
      DataSetException.badQueryProperties();
    super.refresh();
  }

  /**
   * Returns true. Used internally by data-aware controls to determine if a saveChanges()
   * type operation is supported.
   *
   */
  public boolean saveChangesSupported() { return true; }
  /**
   */public boolean refreshSupported() { return true; }

  /**
   * Calls Database.saveChanges() with the DataSet object specified in its parameter.
   * @param dataSet DataSet
   */
  public  void saveChanges(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    Resolver resolver = getResolver();
    if (resolver == null) {
      Database database = getDatabase();
      if (database != null && isOpen()) {
        closeProvider(false);
        database.saveChanges(dataSet);
      }
    }
    else {
      super.saveChanges(dataSet);
    }
  }

  String getRefetchRowQuery()
    /*-throws DataSetException-*/
  {
    String sql = getQueryString(this);
    RefetchQuery query = new RefetchQuery(getDatabase(), sql, 0, JdbcProvider.initCoercer(this));
    return query.makeQueryString(this);
  }

  // Returns a "current" version of a row.
  //
  /**
   * Fetches the original row from the data source based on the key field of the
   * row you pass in. For example, if the key field of the row is "foobar", this
   * method fetches the row in the DataSet with that key field.
   * @param row ReadWriteRow
   */
  public void refetchRow(ReadWriteRow row)
    /*-throws DataSetException-*/
  {
    if (!isOpen())
      DataSetException.dataSetNotOpen();
    try {
      String sql = getQueryString(this);
      RefetchQuery query = new RefetchQuery(getDatabase(), sql, 0, JdbcProvider.initCoercer(this));
      query.setParameters(this,row);
      query.executeQuery(row);
    }
    catch (SQLException se) {
      DataSetException.throwException(DataSetException.SQL_ERROR, se);
    }
  }

  // Only accept a QueryProvider
  //
  public void setProvider(Provider provider) /*-throws DataSetException-*/ {
    if (provider != null && !(provider instanceof QueryProvider))
      DataSetException.needQueryProvider();
//!    failIfOpen();

    setQueryProvider((QueryProvider)provider);
    if (currentProvider != null) {
      queryDescriptor   = currentProvider.getQuery();
      accumulateResults = currentProvider.isAccumulateResults();
    }
  }

  private void setQueryProvider(QueryProvider provider) /*-throws DataSetException-*/ {
    super.setProvider(provider);
    currentProvider = (QueryProvider)provider;
  }

  private           QueryDescriptor queryDescriptor;
  private           boolean         accumulateResults;
  private transient String          generatedQuery;
  private transient QueryProvider   currentProvider;     // current provider auto generated or not
  private static    final long      serialVersionUID = 1L;
//!  private boolean         propertiesChanged;
}
