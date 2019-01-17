//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/util/JndiDataSourceRepository.java,v 1.3 2003/02/21 20:47:08 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;
import com.borland.dx.sql.dataset.*;
import com.borland.dx.dataset.*;
import java.lang.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;


public abstract class JndiDataSourceRepository extends DataSourceRepository {

  public DataSource getDataSource(String name) {
    DataSource source = null;
    try {
      javax.naming.Context ctx = new javax.naming.InitialContext();
      source = (DataSource)ctx.lookup(name);
    }
    catch(Exception ex) {
      DataSourceException.throwExceptionChain(ex);
    }
    return source;
  }
  public void       addDataSource(String name, DataSource dataSource) {
  }
  public void       removeDataSource(String name) {
  }
  public abstract Hashtable  	 getDataSources();

}
