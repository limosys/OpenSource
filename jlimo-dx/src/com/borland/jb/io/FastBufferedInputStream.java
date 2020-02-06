//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/FastBufferedInputStream.java,v 7.1 2003/01/30 21:42:00 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.borland.jb.util.DiagnosticJLimo;

/**
 * An unsynchronized buffered input stream that reads in characters
 * from a stream without causing a read every time. The data is read
 * into a buffer, then subsequent reads result in fast buffer access.
 * This class is patterned after {@link java.io.BufferedInputStream BufferedInputStream}
 * <CODE>java.io.BufferedInputStream</CODE></A>. . The primary
 * difference is that all access is unsynchronized (not thread-safe),
 * for faster response.
 */
public class FastBufferedInputStream extends FilterInputStream {
//  private InputStream in;
  private byte[]      buffer;
  private int         count;
  private int         pos;
  private int         markpos;
  private int         readLimit;

  /**
   * Creates a new buffered stream with a default buffer size
   * of 2048 characters.
   * @param in        the input stream
   */
  public FastBufferedInputStream(InputStream in) {
    this(in, 2048);
  }

  /**
   * Creates a new buffered stream with the specified
   * buffer size.
   * @param in        the input stream
   * @param size      the buffer size
   */
  public FastBufferedInputStream(InputStream in, int size) {
    super(in);
    markpos = -2;
    pos     = -1;
    buffer  = new byte[size];
  }

  protected void fill() throws IOException {
    if (markpos < -1)
      pos = 0;
    else if (pos >= buffer.length) {
      if (markpos > -1) {
        int size = pos - markpos;
        System.arraycopy(buffer, markpos+1, buffer, 0, size);
        pos     = size;
        markpos = -1;
      }
      else if (buffer.length >= readLimit) {
        markpos   = -2;
        pos       = 0;
      }
      else {
        int newSize = pos * 2;
        if (newSize > readLimit)
            newSize = readLimit;
        byte newBuffer[] = new byte[newSize];
        System.arraycopy(buffer, 0, newBuffer, 0, pos);
        buffer = newBuffer;
      }
    }
    DiagnosticJLimo.check(in != null);
    DiagnosticJLimo.check(buffer != null);
    int bytes = in.read(buffer, pos, buffer.length - pos);
    if (bytes > 0) {
      count = bytes + pos;
      --pos;
    }
    else
      count = pos;
  }


  /**
   * Reads a byte of data. This method will block if no input is
   * available.
   *
   * @return  the byte read, or -1 if the end of the
   * stream is reached.
   * @exception IOException If an I/O error has occurred.
   */
  public int read() throws IOException {
    if (++pos < count)
      return buffer[pos] & 0xff;
    fill();
    if (pos < count)
      return read();
    return -1;
  }

   /**
   * "Pushes" the given character back into the input buffer
   *  so the next read() will return it.
   *
   * @throws IOException
   */
  public void unread() throws IOException {
    if (pos >= 0)
      --pos;
  }

  /**
   * Reads into an array of bytes.
   * Blocks until some input is available.
   *
   * @param copyBuffer The buffer into which the data is read.
   * @param off the start offset of the data
   * @param len the maximum number of bytes read
   * @return  the actual number of bytes read; returns -1
   * when the end of the stream is reached.
   * @exception IOException An I/O error has occurred.
   */
  public int read(byte[] copyBuffer, int off, int len) throws IOException {
  int available = this.count - (pos + 1);
    if (available <= 0) {
      fill();
      available = this.count - (pos + 1);
      if (available <= 0)
        return -1;
    }
    int copyCount = (available < len) ? available : len;
    System.arraycopy(this.buffer, pos + 1, copyBuffer, off, copyCount);
    pos += copyCount;
    return copyCount;
  }

  /**
   * Skips the specified number of bytes of input.
   *
   * @param n the number of bytes to be skipped
   * @return  the actual number of bytes skipped.
   * @exception IOException An I/O error has occurred.
   */
  public long skip(long n) throws IOException {
    long available = count - pos + 1;

    if (available >= n) {
      pos += n;
      return n;
    }

    pos += available;
    return available + in.skip(n - available);
  }

  /**
   * Returns the number of bytes that can be read
   * without blocking. This total is the number
   * of bytes in the buffer and the number of bytes
   * available from the input stream.
   *
   * @return the number of available bytes.
   */
  public int available() throws IOException {
    return (count - pos + 1) + in.available();
  }

  /**
   * Marks the current position in the input stream.  A subsequent
   * call to the reset() method will reposition the stream at the last
   * marked position so that subsequent reads will re-read
   * the same bytes.  The stream promises to allow readlimit bytes
   * to be read before the mark position gets invalidated.
   *
   * @param readlimit the maximum limit of bytes allowed to be read before the
   * mark position becomes invalid.
   */
  public void mark(int readlimit) {
    this.readLimit = readlimit;
    markpos        = pos;
  }

  /**
   * Repositions the stream to the last marked position.  If the
   * stream has not been marked, or if the mark has been invalidated,
   * an IOException is thrown.
   * <p>
   * Stream marks are intended to be used in
   * situations where you need to read ahead a little to see what's in
   * the stream.  Often this is most easily done by invoking some
   * general parser.
   *
   * <LI>If the stream is of the type handled by the parser, the
   * operation continues.
   * <LI>If the stream is not of that type, the parser generates an
   * exception when it fails. If an exception gets thrown  within
   * <CODE>readlimit</CODE> bytes, the parser allows the outer code
   * to reset the stream and to try another parser.
   *
   * @exception IOException The stream has not been marked or the mark has been
   * invalidated.
   */
  public synchronized void reset() throws IOException {
    if (markpos < -1)
      throw new IOException(Res.bundle.getString(ResIndex.InvalidMarkReset));
    pos = markpos;
  }

  /**
   * Returns a boolean indicating whether this stream type supports
   * mark/reset.
   */
  public boolean markSupported() { return true; }
}
