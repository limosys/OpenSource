//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/SQLElement.java,v 7.0.2.1 2004/05/05 00:28:41 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! SQLElement
//! We keep track of columns and tables using SQLElement in QueryAnalyzer and
//! UniqueQueryAnalyzer.
//! It is also used as a super class of the tokens generated from the SimpleParser.java
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.DiagnosticJLimo;

// An SqlElement used as a place holder in QueryAnalyzer and UniqueQueryAnalyzer
// Also serves as base class for QueryParseToken
// public because MetaData needs the interface with UniqueQueryAnalyzer.
//
/**
 * This class is used internally by other com.borland classes.
 * You should never use this class directly.
 */
public class SQLElement
{
  SQLElement() {
  }

  // General token constructor.
  //
  SQLElement(String name) {
    tokenName    = name;
  }

  SQLElement(String name, String alias, String prefix1, String prefix2, String prefix3) {
    tokenName    = name;
    tokenAlias   = alias;
    tokenPrefix1 = prefix1;
    tokenPrefix2 = prefix2;
    tokenPrefix3 = prefix3;
  }

  SQLElement(SQLElement element) {
    set(element);
  }

  // General Object stuff
  public boolean equals(Object object) {
    if (object == null || !(object instanceof SQLElement))
      return false;
    SQLElement other = (SQLElement)object;
    return equals(tokenName,    other.tokenName) &&
           (!tokenName.equals("INTERNALROW") || (quoted & 1) == (other.quoted & 1)) &&  // Special for JDataStore quote is meaningfull for INTERNALROW
           equals(tokenPrefix1, other.tokenPrefix1) &&
           equals(tokenPrefix2, other.tokenPrefix2) &&
           equals(tokenPrefix3, other.tokenPrefix3);
  }

  public boolean isUnquotedInternelRow() {
    return tokenName.equals("INTERNALROW") && (quoted & 1) == 0;
  }

  public int hashCode() {
    int hash = 0xABCDEF77;
    if (tokenName != null)
      hash ^= tokenName.hashCode();
    if (tokenPrefix1 != null)
      hash ^= tokenPrefix1.hashCode();
    if (tokenPrefix2 != null)
      hash ^= tokenPrefix2.hashCode();
    if (tokenPrefix3 != null)
      hash ^= tokenPrefix3.hashCode();
    return hash;
  }

  public String toString() {
    return "{"+tokenName+", "+tokenPrefix3+", "+tokenPrefix2+", "+tokenPrefix1+", "+tokenAlias+"}";
  }

  // General stuff

  boolean equalElement(SQLElement anElement) {
    return (anElement != null)                         &&
           equals(tokenName,anElement.tokenName)       &&
           equals(tokenPrefix1,anElement.tokenPrefix1) &&
           equals(tokenPrefix2,anElement.tokenPrefix2) &&
           equals(tokenPrefix3,anElement.tokenPrefix3) &&
           equals(tokenAlias,anElement.tokenAlias);
  }

  boolean hasSameId(SQLElement anElement) {
    return (anElement != null)                         &&
           equals(tokenName,anElement.tokenName)       &&
           equals(tokenPrefix1,anElement.tokenPrefix1) &&
           equals(tokenPrefix2,anElement.tokenPrefix2) &&
           equals(tokenPrefix3,anElement.tokenPrefix3);
  }

  private boolean equals(String s1, String s2) {
    return s1 == s2 || (s1 != null && s1.equals(s2));
  }

  void set(SQLElement element) {
    tokenName    = element.tokenName;
    tokenAlias   = element.tokenAlias;
    tokenPrefix1 = element.tokenPrefix1;
    tokenPrefix2 = element.tokenPrefix2;
    tokenPrefix3 = element.tokenPrefix3;
    quoted       = element.quoted;
  }

  void mkColumnOfTable(SQLElement table) {
    mkColumnOfTable(table,(quoted&1) != 0);
  }

  void mkColumnOfTable(SQLElement table, boolean quoteName) {
    if (table.tokenAlias != null) {
      tokenPrefix1 = table.tokenAlias;
      tokenPrefix2 = null;
      tokenPrefix3 = null;
      quoted = (quoteName ? 1 : 0) + ((table.quoted & 2) != 0 ? 4 : 0);
    }
    else {
      tokenPrefix1 = table.tokenName;
      tokenPrefix2 = table.tokenPrefix1;
      tokenPrefix3 = table.tokenPrefix2;
      quoted = (quoteName ? 1 : 0) + ((table.quoted & 1) != 0 ? 4 : 0) + ((table.quoted & (4+8)) << 1);
    }
  }

