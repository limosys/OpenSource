//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/DefaultDataSetAnalyzer.java,v 7.0 2002/08/08 18:40:06 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.*;

class DefaultDataSetAnalyzer extends DataSetAnalyzer
{
  public DefaultDataSetAnalyzer(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  /**
  *  Returns the number of available columns in a DataSet.
  */
  public int getColumnCount() {
    wasOpen = dataSet.isOpen();
    if (!wasOpen) {
          try {
            dataSet.open();
          }
          catch (Exception ex) {
            return 0;
          }
        }
    return dataSet.getColumnCount();
  }

  /**
  *  Returns a Column component for given column index.
  */
  public Column getColumn(int ordinal) throws MetaDataException {
    try {
      if (dataSet.isOpen())
        return dataSet.getColumn(ordinal);
    }
    catch (DataSetException ex) {
      MetaDataException.rethrowDataSetException(ex);
    }
    return null;
  }

  public void close() {
    if (dataSet.isOpen() && !wasOpen) {
          try {
            dataSet.close();
          }
          catch (Exception ex) {
          }
    }
  }

  boolean       wasOpen;
  DataSet   dataSet;
}
