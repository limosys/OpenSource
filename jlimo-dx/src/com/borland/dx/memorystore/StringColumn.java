//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/StringColumn.java,v 7.0 2002/08/08 18:39:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import java.sql.*;
import java.lang.String;
import java.lang.Character;

// Do not make public.
//

class StringColumn extends DataColumn
{
  public StringColumn(NullState nullState) {
    super(nullState);
    vector          = new String[InitialSize];
    vectorLength    = vector.length;
    immutable       = true;
  }

  final void copy(int source, int dest) {
    vector[dest] = vector[source];
    DiagnosticJLimo.check(vector[dest] != null);
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }

  final void copyReference(int source, int dest) {
    vector[dest] = vector[source];
  }

   void  grow(int newLength) {
    DiagnosticJLimo.check(newLength > vector.length);
    String newVector[] = new String[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector        = newVector;
    vectorLength  = vector.length;
  }

  int compare(int index1, int index2) {
//if (d.verboseLocate) Diagnostic.println(vector[index1]+" "+vector[index2] + " " + vector[index1].compareTo(vector[index2]));
//if (d.verboseLocate) Diagnostic.println(" "+index1+" "+index2);
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    if (vector[index1] == null) DiagnosticJLimo.println("null compare1 "+index1);
    if (vector[index2] == null) DiagnosticJLimo.println("null compare2 "+index2);
    return vector[index1].compareTo(vector[index2]);
  }

  int compareIgnoreCase(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    return compareIgnoreCase(index1, vector[index2]);
  }

  int compareIgnoreCase(int index1, String string2) {
    String  string1  = vector[index1];
    int     length1  = string1.length();
    int     length2  = string2.length();
    int     length;
    int     comp;
    if (length1 > length2)
      length  = length2;
    else
      length  = length1;

    for (int index = 0; index < length; ++index) {
      comp  =   Character.toUpperCase(string1.charAt(index))
              - Character.toUpperCase(string2.charAt(index));
      if (comp != 0)
        return comp;
    }

    if (length1 == length2)
      return 0;

    return length1 - length2;
  }

  // Used for quickSort.
  //
  void setPivot(int indexVector[], int pivotDataRow) {
    this.indexVector  = indexVector;
    this.pivotValue   = vector[pivotDataRow];
  }
  // Used for quickSort.  Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  int forwardCompare(int leftPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) > 0)
          ;
      }
      else {
        while ((comp = vector[indexVector[++leftPivot]].compareTo(pivotValue)) > 0)
          ;
      }
    }
    else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) < 0)
          ;
      }
      else {
        while ((comp = vector[indexVector[++leftPivot]].compareTo(pivotValue)) < 0)
          ;
      }
    }
    return leftPivot;
  }
  // Used for quickSort.  Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  int reverseCompare(int rightPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) < 0)
          ;
      }
      else {
        DiagnosticJLimo.check(rightPivot > 0);
        while ((comp = vector[indexVector[--rightPivot]].compareTo(pivotValue)) < 0)
          DiagnosticJLimo.check(rightPivot > 0);
      }
    }
    else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) > 0)
          ;
      }
      else {
        while ((comp = vector[indexVector[--rightPivot]].compareTo(pivotValue)) > 0)
          ;
      }
    }
    return rightPivot;
  }


  final boolean partialCompare(int index1, int index2, boolean caseInsensitive) {

//! Diagnostic..println(vector[index1]+" "+ vector[index2]);

    return vector[index1].regionMatches(  caseInsensitive, 0,
                                          vector[index2], 0,
                                          vector[index2].length()
                                       );
  }

  final void getVariant(int row, Variant value) {
    if (hasNulls && vector[row] == NULL_STRING) {
      nullState.getNull(row, value, nullMask, assignedMask);
      DiagnosticJLimo.check(value.isNull());
    }
    else {
      if (hasNulls)
        nullState.vector[row] &= ~nullMask;
      value.setString(vector[row]);
    }
  }

  void setVariant (int index, Variant val) {
    if (val.isNull()) {
      vector[index] = NULL_STRING;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index]  = val.getString();
      if (vector[index] == null) {
        vector[index] = NULL_STRING;
        setNull(index, Variant.ASSIGNED_NULL);
      }
    }
  }

  String        pivotValue;
  String[]      vector;
}
