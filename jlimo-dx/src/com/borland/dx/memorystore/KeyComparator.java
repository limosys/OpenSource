//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/KeyComparator.java,v 7.0 2002/08/08 18:39:45 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;


// Do not make public.
//
final class KeyComparator {

  KeyComparator(int[] indexVector, DataColumn[] dataColumns, SortDescriptor descriptor) {
  /*
    int count = 0;

    if (descending != null) {
      for (int index = 0; index < descending.length; ++index) {
        if (descending[index]) {
          ++count;
        }
      }
    }
    return count;
  */
    this.dataColumns      = dataColumns;
    this.indexVector      = indexVector;
    this.firstColumn      = dataColumns[0];
    columnCount           = dataColumns.length;
    this.caseInsensitive  = descriptor.isCaseInsensitive();
    descending            = descriptor.getDescending();
    if (descriptor.isDescending() || (descending != null && descending.length < 1))
      descending  = null;
    else if (descending != null)
      firstDescending = descending[0];

    if (!caseInsensitive)
      enableReferenceCopy();
  }

  final void setIndexVector(int[] indexVector) { this.indexVector = indexVector; }

  private final void printIndexVector() {
    if (false) {
      for (int indexRow= 0; indexRow < indexVector.length; ++indexRow)
        DiagnosticJLimo.print(indexVector[indexRow]+" ");
      DiagnosticJLimo.println("");
    }
  }



  // Same as searchCompare only internalRow is used as a tie breaker.
  //
  final int compare(int dataRow1, int dataRow2) {
    if (caseInsensitive)
      comp  = firstColumn.compareIgnoreCase(dataRow1, dataRow2);
    else
      comp  = firstColumn.compare(dataRow1, dataRow2);

    if (comp == 0)
      secondaryCompare(columnCount, dataRow1, dataRow2, true);
    else if (firstDescending)
      comp  = -comp;
    return comp;
  }

  // Same as compare only internalRow is not used as a tie breaker.
  //
  final int searchCompare(int comparColumnCount, int dataRow1, int dataRow2) {
    if (caseInsensitive)
      comp  = firstColumn.compareIgnoreCase(dataRow1, dataRow2);
    else
      comp  = firstColumn.compare(dataRow1, dataRow2);

    if (comp == 0)
      secondaryCompare(comparColumnCount, dataRow1, dataRow2, false);
    else if (firstDescending)
      comp = -comp;

    return comp;
  }

  // Note that dataRow is an index into the data, not the indexVector.
  // this is useful for repetitive compares of the same row values (sorting) and for
  // compares of data rows that do not exist in the indexVector (locate row values).
  //
  private final void secondaryCompare(int comparColumnCount, int dataRow1, int dataRow2, boolean compareRow) {
    if (comparColumnCount > 1) {
      for (int index = 1; comp == 0 && index < comparColumnCount; ++index) {
        if (caseInsensitive)
          comp  = dataColumns[index].compareIgnoreCase(dataRow1, dataRow2);
        else
          comp  = dataColumns[index].compare(dataRow1, dataRow2);
        // Special case is when sortAsInserted index has extra column which does
        // not participate in descending
        //! bug72202.
        //
        if (descending != null && index < descending.length && descending[index])
          comp  = -comp;
      }
    }
    if (comp == 0) {
      if (referenceCopy) {
        if (dataRow1 > dataRow2)
          firstColumn.copyReference(dataRow1, dataRow2);
        else
          firstColumn.copyReference(dataRow2, dataRow1);
      }
      if (compareRow)
        comp        = dataRow1 - dataRow2;
    }
  }


  // Used for quickSort.
  //
  final void setPivot(int pivotRow) {
    this.pivotRow       = pivotRow;
    this.pivotDataRow   = indexVector[pivotRow];
    firstColumn.setPivot(indexVector, pivotDataRow);
  }

  // Used for quickSort.
  //
  final void findPivots(int leftStart, int rightStart) {
    leftPivot   = leftStart;
    rightPivot  = rightStart;

//! Diagnostic..println("leftPivot "+leftPivot+" rightPivot "+rightPivot);

    while(true) {
      leftPivot = firstColumn.forwardCompare(leftPivot, caseInsensitive, firstDescending);

      if (firstDescending)
        comp  = -firstColumn.comp;
      else
        comp  = firstColumn.comp;

      if (comp > 0)
        break;
      secondaryCompare(columnCount, indexVector[leftPivot], pivotDataRow, true);
      if (comp >= 0)
        break;
    }

    while(true) {
      rightPivot = firstColumn.reverseCompare(rightPivot, caseInsensitive, firstDescending);

      if (firstDescending)
        comp  = -firstColumn.comp;
      else
        comp  = firstColumn.comp;

      if (comp < 0)
        break;
      secondaryCompare(columnCount, indexVector[rightPivot], pivotDataRow, true);
      if (comp <= 0)
        break;
    }
  }

  // Used by insertionSort.  Must check for limit, no sentinal.
  //
  void reverseLimitedCompare() {
    if (firstDescending) {
      if (caseInsensitive) {
        while (     --rightPivot > -1
                &&  (comp = firstColumn.compareIgnoreCase(indexVector[rightPivot], pivotDataRow)) < 0)
          ;
      }
      else {
        while (     --rightPivot > -1
                &&  (comp = firstColumn.compare(indexVector[rightPivot], pivotDataRow)) < 0)
          ;
      }
      comp = -comp;
    }
    else {
      if (caseInsensitive) {
        while (     --rightPivot > -1
                &&  (comp = firstColumn.compareIgnoreCase(indexVector[rightPivot], pivotDataRow)) > 0)
          ;
      }
      else {
        while (     --rightPivot > -1
                &&  (comp = firstColumn.compare(indexVector[rightPivot], pivotDataRow)) > 0)
          ;
      }
    }
  }

  // Used for insertionSort.
  //
  final int findInsertPoint(int step, int delta, boolean compareRow) {
    setPivot(step);
    this.rightPivot       = step+delta;
//! Diagnostic..println("before while");
    while (true) {
      reverseLimitedCompare();
//! Diagnostic..println("after reverseLimited");
      if ( comp < 0)
        break;
      if (rightPivot < 0)
        break;
      secondaryCompare(columnCount, indexVector[rightPivot], pivotDataRow, compareRow);
      if (comp <= 0)
        break;
    }
    return rightPivot+1;
  }

  final void enableReferenceCopy() {
    if (firstColumn.immutable)
      referenceCopy = true;
    else
      referenceCopy = false;
  }

  final void disableReferenceCopy() {
    referenceCopy = false;
  }

  int                   leftPivot;
  int                   rightPivot;
  int                   pivotRow;
  int                   pivotDataRow;
  int                   comp;
  boolean               caseInsensitive;
  private boolean       referenceCopy;
  private int           columnCount;
  private DataColumn    firstColumn;
  private int[]         indexVector;
  private KeyComparator keyComparator;
  private DataColumn[]  dataColumns;
  private boolean[]     descending;
  private boolean       firstDescending;
}
