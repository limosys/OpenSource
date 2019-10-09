package com.limosys.dxtest;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.QueryDataSet;
import com.borland.dx.sql.dataset.QueryDescriptor;
import com.limosys.dxtest.DxTestConnection.DxTestDb;

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
			testPrintOut_disconnectDataSet(db1, qds, 1);

			db1.closeConnection();
			testPrintOut_disconnectDataSet(db1, qds, 2);

			qds.refresh();
			testPrintOut_disconnectDataSet(db1, qds, 3);

			db1.closeConnection();
			qds.setBigDecimal("CONG_FEE_HOLD_AMT", new BigDecimal(1));
			qds.saveChanges();
			testPrintOut_disconnectDataSet(db1, qds, 4);
			
			db1.closeConnection();
			Database db2 = dxConn.getNewDb(DxTestDb.JLimo);
			qds.switchDatabase(db2);
			qds.setBigDecimal("CONG_FEE_HOLD_AMT", new BigDecimal(2));
			qds.saveChanges();
			testPrintOut_disconnectDataSet(db1, qds, 5);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testPrintOut_disconnectDataSet(Database db, QueryDataSet qds, int i) {
		System.out.println("RowCount(" + i + "): " + (qds.isOpen() ? qds.getRowCount() : "qds is closed") 
		                   + "  CONG_FEE_HOLD_AMT=" + (qds.isNull("CONG_FEE_HOLD_AMT") ? null : qds.getBigDecimal("CONG_FEE_HOLD_AMT").doubleValue()));
		System.out.println(qds);
		System.out.println("db.isOpen()=" + db.isOpen() + "  qds.isOpen()=" + qds.isOpen());
		System.out.println();		
	}
}
