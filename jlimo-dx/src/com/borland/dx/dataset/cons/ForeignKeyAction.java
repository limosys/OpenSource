//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/ForeignKeyAction.java,v 7.0 2002/08/08 18:39:40 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset.cons;

import com.borland.jb.util.DiagnosticJLimo;

import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.MatrixData;

 /**
  * Possible values for a foreign key update or delete action.
  * The meanings are as specified by the sql-92 standard.
  */
public interface ForeignKeyAction {
  static final int RESTRICT     = 0;
  static final int NO_ACTION    = RESTRICT;
  static final int CASCADE      = 1;
  static final int SET_NULL     = 2;
  static final int SET_DEFAULT  = 3;
}