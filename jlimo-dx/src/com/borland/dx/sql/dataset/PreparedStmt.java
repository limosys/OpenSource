//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/PreparedStmt.java,v 7.1 2003/10/15 18:41:36 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! PreparedStmt
//! This is a wrapper around JDBC PreparedStatement, that given a query string will bind parameters
//! and execute the query.
//!
//! **** WARNING !!! ****
//!
//! This file is related to: CallableStmt.java, InterbaseCallableStmt.java, and OracleCallableStmt.java.
//! Be careful when changing the signature of methods in this file and the related files. Many of
//! the methods are overridden, and the functionality can easily be broken if the derived methods
//! are not changed accordingly [it may compile, but...]
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.MasterLinkDescriptor;
import com.borland.dx.dataset.ReadWriteRow;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;

class PreparedStmt {
	PreparedStmt() {}

	void resetState(Database database, String query, MasterLinkDescriptor masterDetail, ReadWriteRow parameterRow)
			/*-throws DataSetException-*/ throws SQLException {
		ReadWriteRow[] rows = parameterRow == null ? null : new ReadWriteRow[] { parameterRow };
		resetState(database, query, masterDetail, rows);
	}

	void resetState(Database database, String query, MasterLinkDescriptor masterDetail, ReadWriteRow[] parameterRows)
			/*-throws DataSetException-*/ throws SQLException {
		this.database = database;
		this.query = query;
		this.masterDetail = masterDetail;
		this.parameterRows = parameterRows;
		this.paramColumns = null;
		this.paramIndices = null;
		this.paramValues = null;
		parameterRowCount = 0;
		columnCount = 0;
		closeStatement();

		// Ignore the masterDetail link columns if this is not FetchAsNeeded:
		if (masterDetail != null && !masterDetail.isFetchAsNeeded())
			this.masterDetail = null;
	}

	int executeUpdate() /*-throws DataSetException-*/ throws SQLException {
		prepareParameters();
		statement.execute();
		return statement.getUpdateCount();
	}

	ResultSet executeQuery() /*-throws DataSetException-*/ throws SQLException {
		prepareParameters();
		return statement.executeQuery();
	}

	// ! Warning: This method is overloaded in InterbaseCallableStmt
	void closeResultSet(ResultSet resultSet) /*-throws DataSetException-*/ throws SQLException {
		resultSet.close();

		// We need to be dumber about statement caching. We don't trust the return
		// from getMaxStatements anymore. JdbcOdbc bridge using interbase returns < 1
		// and a SQL server jdbc drivers also returns something <= 10. So now the
		// developer must us a manual override (Database.setUseStatementCaching(false);
		// to disable caching.
		//
		if (!database.isUseStatementCaching())
			closeStatement();
		/*
		 * boolean doClose = true; try { doClose = !(database.isUseStatementCaching() && database.getMaxStatements() > 10); } finally { if (doClose)
		 * closeStatement(); }
		 */
	}

	// ! Warning: This method is overloaded in CallableStmt
	PreparedStatement createStatement(String queryString) /*-throws DataSetException-*/ {
		return database.createPreparedStatement(queryString);
	}

	// ! Warning: This method is overloaded in CallableStmt
	void closeStatement() throws SQLException, DataSetException {
		if (statement != null) {
			statement.close();
			statement = null;
		}
	}

	// ! Warning: This method is overloaded in InterbaseStmt
	void prepareParameters() /*-throws DataSetException-*/ throws SQLException {

		if (paramColumns == null) {
			internalQuery = query;
			char quoteCharacter = database.getIdentifierQuoteChar();
			SimpleParser parser = new SimpleParser(query, quoteCharacter);
			QueryParseToken tokens = parser.getParameterTokens();
			// !kna boolean namedParameters = analyzeParameters(tokens,parser.getParameterCount());
			boolean namedParameters = analyzeParameters(tokens, parser.getParameterCount(), quoteCharacter);// !kna
			if (namedParameters)
				internalQuery = parser.format(tokens, true);
			statement = createStatement(internalQuery);
		} else if (statement == null) {
			statement = createStatement(internalQuery);
		}

		bindParameters();
	}

