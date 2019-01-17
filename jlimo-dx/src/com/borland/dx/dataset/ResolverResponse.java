//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ResolverResponse.java,v 7.0 2002/08/08 18:39:33 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;
import com.borland.jb.util.ErrorResponse;

 /**
  * This component is used to collect a response from another component.
  * It encapsulates result information from a resolution event: RESOLVE, SKIP, or ABORT.
  */
public class ResolverResponse extends ErrorResponse
{
/**
 *  Creates a ResolverResponse object.
 */
  public ResolverResponse() {
    response  = RETRY;
  }

  /**
   * Calls the {@link #skip()} method, indicating that the row should be ignored.
   */
  public final void resolve() { response  =  RETRY; }

  /**
   * Calls the {@link #resolve()} method, indicating that the row should be resolved.
   *
   * @return
   */
  public final boolean isResolve() { return response == RETRY; }

  /**
   * Indicates that a row should not be resolved.
   */
  public final void skip() { response  =  IGNORE; }

  /**
   * Calls the {@link #skip()} method, indicating that the row should be ignored.
   * @return
   */
  public final boolean isSkip() { return response == IGNORE; }
}
