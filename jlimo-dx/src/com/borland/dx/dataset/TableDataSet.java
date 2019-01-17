//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/TableDataSet.java,v 7.1 2003/01/30 21:58:29 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import java.io.IOException;
import java.util.Locale;
//!import java.beans.Beans;

 /**
  *
The <CODE>TableDataSet</CODE> is a simple <CODE>DataSet</CODE> that may or may not have a formal provider or resolver of its data. Its properties allow it to import file-based data. Use this component to create a <CODE>StorageDataSet</CODE> from sources other than SQL databases, for example, by importing data stored in a text file,  from data computations, or to simply work with database data off-line.  You can also use this component, or any other component that extends from <CODE>StorageDataSet</CODE>, to directly access tables stored in a {@link com.borland.datastore.DataStore DataStore} database file.

<P>You attach this component to any UI control in the same way that other subclasses of <CODE>StorageDataSet</CODE> connect to a UI control. Similarly, setting its <CODE>sort</CODE> property affects the sort order of the data in this component, and its {@link com.borland.dx.dataset.DataSet#addRowFilterListener rowFilterListener} filters as it would any other <CODE>StorageDataSet</CODE>.  It thereby mimics single-user SQL server functionality although no database connection is involved.


<P>With file-based data sources, such as a <CODE>TextDataFile</CODE>, the providing phase occurs in a single fetch.
Similarly, if a <CODE>TableDataSet</CODE> is the detail of a master-detail relationship, setting its {@link com.borland.dx.dataset.MasterLinkDescriptor#setFetchAsNeeded fetchAsNeeded} property has no effect as all detail records are read from a single fetch operation when file-based data sources are involved.

<P>The resolving phase is the most simple of all resolvers: the data saved to the file overwrites the existing data. To save the data in any <CODE>StorageDataSet</CODE> to a text file, set the {@link com.borland.dx.dataset.TextDataFile TextDataFile} component's {@link com.borland.dx.dataset.TextDataFile#setFileName fileName} property to the export file prior to calling the <CODE>StorageDataSet</CODE> component's <CODE>save()</CODE> method.



<A NAME="savingtxt2jdbc"></A>
<H3>Saving data in a <CODE>TextDataFile</CODE> to a JDBC data source</H3>

<P>By default, data loaded into a <CODE>TableDataSet</CODE> using a <CODE>TextDataFile</CODE> is loaded with a {@link com.borland.dx.dataset.RowStatus.html#LOADED RowStatus.LOADED}
status.  Calling the <CODE>saveChanges(...)</CODE> method on a <CODE>QueryDataSet</CODE> or <CODE>ProcedureDataSet</CODE> has no effect because these rows are not considered as being  inserted.  Setting the <CODE>TextDataFile</CODE> property {@link com.borland.dx.dataset.TextDataFile#setLoadAsInserted setLoadAsInserted(true)} causes all rows loaded from the <CODE>TextDataFile</CODE> to be  <CODE>RowStatus.INSERTED</CODE>. A subsequent call to  <CODE>saveChanges(...)</CODE> with the <CODE>resolver</CODE> property set to a <CODE>QueryResolver</CODE> or <CODE>ProcedureResolver</CODE> will insert the rows into the JDBC data source for the <CODE>Resolver</CODE>.



<P><BSCAN><STRONG>Note: </STRONG></BSCAN>This component will not delete existing rows if a <CODE>DataSet</CODE> already contains data.

  *
  * Extends StorageDataSet. A simple DataSet with no formal provider
  * or resolver of its data. Used to create a StorageDataSet from sources
  * other than SQL databases.
  */
public class TableDataSet extends StorageDataSet
{

/**
 * Instantiates a TableDataSet class object with default properties.
 */
  public TableDataSet() {
    super();
  }
}
