//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/EditListener.java,v 7.0 2002/08/08 18:39:25 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ErrorResponse;
import java.util.*;

/**
 * Listens for events that are generated when a DataSet is edited,
 * both before and after editing.
 *
 * This interface is used as a notification for row editing before and after edit-related operations are completed
and includes
<UL>
<LI>Methods that occur before a row is posted (methods ending in "ing")
<LI>Methods that occur after a row is posted (methods ending in "ed")
<LI>Methods that occur when an exception is thrown in response to an edit (methods ending in "Error")
</UL>

<P>With an <CODE>EditListener</CODE>, you can
<UL>
<LI>Block adding, deleting, or updating of rows.  This is useful when different users have different access rights, or have rights that are dependent on data values.
Note that the <CODE>DataSet</CODE> enable, insert, delete, and update properties can be used to block certain types of editing.<LI><P>Perform row-level validation just before a row is posted.  Use the <CODE>adding()</CODE> method for new rows and the <CODE>updating()</CODE> method for modified rows.<LI><P>Get control after one of these operations.  For example, you can initialize values in fields after a row is inserted, but before the user begins data entry.<LI><P>Process a <CODE>ValidationException</CODE> for all events.  An exception class that derives from <CODE>Exception</CODE> can be thrown.  These exceptions are caught by the <CODE>DataSet</CODE> event dispatcher and chained into a special <CODE>ValidationException</CODE>.  This <CODE>ValidationException</CODE> has an error code of <CODE>ValidationException.APPLICATION_ERROR</CODE> and the message from the exception that was caught.  This chained <CODE>ValidationException</CODE> is then thrown so that normal <CODE>DataSet</CODE> error handling can deal with the problem.
<!-- JDS start - remove sentence -->
If a JBCL <CODE>StatusBar</CODE> or a dbSwing <CODE>JdbStatusLabel</CODE> is bound to a <CODE>DataSet</CODE>, the message from the user thrown exception is displayed in the status bar.
</UL>
<!-- JDS end -->

<P>Inserting, adding, and updating methods each have a unique purpose.  Insert methods create a new, unposted row. The new, unposted row is sometimes called a <CODE>pseudo-row</CODE> because it does not exist in the data set until it is posted.  Add methods work on newly inserted rows when they are about to be or have been posted.  Update methods work on existing rows only, at the time that modifications to them are about to be or have been posted.

<!-- JDS start - remove for example phrase in last sentence of paragraph -->
<P>A simple way for an application to pass an error message to display in the UI involves the <CODE>EditListener</CODE> before event (those that end in "ing"). These events can be wired to throw a  Exception("custom message"). In turn, this gets thrown as a chained <CODE>ValidationException</CODE> that copies the "custom message" as the message for the <CODE>ValidationException</CODE>. Since all  <CODE>ValidationExceptions</CODE> go to a <CODE>StatusListener</CODE>, for example the JBCL <CODE>StatusBar</CODE> or a dbSwing <CODE>JdbStatusLabel</CODE> control, the custom message displays in the application's UI.
<!-- JDS end -->



 */
public interface EditListener extends EventListener
{
  /**
   * This is an event to notify listeners when the editing of a new or
   * existing row in a DataSet is about to be canceled. An application might
   * use this event to save undo information.
   *
   * @param dataSet     The data set to which edits are about to be canceled.
   * @throws Exception
   */
  void canceling(DataSet dataSet) throws Exception;

  /**
   * This is an event to notify listeners before a modified row is posted to the
   * DataSet. If an exception is thrown inside this method, the post operation is
   * not performed, a ValidationException with an error code of APPLICATION_ERROR
   * is thrown instead. The updating() method is called before checks to make sure
   * all required fields are not null. If a VetoException or Exception is
   * constructed with a STRING parameter, this STRING is used in the default
   * error handling displays, for example,
   * <p>
   * <code>
   * throw new VetoException("My error message");
   * </code>
   *
   * @param dataSet   The data set to which a modified row is about to be posted.
   * @param newRow    The row containing the modified data.
   * @param oldRow    The row containing the data in the row prior to modification.
   * @throws Exception
   */
  void updating(DataSet dataSet, ReadWriteRow newRow, ReadRow oldRow) throws Exception;

