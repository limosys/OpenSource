//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/EncodedInputStream.java,v 7.0 2002/08/08 18:40:46 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * An implementation of SimpleCharInputStream that is optimized for data where most of the characters belong to the specified character set. Other characters
 * are assumed to be encoded in Unicode escapes.
 */
public class EncodedInputStream extends SimpleCharInputStream {

  private InputStream in;
  private byte[]      bufferB;
  private char[]      bufferC;
  private int         count;
  private int         ch;
  private int         pos;
  private int         undoChar;
  private int         undoPos;
  private ByteToCharConverter byteToChar;
  private byte        savedFirstByte; //temp: part of a JDK bug workaround
                                      //temp: see fill()
  private static final int DEFAULTBUFFERSIZE = 2048;

  /**
   * Creates a new buffered stream with the default encoding and with
   * a default buffer size of 2048 characters.
   * @param in        The input stream
   */
  public EncodedInputStream(InputStream in) throws UnsupportedEncodingException {
//    this(in, System.getProperty("file.encoding"));  //NORES
      this(in, (new OutputStreamWriter(System.out)).getEncoding()); // "file.encoding" is not a published property in
                                                                    // some browsers.
  }

  /**
   * Creates a new buffered stream with the specified encoding and
   * with a default buffer size of 2048 characters.
   * @param in        The input stream
   * @param encoding  The specified encoding
   */
  public EncodedInputStream(InputStream in, String encoding) throws UnsupportedEncodingException {
    this(in, encoding, DEFAULTBUFFERSIZE);
  }

  /**
   * Creates a new buffered stream with the specified
   * encoding and specified buffer size.
   * @param in         The input stream
   * @param encoding   The specified encoding
   * @param size       The buffer size
   */
  public EncodedInputStream(InputStream in, String encoding, int size) throws UnsupportedEncodingException {
    this.in           = in;
    byteToChar        = ByteToCharConverter.getConverter(encoding);
    pos               = -1;
    undoPos           = -1;
    bufferB           = new byte[size];
    bufferC           = new char[size*byteToChar.getMaxCharsPerByte()];
  }

  private final void fill() throws IOException {
    // This code includes a workaround for a JDK bug:
    // When a multibyte character straddles a buffer (i.e. trailbyte is first byte of a buffer), an ArrayIndexOutOfBounds occurs in ByteToCharSJIS.convert().
    //!KTien TODO: remove when this is fixed in JDK (lines marked with //temp)
    pos = -1;
    int bytes = in.read(bufferB, 0, bufferB.length-1);  //temp: -1

    if (bytes <= 0) {
      count = pos;
      return;
    }

    try {                                                         //temp
      count = byteToChar.convert(bufferB, 0, bytes, bufferC, 0, bufferC.length);
      savedFirstByte = bufferB[bytes-1];                            //temp
    }                                                             //temp
    catch (ArrayIndexOutOfBoundsException e) {                    //temp
      // this is a workaround for a JDK bug                       //temp
      byteToChar.reset();                                         //temp
      System.arraycopy(bufferB, 0, bufferB, 1, bytes++);          //temp
      bufferB[0] = savedFirstByte;                                //temp
      count = byteToChar.convert(bufferB, 0, bytes, bufferC, 0, bufferC.length);
    }                                                             //temp
    if (count <= 0) count = pos;
  }
  
  private int readX() throws IOException {
    if (++pos < count) {
      ch = bufferC[pos] & 0xffff;
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

  /**
   * Reads a byte of data. This method will block if no input is available.
   * @return  The byte read, or -1 if the end of
   *          the stream is reached
   * @throws  IOException An I/O error occurred
   */
  public int read() throws IOException {
    if (++pos < count) {
      ch = bufferC[pos];
      if (ch == '\\' ) {
        ch  = readX();
        if (ch == '\\')
          return ch;
        else if (ch == 'u') {           //NORES
          int result  = 0;
          loop: for (int i = 0 ; i < 4 ; i++) {
            ch = readX();
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
          --pos;        // undoes last readX().
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

/**
 * "Pushes" the given character back into the input buffer
 * so the next read() will return it.
 * @param undoChar      The given character
 * @throws IOException  An I/O error occurred
 */
  public void unread(int undoChar) throws IOException {
    this.undoChar = undoChar;
    this.undoPos  = pos;
    pos           = count;
  }

  /**
   *  Closes the encoded input stream.
   *  Should be the last operation done with this object.
   * @throws IOException  An I/O error occurred
   */
  public void close() throws IOException {
    in.close();
  }
}

