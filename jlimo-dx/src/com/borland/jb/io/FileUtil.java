//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/FileUtil.java,v 7.2 2004/01/21 19:03:45 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.*;
import com.borland.jb.util.PatternMatch;
import com.borland.jb.util.FastStringBuffer;
import java.util.*;

public class FileUtil implements java.io.FilenameFilter {

  public final boolean accept(File file, String name) {
    return match.match(name);
  }

  public static final String[] list(String dirName, String pattern) {
    FileUtil fileUtil   = new FileUtil();
    fileUtil.match      = new PatternMatch();
    fileUtil.match.setPattern(pattern);
    fileUtil.match.validate();
    return new File(dirName).list(fileUtil);
  }

  public static final String[] listTree(String dir, String pattern) {
    Vector list = new Vector();
    PatternMatch match      = new PatternMatch();
    match.setPattern(pattern);
    match.validate();

    listTree(list, dir, dir, match);

    String[] dirList = new String[list.size()];
    list.copyInto(dirList);
    return dirList;
  }
  private static final void listTree(Vector list, String startDir, String dir, PatternMatch match) {

    String[] dirList = new File(dir).list();
    String fileName;

    if (dirList != null) {
      for (int index = 0; index < dirList.length; ++index) {
        fileName  = dir+File.separator+dirList[index];
        if ((new File(fileName).isDirectory())) {
          listTree(list, startDir, fileName, match);
        }
        else if (match.match(dirList[index]))
          list.addElement(fileName.substring(startDir.length()+1));
      }
    }
  }


  public static final void deleteFile(String name) {
    new File(name).delete();
  }

  public static final void deleteFiles(String dirName, String pattern)

  {
    String[] list = FileUtil.list(dirName, pattern);
    if (list != null) {
      for (int index = 0; index < list.length; ++index) {
        new File(dirName+File.separator+list[index]).delete();
      }
    }
  }

  public static final void deleteFilesThatStartWith(String dirName, String prefix)

  {
    deleteFiles(dirName, prefix+"*");
  }

  public static final void deleteFilesThatStartWith(String fileName) {
    File file = new File(sanitizePath(fileName));
    deleteFilesThatStartWith(file.getParent(), file.getName());
  }

  private static final String sanitizePath(String path) {
    if (sanitizeNeeded == null) {
      sanitizeNeeded = (System.getProperty("java.version").startsWith("1.1.") ? Boolean.TRUE : Boolean.FALSE);  //NORES
    }
    if (sanitizeNeeded.booleanValue())
      path = path.replace('/',File.separatorChar);
    return path;
  }

  public static final void copyFilesWithoutCr(String src, String dest, String pattern)
  {
    String[] list = FileUtil.list(src, pattern);
    String srcName;
    if (list != null) {
      for (int index = 0; index < list.length; ++index) {
        srcName = src + File.separator + list[index];
        if (!new File(srcName).isDirectory())
            copyFileWithoutCr(srcName, dest + File.separator + list[index]);
      }
    }
  }

  public static final void copyFiles(String src, String dest, String pattern)

  {
    makeDirs(dest);
    String[] list = FileUtil.list(src, pattern);
    String srcName;
    if (list != null) {
      for (int index = 0; index < list.length; ++index) {
        srcName = src + File.separator + list[index];
        if (!new File(srcName).isDirectory())
          copyFile(srcName, dest + File.separator + list[index]);
      }
    }
  }

  public static final boolean fileExists(String dirName, String pattern)
  {
    String[] list = FileUtil.list(dirName, pattern);
    return list != null && list.length > 0;
  }


  public static final void deleteTree(String dir) {
    if (new File(dir).exists())
      _deleteTree(dir);
  }


  private static final void _deleteTree(String dir)

  {
    File file = new File(dir);
    if (!file.delete()) {
      if (file.isDirectory()) {
        String[] list = file.list();
        for (int index = 0; index < list.length; ++index) {
          _deleteTree(dir + File.separator + list[index]);
        }
        if (file.delete())
          file = null;
      }

      if (file != null)
        throw new FileUtilException("Cannot delete:  "+dir);
    }
  }

