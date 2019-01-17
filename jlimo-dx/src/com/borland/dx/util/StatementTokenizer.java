//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/util/StatementTokenizer.java,v 7.9 2004/01/23 23:07:55 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;
import com.borland.dx.sql.dataset.*;
import com.borland.dx.dataset.*;
import java.lang.*;
import java.sql.*;
import java.io.*;
import com.borland.jb.util.DiagnosticJLimo;

public class StatementTokenizer {

  private final static char EOF = '\uFFFF';

  public StatementTokenizer() {
    this.ignoreCR           = true;
    this.recognizeBeginEnd  = true;
    this.buf                = new char[64];
  }

  public final void setSQLFileName(String sqlFileName) {
    this.sqlFileName  = sqlFileName;
    this.started = false;
  }

//!cxing prefered way, user can set encoding correctly before calling this method
  public final void setSQLReader(Reader sqlReader) {
//!  public final void setSQLInputStream(InputStream sqlInputStream) {
    this.sqlReader  = sqlReader;
    this.started = false;
  }
  //!cxing for back compatibility, works only for default encoding.
  public final void setSQLInputStream(InputStream sqlInputStream) {
    this.sqlReader  = new InputStreamReader(sqlInputStream);
    this.started = false;
  }

  public final void setSQLString(String sqlString) {
    this.sqlString  = sqlString;
    if (sqlReader != null) {
      try {
        sqlReader.close();
      }
      catch (Exception ex) {
      }
    }
    this.sqlReader  = null;
    this.sqlFileName= null;
    this.started = false;
  }

  public final void setIgnoreCR(boolean ignoreCR) {
    this.ignoreCR = ignoreCR;
  }

  public final void setRecognizeBeginEnd(boolean recognizeBeginEnd) {
    this.recognizeBeginEnd = recognizeBeginEnd;
  }

  public final void setDatabase(Database database) {
    this.database = database;
  }

  public final void setJdbcConnection(java.sql.Connection con) {
    this.database = new Database();
    database.setJdbcConnection(con);
  }

  public final int executeStatements()
    throws java.io.IOException, DataSetException, SQLException
  {
    String token = null;
    boolean complete = false;
    boolean autoCommit = database.getAutoCommit();
    database.setAutoCommit(false);
    Statement statement = database.createStatement();
    int updateCount = 0;
    try {
      while(true) {
        token = nextToken();
        if (token == null)
          break;
        complete = false;
        statement.executeUpdate(token);
        complete = true;
        updateCount += statement.getUpdateCount();
      }
    }
    finally {
      if (!complete && token != null)
        DiagnosticJLimo.println(token);
      statement.close();
      if (autoCommit)
        database.setAutoCommit(true);
    }
    return updateCount;
  }


  public final ResultSet executeQuery()
    throws java.io.IOException, DataSetException, SQLException
  {
    String token;
    boolean autoCommit = database.getAutoCommit();
    database.setAutoCommit(false);
    Statement statement = database.createStatement();
    try {
      token = nextToken();
      if (token == null)
          return null;
      return statement.executeQuery(token);
    }
    finally {
//      statement.close();
      if (autoCommit)
        database.setAutoCommit(true);
    }
  }


  private char read()
    throws java.io.IOException
  {
    prevCh = ch;
    if ((ch = (char)sqlReader.read()) != EOF) {
      filePos++;
      if (ch != '\r'  || !ignoreCR) {
        buf[pos] = ch;
        if (++pos >= length) {
          char[] temp  = new char[buf.length*2];
          length = temp.length;
          System.arraycopy(buf, 0, temp, 0, buf.length);
          buf = temp;
        }
      }
    }
    return ch;
  }


  private void skipComment()
    throws java.io.IOException
  {
    read();
    if (ch == '/') {
      int savePos = pos-1;
      skipToEOL();
      if (!keepComments)
        pos = savePos;
    }
    else if (ch == '*') {
      int savePos = pos-2;
      while (ch != EOF) {
        while (ch != '*' && ch != EOF) {
          read();
        }
        read();
        if (ch == '/')
          break;
      }
      if (!keepComments)
        pos = savePos;
    }
  }

  // Recognize an escaped delimiter:   'o''neil'
  private final void skipSqlString(char delimiter) throws IOException {
    read();
    while (ch != EOF) {
      if (ch == delimiter) {
        read();
        if (ch != delimiter)
          break;
      }
      read();
    }
  }

  private final void trySkipCommentLine()
    throws java.io.IOException
  {
    if (ch == '-') {
      read();
      if (ch == '-') {
        int savePos = pos-2;
        skipToEOL();
        if (!keepComments)
          pos = savePos;
      }
    }
  }

