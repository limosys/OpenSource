//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MasterLinkDescriptor.java,v 7.1 2003/01/30 21:58:29 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

 /**
  * Stores properties that establish a master-detail relationship
  * between two DataSets. You can link different DataSets together,
  * such as a QueryDataSet and a TableDataSet, if there is common data
  * to base the link relationship on.
  *
  * The <CODE>MasterLinkDescriptor</CODE> object stores properties that set a master-detail relationship
between two {@link com.borland.dx.dataset.DataSet DataSet} objects such as
<CODE>QueryDataSet</CODE>, <CODE>ProcedureDataSet</CODE>, <CODE>TableDataSet</CODE> or <CODE>DataSetView</CODE>.
You can link different <CODE>DataSet</CODE> objects together, for example, a <CODE>QueryDataSet</CODE> and a
<CODE>TableDataSet</CODE> as long as there is common data to base the link relationship on.
Indexes for the linking relationship are created as needed, quickly and efficiently.

<A NAME="Detailfetch"></A>
<H3>Mechanisms for fetching detail data</H3>
<P>
<!--BNDX="fetching detail records"-->
<!--BNDX="detail tables:getting values from"-->
<!--BNDX="master-detail relationships:fetching details"-->
There are two methods of using a <CODE>StorageDataSet</CODE> to provide the detail rows in a master-detail relationship:
<UL>
<LI><STRONG>immediate fetching</STRONG>: where a single query is used to fetch the entire detail set in one batch. After the initial execution of the query and fetching of the results, the two <CODE>DataSets</CODE> are linked based on the cached data, using settings stored in the <CODE>MasterLinkDescriptor</CODE>.

<LI><P><STRONG>delayed fetching</STRONG>:  is a less consistent view of the data. Each time a master row is visited for the first time, its detail data is fetched by re-executing the detail query. The detail query must contain a WHERE clause with links to the master <CODE>DataSet</CODE> using named parameters. The detail <CODE>DataSet</CODE>'s <CODE>loadRow()</CODE> method is invoked each time a master row is first encountered. This method extracts the link columns  data values from the master (as specified in the <CODE>MasterLinkDescriptor</CODE>) and binds them to the query. Then the query executes and the results of that query added to any already cached rows in the detail <CODE>DataSet</CODE>. There must be a one-to-one match of the master <CODE>DataSet</CODE> columns names to the named parameters in the WHERE clause of the detail <CODE>QueryDataSet</CODE>.

<P>When specifying the query statement for the detail <CODE>DataSet</CODE>, you should specify the linking columns in a  <CODE>where</CODE> clause if <CODE>fetchAsNeeded</CODE> is <STRONG>true</STRONG>. If the <CODE>fetchAsNeeded </CODE> setting is not compatible with the detail query, you will get a <CODE>DataSetException</CODE> and anomalous results.



<P>For example, to bind parameters in the correct order where
<UL>
<LI>Master table ("Master") contains fields: MasterColumn1, MasterColumn2, MasterColumn3
<LI>Detail table ("Detail") contains fields: DetailColumn1, DetailColumn2, DetailColumn3
<LI>Relationship is Master.MasterColumn1 to Detail.DetailColumn2
</UL>
<P>The correct query statement for the detail query is:
<PRE><CODE>SELECT * FROM DETAIL WHERE DetailColumn2 = :MasterColumn1</CODE></PRE>

</UL>

<A NAME="pgm"></A>
<H3>Programmatically calling the <CODE>MasterLinkDescriptor</CODE></H3>
<P>To work with the <CODE>MasterLinkDescriptor</CODE> class programmatically, you set its properties when instantiating the
<CODE>MasterLinkDescriptor</CODE> object. There are no write accessors for this class.
The properties of the <CODE>MasterLinkDescriptor</CODE> object are:

<UL>
<LI><CODE>fetchAsNeeded</CODE>
<LI><CODE>detailLinkColumns</CODE>
<LI><CODE>masterDataSet</CODE>
<LI><CODE>masterLinkColumns</CODE>
<LI><CODE>cascadeDeletes</CODE>
<LI><CODE>cascadeUpdates</CODE>
</UL>


<!-- JDS start - remove paragraphs -->
<P>These properties can be accessed through the JBuilder user interface when inspecting the
<CODE>masterLink</CODE> property of a <CODE>DataSetView</CODE>, <CODE>QueryDataSet</CODE>, <CODE>ProcedureDataSet</CODE>,
or <CODE>TableDataSet</CODE>. These properties are required on the detail <CODE>DataSet</CODE> only when
setting up a master-detail relationship.

<P>The MasterLink  property editor displays when you
double-click the <CODE>masterLink</CODE> property of the detail <CODE>DataSet</CODE> in the JBuilder Inspector, then click the ellipses button. Set the properties that define the master-detail relationship
using this dialog. This dialog  also includes a Test Link button that tests the <CODE>masterLink</CODE> property
settings. Status messages appear in the gray area to the right of the Test Link button.
<!-- JDS end -->

<A NAME="detailquery"></A>
<H3>fetchAsNeeded for detail DataSets</H3>
<P>
<!--BNDX="queries:fetching detail data"-->
When specifying the query statement for the detail <CODE>DataSet</CODE>, you should specify the linking columns in a  <CODE>where</CODE> clause if <CODE>fetchAsNeeded</CODE> is <STRONG>true</STRONG>. If the <CODE>fetchAsNeeded </CODE> setting is not compatible with the detail query, you will get a <CODE>DataSetException</CODE> and anomalous results.

<!--*********************************-->
<A NAME="editingeffects"></A>
<H3>How editing operations affect multiple rows of a data set</H3>
<!--BNDX="fetchAsNeeded:editing operations;queries:editing detail queries;detail queries;editing"-->
<P>
This section discusses how editing operations that affect multiple rows of a data set work on detail data sets.  Only refresh() varies
according to how the <CODE>fetchAsNeeded</CODE> property is set.
<P>
<UL>
<LI>The <CODE>saveChanges()</CODE> method (and the Save button on a <CODE>JdbNavToolBar</CODE>) saves changes to all details sets for all masters,
     whether <CODE>fetchAsNeeded</CODE> is <STRONG>true</STRONG> or <STRONG>false</STRONG>.
<LI><P>The <CODE>refresh()</CODE> method (and the Refresh button on a  <CODE>JdbNavToolBar</CODE>) performs differently according to <CODE>fetchAsNeeded</CODE>.  If     <CODE>fetchAsNeeded</CODE> is <STRONG>false</STRONG>, it refreshes all detail sets; if <STRONG>true</STRONG>, it refreshes the details for the current master row only. This is consistent with the fact that detail sets are fetched at different times when <CODE>fetchAsNeeded</CODE> is <STRONG>true</STRONG>, or are never fetched if their masters are never visited, so they are also refreshed  independently.
<LI><P>The <CODE>empty()</CODE> method deletes all details for all master rows, whether      <CODE>fetchAsNeeded</CODE> is  <STRONG>true</STRONG> or <STRONG>false</STRONG>.
<LI><P>The <CODE>emptyAllRows()</CODE> method deletes all details for the current  master row only, whether <CODE>fetchAsNeeded</CODE> is <STRONG>true</STRONG> or <STRONG>false</STRONG>.  This is because
     <CODE>emptyAllRows()</CODE> affects visible rows only, and only one detail set is visible at a time.
</UL>

<A NAME="selfjoins"></A>
<H3>Self-joins</H3>
<P>
<!--BNDX="self-joins"-->
Self-joins (where a <CODE>DataSet</CODE> is linked to itself) are not supported.

<A NAME="cascadeupdatesdeletes"></A>
<H3>Cascading updates and deletes</H3>

<P>
<!--BNDX="cascading deletes;cascading updates"-->
To cascade updates and deletes, set the <CODE>cascadeUpdates</CODE> and <CODE>cascadeDeletes</CODE> properties
as appropriate. These properties should be used with care, especially in cases where you have multiple master-detail relationships chained where a detail <CODE>DataSet</CODE>
is a master to another detail (and so on). Typically in those circumstances, you want the updates to be reflected through the chain of related <CODE>DataSets</CODE>. For this to be possible, you must set the
<CODE>cascadeUpdates</CODE> and <CODE>cascadeDeletes</CODE> properties  at each level.  Otherwise, the cascading stops at the <CODE>DataSet</CODE> whose  <CODE>cascade</CODE> property is <STRONG>false</STRONG>.

<P>Partial updates or deletions (orphan records) can also occur in cases  where a <CODE>DataSet</CODE> somewhere in the master-detail chain is not updateable. Program logic may also cause this property to effect only partial updates. For instance, an event handler for the <CODE>editListener</CODE>'s deleting event might allow deletion of some detail rows and block deletion of others.  In the case of cascaded updates, you may end up with orphan details if some rows in a detail set can be updated and others can't.

<A NAME="displayingalldetails"></A>
<H3>Displaying all detail data when a master-detail relationship is in effect</H3>
<P>In a master-detail relationship, visible detail rows include only those which match the current master. To see all detail rows, instantiate a {@link com.borland.dx.dataset.DataSetView DataSetView} component using the detail <CODE>DataSet</CODE> as the data source. Navigation through the data in the <CODE>DataSetView</CODE> is also independent of that of the detail <CODE>DataSet</CODE>. To turn the master-detail relationship off, you
can also call <A {@link com.borland.dx.dataset.DataSet#setMasterLink setMasterLink(null)}.



<A NAME="emptymaster"></A>
<H3>Empty master <CODE>DataSet</CODE></H3>

<P>
<!--BNDX="master-detail relationships:setting up persistent columns for"-->
<!--BNDX="persistent columns:setting up for master-detail relationships"-->
In establishing a master-detail relationship using <CODE>fetchAsNeeded</CODE>, the master <CODE>DataSet</CODE> columns are used in order to run the query that obtains the data for the detail DataSet. If there are no rows in the master <CODE>DataSet</CODE>, the detail query cannot be run.

<P>An application can ensure that the detail query is always executable by setting up persistent columns for the master and detail data sets which include all respective linking columns.

<P>This is especially important for a <CODE>QueryDataSet</CODE> that has a <CODE>MasterLinkDescriptor</CODE> with the <CODE>fetchAsNeeded</CODE> property set to true.  In this case, DataExpress cannot determine what columns are present in the detail because its query cannot be run without parameter values from the current row of the master. Setting persistent columns for the detail  linking columns will allow the detail data set to be opened.


<!-- JDS start - remove section -->
<A NAME="examples"></A>
<H3>Examples</H3>
<!--BNDX="applications:sample. See sample applications"-->
<!--BNDX="sample applications:master-detail links"-->
<!--BNDX="examples. See sample applications"-->
The /samples/OnlineStore application demonstrates an example where  <CODE>delayedDetailFetch</CODE> is enabled.  The <CODE>jbInit()</CODE> method in the <CODE>DataModule1.java</CODE> file shows the following source code:

<PRE>
custOrderDataSet.setMasterLink(new com.borland.dx.dataset.MasterLinkDescriptor(customerDataSet, new String[]{"ID"}, new String[] {"CUSTOMERID"}, true));
custOrderDataSet.setReadOnly(true);
custOrderDataSet.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, "SELECT ID, CUSTOMERID, ORDERDATE, STATUS, SHIPDATE FROM ORDERS WHERE CUSTOMERID = :ID", null, true, Load.ALL));
</PRE>


See the source code for the project directly at the above location of your JBuilder installation.


<P>For additional information on how to create a master-detail relationship and an example, see
<A "Establishing a master-detail relationship"
in the <CITE>Database Application Developer's Guide</CITE>.
<!-- JDS end -->


  */