	// ! Warning: This method is overloaded in CallableStmt
	boolean analyzeParameters(QueryParseToken tokens, int actualParamCount, char quoteChar) /*-throws DataSetException-*/ {// !kna
		parameterRowCount = 0;
		if (parameterRows != null) {
			for (int i = 0; i < parameterRows.length && parameterRows[i] != null; i++)
				parameterRowCount++;
		}

		int paramCount = 0;

		paramColumns = new String[actualParamCount];
		paramIndices = new int[actualParamCount];
		paramValues = new Variant[actualParamCount];

		for (int index = 0; index < paramValues.length; ++index)
			paramValues[index] = new Variant();

		// For validation...
		boolean namedParameters = false;
		boolean ordinalParameters = false;

		while (tokens != null) {
			if (tokens.isParameter()) {
				// If the parameter marker was <?>, get parameter values left-to-right.
				if (tokens.getName() == null) {
					// Validate parameter format
					if (namedParameters)
						DataSetException.mismatchedParameterFormat();
					else if (!ordinalParameters) {
						int paramMaxCount = 0;
						int masterMaxCount = 0;
						for (int i = 0; i < parameterRowCount; i++)
							paramMaxCount += parameterRows[i].getColumnCount();
						if (masterDetail != null) {
							masterMaxCount = masterDetail.getMasterLinkColumns().length;
							paramMaxCount += masterMaxCount;
						}
						if (actualParamCount > paramMaxCount)
							DataSetException.parameterCountMismatch(paramCount, paramMaxCount, 0);
						ordinalParameters = true;
					}

					paramCount++;
				}
				// Else, the parameters are named, get parameter values from row by parameter name.
				else {
					// Validate parameter format
					if (ordinalParameters)
						DataSetException.mismatchedParameterFormat();
					else
						namedParameters = true;

					// !kna classifyNamedParameter(tokens.getName(), paramCount);
					classifyNamedParameter(tokens.getName(), paramCount, quoteChar);// !kna
					paramCount++;
				}
			}
			tokens = tokens.getNextToken();
		}

		if (ordinalParameters)
			classifyOrdinalParameters(paramCount);

		DiagnosticJLimo.trace(Trace.QueryProgress, "Setting columnCount");
		columnCount = paramCount;

		return namedParameters;
	}

	// !kna void classifyNamedParameter(String columnName, int param) /*-throws DataSetException-*/ {
	private void classifyNamedParameter(String columnName, int param, char quoteChar) /*-throws DataSetException-*/ {// !kna
		// !kna, for to fix Bug #19671...
		boolean quoted = (quoteChar != ' ' && columnName.length() >= 2 &&
				quoteChar == columnName.charAt(0) &&
				quoteChar == columnName.charAt(columnName.length() - 1));
		if (quoted) {
			columnName = columnName.substring(1, columnName.length() - 1);
		}
		// !...kna
		int pos = columnName.indexOf((int) '.');

		// User specified parameterRow
		// Allow user to include parameterRow specification i.e. " {call proc(:2.ColumnId)}
		// This would mean use parameterRows[1] i.e. number is 1 based (0 means masterLink).
		//
		if (pos > 0) {
			int paramNo = Integer.parseInt(columnName.substring(0, pos));
			columnName = columnName.substring(pos + 1);
			paramNo--;
			if (paramNo < 0) {
				Column column = (masterDetail == null) ? null : masterDetail.getMasterDataSet().hasColumn(columnName);
				if (column != null) {
					String masterColumns[] = masterDetail.getMasterLinkColumns();
					for (int index = 0; index < masterColumns.length; index++) {
						if (columnName.equalsIgnoreCase(masterColumns[index])) {
							paramColumns[param] = columnName;
							paramIndices[param] = -1;
							return;
						}
					}
				}
			} else if (paramNo < parameterRowCount) {
				ReadWriteRow parameterRow = parameterRows[paramNo];
				Column column = parameterRow.hasColumn(columnName);
				if (column != null) {
					paramColumns[param] = columnName;
					paramIndices[param] = paramNo;
					return;
				}
			}
			DataSetException.unknownParamName(columnName);
		}

		// ParameterRow index not indicated, search for the column left to right.
		//
		if (masterDetail != null) {
			Column column = masterDetail.getMasterDataSet().hasColumn(columnName);
			if (column != null) {
				String masterColumns[] = masterDetail.getMasterLinkColumns();
				for (int index = 0; index < masterColumns.length; index++) {
					if (columnName.equalsIgnoreCase(masterColumns[index])) {
						paramColumns[param] = columnName;
						paramIndices[param] = -1;
						return;
					}
				}
			}
		}
		if (parameterRows != null) {
			for (int i = 0; i < parameterRowCount; i++) {
				ReadWriteRow parameterRow = parameterRows[i];
				Column column = parameterRow.hasColumn(columnName);
				if (column != null) {
					paramColumns[param] = columnName;
					paramIndices[param] = i;
					return;
				}
			}
		}
		DataSetException.unknownColumnName(columnName);
	}

	// Order the parameters from left to right starting with masterDetail columns:
	//
	private void classifyOrdinalParameters(int paramCount) /*-throws DataSetException-*/ {

		int foundParams = 0;

		if (masterDetail != null) {
			String masterColumns[] = masterDetail.getMasterLinkColumns();
			if (parameterRows != null)
				System.arraycopy(masterColumns, 0, paramColumns, 0, masterColumns.length);
			else
				paramColumns = masterColumns;

			foundParams = masterColumns.length;
		}

		if (parameterRows != null) {
			for (int i = 0; i < parameterRowCount; i++) {
				ReadWriteRow parameterRow = parameterRows[i];
				String parameterRowColumns[] = parameterRow.getColumnNames(paramCount - foundParams);
				if (masterDetail != null || parameterRowCount > 1)
					System.arraycopy(parameterRowColumns, 0, paramColumns, foundParams, parameterRowColumns.length);
				else
					paramColumns = parameterRowColumns;

				foundParams += parameterRowColumns.length;
			}
		}
	}

