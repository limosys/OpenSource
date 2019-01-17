//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MasterNavigateEvent.java,v 7.0 2002/08/08 18:39:28 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DispatchableEvent;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.*;

public class MasterNavigateEvent extends com.borland.jb.util.DispatchableEvent
  implements DxDispatch
{
  public static final int NAVIGATED   = 1;
  public static final int NAVIGATING  = 2;

  public MasterNavigateEvent(Object source, boolean canceling, int id) {
    super(source);
    this.id         = id;
    this.canceling  = canceling;
  }

  public void dxDispatch(EventListener listener)
    /*-throws DataSetException-*/
  {
    switch(id) {
      case NAVIGATING:
        ((MasterNavigateListener)listener).masterNavigating(this);
        break;
      case NAVIGATED:
      ((MasterNavigateListener)listener).masterNavigated(this);
        break;
      default:
        DiagnosticJLimo.fail();
    }
  }

  public void dispatch(EventListener listener)
  {
    DiagnosticJLimo.fail();
  }

  int     id;
  boolean canceling;
}
