//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/ErrorResponse.java,v 7.0 2002/08/08 18:40:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;


/**
 * The <code>ErrorResponse</code> component is used to collect a response to an error
 * from an event listener. It contains three constants that
 *  provide possible responses: abort, ignore, and retry. It also has
 *  three methods that return one of the three responses, three <b>boolean</b>
 *  properties that test for the responses, and a <code>response</code> property
 *  that holds the response value.
 */
public class ErrorResponse
{
  public ErrorResponse() {
    response  = ABORT;
  }

  /**
   *   The response is "abort".
   */
  public static final int ABORT   = 1;
  /**
    *   The response is "ignore".
   */
  public static final int IGNORE  = 2;
  /**
    *   The response is "retry".
   */
  public static final int RETRY   = 3;

 /**
  * Returns a response value of ABORT.
  * Call this method to cause the operation to fail with an exception throw.
  */
  public final void abort() { response  = ABORT; }

/**
 *  Returns a response value of IGNORE.
 *  Call this method to cause the operation to fail without an exception throw.
 */
  public final void ignore() { response  = IGNORE; }

  /**
   *  Returns a reponse of RETRY.
   *  Call this method to attempt to retry the operation that failed.
   */
  public final void retry() { response  =  RETRY; }

  public final int getResponse() { return response;}

  /**
   * Tests to see if the response is ABORT. If it returns
   * <strong>true</strong>, the response is ABORT.
   */
  public final boolean isAbort() { return response == ABORT; }
  /**
   * Tests to see if the response is IGNORE. If it returns
   * <strong>true</strong>, the response is IGNORE.
   */
  public final boolean isIgnore() { return response == IGNORE; }
  /**
   * Tests to see if the response is RETRY. If it returns
   * <strong>true</strong>, the response is RETRY.
   */
  public final boolean isRetry() { return response == RETRY; }

  /**
   * The response:
   * <LI>1 = ABORT
   * <LI>2 = IGNORE
   * <LI>3 = RETRY
   */
    protected int response;
}
