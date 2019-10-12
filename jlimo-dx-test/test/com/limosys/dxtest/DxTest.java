package com.limosys.dxtest;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.QueryDataSet;
import com.borland.dx.sql.dataset.QueryDescriptor;
import com.limosys.dxtest.DxTestConnection.DxTestDb;
import com.limosys.dxtest.DxTestUtils.DxTestOption;

public class DxTest {

	@Test
	public void disconnectDataSet() {
		System.out.println("DxText.disconnectDataSet()");
		try {
			DxTestConnection dxConn = new DxTestConnection();
			Database db1 = dxConn.getNewDb(DxTestDb.JLimo);
			QueryDataSet qds = new QueryDataSet();
			qds.setQuery(new QueryDescriptor(db1, "SELECT * FROM BASE_FEE_PARAMS"));
			qds.open();
			BigDecimal bd = qds.isNull("CONG_FEE_HOLD_AMT") ? null : qds.getBigDecimal("CONG_FEE_HOLD_AMT");

			testPrintOut_disconnectDataSet(qds, 1);

			db1.closeConnection();
			testPrintOut_disconnectDataSet(qds, 2);

			db1.closeConnection();
			qds.setBigDecimal("CONG_FEE_HOLD_AMT", new BigDecimal(2.51, MathContext.DECIMAL64));
			qds.saveChanges();
			testPrintOut_disconnectDataSet(qds, 3);

			db1.closeConnection();
			qds.refresh();
			testPrintOut_disconnectDataSet(qds, 4);

			db1.closeConnection();
			Database db2 = dxConn.getNewDb(DxTestDb.JLimo, true);
			qds.switchConnection(db2);
			if (bd == null)
				qds.setAssignedNull("CONG_FEE_HOLD_AMT");
			else
				qds.setBigDecimal("CONG_FEE_HOLD_AMT", bd);
			qds.saveChanges();
			testPrintOut_disconnectDataSet(qds, 5);

			qds.close();
			qds.refresh();
			testPrintOut_disconnectDataSet(qds, 6);

			DxTestUtils.getInstance().crashJLimoConnection(db2, DxTestOption.dbThroughQueryDS);
			qds.refresh();
			testPrintOut_disconnectDataSet(qds, 7);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testPrintOut_disconnectDataSet(QueryDataSet qds, int i) {
		Database db = qds.getDatabase();
		System.out.println(i + ": " + (qds.isOpen() ? "rowCount=" + qds.getRowCount() : "qds is closed")
				+ "  CONG_FEE_HOLD_AMT=" + (qds.isNull("CONG_FEE_HOLD_AMT") ? null : qds.getBigDecimal("CONG_FEE_HOLD_AMT").doubleValue()));
		System.out.println(qds);
		System.out.println("db.isOpen()=" + db.isOpen() + "  qds.isOpen()=" + qds.isOpen() + "  db=" + db + "  " + db.getConnection());
		System.out.println();
	}
}
