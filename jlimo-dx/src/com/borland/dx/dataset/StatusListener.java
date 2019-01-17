//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/StatusListener.java,v 7.0 2002/08/08 18:39:36 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.*;

/**
 * This interface is used as a mechanism by which a status bar gets its information from a DataSet.
 * Use this interface to customize or suppress the message sent from the DataSet to the status bar.
 */
public interface StatusListener extends EventListener
{
/**
 * Called when a message is being sent to the status bar and other status listeners.
 * @param event   The event that prompted the message to be sent.
 */
  public void statusMessage(StatusEvent event);
}
