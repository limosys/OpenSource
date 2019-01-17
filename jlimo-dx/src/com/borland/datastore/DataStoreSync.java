//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/datastore/DataStoreSync.java,v 7.3 2003/09/30 14:33:48 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.datastore;

import com.borland.dx.sql.dataset.*;
import com.borland.datastore.*;
import com.borland.datastore.cons.*;
import com.borland.sql.SQLAdapter;
import java.sql.*;
import java.util.*;
import com.borland.dx.dataset.*;
import com.borland.jb.util.DiagnosticJLimo;
//import com.borland.dbtools.jdbcx.common.*;

/**
  * This component can be used to synchronized data pumped into a JDAtaStore with the
  * DataStorePump component.  The DataStorePump optionally creates a /SYS/CONNECTIONS
  * table and a /SYS/QUERIES table.  The queries table records a query to run against
  * an external database.  An external database can be any database that has
  * a JDBC driver available for it.  The connections table records all the information
  * needed to connect to an external database using its JDBC driver.  The queries
  * table also has fields that can be used to specify whether a table should have
  * its changes saved back to the external database by the DataStoreSync component
  * and whether a table should have its contents refreshed with the contents of
  * the external database version of the same table using the query stored in the
  * queries table.
*/

public class DataStoreSync implements com.borland.dx.dataset.Designable {


  /**
   * Open the DataStoreSync. Set properties have no effect until the open() method is called
   */
  public void open() /*-throws DataSetException-*/  {

    if (!isOpen) {

      if (con == null) {
        throw new RuntimeException(Res.bundle.getString(ResIndex.DataStoreNotSet));
      }


      queries     = DataStorePump.getQueriesTable();
      queries.setStore(con);
      connections = DataStorePump.getConnectionsTable();
      connections.setStore(con);
      if (connections.getNeedsRestructure())
        connections.restructure();

      isOpen = true;
    }
  }

  /**
   * Close the <code>DataStoreSync</code>
   */
  public void close()
    /*-throws DataSetException-*/
  {
    if (isOpen) {
      isOpen = false;
      queries.close();
      connections.close();
    }
  }


  public final DataStoreConnection getDataStore() {
    return this.con;
  }

  public void setDataStore(DataStoreConnection con) {
    this.con = con;
  }


  /**
   * Executes all queries in the /SYS/QUERIES table.
   */
  public void refreshAllTables()
    /*-throws DataSetException-*/
  {
    open();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();
    int lastId = -1;
    int conId;
    Database database = null;
    while (queryView.inBounds()) {
      conId = queryView.getInt(SysQueries.CONNECTION);
      if (conId != lastId) {
        if (database != null)
          database.closeConnection();
        database = findDatabase(conId);
        lastId = conId;
      }
      refreshTable(database, queryView);
      queryView.next();
    }

    if (database != null)
      database.closeConnection();
    queryView.close();
  }

  /**
   * Executes all queries in the /SYS/QUERIES table which pertain to the specified
   *  connectionId. The connectionId parameter is matched to the ID column in the
   * /SYS/CONNECTIONS table. When there are multiple external databases, this allows
   *  tables to be refreshed from a specific external database.
   * @param connectionId
   */
  public void refreshConnectionTables(int connectionId)
    /*-throws DataSetException-*/
  {
    open();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();
    Database database = findDatabase(connectionId);
    while (queryView.inBounds()) {
      if (queryView.getInt(SysQueries.CONNECTION) == connectionId) {
        refreshTable(database, queryView);
      }
      queryView.next();
    }
    database.closeConnection();
    queryView.close();
  }