  public static final void copyTree(String source, String dest, String pattern) {
    PatternMatch match = null;
    if (pattern != null) {
      match = new PatternMatch();
      match.setPattern(pattern);
      match.validate();
    }
    if (new File(source).exists()) {
      makeSubDirs(dest);
      _copyTree(source, dest, match);
    }
  }


  private static final void _copyTree(String source, String dest, PatternMatch match)

  {
    File file = new File(source);
    if (file.isDirectory()) {
      String[] list = file.list();
      makeDirs(dest);
      for (int index = 0; index < list.length; ++index) {
        _copyTree(source + File.separator + list[index], dest + File.separator + list[index], match);
      }
    }
    else if (match == null || match.match(source)) {
      copyFile(source, dest);
    }
  }

  public static final String makeSubDirs(String fileName) {
    char ch;
    int length = fileName.length();
    String subDir;
    int lastDirIndex = -1;
    for (int index = 0; index < length; ++index) {
      ch = fileName.charAt(index);
      if (ch == File.separatorChar || ch == '/' || ch == '\\' ) {
        lastDirIndex = index;
      }
    }
    if (lastDirIndex >= 0) {
      File file = new File(fileName.substring(0, lastDirIndex));
      file.mkdirs();
      if (!file.exists())
        throw new FileUtilException("Cannot make sub dirs for:  "+file.getAbsoluteFile());
    }
    return fileName;
  }

  public static final String findParentFile(String startDir, String name)

  {
    String  dirPath = startDir;
    String  filePath = dirPath;
    int     index;
    while (true) {
      filePath = dirPath + File.separator + name;
      if (new File(filePath).exists())
        return filePath;
      dirPath  = dirPath.substring(0, dirPath.length()-1);
      index = dirPath.lastIndexOf(File.separator);
      if (index < 0)
        break;
      dirPath  = dirPath.substring(0, index);
    }
    throw new FileUtilException("Cannot find parent file named "+name+" from "+startDir);
  }

  private static final int findFile(Vector list, String home, String dir, PatternMatch pm, int count) {
    File dirFile = new File(dir);
    File file;
    String[] dirList = dirFile.list();
    if (dirList != null) {
      String path;
      String dirPath = dir;
      if (dir.length() > 0)
        dirPath += '/';
      for (int index = 0; index < dirList.length && count > 0; ++index) {
        path = dirPath + dirList[index];
        file = new File(path);
        if (pm.match(path)) {
          list.add(path.substring(home.length()));
          if (--count == 0)
            return count;
        }
        if (file.isDirectory())
          count = findFile(list, home, path, pm, count);
      }
    }
    return count;
  }

  public static final String[] findFiles(String startDir, String pattern, int max) {
    Vector list = new Vector();
    PatternMatch pm = new PatternMatch();
    pm.setPattern(pattern);
    pm.validate();
    findFile(list, startDir, startDir, pm, max);
    String[] fileNames = new String[list.size()];
    list.copyInto(fileNames);
    return fileNames;
  }


  private static final void findDir(Vector list, String home, String dir, PatternMatch pm) {
    File dirFile = new File(dir);
    File file;
    String[] dirList = dirFile.list();
    if (dirList != null) {
      String path;
      String dirPath = dir;
      if (dir.length() > 0)
        dirPath += '/';
      for (int index = 0; index < dirList.length; ++index) {
        path = dirPath + dirList[index];
        file = new File(path);
        if (file.isDirectory() && pm.match(path)) {
          list.add(path.substring(home.length()+1));
          findDir(list, home, path, pm);
        }
      }
    }
  }

  public static final String[] findDirs(String startDir, String pattern) {
    Vector list = new Vector();
    PatternMatch pm = new PatternMatch();
    pm.setPattern(pattern);
    pm.validate();
    if (pm.match(startDir))
      list.addElement("");
    findDir(list, startDir, startDir, pm);
    String[] dirs = new String[list.size()];
    list.copyInto(dirs);
    return dirs;
  }


 public static String getClassPath() {
   return System.getProperties().getProperty("java.class.path", ""); //NORES
 }