  /**
   * This is an event to notify listeners that a modified row has been
   * successfully posted to a DataSet.
   *
   * @param dataSet   The data set to which the modified row has has been posted.
   */
  void updated(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   * This is an event to notify listeners before a new row is posted to the DataSet.
   * This event is fired by DataSet.addRow(), which inserts, modifies, and posts a
   * row all in one operation. If a VetoException or Exception is thrown inside
   * this method, the post operation is not performed, a ValidationException with
   * an error code of APPLICATION_ERROR is thrown instead. The adding() method is
   * called before checks to make sure all required fields are not null. If a
   * VetoException or Exception is constructed with a STRING parameter, this STRING
   * is used in the default error handling displays, for example,
   * <p>
   * <code>
   * throw new VetoException("My error message");
   * </code>
   *
   * @param dataSet   The dataSet that the row will be posted to.
   * @param newRow    The row that is to be inserted, modified, and posted.
   * @throws Exception
   */
  void adding(DataSet dataSet, ReadWriteRow newRow) throws Exception;

  /**
   * This is an event to notify listeners that a new row is successfully posted to the DataSet.
   * This event is fired by {@link DataSet#addRow}, which inserts, modifies,
   * and posts a row all in one operation.
   *
   * @param dataSet   The data set the row was added to.
   */
  void added(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   *  This is an event to notify listeners before a row is deleted from the DataSet.
   *  If a VetoException or Exception is thrown inside this method, the delete operation
   *  is not performed, a ValidationException with an error code of APPLICATION_ERROR
   *  is thrown instead. If a VetoException or Exception is constructed with a STRING parameter,
   *  this STRING is used in the default error handling displays, for example,
   *  <p>
   *  <code>
   *  throw new VetoException("My error message");
   *  </code>
   *
   * @param dataSet       The data set from which a row is about to be deleted.
   * @throws Exception
   */
  void deleting(DataSet dataSet) throws Exception;

  /**
   * This is an event to notify listeners that a successful delete
   * operation has been performed.
   *
   * @param dataSet   The data set from which rows have been deleted.
   */
  void deleted(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   * This is an event to notify listeners when a user begins to modify an
   * existing row. If a VetoException or Exception is thrown inside this method,
   * the modify operation is not performed, a ValidationException with an error
   * code of APPLICATION_ERROR is thrown instead. If a VetoException or Exception
   * is constructed with a STRING parameter, this STRING is used in the default
   * error handling displays, for example,
   * <p>
   * <code>
   * throw new VetoException("My error message");
   * </code>
   *
   * @param dataSet     The data set that contains the row being modified.
   * @throws Exception
   */
  void modifying(DataSet dataSet) throws Exception;

  /**
   * This is an event to notify listeners just before a DataSet attempts to
   * insert a new, unposted row. If a VetoException or Exception is thrown
   * inside this method, the insert operation is not performed, a
   * ValidationException with an error code of APPLICATION_ERROR is thrown
   * instead. If a VetoException or Exception is constructed with a STRING
   * parameter, this STRING is used in the default error handling displays, for example,
   * <p>
   * <code>
   * throw new VetoException("My error message");
   * </code>
   *
   * @param dataSet     The data set to which the row is about to be inserted.
   * @throws Exception
   */
  void inserting(DataSet dataSet) throws Exception;

  /**
   *  This is an event to notify listeners that a new, unposted row is inserted
   *  into the DataSet. This event can be used to initialize row values of new rows.
   *
   * @param dataSet     The data set to which the row has just been inserted.
   */
  void inserted(DataSet dataSet) /*-throws DataSetException-*/;

  /**
   *  This is an event to notify listeners when any exceptions occur setting a column value.
   *  This includes validation check failures as well as any VetoExceptions thrown by a
   *  ColumnChangeListener.validating() event handler. The ErrorResponse object allows the
   *  user to indicate how the error should be handled. Call response.abort() (the default)
   *  to cause the operation to fail with an appropriate DataSetException or ValidationException.
   *  Call response.retry() to cause the operation to be retried. Be sure that the retry will
   *  succeed or that your code can handle repeated retries. Call response.ignore() to cause
   *  the operation to silently fail without an exception being thrown.
   *
   * @param dataSet     The data set that has the error.
   * @param column      The column that contains the error.
   * @param value       The value that causes the error.
   * @param ex          The type of exception that was thrown.
   * @param response    The type of response to the error.
   */
  void editError(DataSet dataSet, Column column, Variant value, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/;

  /**
   * This is an event to notify listeners when an exception is thrown for row
   * add operations. Call response.abort() (the default) to cause the operation
   * to fail with an appropriate DataSetException or ValidationException.
   * Call response.retry() to cause the operation to be retried.
   * Be sure that the retry will succeed or that your code can handle
   * repeated retries. Call response.ignore() to cause the operation to
   * silently fail without an exception being thrown.
   *
   * @param dataSet     The data set that has the error.
   * @param row         The row that contains the error.
   * @param ex          The type of exception that was thrown.
   * @param response    The type of response to the error.
   */
  void addError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/;

  /**
   *  This is an event to notify listeners when an exception is thrown for row changes.
   *  Call response.abort() (the default) to cause the operation to fail with an
   *  appropriate DataSetException or ValidationException. Call response.retry() to
   *  cause the operation to be retried. Be sure that the retry will succeed or that
   *  your code can handle repeated retries. Call response.ignore() to cause the
   *  operation to silently fail without an exception being thrown.
   *
   * @param dataSet   The data set that has the error.
   * @param row       The row that contains the error.
   * @param ex        The type of exception that was thrown.
   * @param response  The type of response to the error.
   */
  void updateError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/;

  /**
   *  This is an event to notify listeners when an exception is thrown for row
   *  delete operations. Call response.abort() (the default) to causes the operation
   *  to fail with an appropriate DataSetException or ValidationException.
   *  Call response.retry() to cause the operation to be retried. Be sure that the
   *  retry will succeed or that your code can handle repeated retries. Call
   *  response.ignore() to cause the operation to silently fail without throwing an exception.
   *
   * @param dataSet      The data set that has the error.
   * @param ex           The type of exception that was thrown.
   * @param response     The type of response to the error.
   */
  void deleteError(DataSet dataSet, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/;

}
