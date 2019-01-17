//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/TableDataSetAnalyzer.java,v 7.0 2002/08/08 18:40:08 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;

import com.borland.dx.dataset.*;

class TableDataSetAnalyzer extends DataSetAnalyzer
{
  public TableDataSetAnalyzer(TableDataSet dataSet) {
    this.table = dataSet;
  }

  /**
  *  Returns the number of available columns in a DataSet.
  */
  public int getColumnCount() {
    if (table.isOpen())
      return table.getColumnCount();
    try {
          DataFile dataFile = table.getDataFile();
          if (dataFile != null) {
            dataFile.loadMetaData(table);
          }
          return table.getColumnCount();
    }
    catch (Exception ex) {
    }
    return 0;
  }

  /**
  *  Returns a Column component for given column index.
  */
  public Column getColumn(int ordinal) throws MetaDataException {
    try {
      return table.getColumn(ordinal);
    }
    catch (DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
      return null;
    }
  }

  TableDataSet  table;
}
