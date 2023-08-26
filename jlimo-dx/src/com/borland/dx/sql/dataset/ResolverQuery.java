//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ResolverQuery.java,v 7.0.2.1 2004/02/12 02:02:50 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! ResolverQuery
//! This is a wrapper around JDBC PreparedStatement designed to generate a
//! for resolving changes back to a database.
//! This class provides code that is used in the following derived classes:
//! DeleteQuery, InsertQuery, UpdateQuery, RefetchQuery. There is code for:
//!   1) SQL identifier generation (quotes or not)
//!   2) Setting parameter values
//!   3) Reusing a generated query
//!
//! This file is related to: DeleteQuery.java, InsertQuery.java, UpdateQuery.java, RefetchQuery.java.
//! However no methods are currently overridden. However changing the functionality for
//! one of the variants here, may change the functionality for all others as well.
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.Trace;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.FastStringBuffer;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.borland.dx.dataset.*;

class ResolverQuery {
	public ResolverQuery(Database database, int queryTimeout, Coercer coercer) {
		this.database = database;
		quoteCharacter = database.getIdentifierQuoteChar();
		useObjectForStrings = database.isUseSetObjectForStrings();
		useSpacePadding = database.isUseSpacePadding();
		reuseSaveStatements = database.isReuseSaveStatements();
		// useClearParameters = database.isUseClearParameters();
		queryTimeout = queryTimeout;
		this.coercer = coercer;
	}

	final void setDataSet(DataSet dataSet, int tables, int columnCounts[], int newTable) {
		if (this.dataSet != dataSet || this.tables != tables || !columnCounts.equals(this.columnCounts)) {
			this.dataSet = dataSet;
			this.tables = tables;
			this.columnCounts = columnCounts;
			if (tables <= 1) {
				currentTable = newTable;
				columnCount = dataSet.getColumnCount();
				columnStatus = new int[columnCount];
				columns = new Column[columnCount];
			} else {
				currentTable = newTable + 1; // Force update:
				columnStatusArray = new int[tables][];
				columnsArray = new Column[tables][];
				queryArray = new String[tables];
				preparedStatementArray = new PreparedStatement[tables];

				for (int i = 0; i < tables; ++i) {
					columnStatusArray[i] = new int[columnCounts[i]];
					columnsArray[i] = new Column[columnCounts[i]];
				}
			}
		}

		if (newTable != currentTable) {
			// Get the state variebles of the next table to handle:
			currentTable = newTable;
			columnCount = columnCounts[currentTable];
			columnStatus = columnStatusArray[currentTable];
			columns = columnsArray[currentTable];
			query = queryArray[currentTable];
			preparedStatement = preparedStatementArray[currentTable];
		}
	}

	final void clearRebuildStatus() {
		currentColumn = 0;
		rebuildQuery = false;
	}

	final void setColumnStatus(Column column, int status) {
		if (columns[currentColumn] != column) {
			rebuildQuery = true;
			columns[currentColumn] = column;
		}

		if (columnStatus[currentColumn] != status) {
			rebuildQuery = true;
			columnStatus[currentColumn] = status;
		}

		++currentColumn;
	}

	final void close() throws SQLException {
		for (int i = 0; i < tables; ++i)
			close(i);
	}

	final void close(int tableNo) throws SQLException {
		if (tables <= 1) {
			if (preparedStatement != null) {
				preparedStatement.close();
				preparedStatement = null;
			}
		} else {
			if (preparedStatementArray[tableNo] != null) {
				preparedStatementArray[tableNo].close();
				if (preparedStatement == preparedStatementArray[tableNo])
					preparedStatement = null;
				preparedStatementArray[tableNo] = null;
			}
		}
	}

	final String getQueryString() {
		return query;
	}

	final int execute() throws SQLException {
		DiagnosticJLimo.trace(Trace.DataSetSave, "Executing query: " + query);
		return preparedStatement.executeUpdate();
	}

	protected final void prepare(String query) throws SQLException, DataSetException {
		// // If we statement caching is possible, then close the current statement only,
		// // otherwise close all other open statement.
		// if (database.isUseStatementCaching() && database.getMaxStatements() > tables+2)

		close(currentTable);
		DiagnosticJLimo.trace(Trace.DataSetSave, "Preparing query: " + query);
		preparedStatement = database.createPreparedStatement(query);
		if (tables > 1) {
			preparedStatementArray[currentTable] = preparedStatement;
			queryArray[currentTable] = query;
		}
		if (queryTimeout != 0)
			preparedStatement.setQueryTimeout(queryTimeout);
		DiagnosticJLimo.trace(Trace.DataSetSave, "Query prepared:");
		this.query = query;
	}

