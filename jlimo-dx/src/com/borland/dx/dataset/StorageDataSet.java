//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/StorageDataSet.java,v 7.19.2.5 2005/02/20 01:36:08 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Locale;
import java.util.TooManyListenersException;

import com.borland.dx.dataset.cons.DataConst;
import com.borland.dx.dataset.cons.OpenBlock;
import com.borland.dx.dataset.cons.PropSet;
import com.borland.dx.memorystore.MemoryStore;
import com.borland.dx.sql.dataset.Database;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.ErrorResponse;
import com.borland.jb.util.EventMulticaster;
import com.borland.jb.util.Trace;

//import java.beans.Beans;
/**
 * StorageDataSet manages a 2 dimensional array of of data.  Column data types are
 * specified through Column components.  A StorageDataSet has a Column Component for
 * each data column that it contains.  Column components contain type
 * information for the data columns in a StorageDataSet.
 *
 * This is an abstract class of core functionality for StorageDataSet funcitonality.
 * It cannot be used directly.  See TableDataSet, QueryDataSet, ProcedureDataSet.
 */

/* Internal comments:

    Two monitors are used for synchronization:  The StorageDataSet object and the dataMonitor.
    The dataMonitor is initially just the StorageDataSet.  When the MatrixData is initialized,
    dataMonitor is switched to what the MatrixData implementation provides.

    Rules of usage:
    1) StorageDataSet monitor must be acquired before the dataMonitor to avoid deadlocks.
    2) dataMonitor does not need to be acquired if you "know" the StorageDataSet could not
    be open.
    3) StorageDataSet monitor must be acquired for things that have structural implications.
    4) dataMonitor is acquired for data access/update.  It is also acquired for structural
    changes if it is possible that the StorageDataSet "could" be open.
*/

// Cannot be abstract because entites that extend from this will not be
// designable if StorageDataSet is abstract.
//

/**
 *
 * <P>
 * This class provides the core <CODE>StorageDataSet</CODE> functionality. See also the {@link com.borland.dx.dataset.TableDataSet TableDataSet},
 * {@link com.borland.dx.sql.dataset.QueryDataSet QueryDataSet}, {@link com.borland.dx.sql.dataset.ProcedureDataSet ProcedureDataSet} components.
 * 
 * <P>
 * <CODE>StorageDataSet</CODE> manages a 2-dimensional array of data. <CODE>Column</CODE> data types are specified through <CODE>Column</CODE> components. A
 * <CODE>StorageDataSet</CODE> has a <CODE>Column</CODE> component for each data column that it contains. <CODE>Column</CODE> components contain type
 * information for the data columns in a <CODE>StorageDataSet</CODE>.
 * 
 * <P>
 * The <CODE>StorageDataSet</CODE> component extends the basic cursor functionality provided by its superclass <CODE>DataSet</CODE> with the:
 * <UL>
 * <LI>storage of the data (using <CODE>MemoryStore</CODE> or {@link com.borland.datastore.DataStore DataStore}.
 * <LI>facility for structural (<CODE>Column</CODE>) changes to the <CODE>StorageDataSet</CODE>.
 * <LI><CODE>provider</CODE> and <CODE>resolver</CODE> properties.
 * <LI>ability to be the base storage for multiple <CODE>DataSetView</CODE> components.
 * </UL>
 * 
 * 
 * 
 * <P>
 * The <CODE>StorageDataSet</CODE> component is extended by <CODE>QueryDataSet</CODE>, <CODE>ProcedureDataSet</CODE>, and <CODE>TableDataSet</CODE> components.
 * The <CODE>QueryDataSet</CODE> and <CODE>ProcedureDataSet</CODE> work with a <CODE>Database</CODE> component to obtain data from a remote server through the
 * execution of a query or stored procedure. You can also load data stored in a text file into a <CODE>TableDataSet</CODE> object. Once data is loaded
 * (provided) into a <CODE>StorageDataSet</CODE> object, you handle the data in a common way, regardless of how the data was obtained or which
 * <CODE>StorageDataSet</CODE> extension you use.
 * 
 * 
 * 
 * 
 * <P>
 * When making structure changes to the <CODE>StorageDataSet</CODE>, such as setting the <CODE>store</CODE> property to use a <CODE>DataStore</CODE>, use the
 * <CODE>addColumn</CODE>, <CODE>changeColumn</CODE>, <CODE>dropColumn</CODE>, and <CODE>moveColumn</CODE> methods as these methods allow for better before and
 * after mapping. Otherwise, when the <CODE>StorageDataSet</CODE> is restructured, <CODE>Column</CODE> names are used for mapping, which may not always work as
 * anticipated. The <CODE>Column</CODE> component's <CODE>preferredOrdinal</CODE> property is retained when calling these <CODE>StorageDataSet</CODE> methods.
 * 
 * 
 * <P>
 * If your application involves a master-detail relationship, the <CODE>resolveOrder</CODE> property indicates the order in which changes made to the
 * <CODE>DataSets</CODE> are resolved back to the data source.
 * 
 * Constructs a StorageDataSet component, generates a StatusEvent of StatusEvent.LOADING_DATA.
 * 
 */

public class StorageDataSet extends DataSet implements ColumnDesigner {

	/*
	 * Broken in JavaDoc! and sets the following properties: <ul> <li>maxRows t -1</li> <li>maxDesignRows to 50</li> <li>MetaDataUpdate to MetaDataUpdate.ALL</li>
	 * </ul>
	 *
	 */
	public StorageDataSet() {
		super();
		this.dataSetStore = this;

		matrixDataType = DataConst.TABLE_DATA;
		maxRows = -1;
		maxDesignRows = 50;
		maxExtraRows = 10;
		editBlocked = OpenBlock.NotOpen;
		aggManager = null;
		metaDataUpdate = MetaDataUpdate.ALL;
		resolvable = true;
		dataMonitor = this;
		dataMayExist = true;
		firstOpen = true;
		needsRecalc = false;
	}

