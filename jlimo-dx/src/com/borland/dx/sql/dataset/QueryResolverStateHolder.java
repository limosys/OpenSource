//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryResolverStateHolder.java,v 7.2 2003/06/13 16:21:28 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;

import java.sql.SQLException;
import java.util.Vector;

class QueryResolverStateHolder extends StateHolder {

	QueryResolverStateHolder(QueryResolver resolver, StorageDataSet storageDataSet) {
		this.resolver = resolver;
		this.storageDataSet = storageDataSet;
	}

	DataSet getDataSet() {
		return storageDataSet;
	}

	void close() {
		try {
			if (insertQuery != null)
				insertQuery.close();
			if (deleteQuery != null)
				deleteQuery.close();
			if (updateQuery != null)
				updateQuery.close();
		} catch (Exception ex) {
			DiagnosticJLimo.printStackTrace(ex);
		}
	}

	void insertRow(DataSet dataSet) /*-throws DataSetException-*/
	{
		DiagnosticJLimo.trace(Trace.DataSetSave, "QueryResolver inserting row " + dataSet.getLongRow());

		// If we are processing a different DataSet than when we were last invoked, reset the
		// cached columns, tablename, and dataset.
		//
		cacheDataSet();

		for (int tableNo = 0; tableNo < cachedTableCount; ++tableNo) {
			if (cachedTables[tableNo] == null)
				continue;
			try {
				DataSet workingDs = dataSet;

				int count = 0;

				insertQuery.clearRebuildStatus();

				// A new dataset will cause the query to be rebuilt.
				insertQuery.setDataSet(storageDataSet, cachedTableCount, cachedColumnCount, tableNo);

				for (int index = 0; index < values.length; ++index) {
					if (cachedTableIndex[index] != tableNo)
						continue;

					int status = 0;

					dataSet.getVariant(index, values[index]);

					if (!values[index].isNull() && cachedColumns[index].isResolvable()) {
						status |= ResolverQuery.assignedValue;
						++count;
					}
					if (status != 0)
						insertQuery.setColumnStatus(cachedColumns[index], status);
				}

				if (count < 1)
					DataSetException.noUpdatableColumns();

				DiagnosticJLimo.trace(Trace.DataSetSave, "Insert values:  " + values.length + " queryString:\n" + insertQuery.getQueryString());

				insertQuery.setParameters(cachedTables[tableNo], values);

				int rowCount = insertQuery.execute();

				if (rowCount == 0)
					DataSetException.noRowsAffected(insertQuery.getQueryString());
				else if (rowCount != 1)
					DataSetException.multipleRowsAffected(insertQuery.getQueryString());
			} catch (SQLException ex) {
				ResolutionException.insertFailed(dataSet, cachedTables[tableNo], ex);
			}
		}
	}

	void updateRow(DataSet dataSet, ReadWriteRow oldDataRow) /*-throws DataSetException-*/
	{
		DiagnosticJLimo.trace(Trace.DataSetSave, "QueryResolver updating row: " + dataSet.getLongRow());

		// If we are processing a different DataSet than when we were last invoked, reset the
		// cached columns, tablename, and dataset.
		//
		cacheDataSet();

		for (int tableNo = 0; tableNo < cachedTableCount; ++tableNo) {
			if (cachedTables[tableNo] == null)
				continue;
			try {
				int count = 0;
				boolean noChanges = true;

				updateQuery.clearRebuildStatus();

				// A new dataset will cause the query to be rebuilt.
				updateQuery.setDataSet(storageDataSet, cachedTableCount, cachedColumnCount, tableNo);

				DiagnosticJLimo.trace(Trace.DataSetSave, "QueryResolver looking for changes:");

				boolean resolvable;
				for (int index = 0; index < values.length; ++index) {
					if (cachedTableIndex[index] != tableNo) continue;

					int status = 0;

					dataSet.getVariant(index, values[index]);
					oldDataRow.getVariant(index, oldValues[index]);

					resolvable = cachedColumns[index].isResolvable();

					// ! Diagnostic.println("resolvable: "+resolvable+" "+cachedColumns[index].getColumnName());

					// If updateWhereAll or this is a rowId, set rowId status for this column.
					if (cachedColumns[index].isRowId() ||
							((resolver.getUpdateMode() == UpdateMode.ALL_COLUMNS)
									&& cachedColumns[index].isSearchable()
									&& resolvable)) {
						status |= ResolverQuery.rowId;

						if (oldValues[index].isNull())
							status |= ResolverQuery.nullRowId;

						count++;
					}
					DiagnosticJLimo.trace(Trace.DataSetSave, "Testing column: " + cachedColumns[index].getColumnName());

					if (resolvable && !values[index].equals(oldValues[index])) {
						// The old value and new don't match.
						noChanges = false;
						DiagnosticJLimo.trace(Trace.DataSetSave, "Testing column: " + cachedColumns[index].getColumnName() + " Changed!!!");

						// Set column status to indicate this is a changed column.
						status |= ResolverQuery.changedValue;

						// ! JOAL: Fix for bug130804:
						if (values[index].isNull())
							status |= ResolverQuery.wasNull;

						// If updateWhereChanged and this isn't already a rowId, add this as a rowId.
						if ((resolver.getUpdateMode() == UpdateMode.CHANGED_COLUMNS) &&
								((status & ResolverQuery.rowId) == 0) &&
								cachedColumns[index].isSearchable()) {
							status |= ResolverQuery.rowId;

							if (oldValues[index].isNull())
								status |= ResolverQuery.nullRowId;

							count++;
						}
					}

					if (status != 0)
						updateQuery.setColumnStatus(cachedColumns[index], status);
				}

				DiagnosticJLimo.trace(Trace.DataSetSave, "QueryResolver no changes, count: " + noChanges + " " + count);

				// If this is a "phantom" change row, quit now. A phantom change row means that all of the
				// values in the original and "new" row are the same (ie. no changes where made).
				if (noChanges)
					continue;

				if (count < 1)
					DataSetException.noUpdatableColumns();

				updateQuery.setParameters(cachedTables[tableNo], values, oldValues);

				DiagnosticJLimo.trace(Trace.DataSetSave, "Update query value length:  " + values.length + " queryString:\n" + updateQuery.getQueryString());

				int rowCount = updateQuery.execute();

				if (rowCount == 0) {
					noRowsAffected(oldDataRow, updateQuery.getQueryString());
				} else if (rowCount != 1)
					DataSetException.multipleRowsAffected(updateQuery.getQueryString());
			} catch (SQLException ex) {
				DiagnosticJLimo.printStackTrace(ex);
				ResolutionException.updateFailed(dataSet, cachedTables[tableNo], ex);
			}
		}
	}

