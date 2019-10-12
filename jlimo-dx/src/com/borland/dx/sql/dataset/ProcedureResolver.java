//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ProcedureResolver.java,v 7.1 2003/06/13 16:21:28 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.ReadWriteRow;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;

/**
 * The ProcedureResolver component is used to resolve (save) changes back to a JBDC data source by calling stored procedures in that database. The stored
 * procedures must exist prior to using this component; this component will not generate them. These stored procedures must meet the requirements described in
 * the following properties:
 * <p>
 * deleteProcedure
 * <p>
 * insertProcedure
 * <p>
 * updateProcedure
 * <p>
 * 
 * 
 * The database property of this component must be set to the Database component that this ProcedureResolver is associated with. Otherwise, a DataSetException
 * is generated.
 * 
 * 
 * 
 * <p>
 * Sybase users:
 * <p>
 * Stored procedures on Sybase servers are created in a "Chained" transaction mode. In order to call Sybase stored procedures as part of this component, the
 * procedures must be modified to run in an unchained transaction mode. Use the Sybase stored system procedure sp_procxmode to change the transaction mode to
 * either "anymore" or "unchained". See your Sybase documentation for additional information.
 */
public class ProcedureResolver extends SQLResolver {
	/**
	 * Constructs a ProcedureResolver component.
	 */
	public ProcedureResolver() {
		readonly = new boolean[3];
		params = new ReadWriteRow[3];
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	/**
	 * Frees any system resources used for statements associated with the specified StorageDataSet.
	 * 
	 * @param dataSet
	 *          StorageDataSet
	 */
	public void closeStatements(StorageDataSet dataSet) {}

	/**
	 * Instructs the Resolver to insert the current row of the DataSet into the Database.
	 * 
	 * @param dataSet
	 *          DataSet
	 */
	public synchronized void insertRow(DataSet dataSet) /*-throws DataSetException-*/
	{
		try {
			DiagnosticJLimo.trace(Trace.DataSetSave, "ProcedureResolver inserting row " + dataSet);
			checkBeforeCall(insertProcedure);
			params[0] = dataSet;
			params[1] = insertProcedure.getParameterRow();
			params[2] = null;
			readonly[0] = true;
			readonly[1] = false;
			// ! readonly[2] = false;
			String proc = insertProcedure.getStrippedQueryString();
			proc = replaceWithNumber(proc, strCurrent, 1);
			proc = replaceWithNumber(proc, strParameter, 2);

			ProcedureProvider.callProcedure(database, proc, params, readonly);
		} catch (DataSetException ex) {
			ResolutionException.insertFailed(dataSet, null, ex);
		}
	}

	/**
	 * Instructs the Resolver to update the current row of the DataSet in the Database.
	 * 
	 * @param dataSet
	 *          DataSet
	 * @param oldDataRow
	 *          ReadWriteRow
	 */
	public synchronized void updateRow(DataSet dataSet, ReadWriteRow oldDataRow) /*-throws DataSetException-*/
	{
		try {
			DiagnosticJLimo.trace(Trace.DataSetSave, "ProcedureResolver updating row:  " + dataSet.getLongRow());
			DiagnosticJLimo.trace(Trace.DataSetSave, "                  updating from: " + oldDataRow);
			checkBeforeCall(updateProcedure);
			params[0] = dataSet;
			params[1] = oldDataRow;
			params[2] = updateProcedure.getParameterRow();
			readonly[0] = true;
			readonly[1] = true;
			readonly[2] = false;
			String proc = updateProcedure.getStrippedQueryString();
			proc = replaceWithNumber(proc, strCurrent, 1);
			proc = replaceWithNumber(proc, strOriginal, 2);
			proc = replaceWithNumber(proc, strParameter, 3);

			ProcedureProvider.callProcedure(database, proc, params, readonly);
		} catch (DataSetException ex) {
			DiagnosticJLimo.printStackTrace(ex);
			ResolutionException.updateFailed(dataSet, null, ex);
		}
	}

	/**
	 * Instructs the Resolver to delete the current row in the DataSet from the Database.
	 * 
	 * @param dataSet
	 *          DataSet
	 */
	public synchronized void deleteRow(DataSet dataSet) /*-throws DataSetException-*/
	{
		try {
			DiagnosticJLimo.trace(Trace.DataSetSave, "ProcedureResolver deleting row " + dataSet.getLongRow());
			checkBeforeCall(deleteProcedure);
			params[0] = dataSet;
			params[1] = deleteProcedure.getParameterRow();
			params[2] = null;
			readonly[0] = true;
			readonly[1] = false;
			// ! readonly[2] = false;
			String proc = deleteProcedure.getStrippedQueryString();
			proc = replaceWithNumber(proc, strOriginal, 1);
			proc = replaceWithNumber(proc, strParameter, 2);

			ProcedureProvider.callProcedure(database, proc, params, readonly);
		} catch (DataSetException ex) {
			ResolutionException.deleteFailed(dataSet, null, ex);
		}
	}

	private void checkBeforeCall(ProcedureDescriptor descriptor) /*-throws DataSetException-*/ {
		if (descriptor == null || descriptor.getDatabase() == null || descriptor.getStrippedQueryString() == null)
			DataSetException.badProcedureProperties();
		if (descriptor.getDatabase() != database)
			DataSetException.wrongDatabase();
	}

	public ProcedureDescriptor getInsertProcedure() {
		return insertProcedure;
	}

	public void setInsertProcedure(ProcedureDescriptor insertProcedure) {
		this.insertProcedure = insertProcedure;
	}

	public ProcedureDescriptor getUpdateProcedure() {
		return updateProcedure;
	}

	public void setUpdateProcedure(ProcedureDescriptor updateProcedure) {
		this.updateProcedure = updateProcedure;
	}

	public ProcedureDescriptor getDeleteProcedure() {
		return deleteProcedure;
	}

	public void setDeleteProcedure(ProcedureDescriptor deleteProcedure) {
		this.deleteProcedure = deleteProcedure;
	}

	private String replaceWithNumber(String procedure, String parameter, int parameterValue) {
		if (procedure == null)
			return null;

		String copy = procedure.toLowerCase();

		int index = copy.lastIndexOf(parameter);
		while (index > 0) {
			procedure = procedure.substring(0, index + 1) + parameterValue + procedure.substring(index + parameter.length());
			index = copy.lastIndexOf(parameter, index - 1);
		}
		return procedure;
	}

	private transient ReadWriteRow params[];
	private transient boolean readonly[];
	private Database database;
	private ProcedureDescriptor deleteProcedure;
	private ProcedureDescriptor insertProcedure;
	private ProcedureDescriptor updateProcedure;

	private static final String strOriginal = ":original"; // NORES
	private static final String strCurrent = ":current"; // NORES
	private static final String strParameter = ":parameter"; // NORES
	private static final long serialVersionUID = 1L;

}