	protected final void setParameter(int index, Column column, Variant value)
			throws SQLException, DataSetException {
		if (preparedStatement == null)
			prepare(query);

		if (coercer != null) {
			value = coercer.coerceFromColumn(column, value);

		}
		DiagnosticJLimo.check(value != null);
		DiagnosticJLimo.check(preparedStatement != null);
		DiagnosticJLimo.trace(Trace.DataSetSave, "setParameter " + index + " column " + // NORES
				column.getColumnName() + " type " + value.getType() // NORES
				+ " " + value.toString());

		index++;

		switch (value.getType()) {
			case Variant.STRING:
				int sqlType1 = column.getSqlType();
				String stringValue = value.getString();
				if (!useObjectForStrings) {
					preparedStatement.setString(index, stringValue);
				} else if (sqlType1 == java.sql.Types.LONGVARCHAR) {
					preparedStatement.setObject(index, stringValue, java.sql.Types.LONGVARCHAR, 0);
				} else if (sqlType1 == java.sql.Types.VARCHAR || sqlType1 == 0) {
					preparedStatement.setObject(index, stringValue, java.sql.Types.VARCHAR, 0);
				} else {

					// If precision is set, and the string isn't long enough, append spaces to pad the
					// string, otherwise use default behavior.
					//
					// Space padding is needed for some JDBC drivers when trying to bind a VARCHAR parameter
					// for comparison with a CHAR field in a relation (ie. where clause parameter is a
					// VARCHAR, but the column being compared to is a CHAR).
					if (useSpacePadding) {
						int precision = column.getPrecision();
						int length = stringValue.length();
						if ((precision != -1) && (length < precision)) {
							FastStringBuffer buf = new FastStringBuffer(precision);

							buf.append(stringValue);
							while (length < precision) {
								buf.append(' ');
								length++;
							}

							preparedStatement.setObject(index, buf.toString(), java.sql.Types.CHAR, 0);
						} else
							preparedStatement.setString(index, stringValue);
					} else {
						preparedStatement.setString(index, stringValue);
					}
				}
				break;

			case Variant.BIGDECIMAL:
				BigDecimal bigDecimal = value.getBigDecimal();
				if (bigDecimal == null)
					preparedStatement.setNull(index, column.getSqlType());
				else {
					preparedStatement.setBigDecimal(index, bigDecimal);
				}
				break;

			case Variant.BYTE:
				preparedStatement.setByte(index, value.getByte());
				break;

			case Variant.SHORT:
				preparedStatement.setShort(index, value.getShort());
				break;

			case Variant.INT:
				preparedStatement.setInt(index, value.getInt());
				break;

			case Variant.BOOLEAN:
				preparedStatement.setBoolean(index, value.getBoolean());
				break;

			case Variant.LONG:
				preparedStatement.setLong(index, value.getLong());
				break;

			case Variant.FLOAT:
				preparedStatement.setFloat(index, (float) value.getAsDouble());
				break;

			case Variant.DOUBLE:
				preparedStatement.setDouble(index, value.getDouble());
				break;

			case Variant.INPUTSTREAM:
				InputStream stream = value.getInputStream();
				if (stream == null)
					preparedStatement.setNull(index, column.getSqlType());
				else {
					int length = 0;
					boolean couldReset = true;
					try {
						// May not be implemented or may require a marking first. ByteArrayInputStream
						// will reset to the start if a mark was never set. BufferedInputStream
						// will throw an exception if a mark was never set to hold the complete InputStream.
						//
						stream.reset();
					} catch (IOException ex) {
						couldReset = false;
					}
					try {
						length = stream.available();
						// ! Diagnostic.println("Binary length = "+length);
					} catch (IOException ex) {
						DataSetException.IOException(ex);
					}

					// ! Diagnostic.println("Binary before throw");
					if (!couldReset && length == 0)
						DataSetException.onePassInputStream(column);
					// ! Diagnostic.println("Binary after throw");

					int sqlType = column.getSqlType();
					if (!database.isUseSetObjectForStreams())
						sqlType = 0; // Force use of setInputStream.
					switch (sqlType) {
						case java.sql.Types.BINARY:
						case java.sql.Types.VARBINARY:
						case java.sql.Types.LONGVARBINARY:
							byte[] bytes = null;
							try {
								bytes = InputStreamToByteArray.getBytes(stream);
							} catch (IOException ex) {
								DataSetException.IOException(ex);
							}
							preparedStatement.setObject(index, bytes, sqlType, 0);
							break;
						default:
							preparedStatement.setBinaryStream(index, stream, length);
					}
				}
				break;

			case Variant.TIMESTAMP:
				// preparedStatement.setTimestamp(index, value.getTimestamp());
				Timestamp dtm = value.getTimestamp();
				if (JdbcProvider.colNameStartsWithUTC(column.getServerColumnName())) {
					// preparedStatement.setString(index, UtcTimestamp.formatAsUtc(dtm));
					preparedStatement.setTimestamp(index, dtm, getCalUTC());
				} else {
					preparedStatement.setTimestamp(index, dtm);
				}
				break;

			case Variant.DATE:
				preparedStatement.setDate(index, (java.sql.Date) value.getDate());
				break;

			case Variant.TIME:
				preparedStatement.setTime(index, value.getTime());
				break;

			case Variant.OBJECT:
				preparedStatement.setObject(index, value.getObject());
				break;

			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				preparedStatement.setNull(index, column.getSqlType());
				break;
			default:
				DataSetException.unrecognizedDataType();
				break;
		}
	}

