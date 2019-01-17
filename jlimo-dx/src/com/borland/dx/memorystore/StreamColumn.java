//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/StreamColumn.java,v 7.0 2002/08/08 18:39:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import java.sql.*;
import java.io.InputStream;

// Do not make public.
//
abstract class StreamColumn extends DataColumn {

  public StreamColumn(NullState nullState) {
    super(nullState);
    vector        = new InputStream[InitialSize];
    vectorLength  = vector.length;

  }

  final void copy(int source, int dest) {
    vector[dest]  = vector[source];
    DiagnosticJLimo.check(vector[dest] != null);
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }

   final void  grow(int newLength) {
    DiagnosticJLimo.check(newLength > vector.length);
    InputStream newVector[] = new InputStream[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector        = newVector;
    vectorLength  = vector.length;
  }

  // Technically this only provides "equal" functionality, not relative
  // comparison.  Should not rely on this implementation for "sorting.
  //
  final int compare(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }

    if (vector[index1] == vector[index2])
      return 0;

    if (vector[index1].equals(vector[index2]))
      return 0;

    return 1;
  }

  final int compareIgnoreCase(int index1, int index2) {
    DiagnosticJLimo.fail();
    return 0;
  }

  abstract void  getVariant(int index, Variant value);

  abstract void  setVariant(int index, Variant val);

   InputStream[] vector;
}