public class MasterLinkDescriptor implements java.io.Serializable {

  /**
   * Constructs a MasterLinkDescriptor with properties as specified in its parameters.
   *
   * @param masterDataSet         The DataSet which is the master of the master-detail
   *                              relationship.
   * @param masterLinkColumns     An array of String column names from the masterDataSet
   *                              to use when linking.
   * @param detailLinkColumns     An array of String column names from the detail
   *                              DataSet to use when linking.
   */
  public MasterLinkDescriptor(  DataSet  masterDataSet,
                                String[] masterLinkColumns,
                                String[] detailLinkColumns)
  {
    this.masterDataSet      = masterDataSet;
    this.masterLinkColumns  = masterLinkColumns;
    this.detailLinkColumns  = detailLinkColumns;
    fetchAsNeeded      = true;
  }

  /**
  * Constructor for MasterLinkDescriptor
  *
  * @param masterDataSet The DataSet which is the master of the master/detail relationship
  *
  * @param masterLinkColumns An array of column names to use from master
  *
  * @param masterLinkColumns An array of column names to use from detail
  *
  * @param fetchAsNeeded  If true, causes details to be fetched as the master dataset
  * navigates.  If Query or Procedure DataSets are being used, the detail query will be
  * reexectuted as the master is navigated.
  */

