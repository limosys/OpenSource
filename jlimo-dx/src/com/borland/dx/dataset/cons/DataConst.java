//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/DataConst.java,v 7.0 2002/08/08 18:39:40 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset.cons;
import com.borland.dx.dataset.RowStatus;

public interface DataConst {
  public static final int TABLE_DATA  = 1;
  public static final int AGG_DATA    = 2;
  public static final int FETCH_DATA  = 3;
  public static final int DEFAULT_STATUS        = RowStatus.DEFAULT;
  public static final int DEFAULT_HIDDEN_STATUS = RowStatus.DEFAULT_HIDDEN;
}

