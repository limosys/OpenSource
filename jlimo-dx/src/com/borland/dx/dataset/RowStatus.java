//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/RowStatus.java,v 7.0 2002/08/08 18:39:34 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**

All row in a dataSet have status.  Status settings are used by DataSets
to filter.  Status settings are used recording edit state.  This is
used when the row data must be saved back to its source.  For a QueryDataSet,
the source would be its associated JDBC driver.

*/

public interface RowStatus {
  // Note that a mask of 0 is used to make sure a dataRow never
  // shows up in any View.  Used by locate operations to have hidden
  // row.
  /** Row has been deleted.
  */
  public static final int DELETED         = 0x01;
  /** Row has been changed.
  */
  public static final int UPDATED         = 0x02;
  /** Row was added after the dataset was loaded.
  */
  public static final int INSERTED        = 0x04;
  /** Row was loaded (ie from the execution of a QueryDataSet's JDBC query
      or an import operation).
  */
  public static final int LOADED          = 0x08;
  /** This is the original copy of a changed row.
  */
  public static final int ORIGINAL        = 0x10;
//!  /** Whenever a detail fetch has been attempted for a master
//!      row, this bit is set on the master row.  This way the masters
//!      details are only fetched once.
//!  */
//! public static final int DETAILS_FETCHED  = 0//0x20;


//!  /* Row has been saved back to original data source.
//!  */
//! public static final int INSERT_RESOLVED  = 0//0x40;

//!  /* Row has been saved back to original data source.
//!  */
//! public static final int DELETE_RESOLVED  = 0//0x80;

//!  /* Row has been saved back to original data source.
//!  */
//! public static final int UPDATE_RESOLVED  = 0//0x100;
  /** Row is pending resolution.  Used internally.
  */
  public static final int PENDING_RESOLVED = 0x200;

  // Do not make public.  Use public definitions in DataConst (formerly in MatrixData).
  //
  /**
   *  This variable is used internally by other com.borland classes.
   *  You should never use this variable directly.
   */
  static final int DEFAULT        = (RowStatus.UPDATED|RowStatus.INSERTED|RowStatus.LOADED);

  /**
   * This variable is used internally by other com.borland classes.
   * You should never use this variable directly.
   */
  static final int DEFAULT_HIDDEN = RowStatus.DELETED|RowStatus.ORIGINAL;

}
