package com.borland.dx.sql.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.MetaDataUpdate;
import com.borland.dx.dataset.StorageDataSet;

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

}
