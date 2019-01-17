//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/AccessListener.java,v 7.0 2002/08/08 18:39:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * The AccessListener interface provides notification when a DataSet is opened,
 * closed, or restructured. Useful for component writers. Not for general usage.
 * See com.borland.dbswing source code for usage examples.
 */
public interface AccessListener extends EventListener
{
/**
 * Provides information regarding how a DataSet has been changed.
 *
 * @param event   The type of event that changed a data set: opened, closed,
 *                or restructured.
 * @see com.borland.dx.dataset.AccessEvent for more information.
 */
  public void accessChange(AccessEvent event);
}
