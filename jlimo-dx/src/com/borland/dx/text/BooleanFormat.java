//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/BooleanFormat.java,v 7.0 2002/08/08 18:40:10 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import java.lang.*;
import java.util.*;
import java.text.*;

/**
 *The <CODE>BooleanFormat</CODE> component uses a string-based pattern,
 * typically a <CODE>java.text.Format</CODE> used to control the formatting
 * of boolean values. This class is helpful when working with values that can
 * have two values, stored as <STRONG>true</STRONG> or <STRONG>false</STRONG>,
 *  but formatted using string values you specify. For example, you can store
 *  gender information in a column of type <STRONG>boolean</STRONG> but have
 * JBuilder format the field to display and accept input values of "Male" and "Female".
 *  In addition, the <CODE>BooleanFormat</CODE> class allows a third string to display
 *  for <STRONG>null</STRONG> values for data that has not yet been entered.
*<!-- JDS end -->
*
*<P>The <CODE>BooleanFormat</CODE> pattern consists of three parts,
*  separated by semicolons:
*
*<UL>
*<LI>The format string for <STRONG>true</STRONG> values
*<LI>The format string for <STRONG>false</STRONG> values
*<LI>The format string for <STRONG>null</STRONG> values, for example,
* when a field is left blank. If this part of the pattern is not supplied,
* the default value of <STRONG>false</STRONG> is stored for blank data values
* and the data is formatted according to the format string for <STRONG>false</STRONG>
*  values.
*</UL>
*
*<P>The following table illustrates valid patterns and their formatting effects:
*
*<P><BTBL><STRONG><CODE>BooleanFormat</CODE> patterns and formats</BTBL></STRONG>
*
*<P><TABLE CELLSPACING=0  BORDER=1 WIDTH=583>
*
*<TR><TH ALIGN="LEFT" WIDTH="45%" VALIGN="TOP">BooleanFormat specification</TH>
*<TH ALIGN="LEFT" WIDTH="20%" VALIGN="TOP">Format for true values</TH>
*<TH ALIGN="LEFT" WIDTH="20%" VALIGN="TOP">
*Format for false values</TH>
*<TH ALIGN="LEFT" WIDTH="15%" VALIGN="TOP">Format for null values </TH>
*</TR>
*
*
*
*<TR><TD WIDTH="45%" VALIGN="TOP">
*"T;F;F"</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*T</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*F</TD>
*<TD WIDTH="15%" VALIGN="TOP">
*F</TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*"male;female"</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*Male</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*female</TD>
*<TD WIDTH="15%" VALIGN="TOP">
*female</TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*"smoker;;"</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*smoker</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*(blank)</TD>
*<TD WIDTH="15%" VALIGN="TOP">
*(blank)</TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*"smoker;nonsmoker;(unknown)"</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*smoker</TD>
*<TD WIDTH="20%" VALIGN="TOP">
*nonsmoker</TD>
*<TD WIDTH="15%" VALIGN="TOP">
*(unknown)</TD>
*</TR>
*
*
*</TABLE>
*
*<P>This class can be assigned to the following {@link com.borland.dx.dataset.Column}
* <CODE>Column</CODE></A> properties:
*
*<UL>
*<LI>{@ link com.borland.dx.dataset.Column} <CODE>displayMask</CODE></A>
*<LI>{@link com.borland.dx.dataset.Column} <CODE>editMask</CODE></A>
*<LI>{@link com.borland.dx.dataset.Column}
* <CODE>exportDisplayMask</CODE></A>
*</UL>
*
*
*<P>When used as an <CODE>editMask</CODE>, the field width for data entry is set
*  to the longest of the three parts in the  pattern. All characters of the pattern
* are set as optional which means that the user is able to type any values into
*  this field when entering or editing data. When leaving the field,
*  the formatter is used to validate the data entered against the pattern specification
*  and if invalid, will generate a {@link com.borland.dx.dataset.ValidationException}
* <CODE>ValidationException</CODE></A>.
*<!-- JDS start - remove sentence and rewrite beginning of next -->
*If your application includes a {@link com.borland.dbswing.JdbStatusLabel}
* <CODE>com.borland.dbswing.JdbStatusLabel</CODE></A> (or other status listener),
*  <CODE>ValidationExceptions</CODE> appear on the <CODE>StatusLabel</CODE>. Otherwise,
*  <CODE>ValidationException</CODE> errors display in the {@link com.borland.dbswing.DBExceptionDialog}
* <CODE>DBExceptionDialog</CODE></A>.
*<!-- JDS end -->
*
*<P>You can input abbreviations if  your pattern is specified such that each part is a unique string. For example, the following table illustrates the values entered for various input into a field with a  specification of "Yes;No;Don't know":
*
*
*<P><TABLE CELLSPACING=0 BORDER=1 WIDTH=375>
*<TR>
*<TH ALIGN="LEFT" WIDTH="45%" VALIGN="TOP">
*Input value</TH>
*<TH ALIGN="LEFT" WIDTH="55%" VALIGN="TOP">
*Parsed and stored as</TH>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*Yes</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*<STRONG>true</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*y</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*<STRONG>true</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*No</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*<STRONG>false</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*Don't know</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*assigned <STRONG>null</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*Y</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*<STRONG>true</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*Ye</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*<STRONG>true</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*N</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*<STRONG>false</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*D</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*assigned <STRONG>null</STRONG></TD>
*</TR>
*<TR><TD WIDTH="45%" VALIGN="TOP">
*"" (empty string)</TD>
*<TD WIDTH="55%" VALIGN="TOP">
*assigned <STRONG>null</STRONG></TD>
*</TR>
*</TABLE>
*
*
*
*
*
*<P>The following table illustrates the parser logic where a  pattern specification
*  contains similar initial characters, and whether the parser can or cannot determine
*  values based on abbreviated input. For a format specification
*  of "marmot;monkey;mongoose", the following input values yield results of:
*
*<P><TABLE CELLSPACING=0 BORDER=1  WIDTH=350>
*
*
*<TR>
*<TH ALIGN="LEFT" WIDTH="35%" VALIGN="TOP">
*Input value</TH>
*<TH ALIGN="LEFT" WIDTH="65%" VALIGN="TOP">
*Parsed and stored as</TH>
*</TR>
*<TR><TD WIDTH="35%" VALIGN="TOP">
*M</TD>
*<TD WIDTH="65%" VALIGN="TOP">
*(ambiguous - cannot parse)</TD>
*</TR>
*<TR><TD WIDTH="35%" VALIGN="TOP">
*mon</TD>
*<TD WIDTH="65%" VALIGN="TOP">
*(ambiguous - cannot parse)</TD>
*</TR>
*<TR><TD WIDTH="35%" VALIGN="TOP">
*ma</TD>
*<TD WIDTH="65%" VALIGN="TOP">
*marmot</TD>
*</TR>
*<TR><TD WIDTH="35%" VALIGN="TOP">
*Mong</TD>
*<TD WIDTH="65%" VALIGN="TOP">
*mongoose</TD>
*</TR>
*</TABLE>
*
*
*
*<P><BSCAN><STRONG>Note: </STRONG></BSCAN> Upper and lowercase differences are ignored when parsing data against a  pattern specification.

 */