  private final Database findDatabase(int conId)
    /*-throws DataSetException-*/
  {
      DataSetView connectionView = new DataSetView();
      connectionView.setStorageDataSet(connections.getStorageDataSet());
      connectionView.open();
      DataRow locateRow2 = new DataRow(connectionView, SysConnections.ID);
      locateRow2.setInt(SysConnections.ID, conId);

      Database database = new Database();
      if (connectionView.locate(locateRow2, Locate.FIRST)) {
        database.setConnection(new ConnectionDescriptor(connectionView.getString(SysConnections.URL),
                                                        connectionView.getString(SysConnections.USER_NAME),
                                                        connectionView.getString(SysConnections.PASSWORD),
                                                        false,
                                                        connectionView.getString(SysConnections.DRIVER),
                                                        DataStorePump.getExtendedProperties(connectionView.getString(SysConnections.PROPERTIES))));
      }

      connectionView.close();

    return database;
  }


  private final void refreshTable(Database database, DataSetView queryView)
    /*-throws DataSetException-*/
  {
    if (queryView.getBoolean(SysQueries.ENABLE_REFRESH)) {
      boolean enableSave = queryView.getBoolean(SysQueries.ENABLE_SAVE);
      QueryDataSet queryDataSet = new QueryDataSet();
      queryDataSet.setStoreName(queryView.getString(SysQueries.STORE_NAME));
      DataStoreConnection con = (DataStoreConnection)queries.getStore();
      queryDataSet.setStore(con);
      queryDataSet.setMaxDesignRows(-1);
      try {
        con.deleteStream(queryDataSet.getSchemaStoreName());
      }
      catch(Exception ex) {
        DiagnosticJLimo.printStackTrace(ex);
      }
      queryDataSet.setQuery(new QueryDescriptor(database,
                                                queryView.getString(SysQueries.QUERY),
                                                null,
                                                true,
                                                queryView.getInt(SysQueries.LOAD_OPTION)));
      queryDataSet.setMetaDataUpdate(enableSave ? MetaDataUpdate.ALL : MetaDataUpdate.NONE);
      queryDataSet.setResolvable(enableSave);
      queryDataSet.open();
//      queryDataSet.executeQuery();
      queryDataSet.close();
      queryDataSet.closeProvider(true);
      if (queryView.getBoolean(SysQueries.UPPERCASE_COLUMNS))
        DataStorePump.upperCaseColumnNames(queryDataSet);
    }
  }

  /**
   * Refresh a specific table from its external data source. The storeName property
   * specified should be the name of a table in the JDataStore.
   * @param storeName
   */
  public void refreshTable(String storeName)
    /*-throws DataSetException-*/
  {
    open();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();
    if (!queryView.getBoolean(SysQueries.ENABLE_REFRESH)) {
      return;
    }
    DataRow locateRow = new DataRow(queryView, SysQueries.STORE_NAME);
    locateRow.setString(SysQueries.STORE_NAME, storeName);
    if (queryView.locate(locateRow, Locate.FIRST)) {

      Database database = findDatabase(queryView.getInt(SysQueries.CONNECTION));

      refreshTable(database, queryView);

      database.closeConnection();
    }
    queryView.close();
  }
/*
  public void loadQuery(String storeName)
  {
    open();
    QueryDataSet queryDataSet = new QueryDataSet();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();

    DataRow locateRow = new DataRow(queryView, SysQueries.STORE_NAME);
    locateRow.setString(SysQueries.STORE_NAME, storeName);
    if (queryView.locate(locateRow, Locate.FIRST)) {
      DataSetView connectionView = new DataSetView();
      connectionView.setStorageDataSet(connections.getStorageDataSet());
      connectionView.open();
      DataRow locateRow2 = new DataRow(connectionView, SysConnections.ID);
      locateRow2.setInt(SysConnections.ID, queryView.getInt(SysQueries.CONNECTION));

      Database database = new Database();
      if (connectionView.locate(locateRow2, Locate.FIRST)) {
        database.setConnection(new ConnectionDescriptor(connectionView.getString(SysConnections.URL),
                                                        connectionView.getString(SysConnections.USER_NAME),
                                                        connectionView.getString(SysConnections.PASSWORD),
                                                        false,
                                                        connectionView.getString(SysConnections.DRIVER),
                                                        getExtendedProperties(connectionView.getString(SysConnections.PROPERTIES))));
      }
      queryDataSet.setStoreName(storeName);
      queryDataSet.setStore(queries.getStore());
      queryDataSet.setMaxDesignRows(-1);
      queryDataSet.setQuery(new QueryDescriptor(database,
                                                queryView.getString(SysQueries.QUERY),
                                                null,
                                                true,
                                                queryView.getInt(SysQueries.LOAD_OPTION)));
      queryDataSet.open();
      queryDataSet.close();
      connectionView.close();
    }
    queryView.close();
  }
*/

/**
 * Save all tables in the JDataStore back to their counterparts in external
 * database connections
 */
  public void saveAllTables()
    /*-throws DataSetException-*/
  {
    open();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();
    int lastId = -1;
    int conId;
    Database database = null;
    while (queryView.inBounds()) {
      conId = queryView.getInt(SysQueries.CONNECTION);
      if (conId != lastId) {
        if (database != null)
          database.closeConnection();
        database = findDatabase(conId);
        lastId = conId;
      }
      saveTable(database, queryView);
      queryView.next();
    }
    if (database != null)
      database.closeConnection();
    queryView.close();
  }

