//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/ExceptionDispatch.java,v 7.0 2002/08/08 18:40:51 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.util.*;

/**
 * The ExceptionDispatch interface is an interface for events
 * that {@link com.borland.jb.util.EventMulticaster} can send to multiple listeners.
 * The event can throw exceptions
 */
public interface ExceptionDispatch
{
/**
 * Sends an event that to the specified listener.
 * @param listener    The object listening for the event.
 * @throws Exception
 */
  public void exceptionDispatch(EventListener listener) throws Exception;
}
