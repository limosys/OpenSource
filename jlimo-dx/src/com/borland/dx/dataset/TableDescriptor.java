//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/TableDescriptor.java,v 7.0 2002/08/08 18:39:38 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;



/**
 * Used by DataStorePump to present tables available for import from
 * an external database into a JDataStore database
 */
public class TableDescriptor implements java.lang.Cloneable {

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }

  public final String toString() {
    String ret = table;
    if ( schema != null && schema.length() > 0 ) {
      ret = schema + "." + ret; //NORES
    }
    if ( catalog != null && catalog.length() > 0 ) {
      ret = catalog + "." + ret; //NORES
    }
    return ret;
  }

  public String                   catalog;
  public String                   schema;
  public String                   table;
  public SortDescriptor           primaryKey;
  public SortDescriptor[]         indexes;
  public Column[]                 columns;
  public ForeignKeyDescriptor[]   foreignKeys;
}
