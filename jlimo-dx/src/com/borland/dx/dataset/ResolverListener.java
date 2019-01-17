//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ResolverListener.java,v 7.0 2002/08/08 18:39:33 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;
import com.borland.jb.util.ErrorResponse;

/**
 * This interface is used as a notification before and after a StorageDataSet is resolved.
 * This interface includes:
 * <ul>
 *  <li>Methods that occur before a data set is resolved (methods containing "ing").
 *      These events (insertingRow, updatingRow, deletingRow) can be used to validate
 *      the row being resolved, and prevent unwanted changes. Methods that occur after a
 *      data set is resolved (methods containing "ed"). These events (insertedRow,
 *      updatedRow, deletedRow) can be used to indicate that the action has been performed.</li>
 *  <li>Methods that occur when an exception is thrown in response to an attempt to resolve
 *      (methods ending in "Error"). The events (insertError, updateError, deleteError)
 *      can be used to trap errors during resolution, and take the appropriate action,
 *      such as aborting the transaction, ignoring the error, or retrying the resolution.</li>
 * </ul>
 * <p>
 * This listener is added to instances of the QueryResolver class.
 * The QueryResolver is hooked to the StorageDataSet by setting the resolver property
 * of the StorageDataSet.
 */
public interface ResolverListener extends EventListener
{

/**
 * This method is called when just before the insertion of a row into the data set is resolved to the server.
 *
 * @param row           The row that is about to be resolved.
 * @param response      Specify a response of ABORT, IGNORE, or RETRY for this error.
 *                      These constants are defined in util.ErrorResponse.
 */
  public void insertingRow(ReadWriteRow row, ResolverResponse response) /*-throws DataSetException-*/;

  /**
   * This method is called just before the deletion of a row from the data set is resolved on the server.
   *
   * @param row                     The row that is to be deleted.
   * @param response                Specify a response of ABORT, IGNORE, or RETRY for this error.
   *                                These constants are defined in util.ErrorResponse. Note that an
   *                                ABORT response causes all insert, update, and delete operations
   *                                in the same transaction to be rolled back.
   * @throws DataSetException
   */
  public void deletingRow(ReadWriteRow row, ResolverResponse response) throws  DataSetException;

  /**
   * This method is called just before modifications to a row in the data set are resolved on the server.
   * @param row             The row that has been modified.
   * @param oldRow          The original row.
   * @param response        Specify a response of ABORT, IGNORE, or RETRY for this error.
   *                        These constants are defined in util.ErrorResponse.
   */
  public void updatingRow(ReadWriteRow row, ReadRow oldRow, ResolverResponse response)  /*-throws DataSetException-*/;

  /**
   * This method is called when the insertion of a row into the data set has been resolved on the server.
   * @param row   The row that has been inserted and resolved.
   */
  public void insertedRow(ReadWriteRow row) /*-throws DataSetException-*/;

  /**
   * This method is called when the deletion of a row from the data set has been resolved on the server.
   * @param row   The row that has been deleted.
   */
  public void deletedRow(ReadWriteRow row) /*-throws DataSetException-*/;

  /**
   * This method is called when modifications to a row in the data set have been resolved on the server.
   * @param row       The row that has been modified.
   * @param oldRow    The original row.
   */
  public void updatedRow(ReadWriteRow row, ReadRow oldRow) /*-throws DataSetException-*/;

  /**
   * This method is called when an exception is thrown during resolution of an insertion into the DataSet.
   *
    @param dataSet  The original DataSet passsed in to be resolved.  This can be used
                    to position the any controls bound to it if user interaction is needed.

    @param row      The row with the problem.  This can be modified to correct the problem
                    and retry the operation.

    @param ex       The exception that caused the error.  Note that this may be a chained
                    exception.  @see DataSetException.getExceptionChain().

    @param response Specify abort/ignore/retry response for this error.
                    @see com.borland.jb.util.ErrorResponse  Note that an abort response causes
                    the all insert/update/delete operations in the same transaction to be
                    rolled back.

  */
  public void insertError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/;

  /**
   * This method is called when an exception is thrown during resolution of a deletion from the DataSet.
   *
    @param dataSet  The original DataSet passed in to be resolved. This can be used to
                    position any controls bound to it if user interaction is needed.

    @param row      The row with the problem, positioned at the row that caused the error.
                    This can be modified to correct the problem and retry the operation.

    @param ex       The exception that caused the error. Note that this may be a chained exception.

    @param response Specify abort/ignore/retry response for this error.
                    @see com.borland.jb.util.ErrorResponse  Note that an ABORT response causes
                    the all insert/update/delete operations in the same transaction to be
                    rolled back.

    @see            com.borland.dx.dataset.DataSetException

  */
  public void deleteError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) throws  DataSetException;

  /**
   * This method is called when an exception is thrown during resolution of modifications to a
   * row in the DataSet.
   *
    @param dataSet  The original DataSet passsed in to be resolved.  This can be used
                    to position the any controls bound to it if user interaction is needed.

    @param row      The row with the problem.  This can be modified to correct the problem
                    and retry the operation.

    @param oldRow   The original row.

    @param updRow   The row to use for the next update query.  At input this is a copy of
                    the original row.  Pass this to DataSet.refetchRow().

    @param ex       The exception that caused the error.  Note that this may be a chained
                    exception.  @see DataSetException.getExceptionChain().

    @param response Specify abort/ignore/retry response for this error.
                    @see com.borland.jb.util.ErrorResponse  Note that an abort response causes
                    the all insert/update/delete operations in the same transaction to be
                    rolled back.

  */
  public void updateError(DataSet dataSet, ReadWriteRow row, ReadRow oldRow, ReadWriteRow updRow, DataSetException ex, ErrorResponse response)  /*-throws DataSetException-*/;
}