  /**
   *  Constructs a MasterLinkDescriptor with properties as specified in its parameters.
   * @param masterDataSet         The DataSet which is the master of the master-detail
   *                              relationship.
   * @param masterLinkColumns     An array of String column names from the masterDataSet
   *                              to use when linking.
   * @param detailLinkColumns     An array of String column names from the detail DataSet
   *                              to use when linking.
   * @param fetchAsNeeded         A boolean value which determines whether detail data is
   *                              fetched all at once or fetched when a master is navigated
   *                              to a row whose details have not yet been fetched.
   *                              For more information on this property, see fetchAsNeeded.
   */
  public MasterLinkDescriptor(DataSet  masterDataSet,
                                String[] masterLinkColumns,
                                String[] detailLinkColumns,
                                boolean  fetchAsNeeded)
  {
    this.masterDataSet      = masterDataSet;
    this.masterLinkColumns  = masterLinkColumns;
    this.detailLinkColumns  = detailLinkColumns;
    this.fetchAsNeeded = fetchAsNeeded;
  }

  /**
   * Constructs a MasterLinkDescriptor with properties as specified in its parameters.
   *
   * @param masterDataSet       The DataSet which is the master of the master/detail
   *                            relationship.
   * @param masterLinkColumns   An array of column names to use from master the master
   *                            DataSet.
   * @param detailLinkColumns   An array of column names to use from the detail DataSet.
   * @param fetchAsNeeded       If true, causes details to be fetched as the master DataSet
   *                            navigates. If the QueryDataSet or ProcedureDataSet components
   *                            are being used, the detail query will be re-executed as the
   *                            master is navigated.
   * @param cascadeUpdates      If true, updates to linking columns in the master DataSet are
   *                            also cascaded to applicable rows in the detail DataSet.
   *                            If false, updates to linking columns are not allowed if the
   *                            master has details: they would be orphaned by this operation.
   * @param cascadeDeletes      If true, matching detail rows are deleted when the corresponding
   *                            master DataSet row is deleted. If false, master rows that have
   *                            details cannot be deleted.
   */
  public MasterLinkDescriptor(DataSet  masterDataSet,
                                String[] masterLinkColumns,
                                String[] detailLinkColumns,
                                boolean  fetchAsNeeded,
                                boolean  cascadeUpdates,
                                boolean  cascadeDeletes
                             )
  {
    this.masterDataSet      = masterDataSet;
    this.masterLinkColumns  = masterLinkColumns;
    this.detailLinkColumns  = detailLinkColumns;
    this.fetchAsNeeded      = fetchAsNeeded;
    this.cascadeUpdates     = cascadeUpdates;
    this.cascadeDeletes     = cascadeDeletes;
  }

