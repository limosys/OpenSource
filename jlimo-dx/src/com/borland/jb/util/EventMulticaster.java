//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/EventMulticaster.java,v 7.0 2002/08/08 18:40:50 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DispatchableEvent;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.*;

/**
 * <CODE>EventMulticaster </CODE>is a multicaster for all user-defined
 * events. All events, such as model and selection events, use <CODE>
 * EventMulticaster</CODE>, which sends an event to all listeners.
 * <CODE>EventMulticaster</CODE> maintains an array of listeners. The
 * <CODE>add()</CODE>, <CODE>remove()</CODE>, and <CODE>find()</CODE>
 * methods maintain this list.
 *
 * <p>The <CODE>hasListeners()</CODE> method determines whether any
 * objects are listening for events. <CODE>EventMulticaster </CODE>has
 * a <CODE>dispatch()</CODE> method and two specialized dispatch
 * methods: <CODE>exceptionDispatch()</CODE>, which is used for events
 * that can throw exceptions, and <CODE>vetoableDispatch()</CODE>,
 * which is used for events that can decline an event.
 */
public class EventMulticaster
{
/**
 * Determines if there are any listeners for events.
 * @return  <b>true</b> if one ore more event listeners are present,
 *          <b>false</b> otherwise.
 */
  public final boolean hasListeners() {
    return listeners != null;
  }

   /**
    * Sends a <code>DispatchableEvent</code> to all listeners.
    * This method is a high-speed dispatcher that does not need to be synchronized.
    * @param e    The dispatchable event sent to all listeners.
    */
  public final void dispatch(DispatchableEvent e) {
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
        e.dispatch(listenersCopy[index]);
        //Diagnostic.trace(EventMulticaster.class, "  dispatch took " + (System.currentTimeMillis()-time) + "ms");
      }
    }
  }

/**
 * Sends a <code>VetoableDispatch</code> event to all listeners. It is used for all
 * events that can throw {@link com.borland.jb.util.VetoException} and therefore decline the event.
 * This method is a high-speed dispatcher that does not need to be synchronized.
 * <p>
 * @param e   The <code>VetoableDispatch</code> event sent to all listeners.
 * @return <b>false</b> if an event listener throws a <code>VetoException</code>.
 * A return value of <b>true</b> indicates the listener accepted the event.
 */
  public final boolean vetoableDispatch(VetoableDispatch e) {
    // Synchronized not needed becuase all updates are made to a "copy"
    // of listeners.  Assumes reference assignment is atomic.
    EventListener[] listenersCopy = this.listeners;

    // Once I have a local copy of the list, don't have to worry about threads
    // adding/deleting from this list since they will make a copy of the list,
    // not modify the list.
    try {
      if (listenersCopy != null) {
        int count = listenersCopy.length;
        for (int index = 0; index < count; ++index) {
          e.vetoableDispatch(listenersCopy[index]);
        }
      }
    }
    catch (VetoException ex) {
      return false;
    }
    return true;
  }

/**
 * Sends an <code>ExceptionDispatch</code> event to all listeners.
 * It is used for all events that can throw exceptions.
 * This method is a high-speed dispatcher that does not need to be synchronized.
 * @param e     The exception event sent to all listeners.
 * @throws      Exception
 */
  public final void exceptionDispatch(ExceptionDispatch e)
    throws Exception
  {
    // Synchronized not needed becuase all updates are made to a "copy"
    // of listeners.  Assumes reference assignment is atomic.
    EventListener[] listenersCopy = this.listeners;

    // Once I have a local copy of the list, don't have to worry about threads
    // adding/deleting from this list since they will make a copy of the list,
    // not modify the list.
      if (listenersCopy != null) {
        int count = listenersCopy.length;
        for (int index = 0; index < count; ++index) {
          e.exceptionDispatch(listenersCopy[index]);
        }
      }
  }

  /* original comments
   * Simple list management that avoids synchronized/functional interface of Vector.  Key to this
   * implementation is that all changes are made to a "copy" of the original list.  This allows
   * for non-synchronized access of listners when event dispatch is called.
   */

   /**
    * Searches for the specified listener among the array of listening objects.
    * @param listener   The object being searched for in the list of listeners.
    * @return Index in the array of listeners which is equivalent to the
    *         listener object given as a parameter to this method.
    */
  public int find(EventListener listener) {
    if (listeners != null ) {
      for (int index = 0; index < listeners.length; ++index)
        if (listeners[index] == listener)
          return index;
    }
    return -1;
  }

