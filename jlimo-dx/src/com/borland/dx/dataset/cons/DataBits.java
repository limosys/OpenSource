//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/DataBits.java,v 7.0 2002/08/08 18:39:40 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;
import com.borland.dx.dataset.RowStatus;

public interface DataBits {
  public final static int  HIDDEN_BIT         = 0x80;
  public final static int  ROWID_BIT          = 0x40;
  public final static int  NOT_RESOLVABLE_BIT = 0x20;
  public final static int  DATATYPE_MASK      = 0x1F;
  public final static int  NOT_NULL           = 0x0;
  public final static int  ASSIGNED_NULL      = 0x1;
  public final static int  UNASSIGNED_NULL    = 0x2;
  public final static int  UNCHANGED_NULL     = 0x3;
  public final static int  NULL_MASK          = 0x3;
  public final static int  EACH_ROW           = 0;
  public final static int  UPDATED_ROWS       = 1;
  public final static int  DELETED_ROWS       = 2;
  public final static int  INSERTED_ROWS      = 3;

  public final static String  INTERNALROW     = "INTERNALROW"; //NORES
}

