//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/util/OdbcDataSourceRepository.java,v 1.3 2003/06/11 17:43:36 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;
import java.util.Hashtable;
import java.util.Properties;

import javax.sql.DataSource;

import com.borland.dx.sql.metadata.NativeMetaData;
import com.borland.javax.sql.JdbcDataSource;


public class OdbcDataSourceRepository extends DataSourceRepository {

  OdbcDataSourceRepository() {
    dataSources = new Hashtable();
    new NativeMetaData().getOdbcInfo(dataSources);
  }

  public DataSource getDataSource(String name) {
    return (DataSource)dataSources.get(name);
  }
  public void addDataSource(String name, DataSource dataSource) {
    // read only.
  }
  public void removeDataSource(String name) {
    // read only.
  }
  public void store(DataSource dataSource, Properties props) {
    // read only.
  }
  public void load(DataSource dataSource, Properties props) {
    JdbcDataSource jdbcDataSource = (JdbcDataSource)dataSource;
    jdbcDataSource.setDriver("sun.jdbc.odbc.JdbcOdbcDriver"); //NORES
    jdbcDataSource.setUrl("jdbc:odbc:"+jdbcDataSource.getDatabaseName()); //NORES
  }

  public Hashtable getDataSources() {
    return dataSources;
  }

	Hashtable dataSources = new Hashtable();
}