  /**
   * Save all tables in the JDataStore which are connected to a specific
   *  external database back to their external counterparts. The connectionId
   * parameter is matched to the ID column in the /SYS/CONNECTIONS table. When
   * there are multiple external databases, this allows tables to be saved back
   *  to a specific external database.
   * @param connectionId
   */
  public void saveConnectionTables(int connectionId)
    /*-throws DataSetException-*/
  {
    open();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();
    Database database = findDatabase(connectionId);
    while (queryView.inBounds()) {
      if (queryView.getInt(SysQueries.CONNECTION) == connectionId) {
        saveTable(database, queryView);
      }
      queryView.next();
    }
    database.closeConnection();
    queryView.close();
  }

  /**
   *
   * @param database
   * @param queryRow
   */
  public void saveTable(Database database, ReadRow queryRow)
    /*-throws DataSetException-*/
  {
    if (queryRow.getBoolean(SysQueries.ENABLE_SAVE)) {
      QueryDataSet queryDataSet = new QueryDataSet();
      queryDataSet.setStoreName(queryRow.getString(SysQueries.STORE_NAME));
      queryDataSet.setStore(queries.getStore());
      if (!queryRow.isNull(SysQueries.UPDATE_MODE)) {
        int updateMode = queryRow.getInt(SysQueries.UPDATE_MODE);
        if (updateMode == UpdateMode.CHANGED_COLUMNS || updateMode == UpdateMode.KEY_COLUMNS) {
          QueryResolver resolver = new QueryResolver();
          resolver.setUpdateMode(updateMode);
          queryDataSet.setResolver(resolver);
        }
      }
      database.saveChanges(queryDataSet);
      queryDataSet.close();
    }
  }

  /**
   * Save a specific table back to its counterpart in an external database connection.
   * The storeName property specified should be the name of a table in the JDataStore
   * @param storeName
   */
  public void saveTable(String storeName)
    /*-throws DataSetException-*/
  {
    open();
    DataSetView queryView = new DataSetView();
    queryView.setStorageDataSet(queries.getStorageDataSet());
    queryView.open();
    if (!queryView.getBoolean(SysQueries.ENABLE_SAVE)) {
      return;
    }
    DataRow locateRow = new DataRow(queryView, SysQueries.STORE_NAME);
    locateRow.setString(SysQueries.STORE_NAME, storeName);
    if (queryView.locate(locateRow, Locate.FIRST)) {

      Database database = findDatabase(queryView.getInt(SysQueries.CONNECTION));

      saveTable(database, queryView);

      database.closeConnection();
    }
    queryView.close();
  }


  private boolean               isOpen;
  private DataStoreConnection   con;
  private TableDataSet          queries;
  private TableDataSet          connections;
}
