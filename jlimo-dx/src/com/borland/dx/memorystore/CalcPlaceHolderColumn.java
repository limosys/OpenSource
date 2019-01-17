//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/CalcPlaceHolderColumn.java,v 7.0 2002/08/08 18:39:44 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;
import java.sql.*;

// Do not make public.
//
class CalcPlaceHolderColumn extends DataColumn {

  public CalcPlaceHolderColumn(NullState nullState) {
    super(nullState);
    vectorLength  = InitialSize;
  }

  final void copy(int source, int dest) {
  }

   final void  grow(int newLength) {
    vectorLength  = newLength;
  }

  final int compare(int index1, int index2) {
    DiagnosticJLimo.fail();
    return 0;
  }

  final int compareIgnoreCase(int index1, int index2) {
    DiagnosticJLimo.fail();
    return 0;
  }

  final void  getVariant(int index, Variant value) { }

  final void  setVariant(int index, Variant val) { }
}
