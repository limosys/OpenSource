//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ProcedureDataSet.java,v 7.0 2002/08/08 18:39:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.sql.SQLException;

import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Provider;
import com.borland.dx.dataset.ProviderHelp;
import com.borland.dx.dataset.ReadWriteRow;
import com.borland.dx.dataset.Resolver;
import com.borland.dx.dataset.StorageDataSet;

//!TODO.  Need to add event that is fired inside a transaction
//! before a query is provided for.  Allows user to set up temp tables or
//! other optimizations before a query is performed.

/**
 * ProcedureDataSet used to retrieve and edit data from JDBC Stored Procedure data sources. The <CODE>ProcedureDataSet</CODE> class is an extension of its
 * superclass {@link com.borland.dx.dataset.StorageDataSet}<CODE>StorageDataSet</CODE></A>) and provides functionality to run a stored procedure against data
 * stored in a SQL database, passing in parameters if the procedure expects them. The procedure call is expected to return a cursor.
 *
 * <!--The procedure call is expected to return a cursor, or in the case of an InterBase select procedure, output that can be used to generate a
 * <CODE>DataSet</CODE>. -->
 *
 * <P>
 * In any application that uses the <CODE>ProcedureDataSet</CODE>, the following components are also required:
 * <UL>
 * <LI>an instantiated {@link com.borland.dx.sql.dataset.Database} <CODE>Database</CODE></A> component to handle the JDBC connection to the SQL database
 * <LI>an instantiated {@link com.borland.dx.sql.dataset.ProcedureDescriptor} <CODE>ProcedureDescriptor</CODE></A> object to store the stored procedure's
 * properties
 * </UL>
 *
 *
 * <P>
 * The data contained in a <CODE>ProcedureDataSet</CODE> is the result of the most recent execution of the stored procedure. Storing the "result set" from the
 * execution of the stored procedure in a
 *
 * <CODE>ProcedureDataSet</CODE> allows for greater flexibility in navigation of the resulting data.
 *
 * <P>
 * The <CODE>ProcedureDataSet</CODE> inherits the {@link com.borland.dx.dataset.StorageDataSet} <CODE>maxRows</CODE></A> property which allows you to set the
 * maximum number of rows stored initially in the <CODE>
* ProcedureDataSet</CODE> as a result of the stored procedure execution.
 *
 * <P>
 * Once the data is stored in the <CODE>ProcedureDataSet</CODE>, you manipulate it and connect it to UI controls in exactly the same way as you would other
 * <CODE>StorageDataSet</CODE> components, without regard to which component is storing the data.
 *
 * <P>
 * This class is used with servers whose JDBC driver supports executing stored procedures that generate a result set. Not all JDBC drivers support this; some
 * vendor libraries require special API calls to invoke stored procedures. Refer to your server documentation for more information on whether it meets this
 * requirement.
 *
 * <!-- JDS start - remove paragraph -->
 * <P>
 * For an example on executing stored procedures, see "Obtaining data through a stored procedure" in the <CITE>Database Application Developer's Guide</CITE>.
 * <!-- JDS end -->
 *
 *
 * <A NAME="orastoredprocs"></A>
 * <H3>Oracle stored procedures</H3>
 *
 * <P>
 * Oracle stored procedures work only with Oracle's type-2 and type-4 drivers. Also, the Oracle server version must be 7.3.4 or 8.0.4 or newer.
 *
 * <P>
 * <STRONG>Note: </STRONG>Stored "Functions" work with older versions of Oracle Servers as well.
 *
 * <P>
 * The following example demonstrates a stored procedure in a package:
 *
 * <PRE>
 * <CODE>CREATE PACKAGE my_pack is
*   type cust_cursor is ref cursor return CUSTOMER%rowtype;
*   procedure sp_test ( rc1 in out cust_cursor );
*end;
*
*CREATE PACKAGE BODY my_pack IS
*  PROCEDURE sp_test (rc1 in out cust_cursor) IS
*  BEGIN
*    open rc1 for select * from CUSTOMER;
*  END sp_test;
*END my_pack;</CODE>
 * </PRE>
 *
 *
 *
 * <P>
 * The call string for this procedure should be:
 * 
 * <PRE>
 * <CODE>"{ call my_pack.sp_test(?) }"</CODE>
 * </PRE>
 * 
 * No parameter row is needed. The cursor is used to load the <CODE>ProcedureDataSet</CODE>.
 *
 *
 * <P>
 * The result set you need to load the data into the <CODE>ProcedureDataSet</CODE> with must be the first parameter in the stored procedure argument list. If
 * additional parameters need to be sent or received, specify the <CODE>ParameterRow</CODE> in the <CODE>Procedure</CODE> property (or programmatically through
 * the <CODE>ProcedureDescriptor</CODE>).
 *
 *
 * <A NAME="Sybase"></A>
 * <H3>Sybase stored procedures</H3>
 *
 *
 * <P>
 * Stored procedures on Sybase servers are created in a "Chained" transaction mode. In order to call Sybase stored procedures as part of a
 * {@link com.borland.dx.sql.dataset.ProcedureResolver}<CODE>ProcedureResolver</CODE></A>, the procedures must be modified to run in an unchained transaction
 * mode. Use the Sybase stored system procedure <CODE>sp_procxmode</CODE> to change the transaction mode to either "anymode" or "unchained". See your Sybase
 * documentation for additional information.
 * 
 */
