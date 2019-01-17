//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/ExceptionHandler.java,v 7.0 2002/08/08 18:40:52 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.awt.Component;

/**
 * The <code>ExceptionHandler</code> interface allows an object to
 * generically handle exceptions.
 */
public interface ExceptionHandler
{
  /**
   * This method processes the exception as appropriate.
   * @param x The exception to handle.
   */
  public void handleException(Exception ex);
}