	protected final boolean rebuildNeeded() {
		if (reuseSaveStatements) {
			if (currentColumn < columnCount && columns[currentColumn] != null) {
				for (int index = currentColumn; index < columnCount; index++) {
					columns[index] = null;
					columnStatus[index] = 0;
				}
				rebuildQuery = true;
			}
		} else
			rebuildQuery = true;

		return rebuildQuery;
	}

	protected final void columnString(Column column, FastStringBuffer buf) {
		String result = column.getServerColumnName();
		if (quoteCharacter != '\0') {

			// ! JOAL:
			// The following is a workaround for DataGateway.
			// Since the local SQL parser doesn't accept identifier quotes,
			// but Paradox tables allows spaces in their column names, this is
			// a way to fool the parser into accepting these columns.
			{
				if (database.isUseTableName()) {
					String tableName = column.getTableName();
					if (tableName == null)
						tableName = column.getDataSet().getTableName();
					buf.append(tableName);
					buf.append('.');
				}
				// ! JOAL: Weirdness, we want a reference to a pseudo column INTERNALROW to appear without quotes
				// ! But pseudo property is not available: use rowId && !resolvable && isInternalRow
				boolean skipColumnQuotes = (column.isRowId() && !column.isResolvable() && result.equals("INTERNALROW")); // NORES
				if (!skipColumnQuotes)
					buf.append(quoteCharacter);
				buf.append(result);
				if (!skipColumnQuotes)
					buf.append(quoteCharacter);
			}
		} else
			buf.append(result);
	}

	protected final void whereClause(FastStringBuffer buf) {
		boolean firstTime = true;
		buf.append(" WHERE "); // NORES

		for (int index = 0; index < currentColumn; index++) {
			int status = columnStatus[index];
			if ((status & rowId) != 0) {
				Column column = columns[index];

				DiagnosticJLimo.check(column.isSearchable() ? null : "column: " + column.getColumnName() + " shouldn't be in WHERE clause!");

				if (!firstTime)
					buf.append(' ', 'A', 'N', 'D', ' ');

				firstTime = false;

				columnString(column, buf);

				if ((status & nullRowId) != 0)
					buf.append(" IS NULL"); // NORES
				else
					buf.append(' ', '=', ' ', '?');

			}
		}
		// Diagnostic.println("STEVE:::::::::::::::"+buf.toString());
	}

	protected final void setWhereParameters(int parameterNumber, Variant[] values)
			/*-throws DataSetException-*/ throws SQLException {
		DiagnosticJLimo.trace(Trace.DataSetSave, "Setting Where parameters");
		for (int index = 0; index < currentColumn; index++) {
			int status = columnStatus[index];
			if ((status & rowId) != 0)
				if ((status & nullRowId) == 0) {
					Column column = columns[index];

					DiagnosticJLimo.check(column.isSearchable(), "column: " + column.getColumnName() + "shouldn't be bound in WHERE clause!");

					setParameter(parameterNumber++, column, values[column.getOrdinal()]);
				}
		}
	}

	private Calendar getCalUTC() {
		if (calUTC == null) calUTC = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		return calUTC;
	}

	// Status indicators for query optimization (see below)
	final static int nullRowId = 0x01;
	final static int rowId = 0x02;
	final static int wasNull = 0x04;
	final static int assignedValue = 0x08;
	final static int changedValue = 0x10;

	// Generated query string

	private boolean useObjectForStrings;
	private boolean useSpacePadding;
	protected char quoteCharacter;
	protected Database database;

	// For query optimization (ie. when to rebuild a query) For all tables:
	private DataSet dataSet;
	private int columnCounts[];
	private int tables;
	private int currentTable;
	protected int currentColumn;
	private boolean rebuildQuery;
	private int columnStatusArray[][];
	private Column columnsArray[][];
	private String queryArray[];
	private PreparedStatement preparedStatementArray[];

	// For query optimization (ie. when to rebuild a query) For each table:
	protected int columnCount;
	protected int columnStatus[];
	protected Column columns[];
	private String query;
	private int queryTimeout;
	private Coercer coercer;
	protected PreparedStatement preparedStatement;
	boolean reuseSaveStatements;
	boolean useClearParameters;
	private Calendar calUTC;

}
