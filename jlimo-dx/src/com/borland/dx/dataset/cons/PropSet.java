//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/PropSet.java,v 7.0 2002/08/08 18:39:41 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;
import com.borland.dx.dataset.RowStatus;

public interface PropSet {
  final static int Opened             = 0x1;
  final static int Resolvable         = 0x2;
  final static int TableName          = 0x4;
  final static int StoreClassFactory  = 0x8;
  final static int ResolveOrder       = 0x10;
  final static int Locale             = 0x20;
}

