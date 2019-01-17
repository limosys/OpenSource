//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/DataModelProvider.java,v 7.0 2002/08/08 18:39:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;

/**
  * DataModelProvider adds client-side properties for designer.
  * This class is used internally by other com.borland classes.
  * You should never use this class directly.
*/

public class DataModelProvider extends Provider {
  protected boolean executeOnOpen = true;
  protected int maxRows = 100;

  public void setExecuteOnOpen(boolean value) {
    executeOnOpen = value;
  }

  public boolean getExecuteOnOpen() {
    return executeOnOpen;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int value) {
    maxRows = value;
  }

  public void provideData(StorageDataSet dataSet, boolean toOpen) /*-throws DataSetException-*/ {
  }
}


