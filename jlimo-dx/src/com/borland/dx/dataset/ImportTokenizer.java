//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ImportTokenizer.java,v 7.0 2002/08/08 18:39:26 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.io.SimpleCharInputStream;
import java.sql.*;
import java.io.*;

class ImportTokenizer {
  static final  int EOF    = -1;
  static final  int EOL    = -2;
  static final  int TOKEN  = -3;

  char[]                          token;
  int                             tokenLength;
  boolean                         delimiterHit;
  private int                     tokenBufLen;
  private char                    separator;
  private char                    delimiter;
  private int                     separatorCount;
  private SimpleCharInputStream   stream;


  ImportTokenizer(SimpleCharInputStream stream, char separator, char delimiter) {

    this.stream     = stream;
    this.separator  = separator;
    this.delimiter  = delimiter;
    growTokenBuffer();
  }

  private final void growTokenBuffer() {
    char[]  saveToken = token;
    this.token        = new char[token==null?16:(token.length*2)];
    this.tokenBufLen  = token.length;
    if (saveToken != null)
      System.arraycopy(saveToken, 0, token, 0, saveToken.length);
  }

  // Returns whether new line or eof hit.
  // Simple and fast.
  //
  final int nextToken()
  throws IOException
  {
    int ch;
    tokenLength   = 0;
    delimiterHit  = false;
    while (true) {

      if (tokenLength == tokenBufLen)
        growTokenBuffer();

      ch = stream.read();

      if (ch == delimiter) {
        delimiterHit  = true;
        tokenLength   =  0;
        while (true) {
          ch = stream.read();
          // May have found terminating delimeter.
          //
          if (ch == delimiter) {

            // If no separator used, cannot escape delimeter.
            //
            if (separator == 0)
              return TOKEN;

            // Now look ahead to see if delimeter being escapped.  Two consecutive
            // delimeters are considered as one.
            //
            ch = stream.read();

            if (ch != delimiter) {
              //
              // Next char should be a separator or line break;.  To be sure,
              // eat any unexpected garbage.  Otherwise, will end up reading
              // to EOF.  See bug 5390.
              //
              while (true) {
                if ( ch == separator)
                  break;
                if (ch < 0)
                  return TOKEN;
                if (ch < ' ')
                  break;
                ch = stream.read();
              }
              break;
            }
          }
          if (ch == -1) {
            return EOF;
          }

          if (tokenLength == tokenBufLen)
            growTokenBuffer();

          token[tokenLength++] = (char)ch;
//!         Diagnostic.println("token:  "+new String(token, 0, tokenLength)+":count:"+tokenLength);
        }
      }


      if (ch == separator) {
        ++separatorCount;
        return TOKEN;
      }

      if (ch < ' ' ) {
        if (ch == '\r') {
          int ch2 = stream.read();
          if (ch2 == -1 || ch2 == 0x1A)  // EOF or Ctrl-Z
            return EOF;
          if (ch2 != '\n')
            stream.unread(ch2);
          return EOL;
        }

        if (ch == '\n')
          return EOL;

        if (ch == -1 || ch == 0x1A)  // EOF or Ctrl-Z
          return EOF;
      }
      token[tokenLength++] = (char)ch;
    }
  }

  final void closeStream() {
    try {
      stream.close();
    }
    catch (IOException ex) {
      DiagnosticJLimo.printStackTrace(ex);
    }
  }
}
