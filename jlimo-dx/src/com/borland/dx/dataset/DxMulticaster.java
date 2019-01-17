//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DxMulticaster.java,v 7.0 2002/08/08 18:39:24 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DispatchableEvent;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.EventMulticaster;
import java.util.*;

/**
 *
 */
 class DxMulticaster extends EventMulticaster
{
  /**
   * High speed dispatcher that does not need to be synchronized.
   */
  public final void dxDispatch(DxDispatch e)
    /*-throws DataSetException-*/
  {
    // Synchronized not needed becuase all updates are made to a "copy"
    // of listeners.  Assumes reference assignment is atomic.
    //
    EventListener[] listenersCopy = this.listeners;

    // Once I have a local copy of the list, don't have to worry about threads
    // adding/deleting from this list since they will make a copy of the list,
    // not modify the list.
    //
    if (listenersCopy != null) {
      int count = listenersCopy.length;
      for (int index = 0; index < count; ++index) {
        // WARNING:  Please don't checkin diagnostics here.  It slows my smoke
        // test down enormously.  Test that usually takes < 2 minutes, takes 15 minutes
        // when one of the diagnostics below was left in.  (SteveS).
        //
        //long time = System.currentTimeMillis();
        //Diagnostic.trace(EventMulticaster.class, "->dispatch e=" + e + " => " + listenersCopy[index]);
        e.dxDispatch(listenersCopy[index]);
        //Diagnostic.trace(EventMulticaster.class, "  dispatch took " + (System.currentTimeMillis()-time) + "ms");
      }
    }
  }

  /**
   * Used for efficiency to avoid allocating an EventMultiCaster until (or if) there are
   * any listeners.  Also allows the caller to be slightly more efficient about whether there
   * are any listeners because the EventMultiCaster member can be tested for null to see if
   * there are any listeners.
   */
  public final static DxMulticaster add(DxMulticaster caster, EventListener listener) {
    if (caster == null)
      caster = new DxMulticaster();
    DiagnosticJLimo.check(caster.find(listener) == -1);
    caster.add(listener);
    return caster;
  }

  public final static DxMulticaster remove(DxMulticaster caster, EventListener listener) {
    if (caster != null) {
      caster.remove(listener);
      if (!caster.hasListeners())
        caster = null;
    }
    return caster;
  }
}
