//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/VetoException.java,v 7.0 2002/08/08 18:40:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

/**
 * A VetoException object is thrown when an EventListener wishes to
 * halt the multicasting of a vetoable event.
 * @see com.borland.jb.util.EventMulticaster
 * @see com.borland.jb.util.VetoableDispatch
 */
public class VetoException extends java.lang.Exception
{
  /**
   * Constructs an VetoException with no detail message.
   */
  public VetoException() {
    super();
    vetoMessage = null;
  }

/**
 * Provides the application the opportunity to pass a message for
 * default error message handling. Many event processers will
 * ignore the vetoMessage.
 * @param vetoMessage A string containing the message
 *                    for default error handling. A vetoMessage is not required.
 */
  public VetoException(String vetoMessage) {
    super(vetoMessage);
    this.vetoMessage  = vetoMessage;
  }

  /**
   * Returns the message used for default error handling.
   */
  public String getVetoMessage() {
    return vetoMessage;
  }

  private String vetoMessage;
}
