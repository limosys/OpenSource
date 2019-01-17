//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/VetoableDispatch.java,v 7.0 2002/08/08 18:40:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import com.borland.jb.util.VetoException;

import java.util.*;

/**
 *
 * The VetoableDispatch interface is an interface for vetoable events
 * that {@link com.borland.jb.util.EventMulticaster} can send to multiple listeners.
 * A listener for a vetoable event can choose to not respond to the event,
 * thereby refusing or vetoing it.
 * @see com.borland.jb.util.ExceptionDispatch
 */
public interface VetoableDispatch
{

/**
 * Sends an event to the specified listener.
 * The listener can choose to ignore the event and not respond to it
 * by throwing {@link com.borland.jb.util.VetoException}.
 * @param listener        The object listening for the event.
 * @throws VetoException
 */
  public void vetoableDispatch(EventListener listener) throws VetoException;
}