	// ! Warning: This method is overloaded in OracleCallableStmt
	void bindParameters() throws SQLException, DataSetException {
		String name;
		int paramNo;
		Column column;
		DataSet master = null;

		if (masterDetail != null)
			master = masterDetail.getMasterDataSet();

		for (int index = 0; index < columnCount; index++) {
			paramNo = paramIndices[index];
			name = paramColumns[index];
			if (paramNo < 0) {
				master.getVariant(name, paramValues[index]);
				column = master.getColumn(name);
			} else {
				ReadWriteRow row = parameterRows[paramNo];
				row.getVariant(name, paramValues[index]);
				column = row.getColumn(name);
			}
			bindParameter(paramValues[index], column, index + 1);
		}
	}

	// ! Warning: This method is overloaded in CallableStmt
	void bindParameter(Variant data, Column column, int param)
			/*-throws DataSetException-*/ throws SQLException {
		if (data.isNull()) {
			int sqlType = column.getSqlType();
			if (sqlType == 0)
				sqlType = RuntimeMetaData.variantTypeToSqlType(column.getDataType());
			statement.setNull(param, sqlType);
		} else {
			switch (data.getType()) {
				case Variant.STRING:
					DiagnosticJLimo.trace(Trace.QueryProgress, "Setting param: " + param + "  Value: " + data.getString());
					statement.setString(param, data.getString());
					break;

				case Variant.BIGDECIMAL:
					statement.setBigDecimal(param, data.getBigDecimal());
					break;

				case Variant.BOOLEAN:
					statement.setBoolean(param, data.getBoolean());
					break;

				case Variant.BYTE:
					statement.setByte(param, data.getByte());
					break;

				case Variant.SHORT:
					statement.setShort(param, data.getShort());
					break;

				case Variant.LONG:
					statement.setLong(param, data.getLong());
					break;

				case Variant.INT:
					statement.setInt(param, data.getInt());
					break;

				case Variant.FLOAT:
					statement.setFloat(param, data.getFloat());
					break;

				case Variant.DOUBLE:
					statement.setDouble(param, data.getDouble());
					break;

				case Variant.INPUTSTREAM:
					InputStream stream = data.getInputStream();
					int length = 0;
					boolean couldReset = true;

					try {
						// May not be implemented or may require a marking first.
						// ByteArrayInputStream will reset to the start if a mark was
						// never set.
						// BufferedInputStream will throw an exception if a mark was
						// never set to hold the complete InputStream.
						//
						stream.reset();
					} catch (IOException ex) {
						couldReset = false;
					}

					try {
						length = stream.available();
					} catch (IOException ex) {
						DataSetException.IOException(ex);
					}

					if (!couldReset && length == 0)
						DataSetException.onePassInputStream(column);

					statement.setBinaryStream(param, stream, length);
					break;

				case Variant.TIMESTAMP:
					// statement.setTimestamp(param, data.getTimestamp());
					Timestamp dtm = data.getTimestamp();
					if (JdbcProvider.colNameStartsWithUTC(column.getServerColumnName())) {
						// statement.setString(param, UtcTimestamp.formatAsUtc(dtm));
						statement.setTimestamp(param, dtm, getCalUTC());
					} else {
						statement.setTimestamp(param, dtm);
					}
					break;

				case Variant.DATE:
					statement.setDate(param, data.getDate());
					break;

				case Variant.TIME:
					statement.setTime(param, data.getTime());
					break;

				case Variant.OBJECT:
					statement.setObject(param, data.getObject());
					break;

				default:
					DataSetException.unrecognizedDataType();
					break;
			}
		}
	}

	protected void finalize() {
		try {
			closeStatement();
		} catch (Throwable ex) {
			DiagnosticJLimo.printStackTrace(ex);
		}
	}

	private Calendar getCalUTC() {
		if (calUTC == null) calUTC = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		return calUTC;
	}

	private PreparedStatement statement;
	private Calendar calUTC;
	Database database;
	String query;
	String internalQuery;
	MasterLinkDescriptor masterDetail;
	int parameterRowCount; // Number of parameterRows
	ReadWriteRow[] parameterRows; // ReadWriteRow[parameterRowCount]
	int columnCount; // Number of parameter markers in query
	String[] paramColumns; // String[columnCount] : Name of parameter
	int[] paramIndices; // int[columnCount] : Which parameterRow (-1 = masterLink)
	Variant[] paramValues; // Local place holder for a value
	
}
