//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/AsciiOutputStream.java,v 7.0 2002/08/08 18:40:45 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import com.borland.jb.util.Hex;

import com.borland.jb.util.DiagnosticJLimo;

import com.borland.jb.util.DiagnosticJLimo;

import java.io.*;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Hex;

/**
 * An implementation of SimpleCharOutputStream that is optimized for data
 * where most of the characters belong to either the ASCII character set
 * or the 8859_1 character set. Other characters are assumed to be encoded
 * in Unicode escapes.
 * <p>
 * Under Western European versions of Windows, including
 * the United States version, the javac compiler assumes the
 * encoding of 8859_1, even though the actual encoding should be CP1252.
 * CP1252 contains some characters that are not in 8859_1. If your source
 * file contains these additional characters, they will not be correctly
 * interpreted. In this case, you should specify CP1252 as the encoding.
 */
public class AsciiOutputStream extends SimpleCharOutputStream {

  private byte[]        buffer;
  private int           count;
  private int           bufferLength;
  private int           backCount;
  private OutputStream  out;

  /**
   *  Creates a new buffered stream with a default buffer size of
   *  2048 characters.
   * @param out       The output stream.
   */
  public AsciiOutputStream(OutputStream out) {
    this(out, 2048);
  }

  /**
   * Creates a new buffered stream with the specified
   * buffer size.
   * @param out               The output stream.
   * @param bufferLength      The buffer size.
   */
  public AsciiOutputStream(OutputStream out, int bufferLength) {
    this.out          = out;
    this.count        = -1;
    this.bufferLength = bufferLength;
    buffer            = new byte[bufferLength];
  }

  /**
   * Writes the specified character. This method will block until
   * the character is actually written.
   * @throws IOException
   *    An I/O error occurred.
   */
  public void write(int ch) throws IOException {
    if ((ch & 0xFF00) != 0) {
      writeByte('\\');
      if (backCount > 0) {
        writeByte('\\');
        backCount = 0;
      }
      writeByte('u');           //NORES
      writeByte(Hex.chars[(ch >> 12) & 0xF]);
      writeByte(Hex.chars[(ch >>  8) & 0xF]);
      writeByte(Hex.chars[(ch >>  4) & 0xF]);
      writeByte(Hex.chars[ch & 0xF]);
    }
    else if (ch == '\\') {
      writeByte(ch);
      if (++backCount > 1) {
        writeByte(ch);
        writeByte(ch);
        backCount = 0;
      }
    }
    else if (backCount > 0) {
      if (ch == 'u') {          //NORES
        writeByte('\\');
      }
      writeByte(ch);
      backCount = 0;
    }
    else if (++count < bufferLength) {
      buffer[count] = (byte)ch;
    }
    else {
      flush();
      writeByte(ch);
    }
  }

  private void writeByte(int ch) throws IOException {
    if (++count < bufferLength)
      buffer[count] = (byte)ch;
    else {
      flush();
      buffer[++count] = (byte)ch;
    }
  }

  /**
   *  Causes all currently buffered information to be
   *  written to the output stream.
   */
  public void flush() throws IOException {
    if (count > 0) {
      out.write(buffer, 0, count);
      out.flush();
      count = -1;
    }
  }

  /**
   * Closes the output stream.
   * Should be the last operation done with this object.
   */
  public void close() throws IOException {
    //Diagnostic.println("buffer.length "+buffer.length+" count "+count);
    flush();
    out.close();
  }
}

