//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/DataColumn.java,v 7.0 2002/08/08 18:39:44 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;
import java.io.*;
import java.math.*;
import java.sql.*;

// Not public.  Not used outside package.
//
abstract class DataColumn {


  // This gets Nulls to the higher end of a sort.  Needed
  // by quick sort compares that ignore null state.  Insertion
  // sort that follows a quick sort will used null state for final
  // sort positioning.
  //
   static final byte NULL_BYTE            = Byte.MAX_VALUE;
   static final short NULL_SHORT          = Short.MAX_VALUE;
   static final int NULL_INT              = Integer.MAX_VALUE;
   static final float NULL_FLOAT          = Float.MAX_VALUE;
   static final double NULL_DOUBLE        = Double.MAX_VALUE;
   static final long  NULL_LONG           = Long.MAX_VALUE;
   static final String NULL_STRING        = "\u7FFF\u7FFF\u7fff";  //NORES
   static final InputStream NULL_STREAM   = new ByteArrayInputStream(new byte[1]);

//!   static final java.util.Date nullDate  = new java.util.Date(130, 0, 0);
//!   static final Time nullTime            = new Time(23, 59, 59);
//!   static final Timestamp nullTimestamp  = new Timestamp(130, 0, 0, 0, 0, 0, 0);
   static final BigDecimal nullBigDecimal   = new BigDecimal("999999999999999999");  //NORES

            static final int  InitialSize       = 16;

            int       lastRow;
   int       vectorLength;

            boolean   immutable;

   boolean   hasNulls;
   NullState nullState;
   int       assignedMask;
   int       unassignedMask;
   int       nullMask;

  // Used for quick sort.
  //
            int       comp;
   int[]     indexVector;
   int       pivotDataRow;

  DataColumn(NullState nullState) {

    this.nullState  = nullState;
    if (nullState != null) {
      assignedMask    = 1 << nullState.slot;
      unassignedMask  = 2 << nullState.slot;
      nullMask        = (assignedMask|unassignedMask);
    }

//! Diagnostic..println("assignedMask "+Integer.toString(assignedMask, 16));
//! Diagnostic..println("unassigndedMask "+Integer.toString(unassignedMask, 16));
//! Diagnostic..println("nullMask "+Integer.toString(nullMask, 16));
//! Diagnostic..println("");

    // 0 element reserved for locate operations.
    //
    lastRow    = 1;
  }

  static int getNewSize(int oldSize) {
    if (oldSize < InitialSize)
      oldSize = 16;
    if (oldSize < 0xFFFF)
      return oldSize * 2;
    return oldSize + 0xFFFF;
  }

  final void  growTo(int newLength, int lastRow) {
    DiagnosticJLimo.check(newLength >= lastRow);
    if (vectorLength < newLength){
      grow(newLength);
      if (hasNulls)
        nullState.grow(newLength);
    }

    if (this.lastRow < lastRow && nullState != null) {
      Variant nullValue = new Variant();
      nullValue.setUnassignedNull();
//!      Diagnostic.println("lastRow: "+this.lastRow+" "+lastRow+" newLength:  "+newLength);
      for (int row = this.lastRow; row <= lastRow; ++row) {
        append();
        setVariant(row, nullValue);
      }
      this.lastRow = lastRow;
    }
  }

  final void append() {
    if (++lastRow >= vectorLength) {
      grow(getNewSize(vectorLength));
      if (hasNulls)
        nullState.grow(vectorLength);
    }
  }

  final void setNull(int row, int nullType) {
    if (!hasNulls) {
      nullState.grow(vectorLength);
      hasNulls  = true;
    }
    nullState.setNull(row, nullType, nullMask, assignedMask, unassignedMask);
  }

  final boolean isNull(int row) {
    if (!hasNulls)
      return false;

    return (nullState.vector[row] & nullMask) != 0;
  }

  boolean partialCompare(int row1, int row2, boolean caseInsensitive) {
    DiagnosticJLimo.fail();
    return false;
  }

  void copyReference(int source, int dest) {
    DiagnosticJLimo.fail();
  }

  // Used for quickSort.
  //
  void setPivot(int indexVector[], int pivotDataRow) {
    this.indexVector  = indexVector;
    this.pivotDataRow = pivotDataRow;
  }
  // Used for quickSort.
  //
  int forwardCompare(int leftPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      while ((comp = compare(indexVector[++leftPivot], pivotDataRow)) > 0)
        ;
    }
    else {
      while ((comp = compare(indexVector[++leftPivot], pivotDataRow)) < 0)
        ;
    }
    return leftPivot;
  }
  // Used for quickSort.
  //
  int reverseCompare(int rightPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      while ((comp = compare(indexVector[--rightPivot], pivotDataRow)) < 0)
        ;
    }
    else {
      while ((comp = compare(indexVector[--rightPivot], pivotDataRow)) > 0)
        ;
    }
    return rightPivot;
  }



   abstract void     grow(int length);
            abstract void     copy(int source, int dest);
            abstract int      compare(int row1, int row2);
            abstract int      compareIgnoreCase(int row1, int row2);

            abstract void     getVariant(int row, Variant val);
            abstract void     setVariant(int row, Variant val);
}
