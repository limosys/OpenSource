//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jdbc/Monitor.java,v 7.0 2002/08/08 18:40:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jdbc;

// Adapted from BDE Bridge and InterClient/Statement.java

import java.sql.DriverManager;
import java.util.Vector;

/**
 * The Monitor class is an extension to the JDBC API.
 * Every driver instance has its own monitor instance.
 * As connections are added, they each get a monitor
 * instance of their own, except that all connections to
 * the same URL use the same monitor.
 **/

public abstract class Monitor
{
    public abstract void setURL( String url);
    public abstract String getURL();
    public abstract void enableDriverTrace( boolean enable);
    public abstract void enableConnectionTrace( boolean enable);
    public abstract void enableResultSetTrace( boolean enable);
    public abstract void enableFetchTrace( boolean enable);
    public abstract void enableAllTraces( boolean enable);
    public abstract void enableStatementTrace( boolean enable);
    public abstract void setMonitorStream(java.io.PrintStream out);
    public abstract java.io.PrintStream getMonitorStream();
    public abstract void resetAll();
    public abstract void duplicateSettings( Monitor newMonitor);
}
