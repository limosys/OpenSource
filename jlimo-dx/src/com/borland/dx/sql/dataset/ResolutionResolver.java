//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ResolutionResolver.java,v 7.0 2002/08/08 18:39:57 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;
import java.util.*;
import java.io.*;

/**
*   The ResolutionResolver class is a base class abstraction for classes designed to
*   update a data source using the standard resolution management found
*   in ResolutionManager.
*/

public abstract class ResolutionResolver extends Resolver {


  /**
  *   The method insertRow will insert the current row passed by the dataSet
  *   into the tables of the specified Database.
  */
  public abstract void insertRow(DataSet dataSet) /*-throws DataSetException-*/;

  /**
  *   The method updateRow will update the original row on the Database
  *   identified by oldRow with the changes residing in the current row
  *   of the dataSet.
  */
  public abstract void updateRow(DataSet dataSet, ReadWriteRow oldRow) /*-throws DataSetException-*/;

  /**
  *   The method deleteRow will delete the original row on the Database
  *   identified by the current row of the dataSet.
  */
  public abstract void deleteRow(DataSet dataSet) /*-throws DataSetException-*/;

  /**
  *   The method closeResources should close any open resources
  *   cached by a Resolver.
  */
  public abstract void closeResources(StorageDataSet dataSet) /*-throws DataSetException-*/;


  // *********************************************
  // ****** event management code follows ********
  // *********************************************

  /**
  *   Register a unicast listener for resolution event.
  */
  public synchronized void addResolverListener(ResolverListener listener) throws TooManyListenersException
  {
    if (listener == null)
      throw new IllegalArgumentException();

    if (resolverListener != null)
      throw new TooManyListenersException();

    resolverListener = listener;
  }

  /**
  *   De-register a unicast listener for resolution event.
  */
  public synchronized void removeResolverListener(ResolverListener listener) {
    if (resolverListener == listener)
      resolverListener = null;
    else
      throw new IllegalArgumentException();
  }

  /**
  *   This method is required by the default resolution manager in Database.
  */
  public ResolverListener fetchResolverListener() {
    return resolverListener;
  }

  private transient ResolverListener resolverListener;
  private static final long serialVersionUID = 1L;
}
