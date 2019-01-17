//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemFormatter.java,v 7.0 2002/08/08 18:40:13 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.ItemFormatStr;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.TextFormat;
import com.borland.dx.text.BooleanFormat;


//!import com.borland.jbcl.text.*;
import java.text.*;
import java.util.*;


/**
 * *ItemFormatter</CODE> is an abstract class that is the superclass of
 * several others used to format and parse various types of data. Because
 *  different data types typically require different kinds of handling,
 * there are five basic parse/format types currently supported:
 *
 *<UL>
 *<LI>Numeric
 *<LI>Currency
 *<LI>Date and time
 *<LI>Text
 *<LI>Boolean
 *</UL>
 *
 *<CODE>ItemFormatter</CODE> has defined constants for each of these:
 *
 *<ul>
 *<li>NUMERIC = 1
 *<li>DECIMAL = 2
 *<li>DATETIME= 3
 *<li>TEXT = 4
 *<li>BOOLEAN = 5
 *</ul>
 *
 *<p>All Formatter classes have a <CODE>format()</CODE> method that returns
 * a data object as a string, and they have a <CODE>parse()</CODE> method that
 *  analyzes a string value and returns a data object.

*/

public abstract class ItemFormatter
{
  // ---- Following are the basic subclasses of formatter types (used internally only) -----

  /** The formatter/parser category for doubles, ints, longs, etc. Will use DecimalFormat from JDK */
  static final int NUMERIC  = 1;
  /** The formatter/parser category for currency, BigDecimal, etc.  TODO! JDK 1.1 doesn't support this yet */
  static final int DECIMAL  = 2;
  /** The formatter/parser category for date, time and timestamp.  Will use SimpleDateFormat from JDK */
  static final int DATETIME = 3;
  /** The formatter/parser for strings.  Will use util\TextFormat (subclassed from JDK Format classes) */
  static final int TEXT   = 4;
  /** The formatter/parser for boolean values.  Will use util.BooleanFormat */
  static final int BOOLEAN = 5;

// ------- Following are some special character values (used internally only) -----------------

  /** Internal value used to indicate a char is not valid */
  static final char NOTACHAR = 0xffff;
  /** Value used for identifier in get/setSpecialObject to indicate the "fill" character for empty space */
  static final int FILLCHARACTER    = 1;
  /** Valued used for identifier in get/setSpecialObject to indicate char to replace the "fill" char before parse */
  static final int REPLACECHARACTER = 2;


  /**
   * Constructs a String representing the given
   * value stored in the supplied object.  All reasonable attempts will
   * be made to "cast" the type found in the object into the appropriate
   * type specified in the constructor of the implementors of this.
   *
   * @param value Contains the value to be formatted.  It will be cast to
   * the appropriate type where possible
   *
   * @return A String containing the formatted data.  An empty
   * string indicates a null or empty input value.  A null return means
   * the formatting failed.  Be ready for that null possibility.
   */
  public abstract String format(Object value) throws InvalidFormatException;

  /**
   * Parses (i.e. analyzes) the given String and produces as
   * output a Object containing the appropriate value.
   * @param stringValue Contains the string to be parsed.  A null or empty
   *        stringValue will return a null object.
   * @return An object whose value was determined from 'stringValue'.  A null return
   *         is given when the 'stringValue' is null or empty.
   */
  public abstract Object parse(String stringValue) throws InvalidFormatException;


  /**
   * Returns the pattern currently being used by this Formatter
   * for parsing and formatting.
   * @return The current pattern being used.
   */
  public String getPattern() { return null; }

  /**
   * Sets the current pattern to be used for parsing and formatting.
   * @param pattern Contains the new pattern to use.  If null (or empty), this
   *        method will choose the "best" default pattern for the current locale.
   * @return Returns the previous pattern.
   * <P><B>Note:</B> The new pattern <B>must</B> be of the same basic type associated
   * with this type of formatter.  For example, if you used a Date/Time pattern in the
   * constructor, it would be an error to switch to a numeric pattern, since each basic
   * pattern type has its own data-dependent Formatters and Parsers.
   */
  public String setPattern(String pattern) { return pattern; }


//  /**
//   * Some Formatter implementations define special Objects for their
//   * use.  This method allows them to be set.  You must know the internal
//   * details of the Format subclass being used to use this method.
//   *
//   * @param objType Identifies the particular Object to set.
//   * Currently, the ones which can be set are:
//   *
//   *    ItemFormatter.FillCharacter  [Character] Text pattern only -- the char to use to fill blank slots
//   *    ItemFormatter.ReplaceCharacter [Character] Text pattern only -- used to replace FillChar on parse
//   *
//   * @return The prior value of this Object, which can be useful in restoring it after a temporary switch.
//   */
//  public Object setSpecialObject(int charType, Object obj) { return null; }

  /**
   * Returns the value of the named special object.
   * @param objType Identifies the special object to return.
   * @return Returns the named special object.
   * @see ItemFormatStr#setSpecialObject
   */
  public Object getSpecialObject(int objType) { return null; }

  /**
   * Returns the Locale currently being used by this Formatter.
   * Currently, there is no way to change this Locale once the Formatter
   * has been created.
   *
   * @return The Locale of this Formatter.  Will never be null.
   */
  public Locale getLocale() { return Locale.getDefault(); }

  /**
   * The ItemFormatter interface is itself a layer on the JDK's Format interface.
   * This method allows access to the underlying Format object being used
   * by this particular Formatter (which depends on the type of data being
   * formatted).
   *
   * @return The Format object being used (see JDK's description
   * of Format, NumberFormat, DecimalFormat, and SimpleTimeFormat).
   * May return null if the constructor could not accept the initial
   * pattern.
   */
  public Format getFormatObj() { return null; }
}
