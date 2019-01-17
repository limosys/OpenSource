//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/NavigationListener.java,v 7.0 2002/08/08 18:39:30 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

/**
 * This interface is used as a notification that current row position has changed,
 * or navigated, from a row. This is used for data-aware controls so that the UI
 * can respond to the change in cursor location. For example, a status bar can
 * change its display from "1 of 10" to "2 of 10" when the user moves from the
 * first row to the second.
 */
public interface NavigationListener extends EventListener
{

/**
 * This is an event to notify listeners that the current row has changed,
 * that a user has moved from one row to another.
 *
 * @param event   The event that called the listener, indicating in this
 *                case that the current row has been changed.
 */
  public void navigated(NavigationEvent event);
}
