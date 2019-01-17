//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ResponseEvent.java,v 7.0 2002/08/08 18:39:34 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DispatchableEvent;
import java.util.EventListener;

 /**
  * The ResponseEvent class is used for collecting a response from an application
  * about how to deal with error conditons, table restructuring operations, key violations, etc.
  */
public class ResponseEvent extends com.borland.jb.util.DispatchableEvent
{

  /**
      Restructure operation is converting from one data type to another.
      Old values will not be converted to the new data type.
      Call ok() to continue.  Call cancel() to abort or ignoreAll() to continue and ignore any future messages
      of this type.
  */

  /**
   * The restructure operation is converting data from one type to another.
   * Old values will not be converted to the new data type.
   * <p>
   * <ol>
   *    <li>Call ok() to continue.                                                    </li>
   *    <li>Call cancel() to abort.                                                   </li>
   *    <li>Call ignoreAll() to continue and ignore any future messages of this type. </li>
   *  </ol>
   */
  public final static int TYPE_CHANGE_DATA_LOSS         = 1;
  /**
      Restructure operation is converting from one data type to another.
      Type conversion may result in precision lose when values of the old data type
      are converted to values of the new data type.
      Call ok() to continue.  Call cancel() to abort or ignoreAll() to continue and ignore any future messages
      of this type.
  */
  /**
   * The restructure operation is converting data from one type to another.
   * This conversion might result in a loss of precision when values of the old data type
   * are converted to values of the new data type.
   * <p>
   * <ol>
   *    <li>Call ok() to continue.                                                    </li>
   *    <li>Call cancel() to abort.                                                   </li>
   *    <li>Call ignoreAll() to continue and ignore any future messages of this type. </li>
   *  </ol>
   */
  public final static int TYPE_CHANGE_PRECISION_LOSS    = 2;
  /**
      Restructure operation is converting from one data type to another.
      A parse error occured converting a String data type to a non String
      data type.
      Call ok() to continue.  Call cancel() to abort or ignoreAll() to continue and ignore any future messages
      of this type.
  */
  /**
   * The restructure operation is converting data from one type to another.
   * A parse error occurred while converting a String data type to a non-String data type.
   * <p>
   * <ol>
   *    <li>Call ok() to continue.                                                    </li>
   *    <li>Call cancel() to abort.                                                   </li>
   *    <li>Call ignoreAll() to continue and ignore any future messages of this type. </li>
   *  </ol>
   */
  public final static int TYPE_CHANGE_PARSE_ERROR       = 3;
  /**
      Restructure operation encountered 1 or more TYPE_CHANGE_PARSE_ERRORS.
      This is sent out just before the restructure operation is about to be
      committed.  Select cancel() to abort the restructure operation.
      Call ok() to continue.  Call cancel() to abort or ignoreAll() to continue and ignore any future messages
      of this type.
  */
  /**
   * The restructure operation encountered one or more TYPE_CHANGE_PARSE_ERROR occurrances.
   * The error is sent out just before the restructure operation is about to be committed.
   * <p>
   * <ol>
   *    <li>Call ok() to continue.                                                    </li>
   *    <li>Call cancel() to abort.                                                   </li>
   *    <li>Call ignoreAll() to continue and ignore any future messages of this type. </li>
   *  </ol>
   */
  public final static int TYPE_CHANGE_PARSE_ERROR_TOTAL = 4;
  /**
      The {@link com.borland.datastore.DataStore} appears to already be open
      by this process or another process.

      Call ok() to attempt to determine if the DataStore is really still opened.  If it
      is determined that the DataStore is no longer open, a {@link #DATASTORE_CAN_REOPEN}
      ResponseEvent will be sent and the open process will continue.

      Call cancel() to cause this open operation to fail.
  */
  public final static int DATASTORE_ALREADY_OPEN        = 5;
  /**
      The {@link com.borland.datastore.DataStore} was left open, but the process
      that had it open has terminated. This message comes after DATASTORE_ALREADY_OPEN.

      Call ok() to continue the DataStore open operation.
      Call cancel() to cause this open operation to fail.
  */
  public final static int DATASTORE_CAN_REOPEN        = 6;

  /**
   * The DataStore sends this response out whenever there is a read or write failure.
   *
   * A common source for this response event would be when there is insufficient disk space
   * to increase the size of the DataStore file when needed. T
   *
   * he source of the error is set to the DataStore instance and exception is set to the
   * IOException that was encountered.
   *
   * Call ok() to have the I/O operation retried.
   * Call cancel() to cause this operation to fail.
   */
  public final static int IOEXCEPTION                  = 7;

//!  /** @since JB3.0
//!      DataStore sends this response out whenever there duplicate key values are
//!      found while building a secondary index for a SortDescriptor that has
//!      unique property set.
//!      Call ok() to have the io operation retried.
//!      Call cancel() to cause this operation to fail.
//!  */
//!  public final static int KEY_VIOLATION                = 8;


