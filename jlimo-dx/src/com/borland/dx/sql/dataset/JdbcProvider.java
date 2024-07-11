//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/JdbcProvider.java,v 7.2 2003/03/08 02:57:12 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! JdbcProvider
//! Base class for QueryProvider and ProcedureProvider.
//! It contains code for copying data from a ResultSet into a dataset.
//!
//! **** WARNING !!! ****
//!
//! This file is related to: QueryProvider.java, and ProcedureProvider.java.
//! Be careful when changing the signature of methods in this file and the related files. Many of
//! the methods are overridden, and the functionality can easily be broken if the derived methods
//! are not changed accordingly [it may compile, but...]
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.borland.dx.cache.Caching;
import com.borland.dx.cache.CachingProvider;
import com.borland.dx.cache.CachingService;
import com.borland.dx.cache.DataLoad;
import com.borland.dx.dataset.Coercer;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.LoadCancel;
import com.borland.dx.dataset.Provider;
import com.borland.dx.dataset.ProviderHelp;
import com.borland.dx.dataset.RowStatus;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.Variant;
import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;

/*
  NOTE that this class assumes that it is called from within a critical
  section of the dataset it is providing for.  Otherwise it would not
  be able to call the efficient loadRow method of DataSet.
*/
/**
 * This class is used internally by other com.borland classes. You should never use this class directly.
 */
public abstract class JdbcProvider extends Provider implements LoadCancel, Task, ConnectionUpdateListener {

	abstract ResultSet provideResultSet() /*-throws DataSetException-*/ throws SQLException;

	abstract void closeResultSet(ResultSet resultSet) /*-throws DataSetException-*/ throws SQLException;

	abstract void providerFailed(Exception ex) /*-throws DataSetException-*/;

	private DataLoad provideCachedData(StorageDataSet dataSet, boolean toOpen) {
		CachingProvider cachingProvider = getCachingProvider();
		if (cachingProvider == null) {
			CachingService service = Caching.service();
			if (service != null) {
				cachingProvider = service.createCachingProvider(this);
				setCachingProvider(cachingProvider);
			}
		}
		if (cachingProvider != null) { return cachingProvider.provideData(dataSet, toOpen, this); }
		return DataLoad.LOAD;
	}

	public void provideData(StorageDataSet dataSet, boolean toOpen) /*-throws DataSetException-*/ {

		cacheDataSet(dataSet);
		if (toOpen && !descriptor.isExecuteOnOpen())
			return;

		DataLoad dataLoad = provideCachedData(dataSet, toOpen);
		if (dataLoad == DataLoad.LOAD_STOP) {
			return;
		}
		
		ifBusy();
		blockConnectionChanges(true);

		try {
			if (isPropertyChanged())
				resetState();
			resultSet = provideResultSet();
			settingMetadata = true;
			// !/*
			// ! if (!isAccumulating()) {
			// ! if (dataSet.isDetailDataSetWithFetchAsNeeded())
			// ! dataSet.emptyAllRows();
			// ! else
			// ! dataSet.empty();
			// ! }
			// !*/
			computeColumnInfo();
			setPropertyChanged(false);
			settingMetadata = false;
			setLoadOptions();
			if (maxLoadRows == 0)
				closePrivateResources(true);
			else {
				if (!isAsyncLoad)
					copyData(true);
				else {
					task = new TaskRunner(this);
					task.start();
				}
			}
		} catch (Exception ex) {
			settingMetadata = false;
			closePrivateResourcesIgnoreExceptions();
			// ! Diagnostic.printStackTrace(ex);
			providerFailed(ex);
		}
		
		if (dataLoad == DataLoad.LOAD_AND_CALLBACK) {
			getCachingProvider().dataLoaded(dataSet, this);
		}

	}

	public boolean hasMoreData(StorageDataSet sds) {
		return !isAsyncLoad && resultSet != null && !settingMetadata;
	}

