//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/AsciiInputStream.java,v 7.0 2002/08/08 18:40:45 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.*;
import com.borland.jb.util.DiagnosticJLimo;

/**
 * An implementation of SimpleCharInputStream that is optimized for data
 * where most of the characters belong to either the ASCII character set
 * or the 8859_1 character set. Other characters are assumed to be encoded
 * in Unicode escapes.
 * <p>
 * Note that under Western European versions of Windows, including
 * the  United States version, the javac compiler assumes the
 * encoding of  8859_1, even though the actual encoding should be
 * CP1252. CP1252  contains some characters that are not in
 * 8859_1. If your source file  contains  these additional
 * characters, they will not be correctly  interpreted. In this
 * case, you should specify Cp1252 as the encoding.
 */
public class AsciiInputStream extends SimpleCharInputStream {

  private InputStream in;
  private byte[]      buffer;
  private int         count;
  private int         backCount;
  private int         ch;
  private int         pos;
  private int         undoChar;
  private int         undoPos;

  /**
   * Creates a new buffered stream with a default buffer
   * size of 2048 characters.
   * @param in        The input stream.
   */
  public AsciiInputStream(InputStream in) {
    this(in, 2048);
  }

  /**
   * Creates a new buffered stream with the specified buffer size.
   * @param in        The input stream.
   * @param size      The buffer size.
   */
  public AsciiInputStream(InputStream in, int size) {
    this.in = in;
    pos     = -1;
    undoPos = -1;
    buffer  = new byte[size];
  }

  private final void fill() throws IOException {
    pos = -1;
    count = in.read(buffer, 0, buffer.length);
    if (count < 0)
      count = pos;
  }

  /**
   * Reads a byte of data. This method will block if no input is available.
   * @return  the byte read, or -1 if the end of the stream is reached.
   * @exception IOException
   *    An I/O error has occurred.
   * @see com.borland.jb.io.AsciiOutputStream#write(int)
   */
  public int read() throws IOException {
    if (++pos < count) {
      ch = buffer[pos] & 0xff;
      if (ch == '\\' ) {
        ch  = readByte();
        if (ch == '\\')
          return ch;
        else if (ch == 'u') {           //NORES
          int result  = 0;
          loop: for (int i = 0 ; i < 4 ; i++) {
            ch = readByte();
            switch (ch) {
              case '0': case '1': case '2': case '3': case '4':
              case '5': case '6': case '7': case '8': case '9':
                result = (result << 4) + ch - '0';
                break;
              case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':               //NORES
                result = (result << 4) + 10 + ch - 'a';         //NORES
                break;
              case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                result = (result << 4) + 10 + ch - 'A';
                break;
              default:
                break loop;
            }
          }
          return result;
        }
        else {
          --pos;        // undoes last readByte().
          return '\\';
        }
      }
      else
        return ch;
    }
    else {
      if (undoPos != -1) {
        pos     = undoPos;
        undoPos = -1;
        return undoChar;
      }
      fill();
      if (pos < count)
        return read();
      return -1;
    }
  }

  private int readByte() throws IOException {
    if (++pos < count)
      return buffer[pos] & 0xff;
    fill();
    if (pos < count)
      return readByte();
    return -1;
  }

  /**
   * "Pushes" the given character back into the input buffer so the next read()
   *  will return it.
   * @param undoChar  The given character.
   */
  public void unread(int undoChar) throws IOException {
    this.undoChar = undoChar;
    this.undoPos  = pos;
    pos           = count;
  }

  /**
   * Closes the input stream.
   * Should be the last operation done with this object.
   */
  public void close() throws IOException {
    in.close();
  }
}