  /**
   * Operation cannot continue because the file name in the message already exists.
   *
   * Call ok() if you have deleted the file so that the operation can continue.
   * Call cancel() to cause this operation to fail.
   */
  public final static int FILE_EXISTS                = 9;

  /**
   *  A DataStore log file is about to be deleted because it is no longer needed
   *  for any active transaction or for crash recovery.
   *
   *  This will be called for the "A" log files, "B" log files (if the log is being duplexed),
   *  and status log files (if status logging is enabled).
   *
   *  Call the ok() method if you have deleted the file so that the operation can continue.
   *  Call the cancel() method to cause this operation to fail.
   */
  public final static int DROP_LOG                = 10;

/**
 *  The DataStore.shutdown() method was not called for the source DataStore by
 *  the last process that accessed it.
 *  This notification comes just before the system attempts to recover.
 *
 *  Call ok() to proceed with recovery. Call cancel() to cause this operation to fail.
 */
  public final static int DATASTORE_RECOVERING    = 11;

  /**
   * DataStoreConnection is closing and the current transaction is about to be committed.
   * <p>
   * <ol>
   *    <li>Call ok() to proceed with the commit().                           </li>
   *    <li>Call cancel() to cause rollback() to be called on the connection. </li>
   *  </ol>
   */
  public final static int COMMIT_ON_CLOSE    = 12;

  /** @since JB3.0
      DataStore file cannot be opened.  An attempt to open the file in read only
      mode is about to be made.
      Call ok() to proceed with readOnly mode.
      Call cancel() to cause the open to fail with an exception throw.
  */
  /**
   *  The DataStore file cannot be opened.
   *  An attempt to open the file in read-only mode is about to be made.
   *
   * <ol>
   *    <li>Call ok() to proceed open the file in readOnly mode.             </li>
   *    <li>Call cancel() to fail to open the file and to throw an exception. </li>
   *  </ol>
   */
  public final static int READ_ONLY_OPEN    = 13;


  public ResponseEvent(Object source, int code, String message) {
    super(source);
    this.message    = message;
    this.response   = OK;
    this.code       = code;
  }
  public ResponseEvent(Object source, int code, Exception ex) {
    super(source);
    this.message    = ex.getMessage();
    this.ex         = ex;
    this.response   = OK;
    this.code       = code;
  }

  /**
   * Response code requesting that the operation be continued.
   */
  public static final int OK                  = 1;
  /**
   * Response code requesting that the operation be canceled.
  */
  public static final int CANCEL              = 2;

  /**
   * Response code requesting that any more error/response requests of this code
   * should be ignored for the duration of this operation.
   */
  public static final int IGNORE_ALL          = 3;

  /**
   * This method is an implementation of DispatchableEvent that an
   * EventMulticaster uses to dispatch an event of this type to the listener.
   * @param listener    The listener to dispatch this event to.
   * @see com.borland.jb.util.DispatchableEvent
   * @see com.borland.jb.util.EventMulticaster
   */
  public void dispatch(EventListener listener) {
    ((ResponseListener)listener).response(this);
  }

  /** Call to acknowledge reciept of the ResponseEvent.  The operation
      will continue if possible.
  */
  public final void ok() { response  = OK; }

  /**
   * Fails the operation. An Exception may be thrown to cancel the operation.
   */
  public final void cancel() { response  = CANCEL; }

  /**
   *  Causes all future errors/response requests with this event's code
   *  to be ignored. The operation then continues, if possible.
   */
  public final void ignoreAll() { response  = IGNORE_ALL; }

  /**
   * Returns information on what needs to be responded on.
   * @return Information on what needs to be responded on.
  */
  public final String getMessage() { return message;}

  /**
   * Returns response setting of OK or CANCEL.
   * @return The response setting of OK or CANCEL.
  */
  public final int getResponse() { return response;}

  /**
   * Returns code value.  See code constants above.
   * @return Code value.
  */
  public final int getCode() { return code;}

  /**
   * Returns Exception if set.  Otherwise null is returned.
   * @return Exception if set.
  */
  public final Exception getException() { return ex;}

  /**
   * Returns <b>true</b> if response == OK.
   * @return <b>true</b> if response == OK.
  */
  public final boolean isOk() { return response == OK; }

  /**
   * Returns <b>true</b> if response == CANCEL.
   * @return <b>true</b> if response == CANCEL.
   */
  public final boolean isCancel() { return response == CANCEL; }

  /**
   * Returns <b>true</b> if response == CANCEL.
   * @return <b>true</b> if response == CANCEL.
  */
  public final boolean isIgnoreAll() { return response == IGNORE_ALL; }

  private String    message;
  private int       response;
  private int       code;
  private Exception ex;
}
