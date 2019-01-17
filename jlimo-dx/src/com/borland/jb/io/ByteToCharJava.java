//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/ByteToCharJava.java,v 7.0 2002/08/08 18:40:46 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import com.borland.jb.util.Hex;

import com.borland.jb.util.Hex;

/*
 *  This class provides static functions that help in reading
 *  Java encoding (Unicode escapes) in text files.
 */

/**
 * This class is used internally by other
 * <code>com.borland</code> classes. You should
 * never use this class directly.
 */
public final class ByteToCharJava {

 /*
  * Returns a byte array containing the Unicode escape
  * of the given character.
  */

 /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  public static final byte[] toUnicodeEscape(char chIn) {
    int in = (int) chIn;
    byte [] out = new byte[6];
    out[0] = '\\';
    out[1] = 'u';               //NORES
    out[2] = Hex.bytes[in>>12];
    out[3] = Hex.bytes[(in>>8) & 0xf];
    out[4] = Hex.bytes[(in>>4) & 0xf];
    out[5] = Hex.bytes[in & 0xf];
    return out;
  }

  private static final int hexdigit2int(char ch) {
    if ('0' <= ch && ch <= '9')
      return ch - '0';
    if ('A' <= ch && ch <= 'F')
      return ch - 'A' + 10;
    if ('a' <= ch && ch <= 'f')         //NORES
      return ch - 'a' + 10;             //NORES
    return -1;
  }

 /*
  * Parses a string, converting Unicode escapes to the corresponding Unicode
  * characters.
  *
  * @return The resulting Unicode encoded string.
  * @param  srcString the string to be converted.
  */

  /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  public static final String parseUnicodeEscapes(String srcString) {
    char[] source = srcString.toCharArray();
    int resultLen = parseUnicodeEscapes(source, 0, source.length);
    if (resultLen != srcString.length()) {
      return new String(source, 0, resultLen);
    }
    else
      return srcString;
  }

 /*
  * Parses a char array, converting Unicode escapes to the
  * corresponding Unicode characters.
  * @param source  the input char array
  * @param start   the starting position to convert
  * @param end     the offset to stop converting
  * @return        The resulting number of
  *                unicode characters from the conversion.
  */

  /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  public static final int parseUnicodeEscapes(char[] source, int start, int end) {
    char[] dest = source;
    int i = start;
    int j = start;
    int bslashes = 0;
    while (i < end) {
      if (source[i] == '\\') {
        bslashes++; // Unicode escapes are only parsed for an *odd*
                    // number of backslashes.
        if (bslashes%2!=0 && i + 1 < end && source[i+1] == 'u') {               //NORES
          bslashes = 0;
          int l = i + 2; // skip over the '\' and the first 'u'
          while (l < end && source[l] == 'u') // and any subsequent u's         //NORES
            l++;
          if (l + 4 <= end) {                 // process exactly 4 hex digits, ignore if too near end
            int code = 0;
            int d = 0;
            for (int k = 0; k < 4 && d >= 0; k++) {
              d = hexdigit2int(source[l + k]);
              code = code * 16 + d;
            }
            if (d >= 0) {                     // got exactly 4 good hex digits
              dest[j++] = (char)code;
              i = l + 4;
              continue;
            }
          }
        }
      }
      else {
        bslashes = 0;
      }
      dest[j++] = source[i++];
    }
    return j - start;
  }

 /*
  * Parses a string, converting escapes other than
  * Unicode escapes, to the correspoding control
  * characters. \n \t \b \r \f are converted.
  * @return   The resulting Unicode encoded string.
  * @param    srcString the string to be converted.
  */

  /**
  * This method is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this method directly.
 */
  public static final String parseEscapes(String srcString) {
    int len = srcString.length();
    char[] source = srcString.toCharArray();
    char[] dest = source;
    int j = 0;
    int i = 0;
    while (i < len) {
      if (source[i] == '\\' && i + 1 < len) {
        i++;
        switch (source[i]) {
          case 'n':             //NORES
            dest[j++] = '\n';
            i++;
            continue;
          case 't':             //NORES
            dest[j++] = '\t';
            i++;
            continue;
          case 'b':             //NORES
            dest[j++] = '\b';
            i++;
            continue;
          case 'r':             //NORES
            dest[j++] = '\r';
            i++;
            continue;
          case 'f':             //NORES
            dest[j++] = '\f';
            i++;
            continue;
          default:
        }
      }
      dest[j++] = source[i++];
    }
    if (i != j)
      return new String(dest, 0, j);
    return srcString;
  }
}

