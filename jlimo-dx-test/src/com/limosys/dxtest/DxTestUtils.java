package com.limosys.dxtest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.ProcedureDataSet;
import com.borland.dx.sql.dataset.ProcedureDescriptor;
import com.borland.dx.sql.dataset.QueryDataSet;
import com.borland.dx.sql.dataset.QueryDescriptor;

public class DxTestUtils {

	public static DxTestUtils getInstance() {
		return new DxTestUtils();
	}
	
	public static enum DxTestOption {
		QueryDS,
		ProcDS,
		dbThroughQueryDS,
		dbThroughProcDS
	}

	private int tCount = 0;

	public boolean crashJLimoConnection(Database db, DxTestOption testOption) throws SQLException {

		// lsp_DRVR_S_v63
		
		boolean isConnectionCarshed = false;
		String qStr = "SELECT TOP 50 * FROM JLimo.dbo.DRVR";
		// String qStr = "SELECT * FROM JLimo.dbo.HIST_JOB";
		QueryDataSet qds = new QueryDataSet();
		qds.setQuery(new QueryDescriptor(db, qStr));

		String procStr = "exec JLimo.dbo.lsp_DRVR_S_v63";
		ProcedureDataSet pds = new ProcedureDataSet();
		pds.setProcedure(new ProcedureDescriptor(db, procStr));

		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(6);
			tCount = 0;

			List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
			switch (testOption) {
				case dbThroughQueryDS:
					tasks.add(initCrashJLimoTask(101, db, qStr));
					tasks.add(initCrashJLimoTask(102, db, qStr));
					tasks.add(initCrashJLimoTask(103, db, qStr));
					tasks.add(initCrashJLimoTask(104, db, qStr));
					tasks.add(initCrashJLimoTask(105, db, qStr));
					tasks.add(initCrashJLimoTask(106, db, qStr));
					break;
				case QueryDS:
					tasks.add(initCrashJLimoTask(201, qds));
					tasks.add(initCrashJLimoTask(202, qds));
					tasks.add(initCrashJLimoTask(203, qds));
					tasks.add(initCrashJLimoTask(204, qds));
					tasks.add(initCrashJLimoTask(205, qds));
					tasks.add(initCrashJLimoTask(206, qds));
					break;
				case ProcDS:
					tasks.add(initCrashJLimoTask(301, pds));
					tasks.add(initCrashJLimoTask(302, pds));
					tasks.add(initCrashJLimoTask(303, pds));
					tasks.add(initCrashJLimoTask(304, pds));
					tasks.add(initCrashJLimoTask(305, pds));
					tasks.add(initCrashJLimoTask(306, pds));
					break;
				case dbThroughProcDS:
					break;
				default:
					break;
			}

			long timeoutMillis = 5000;
			for (Future<Boolean> future : executor.invokeAll(tasks, timeoutMillis, TimeUnit.MILLISECONDS)) {
				if (future.isDone()) {
					isConnectionCarshed = isConnectionCarshed || future.get();
				}
			}
		} catch (CancellationException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executor != null) executor.shutdown();
		}

		try {
			qds.refresh();
			System.out.println("crashJLimoConnection() r:" + qds.getRowCount());
			qds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("crashJLimoConnection() = " + isConnectionCarshed + "  tCount=" + tCount);
		System.out.println();
		return isConnectionCarshed;
	}

	private Callable<Boolean> initCrashJLimoTask(int taskId, QueryDataSet qds) {
		Callable<Boolean> task = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int count = 0;
				while (true) {
					try {
						tCount++;
						qds.refresh();
						System.out.println("qds" + taskId + ": refresh " + Integer.toString(count++)
								// + "  tCount=" + tCount
								);
					} catch (Exception e) {
						e.printStackTrace();
						// System.out.println(this.hashCode() + " error: " + e.getMessage());
						return true;
					}
				}
			}
		};
		return task;
	}

	private Callable<Boolean> initCrashJLimoTask(int taskId, ProcedureDataSet pds) {
		Callable<Boolean> task = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int count = 0;
				while (true) {
					try {
						tCount++;
						pds.refresh();
						System.out.println("procDs" + taskId + ": refresh " + Integer.toString(count++)
								// + "  tCount=" + tCount
								);
					} catch (Exception e) {
						e.printStackTrace();
						// System.out.println(this.hashCode() + " error: " + e.getMessage());
						return true;
					}
				}
			}
		};
		return task;
	}
	
	private Callable<Boolean> initCrashJLimoTask(int taskId, Database db, String qStr) {
		Callable<Boolean> task = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int count = 0;
				QueryDataSet qds = new QueryDataSet();
				qds.setQuery(new QueryDescriptor(db, qStr));
				while (true) {
					try {
						tCount++;
						qds.refresh();
						System.out.println("db" + taskId + ": refresh " + Integer.toString(count++)
								// + "  tCount=" + tCount
								+ "  rows=" + qds.getRowCount());
					} catch (Exception e) {
						e.printStackTrace();
						// System.out.println(this.hashCode() + " error: " + e.getMessage());
						return true;
					}
				}
			}
		};
		return task;
	}

}