public class BooleanFormat extends java.text.Format {

  static int TRUESTR  = 0;    // offsets into strings[] for each
  static int FALSESTR = 1;
  static int NULLSTR  = 2;

  private String pattern;
  private String[] strings = new String[3];

  /**
   * Creates a BooleanFormat object
   */
  public BooleanFormat () {
    applyPattern(null);
  }

  /**
   * Creates a BooleanFormat object with the specified pattern.
   * @param pattern String
   */
  public BooleanFormat (String pattern) {
    this();
    applyPattern(pattern);
  }

  /**
   * Sets the BooleanFormat specification that defines the format of true,
   * false, and null values. If an empty pattern is used, this method defaults
   *  to "true;false"
   * @param pattern String
   */
  public void applyPattern(String pattern) {
    // If we don't have a pattern or someone has asked for the null
    // pattern, we default to a simple "true;false" pattern
    if (pattern == null || pattern.length() == 0)
      pattern = "true;false";   // NORES
    this.pattern = pattern;
    decomposePattern();
  }

  /**
   * Returns the String pattern specification of the BooleanFormat component.
   * @return String
   */
  public String toPattern() {
    return (pattern == null) ? null : pattern;
  }

  private void decomposePattern() {

    strings[0] = strings[1] = strings[2] = "";

    if (pattern == null || pattern.length() == 0)
      return;
    //!System.err.println("decomposing pattern: " + pattern);

    String workStr = pattern;
    for (int i = 0; i < strings.length; ++i) {
      //!System.err.println(" workStr is [" + workStr + "]");
      int index = workStr.indexOf(";");
      //!System.err.println(" index is " + index);
      if (index < 0)                // no semi means everything remaining belongs to this string
        index = workStr.length();
      if (index > 0)
        strings[i] = workStr.substring(0, index).trim();
      //!System.err.println("  strings[" + i + "] is " + strings[i]);
      if (index >= workStr.length())
        break;
      workStr = workStr.substring(index+1, workStr.length());
    }
//!/*
//! * //!RC StringTokenizer misses empty substrings
//! *    StringTokenizer tokenizer = new StringTokenizer(pattern, ";");
//! *    for (int i = 0; i < strings.length && tokenizer.hasMoreTokens(); ++i) {
//! *      strings[i] = tokenizer.nextToken();
//! *      System.err.println("boolean.decomposePattern[" + i + "] is " + strings[i]);
//! *    }
//! */
  }

