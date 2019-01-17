//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ResolutionException.java,v 7.1 2003/07/18 23:21:44 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;

/**
 * The ResolutionException class defines error constants and behavior
 *  associated with error conditions encountered when resolving changes
 *  made to data back to its data source.
 * It extends DataSetException and may include chained exceptions
 */
public class ResolutionException extends DataSetException {

  private final static int BASE = 1000*5;

  /**
  */
  public  final static int INSERT_FAILED    = BASE+1;
  /** Attempting to assign a value to a readonly dataset.
  */
  public final static int DELETE_FAILED     =  BASE+2;
  /** Master rows that have detail rows linked to them cannot be deleted or have their linking
      columns modified.
  */
  public final static int UPDATE_FAILED     = BASE+3;

  /**
      If the StorageDataSet.MaxResolveErrors property is set, a ResolutionException
      with this error code will be thrown when the maximum errors are encountered.
      The resolution process is aborted (i.e. changes are rolled back).
      @since JB2.0
      @see ResolveError
  */
  public final static int RESOLVE_FAILED   = BASE+4;
  /**
      If the StorageDataSet.MaxResolveErrors property is set, a ResolutionException
      with this error code will be thrown when all rows have been processed.
      Any successfully processed rows will be commited.
      @since JB2.0
      @see ResolveError
  */
  public final static int RESOLVE_PARTIAL   = BASE+5;


  static final void     throwException(int errorCode, DataSet dataSet, String tableName, int id, Exception ex)
    /*-throws DataSetException-*/
  {
    if (ex instanceof DataSetException)
      throw (DataSetException)ex;

    if (tableName == null)
      tableName = dataSet.getTableName();

    String message  = new String( Res.bundle.format(id, new String[] { tableName }));

    throw new ResolutionException(errorCode, dataSet, message, ex);
  }


  static final void     insertFailed(DataSet dataSet, String tableName, Exception ex)
    /*-throws DataSetException-*/
  {
    throwException(INSERT_FAILED, dataSet, tableName, ResIndex.InsertFailed, ex);
  }

  static final void     deleteFailed(DataSet dataSet, String tableName, Exception ex)
    /*-throws DataSetException-*/
  {
    throwException(DELETE_FAILED, dataSet, tableName, ResIndex.DeleteFailed, ex);
  }

  static final void     updateFailed(DataSet dataSet, String tableName, Exception ex)
    /*-throws DataSetException-*/
  {
    throwException(UPDATE_FAILED, dataSet, tableName, ResIndex.UpdateFailed, ex);
  }

  /**
   * This ResolutionException is only used when the {@link com.borland.dx.dataset.StorageDataSet#setMaxResolveErrors setMaxResolveErrors}
   * property has been set to 0.
   *
   * @param errorCode one of the static final int error codes in this class
   * @param dataSet DataSet that resolve error occured
   * @param message Error string for the error encountered.
   * @param ex Exception that was encountered.
   */
  public ResolutionException(int errorCode, DataSet dataSet, String message, Exception ex) {
    super(errorCode, message, ex);
    this.dataSet  = dataSet;
  }

  /**
   * This ResolutionException is only used when the {@link com.borland.dx.dataset.StorageDataSet#setMaxResolveErrors setMaxResolveErrors}
   * property has been set to 1 or higher.
   *
   * @param errorCode one of the static final int error codes in this class
   * @param errorDataSets StorageDataSet[] collection of error datasets.  These errorDataSets have
   * the contents of the row there was a problem with with an extra column that contains the
   * Exception that was encountered.
   * @param message Error string for the error encountered.
   */
  public ResolutionException(int errorCode, StorageDataSet[] errorDataSets, String message) {
    super(errorCode, message);
    this.errorDataSets  = errorDataSets;
  }

  /**
   * This value is is only non-null when the {@link com.borland.dx.dataset.StorageDataSet#setMaxResolveErrors setMaxResolveErrors}
   * property has been set to 1 or higher.
   *
   * Return the DataSet with the inserted, updated or deleted row that encountered
   * an error during the resolution process. A resolution process can be invoked by calling
   * <CODE>DataSet.saveChanges()</CODE> or <CODE>Database.saveChanges()</CODE>.
   * @return dataset
   */
  public DataSet getDataSet() { return dataSet; }

  /**
   * This value is is only non-null when the {@link com.borland.dx.dataset.StorageDataSet#setMaxResolveErrors setMaxResolveErrors}
   * property has been set to 1 or higher.
   * @returns an array of StorageDataSets that contain errors from a resolution
   * process.  A resolution process can be invoked by calling DataSet.saveChanges() or
   * Database.saveChanges().  This property will only be non-null if one or more
   * StorageDataSets that participated in the resolve operation had the
   * StorageDataSet.MaxResolveErrors property set to -1 or a value greater than
   * 0.  The returned array has a StorageDataDataSet entry for each StorageDataSet
   * that participated in the resolve operation.  The order of these errorDataSets
   * in the array corresponds directly to the order of the StorageDataSets in the
   * array of StorageDataSets passed into the resolution manager class.  In the
   * case that Database.saveChanges() is called to invoke the resolution manager,
   * the array passed to this method dictates the order of the errorDataSets array
   * if there are any errors logged.  If there are no errors logged for a
   * StorageDataSet being resolved, its entry in the errorDataSets array will
   * be null.  The structure of the errorDataSets is described in ResolveError.

   * @since JB2.0
   * @see ResolveError
  */
  public StorageDataSet[] getErrorDataSets() { return errorDataSets; }

  public transient DataSet            dataSet;
  private transient StorageDataSet[]   errorDataSets;
}
