//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/SQLToken.java,v 7.0 2002/08/08 18:39:58 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

//
// Tokens for light-weight SQL Parser
//
// Not public.
//
/**
 * This interface is used internally by other com.borland classes.
 *  You should never use this interface directly.
 */
interface SQLToken
{
  public static final int UNKNOWN     = 0;
  public static final int SELECT      = 1;
  public static final int FIELD       = 2;
  public static final int CONSTANT    = 3;
  public static final int STRING      = 4;
  public static final int FUNCTION    = 5;
  public static final int EXPRESSION  = 6;
  public static final int FROM        = 7;
  public static final int TABLE       = 8;
  public static final int WHERE       = 9;
  public static final int PARAMETER   = 10;
  public static final int COMMENT     = 11;
  public static final int OTHER       = 12;
  public static final int GROUP       = 13;
  public static final int HAVING      = 14;
  public static final int ORDER       = 15;
}