  // NOTE:  compares everything "but" cascadeUpdates and cascadeDelete.
  //
  final boolean linkEquals(MasterLinkDescriptor descriptor) {
    if (descriptor == this)
      return true;

    if (fetchAsNeeded != descriptor.fetchAsNeeded)
      return false;

    if (!SortDescriptor.equalsIgnoreCase(masterLinkColumns, descriptor.masterLinkColumns))
      return false;

    if (!SortDescriptor.equalsIgnoreCase(detailLinkColumns, descriptor.detailLinkColumns))
      return false;

    return true;
  }

  public DataSet  getMasterDataSet() { return masterDataSet; }
  public String[] getMasterLinkColumns() { return masterLinkColumns; }

  /**
   * Read-only property that returns a String array containing the names of
   * the column or columns of the detail DataSet that are used to link to the
   * MasterDataSet. An index on the link columns of the detail DataSet is not
   * required. The names of the link columns between the master and detail data
   * sets do not need to match as long as the number and data types of linking
   * columns do match.
   * <p>
   * Set this property when creating the MasterLinkDescriptor object by calling a
   * MasterLinkDescriptor constructor that takes this property as a parameter.
   *
   * @return
   */
  public String[] getDetailLinkColumns() { return detailLinkColumns; }

  public boolean  isFetchAsNeeded() { return fetchAsNeeded; }

  /**
   * Read-only property that returns whether updates to linking columns in the
   * master DataSet are also applied to rows in detail DataSets. If true,
   * updates are cascaded to details; if false, updates to linking columns are
   * not allowed if the master has details: the details would be orphaned by
   * this operation. This property defaults to false meaning that updates are not cascaded.
   *
   * @return
   */
  public boolean  isCascadeUpdates() { return cascadeUpdates; }

  /**
   *  Read-only property that returns whether matching detail (DataSet) rows
   *  should be deleted when the corresponding row in the master DataSet is deleted.
   *  If <b>true</b>, deletes are also applied to matching details; if <b>false</b>, master rows
   *  that have details cannot be deleted. This property defaults to <b>false</b> meaning
   *  that deletes are not cascaded.
   * @return
   */
  public boolean  isCascadeDeletes() { return cascadeDeletes; }

  private DataSet  masterDataSet;
  private String[] masterLinkColumns;
  private String[] detailLinkColumns;
  private boolean  fetchAsNeeded;
  private boolean  cascadeUpdates;
  private boolean  cascadeDeletes;
  private static final long serialVersionUID = 1L;
}
