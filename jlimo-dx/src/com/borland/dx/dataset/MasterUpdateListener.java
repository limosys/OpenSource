//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MasterUpdateListener.java,v 7.0 2002/08/08 18:39:29 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ExceptionDispatch;
import java.util.*;

public interface MasterUpdateListener extends EventListener {
  public void masterDeleting(MasterUpdateEvent event) throws Exception;
  public void masterChanging(MasterUpdateEvent event) throws Exception;
  public void masterCanChange(MasterUpdateEvent event) throws Exception;
}
