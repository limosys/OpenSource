//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemFormatStr.java,v 7.0 2002/08/08 18:40:13 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatStr;
import com.borland.dx.text.ItemFormatter;
import com.borland.dx.text.InvalidFormatException;

import java.util.*;
import java.text.*;
import java.sql.*;

import com.borland.dx.dataset.Variant;

/***ItemFormatStr</CODE> extends the {@link com.borland.dx.text.ItemFormatter}
 * <CODE>ItemFormatter</CODE></A> class through the use of <CODE>String</CODE>
 * patterns to control formatting and parsing. Though other implementations of
 * Formatter are allowed, <CODE>ItemFormatStr</CODE> is the only one currently
 * provided. Note that the current implementation supports only <CODE>Variants</CODE>
 * and is only a wrapper for <CODE>VariantFormatStr</CODE>.
*
*<P>Four different kinds of pattern strings can be used. Each is distinct, and
* the fields from one cannot be used with another. The type used will be inferred
* from the <CODE>Variant.Type</CODE> passed into the constructor. The types are:
*<UL>
*<LI><CODE>ItemFormatter.NUMERIC</CODE> (double)
*<LI><CODE>ItemFormatter.DECIMAL</CODE> (BigDecimal)
* <LI><CODE>ItemFormatter.DATETIME</CODE>
*<LI><CODE>ItemFormatter.TEXT</CODE>
*</UL>
*
*
*<A NAME="numeric_fields"></A>
*<H3>Numeric fields</H3>
*
*<P>The numeric format mask actually consists of two semicolon separated masks.
* The first is required.  The second, if provided, will determine how negative
* numbers are formatted.  For example, <CODE>"###.##;(###.##)"</CODE> will format
*  negative numbers in parentheses.
*
*<P>The following table illustrates the characters allowed in numeric fields.
*
*<P><BTBL><STRONG>Valid characters - numeric fields</BTBL></STRONG>
*<P><TABLE CELLSPACING=0  BORDER=1>
*
*<TR>
*<TH ALIGN="LEFT" VALIGN="TOP">Symbol</TH>
*<TH ALIGN="LEFT" VALIGN="TOP">Meaning</TH>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">0</TD>
*<TD VALIGN="TOP">A digit.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">#</TD>
*<TD VALIGN="TOP">A digit, zero shows as absent.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">.</TD>
*<TD VALIGN="TOP">Placeholder for decimal separator.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">,</TD>
*<TD VALIGN="TOP">Placeholder for grouping delimiter. Shows the interval to be used.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">;</TD>
*<TD VALIGN="TOP">Separates formats. There are two: positive and negative.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">%</TD>
*<TD VALIGN="TOP">Divide by 100 and show as percentage. </TD>
*</TR>
*
*
*<TR>
*<TD VALIGN="TOP">X</TD>
*<TD VALIGN="TOP">Any other characters can be used in.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">-</TD>
*<TD VALIGN="TOP">Leading minus for negative numbers.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">()</TD>
*<TD VALIGN="TOP">Parenthesis to show the entire expression as negative
* (e.g. "($#,###.##)").</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">{}</TD>
*<TD VALIGN="TOP">Optional fields  (e.g. "0000.{00}"). Will allow the decimal
* fraction to be omitted when editing.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">^</TD>
*<TD VALIGN="TOP">Sets the initial cursor when editing.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">\nnnn{}</TD>
*<TD VALIGN="TOP">Single character literal (e.g. \002, \0x0A, \u2030).</TD>
*</TR>
*
*</TABLE>
*
*
*<A NAME="date_timestamp_fields"></A>
*<H3>Date and timestamp fields</H3>
*
*
*<P>The following table illustrates the characters allowed in date or timestamp fields.
*
*
*<P><BTBL><STRONG>Valid characters - date and timestamp fields</BTBL></STRONG>
*
*<P><TABLE CELLSPACING=0  BORDER=1>
*
*<TR>
*<TH ALIGN="LEFT" VALIGN="TOP">Symbol</TH>
*<TH ALIGN="LEFT" VALIGN="TOP">Meaning</TH>
*<TH ALIGN="LEFT" VALIGN="TOP">Presentation</TH>
*<TH ALIGN="LEFT" VALIGN="TOP">Notes</TH>
*</TR>
*
*
*<TR>
*<TD VALIGN="TOP">G</TD>
*<TD VALIGN="TOP">Era designator</TD>
*<TD VALIGN="TOP">Text</TD>
*<TD VALIGN="TOP">AD/BC</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">y</TD>
*<TD VALIGN="TOP">Year</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">Y,YY = 97, YYYY = 1997</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">M</TD>
*<TD VALIGN="TOP">Month in year</TD>
*<TD VALIGN="TOP">Text and number</TD>
*<TD VALIGN="TOP">M,MM = numeric, MMM = month abbrev, MMMM = full month</TD>
*</TR>
*
*
*<TR>
*<TD VALIGN="TOP">d</TD>
*<TD VALIGN="TOP">Day in year</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">d,dd = 10, dddd = 0010</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">h</TD>
*<TD VALIGN="TOP">Hour in am/pm (1~12)</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">h,hh = 12, hhhh = 0012</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">H</TD>
*<TD VALIGN="TOP">Hour in day (0~23)</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">H,HH = 23, HHHH = 0023</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">m</TD>
*<TD VALIGN="TOP">Minute in hour (0~23)</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">m,mm = 59, mmmm = 0059</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">s</TD>
*<TD VALIGN="TOP">Second in minute</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">s,ss = 59, ssss = 0059</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">S</TD>
*<TD VALIGN="TOP">Millisecond</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">S = 9, SS = 99, SSS = 999</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">E</TD>
*<TD VALIGN="TOP">Day in week</TD>
*<TD VALIGN="TOP">Text</TD>
*<TD VALIGN="TOP">E,EE,EEE = Sun, EEEE = Sunday</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">D</TD>
*<TD VALIGN="TOP">Day in year</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">D,DD = 364, DDDD = 0364</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">F</TD>
*<TD VALIGN="TOP">Day of week in month</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">2 (2nd Wed in July)</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">w</TD>
*<TD VALIGN="TOP">Week in year</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">27</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">W</TD>
*<TD VALIGN="TOP">Week in month</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">2</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">a</TD>
*<TD VALIGN="TOP">AM/PM marker</TD>
*<TD VALIGN="TOP">Text</TD>
*<TD VALIGN="TOP">AM/PM</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">k</TD>
*<TD VALIGN="TOP">Hour in day (1~24)</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">24</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">K</TD>
*<TD VALIGN="TOP">Hour in am/pm (0~11)</TD>
*<TD VALIGN="TOP">Number</TD>
*<TD VALIGN="TOP">0</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">z</TD>
*<TD VALIGN="TOP">Time zone</TD>
*<TD VALIGN="TOP">Text</TD>
*<TD VALIGN="TOP">z,zz,zzz = PDT, zzzz = Pacific Standard Time</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">'</TD>
*<TD VALIGN="TOP">Escape for text</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">''</TD>
*<TD VALIGN="TOP">Single quote</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">\nnnn</TD>
*<TD VALIGN="TOP">Single character literal (e.g. \002, \0x0A, \u2030)</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">()</TD>
*<TD VALIGN="TOP">Can be used to bracket optional fields
*(e.g. "0000.{00}" will allow the decimal fraction to be omitted when editing)</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">^</TD>
*<TD VALIGN="TOP">Sets the initial cursor position when editing</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*<TD VALIGN="TOP">&nbsp;</TD>
*</TR>
*
*</TABLE>
*
*<A NAME="string_fields"></A>
*<H3>String fields</H3>
*
*<P>The string editmask can actually consist of up to four distinct subfields,
*  separated by the semicolons. The subfields are:
*<UL>
*<LI>The edit mask itself
*<LI>A '0' or '1' indicating whether literals should be stripped ('0' = strip out
*  literals)
*<LI>The character to use as a "blank" indicator.  This is used to show the user
* which elements have not yet been entered.
*<LI>The character to be used to replace blank characters on output. If there is
* no character given, blank characters are stripped.
*</UL>
*
*<P>For example, the edit mask <CODE>"(999)000-0000;0;_;"</CODE> would indicate:
*<UL>
*<LI>The template the user would see on field entry would be <CODE>"(___)___-____</CODE>
*<LI>Unfilled characters will be shown by <CODE>"_"</CODE> (underscore)
*<LI>Literals will be removed on output, so <CODE>(408)555-1234</CODE>
*  would become <CODE>4085551234</CODE>
*<LI>Blank characters will be stripped, so <CODE>(___)555-1234</CODE> would
* become <CODE>5551234</CODE>
*</UL>
*
*<P>Not all three subfields are required. If subfield 3 is omitted,
* the <CODE>"_"</CODE> (underscore) character will be the blank indicator.
* If subfield 2 is omitted, literals are not removed.
*
*<P>The following table illustrates the characters allowed in string fields.
*
*<P><BTBL><STRONG>Valid characters - string fields</BTBL></STRONG>
*
*<P><TABLE CELLSPACING=0  BORDER=1>
*
*<TR>
*<TH ALIGN="LEFT" VALIGN="TOP">Symbol</TH>
*<TH ALIGN="LEFT" VALIGN="TOP">Meaning</TH>
*</TR>
*
*
*<TR>
*<TD VALIGN="TOP">0</TD>
*<TD VALIGN="TOP">Digit (0 through 9, entry required; plus [+] and minus [-]
* signs not allowed).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">9</TD>
*<TD VALIGN="TOP">Digit or space (entry not required; plus and minus
* signs not allowed).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">#</TD>
*<TD VALIGN="TOP">Digit or space (entry not required; blank positions converted
*  to spaces, plus and minus signs allowed).</TD>
*</TR>
*
*
*<TR>
*<TD VALIGN="TOP">L</TD>
*<TD VALIGN="TOP">Letter (A through Z, entry required).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">l</TD>
*<TD VALIGN="TOP">Letter (A through Z, entry optional).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">?</TD>
*<TD VALIGN="TOP">Letter (A through Z, entry optional).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">A</TD>
*<TD VALIGN="TOP">Letter or digit (entry required).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">a</TD>
*<TD VALIGN="TOP">Letter or digit (entry optional).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">C</TD>
*<TD VALIGN="TOP">Any character or a space (entry optional).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">c</TD>
*<TD VALIGN="TOP">Any character or a space (entry required).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">&amp;</TD>
*<TD VALIGN="TOP">Any character or a space (entry required).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">&lt;</TD>
*<TD VALIGN="TOP">Causes all characters that follow to be converted to lowercase.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">&gt;</TD>
*<TD VALIGN="TOP">Causes all characters that follow to be converted to uppercase.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">!</TD>
*<TD VALIGN="TOP">Causes input mask to fill from right to left, rather than from left to right when characters on the left side of the input mask are optional. The exclamation point can be included anywhere in the input mask.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">\</TD>
*<TD VALIGN="TOP">Causes the character that follows to be displayed as a literal character.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">\</TD>
*<TD VALIGN="TOP">Causes the character that follows to be displayed as a literal character. Used to display any of the characters listed in this table as literal characters (for example, \A is displayed as just A).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">\nnnn</TD>
*<TD VALIGN="TOP">Single character literal (e.g. \002, \0x0A, \u2030).</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">''</TD>
*<TD VALIGN="TOP">Encloses a literal expression (for example, the pattern "990' units sold'" would display as "27 units sold").</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">**</TD>
*<TD VALIGN="TOP">Encloses a password encrypted string. For example, the pattern "*AAAAaaaa*" would accept a password of at least 4, and at most 8, alphanumeric characters. The characters would echo as '*' as they were typed.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">{}</TD>
*<TD VALIGN="TOP">Can be used to bracket optional fields. For example, "LLLL{LLL}" will allow a user to edit at least (but optionally up to 7) letters.</TD>
*</TR>
*
*<TR>
*<TD VALIGN="TOP">^</TD>
*<TD VALIGN="TOP">Sets the initial cursor position when editing.</TD>
*</TR>
*
*</TABLE>
*
*
*
*

 */
