//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/DoubleColumn.java,v 7.0 2002/08/08 18:39:45 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;
import java.sql.*;

// Not public.
//
class DoubleColumn extends DataColumn {

  public DoubleColumn(NullState nullState) {
    super(nullState);
    vector        = new double[InitialSize];
    vectorLength  = vector.length;
  }

  final void copy(int source, int dest) {
    vector[dest]            = vector[source];
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }

   final void  grow(int newLength) {
    DiagnosticJLimo.check(newLength > vector.length);
    double newVector[] = new double[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector        = newVector;
    vectorLength  = vector.length;
  }

  final int compare(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    result = vector[index1] - vector[index2];
    if (result < 0)
      return -1;
    if (result > 0)
      return 1;
    return 0;
  }

  final int compareIgnoreCase(int index1, int index2) {
    return compare(index1, index2);
  }

  final void  getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      value.setDouble(vector[index]);
  }

  final void  setVariant(int index, Variant val) {
    if (val.isNull()) {
      // Force High sort.  Not perfect because its a possible value.
      //
      vector[index] = NULL_DOUBLE;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index]  = val.getDouble();
    }
  }

  double          result;
  double[]        vector;
}
