package com.borland.dx.sql.dataset;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnDbMapping;
import com.borland.dx.dataset.LoadCancel;
import com.borland.dx.dataset.MetaDataUpdate;
import com.borland.dx.dataset.RowStatus;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.DiagnosticJLimo;

public class SqlDataSetUtils {

	public static void addMissingColumns(Database db, ResultSet result, StorageDataSet sds) throws SQLException {
		Column[] cols = RuntimeMetaData.processMetaData(db, MetaDataUpdate.ALL, result);
		if (cols == null) return;
		for (int i = 0; i < cols.length; i++) {
			if (sds.hasColumn(cols[i].getColumnName()) == null) {
				sds.addColumn(cols[i]);
			}
		}
	}
	
  public static void loadDataFromResultSet(Database db, StorageDataSet sds, ResultSet rs, LoadCancel loadCancel) {
		try {
			if (rs != null) SqlDataSetUtils.addMissingColumns(db, rs, sds);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Variant[] variants = sds.startLoading(loadCancel, RowStatus.LOADED, false);
		if (rs != null) {
			try {
				Calendar calUTC = new GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
				while (rs.next()) {
					loadRow(variants, sds, rs, calUTC);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		sds.endLoading();  	
  }
	
	private static void loadRow(Variant[] variants, StorageDataSet dataSet, ResultSet rs, Calendar calUTC) throws SQLException {
		String tmpString;
		// InputStream tmpStream;
		BigDecimal tmpBigDecimal;
		int tmpInt;
		short tmpShort;
		long tmpLong;
		boolean tmpBool;
		byte tmpByte;
		float tmpFloat;
		double tmpDouble;
		java.sql.Date tmpDate;
		java.sql.Time tmpTime;
		java.sql.Timestamp tmpTimestamp;
		Object tmpObject;
		
		ResultSetMetaData rsMetaData = rs.getMetaData();
		for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
			Column col = dataSet.hasColumn(rsMetaData.getColumnName(i));
			if (col == null) continue;
			Variant v = variants[col.getOrdinal()];			
			if (ColumnDbMapping.load(v, rs, i, col)) continue;
			switch (v.getSetType()) {
				case Variant.STRING:
					tmpString = rs.getString(i);
					if (tmpString != null)
						v.setString(tmpString);
					else
						v.setAssignedNull();
					break;
					
				// case Variant.INPUTSTREAM:
				// tmpStream = rs.getBinaryStream(i);
				// if (tmpStream != null) {
				// if (copyStreams || JdbcProvider.isJdbcOdbcInputStream(tempStream))
				// tempStream = copyByteStream(tempStream);
				// if (tempStream != null)
				// value.setInputStream(tempStream);
				// else
				// value.setAssignedNull();
				// } else
				// value.setAssignedNull();
				// break;
					
				case Variant.BIGDECIMAL: 
					tmpBigDecimal = rs.getBigDecimal(i);
					if (tmpBigDecimal != null) 
						v.setBigDecimal(tmpBigDecimal);
					else
						v.setAssignedNull();
					break;

				case Variant.INT:
					tmpInt = rs.getInt(i);
					if (tmpInt==0 && rs.wasNull())
						v.setAssignedNull();
					else
						v.setInt(tmpInt);
					break;
					
				case Variant.SHORT:
					tmpShort = rs.getShort(i);
					if (tmpShort==0 && rs.wasNull())
						v.setAssignedNull();
					else
						v.setShort(tmpShort);
					break;

				case Variant.LONG: 
					tmpLong = rs.getLong(i);
					if (tmpLong==0 && rs.wasNull())
						v.setAssignedNull();
					else
						v.setLong(tmpLong);
					break;

				case Variant.BOOLEAN:
					tmpBool = rs.getBoolean(i);
					if (!rs.wasNull())
						v.setBoolean(tmpBool);
					else
						v.setAssignedNull();
					break;						
					
				case Variant.BYTE:
					tmpByte = rs.getByte(i);
					if (!rs.wasNull())
						v.setByte(tmpByte);
					else
						v.setAssignedNull();
					break;

				case Variant.FLOAT:
					tmpFloat = rs.getFloat(i);
					if (!rs.wasNull())
						v.setFloat(tmpFloat);
					else
						v.setAssignedNull();
					break;

				case Variant.DOUBLE:
					tmpDouble = rs.getDouble(i);
					if (!rs.wasNull())
						v.setDouble(tmpDouble);
					else
						v.setAssignedNull();
					break;
					
				case Variant.DATE:
					tmpDate = rs.getDate(i);
					if (tmpDate != null) 
						v.setDate(tmpDate);
					else
						v.setAssignedNull();
					break;

				case Variant.TIME:
					tmpTime = rs.getTime(i);
					if (tmpTime != null) 
						v.setTime(tmpTime);
					else
						v.setAssignedNull();
					break;

				case Variant.TIMESTAMP:
					if (JdbcProvider.loadUtcTime(v, rs, i, col.getColumnName(), calUTC)) break;
					
					tmpTimestamp = rs.getTimestamp(i);
					if (tmpTimestamp != null) { 
						// ! Diagnostic.println("timestamp: "+tempTimestamp+" "+tempTimestamp.getTime());
						v.setTimestamp(tmpTimestamp);
					} else
						v.setAssignedNull();
					break;

				case Variant.OBJECT:
					tmpObject = rs.getObject(i);
					if (tmpObject != null) 
						v.setObject(tmpObject);
					else
						v.setAssignedNull();
					break;

				default:
					DiagnosticJLimo.fail();
					break;
			}
		}
		dataSet.loadRow();
	}
	

}
