//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/NavigationEvent.java,v 7.0 2002/08/08 18:39:30 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DispatchableEvent;

import java.util.*;

/**
 * This class is used to provide notification that the DataSet's cursor position has
 * changed. This class is used by data-aware controls so that the UI can respond
 * to the change in cursor location.
 * <p>
 * This event is sent out any time the current row position changes due to navigation
 * operations like first, last, next, prior, and for editing operations like
 * deleteRow and insertRow. Also note that post() can cause navigation since post()
 * will navigate the newly posted row to its correct position in the DataSet.
 * For sorted DataSets the posted position is determined by sort order.
 * For non-sorted DataSets the posted position is at the end of the DataSet.
 */
public class NavigationEvent extends com.borland.jb.util.DispatchableEvent {

/**
 * Creates a NavigationEvent object.
 *
 * @param source
 */
  public NavigationEvent(Object source) {
    super(source);
  }

  /**
   *  This method is an implementation of DispatchableEvent that an
   *  EventMulticaster uses to dispatch an event of this type to the listener.
   *
   * @param listener    The listener to dispatch this event to.
   * @see   com.borland.jb.util.DispatchableEvent
   * @see   com.borland.jb.util.EventMulticaster
   */
  public void dispatch(EventListener listener) {
    ((NavigationListener)listener).navigated(this);
  }

}