	private final void noRowsAffected(ReadRow searchRow, String queryString)
	/*-throws DataSetException-*/
	{
		FastStringBuffer buffer = new FastStringBuffer();
		try {
			int count = searchRow.getColumnCount();
			int ordinal = 0;
			while (true) {
				buffer.append(searchRow.getColumn(ordinal).getColumnName());
				buffer.append('=');
				buffer.append(searchRow.format(ordinal));
				if (++ordinal >= count)
					break;
				buffer.append(':');
			}
		} catch (DataSetException ex) {
			DiagnosticJLimo.printStackTrace();
		}
		DataSetException.noRowsAffected(Res.bundle.format(ResIndex.NoRowsAffected, queryString, buffer.toString()));
	}

	void deleteRow(DataSet dataSet) /*-throws DataSetException-*/
	{
		DiagnosticJLimo.trace(Trace.DataSetSave, "QueryResolver deleting row " + dataSet.getLongRow());

		// If we are processing a different DataSet than when we were last invoked, reset the
		// cached columns, tablename, and dataset.
		//
		cacheDataSet();

		for (int tableNo = cachedTableCount - 1; tableNo >= 0; tableNo--) {
			if (cachedTables[tableNo] == null)
				continue;
			try {
				int count = 0;

				deleteQuery.clearRebuildStatus();

				// A new dataset will cause the query to be rebuilt.
				deleteQuery.setDataSet(storageDataSet, cachedTableCount, cachedColumnCount, tableNo);

				for (int index = 0; index < values.length; ++index) {
					if (cachedTableIndex[index] != tableNo)
						continue;

					int status = 0;

					dataSet.getVariant(index, values[index]);

					if (cachedColumns[index].isRowId() ||
							((resolver.getUpdateMode() == UpdateMode.ALL_COLUMNS)
									&& cachedColumns[index].isSearchable()
									&& cachedColumns[index].isResolvable())) {
						status |= ResolverQuery.rowId;

						if (values[index].isNull())
							status |= ResolverQuery.nullRowId;

						++count;
						deleteQuery.setColumnStatus(cachedColumns[index], status);
					}
				}

				if (count < 1)
					DataSetException.noUpdatableColumns();

				deleteQuery.setParameters(cachedTables[tableNo], values);

				DiagnosticJLimo.trace(Trace.DataSetSave, "delete query values length:  " + values.length + " delete query:\n" + deleteQuery.getQueryString());

				int rowCount = deleteQuery.execute();

				if (rowCount == 0)
					noRowsAffected(dataSet, deleteQuery.getQueryString());
				else if (rowCount != 1)
					DataSetException.multipleRowsAffected(deleteQuery.getQueryString());
			} catch (SQLException ex) {
				ResolutionException.deleteFailed(dataSet, cachedTables[tableNo], ex);
			}
		}
	}

	private static final int HAS_A_ROWID = 1;

