package com.borland.dx.dataset;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnDbMapping {

	public static final int DEFAULT = 0;
	public static final int PG_MONEY = 1;
	
	public static boolean load(Variant value, ResultSet result, int index, Column col) throws SQLException {
		if (col.getDbMapping() == null) return false;
		switch (col.getDbMapping().intValue()) {
			case PG_MONEY:
				Object tempObj = result.getObject(index);
				if (!result.wasNull())
					//value.setBigDecimal(new BigDecimal(tempDouble, MathContext.DECIMAL32));
					value.setAssignedNull();
				else
					value.setAssignedNull();
				return true;
		}
		return false;		
	}

	public static boolean setParameter(PreparedStatement statement, Variant data, Column col, int param) throws SQLException {
		if (col.getDbMapping() == null) return false;
		switch (col.getDbMapping().intValue()) {
			case PG_MONEY:
				if (col.getDbMappingListener() == null) throw new SQLException("column[" + col.getColumnName() + "] missing dbMappingListener");
				statement.setObject(param, col.getDbMappingListener().getStatementParamValue(data));
				return true;
		}
		return false;
	}

	public static void setMappingDetails(Column col, Integer dbMapping) {
		switch (dbMapping == null ? 0 : dbMapping.intValue()) {
			case ColumnDbMapping.PG_MONEY:
				col.setDataType(Variant.BIGDECIMAL);
				col.setPrecision(19);
				col.setScale(2);
				col.setSqlType(java.sql.Types.NUMERIC);
				break;
		}
		
	}
	
}
