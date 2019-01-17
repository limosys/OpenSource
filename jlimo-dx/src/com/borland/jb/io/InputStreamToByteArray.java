//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/InputStreamToByteArray.java,v 7.0 2002/08/08 18:40:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * This class is a wrapper around ByteArrayInputStream.
 * The DataSet package needs access to the byte array itself when resolving.
 *
 * @author  Jens Ole Lauridsen
 * @version 1.01 8/26/97
 * @see     java.io.ByteArrayInputStream
 */
public class InputStreamToByteArray extends ByteArrayInputStream
{

/**
 * Constructs an InputStreamToByteArray object.
 *
 * @param buf The buffer into which the data is read.
 */
  public InputStreamToByteArray(byte buf[]) {
    super(buf);
  }

  /**
   * Constructs an InputStreamToByteArray object.
   *
   * @param buf     The buffer into which the data is read.
   * @param offset  The start offset of the data.
   * @param length  The maximum number of bytes read.
   */
  public InputStreamToByteArray(byte buf[], int offset, int length) {
    super(buf, offset, length);
  }

  /*
   * @return The number of bytes read.
   */
  public byte[] getBytes() {
    return buf;
  }

  /**
   * A static method that returns an array of bytes
   * representing the InputStream with the specified stream.
   * @param stream        The input stream
   * @return              An array of bytes
   * @throws IOException  An I/O error occurred.
   */
  static public byte[] getBytes(InputStream stream) throws IOException {
    InputStreamToByteArray buffer;
    if (stream instanceof InputStreamToByteArray) {
      buffer = (InputStreamToByteArray)stream;
      return buffer.getBytes();
    }
    else {
      byte[] buf = new byte[stream.available()];
      stream.read(buf);
      return buf;
    }
  }
}
