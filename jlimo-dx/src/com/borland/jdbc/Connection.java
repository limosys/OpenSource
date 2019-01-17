//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jdbc/Connection.java,v 7.0 2002/08/08 18:40:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jdbc;

// Adapted from BDE Bridge and InterClient/Connection.java

import java.sql.SQLException;

// Implements Connection class for both local and remote Velcro drivers
public interface Connection extends java.sql.Connection
{

    // getMonitor returns the Monitor instance for the connection.
    // This may be either a global monitor for all connections for
    // a URL, or the connection's individual monitor instance.
    public Monitor getMonitor();

    // return URL for this connection
    public String getURL();

    // reset monitor to new instance - used to reset connection's
    // monitor to a default instance if the URL is removed from
    // the driver's list.
    public void resetMonitor( Monitor m);

    //To prevent circular reference Connection->Statement
    //Connection-Statement statemnet will call this method to remove
    //the refernce from statement->Connection
    public void removeElement(java.sql.Statement st);

}
