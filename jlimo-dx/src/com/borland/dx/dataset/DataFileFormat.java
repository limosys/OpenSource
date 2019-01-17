//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataFileFormat.java,v 7.0 2002/08/08 18:39:21 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

/**
 * This class defines localization variables related to data storage.
 */

 /* Not going to work with JavaDoc!
 * Its variables state whether:
 * <p>
 * <ul>
 *    <li>the data is stored as 8-bit ASCII characters</li>
 *    <li>conversations from locale-specific Unicode to multibyte character
 *        sets need to take place when reading and writing data</li>
 * </ul>
 * <p>
 * The DataFileFormat is the default of the TextDataFile component's fileFormat property.
 */
public class DataFileFormat {

  /**
   * Data is read and written as 8-bit ASCII values.
   */
  public static final int ASCII          = 1;

 /**
  * Data is read and written using locale-specific Unicode to multi-byte conversions.
  */
  public static final int ENCODED      = 2;
}
