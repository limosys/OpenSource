package com.limosys.dxtest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.borland.dx.dataset.DataSetException;
import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.QueryDataSet;
import com.borland.dx.sql.dataset.QueryDescriptor;
import com.borland.dx.sql.dataset.UtcTimestamp;
import com.limosys.dxtest.DxTestConnection.DxTestDb;
import com.limosys.dxtest.DxTestUtils.DxTestOption;

public class DxTest {

	@Test
	public void disconnectDataSet() {
		boolean switchConnectionURL = true;
		boolean performCrashConnection = false;

		System.out.println("DxText.disconnectDataSet()");
		try {
			DxTestConnection dxConn = new DxTestConnection();
			Database db1 = dxConn.getNewDb(DxTestDb.JLimo);
			QueryDataSet qds = new QueryDataSet();
			qds.setQuery(new QueryDescriptor(db1, "SELECT * FROM BASE_FEE_PARAMS"));
			qds.open();
			// BigDecimal bd = qds.isNull("CONG_FEE_WARNING_AMT") ? null : qds.getBigDecimal("CONG_FEE_WARNING_AMT");
			BigDecimal bd = null;

			testPrintOut_disconnectDataSet(qds, 1);

			db1.closeConnection();
			testPrintOut_disconnectDataSet(qds, 2);

			db1.closeConnection();
			qds.setBigDecimal("CONG_FEE_WARNING_AMT", new BigDecimal(-100, MathContext.DECIMAL64));
			qds.saveChanges();
			testPrintOut_disconnectDataSet(qds, 3);

			// use breakpoints to stop lsvpn to cause the problem and then resume after switching database connection
			db1.closeConnection(); // breakepoint 1 to stop lsvpn
			try {
				qds.refresh();
				testPrintOut_disconnectDataSet(qds, 4);
			} catch (DataSetException dse) {
				dse.printStackTrace();
			}

			db1.closeConnection(); // breakpoint 2 to start lsvpn
			Database db2;
			if (switchConnectionURL) {
				db2 = db1;
				db2.getConnection().setConnectionURL(dxConn.getConnectionURL(DxTestDb.JLimo, true));
			} else {
				db2 = dxConn.getNewDb(DxTestDb.JLimo, true);
				qds.switchConnection(db2);
			}
			qds.refresh();
			if (bd == null)
				qds.setAssignedNull("CONG_FEE_WARNING_AMT");
			else
				qds.setBigDecimal("CONG_FEE_WARNING_AMT", bd);
			qds.saveChanges();
			testPrintOut_disconnectDataSet(qds, 5);

			qds.close();
			qds.refresh();
			testPrintOut_disconnectDataSet(qds, 6);

			if (performCrashConnection) {
				DxTestUtils.getInstance().crashJLimoConnection(db2, DxTestOption.dbThroughQueryDS);
				qds.refresh();
				testPrintOut_disconnectDataSet(qds, 7);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testPrintOut_disconnectDataSet(QueryDataSet qds, int i) {
		Database db = qds.getDatabase();
		System.out.println(i + ": " + (qds.isOpen() ? "rowCount=" + qds.getRowCount() : "qds is closed")
				+ "  CONG_FEE_WARNING_AMT=" + (qds.isNull("CONG_FEE_WARNING_AMT") ? null : qds.getBigDecimal("CONG_FEE_WARNING_AMT").doubleValue()));
		System.out.println(qds);
		System.out.println("db.isOpen()=" + db.isOpen() + "  qds.isOpen()=" + qds.isOpen() + "  db=" + db + "  " + db.getConnection());
		System.out.println();
	}

	@Test
	public void utcTimeTest() {
		// String[] ids = TimeZone.getAvailableIDs();
		// for (String id : ids) {
		// System.out.println(id + ": " + TimeZone.getTimeZone(id).getDisplayName());
		// }
		
		// TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		
		QueryDataSet qds = new QueryDataSet();
		try {
			DxTestConnection dxConn = new DxTestConnection();
			Database db = dxConn.getNewDb(DxTestDb.JLimo);
			db.openConnection();
			qds.setQuery(new QueryDescriptor(db, "SELECT JOB_ID, UTC_DISP_DTM, CUST_FIRST_NME FROM JOB WHERE JOB_ID=1616887"));
			qds.open();
			Timestamp tm = qds.getTimestamp("UTC_DISP_DTM");
			Date dtm = new Date(tm.getTime());
			System.out.println("dtmLcl: " + dtm);
			System.out.println("dtmUtc: " + UtcTimestamp.formatAsUtc(tm));
			System.out.println("name: " + qds.getString("CUST_FIRST_NME"));
			// qds.setString("CUST_FIRST_NME", "Andrew");
			// qds.post();
			// qds.saveChanges();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			qds.close();
		}		
		
		// GregorianCalendar calUTC = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		//
		// String serverName = "ecl-db1.lsvpn.net";
		// int portNumber = 1433;
		// String databaseName = "JLimo";
		// String username = "sa";
		// String password = "jlimo";
		//
		// String connectionUrl = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";" + "databaseName="
		// + databaseName + ";username=" + username + ";password=" + password + ";";
		//
		// // Establish the connection.
		// try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()) {
		//
		// System.out.println();
		// System.out.println("Connection established successfully.");
		//
		// // Create and execute an SQL statement that returns user name.
		// // String SQL = "SELECT SUSER_SNAME()";
		// String SQL = "SELECT JOB_ID, UTC_DISP_DTM FROM JOB WHERE JOB_ID=1616887";
		// try (ResultSet rs = stmt.executeQuery(SQL)) {
		//
		// // Iterate through the data in the result set and display it.
		// while (rs.next()) {
		// Date dtmLcl = new Date(rs.getTimestamp(2).getTime());
		// Date dtmUtc = new Date(rs.getTimestamp(2, calUTC).getTime());
		// System.out.println("JOB_ID: " + rs.getString(1));
		// System.out.println("dtmLcl: " + dtmLcl);
		// System.out.println("dtmUtc: " + dtmUtc);
		// }
		// }
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		
	}

	@Test
	public void lastRefreshDtmMillisTest() {
		DateFormat dtFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		QueryDataSet qds = new QueryDataSet();
		try {
			DxTestConnection dxConn = new DxTestConnection();
			Database db = dxConn.getNewDb(DxTestDb.JLimo);
			db.openConnection();
			qds.setQuery(new QueryDescriptor(db, "SELECT TOP 10 * FROM JOB"));
			qds.open();
			System.out.println("refresh #1: " + dtFormat.format(new Date(qds.getLastRefreshDtmMillis())));
			for (int i = 0; i < 5; i++) {
				Thread.sleep(750);
				qds.refresh();
				System.out.println("refresh #" + Integer.toString(i+2) + ": " + dtFormat.format(new Date(qds.getLastRefreshDtmMillis())));				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			qds.close();
		}		
		
	}
	
}
