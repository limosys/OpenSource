//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/FastBufferedOutputStream.java,v 7.0 2002/08/08 18:40:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An unsynchronized buffered output stream
 * that reads out characters from a stream
 * without causing a read every time. The data is read from a buffer,
 * then subsequent reads result in fast buffer access.
 * This class is patterned after java.io.BufferedOutputStream.
 * The primary difference is that all access is unsynchronized
 * (not thread-safe), for faster response.
 */
public class FastBufferedOutputStream extends FilterOutputStream {
  /**
   * The buffer where data is stored.
   */
  protected byte[] buf;

  /**
   * The number of bytes in the buffer.
   */
  protected int count;

  private boolean buffer;

  /**
   * Creates a new buffered stream with a default buffer
   * size of 2048 characters.
   * @param out       the output stream
   */
  public FastBufferedOutputStream(OutputStream out) {
    this(out, 2048);
  }

  /**
   * Creates a new buffered stream with the specified buffer size.
   * @param out               the output stream
   * @param size              the buffer size
   */
  public FastBufferedOutputStream(OutputStream out, int size) {
    super(out);
    buf = new byte[size];
  }


  //
  // **** UNSYNCHRONIZED FOR SPEED ****
  //
  /**
   * Writes a byte. This method will block until the byte is actually
   * written.
   * @param     b the byte to be written
   * @exception IOException An I/O error has occurred.
   */
  public void write(int b) throws IOException {
    if (count == buf.length) {
      if (!buffer)
        flush();
    }
    buf[count++] = (byte)b;
  }

  //
  // **** UNSYNCHRONIZED FOR SPEED ****
  //
  /**
   * Writes a subarray of bytes.
   * @param b         the data to be written
   * @param off       the start offset in the data
   * @param len       the number of bytes that are written
   * @exception IOException An I/O error has occurred.
   */
  public void write(byte b[], int off, int len) throws IOException {

    // More optimal than BufferedOutputStream which tends to do more writes, and
    // more unaligned writes.
    //
    int avail = buf.length - count;

    while(len > avail) {
      write(b, off, avail);
      if (!buffer)
        flush();
      off   += avail;
      len   -= avail;
      avail =  buf.length - count;
    }

    if (len > 0) {
      System.arraycopy(b, off, buf, count, len);
      count += len;
    }

  }

  //
  // **** UNSYNCHRONIZED FOR SPEED ****
  //
  /**
   * Flushes the stream. This will write any buffered
   * output bytes.
   * @exception IOException An I/O error has occurred.
   */
  public void flush() throws IOException {
    if (count > 0) {
      out.write(buf, 0, count);
      out.flush();
      count = 0;
    }
  }
}
