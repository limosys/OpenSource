//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jdbc/Driver.java,v 7.0 2002/08/08 18:40:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jdbc;

// Adapted from BDE Bridge JdbcBdeDriver.java

//imports
import java.sql.DriverManager;
import java.sql.SQLException;

// Implements Driver class for both local and remote Velcro drivers
public interface Driver extends java.sql.Driver
{

    // getMonitor returns the Monitor instance for the driver.
    // This instance can be used to set desired traces which
    // will be passed on to connections made by the driver.
    public Monitor getMonitor();

    // Returns a list of data sources (BDE aliases) for local BDE.
    public  String[] getDataSourcesList()
        throws SQLException;

    // Returns a list of data sources (BDE aliases) for a server.
    public  String[] getDataSourcesList( String serverName)
        throws SQLException;

    // Returns an array of connections currently active for
    // the driver.   These can be used to set connection-specific
    // monitor traces for each connection by using the connections'
    // own getMonitor methods.
    public  Object[] getConnections();

    // Express interest in monitoring a URL for a driver.   Adds
    // URL to the driver's monitored URLs list and returns the
    // montor instance for the URL.
    public  Monitor monitorURL( String URL);

    // Get monitor for a URL, if it's in the list.
    public  Monitor getMonitorForURL( String URL);

    // Return an array of currently monitored or connected URLs
    public  Object[] getMonitorsForURLs();

    // Remove interest in monitoring a URL
    public  void unMonitorURL( String URL);

    //To prevent circular reference Driver->Connection
    //Connection->Driver connection will call this method to remove
    //the refernce from driver->connection
    public  void removeElement(java.sql.Connection co);



}
