//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/NativeMetaData.java,v 7.2 2003/03/23 04:41:42 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;

public class NativeMetaData
{
  public NativeMetaData() {
    System.loadLibrary ("jbmetadata");  //NORES
  }

  static private final int MAX_DESCR_LENGTH   = 256;
  static private final int SQL_MAX_DSN_LENGTH =  32;

  void getOdbcURLs(java.util.Vector vector) {

    String prefix = "jdbc:odbc:";  //NORES
    String newURL = null;
    byte DataSourceName[] = new byte[SQL_MAX_DSN_LENGTH+2];
    byte DataSourceDescription[] = new byte[MAX_DESCR_LENGTH+2];
    boolean first = true;
    int hEnv = getOdbcEnv();
    int len = 0;

    while (getOdbcDataSource(hEnv, first, DataSourceName, DataSourceDescription)) {
      len = DataSourceName[0];
      newURL = prefix + new String(DataSourceName,1,len);
      vector.addElement(newURL);
      first = false;
    }

    freeOdbcEnv(hEnv);
  }

  public void getOdbcInfo(java.util.Hashtable table) {

    byte DataSourceName[] = new byte[SQL_MAX_DSN_LENGTH+2];
    byte DataSourceDescription[] = new byte[MAX_DESCR_LENGTH+2];
    String name;
    String desc;
    boolean first = true;
    int hEnv = getOdbcEnv();
    int len = 0;

    while (getOdbcDataSource(hEnv, first, DataSourceName, DataSourceDescription)) {
      len = DataSourceName[0];
      name = new String(DataSourceName,1,len);
      len = DataSourceDescription[0];
      desc = new String(DataSourceDescription,1,len);
      table.put(name, desc);
      first = false;
    }

    freeOdbcEnv(hEnv);
  }

  public native int     getOdbcEnv();            // SQLAllocEnv in JdbcOdbc
  public native void    freeOdbcEnv(int hEnv);   // SQLFreeEnv  in JdbcOdbc
  public native boolean getOdbcDataSource(int hOdbcEnv, boolean first, byte DataSourceName[] ,byte DataSourceDescription[]); // Not in JdbcOdbc
}
