//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/Task.java,v 7.0 2002/08/08 18:39:59 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

/**
 * This interface is used internally by other com.borland classes.
 *  You should never use this interface directly.
 */
interface Task {
  public void executeTask() throws Exception;
}
