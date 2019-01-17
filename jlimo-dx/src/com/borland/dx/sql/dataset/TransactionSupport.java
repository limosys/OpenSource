//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/TransactionSupport.java,v 7.0 2002/08/08 18:39:59 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;
import com.borland.dx.dataset.*;

/**
 * This interface is used internally by other com.borland classes.
 *  You should never use this interface directly.
 */
public interface TransactionSupport {
  public void     start() /*-throws DataSetException-*/;
  public void     commit() /*-throws DataSetException-*/;
  public void     rollback() /*-throws DataSetException-*/;

  //! DISABLED.  When resolving multiple DataSets, transactions can be performed for each DataSet resolution,
  //! or once for all DataSets. If isSingleTransaction() is true, one transaction will span all DataSets
  //! being resolved. One failure will cause all work to be rolled back. If multipleDataSets is
  //! false, a transaction will be started and committed (or rolled-back) for each DataSet being
  //! resolved.
  //
//!  public boolean  isSingleTransaction();
}