  /**
   * This method formats a boolean value into a java.lang.StringBuffer.
   * @param value Boolean
   * @param result StringBuffer
   * @param pos FieldPosition
   * @return StringBuffer
   */
  public StringBuffer format(Boolean value, StringBuffer result, FieldPosition pos) {
    //!System.err.println("boolean.format(" + value + ")");
    int i = (value == null)
                ? NULLSTR
                : (value.booleanValue())
                  ? TRUESTR
                  : FALSESTR;
    if (result == null)
      result = new StringBuffer();
    result.append(strings[i]);
    //!System.err.println(" returning " + result);

    return result;
  }

  /**
   * This method parses a string into a boolean value.
   *
   * @param text String
   * @param pos ParsePosition
   *
   */
  public Boolean parse(String text, ParsePosition pos) {

    //!System.err.println("boolean.parse(" + text + ")");

    // Empty string counts as legal "unassigned", even if
    // the nullString is nonblank.
    if (text == null)
      return null;

    int posIndex = pos.getIndex();

    // Fetch out the substring starting at the given parsePosition
    String s = text.substring(posIndex);
    int textLen = s.length();

    // Part of the way we differentiate a null meaning unparsable from
    // a null meaning this is the null string is whether the ParsePosition
    // is advanced.  Advance it now presuming success.
    pos.setIndex(posIndex + textLen);

    if (textLen == 0)
      return null;

    // Need to strip out the underscore characters and make them spaces
    char[] chars = new char[textLen];
    s.getChars(0, textLen, chars, 0);
    for (int i = 0; i < textLen; ++i)
      if (chars[i] == '_')
        chars[i] = ' ';
    s = new String(chars);

    s = s.trim();
    textLen = s.length();
    if (textLen == 0)
      return null;

    //!System.err.println(" actual parse string is " + s);

    if (s.equalsIgnoreCase(strings[TRUESTR])) {
      //!System.err.println("matched true");
      return new Boolean(true);
    }

    if (s.equalsIgnoreCase(strings[FALSESTR])) {
      //!System.err.println("matched false");
      return new Boolean(false);
    }

    if (s.equalsIgnoreCase(strings[NULLSTR])) {
      //!System.err.println("matched null");
      return null;
    }

    String abbrevTrue = "";
    String abbrevFalse = "";
    String abbrevNull = "";
    if (textLen <= strings[TRUESTR].length()) {
      abbrevTrue = strings[TRUESTR].substring(0, s.length());
      //!System.err.println("true abbreviation is: " + abbrevTrue);
    }
    if (textLen <= strings[FALSESTR].length()) {
      abbrevFalse = strings[FALSESTR].substring(0, s.length());
      //!System.err.println("false abbreviation is: " + abbrevFalse);
    }
    if (textLen <= strings[NULLSTR].length()) {
      abbrevNull = strings[NULLSTR].substring(0, s.length());
      //!System.err.println("null abbreviation is: " + abbrevNull);
    }

    boolean isTrue = s.equalsIgnoreCase(abbrevTrue);
    boolean isFalse = s.equalsIgnoreCase(abbrevFalse);
    boolean isNull = s.equalsIgnoreCase(abbrevNull);

    //!System.err.println("abbrev matches are: " + isTrue + ", " + isFalse + ", " + isNull);

    if (isTrue && !isFalse && !isNull) {
      //!System.err.println("matched true abbrev");
      return new Boolean(true);
    }

    if (isFalse && !isTrue && !isNull) {
      //!System.err.println("matched false abbrev");
      return new Boolean(false);
    }

    if (isNull && !isTrue && !isFalse) {
      //!System.err.println("matched null abbrev");
      return null;
    }

    // We don't know what this is -- reset the parseposition to tell caller
    // we could not parse it.
    pos.setIndex(posIndex);
    return null;
  }


  /**
   * This method parses a string into an Object. A return value of null equals a
   * blank string or the third part of the BooleanFormat pattern. An error in
   * parsing is indicated by returning a null and by not advancing the parse position index.
   * @param source String
   * @param pos ParsePosition
   * @return Object
   */
  public final Object parseObject(String source, ParsePosition pos) {
    return parse(source, pos);
  }

  /**
   * This method formats a boolean value into a java.lang.StringBuffer
   * @param obj Object
   * @param toAppendTo StringBuffer
   * @param pos FieldPosition
   *
   */
  public final StringBuffer format(Object obj,
                                   StringBuffer toAppendTo,
                                   FieldPosition pos) {
    //! BTS 16394 format(Boolean, StringBuffer, FieldPosition) was resulting in an infinite loop
//!      return format(obj.toString(), toAppendTo, pos);
    if (obj instanceof Boolean) {
      return format((Boolean) obj, toAppendTo, pos);
    }
    else {
      throw new IllegalArgumentException(Res.bundle.getString(ResIndex.BooleanBad));
    }
  }

  public String getTrueString() {
    return strings[TRUESTR];
  }
  public String getFalseString() {
    return strings[FALSESTR];
  }
  public String getNullString() {
    return strings[NULLSTR];
  }
}
