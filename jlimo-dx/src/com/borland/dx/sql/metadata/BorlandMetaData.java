//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/BorlandMetaData.java,v 7.0 2002/08/08 18:40:05 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.ConnectionDescriptor;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.jdbc.*;
import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;

public class BorlandMetaData extends MetaDataImplementor
{
  BorlandMetaData(MetaData metaData) {
    super(metaData);
  }

  static private final int MAX_DESCR_LENGTH   = 256;
  static private final int SQL_MAX_DSN_LENGTH =  32;
  static private final String RemoteClassName = "com.borland.jdbc.Broker.RemoteDriver";
  static private final String LocalClassName = "com.borland.jdbc.Bridge.LocalDriver";
  static private final String RemoteURL =         "jdbc:BorlandBroker://";  //NORES
  static private final String LocalURL = "jdbc:BorlandBridge:";         //NORES

//
//      it is OK to use the remote class if a driver of the correct name exists, and
//      it is indeed a java.sql.Driver.  If not found or we are really looking at the
//      stub, don't call the driver.

  static public boolean getRemoteEnabled() {
    try {
      Class c = Class.forName(RemoteClassName);
      return true;
    }
    catch (Throwable ex) {
      DiagnosticJLimo.printStackTrace(ex);
      return false;
    }
  }

//
//      it is OK to use the local class if a driver of the correct name exists, and
//      it is indeed a java.sql.Driver.  If not found or we are really looking at the
//      stub, don't call the driver.


  static public boolean getLocalEnabled() {
    try {
      Class c = Class.forName(LocalClassName);
      return true;
    }
    catch (Throwable ex) {
      DiagnosticJLimo.printStackTrace(ex);
      return false;
    }
  }


  static public String getRemoteClassName() {
    return RemoteClassName;
  }

  static public String getLocalClassName() {
    return LocalClassName;
  }
//
//      return a URL list for the server represented by s
//      "s" can either be a server name alone, or a full URL
//

  static public void getBorlandRemoteURLs(String s, java.util.Vector vector) {
    try {
      String server = null;
//
//      check if the string passed in is a URL or a server.  if it is a URL, parse out
//      the server name and user it to get the data source list

      if (s.startsWith(RemoteURL, 0)) {
        int firstIndex = RemoteURL.length();
        int lastIndex = s.indexOf('/', firstIndex+1);
        if (lastIndex > 0) {
          server =  s.substring(firstIndex, lastIndex);
        }
      }
      if (server == null) server = s;
//
//      look for a Remote Driver in the list of loaded drivers

      Enumeration e = DriverManager.getDrivers();
      while (e.hasMoreElements()) {
//        try {
        Object o = e.nextElement();
        if (o.getClass().getName().equals(RemoteClassName)) {
          String[] list = ((com.borland.jdbc.Driver)o).getDataSourcesList(server);
          for (int i = 0; i < list.length; i++) {
            try {
//!System.err.println(list[i]);
              vector.addElement(RemoteURL + server + '/' + list[i]);
            }
            catch (Throwable ex) {
              DiagnosticJLimo.printStackTrace(ex);
              //!exprintStackTrace();
            }
          }
        }
//        }
//        catch (Exception ex) {
//          //!exprintStackTrace();
//        }
      }
    }
    catch (java.sql.SQLException e) {
//!      vector.addElement("the Borland Remote could not be loaded");
    }
  }
//
//      for the local driver, get a vector of alias strings
//
  static public void getBorlandLocalURLs(java.util.Vector vector) {
//!              try {
    Enumeration e = DriverManager.getDrivers();
    while (e.hasMoreElements()) {
      try {
        Object o = e.nextElement();
//!System.err.println("obj is " + o);
        if (o.getClass().getName().equals(LocalClassName)) {
          String[] list = ((com.borland.jdbc.Driver)o).getDataSourcesList( );
          for (int i = 0; i < list.length; i++) {
            try {
//!                System.err.println(list[i]);
              vector.addElement(LocalURL + list[i]);
            }
            catch (Throwable ex) {
              DiagnosticJLimo.printStackTrace(ex);
              //!exprintStackTrace();
            }
          }
        }
      }
      catch (Throwable ex) {
        DiagnosticJLimo.printStackTrace(ex);
        //!exprintStackTrace();
      }
    }
//!      }
//!    catch (java.sql.SQLException e) {
//!      vector.addElement("the Borland Local could not be loaded");
//!    }
  }

  static public String getBorlandRemoteServerName(String URL) {
    if (URL.startsWith(RemoteURL, 0)) {
      int firstIndex = RemoteURL.length();
      int lastIndex = URL.indexOf('/', firstIndex+1);
      if (lastIndex > 0) return URL.substring(firstIndex, lastIndex);
    }
    return null;
  }

  public String columnToSQLDataType(Column column) throws MetaDataException {
    return getImplementor().columnToSQLDataType(column);
  }

  /**
   * Determines whether the given Database is connected to the Borland
   * DataGateway.  The database doesn't have to be open at the time,
   * but its driver should have been registered already.
   *
   * @param database is the Database object to be tested
   *
   * @return True if this database is connected to DataGateway
   */
  static public boolean isBorlandDataGateway(Database database) {
    boolean result = false;

    if (database == null)
      return false;

    // Cannot succeed if DataGateway is absent
    if (!getRemoteEnabled() && !getLocalEnabled())
      return false;

    ConnectionDescriptor connectionDescriptor = database.getConnection();
    if (connectionDescriptor != null) {
      String url = connectionDescriptor.getConnectionURL();
      if (url != null && url.length() > 0) {
        java.sql.Driver driver = null;
        try {
          driver = DriverManager.getDriver(url);
          result = (driver != null && driver instanceof com.borland.jdbc.Driver);
        }
        catch (Throwable ex) {
          DiagnosticJLimo.printStackTrace(ex);
        }
      }
    }
    //!System.err.println("isBorlandDataGateway returning " + result);
    return result;
  }

  private MetaDataImplementor getImplementor() {
    if (implementor == null)
      implementor = metaData.getImplementorByDialect();
    return implementor;
  }

  private MetaDataImplementor implementor;
}


