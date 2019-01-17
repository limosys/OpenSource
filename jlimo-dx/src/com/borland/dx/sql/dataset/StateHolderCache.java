//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/StateHolderCache.java,v 7.0 2002/08/08 18:39:59 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.util.*;

class StateHolderCache {

  StateHolder get(Object key) {
    if (object0 == key) return state0;
    if (object1 == key) return state1;
    if (object2 == key) return state2;
    if (object3 == key) return state3;
    if (object4 == key) return state4;
    if (hash == null)   return null;
    return (StateHolder)hash.get(key);
  }

  void put(Object key, StateHolder state) {
    if (object0 == null) { object0 = key; state0 = state; return; }
    if (object1 == null) { object1 = key; state1 = state; return; }
    if (object2 == null) { object2 = key; state2 = state; return; }
    if (object3 == null) { object3 = key; state3 = state; return; }
    if (object4 == null) { object4 = key; state4 = state; return; }
    if (hash == null)
      hash = new Hashtable();
    hash.put(key,state);
  }

  void close(Object key) {
    if (object0 == key) { object0 = null; state0.close(); state0 = null; return; }
    if (object1 == key) { object1 = null; state1.close(); state1 = null; return; }
    if (object2 == key) { object2 = null; state2.close(); state2 = null; return; }
    if (object3 == key) { object3 = null; state3.close(); state3 = null; return; }
    if (object4 == key) { object4 = null; state4.close(); state4 = null; return; }
    if (hash != null) {
      StateHolder state = (StateHolder)hash.remove(key);
      if (state != null)
        state.close();
    }
  }

  void close() {
    if (object0 != null) close(object0);
    if (object1 != null) close(object1);
    if (object2 != null) close(object2);
    if (object3 != null) close(object3);
    if (object4 != null) close(object4);
    if (hash != null) {
      Enumeration states = hash.elements();
      while (states.hasMoreElements()) {
        StateHolder state = (StateHolder)states.nextElement();
        state.close();
      }
      hash = null;
    }
  }

  private StateHolder   state0;
  private StateHolder   state1;
  private StateHolder   state2;
  private StateHolder   state3;
  private StateHolder   state4;
  private Object        object0;
  private Object        object1;
  private Object        object2;
  private Object        object3;
  private Object        object4;
  private Hashtable     hash;
}
