//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ProcedureProvider.java,v 7.0 2002/08/08 18:39:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.ProviderHelp;
import com.borland.dx.dataset.ReadWriteRow;
import com.borland.dx.dataset.StorageDataSet;

/**
 * The <CODE>ProcedureProvider</CODE> class provides data to the {@link com.borland.dx.dataset.StorageDataSet} <CODE>StorageDataSet</CODE></A> by executing the
 * specified stored procedure through JDBC. You connect this component to the <CODE>StorageDataSet</CODE> component through the <CODE>StorageDataSet's</CODE>
 * <CODE>provider</CODE> property.
 * 
 * 
 * <P>
 * This class provides the data to the <CODE>StorageDataSet</CODE> however it does not attempt to make the <CODE>StorageDataSet</CODE> updatable or editable; it
 * is the developer's responsibility to ensure this prior to the start of the resolution phase.
 * 
 * <P>
 * The stored procedure is expected to return a result set. For stored procedures that don't return values, use either static form of the
 * <CODE>callProcedure()</CODE> method to execute them.
 * 
 */
public class ProcedureProvider extends JdbcProvider {

	CallableStmt createProcedure() /*-throws DataSetException-*/ {
		Database db = null;
		if (procedureDescriptor != null)
			db = procedureDescriptor.getDatabase();
		if (db != null) {
			if (!db.isOpen())
				db.openConnection();
			RuntimeMetaData meta = db.getRuntimeMetaData();
			if (meta.getBooleanValue(RuntimeMetaData.USE_ORACLERESULTSET))
				return new OracleCallableStmt();
		}
		return new CallableStmt();
	}

	// Simple Procedure Calls
	//
	/**
	 * This method can also be used for queries with an output parameter, for example:
	 * 
	 * <PRE>
	 * <CODE> callProcedure(db1,"select sum(expense) into ? from expense_table",paramRow);
	 * 
	 * @param database
	 *          Database
	 * @param procedureSpecification
	 *          String
	 * @param parameters
	 *          ReadWriteRow
	 *          <p>
	 */
	public static final int callProcedure(Database database, String procedureSpecification, ReadWriteRow parameters)
	/*-throws DataSetException-*/
	{
		ReadWriteRow[] rows = parameters == null ? null : new ReadWriteRow[] { parameters };
		return callProcedure(database, procedureSpecification, rows, null);
	}

	/**
	 * Calls a stored procedure with named access to multiple rows of parameters. The parameters are accessed via an optional tag of the named parameters. The tag
	 * is the number of the passed <CODE>ReadWriteRow</CODE>, starting with 1. For example, the following code passes the value of the 'Name' column from the
	 * parameter row 'param1' as the first parameter, and the value of the 'Name' column from the parameter row 'param2'.
	 *
	 * <PRE>
	 * <CODE>     callProcedure(db1,"call foo(:2.Name,:1.Name)",
	*  new ReadWriteRow[]{param1,param2});</CODE>
	 * </PRE>
	 *
	 * <P>
	 * If no tag is given, the parameter name is found by searching the parameter rows from left to right. Therefore, tags can be used to differentiate columns
	 * with identical names in different parameter rows.
	 *
	 * @param database
	 *          database
	 * @param procedureSpecification
	 *          String
	 * @param parameters
	 *          ReadWriteRow
	 *
	 */
	public static final int callProcedure(Database database, String procedureSpecification, ReadWriteRow[] parameters)
	/*-throws DataSetException-*/
	{
		return callProcedure(database, procedureSpecification, parameters, null);
	}

	static final int callProcedure(Database database, String procedureSpecification, ReadWriteRow[] parameters, boolean[] inputOnly)
	/*-throws DataSetException-*/
	{
		int updateCount = -1;
		try {
			if (database == null || procedureSpecification == null)
				DataSetException.badProcedureProperties();

			CallableStmt procedure = new InterbaseCallableStmt();
			procedure.resetState(database, procedureSpecification, null, parameters, inputOnly);
			updateCount = procedure.executeUpdate();
			procedure.setOutputValues();
			procedure.closeStatement();
		} catch (SQLException ex) {
			DataSetException.SQLException(ex);
		}
		return updateCount;
	}

	public final void setProcedure(ProcedureDescriptor procedureDescriptor)
	/*-throws DataSetException-*/
	{
		if (dataSet != null)
			ProviderHelp.failIfOpen(dataSet);
		this.procedureDescriptor = procedureDescriptor;
		if (procedure != null) {
			try {
				procedure.closeStatement();
			} catch (SQLException ex) {
				DataSetException.SQLException(ex);
			}
			procedure = null;
		}
		setPropertyChanged(true);
	}

	public final ProcedureDescriptor getProcedure() {
		return procedureDescriptor;
	}

	void cacheDataSet(StorageDataSet dataSet) /*-throws DataSetException-*/ {
		super.cacheDataSet(dataSet);
		if (procedureDescriptor == null || procedureDescriptor.getDatabase() == null || procedureDescriptor.getStrippedQueryString() == null)
			DataSetException.badProcedureProperties();
		setQueryDescriptor(procedureDescriptor);
	}

	ResultSet provideResultSet() /*-throws DataSetException-*/ throws SQLException {
		if (procedureDescriptor == null || procedureDescriptor.getDatabase() == null || procedureDescriptor.getStrippedQueryString() == null)
			DataSetException.badProcedureProperties();

		ProviderHelp.setMetaDataMissing(dataSet, dataSet.hasRowIds());
		return procedure.executeQuery();
	}

	void providerFailed(Exception ex) /*-throws DataSetException-*/ {
		DataSetException.procedureFailed(ex);
	}

	void closeResultSet(ResultSet resultSet) /*-throws DataSetException-*/ throws SQLException {
		procedure.setOutputValues();
		procedure.closeResultSet(resultSet);
	}

	void resetState() /*-throws DataSetException-*/ throws SQLException {
		super.resetState();
		if (procedure == null)
			procedure = createProcedure();
		if (procedureDescriptor == null || dataSet == null)
			procedure.resetState(null, null, null, (ReadWriteRow[]) null, null);
		else
			procedure.resetState(procedureDescriptor.getDatabase(), procedureDescriptor.getStrippedQueryString(), dataSet.getMasterLink(), procedureDescriptor
					.getParameterRow(), null);
	}

	// get better error message:
	/**
	 * Tests whether the data is present. This method is used when providing data asynchronously to determine whether editing, resolving, and other such actions
	 * should be blocked until the data is available.
	 */
	public void ifBusy() /*-throws DataSetException-*/ {
		try {
			super.ifBusy();
		} catch (DataSetException ex) {
			DataSetException.procedureInProcess();
		}
	}

	public ReadWriteRow getParameterRow() {
		if (procedureDescriptor != null)
			return procedureDescriptor.getParameterRow();
		else
			return null;
	}

	public void setParameterRow(ReadWriteRow value) {
		if (procedureDescriptor != null)
			procedureDescriptor.setParameterRow(value);
	}

	private ProcedureDescriptor procedureDescriptor;
	private transient CallableStmt procedure;
	private static final long serialVersionUID = 1L;

	/**
	 * LimoSys feature to change database connection to support efficient object sharing functionality
	 */
	public void switchConnection(Database db) {
		if (procedureDescriptor != null) procedureDescriptor.switchConnection(db);
	}
}
