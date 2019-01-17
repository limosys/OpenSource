//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/SearchPath.java,v 7.0 2002/08/08 18:40:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.awt.Toolkit;
import java.awt.Image;
import java.io.File;

//!CQ still needs to have ZIP file support added

/**
 * Encapsulates a search path (such as classpath), and can perform searches
 * along that path given a relative directory and file name.
 */
public class SearchPath
{
  private String baseDirList;
  private String relDir;
  private String dirPath;

  /**
   * Constructs a SearchPath object, with the image's full path.
   * @param baseDirList   The name of the image's base directory.
   * @param relDir        The name of the image's directory, relative to the base directory.
   */
  public SearchPath(String baseDirList, String relDir) {
    this.baseDirList = baseDirList;
    this.relDir = relDir;
    //System.err.println("SearchPath: " + baseDir + "+" + relDir + " exists: " + (path.exists() ? "OK" : "NO!"));
  }

  /**
   * Returns a File object containing an image's full path.
   * @param name The name of the image.
   */
  public File getFile(String name) {
    if (dirPath != null)
      return new File(dirPath + File.separator + name + File.separator); // found dir already, use it

    File file = null;
    //System.err.println("SearchPath.getFile: " + this + "+" + name);
    String baseDir = baseDirList;
    while (baseDir.length() > 0) {
      int n = baseDir.indexOf(File.pathSeparatorChar);
      String p;
      if (n == -1) {
        p = baseDir;
        baseDir = "";
      }
      else {
        p = baseDir.substring(0, n);
        baseDir = baseDir.substring(n + 1);
      }
      dirPath = p + File.separator + relDir + File.separator;
      //!CQ check File(dirPath).isDirectory() for dirs
      //!CQ check File(dirPath).isDirectory() for ZIPs & drill down if so
      file = new File(dirPath + name);
      //System.err.println("  trying " + file.getPath());
      if (file.exists()) {
        //System.err.println("  found!");
        baseDirList = relDir = null;  // don't need these anymore, have dirPath
        return file;
      }
    }
    if (file == null || !file.exists()) {
      dirPath = null;
      //System.err.println("  not found! ");
    }
    return file;
  }

  /**
   *  Returns a String containing an image's full path.
   *  @param name The name of the image.
   */
  public String getPath(String name) {
    return getFile(name).getPath();
  }

  /**
   * Converts this SearchPath object into a string representation.
   * @return
   */
  public String toString() {
    return dirPath != null ? dirPath : (baseDirList + "+" + relDir);
  }
}
