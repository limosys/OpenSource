//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/util/DataSourceRepository.java,v 1.5 2003/06/09 15:45:34 arsenm2 Exp $
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


public abstract class DataSourceRepository {

  public void       open() {}
  public void       close() {}

  public Connection getConnection(String name)
    throws java.sql.SQLException
  {
    return getDataSource(name).getConnection();
  }
  public abstract DataSource getDataSource(String name);
  public abstract void       addDataSource(String name, DataSource dataSource);
  public abstract void       removeDataSource(String name);
  public abstract Hashtable  getDataSources();
  public abstract void       load(DataSource dataSource, Properties props);
  public abstract void       store(DataSource dataSource, Properties props);
}
