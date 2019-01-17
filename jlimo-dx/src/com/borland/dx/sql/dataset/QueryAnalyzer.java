//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryAnalyzer.java,v 7.0 2002/08/08 18:39:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! QueryAnalyzer
//! Will call the SimpleParser.java to parse a query string, and then
//! has code to identify tables and columns of a query.
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.*;

import java.lang.*;
import java.util.*;
import java.sql.*;

/**
 *
 * This class is used internally by other com.borland classes.
 * You should never use this class directly.
 */
class QueryAnalyzer
{
  QueryAnalyzer(Database database, String query) {
    this.database = database;
    this.query = query;
  }

  // Scan token list from parser to determine number of tables
  // in SELECT clause. Returns a Vector of strings. Each table
  // is represented by 4 strings: tableName, schemaName,
  // databaseName, and aliasName.
  //
  Vector getTables() /*-throws DataSetException-*/ {
    if (parser == null)
      parse();

    QueryParseToken   tokens = parser.getParsedTokens();
    String            tableName, schemaName, aliasName;
    Vector            result = new Vector(1);

    DiagnosticJLimo.trace(Trace.QueryAnalyze,"Get table names from parser");

    while (tokens != null) {

      if (tokens.isTable())
      {
        // If the tableName is not quoted, see if we need to upper or lower case the string in
        // the query. The rationale behind this is that we use the tableName in calls to the
        // Jdbc API. The Jdbc API treats literals (unquoted strings) as quoted strings when
        // passing the string to the server. Therefore, a query like: "select * from foo" against
        // an Oracle server will work if the relation is stored as "FOO" on the server. When we
        // pass the string "foo" to Jdbc to get column, index, ... information, Jdbc asks the
        // server for meta information on "foo" and doesn't find a match.

        SQLElement element = new SQLElement(tokens);
        element.unquoteStrings(this);
        result.addElement(element);
      }
      tokens = tokens.getNextToken();
    }

    return result;
  }

  // Scan token list from parser to determine number of columns
  // in a SELECT clause. Returns a Vector of column names.
  //
  Vector getColumns() /*-throws DataSetException-*/ {
    if (parser == null)
      parse();

    QueryParseToken   tokens = parser.getParsedTokens();
    String            columnName, tableName, aliasName;
    Vector            result = new Vector(10);

    while (tokens != null && !tokens.isWhere()) {

      if (tokens.isField()) {
        SQLElement element = new SQLElement(tokens);
        element.unquoteStrings(this);
        result.addElement(element);
      }
      else if (tokens.isExpression()) {
        result.addElement(null);
      }
      tokens = tokens.getNextToken();
    }

    return result;
  }

  // Scan token list from parser to determine the parameter names
  // in a SELECT clause. Returns a Vector of parameter names.
  //
  Vector getParameters() /*-throws DataSetException-*/ {
    if (parser == null)
      parse();

    QueryParseToken   tokens = parser.getParsedTokens();
    String            name;
    Vector            result = new Vector(10);

    while (tokens != null) {

      if (tokens.isParameter()) {
        name = tokens.getName();
        name = unquoteString(name);
        result.addElement(name);
      }

      tokens = tokens.getNextToken();
    }

    return result;
  }

  // Method used to correct the case of an identifier returned from a metadata
  // call, where the database is caseInsensitive.
  String adjustCase(String dbString) {
    DiagnosticJLimo.check(adjustIdentifiers);
    if (quoteCharacter != '\0') {
      if (mkUpperQuoted)
        return dbString.toUpperCase();
      else
        return dbString.toLowerCase();
    }
    else {
      if (mkUpper)
        return dbString.toUpperCase();
      else
        return dbString.toLowerCase();
    }
  }

  String unquoteString(String dbString)
  {
    if (dbString == null || dbString.length() == 0)
      return null;

    if (dbString.charAt(0) != quoteCharacter) {
      if (isCaseInsensitive) {
        if (mkUpper)
          dbString = dbString.toUpperCase();
        else
          dbString = dbString.toLowerCase();
      }
    }
    else {
      // Store the string without quotes (JDBC API doesn't require quotes for parameters).
      dbString = dbString.substring(1,dbString.length()-1);

      if (isCaseInsensitiveQuoted) {
        if (mkUpperQuoted)
          dbString = dbString.toUpperCase();
        else
          dbString = dbString.toLowerCase();
      }
    }
    return dbString;
  }

  // Parse the query.
  //
  boolean parse() /*-throws DataSetException-*/ {
    DiagnosticJLimo.trace(Trace.QueryAnalyze,"Invoking parser");
    init();
    parser = new SimpleParser(query, quoteCharacter);
    Object tokens = parser.getParsedTokens();
    couldParse = (tokens != null);
    return couldParse;
  }

  boolean couldParse() {
    return couldParse;
  }

  // Init the private method variables.
  //
  void init() /*-throws DataSetException-*/ {

    isCaseInsensitive = !database.isUseCaseSensitiveId();
    mkUpper = isCaseInsensitive && !database.storesLowerCaseIdentifiers();
    adjustIdentifiers = isCaseInsensitive;

    quoteCharacter = database.getIdentifierQuoteChar();
    if (quoteCharacter != '\0') {
      isCaseInsensitiveQuoted = !database.isUseCaseSensitiveQuotedId();
      mkUpperQuoted = isCaseInsensitiveQuoted && !database.storesLowerCaseQuotedIdentifiers();
      adjustIdentifiers = isCaseInsensitiveQuoted;
    }

    // Get the user name
    try {
      userName = database.getMetaData().getUserName();
      userName = unquoteString(userName);
    }
    catch (Exception ex) {
      DiagnosticJLimo.printStackTrace(ex);
      userName = null;
    }
  }

  // "internal" variables, non private members are left in package scope
  // to obtain obfuscation, they should really be protected.

  private   String            query;                   // setup in constructor
            Database          database;                // setup in constructor
            char              quoteCharacter;          //    "
            boolean           adjustIdentifiers;       //    "
  private   boolean           isCaseInsensitive;       //    "
  private   boolean           mkUpper;                 //    "
  private   boolean           isCaseInsensitiveQuoted; //    "
  private   boolean           mkUpperQuoted;           //    "
  private   boolean           couldParse;
            String            userName;                // setup in init() from database
            SimpleParser      parser;                  // setup in parse()
}