 public static String [] getSearchPathElements(String searchPath) {
    Vector v = new Vector();
    while ( searchPath.length() > 0 ) {
      int separatorIndex = searchPath.indexOf(File.pathSeparatorChar);
      if ( separatorIndex != - 1) {
        String s = searchPath.substring(0, separatorIndex++);
        if ( s.length() > 0 ) {
          v.addElement(s);
        }
        if ( separatorIndex < searchPath.length() ) {
          searchPath = searchPath.substring(separatorIndex);
        }
        else {
          searchPath = ""; //NORES
        }
      }
      else {
        if ( searchPath.length() > 0 ) {
          v.addElement(searchPath);
        }
        searchPath = ""; //NORES
      }
    }
    String [] ret = new String[v.size()];
    v.copyInto(ret);
    return ret;
  }

  // To make this abstraction complete need a corresponding File interface
  // and supporting object(s).  Just deal with plain File for now.
  //
  // To bring complete FileMan functionality here, need native functions for
  // delete directory (only have delete file), and setLastModified().
  //
  public static String createFilePath(String path, String file) {
    return path + File.separator + file;
  }

  public static void copyFile(String src, String dest) {
    copyFile(src, dest, null);
  }

  public static void copyFile(String src, String dest, String header) {
    try {
      // Use unbuffered streams, because we're going to use a large buffer
      // for this sequential io.
      FileInputStream  input   = new FileInputStream(src);
      FileOutputStream output  = new FileOutputStream(dest);

      if (header != null) {
        int  headerLength  = header.length();
        byte[] headerBytes = new byte[headerLength];
        header.getBytes(0, headerLength, headerBytes, 0);
        output.write(headerBytes, 0, headerLength);
      }

      int bytesRead;
      byte[] buffer = new byte[32*1024];
      while ((bytesRead = input.read(buffer, 0, buffer.length)) > 0)
        output.write(buffer, 0, bytesRead);

      input.close();
      output.close();
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
  }

  public static void copyFile(String src, OutputStream out)

  {
    try {
      FileInputStream  input   = new FileInputStream(src);
      int bytesRead;
      byte[] buffer = new byte[32*1024];
      while ((bytesRead = input.read(buffer, 0, buffer.length)) > 0)
        out.write(buffer, 0, bytesRead);
      input.close();
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
  }

  public static boolean compareBytes(byte[] buf1, byte[] buf2, int count) {
    for (int i = 0; i < count; i++)
      if (buf1[i] != buf2[i])
        return false;
    return true;
  }

  public static boolean compareFiles(String path1, String path2) {
    long total = newFile(path1).length();
    if (total != newFile(path2).length())
      return false;

    try {
      FileInputStream input1 = new FileInputStream(path1);
      FileInputStream input2 = new FileInputStream(path2);

      byte[] buffer1 = new byte[32*1024];
      byte[] buffer2 = new byte[32*1024];

      long totalRead = 0;
      int  bytesRead;
      while ((bytesRead = input1.read(buffer1, 0, buffer1.length)) > 0) {
        if (input2.read(buffer2, 0, buffer2.length) != bytesRead)
          return false;
        if (!compareBytes(buffer1, buffer2, bytesRead))
          return false;
        totalRead += bytesRead;
      }
      input1.close();
      input2.close();
      return total == totalRead;
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
  }


  public static boolean traverse(TraverseAction act, File dir) throws Exception {
    if (!dir.isAbsolute())
      return false;

    String[] fileList = dir.list();
    if (fileList == null)
      return false;

    for (int i = 0; i < fileList.length; i++) {
      File curFile = newFile(createFilePath(dir.getAbsolutePath(), fileList[i]));
      if (curFile.isFile()) {
        if (act.onFile(curFile) == act.ABORT_TRAVERSE)
          return false;
      }
      else if (curFile.isDirectory()) {
        if (act.onDirEntry(curFile) == act.DIVE_TRAVERSE) {
          if (!traverse(act, newFile(curFile.getAbsolutePath()))
              || act.onDirExit(curFile, fileList) != act.CONTINUE_TRAVERSE)
            return false;
        }
      }
    }
    return true;
  }

  public static Properties loadProperties(String path) {
    Properties properties  = new Properties();
    try {
      BufferedInputFile stream  = new BufferedInputFile(path);
      properties.load(stream);
      stream.close();
      return properties;
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
  }

  public static boolean makeDirs(String path) {
    File dir = newFile(path);
    dir.mkdirs();
    return dir.isDirectory();
  }

  public static boolean makeDir(String path) {
    File dir = newFile(path);
    dir.mkdir();
    return dir.isDirectory();
  }

  public static String addDir(String path, String dir) {
    File file = newFile(sanitizePath(path));
    return file.getParent() + File.separator + dir + File.separator + file.getName();
  }

  // This is in here because the list() method of the File class returns a String[]
  public static int listContains(String[] baseList, String name) {
    for (int i = 0; i < baseList.length; i++) {
      // Have to use IgnoreCase for netware server with long filename support.
      if (baseList[i].equalsIgnoreCase(name))
        return i;
    }
    return -1;
  }

  public static File newFile(String path) {
    return new File(path);
  }

  public static File getClassesFile(String fileName) {
    String path = getClassPath();
    StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
    while (tokenizer.hasMoreTokens()) {
      path = tokenizer.nextToken();
      File file = new File(path + File.separator + fileName);
      if (file.exists())
        return file;
    }
    return null;
  }

  public static File getClassesFile(Object object) {
    String name = object.getClass().getName().replace('.', File.separatorChar);
    name  = name + ".class";
    return getClassesFile(name);
  }

  public static String getClassesDirectory(Object object) {
    return getClassesFile((Object)object).getParent();
  }

  public static final void copyStringToFile(String value, String dest) {
    try {
      PrintStream outCount = new PrintStream(new FileOutputStream(dest));
      outCount.println(value);
      outCount.close();
    }
    catch(java.io.FileNotFoundException ex) {
      throw new FileUtilException(ex);
    }
  }

  public static final void copyFileWithoutCr(String src, String dest) {
    try {
      FileUtil.copyFile(src, new RemoveCrOutputStream(dest));
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
  }

  public static String getCannonicalPath(String name) {
    try {
      return new File(name).getCanonicalPath();
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
  }

  public static String appendSeparator(String path) {
    char lastChar = path.charAt(path.length()-1);
    if (lastChar != '/' && lastChar != '\\' && lastChar != File.separatorChar)
      path += File.separatorChar;
    return path;
  }

  /**
   * Simplistic technique to parse a java source file and retrieve its package name.
  public static String getJavaPackageName(String fileName) {
    String packageName = null;
    int ch;
    InputStream in = null;
    try {
      FastStringBuffer buf = new FastStringBuffer();
      in = new BufferedInputStream(new FileInputStream(fileName), 256);
      ch = in.read();
      while (true) {
        if (ch == -1)
          break;
        if (ch == 'p') {
          ch = in.read();
          if (ch == 'a') {
            ch = in.read();
            if (ch == 'c') {
              ch = in.read();
              if (ch == 'k') {
                ch = in.read();
                if (ch == 'a') {
                  ch = in.read();
                  if (ch == 'g') {
                    ch = in.read();
                    if (ch == 'e') {
                      ch = in.read();
                      if (ch == ' ' || ch == '\t') {
                        ch = in.read();
                        while(ch <= ' ')
                          ch = in.read();
                        do {
                          buf.append((char)ch);
                        } while ((ch = in.read()) > ' ' && ch != ';');
                        return buf.toString();
                      }
                    }
                  }
                }
              }
            }
          }
        }
        else
          in.read();
      }
    }
    catch(IOException ex) {
      throw new FileUtilException(ex);
    }
    finally {
      if (in != null) {
        try {
          in.close();
        }
        catch(IOException ex) {
          throw new FileUtilException(ex);
        }
      }
    }
    return packageName;
  }
*/

  private static Boolean sanitizeNeeded;
  private PatternMatch match;
}

class RemoveCrOutputStream extends OutputStream {
  RemoveCrOutputStream(String name)
    throws FileNotFoundException
  {
    out = new FileOutputStream(name);
  }

  public void write(int b)
    throws IOException
  {
    if (b != '\r')
      out.write(b);
  }

  FileOutputStream out;
}
