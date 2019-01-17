//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Locate.java,v 7.0 2002/08/08 18:39:27 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------


package com.borland.dx.dataset;

public interface Locate
{
  /** Allow partial matches for String columns.
  */
  public static final int PARTIAL         = 0x1;
  /** Search from current dataSet position.
  */
  public static final int NEXT            = 0x2;
  /** Search backwards from current dataSet postion.
  */
  public static final int PRIOR           = 0x4;
  /** CaseInsensitive search for String Columns.
  */
  public static final int CASE_INSENSITIVE = 0x8;
  /* Locate closest match. NOT SUPPORTED YET.
  public static final int CLOSEST         = 0x10;
  */

  /** Locate first occurance.
  */
  public static final int FIRST           = 0x20;
  /** Locate Last occurance
  */
  public static final int LAST            = 0x40;
  /** Fast semantics.  Search values not initialized for
      Next/Prior operations.  Values used from previous
      search.
  */
  public static final int FAST            = 0x80;
         // Used internally to force scoping of the search.
         //
         static final int DETAIL          = 0x100;

  public static final int NEXT_FAST       = NEXT|FAST;
  public static final int PRIOR_FAST      = PRIOR|FAST;


  static final        int START_MASK      = (FIRST|LAST|NEXT|PRIOR);
//!  static String toString(int mask) {
//!    String ret  = Integer.toHexString(mask);
//!    if ((mask&PARTIAL) != 0)
//!      ret = ret + ":PARTIAL";  //NORES
//!    if ((mask&NEXT) != 0)
//!      ret = ret + ":NEXT";  //NORES
//!    if ((mask&PRIOR) != 0)
//!      ret = ret + ":PRIOR";  //NORES
//!    if ((mask&CASE_INSENSITIVE) != 0)
//!      ret = ret + ":CASE_INSENSITIVE";  //NORES
//!//!
//!//!    if ((mask&CLOSEST) != 0)
//!//!      ret = ret + ":CLOSEST";  //NORES
//!//!
//!    if ((mask&FIRST) != 0)
//!      ret = ret + ":FIRST";  //NORES
//!    if ((mask&LAST) != 0)
//!      ret = ret + ":LAST";  //NORES
//!    if ((mask&FAST) != 0)
//!      ret = ret + ":FAST";  //NORES
//!    return ret;
//!  }
}
