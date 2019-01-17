//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/BufferedInputFile.java,v 7.0 2002/08/08 18:40:46 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------


package com.borland.jb.io;

import java.io.*;

/**
 * This class is used internally by other
 * <code>com.borland</code> classes. You should
 * never use this class directly.
 */
public class BufferedInputFile extends BufferedInputStream
{
  FileInputStream f;
  StringBuffer    buffer;

  /**
    * This constructor is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this constructor directly.
   */
  public BufferedInputFile(String path) throws FileNotFoundException {
    super(new FileInputStream(path));
    f = (FileInputStream)in;
    buffer = new StringBuffer(128);
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public final void close() throws IOException {
    f.close();
  }

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public final String readLine() throws IOException {

    // Reuse buffer to avoid allocating memory over and over.
    //
    buffer.setLength(0);

    int ch;
    while ((ch = read()) != -1) {
      if (ch == '\r')
        continue;
      if (ch == '\n')
        break;

      buffer.append((char)ch);
    }

    // Note toString is documented as not copying the buffer (avoids another
    // allocation).
    //
    if (buffer.length() > 0)
      return buffer.toString();

    return null;
  }
}
