//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/EncodedOutputStream.java,v 7.0 2002/08/08 18:40:46 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.borland.jb.util.Hex;

/**
 * An implementation of SimpleCharOutputStream that is optimized for data
 * where most of the characters belong to the specified character set.
 * Other characters are assumed to be encoded in Unicode escapes.
 */
public class EncodedOutputStream extends SimpleCharOutputStream {

  private char[]        bufferIn;
  private byte[]        bufferOut;
  private CharToByteConverter charToByte;
  private OutputStream  out;
  private int           charSize;
  private int           backCount;

  /**
   * Creates output stream encoded in the default encoding.
   * @param out                              The output stream
   * @throws UnsupportedEncodingException  Character encoding is not supported
   */
  public EncodedOutputStream(OutputStream out) throws UnsupportedEncodingException
  {
//    this(out, System.getProperty("file.encoding"));  //NORES
      this(out, (new OutputStreamWriter(System.out)).getEncoding());  // "file.encoding" is not a published property in
  }                                                                   // some browsers.

  /**
   * Creates output stream encoded in the specified encoding.
   * Unicode characters passed in for a write() will be converted
   * to native, potentially multibyte, forms.
   * Characters that cannot be encoded in the specified encoding
   * will be represented in Unicode escapes.
   *
   * @param out               The output stream
   * @param encodingString    The encoding in the resulting stream
   * @throws UnsupportedEncodingException Character encoding is not supported
   */
  public EncodedOutputStream(OutputStream out, String encodingString) throws UnsupportedEncodingException
  {
    this.out          = out;
    charToByte        = CharToByteConverter.getConverter(encodingString);
    charToByte.setSubstitutionMode(false);
    this.charSize     = charToByte.getMaxBytesPerChar();
    bufferOut         = new byte[charSize];
    bufferIn          = new char[1];
  }

  /**
   * Creates output stream encoded in the specified encoding
   * and the specified buffer size.  Currently this class
   * does not do its own buffering -- the buffer size is ignored.
   * Unicode characters passed in for a write() will be converted
   * to native, potentially multibyte, forms.
   *
   * @param out                The output stream
   * @param encodingString     The encoding in the resulting stream
   * @param bufferSize          The size of the output stream buffers
   * @throws UnsupportedEncodingException Character encoding is not supported
   */
  public EncodedOutputStream(OutputStream out, String encodingString, int bufferSize)
      throws UnsupportedEncodingException
  {
    this(out, encodingString);
  }

  /**
   * Writes the character represented by the ch parameter.
   * This method will block until the byte is actually written.
   *<p>
   * The destination is encoded with the specified character set.
   * Characters that cannot be encoded in the specified character
   * set are encoded with Unicode escapes, like "?". Malformed
   * Unicode characters (characters that are invalid in any encoding) are converted to '?'.
   *
   * @param ch The characterw to be written
   * @see com.borland.jb.io.SimpleCharOutputStream#write(int)
   * @throws IOException  An I/O error has occurred
   */
  public void write(int ch) throws IOException
  {
    bufferIn[0] = (char) ch;

    if (ch == '\\') {
      out.write('\\');
      if (++backCount > 1) {
        out.write('\\');
        out.write('\\');
        backCount = 0;
      }
    }
    else if ((backCount>0) && (ch=='u')) {              //NORES
                                        out.write('\\');
                                        out.write('u');         //NORES
                                        backCount = 0;
                }
                else {
      try {
        int size = charToByte.convert(bufferIn, 0, 1, bufferOut, 0, charSize);
        out.write(bufferOut, 0, size);
                                backCount = 0;
      }
      catch (UnknownCharacterException e) {
        if (backCount > 0) {
          out.write('\\');
          backCount = 0;
        }
        out.write('\\');
        out.write('u');         //NORES
        out.write(Hex.chars[(ch >> 12) & 0xF]);
        out.write(Hex.chars[(ch >>  8) & 0xF]);
        out.write(Hex.chars[(ch >>  4) & 0xF]);
        out.write(Hex.chars[ch & 0xF]);
      }
      catch (MalformedInputException e) {
        // For unconvertible characters, write a '?'
        // These are illegal Unicode characters, not characters
        // that are outside the target charset range.
                                backCount = 0;
        out.write('?');
      }
    }
  }

  /**
   * Causes all currently buffered information to be
   * written to the output stream.
   * @throws IOException    An I/O error has occurred
   */
  public void flush() throws IOException {
    out.flush();
  }

  /**
   * Closes the encoded output stream.
   * Should be the last operation done with this object.
   * @throws IOException   An I/O error has occurred
   */
  public void close() throws IOException {
    out.flush();
    out.close();
  }
}

