//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/StatusEvent.java,v 7.0 2002/08/08 18:39:36 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DispatchableEvent;

import java.util.*;

/**
    Status event used to inform any listeners of interesting messages.
    Typically these are informative messages, but listener may want to
    take some action for some status messages.  If so, the getCode() method
    can be used to determine what type of message is being sent.
*/
public class StatusEvent extends com.borland.jb.util.DispatchableEvent {

  /** A message about loading rows in a DataSet. */
  public final static int   LOADING_DATA            = 1;
  /** Sent by DataSet.interactiveLocate().  Use enter key to start the locate operation.
  */
  public final static int   LOCATE_USE_ENTER        = 2;
  /** Sent by DataSet.interactiveLocate().  Locate operation found a match.
  */
  public final static int   LOCATE_MATCH_FOUND      = 3;
  /** Sent by DataSet.interactiveLocate().  Locate operation did not find a match.
  */
  public final static int   LOCATE_MATCH_NOT_FOUND  = 4;
  /** Sent by DataSet.interactiveLocate().  Enter a value.  Use mixed case characters for a case sensitive search.
  */
  public final static int   LOCATE_USE_MIXED_CASE   = 5;
  /** Sent by DataSet.interactiveLocate().  For string columns:  Enter a value and press enter to begin search.
  */
  public final static int   LOCATE_STRING           = 6;
  /** Sent by DataSet.interactiveLocate().  For non string columns:  Enter a value and press enter to begin search.
  */
  public final static int   LOCATE_NON_STRING       = 7;
  /** Sent by DataSet.interactiveLocate().  Use enter key to start the locate operation.
  */
  public final static int   DATA_CHANGE             = 8;
  /** Sent by DataSet.interactiveLocate().  Use enter key to start the locate operation.
  */
  public final static int   EXCEPTION               = 9;
  /** Sent by DataSet.clearStatus() method to clear status (ie a status bar control).
  */
  public final static int   CLEAR                   = 10;
  /** Entered edit state for new or existing row of a DataSet.
  */
  public final static int   EDIT_STARTED            = 11;
  /** Cancelled edit for new or existing row of a DataSet.
  */
  public final static int   EDIT_CANCELED           = 12;
  /**
      @since JB2.0
      Long running sorts send this status notification.
  */
  public final static int   SORTING                 = 13;
  /**
      @since JB2.0
      Long running restructure operations send this status notification.
  */
  public final static int   RESTRUCTURING           = 14;

  /**
      @since JB2.0
      Checking to see if DataStore was not closed properly.  This check can
      take 7-10 seconds.
  */
  public final static int   CHECKING_DATASTORE   = 15;

  public StatusEvent(Object source, int code, String message) {
    super(source);
    this.code     = code;
    this.message  = message;
    this.exception = null;
  }

  public StatusEvent(Object source, Throwable ex) {
    super(source);
    this.code     = EXCEPTION;
    this.exception = ex;
    this.message  = ex.getMessage();
  }

  public void dispatch(EventListener listener) {
    ((StatusListener)listener).statusMessage(this);
  }

  /** Get status message. */
  public final String getMessage() { return message; }

  /** Get status message. */
  public final void setMessage(String message) {
    this.message = message;
  }

  /** Get code that indicates the type of message. */
  public final int getCode() { return code; }

  /** Get the exception which caused the message.  Null unless code = EXCEPTION */
  public final Throwable getException() { return exception; }

  private String  message;
  private int     code;
  private Throwable exception;
}

