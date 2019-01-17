//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/TriStateProperty.java,v 7.0.2.1 2004/12/08 01:17:44 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

/**
 *  The TriStateProperty interface is used to implement a property
 *  that can have three states: true, false, or default.
 *  The three integer variables of the interface control the property value.
 */
public interface TriStateProperty
{
/**
 *  The property value is <b>false</b>.
 */
  public static final int FALSE    =  0;

  /**
   * The property value is <b>true</b>.
   */
  public static final int TRUE     =  1;

  /**
   * The property value is the default value.
   */
  public static final int DEFAULT  = -1;

  /**
   * The property value is unknown (=DEFAULT).
   */
  public static final int UNKNOWN  = DEFAULT;
}
