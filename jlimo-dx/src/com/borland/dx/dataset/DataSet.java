//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataSet.java,v 7.15.2.3 2005/06/23 00:08:36 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.TooManyListenersException;

import com.borland.dx.dataset.cons.PropSet;
import com.borland.dx.text.InvalidFormatException;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.ErrorResponse;
import com.borland.jb.util.EventMulticaster;
import com.borland.jb.util.Trace;
import com.borland.jb.util.TriStateProperty;

//import java.beans.Beans;

/**
 *
 * The <CODE>dx.dataset</CODE> package contains classes and interfaces that provide basic data access. This package also defines base provider and resolver
 * classes as well as an abstract <CODE>DataSet</CODE> class that is extended to other <CODE>DataSet</CODE> objects. These classes provide access to information
 * stored in databases and other data sources.
 * <P>
 * This package includes functionality covering the three main phases of data handling:
 * <P>
 * <BTBL><STRONG>Data handling</BTBL></STRONG>
 * <P>
 * <TABLE CELLSPACING=0 CELLPADDING=4 BORDER=1>
 * 
 * <TR>
 * <TD VALIGN=TOP>Providing</TD>
 * <TD VALIGN=TOP>General functionality to obtain data and manage local data sets. (JDBC specific connections to remote servers are handled by classes in the
 * <CODE>com.borland.dx.sql.dataset</CODE> package.)</TD>
 * </TR>
 * <TR>
 * <TD VALIGN=TOP>Manipulation&nbsp;&nbsp;&nbsp;</TD>
 * <TD VALIGN=TOP>Navigation and editing of the data locally.</TD>
 * </TR>
 * <TR>
 * <TD VALIGN=TOP>Resolving</TD>
 * <TD VALIGN=TOP>General routines for the updating of data from the local <CODE>DataSet</CODE> back to the original source of the data. (Resolving data changes
 * to remote servers through JDBC is handled by classes in the <CODE>com.borland.dx.sql.dataset</CODE> package.)</TD>
 * </TR>
 * </TABLE>
 * 
 * <BR>
 * The <CODE>DataSet</CODE> class is an abstract class that provides basic editing, view, and cursor functionality for access to two-dimensional data. It
 * supports the concept of a current row position which allows for navigation of the data in the <CODE>DataSet</CODE>. The <CODE>DataSet</CODE> also manages a
 * <CODE>pseudo</CODE> record -- an area in memory where a newly inserted row or changes to the current row are temporarily stored.
 * 
 * <P>
 * The data in a <CODE>DataSet</CODE> can be modified programmatically or through a data-aware control. To connect a <CODE>DataSet</CODE> to a data-aware
 * control, set the control's <CODE>dataSet</CODE> property to the <CODE>DataSet</CODE> you want to use as the data source. Several data-aware controls can be
 * associated with the same <CODE>DataSet</CODE>. In such cases, the controls navigate together and when you move the row position of a control, the row
 * position changes for all controls that share the same <CODE>DataSet</CODE>. This synchronization of controls that share a common <CODE>DataSet</CODE> can
 * greatly ease the development of the user-interface portion of your application.
 * 
 * <P>
 * Controls which share the same <CODE>DataSet</CODE> as their data source share also the same pseudo record. This allows updates to be visible as soon as entry
 * at the field level is complete, for example, by navigating off the field.
 * 
 * <P>
 * The <CODE>DataSet</CODE> component is opened implicitly (by default) when visual components bound to it are shown. For example, launching an application that
 * includes a data-aware control that is bound to a <CODE>DataSet</CODE> automatically opens the <CODE>DataSet</CODE> so you seldom have to open the
 * <CODE>DataSet</CODE> explicitly. No code is generated for this implicit open.
 * 
 * <P>
 * You can navigate through the data in a <CODE>DataSet</CODE> using a UI control or programmatically. To navigate programmatically, use the
 * <CODE>first()</CODE>, <CODE>last()</CODE>, <CODE>next()</CODE>, and <CODE>prior()</CODE> methods. To verify if such a navigation is valid use the
 * <CODE>inBounds()</CODE> method. Similarly, to perform tests for the "end of file" or "beginning of file" conditions, use the <CODE>atLast()</CODE> or
 * <CODE>atFirst()</CODE> methods.
 * 
 * <!-- JDS start - remove last sentence of paragraph -->
 * 
 * <P>
 * The <CODE>DataSet</CODE> has an associated {@link com.borland.dx.dataset.SortDescriptor SortDescriptor} object that defines properties which affect the sort
 * order of the data in the <CODE>DataSet</CODE>. This is accessed from the <CODE>sort</CODE> property in the JBuilder Inspector. <!-- JDS end -->
 * 
 * <A NAME="masterdetail"></A>
 * <P>
 * <CODE>DataSet</CODE> objects have relational capability and can be linked to form master-detail relationships. The
 * {@link com.borland.dx.dataset.MasterLinkDescriptor MasterLinkDescriptor} holds the properties required for relational capability between <CODE>DataSet</CODE>
 * objects. <!-- JDS start - remove sentence --> For a tutorial using the MasterLinkDescriptor, see "Establishing a master-detail relationship" in the
 * <CITE>Database Application Developer's Guide</CITE>. <!-- JDS end -->
 * 
 * 
 * <P>
 * The <CODE>DataSet</CODE> class is extended by {@link com.borland.dx.dataset.DataSetView DataSetView} and {@link com.borland.dx.dataset.StorageDataSet
 * StorageDataSet}. The <CODE>DataSetView</CODE> component allows for an alternate view (sort order, navigation, and filter criteria) for the data contained in
 * the <CODE>DataSet</CODE>. A <CODE>StorageDataSet</CODE>, and any class that extends <CODE>StorageDataSet</CODE>, manages the storage aspects of the data
 * operated on by the <CODE>DataSet</CODE>.
 * 
 * An abstract class to provide a cursor for accessing and navigating table data. Manages a pseudo-record in memory to temporarily store a newly inserted row,
 * or changes to the current row. Multiple data-aware controls can be bound to and synchronized with the same DataSet. Supports master-detail relationships.
 * Supports model-view data-aware controls, to enable easy and flexible editing and navigation of data in a common way regardless of how the data was obtained.
 */

