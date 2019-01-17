//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/datastore/TableDef.java,v 7.0 2002/08/08 18:37:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.datastore;

import com.borland.dx.dataset.*;

/**
 * Used by DataStorePump to present tables available for import from
 * an external database into a JDataStore database Each TableDef represents an
 *  individual table. The properties of the TableDef can be modified after the
 * DataStorePump.getTableDefs() method is called and before importing the tables
 * to the JDataStore.
 */
public class TableDef extends com.borland.dx.dataset.TableDescriptor {

  /**
   * The SQL query used to access information from the external database for this table
   */
  public String             query;

  /**
   * The name to give the table in the JDataStore
   */
  public String             storeName;

  /**
   * If true, save the query information associated with this table, so
   * that the table can later be refreshed from the external database.
   */
  public boolean            enableRefresh;

  /**
   * If true, save the connection information associated with this table,
   * so that changes to the table can be saved back to the external database.
   */
  public boolean            enableSave;
}
