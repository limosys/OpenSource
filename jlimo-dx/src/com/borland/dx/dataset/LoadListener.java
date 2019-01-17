//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/LoadListener.java,v 7.0 2002/08/08 18:39:27 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/** This is used as a notification that a load operation on a StorageDataSet
    has been completed.  Currently load operations occur when a query/procedure
    execution and when a StorageDataSet is loaded from an import operation.
    This notification is most interesting for queries/procedures that are executed
    with asynchronous fetching, since this is done with a separate thread.
*/

public interface LoadListener extends EventListener {
  /** StorageDataSet has been loaded.
  */
  public void dataLoaded(LoadEvent event);
}
