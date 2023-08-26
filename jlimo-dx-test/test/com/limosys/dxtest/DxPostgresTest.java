package com.limosys.dxtest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.postgresql.util.GT;
import org.postgresql.util.PGmoney;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnDbMapping;
import com.borland.dx.dataset.ColumnDbMappingParamListener;
import com.borland.dx.dataset.Variant;
import com.borland.dx.sql.dataset.ConnectionDescriptor;
import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.QueryDataSet;
import com.borland.dx.sql.dataset.QueryDescriptor;

public class DxPostgresTest {

	@Test
	public void pgTestQueryDataSet() {
		boolean showSqlTypes = false;
		try {
			String s = "1,234.50";
			String s1 = s.trim().replace(",", "");
			double d = Double.parseDouble(s1);
			System.out.println("[" + s + "] -> [" + d + "]");
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		System.setProperty("jb.util.diagnostic", "on");
		try {
			Database db = initDb_LimosysDev();

			if (showSqlTypes) {
				ResultSet rs = db.getJdbcConnection().getMetaData().getTypeInfo();
				while (rs.next()) {
					System.out.println("else if (cType.equals(\"" + rs.getString("TYPE_NAME") + "\"))\r\n"
							+ "			return \"Variant." + JDBCType.valueOf(rs.getInt("DATA_TYPE")).getName() + "\"; "
									+ "// JDBCType." + JDBCType.valueOf(rs.getInt("DATA_TYPE")).getName() + ",  sqlType = " + rs.getInt("DATA_TYPE"));
				}
			}
			
			QueryDataSet qds = new QueryDataSet();
			Column colMoney = new Column();
			colMoney.setColumnName("KT_MONEY");
			colMoney.setServerColumnName("KT_MONEY");
			colMoney.setTableName("kor_test");
			colMoney.setDbMapping(ColumnDbMapping.PG_MONEY, new ColumnDbMappingParamListener() {
				@Override
				public Object getStatementParamValue(Variant data) {
					return new PGmoney(data.getAsDouble());
				}
			});
			qds.addColumn(colMoney);

			qds.setQuery(new QueryDescriptor(db, "select * from kor_test"));
			qds.open();
			System.out.println(qds);
			Column[] cols = qds.getColumns();
			for (int i = 0; i < cols.length; i++) {
				Column col = cols[i];
				System.out.println("col:" + col.getColumnName()
						+ " srvrCol:" + col.getServerColumnName()
						+ " type:" + col.getDataType()
						+ " sqlType:" + col.getSqlType());
			}
			
			// int -> Variant.INT
			// date -> Variant.DATE
			// time -> Variant.TIME
			// timestamp -> Variant.TIMESTAMP
			// char -> Variant.STRING
			// varchar -> Variant.STRING
			// money -> Variant.DOUBLE
			// numeric -> Variant.BIGDECIMAL

			db.closeConnection();
			System.out.println(qds);

			Database db2 = initDb_LimosysDev();
			qds.switchConnection(db2);
			System.out.println(qds);
			System.out.print("Date[" + qds.getDate("kt_dt").toString() + "]");

			qds.setInt("KT_ID", qds.getInt("KT_ID") + 1);
			qds.setBigDecimal("KT_MONEY", new BigDecimal(50, MathContext.DECIMAL64).setScale(4, RoundingMode.HALF_UP));
			qds.saveChanges();
			System.out.println(qds);

			qds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Database initDb_LimosysDev() {
		// example: Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/?user=name&amp;password=abc&amp;ssl=false");
		String connectionURL = "jdbc:postgresql://limosys-dev.lsoffice.local:5432/postgres?currentSchema=public"; // jdbc:postgresql://host:port/database
		String userName = "postgres";
		String password = "jlimo";
		boolean promptPassword = false;
		String driver = "org.postgresql.Driver";
		Properties properties = null;

		Database db = new Database();
		db.setConnection(new ConnectionDescriptor(connectionURL, userName, password, promptPassword, driver, properties));
		db.executeStatement("set lc_monetary to \"de-DE-x-icu\";");

		return db;
	}

	@Test
	public void pgTestMoneyToDouble() {
		List<String> testValues = new ArrayList<String>();
		testValues.add("1,234.50");
		testValues.add("-1,234.50");
		testValues.add("(1,234.50)");
		testValues.add("1,234.50 $");
		testValues.add("($1,234.518)");

		testValues.add("1.234.567,50");
		testValues.add("-1.234,50");
		testValues.add("(1.234,50)");
		testValues.add("1.234,50 $");
		testValues.add("($1.234.567,50)");

		for (String s: testValues) {
			try {
				System.out.println(s + " -> " + moneyToDouble(s));
			} catch (Exception ex) {
				System.out.println(s + " -> " + ex.getMessage());				
			}
		}
	}
	
	public static double moneyToDouble(@Nullable String s) throws SQLException {
		if (s == null) return 0; // SQL NULL
		s = s.trim();
		String s1 = stripLeadingAndTrailingMoneyChars(s, true);
    
		boolean isNegative = false;
		if (s1.startsWith("-")) {
			isNegative = true;
			s1 = s1.substring(1);
		} else if (s1.startsWith("(") && s1.endsWith(")")) {
			isNegative = true;
			s1 = stripLeadingAndTrailingMoneyChars(s1.substring(1, s1.length() - 1), false);
		}

		int iLastComma = s1.lastIndexOf(",");
		if (iLastComma >= 0) {
			int iLastPeriod = s1.lastIndexOf(".");
			if (iLastPeriod > iLastComma) {
				s1 = s1.replace(",", "");
			} else {
				s1 = s1.substring(0, iLastComma).replace(".", "") + "." + s1.substring(iLastComma + 1).replace(".", "");
			}
		}

		try {
			double d = Double.parseDouble(s1);
			return (isNegative ? -d : d);
		} catch (NumberFormatException e) {
			throw new PSQLException(GT.tr("Bad value for type {0} : {1}", "money", s), PSQLState.NUMERIC_VALUE_OUT_OF_RANGE);
		}
	}
	
	private static boolean isNotAllowedMoneyChar(char ch) {
		// TODO add additional logic to invalidate input before stripping leading/trailing characters
		return false;	
	}
	
	private static boolean isValidMoneyChar(char ch, Character allowedParenthesisChar) {
		return Character.isDigit(ch)
				|| ch == '.'
				|| ch == ','
				|| ch == '-'
				|| (allowedParenthesisChar != null && allowedParenthesisChar.charValue() == ch);
	}
	
	private static String stripLeadingAndTrailingMoneyChars(String sMoney, boolean allowParenthesis) throws SQLException {
		Character allowedParanthesisChar = (allowParenthesis ? '(' : null);
		int pStart = 0;
		while (pStart < sMoney.length()) {
			char ch = sMoney.charAt(pStart);
			if (isValidMoneyChar(ch, allowedParanthesisChar)) 
				break;
			else if (isNotAllowedMoneyChar(ch))
				throw new PSQLException(GT.tr("Bad value for type {0} : {1}", "money", sMoney), PSQLState.NUMERIC_VALUE_OUT_OF_RANGE);
			else
				pStart++;
		}

		allowedParanthesisChar = (allowParenthesis ? ')' : null);		
		int pEnd = sMoney.length();
		while (pEnd > 0) {
			char ch = sMoney.charAt(pEnd - 1);
			if (isValidMoneyChar(ch, allowedParanthesisChar)) 
				break;
			else if (isNotAllowedMoneyChar(ch))
				throw new PSQLException(GT.tr("Bad value for type {0} : {1}", "money", sMoney), PSQLState.NUMERIC_VALUE_OUT_OF_RANGE);
			else
				pEnd--;
		}		
		
		return sMoney.substring(pStart, pEnd);
	}
	
}
