//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/DispatchableEvent.java,v 7.0 2002/08/08 18:40:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import com.borland.jb.util.ExceptionChain;

import java.util.*;

/**
 *
 * An abstract base class for dispatchable events that
 * {@link com.borland.jb.util.EventMulticaster} can send to
 * mulitple listeners. An <code>EventMulticaster</code> can also send
 * {@link com.borland.jb.util.VetoableDispatch}
 * and {@link com.borland.jb.util.ExceptionDispatch} events,
 * but these are not dispatchable events.
 */
public abstract class DispatchableEvent extends EventObject
{

/**
 *  Constructs a <code>DispatchableEvent</code> event.
 * @param source  The Object that generates the event.
 */
  public DispatchableEvent(Object source) {
    super(source);
  }

  /**
   * An abstract dispatch method.
   * @param listener The listener the event is sent to.
   */
  public abstract void dispatch(EventListener listener);

  /**
   * Adds the specified exception to the end of the
   * exception chain for this event.
   * @param ex    An exception that occurred.
   */
  public void appendException(Exception ex) {
    if (chain == null)
      chain = new ExceptionChain();
    chain.append(ex);
  }

/**
 * Returns the exception chain for this event.
 * @return  The exception chain for this event.
 */
  public ExceptionChain getExceptionChain() { return chain; }

  /**
   *  Converts the exception to a string that contains the name
   *  of the exception and its parameter string.
   * @return The name of the exception and its parameter string.
   */
  public String toString() {
    String cn = getClass().getName();
    return cn.substring(cn.lastIndexOf('.')+1) +  "[" + paramString() + "]";
  }

  /**
   * Returns an empty string: " ". The string is the parameter
   * string for the exception.
   */
  protected String paramString() {
    return "";
  }

  private ExceptionChain  chain;
}