public class ItemFormatStr extends  ItemFormatter {
  VariantFormatStr vFormatter;

  /**
   * This is the constructor to build a string-based implementation
   * of the Formatter interface.
   *
   * @param pattern Is the string of special characters used to format
   * values when using the format() method of this interface.  If this
   * value is null (or empty), the best "default" pattern will be selected based
   * on the locale.
   *
   * @param formatterType Is the type of pattern being used.  This can be any
   * one of the following 4 types: ItemFormatter.NUMERIC, ItemFormatter.DATETIME,
   * ItemFormatter.TEXT, or ItemFormatter.DECIMAL.  A value of zero will default
   * to ItemFormatter.TEXT.
   *
   * @param locale Is the locale to control this pattern.  This will determine
   * things like the decimal point sign, the currency sign, etc.  If this value
   * is null, the current default locale will be used.
   */
  public ItemFormatStr(String pattern, int variantType, Locale locale) {
    vFormatter = new VariantFormatStr(pattern, variantType, locale);
  }

  public ItemFormatStr(String pattern, int variantType) {
    this(pattern, variantType, null);
  }

  // ------------ Extension Methods for ItemFormatter -------------------
/**
* Constructs a String representing the given
* value stored in the supplied Variant.  All reasonable attempts will
* be made to "cast" the type found in the variant into the appropriate
* type specified in the constructor of the implementors of this.
*
* @param value Contains the value to be formatted.  It will be cast to
* the appropriate type where possible
*
* @return A String containing the formatted data.  An empty
* string indicates a null or empty input value.  A null return means
* the formatting failed.  Be ready for that null possibility.
*/
  public String format(Object value) throws InvalidFormatException {
    if (!(value instanceof Variant))
      throw new InvalidFormatException(Res.bundle.getString(ResIndex.MustBeVariant));
    return vFormatter.format((Variant) value);
  }

/**
* Parses (i.e. analyzes) the given String and produces as
* output a Object containing the appropriate value.
*
* @param stringValue Contains the string to be parsed.  A null or empty
* stringValue will return a Variant.ASSIGNED_NULL variant.
*
*/
  public Object parse(String stringValue) throws InvalidFormatException {
    Variant v = new Variant();
    vFormatter.parse(stringValue, v);
    return (Object) v;
  }