  private final void skipToEOL()
    throws java.io.IOException
  {
    while (ch != '\n' && ch != '\r' && ch != EOF)
      read();
  }

  private final void skipSpacing(boolean allowComment)
    throws java.io.IOException
  {
    while (true) {
      read();
      switch (ch) {
        case '\r':
        case ' ':
        case '\t':
          continue;
        case '\n':
          fileLine++;
          continue;
        case '-':
          trySkipCommentLine();
          continue;

        case '/':
          if (allowComment) {
            skipComment();
            continue;
          }

        default:
          return;
      }
    }
  }

  private final boolean isWhiteSpace(char ch) {
    switch (ch) {
      case ' ':
      case '\t':
      case '\r':
      case '\n':
        return true;
      default:
        return false;
    }
  }

  private boolean skipKeyword(String keyword)
    throws java.io.IOException
  {
    if (!isWhiteSpace(prevCh))
      return false;

    int count = keyword.length();

    for (int index = 0; index < count; ++index) {
      if (java.lang.Character.toUpperCase(ch) != keyword.charAt(index))
        return false;
      read();
    }

    if (ch != EOF) {
      if (ch != ';' && !isWhiteSpace(ch))
        return false;
    }

    return true;
  }

  private final void init() throws java.io.IOException
  {
    if (sqlReader == null) {
      if (sqlFileName == null) {
      //!cxing StringBufferInputStream can not handle intl characters properly
        sqlReader  = new java.io.StringReader(sqlString);
      //!  sqlInputStream  = new java.io.StringBufferInputStream(sqlString);
      }
      else
      //!cxing
        sqlReader  = new java.io.BufferedReader(new InputStreamReader(new java.io.FileInputStream(sqlFileName)));
      //!  sqlInputStream  = new java.io.BufferedInputStream(new java.io.FileInputStream(sqlFileName));
    }
    started = true;
    filePos = 0;
    fileLine = 1;
  }

  public int getCurrentTokenStartPosition() {
    return tokenStartFilePos;
  }

  public boolean getKeepComments() {
  return keepComments;
  }

  public String nextToken()
    throws java.io.IOException
  {
    if (!started) {
      init();
    }
    ch = ' ';
    pos = 0;
    read();
    if (!keepComments && isWhiteSpace(ch))
      skipSpacing(true);
    if (ch == EOF)
      return null;
    tokenStartFilePos = filePos - 1;
    tokenStartFileLine = fileLine;
    buf[0] = ch;
    pos = 1;
    int begin_level = 0;
    boolean is_creating = false;
    boolean was_creating = false;

    while (true) {
      if (ch == EOF)
        break;
      if (ch == '-')
        trySkipCommentLine();
      if (ch == '/')
        skipComment();
      if (ch == '\'' || ch == '"') {
        skipSqlString(ch);
        continue;
      }
      if (ch == '\n')
        fileLine++;
      if (ch == ';' && begin_level == 0)
        break;
      if (recognizeBeginEnd) {
        switch (ch) {
          case 'b':             //NORES
          case 'B':
            if (skipKeyword("BEGIN")) {
              if (is_creating)
                is_creating = false;
              else
                begin_level++;
              continue;
            }
            break;

          case 'c':             //NORES
          case 'C':
            if (skipKeyword("CREATE")) {
              skipSpacing(false);
              if (skipKeyword("PROCEDURE") || skipKeyword("FUNCTION")) {
                begin_level++;
                is_creating = true;
                was_creating = true;
              }
              continue;
            }
            break;

          case 'e':             //NORES
          case 'E':
            if (skipKeyword("END")) {            //NORES
              begin_level--;
              continue;
            }
            break;

          default:
        }
      }
      read();
    }
    if ( ch == EOF ) {
      int index = 0;
      for (; index<pos && isWhiteSpace(buf[index]); ++index);
      if (index == pos)
        return null;
    }
    if ( ch == ';' && pos > 0 && !keepComments) {
      pos--;
//    if (pos == 0)
//      return null;
    }
    return new String(buf, 0, pos);
  }

  public void setKeepComments(boolean keepThem) {
    keepComments = keepThem;
  }

  public int getLine() {
    return tokenStartFileLine;
  }

  private Database      database;
  private char[]        buf;
  private int           pos;
  private int           length;
  private char          ch;
  private char          prevCh;
  private int           filePos;
  private int           tokenStartFilePos;
  private int           fileLine;
  private int           tokenStartFileLine;
  //!cxing for BTS# 88893
  private Reader   sqlReader;
//!  private InputStream   sqlInputStream;
  private String        sqlString;
  private String        sqlFileName;
  private boolean       started;
  private boolean       recognizeBeginEnd;
  private boolean       ignoreCR;
  private boolean       keepComments;
}

