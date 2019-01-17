//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/ArrayResourceBundle.java,v 7.0 2002/08/08 18:40:48 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.util.*;

/**
 * The <CODE>ArrayResourceBundle</CODE> is an abstract subclass of
 * <CODE>java.util.ResourceBundle</CODE> that manages locale-
 * dependent resources in an array.  By using numeric references
 * rather than string references, it requires less overhead and
 * provides better performance than <CODE>
 * java.util.PropertyResourceBundle</CODE> and <CODE>
 * java.util.ListResourceBundle</CODE>
 *
 * <P>Subclasses must override the <CODE>getContents()</CODE> method
 * and provide an array, where each item in the array is the
 * resource value.  The key for each resource value is its numeric
 * offset in the array.  For example, the first element in the array
 * has the key 0.  It may be retrieved by using either <CODE>
 * getObject(0)</CODE> or <CODE>getObject("0")</CODE>.
 *
 * <P>Unlike <CODE>ListResourceBundle</CODE> and <CODE>
 * PropertyResourceBundle</CODE>, where each locale-specific
 * variation of a bundle can override only selected resources, each
 * variation of <CODE>ArrayResourceBundle</CODE> must provide the
 * complete set of resources.  For example, if the custom class
 * <CODE>MyResources</CODE> has three resources, then its subclasses
 * <CODE>MyResources_ja</CODE> and <CODE>MyResources_fr</CODE>  must
 * also have three resources.
 *
 * <p>The following example shows the structure of a ResourceBundle
 * based on ArrayResourceBundle.
 * <pre>
 * class MyResource extends ArrayResourceBundle {
 *      public Object[] getContents() {
 *              return contents;
 *      }
 *      static final Object[] contents = {
 *      // LOCALIZE THIS
 *              "Yes",    // Label for the YES button
 *              "No",     // Label for the NO button
 *              "Cancel"  // Label for the CANCEL button
 *      // END OF MATERIAL TO LOCALIZE
 *      };
 * }
 * </pre>
 */

 /*
  * broken links
 * @see ResourceBundle
 * @see ListResourceBundle
 * @see PropertyResourceBundle
 */
public abstract class ArrayResourceBundle extends ResourceBundle {

   /**
    * Gets an object from an ArrayResourceBundle. This is a convenience
    * method that saves the extra step of casting by returning a String.
    * If an error occurrs, getString(int) throws a MissingResourceException.
    * @param key    The numeric offset of the resource value in the array.
    * @return An object from an ArrayResourceBundle
    * @throws MissingResourceException Signals that a resource is missing.
    */
  public final String getString(int key) throws MissingResourceException {
    return (String) getObject(key);
  }

   /**
    *  Gets an object from a ResourceBundle. This is a convenience method
    *  that saves the extra step of casting by returning a String. If an
    *  error occurrs, getStringArray(int) throws a MissingResourceException.
    * @param key    The numeric offset of the resource value in the array.
    * @return An object from a ResourceBundle
    * @exception MissingResourceException
    */
  public final String[] getStringArray(int key)
    throws MissingResourceException {
    return (String[]) getObject(key);
  }

  private Object[] contents;

/**
 * Gets the contents of the array. See "About the ArrayResourceBundle class"
 * for more information.
 *
 * @return The contents of the array
 */
  protected abstract Object[] getContents();
//!RC 3/29/99 Made this method protected again since obfuscation bug is now fixed

/**
 * Gets an object from a ResourceBundle. If the specified key is not found,
 * handleGetObject must return null.
 *
 * @param key   The numeric offset of the resource value in the array
 * @return An object from a ResourceBundle
 */
  protected Object handleGetObject(String key) {
    return getObject(Integer.parseInt(key));
  }

  /**
   * Gets an element in the array. If index is 0, the first element
   * in the array is retrieved.
   *
   * @param index The element in the array to retrieve
   * @return The contents of the requested array element
   */
  public Object getObject(int index) {
    if (contents == null)
      contents = getContents();
    try {
      return contents[index];
    }
    catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  /**
   * Returns an enumeration of the keys.
   */
  public Enumeration getKeys() {
    if (contents == null)
      contents = getContents();
    Vector result = new Vector(contents.length);
    for (int i=0; i<contents.length; i++) {
      result.addElement(String.valueOf(i));
    }
    return result.elements();
  }
}
