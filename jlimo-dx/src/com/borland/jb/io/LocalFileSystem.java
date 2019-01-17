//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/LocalFileSystem.java,v 7.0 2002/08/08 18:40:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import com.borland.jb.util.DiagnosticJLimo;

import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

/**
 * This class is used internally by other
 * <code>com.borland</code> classes. You should
 * never use this class directly.
 */
public class LocalFileSystem implements FileSystem
{
  // To make this abstraction complete need a corresponding File interface
  // and supporting object(s).  Just deal with plain File for now.
  //
  // To bring complete FileMan functionality here, need native functions for
  // delete directory (only have delete file), and setLastModified().
  //

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public String createFilePath(String path, String file) {
    return FileUtil.createFilePath(path, file);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public void copyFile(String src, String dest) throws IOException {
    FileUtil.copyFile(src, dest);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public void copyFile(String src, String dest, String header) throws IOException {
    FileUtil.copyFile(src, dest, header);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public boolean compareBytes(byte[] buf1, byte[] buf2, int count) {
    return FileUtil.compareBytes(buf1, buf2, count);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public boolean compareFiles(String path1, String path2) throws IOException {
    return FileUtil.compareFiles(path1, path2);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public void deleteDirectoryTree(String dirPath) throws Exception {
    DiagnosticJLimo.fail();
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public boolean traverse(TraverseAction act, File dir) throws Exception {
    return FileUtil.traverse(act, dir);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public Properties loadProperties(String path) throws IOException {
    return FileUtil.loadProperties(path);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public boolean makeDirs(String path) {
    return FileUtil.makeDirs(path);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public boolean makeDir(String path) {
    return FileUtil.makeDir(path);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public String addDir(String path, String dir) {
    return FileUtil.addDir(path, dir);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public int listContains(String[] baseList, String name) {
    return FileUtil.listContains(baseList, name);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public File newFile(String path) {
    return FileUtil.newFile(path);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public File getClassesFile(String fileName) {
    return FileUtil.getClassesFile(fileName);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public File getClassesFile(Object object) {
    return FileUtil.getClassesFile(object);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public String getClassesDirectory(Object object) {
    return FileUtil.getClassesDirectory(object);
  }
}
