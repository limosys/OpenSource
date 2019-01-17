//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ConnectionUpdateAdapter.java,v 7.0 2002/08/08 18:39:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.util.*;

/**
 * This is an adapter class for {@link com.borland.dx.sql.dataset.ConnectionUpdateListener}<CODE>ConnectionUpdateListener</CODE></A>,
 * which provides notification before and after closing a database connection or changing the attributes of the JDBC connection.

 */
public class ConnectionUpdateAdapter
  implements ConnectionUpdateListener {
  public void connectionChanged(ConnectionUpdateEvent event) {}
  public void connectionClosed(ConnectionUpdateEvent event) {}
  public void canChangeConnection(ConnectionUpdateEvent event) throws Exception {}
  public void connectionOpening(ConnectionUpdateEvent event) {}
}
