//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/NullState.java,v 7.0 2002/08/08 18:39:46 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.sql.*;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;

// Do not make public.
//
class NullState
{

  public NullState() {
    // 0 element reserved for locate operations.
    //
    lastRow = 1;
  }

  final void  grow(int newLength) {
    if (vectorLength < newLength) {
      byte newVector[] = new byte[newLength];
      if (vectorLength > 0)
        System.arraycopy(vector, 0, newVector, 0, vectorLength);
      vector        = newVector;
      vectorLength  = vector.length;
    }
  }

  final void copy(int sourceRow, int destRow, int nullMask) {
    vector[destRow]  = (byte)((vector[destRow] & ~nullMask) | (vector[sourceRow] & nullMask));
  }

  final void setNull(int row, int nullType, int nullMask, int assignedMask, int unassignedMask) {
    vector[row] &= ~nullMask;
    if (nullType == Variant.ASSIGNED_NULL)
      state =  assignedMask;
    else {
      if (nullType != Variant.UNASSIGNED_NULL) {
        DiagnosticJLimo.println("type "+nullType);
      }
      DiagnosticJLimo.check(nullType == Variant.UNASSIGNED_NULL);
      state =  unassignedMask;
    }
//! Diagnostic..println("setNull "+state);
    vector[row] |= state;
  }

  final boolean getNull(int row, Variant value, int nullMask, int assignedMask) {

    state = vector[row] & nullMask;

    if (state == 0)
      return false;

    if (state == assignedMask)
      value.setAssignedNull();
    else {
      DiagnosticJLimo.check(state != nullMask);
      value.setUnassignedNull();
    }

    return true;
  }

  final int compare(int row1, int row2, int nullMask) {
    return (vector[row1] & nullMask) - (vector[row2] & nullMask);
  }

  int               slot;
  private int       state;
  private int       lastRow;
  private int       vectorLength;
  byte[]            vector;
}
