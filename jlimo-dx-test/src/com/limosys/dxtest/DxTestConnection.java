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
	private static Integer timeout = 30;
	private static boolean useIntegratedSecurity = false;
	

	public Database getNewDb(DxTestDb dxTestDb) {
		return getNewDb(dxTestDb, false);
	}

	public Database getNewDb(DxTestDb dxTestDb, boolean switchLsvpn) {
		Database db = new Database();
		String connURL = getConnectionURL(dxTestDb, switchLsvpn);
		db.setConnection(new com.borland.dx.sql.dataset.ConnectionDescriptor(connURL, "sa", JLimoRegistryVals.PASSWORD.getVal(), false, driver));
		return db;
	}

	public String getConnectionURL(DxTestDb dxTestDb, boolean switchLsvpn) {
		String srvrStr = JLimoRegistryVals.SERVER.getVal().toLowerCase();
		if (switchLsvpn) {
			if (srvrStr.contains("lsvpn.net"))
				srvrStr = srvrStr.replace("lsvpn.net", "lsvpn.com");
			else if (srvrStr.contains("lsvpn.com"))
				srvrStr = srvrStr.replace("lsvpn.com", "lsvpn.net");
		}
		String connectionString = "jdbc:sqlserver://"
				+ srvrStr
				+ (srvrStr.indexOf("\\") >= 0 ? "" : ":" + JLimoRegistryVals.PORT.getVal())
				+ ";databaseName="
				+ dxTestDb.dbName
				+ ";useServerPrepStmts=false"
				+ (timeout != null && timeout > 0 && timeout < 60 ? ";loginTimeout=" + timeout + ";socketTimeout=" + timeout : "")
				+ (useIntegratedSecurity ? ";integratedSecurity=true" : "");
		return connectionString;
	}

}