	/**
	 * Typically used to provide trigger support for applications that use the JDBC API to access the JDataStore database. This property has no affect When
	 * MemoryStore is used.
	 *
	 * The JDataStore database engine internally uses StorageDataSet components for all insert, delete, and update (DML) operations made from the a JDataStore
	 * JDBC driver. The StoreClassFactory.getStorageDataSet method can be implemented as a call back for the JDataStore database engine to call when a
	 * StorageDataSet is needed for JDBC DML operation(s). For trigger support, StoreClassFactory implementations typically instantiate a StorageDataSet and wire
	 * an EditListener implementation. A StoreClassFactory implementation must be registered for a table using the StorageDataSet.setStoreClassFactory method.
	 *
	 * @see com.borland.dx.dataset.StoreClassFactory
	 * @param factory
	 */
	public final void setStoreClassFactory(StoreClassFactory factory)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (data == null || (this.storeClassFactory != factory && (this.storeClassFactory == null || factory == null || !storeClassFactory.getClass().equals(
																																																																														factory
																																																																																.getClass())))) {
				propSet |= PropSet.StoreClassFactory;
				this.storeClassFactory = factory;
				if (data != null)
					updateProperties();
			}
		}
	}

	/**
	 * @see comments for StoreClassFactory
	 */
	/**
	 * Allows a DataStore table to be accessed only by a specific StorageDataSet class or StorageDataSet class extension. This allows an application to make sure
	 * that property and event settings are always activated when a DataStore table is accessed.
	 * <p>
	 * Currently, this property is only meaningful when the StorageDataSet.store property is set to a DataStoreConnection or DataStore component.
	 *
	 * @see com.borland.dx.dataset.StoreClassFactory
	 * @return
	 */
	public final StoreClassFactory getStoreClassFactory() {
		return storeClassFactory;
	}

	public Resolver getResolver() {
		return resolver;
	}

	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
	}

	public Provider getProvider() {
		return provider;
	}

	/**
	 * Specifies the Provider component that controls how the data is fetched from the database when the StorageDataSet is opened. DataExpress includes these
	 * provider components:
	 * <p>
	 * <ul>
	 * <li>{@link com.borland.dx.sql.dataset.QueryProvider}</li>
	 * <li>{@link com.borland.dx.sql.dataset.ProcedureProvider}</li>
	 * <li>{@link com.borland.dx.sql.dataset.OracleProcedureProvider}</li>
	 * </ul>
	 * 
	 * @param provider
	 */
	public void setProvider(Provider provider) /*-throws DataSetException-*/ {
		synchronized (getOpenMonitor(editBlocked)) {
			synchronized (this) {
				StorageDataSet[] dataSets = null;
				int oldEditBlocked = preparePropertyRestructure();
				this.provider = provider;
				// ! Diagnostic.println("setProvider : " + provider);
				if (provider == null) {
					dataSets = _empty();
					dropColumns(true, false, null);
				}
				setProviderPropertyChanged(true);
				commitPropertyRestructure(oldEditBlocked);
				reopenOtherDataSets(dataSets);
			}
		}
	}

	private final void reopenOtherDataSets(StorageDataSet[] dataSets)
	/*-throws DataSetException-*/
	{
		if (dataSets != null) {
			for (int index = 0; index < dataSets.length; ++index) {
				if (dataSets[index] != this)
					dataSets[index].openStorage(null, new AccessEvent(this, AccessEvent.OPEN, AccessEvent.DATA_CHANGE));
			}
		}
	}

	private final void addDataColumn(Column column)
	/*-throws DataSetException-*/
	{
		if (data != null) {
			data.addColumn(column);
		}
	}

	private final int initExistingDataAndPrepareRestructure(boolean replaceColumns) {
		int ret = initExistingData(replaceColumns) | prepareRestructure();
		// initExistingData(replaceColumns);
		return ret;
	}

	int initExistingData(boolean replaceColumns)
	/*-throws DataSetException-*/
	{
		if (data == null && dataMayExist) {
			if (produceStore().exists(this)) {
				initData(replaceColumns);
				// ! data.initExisting = true;
				return OpenBlock.DataNull;
			} else
				dataMayExist = false;
		}
		return 0;
	}

	private final void setData(MatrixData data) {
		this.data = data;
		if (data == null)
			indexData = null;
		else
			indexData = data.getIndexData();
	}

	private void initData(boolean replaceColumns)
	/*-throws DataSetException-*/
	{
		setData(produceStore().open(this, data, matrixDataType, aggGroupColumnCount, aggManager, replaceColumns));
		indexData = data.getIndexData();
		DiagnosticJLimo.check(data != null);
		if (produceStore().isReadOnly(name))
			editBlocked |= OpenBlock.StoreReadOnly;
		else
			editBlocked &= ~OpenBlock.StoreReadOnly;

		dataMayExist = true;
		dataMonitor = data.getDataMonitor();
		DiagnosticJLimo.check(dataMonitor != null);
		if (needsPropertyUpdate && matrixDataType == DataConst.TABLE_DATA)
			updateProperties();
	}

	private final void checkStore(Store store, String storeName)
	/*-throws DataSetException-*/
	{
		// For designer's benefit. If already open, make sure that store is openable
		// before closing the StorageDataset().
		//
		if (store != null && isOpen() && java.beans.Beans.isDesignTime()) {
			store.getStoreInternals().open();
			if (storeName == null || storeName.length() < 1)
				DataSetException.invalidStoreName(storeName);
		}
	}

	/**
	 * Set the storage for the dataSet. By default memory storage is used for DataSets.
	 */
	public final void setStore(Store store)
	/*-throws DataSetException-*/
	{
		checkStore(store, name);
		boolean wasOpen = close();
		// ! int oldEditBlocked = preparePropertyRestructure();
		this.propertyStore = store;
		if (this.store != store) {
			setProviderPropertyChanged(true);
			this.store = store;
			this.schemaStoreName = null;
		}
		if (store != null) {
			dataMayExist = true;
			produceStore().attach(this);
		}
		// ! commitPropertyRestructure(oldEditBlocked);
		if (wasOpen) {
			// Diagnostic.check(java.beans.Beans.isDesignTime());
			open();
		}
	}

	public final Store getStore() {
		return propertyStore;
	}

	private StoreInternals produceStore() {
		if (store == null) {
			store = new MemoryStore();
			propertyStore = store;
		}
		return store.getStoreInternals();
	}

	boolean _isSortable(Column column) {
		return produceStore().isSortable(column);
	}

	final Object getOpenMonitor()
	/*-throws DataSetException-*/
	{
		// Diagnostic.printStackTrace();
		return produceStore().getOpenMonitor(this);
	}

	final Object getOpenMonitor(int editBlock)
	/*-throws DataSetException-*/
	{
		if ((editBlock & OpenBlock.NotOpen) != 0)
			return this;
		return getOpenMonitor();
	}

	final Object getOpenMonitor(boolean wasOpen)
	/*-throws DataSetException-*/
	{
		if (!wasOpen)
			return this;
		return getOpenMonitor();
	}

	final boolean isStorageOpen() {
		return (editBlocked & OpenBlock.NotOpen) == 0;
	}

	// DO NOT MAKE public see ProviderHelp.
	//
	void failIfOpen()
	/*-throws DataSetException-*/
	{
		if (isStorageOpen())
			DataSetException.dataSetOpen();
	}

	// Returns false if StorageDataSet was already open. Derived classes can implement this if
	// they need the additional context. This method is invoked from DataSet.open().
	// QueryDataSet overrides this method to extract pertinent master/detail information.
	//
	/** Used internally. */
	synchronized boolean openStorage(DataSet dataSet, AccessEvent event)
	/*-throws DataSetException-*/
	{
		StorageDataSet[] dataSets = null;
		boolean operationComplete;

		// ! if (store != null)
		// ! store = produceStore().opening(this);

		if (isProviderPropertyChanged()) {
			if (provider != null && dataSet != null && !isDetailDataSetWithFetchAsNeeded()) {
				if (!firstOpen || !produceStore().exists(this)) {
					synchronized (dataMonitor) {
						if (!provider.isAccumulateResults())
							dataSets = _empty();
						provider.provideData(this, true);
					}
				}
			}
			setProviderPropertyChanged(false);
		}

		if ((editBlocked & OpenBlock.Restructuring) != 0)
			editError();

		if (!isStorageOpen()) {

			// This effectively invalidates any DataRows that are refering to the
			// old column state.
			//
			if (reallocateColumnList) {
				reallocateColumns();
			}

			if (dataFileChanged && dataFile != null && dataFile.isLoadOnOpen()) {
				dataFileChanged = false;
				dataSets = _empty();
				operationComplete = false;
				try {
					dataFile.load(this);
					operationComplete = true;
				} catch (Exception ex) {
					DiagnosticJLimo.printStackTrace(ex);
					closeStorage(AccessEvent.UNKNOWN);
					DataSetException.throwException(DataSetException.DATA_FILE_LOAD_FAILED,
																					Res.bundle.getString(ResIndex.DataFileLoadFailed), ex);
				} finally {
					if (!operationComplete) {
						closeStorage(AccessEvent.UNKNOWN);
					}
				}
				initColumns();
			} else {
				initData(false);
				initColumns();
			}

			// ! Diagnostic.check(reallocateColumnList == false);

			setProviderPropertyChanged(false);

			// ! initData();

			setEditBlock(OpenBlock.NotOpen, false);
			DiagnosticJLimo.check(isStorageOpen());
			// ! Diagnostic.println("StorageDataSet: "+this);

			// Must come before call to addMasterUpdateListener() else
			// DataSet.openAccess will be called for the detail that has
			// already been opened.
			//
			if (accessListeners != null) {
				dispatchOpenAccessEvent(accessListeners, this, event);
			}

			DiagnosticJLimo.check(data != null);

			synchronized (dataMonitor) {
				if (!recalced() && calcFieldsColumns == null && aggManager == null)
					initCalcs();
			}

			// Now attempt to open the DataSet that is built into this StorageDataSet.
			// This openStorage() could have been invoked for a DataSetView that shares
			// this storage. If the DataSetView and StorageDataSet object are the same,
			// this code has no affect.
			//
			try {
				open();
			} finally {
				if (!isOpen())
					closeStorage(AccessEvent.UNKNOWN);
			}
			editBlocked &= ~OpenBlock.StoreClass;
			String storeClassName = data.getStoreClassName();
			if (storeClassName != null) {
				if (storeClassFactory == null)
					editBlocked |= OpenBlock.StoreClass;
			}

			firstOpen = false;
			reopenOtherDataSets(dataSets);
			initForeignKeys();
			return true;
		}
		return false;
	}

	private final void closeForeignKeys() {
		if (editListeners != null) {
			EditListener listener;
			for (int index = 0; editListeners != null && index < editListeners.length;) {
				listener = editListeners[index];
				if (listener instanceof ReferencedForeignKey) {
					((ReferencedForeignKey) listener).close();
					removeEditListener(listener);
				} else if (listener instanceof ReferencingForeignKey) {
					((ReferencingForeignKey) listener).close();
					removeEditListener(listener);
				} else
					++index;
			}
		}
	}

	private final void initForeignKeys() {
		if (foreignKeyDescs != null) {
			ReferencedForeignKey fk;
			for (int index = 0; index < foreignKeyDescs.length; ++index) {
				fk = new ReferencedForeignKey();
				fk.setForeignKeyDescriptor(foreignKeyDescs[index]);
				fk.setStore(store);
				fk.setOtherReference(this);
				addEditListener(fk);
			}
		}
		if (foreignKeyReferenceDescs != null) {
			ReferencingForeignKey fk;
			for (int index = 0; index < foreignKeyReferenceDescs.length; ++index) {
				fk = new ReferencingForeignKey();
				fk.setForeignKeyDescriptor(foreignKeyReferenceDescs[index]);
				fk.setOtherReference(this);
				fk.setStore(store);
				addEditListener(fk);
			}
		}
	}

	private final void initColumns()
	/*-throws DataSetException-*/
	{
		columnList.initColumns();

		if (calcFieldsListener != null && columnList.countCalcColumns(true, false) == 0)
			DataSetException.noCalcFields();

		if (calcAggFieldsListener != null && columnList.countAggCalcColumns() == 0)
			DataSetException.noCalcAggFields();
	}

	final void reallocateColumns()
	/*-throws DataSetException-*/
	{
		if (reallocateColumnList && !isStorageOpen()) {
			columnList = new ColumnList(this, columnList);
			shareColumns = null;
			reallocateColumnList = false;
		}
	}

	Column[] getShareColumns() {
		Column[] ret = shareColumns;
		if (ret == null)
			ret = columnList.cloneColumns();
		shareColumns = ret;
		return ret;
	}

	private final int preparePropertyRestructure()
	/*-throws DataSetException-*/
	{
		if (!java.beans.Beans.isDesignTime())
			failIfOpen();
		return prepareRestructure();
	}

	final void commitPropertyRestructure(int oldEditBlocked)
	/*-throws DataSetException-*/
	{
		if ((oldEditBlocked & OpenBlock.NotOpen) == 0)
			commitRestructure(new AccessEvent(this, AccessEvent.OPEN, AccessEvent.UNSPECIFIED), oldEditBlocked);
		else
			commitRestructure(null, oldEditBlocked);
	}

	final synchronized int prepareColumnPropertyRestructure()
	/*-throws DataSetException-*/
	{
		reallocateColumnList = true;
		return initExistingData(false) | preparePropertyRestructure();
	}

	final synchronized void notifyPropertyChange()
	/*-throws DataSetException-*/
	{
		if (isOpen() && accessListeners != null) {
			dispatchOpenAccessEvent(accessListeners, this, new AccessEvent(this, AccessEvent.CLOSE, AccessEvent.PROPERTY_CHANGE));
			dispatchOpenAccessEvent(accessListeners, this, new AccessEvent(this, AccessEvent.OPEN, AccessEvent.PROPERTY_CHANGE));
		}
	}

	final synchronized void updateProperties()
	/*-throws DataSetException-*/
	{
		if (store != null && data != null) {
			produceStore().updateProperties(this);
			needsPropertyUpdate = false;
		} else
			needsPropertyUpdate = true;
		// ! if (data != null) {
		// ! data.prepareRestructure(this);
		// ! data.commitRestructure(this);
		// ! }
	}

	final boolean closeStorage(int reason)
	/*-throws DataSetException-*/
	{
		return closeStorage(reason, reason == AccessEvent.UNKNOWN);
	}

	/*
	 * Close the dataSet and notify any accessListeners of this event. Returns true if the StorageDataSet was open.
	 */
	// DO NOT MAKE public. Can add to ProviderHelp if needed.
	//
	final boolean closeStorage(int reason, boolean closeData)
	/*-throws DataSetException-*/
	{
		// isStorageOpen() not always enough. Could be in openStorage and _empty() called.
		// in that case, data can be non null, but the OpenBlock.NotOpen bit is not set.
		// Another important case is errors that occur during DataSet.open(). In
		// this case data may have been initilized (provider call to initData()), but
		// OpenBlock.NotOpen bit has not been cleared yet. So if closeData true and data
		// not null, attempt to close the data. Otherwise, data left open can cause
		// problems for JDataStore implementation of Store - dangling cache block
		// references from table that will never be closed.
		//
		if (isStorageOpen() || (closeData && data != null)) {
			// Note that request to close could fail with exception due to something
			// like a row that does not pass a validity check not being postable.
			// Its also nice to not put event notifications to other components in a
			// synchronized block. Thats why setEditBlock() is done after event though
			// calls to this method could turn recursive.
			//
			if (accessListeners != null)
				dispatchAccessEvent(accessListeners, new AccessEvent(this, AccessEvent.CLOSE, reason));
			DiagnosticJLimo.check(reason != AccessEvent.PROPERTY_CHANGE);
			if (provider != null && reason != AccessEvent.STRUCTURE_CHANGE && reason != AccessEvent.PROPERTY_CHANGE)
				provider.close(this, false);
			closeData(reason, closeData);
			closeForeignKeys();

			// Reset cached value:
			this.schemaStoreName = null;

			return true;
		}
		return false;
	}

	private final Object getDataMonitor() {
		// DataSet may be closed, but data (TxData) may be initialized.
		//
		if (data != null)
			return data.getDataMonitor();
		else
			return dataMonitor;
	}

	final void closeData(int reason, boolean closeData)
	/*-throws DataSetException-*/
	{
		if (data != null && (editBlocked & OpenBlock.Closing) == 0) {
			synchronized (this) {
				synchronized (getDataMonitor()) {
					synchronized (dataMonitor) {
						setEditBlock(OpenBlock.NotOpen, true);
						// ! Diagnostic.println("close StorageDataSet: "+this);
						// ! Diagnostic.printStackTrace();
						// Block reentrancy.
						//
						editBlocked |= OpenBlock.Closing;
						try {
							setData(data.closeDataSet(this, matrixDataType, aggManager, fetchDataSet, reason, closeData));
						} finally {
							editBlocked &= ~OpenBlock.Closing;
						}
						dataMayExist = true;

						// subtle. Aggmanager.internalReadRow goes out of date, especially
						// with a DataStore, so let go.
						//
						if (data == null)
							clearCalcFieldsState();
						dataMonitor = this;
					}
				}
			}
		}
	}

	final void startEditing() /*-throws DataSetException-*/ {
		if (editBlocked != 0)
			editError();
	}

	private final void editError() /*-throws DataSetException-*/ {
		DiagnosticJLimo.check(editBlocked != 0);

		if ((editBlocked & OpenBlock.Corrupt) != 0) {
			DataSetException.dataSetCorrupt(getReadableTableName(name));
		}

		if ((editBlocked & OpenBlock.Restructuring) != 0)
			DataSetException.restructureInProgress(getReadableTableName(name));

		if ((editBlocked & OpenBlock.NotOpen) != 0) {
			DataSetException.dataSetNotOpen();
		}

		if ((editBlocked & OpenBlock.ReadOnly) != 0) {
			ValidationException.readOnlyDataSet();
		}

		if ((editBlocked & (OpenBlock.Restructure | OpenBlock.DataNull)) != 0)
			DataSetException.restructureInProgress(getReadableTableName(name));

		if ((editBlocked & OpenBlock.MetaDataMissing) != 0)
			DataSetException.notUpdatable();

		if ((loadBlocked & OpenBlock.Resolve) != 0)
			DataSetException.resolveInProgress();

		if ((editBlocked & OpenBlock.StoreReadOnly) != 0)
			DataSetException.readOnlyStore(getReadableTableName(name));

		if ((editBlocked & OpenBlock.NeedsRecalc) != 0)
			DataSetException.needsRecalc(getReadableTableName(name));

		if ((editBlocked & OpenBlock.StoreClass) != 0)
			DataSetException.invalidClass(getClass().getName(), data.getStoreClassName());

		DiagnosticJLimo.fail();
	}

	private final void failIfReadOnlyStore()
	/*-throws DataSetException-*/
	{
		if ((editBlocked & OpenBlock.StoreReadOnly) != 0)
			DataSetException.readOnlyStore(tableName);
	}

	// The prepare/commitRestructure forces the StorageDataSet, its
	// associated DataSets to effectively close. UI controls that access DataSets
	// are notified that their DataSet is disabled. The commit will signal the associated
	// objects to reopen. Note that DataRows just go out of date when Restructures happen.
	// This is because DataRows are not AccessEvent listeners of StorageDataSets.
	//

	private final int prepareRestructure()
	/*-throws DataSetException-*/
	{
		synchronized (getOpenMonitor(editBlocked)) {
			int oldEditBlocked = editBlocked;
			closeStorage(AccessEvent.STRUCTURE_CHANGE);

			reallocateColumnList = true;

			setEditBlock(OpenBlock.Restructure, true);

			if (data != null) {
				synchronized (dataMonitor) {
					data.clearInternalReadRow();
					data.prepareRestructure(this);
				}
			}

			return oldEditBlocked;
		}
	}

	private final void commitRestructure(AccessEvent accessEvent, int oldEditBlocked)
	/*-throws DataSetException-*/
	{
		synchronized (getOpenMonitor(oldEditBlocked)) {
			commitRestructure(oldEditBlocked);

			if ((oldEditBlocked & OpenBlock.NotOpen) == 0)
				openStorage(this, accessEvent);
			else if (data != null && (oldEditBlocked & OpenBlock.DataNull) != 0) {
				closeData(AccessEvent.UNKNOWN, true);
			}
		}
	}

	/*
	 * WARNING: sends no notifications.
	 */
	private final void commitRestructure(int oldEditBlocked)
	/*-throws DataSetException-*/
	{
		if ((oldEditBlocked & OpenBlock.Restructure) == 0) {
			failIfOpen();
			// ! columnList.invalidateDataRows();
			columnList.clearCache();
			setEditBlock(OpenBlock.Restructure, false);

			// ! Diagnostic.println("commitR:");
			if (data != null) {
				synchronized (dataMonitor) {
					// ! Diagnostic.println("commitR2:");
					data.commitRestructure(this);
				}
			}

			structureAge++;

			if (data != null && (oldEditBlocked & OpenBlock.DataNull) != 0)
				closeData(AccessEvent.UNKNOWN, true);

			// ! JOAL: DO NOT call openStorage here (Will cause problems for master detail)
		}
	}

	/**
	 * Add a column to a StorageDataSet. The passed in column is cloned before being added to the dataset. Returns ordinal position of the column.
	 */
	public final int addColumn(Column column)
	/*-throws DataSetException-*/
	{
		column.setPersist(true);
		return addColumn(column, true, false, true);
	}

	private final int addColumn(Column column, boolean processOrdinals, boolean isUnique, boolean setOrdinals)
	/*-throws DataSetException-*/
	{
		int position = -1;
		Column newColumn = column;// (Column)column.clone();

		// !System.err.println("addColumn(" + column + "), clone is " + newColumn);
		synchronized (this) {
			synchronized (dataMonitor) {
				int oldEditBlocked = prepareRestructure();
				int step = 0;
				try {
					ColumnList newColumnList;
					// major performance implications for wide DataSests
					// (ie 100 Columns. Optimizing on reallocColumnList resulted
					// in performance win that is order of magnitudes.
					//
					if (reallocateColumnList)
						newColumnList = columnList;
					else
						newColumnList = new ColumnList(this, columnList);

					position = newColumnList.addColumn(this, newColumn, isUnique, setOrdinals);

					step = 1;

					addDataColumn(newColumnList.cols[position]);

					step = 2;

					// If no exceptions, switch to new columnList.
					//
					columnList = newColumnList;
					// Undoes the setting by prepareRestructure, because
					// the columnList has already been reallocated - performance
					// and functionality affect here. If all columns added, then
					// DataRow constructed with DataSet "before" the DataSet is opened,
					// do not want to reallocate ColumnList, else new DataRow will
					// be deemed incompatible with DataSet.
					//
					// ! reallocateColumnList = false;

					// Now reorder according to preferred ordinals:
					if (processOrdinals)
						processPreferredOrdinals();
				} finally {
					if (step == 1 && reallocateColumnList)
						columnList.dropColumn(newColumn);

					if ((oldEditBlocked & OpenBlock.NotOpen) == 0)
						commitRestructure(new AccessEvent(this, AccessEvent.OPEN, AccessEvent.COLUMN_ADD), oldEditBlocked);
					else
						commitRestructure(oldEditBlocked);
				}
			}
		}

		return position;
	}

	/**
	 * Adds a Column to the DataSet only if it does not already exist. The specified column is cloned before being added to the DataSet. The return value int
	 * indicates the ordinal position of the newly-added Column. On error, this method throws a DataSetException of COLUMN_TYPE_CONFLICT.
	 *
	 * @param column
	 *          The Column component to add to this StorageDataSet.
	 * @return
	 */
	public final int addUniqueColumn(Column column)
	/*-throws DataSetException-*/
	{
		// !System.err.println("addUniqueColumn(" + column.toString() + ")");

		int ordinal = findOrdinal(column.getColumnName());
		if (ordinal == -1)
			return addColumn(column);

		return ordinal;
	}

	/**
	 * Add a column to a StorageDataSet. Returns ordinal position of the column.
	 */
	/**
	 * Adds a Column to the end of existing columns in a DataSet. Row values for the added Column are set to <b>null</b>.
	 * <p>
	 * If the store property is set, StorageDataSet.restructure() must be called before this column can be edited.
	 *
	 * @param columnName
	 *          The String name of the Column by which you refer to it most of the time.
	 * @param caption
	 *          The name of the (displayed) label for the Column in a data-aware control. You may want to use a more descriptive name than columnName.
	 * @param dataType
	 *          The data type of the field to be added. Valid values for this parameter are described in {@link com.borland.dx.dataset.Variant} variables.
	 * @return The return value int indicates the ordinal position of the newly-added Column.
	 */
	public final int addColumn(String columnName,
			String caption,
			int dataType)
	/*-throws DataSetException-*/
	{
		return addColumn(new Column(columnName, caption, dataType));
	}

	/**
	 * Add a column to a StorageDataSet. Returns ordinal position of the column.
	 */

	/**
	 * Adds a Column to a DataSet where columnName indicates the String name of the Column and dataType is the data type of the Column. The Column is added at the
	 * end of all existing columns. The return value int indicates the ordinal position of the newly-added Column. Use the {@link #enableDataSetEvents(boolean)}
	 * method to propagate changes to a control that is bound to this DataSet.
	 * <p>
	 * To achieve similar results as when calling StorageDataSet.setColumns(...) method, call the addColumn(...) method followed by Column.setPersist(true).
	 * <p>
	 * If the store property is set, StorageDataSet.restructure() must be called before this column can be edited.
	 *
	 * @param columnName
	 *          The String name of the Column component to add to the StorageDataSet.
	 * @param datatype
	 *          The data type of the data in the Column. Valid values for this parameter are defined in {@link com.borland.dx.dataset.Variant} variables.
	 * @return The ordinal position of the newly-added Column.
	 */
	public final int addColumn(String columnName, int datatype)
	/*-throws DataSetException-*/
	{
		return addColumn(columnName, columnName, datatype);
	}

	/**
	 * Change column at ordinal position to match the properties in newColumn. Note that newColumn must be a separate Column instance from the one stored at
	 * position Ordinal in this StorageDataSet. To ensure this, clone the target Column before making changes to it.
	 */

	public synchronized final void changeColumn(int oldOrdinal, Column newColumn)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			Column oldColumn = getColumn(oldOrdinal);

			columnList.checkChangeColumn(oldOrdinal, newColumn);

			// !RC We can't do this test if client passes in Column from DataSet
			// ! if (oldColumn == newColumn)
			// ! DataSetException.cannotChangeColumn();

			int oldEditBlocked = initExistingDataAndPrepareRestructure(false);

			try {

				if (data != null) {
					// data may have been set and dataMonitor changed, so synch on
					// dataMonitor.
					//
					synchronized (dataMonitor) {
						data.changeColumn(oldOrdinal, oldColumn, newColumn);
					}
				}

				columnList.changeColumn(this, oldOrdinal, newColumn);

			} finally {
				AccessEvent event = new AccessEvent(this, AccessEvent.OPEN,
						AccessEvent.COLUMN_CHANGE,
						oldColumn, newColumn);
				commitRestructure(event, oldEditBlocked);
			}

			// Some clown changed the preferredOrdinal and called changeColumn.
			//
			int preferredOrdinal = newColumn.getPreferredOrdinal();
			if (preferredOrdinal > -1 && preferredOrdinal != oldOrdinal) {
				moveColumn(oldOrdinal, preferredOrdinal >= columnList.count ? columnList.count - 1 : preferredOrdinal);
			}

			if (!oldColumn.getColumnName().equalsIgnoreCase(newColumn.getColumnName()))
				updateForeignKeyColumnName(oldColumn.getColumnName(), newColumn.getColumnName(), true, true);
		}
	}

	/**
	 * Move column at ordinal postion oldOrdinal to ordinal position newOrdinal.
	 */

	/**
	 * Moves a Column at the specified ordinal position to a new ordinal position. If either parameter is invalid, a DataSetException of type
	 * INVALID_COLUMN_POSITION is thrown.
	 * <p>
	 * If the store property is set, StorageDataSet.restructure() must be called before this column can be edited.
	 *
	 * @param oldOrdinal
	 *          The ordinal position of the Column component to move.
	 * @param newOrdinal
	 *          The ordinal position where the Column should be moved to.
	 */
	public synchronized final void moveColumn(int oldOrdinal, int newOrdinal)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			// Cannot initExistingData because processColumnOrder() calls this while opening
			// the StorageDataSet. Causes performance issues and infinite recursion.
			// int oldEditBlocked = initExistingData(false) | prepareRestructure();
			int oldEditBlocked = prepareRestructure();

			try {
				columnList.moveColumn(oldOrdinal, newOrdinal);

				if (data != null)
					data.moveColumn(oldOrdinal, newOrdinal);
			} finally {
				AccessEvent event = new AccessEvent(this, AccessEvent.OPEN,
						AccessEvent.COLUMN_MOVE,
						oldOrdinal, newOrdinal);
				commitRestructure(event, oldEditBlocked);
			}
		}

	}

	/**
	 * Returns true for StorageDataSet components when structural changes have occurred (add, drop, move, change column) and the store being used requires a
	 * restructure. To cause the restructure to happen, call the restructure() method or click the Restructure button in the JBuilder Column Designer. After a
	 * successful restructure, getNeedsRestructure() returns <b>false</b>.
	 * <p>
	 * A StorageDataSet that uses a MemoryStore always returns <b>false</b>. Currently, this method is meaningful only with DataStore use.
	 * <p>
	 * A StorageDataSet with pending structural changes can still be used. Moved columns can be read and written to. Deleted columns are not visible, inserted
	 * columns can be read, but not written, changed data type columns can be read but not written to.
	 *
	 * @return
	 */
	public final boolean getNeedsRestructure()
	/*-throws DataSetException-*/
	{
		int dataNull = initExistingData(false);

		if (data == null)
			return false;

		// ! boolean wasOpen = open();
		boolean ret;

		try {
			synchronized (dataMonitor) {
				ret = data.getNeedsRestructure();
			}
		} finally {
			if (dataNull != 0)
				closeData(AccessEvent.UNKNOWN, true);
			// ! if (!wasOpen)
			// ! close();
		}

		return ret;
	}
	// !/*
	// ! public final void setNeedsRestructure(boolean needsRestructure)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! if (data != null)
	// ! data.setNeedsRestructure(needsRestructure);
	// ! }
	// !*/

	/**
	 * Restructures the StorageDataSet. Currently meaningful only for DataStore after a move, delete, add, or change column method call.
	 * <p>
	 * The needsRestructure property returns whether a restructure operation is pending. A StorageDataSet with pending structural changes can still be used, and
	 * moved columns can be read and written to. Deleted columns are not visible, inserted columns can be read but not written and changed data type columns can
	 * be read but not written to.
	 * <p>
	 * You can perform a restructure operation through the JBuilder column designer by opening the StorageDataSet and clicking the restructure button.
	 * <p>
	 * The restructure() method can also be used to repair or compact a StorageDataSet and its associated indexes even when there are no pending structural
	 * changes.
	 */
	public final void restructure()
	/*-throws DataSetException-*/
	{
		initCalcs();

		boolean operationCompleted = false;
		boolean wasOpen = false;
		boolean dataNeedsRecalc = needsRecalc;
		ForeignKeyDescriptor[] oldFks = null;
		ForeignKeyDescriptor[] oldReferenceFks = null;
		ForeignKeyDescriptor desc;
		try {
			synchronized (this) {
				wasOpen = (editBlocked & OpenBlock.NotOpen) == 0 && close();
				setEditBlock(OpenBlock.Restructuring, true);
			}
			if (data == null)
				initData(false);
			oldFks = foreignKeyDescs;
			oldReferenceFks = foreignKeyReferenceDescs;
			failIfReadOnlyStore();
			if (fetchDataSet != null)
				fetchDataSet.close();
			synchronized (dataMonitor) {
				setData(data.restructure(this, calcFieldsListener, calcAggFieldsListener));
			}
			if (data != null)
				dataNeedsRecalc = data.needsRecalc(this);
			// This is important for DataStore with a Agg calc - must reinitialize
			// with new AggDataSets.
			//

			clearCalcFieldsState();

			operationCompleted = true;
		} finally {
			synchronized (this) {
				setEditBlock(OpenBlock.Restructuring, false);
				if (wasOpen) {
					open();
					DiagnosticJLimo.check(data != null);
				}
			}
			// ! if (data.shouldRecalc())
			// ! data.recalc(this, aggManager);
			if (operationCompleted) {
				if (!dataNeedsRecalc)
					synchronized (getDataMonitor()) {
						setNeedsRecalc(false);
						if (oldFks != null) {
							for (int index = 0; index < oldFks.length; ++index) {
								desc = oldFks[index];
								StorageDataSet table = desc.openReferenceTableData(this, getStore());
								ForeignKeyDescriptor invertedDesc = desc.invert(this);
								// This will have already have been removed if the stream was dropped.
								// Stream gets dropped during restructure if the actual structure
								// of the table itself changes (ie column add/drop).
								//
								if (table.hasForeignKey(invertedDesc, table.foreignKeyReferenceDescs) != null)
									table.removeForeignKeyReference(invertedDesc);
								if (table != this)
									table.closeData(AccessEvent.UNKNOWN, true);
							}
							try {
								setForeignKeys(oldFks, 0, 0);
							} catch (DataSetException ex) {
								for (int index = 0; index < oldFks.length; ++index) {
									try {
										addForeignKey(oldFks[index]);
									} catch (DataSetException ex2) {
										DiagnosticJLimo.println("ex: " + ex2.getMessage());
									}
								}
							}
						}
						if (oldReferenceFks != null) {
							for (int index = 0; index < oldReferenceFks.length; ++index) {
								try {
									desc = oldReferenceFks[index];
									StorageDataSet table = desc.openReferenceTableData(this, getStore());
									table.removeForeignKey(desc.invert(this));
									table.addForeignKey(desc.invert(this));
									if (table != this)
										table.closeData(AccessEvent.UNKNOWN, true);
								} catch (DataSetException ex) {
									DiagnosticJLimo.println("ex: " + ex.getMessage());
								}
							}
						}
					}
			} else
				clearCalcFieldsState();
		}
	}

	synchronized final void setNeedsRecalc(boolean needsRecalc)
	/*-throws DataSetException-*/
	{
		this.needsRecalc = needsRecalc;

		if (needsRecalc)
			clearCalcFieldsState();
		else {
			editBlocked &= ~OpenBlock.NeedsRecalc;
			if (data != null) {
				data.notifyRecalc(this);
				updateProperties();
			}
		}
	}

	private final boolean recalced()
	/*-throws DataSetException-*/
	{
		// ! Diagnostic.println("bugCount: "+bugCount+" "+thisCount);
		if (matrixDataType == DataConst.TABLE_DATA) {
			if (data == null)
				failIfNotOpen();
			if (needsRecalc) {
				if (data.needsRecalc(this)) {
					if (!getNeedsRestructure()) {
						recalc();
						return true;
					}
				} else {
					editBlocked &= ~OpenBlock.NeedsRecalc;
					needsRecalc = false;
				}
			} else {
				if (data.needsRecalc(this) && !data.isEmpty()) {
					DiagnosticJLimo.check(data.needsRecalc(this) && !data.isEmpty());
					editBlocked |= OpenBlock.NeedsRecalc;
					return false;
				} else {
					editBlocked &= ~OpenBlock.NeedsRecalc;
					needsRecalc = false;
				}
			}
		} else {
			editBlocked &= ~OpenBlock.NeedsRecalc;
			needsRecalc = false;
		}
		return false;
	}

	/**
	 * Specifies the Column components in the StorageDataSet and sets the persist property to <b>true</b> for all specified columns. Any pre-existing columns are
	 * removed when this property is set.
	 *
	 * @param columns
	 */
	public synchronized void setColumns(Column[] columns)
	/*-throws DataSetException-*/
	{
		int oldEditBlocked = preparePropertyRestructure();
		if (data != null)
			dropColumns(false, true, columns);
		// !/*
		// ! columnList = new ColumnList();
		// ! if (data != null)
		// ! setData(produceStore().setColumns(this, data, columns));
		// !*/
		addUniqueColumns(columns, columns, true, false);
		for (int i = 0; i < columnList.count; ++i)
			columnList.cols[i].setPersist(true);

		commitPropertyRestructure(oldEditBlocked);
	}

	/*
	 * Used by providers. Different than setColumns in that it preserves persistent columns. If keepExistingColumns is true non-persistent columns will also be
	 * retained. Note that several column properties in the columns array will be merged in with existing columns in the StorageDataSet that have the same name
	 * property setting.
	 */
	// DO NOT MAKE public. see ProviderHelp.
	//
	final int[] initData(Column[] columns,
			boolean updateColumns,
			boolean keepExistingColumns)
	/*-throws DataSetException-*/
	{
		synchronized (this) {
			synchronized (dataMonitor) {
				// !/*
				// ! if (emptyRows) {
				// ! if (isDetailDataSetWithFetchAsNeeded())
				// ! emptyAllRows();
				// ! else {
				// ! empty();
				// ! }
				// ! }
				// !*/
				if (updateColumns) {
					int oldEditBlocked = prepareRestructure();
					// ! Diagnostic.check((oldEditBlocked&OpenBlock.NotOpen) != 0);
					dropColumns(true, keepExistingColumns, columns);
					addUniqueColumns(columns, columns, true, true);
					commitRestructure(oldEditBlocked);
				}
				if (data == null)
					initExistingData(true);
				if (data != null && getNeedsRestructure()) {
					restructure();
					// Must initData because provider may not call startLoading().
					// Normally startLoading() will initData, but if provider sees
					// no rows, the data will not be initialized. Shows as bug
					// in fetchAsNeeded details where StorageDataSet will be open
					// and have a null data.
					//
					if (data == null)
						initData(false);
				}
				return createColumnMap(columns, null);
			}
		}
	}

	// DO NOT MAKE public see ProviderHelp.
	//
	final int[] createColumnMap(Column[] columns, int[] columnMap)
	/*-throws DataSetException-*/
	{
		if (columnMap == null)
			columnMap = new int[columns.length];
		Column[] boundColumns = columnList.cols;

		for (int index = 0; index < columns.length; ++index) {
			if (columns[index] == boundColumns[index])
				columnMap[index] = boundColumns[index].ordinal;
			else
				columnMap[index] = getColumn(columns[index].getColumnName()).ordinal;
		}
		return columnMap;
	}

	final void addUniqueColumns(Column[] columns, Column[] orderColumns, boolean reconcile, boolean coerceColumns)
	/*-throws DataSetException-*/
	{
		boolean taskComplete = false;

		Column column;
		Column newColumn;
		boolean changedColumns = false;
		int ordinal;

		try {
			DiagnosticJLimo.check(columns != null);
			for (int index = 0; index < columns.length; ++index) {
				newColumn = columns[index];
				ordinal = columnList.findOrdinal(newColumn.getColumnName(), newColumn.hash);
				if (ordinal != -1) {
					if (reconcile) {
						column = columnList.cols[ordinal];
						changedColumns = true;
						// ! Column copy = (Column)column.clone();

						column.reconcile(newColumn, metaDataUpdate, coerceColumns);
						// ! System.err.println("replace() -- reconciling " + columns[index].toString());
						if (data != null)
							changeColumn(column.ordinal, column);
					} else if (ordinal != index)
						changedColumns = true;
				} else {
					columns[index].ordinal = -1;
					// ! System.err.println("replace() -- adding col " + columns[index].toString());
					addColumn(columns[index], false, true, false);
				}
			}

			columnList.setOrdinals();

			processColumnOrder(orderColumns, changedColumns);

			taskComplete = true;
		} finally {
			if (!taskComplete) // Exception was thrown! - data and columnList could be wacked out!
				editBlocked |= OpenBlock.Corrupt;
		}
	}

	private final void processColumnOrder(Column[] columns, boolean changedColumns)
	/*-throws DataSetException-*/
	{
		// First order them by the way columns[] came in.
		//
		int maxColumns = Math.min(columns.length, getColumnCount());
		Column column;
		if (getColumnCount() > columns.length || changedColumns) {
			for (int index = 0; index < maxColumns; ++index) {
				column = getColumn(columns[index].getColumnName());
				if (column.preferredOrdinal == -1 && column.ordinal != index) {
					moveColumn(column.ordinal, index);
				}
			}
		}

		// Now order anything that has an explicit preference. This comes last
		// because it takes precedence over the loop above.
		//
		processPreferredOrdinals();
	}

	// !/*
	// ! final boolean processPreferredOrdinals()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! boolean movedColumns = false;
	// ! Column[] boundColumns = columnList.columns;
	// ! Column column;
	// ! Column column2;
	// ! for (int index = 0; index < boundColumns.length; ++index) {
	// ! column = boundColumns[index];
	// ! if (column.preferredOrdinal > -1 && column.preferredOrdinal != column.ordinal) {
	// ! int ordinal = column.preferredOrdinal;
	// ! if (ordinal >= boundColumns.length)
	// ! ordinal = boundColumns.length - 1;
	// ! column2 = boundColumns[ordinal];
	// ! if (column2.preferredOrdinal != ordinal) {
	// ! moveColumn(column.ordinal, ordinal);
	// ! movedColumns = true;
	// ! Diagnostic.check(getColumn(column.getColumnName()) != null);
	// ! }
	// ! }
	// ! }
	// ! return movedColumns;
	// ! }
	// !*/

	final void processPreferredOrdinals()
	/*-throws DataSetException-*/
	{
		// ++bugCount;
		// if (bugCount == 1427)
		// Diagnostic.println("stop:");
		Column[] columns = columnList.cols;
		// Sort by preferred ordinal. This works great for nearly
		// sorted and small sort sets. Sorting keeps the positioning alg simple.
		//
		int jOrdinal;
		int count = columnList.count;
		for (int ordinal = 1; ordinal < count; ++ordinal) {
			jOrdinal = ordinal;
			while (jOrdinal > 0 && columns[ordinal].preferredOrdinal < columns[jOrdinal - 1].preferredOrdinal)
				--jOrdinal;
			// Moves are expensive, so wait til we know the position.
			//
			if (jOrdinal < ordinal)
				moveColumn(ordinal, jOrdinal);
		}
		DiagnosticJLimo.check(columns == columnList.cols);
		Column column;
		int pos;
		// Now pull preferred ordinals into place, low ordinals first.
		//
		for (int ordinal = 0; ordinal < count; ++ordinal) {
			column = columns[ordinal];
			if (column.preferredOrdinal > -1) {
				if (column.preferredOrdinal < ordinal) {
					pos = column.preferredOrdinal;
					while (columns[pos].preferredOrdinal != -1 || pos >= ordinal)
						++pos;
					if (pos < ordinal)
						moveColumn(ordinal, pos);
					else
						break; // We are sorted, so nothing else will move.
				} else
					break; // We are sorted, so nothing else will move.
			}
		}
	}

	private static final Column hasColumn(Column[] columns, Column column) {
		int hash = column.hash;
		for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
			// ! Diagnostic.println("name: "+name);
			// ! Diagnostic.println("columnName: "+columns[ordinal].getColumnName());
			if (columns[ordinal].hash == hash && columns[ordinal].getColumnName().equalsIgnoreCase(column.getColumnName()))
				return columns[ordinal];
		}
		return null;
	}

	private final void dropColumns(boolean keepPersistentColumns, boolean keepExistingColumns, Column[] newColumns)
	/*-throws DataSetException-*/
	{
		Column[] columns = columnList.cols;
		if (columns != null) {
			for (int ordinal = 0; ordinal < columnList.count; ++ordinal) {
				if (!keepPersistentColumns || !columns[ordinal].isPersist()) {
					if (!keepExistingColumns || hasColumn(newColumns, columns[ordinal]) == null) {
						dropColumn(columns[ordinal]);
						--ordinal;
					}
				}
			}
		}
	}

	/**
	 * Creates a copy of all Columns in the StorageDataSet
	 * 
	 * @return
	 */
	public Column[] cloneColumns() {
		return columnList.cloneColumns();
	}

	/**
	 * Drop column by Column name.
	 */
	public final void dropColumn(String columnName) /*-throws DataSetException-*/ {
		dropColumn(columnList.getColumn(columnName));
	}

	/**
	 * Drop column by Column component.
	 */
	public synchronized final void dropColumn(Column column) /*-throws DataSetException-*/ {

		synchronized (dataMonitor) {

			int oldEditBlocked = initExistingDataAndPrepareRestructure(false);

			try {
				int ordinal = columnList.dropColumn(column).ordinal;
				if (data != null) {
					synchronized (dataMonitor) {
						data.dropColumn(ordinal);
					}
				}

				// Now reorder according to preferred ordinals:
				processPreferredOrdinals();
			} finally {
				if ((oldEditBlocked & OpenBlock.NotOpen) == 0) {
					AccessEvent event = new AccessEvent(this,
							AccessEvent.OPEN,
							AccessEvent.COLUMN_DROP,
							column);
					commitRestructure(event, oldEditBlocked);
				} else
					commitRestructure(oldEditBlocked);
			}
		}
	}

	private final EventMulticaster casterAdd(EventMulticaster listeners, EventListener listener) {
		// ! Causes dead lock when DataSet.close removes access listeners and another thread is closing
		// ! a table that is using the same conneciton
		// ! synchronized(dataMonitor) {
		return EventMulticaster.add(listeners, listener);
		// ! }
	}

	private final EventMulticaster casterRemove(EventMulticaster listeners, EventListener listener) {
		// ! Causes dead lock when DataSet.close removes access listeners and another thread is closing
		// ! a table that is using the same conneciton
		// ! synchronized(dataMonitor) {
		return EventMulticaster.remove(listeners, listener);
		// ! }
	}

	// Call made to master dataset.
	//
	final synchronized void storageAddMasterUpdateListener(MasterUpdateListener listener) {
		masterUpdateListeners = casterAdd(masterUpdateListeners, listener);
		if (masterUpdateEvent == null)
			masterUpdateEvent = new MasterUpdateEvent(this);
	}

	// Call made to master dataset.
	//
	final synchronized void storageRemoveMasterUpdateListener(MasterUpdateListener listener) {
		masterUpdateListeners = casterRemove(masterUpdateListeners, listener);
	}

	/**
	 * Adds the specified {@link com.borland.dx.dataset.LoadListener} object.
	 * 
	 * @param listener
	 */
	public synchronized final void addLoadListener(LoadListener listener) {
		loadListeners = casterAdd(loadListeners, listener);
	}

	/**
	 * Removes the specified LoadRowListener object.
	 * 
	 * @param listener
	 */
	public synchronized final void removeLoadListener(LoadListener listener) {
		loadListeners = casterRemove(loadListeners, listener);
	}

	public void addLoadRowListener(LoadRowListener listener)
			throws TooManyListenersException {
		if (listener == null)
			throw new IllegalArgumentException();

		if (loadRowListener != null)
			throw new TooManyListenersException();

		loadRowListener = listener;
	}

	public synchronized void removeLoadRowListener(LoadRowListener listener) {

		synchronized (dataMonitor) {
			if (loadRowListener != listener)
				throw new IllegalArgumentException();

			loadRowListener = null;
		}
	}

	synchronized void openIndex(DataSet dataSet) /*-throws DataSetException-*/ {
		synchronized (dataMonitor) {
			DiagnosticJLimo.check(dataMonitor == data.getDataMonitor());
			dataSet.index = data.openIndex(dataSet, true);
		}
		DiagnosticJLimo.check(dataSet.index != null);
	}

	final boolean lookup(DataSet dataSet, Column[] columns, ReadRow readRow, DataRow returnRow, int locateOptions)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (dataSet.isEditing())
				dataSet._post();
			long rowFound = find(dataSet.index, dataSet.currentRow, columns, readRow, locateOptions);
			if (rowFound < 0)
				return false;

			if (returnRow != null) {
				long internalRow = dataSet.index.internalRow(rowFound);
				if (returnRow.columnList.hasScopedColumns() || aggManager != null)
					getScopedRowData(internalRow, returnRow);
				else
					data.getRowData(internalRow, returnRow.getRowValues(columnList));
			}

			return true;
		}
	}

	final boolean lookup(DataSet dataSet,
			Column[] columns,
			ReadRow readRow,
			int sourceOrdinal,
			Variant sourceValue,
			int locateOptions)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (dataSet.isEditing())
				dataSet._post();
			long rowFound = find(dataSet.index, dataSet.currentRow, columns, readRow, locateOptions);
			if (rowFound < 0)
				return false;

			long internalRow = dataSet.index.internalRow(rowFound);
			getStorageVariant(internalRow, sourceOrdinal, sourceValue);

			return true;
		}
	}

	boolean locate(DataSet dataSet, Column[] columns, ReadRow readRow, int locateOptions)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			// ! dataSet._cancel();
			if (dataSet.isEditing())
				dataSet._post();
			long rowFound = find(dataSet.index, dataSet.currentRow, columns, readRow, locateOptions);
			if (rowFound < 0 || !dataSet._goToRow(rowFound))
				return false;
			return true;
		}
	}

	final int getOtherReferenceCount(DataSet other, ReadRow readRow) {
		Index otherIndex = other.index;
		long foundRow = otherIndex.locate(0, readRow.getColumnList().getScopedArray(), readRow.getLocateValues(columnList), Locate.FIRST);
		if (foundRow != -1 && this == other.dataSetStore) {
			if (otherIndex.lastRow() == foundRow)
				return 1;
			foundRow = otherIndex.locate(foundRow + 1, readRow.getColumnList().getScopedArray(), readRow.getLocateValues(columnList), Locate.NEXT | Locate.FAST);
			if (foundRow != -1)
				return 2;
			return 1;
		}
		if (foundRow != -1)
			return 2;
		return 0;
	}

	final boolean exists(DataSet other, ReadRow readRow) {
		long foundRow = other.index.locate(0, readRow.getColumnList().getScopedArray(), readRow.getLocateValues(columnList), Locate.FIRST);
		return foundRow != -1;
	}

	final long find(Index index, long currentRow, Column[] columns, ReadRow readRow, int locateOptions)
	/*-throws DataSetException-*/
	{
		boolean moved = false;

		long startRow;

		int startMask = (locateOptions & Locate.START_MASK);

		switch (startMask) {
			case Locate.NEXT:
				if (currentRow < index.lastRow())
					startRow = currentRow + 1;
				else
					return -1;
				break;
			case Locate.PRIOR:
				if (currentRow > 0)
					startRow = currentRow - 1;
				else
					return -1;
				break;
			case Locate.LAST:
				startRow = index.lastRow();
				if (startRow < 0)
					return -1;
				break;
			case Locate.FIRST:
				startRow = 0;
				break;
			default:
				startRow = 0;
				DataSetException.needLocateStartOption();
				break;
		}

		long retVal;

		while (true) {
			retVal = index.locate(startRow, columns,
														readRow.getLocateValues(columnList), locateOptions);
			if (retVal < 0 && (locateOptions & (Locate.FIRST | Locate.NEXT)) != 0 && hasMoreData()) {
				startRow = index.lastRow();
				locateOptions |= Locate.FAST;
				provideMoreData();
			} else
				break;
		}
		return retVal;
	}

	final void getStorageVariant(long internalRow, int ordinal, Variant value)
	/*-throws DataSetException-*/
	{
		// ! Diagnostic.println("getVariant: "+hashCode()+" "+aggManager);
		if (aggManager != null && aggManager.isCalc(ordinal))
			aggManager.getVariant(internalRow, ordinal, value);
		else
			data.getVariant(internalRow, ordinal, value);
	}

	// Read into appropriate column. Needed because reads are not thread safe. This
	// way the type is always correct. The actual value may be out of date.
	//
	final RowVariant getVariantStorage(DataSet dataSet, String columnName)
	/*-throws DataSetException-*/
	{

		int ordinal = columnList.getOrdinal(columnName);

		// ! Diagnostic.println("getVariant: "+hashCode()+" "+aggManager);
		if (aggManager != null && aggManager.isCalc(ordinal))
			aggManager.getVariant(dataSet, ordinal, dataSet.readRow.rowValues[ordinal]);
		else
			data.getVariant(dataSet.internalRow, ordinal, dataSet.readRow.rowValues[ordinal]);

		return dataSet.readRow.rowValues[ordinal];
	}

	final RowVariant getVariantStorage(DataSet dataSet, int ordinal)
	/*-throws DataSetException-*/
	{

		if (aggManager != null && aggManager.isCalc(ordinal))
			aggManager.getVariant(dataSet, ordinal, dataSet.readRow.rowValues[ordinal]);
		else
			data.getVariant(dataSet.internalRow, ordinal, dataSet.readRow.rowValues[ordinal]);

		return dataSet.readRow.rowValues[ordinal];
	}

	final RowVariant getVariantStorage(ReadRow readRow, long internalRow, int ordinal, RowVariant value)
	/*-throws DataSetException-*/
	{

		if (aggManager != null && aggManager.isCalc(ordinal))
			aggManager.getVariant(readRow, ordinal, value);
		else
			data.getVariant(internalRow, ordinal, value);

		return value;
	}

	final void editRow(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			Variant[] rowValues = dataSet.getRowValues();
			Variant[] originals = dataSet.getOriginalValues();
			data.getRowData(dataSet._synchRow(), rowValues);
			for (int ordinal = 0; ordinal < rowValues.length; ++ordinal)
				originals[ordinal].setVariant(rowValues[ordinal]);
			dataSet._editRow();
		}
	}

	// DO NOT MAKE PUBLIC. Some ReadWriteRows cannot be added (ie ParameterRow).
	//
	private final long insertReadWriteRow(DataSet dataSet, ReadWriteRow dataRow)
	/*-throws DataSetException-*/
	{
		long internalRow;

		synchronized (dataMonitor) {

			if (!allowInsert)
				ValidationException.insertNotAllowed();

			if (editBlocked == 0) {
				if (calcFieldsColumns != null)
					calcFields(dataRow, true);
				if (!loadUncached) {

					if (dataSet.heapIndex)
						dataSet.index.setInsertPos(dataSet.currentRow);

					internalRow = data.insertRow(dataRow, dataRow.getRowValues(columnList), resolvable ? RowStatus.INSERTED : RowStatus.LOADED);

					if (dataSet.heapIndex)
						dataSet.index.setInsertPos(-1);
				} else
					internalRow = data.replaceLoadedRow(lastLoadedRow, dataRow, dataRow.getRowValues(columnList), resolvable ? RowStatus.INSERTED : RowStatus.LOADED);
				if (aggManager != null)
					aggManager.add(dataRow, internalRow);
			} else {
				editError();
				return -1;
			}

		}

		return internalRow;

	}

	int[] getRequiredOrdinals() {
		return data.getRequiredOrdinals();
	}

	final long storageAddRowNoNotify(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		dataSet.requiredColumnsCheck();

		return insertReadWriteRow(dataSet, dataSet);
		// DO NOT SEND NOTFICATIONS OUT HERE. Can lead to endless recursion
		// if DataSet posting a pseudo row due to edit/modified state not
		// yet cleared. DataSet sends out notifications by calling
		// StorageDataSet notify methods.
	}

	final long storageAddRow(DataSet dataSet, DataRow dataRow)
	/*-throws DataSetException-*/
	{
		long internalRow;

		requiredColumnsCheck(dataRow.rowValues);

		internalRow = insertReadWriteRow(dataSet, dataRow);
		if (indexData.dataChangeListeners != null)
			processDataChangeEvent(DataChangeEvent.ROW_ADDED, internalRow);
		return internalRow;
	}

	/**
	 * This method calls startLoading(loader, loadStatus, loadAsync, false, false), where the last two parameters (loadUncached and loadValidate) are set to
	 * false.
	 * <p>
	 * This method is used for high-speed loading of data into a StorageDataSet. It returns an array of Variant objects for all columns in a DataSet. You set the
	 * values in the array and call {@link com.borland.dx.dataset.StorageDataSet#loadRow()} or {@link com.borland.dx.dataset.StorageDataSet#loadRow(int)}. When
	 * the load operation is complete, you must call {@link com.borland.dx.dataset.StorageDataSet#endLoading()}. Only one load operation may be active at one
	 * time.
	 * <p>
	 * This method may generate a DataSetException of LOADING_NOT_STARTED.
	 *
	 * @param loader
	 *          A call back interface that requests the load operation to be canceled. Cannot be <b>null</b>.
	 * @param loadStatus
	 *          Boolean that determines the status of the load process. Can be RowStatus.INSERTED or RowStatus.LOADED. Normally RowStatus.LOADED is used to
	 *          indicate that the row was not inserted or updated.
	 * @param loadAsync
	 *          Whether the loading should be done in a separate thread. Set this parameter to <b>true</b> if the intention is to load the StorageDataSet using a
	 *          separate thread. This will cause periodic update notifications to be sent to any data aware controls and StatusListeners.
	 * @see #loadRow()
	 * @see #endLoading()
	 * @return
	 */
	public final Variant[] startLoading(LoadCancel loader, int loadStatus, boolean loadAsync)
	/*-throws DataSetException-*/
	{
		return startLoading(loader, loadStatus, loadAsync, false, false);
	}

	/**
	 * Calls startLoading(loader, loadStatus, loadAsync, loadUncached, false). The last parameter, loadValidate, is set to <b>false</b>.
	 *
	 * @param loader
	 * @param loadStatus
	 * @param loadAsync
	 * @param loadUncached
	 * @return
	 */
	public synchronized final Variant[] startLoading(LoadCancel loader, int loadStatus, boolean loadAsync, boolean loadUncached)
	/*-throws DataSetException-*/
	{
		return startLoading(loader, loadStatus, loadAsync, loadUncached, false);
	}

	/**
	 * Used for high speed loading of data into a StorageDataSet.
	 *
	 * @param loader
	 *          A call back interface that requests the load operation to be canceled. Cannot be <b>null</b>.
	 * @param loadStatus
	 *          Boolean that determines the status of the load process. Can be RowStatus.INSERTED or RowStatus.LOADED. Normally RowStatus.LOADED is used to
	 *          indicate that the row was not inserted or loaded.
	 * @param loadAsync
	 *          Whether the loading should be done in a separate thread. Set this parameter to <b>true</b> if the intention is to load the StorageDataSet using a
	 *          separate thread. This will cause periodic update notifications to be sent to any data aware controls and StatusListeners.
	 * @param loadUncached
	 *          Boolean that indicates how to load rows. <b>True</b> means to load one row at a time.
	 *          <p>
	 *          A single row of data is stored in the StorageDataSet. If loadUncached is <b>True</b>, attempts are made to navigate past the end of the DataSet
	 *          and have the following effect:
	 *          <p>
	 *          If the Provider property is set, and it returns <b>True</b> for the Provider.hasMoreData(StorageDataSet),
	 *          <p>
	 *          <ul>
	 *          <li>The current row is discarded if it has not been updated or deleted.</li>
	 *          <li>The Provider.ProvideMoreData(StorageDataSet) will be called. The Provider.ProvideMoreData(StorageDataSet) should call the
	 *          StorageDataSet.loadRow() to load one more row of data.</li>
	 *          </ul>
	 *          <p>
	 *          If loadUncached is set to <b>False</b> (the default), the row is posted to the StorageDataSet and retained until it is explicitly removed.
	 *          <p>
	 *          The intent of loadUncached is to support batch processing where each row only needs to be accessed one time. Use this with QueryProviders and
	 *          ProcedureProviders that use the Load.UNCACHED option.
	 *
	 * @param loadValidate
	 *          Boolean that indicates whether to have rows validated by calling {@link com.borland.dx.dataset.DataRow#validate()} for each row before it is
	 *          loaded.
	 * @return
	 */
	public synchronized final Variant[] startLoading(LoadCancel loader,
			int loadStatus,
			boolean loadAsync,
			boolean loadUncached,
			boolean loadValidate)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (loading)
				DataSetException.throwAlreadyLoading();
			// !Needed to add readonly for bug4643 - Chris doing a distinct query.
			// Allow unopened DataSets to be "loaded", but not general
			// row addition. Useful for queries to start loading the StorageDataSet
			// without notifying listeners that the StorageDataSet is open.
			//
			if (editBlocked != 0 && (editBlocked & (OpenBlock.MetaDataMissing | OpenBlock.ReadOnly | OpenBlock.NotOpen)) != editBlocked)
				editError();

			if (loadBlocked != 0)
				editError();

			// Problematic to call initData when two StorageDataSets have the same
			// table open in a JDataStore database and Columns with preferred ordinals
			// settings exist. Nested restructure calls made from moveColumn() made
			// for each DataSet.
			//
			if (data == null)
				initData(false);

			failIfReadOnlyStore();

			if (!recalced() && calcFieldsColumns == null && aggManager == null)
				initCalcs();

			this.loadStatus = loadStatus;
			this.loadValidate = loadValidate;
			this.loadUncached = loadUncached;
			this.lastLoadedRow = -1;
			this.lastRefreshDtmMillis = null;
			loading = true;
			this.loadAsync = loadAsync;
			loadedRows = 0;
			loadRow = new DataRow(this, null, loadValidate);
			loadValues = loadRow.getRowValues(columnList);
			this.loader = loader;
			originalInternalRow = -1L;
			hasMoreData = 0;
			return loadValues;
		}
	}

	public final DataRow getLoadRow() {
		return loadRow;
	}

	// Currently used by restructure via MatrixData.setLoadCancel() to
	// allow for canceling restructure/sort opertations.
	//
	final void setLoadCancel(LoadCancel loader) {
		this.loader = loader;
	}

	/**
	 * Stops the loading of data into the StorageDataSet.
	 * 
	 * @see #startLoading(com.borland.dx.dataset.LoadCancel, int, boolean)
	 */
	public synchronized final void endLoading()
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (loading) {
				lastRefreshDtmMillis = System.currentTimeMillis();
				loading = false;
				loadValidate = false;
				// loadUncached= false;
				loadRow = null;
				loadValues = null;
				hasMoreData = 0;
				lastLoadedRow = -1;
			}
		}
		displayRowsLoaded();
		if (loadListeners != null) {
			if (isStorageOpen())
				notifyLoad();
			else
				notifyLoad = true;
		}
	}

	final void notifyLoad() {
		loadListeners.dispatch(new LoadEvent(this));
	}

	/*
	 * Allows for faster loading of a StorageDataSet. calls loadRow(loadStatus) where loadStatus is the value of loadStatus passed to startLoading().
	 * 
	 * @see #startLoading()
	 * 
	 * @see #endLoading()
	 * 
	 */
	// ! Steves TODO. Loading does not take constraints into account.
	// ! Should have option to enable/disable?
	// !
	/**
	 * This method allows for faster loading of a StorageDataSet. It calls loadRow(loadStatus), where loadStatus is the value of loadStatus passed to
	 * startLoading().
	 *
	 * @see #startLoading(com.borland.dx.dataset.LoadCancel, int, boolean)
	 * @see #endLoading()
	 */
	public final void loadRow() /*-throws DataSetException-*/ {
		loadRow(loadStatus);
	}

	/**
	 * Allows for faster loading of a StorageDataSet. You can also use this method to load rows as updated or deleted.
	 * <p>
	 * To load a row as deleted, set the variant values returned from startLoading() and call loadRow(RowStatus.DELETED).
	 * <p>
	 * To load a row as updated, set the variant values returned from startLoading() to the original row values and call loadRow(RowStatus.ORIGINAL). Then set the
	 * variant values returned form startLoading to the updated row values and call loadRow(RowStatus.UPDATED).
	 *
	 * @param status
	 *          Status of the row, typically RowStatus.INSERTED or RowStatus.LOADED.
	 * @return
	 */
	public final long loadRow(int status)
	/*-throws DataSetException-*/
	{
		long loadedInternalRow;

		if (!loading)
			DataSetException.throwLoadingNotStarted();

		synchronized (getDataMonitor()) {

			if (loadRowListener != null) {
				loadRowListener.loadRow(status, loadRow);
			}

			if (loadValidate &&
					(status & (RowStatus.INSERTED | RowStatus.UPDATED)) != 0) {
				loadRow.validate();
			}

			if (calcFieldsColumns != null) {
				if (aggManager != null)
					aggManager.getLookupData(loadRow, loadRow);
				calcFields(loadRow, true);
			}

			if (loadUncached) {
				lastLoadedRow = internalRow = data.replaceLoadedRow(lastLoadedRow, loadRow, loadValues, RowStatus.LOADED);
				return internalRow;
			}

			int mkStatus = status;
			if ((status & (RowStatus.DELETED | RowStatus.UPDATED | RowStatus.ORIGINAL)) != 0) {
				mkStatus &= ~(RowStatus.DELETED | RowStatus.UPDATED | RowStatus.ORIGINAL);
				if (mkStatus == 0)
					mkStatus = RowStatus.LOADED;

				if ((status & RowStatus.UPDATED) != 0) {
					if (originalInternalRow == -1L)
						DataSetException.noOriginalRow();
					data.updateStoreRow(originalInternalRow, loadValues, null);
					if ((status & RowStatus.DELETED) != 0)
						data.deleteRow(originalInternalRow);
					data.setStatus(originalInternalRow, status);
					// ! internalRow = originalInternalRow;
					loadedInternalRow = originalInternalRow;
					originalInternalRow = -1L;
					return loadedInternalRow;
				}
			}

			originalInternalRow = -1L;
			if (data == null)
				DiagnosticJLimo.println("null data:");
			loadedInternalRow = data.insertRow(loadRow, loadValues, mkStatus);

			if (aggManager != null)
				aggManager.add(loadRow, loadedInternalRow);

			++loadedRows;

			if (status != mkStatus) {
				if ((status & RowStatus.DELETED) != 0)
					data.deleteRow(loadedInternalRow);
				if ((status & RowStatus.ORIGINAL) != 0)
					originalInternalRow = loadedInternalRow;
			}
		}

		if (loadAsync && (loadedRows == 25 || (loadedRows % 500) == 0)) {
			// !System.err.println(" starting display of rows");
			displayRowsLoaded();
			// ! This gives a painting thread some breathing room.
			// ! If not done, UI may refresh "very" slowly.
			// !
			{// if (loadedRows < 300) {
				try {
					Thread thread = Thread.currentThread();
					int savePriority = thread.getPriority();
					thread.setPriority(2);
					thread.setPriority(savePriority);
				} catch (Exception ex) {
					DiagnosticJLimo.printStackTrace();
				}
			}
		}
		return loadedInternalRow;
	}

	private final void displayRowsLoaded()
	/*-throws DataSetException-*/
	{
		if (statusListeners != null) {
			loadingEvent.setMessage(Res.bundle.format(ResIndex.RowsLoaded, String.valueOf(loadedRows)));
			if (statusListeners != null)
				statusListeners.dispatch(loadingEvent);
		}
		processDataChanged(1);
	}

	/**
	 * Cancels any long running load operations currently active on this DataSet. For this have an effect, the long running loading operation must be executing on
	 * a different thread than the thread that calls this method.
	 */
	public final void cancelLoading() {
		if (loader != null)
			loader.cancelLoad();
	}

	/**
	 * Cancels any long running operation currently active on this DataSet. This includes loading a query result, restructure operations, and index building
	 * operations. For this have an effect, the long running operation must be executing on a different thread than the thread that calls this method.
	 */
	public final void cancelOperation()
	/*-throws DataSetException-*/
	{
		cancelLoading();
		MatrixData dataCopy = data;
		dataCopy.cancelOperation();
	}

	/**
	 * Limits the number of rows that can initially be loaded into the DataSet when running the application or applet. Also called a governor. The setMaxRows()
	 * method has no effect on rows added after the initial loading of data, for example, from a query or import specification.
	 * <p>
	 * This property defaults to -1 which indicates that there is no limit to the number of rows that can be initially loaded into a DataSet. No message is
	 * displayed nor is any Exception thrown when the limit is reached, however, only that number of rows is included in the DataSet when it is loaded with data.
	 * With long running queries that can possibly return large result sets, it is advisable to set the maxRows property since the maximum number of rows defaults
	 * to unlimited.
	 *
	 * @see com.borland.dx.sql.dataset.Load#AS_NEEDED
	 * @param maxRows
	 */
	public final void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
		setMaxExtraRows(maxRows);
	}

	public final int getMaxRows() {
		return maxRows;
	}

	/**
	 * Limits the number of rows that can be initially loaded into a DataSet in the UI Designer. The default is to load 50 rows. If the limit is reached, only
	 * that number of rows is displayed; no message is displayed nor is any Exception thrown.
	 *
	 * @param maxDesignRows
	 */
	public final void setMaxDesignRows(int maxDesignRows) {
		this.maxDesignRows = maxDesignRows;
	}

	public final int getMaxDesignRows() {
		return maxDesignRows;
	}

	/*
	 * The reload threshold for the load as needed option. If data is requested for viewing within this number of rows from the total loaded rows, we will load
	 * more data. Default is 25.
	 */
	final int getMaxExtraRows() {
		return loadUncached ? -1 : maxExtraRows;
	}

	final void setMaxExtraRows(int maxExtraRows) {
		if (maxExtraRows <= 0)
			maxExtraRows = 10;
		this.maxExtraRows = maxExtraRows;
	}

	final void deleteRow(DataSet dataSet, long internalRow, boolean sync, boolean empty)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {

			if (!allowDelete && !empty)
				ValidationException.deleteNotAllowed();

			DiagnosticJLimo.check(this == dataSet.getStorageDataSet());

			if (dataSet.isEditingNewRow()) {
				dataSet._cancel();
				internalRow = dataSet._synchRow();
			} else {
				if (editBlocked == 0) {

					if (dataSet.getLongRowCount() < 1) {
						if (empty)
							return;
						ValidationException.noRowsToDelete(dataSet);
					}

					if (masterUpdateListeners != null && !empty)
						processMasterDeleting(dataSet);

					if (aggManager != null)
						aggManager.delete(dataSet, dataSet.getInternalRow());

					dataSet._cancel();
					// ! internalRow = dataSet._synchRow();
					if (empty)
						data.emptyRow(internalRow);
					else
						deleteDataRow(internalRow);
					// Index now updated, so update dataSets internalRow to be the row thats
					// now at the indexes current postion. Done so DataSet._fixRowPosition will
					// go to same relative row position. Another reason do do this here is
					// for performance. Synching by currentRow is more efficient than synching
					// by internalRow when you "know" for sure the internalRow is gone.
					//
					if (sync)
						dataSet._synchCurrentRow();
				} else {
					editError();
					return;
				}
			}
		}
		if (internalRow > -1 && indexData.dataChangeListeners != null && !empty)
			processDataChangeEvent(DataChangeEvent.ROW_DELETED, internalRow);
	}

	/**
	 * Empties all rows of the DataSet and resets the DataSet to contain no rows. All change state information (inserted, deleted, changed) is lost and therefore,
	 * nothing remains in the DataSet to be resolved back to the original data source. On error, this method generates a DataSetException.
	 */
	public final void empty()
	/*-throws DataSetException-*/
	{
		synchronized (getOpenMonitor(editBlocked)) {
			synchronized (this) {
				synchronized (dataMonitor) {

					// ! if (!allowDelete)
					// ! ValidationException.deleteNotAllowed();

					boolean wasOpen = closeStorage(AccessEvent.STRUCTURE_CHANGE, true);
					StorageDataSet[] dataSets = _empty();
					if (wasOpen) {
						openStorage(null, new AccessEvent(this, AccessEvent.OPEN, AccessEvent.DATA_CHANGE));
					}
					reopenOtherDataSets(dataSets);
				}
			}
		}
	}

	/*
	 * Do not make public, it does not send out notifications.
	 */
	private final StorageDataSet[] _empty()
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			// Necessary for MemoryStore, but not DataStore.
			// DataStore empties table and all related indexes.
			//
			if (fetchDataSet != null && fetchDataSet.isOpen()) {
				fetchDataSet.close();
			}
			fetchDataSet = null;
			closeData(AccessEvent.STRUCTURE_CHANGE, true);
			StorageDataSet[] dataSets = produceStore().empty(this);
			setData(null);
			dataMayExist = true;
			clearCalcFieldsState();
			return dataSets;
		}
	}

	private final void deleteDataRow(long internalRow)
	/*-throws DataSetException-*/
	{
		data.deleteRow(internalRow);
		if (internalRow == lastLoadedRow)
			lastLoadedRow = -1;
	}

	/*
	 * delete all rows in given dataSet. Do not make public, can be invoked through DataSet.deleteAllRows.
	 */
	final void deleteAllRows(DataSet dataSet, boolean empty)
	/*-throws DataSetException-*/
	{

		synchronized (dataMonitor) {

			if (!allowDelete && !empty)
				ValidationException.deleteNotAllowed();

			if (editBlocked == 0 || empty) {
				dataSet._cancel();
				dataSet.open();
				long count = dataSet.getLongRowCount();
				long internalRow;
				// Delete backwards for efficient deletes from at least dataSet's index.
				//
				while (--count >= 0) {
					if (masterUpdateListeners != null && !empty)
						processMasterDeleting(dataSet);
					internalRow = dataSet.index.internalRow(count);
					// ! if (empty)
					// ! data.emptyRow(internalRow);
					// ! else
					dataSet.deleteRow(internalRow, false, empty);
				}
			} else
				editError();
		}
		processDataChanged(-1);
	}

	final boolean isNewRow(long internalRow)
	/*-throws DataSetException-*/
	{
		return (data.getStatus(internalRow) & RowStatus.INSERTED) != 0;
	}

	// These three are used for resolving.
	//
	final void getRowData(long internalRow, Variant[] values)
	/*-throws DataSetException-*/
	{
		data.getRowData(internalRow, values);
	}

	final int getStatus(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			return data.getStatus(dataSet._synchRow());
		}
	}

	/*
	 * @since JB2.0 The ResolveOrder property is a property which specifies the default resolution order for a multi table query resolver. Inserts and updates
	 * should be performed in the order of the list, and deletes should be performed in the reverse order. The tablenames should include any necessary schema
	 * names.
	 */
	public final String[] getResolveOrder() {
		return resolveOrder;
	}

	public final void setResolveOrder(String[] resolveOrder)
	/*-throws DataSetException-*/
	{
		propSet |= PropSet.ResolveOrder;
		this.resolveOrder = resolveOrder;
		if (data != null)
			updateProperties();
	}

	/*
	 * @since JB4.0 Number of errors to log before a ResolutionException error code of RESOVE_FAILED is thrown.
	 */
	public final int getMaxResolveErrors() {
		return maxResolveErrors;
	}

	public final void setMaxResolveErrors(int maxResolveErrors) {
		this.maxResolveErrors = maxResolveErrors;
	}

	final void _setResolveOrder(String[] resolveOrder) {
		this.resolveOrder = resolveOrder;
	}

	/**
	 * Resets the StorageDataSet with the original values it was loaded with. All insert, update, and delete operations performed by the application are backed
	 * out.
	 *
	 * @param resolved
	 *          true if changes were resolved and the changed rows should now be treated as originals in a new resolution query. false if changes were rolled back
	 *          and the changed rows should still be treated as changed rows.
	 */

	public final void resetPendingStatus(boolean resolved)
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		synchronized (dataMonitor) {
			data.resetPendingStatus(resolved);
		}
	}

	/**
	 * Reset the status bits of a specific row.
	 *
	 * @param internalRow
	 *          The row where the the status bits should be reset.
	 * @param resolved
	 *          true if changes were resolved and the changed rows should now be treated as originals in a new resolution query. false if changes were rolled back
	 *          and the changed rows should still be treated as changed rows.
	 */
	public final void resetPendingStatus(long internalRow, boolean resolved)
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		data.resetPendingStatus(internalRow, resolved);
	}

	void markStatus(long internalRow, int status, boolean on)
	/*-throws DataSetException-*/
	{
		if (on)
			data.setStatus(internalRow, data.getStatus(internalRow) | status);
		else
			data.setStatus(internalRow, data.getStatus(internalRow) & ~status);
	}

	private synchronized void setEditBlock(int block, boolean on) {
		synchronized (dataMonitor) {
			if (on)
				editBlocked |= block;
			else
				editBlocked = editBlocked & ~block;
		}
	}

	/**
	 * Disallow edits to this dataset.
	 * 
	 * Note that a StorageDataSet can be internally marked readOnly in some situations. These automatic readOnly settings cannot be cleared by calling
	 * setReadOnly(false). The return value of DataSetException.getMessage() explains why a StorageDataSet is readOnly.
	 * 
	 * A common reason for a StorageDataSet to be automatically marked readOnly is that a Provider such as QueryProvider or ProcedureProvider could not determine
	 * what Columns should have their rowId property set to true. This readOnly state can be cleared by calling Column.setRowId(), StorageDataSet.setRowId(), or
	 * StorageDataSet.setAllRowIds().
	 * 
	 * Note that if a QueryProvider or ProcedureProvider cannot determine the tableName property setting, the changes in the StorageDataSet cannot be saved back.
	 * To save changes back, the StorageDataSet.setTableName property must be explicitly set. If the query was the result of a join operation and you would like
	 * to resolve the changes back, the Column.TableName property will have to be set for each column. If this was not successfully accomplished by the Provider,
	 * this property will have to be set for each Column that is to have its values saved (resolved) back.
	 * 
	 */
	public final void setReadOnly(boolean readOnly) {
		setEditBlock(OpenBlock.ReadOnly, readOnly);
	}

	public final boolean isReadOnly() {
		return (editBlocked & OpenBlock.ReadOnly) != 0;
	}

	/** Disallow saving this dataSet back to a data source. */
	public synchronized final void setResolvable(boolean resolvable)
	/*-throws DataSetException-*/
	{
		// int oldEditBlocked = preparePropertyRestructure();
		// this.resolvable = resolvable;
		// commitPropertyRestructure(oldEditBlocked);

		if (data == null || this.resolvable != resolvable) {
			this.resolvable = resolvable;
			propSet |= PropSet.Resolvable;
			if (data != null) {
				updateProperties();
				data.updateProperties(this);
			}
		}
	}

	final void _setResolvable(boolean resolvable)
	/*-throws DataSetException-*/
	{
		this.resolvable = resolvable;
	}

	public final boolean isResolvable() {
		return resolvable;
	}

	/*
	 * Used by Providers. Normally set by providers to indicate that there is insufficient metadata to post any changes back to its original source.
	 */
	// DO NOT MAKE public. See ProviderHelp.
	//
	final void setMetaDataMissing(boolean hasRowIds) {
		setEditBlock(OpenBlock.MetaDataMissing, !hasRowIds);
	}

	final synchronized void rowIdSet() {
		// This is conservative in the sense that OpenBlock.MetaDataMissing is not
		// set if all rowIds are removed.
		//
		if (columnList != null)
			setEditBlock(OpenBlock.MetaDataMissing, false);
	}

	/*
	 * Used by Providers and Resolvers. Return this counter, which is incremented everytime a structural change is made.
	 */
	// DO NOT MAKE public. See ProviderHelp.
	//
	final int getStructureAge() {
		return structureAge;
	}

	// ! /* Used by Providers.
	// ! */
	// ! // DO NOT MAKE public. See ProviderHelp.
	// ! //
	// ! /*
	// ! final boolean isMetDataMissing()
	// ! {
	// ! return (editBlocked & OpenBlock.MetaDataMissing) == 0;
	// ! }
	// ! */

	public final int getMetaDataUpdate() {
		return metaDataUpdate;
	}

	/**
	 * Determines what kind of metadata discovery is performed when executing a query or a stored procedure against a SQL server database. Valid values for this
	 * property are defined in the {@link com.borland.dx.dataset.MetaDataUpdate} interface.
	 * <p>
	 * To prevent the addition of row ID columns and various metadata related properties on DataSet and Column components, set this property to
	 * MetaDataUpdate.NONE.
	 *
	 * @param metaDataUpdate
	 */
	public synchronized final void setMetaDataUpdate(int metaDataUpdate)
	/*-throws DataSetException-*/
	{
		int oldEditBlocked = preparePropertyRestructure();
		this.metaDataUpdate = metaDataUpdate;
		commitPropertyRestructure(oldEditBlocked);
	}

	final void startEdit(DataSet dataSet, Column column)
	/*-throws DataSetException-*/
	{
		if (masterUpdateListeners != null) {
			masterUpdateEvent.setProperties(dataSet, MasterUpdateEvent.CAN_CHANGE, column);
			try {
				masterUpdateListeners.exceptionDispatch(masterUpdateEvent);
			} catch (Exception ex) {
				ValidationException.cannotOrphanDetails(ex);
			}
		}
		if (editBlocked != 0)
			editError();

		column.startEdit(true);
	}

	// ! /*
	// ! synchronized void copyRow(DataSet dataSet, int row, DataSet sourceDataSet, int rowSource) throws Exception {
	// ! int count = columnCount();
	// ! Column[] columns = columnList.columns;
	// ! StorageDataSet sourceDataSet = sourceDataSet.getDataSet();
	// ! for (int element = 0; element < count; ++element) {
	// ! sourceDataSet.getVariant(sourceDataSet, columns[element], rowSource, tempValue);
	// ! setVariant(dataSet, columns[element], tempValue);
	// ! }
	// ! }
	// ! */

	// DO NOT MAKE PUBLIC. Some ReadWriteRows cannot be used (ie ParameterRow).
	// Also, its callers responsibility to know when its safe to send notifications
	// to listeners.
	final void updateRow(DataSet dataSet, long internalRow, Variant[] originalValues, ReadWriteRow dataRow, Column[] updateColumns)
	/*-throws DataSetException-*/
	{

		if (!allowUpdate)
			ValidationException.updateNotAllowed();

		if (editBlocked != 0)
			editError();

		RowVariant[] values = dataRow.getRowValues(columnList);

		if (editListeners != null)
			processUpdating(dataSet, dataRow);

		dataRow.requiredColumnsCheckForUpdate();

		if (masterUpdateListeners != null)
			processMasterChanging(dataSet, dataRow);

		if (calcFieldsColumns != null)
			calcFields(dataRow, true);

		if (aggManager != null)
			aggManager.update(getInternalReadRow(dataSet), dataRow, internalRow);

		data.updateRow(internalRow, originalValues, values, updateColumns);

		int autoIncrementOrdinal = indexData.autoIncrementOrdinal;
		if (autoIncrementOrdinal > -1 && values[autoIncrementOrdinal].changed)
			dataSet.goToInternalRow(values[autoIncrementOrdinal].getAsLong());

		// DO NOT SEND NOTFICATIONS OUT HERE. Can lead to endless recursion
		// if DataSet posting a pseudo row due to edit/modified state not
		// yet cleared. DataSet sends out notifications by calling
		// StorageDataSet notify methods.
	}

	final void getRowData(DataSet dataSet, long row, DataRow dataRow)
	/*-throws DataSetException-*/
	{
		long internalRow = dataSet.index.internalRow(row);
		if (dataRow.columnList.hasScopedColumns()) {
			getScopedRowData(internalRow, dataRow);
		} else {
			data.getRowData(internalRow, dataRow.getRowValues(columnList));
			if (aggManager != null)
				aggManager.getRowData(internalRow, dataRow);
		}
	}

	private final void getScopedRowData(long internalRow, DataRow rowData)
	/*-throws DataSetException-*/
	{
		// ! Diagnostic.check(rowData.columnList.hasScopedColumns());
		Column[] columns = rowData.columnList.getScopedArray();
		Variant[] rowValues = rowData.getRowValues(columnList);
		int ordinal;
		for (int index = 0; index < columns.length; ++index) {
			ordinal = columns[index].ordinal;
			getStorageVariant(internalRow, ordinal, rowValues[ordinal]);
		}
	}

	public void addColumnChangeListener(ColumnChangeListener listener)
			throws TooManyListenersException, DataSetException {
		failIfOpen();

		if (listener == null)
			throw new IllegalArgumentException();

		if (changeListener != null)
			throw new TooManyListenersException();

		changeListener = listener;

		if (columnList != null)
			columnList.initHasValidations();
	}

	public synchronized void removeColumnChangeListener(ColumnChangeListener listener)
	/*-throws DataSetException-*/
	{
		failIfOpen();
		if (changeListener == listener) {
			changeListener = null;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * Add an listener for DataChange and Structure.
	 */

	final void addStorageDataChangeListener(DataSet listener)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			indexData.addDataSet(listener);
		}
	}

	final void removeStorageDataChangeListener(DataSet listener)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (indexData != null)
				indexData.removeDataSet(listener);
		}
	}

	final void processDataChangeEvent(int errorCode, long rowAffected)
	/*-throws DataSetException-*/
	{
		DataSet[] listeners = indexData.dataChangeListeners;
		int length = listeners.length;
		for (int index = 0; index < length; ++index)
			listeners[index].dataChanged(errorCode, rowAffected);
	}

	/**
	 * Add an listener for OpenAccessEvent and CloseAccessEvent.
	 */
	synchronized final void addStorageAccessListener(AccessListener listener)
	/*-throws DataSetException-*/
	{
		accessListeners = casterAdd(accessListeners, listener);
	}

	synchronized final void removeStorageAccessListener(AccessListener listener) {
		accessListeners = casterRemove(accessListeners, listener);
	}

	/**
	 * Attempts to post any unposted rows in the dataSet and and DataSetViews that share the same StorageDataSet property.
	 */

	/**
	 * Attempts to post any unposted rows in the DataSet and DataSetView components that share the same StorageDataSet property.
	 */
	public final void postAllDataSets()
	/*-throws DataSetException-*/
	{
		DataSet[] listeners = indexData.dataChangeListeners;
		if (listeners != null) {
			for (int index = 0; index < listeners.length; ++index)
				listeners[index].postDataSet();
		}
	}

	/**
	 * Add an listener for LoadingEvent.
	 */

	synchronized final void addStorageStatusListener(StatusListener listener)
	/*-throws DataSetException-*/
	{
		statusListeners = casterAdd(statusListeners, listener);
		if (loadingEvent == null)
			loadingEvent = new StatusEvent(this, StatusEvent.LOADING_DATA, null);
	}

	synchronized final void removeStorageStatusListener(StatusListener listener) {
		statusListeners = casterRemove(statusListeners, listener);
	}

	/*
	 * Must be Invoked after calling loadRow() so that all listeners (ie data aware controls, DataSets) are made aware of changes.
	 */
	final void processDataChanged(int change)
	/*-throws DataSetException-*/
	{
		if (indexData != null && indexData.dataChangeListeners != null) {
			if (change == 0)
				processDataChangeEvent(DataChangeEvent.DATA_CHANGED, -1);
			else if (change > 0)
				processDataChangeEvent(DataChangeEvent.ROW_ADDED, -1);
			else
				processDataChangeEvent(DataChangeEvent.ROW_DELETED, -1);
		}
	}

	final void processRowChangePosted(long internalRow)
	/*-throws DataSetException-*/
	{
		if (indexData.dataChangeListeners != null)
			processDataChangeEvent(DataChangeEvent.ROW_CHANGE_POSTED, internalRow);
	}

	/**
	 * Creates a new empty StorageDataSet object with the identical structure as the current StorageDataSet.
	 * 
	 * @return
	 */
	public synchronized final StorageDataSet cloneDataSetStructure() /*-throws DataSetException-*/
	{
		StorageDataSet dataSet = new TableDataSet();
		Column column;
		for (int columnIndex = 0; columnIndex < getColumnCount(); ++columnIndex) {
			column = getColumn(columnIndex);
			dataSet.addColumn(column.getColumnName(),
												column.getCaption(),
												column.getDataType());
		}
		return dataSet;
	}

	/**
	 * Initializes deleteDataSet to display only the deleted rows in the current DataSet.
	 *
	 * @param deleteDataSet
	 *          The DataSetView initialized by this method to includes only deleted rows.
	 */
	public synchronized final void getDeletedRows(DataSetView deleteDataSet)
	/*-throws DataSetException-*/
	{
		deleteDataSet.close();
		deleteDataSet.setVisibleMask(RowStatus.DELETED, 0);
		synchronized (dataMonitor) {
			data.getDeletedRows(this, deleteDataSet);
		}
		// Force columnList to be same as this. For MemoryStore this true anyway, but
		// for DataStore, it is a separate StorageDataSet.
		//
		deleteDataSet.getStorageDataSet().columnList = columnList;
		deleteDataSet.open();
		deleteDataSet.resolverStorageDataSet = this;
	}

	/**
	 * Initializes the passed in dataSet to show all inserted rows.
	 */

	/**
	 * Initializes the DataSetView to display only the inserted (new) rows in the current DataSet.
	 *
	 * @param insertDataSet
	 *          The DataSetView initialized by this method to include only inserted rows.
	 */
	public synchronized final void getInsertedRows(DataSetView insertDataSet)
	/*-throws DataSetException-*/
	{
		insertDataSet.close();
		insertDataSet.setVisibleMask(RowStatus.INSERTED, RowStatus.DELETED);
		synchronized (dataMonitor) {
			data.getInsertedRows(this, insertDataSet);
		}
		insertDataSet.open();
		insertDataSet.resolverStorageDataSet = this;
	}

	/**
	 * Initializes the passed in dataSet to show all updated rows.
	 */

	/**
	 * Initializes a DataSetView object to display only the updated rows in the current DataSet.
	 * 
	 * @param updateDataSet
	 *          The DataSetView that is initialized by this method to contain only updated rows.
	 */
	public synchronized final void getUpdatedRows(DataSetView updateDataSet)
	/*-throws DataSetException-*/
	{
		updateDataSet.close();
		updateDataSet.setVisibleMask(RowStatus.UPDATED, RowStatus.DELETED);
		synchronized (dataMonitor) {
			data.getUpdatedRows(this, updateDataSet);
		}
		// Force columnList to be same as this. For MemoryStore this true anyway, but
		// for DataStore, it is a separate StorageDataSet.
		//
		updateDataSet.getStorageDataSet().columnList = columnList;
		updateDataSet.open();
		updateDataSet.resolverStorageDataSet = this;
	}

	/**
	 * Given a dataSet with a changed record, initialize a dataRow with the original values.
	 */

	/**
	 * Given a DataSet with a changed record, this method initializes a dataRow with the original values.
	 * 
	 * @param updateDataSet
	 * @param originalRow
	 */
	public final void getOriginalRow(DataSet updateDataSet, ReadWriteRow originalRow)
	/*-throws DataSetException-*/
	{
		data.getOriginalRow(updateDataSet.index.internalRow(updateDataSet.currentRow),
												originalRow.getRowValues(columnList));
		// ! Diagnostic.println("original: "+originalRow);
	}

	final void _getOriginalVariant(long internalRow, int ordinal, Variant value)
	/*-throws DataSetException-*/
	{
		data.getOriginalVariant(internalRow, ordinal, value);
	}

	/**
	 * Read-only property that returns the number of inserted rows visible to this StorageDataSet.
	 * 
	 * @return
	 */
	public final int getInsertedRowCount()
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		synchronized (dataMonitor) {
			return (int) data.getInsertedRowCount();
		}
	}

	/**
	 * Read-only property that returns the number of deleted rows not visible to this StorageDataSet
	 * 
	 * @return The number of deleted rows not visible to this StorageDataSet
	 */
	public final int getDeletedRowCount()
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		synchronized (dataMonitor) {
			return (int) data.getDeletedRowCount();
		}
	}

	/**
	 * @since JB2.0 Number of updated rows visible with this dataSet.
	 */
	public final int getUpdatedRowCount()
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		synchronized (dataMonitor) {
			return (int) data.getUpdatedRowCount();
		}
	}

	/**
	 * @return <b>true</b> if there are any inserted, deleted, or updated rows to be resolved back to the data source. If <b>true</b>, one or more of the
	 *         following methods getUpdatedRowCount(), getDeletedCount(), or getInsertedCount() return a value greater than 0.
	 */
	public boolean changesPending()
	/*-throws DataSetException-*/
	{
		return getInsertedRowCount() > 0
				|| getUpdatedRowCount() > 0
				|| getDeletedRowCount() > 0;
	}

	/**
	 * Specifies the DataFile implementation used for file import and export operations.
	 *
	 * @see TextDataFile for an implementation of DataFile.
	 *
	 * @param dataFile
	 */
	public void setDataFile(DataFile dataFile)
	/*-throws DataSetException-*/
	{
		// failIfOpen();
		this.dataFile = dataFile;
		dataFileChanged = true;
		setProviderPropertyChanged(true);
		if (dataFile != null && dataFile.isLoadOnOpen()) {
			if (close())
				open();
		}
	}

	public final DataFile getDataFile() {
		return dataFile;
	}

	// Used when an empty detail found.
	//
	void loadDetailRows(DataSet detailDataSet, DataSet masterDataSet) /*-throws DataSetException-*/ {
		if (!detailDataSet.isDetailDataSetWithFetchAsNeeded())
			return;
		if (provider != null)
			provider.provideData(this, false);
	}

	void checkMasterLink(MasterLinkDescriptor masterLink)
	/*-throws DataSetException-*/
	{
		if (provider != null)
			provider.checkMasterLink(this, masterLink);
	}

	/**
	 * Specifies that the named Column component either uniquely identifies a row in the server table where changes made to this StorageDataSet will be saved back
	 * to, or is one of a group of Column components that uniquely identifies a row. A <b>false</b> value indicates that the specified Column is not (part of) a
	 * unique row identifier.
	 *
	 * @param columnName
	 *          The String name of the Column component.
	 * @param setting
	 *          A boolean value indicating the participation of the columnName in the row identifier (<b>true</b>) or not (<b>false</b>).
	 */
	public synchronized final void setRowId(String columnName, boolean setting)
	/*-throws DataSetException-*/
	{
		columnList.getColumn(columnName).setRowId(setting);
	}

	/**
	 * Does this dataset have any unique row identifiers.
	 */

	/**
	 * Returns whether the DataSet has any unique row identifiers.
	 * 
	 * @return If at least one exists, this method returns true.
	 */
	public synchronized final boolean hasRowIds() {
		return columnList.hasRowIds();
	}

	/**
	 * Sets all Column components as being unique row identifiers.
	 * 
	 * @param setting
	 */
	public synchronized final void setAllRowIds(boolean setting) {
		columnList.setAllRowIds(setting);
		setMetaDataMissing(setting);
	}

	synchronized final String getBestLocateColumn(int column) {
		return columnList.getBestLocateColumn(column);
	}

	/**
	 * If a Dataset has any updatable columns, the name of the table for these columns is stored as tableName.
	 */
	public synchronized void setTableName(String tableName)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {

			propSet |= PropSet.TableName;
			if (store != null && data != null && (this.tableName == null || !this.tableName.equals(tableName))) {
				this.tableName = tableName;
				updateProperties();
			} else
				this.tableName = tableName;

		}
	}

	// Used by resolver dataSets from DataStore, do not make public.
	//
	final synchronized void _setTableName(String tableName) {
		this.tableName = tableName;
	}

	// Used by resolver dataSets from DataStore, do not make public.
	//
	final synchronized void _setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	// getTableName() is in base DataSet class.
	//
	public final String getTableName() {
		return tableName;
	}

	/**
	 * @updated JB2.0 Generic name for DataSet that can be used to identify a StorageDataSet. If the Store property component supports naming of DataSets in the
	 *          Store, this can be set. If this property is null, the TableName property will be returned. MemoryStore is the default store and it does not
	 *          support naming of dataSets.
	 */
	public synchronized void setStoreName(String name)
	/*-throws DataSetException-*/
	{
		checkStore(store, name);
		// !/***********************************************************************
		// !** This was the old way:
		// !************************************************************************
		// ! if (store != null) {
		// ! dataMayExist = true;
		// ! produceStore().attach(this);
		// ! }
		// ! synchronized(dataMonitor) {
		// ! boolean exists;
		// !
		// ! if (name != null && store != null && produceStore().exists(this))
		// ! exists = true;
		// ! else
		// ! exists = false;
		// !
		// ! String oldName = this.name;
		// !
		// ! if (exists && !MatrixData.storeNameEquals(oldName, name)) {
		// ! int oldEditBlocked = preparePropertyRestructure();
		// ! dataMayExist = true;
		// ! this.name = name;
		// ! produceStore().rename(oldName, name);
		// ! commitPropertyRestructure(oldEditBlocked);
		// ! }
		// ! else if (name != null && (oldName == null || !MatrixData.storeNameEquals(oldName, name))) {
		// ! int oldEditBlocked = preparePropertyRestructure();
		// ! this.name = name;
		// ! setProviderPropertyChanged(true);
		// ! commitPropertyRestructure(oldEditBlocked);
		// ! }
		// ! else
		// ! this.name = name;
		// ! }
		// !***********************************************************************/
		synchronized (dataMonitor) {
			if (!MatrixData.identifierEquals(this.name, name)) {
				boolean wasOpen = close();
				// ! int oldEditBlocked = preparePropertyRestructure();

				// ! JOAL:
				// Need to null the store to reset the columns.
				// The columns must reset to be able to switch to another data stream
				// for a StorageDataSet that is hooked to UI components.
				//
				Store store = this.store;
				Store prop = this.propertyStore;
				this.store = null;
				this.propertyStore = null;
				dropColumns(true, false, null);
				this.store = store;
				this.propertyStore = prop;

				// If opened before and changing to a different table, forget what
				// has been set.
				//
				if ((this.propSet & PropSet.Opened) != 0)
					this.propSet = 0;

				this.name = name;
				this.schemaStoreName = null;
				setProviderPropertyChanged(true);
				// ! commitPropertyRestructure(oldEditBlocked);
				if (wasOpen)
					open();
			}
		}
	}

	/**
	 * @updated JB2.0 returns associated name property for this StorageDataset.
	 */
	public final String getStoreName() {
		// !/* if (name == null)
		// ! return tableName;
		// !*/
		return name;
	}

	/**
	 * @since JDS7.0 returns associated schema qualified name property for this StorageDataset.
	 */
	public final String getSchemaStoreName() {
		if (schemaStoreName == null)
			schemaStoreName = getSchemaQualifiedStoreName(name);
		return schemaStoreName;
	}

	private final String getSchemaQualifiedStoreName(String storeName) {
		if (storeName == null || storeName.length() < 1)
			DataSetException.invalidStoreName(storeName);
		if (store == null)
			return storeName;
		else {
			StoreInternals internals = store.getStoreInternals();
			return internals.getSchemaStoreName(storeName);
		}
	}

	final String getReadableTableName(String storeName) {
		if (store == null)
			return storeName;
		else {
			StoreInternals internals = store.getStoreInternals();
			return internals.getReadableTableName(storeName);
		}
	}

	/**
	 * If a Dataset has any updatable columns, the name of the table for these columns is stored as tableName.
	 */
	public synchronized void setSchemaName(String schemaName)
	/*-throws DataSetException-*/
	{
		this.schemaName = schemaName;
	}

	// getSchemaName() is in base DataSet class.
	//
	public final String getSchemaName() {
		return schemaName;
	}

	public synchronized void addCalcFieldsListener(CalcFieldsListener listener)
			throws TooManyListenersException, DataSetException {
		synchronized (dataMonitor) {
			if (listener == null)
				throw new IllegalArgumentException();

			if (calcFieldsListener != null)
				throw new TooManyListenersException();

			calcFieldsListener = listener;
			setNeedsRecalc(true);
		}
	}

	public synchronized void removeCalcFieldsListener(CalcFieldsListener listener)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (calcFieldsListener == listener) {
				calcFieldsListener = null;
			} else
				throw new IllegalArgumentException();
			setNeedsRecalc(true);
		}
	}

	/**
	 * Read-only property that returns the CalcAggFieldsListener of the StorageDataSet.
	 * 
	 * @return The CalcAggFieldsListener of the StorageDataSet.
	 */
	public final CalcAggFieldsListener getCalcAggFieldsListener() {
		return calcAggFieldsListener;
	}

	/**
	 * Read-only property that returns the CalcFieldsListener of the StorageDataSet
	 * 
	 * @return The CalcFieldsListener of the StorageDataSet
	 */
	public final CalcFieldsListener getCalcFieldsListener() {
		return calcFieldsListener;
	}

	public synchronized void addCalcAggFieldsListener(CalcAggFieldsListener listener)
			throws TooManyListenersException, DataSetException {
		synchronized (dataMonitor) {
			if (listener == null)
				throw new IllegalArgumentException();

			if (calcAggFieldsListener != null)
				throw new TooManyListenersException();

			calcAggFieldsListener = listener;
			setNeedsRecalc(true);
		}
	}

	public synchronized void removeCalcAggFieldsListener(CalcAggFieldsListener listener)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (calcAggFieldsListener == listener) {
				calcAggFieldsListener = null;
			} else
				throw new IllegalArgumentException();
			setNeedsRecalc(true);
		}
	}

	boolean initCalcs()
	/*-throws DataSetException-*/
	{
		boolean operationComplete;

		clearCalcFieldsState();

		if (data != null && data.canCalc()) {
			if ((calcFieldsListener != null || calcAggFieldsListener != null)) {
				String[] calcColumnNames = columnList.getCalcColumnNames(true, calcAggFieldsListener != null);
				if (calcColumnNames != null) {
					operationComplete = false;
					try {
						calcFieldsRow = new DataRow(this, calcColumnNames, false);
						if (calcFieldsListener != null) {
							calcFieldsColumns = calcFieldsRow.columnList.getScopedArray();
							calcFieldsValues = calcFieldsRow.getRowValues(columnList);
						}
						operationComplete = true;
					} finally {
						if (!operationComplete) {
							clearCalcFieldsState();
							DiagnosticJLimo.printStackTrace();
						}
					}
				}
			}
			operationComplete = false;
			try {
				aggManager = AggManager.init(this);
				operationComplete = true;
			} finally {
				if (!operationComplete)
					clearCalcFieldsState();
			}
		}

		return (calcFieldsListener != null || aggManager != null);
	}

	// Must be synchronized!!! Could have started a query before openStorage()
	// completes.
	//

	/**
	 * Forces a recalculation of any calculated columns in the StorageDataSet.
	 */
	public final synchronized void recalc()
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			if (data == null)
				failIfNotOpen();
			if (initCalcs()) {
				if (aggManager != null)
					aggManager.setLoading(true);
				boolean operationComplete = false;
				try {
					if (data.getRowCount() > 1)
						data.recalc(this, aggManager);
					operationComplete = true;
				} finally {
					if (operationComplete)
						setNeedsRecalc(false);
					else
						clearCalcFieldsState();
					if (aggManager != null)
						aggManager.setLoading(false);
				}
			}
		}
	}

	final StorageDataSet createAggDataSet(String[] groupColumnNames)
	/*-throws DataSetException-*/
	{
		StorageDataSet aggDataSet = new TableDataSet();
		aggDataSet.matrixDataType = DataConst.AGG_DATA;
		aggDataSet.setStoreName(getStoreName());
		aggDataSet.setTableName(getTableName());
		if (groupColumnNames.length < 1) {
			aggDataSet.addColumn("GrOuPnOnE", Variant.INT); // NORES
			aggDataSet.aggGroupColumnCount = 1;
		} else
			aggDataSet.aggGroupColumnCount = groupColumnNames.length;
		aggDataSet.setStore(getStore());
		return aggDataSet;
	}

	final void initFetchDataSet(DataSet masterDataSet,
			String[] masterLinkNames,
			String[] detailLinkNames)
	/*-throws DataSetException-*/
	{
		Column column;
		Column column2;
		boolean match = true;

		if (data == null)
			initData(false);

		// See if fetchDataSet is still structurally up to date.
		//
		if (fetchDataSet != null) {
			if (detailLinkNames.length != fetchDataSet.getColumnCount())
				match = false;
			else {
				for (int index = 0; index < masterLinkNames.length; ++index) {
					column = masterDataSet.getColumn(masterLinkNames[index]);
					column2 = fetchDataSet.getColumn(index);
					if (!column2.getColumnName().equals(detailLinkNames[index])) {
						match = false;
						break;
					}
					if (column.getDataType() != column2.getDataType()) {
						match = false;
						break;
					}
				}
			}
			if (!match) {
				synchronized (dataMonitor) {
					fetchDataSet.close();
					data.freeFetchIndex();
					fetchDataSet = null;
				}
			}
		}

		if (fetchDataSet == null) {
			fetchDataSet = new TableDataSet();
			fetchDataSet.setStoreName(getStoreName());
			fetchDataSet.setTableName(getTableName());
			fetchDataSet.matrixDataType = DataConst.FETCH_DATA;
			// Diagnostic.println("masterDataSet: "+masterDataSet);
			for (int index = 0; index < masterLinkNames.length; ++index) {
				column = masterDataSet.getColumn(masterLinkNames[index]);
				fetchDataSet.addColumn(detailLinkNames[index], column.getDataType());
			}
			fetchDataSet.aggGroupColumnCount = detailLinkNames.length;
			fetchDataSet.setStore(getStore());
			fetchDataSet.setSort(new SortDescriptor(detailLinkNames));
			fetchDataSet.open();
			fetchRow = new DataRow(fetchDataSet);
			DiagnosticJLimo.check(data == null || !(data instanceof com.borland.dx.memorystore.MemoryData) || fetchDataSet.getLongRowCount() == 0);

		}
	}

	final boolean detailsFetched(ReadRow row,
			String[] sourceLinkNames,
			String[] detailLinkNames)
	/*-throws DataSetException-*/
	{
		// Make sure its open.
		//
		fetchDataSet.open();
		// ! Diagnostic.println(name+" "+fetchDataSet.getRowCount()+" masterRow: "+row);
		row.copyTo(sourceLinkNames, row, detailLinkNames, fetchRow);
		if (fetchDataSet.locate(fetchRow, Locate.FIRST)) {
			// ! Diagnostic.println(" found: "+fetchRow);
			return true;
		}
		// ! Diagnostic.println(" notFound: "+fetchRow);
		return false;
	}

	// WARNING! assumes that detailsFetched() called which sets up the contents
	// of fetchRow.
	//
	final void recordDetailsFetched()
	/*-throws DataSetException-*/
	{
		fetchDataSet.open();
		fetchDataSet.addRow(fetchRow);
	}

	final InternalRow getInternalReadRow()
	/*-throws DataSetException-*/
	{
		if (data == null)
			initData(false);
		return data.getInternalReadRow(this);
	}

	final InternalRow getInternalReadRow(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		InternalRow readRow = data.getInternalReadRow(this);
		readRow.setInternalRow(dataSet._synchRow());
		return readRow;
	}

	private void clearCalcFieldsState() {
		aggManager = null;
		calcFieldsValues = null;
		calcFieldsColumns = null;
		calcFieldsRow = null;
	}

	void calcUnpostedFields(ReadWriteRow changedRow)
	/*-throws DataSetException-*/
	{
		if (calcFieldsListener != null)
			calcFields(changedRow, false);

		// Don't attempt to incorporate changes into an aggregator.
		// Its not posted so it could be confusing and it is potentially
		// problematic for aggs like min/max that optimize on internal rows.
		// Could probably be supported if it was important enough - for new pseudo
		// rows there is no internalRow, for modified pseudo rows an aggregator
		// delete/insert would be needed in case agg columns or grouping columns were
		// changed.
		//
		if (aggManager != null)
			aggManager.getRowData(changedRow, changedRow);

	}

	final void calcFields(ReadWriteRow changedRow, boolean isPosted)
	/*-throws DataSetException-*/
	{
		// Note that if calcFieldsColumns is null you should never get here. Its ok
		// to have a calcFieldsListener, and not a calcFieldsColumns. A non-null calcFieldsColumns
		// indicates that the calcs have been initialized. Since initialization and data
		// updates are synchronized on the StorageDataSet, this all works with threads.
		// ie query starts running before calcs initialized. Calc initialization occurs
		// quickly afterwards in a synchronized method (recalc()).
		//
		DiagnosticJLimo.check(calcFieldsRow != null);
		DiagnosticJLimo.check(calcFieldsListener != null);
		calcFieldsRow._clearValues();
		calcFieldsListener.calcFields(changedRow, calcFieldsRow, isPosted);

		int ordinal;
		for (int index = 0; index < calcFieldsColumns.length; ++index) {
			ordinal = calcFieldsColumns[index].ordinal;
			DiagnosticJLimo.check(getColumn(ordinal).getCalcType() != CalcType.NO_CALC);
			changedRow.setVariantNoValidate(ordinal, calcFieldsValues[ordinal]);
		}
	}

	/*
	 * Used to retrieve the MatrixData implementation for this StorageDataSet.
	 */
	// NOTE: should not have a setter of this!!! Setting done by requests
	// to the Store implementation.
	// DO NOT MAKE PUBLIC. Use static MatrixData.getMatrixData.
	//
	final MatrixData getMatrixData() {
		return data;
	}

	// Very dangerous to make this public.
	//
	final void setMatrixData(MatrixData data) {
		this.data = data;
	}

	final void processCanceling(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Canceling");
			try {
				for (int index = 0; index < editListeners.length; ++index)
					editListeners[index].canceling(dataSet);
			} catch (Exception ex) {
				ValidationException.throwApplicationError(ex);
			}
		}
		if (dataSet.isEditingNewRow() && masterUpdateListeners != null)
			processMasterDeleting(dataSet);
	}

	final void processUpdating(DataSet dataSet, ReadWriteRow row)
	/*-throws DataSetException-*/
	{

		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Posting");
			try {
				for (int index = 0; index < editListeners.length; ++index)
					editListeners[index].updating(dataSet, row, getInternalReadRow(dataSet));
			} catch (Exception ex) {
				ValidationException.throwApplicationError(ex);
			}
		}
	}

	final void processUpdated(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Posted");
			for (int index = 0; index < editListeners.length; ++index)
				editListeners[index].updated(dataSet);
		}
	}

	final void processAdding(DataSet dataSet, ReadWriteRow row)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Adding");
			try {
				for (int index = 0; index < editListeners.length; ++index)
					editListeners[index].adding(dataSet, row);
			} catch (Exception ex) {
				ValidationException.throwApplicationError(ex);
			}
		}
	}

	final void processAdded(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Added");
			for (int index = 0; index < editListeners.length; ++index)
				editListeners[index].added(dataSet);
		}
	}

	final void processDeleting(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Deleting");
			try {
				for (int index = 0; index < editListeners.length; ++index)
					editListeners[index].deleting(dataSet);
			} catch (Exception ex) {
				ValidationException.throwApplicationError(ex);
			}
		}
	}

	final void processDeleted(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Deleted");
			for (int index = 0; index < editListeners.length; ++index)
				editListeners[index].deleted(dataSet);
		}
	}

	final void processModifying(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Modifying");
			try {
				for (int index = 0; index < editListeners.length; ++index)
					editListeners[index].modifying(dataSet);
			} catch (Exception ex) {
				ValidationException.throwApplicationError(ex);
			}
		}
	}

	final void processInserting(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Inserting");
			try {
				for (int index = 0; index < editListeners.length; ++index)
					editListeners[index].inserting(dataSet);
			} catch (Exception ex) {
				ValidationException.throwApplicationError(ex);
			}
		}
	}

	final void processInserted(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "Inserted");
			for (int index = 0; index < editListeners.length; ++index)
				editListeners[index].inserted(dataSet);
		}
	}

	final ErrorResponse processEditError(DataSet dataSet, Column column, Variant value, DataSetException ex)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "EditError");
			response.abort();
			Exception saveEx = null;
			for (int index = 0; index < editListeners.length; ++index) {
				try {
					editListeners[index].editError(dataSet, column, value, ex, response);
				} catch (Exception ex2) {
					saveEx = ex2;
				}
				handleError(saveEx);
			}
			return response;
		}
		return null;
	}

	private final void handleError(Exception ex) {
		if (ex != null)
			DataSetException.throwExceptionChain(ex);
	}

	final ErrorResponse processUpdateError(DataSet dataSet, ReadWriteRow row, DataSetException ex)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "UpdateError");
			response.abort();
			Exception saveEx = null;
			for (int index = 0; index < editListeners.length; ++index) {
				try {
					editListeners[index].updateError(dataSet, row, ex, response);
				} catch (Exception ex2) {
					saveEx = ex2;
				}
				handleError(saveEx);
			}
			return response;
		}
		return null;
	}

	final ErrorResponse processAddError(DataSet dataSet, ReadWriteRow row, DataSetException ex)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "AddError");
			response.abort();
			Exception saveEx = null;
			for (int index = 0; index < editListeners.length; ++index) {
				try {
					editListeners[index].addError(dataSet, row, ex, response);
				} catch (Exception ex2) {
					saveEx = ex2;
				}
				handleError(saveEx);
			}
			return response;
		}
		return null;
	}

	final ErrorResponse processDeleteError(DataSet dataSet, DataSetException ex)
	/*-throws DataSetException-*/
	{
		if (editListeners != null) {
			DiagnosticJLimo.trace(Trace.EditEvents, "DeleteError");
			response.abort();
			Exception saveEx = null;
			for (int index = 0; index < editListeners.length; ++index) {
				try {
					editListeners[index].deleteError(dataSet, ex, response);
				} catch (Exception ex2) {
					saveEx = ex2;
				}
				handleError(saveEx);
			}
			return response;
		}
		return null;
	}

	public void addEditListener(EditListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();

		if (editListeners != null) {
			for (int index = 0; index < editListeners.length; ++index) {
				if (editListeners[index] == listener) { return; }
			}
		}

		// if (editListeners != null)
		// throw new TooManyListenersException();

		if (editListeners == null) {
			editListeners = new EditListener[] { listener };
			response = new ErrorResponse();
		} else {
			EditListener[] newListeners = new EditListener[editListeners.length + 1];
			System.arraycopy(editListeners, 0, newListeners, 0, editListeners.length);
			newListeners[editListeners.length] = listener;
			editListeners = newListeners;
		}
	}

	public synchronized void removeEditListener(EditListener listener) {

		synchronized (dataMonitor) {
			if (editListeners != null) {
				EditListener[] newListeners = new EditListener[editListeners.length - 1];
				int count = 0;
				for (int index = 0; index < editListeners.length; ++index) {
					if (editListeners[index] != listener) {
						newListeners[count] = editListeners[index];
						++count;
					}
				}

				if (count == newListeners.length) {
					if (count == 0) {
						editListeners = null;
						response = null;
					} else
						editListeners = newListeners;
					return;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	void processMasterDeleting(DataSet master)
	/*-throws DataSetException-*/
	{
		masterUpdateEvent.setProperties(master, MasterUpdateEvent.DELETING);
		try {
			masterUpdateListeners.exceptionDispatch(masterUpdateEvent);
		} catch (Exception ex) {
			ValidationException.cannotOrphanDetails(ex);
		}
	}

	void processMasterChanging(DataSet master, ReadRow newRow)
	/*-throws DataSetException-*/
	{
		if (masterUpdateListeners != null) {
			masterUpdateEvent.setProperties(master, MasterUpdateEvent.CHANGING, newRow);
			try {
				masterUpdateListeners.exceptionDispatch(masterUpdateEvent);
			} catch (Exception ex) {
				ValidationException.cannotOrphanDetails(ex);
			}
		}
	}

	// DO NOT MAKE public see ProviderHelp.
	//
	synchronized void startResolution(boolean postEdits)
	/*-throws DataSetException-*/
	{
		synchronized (dataMonitor) {
			failIfProviderIsBusy();

			if (loadBlocked != 0 || editBlocked != 0)
				editError();

			if (postEdits)
				postAllDataSets();

			loadBlocked |= OpenBlock.Resolve;
		}
	}

	// DO NOT MAKE public see ProviderHelp.
	//
	synchronized void endResolution() {
		loadBlocked &= ~OpenBlock.Resolve;
	}

	/**
	 * @return <b>true</b> if the data source supports resolving changes made to the StorageDataSet, <b>false</b> if not. Typically, file-based data sources do
	 *         not support data resolution.
	 */
	public boolean saveChangesSupported() {
		return (resolver != null);
	}

	/**
	 * @return <b>true</b> if the data source of the StorageDataSet supports refresh operations. Otherwise, this method returns <b>false</b>.
	 */
	public boolean refreshSupported() {
		return (provider != null);
	}

	/**
	 * Saves changes made to the data in the StorageDataSet back to its data source. If the resolver property is <b>null</b>, a DataSetException of
	 * CANNOT_SAVE_CHANGES is thrown.
	 * 
	 * @param dataSet
	 *          The DataSet containing the updated data.
	 */
	public void saveChanges(DataSet dataSet) /*-throws DataSetException-*/ {
		if (resolver == null) DataSetException.cannotSaveChanges(this);
		synchronized (syncRefreshResolve) {
			closeProvider(false);
			if (isOpen()) resolver.resolveData(dataSet);
		}
	}

	/**
	 * Calls the Provider to refresh data from the data source of the StorageDataSet.
	 */
	public void refresh() /*-throws DataSetException-*/ {
		if (provider == null) DataSetException.cannotRefresh(this);

		synchronized (syncRefreshResolve) {
			provider.close(this, false);
			failIfProviderIsBusy();

			if (isDetailDataSetWithFetchAsNeeded()) {
				emptyAllRows();
			} else if (!provider.isAccumulateResults()) {
				close();
				synchronized (getOpenMonitor(editBlocked)) {
					synchronized (this) {
						synchronized (dataMonitor) {
							_empty();
						}
					}
				}
			}
			// Causes problem for refresh on a detail with fetchAsNeeded, since
			// This must have an openDataSet to perform a call to emptyAllRows().
			//
			// ! boolean wasOpen = close();
			provider.provideData(this, false);
			setProviderPropertyChanged(false);
			// ! if (wasOpen)
			open();
			// Now internalRow and currentRow need to be synched. Also need to go to first row.
			//
			first();
		}
	}

	final boolean isProviding() {
		return provider != null && loading;
	}

	/**
	 * This method can be used to terminate a provider that is currently still loading rows into the StorageDataSet. If a load option like Load.AS_NEEDED,
	 * Load.ASYNCHRONOUS is being used by a QueryProvider or StoredProcedureProvider and closeProvider() is called with loadRemainingRows set to true, the
	 * remaining rows will be retrieved before the provider is closed. If loadRemainingRows is false, provide operation will be terminated and the remaining rows
	 * will not be retrieved.
	 */

	public void closeProvider(boolean loadRemainingRows) /*-throws DataSetException-*/ {
		Provider temp = provider;
		if (temp != null)
			temp.close(this, loadRemainingRows);
	}

	// !/*
	// ! synchronized void detailsFetched(long internalRow)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! synchronized(dataMonitor) {
	// ! data.setStatus(internalRow, data.getStatus(internalRow)|RowStatus.DETAILS_FETCHED);
	// ! }
	// ! }
	// !*/

	/**
	 * Used for formatting the data stored in the StorageDataSet. Locale contains country or area-specific formatting specifications such as date format
	 * (MM/DD/YY, DD/MM/YY, YY/MM/DD), currency symbol, and so on.
	 * <p>
	 * When set at the StorageDataSet level, this property is the default locale for all {@link com.borland.dx.dataset.Column} components in the StorageDataSet.
	 * If the StorageDataSet's locale property is <b>null</b>, and the store property is set to a DataStore component, the locale property of the DataStore will
	 * be used if set. If not set any level, it defaults to the locale of the Java environment.
	 *
	 * @return
	 */
	public Locale getLocale() {
		if (locale == null)
			return produceStore().getLocale();
		return locale;
	}

	public synchronized void setLocale(Locale locale)
	/*-throws DataSetException-*/
	{
		if (data == null || this.locale != locale) {
			// In case not open, make sure columns reinitialized because
			// they may depend on this setting.
			reallocateColumnList = true;
			// int oldEditBlocked = initExistingData(false) | prepareRestructure();
			this.locale = locale;
			propSet |= PropSet.Locale;
			if (data != null)
				updateProperties();
		}
		// commitPropertyRestructure(oldEditBlocked);
	}

	final void _setLocale(Locale locale)
	/*-throws DataSetException-*/
	{
		this.locale = locale;
	}

	final synchronized void changeLocale(Locale locale)
	/*-throws DataSetException-*/
	{
		int oldEditBlocked = prepareRestructure();
		setLocale(locale);
		commitPropertyRestructure(oldEditBlocked);
	}

	final boolean dropIndex(DataSet dataSet) /*-throws DataSetException-*/ {
		boolean ret;
		if (data == null)
			initData(false);

		failIfReadOnlyStore();

		synchronized (getOpenMonitor(editBlocked)) {
			synchronized (dataMonitor) {
				boolean wasOpen = closeStorage(AccessEvent.STRUCTURE_CHANGE);

				if (dataSet == null) {
					data.dropAllIndexes();
					ret = true;
				} else {
					ret = data.dropIndex(	dataSet.getSort(),
																dataSet.getStorageDataSet().getLocale(),
																dataSet.getRowFilterListener(),
																dataSet.visibleMask,
																dataSet.invisibleMask);
				}

				if (wasOpen)
					openStorage(null, new AccessEvent(this, AccessEvent.OPEN, AccessEvent.DATA_CHANGE));
			}
		}
		return ret;
	}

	/**
	 * @deprecated Use dropAllIndexes.
	 */

	public final void freeAllIndexes()
	/*-throws DataSetException-*/
	{
		dropAllIndexes();
	}

	/**
	 * @since JB2.0. Drops all indexes used by this StorageDataSet. For MemoryStore, this can be used to save memory after viewing in many different sort orders.
	 *        For DataStore this will free up maintained secondary indexes, releasing disk storage. In MemoryStore and DataStore, this can improve update
	 *        performance.
	 */
	public final void dropAllIndexes()
	/*-throws DataSetException-*/
	{
		dropIndex(null);
	}

	/**
	 * returns true if a maintained index already exists for the given descriptor and listener.
	 * 
	 * @param descriptor
	 *          Sort settings that describe the index
	 * @param listener
	 *          RowFilterListener used for filtering. Pass null if not a filtered index.
	 */

	/**
	 *
	 * @param descriptor
	 *          Sort settings that describe the index.
	 * @param listener
	 *          RowFilterListener used for filtering. Pass null if not a filtered index.
	 * @return Returns true if a maintained index already exists for the given descriptor and listener.
	 */
	public final boolean indexExists(SortDescriptor descriptor, RowFilterListener listener)
	/*-throws DataSetException-*/
	{
		// failIfNotOpen();
		synchronized (dataMonitor) {
			return data.indexExists(descriptor, listener);
		}
	}

	/**
	 * dropIndex can be used to drop unneeded indexes used to maintain a sorted and filtered view of a DataSet. droping and index can save space and improve
	 * performance of insert/add/delete operations.
	 * 
	 * @param descriptor
	 *          Sort settings that describe the index
	 * @param listener
	 *          RowFilterListener used for filtering. Pass null if not a filtered index.
	 */

	/**
	 * This method can be used to drop unneeded indexes used to maintain a sorted and filtered view of a DataSet. Dropping an index can save space and improve the
	 * performance of insert/add/delete operations.
	 *
	 * @param descriptor
	 *          Sort settings that describe the index.
	 * @param listener
	 *          RowFilterListener used for filtering. Pass null if not a filtered index.
	 * @return
	 */
	public final boolean dropIndex(SortDescriptor descriptor, RowFilterListener listener)
	/*-throws DataSetException-*/
	{
		synchronized (getOpenMonitor(editBlocked)) {
			if (data == null)
				initData(false);
			synchronized (dataMonitor) {
				boolean ret;
				boolean wasOpen = closeStorage(AccessEvent.STRUCTURE_CHANGE);
				ret = data.dropIndex(	descriptor,
															descriptor.getLocale(),
															listener,
															RowStatus.DEFAULT,
															RowStatus.DEFAULT_HIDDEN);
				if (wasOpen)
					openStorage(null, new AccessEvent(this, AccessEvent.OPEN, AccessEvent.DATA_CHANGE));
				return ret;
			}
		}
	}

	/*
	 * Used by providers. The PropertyChanged property reflects whether this StorageDataSet has received some property change which could affect the column
	 * structure or set of row data (such as change in queryDescriptor or textDataFile).
	 */
	// DO NOT MAKE public. See ProviderHelp.
	//
	final boolean isProviderPropertyChanged() {
		// ! Diagnostic.println(propertiesChanged+" isProviderPropertyChanged: "+name);
		return propertiesChanged;
	}

	/*
	 * Used by providers.
	 */
	// DO NOT MAKE public. See ProviderHelp.
	//
	final void setProviderPropertyChanged(boolean propertiesChanged) {
		// ! Diagnostic.println(propertiesChanged+" setProviderPropertyChanged: "+name);
		this.propertiesChanged = propertiesChanged;
	}

	void failIfProviderIsBusy() /*-throws DataSetException-*/ {
		if (provider != null)
			provider.checkIfBusy(this);
	}

	private void writePersistentColumns(ObjectOutputStream stream) throws IOException {
		short persistColumns = 0;
		int count = columnList.count;
		for (int ordinal = 0; ordinal < count; ++ordinal) {
			if (columnList.cols[ordinal].isPersist())
				persistColumns++;
		}
		stream.writeShort(persistColumns);
		for (int ordinal = 0; ordinal < count; ++ordinal) {
			if (columnList.cols[ordinal].isPersist())
				stream.writeObject(columnList.cols[ordinal]);
		}
	}

	private void readPersistentColumns(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		short persistColumns = stream.readShort();
		if (persistColumns <= 0)
			return;

		Column[] columns = new Column[persistColumns];
		for (int i = 0; i < persistColumns; i++) {
			columns[i] = (Column) stream.readObject();
		}
		try {
			setColumns(columns);
		} catch (DataSetException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		writePersistentColumns(stream);
	}

	private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		editBlocked = (editBlocked & OpenBlock.ReadOnly) | OpenBlock.NotOpen; // Make sure to use only the readOnly setting !
		dataSetStore = this;
		dataMonitor = this;
		readPersistentColumns(stream);
	}

	boolean hasMoreData() /*-throws DataSetException-*/ {
		if (hasMoreData == 0) {
			if (provider == null || !provider.hasMoreData(this))
				hasMoreData = 1;
			else
				hasMoreData = 2;
		}
		return (hasMoreData >= 2);
	}

	/**
	 * If a Provider, such as QueryProvider or ProcedureProvider, has a load property setting of Load.AS_NEEDED, calling this method retrieves the next batch of
	 * rows.
	 * 
	 * @return
	 */
	public boolean provideMoreData() /*-throws DataSetException-*/ {
		if (!hasMoreData())
			return false;
		provider.provideMoreData(this);
		processDataChanged(1);
		return true;
	}

	// !/*
	// ! public void enableDataSetEventsInViews(boolean enable)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! DataSet[] listeners = indexData.dataChangeListeners;
	// ! if (listeners != null) {
	// ! for (int i=0; i < listeners.length; ++i) {
	// ! listeners[i].enableDataSetEvents(enable);
	// ! }
	// ! }
	// ! }
	// !*/

	private long getLastInternalRow() /*-throws DataSetException-*/ {
		if (index == null)
			return -1;
		long lastRow = index.lastRow();
		if (lastRow < 0)
			return -1;
		return index.internalRow(lastRow);
	}

	// ! boolean isReplacingLoadRows() {
	// ! return loadUncached;
	// ! }

	// ! /*
	// ! This is a simple error logging mechanism that is intended to be useful
	// ! for 2 and n tier applications. The intent is to keep the error handling
	// ! identical for both types of applications.
	// ! */
	// ! public void logError(DataSet dataSet, Exception ex)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! getErrorDataSet();
	// ! errorDataSet.insertRow(false);
	// ! errorDataSet.setLong(0,dataSet.getInternalRow());
	// ! errorDataSet.setObject(1,ex);
	// ! errorDataSet.post();
	// ! }
	// !
	// !
	// ! public TableDataSet getErrorDataSet()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! if (errorDataSet == null) {
	// ! errorDataSet = new TableDataSet();
	// ! if (getStoreName() != null) {
	// ! errorDataSet.setStore(getStore());
	// ! errorDataSet.setStoreName(getStoreName()+".errors");
	// ! }
	// ! errorDataSet.addColumn("INTERNAL_ROW", Variant.LONG);
	// ! errorDataSet.addColumn("EXCEPTION", Variant.OBJECT);
	// ! }
	// ! errorDataSet.open();
	// ! return errorDataSet;
	// ! }
	// !
	// ! public TableDataSet setErrorDataSet()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! }
	// !
	// ! public final void setRecordErrors(boolean recordErrors)
	// !

	// ! /** @since JB2.0.
	// ! returns number of Columns that are CalcType.AGGREGATE
	// !
	// ! public final int getAggColumnCount()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! if (columnList == null)
	// ! failIfNotOpen();
	// ! return columnList.countCalcColumns(false, true);
	// ! }
	// !
	// !/**
	// ! @since JB2.0.
	// ! returns number of Columns that are of CalcType.CALC or CalcType.AGGREGATE
	// ! public final int getCalcColumnCount()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! if (columnList == null)
	// ! failIfNotOpen();
	// ! return columnList.countCalcColumns(true, true);
	// ! }
	// !*/
	// !
	// ! Deprecated methods:
	// !
	// !//!/**
	// !//! * @deprecated This property is discontinued.
	// ! public final boolean isUpdatable() {
	// ! return false;
	// ! }
	// !*/

	/**
	 * When a unique sort property is applied for the first time, rows that violate the unique constraint will be copied off to a separate duplicates DataSet. The
	 * duplicates DataSet can be retrieved by calling getDuplicates().
	 * 
	 * @see #deleteDuplicates()
	 */
	public final StorageDataSet getDuplicates()
	/*-throws DataSetException-*/
	{
		return produceStore().getDuplicates(this);
	}

	/**
	 * When a unique sort property is applied for the first time, rows that violate the unique constraint will be copied off to a separate duplicates DataSet. The
	 * duplicates DataSet can be retrieved by calling getDuplicates(). Note that opening a DataSet with a new sort property setting that has a unique constraint
	 * will not succeed if a duplicates StorageDataSet already exists. This restriction protects an application from accidentally loosing valuable data in
	 * duplicate rows.
	 * 
	 * Call deleteDuplicates() to delete the old set of duplicates.
	 * 
	 * @see #getDuplicates()
	 */
	public final void deleteDuplicates()
	/*-throws DataSetException-*/
	{
		produceStore().deleteDuplicates(this);
	}

	/**
	 * Resets the StorageDataSet with the original values it was loaded with. All insert, update, and delete operations performed by the application are backed
	 * out.
	 */
	public final void reset()
	/*-throws DataSetException-*/
	{
		failIfNotOpen();
		synchronized (dataMonitor) {
			boolean operationComplete = false;
			DataSetView view = new DataSetView();
			DataSetView dataView = new DataSetView();

			try {
				getInsertedRows(view);
				dataView.setStorageDataSet(this);
				dataView.open();

				view.first();
				while (view.inBounds()) {
					dataView.goToInternalRow(view.getInternalRow());
					dataView.deleteRow();
					// ! bug 142719
					// ! view.next();
				}

				view.close();
				getUpdatedRows(view);

				DataRow row = new DataRow(this);
				view.first();
				while (view.inBounds()) {
					getOriginalRow(view, row);
					dataView.goToInternalRow(view.getInternalRow());
					dataView.updateRow(row);
					ProviderHelp.markPendingStatus(view, true);
					view.next();
				}
				view.close();

				getDeletedRows(view);
				view.first();
				if (view.getLongRowCount() > 0) {

					view.first();
					Variant[] values = startLoading(null, RowStatus.LOADED, false);
					try {
						while (view.inBounds()) {
							for (int ordinal = 0; ordinal < values.length; ++ordinal)
								view.getVariant(ordinal, values[ordinal]);
							loadRow();
							ProviderHelp.markPendingStatus(view, true);
							view.next();
						}
					} finally {
						endLoading();
					}
				}
				operationComplete = true;
			} finally {
				view.close();
				dataView.resetPendingStatus(operationComplete);
			}
		}
	}

	private final SortDescriptor hasUniqeIndex(String name) {
		DirectIndex[] indexes = data.getIndices();
		SortDescriptor sort;
		if (indexes != null) {
			for (int index = 0; index < indexes.length; ++index) {
				sort = indexes[index].getSort();
				if (sort != null && sort.isUnique()) {
					if (sort.getIndexName() != null) {
						if (sort.getIndexName().equals(name))
							return sort;
					} else {
						DiagnosticJLimo.check(sort.isPrimary() && sort.getKeys().length == 1);
						DiagnosticJLimo.check(getColumn(sort.getKeys()[0]).isAutoIncrement());
					}
				}
			}
		}
		return null;
	}

	final void dropConstraint(String name) {
		SortDescriptor desc = hasUniqeIndex(name);
		if (desc != null) {
			if (desc.isPrimary() && foreignKeyReferenceDescs != null && foreignKeyReferenceDescs.length > 0)
				DataSetException.dependentForeignKey(name, foreignKeyReferenceDescs[0].name);
			else
				dropIndex(desc, null);
		} else if (hasForeignKeyName(name, foreignKeyDescs) != null) {
			ForeignKeyDescriptor[] newDescs = new ForeignKeyDescriptor[foreignKeyDescs.length - 1];
			int count = 0;
			for (int index = 0; index < foreignKeyDescs.length; ++index) {
				if (!foreignKeyDescs[index].name.equals(name)) {
					newDescs[count] = foreignKeyDescs[index];
					++count;
				}
			}
			setForeignKeys(newDescs);
		} else if (autoIncConstraint != null && name.equals(autoIncConstraint)) {
			synchronized (this) {
				int autoIncrementOrdinal = indexData.autoIncrementOrdinal;
				if (autoIncrementOrdinal > -1) {
					Column column = getColumn(autoIncrementOrdinal);
					column.setPrimaryKey(false);
					column.setRequired(false);
					if (!column.isClusteredOrAutoIncrement()) {
						column.setClustered(false);
					}
				}
				autoIncConstraint = null;
				updateProperties();
			}
		} else {
			DataSetException.invalidConstraintName(name);
		}
	}

	private final ForeignKeyDescriptor hasForeignKeyName(String name, ForeignKeyDescriptor[] descs) {
		if (descs != null) {
			for (int index = 0; index < descs.length; ++index) {
				if (descs[index] != null && descs[index].name != null && descs[index].name.equals(name))
					return descs[index];
			}
		}
		return null;
	}

	private final ForeignKeyDescriptor hasForeignKey(ForeignKeyDescriptor desc, ForeignKeyDescriptor[] descs) {
		if (descs != null) {
			for (int index = 0; index < descs.length; ++index) {
				if (descs[index] != null && descs[index].name != null && descs[index].name.equals(desc.name))
					if (descs[index].referencedTableName.equals(desc.referencedTableName))
						return descs[index];
			}
		}
		return null;
	}

	private void checkUniqueContraintName(String name, ForeignKeyDescriptor[] descs) {
		if (hasUniqeIndex(name) != null
				|| hasForeignKeyName(name, descs) != null
				|| autoIncConstraint != null && name.equals(autoIncConstraint))
			ValidationException.constraintNameUsed(name);
	}

	private void checkUniqueContraintName(ForeignKeyDescriptor[] newDescs, ForeignKeyDescriptor[] descs) {
		String name;
		ForeignKeyDescriptor other;
		if (descs != null) {
			for (int index = 0; index < newDescs.length; ++index) {
				name = newDescs[index].name;
				if (hasUniqeIndex(name) != null)
					ValidationException.constraintNameUsed(name);
				other = hasForeignKeyName(name, descs);
				if (other != null && other != newDescs[index]) {
					ValidationException.constraintNameUsed(name);
				}
			}
		}
	}

	private String generateConstraintName(String prefix, ForeignKeyDescriptor[] descs) {
		int number = 0;
		String name;
		do {
			++number;
			name = prefix + number;
		} while (hasUniqeIndex(name) != null || hasForeignKeyName(name, descs) != null);
		return name;
	}

	final void setConstraintName(SortDescriptor desc) {
		if (desc != null && desc.isUnique()) {
			String name = desc.getIndexName();
			if (name == null)
				desc.indexName = generateConstraintName("$UNIQUE$", foreignKeyDescs); // NORES
			else
				checkUniqueContraintName(name, foreignKeyDescs);
		}
	}

	final String[] createReferencedColumns(ForeignKeyDescriptor desc) {
		SortDescriptor sort = getPrimarySort();
		if (sort == null)
			ValidationException.foreignKeyPrimaryMissing(desc.name, getStoreName());
		return sort.getKeys();
	}

	/**
	 * Add one foreign key
	 * 
	 * @param desc
	 *          Foreign key to create
	 */
	public void addForeignKey(ForeignKeyDescriptor desc) {
		if (foreignKeyDescs == null)
			setForeignKeys(new ForeignKeyDescriptor[] { desc });
		else {
			ForeignKeyDescriptor[] descs = new ForeignKeyDescriptor[foreignKeyDescs.length + 1];
			System.arraycopy(foreignKeyDescs, 0, descs, 0, foreignKeyDescs.length);
			descs[foreignKeyDescs.length] = desc;
			setForeignKeys(descs, foreignKeyDescs.length, foreignKeyDescs.length);
		}
	}

	/**
	 * Remove one foreign key
	 * 
	 * @param desc
	 *          Foreign key to create
	 */
	public void removeForeignKey(ForeignKeyDescriptor desc) {
		if (foreignKeyDescs != null) {
			int removeIndex;
			for (removeIndex = 0; removeIndex < foreignKeyDescs.length; ++removeIndex) {
				if (foreignKeyDescs[removeIndex].name.equals(desc.name))
					break;
			}
			if (removeIndex < foreignKeyDescs.length) {
				ForeignKeyDescriptor[] newDescs = new ForeignKeyDescriptor[foreignKeyDescs.length - 1];
				int newIndex = 0;
				for (int index = 0; index < foreignKeyDescs.length; ++index) {
					if (index != removeIndex) {
						newDescs[newIndex++] = foreignKeyDescs[index];
					}
				}
				setForeignKeys(newDescs);
			}
		}
	}

	/**
	 * Create one or more foreign keys
	 * 
	 * @param descs
	 *          An array of foreign keys to create
	 */
	public void setForeignKeys(ForeignKeyDescriptor[] descs) {
		setForeignKeys(descs, 0, 0);
	}

	private void setForeignKeys(ForeignKeyDescriptor[] descs, int dropStart, int addStart) {
		ForeignKeyDescriptor desc;
		int oldEditBlocked = initExistingDataAndPrepareRestructure(false);
		synchronized (getDataMonitor()) {
			Object savepoint = getStore().getStoreInternals().setSavepoint(name);
			ForeignKeyDescriptor[] newDescs;
			checkUniqueContraintName(descs, descs);
			StorageDataSet referencedTable = null;
			try {
				if (foreignKeyDescs != null) {
					ForeignKeyDescriptor[] tempDescs = foreignKeyDescs;
					for (int index = dropStart; index < tempDescs.length; ++index) {
						desc = tempDescs[index];
						StorageDataSet table = desc.openReferenceTableData(this, getStore());
						table.removeForeignKeyReference(desc.invert(this));
						try {
							dropIndex(new SortDescriptor(desc.name + "_IDX"), null); // NORES
						} catch (Exception ex) {
							DiagnosticJLimo.printStackTrace(ex);
						}
						if (table != this)
							table.closeData(AccessEvent.UNKNOWN, true);
					}
				}
				newDescs = ForeignKeyDescriptor.clone(descs);
				for (int index = addStart; index < newDescs.length; ++index) {
					desc = newDescs[index];
					desc.referencedTableName = getSchemaQualifiedStoreName(desc.referencedTableName);
					if (desc.name != null) {
						newDescs[index] = null;
						checkUniqueContraintName(desc.name, newDescs);
						newDescs[index] = desc;
					} else {
						desc.name = generateConstraintName("$FOREIGN_KEY$", newDescs); // NORES
					}
					SortDescriptor indexDesc;
					if (!indexExists(desc.makeLocateSort(desc.referencingColumns), null)) {
						indexDesc = new SortDescriptor(desc.name + "_IDX", // NORES
								desc.referencingColumns,
								null,
								null,
								0);
						DataSetView view = new DataSetView();
						view.setStorageDataSet(this);
						view.setSort(indexDesc);
						data.openIndex(view, true);
						view.close();
					}
					referencedTable = desc.openReferenceTableData(this, getStore());
					if (referencedTable.data == null)
						ValidationException.tableMissing(desc.referencedTableName);
					String[] referencedColumns = desc.referencedColumns;
					String[] referencingColumns = desc.referencingColumns;
					if (referencedColumns == null) {
						referencedColumns = referencedTable.createReferencedColumns(desc);
						desc.referencedColumns = referencedColumns;
					}
					if (referencingColumns == null || referencedColumns.length != referencingColumns.length)
						ValidationException.foreignKeyColumnMismatch(desc.name);

					Column referencedColumn;
					Column referencingColumn;
					for (int colIndex = 0; colIndex < referencedColumns.length; ++colIndex) {
						referencedColumn = referencedTable.getColumn(referencedColumns[colIndex]);
						referencingColumn = getColumn(referencingColumns[colIndex]);
						if (referencedColumn.dataType != referencingColumn.dataType
								|| referencedColumn.getScale() != referencingColumn.getScale()
								|| referencedColumn.getPrecision() != referencedColumn.getPrecision()) {
							ValidationException.foreignKeyColumnMismatch(desc.name);
						}
					}
					if (referencedTable != this)
						referencedTable.closeData(AccessEvent.UNKNOWN, true);
				}
				if (newDescs != null) {
					for (int index = addStart; index < newDescs.length; ++index) {
						desc = newDescs[index];
						StorageDataSet table = desc.openReferenceTableData(this, getStore());
						table.addForeignKeyReference(desc.invert(this));
						if (table != this) {
							table.close();
							// Leaves table.open set to true with index and data closed.
							// table.closeData(AccessEvent.UNKNOWN, true);
							DiagnosticJLimo.check(!table.isOpen());
						}
					}
				}
				referencedTable = null;
				savepoint = null;
			} finally {
				if (savepoint != null)
					getStore().getStoreInternals().rollback(savepoint);
			}
			foreignKeyDescs = newDescs;
			DiagnosticJLimo.check(newDescs == null || newDescs.length < 2 || !newDescs[newDescs.length - 2].name.equals(newDescs[newDescs.length - 1].name));
			commitPropertyRestructure(oldEditBlocked);
		}
	}

	private final SortDescriptor getPrimarySort() {
		DirectIndex[] indexes = data.getIndices();
		SortDescriptor sort;
		if (indexes != null) {
			for (int index = 0; index < indexes.length; ++index) {
				sort = indexes[index].getSort();
				if (sort != null && sort.isPrimary())
					return sort;
			}
		}
		Column[] cols = columnList.getScopedColumns();
		int columnCount = columnList.getScopedColumnLength();
		for (int ordinal = 0; ordinal < columnCount; ++ordinal) {
			if (cols[ordinal].isClusteredOrAutoIncrement()) {
				return new SortDescriptor(null,
						new String[] { cols[ordinal].getColumnName() },
						null,
						null,
						Sort.PRIMARY);
			}
		}
		return null;
	}

	public ForeignKeyDescriptor[] getForeignKeys() {
		return foreignKeyDescs;
	}

	public ForeignKeyDescriptor[] getReferenceForeignKeys() {
		return foreignKeyReferenceDescs;
	}

	private void addForeignKeyReference(ForeignKeyDescriptor newDesc) {
		int oldEditBlocked = initExistingDataAndPrepareRestructure(false);
		// ForeignKeyDescriptor desc;
		for (int index = 0; index < newDesc.referencingColumns.length; ++index)
			getColumn(newDesc.referencingColumns[index]);
		if (foreignKeyReferenceDescs == null)
			foreignKeyReferenceDescs = new ForeignKeyDescriptor[] { newDesc };
		else {
			ForeignKeyDescriptor[] newDescs = new ForeignKeyDescriptor[foreignKeyReferenceDescs.length + 1];
			System.arraycopy(foreignKeyReferenceDescs, 0, newDescs, 0, foreignKeyReferenceDescs.length);
			newDescs[foreignKeyReferenceDescs.length] = newDesc;
			checkUniqueReference(newDescs);
			foreignKeyReferenceDescs = newDescs;
		}
		updateProperties();
		commitPropertyRestructure(oldEditBlocked);
	}

	final void checkUniqueReference(ForeignKeyDescriptor[] descs) {
		ForeignKeyDescriptor newDesc;
		if (descs != null) {
			for (int outerIndex = 0; outerIndex < descs.length; ++outerIndex) {
				newDesc = descs[outerIndex];
				for (int index = 0; index < descs.length; ++index) {
					if (outerIndex != index && newDesc.name.equals(descs[index].name)) {
						if (newDesc.referencedTableName.equals(descs[index].referencedTableName))
							ValidationException.constraintNameUsed(newDesc.name);
					}
				}
			}
		}
	}

	private void removeForeignKeyReference(ForeignKeyDescriptor removeDesc) {
		if (foreignKeyReferenceDescs != null) {
			int oldEditBlocked = initExistingDataAndPrepareRestructure(false);
			ForeignKeyDescriptor desc;
			ForeignKeyDescriptor[] newDescs = new ForeignKeyDescriptor[foreignKeyReferenceDescs.length - 1];
			int count = 0;
			for (int index = 0; index < foreignKeyReferenceDescs.length; ++index) {
				desc = foreignKeyReferenceDescs[index];
				if (!desc.name.equals(removeDesc.name) || !desc.referencedTableName.equals(removeDesc.referencedTableName)) {
					newDescs[count] = desc;
					++count;
				}
			}
			DiagnosticJLimo.check(count == foreignKeyReferenceDescs.length - 1);
			checkUniqueReference(newDescs);
			foreignKeyReferenceDescs = newDescs;
			updateProperties();
			commitPropertyRestructure(oldEditBlocked);
		}
	}

	final void updateForeignKeyColumnName(ForeignKeyDescriptor[] descs, String oldName, String name, boolean referencing, boolean recurse) {
		String[] colNames;
		ForeignKeyDescriptor desc;
		if (descs != null) {
			int oldEditBlocked = initExistingDataAndPrepareRestructure(false);
			for (int index = 0; index < descs.length; ++index) {
				desc = descs[index];
				if (referencing)
					colNames = desc.referencingColumns;
				else
					colNames = desc.referencedColumns;
				for (int colIndex = 0; colIndex < colNames.length; ++colIndex) {
					if (colNames[colIndex].equalsIgnoreCase(oldName)) {
						colNames[colIndex] = name;
						if (recurse) {
							StorageDataSet table = desc.openReferenceTableData(this, store);
							table.updateForeignKeyColumnName(oldName, name, !referencing, false);
							if (table != this)
								table.closeData(AccessEvent.UNKNOWN, true);
						}
					}
				}
			}
			commitPropertyRestructure(oldEditBlocked);
		}
	}

	final void updateForeignKeyColumnName(String oldName, String name, boolean referencing, boolean recurse) {
		if (oldName != null && !oldName.equalsIgnoreCase(name)) {
			updateForeignKeyColumnName(foreignKeyDescs, oldName, name, referencing, recurse);
			updateForeignKeyColumnName(foreignKeyReferenceDescs, oldName, name, referencing, recurse);
		}
	}

	final void updateForeignKeyStoreName(String oldName, String name) {
		if (foreignKeyReferenceDescs != null) {
			renameForeignKeyTable(oldName, name, foreignKeyReferenceDescs, false);
		}
		if (foreignKeyDescs != null) {
			renameForeignKeyTable(oldName, name, foreignKeyDescs, true);
		}
	}

	private final void renameForeignKeyTable(String oldName, String name, ForeignKeyDescriptor[] descs, boolean isRef) {
		StorageDataSet table;
		ForeignKeyDescriptor desc;
		for (int index = 0; index < descs.length; ++index) {
			desc = descs[index];
			table = desc.openReferenceTableData(this, store);
			if (table != this) {
				table.renameForeignKeyTable(oldName, name, isRef);
				table.closeData(AccessEvent.UNKNOWN, true);
			} else if (isRef) {
				// Self referencing have to be handled special.
				// Only process when isRef is true, and do both at the same time.
				// Otherwise the foreignKeyDescs and foreignKeyReferenceDescs
				// referencedTableName gets out of sync.
				//
				table.renameForeignKeyTable(oldName, name, isRef);
				table.renameForeignKeyTable(oldName, name, !isRef);
			}
		}
	}

	private final void renameForeignKeyTable(String oldName, String name, boolean isRef) {
		ForeignKeyDescriptor[] descs;
		ForeignKeyDescriptor desc;
		if (isRef)
			descs = foreignKeyReferenceDescs;
		else
			descs = foreignKeyDescs;
		// int oldEditBlocked = initExistingDataAndPrepareRestructure(false);
		if (descs != null) {
			for (int index = 0; index < descs.length; ++index) {
				desc = descs[index];
				if (desc.referencedTableName.equals(oldName)) {
					desc.referencedTableName = name;
					updateProperties();
				}
			}
		}
		// commitPropertyRestructure(oldEditBlocked);
	}
	
	public Long getLastRefreshDtmMillis() {
		return lastRefreshDtmMillis;
	}

	ForeignKeyDescriptor[] foreignKeyDescs;
	ForeignKeyDescriptor[] foreignKeyReferenceDescs;

	private int maxRows;
	private int maxDesignRows;
	private transient int maxExtraRows;
	private int metaDataUpdate;
	private String schemaName;
	private String tableName;
	private String name;
	private String schemaStoreName;
	private String[] resolveOrder;

	private transient IndexData indexData;
	private transient MatrixData data;
	private transient EventMulticaster statusListeners;
	private transient EventMulticaster accessListeners;
	private transient EventMulticaster loadListeners;

	transient EventMulticaster masterUpdateListeners;
	private transient MasterUpdateEvent masterUpdateEvent;

	private int editBlocked; // !JOAL: Save for readonly
	private transient int loadBlocked;

	private transient boolean loading;
	private transient int loadedRows;
	private transient StatusEvent loadingEvent;
	private transient LoadCancel loader;
	private transient DataRow loadRow;
	private transient RowVariant[] loadValues;
	private transient int loadStatus;
	private transient boolean loadAsync;
	private transient boolean loadValidate;
	private transient Long lastRefreshDtmMillis;

	private transient boolean dataFileChanged;
	private DataFile dataFile;

	private transient CalcFieldsListener calcFieldsListener;
	transient CalcAggFieldsListener calcAggFieldsListener;
	transient DataRow calcFieldsRow;
	private transient Column[] calcFieldsColumns;
	transient Variant[] calcFieldsValues;
	transient AggManager aggManager;

	boolean resolvable;
	private Resolver resolver;
	private Provider provider;
	private transient Store store;
	private transient Store propertyStore;
	transient EditListener[] editListeners;
	private transient LoadRowListener loadRowListener;
	private transient boolean reallocateColumnList;
	private transient ErrorResponse response;
	Locale locale;
	private transient boolean propertiesChanged;
	private transient int aggGroupColumnCount = -1;

	private int matrixDataType;
	transient StorageDataSet fetchDataSet;
	private transient DataRow fetchRow;
	private transient long originalInternalRow;
	private transient long lastLoadedRow;
	private transient boolean loadUncached;
	private transient int hasMoreData;
	private transient boolean dataMayExist;
	private transient boolean firstOpen;

	private transient int structureAge;
	transient ColumnChangeListener changeListener;

	private transient TableDataSet errorDataSet;
	private transient boolean needsRecalc;
	transient StoreClassFactory storeClassFactory;
	private int maxResolveErrors;
	transient int propSet;
	transient boolean needsPropertyUpdate;
	transient Column[] shareColumns;
	transient String autoIncConstraint;

	// 2: added maxResolveErrors.
	// 3: added foreignKeyDescriptor[] property.
	//
	private static final long serialVersionUID = 3L;
	private transient Object syncRefreshResolve = new Object();
	
}
