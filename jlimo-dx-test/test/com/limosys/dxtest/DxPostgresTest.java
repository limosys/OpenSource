package com.limosys.dxtest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.postgresql.util.PGmoney;

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

			// ResultSet rs = db.getJdbcConnection().getMetaData().getTypeInfo();
			// while (rs.next()) {
			// System.out.println(rs.getString("TYPE_NAME") + "\t" + JDBCType.valueOf(rs.getInt("DATA_TYPE")).getName());
			// }
			
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
			qds.setBigDecimal("KT_MONEY", new BigDecimal(1234567.878, MathContext.DECIMAL64).setScale(4, RoundingMode.HALF_UP));
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

		return db;
	}

}