  boolean isColumnFromTable(SQLElement table, String userName) {
    DiagnosticJLimo.check(tokenPrefix1 != null);
    if (table.tokenAlias != null)
      return (tokenPrefix1.equalsIgnoreCase(table.tokenAlias) && tokenPrefix2 == null && tokenPrefix3 == null);
    else {
      if (!tokenPrefix1.equalsIgnoreCase(table.tokenName))
        return false;
      if (tokenPrefix2 == null && table.tokenPrefix1 != null && !table.tokenPrefix1.equalsIgnoreCase(userName))
        return false;
      if (tokenPrefix2 != null && (table.tokenPrefix1 != null ? !tokenPrefix2.equalsIgnoreCase(table.tokenPrefix1) : !tokenPrefix2.equalsIgnoreCase(userName)))
        return false;
      return (tokenPrefix3 == null ? table.tokenPrefix2 == null : tokenPrefix3.equalsIgnoreCase(table.tokenPrefix2));
    }
  }

  // Accessor methods

         void setName(String name) { tokenName = name; }
  public String getName() { return tokenName; }

         void setPrefixName(String name) { tokenPrefix1 = name; }
  public String getPrefixName() { return tokenPrefix1; }
  public String getPrefix2Name() { return tokenPrefix2; }
  public String getPrefix3Name() { return tokenPrefix3; }

         void setAlias(String alias) { tokenAlias = alias; }
  public String getAlias() { return tokenAlias; }

  public String getLabelName() { return tokenAlias != null ? tokenAlias : tokenName; }

  // Special db stuff

  boolean getLabelQuote() {
    if (tokenAlias != null)
      return ((quoted & 2) != 0);
    else
      return ((quoted & 1) != 0);
  }

  void setQuotes(boolean nameQuote, boolean aliasQuote, boolean prefix1Quote, boolean prefix2Quote, boolean prefix3Quote) {
    quoted = 0;
    if (nameQuote)
      quoted += 1;
    if (aliasQuote)
      quoted += 2;
    if (prefix1Quote)
      quoted += 4;
    if (prefix2Quote)
      quoted += 8;
    if (prefix3Quote)
      quoted += 16;
  }

  void unquoteStrings(QueryAnalyzer analyzer) {
    quoted |= tokenName    != null && tokenName.charAt(0)    == analyzer.quoteCharacter ?  1 : 0;
    quoted |= tokenAlias   != null && tokenAlias.charAt(0)   == analyzer.quoteCharacter ?  2 : 0;
    quoted |= tokenPrefix1 != null && tokenPrefix1.charAt(0) == analyzer.quoteCharacter ?  4 : 0;
    quoted |= tokenPrefix2 != null && tokenPrefix2.charAt(0) == analyzer.quoteCharacter ?  8 : 0;
    quoted |= tokenPrefix3 != null && tokenPrefix3.charAt(0) == analyzer.quoteCharacter ? 16 : 0;
    tokenName    = analyzer.unquoteString(tokenName);
    tokenAlias   = analyzer.unquoteString(tokenAlias);
    tokenPrefix1 = analyzer.unquoteString(tokenPrefix1);
    tokenPrefix2 = analyzer.unquoteString(tokenPrefix2);
    tokenPrefix3 = analyzer.unquoteString(tokenPrefix3);
  }

  private void appendItem(StringBuffer buffer, String item, int mask, char quoteCharacter, boolean dot) {
    if ((quoted & mask) != 0)
      buffer.append(quoteCharacter);
    buffer.append(item);
    if ((quoted & mask) != 0)
      buffer.append(quoteCharacter);
    if (dot)
      buffer.append('.');
  }

  String generateString(char quoteCharacter,boolean ignorePrefix, boolean ignoreAlias) {

    StringBuffer buffer = new StringBuffer(10);
    if (quoteCharacter == '\0')
      quoted = 0;

    // Add Prefix:
    if ((!ignorePrefix || tokenName.equals("*") || tokenPrefix2 != null) && tokenPrefix1 != null) {
      if (tokenPrefix3 != null)
        appendItem(buffer, tokenPrefix3, 16, quoteCharacter, true);
      if (tokenPrefix2 != null)
        appendItem(buffer, tokenPrefix2,  8, quoteCharacter, true);
      if (tokenPrefix1 != null)
        appendItem(buffer, tokenPrefix1,  4, quoteCharacter, true);
    }

    // Add Name:
    appendItem(buffer, tokenName,  1, quoteCharacter, false);

    // If this token is aliased, add an <AS> and the <alias name>.
    if (!ignoreAlias && tokenAlias != null) {
      buffer.append(' ');
      appendItem(buffer, tokenAlias,  2, quoteCharacter, false);
    }
    return buffer.toString();
  }

  // Instance variables

  private String tokenName;      // The column/table name
  private String tokenPrefix1;   // Table or Schema:    table1.column1          or  schema1.table1
  private String tokenPrefix2;   // Schema or Database: schema1.table1.column1  or  db1.schema1.table1
  private String tokenPrefix3;   // Database:           db1.schema1.table1.column1
  private String tokenAlias;     // The column/table alias:  col1 as fname      or  table1 as t1
  private int    quoted;
}
