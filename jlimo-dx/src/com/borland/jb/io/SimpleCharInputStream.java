//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/SimpleCharInputStream.java,v 7.0 2002/08/08 18:40:48 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.*;

/**
 * This abstract class provides subclasses with
 * the capability of reading input one line at a time.
 * This class is used by the AsciiEncodedInputStream and
 * AsciiEncodedOutputStream classes.
 */
abstract public class SimpleCharInputStream {

  private char[] lineBuffer       = new char[0];
  private int    lineBufferLength = 0;

  /**
   *  Reads a byte of data.
   *  This method will block if no input is available.
   * @return      The byte read, or -1 if the end of the stream is reached.
   * @throws IOException    An I/O error occured.
   */
  abstract public int   read() throws IOException;

  /**
   * "Pushes" the given character back into the input buffer
   * so the next read() will return it.
   * @param ch            The given character.
   * @throws IOException  An error has occurred.
   */
  abstract public void  unread(int ch) throws IOException;

  /**
   * Closes the input stream.
   * Should be the last operation done with this object.
   * @throws IOException  An error has occurred.
   */
  abstract public void  close() throws IOException;

  /**
   * Reads a complete line of text, which is defined
   * as a line terminated with either a line-feed
   * character or a carriage-return/line-feed pair.  Carriage
   * returns that are not immediately followed by a line
   * feed character are not treated as line terminators,
   * but as part of the current line.
   *
   * @exception IOException An error has occurred.
   */
  public String readLine() throws IOException {
    int offset = 0;
    while (true) {
      int ch = read();
      switch (ch) {
        case -1:
          if (offset == 0)
            return null;
          return new String(lineBuffer, 0, offset);
        case '\r':
          int ch2 = read();
          if (ch2 != '\n')
            unread(ch2);
        case '\n':
          return new String(lineBuffer, 0, offset);
        default:
          if (offset >= lineBufferLength) {
            char[] newBuffer = new char[lineBufferLength+128];
            System.arraycopy(lineBuffer, 0, newBuffer, 0, offset);
            lineBuffer       = newBuffer;
            lineBufferLength = lineBuffer.length;
          }
          lineBuffer[offset++] = (char)ch;
          break;
      }
    }
  }
}