	public void provideMoreData(StorageDataSet sds) /*-throws DataSetException-*/ {
		if (!hasMoreData(sds))
			return;
		try {
			copyData(false);
		} catch (Exception ex) {
			DiagnosticJLimo.printStackTrace(ex);
			DataSetException.providerFailed(ex);
		}
	}

	// ! public synchronized void close(StorageDataSet sds, boolean loadRemainingRows) /*-throws DataSetException-*/ {
	// ! try {
	// ! if (task != null) {
	// ! try {
	// ! int loads = loadedRows - 100;
	// ! while (task.isAlive() && loadedRows > loads) {
	// ! loads = loadedRows;
	// ! task.join(5000);
	// ! }
	// ! }
	// ! catch (InterruptedException ex) {
	// ! }
	// !
	// ! if (task.isAlive())
	// ! loadRemainingRows = false;
	// ! else
	// ! task = null;
	// ! }
	// ! if (loadRemainingRows && resultSet != null && !settingMetadata) {
	// ! maxLoadRows = -1;
	// ! isAsyncLoad = false;
	// ! copyData();
	// ! }
	// ! closePrivateResources(false);
	// ! }
	// ! catch (SQLException ex) {
	// ! DataSetException.SQLException(ex);
	// ! }
	// ! }

	public synchronized void close(StorageDataSet sds, boolean loadRemainingRows) /*-throws DataSetException-*/ {
		try {
			TaskRunner temp = task;

			// First handle the asynchronious queries:
			if (temp != null) {
				if (!loadRemainingRows)
					cancelLoad();
				temp.waitFor();
			}

			// Now load the remaining rows if wanted:
			else if (loadRemainingRows && resultSet != null && !settingMetadata) {
				maxLoadRows = -1;
				copyData(false);
			}

			// Then close the private resources:
			closePrivateResources(false);
			// resetState();
		} catch (SQLException ex) {
			DataSetException.SQLException(ex);
		}
	}

	private void setLoadOptions() {
		isAsyncLoad = false;
		loadRowByRow = false;
		int loadOption = descriptor.getLoadOption();
		if (java.beans.Beans.isDesignTime()) {
			// Set the number of rows to load
			maxLoadRows = dataSet.getMaxDesignRows();
			if (maxLoadRows != 0 && loadOption == Load.UNCACHED)
				maxLoadRows = 1;
		} else {
			// Set load status:
			if (loadOption == Load.UNCACHED)
				loadRowByRow = true;

			// Set the number of rows to load
			maxLoadRows = dataSet.getMaxRows();
			if (maxLoadRows != 0 && loadOption == Load.UNCACHED)
				maxLoadRows = 1;
			if (maxLoadRows == -1 && loadOption == Load.AS_NEEDED)
				maxLoadRows = 25;

			// Is asynchronous load:
			isAsyncLoad = (loadOption == Load.ASYNCHRONOUS && !dataSet.isDetailDataSetWithFetchAsNeeded());
		}
	}

	void cacheDataSet(StorageDataSet dataSet) /*-throws DataSetException-*/ {
		if (dataSet != null && this.dataSet != null && this.dataSet != dataSet)
			DataSetException.providerOwned();
		this.dataSet = dataSet;
	}

	public StorageDataSet fetchDataSet() {
		if (dataSet != null && dataSet.getProvider() != this)
			dataSet = null;
		return dataSet;
	}

