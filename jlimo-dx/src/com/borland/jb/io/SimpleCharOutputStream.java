//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/SimpleCharOutputStream.java,v 7.0 2002/08/08 18:40:48 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.*;

/**
 * This abstract class provides subclasses
 * with the ability to write complete strings,
 * arbitrarily delimited strings, and terminated
 * lines.
 */
abstract public class SimpleCharOutputStream {

/**
 * Writes the value represented by ch to the output stream.
 *
 * @param ch            The value to write to the output stream.
 * @throws IOException  An I/O error occurred.
 */
  abstract public void write(int ch) throws IOException;

  /**
   * Closes the output stream.
   * Should be the last operation done with this object.
   *
   * @throws IOException  An I/O error occurred.
   */
  abstract public void close() throws IOException;

  /**
   * Writes all the characters in the given string to the
   * output stream.
   *
   * @param   The string to write to the output stream.
   * @throws IOException  An I/O error occurred.
   */
  public void write(String string) throws IOException {
    int length = string.length();
    for (int i = 0; i < length; i++)
      write(string.charAt(i));
  }

  /**
   * Writes a string with the given delimiter.  Each
   * occurrence of the given delimiter is escaped by
   * repeating the delimiter.
   * @param   string      The string to write to the output stream.
   * @param delimiter     The delimiter to follow the string.
   * @throws IOException  An error occurred.
   */
  public void writeDelimited(String string, char delimiter) throws IOException {
    int length = string.length();
    write(delimiter);
    for (int i = 0; i < length; i++) {
      int ch = string.charAt(i);
      if (ch == delimiter)
        write(delimiter);
      write(ch);
    }
    write(delimiter);
  }

  /**
   * Writes the given string and follows
   * it with a line feed.  The line delimiter
   * in this case is a fixed line-feed character, regardless
   * of the line-terminating convention of the
   * current environment.
   *
   * @param   string The string to write to the output stream.
   * @throws IOException  An error occurred.
   */
  public void writeln(String string) throws IOException {
    write(string);
    writeln();
  }

  /**
   * Writes out a line delimeter.  The line delimiter
   * in this case is a fixed line-feed character, regardless
   * of the line-terminating convention of the
   * current environment.
   * @throws IOException  An error occurred.
   */
  public void writeln() throws IOException {
    write('\n');
  }
}
