//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/UpdateMode.java,v 7.1 2003/05/20 18:47:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------


package com.borland.dx.dataset;

/**
 * The UpdateMode component collects constants used when resolving changes
 * for the QueryDataSet and ProcedureDataSet components.
 */
public interface UpdateMode {

/**
 * The update mode has not been assigned.
 */
  public static final int UNASSIGNED    = 0;

  /**
   *  Every column is used to find the row being updated (the default).
   *  This is the most restrictive mode.
   */
  public static final int ALL_COLUMNS      = 1;

  /**
   *  Only the key columns are used to find the record being updated. This is
   *  the least restrictive mode and should be used only if other users will
   *  not be changing the records being updated.
   */
  public static final int KEY_COLUMNS  = 2;

/**
 * The key columns and columns that have changed are used to find the record being updated.
 */
  public static final int CHANGED_COLUMNS  = 3;
}
