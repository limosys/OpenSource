//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/QueryDataSetAnalyzer.java,v 7.0 2002/08/08 18:40:08 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;
import com.borland.dx.dataset.Variant;
import java.lang.*;
import java.util.*;
import java.sql.*;

class QueryDataSetAnalyzer extends DataSetAnalyzer
{
  public QueryDataSetAnalyzer(StorageDataSet dataSet) {
    this.query = dataSet;
  }

  /**
  *  Returns the number of available columns in a DataSet.
  */
  public int getColumnCount() {
    if (query.isOpen())
      return query.getColumnCount();
    try {
      if (columns == null)
        analyze();
      return columns.size();
    }
    catch (DataSetException ex) {
    }
    return 0;
  }

  /**
  *  Returns a Column component for given column index.
  */
  public Column getColumn(int ordinal) throws MetaDataException {
    try {
      if (query.isOpen())
        return query.getColumn(ordinal);
      if (columns == null)
        analyze();
      return (Column)columns.elementAt(ordinal);
    }
    catch (DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
      return null;
    }
  }

  private void addColumns(UniqueQueryAnalyzer analyzer, Vector cols, boolean hidden, Vector rowids)
    /*-throws DataSetException-*/
  {
    if (cols == null)
      return;
    boolean useSchemaName = database.isUseSchemaName();
    String  defSchemaName = analyzer.getDefaultSchemaName();

    for (int i=0; i<cols.size(); ++i) {
      SQLElement column = (SQLElement)cols.elementAt(i);
      SQLElement table  = analyzer.tableFromColumn(column);
      String     tableName  = table.getName();
      String     schemaName = table.getPrefixName();
      String     usedSchema = schemaName;
      String     name       = column.getLabelName();
      if (usedSchema == null && useSchemaName)
        usedSchema = defSchemaName;

      Column col = metadata.createColumn(usedSchema,tableName,name);
      col.setTableName(tableName);
      col.setSchemaName(schemaName);
      col.setColumnName(column.getLabelName());
      col.setCaption(column.getLabelName());
      if (hidden) {
        col.setHidden(true);
        col.setRowId(true);
      }
      else if (rowids != null && rowids.contains(column)) {
        col.setRowId(true);
      }
      columns.addElement(col);
    }
  }

  private void analyze() throws MetaDataException {
    try {
      QueryDescriptor descriptor;
      if (query instanceof QueryDataSet)
        descriptor = ((QueryDataSet)query).getQuery();
      else
        descriptor = ((QueryProvider)query.getProvider()).getQuery();
      if (descriptor == null || descriptor.getDatabase() == null || descriptor.getQueryString() == null)
        DataSetException.badQueryProperties();
      database = descriptor.getDatabase();
      String queryString = descriptor.getQueryString();
      if (database == null || queryString == null)
        DataSetException.badQueryProperties();
      this.metadata = MetaData.getMetaData(database);

      UniqueQueryAnalyzer analyzer = new UniqueQueryAnalyzer( database, queryString );
      if ((query.getMetaDataUpdate() & MetaDataUpdate.ROWID) == 0)
        analyzer.analyzeTableName();
      else {
        analyzer.analyze();
        Vector bestRowId = analyzer.getBestRowId();
                if (bestRowId != null)
          analyzer.setBestRowId(bestRowId);
      }
      Vector added = analyzer.getAddedColumns();
      Vector listed = analyzer.getColumnList();
      Vector rowids = analyzer.getCurrentRowId();
      int count = (added != null ? added.size() : 0) +
                  (listed != null ? listed.size() : 0);
      columns = new Vector(count);
      addColumns(analyzer,added,true,rowids);
      addColumns(analyzer,listed,false,rowids);
    }
    catch (SQLException sex) {
      MetaDataException.rethrowSQLException(sex);
    }
    catch (DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
  }

  Vector          columns;
  StorageDataSet  query;
  MetaData        metadata;
  Database        database;
}
