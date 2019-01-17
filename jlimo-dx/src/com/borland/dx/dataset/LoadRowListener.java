//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/LoadRowListener.java,v 7.0 2002/08/08 18:39:27 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
*/

public interface LoadRowListener extends EventListener {
  /**
      If a class implementing this interface is wired to a StorageDataSet by calling
      the StorageDataSet.addLoadRowListener() method, then the loadRow implementation
      will be called for every row that is loaded into the StorageDataSet.

      Rows are "loaded" into a StorageDataSet by calling StorageDataSet.loadRow().

      StorageDataSet.Provider implementations and DataSetData use StorageDataSet.loadRow()
      to quickly populate a StorageDataSet.

      @param status  One of the values specified in the RowStatus interface.
      @param row     The row about to be loaded into the StorageDataSet.
  */
  public void loadRow(int status, ReadWriteRow row) /*-throws DataSetException-*/;
}