	void setQueryDescriptor(QueryDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public void setLoadAsInserted(boolean loadAsInserted) {
		if (loadAsInserted)
			loadStatus = RowStatus.INSERTED;
		else
			loadStatus = loadStatus;
	}

	public boolean isLoadAsInserted() {
		return loadStatus == RowStatus.INSERTED;
	}

	// WARNING! overrides implementation in Provider base class.
	//
	public boolean isAccumulateResults() {
		return accumulateResults;
	}

	public void setAccumulateResults(boolean accumulate) {
		accumulateResults = accumulate;
		setPropertyChanged(true);
	}

	private boolean isAccumulating() {
		return accumulateResults;// ! || dataSet.isDetailDataSetWithFetchAsNeeded();
	}

	void setPropertyChanged(boolean changed) {
		// ! Diagnostic.println(changed+" ***JDBC propertyChanged: "+(dataSet == null?null:dataSet.getName()));
		if (dataSet != null && changed)
			ProviderHelp.setProviderPropertyChanged(dataSet, true);
		propertyChanged = changed;
	}

	boolean isPropertyChanged() {
		// ! Diagnostic.println(propertyChanged+" ***JDBC IS propertyChanged: "+(dataSet == null?null:dataSet.getName()));
		if (propertyChanged)
			return true;
		// We are not notified directly about masterDetail changes:
		return (dataSet != null && ProviderHelp.isProviderPropertyChanged(dataSet));
	}

	void resetState() /*-throws DataSetException-*/ throws SQLException {
		columnMap = null;
		metaDataColumns = null;
		closePrivateResources(true);
	}

	void closePrivateResourcesIgnoreExceptions() {
		try {
			closePrivateResources(true);
		} catch (Exception ex) {
			DiagnosticJLimo.printStackTrace(ex);
		}
	}

	void closePrivateResources(boolean force) /*-throws DataSetException-*/ throws SQLException {
		if (force || !settingMetadata) {
			try {
				try {
					// Order is important here: close ResultSet before calling endLoading,
					// because endLoading will cause a dataLoaded event to be fired: BUG13135
					if (resultSet != null)
						closeResultSet(resultSet);
				} finally {
					resultSet = null;
					if (variants != null)
						variantDataSet.endLoading();
				}
			} finally {
				binaryBytes = null;
				variants = null;
				loadVariants = null; // bug 107025
				variantDataSet = null;
				resultSet = null;
				if (providerBusy)
					blockConnectionChanges(false);
			}
		}
	}

	void adjustColumnInfo(Column[] metaDataColumns) /*-throws DataSetException-*/ {}

	private final void computeColumnInfo() /*-throws DataSetException-*/ throws SQLException {
		if (metaDataColumns == null) {
			Database database = descriptor.getDatabase();
			metaDataColumns = RuntimeMetaData.processMetaData(database, dataSet.getMetaDataUpdate(), resultSet);
			adjustColumnInfo(metaDataColumns);
		} else {
			try {
				columnMap = ProviderHelp.initData(dataSet, metaDataColumns, false, false);// , true);
				// ! columnMap = ProviderHelp.createColumnMap(dataSet, metaDataColumns, null);
				coercer = initCoercer(dataSet);
				return;
			} catch (Exception ex) {
				// Could get here because someone mucked with the columns since
				// the last query. In this case, merge columns back in and redo
				// the map.
				//
				DiagnosticJLimo.printStackTrace(ex);
			}
		}
		columnMap = ProviderHelp.initData(dataSet, metaDataColumns, true, isAccumulating());// , !isAccumulating());
		coercer = initCoercer(dataSet);
		// ! columnMap = ProviderHelp.createColumnMap(dataSet, metaDataColumns, null);
	}

	static Coercer initCoercer(StorageDataSet dataSet)
	/*-throws DataSetException-*/
	{
		Coercer coercer = null;
		int resultSetType;
		int columnCount = dataSet.getColumnCount();
		int firstOrdinal = 0;
		int lastOrdinal = 0;
		Variant[] coerceValues = null;
		Column column;
		int sqlType;
		for (int ordinal = 0; ordinal < columnCount; ++ordinal) {
			column = dataSet.getColumn(ordinal);
			sqlType = column.getSqlType();
			if (sqlType != 0) {
				resultSetType = RuntimeMetaData.sqlTypeToVariantType(sqlType);
				if (column.getDataType() != resultSetType) {
					if (coerceValues == null) {
						coerceValues = new Variant[columnCount];
						firstOrdinal = ordinal;
					}
					lastOrdinal = ordinal + 1;
					coerceValues[ordinal] = new Variant(resultSetType);
				}
			}
		}

		if (coerceValues != null)
			coercer = new Coercer(dataSet, coerceValues, firstOrdinal, lastOrdinal);

		return coercer;
	}

	public void connectionChanged(ConnectionUpdateEvent event) {}

	public void connectionClosed(ConnectionUpdateEvent event) {}

	public void canChangeConnection(ConnectionUpdateEvent event) throws Exception {
		ifBusy();
	}

	public void connectionOpening(ConnectionUpdateEvent event) {}

	public void ifBusy(StorageDataSet dataSet) /*-throws DataSetException-*/ {
		ifBusy();
	}

	void ifBusy() /*-throws DataSetException-*/ {
		if (providerBusy)
			DataSetException.queryInProcess(); // Note ProcedureProvider overrides this for better message.
	}

	synchronized void blockConnectionChanges(boolean block) /*-throws DataSetException-*/ {
		Database database = descriptor.getDatabase();

		if (block)
			database.addConnectionUpdateListener(this);
		else
			database.removeConnectionUpdateListener(this);

		providerBusy = block;

		// !JOAL TODO. add event to notify that query complete (in case it was async).
	}

	public void executeTask()
			// ! throws SQLException, DataSetException
			throws Exception {
		// ! synchronized(this) {
		// ! wait(3000);
		// ! }
		copyData(true);
	}

	private void copyData(boolean firstTime) throws SQLException, DataSetException {
		try {
			DiagnosticJLimo.trace(Trace.DataSetFetch, "Before copy result!");
			copyResult(dataSet, resultSet, columnMap);
		} catch (IOException ioEx) {
			DataSetException.throwExceptionChain(ioEx);
		} catch (SQLException sqlEx) {
			// optimized so that if Oracle ever removes there terrible limitations for
			// LONG data types, there will be no performance penalty. See QueryProvider.retryQuery()
			// comments.
			//
			if (firstTime && loadedRows == 0 && (resultSet = retryQuery()) != null)
				copyData(false);
			else
				DataSetException.throwExceptionChain(sqlEx);
		}
	}

	ResultSet retryQuery()
			throws java.sql.SQLException {
		return null;
	}

	public final void cancelLoad() {
		cancel = true;
	}

	private static boolean classMissing;

	private static final boolean isJdbcOdbcInputStream(Object stream) {
		// Class may not be available.
		//
		if (!classMissing) {
			try {
				return JdbcOdbcInputStream.isJdbcOdbcInputStream(stream);
			} catch (Throwable ex) {
				classMissing = true;
			}
		}
		return false;
	}

	final void copyResult(StorageDataSet dataSet, ResultSet result, int columnMap[])
			throws SQLException, IOException, DataSetException {
		// ! Diagnostic.println("========================= copyResult: "+dataSet.getTableName());
		if (variants == null) {
			if (!result.next()) {
				closePrivateResources(true);
				// tod: next two lines added as candidate fix
				dataSet.startLoading(this, loadStatus, isAsyncLoad, loadRowByRow);
				dataSet.endLoading();
				return;
			}
			loadVariants = dataSet.startLoading(this, loadStatus, isAsyncLoad, loadRowByRow);

			if (coercer != null)
				variants = coercer.init(loadVariants);
			else
				variants = loadVariants;

			loadColumns = dataSet.getColumns();
			variantDataSet = dataSet;
		}

		int lastMap = columnMap.length + 1;
		boolean earlyBreak = false;
		boolean copyStreams = ProviderHelp.isCopyProviderStreams(dataSet);

		// ! Variant value = new Variant();
		// ! System.gc();
		// ! System.out.println("starting...");
		// ! long start = System.currentTimeMillis();
		try {
			boolean rightTrimStrings = true;
			if (result instanceof com.borland.sql.SQLAdapter) {
				com.borland.sql.SQLAdapter fast = (com.borland.sql.SQLAdapter) result;
				// Code doesn't care if Date/Time/Timestamp/Date are single instance
				// so don't need to record if its supported.
				//
				fast.adapt(fast.SINGLE_INSTANCE_TIME, null);
				rightTrimStrings = !fast.adapt(fast.RIGHT_TRIM_STRINGS, null);
				if (fast.adapt(fast.RESETABLE_STREAM, null))
					copyStreams = false;
			}
			cancel = false;
			Variant value;
			int ordinal;
			int tempInt;
			java.io.InputStream tempStream;
			String tempString;
			BigDecimal tempBigDecimal;
			boolean tempBoolean;
			byte tempByte;
			short tempShort;
			long tempLong;
			float tempFloat;
			double tempDouble;
			java.sql.Date tempDate;
			java.sql.Time tempTime;
			java.sql.Timestamp tempTimestamp;// ! = new Timestamp(System.currentTimeMillis());
			Object tempObject;
			int pos;
			int startLen;
			loadedRows = 0;

			do {
				for (int index = 0; ++index < lastMap;) {
					ordinal = columnMap[index - 1];
					value = variants[ordinal];
					// !System.err.println(" ordinal is " + ordinal + ", index is " + index + ", type is " + variants[ordinal].getSetType());
					switch (value.getSetType()) {
						case Variant.STRING:
							tempString = result.getString(index);
							if (tempString != null) {// !result.wasNull()) {
								if (rightTrimStrings && loadColumns[ordinal].getSqlType() == java.sql.Types.CHAR) {
									if (tempString != null) {
										tempString = trimRight(tempString);
									}
								}
								value.setString(tempString);
							} else
								value.setAssignedNull();
							break;

						case Variant.INPUTSTREAM:
							tempStream = result.getBinaryStream(index);
							if (tempStream != null) {// !result.wasNull()) {

								// ! read(buf, offset, length) method not supported by bridge.
								//
								if (copyStreams || JdbcProvider.isJdbcOdbcInputStream(tempStream))
									tempStream = copyByteStream(tempStream);
								if (tempStream != null)
									value.setInputStream(tempStream);
								else
									value.setAssignedNull();
							} else
								value.setAssignedNull();
							break;

						case Variant.BIGDECIMAL:
							// !ChrisO TODO. paramatize scale.
							tempBigDecimal = result.getBigDecimal(index, loadColumns[ordinal].getScale());
							if (tempBigDecimal != null) // !result.wasNull())
								value.setBigDecimal(tempBigDecimal);
							else
								value.setAssignedNull();
							break;

						case Variant.INT:
							tempInt = result.getInt(index);
							if (tempInt == 0 && result.wasNull())
								value.setAssignedNull();
							else
								value.setInt(tempInt);
							break;

						case Variant.BOOLEAN:
							tempBoolean = result.getBoolean(index);
							if (!result.wasNull())
								value.setBoolean(tempBoolean);
							else
								value.setAssignedNull();
							break;

						case Variant.BYTE:
							tempByte = result.getByte(index);
							if (!result.wasNull())
								value.setByte(tempByte);
							else
								value.setAssignedNull();
							break;

						case Variant.SHORT:
							tempShort = result.getShort(index);
							if (!result.wasNull())
								value.setShort(tempShort);
							else
								value.setAssignedNull();
							break;

						case Variant.LONG:
							tempLong = result.getLong(index);
							if (!result.wasNull())
								value.setLong(tempLong);
							else
								value.setAssignedNull();
							break;

						case Variant.FLOAT:
							tempFloat = result.getFloat(index);
							if (!result.wasNull())
								value.setFloat(tempFloat);
							else
								value.setAssignedNull();
							break;

						case Variant.DOUBLE:
							tempDouble = result.getDouble(index);
							if (!result.wasNull())
								value.setDouble(tempDouble);
							else
								value.setAssignedNull();
							break;

						case Variant.DATE:
							tempDate = result.getDate(index);
							if (tempDate != null) // !result.wasNull())
								value.setDate(tempDate);
							else
								value.setAssignedNull();
							break;

						case Variant.TIME:
							tempTime = result.getTime(index);
							if (tempTime != null) // !result.wasNull())
								value.setTime(tempTime);
							else
								value.setAssignedNull();
							break;

						case Variant.TIMESTAMP:
							// LimoSys update
							if (loadUtcTime(value, result, index, loadColumns[ordinal])) break;

							tempTimestamp = result.getTimestamp(index);
							if (tempTimestamp != null) { // !result.wasNull()) {
								// ! Diagnostic.println("timestamp: "+tempTimestamp+" "+tempTimestamp.getTime());
								value.setTimestamp(tempTimestamp);
							} else
								value.setAssignedNull();
							break;

						case Variant.OBJECT:
							tempObject = result.getObject(index);
							if (tempObject != null) // !result.wasNull())
								value.setObject(tempObject);
							else
								value.setAssignedNull();
							break;

						default:
							DiagnosticJLimo.fail();
							break;

					}
				}

				if (coercer != null)
					coercer.coerceToColumn(loadColumns, loadVariants);

				dataSet.loadRow(loadStatus);

				if (cancel || (maxLoadRows > 0 && ++loadedRows >= maxLoadRows)) {
					earlyBreak = true;
					break;
				}
			} while (result.next());

			// ! System.out.println("countTrims: "+countTrims);
		} finally {
			// ! System.out.println("end: "+(System.currentTimeMillis()-start));
			// ! System.gc();
			// ! System.out.println("end gc: ===================================== ");
			if (cancel || !earlyBreak || descriptor.getLoadOption() == Load.ALL || descriptor.getLoadOption() == Load.ASYNCHRONOUS || !result.next()) {
				closePrivateResources(true);
			}
		}
	}

	private final synchronized InputStream copyByteStream(InputStream inStream)
			throws IOException {
		// !Very funky here. If set to 4k, cannot read more than 4k
		// ! without hanging in read for interbase visigenics driver.
		// !
		if (binaryBytes == null) {
			binaryBytes = new byte[100 * 1024];
		}

		int count = 0;
		byte[] streamBytes = null;
		byte[] newBytes;

		try {
			while ((count = inStream.read(binaryBytes)) > 0) {
				// !Diagnostic.println(count+" bytes read");
				if (streamBytes == null) {
					streamBytes = new byte[count];
					System.arraycopy(binaryBytes, 0, streamBytes, 0, count);
				} else {
					newBytes = new byte[count + streamBytes.length];
					System.arraycopy(streamBytes, 0, newBytes, 0, streamBytes.length);
					System.arraycopy(binaryBytes, 0, newBytes, streamBytes.length, count);
					streamBytes = newBytes;
				}
			}
		} catch (Exception ex) {
			DiagnosticJLimo.println("JdbcProvider: Problems with reading blob");
			DiagnosticJLimo.printStackTrace(ex);
			streamBytes = null;
			return null; // ! Bail out with a null value.
		}
		if (streamBytes == null)
			streamBytes = new byte[0];
		return new InputStreamToByteArray(streamBytes);
	}

	// ! Take a JDBC resultSet and returns a Metro DataSet (assumed
	// ! already set inside the Provider via the constructor
	// !
	final void resultSetToDataSet(Database database, StorageDataSet dataSet, ResultSet result)
	/*-throws DataSetException-*/
	{
		if (result != null && dataSet != null) {
			synchronized (dataSet) {

				// Needs to be open if persistent columns are to be merged with the ResultSet
				// columns when DataStore used as backing store.
				//
				dataSet.open();// ProviderHelp.failIfOpen(dataSet);

				try {
					int columnMap[];
					Column[] columns = RuntimeMetaData.processMetaData(database, dataSet.getMetaDataUpdate(), result);
					columnMap = ProviderHelp.initData(dataSet, columns, true, true);// , false);
					// ! columnMap = ProviderHelp.createColumnMap(dataSet, columns, null);
					copyResult(dataSet, result, columnMap);
				} catch (SQLException ex) {
					// ! Diagnostic.check(processSQLError(ex));
					DataSetException.SQLException(ex);
				} catch (IOException ex) {
					DataSetException.throwExceptionChain(ex);
				}
				/*
				 * catch (java.lang.Exception ex) { Diagnostic.printStackTrace(ex); }
				 */
			}
		}
	}

	// Do not make synchronized. Must allow for asynchronous execution.
	//
	private final boolean processSQLError(SQLException ex) {
		// A SQLException was generated. Catch it and
		// display the error information. Note that there
		// could be multiple error objects chained
		// together

		DiagnosticJLimo.println("\n*** SQLException caught ***\n");

		while (ex != null) {
			DiagnosticJLimo.println("SQLState: " + ex.getSQLState());
			DiagnosticJLimo.println("Message:  " + ex.getMessage());
			DiagnosticJLimo.println("Vendor:   " + ex.getErrorCode());
			if (ex.getNextException() == null) {
				DiagnosticJLimo.printStackTrace(ex);
			}
			ex = ex.getNextException();
			DiagnosticJLimo.println("");
		}
		return true;
	}

	static String trimRight(String source) {
		if (source == null) return null;

		int len = source.length();

		while ((len > 0) && (source.charAt(len - 1) == ' '))
			len--;

		return (len < source.length()) ? source.substring(0, len) : source;
	}

	/**
	 * If Database.isUseStatementCaching() returns true, JDBC statements can be cached. By default these statements will be closed during garbage collection. If
	 * resources are scarce, the statement can be forced closed by calling this method.
	 */
	public void closeStatement()
	/*-throws DataSetException-*/
	{
		try {
			if (dataSet != null)
				dataSet.close();
			resetState();
		} catch (SQLException ex) {
			DataSetException.throwExceptionChain(ex);
		}
	}

	private transient QueryDescriptor descriptor;
	private transient int maxLoadRows;
	private transient int loadedRows;
	private transient byte[] binaryBytes;
	private transient boolean cancel;
	private transient boolean propertyChanged;
	private transient int[] columnMap;
	private transient Column[] metaDataColumns;
	private transient boolean providerBusy;
	private transient boolean settingMetadata;
	private boolean accumulateResults;
	private transient ResultSet resultSet;
	transient StorageDataSet dataSet;
	private transient TaskRunner task;
	private transient boolean loadRowByRow;
	private transient boolean isAsyncLoad;
	private transient Variant[] variants;
	private transient Variant[] loadVariants;
	private transient Column[] loadColumns;
	private transient Coercer coercer;
	private transient StorageDataSet variantDataSet;
	private static final long serialVersionUID = 1L;
	private int loadStatus = RowStatus.LOADED;
	private transient Calendar calUTC;

	// -------------------------------- LimoSys Additions Start ----------------------------------

	private Calendar getCalUTC() {
		if (calUTC == null) calUTC = new GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
		return calUTC;
	}

	private boolean loadUtcTime(Variant value, ResultSet result, int index, Column col) {
		return JdbcProvider.loadUtcTime(value, result, index, col.getColumnName(), getCalUTC());
	}
	
	public static boolean loadUtcTime(Variant value, ResultSet result, int index, String colName, Calendar calUTC) {
		try {
			if (!colName.startsWith("UTC_")) return false;
			Timestamp tm = result.getTimestamp(index, calUTC);
			if (tm != null) {
				value.setTimestamp(tm);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	// -------------------------------- LimoSys Additions End ----------------------------------

}

class JdbcOdbcInputStream {
	static boolean isJdbcOdbcInputStream(Object stream) {
		// return stream instanceof sun.jdbc.odbc.JdbcOdbcInputStream;
		return false; // Kornel fix for java8
	}
}
