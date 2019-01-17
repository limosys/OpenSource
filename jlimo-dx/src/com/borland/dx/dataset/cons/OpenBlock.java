//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/OpenBlock.java,v 7.0 2002/08/08 18:39:40 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;
import com.borland.dx.dataset.RowStatus;

public interface OpenBlock {

  final static int ReadOnly        =    0x1;
  final static int Restructuring   =    0x2;
  final static int Restructure     =    0x4;
  final static int MetaDataMissing =    0x8;
  final static int NotOpen         =   0x10;
  final static int Corrupt         =   0x20;
  final static int DataNull        =   0x40;  // used for restructure.
  final static int NeedsRecalc     =   0x80;
  // Resolve in progress.
  //
  final static int Resolve         =  0x100;

  final static int StoreReadOnly   =  0x200;
  final static int StoreClass      =  0x400;
  final static int Closing         =  0x800;
}