public class ProcedureDataSet extends StorageDataSet {
	public final void setProcedure(ProcedureDescriptor procedureDescriptor)
			/*-throws DataSetException-*/ throws SQLException {
		ProviderHelp.failIfOpen(this);

		this.procedureDescriptor = procedureDescriptor;
		// !propertiesChanged = true;
		ProviderHelp.setProviderPropertyChanged(this, true);
		if (currentProvider == null)
			setProcedureProvider(new ProcedureProvider());
		currentProvider.setProcedure(procedureDescriptor);
	}

	/**
	 *
	 * @return ProcedureDescriptor
	 */
	public final ProcedureDescriptor getProcedure() {
		return procedureDescriptor;
	}

	/**
	 *
	 *
	 */
	public final String getQueryString() {
		return procedureDescriptor != null ? procedureDescriptor.getQueryString() : null;
	}

	/**
	*
	*/
	public final Database getDatabase() {
		return procedureDescriptor != null ? procedureDescriptor.getDatabase() : null;
	}

	public ReadWriteRow getParameterRow() {
		if (procedureDescriptor != null)
			return procedureDescriptor.getParameterRow();
		else
			return null;
	}

	/**
	 * Returns state of accumulateResults property.
	 */
	public final boolean isAccumulateResults() {
		return accumulateResults;
	}

	/**
	 * If this property is enabled, provided data will be accumulated over consecutive calls to executeQuery. If the property is disabled, subsequent executeQuery
	 * calls will overwrite the existing dataset.
	 */
	public final void setAccumulateResults(boolean accumulate) {
		accumulateResults = accumulate;
		// !propertiesChanged = true;
		ProviderHelp.setProviderPropertyChanged(this, true);
		if (currentProvider != null)
			currentProvider.setAccumulateResults(accumulate);
	}

	/**
	 * Given that the database and query properties have been set, executes the query and populates the dataset.
	 */
	public final void executeQuery() /*-throws DataSetException-*/ {
		refresh();
	}

	/**
	 *
	 */
	public void refresh() /*-throws DataSetException-*/ {
		if (currentProvider == null)
			DataSetException.badProcedureProperties();
		super.refresh();
	}

	/**
	 * Used internally to specify whether saveChanges() is always supported by the ProcedureDataSet.
	 *
	 */
	public boolean saveChangesSupported() {
		return true;
	}

	public boolean refreshSupported() {
		return true;
	}

	/**
	 * Saves changes made to the data. If no resolver has been specified, a QueryResolver is used by default.
	 * 
	 * @param dataSet
	 */
	public void saveChanges(DataSet dataSet) /*-throws DataSetException-*/ {
		Resolver resolver = getResolver();
		if (resolver == null) {
			Database database = getDatabase();
			if (database != null && isOpen()) {
				closeProvider(false);
				database.saveChanges(dataSet);
			}
		} else {
			super.saveChanges(dataSet);
		}
	}

	// Trick the property inspectors into believe their is no autoProvider
	//
	public void setProvider(Provider provider) /*-throws DataSetException-*/ {
		if (provider != null && !(provider instanceof ProcedureProvider))
			DataSetException.needProcedureProvider();
		// ! failIfOpen();

		setProcedureProvider((ProcedureProvider) provider);
		if (currentProvider != null) {
			procedureDescriptor = currentProvider.getProcedure();
			accumulateResults = currentProvider.isAccumulateResults();
		}
	}

	private void setProcedureProvider(ProcedureProvider provider) /*-throws DataSetException-*/ {
		super.setProvider(provider);
		currentProvider = provider;
	}

	/**
	 * If Database.isUseStatementCaching() returns true, JDBC statements can be cached. By default these statements will be closed during garbage collection. If
	 * resources are scarce, the statment can be forced closed by calling this method.
	 */
	public void closeStatement()
	/*-throws DataSetException-*/
	{
		if (currentProvider != null)
			currentProvider.closeStatement();
	}

	private transient ProcedureProvider currentProvider;
	private ProcedureDescriptor procedureDescriptor;
	private boolean accumulateResults;

	private static final long serialVersionUID = 1L;

	/**
	 * LimoSys feature to change database connection to support efficient object sharing functionality
	 */
	public void switchConnection(Database db) {
		if (currentProvider != null) currentProvider.switchConnection(db);
		Resolver resolver = getResolver();
		if (resolver != null && resolver instanceof ProcedureResolver) ((ProcedureResolver) resolver).setDatabase(db);
	}
}
