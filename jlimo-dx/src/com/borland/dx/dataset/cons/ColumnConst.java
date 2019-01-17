//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/ColumnConst.java,v 7.2.2.2 2004/10/15 19:54:30 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset.cons;
import com.borland.dx.dataset.RowStatus;

public interface ColumnConst {
  public final static int  FORMATTER_SET   = 0x00000001;
  public final static int  EDITMASKER_SET  = 0x00000002;
  public final static int  NOW_DEFAULT     = 0x00000004;
  public final static int  FIXED_PRECISION = 0x00000008;
  public final static int  HIDDEN          = 0x00000010;
  public final static int  SEARCHABLE      = 0x00000020;
  public final static int  REQUIRED        = 0x00000040;
  public final static int  PERSIST         = 0x00000080;
  public final static int  CURRENCY        = 0x00000100;
  // disallows edits from a UI control
  //
  public final static int  EDITABLE        = 0x00000200;
  public final static int  ROWID           = 0x00000800;
  public final static int  ALIGNMENT_SET   = 0x00001000;
  public final static int  CAPTION_SET     = 0x00002000;

  public final static int  IN_VALIDATE     = 0x00004000;
  public final static int  IN_CHANGED      = 0x00008000;

  public final static int  AUTOINCREMENT   = 0x00010000;
  public final static int  PRIMARY_KEY     = 0x00020000;

  public final static int  EXPORT_FORMATTER_SET
                                              = 0x00040000;
  public final static int  PRECISION_SET      = 0x00080000;
  public final static int  SCALE_SET          = 0x00100000;
  public final static int  FORCE_AUTO_INC     = 0x00200000;
  public final static int  CLUSTERED          = 0x00400000;
}

