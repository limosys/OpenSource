//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/FileSystem.java,v 7.0 2002/08/08 18:40:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.*;
import java.util.*;

/**
  * This interface is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this interface directly.
  */
public interface FileSystem
{
  String createFilePath(String path, String file);

  /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  void copyFile(String src, String dest) throws IOException;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  void copyFile(String src, String dest, String header) throws IOException;

  /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  boolean compareBytes(byte[] buf1, byte[] buf2, int count);

  /**
   * This method is used internally by other
   * <code>com.borland</code> classes. You should
   * never use this method directly.
 */
  boolean compareFiles(String path1, String path2) throws IOException;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  void deleteDirectoryTree(String dirPath) throws Exception;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  boolean traverse(TraverseAction act, File dir) throws Exception;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  Properties loadProperties(String path) throws IOException;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  boolean makeDirs(String path);

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  boolean makeDir(String path);

  /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  String addDir(String path, String dir);

  // This is here because the list() method of the File class returns a String[]
  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  int listContains(String[] baseList, String name);

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  File newFile(String path);

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  File getClassesFile(String file);
  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  File getClassesFile(Object object);
}
