package com.limosys.dxtest;

import com.borland.dx.sql.dataset.Database;
import com.limosys.registry.JLimoRegistryVals;

public class DxTestConnection {

	public static enum DxTestDb {
		JLimo(JLimoRegistryVals.DB_DISPATCH.getVal()),
		JLimoAcct(JLimoRegistryVals.DB_ACCT.getVal()),
		JLimoAddr(JLimoRegistryVals.DB_ADDR.getVal()),
		JLimoGps(JLimoRegistryVals.DB_GPS.getVal()),
		JLimoTrace("JLimoTrace");

		private String dbName;

		private DxTestDb(String dbName) {
			this.dbName = dbName;
		}
	}

	private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public Database getNewDb(DxTestDb dxTestDb) {
		Database db = new Database();
		String connStr = getConnectionStringLogin(dxTestDb.dbName, 15, false);
		db.setConnection(new com.borland.dx.sql.dataset.ConnectionDescriptor(connStr, "sa", JLimoRegistryVals.PASSWORD.getVal(), false, driver));
		return db;
	}

	private String getConnectionStringLogin(String dbName, Integer timeout, boolean useIntegratedSecurity) {
		String connectionString = "jdbc:sqlserver://"
				+ JLimoRegistryVals.SERVER.getVal()
				+ (JLimoRegistryVals.SERVER.getVal().indexOf("\\") >= 0 ? "" : ":" + JLimoRegistryVals.PORT.getVal())
				+ ";databaseName="
				+ dbName
				+ ";useServerPrepStmts=false"
				+ (timeout != null && timeout > 0 && timeout < 60 ? ";loginTimeout=" + timeout + ";socketTimeout=" + timeout : "")
				+ (useIntegratedSecurity ? ";integratedSecurity=true" : "");
		return connectionString;
	}

}
