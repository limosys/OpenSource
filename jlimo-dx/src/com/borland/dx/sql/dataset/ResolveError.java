//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ResolveError.java,v 7.1 2003/06/13 16:21:28 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;

/**
    This class provides information about a specific row that could not
    be resolved.  The ResolutionManager which is invoked by calling
    DataSet.saveChanges() or Database.saveChanges() will log errors in an
    error DataSet for all rows that meet the following criteria:

    1) One or more StorageDataSets being resolved has the StorageDataSet.MaxResolveErrors
    property set to a value greater than 0.  If this property is set to -1, then
    all errors will be logged.


    2) An exception (i.e. SQLException) was encountered for the row as it was
    being inserted, deleted or updated.

    At the end of the resolution process that is not aborted, a ResolutionException
    with an error code of ResolutionException.RESOLVE_PARTIAL will be thrown after
    an attempt to resolve all rows is made and the successfully resolved rows
    are commited.

    If the StorageDataSet.MaxResolveErrors limit is reached, a ResolutionException
    with an error code of ResolutionException.RESOLVE_FAILED will be thrown.

    The ResolutionException.getErrorDataSets() can be used to get all the
    errors for which ErrorResponse.ignore() was called.  This method returns
    an array of StorageDataSets.  There is one StorageDataSet for every DataSet
    that participated in the resolution and the order of the StorageDataSets
    in the error StorageDataSet array corresponds to the order of the DataSets that were
    passed into the Database.saveChanges() method.  If there are multiple StorageDataSets
    being resolved and a particular DataSet did not encounter any errors, its
    index in the error StorageDataSet array will be null.

    The error StorageDataSet array has the same columns as its corresponding
    DataSet that was resolved plus one extra RESOLVE_ERROR column.  The RESOLVE_ERROR
    column is of type Variant.OBJECT and is appended as the last column in the
    error StorageDataSet.  The RESOLVE_ERROR column contains an instance of the
    ResolveError class.


    @see StorageDataSet#setMaxResolveErrors property
    @see ResolutionException#RESOLVE_PARTIAL
    @see ResolutionException#RESOLVE_FAILED
*/

public class ResolveError implements java.io.Serializable {

  /**
      DataSet Row that encountered the error.  This is not
      always unique in the case of detail DataSets since
      a single detail DataSet can have multiple groups of records
      with identical row values.
  */
  public  long                row;
  /**
      DataSet internalRow that encountered the error.
      internalRow is a unique identifier for the row.
  */
  public  long               internalRow;
  /**
      Set to ErrorResponse.ABORT if the error terminated the resolution transaciton
      Set to ErrorResponse.IGNORE if the error was ignored.
  */
  public  int                response;
  /**
      Error message for the error.  Usually the same as ex.getMessage().
  */
  public  String             message;
  /**
      If the error is caused by a java.sql.SQLException, this is the String value
      returned from calling SQLException.getMessage() concatenated with the return value of
      SQLException.getSQLState
  */
  public  String             context;
  /**
      Currently not used for JDBC related errors.
  */
  public  int                category;
  /**
      Currently set to ex.getErrorCode() if the exception is extends from DataSetException
      If the error was caused by an java.sql.SQLException, this is the int value
      returned from calling SQLException.getErrorCode().
  */
  public  int                code;
  /**
      If not null, the Exception that caused this error.
  */
  public  Exception          ex;

  private static final long   serialVersionUID = 1L;

}
