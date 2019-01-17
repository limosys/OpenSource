//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/LongColumn.java,v 7.0 2002/08/08 18:39:45 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;
import java.sql.*;

// Not public.
//
class LongColumn extends DataColumn {

  public LongColumn(NullState nullState) {
    super(nullState);
    vector        = new long[InitialSize];
    vectorLength  = vector.length;
  }

  void copy(int source, int dest) {
    vector[dest]            = vector[source];
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }

   void  grow(int newLength) {
    DiagnosticJLimo.check(newLength > vector.length);
    long newVector[] = new long[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector        = newVector;
    vectorLength  = vector.length;
  }

  int compare(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    long val1 = vector[index1];
    long val2 = vector[index2];
    if (val1 < val2)
      return -1;
    if (val1 > val2)
      return 1;
    return 0;
  }

  int compareIgnoreCase(int index1, int index2) {
    return compare(index1, index2);
  }

  void  getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      value.setLong(vector[index]);
  }

  void  setVariant(int index, Variant val) {
    if (val.isNull()) {
      // Force High sort.  Not perfect because its a possible value.
      //
      vector[index] = NULL_LONG;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index]  = val.getLong();
    }
  }

  long          result;
  long[]        vector;
}