/**
 * Adds an object to the array of listeners.
 * @param listener    The object that is added to the list of listeners for events.
 */
  public synchronized final void add(EventListener listener) {
    if (find(listener) < 0) {
      EventListener[] newListeners;

      if (listeners == null)
        newListeners = new EventListener[1];
      else {
        newListeners = new EventListener[listeners.length+1];
        System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
      }

      newListeners[newListeners.length-1] = listener;
      listeners = newListeners;  // Assumed atomic.
    }
  }

  /**
   * Removes the specified listening object from the array of event listeners.
   * @param listener The listening object that is removed from the array of event listerns.
   */
  public synchronized final void remove(EventListener listener) {
    int index = find(listener);
    if (index > -1) {
      // Important: hasListeners() expects listeners too be null if there are no listeners.
      if (listeners.length == 1)
        listeners = null;
      else {
        EventListener[] newListeners = new EventListener[listeners.length-1];
        System.arraycopy(listeners, 0, newListeners, 0, index);

        if (index < newListeners.length)
          System.arraycopy(listeners, index+1, newListeners, index, newListeners.length-index);

        listeners = newListeners;  // Assumed atomic.
      }
    }
  }

/**
 * Adds an object to the array of listeners. It is used for efficiency
 * to avoid allocating an <code>EventMulticaster</code> until there are listeners.
 * The allocated <code>EventMulticaster</code> can be tested for null to see if
 * there are any listeners, thereby further improving efficiency.
 * @param caster    The multicaster that is allocated.
 * @param listener  The object that is added to the list of listeners for events.
 * @return EventMulticaster
 */
  public final static EventMulticaster add(EventMulticaster caster, EventListener listener) {
    if (caster == null)
      caster = new EventMulticaster();
    DiagnosticJLimo.check(caster.find(listener) == -1);
    caster.add(listener);
    return caster;
  }

  /**
   * Removes the specified listening object from the array of event listeners.
   * This <code>remove</code> method is the counterpart of the the
   * {@link com.borland.jb.util.EventMulticaster#add(com.borland.jb.util.EventMulticaster, java.util.EventListener) add} method that
   * allocates an event multicaster and improves efficiency.
   * @param caster        The event multicaster object.
   * @param listener      The listening object that is removed from the array of event listeners.
   * @return  The <code>EventMulticaster</code> object given as a parameter.
   */
  public final static EventMulticaster remove(EventMulticaster caster, EventListener listener) {
    if (caster != null) {
      caster.remove(listener);
      if (!caster.hasListeners())
        caster = null;
    }
    return caster;
  }

  /**
   * Returns the number of event listeners in the array of listeners.
    */
  public int getListenerCount() {
    return listeners != null ? listeners.length : 0;
  }

  public static final EventMulticaster merge(EventMulticaster destCaster, EventMulticaster sourceCaster) {
    EventListener[] source = sourceCaster.listeners;

    if (destCaster == null)
      destCaster = new EventMulticaster();

    if (source != null) {
      for (int index = 0; index < source.length; ++index) {
        if (destCaster.find(source[index]) == -1)
          destCaster = add(destCaster, source[index]);
      }
    }
    return destCaster;
  }

  /**
   * The array of action listeners.
   */
  protected transient EventListener[] listeners;
}
