//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/DesignerConnectionCache.java,v 7.0 2002/08/08 18:39:51 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.util.Vector;
import java.sql.Connection;
import com.borland.jb.util.DiagnosticJLimo;

/** For internal design time purposes only.
 * We have found that the Database.finalize() is rarely being called, meaning
 * we exhaust database connections very quickly toggling back and forth to
 * design.  To provide a partial solution, we maintain a designtime-only
 * cache associating ConnectionDescriptors with their JDBC Connection object.
 * In other words, if 2 ConnectionDescriptors are identical, they will share
 * the same instance of the Connection (via this caching mechanism)
 */

/**
 * This class is used internally by other com.borland classes.
 *  You should never use this class directly.
 */
public class DesignerConnectionCache {
  private static Vector cache = new Vector();
  private Shutdown shutdownHook;

  ConnectionDescriptor connectionDescriptor;
  int useCount;
  Connection connection;


  public DesignerConnectionCache(ConnectionDescriptor cDesc, Connection connection) {
    this.connectionDescriptor = cDesc;
    this.connection = connection;
    useCount = 1;
    addShutdownHook();
  }


  /**
   * Adds a brand new entry into the cache associating the given connecction
   * with a ConnectionDescriptor,
   */
  static synchronized void addConnection(ConnectionDescriptor cDesc, Connection connection) {

    //!Diagnostic.println("++ add new Connection: " + connection + ", desc = " + cDesc);
    if (cDesc == null || connection == null)
      return;
    DesignerConnectionCache dcc = new DesignerConnectionCache(cDesc, connection);
    cache.addElement(dcc);

    //dumpCache();
  }


  /**
   * Determines whether the given ConnectionDescriptor is already
   * associated with a Connection.  If yes, bumps the use count up
   * by 1 and returns associated Connection.  Returns null to show
   * this ConnectionDescriptor is not in use yet
   */
  public static synchronized Connection shareConnection(ConnectionDescriptor cDesc) {


    if (cDesc == null) return null;

    for (int i = 0; i < cache.size(); ++i) {
      DesignerConnectionCache cachedEntry = (DesignerConnectionCache) cache.elementAt(i);
      if (cachedEntry.connectionDescriptor.canShare(cDesc)) {
        ++cachedEntry.useCount;
//!        cache.setElementAt(cachedEntry, i);
//!        //!Diagnostic.println("    shareConnection: sharing " + cachedEntry.connection + ", useCount at " + cachedEntry.useCount);
//!        //dumpCache();
        return cachedEntry.connection;
      }
    }
    return null;
  }

  /**
   * Called when done with a Connection, this method decrements the
   * useCount and frees the cache entry when it hits zero.
   * It returns true iff there are no more references to this connection.
   */
  static synchronized boolean releaseConnection(Connection connection) {
    for (int i = 0; i < cache.size(); ++i) {
      DesignerConnectionCache cachedEntry = (DesignerConnectionCache) cache.elementAt(i);
      if (connection == cachedEntry.connection) {
        //!Diagnostic.println("    releaseConnection: useCount at " + cachedEntry.useCount);
        if (--cachedEntry.useCount <= 0) {
          //!Diagnostic.println("    --- final release ---");
          cache.removeElementAt(i);
          return true;
        }
        return false;
      }
    }
    //!Diagnostic.println("!! connection not cached -- signalling final release !!");
    return true;
  }

  // Should only be called on shutDown!!!  Does not know about Database component and
  // Database.component is tricky about how it really does closing and we are about
  // to ship so I do something very direct.
  //
  public synchronized static void shutDown() {
    for (int i = 0; i < cache.size(); ++i) {
      try {
        ((DesignerConnectionCache) cache.elementAt(i)).connection.close();
        DiagnosticJLimo.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ Connection closed $$$$$$$$$");
      }
      catch(Exception ex) {
        DiagnosticJLimo.fail();  // Oh well.
      }
    }
    // For insurance.
    //
    cache = new Vector();
  }
  private final void addShutdownHook() {
    try {
      shutdownHook = new Shutdown(this);
      Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
    catch(Throwable ex) {
      removeShutdownHook();
    }
  }

  private final void removeShutdownHook() {
    if (shutdownHook != null) {
      try {
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
      }
      catch(Throwable ex) {
      }
      finally {
        shutdownHook.cache = null;
        // Subtle.  If thread not started and terminated, will remain
        // on main ThreadGroup list causing a memory leak.
        //
        shutdownHook.start();
        shutdownHook = null;
      }
    }
  }


//!/* Comment back in for debugging
//!  static synchronized void dumpCache() {
//!    Diagnostic.println("<<<<< Cache dump >>>>>");
//!    for (int i = 0; i < cache.size(); ++i) {
//!      DesignerConnectionCache cachedEntry = (DesignerConnectionCache) cache.elementAt(i);
//!      Diagnostic.println("Cache entry[" + i + "] use count " + cachedEntry.useCount + ", connection = " + cachedEntry.connection);
//!      Diagnostic.println("   descriptor: " + cachedEntry.connectionDescriptor);
//!    }
//!    Diagnostic.println("_____ end cache dump _____");
//!  }
//!*/

//!  /*
//!-- The old and buggy stuff now commented out --
//!  private static final Database getEntry(int entry) {
//!    return (Database) cache.elementAt(entry);
//!  }
//!
//!  private static final Database find(Database database) {
//!    int count = cache.size();
//!    Database cachedDatabase;
//!    for (int entry = 0; entry < count; ++entry) {
//!      cachedDatabase  = getEntry(entry);
//!      if (cachedDatabase.getConnection().canShare(database.getConnection()))
//!        return cachedDatabase;
//!    }
//!    return null;
//!  }
//!
//!  static final Connection openConnection(Database database) {
//!    Database  cachedDatabase  = find(database);
//!    if (cachedDatabase != null) {
//!      // Add all databases that share a connection to the cache.
//!      // If one is closed, than all that share the connection must
//!      // also be closed.
//!      //
//!      addConnection(database);
//!      return cachedDatabase.connection;
//!    }
//!    return null;
//!  }
//!  static final void removeConnection(Database database)
//!    /*-throws DataSetException-*/
//!  {
//!    int count = cache.size();
//!    Database cachedDatabase;
//!    int entry;
//!    for (entry = 0; entry < count; ++entry) {
//!      if (getEntry(entry) == database)
//!        break;
//!    }
//!    Diagnostic.check(entry < count);
//!    // If one is closed, than all that share the connection must
//!    // also be closed.
//!    //
//!    cache.removeElementAt(entry);
//!
//!    //!RC Steve, I am trying to use a connectionUseCount in each database to
//!    //! prevent this need to shut down other connections
//!    //!while ((cachedDatabase  = find(database)) != null)
//!    //!  cachedDatabase.sharedCloseConnection();
//!  }
//!
//!  static final void addConnection(Database database) {
//!    cache.addElement(database);
//!  }
//!
//!  public void removeAllConnections()
//!    /*-throws DataSetException-*/
//!  {
//!    while (cache.size() > 0)
//!      removeConnection(getEntry(0));
//!  }
//!  */
}

class Shutdown extends Thread {
  DesignerConnectionCache cache;

  Shutdown(DesignerConnectionCache cache) {
    super("DesignerConnectionCache"); //NORES
    this.cache = cache;
  }

  public final void run() {
    if (cache != null) {
      DiagnosticJLimo.println(toString()+" AUTOSHUTDOWN"); //NORES
      cache.shutDown();
    }
  }
}
