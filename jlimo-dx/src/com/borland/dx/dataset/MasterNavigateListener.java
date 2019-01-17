//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MasterNavigateListener.java,v 7.0 2002/08/08 18:39:28 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

public interface MasterNavigateListener extends EventListener {
  public void masterNavigating(MasterNavigateEvent event) /*-throws DataSetException-*/;
  public void masterNavigated(MasterNavigateEvent event) /*-throws DataSetException-*/;
}

