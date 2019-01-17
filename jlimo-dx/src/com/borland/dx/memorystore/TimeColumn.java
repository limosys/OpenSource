//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/TimeColumn.java,v 7.0 2002/08/08 18:39:48 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
//!import java.util.*;
import java.sql.*;

// Do not make public.
//
class TimeColumn extends LongColumn {


  TimeColumn(NullState nullState) {
    super(nullState);
  }

  final void  getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      LocalDateUtil.setAsLocalTime(value, vector[index], null);
  }

  final void  setVariant(int index, Variant val) {
    if (val.isNull()) {
      // Force High sort.  Not perfect because its a possible value.
      //
      vector[index] = NULL_LONG;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index] = LocalDateUtil.getLocalTimeAsLong(val.getTime(), null);
    }
  }
}