  /**
   * This method will return the pattern currently being used by this
   * ItemFormatStr object for formatting and parsing.
   */
  public String getPattern() {
    return vFormatter.getPattern();
  }

  /**
   * This method will set the current pattern used by this ItemFormatStr
   * object for formatting and parsing.
   *
   * @param pattern Contains the new patter to use.  If it is null or
   * empty, this routine will automatically select the best default it
   * can based on the current locale and format type.
   *
   * @return The prior pattern (also available via getPattern()) is returned.
   * This is a convenience in case a temporary pattern is being set (to be
   * reset a short time later)
   */
  public String setPattern(String pattern) {
    return vFormatter.setPattern(pattern);
  }

  /**
   * Sets the "special" item associated with a particular ItemFormatter.
   * This is a general purpose routine to set particular booleans, characters,
   * flags etc inside a formatter -- but it is completely dependent on the
   * formatter being used.
   *
   * @param objType contains an identifier telling which object in which formatter to set.  Currently may be one of:
   *
   *   ItemFormatter.FILLCHARACTER  [Character] For Text formatters, the char to place in empty slots in the string
   *   ItemFormatter.REPLACECHARACTER  [Character] For Text formatters, the char used to replace FILLCHARACTER on output
   *
   *
   * @param obj Contains the object to be set.  The type of the Object MUST match the expected type for the
   * given 'objType'.  Do NOT pass in a null object.
   *
   * @return The object containing the prior value of this item.  Useful for restoring to original value after
   * temporary alteration.
   */
  public Object setSpecialObject(int objType, Object obj) {
    return vFormatter.setSpecialObject(objType, obj);
  }

  /**
   * Retrieves the "special" item associated with a particular ItemFormatter.
   * This is a general purpose routine to get particular booleans, characters,
   * flags etc inside a formatter -- but it is completely dependent on the
   * formatter being used.
   *
   * @param objType contains an identifier telling which object in which formatter to retrieve.  Currently may be one of:
   *
   * ItemFormatter.FILLCHARACTER  [Character] For Text formatters, the char to place in empty slots in the string
   * ItemFormatter.REPLACECHARACTER  [Character] For Text formatters, the char used to replace FILLCHARACTER on output
   *
   *
   * @return The object containing the value of this item.  temporary alteration.
   */
  public Object getSpecialObject(int objType) {
    return vFormatter.getSpecialObject(objType);
  }

  /**
   * Returns the Locale being used by this ItemFormatter.
   *
   * @return The Locale used by the VariantFormatter (will never be null)
   */
  public Locale getLocale() {
    return vFormatter.getLocale();
  }

  /**
   * Returns the JDK Format subclass associated with this ItemFormatter.
   *
   * @return The JDK Format subclass for this ItemFormatter.  Could return null.
   */
  public Format getFormatObj(){
    return vFormatter.getFormatObj();
  }
}
