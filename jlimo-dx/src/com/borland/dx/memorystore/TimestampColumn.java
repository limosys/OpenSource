//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/TimestampColumn.java,v 7.0 2002/08/08 18:39:48 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.*;
import java.sql.*;

// Do not make public.
//
class TimestampColumn extends LongColumn {

  TimestampColumn(NullState nullState) {
    super(nullState);
    nanoVector  = new int[InitialSize];
  }

  final void copy(int source, int dest) {
    super.copy(source, dest);
    nanoVector[dest]  = nanoVector[source];
  }

   final void  grow(int newLength) {
    super.grow(newLength);
    DiagnosticJLimo.check(newLength > nanoVector.length);
    int newNanoVector[] = new int[newLength];
    System.arraycopy(nanoVector, 0, newNanoVector, 0, nanoVector.length);
    nanoVector        = newNanoVector;
    DiagnosticJLimo.check(nanoVector.length == vectorLength);
  }

  final int compare(int index1, int index2) {
    intResult  = super.compare(index1, index2);
    if (intResult == 0)
      return nanoVector[index1] - nanoVector[index2];
    return intResult;
  }

  final int compareIgnoreCase(int index1, int index2) {
    return compare(index1, index2);
  }

  final void  getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      value.setTimestamp(vector[index], nanoVector[index]);
  }

  void  setVariant(int index, Variant val) {
    if (val.isNull()) {
      // Force High sort.  Not perfect because its a possible value.
      //
      vector[index]     = NULL_LONG;
      nanoVector[index] = NULL_INT;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index]     = val.getAsLong();
      nanoVector[index] = val.getTimestamp().getNanos();
    }
  }


  int[]       nanoVector;
  int         intResult;
}
