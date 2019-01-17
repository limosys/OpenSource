//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ExceptionEvent.java,v 7.0 2002/08/08 18:39:25 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DispatchableEvent;
import java.awt.Component;

import java.util.*;

/**
 * This class is used to override the DataSet error handling for data-aware controls.
 */
public class ExceptionEvent extends com.borland.jb.util.DispatchableEvent {

/**
 * Constructs an ExceptionEvent object.
 *
 * @param dataSet     The DataSet being accessed when this error occurred.
 * @param component   The Component on which the exception occurred.
 * @param ex          The Exception that occurred.
 */
  public ExceptionEvent(DataSet dataSet, Component component, Throwable ex) {
    // JDK no longer likes null sources.  If this is removed,
    // handleException() methods in DataSetException will need
    // to be deprecated.
    //
    super(dataSet == null?(Object)"":(Object)dataSet);
    this.ex = ex;
    this.component = component;
  }

  /**
   * This method is an implementation of DispatchableEvent that an EventMulticaster
   * uses to dispatch an event of this type to the listener.
   *
   * @param listener    The listener to dispatch this event to.
   * @see com.borland.jb.util.DispatchableEvent
   * @see com.borland.jb.util.EventMulticaster
   */
  public void dispatch(EventListener listener) {
    ((ExceptionListener)listener).exception(this);
  }

  /**
   * Exception that occurred.
   * @return
   */
  public Throwable  getException() { return ex; }

  /**
   * DataSet that was being accessed when the exception occurred. Can be <b>null</b>.
   * @return
   */
  public DataSet    getDataSet() { return (DataSet)source; }

  /**
   * Component that was being accessed when the exception occurred. Can be <b>null</b>.
   * @return
   */
  public Component  getComponent() { return component; }

  private Component component;
  private Throwable ex;

}