	private void cacheDataSet() /*-throws DataSetException-*/ {
		// WARNING. DataStore implementation of MatrixData uses separate StorageDataSet
		// for deletes and original rows. If any other properties are cached,
		// com.borland.datastore.TableData.openResolverDataSet() must be updated.
		//
		int age = ProviderHelp.getStructureAge(storageDataSet);
		if (age != structureAge || cachedColumns == null) {
			Database db = resolver.getDatabase();
			int queryTimeout = resolver.getResolverQueryTimeout();
			Coercer coercer = JdbcProvider.initCoercer(storageDataSet);
			deleteQuery = new DeleteQuery(db, queryTimeout, coercer);
			insertQuery = new InsertQuery(db, queryTimeout, coercer);
			updateQuery = new UpdateQuery(db, queryTimeout, coercer);

			structureAge = age;
			cachedColumns = storageDataSet.getColumns();
			cachedTableIndex = new byte[cachedColumns.length];
			values = storageDataSet.allocateValues();
			oldValues = storageDataSet.allocateValues();

			if (storageDataSet.getTableName() != null) {
				char quoteCharacter = db.getIdentifierQuoteChar();
				String tableName = db.makeTableIdentifier(null, storageDataSet.getSchemaName(), storageDataSet.getTableName());
				cachedTableCount = 1;
				cachedTables = new String[1];
				cachedTables[0] = tableName;
				cachedColumnCount = new int[1];
				cachedColumnCount[0] = cachedColumns.length;
			} else {
				cachedTables = storageDataSet.getResolveOrder();
				if (cachedTables == null)
					getCachedTables();
				else
					cachedTableCount = cachedTables.length;

				char quoteCharacter = db.getIdentifierQuoteChar();
				String lookup[] = stripQuotes(quoteCharacter, cachedTables);

				int count = cachedColumns.length;
				byte tableHasRowId[] = new byte[count]; // max 1 table per column.
				for (int ordinal = 0; ordinal < count; ++ordinal) {
					Column column = cachedColumns[ordinal];
					String tableName = column.getTableName();
					String schemaName = column.getSchemaName();
					if (tableName == null || tableName.length() <= 0)
						cachedTableIndex[ordinal] = -1;
					else {
						if (schemaName != null)
							tableName = schemaName + "." + tableName;
						int index = getIndexOfTable(lookup, tableName);
						cachedTableIndex[ordinal] = (byte) index;
						if (index >= 0 && column.isRowId()) {
							tableHasRowId[index] |= HAS_A_ROWID;
						}
					}
				}
				cachedColumnCount = new int[cachedTableCount];
				for (int ordinal = 0; ordinal < count; ++ordinal) {
					int index = cachedTableIndex[ordinal];
					if (index >= 0)
						cachedColumnCount[index] += 1;
				}
				for (int i = 0; i < cachedTableCount; i++) {
					if (tableHasRowId[i] != HAS_A_ROWID) {
						if (cachedTables == storageDataSet.getResolveOrder()) {
							cachedTables = new String[cachedTables.length];
							System.arraycopy(storageDataSet.getResolveOrder(), 0, cachedTables, 0, cachedTables.length);
						}
						cachedTables[i] = null;
					}
				}
			}
		}
	}

	private String[] stripQuotes(char quoteCharacter, String cachedTables[]) {
		int count = cachedTables.length;
		String lookup[] = new String[count];
		for (int i = 0; i < count; i++) {
			String table = cachedTables[i];
			if (table == null)
				continue;
			int index = table.indexOf(quoteCharacter);
			while (index >= 0) {
				if (index == 0)
					table = table.substring(1);
				else if (index + 1 == table.length())
					table = table.substring(0, index);
				else if (index > 0)
					table = table.substring(0, index) + table.substring(index + 1);
				index = table.indexOf(quoteCharacter);
			}
			lookup[i] = table;
		}
		return lookup;
	}

	private int getIndexOfTable(String lookup[], String tableName) {
		int count = lookup.length;
		for (int i = 0; i < count; i++) {
			String table = lookup[i];
			if (tableName.equalsIgnoreCase(table))
				return i;
		}
		return -1;
	}

	private void getCachedTables() /*-throws DataSetException-*/ {
		Database db = resolver.getDatabase();
		char quoteCharacter = db.getIdentifierQuoteChar();
		Vector tables = new Vector(5, 5);
		int count = cachedColumns.length;
		for (int ordinal = 0; ordinal < count; ++ordinal) {
			Column column = cachedColumns[ordinal];
			String tableName = column.getTableName();
			String schemaName = column.getSchemaName();
			if (tableName != null && tableName.length() > 0) {
				if (quoteCharacter != '\0' && !db.isUseTableName())
					tableName = quoteCharacter + tableName + quoteCharacter;
				if (schemaName != null && schemaName.length() > 0) {
					if (quoteCharacter != '\0')
						tableName = quoteCharacter + schemaName + quoteCharacter + "." + tableName;
					else
						tableName = schemaName + "." + tableName;
				}
				if (!tables.contains(tableName))
					tables.addElement(tableName);
			}
		}
		cachedTableCount = tables.size();
		if (cachedTableCount == 0)
			DataSetException.dataSetHasNoTable();
		cachedTables = new String[cachedTableCount];
		tables.copyInto(cachedTables);
	}

	private QueryResolver resolver;
	private StorageDataSet storageDataSet;
	private int structureAge;
	private DataSet cachedDataSet;
	private Column cachedColumns[];
	private String cachedTables[];
	private byte cachedTableIndex[];
	private int cachedColumnCount[];
	private int cachedTableCount;
	private Variant values[];
	private Variant oldValues[];
	private DeleteQuery deleteQuery;
	private InsertQuery insertQuery;
	private UpdateQuery updateQuery;
}
