//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/VariantException.java,v 7.0 2002/08/08 18:39:39 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 *  An exception thrown when an error occurs in a Variant object.
 */
public class VariantException extends java.lang.RuntimeException {
/**
 * Constructs a VariantException object that contains the error
 * that occurred in the Variant.
 *
 * @param error   The string containing the error that occurred.
 */
  public VariantException(String error) {
    super(error);
  }

  /**
   *  Throws a VariantException.
   *
   * @param error               The string containing the error that occurred.
   * @throws VariantException
   */
  public static final void fire(String error) throws VariantException {
    throw new VariantException(error);
  }
}