abstract public class DataSet extends ReadWriteRow
		implements StatusListener, MasterNavigateListener, AccessListener, Designable {
	DataSet() {
		// Prevents null pointer exceptions when the DataSet is referenced before
		// it is open. Used to be in StorageDataSet
		//
		columnList = new ColumnList();
		this.visibleMask = RowStatus.DEFAULT;
		this.invisibleMask = RowStatus.DEFAULT_HIDDEN;

		this.displayErrors = true;
		this.editable = true;
		this.allowInsert = true;
		this.allowUpdate = true;
		this.allowDelete = true;
		// !Diagnostic.println("created new DataSet: " + this);
	}

	/**
	 * Returns the number of rows visible with this DataSet. This number may be different than the number of rows in the associated StorageDataSet if a
	 * RowFilterListener is being used to filter out some rows of the DataSet.
	 * <p>
	 * Also, when a Provider, such as the QueryProvider used by QueryDataSet, is loading rows asynchronously, attempting to access the DataSet data or rowCount
	 * property may lead to unexpected results. This is due to the way the rows are being loaded. The rowCount will continue to increase as 0 or more rows are
	 * loaded, until the asynchronous load operation is completed.
	 *
	 * @return The number of rows visible with this DataSet.
	 */
	public final int getRowCount() {
		return (int) getLongRowCount();
	}

	public final long getLongRowCount()
	/*-throws DataSetException-*/
	{
		// Note that if there are no rows, index.lastRow() will be -1.
		//
		if (!open)
			DataSetException.dataSetNotOpen();

		// !RC Put this back in so we survive -- do future tests with diagnostics on
		if (index == null) {
			DiagnosticJLimo.println("rowCount called when not open!");
			return 0;
		}

		if (editing && newRow)
			return index.lastRow() + 2;
		return index.lastRow() + 1;
	}

	/**
	 * should use getRowCount() - deprecated annotation removed by J-Limo update
	 * 
	 * @return The number of rows visible with this DataSet.
	 */
	public final int rowCount()
	/*-throws DataSetException-*/
	{
		return (int) getLongRowCount();
	}

	public final boolean isEmpty() /*-throws DataSetException-*/ {
		if (!open)
			DataSetException.dataSetNotOpen();
		if (index == null)
			return true;
		return index.lastRow() == -1;
	}

	/**
	 * Is this dataSet at the last row visible by this dataSet?
	 */

	/**
	 *
	 * @return <b>true</b> if the current position is the last visible row of DataSet, otherwise, this method returns false. This method is the equivalent of the
	 *         EOF() (beginning of file) function that is available in many programming languages.
	 */
	public final boolean atLast()
	/*-throws DataSetException-*/
	{
		long rowCount = getLongRowCount();
		return (rowCount == 0 || currentRow == (rowCount - 1)) && !dataSetStore.hasMoreData();
	}

	/**
	 * Is this dataSet at the first row visible by this dataSet? No need for synchronization.
	 */

	/**
	 * @return <b>true</b> if the current position is the first visible row of DataSet, otherwise, this method returns <b>false</b>. This method is the equivalent
	 *         of the BOF() (beginning of file) function that is available in many programming languages.
	 */
	public final boolean atFirst() {
		return currentRow == 0;
	}

	/**
	 * Moves the row position to the first row visible to the DataSet and sends a NavigationEvent to registered NavigationListeners if the move was successful.
	 * This may cause the {@ link #inBounds()} method to return true if more than one row exists.
	 */
	public final void first() /*-throws DataSetException-*/ {
		boolean moved;
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			if (needsSynch != TriStateProperty.FALSE)
				_synchRow();
			moved = _goToRow(0);
		}
		if (moved)
			rowNavigatedDispatch();
	}

	/**
	 * Moves the current position to the last row visible to the DataSet and sends a NavigationEvent notification to registered NavigationListeners if the move
	 * was successful. This may cause the inBounds() to return true if more than one row exists.
	 * <p>
	 * If an edit is in progress, the changes are posted prior to performing the move operation. On failure, this method throws a DataSetException.
	 */
	public final void last() /*-throws DataSetException-*/ {
		boolean moved = false;
		if (!open)
			failIfNotOpen();

		dataSetStore.closeProvider(true);
		synchronized (dataMonitor) {
			// Don't know what the last row really is if editing new row,
			// so stop editing before attempting to move
			//
			if (!editing || _post()) {
				if (needsSynch != TriStateProperty.FALSE)
					_synchRow();
				moved = _goToRow(getLongRowCount() - 1);
			}
		}

		if (moved)
			rowNavigatedDispatch();
	}

	/**
	 * Moves the row position to the next row visible to the DataSet and sends a NavigationEvent notification of rowChanged to its registered NavigationListeners
	 * if the move was successful. The {@link #inBounds()} method returns false if {@link #next()} is called when the DataSet is positioned at the last visible
	 * row.
	 *
	 * @return <b>true</b> if the row position could be moved to the next row visible to the DataSet.
	 */
	public final boolean next() /*-throws DataSetException-*/ {
		boolean moved;
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			if (needsSynch != TriStateProperty.FALSE)
				_synchRow();

			moved = _goToRow(currentRow + 1);
		}
		if (moved) {
			rowNavigatedDispatch();
		}
		return moved;
	}

	/**
	 * Moves the row position to the previous row visible to the DataSet and sends a NavigationEvent notification of rowChanged to its registered
	 * NavigationListeners if the move was successful. This will cause the inBounds() method to return <b>false</b> if {@link #prior()} is called when the DataSet
	 * is positioned at the first visible row.
	 *
	 * @return <b>true</b> if the row position could be moved to the previous row visible to the DataSet.
	 */
	public final boolean prior()
	/*-throws DataSetException-*/
	{
		boolean moved;
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			if (needsSynch != TriStateProperty.FALSE)
				_synchRow();

			moved = _goToRow(currentRow - 1);
		}
		if (moved)
			rowNavigatedDispatch();
		return moved;
	}

	final void processMasterNavigating(MasterNavigateEvent event)
	/*-throws DataSetException-*/
	{
		// ! try {
		masterNavigateListeners.dxDispatch(event);
		// ! //detailsFetched();
		// ! }
		// ! catch(Exception ex) {
		// ! DataSetException.throwException(DataSetException.MASTER_NAVIGATION_ERROR, ex);
		// ! }
	}

	/**
	 * Start editing a new row. The before parameter indicates whether to insert before or after the current row.
	 * <p>
	 * This method adds a new, unposted "pseudo" row into the DataSet. Until post() is called, this row will not be visible to DataSetView components that are
	 * sharing the same StorageDataSet. After insertRow() has been called, values can be set in the pseudo row by calling DataSet set methods. The insertRow()
	 * method provides the functionality of row inserting in data aware visual components.
	 * <p>
	 * When the post() method is called, the unposted row is stored in the StorageDataSet and becomes visible by DataSetViews sharing the same StorageDataSet.
	 * When the row is posted, and the sort property is not set, the current row in the DataSet will change to the newly-posted row at the end of the DataSet. If
	 * the DataSet sort property is set, the current row of the DataSet will be the newly-posted row, displayed in its sorted position within the DataSet.
	 * <p>
	 * Many navigating methods will cause an unposted row to be automatically posted. In this case, the new row appears to "fly away" to its proper position,
	 * which may be at the end of current rows if the sort property is <b>null</b>, or to its sorted position if the sort property is not <b>null</b>. In these
	 * cases, the current row is not necessarily (and is not likely to be) the newly-posted row. Instead the cursor is positioned according to the navigating
	 * request. For example, if the prior() method is called while an unposted row exists, the new row will be posted into the StorageDataSet according to the
	 * sort property setting, but the current row position will be one row up.
	 *
	 * @see #addRow(com.borland.dx.dataset.DataRow) for higher performance batch row adding.
	 *
	 * @param before
	 *          Whether the new row should be inserted before (<b>true</b>), or after (<b>false</b>), the current row. This property defaults to <b>false</b>.
	 */
	public final void insertRow(boolean before) /*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		synchronized (dataMonitor) {

			if (needsSynch != TriStateProperty.FALSE)
				_synchRow();

			if (!allowInsert || !dataSetStore.allowInsert)
				ValidationException.insertNotAllowed();

			if (masterNavigateListeners != null)
				processMasterNavigating(masterNavigatingEvent);

			if (dataSetStore.editListeners != null)
				dataSetStore.processInserting(this);
			// used to do _post(), but this does not fix up the row position before
			// attempting the insert. Bad that this is all done in critical section,
			// but necessary. Test case is to insert when already on a new row - ie
			// keep adding new rows at the end.
			//
			if (editing == false || post()) {
				editing = true;
				newRow = true;
				// Must do before rowDirty set and after editing state set.
				// editing set ensures that row buffer will be set, and rowDirty
				// cleared allows the row to be closed up if no more edit changes
				// are made.
				//
				setDefaultValues();
				rowDirty = allowPostUnmodified;
				if (before == false && index.lastRow() > -1)
					++currentRow;
			}
		}

		if (before == false)
			rowNavigatedDispatch();

		rowAddedDispatch(currentRow);

		if (statusListeners != null)
			notifyDataChangeStatus(StatusEvent.DATA_CHANGE, ResIndex.RowAdded);

		if (masterNavigateListeners != null) {
			DiagnosticJLimo.trace(Trace.Master, "Master moving to new row ");
			masterNavigateListeners.dxDispatch(masterNavigatedEvent);
		}
		if (dataSetStore.editListeners != null)
			dataSetStore.processInserted(this);
	}

	/**
	 * Edits the existing row of the DataSet. When the edits are posted, the row's status flag is set to UPDATED. On failure, this method throws a
	 * DataSetException.
	 */
	public final void editRow()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (!editing) {
			if (index.lastRow() == -1)
				insertRow(true);
			else {

				if (!allowUpdate || !dataSetStore.allowUpdate)
					ValidationException.updateNotAllowed();

				if (dataSetStore.editListeners != null)
					dataSetStore.processModifying(this);

				dataSetStore.editRow(this);

				if (statusListeners != null)
					notifyDataChangeStatus(StatusEvent.EDIT_STARTED, ResIndex.RowEdited);
			}
		}
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final void _editRow() {
		if (editing == false) {
			editing = true;
			newRow = false;
			rowDirty = false;
		}
	}

	/**
	 * Locates the row of data with the specified row of values and moves the current row position to that row. The locate operation includes all columns of the
	 * ReadRow; to limit the locate to specific columns of interest, use a scoped DataRow. If the row is scoped to a specific set of columns, only those columns
	 * are used for the locate.
	 *
	 * @param rowLocate
	 *          The ReadRow that contains values to use in for the locate operation.
	 * @param locateOptions
	 *          Options that are applicable when locating data. Valid values for the locateOptions are defined in {@link com.borland.dx.dataset.Locate} variables.
	 *          The Locate variables may be combined using the Java bit-wise OR operator of a vertical pipe symbol (|) between each variable where it makes sense
	 *          to do so. For example, you can search using partial strings and specifying case insensitivity.
	 * @return <b>true</b> if the row of data containing the specified values were located.
	 */
	public final boolean locate(ReadRow rowLocate, int locateOptions) /*-throws DataSetException-*/ {
		if (!open)
			failIfNotOpen();
		if (needsSynch != TriStateProperty.FALSE)
			synchRow();
		if (dataSetStore.locate(this, rowLocate.getColumnList().getScopedArray(), rowLocate, locateOptions)) {
			rowNavigatedDispatch();
			return true;
		}
		return false;
	}

	/**
	 * Performs a lookup for the row with the specified values. If a match is found, this method returns the data from the matching row as resultRow, and returns
	 * <b>true</b> but does not navigate to the matching row. If no match is found, this method returns <b>false</b>. This method includes all columns of the
	 * ReadRow; to limit the lookup to specific columns, use a "scoped" DataRow that includes only the columns of interest.
	 *
	 * @param locateRow
	 *          The ReadRow that contains values to use in for the lookup operation.
	 * @param resultRow
	 *          The data in the row where the match with the rowLocate values is found.
	 * @param locateOptions
	 *          Options that are applicable when locating data. Valid values for the locateOptions are defined in {@link com.borland.dx.dataset.Locate} variables.
	 *          The Locate variables may be combined using a vertical pipe symbol ( | ) between each variable where it makes sense to do so. For example, you can
	 *          search using partial strings and specifying case insensitivity.
	 * @return <b>true</b> if a match is found, <b>false</b> otherwise.
	 */
	public final boolean lookup(ReadRow locateRow, DataRow resultRow, int locateOptions)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		return dataSetStore.lookup(	this, locateRow.getColumnList().getScopedArray(),
																locateRow, resultRow, locateOptions);
	}

	/**
	 * If a new or existing record is being edited, calling post() force the record to be posted. If this method is called for an unposted row, the row will be
	 * posted in the StorageDataSet with a RowStatus of INSERTED. If the sort property is not set, the new row will be positioned at the end of the
	 * StorageDataSet. If the sort property is set, the row will be positioned according to the sort property setting.
	 * <p>
	 * If called for an existing row, the RowStatus for that row is UPDATED, if it does not already have the RowStatus of INSERTED. As with new rows, it will be
	 * positioned according to the sort property setting.
	 *
	 * @return <b>true</b> if the row was successfully posted in the StorageDataSet.
	 */
	public final boolean post() /*-throws DataSetException-*/ {
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			boolean posted;
			long saveRow = currentRow;
			boolean wasRowDirty = rowDirty;
			if ((posted = (editing && _post()))) {
				if (wasRowDirty)
					internalRow = postedRow;
				isInBounds = true;
				// !/*
				// ! index.findClosest(postedRow, currentRow);
				// ! if (index.internalRow != postedRow) {
				// ! Diagnostic.println("postedRow: "+postedRow+" != "+index.internalRow);
				// ! index.findClosest(postedRow, currentRow);
				// ! Diagnostic.exit(1);
				// ! }
				// ! Diagnostic.println("postedRow: "+internalRow);
				// !*/
				_synchRow();
				// ! if (fixRowNeeded())
				// ! _fixRowPosition();
				if (saveRow != currentRow) {
					rowNavigatedDispatch();
				}
				if (masterNavigateListeners != null) {
					DiagnosticJLimo.trace(Trace.Master, "Master moving to " + currentRow);
					masterNavigateListeners.dxDispatch(masterNavigatedEvent);
				}
			}
			if (posted && statusListeners != null)
				notifyDataChangeStatus(StatusEvent.DATA_CHANGE, ResIndex.RowChangePosted);
			return posted;
		}
	}

	/**
	 * If new or existing record is being edited, force the record editing to be cancled.
	 */

	/**
	 * Cancels the edits to a new or existing record. On error, this method throws a DataSetException.
	 */
	public final void cancel()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (editing) {
			synchronized (dataMonitor) {
				_cancel();
			}
			rowDeletedDispatch(currentRow);
			if (statusListeners != null)
				notifyDataChangeStatus(StatusEvent.EDIT_CANCELED, ResIndex.RowCanceled);
		}
	}

	/**
	 * @updated JB2.0 Cancels the loading of data into the DataSet. This can be used to terminate a long running asynchronous provider operation. The
	 *          QueryProvider used by QueryDataSet has Load options that allow it to load query results into a StorageDataSet asynchronously on a separate thread.
	 *          Calling this method will cause such an operation to terminate.
	 * 
	 * @see #cancelOperation().
	 */
	public void cancelLoading() {
		StorageDataSet dataSetStoreCopy = dataSetStore;
		if (dataSetStoreCopy != null)
			dataSetStoreCopy.cancelLoading();
	}

	/**
	 * @since JB2.0 Cancels any long running operation currently active on this DataSet. This includes loading a query result, restructure operations, and index
	 *        building operations. For this have an effect, the long running operation must be executing on a different thread, than the thread that calls this
	 *        method.
	 */
	public void cancelOperation()
	/*-throws DataSetException-*/
	{
		StorageDataSet dataSetStoreCopy = dataSetStore;
		if (dataSetStoreCopy != null)
			dataSetStoreCopy.cancelOperation();
	}

	/**
	 * Reset the status bits of the rows marked pending during resolution.
	 * 
	 * @param resolved
	 *          <b>true</b> if changes were resolved and the changed rows should now be treated as originals in a new resolution query. <b>false</b> if changes
	 *          were rolled back and the changed rows should still be treated as changed rows.
	 */
	public void resetPendingStatus(boolean resolved)
	/*-throws DataSetException-*/
	{
		StorageDataSet dataSetStoreCopy = dataSetStore;
		if (dataSetStoreCopy != null)
			dataSetStoreCopy.resetPendingStatus(resolved);
	}

	/*
	 * @since JB2.0 Reset the status bits of a specific row.
	 * 
	 * @param internalRow the row where the the status bits should be reset.
	 * 
	 * @param resolved true if changes were resolved and the changed rows should now be treated as originals in a new resolution query. false if changes were
	 * rolled back and the changed rows should still be treated as changed rows.
	 */
	public void resetPendingStatus(long internalRow, boolean resolved)
	/*-throws DataSetException-*/
	{
		StorageDataSet dataSetStoreCopy = dataSetStore;
		if (dataSetStoreCopy != null)
			dataSetStoreCopy.resetPendingStatus(internalRow, resolved);
	}

	/**
	 * Empty the current DataSet row. The emptied row is not marked as deleted. However, it is completely forgotten and cannot be resolved back to a data source.
	 *
	 * @see #deleteRow()
	 */
	public final void emptyRow()
	/*-throws DataSetException-*/
	{
		deleteRow(true);
	}

	/**
	 * This method calls the emptyRow() method for all rows visible in the DataSet. If called on a detail DataSet, only the rows in the current group are emptied.
	 * <p>
	 * The emptied rows are not marked as deleted rows. However, they are completely forgotten and cannot be resolved back to a data source.
	 */
	public final void emptyAllRows()
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		if (index != null)
			index.emptyAllRows(this);
	}

	/**
	 * Deletes the current row of the DataSet. If the current row is new or being edited, the edit state is canceled and the new or edited row is deleted. On
	 * failure, this method throws a DataSetException.
	 */
	public final void deleteRow()
	/*-throws DataSetException-*/
	{
		deleteRow(false);
	}

	/**
	 * Deletes the current row of the DataSet. If the current row is new or being edited, the edit state is canceled and the new or edited row is deleted.
	 *
	 * @param empty
	 */
	final void deleteRow(boolean empty)
	/*-throws DataSetException-*/
	{
		long internalRow;
		synchronized (dataMonitor) {
			internalRow = _synchRow();
		}
		deleteRow(internalRow, true, empty);
	}

	final void deleteRow(long internalRow, boolean sync, boolean empty)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (!allowDelete && !empty)
			ValidationException.deleteNotAllowed();

		if (dataSetStore.editListeners == null || empty)
			dataSetStore.deleteRow(this, internalRow, sync, empty);
		else {
			while (true) {
				try {
					goToInternalRow(internalRow);
					dataSetStore.processDeleting(this);
					dataSetStore.deleteRow(this, internalRow, sync, empty);
				} catch (DataSetException ex) {
					ErrorResponse response = dataSetStore.processDeleteError(this, ex);
					if (response.isRetry())
						continue;
					else if (response.isAbort())
						throw ex;
					else
						return;
				}
				break;
			}
			dataSetStore.processDeleted(this);
		}

		if (statusListeners != null)
			notifyDataChangeStatus(StatusEvent.DATA_CHANGE, ResIndex.RowDeleted);
	}

	/**
	 * calls deleteRow for all rows visible in the DataSet. If called on a detail dataSet, only the rows in the current group are deleted. Deletion status
	 * information is tracked for resolution back to the original data source
	 * 
	 * @see emptyAllRows to remove visible rows in the DataSet without marking them as deleted.
	 */

	/**
	 * Deletes all rows of data visible in the DataSet. If called on a detail DataSet, only the rows in the current group are deleted. Deletion status information
	 * is tracked for resolution back to the original data source. If all rows of the DataSet cannot be deleted, this method throws a DataSetException.
	 *
	 * @see #emptyAllRows to remove visible rows in the DataSet without marking them as deleted.
	 */
	public final void deleteAllRows()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (!allowDelete)
			ValidationException.deleteNotAllowed();

		dataSetStore.deleteAllRows(this, false);
	}

	public String getTableName() {
		if (dataSetStore == null)
			return null;
		return dataSetStore.getTableName();
	}

	public String getSchemaName() {
		if (dataSetStore == null)
			return null;
		return dataSetStore.getSchemaName();
	}

	/**
	 * Assigns the value stored at the intersection of the specified columnName and row to value.
	 *
	 * @param columnName
	 *          The String name of the Column.
	 * @param row
	 *          The unique row identifier for the row.
	 * @param value
	 *          The value that is assigned by executing this method.
	 */
	public final void getVariant(String columnName, int row, Variant value)
	/*-throws DataSetException-*/
	{
		getVariant(columnList.getOrdinal(columnName), row, value);
	}

	public final void getVariant(String columnName, long row, Variant value)
	/*-throws DataSetException-*/
	{
		getVariant(columnList.getOrdinal(columnName), row, value);
	}

	/**
	 * Assigns the value stored at the intersection of the specified Column and row to value.
	 *
	 * @param ordinal
	 *          An integer value that represents the nth Column in the DataSet where the Variant value is located.
	 * @param row
	 *          A integer value representing the unique row identifier for the row.
	 * @param value
	 *          The value that is assigned by executing this method.
	 * @see #getDisplayVariant(int, int, com.borland.dx.dataset.Variant)
	 */
	public final void getVariant(int ordinal, int row, Variant value)
	/*-throws DataSetException-*/
	{
		getVariant(ordinal, (long) row, value);
	}

	public final void getVariant(int ordinal, long row, Variant value)
	/*-throws DataSetException-*/
	{

		if (!open)
			failIfNotOpen();

		if (editing) {
			if (this.currentRow == row) {
				value.setVariant(getVariantStorage(ordinal));
				return;
			}
			// Take pseudo row into account.
			//
			if (newRow && row > this.currentRow)
				--row;
		}

		if (index.lastRow() < 0)
			value.setUnassignedNull();
		else
			dataSetStore.getStorageVariant(index.internalRow(row), ordinal, value);
	}

	/**
	 * @since JB2.0 Called by data aware controls. Sets value to the value that should be displayed in the visual control. Normally this is the same value that
	 *        would be retrieved from a call to getVariant(). If the column at the ordinal position has its PickList property set, value may be initialized to the
	 *        value from another column in a separate pickList dataSet. This provides the capability to store a compact "code" value, but display a more
	 *        "descriptive" value. This behavior is enabled when the column at position ordinal has its CalcType property set to LOOKUP and it has a PickList
	 *        property setting with lookupDisplayColumn property is set to a column name in the PickList pickListDataSet.
	 * 
	 * @see Column.PickList
	 */
	/**
	 * Called by data-aware controls. Sets the value parameter to the value that should be displayed in the visual control. Normally this is the same as what
	 * {@link #getVariant(int, int, com.borland.dx.dataset.Variant)} returns. If the column at the ordinal position has its pickList property set, the value
	 * parameter may be initialized to the value from another column in a separate pick list DataSet. This provides the capability to store a compact "code"
	 * value, but display a more "descriptive" value. This behavior is enabled when the column at position ordinal has its calcType property set to LOOKUP and it
	 * has a defined PickListDescriptor with its lookupDisplayColumn property set to a column name in the pick list DataSet.
	 *
	 * @param ordinal
	 * @param row
	 * @param value
	 */
	public final void getDisplayVariant(int ordinal, int row, Variant value)
	/*-throws DataSetException-*/
	{
		getDisplayVariant(ordinal, (long) row, value);
	}

	public final void getDisplayVariant(int ordinal, long row, Variant value)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (dataSetStore.getMaxExtraRows() > index.lastRow() - row) {
			if (dataSetStore.provideMoreData())
				;// rowNavigatedDispatch(); RAID 219287
		}

		Column column = getColumn(ordinal);
		if (column.lookup != null) {
			if (editing && row == currentRow)
				column.lookup.lookup(this, value);
			else
				column.lookup.lookup(this, row, value);
		} else
			getVariant(ordinal, row, value);
	}

	public final String format(String columnName)
	/*-throws DataSetException-*/
	{
		return format(getColumn(columnName).getOrdinal());
	}

	public final String format(int ordinal) {
		Column column = getColumn(ordinal);
		Variant value = getVariantStorage(ordinal);
		getDisplayVariant(ordinal, currentRow, value);
		return column.format(value);
	}

	/**
	 * @since JB2.0 Called by data aware controls. Performs the same operation as setVariant() if there is no PickList property with LookupDisplayColumn.
	 * 
	 *        If the Column at the ordinal position does have a PickList property set with LookupDisplayColumn, then value is assumed to be a LookupDisplayColumn
	 *        value and all the corresponding fillin columns are assigned if value can be found in the pickListDataSet.
	 * 
	 * 
	 * @see Column.PickList
	 */

	/**
	 * Called by data aware controls. Sets the value parameter to the value that should be displayed in the visual control. Normally this is the same as what
	 * {@link #getVariant(int, int, com.borland.dx.dataset.Variant)} returns.
	 * <p>
	 * If the column at the ordinal position has its {@link com.borland.dx.dataset.Column} property set, the value parameter may be initialized to the value from
	 * another column in a separate pick list DataSet. This provides the capability to store a compact "code" value, but display a more "descriptive" value. This
	 * behavior is enabled when the column at position ordinal has its com.borland.dx.dataset.Column.calcType property set to LOOKUP and it has a defined
	 * {@link PickListDescriptor} with its lookupDisplayColumn property set to a column name in the pick list DataSet.
	 *
	 * @param ordinal
	 * @param value
	 */
	public final void setDisplayVariant(int ordinal, Variant value)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		Column column = getColumn(ordinal);
		if (column.lookup != null) {
			DiagnosticJLimo.println("setDisplayVariant:  " + column.getColumnName() + " " + Variant.typeName(value.getType()));
			column.lookup.fillIn(this, value);
		} else
			setVariant(ordinal, value);
	}

	/*
	 * Override of ReadWriteRow implementation. Should not use externally.
	 */
	final void rowEdited()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (!editing) {
			dataSetStore.startEditing();
			if (index.lastRow() == -1)
				insertRow(true);
			else
				editRow();
		}
		rowDirty = true;
	}

	/*
	 * Override of ReadWriteRow implementation. Should not use externally.
	 */
	void processColumnPost(RowVariant value) /*-throws DataSetException-*/ {
		if (value.column.hasValidations && value.doValidations)
			value.validateAndSet(this);
		if (notifyColumnPost)
			notifyColumnPost(value);
	}

	void notifyColumnPost(RowVariant value) /*-throws DataSetException-*/
	{
		if (!value.column.inValidateOrChanged()) {
			if (value.column.getCalcType() == CalcType.NO_CALC) { // Avoid recursion because calcs will be posted.
				dataSetStore.calcUnpostedFields(this);
			}

			dataChangeListenersDispatch(new DataChangeEvent(this, DataChangeEvent.ROW_CHANGED, currentRow));
		}
	}

	/**
	 * Returns <b>true</b> if validation constraints are in affect for this row.
	 * 
	 * @return <b>true</b> if validation constraints are in affect for this row.
	 */
	public final boolean hasValidations() {
		return hasValidations;
	}

	/*
	 * Override of ReadWriteRow implementation. Should not use externally because it returns internal Variant storage. Returns internal storage, do not make
	 * public.
	 */
	final RowVariant getVariantStorage(String columnName)
	/*-throws DataSetException-*/
	{
		return getVariantStorage(columnList.getOrdinal(columnName));
	}

	// Do not make public, called by trusted code.
	//
	final Variant[] getRowValues() {
		return rowValues;
	}

	final Variant[] getOriginalValues() {
		if (originalValues == null)
			this.originalValues = dataSetStore.allocateValues();
		return originalValues;
	}

	// Returns internal variant storage - do not make public.
	//
	final RowVariant getVariantStorage(int ordinal)
	/*-throws DataSetException-*/
	{
		if (editing && !columnList.cols[ordinal].isLookupOrAggregate())
			return rowValues[ordinal];
		else {
			if (!open)
				DataSetException.dataSetNotOpen();
			if (currentRow == 0 && getLongRowCount() < 1)
				return RowVariant.nullVariant;

			if (needsSynch != TriStateProperty.FALSE)
				synchRow();

			return dataSetStore.getVariantStorage(this, ordinal);
		}
	}

	/**
	 * Applies min, max, required, and precision checks. Calls {@link com.borland.dx.dataset.Column#validate(DataSet, Variant)} method if it is set. Note that
	 * these checks will be made as data is edited in the DataSet, and when the changes are saved back to the database.
	 */
	public final void validate()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (hasValidations) {
			for (int ordinal = 0; ordinal < rowValues.length; ++ordinal) {
				getVariantStorage(ordinal).validateAndSet(this);
			}
		}
	}

	/*
	 * Applies min, max, required, precision checks. Calls Column.validate() method if it is set. Note that these checks will be made as data is edited in the
	 * DataSet and when the changes are saved back to the database
	 */
	public void validate(ReadRow readRow)
	/*-throws DataSetException-*/
	{

		if (validateRow == null)
			validateRow = new DataRow(this, true);
		readRow.copyTo(validateRow);
		validateRow.requiredColumnsCheck();
		validateRow.validate();
	}

	/*
	 * @deprecated use getStatus() return true if editing a "new" record.
	 */

	// bad name, should be called isInserted.
	//
	/**
	 * @param row
	 *          The row parameter represents the unique row identifier for the row.
	 * @return <b>true</b> if he row indicated by the row parameter is new. Otherwise, this method returns false.
	 */
	public final boolean isNew(int row)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (isEditingNewRow())
			return false;
		return dataSetStore.isNewRow(index.internalRow(row));
	}

	/**
	 * Returns the status for the current row of the DataSet.
	 * 
	 * @return The status for the current row of the DataSet.
	 */
	public final int getStatus()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (editing && newRow)
			return RowStatus.INSERTED;
		return dataSetStore.getStatus(this);
	}

	// ! /*
	// ! * @deprecated Use resetPendingStatus()
	// ! public final void resolvePending(boolean resolved) /*-throws DataSetException-*/ {
	// ! resetPendingStatus(resolved);
	// ! }
	// ! */

	/*
	 * Marks a row as pending resolution. Used internally when saving changes from a DataSet to a remote data provider that supports transactions (ie JDBC
	 * connections)
	 */
	// DO NOT MAKE public. see ProviderHelp.
	//
	final void markPendingStatus(boolean on)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			index.markStatus(currentRow, RowStatus.PENDING_RESOLVED, on);
		}
	}

	// ! /** Copy a row from sourceDataSet to this dataSet.
	// ! */
	// ! /*
	// ! public final void copyRow(int row, DataSet sourceDataSet, int sourceRow)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! dataSetStore.copyRow(this, row, sourceDataSet, sourceRow);
	// ! }
	// ! */

	// Must be a DataRow to ensure Column level validate and constraints applied.
	//

	/**
	 * Updates all values in the current row with those in the dataRow. If the DataSet is not open, a DataSetException of DataSetNotOpen is generated.
	 * 
	 * @param dataRow
	 */
	public final void updateRow(DataRow dataRow)
	/*-throws DataSetException-*/
	{
		Column[] updateColumns;
		if (dataRow.columnList.hasScopedColumns())
			updateColumns = dataRow.columnList.getScopedArray();
		else
			updateColumns = null;

		updateRow(-1, dataRow, updateColumns);
	}

	/**
	 * Made public by LimoSys to allow external updates of other rows beside current
	 * 
	 * @param updateRow
	 * @param dataRow
	 * @param updateColumns
	 */
	public final void updateRow(long updateRow, DataRow dataRow, Column[] updateColumns)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (!allowUpdate || !dataSetStore.allowUpdate)
			ValidationException.updateNotAllowed();

		dataRow.validate();

		if (dataSetStore.aggManager != null)
			dataSetStore.aggManager.getLookupData(dataRow, dataRow);

		if (dataSetStore.editListeners == null)
			_updateRow(updateRow, dataRow, updateColumns);
		else {
			while (true) {
				try {
					goToInternalRow(updateRow);
					_updateRow(updateRow, dataRow, updateColumns);
				} catch (DataSetException ex) {
					ErrorResponse response = dataSetStore.processUpdateError(this, dataRow, ex);
					if (response.isRetry())
						continue;
					else if (response.isAbort())
						throw ex;
					else
						return;
				}
				break;
			}
			dataSetStore.processUpdated(this);
		}
		dataSetStore.processRowChangePosted(internalRow);
	}

	private final void _updateRow(long updateRow, DataRow dataRow, Column[] updateColumns)
	/*-throws DataSetException-*/
	{
		if (editing && (newRow || rowDirty)) {
			Column[] columns = dataRow.columnList.getScopedArray();
			for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
				// ! Diagnostic.println("count: "+count+" "+ordinal);
				setVariant(columns[ordinal].getColumnName(), dataRow.getVariantStorage(ordinal));
			}
			// dataRow.copyTo(this);
			post();
		} else {
			synchronized (dataMonitor) {
				if (updateRow != -1) {
					needsSynch = TriStateProperty.TRUE;
					internalRow = updateRow;
				}
				if (updateColumns == null || updateColumns.length == columnList.count) {
					if (originalValues == null)
						getOriginalValues();
					_synchRow();
					dataSetStore.getRowData(internalRow, originalValues);
					dataSetStore.updateRow(this, internalRow, originalValues, dataRow, updateColumns);
				} else
					dataSetStore.updateRow(this, internalRow, null, dataRow, updateColumns);
			}
		}
	}

	/**
	 * Adds a new row to the DataSet at the end of existing rows and sets the new row's RowStatus to INSERTED. The new row is validated against data constraints
	 * (if any) to ensure that only valid data is added.
	 *
	 * @param dataRow
	 *          The DataRow to add to the current DataSet.
	 */
	public final void addRow(DataRow dataRow) {
		addRowReturnInternalRow(dataRow);
	}

	/**
	 * Add a row to underlying dataset. Like addRow(DataRow) except the internalRow of the row added is returned.
	 *
	 * @see #insertRow(boolean before)
	 * @param dataRow
	 *          The row to be added to the underlying dataset.
	 * @return The internalRow of the row added.
	 */
	public final long addRowReturnInternalRow(DataRow dataRow)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (!allowInsert)
			ValidationException.insertNotAllowed();

		dataRow.validate();

		if (dataSetStore.aggManager != null)
			dataSetStore.aggManager.getLookupData(dataRow, dataRow);

		long row;
		if (dataSetStore.editListeners == null)
			row = dataSetStore.storageAddRow(this, dataRow);
		else {
			while (true) {
				try {
					dataSetStore.processAdding(this, dataRow);
					row = dataSetStore.storageAddRow(this, dataRow);
				} catch (DataSetException ex) {
					row = -1;
					ErrorResponse response = dataSetStore.processAddError(this, dataRow, ex);
					if (response.isRetry())
						continue;
					else if (response.isAbort())
						throw ex;
				}
				break;
			}
			internalRow = row;
			needsSynch = TriStateProperty.TRUE;
			dataSetStore.processAdded(this);
		}
		return row;
	}

	/**
	 * Returns all the values at row.
	 * 
	 * @param row
	 *          The unique rowId for a row in the DataSet.
	 * @param dataRow
	 *          The DataRow object that gets its values from the row indicated by the row parameter when this method is executed.
	 */
	public final void getDataRow(int row, DataRow dataRow)
	/*-throws DataSetException-*/
	{
		getDataRow((long) row, dataRow);
	}

	public final void getDataRow(long row, DataRow dataRow)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (editing && row == currentRow)
			copyTo(dataRow);
		else
			dataSetStore.getRowData(this, newRow && row > currentRow ? row - 1 : row, dataRow);
	}

	/**
	 * Returns all the values at the current row.
	 * 
	 * @param dataRow
	 *          The DataRow object that gets its values from the current row position when this method is executed.
	 */
	public final void getDataRow(DataRow dataRow)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		if (editing)
			copyTo(dataRow);
		else
			dataSetStore.getRowData(this, currentRow, dataRow);
	}

	/**
	 * Add a listener for open/close access events.
	 * 
	 * @param listener
	 *          The listener for open/close access events.
	 */
	public final void addAccessListener(AccessListener listener) {
		accessListeners = EventMulticaster.add(accessListeners, listener);
	}

	public final void removeAccessListener(AccessListener listener) {
		accessListeners = EventMulticaster.remove(accessListeners, listener);
	}

	/**
	 * Add a listener for NavigationEvent dispatches.
	 * 
	 * @param listener
	 *          The listener for NavigationEvent dispatches.
	 */
	public final void addNavigationListener(NavigationListener listener) {
		navigationListeners = EventMulticaster.add(navigationListeners, listener);
		if (navigationEvent == null)
			this.navigationEvent = new NavigationEvent(this);
	}

	public final void removeNavigationListener(NavigationListener listener) {
		navigationListeners = EventMulticaster.remove(navigationListeners, listener);
	}

	/**
	 * Add a listener for DataChangeEvent dispatches.
	 * 
	 * @param listener
	 *          The listener for DataChangeEvent dispatches.
	 */
	public final void addDataChangeListener(DataChangeListener listener) {
		dataChangeListeners = EventMulticaster.add(dataChangeListeners, listener);
		notifyColumnPost = (dataChangeListeners != null);
	}

	public final void removeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners = EventMulticaster.remove(dataChangeListeners, listener);
		notifyColumnPost = (dataChangeListeners != null);
	}

	private final void notifyDataChangeStatus(int code, int resId) {
		if (statusListeners != null)
			statusListeners.dispatch(new StatusEvent(this, code, Res.bundle.getString(resId)));
	}

	public final void setDisplayErrors(boolean displayErrors) {
		this.displayErrors = displayErrors;
	}

	public final boolean isDisplayErrors() {
		return displayErrors;
	}

	/**
	 * Instructs all status listeners (for example, the StatusBar control) to clear their status messages.
	 */
	public final void clearStatus() {
		if (statusListeners != null)
			statusListeners.dispatch(new StatusEvent(this, StatusEvent.CLEAR, ""));
	}

	boolean displayError(Throwable ex) {
		if ((ex instanceof ValidationException || !displayErrors) && statusListeners != null) {
			statusListeners.dispatch(new StatusEvent(this, ex));
			return false;
		}

		return displayErrors;

		// !RC 6/2/97 Steve and I agreed hadOpenError was obsolete
		// !RC TODO remove stale code when appropriate
		// !/*
		// ! * else if (displayErrors) {
		// ! * if (isOpen() || !hadOpenError) {
		// ! * // Problematic. will not show another until a successful open done.
		// ! * //
		// ! * //hadOpenError = true;
		// ! * return true;
		// ! * }
		// ! * }
		// ! * return false;
		// ! */

	}

	/**
	 * Add a listener for StatusEvent dispatches.
	 * 
	 * @param listener
	 *          The listener for StatusEvent dispatches.
	 */
	public final void addStatusListener(StatusListener listener) {
		statusListeners = EventMulticaster.add(statusListeners, listener);
	}

	public final void removeStatusListener(StatusListener listener) {
		statusListeners = EventMulticaster.remove(statusListeners, listener);
	}

	/**
	 * Add a listener for MasterNavigate dispatches.
	 * 
	 * @param listener
	 *          The listener for MasterNavigate dispatches.
	 */
	public final void addMasterNavigateListener(MasterNavigateListener listener) {
		DiagnosticJLimo.trace(Trace.Master, "adding MasterDataSet listener");
		masterNavigateListeners = DxMulticaster.add(masterNavigateListeners, listener);
		if (masterNavigatedEvent == null) {
			this.masterNavigatedEvent = new MasterNavigateEvent(this, false, MasterNavigateEvent.NAVIGATED);
			this.masterNavigatingEvent = new MasterNavigateEvent(this, false, MasterNavigateEvent.NAVIGATING);
		}
	}

	public final void removeMasterNavigateListener(MasterNavigateListener listener) {
		DiagnosticJLimo.trace(Trace.Master, "removing MasterDataSet listener");
		masterNavigateListeners = DxMulticaster.remove(masterNavigateListeners, listener);
	}

	public final void addRowFilterListener(RowFilterListener listener)
			throws TooManyListenersException {
		if (listener == null)
			throw new IllegalArgumentException();

		if (rowFilterListener != null)
			throw new TooManyListenersException();

		rowFilterListener = listener;
	}

	public final void removeRowFilterListener(RowFilterListener listener) {
		rowFilterListener = null;
	}

	public final void addOpenListener(OpenListener listener)
			throws TooManyListenersException {
		if (listener == null)
			throw new IllegalArgumentException();

		if (openListener != null)
			throw new TooManyListenersException();

		openListener = listener;
	}

	public final void removeOpenListener(OpenListener listener) {
		if (openListener != listener)
			throw new IllegalArgumentException();
		openListener = null;
	}

	public final RowFilterListener getRowFilterListener() {
		return rowFilterListener;
	}

	/**
	 * Specifies the SortDescriptor object where properties that define the sort order of the data in a DataSet are stored. This property is accessed in the
	 * JBuilder Inspector from the sort property.
	 * <p>
	 * Setting the sort descriptor generates a dispatch to the registered AccessListener object.
	 *
	 * @return The SortDescriptor object where properties that define the sort order of the data in a DataSet are stored.
	 */
	public final SortDescriptor getSort() {
		// ! Diagnostic.println("DataSet.getSort() on " + this + " --> " + descriptor);
		return descriptor;
	}

	String[] getSortKeys() {
		if (descriptor == null)
			return null;
		return descriptor.getKeys();
	}

	// Do not make public. Used by detail StorageDataSet initialization.
	//
	final void resetSort(SortDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	// !Used by at designTime to safely attempt reopening
	// !if the dataSetStore was open before a property was set.
	// !SteveS TODO. propagate this to designTime tools?
	//
	// !designerHandlesThis private void designTimeOpen() {
	// !designerHandlesThis try{ open();}
	// !designerHandlesThis catch(Exception ex) {
	// !designerHandlesThis Diagnostic.printStackTrace(ex);
	// !designerHandlesThis }
	// !designerHandlesThis }

	private final void closeProvider()
	/*-throws DataSetException-*/
	{
		StorageDataSet temp = dataSetStore;
		if (temp != null)
			temp.closeProvider(false);
	}

	/**
	 * Specifies the SortDescriptor object where properties that define the sort order of the data in a DataSet are stored. This property is accessed in the
	 * JBuilder Inspector from the sort property.
	 * <p>
	 * Setting the sort descriptor generates a dispatch to the registered AccessListener object. On error, setSort() generates a DataSetException.
	 *
	 * @param descriptor
	 *          The SortDescriptor object where properties that define the sort order of the data in a DataSet are stored.
	 */
	public final void setSort(SortDescriptor descriptor)
	/*-throws DataSetException-*/
	{
		// !Ignore sorts if we are replacing the rows being loaded.
		// ! if (open && dataSetStore.isReplacingLoadRows())
		// ! return;

		// ! Load the rest of the data if any rows are pending before sorting:
		if (openComplete)
			closeProvider();

		if (descriptor != null)
			descriptor.check();

		synchronized (getOpenMonitor(open)) {
			synchronized (this) {
				long saveInternalRow = internalRow;
				if (open)
					saveInternalRow = getInternalRow();
				// ! Diagnostic.println("toggleViewOrder isOpen(): "+isOpen());
				boolean wasOpen = open;
				if (open)
					dataSetStore.closeStorage(AccessEvent.STRUCTURE_CHANGE);
				DiagnosticJLimo.check(!isOpen());

				this.descriptor = descriptor;

				DiagnosticJLimo.check(!isOpen());
				if (wasOpen) {
					open(new AccessEvent(this, AccessEvent.OPEN, AccessEvent.DATA_CHANGE));
					goToRow(index.findClosest(saveInternalRow));
				}
			}
		}
	}

	/**
	 * Toggles the view order of the DataSet from ascending to descending (and vice versa) given the values in the specified columnName.
	 *
	 * @param columnName
	 */
	public synchronized void toggleViewOrder(String columnName)
	/*-throws DataSetException-*/
	{
		boolean descending = false;
		String oldKeys[] = getSortKeys();
		if (oldKeys != null && oldKeys.length == 1 && oldKeys[0].equals(columnName) && !descriptor.isDescending(0)) {
			descending = true;
		}

		setSort(new SortDescriptor(new String[] { columnName }, false, descending));
	}

	/**
	 * Affects whether data-aware controls ignore changes or repaint as a result of programmatic changes to the position and general state of a DataSet. A control
	 * cannot repaint properly while the DataSet events are disabled.
	 * <p>
	 * When performing lengthy DataSet operations, you may want to set this property to <b>false</b> prior to initiating such operations. If you do, you must set
	 * this property back to <b>true</b> to allow UI controls to repaint properly. You may also want to execute this method to propagate changes made to a DataSet
	 * object's structure to a control that is bound to the DataSet.
	 *
	 * @param enable
	 */
	public void enableDataSetEvents(boolean enable)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (accessListeners != null) {
			if (!enable)
				dispatchAccessEvent(accessListeners, new AccessEvent(this, AccessEvent.CLOSE, AccessEvent.STRUCTURE_CHANGE));
			else
				dispatchAccessEvent(accessListeners, new AccessEvent(this, AccessEvent.OPEN, AccessEvent.DATA_CHANGE));
		}
	}

	/**
	 * Specifies the most recent Column that was navigated to in a data-aware control. This method is used by the interactiveLocate() method as the default Column
	 * to locate on if its parameter of a target Column for the locate is not specified.
	 *
	 * @param columnName
	 *          The most recent Column that was navigated to in a data-aware control.
	 */
	public final void setLastColumnVisited(String columnName) {
		lastColumnVisited = columnName;
	}

	public final String getLastColumnVisited() {
		return lastColumnVisited;
	}

	/**
	 * Used for interactive (incremental) searching on a single column. Some what inefficient if used progrmatically. If locateColumnName is null, will attempt to
	 * use last column visited (see setLastColumnVisited()). If lastColumnVisted is not set, first column in StorageDataSet used. Used by LocatorControl.
	 */
	/**
	 * Searches the specified Column of the DataSet for the value specified in text.
	 *
	 * @param text
	 *          The String representation of the value to search for.
	 * @param locateColumnName
	 *          The String name of the Column in which to perform the search. If null, this method attempts to use the last column visited (as returned by
	 *          {@link #getLastColumnVisited()}). If the last column visited was not set, the first Column in the StorageDataSet is used.
	 * @param locateOptions
	 *          An integer value representing the locate options to use. See {@link com.borland.dx.dataset.Locate} for valid values for this parameter.
	 * @param enterPressed
	 *          Specifies whether the Enter key should be pressed prior to initiating the search or if each key press should be used to perform an incremental
	 *          search. Incremental searches are available only for String searches.
	 */
	public void interactiveLocate(String text,
			String locateColumnName,
			int locateOptions,
			boolean enterPressed)
	/*-throws DataSetException-*/
	{

		try {
			// ! Diagnostic.println("interactiveLocate "+text+" "+locateColumnName);
			// ! Diagnostic.println("locateOptions: "+Locate.toString(locateOptions));
			if (locateColumnName == null) {
				locateColumnName = lastColumnVisited;
				if (locateColumnName == null && getColumnCount() > 0)
					locateColumnName = getColumn(0).getColumnName();
				// ! Diagnostic.println(locateColumnName+" lastColumnVisited: "+lastColumnVisited);
			}
			if (locateColumnName != null) {
				DataRow dataRow = new DataRow(this, locateColumnName);
				Column locateColumn = dataSetStore.getColumn(locateColumnName);
				if (locateColumn.getDataType() == Variant.STRING) {
					DiagnosticJLimo.println("locating string column:  " + locateColumnName);
					dataRow.setString(locateColumnName, text);
					interactiveLocate(dataRow, locateOptions | Locate.PARTIAL);
					if (text.length() < 1)
						statusMessage(StatusEvent.LOCATE_STRING, Res.bundle.getString(ResIndex.LocateString));
				} else if (enterPressed || (locateOptions & (Locate.NEXT | Locate.PRIOR)) != 0) {
					int dataType = locateColumn.getDataType();
					Variant value = new Variant(dataType);
					locateColumn.getFormatter().parse(text, value);
					dataRow.setVariant(locateColumnName, value);
					interactiveLocate(dataRow, locateOptions);
					if (text.length() < 1)
						statusMessage(StatusEvent.LOCATE_NON_STRING, Res.bundle.getString(ResIndex.LocateNonString));
				} else
					statusMessage(StatusEvent.LOCATE_USE_ENTER, Res.bundle.getString(ResIndex.PressEnterToSearch));
			}
		} catch (InvalidFormatException ex) {
			ValidationException.invalidFormat(ex, null, null);
		}
	}

	private final void interactiveLocate(DataRow searchRow, int locateOptions)
	/*-throws DataSetException-*/
	{
		if (locate(searchRow, locateOptions))
			statusMessage(StatusEvent.LOCATE_MATCH_FOUND, Res.bundle.getString(ResIndex.MatchFound));
		else
			statusMessage(StatusEvent.LOCATE_MATCH_NOT_FOUND, Res.bundle.getString(ResIndex.MatchNotFound));
	}

	public boolean isOpen() {
		return open;
	}

	void failIfOpen()
	/*-throws DataSetException-*/
	{
		if (open)
			DataSetException.dataSetOpen();
	}

	final void failIfNotOpen()
	/*-throws DataSetException-*/
	{
		if (!open)
			DataSetException.dataSetNotOpen();
	}

	/**
	 * This method returns <b>true</b> if the DataSet is closed by executing this method and <b>false</b> if the DataSet does not need closing (for example, it is
	 * already closed). Closing a DataSet allows for structural changes to be made to the DataSet, such as adding a new Column or changing the sort order, and so
	 * on. All changes are applied to the data currently in the DataSet. The close() method does not discard any inserted, edited or deleted rows in the DataSet.
	 * For the DataSetView subclass of DataSet, the close() method must be called when explicitly removing an instantiated DataSetView be to garbage-collected.
	 * Otherwise, the instantiated DataSetView remains allocated in memory as long as its associated StorageDataSet cannot be garbage-collected. If you connect to
	 * your data source using a QueryDataSet and its query statement contains parameters, you need to call DataSet.close() before providing for another
	 * QueryDataSet.
	 *
	 * @return <b>true</b> if the DataSet is closed by executing this method and <b>false</b> if the DataSet does not need closing.
	 */
	public boolean close()
	/*-throws DataSetException-*/
	{
		closeProvider();

		return close(false, AccessEvent.UNKNOWN, true);
	}

	/**
	 * Posts all unposted rows for all DataSet and DataSetView components that share the same StorageDataSet property.
	 */
	public void postAllDataSets()
	/*-throws DataSetException-*/
	{
		DataSet dataSetStoreCopy = dataSetStore;
		if (dataSetStoreCopy != null) {
			if (this != dataSetStoreCopy)
				dataSetStoreCopy.postAllDataSets();
		}
	}

	// static int counter;
	// int bugcount;
	// !JOAL: This method might not need to be synchronized if the misc.
	// !listeners were reference copied before use.
	final boolean close(boolean preserveAccessListener, int reason, boolean closeData)
	/*-throws DataSetException-*/
	{
		boolean wasOpen = false;
		if (open) {
			// bugcount = ++counter;
			// if (bugcount == 103)
			// Diagnostic.println("stop:");
			// Diagnostic.printStackTrace();
			synchronized (getOpenMonitor()) {
				synchronized (this) {

					if (iterators != null) {
						RowIterator i = iterators;
						while (i != null) {
							i.unBind(false);
							i = i.next;
						}
					}

					if (openListener != null)
						openListener.closing(this);

					try {
						// !JOAL: Bug12739 Move the accessEvent dispatch outside the dataMonitor

						// !Protocal is to notify an impending close BEFORE setting the closed
						// !state. Receivers may access the dataSet when this event comes. See
						// !bug 5531 for an example.
						//
						if (openComplete && accessListeners != null)
							dispatchAccessEvent(accessListeners, new AccessEvent(this, AccessEvent.CLOSE, reason));

						if (dataMonitor == null) {
							// Probably get here because open failed.
							// Try to shut down the DataSet so it can be reopened if problem is corrected.
							//
							DiagnosticJLimo.check(!openComplete);
							open = false;
							if (dataSetStore != this)
								this.dataMonitor = this;
							index = null;
						} else {
							synchronized (dataMonitor) {
								if (editing)
									_post(); // !cancel();

								open = false;
								wasOpen = true;
								if (dataSetStore != this)
									this.dataMonitor = this;
								dataSetStore.removeStorageDataChangeListener(this);
								dataSetStore.removeStorageStatusListener(this);
								if (!preserveAccessListener)
									dataSetStore.removeStorageAccessListener(this);

								index = null;
								originalValues = null;
								if (dataSetStore == this)
									dataSetStore.closeStorage(reason, closeData);

								if (detailIndex != null) {
									detailIndex.close(this, preserveAccessListener);
									detailIndex = null;
								}
								openComplete = false;
							}
						}
					} finally {
						// ! BUG17788, JOAL, reactivate the UI
						// ! The _post or the accessEvent failed, notify listeners that the close didn't happen!
						if (open) {
							dispatchOpenAccessEvent(accessListeners, this, null);
						}
					}

					// !JOAL: Move the openEvent dispatch outside the dataMonitor
					if (openListener != null)
						openListener.closed(this);
				}
			}
		} else {
			if (dataSetStore != null) {
				// Warning: If this is not done, The StorageDataSet will always reference
				// this DataSet and the DataSet can no longer be garbage collected.
				//
				if (!preserveAccessListener)
					dataSetStore.removeStorageAccessListener(this);
				if (closeData && dataSetStore == this)
					dataSetStore.closeData(reason, closeData);
			}
		}

		return wasOpen;
	}

	// Don't make public. Utility method that is also used by StorageDataSet.
	//
	static void dispatchOpenAccessEvent(EventMulticaster accessListeners,
			Object source,
			AccessEvent event)
	/*-throws DataSetException-*/
	{
		if (accessListeners != null) {
			if (event == null)
				event = new AccessEvent(source, AccessEvent.OPEN);
			else
				event = new AccessEvent(source, event);
			dispatchAccessEvent(accessListeners, event);
		}
	}

	// Don't make public. Utility method that is also used by StorageDataSet.
	//
	static void dispatchAccessEvent(EventMulticaster accessListeners,
			AccessEvent event)
	/*-throws DataSetException-*/
	{
		if (accessListeners != null) {
			accessListeners.dispatch(event);
			if (event.getExceptionChain() != null)
				DataSetException.throwException(DataSetException.REOPEN_FAILURE,
																				Res.bundle.getString(ResIndex.ReopenFailure),
																				event.getExceptionChain());
		}
	}

	/**
	 * If the DataSet is not open, this method attempts to open the DataSet.
	 * 
	 * @return The method returns <b>true</b> if the DataSet can be successfully opened. If the DataSet is already open, the method returns <b>false</b>.
	 */
	public boolean open()
	/*-throws DataSetException-*/
	{
		return open(null);
	}

	private final Object getOpenMonitor()
	/*-throws DataSetException-*/
	{
		StorageDataSet temp = dataSetStore;
		if (temp == null)
			return this;
		return temp.getOpenMonitor();
	}

	private final Object getOpenMonitor(boolean open)
	/*-throws DataSetException-*/
	{
		if (!open)
			return this;
		return getOpenMonitor();
	}

	boolean open(AccessEvent event)
	/*-throws DataSetException-*/
	{
		if (isOpen())
			return false;
		if (openListener != null)
			openListener.opening(this);

		synchronized (getOpenMonitor()) {
			synchronized (this) {
				if (resolverStorageDataSet != null) {
					StorageDataSet temp = resolverStorageDataSet;
					resolverStorageDataSet = null;
					temp.open();
					temp.removeStorageAccessListener(this);
					if (!temp.resolvable)
						return true;
					switch (visibleMask) {
						case RowStatus.DELETED:
							temp.getDeletedRows((DataSetView) this);
							return true;
						case RowStatus.INSERTED:
							temp.getInsertedRows((DataSetView) this);
							return true;
						case RowStatus.UPDATED:
							temp.getUpdatedRows((DataSetView) this);
							return true;
					}
					DiagnosticJLimo.fail();
				}
				if (dataSetStore != null) {
					dataSetStore.removeStorageAccessListener(this);
					openComplete = false;
					try {
						// Do this up front to avoid possible recursion when StorageDataSet
						// opened.
						//
						open = true;

						DetailIndex tempDetailIndex = null;
						// Open detail index before setting columnList, readRow
						// because could be something like a fetchAsNeeded Query. With these
						// we don't know what the columns are until the query is run for the first
						// time.
						//
						if (masterLink != null && masterLink.getMasterDataSet() != null)
							tempDetailIndex = new DetailIndex(this);

						dataSetStore.openStorage(this, event);
						this.dataMonitor = dataSetStore.dataMonitor;
						DiagnosticJLimo.check(dataMonitor != null);
						this.dataSetStore = dataSetStore;

						// set columnList before and after Open detail index
						// because could be something like a fetchAsNeeded Query will update the
						// columnList. With these we don't know what the columns are until the query is run for the first
						// time. Must also be set before for when a DataSetView is used
						// as the detail. Otherwise the DetailIndex.init() will fire an exception
						// if there are not columns in the detail and no rows in the master.
						//
						this.columnList = dataSetStore.columnList; // Must be done before and after.
						if (masterLink != null && masterLink.getMasterDataSet() != null) {
							tempDetailIndex.init();
							detailIndex = tempDetailIndex;
							index = tempDetailIndex;
						} else
							dataSetStore.openIndex(this);
						heapIndex = (descriptor != null && descriptor.isSortAsInserted());
						// Note that the DataSet just takes a reference. This is ok, because
						// the Dataset will be instructed to close/open (through AccessEvents)
						// if any structural changes are applied to the StorageDataSet.
						//
						this.columnList = dataSetStore.columnList; // Must be done before and after.
						setCompatibleList(columnList);
						this.readRow = new DataRow(dataSetStore, true);
						this.editing = false;
						this.rowDirty = false;
						this.newRow = false;
						this.isInBounds = true;
						initRowValues(true);

						// Must happen after openIndex so that index is set (not null)
						// when the dataSet becomes a listener to DataChange events.
						// Problem most notable when asynchronous fetch race conditions occur.
						//
						dataSetStore.addStorageDataChangeListener(this);
						dataSetStore.addStorageStatusListener(this);
						dataSetStore.addStorageAccessListener(this);

						// if _goToRow() called, will acquire a table lock in strong isolation
						// level.
						//
						currentRow = 0;
						internalRow = 0;
						needsSynch = TriStateProperty.DEFAULT;
						isInBounds = false;
						// _goToRow(0);

						if (iterators != null) {
							RowIterator i = iterators;
							while (i != null) {
								i.bind(this);
								i = i.next;
							}
						}

						dataSetStore.propSet |= PropSet.Opened;
						openComplete = true;
						if (notifyLoad) {
							dataSetStore.notifyLoad();
							notifyLoad = false;
						}
					} finally {
						if (!openComplete) {
							try {
								close();
							} catch (Exception ex) {
								DiagnosticJLimo.printStackTrace(ex);
							}
						}
					}
					dispatchOpenAccessEvent(accessListeners, this, event);
					// !RC TODO hadOpenError = false;
				}
			}
		}
		if (openListener != null)
			openListener.opened(this);
		return true;
	}

	/**
	 * ReadOnly property that specifies the name of StorageDataSet object that manages the storage of the rows that DataSet operates on.
	 * 
	 * @return The name of StorageDataSet object that manages the storage of the rows that DataSet operates on.
	 */
	public final StorageDataSet getStorageDataSet() {
		return dataSetStore;
	}

	/**
	 * Can edit values at column and row.
	 */

	/**
	 * Returns whether the values at the current row and specified Column can be edited.
	 * 
	 * @param column
	 *          The specified Column.
	 * @return <b>true</b> If the values at the current row and specified Column can be edited, <b>false</b> otherwise.
	 */
	public final boolean canSet(Column column)
	/*-throws DataSetException-*/
	{
		try {
			dataSetStore.startEditCheck(column);
		} catch (DataSetException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Calls editRow() if the column can be edited.
	 * 
	 * @param column
	 */
	public final void startEdit(Column column)
	/*-throws DataSetException-*/
	{
		startEditCheck(column);
		editRow();
	}

	/**
	 * Throws an exception that has informative error message if editing of column cannot be allowed.
	 * 
	 * @param column
	 */
	public final void startEditCheck(Column column)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		// In case performed from a DataSetView or a StorageDataSet, check here.
		if (!editable)
			ValidationException.readOnlyDataSet();

		if (getLongRowCount() < 1) {
			if (!allowInsert || !dataSetStore.allowInsert)
				ValidationException.insertNotAllowed();
		}

		if (!allowUpdate || !dataSetStore.allowUpdate)
			ValidationException.updateNotAllowed();

		dataSetStore.startEdit(this, column);
	}

	/*
	 * DO NOT MAKE public! User can access methods off of StorageDataSet to get dataSets that have only deleted, changed and inserted rows. Providing this to the
	 * user forces any future storage implementations to support a broader variety of data delta indexes. Set visible rows in the dataSet by row status mask.
	 * 
	 * @see RowStatus
	 */
	final void setVisibleMask(int mask, int invisibleMask)
	/*-throws DataSetException-*/
	{
		// Trying to limit the expected symantics of the underlying
		// data store.
		//
		DiagnosticJLimo.check(mask == RowStatus.DEFAULT
				|| mask == RowStatus.UPDATED
				|| mask == RowStatus.INSERTED
				|| mask == RowStatus.DELETED ? null : "Mask:  " + Integer.toHexString(mask));
		boolean moved = false;
		// !designerHandlesThis boolean wasOpen = Beans.isDesignTime() && close();
		failIfOpen();
		synchronized (this) {
			if (mask != 0) {
				if (mask != visibleMask || this.invisibleMask != invisibleMask) {
					this.visibleMask = mask;
					this.invisibleMask = invisibleMask;
					// ! /* This can't be right. _goToRow() requires dataSet to be open.
					// ! if (dataSetStore != null) {
					// ! dataSetStore.openIndex(this);
					// ! moved = _goToRow(0);
					// ! }
					// ! */
				}
			}
		}
		// !designerHandlesThis if (wasOpen)
		// !designerHandlesThis designTimeOpen();

		if (moved)
			rowNavigatedDispatch();
	}

	/**
	 * Returns whether the specified column can be navigated to (<b>true</b>) or not (<b>false</b>).
	 *
	 * This method returns false if a Column's readOnly property is set to <b>true</b>. This method is typically used by visual components.
	 *
	 * @param column
	 *          The Column component to navigate to.
	 * @param row
	 *          The row position in the DataSet.
	 * @return <b>true</b> if the specified column can be navigated to, <b>false</b> otherwise.
	 */
	public final boolean canNavigate(Column column, int row) {
		return !column.readOnly;
	}

	/**
	 * Return current row position.
	 * 
	 * @return The current row position.
	 */
	public final int getRow() {
		return (int) currentRow;
	}

	public final long getLongRow() {
		return currentRow;
	}

	/**
	 * @deprecated Use getRow() instead.
	 * @see #getRow()
	 * @return The current row position.
	 */
	public final int row() {
		return (int) getLongRow();
	}

	/**
	 * Moves to the specified row position, where row represents the unique row identifier for the row.
	 *
	 * @param row
	 *          The unique row identifier of the row to move to.
	 * @return Returns <b>true</b> if the move is successful, otherwise, it returns <b>false</b>.
	 */
	public final boolean goToRow(int row)
	/*-throws DataSetException-*/
	{
		return goToRow((long) row);
	}

	public final boolean goToRow(long row)
	/*-throws DataSetException-*/
	{
		boolean moved;
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {

			if (needsSynch != TriStateProperty.FALSE)
				_synchRow();

			moved = _goToRow(row);
		}
		if (moved)
			rowNavigatedDispatch();
		return moved;
	}

	/**
	 * Moves the row position to the closest row indicated by the row parameter.
	 *
	 * @param row
	 *          The unique row identifier for the row of the DataSet that this method should attempt to move to.
	 * @return This method returns <b>true</b> if the move is successful, otherwise, it returns <b>false</b>.
	 */
	public final boolean goToClosestRow(int row)
	/*-throws DataSetException-*/
	{
		return goToClosestRow((long) row);
	}

	public final boolean goToClosestRow(long row)
	/*-throws DataSetException-*/
	{
		boolean moved;
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			moved = _goToClosestRow(row);
		}
		if (moved)
			rowNavigatedDispatch();
		return moved;
	}

	// Internals

	private final void rowNavigatedDispatch() {
		if (navigationListeners != null) {
			DiagnosticJLimo.trace(Trace.Notifications, "DataSet.rowNavigatedDispatch:  ");
			navigationListeners.dispatch(navigationEvent);
		}
	}

	final void dataChangeListenersDispatch(DataChangeEvent event) {

		// Typically more optimal if caller makes test because caller may
		// be paying the price of allocating an event object if an event needs
		// to be sent.
		//
		DiagnosticJLimo.check(dataChangeListeners != null);

		// ! Diagnostic.println("DataSet.dataChangeListenersDispatch: "+event);
		DiagnosticJLimo.trace(Trace.Notifications, "DataSet.dataChangeListenersDispatch:  " + event);
		DiagnosticJLimo.check(event != null);
		dataChangeListeners.dispatch(event);
		DiagnosticJLimo.trace(Trace.Notifications, "DataSet.dataChangeListenersDispatch done");
	}

	private final void rowAddedDispatch(long row) {
		if (dataChangeListeners != null)
			dataChangeListenersDispatch(new DataChangeEvent(this, DataChangeEvent.ROW_ADDED,
					row));
	}

	private final void rowDeletedDispatch(long row) {
		if (dataChangeListeners != null)
			dataChangeListenersDispatch(new DataChangeEvent(this, DataChangeEvent.ROW_DELETED,
					row));
	}

	/*
	 * Sends event to all registered StatusListeners of this DataSet.
	 */
	public void statusMessage(StatusEvent event) {
		// ! /*
		// ! if (event.getCode() == StatusEvent.LOADING_DATA) {
		// ! try {
		// ! if (fixRowNeeded()) {
		// ! synchronized(dataMonitor) {
		// ! _fixRowPosition();
		// ! }
		// ! }
		// ! }
		// ! catch(DataSetException ex) {
		// ! Diagnostic.printStackTrace(ex);
		// ! }
		// ! }
		// !*/
		if (statusListeners != null)
			statusListeners.dispatch(event);
	}

	/**
	 *
	 * @param code
	 *          A status constant from StatusEvent
	 * @param message
	 *          The message to send to all of this DataSet's StatusListeners
	 */
	public void statusMessage(int code, String message) {
		if (statusListeners != null) {
			statusListeners.dispatch(new StatusEvent(this, code, message));
		}
	}

	final void postDataSet()
	/*-throws DataSetException-*/
	{
		try {
			post();
		} catch (Exception ex) {
			DataSetException.throwExceptionChain(ex);
		}
	}

	private final void dataChangedIterators() {
		RowIterator i = iterators;
		while (i != null) {
			i.needsSynch = true;
			i = i.next;
		}
	}

	final void dataChanged(int errorCode, long rowAffected)
	/*-throws DataSetException-*/
	{
		if (iterators != null)
			dataChangedIterators();

		if (detailIndex == null
				&& masterNavigateListeners == null
				&& dataChangeListeners == null) {
			needsSynch = TriStateProperty.TRUE;
		} else {
			if (detailIndex != null || fixRowNeeded()) {
				synchronized (dataMonitor) {
					if (rowAffected == internalRow && (editing && !newRow))
						_cancel();

					if (detailIndex != null)
						relinkDetail(false);

					if (fixRowNeeded())
						_fixRowPosition();
				}
			}

			/*
			 * Try to avoid notifying the listeners, especially if rowAffected is known (>=0). Note that a detail view can have its position set to the first row when
			 * this dispatch is made. That can throw off detail iteration logic. Especially if the detail is not affected by the data change.
			 */
			if (masterNavigateListeners != null && (getInternalRow() == rowAffected || rowAffected < 0)) {
				DiagnosticJLimo.trace(Trace.Master, "Master moving to new row ");
				masterNavigateListeners.dxDispatch(masterNavigatedEvent);
			}

			if (dataChangeListeners != null) {
				if (rowAffected > -1) {
					long dataSetRow;
					try {
						dataSetRow = index.findClosest(rowAffected);
					} catch (DataSetException ex) {
						dataSetRow = -1;
						DiagnosticJLimo.printStackTrace();
					}
					dataChangeListenersDispatch(new DataChangeEvent(this, errorCode, dataSetRow));
					if (errorCode == DataChangeEvent.ROW_DELETED) {
						// ! Diagnostic.println("row navigating");
						rowNavigatedDispatch();
					}
				} else
					dataChangeListenersDispatch(new DataChangeEvent(this, errorCode, -1));
			}
		}
	}

	public synchronized void accessChange(AccessEvent event) {
		if (event.getReason() == event.PROPERTY_CHANGE) {
			if (accessListeners != null) {
				try {
					AccessEvent e = event;
					if (this != event.getSource())
						e = new AccessEvent(this, event.getID(), event.getReason());
					dispatchAccessEvent(accessListeners, e);
				} catch (Exception ex) {
					ex.printStackTrace();
					event.appendException(ex);
				}
			}
		} else {
			switch (event.getID()) {
				case AccessEvent.OPEN:
					// ! Diagnostic.trace(Trace.AccessEvents,"DataSet.openAccess for: "+dataSetStore.getTableName()+" "+this);
					try {
						open(event);
					} catch (Exception ex) {
						ex.printStackTrace();
						event.appendException(ex);
					}
					break;
				case AccessEvent.CLOSE:
					DiagnosticJLimo.trace(Trace.AccessEvents, "DataSet.closeAccess for:  " + hashCode());
					try {
						int reason = event.getReason();
						close(true, reason, reason == AccessEvent.UNKNOWN);
					} catch (DataSetException ex) {
						event.appendException(ex);
					}
					break;
			}
		}
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final boolean _goToRow(long row)
	/*-throws DataSetException-*/
	{
		long lastRow;
		if (!editing || _post()) {
			if (!open)
				DataSetException.dataSetNotOpen();

			lastRow = index.lastRow();
			// FETCH_AS_NEEDED and LOAD_ONE_ROW require us to ask for more data.
			// For LOAD_ONE_ROW we may need to adjust the row number (if row was replaced).
			//
			if (row > lastRow && dataSetStore.provideMoreData()) {
				lastRow = index.lastRow();
				if (row > lastRow)
					row = lastRow;
			}

			// Note that there is no pseudo record at this point.
			// index.lastRow() will be negative when no rows present.
			//
			if (row < 0 || (row > lastRow && row != 0)) {
				isInBounds = false;
			} else {
				if (masterNavigateListeners != null)
					processMasterNavigating(masterNavigatingEvent);
				isInBounds = true;
				this.currentRow = row;
				internalRow = index.internalRow(row);
				needsSynch = TriStateProperty.FALSE;
				if (masterNavigateListeners != null) {
					DiagnosticJLimo.trace(Trace.Master, "Master moving to " + row);
					masterNavigateListeners.dxDispatch(masterNavigatedEvent);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the most recent navigation was in bounds.
	 */
	/**
	 * Returns <b>true</b> if the most recent navigation was in bounds, otherwise, this method returns <b>false</b>. A navigation is in bounds if it falls between
	 * the first and last records (inclusive) that are visible to the cursor. This method allows you to test whether either condition of beginning or end of file
	 * is encountered with a single method call.
	 * <p>
	 * Methods that set the "in bounds" flag include:
	 * <p>
	 * <ul>
	 * <li>{@link #first()}</li>
	 * <li>{@link #last()}</li>
	 * <li>{@link #next()}</li>
	 * <li>{@link #prior()}</li>
	 * </ul>
	 * <p>
	 * If you prefer or if your application requires it, you can call any of these methods to reset the <code>inBounds()</code> flag. Any navigation effected
	 * through the user-interface also sets <code>inBounds()</code> flag.
	 *
	 * @return <b>true</b> if the most recent navigation was in bounds, otherwise, this method returns <b>false</b>.
	 */
	public final boolean inBounds()
	/*-throws DataSetException-*/
	{
		if (!open)
			DataSetException.dataSetNotOpen();
		if (needsSynch != TriStateProperty.FALSE)
			synchRow();
		// Quick check before rowCount invoked.
		//
		return isInBounds && (index.lastRow() > -1 || getLongRowCount() > 0);
	}

	/**
	 * Sets the in bounds state to <b>true</b>. This method is not normally used in an application's code since any successful navigation sets this to
	 * <b>true</b>.
	 */
	public final void resetInBounds()
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();
		synchronized (dataMonitor) {
			isInBounds = true;
		}
	}

	private final boolean fixRowNeeded()
	/*-throws DataSetException-*/
	{
		return currentRow > index.lastRow() || internalRow != index.internalRow(currentRow);
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final void _fixRowPosition()
	/*-throws DataSetException-*/
	{
		if (editing && newRow) {
			long lastRow = index.lastRow();
			++lastRow;
			if (currentRow > lastRow)
				currentRow = lastRow;
		} else {
			_synchRow();
		}
		// !/*
		// ! try {
		// ! int newCurrentRow = index.findClosest(internalRow);
		// ! int lastRow = index.lastRow();
		// ! // If row we were on is gone, try to stay on the same logical row.
		// ! // If logical row is past the end, go to the end.
		// ! //
		// ! if (internalRow != index.internalRow(newCurrentRow)) {
		// ! if (currentRow > lastRow)
		// ! newCurrentRow = lastRow;
		// ! else
		// ! newCurrentRow = currentRow;
		// ! }
		// ! _goToRow(newCurrentRow);
		// ! }
		// ! catch(DataSetException ex) {
		// ! Diagnostic.printStackTrace(ex);
		// ! try { _goToRow(0); }
		// ! catch(DataSetException ex2) { Diagnostic.printStackTrace(ex2); }
		// ! }
		// ! }
		// !*/
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final boolean _goToClosestRow(long row)
	/*-throws DataSetException-*/
	{
		if (_goToRow(row))
			return true;

		if (row < 0 || index.lastRow() < 0)
			return _goToRow(0);

		return _goToRow(index.lastRow());
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final boolean _postIterator()
	/*-throws DataSetException-*/
	{
		boolean wasRowDirty = rowDirty;
		if ((editing && _post())) {
			if (wasRowDirty)
				internalRow = postedRow;
			_synchRow();
			return true;
		}
		return false;
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final boolean _post()
	/*-throws DataSetException-*/
	{
		if (dataChangeListeners != null) {
			try {
				dataChangeListeners.exceptionDispatch(new DataChangeEvent(this, DataChangeEvent.POST_ROW));
			} catch (DataSetException ex) {
				throw ex;
			} catch (Exception ex2) {
				DataSetException.fieldPostError(ex2);
			}
		}

		if (newRow) {
			if (rowDirty) {
				if (dataSetStore.editListeners == null)
					postedRow = dataSetStore.storageAddRowNoNotify(this);
				else {
					while (true) {
						try {
							dataSetStore.processAdding(this, this);
							postedRow = dataSetStore.storageAddRowNoNotify(this);
						} catch (DataSetException ex) {
							ErrorResponse response = dataSetStore.processAddError(this, this, ex);
							if (response.isRetry())
								continue;
							else if (response.isAbort())
								throw ex;
							else
								return false;
						}
						break;
					}
				}
				// These changes must happen after the addRow else
				// calc and aggregators will not use the pseudo row.
				// Instead the will get the row before.
				rowDirty = false;
				editing = false;
				relinkAfterPost();
				if (dataSetStore.editListeners != null) {
					dataSetStore.processAdded(this);
				}
				dataSetStore.processDataChangeEvent(DataChangeEvent.ROW_ADDED, postedRow);
			} else {
				editing = false;
				rowDeletedDispatch(currentRow);
			}
			newRow = false;
		} else if (rowDirty) {
			if (originalValues == null)
				getOriginalValues();
			if (dataSetStore.editListeners == null)
				dataSetStore.updateRow(this, _synchRow(), originalValues, this, null);
			else {
				while (true) {
					try {
						dataSetStore.updateRow(this, _synchRow(), originalValues, this, null);
					} catch (DataSetException ex) {
						ErrorResponse response = dataSetStore.processUpdateError(this, this, ex);
						if (response.isRetry())
							continue;
						else if (response.isAbort())
							throw ex;
						else
							return false;
					}
					break;
				}
			}
			rowDirty = false;
			editing = false;
			postedRow = internalRow;
			dataSetStore.processRowChangePosted(internalRow);

			relinkAfterPost();
			if (dataSetStore.editListeners != null)
				dataSetStore.processUpdated(this);
		} else {
			editing = false;
			postedRow = internalRow;
			// This should probably return true since all the navigation methods will
			// refuse to navigate if this returns false - but, sometimes change notifications
			// are made based on a true return value so special handling is necessary
			// to fix this. fix next time.
			//
			return false;
		}

		return true;
	}

	private final void relinkAfterPost() {
		if (detailIndex != null && dataSetStore.isStorageOpen())
			relinkDetail(false);
	}

	// DO NOT MAKE PUBLIC!!! Must go through synchronized StorageDataSet
	//
	final void _cancel()
	/*-throws DataSetException-*/
	{
		if (editing) {
			dataSetStore.processCanceling(this);
			if (masterNavigateListeners != null)
				processMasterNavigating(new MasterNavigateEvent(this, true, MasterNavigateEvent.NAVIGATING));
			editing = false;
			rowDirty = false;
			if (masterNavigateListeners != null)
				masterNavigateListeners.dxDispatch(masterNavigatedEvent);
		}
	}

	/**
	 * Copies the DataSetView and returns the copied object.
	 * 
	 * @return The copied object.
	 */
	public final DataSetView cloneDataSetView()
	/*-throws DataSetException-*/
	{
		DataSetView dataSet = new DataSetView();
		cloneDataSetView(dataSet);
		return dataSet;
	}

	public final void cloneDataSetView(DataSetView dataSet)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		synchronized (dataMonitor) {
			dataSet.setStorageDataSet(dataSetStore);
			if (descriptor != null)
				dataSet.setSort(new SortDescriptor(descriptor));
			if (masterLink != null) {
				dataSet.setMasterLink(masterLink);
			}
			if (rowFilterListener != null) {
				try {
					dataSet.addRowFilterListener(rowFilterListener);
				} catch (TooManyListenersException ex) {
					DiagnosticJLimo.printStackTrace(ex); // Should never happen.
				}
			}
			dataSet.open();
			dataSet._goToRow(currentRow);
		}
	}

	public final boolean isEditing() {
		return editing;
	}

	public final boolean isEditingNewRow() {
		return editing && newRow;
	}

	public void masterNavigating(MasterNavigateEvent event) /*-throws DataSetException-*/
	{
		if (masterNavigateListeners != null) {
			if (event.canceling)
				processMasterNavigating(new MasterNavigateEvent(this, true, MasterNavigateEvent.NAVIGATING));
			else
				processMasterNavigating(masterNavigatingEvent);
		}

		if (event.canceling)
			cancel();
		else
			post();
	}

	public void masterNavigated(MasterNavigateEvent event)
	/*-throws DataSetException-*/
	{
		boolean needsFirstLoad = false;
		boolean needsRelink = true;
		synchronized (dataMonitor) {
			DiagnosticJLimo.trace(Trace.Detail, "Master moved:  " + this.getTableName());
			if (detailIndex == null || (needsRelink = detailIndex.needsRelink())) {
				// ! try{
				_cancel();
				if (detailIndex != null) {
					if (masterLink.isFetchAsNeeded() && !detailIndex.detailsLoaded() && detailIndex.canLoadDetails(false)) {
						needsFirstLoad = true;
					} else
						relinkDetail(true);
				}
				// ! }
				// ! catch(DataSetException ex) {
				// ! Diagnostic.printStackTrace(ex);
				// ! }
				if (iterators != null)
					dataChangedIterators();
			}
		}

		if (needsFirstLoad) {
			synchronized (getOpenMonitor()) {
				DiagnosticJLimo.check(isOpen());
				dataSetStore.closeStorage(AccessEvent.STRUCTURE_CHANGE);
				DiagnosticJLimo.check(!isOpen());
				// Columns may have been added, so don't know what happened (UNSPECIFIED).
				//
				open(new AccessEvent(this, AccessEvent.OPEN, AccessEvent.UNSPECIFIED));
			}
		}

		if (needsRelink) {
			try {
				first();
			} catch (DataSetException ex) {
				DiagnosticJLimo.printStackTrace(ex);
			}
		}
	}

	/**
	 * Initializes the current row to default values of all columns. If the DataSet is a detail DataSet of a master-detail relationship, this method also
	 * initializes the detail linking columns.
	 */
	public void setDefaultValues()
	/*-throws DataSetException-*/
	{
		super.setDefaultValues();
		if (detailIndex != null)
			detailIndex.setDefaultValues(this);
	}

	/**
	 * Initializes the row DataRow to default values of all columns. If the DataSet is a detail DataSet of a master-detail relationship, this method also
	 * initializes the detail linking columns.
	 * 
	 * @param row
	 *          The DataRow whose default values for all of its columns will be set.
	 * @see com.borland.dx.dataset.ReadWriteRow#setDefaultValues()
	 */
	public void setDefaultValues(DataRow row)
	/*-throws DataSetException-*/
	{
		row.setDefaultValues();
		if (detailIndex != null)
			detailIndex.setDefaultValues(row);
	}

	private void relinkDetail(boolean doFetchAsNeeded)
	/*-throws DataSetException-*/
	{
		detailIndex.reLink(doFetchAsNeeded);
		// !/*
		// ! if (goToEnd) {
		// ! Diagnostic.trace(Trace.Detail, "New Detail rowCount: "+safeRowCount());
		// ! try{_goToRow(safeRowCount()-1);}
		// ! catch(Exception ex) {
		// ! Diagnostic.printStackTrace(ex);
		// ! }
		// !// ! }
		// !*/
		if (dataChangeListeners != null)
			dataChangeListenersDispatch(new DataChangeEvent(this, DataChangeEvent.DATA_CHANGED));
	}

	private long safeRowCount() {
		try {
			return getLongRowCount();
		} catch (DataSetException ex) {
			DiagnosticJLimo.printStackTrace(ex);
			return 0;
		}
	}

	final String formatRowValues() {
		if (isOpen()) {

			try {
				if (getLongRowCount() > 0)
					return super.formatRowValues();
				else
					return Res.bundle.getString(ResIndex.NoRows);
			} catch (Exception ex) {
				// ! Diagnostic.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * Stores the MasterLinkDescriptor object that contains the properties of a master-detail relationship. This proerty is set for the detail DataSet of a
	 * master-detail relationship and is applicable for any StorageDataSet implementation such as QueryDataSet. This property can be be used with a DataSetView
	 * component if the MasterLinkDescriptor FetchAsNeeded property is not set to true.
	 * 
	 * @param descriptor
	 *          The masterLink property. The MasterLink property editor in the JBuilder Visual component designer can be used to construct and set a MasterLink
	 *          property.
	 */

	public synchronized void setMasterLink(MasterLinkDescriptor descriptor)
	/*-throws DataSetException-*/
	{
		// ! Diagnostic.println("DataSet.setMasterLink(" + descriptor + ") on " + this
		// ! + "\r\n and open state = " + isStorageOpen());
		failIfOpen();

		if (masterLink != null && masterLink.getMasterDataSet() != null)
			masterLink.getMasterDataSet().removeDetail(this);

		this.masterLink = descriptor;

		if (descriptor != null && descriptor.getMasterDataSet() != null)
			descriptor.getMasterDataSet().addDetail(this);
	}

	/**
	 * Stores the MasterLinkDescriptor object that contains the properties of a master-detail relationship. This property is set for the detail DataSet of a
	 * master-detail relationship and is applicable for any StorageDataSet implementation such as QueryDataSet. This property can be be used with a DataSetView
	 * component if the MasterLinkDescriptor FetchAsNeeded property is not set to true.
	 * 
	 * @return MasterLinkDescriptor The MasterLinkDescriptor object that contains the properties of a master-detail relationship.
	 */
	public MasterLinkDescriptor getMasterLink() {
		// ! Diagnostic.println("DataSet.getMasterLink() on " + this + " -->" + masterLink);
		return masterLink;
	}

	final void addMasterUpdateListener(MasterUpdateListener listener) {
		dataSetStore.storageAddMasterUpdateListener(listener);
	}

	final void removeMasterUpdateListener(MasterUpdateListener listener) {
		dataSetStore.storageRemoveMasterUpdateListener(listener);
	}

	final synchronized void addDetail(DataSet detailDataSet) {
		int newLength = detailDataSets == null ? 1 : detailDataSets.length + 1;
		DataSet[] newDataSets = new DataSet[newLength];
		if (detailDataSets != null)
			System.arraycopy(detailDataSets, 0, newDataSets, 0, detailDataSets.length);
		detailDataSets = newDataSets;
		detailDataSets[newLength - 1] = detailDataSet;
	}

	final synchronized void removeDetail(DataSet detailDataSet) {
		if (detailDataSets.length < 2) {
			DiagnosticJLimo.check(detailDataSet == detailDataSets[0]);
			detailDataSets = null;
		} else {
			for (int i = 0; i < detailDataSets.length; ++i) {
				if (detailDataSets[i] == detailDataSet) {
					for (int j = i + 1; j < detailDataSets.length; ++j)
						detailDataSets[j - 1] = detailDataSets[j];
					DataSet[] newDetailDataSets = new DataSet[detailDataSets.length - 1];
					System.arraycopy(detailDataSets, 0, newDetailDataSets, 0, newDetailDataSets.length);
					detailDataSets = newDetailDataSets;
					return;
				}
			}
			DiagnosticJLimo.fail();
		}
	}

	/**
	 * Read-only property that returns an array of all detail DataSets associated with this master DataSet.
	 *
	 * @return The array of all detail DataSets associated with this master DataSet.
	 */
	public final synchronized DataSet[] getDetails() {
		if (detailDataSets != null) {
			DataSet[] details = new DataSet[detailDataSets.length];
			System.arraycopy(detailDataSets, 0, details, 0, detailDataSets.length);
			return details;
		}
		return null;
	}

	/**
	 * Returns the detail DataSet with given tableName property setting. This method throws a DataSetException.UNKNOWN_DETAIL_NAME if there is no detail with
	 * tableName. It is possible (but not likely) that more than one detail has the same tableName.
	 *
	 * @param tableName
	 *          The tableName property containing the detail DataSet.
	 * @return The detail DataSet with given tableName property setting.
	 */
	public final synchronized DataSet getDetail(String tableName)
	/*-throws DataSetException-*/
	{
		DataSet dataSet = hasDetail(tableName);
		if (dataSet == null)
			DataSetException.unknownDetailName(tableName);
		return dataSet;
	}

	/**
	 * @since JB2.0 returns detail dataSet with given TableName property seting. returns null if there is no detail with tableName. Note that it is possible, but
	 *        not likely that more than one detail have the same tableName.
	 */

	/**
	 * Returns the detail DataSet with given tableName property setting or <b>null</b> if there is no detail with tableName.
	 * <p>
	 * <b>Note:</b> It is possible, but not likely, that more than one detail has the same tableName.
	 *
	 * @param tableName
	 * @return The detail DataSet with given tableName property setting or null if there is no detail with tableName.
	 */
	public final synchronized DataSet hasDetail(String tableName) {
		StorageDataSet dataSet;
		if (detailDataSets != null && tableName != null) {
			for (int index = 0; index < detailDataSets.length; ++index) {
				dataSet = detailDataSets[index].getStorageDataSet();
				if (dataSet != null && dataSet.getTableName() != null && dataSet.getTableName().equals(tableName))
					return detailDataSets[index];
			}
		}
		return null;
	}

	/**
	 * Call this method on a master DataSet to force all details to open. By default, a detail will ask its master DataSet to open, but a master DataSet does not
	 * ask the detail DataSet to open.
	 */
	public final synchronized void openDetails()
	/*-throws DataSetException-*/
	{
		open();
		if (detailDataSets != null) {
			for (int index = 0; index < detailDataSets.length; ++index)
				detailDataSets[index].openDetails();
		}
	}

	/**
	 * Indicates true if the Column should be displayed in UI controls. This method returns true for Columns that have their visible property set. Aggregate
	 * calculated columns and master-detail link columns are hidden by default and return false.
	 *
	 * @param columnName
	 *          The String name of the Column component.
	 * @return <b>true</b> if the Column should be displayed in UI controls.
	 */
	public boolean columnIsVisible(String columnName)
	/*-throws DataSetException-*/
	{
		if (!open)
			DataSetException.dataSetNotOpen();

		Column column = getColumn(columnName);
		switch (column.getVisible()) {
			case TriStateProperty.TRUE:
				return true;
			case TriStateProperty.FALSE:
				return false;
			default:
				if (column.getCalcType() == CalcType.AGGREGATE)
					return false;
				if (column.isHidden())
					return false;
				// Advise that link columns should be hidden.
				//
				if (masterLink != null) {
					// Could persist as hidden in Column component, but then hidden
					// property would persist after a MasterLinkDescriptor was removed.
					//
					String[] detailLinkColumns = masterLink.getDetailLinkColumns();
					if (detailLinkColumns != null) {
						for (int index = 0; index < detailLinkColumns.length; ++index) {
							if (detailLinkColumns[index].equalsIgnoreCase(columnName))
								return false;
						}
					}
				}
				break;
		}
		return true;
	}

	// ! /*
	// ! * This method is used to add or retrieve a persistent Column object for the given DataSet.
	// ! * If the Column already exists, this method will merely return it. If it does not yet
	// ! * exist, it will create one and return that. The primary use of this method lies in
	// ! * simplifying the code generation for setting properties against Columns in a DataSet
	// ! * from the UI designer.
	// ! *
	// ! * @param columnName The name of the column to retrieve or to add
	// ! *
	// ! * @return A Column Object
	// ! *
	// ! * Note, calling this method will always mark the given Column as "persistent". Do not use
	// ! * this method as an equivalent of getColumn()
	// ! public synchronized Column persistColumn(String columnName) {
	// ! Diagnostic.trace(Trace.DataSetSave, "persistentColumn(" + columnName + ")");
	// ! Column column = null;
	// ! try {
	// ! column = getColumn(columnName);
	// ! Diagnostic.trace(Trace.DataSetSave, "getColumn returned " + column);
	// ! }
	// ! catch (Exception ex) {
	// ! Diagnostic.println("Error in fetching existing persistent column: " + ex);
	// ! }
	// ! if (column == null) {
	// ! try {
	// ! Diagnostic.trace(Trace.DataSetSave, "adding new column");
	// ! column = new Column(columnName, null, Variant.STRING);
	// ! // No longer default to String -- since we can't tell whether user wanted this or not
	// ! // note: caption == null to force to use columnName
	// ! // and use StringType as initial guess regarding dataType
	// ! dataSetStore.addColumn(column);
	// ! column = getColumn(columnName); // Steve is currently copying -- we want real instance
	// ! column.unassignDataType(); // now mark data type as unknown --pending later set
	// ! }
	// ! catch (Exception ex2) {
	// ! Diagnostic.println("Error creating persistent column: " + ex2);
	// ! }
	// ! }
	// ! column.setPersist(true);
	// ! return column;
	// ! }
	// ! */

	/**
	 * Forces this DataSet to move to the same position as row if row is of the same StorageDataSet as this DataSet. If row is not from the same StorageDataSet,
	 * this method throws a DataSetException of INCOMPATIBLE_DATA_ROW. If this DataSet object's current view is filtered and does not include row, the current
	 * position of this DataSet moves to the closest row that is in its view.
	 *
	 * @param row
	 *          The unique row identifier of the row to move to.
	 */
	public void goToRow(ReadRow row)
	/*-throws DataSetException-*/
	{
		if (dataSetStore != null)
			if (row instanceof DataSet) {
				DataSet dataSet = (DataSet) row;

				if (dataSetStore == dataSet.dataSetStore) {
					goToRow(index.findClosest(dataSet.internalRow));
					return;
				}
				locate(row, Locate.FIRST);
			}
	}

	/**
	 * Saves changes made to the data in the DataSet back to the data source using default {@link com.borland.dx.sql.dataset.QueryResolver} behavior.
	 */
	public void saveChanges() /*-throws DataSetException-*/ {
		failIfNotOpen();
		dataSetStore.saveChanges(this);
	}

	/**
	 * Refreshes the data from the DataSet if the data provider supports this operation (for example, QueryDataSet and ProcedureDataSet).
	 */
	public void refresh() /*-throws DataSetException-*/ {
		failIfNotOpen();
		dataSetStore.refresh();
	}

	/**
	 * Returns a unique identifier for the current row. Can be used by the goToInternalRow method to reposition the current row position. The internalRow
	 * assignment for a row never changes, and is never reused by another row. Setting this property is the fastest way to navigate to an arbitrary row.
	 *
	 * @return The unique identifier for the current row.
	 */
	public final long getInternalRow()
	/*-throws DataSetException-*/
	{
		if (needsSynch != TriStateProperty.FALSE)
			return synchRow();
		return internalRow;
	}

	final long synchRow()
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			return _synchRow();
		}
	}

	/*
	 * Must be synchronized by dataMonitor.
	 */
	// ! WARNING: Duplicated in RowIterator!!!
	final long _synchRow()
	/*-throws DataSetException-*/
	{
		if (needsSynch == TriStateProperty.DEFAULT) {
			DiagnosticJLimo.check(currentRow == 0);
			// Diagnostic.check(internalRow == 0);
			DiagnosticJLimo.check(!isInBounds);
			_goToRow(0);
		}
		currentRow = index.findClosest(internalRow, currentRow);

		// ! Diagnostic.println("internalRow: "+index.internalRow+" "+internalRow);
		if (index.getInternalRow() != internalRow) {
			// internal row is gone so position to closest logical row.
			//
			if (currentRow < 0) {
				currentRow = 0;
				internalRow = 0;
				isInBounds = false;
			} else {
				// ! Diagnostic.println("internalRow: "+index.internalRow+" "+internalRow);
				internalRow = index.getInternalRow();
				isInBounds = true;
				if (internalRow < 0) {
					// ! JOAL: BUG16898, BUG17157, TODO: Steve please verify fix!
					// ! No more rows exist, so reset the following fields:
					currentRow = 0;
					internalRow = 0;
					isInBounds = false;

				}
			}
			if (masterNavigateListeners != null) {
				DiagnosticJLimo.trace(Trace.Master, "Master moving to " + currentRow);
				masterNavigateListeners.dxDispatch(masterNavigatedEvent);
			}
		}
		needsSynch = TriStateProperty.FALSE;
		return internalRow;
	}

	/*
	 * Must be synchronized by dataMonitor. Call this when you know the current internalRow is gone (deleted). Basically, an internalRow(row) call is more
	 * efficient than a findClosest(internalRow).
	 */
	final long _synchCurrentRow()
	/*-throws DataSetException-*/
	{
		DiagnosticJLimo.check(index != null);
		long lastRow = index.lastRow();

		if (currentRow > lastRow)
			currentRow = lastRow;

		if (currentRow < 0) {
			currentRow = 0;
			internalRow = 0;
			isInBounds = false;
		} else {
			// ! Diagnostic.println("internalRow: "+index.internalRow+" "+internalRow);
			internalRow = index.internalRow(currentRow);
			isInBounds = true;
		}
		if (masterNavigateListeners != null) {
			DiagnosticJLimo.trace(Trace.Master, "Master moving to " + currentRow);
			masterNavigateListeners.dxDispatch(masterNavigatedEvent);
		}

		return internalRow;
	}

	/**
	 * Attempts to navigate to newInternalRow. If newInternalRow no longer exists, position remains unchanged.
	 * 
	 * @param newInternalRow
	 * @return Returns true for success.
	 */
	public final boolean goToInternalRow(long newInternalRow)
	/*-throws DataSetException-*/
	{
		if (!open)
			failIfNotOpen();

		if (needsSynch != TriStateProperty.FALSE)
			synchRow();

		if (internalRow != newInternalRow) {
			long oldInternalRow;
			synchronized (dataMonitor) {
				oldInternalRow = internalRow;
				internalRow = newInternalRow;
				_synchRow();
				if (internalRow != newInternalRow) {
					internalRow = oldInternalRow;
					_synchRow();
					return false;
				}
				if (internalRow == 0 && index.lastRow() < 0)
					return false;
			}
			rowNavigatedDispatch();
		} else if (newInternalRow == 0 && index.lastRow() < 0)
			return false;
		return true;
	}

	// ! /*
	// ! void detailsFetched()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! dataSetStore.detailsFetched(internalRow);
	// ! }
	// ! */

	// Returns an "current" version of a row.
	//

	/**
	 * Refetches the specified row if the DataSet has a data provider (for example, QueryDataSet and ProcedureDataSet) that supports this operation.
	 *
	 * @param row
	 *          The ReadWriteRow implementation that you want refetched.
	 */
	public void refetchRow(ReadWriteRow row)
	/*-throws DataSetException-*/
	{
		if (dataSetStore == null || dataSetStore == this)
			DataSetException.refreshRowNotSupported();

		dataSetStore.refetchRow(row);
	}

	/*
	 * @since JB2.0 If there is a sort order for this DataSet, the maintained index that maintains the sort based on DataSet.Sort property and the
	 * DataSet.rowFilter event will be dropped.
	 * 
	 * Note that if the DataSet is immediately reopened without changing the DataSet.Sort property or DataSet.rowFilter event listener, the index will be rebuilt.
	 */

	/**
	 * Deletes the index (if applicable) that maintains the sort order created according to the sort property and the rowFilter event of the DataSet.
	 * <p>
	 * <b>Note:</b> If the DataSet is immediately reopened without changing the sort property or rowFilter event listener of this class, the index will be
	 * rebuilt.
	 *
	 * @return
	 */
	public synchronized boolean dropIndex()
	/*-throws DataSetException-*/
	{
		if (dataSetStore != null)
			return dataSetStore.dropIndex(this);
		return false;
	}

	/**
	 * Forces the filter for this DataSet to be recomputed on all rows.
	 * <p>
	 * Closing and re-opening a DataSet with a filter will not always cause the filter code to be re-executed. Similarly, if your filter is based on the value of
	 * a global variable, changing the global variable will not change the set of rows displayed. In both cases, use the refilter() method to ensure that the
	 * filter is re-executed.
	 */
	public void refilter()
	/*-throws DataSetException-*/
	{
		if (rowFilterListener != null)
			dropIndex();
	}

	/**
	 * Copies the contents of the previous row to the current (new) row. Does nothing if the current row is the first row. If the current row is an existing row,
	 * and you want to copy the contents of the previous row to it, set dittoExisting to true. By default, dittoExisting is false, as copying over the contents of
	 * an existing row could cause data loss.
	 *
	 * @param dittoExisting
	 *          If true, copy the contents of the previous row to an existing row. By default this option is false, as it could cause data loss.
	 */
	public void dittoRow(boolean dittoExisting) /*-throws DataSetException-*/ {
		dittoRow(dittoExisting, false);
	}

	/**
	 * Copies the contents of the previous row to the current row. Does nothing if the current row is the first row.
	 * 
	 * @param dittoExisting
	 *          If <b>true</b>, copies the contents of the previous row to an existing row. By default this option is <b>false</b>, as it could cause data loss.
	 * @param autoInsert
	 *          Automatically inserts a new row if the DataSet is not in edit mode, then copies the contents of the previous row to the new row.
	 */
	public void dittoRow(boolean dittoExisting, boolean autoInsert) /*-throws DataSetException-*/ {
		failIfNotOpen();
		synchronized (dataMonitor) {
			if (autoInsert && !editing)
				insertRow(false);
			if (!dittoExisting && (!editing || !newRow))
				ValidationException.cannotDittoExisting();
			if (currentRow > 0) {
				int columnCount = getColumnCount();
				Column column;

				for (int ordinal = 0; ordinal < columnCount; ++ordinal) {
					column = getColumn(ordinal);
					if (column.canDitto())
						startEdit(column);
				}
				Variant value = new Variant();
				for (int ordinal = 0; ordinal < columnCount; ++ordinal) {
					column = getColumn(ordinal);
					if (column.canDitto()) {
						dataSetStore.getStorageVariant(index.internalRow(currentRow - 1), ordinal, value);
						setVariant(ordinal, value);
					}
				}
			}
		}
	}

	/**
	 * @return Returns whether the data source supports the saving of data changes (<b>true</b>) or not (<b>false</b>). For TableDataSet components, this method
	 *         returns <b>false</b> if the resolver property is not set.
	 */
	public boolean saveChangesSupported() {
		return dataSetStore != null ? dataSetStore.saveChangesSupported() : false;
	}

	/**
	 * Returns whether a refresh operation is supported. For example, QueryDataSet and ProcedureDataSet components return <b>true</b>.
	 * 
	 * @return
	 */
	public boolean refreshSupported() {
		return dataSetStore != null ? dataSetStore.refreshSupported() : false;
	}

	public final boolean isDetailDataSetWithFetchAsNeeded() {
		MasterLinkDescriptor descriptor = getMasterLink();
		DataSet masterDataSet = null;

		if (descriptor != null)
			masterDataSet = descriptor.getMasterDataSet();

		return (masterDataSet != null && descriptor.isFetchAsNeeded());
	}

	/**
	 * Allocates an array of Variants for all the Columns in the DataSet.
	 * 
	 * @return
	 */
	public final Variant[] allocateValues() {
		int count = columnList.count;
		Variant variants[] = new Variant[count];
		for (int ordinal = 0; ordinal < count; ++ordinal)
			variants[ordinal] = new Variant(columnList.cols[ordinal].getDataType());
		return variants;
	}

	/**
	 * Disallow row insertion. If a row insertion is attempted, a ValidationException will be thrown. UI controls have default error handling that will show this
	 * in a StatusControl if one is present. If there is no StatusControl, the error will be displayed in an error dialog. If this property is set on a
	 * DataSetView, only that view is affected by this setting.
	 */
	public final void setEnableInsert(boolean enable) {
		this.allowInsert = enable;
	}

	public final boolean isEnableInsert() {
		return allowInsert;
	}

	/**
	 * Enable/Disable row update. If row update is attempted, a ValidationException will be thrown. UI controls have default error handling that will show this in
	 * a StatusControl if one is present. If there is no StatusControl, the error will be displayed in an error dialog. If this property is set on a DataSetView,
	 * only that view is affected by this setting.
	 */
	public final void setEnableUpdate(boolean enable) {
		this.allowUpdate = enable;
	}

	public final boolean isEnableUpdate() {
		return allowUpdate;
	}

	/**
	 * Enable/Disable row deletion. If a delete is attempted, a ValidationException will be thrown. UI controls have default error handling that will show this in
	 * a StatusControl if one is present. If there is no StatusControl, the error will be displayed in an error dialog. If this property is set on a DataSetView,
	 * only that view is affected by this setting.
	 */
	public final void setEnableDelete(boolean enable) {
		this.allowDelete = enable;
	}

	public final boolean isEnableDelete() {
		return allowDelete;
	}

	/**
	 * Enable/Disable edits to this DataSet. If an edit operation is attempted, a ValidationException will be thrown. UI controls have default error handling that
	 * will show this in a StatusControl if one is present. If there is no StatusControl, the error will be displayed in an error dialog. If this property is set
	 * on a DataSetView, only that view is affected by this setting.
	 */
	public final void setEditable(boolean editable) {
		this.editable = editable;
	}

	public final boolean isEditable() {
		return editable;
	}

	/**
	 * Enable posting of unmodified rows. By default unmodified rows are removed during a post. Setting this property would preserve rows with just default
	 * values.
	 * 
	 * @param postUnmodified
	 *          boolean true if unmodified rows are to be posted.
	 */
	public final void setPostUnmodifiedRow(boolean postUnmodified) {
		allowPostUnmodified = postUnmodified;
	}

	public final boolean isPostUnmodifiedRow() {
		return allowPostUnmodified;
	}

	// This method is supposed to initialize members, that must be initialized
	// (see the default constructor) but is marked transient.
	//
	private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		columnList = new ColumnList();
		this.masterNavigatedEvent = new MasterNavigateEvent(this, false, MasterNavigateEvent.NAVIGATED);
		this.masterNavigatingEvent = new MasterNavigateEvent(this, false, MasterNavigateEvent.NAVIGATING);
		this.navigationEvent = new NavigationEvent(this);
	}

	final void addIterator(RowIterator iterator)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (open)
				iterator.internalRow = _synchRow();
			iterator.currentRow = currentRow;
			iterator.next = iterators;
			iterators = iterator;
		}
	}

	final void removeIterator(RowIterator iterator) {
		synchronized (dataMonitor) {
			RowIterator i = iterators;
			RowIterator p = i;
			while (i != null) {
				if (i == iterator) {
					if (p == i)
						iterators = iterators.next;
					else
						p.next = i.next;
					break;
				}
				p = i;
				i = i.next;
			}
		}
	}

	/*
	 * original comments For DataSets that have the Sort.SortAsInserted property set to true, this method can be used to move the row.
	 * 
	 * @returns delta moved. If the index does not have the Sort.SortAsInserted property set, this will return 0. If an attempt is made to move the row out of
	 * range, the return value will reflect how much of a position change was made. The row will not move out of range. Range is defined as the first and last row
	 * of the DataSet when the Sort property has no sort keys. If the Sort property has sort keys, then range is bound by the rows that share the same sort key
	 * values.
	 */

	/**
	 * For DataSets that have the Sort.SortAsInserted property set to true, this method can be used to move the row.
	 *
	 * @param delta
	 * @return If the index does not have the Sort.SortAsInserted property set, this will return 0. If an attempt is made to move the row out of range, the return
	 *         value will reflect how much of a position change was made. The row will not move out of range. Range is defined as the first and last row of the
	 *         DataSet when the Sort property has no sort keys. If the Sort property has sort keys, then range is bound by the rows that share the same sort key
	 *         values.
	 */
	public final int moveRow(int delta)
	/*-throws DataSetException-*/
	{
		return (int) moveLongRow(delta);
	}

	final long moveLongRow(long delta)
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		if (heapIndex) {
			synchronized (dataMonitor) {

				long beforeRow = _synchRow();
				long ret = index.moveRow(currentRow, delta);
				long afterRow = _synchRow();
				goToInternalRow(beforeRow);
				dataSetStore.processDataChangeEvent(DataChangeEvent.ROW_CHANGED, beforeRow);
				dataSetStore.processDataChangeEvent(DataChangeEvent.ROW_CHANGED, afterRow);
				if (ret != 0)
					rowNavigatedDispatch();
				return ret;
			}
		}
		return 0;
	}

	/**
	 * @param ordinal
	 *          The specified ordinal.
	 * @return <b>true</b> if the value at the specified ordinal has been modified.
	 */
	public final boolean isModified(int ordinal)
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		if (newRow)
			return super.isModified(ordinal);
		synchronized (dataMonitor) {
			if (needsSynch != TriStateProperty.FALSE)
				_synchRow();
			if (originalValues == null)
				getOriginalValues();
			dataSetStore._getOriginalVariant(internalRow, ordinal, originalValues[ordinal]);
			return !getVariantStorage(ordinal).equals(originalValues[ordinal]);
		}
	}

	/**
	 * @param columnName
	 *          The specified columnName.
	 * @return Returns true if the value at the specified columnName has been modified.
	 */
	public final boolean isModified(String columnName)
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		return isModified(columnList.getOrdinal(columnName));
	}

	/**
	 * Indicates whether or not the specified column is sortable.
	 * 
	 * @param column
	 *          The specified column.
	 * @return <b>true</b> If the specified column is sortable, <b>false</b> otherwise.
	 */
	public boolean isSortable(Column column) {
		return dataSetStore._isSortable(column);
	}

	private transient boolean open;
	private transient boolean openComplete;
	private transient boolean editing;
	// private transient boolean empty;
	private transient boolean newRow;
	private transient boolean rowDirty;
	transient DataRow readRow;
	private transient Variant[] originalValues;
	transient StorageDataSet dataSetStore;
	transient long currentRow;
	transient long internalRow;
	transient long postedRow;
	transient Index index;
	int invisibleMask;
	int visibleMask;
	private SortDescriptor descriptor;

	private transient boolean isInBounds;

	private transient NavigationEvent navigationEvent;
	private transient EventMulticaster navigationListeners;
	private transient EventMulticaster dataChangeListeners;
	private transient EventMulticaster accessListeners;
	// ! private transient EventMulticaster validationErrorListeners;
	private transient EventMulticaster statusListeners;
	private transient DxMulticaster masterNavigateListeners;
	private transient DataSet[] detailDataSets;
	private transient MasterNavigateEvent masterNavigatedEvent;
	private transient MasterNavigateEvent masterNavigatingEvent;
	private transient RowFilterListener rowFilterListener;
	private transient OpenListener openListener;
	private transient String lastColumnVisited;

	private MasterLinkDescriptor masterLink;
	private transient DetailIndex detailIndex;

	private boolean displayErrors;
	private transient int needsSynch;
	// !RC TODO private boolean hadOpenError;

	// Placed here because this class is less likely to be gc'd than DataSetException.
	//
	static EventMulticaster exceptionListeners;

	boolean allowInsert;
	boolean allowDelete;
	boolean allowUpdate;
	boolean allowPostUnmodified;
	boolean editable;
	boolean heapIndex;
	transient boolean notifyLoad;

	transient Object dataMonitor;
	private transient DataRow validateRow;

	private transient RowIterator iterators;

	transient StorageDataSet resolverStorageDataSet;

	private static final long serialVersionUID = 1L;
}
