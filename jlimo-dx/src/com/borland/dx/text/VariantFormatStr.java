//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/VariantFormatStr.java,v 7.2.2.1 2004/04/21 00:24:11 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.SystemResourceBundle;
import com.borland.dx.text.VariantFormatter;
import com.borland.dx.text.ItemFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.TextFormat;
import com.borland.dx.text.BooleanFormat;
import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;

import java.util.*;
import java.text.*;
import java.math.*;
import java.sql.*;


import com.borland.dx.dataset.Variant;

/**
 * This class adds String patterns for formatting and parsing control.
 *
<a name="_top_"></a>
<a name="maskpatterns"></a>
<h2>String-based patterns (masks)</h2>
<!--BNDX="masks;edit masks;display masks;import masks;export masks;editing;formatting;parsing;patterns;String-based patterns"-->

<p>In DataExpress components, the pattern syntax for all {@link com.borland.dx.dataset.Column#setEditMask(java.lang.String) edit masks}, @{link com.borland.dx.dataset.Column#setDisplayMask display masks}, and {@link com.borland.dx.dataset.Column#setExportDisplayMask(java.lang.String) import/export masks}, and for parsing such masks, is a direct extension of that used by the format classes in the {@link java.text package}. There are separate patterns and symbols for <a href="#numericdatapatterns">numeric data</a>, <a href="#datetimedatapatterns">date/time data</a>, <a href="#textdatapatterns">text data</a> and boolean data (see {@link com.borland.dx.text.BooleanFormat com.borland.dx.text.BooleanFormat}. </p>

<p>Some of these symbols, such as the braces to indicate optional portions, are additions to the JDK. The DataExpress classes ensure that such extensions are never sent to the JDK classes (which would not understand them). The extensions are used and then stripped from the pattern.</p>

<p><span class="bscan">Note:</span> For ease of use, the symbol definitions are designed to be compatible with those in Borland&#39;s Delphi product. In those cases where Visual Basic is different from, but not incompatible with Delphi, those symbols are defined as well.</p>


<!--****************************NUMERIC data MASKS************************-->
<br><br>
<a name="numericdatapatterns"></a>
<h3>Numeric data patterns</h3>
<!--BNDX="numeric masks;masks:numeric"-->

<p>A numeric pattern consists of two formats, separated by a semicolon. The second pattern, when present, determines how negative numbers are formatted and displayed. </p>

<p>To format the display or editing of a numeric data, use the symbols in the tables below.</p>

<a name="jdk_standard_numeric_format_symbols"></a>
<h4>JDK standard numeric format symbols</h4>

<table border="1">
<tr>
<th>Symbol</th>
<th>Meaning</th>
<th>Notes</th>
</tr>

<tr>
<td valign="top">0</td>
<td valign="top">In an edit pattern, a required digit
<br>
In a display pattern, indicates that leading zeroes are displayed.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">#</td>
<td valign="top">In an edit pattern, an optional digit
<br>
In a display pattern, indicates that leading zeroes do not display.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">.
<br>
(period)</td>
<td valign="top">Placeholder for the decimal separator.</td>
<td valign="top">This is not necessarily the actual character displayed as the separator. That is determined by your Locale.</td>
</tr>

<tr>
<td valign="top">,
<br>
(comma)</td>
<td valign="top">Placeholder for the grouping (thousands) separator, showing the grouping to be used.</td>
<td valign="top">This is not necessarily the actual character displayed as the separator. That is determined by your Locale.</td>
</tr>

<tr>
<td valign="top">;
<br>
(semicolon)</td>
<td valign="top">Format separator. Place a semicolon between the format pattern for a positive number (first) and the pattern for a negative number (second).</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">%</td>
<td valign="top">Divide entry by 100 and show as a percentage.</td>
<td valign="top">&#160;</td>
</tr>

<!--put this row back in when the bug is fixed<TR>
<TD VALIGN="top">undefined symbols</TD>
<TD VALIGN="top">Any characters not defined as symbols are interpreted as literals that always appear in the formatted string.</TD>
<TD VALIGN="top">Use of this is deprecated, as the result is unreliable. Due to bugs in the JDK, some characters are ignored while others cause an exception.</TD>
</TR>-->
<tr>
<td valign="top">-
<br>
(hyphen)</td>
<td valign="top">A leading minus sign appears for negative numbers.</td>
<td valign="top">Do not use together with parentheses.</td>
</tr>

<tr>
<td valign="top">()</td>
<td valign="top">The entire expression is enclosed in parentheses to show that it is negative, for example: ($#,###.##).</td>
<td valign="top">Do not use together with the hyphen.</td>
</tr>

<!-- JDS start - remove link -->
<tr>
<td valign="top">\nnnn</td>
<td valign="top">Single character literal.</td>
<td valign="top">Enter the octal, hexidecimal, or Unicode umber for the desired character. This symbol escapes even out of single quotes.</td>
</tr>
</table>

<!-- JDS end -->

<a name="dataexpress_additional_numeric_format_symbols"></a>
<h4>DataExpress additional numeric format symbols</h4>

<table border="1">
<tr>
<th>Symbol</th>
<th>Meaning</th>
<th>Notes</th>
</tr>

<tr>
<td valign="top">{}</td>
<td valign="top">Use to bracket optional portions of the pattern.</td>
<td valign="top">Entering ####.{##} allows the user to choose whether to enter the decimal fraction.</td>
</tr>

<tr>
<td valign="top">^
<br>
(carat)</td>
<td valign="top">Sets the initial cursor position when editing.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">&#39;
<br>
(single quote)</td>
<td valign="top">Enclose text constants. This method of including constants is preferred to the other symbol styles, as being more reliable.</td>
<td valign="top">&#39;Lira&#39; yields the words &quot;Lira&quot; in the data.</td>
</tr>
</table>


<!--****************************DATE/TIME data MASKS************************-->
<br><br>
<a name="datetimedatapatterns"></a>
<h3>Date/time data patterns</h3>
<!--BNDX="date/time masks;masks:date/time"-->
<p>To format the display or editing of a date or timestamp data, use the symbols in the tables that follow.</p>

<a name="jdk_standard_date-time_format_symbols"></a>
<h4>JDK standard date/time format symbols</h4>

<table border="1">
<tr>
<th>Symbol</th>
<th>Meaning</th>
<th>Presentation</th>
</tr>

<tr>
<td valign="top">G</td>
<td valign="top">Era designator.</td>
<td valign="top">A &quot;G&quot; in the pattern yields AD.</td>
</tr>

<tr>
<td valign="top">y</td>
<td valign="top">Year. Enter four &quot;y&quot;s to format to all four digits of the year. Enter &quot;y&quot; or &quot;yy&quot; to yield the last two digits of the year.</td>
<td valign="top">yyyy yields 1996
<br>
yy yields 96.</td>
</tr>

<tr>
<td valign="top">M</td>
<td valign="top">The month of the year.</td>
<td valign="top">&quot;M&quot; yields the number of the month (February would be 2).
<br>
&quot;MM&quot; yields the number of the month, but single digits have leading zeroes (02).
<br>
&quot;MMM&quot; yields the abbreviation of the month (Feb).
<br>
&quot;MMMM&quot; yields the full name of the month (February).</td>
</tr>

<tr>
<td valign="top">d</td>
<td valign="top">The day of the month.</td>
<td valign="top">For Christmas Day, entering:
<br>
&quot;d&quot; yields 25
<br>
&quot;dd&quot; yields 25
<br>
&quot;ddd&quot; yields 025
<br>
&quot;dddd&quot; yields 0025.</td>
</tr>

<tr>
<td valign="top">h</td>
<td valign="top">The hour in the day, using a 12-hour clock.</td>
<td valign="top">1 through 12</td>
</tr>

<tr>
<td valign="top">H</td>
<td valign="top">The hour in the day, using a 24-hour clock.</td>
<td valign="top">0 through 23</td>
</tr>

<tr>
<td valign="top">m</td>
<td valign="top">The minute in the hour.</td>
<td valign="top">1 through 59</td>
</tr>

<tr>
<td valign="top">s</td>
<td valign="top">The second in the minute.</td>
<td valign="top">1 through 59</td>
</tr>

<tr>
<td valign="top">S</td>
<td valign="top">The millisecond.</td>
<td valign="top">1 through 999</td>
</tr>

<tr>
<td valign="top">E</td>
<td valign="top">The day in the week.</td>
<td valign="top">&quot;EEEE&quot; yields the full name of the day, e.g., Tuesday.
<br>
&quot;EEE&quot; (or fewer) yields the abbreviation, e.g., Tues.</td>
</tr>

<tr>
<td valign="top">D</td>
<td valign="top">The day in the year, as a Julian date (number).</td>
<td valign="top">July 7 would be 189</td>
</tr>

<tr>
<td valign="top">F</td>
<td valign="top">The ordinal of the day of the week in the month. For example, the second Tuesday in July.</td>
<td valign="top">For January 7, 1997, entering:
<br>
&quot;F&quot; yields 1
<br>
&quot;FF&quot; yields 01
<br>
&quot;FFF&quot; yields 001
<br>
&quot;FFFF&quot; yields 0001.</td>
</tr>

<tr>
<td valign="top">w</td>
<td valign="top">The week in the year, as a number.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">W</td>
<td valign="top">The week in the month.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">a</td>
<td valign="top">Marker for am/pm.</td>
<td valign="top">For 1800 hours, displays 6:00 pm.</td>
</tr>

<tr>
<td valign="top">k</td>
<td valign="top">The hour in the day, using a 24-hour clock.</td>
<td valign="top">1 through 24</td>
</tr>

<tr>
<td valign="top">K</td>
<td valign="top">The hour in the day, using a 12 hour clock.</td>
<td valign="top">0 through 11</td>
</tr>

<tr>
<td valign="top">z</td>
<td valign="top">Time zone</td>
<td valign="top">Entering one through three &quot;z&quot;s yields the abbreviation (PST)
<br>
Entering four &quot;z&quot;s yields the completely spelled out time zone (Pacific Standard Time).</td>
</tr>

<tr>
<td valign="top">&#39;
<br>
(single quote)</td>
<td valign="top">Enclose text constants. This method of including constants is preferred of the other symbol styles, as being more reliable.</td>
<td valign="top">&#39;Area Code&#39; yields the words &quot;Area Code&quot; in the data.</td>
</tr>

<tr>
<td valign="top">&#39;&#39;
<br>
(two single quotes)</td>
<td valign="top">Indicates a literal single quote as a part of a text constant.</td>
<td valign="top">&#160;</td>
</tr>

<!-- JDS start - remove link -->
<tr>
<td valign="top">\nnnn</td>
<td valign="top">Single character literal.</td>
<td valign="top">Enter the octal, hexidecimal, or Unicode number for the desired character. This symbol escapes even out of single quotes.</td>
</tr>
</table>

<!-- JDS end -->

<a name="dataexpress_additional_date-time_format_symbols"></a>
<h4>DataExpress additional date/time format symbols</h4>

<table border="1">
<tr>
<th>Symbol</th>
<th>Meaning</th>
<th>Presentation</th>
</tr>

<tr>
<td valign="top">{}</td>
<td valign="top">Use to bracket optional portions of the pattern.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">^
<br>
(carat)</td>
<td valign="top">Sets the initial cursor position when editing.</td>
<td valign="top">&#160;</td>
</tr>
</table>


<!--****************************TEXT data MASKS************************-->
<br><br>
<a name="textdatapatterns"></a>
<h3>Text data patterns</h3>
<!--BNDX="text masks;masks:text"-->
<p>Patterns for formatting and editing text datas are specific to DataExpress classes. They consist of up to four parts, separated by semicolons. Only the first part is required. To include an optional third part, but not the second or fourth, enter the semicolons for each part. The parts include </p>

<ol>
<li>The actual edit or display mask.</li>

<li>A zero or one (0 or 1), indicating whether literals should be stripped before posting information to a database. Zero indicates the literals should be stripped. If this part is omitted, literals are not stripped.</li>

<li>The character to use as a &quot;blank&quot; indicator. This character indicates the spaces to be filled in the data. If this part is omitted, the underscore character is used.</li>

<li>The character to be used to replace blank positions on output. If this part of the mask is omitted, blank positions are stripped.</li>
</ol>

<p>To format the display or editing of a text data, use the symbols in the following table for the first part of the pattern.</p>

<p>For example, the edit mask "(999)000-0000;0;_;" would indicate:</p>
<ol>
<li>The template the user would see on field entry would be "(___)___-____
<li>Unfilled characters will be shown by "_" (underscore)
<li>Literals will be removed on output, so (408)555-1234 would become 4085551234
<li>Blank characters will be stripped, so (___)555-1234 would become 5551234
</ol>

<a name="dataexpress_text_data_format_symbols"></a>
<h4>DataExpress text data format symbols</h4>

<table border="1">
<tr>
<th>Symbol</th>
<th>Meaning</th>
<th>Presentation</th>
</tr>

<tr>
<td valign="top">0</td>
<td valign="top">A digit. 0 -9, entry required. Plus (+) and minus (-) signs not allowed.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">9</td>
<td valign="top">A digit. 0 -9, entry optional. A space, plus (+) and minus (-) signs are not allowed.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">#</td>
<td valign="top">A digit or space, entry optional. Blank positions are converted to spaces. Plus (+) and minus (-) signs allowed.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">L</td>
<td valign="top">A letter. A through Z, entry required.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">l</td>
<td valign="top">A letter. A through Z, entry optional.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">?</td>
<td valign="top">A letter. A through Z, entry optional.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">A</td>
<td valign="top">A letter or digit, entry required.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">a</td>
<td valign="top">A letter or digit, entry optional.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">C</td>
<td valign="top">A character or space, entry required.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">c</td>
<td valign="top">A character or space, entry optional.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">&amp;</td>
<td valign="top">A character or space, entry required.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">&lt;</td>
<td valign="top">Converts the characters that follow to lower case.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">&gt;</td>
<td valign="top">Converts the characters that follow to upper case.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">!</td>
<td valign="top">For an edit mask, makes the data fill from right to left, rather than from left to right, when characters to the left are optional. Can be placed anywhere in the edit mask.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">\</td>
<td valign="top">Causes the character immediately following the back slash to be displayed as a literal. Use to display any of the syntax symbol characters.</td>
<td valign="top">&#160;</td>
</tr>

<!-- JDS start - remove link -->
<tr>
<td valign="top">\nnnn</td>
<td valign="top">Single character literal.</td>
<td valign="top">Enter the octal, hexidecimal, or Unicode number for the desired character. This symbol escapes even out of single quotes.</td>
</tr>

<!-- JDS end -->
<tr>
<td valign="top">&#39;
<br>
(single quote)</td>
<td valign="top">Enclose character constants. For example, &quot;990&#39; units sold&#39; &quot; should display as &quot;27 units sold&quot;.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">*</td>
<td valign="top">Enclose a password encrypted string. For example, &quot;*AAAAaaaa* describes a password of at least four, and at most eight alphanumeric characters. They echo as &quot;*&quot; as they are entered.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">{}</td>
<td valign="top">Enclose optional portions of a pattern.</td>
<td valign="top">&#160;</td>
</tr>

<tr>
<td valign="top">^
<br>
(carat)</td>
<td valign="top">Sets the initial cursor position when editing.</td>
<td valign="top">&#160;</td>
</tr>
</table>
 *
 *
 */
public class VariantFormatStr extends VariantFormatter {
  String pattern;
  ParsePosition parsePos;
  FieldPosition fieldPos;
  DecimalFormat decimalFormat;
  SimpleDateFormat timeFormat;
  TextFormat textFormat;
  BooleanFormat booleanFormat;
  Locale locale;
  int variantType;
  int formatterType;
  int scale;
  int precision;
  boolean isCurrency;

  /**
   * This is the constructor to build a string-based implementation
   * of the Formatter interface.
   *
   * @param pattern Is the string of special characters used to format
   * values when using the format() method of this interface.  If this
   * value is null (or empty), the best "default" pattern will be selected based
   * on the locale.
   *
   * @param variantType Is the Variant data type which will be used by this
   * formatter (see the format() and parse() methods in this class).
   *
   * @param locale Is the locale to control this pattern.  This will determine
   * things like the decimal point sign, the currency sign, etc.  If this value
   * is null, the current default locale will be used.
   *
   * @param scale Is currently used only for BigDecimal data types.  Any value
   * other than -1 will select the number of decimal digits used in formatting
   * and parsing BigDecimal values.  Note that the pattern must still express
   * the number of digits to be displayed.
   *
   * @param precision Is currently unused.  Must presently be -1.
   *
   * @param isCurrency Is used to indicate that a numeric field is currency.  This
   * parameter is used only if the pattern is left blank (which asks this constructor
   * to manufacture a pattern from the given Locale).  A true value will select the
   * Locale's currency format.  A false will select a non-currency numeric format.
   */
public VariantFormatStr(String pattern,
                        int variantType,
                        Locale locale,
                        int scale,
                        int precision,
                        boolean isCurrency) {

//Diagnostic.addTraceCategory(Trace.FormatStr);

    this.scale = scale;
    this.precision = precision;
    this.isCurrency = isCurrency;


    //
    // We tolerate a bad variant type and revert to "String" type.  This
    // allows controls which do not know what type they want to at least
    // get a reasonable default.  Data-aware controls know their type.
    //
    if (variantType <= Variant.NULL_TYPES /* || variantType > Variant.MaxTypes*/)
      variantType = Variant.STRING;    //! TODO <rac> Get Steve to make MaxTypes public

    this.variantType = variantType;
    this.formatterType = formatTypeFromVariantType(variantType);

    if (locale == null)
      this.locale = Locale.getDefault();
    else this.locale = locale;

    //
    // The following objects are required for the JDK Format subclasses
    // (we are essentially a layer on top of them)
    //
    parsePos = new ParsePosition(0);
//    fieldPos = new FieldPosition(0);
    decimalFormat = null;
    timeFormat = null;
    textFormat = null;
    booleanFormat = null;
    //
    // A null pattern means we load the best default we can from those
    // in LocaleElements based on locale.  This is actually a popular path
    // through the code, since it allows columns to track their locales
    // as apps move to different countries.
    //
    this.pattern = (pattern == null || pattern.length() == 0)
                   ? getDefaultPattern(this.variantType)
                   : (formatterType == VariantFormatter.TEXT)
                      ? pattern
                      : buildTrueFormatMask(pattern);
    DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr using pattern \"" + this.pattern + "\"");

    //
    // Now that we know we have a pattern, create the JDK Format object
    // which can deal with this type of pattern.
    //
    DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr constructor using formatterType = " + formatterType + " (variant " + variantType + ")");
    switch (formatterType)
    {
      case ItemFormatter.DECIMAL:
//!  Diagnostic.trace(Trace.FormatStr, "Warning -- no support in JDK for BigDecimal formatting yet!");
  // falls into
      case ItemFormatter.NUMERIC:
      //! TODO: New JDK method allows locale as input -- be sure this works
//!        decimalFormat = (DecimalFormat) NumberFormat.getDefault(this.locale);
        decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(this.locale);
//!  if (decimalFormat == null)
//!    Diagnostic.trace(Trace.FormatStr, "Failed to get decimalFormat");
//!  else {
//!    Class cls = decimalFormat.getClass();
//!    Diagnostic.trace(Trace.FormatStr, "Succeeded in creating " + cls.getName() + " class");
//!  }
//        try {
// ktien: We don't currently support localized patterns.
//          decimalFormat.applyLocalizedPattern(this.pattern);
          decimalFormat.applyPattern(this.pattern);
//        }
//        catch (ParseException pe) {
          //!TODO <rac> How do we handle a bad pattern coming in?
//        }
        break;

      case ItemFormatter.DATETIME:
  //! TODO <rac> The following line is the only way I can see to get a locale down to the SimpleDateFormat
  //! BUT that version cannot parse short month names.  So the line following is the only way I know to get
  //! a working formatter, but it uses the default locale
  //timeFormat = (SimpleDateFormat) DateFormat.getDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT, this.locale);
  //! <rac> 11/25/95 was this way --> timeFormat = new SimpleDateFormat(this.pattern);
//!        timeFormat = (SimpleDateFormat) DateFormat.getDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT, this.locale);
        timeFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, this.locale);
        // Enforce strict date parsing by default (since JB 3.0).  Comment the following line out to enable lenient date parsing
        timeFormat.getCalendar().setLenient(false);

        // ktien:
        // Above factory method sets the timezone to the default for the given locale.
        // A given locale can have multiple timezones (e.g. in the US),
        // so the returned timezone will not necessary match the user's time zone.
        // There is no DateFormat constructor/factory method that takes a timezone -- need to set
        // after construction.  Following will set the timezone to the default.  We probably
        // should, in the next version, add a constructor for VariantFormatStr that takes
        // a timezone.
        timeFormat.setTimeZone(TimeZone.getDefault());

        DiagnosticJLimo.trace(Trace.FormatStr, "At construction, timeFormat.setPattern(" + pattern + ")");

//ktien
//        timeFormat.applyLocalizedPattern(this.pattern);
        timeFormat.applyPattern(this.pattern);
        break;

      case ItemFormatter.BOOLEAN:
        booleanFormat = new BooleanFormat(this.pattern);
        break;

      case ItemFormatter.TEXT:
        default:
          DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr defaulting to text");
          textFormat = new TextFormat(this.pattern);
          break;
    }
  }  // end of VariantFormatStr constructor

  /**
   * This is the constructor to build a string-based implementation
   * of the Formatter interface.
   *
   * @param pattern Is the string of special characters used to format
   * values when using the format() method of this interface.  If this
   * value is null (or empty), the best "default" pattern will be selected based
   * on the locale.
   *
   * @param variantType Is the Variant data type which will be used by this
   * formatter (see the format() and parse() methods in this class).
   *
   */
  public VariantFormatStr(String pattern, int variantType) {
    this(pattern, variantType, null);
  }

  /**
   * This is the constructor to build a string-based implementation
   * of the Formatter interface.
   *
   * @param pattern Is the string of special characters used to format
   * values when using the format() method of this interface.  If this
   * value is null (or empty), the best "default" pattern will be selected based
   * on the locale.
   *
   * @param variantType Is the Variant data type which will be used by this
   * formatter (see the format() and parse() methods in this class).
   *
   * @param locale Is the locale to control this pattern.  This will determine
   * things like the decimal point sign, the currency sign, etc.  If this value
   * is null, the current default locale will be used.
   */
  public VariantFormatStr(String pattern, int variantType, Locale locale) {
    this(pattern, variantType, locale, -1, -1, false);
}

// --------------- Extension Methods for VariantFormatter ----------------------

  /**
   *  Returns the Variant type this VariantFormatter will use for its getValue().
   *
   * @return The Variant type used to control the Variant type in getValue();
   */
  public int getVariantType() {
    return variantType;
  }

  /**
   *  This method will format (i.e. produce a String) from the given
   *  value using the current pattern.
   *
   *  @param value Contains the value to be formatted. The type of data
   *  stored in the Variant is self-identifying via value.getType().  Not
   *  all types are supported, but every reasonable attempt will be made
   *  to cast the type provided into the type required for the format logic.
   *  Note that the value Variant will not be altered by this function.
   *
   *  @return A String containing the formatted data.  This String
   *  could be empty if the input Variant was null or unassigned.
   *  A null will be returned in the event formatting fails.
   */
  public String format(Variant value) {
    StringBuffer result = null;

    //
    // It is acceptable to pass in nulls or empty variants -- but we guarantee an empty string out
    // The only exception is boolean formatting which may want a special value for an null

    if (booleanFormat == null && (value == null || value.isNull() || !(value instanceof Variant)))
      return new String();

    fieldPos = new FieldPosition(0);
//    fieldPos.alignField = 0;  // The JDK Format objects use this information -- init it each time
//    fieldPos.setBeginIndex(0);
//    fieldPos.setEndIndex(0);

    //
    // Only one of the Format objects will be defined based on the type we defined in the constructor.
    // Simply decode the right one.
    //
    if (timeFormat != null) {
      java.util.Date dateObj = variantToLongDate(value);    // SimpleDateFormat always wants Dates
      if (dateObj != null) {
        try {
          result = new StringBuffer();
          DiagnosticJLimo.trace(Trace.FormatStr, "timeFormat.format(" + dateObj.toString() + ")");
          result = timeFormat.format(dateObj, result, fieldPos);
        }
        catch (IllegalArgumentException e) {
          result = null;
        }
      }
    }

    else if (decimalFormat != null) {
      Double doubleObj = variantToDouble(value, scale);  // DecimalFormat always wants doubles
      if (doubleObj != null) {
        result = new StringBuffer();
        result = decimalFormat.format(doubleObj.doubleValue(), result, fieldPos);
      }
    }

    else if (textFormat != null) {
      String stringObj = variantToString(value);
      if (stringObj != null) {
        result = new StringBuffer();
        result = textFormat.format(stringObj, result, fieldPos);
      }
    }
    else if (booleanFormat != null) {
      Boolean booleanObj = variantToBoolean(value);
      result = new StringBuffer();
      result = booleanFormat.format(booleanObj, result, fieldPos);
    }

    return result == null ? null : result.toString();
  }

  private static boolean BUG6296 = true;
  private static boolean BUG5595 = true;

  static final InvalidFormatException makeInvalidFormatException(String pattern, int pos) {
    return new InvalidFormatException(Res.bundle.format(ResIndex.ParseError, pattern), pos);
  }

  /**
   * This method will parse a string using the current pattern and produce
   * the appropriate value which is returned in the form of a Variant.
   *
   * @param stringValue Contains the string to be parsed.  A null or empty stringValue
   * will result in a Variant object being returned which is set to an AssignedNull
   *
   * @param value Is a pre-allocated Variant used to contain the result.
   * Its type will be determined by the 'variantType' parameter.
   *
   * @param variantType Contains one of the Variant.type values to control
   * the output type of the returned Variant
   *
   */
  public void parse(String stringValue, Variant value, int variantType) throws InvalidFormatException {

    if (value == null || !(value instanceof Variant)) {
      DiagnosticJLimo.println(value + " is not a variant in VariantFormatStr.parse()");
      if (value != null)
        DiagnosticJLimo.println("  it is of class: " + value.getClass().getName());
      throw new InvalidFormatException(Res.bundle.getString(ResIndex.MustBeVariant));
    }
    //
    // If no string was given (or it is empty), turn it right back
    // around as an AssignedNull.  Don't rely on the parse routine to
    // handle this.
    //
    if (stringValue == null || stringValue.length() == 0) {
      value.setNull(Variant.ASSIGNED_NULL);
      return;
    }

    DiagnosticJLimo.trace(Trace.FormatStr, "FormatStr.parse(\"" + stringValue + "\", " + Variant.typeName(variantType));

    //
    // Make sure the variantType passed in is valid -- use the default if questionable
    //
    if (variantType <= Variant.NULL_TYPES)
      variantType = this.variantType;

    parsePos.setIndex(0);    // JDK parse routines use this to know where to start and also
                             // return the parse failure point here

    if (timeFormat != null) {
      DiagnosticJLimo.trace(Trace.FormatStr, "Parsing date " + stringValue + " using pattern " + timeFormat.toLocalizedPattern());

      java.util.Date dateResult = timeFormat.parse(stringValue, parsePos);
      if (dateResult == null) {
        DiagnosticJLimo.println(" timeFormat returned null");
        throw makeInvalidFormatException(timeFormat.toLocalizedPattern(), parsePos.getIndex());
      }
      DiagnosticJLimo.trace(Trace.FormatStr, "which yielded " + dateResult + " of class " + dateResult.getClass().getName());
      DiagnosticJLimo.trace(Trace.FormatStr, "parse() calling longDateToVariant");

      value = longDateToVariant(dateResult, value, variantType);
    }

    else if (decimalFormat != null) {
      Number nResult = decimalFormat.parse(stringValue, parsePos);

      if (nResult == null) {
        try {
      //!RC Workaround for bug 6296 -- JDK cannot parse leading '+'
      //!RC remove it and try again
          if (BUG6296 && stringValue.charAt(0) == '+') {
//!System.err.println("invoking bug 6296 workaround...");
            FastStringBuffer fs = new FastStringBuffer(stringValue);
            fs.removeCharAt(0);
            try {
              nResult = decimalFormat.parse(fs.toString(), parsePos);
            }
            catch (Exception ex) {
//!System.err.println(" still cannot parse");
            }
          }
      //!RC workAround for Bug 5595.  JavaSoft JDK 1.1.2 will return a null
      // when parsing a zero with ###.  We get around this by trying a simpler parse
      // technique (but only when we fail for this specific case)
          else if (BUG5595) {
            Double d = Double.valueOf(stringValue);   // will throw if has a problem parsing
            if (d.doubleValue() == 0.0) {             // limit this to zero value case
              nResult = d;
              parsePos.setIndex(1);
            }
          }
        }
//        catch (InvalidFormatException ex) {
//!System.err.println(" rethrowing invalidFormatException");
//          throw ex;
//        }
        catch (Exception ex) {
//ex.printStackTrace();
        }
      }
      //!RC end bug workarounds

        if (nResult == null ||
          (nResult instanceof Double && ((Double)nResult).isNaN()) ||
          parsePos.getIndex() == 0)
        value = null;
      if (nResult != null) DiagnosticJLimo.trace(Trace.FormatStr, "decimal.parse gives: " + nResult
                                              + " and doubleVal as " + nResult.doubleValue());  //NORES
      if (nResult != null) doubleToVariant(nResult.doubleValue(), value, variantType, scale);
    }

    else if (textFormat != null) {
      StringBuffer sResult = textFormat.parse(stringValue, parsePos);
      if (sResult == null)
        value = null;
      else value = stringToVariant(sResult.toString(), value, variantType, scale);
    }

    else if (booleanFormat != null) {
      Boolean booleanObj = booleanFormat.parse(stringValue, parsePos);
      // To distinguish between a null meaning a legal null string vs something
      // we cannot parse, we check whether the parsePosition was advanced.
      // An empty string is legal for the nullString value.
      if (booleanObj == null && stringValue != null && stringValue.length() > 0 && parsePos.getIndex() == 0)
        throw makeInvalidFormatException(booleanFormat.toPattern(), parsePos.getIndex());
      value = booleanToVariant(booleanObj, value, variantType);
    }

    else value = null;    // should never get here, but this is for robustness

    if (value == null)
      throw makeInvalidFormatException(booleanFormat.toPattern(), parsePos.getIndex());
  }

  /**
   * This method will parse a string using the current pattern and produce
   * the appropriate value which is returned in the form of a Variant.
   *
   * @param stringValue Contains the string to be parsed.  A null or empty stringValue
   * will result in a Variant object being returned which is set to an AssignedNull
   *
   * @param value Is a pre-allocated Variant used to contain the result.
   * Its type will be determined by the 'variantType' parameter originally
   * passed into the constructor of this ItemFormatter.
   *
   */
  public void parse(String stringValue, Variant value) throws InvalidFormatException {
    parse(stringValue, value, this.variantType);
  }

  /**
   * This method will return the pattern currently being used by this
   * VariantFormatStr object for formatting and parsing.
   */
  public String getPattern() {

//!    if (timeFormat != null)
//!      Diagnostic.trace(Trace.FormatStr, "timeFormat.getPattern(" + timeFormat.getPattern(true) + ")");

/*
 *    String pattern = (decimalFormat != null)
 *           ? decimalFormat.getPattern(true)
 *           : (timeFormat != null)
 *       ? timeFormat.getPattern(true)
 *       : textFormat.getPattern();
 */
    DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr: getPattern returning \"" + pattern);
    return pattern;
  }

  /**
   * This method will set the current pattern used by this VariantFormatStr
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
    String oldFormat = getPattern();
    if (pattern == null || pattern.length() == 0)
      pattern = getDefaultPattern(variantType);

    DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr: setting pattern to \"" + pattern + "\"");

//    try {
      if (decimalFormat != null)
//ktien
//        decimalFormat.applyLocalizedPattern(pattern);
        decimalFormat.applyPattern(pattern);
      else if (timeFormat != null)
//        timeFormat.applyLocalizedPattern(pattern);
        timeFormat.applyPattern(pattern);
      else if (textFormat != null)
        textFormat.applyPattern(pattern);
      else if (booleanFormat != null)
        booleanFormat.applyPattern(pattern);
      this.pattern = pattern;
//    }
//    catch (ParseException pe) {
//      //!TODO <rac> How do we handle a bad pattern coming in?
//    }
    return oldFormat;
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
    Object resultObj = null;

    if (textFormat != null) {
      switch (objType)
      {
        case ItemFormatter.FILLCHARACTER:
          resultObj = new Character(textFormat.getFillCharacter());
          textFormat.setFillCharacter(((Character)obj).charValue());
          break;
        case ItemFormatter.REPLACECHARACTER:
          resultObj = new Character(textFormat.getReplaceCharacter());
          textFormat.setReplaceCharacter(((Character)obj).charValue());
          break;
      }
    }
    return resultObj;
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

    if (textFormat != null) {
      switch (objType)
      {
        case ItemFormatter.FILLCHARACTER:
          return new Character(textFormat.getFillCharacter());
        case ItemFormatter.REPLACECHARACTER:
          return new Character(textFormat.getReplaceCharacter());
      }
    }
    return null;
  }

  /**
   * Returns the Locale being used by this ItemFormatter.
   *
   * @return The Locale used by the VariantFormatter (will never be null)
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Returns the JDK Format subclass associated with this ItemFormatter.
   *
   * @return The JDK Format subclass for this ItemFormatter.  Could return null.
   */
  public Format getFormatObj(){
    return (decimalFormat != null)
      ? (Format) decimalFormat
      : (timeFormat != null)
        ? (Format) timeFormat
        : (textFormat != null)
           ? (Format) textFormat
           : (booleanFormat != null)
              ? (Format) booleanFormat
              : null;
  }

  /**
   * Returns the current scale factor
   */
  public int getScale() { return scale; };

  /**
   *  Strips out any Borland-specific extensions from an edit/format mask.
   *
   *  @param editMask Contains the editMask to be used for formatting/parsing/editing
   *
   *  @return A new String stripped of any Borland extensions to the JDK's Format strings
   *
   */
  public static final String buildTrueFormatMask(String editMask) {
    int iLen = editMask.length();
    char[] src = new char[iLen];
    char[] dst = new char[iLen];
    char c;

    // Currently, merely stripping the '{','}' and '^' are sufficient to
    // make a Borland edit mask into a legal JDK edit mask.
    //
    editMask.getChars(0, iLen, src, 0);

    int iDst = 0;
    for (int i = 0; i < iLen; ++i) {
      c = src[i];
      switch (c)
      {
        case '^':
        case '{':
        case '}':
          DiagnosticJLimo.trace(Trace.FormatStr, "BuildTrueFormat: stripping \'" + c + "\'");
          continue;
        default:
          dst[iDst++] = src[i];
      }
    }

    return new String(dst, 0, iDst);
  }


// ================== internal methods ======================================

  //
  // This method will return one of the basic 4 ItemFormatter.types based on given variant type
  //
  static final int formatTypeFromVariantType(int variantType) {
    //! TODO <rac> How do we choose currency?
    switch (variantType)
    {
      case Variant.BYTE:
      case Variant.SHORT:
      case Variant.INT:
      case Variant.LONG:
      case Variant.FLOAT:
      case Variant.DOUBLE:
        return ItemFormatter.NUMERIC;

      case Variant.BIGDECIMAL:
        return ItemFormatter.DECIMAL;

      case Variant.BOOLEAN:
        return ItemFormatter.BOOLEAN;

      case Variant.DATE:
      case Variant.TIME:
      case Variant.TIMESTAMP:
        return ItemFormatter.DATETIME;
      default: break; // to make compiler happy
    }
    return ItemFormatter.TEXT;
  }

  //
  // This method will determine the best "default" pattern to use based
  // on the current format type using the current Locale.  It is a good
  // idea to pass null patterns to the formatter so that you get the
  // default locale formatting when apps move to different locales.
  //
  protected String getDefaultPattern(int variantType) {
    String pattern = null;
    String[] resourceArray;
    int offset = 0;
    DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr.getDefaultPattern(" + Variant.typeName(variantType) + "' " + locale.toString() + ")");
    try {
      ResourceBundle resource = SystemResourceBundle.getLocaleElementsBundle(locale);

      switch (formatterType)
      {
        case ItemFormatter.DECIMAL:
          //!System.err.println("DECIMAL default pattern");
          // falls into NUMERIC
        case ItemFormatter.NUMERIC:

          switch (variantType) {
            case Variant.BYTE:
              pattern = new String("###");
              break;
            case Variant.SHORT:
              pattern = new String("#####");
              break;
            case Variant.INT:
              pattern = new String("###########");
              break;
            case Variant.LONG:
              pattern = new String("####################");
              break;
            default:
              //case Variant.BIGDECIMAL:
              //case Variant.DOUBLE:
              resourceArray = resource.getStringArray("NumberPatterns");
              if (isCurrency) {             // if user asked for currency, use currency pattern
                //!System.err.println(" using currency");
                ++offset;
              }
              pattern = resourceArray[offset];
              //!System.err.println(" pattern is: " + pattern);
              break;
          }
          break;

        case ItemFormatter.DATETIME:
          resourceArray = resource.getStringArray("DateTimePatterns");

          switch (variantType) {
            case Variant.TIME:
              pattern = resourceArray[2];    // medium time
              break;
            case Variant.TIMESTAMP:
              //!System.err.println("resourceArray size is: " + resourceArray.length);
              //!System.err.println("Default timestamp, msgFormat pattern is: " + resourceArray[8]);
              //!System.err.println("  params are: " + resourceArray[7] + " and " + resourceArray[2]);

              // JDK now provides a resourced string to dictate how date + time are combined
              pattern = MessageFormat.format(resourceArray[8], new Object[] {resourceArray[2], resourceArray[7]});
//              pattern = new String(resourceArray[7] + " " + resourceArray[2]);
              //!System.err.println("  yields timeStamp format of: " + pattern);
              break;        // medium date + medium time
            default:
              //case Variant.Date:
              pattern = resourceArray[7];  // short date
              //! TODO <rac> We were using medium date (element[7]) but 11/6/96 JDK gags on it
              break;
          }
          //! TODO <rac> If we granularize the Formatter types, we can make better choices
          break;

        case ItemFormatter.BOOLEAN:
          pattern = Res.bundle.getString(ResIndex.TrueFalsePattern);
          break;

        case ItemFormatter.TEXT:
        default:
          pattern = new String("");
          // String with no pattern is an anomaly triggered in the general case by our painters asking
          // for a formatter for all types.  The empty string has special signficance in string formatting/parsing
          break;
      }  // end switch
    }    // end try
    catch (MissingResourceException e) {  // should NEVER get here, but if we do, use US symbols
      pattern = new String("");
    }

    DiagnosticJLimo.trace(Trace.FormatStr, "VariantFormatStr: (" + formatterType + "/" + Variant.typeName(variantType) +
                       ") chose default pattern of \"" + pattern + "\"");
    return pattern;
  }

  //
  // This method will produce a Date object out of the given variant if at
  // all possible.  It will return a null for uncastable variants.
  //
  static final java.util.Date variantToLongDate(Variant value) {

    java.util.Date result = null;

    switch (value.getType())
    {
      case Variant.LONG:
        result = new java.util.Date(value.getLong());
        break;

      case Variant.DATE:
        java.sql.Date dateObj = value.getDate();
        result = new java.util.Date(dateObj.getTime());
        break;

      case Variant.TIME:
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        Time timeObj = value.getTime();
        result = new java.util.Date(timeObj.getTime());
        break;

      case Variant.TIMESTAMP:
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        Timestamp timestampObj = value.getTimestamp();
        //result = new java.util.Date(timestampObj.getTime());
        result = new java.util.Date(timestampObj.getTime() + timestampObj.getNanos() / 1000000);
          // Note: as of 2/1/97, JDK stores fractional seconds in 'nanos' and forces the client
          // to add them in as needed.

        break;

      default:
        result = new java.util.Date(value.toString());  // fallback -- not expected to use this path
    }
    DiagnosticJLimo.trace(Trace.FormatStr, "variantToLongDate --> " + result);
    return result;
  }

  //
  // This method is the complement of the one above.  It will take
  // a long value and place it into the given variant, making all
  // reasonable attempts to cast it into the appropriate type currently
  // in the variant. This is used strictly for date/time stuff.
  //
  static final Variant longDateToVariant(java.util.Date dateVal, Variant value, int variantType) {
    Variant newValue;

    DiagnosticJLimo.trace(Trace.FormatStr, "longDateToVariant(" + dateVal + ") -->" + dateVal.getClass().getName());

    if (value == null)    // robustness measure
      value = new Variant();

    if (dateVal == null) {  // robustness measure
      DiagnosticJLimo.trace(Trace.FormatStr, "longDateToVariant(null)");
      value.setAssignedNull();
      return value;
    }

    switch (variantType)
    {
      case Variant.DATE:
        DiagnosticJLimo.trace(Trace.FormatStr, "Date assign " + dateVal);
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        //!java.util.Date dateObj =  new java.util.Date(dateVal.getTime());
        //!value.setDate(dateObj);
        if (dateVal == null) {
          DiagnosticJLimo.trace(Trace.FormatStr, "LongDateToVariant -- yikes, it's null!");
          value.setNull(Variant.ASSIGNED_NULL);
        }
        else {
          //!  Class cls = dateVal.getClass();
          //!  Diagnostic.trace(Trace.FormatStr, "longDateToVariant -- set class to " + cls.getName());
          value.setDate(dateVal.getTime());
        }
        break;

      case Variant.TIME:
        DiagnosticJLimo.trace(Trace.FormatStr, "Time assign " + dateVal);
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        java.sql.Time timeObj = new java.sql.Time(dateVal.getTime());
        DiagnosticJLimo.trace(Trace.FormatStr, "  assigning " + timeObj);
        value.setTime(timeObj);
        break;

      case Variant.TIMESTAMP:
         DiagnosticJLimo.trace(Trace.FormatStr, "Timestamp assign " + dateVal);
       //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        Timestamp timestampObj = new Timestamp(dateVal.getTime());
        DiagnosticJLimo.trace(Trace.FormatStr, "  assigning " + timestampObj);
        value.setTimestamp(timestampObj);
        break;

      case Variant.LONG:
        DiagnosticJLimo.trace(Trace.FormatStr, "Long assign " + dateVal);
        value.setLong(dateVal.getTime());
        break;

      default:
        return null;  //! for now, don't decode any more.  We can add that if people insist, but
        //! it doesn't make sense to ask for Date/Time in any other form from a Parse

    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, "longDateToVariant returning");
    return value;
  }

  static final Boolean variantToBoolean(Variant value) {
    //!System.err.println("variantToBoolean: variant is " + value);

    Boolean result = null;

    switch (value.getType())
    {
      case Variant.BOOLEAN:
        result = new Boolean(value.getBoolean());
        break;


      case Variant.BYTE:
      case Variant.SHORT:
      case Variant.INT:
        result = new Boolean((value.getAsInt() != 0));
        break;

      case Variant.LONG:
        result = new Boolean((value.getLong() != 0));
        break;
    }
    //!System.err.println(" returning " + result);

    return result;
  }

  //
  // This method will make all reasonable attempts to return a Double
  // object from the given variant. It will return null if it cannot.
  //
  static final Double variantToDouble(Variant value, int scale) {

    Double result = null;

    switch (value.getType())
    {
      case Variant.FLOAT:
        result = new Double((double)value.getFloat());
        break;
      case Variant.DOUBLE:
        result = new Double(value.getDouble());
        break;

      case Variant.BIGDECIMAL:
        //! TODO <rac> this is not right but now support for BCD yet
        // Further note: this weirdness of going through a string first seems
        // to be necessary.  Converting directly to a Double loses considerable precision.
        //String s = value.getBigDecimal().toString();
        //result = new Double(s);
        BigDecimal bn = value.getBigDecimal();
        //!System.err.println("variantToDouble(), scale is: " + scale);
        if (scale >= 0)
          bn = bn.setScale(scale, BigDecimal.ROUND_HALF_UP);
        result = new Double(bn.doubleValue());
        //!System.err.println(" results in double: " + result);

//        result = new Double(value.getBigDecimal().doubleValue());
          // this works better in the FCS JDK
        DiagnosticJLimo.trace(Trace.FormatStr, "variantToDouble -> " + result.toString());
        break;

      case Variant.BYTE:
      case Variant.SHORT:
      case Variant.INT:
        result = new Double((double) value.getAsInt());
        break;

      case Variant.LONG:
        result = new Double((double) value.getLong());
        break;

      default:
        result = new Double(value.toString());
    }
    return result;
  }

  static final Variant booleanToVariant(Boolean bool, Variant value, int variantType) {
    if (value == null)
      value = new Variant();

    if (bool == null)
      value.setAssignedNull();

    else switch (variantType)
    {
      case Variant.BOOLEAN:
        value.setBoolean(bool.booleanValue());
        break;
    }
    return value;
  }

  // This method is a complement of the above.  It will make every
  // effort to store the given doubleValue into the given variant
  // in the current type of the variant.
  //
  static final Variant doubleToVariant(double doubleValue, Variant value, int variantType, int scale)
      throws InvalidFormatException {
    DiagnosticJLimo.trace(Trace.FormatStr, "doubleToVariant(" + doubleValue + ", " + Variant.typeName(variantType) + ")");
//!System.err.println("doubleToVariant(" + doubleValue + ", " + Variant.typeName(variantType) + ")");

    if (value == null)
      value = new Variant();

    int i;
    switch (variantType)
    {
      case Variant.BYTE:
        i = (int) doubleValue;
        if (i > 127 || i < -128) {
          throw new InvalidFormatException(Res.bundle.getString(ResIndex.BytePrecisionBad));
        }
        value.setByte((byte) doubleValue);
        break;

      case Variant.SHORT:
        i = (int) doubleValue;
        if (i > 32767 || i < -32768) {
          throw new InvalidFormatException(Res.bundle.getString(ResIndex.ShortPrecisionBad));
        }
        value.setShort((short) doubleValue);
        break;

      case Variant.INT:
        value.setInt((int) doubleValue);
        break;

      case Variant.LONG:
        value.setLong((long) doubleValue);
        break;

      case Variant.DOUBLE:
        value.setDouble(doubleValue);
        break;
      case Variant.FLOAT:
        value.setFloat((float)doubleValue);
        break;

      case Variant.BIGDECIMAL:
        //! TODO <rac> Waiting for BCD support
        //! String s = "" + doubleValue;
        //! TODO <rac> Again, double to BigDecimal is problematic.  This business
        //! of going to a string first and then to a BigDecimal seems to be the only
        //! current way to avoid precision loss (1/17/97)
        //! BigDecimal bn = new BigDecimal(s);
        //! System.out.println("** double = " + s + " and bigDecimal = " + bn);   //$$$
        BigDecimal bn = new BigDecimal(doubleValue);
        //! System.err.println("--- scale is " + scale + " ---");
        if (scale >= 0)
          bn  = bn.setScale(scale, BigDecimal.ROUND_HALF_UP);
        //! System.err.println("  value is " + bn);
        value.setBigDecimal(bn);
        DiagnosticJLimo.trace(Trace.FormatStr, "DoubleToVariant -> " + bn.toString());
        break;

      default:
        return null;

    }
    return value;
  }


  //
  // This routine will return a String from the given variant
  // Be warned, it is expected that what is in the Variant is already
  // a String, and it is only common courtesy to try for a cast if
  // possible.  We cannot promise International conversion rules if
  // someone gave us a double or something.
  //
  static final String variantToString(Variant value) {

    String result;
    switch (value.getType()) {
      case Variant.STRING:
        result = value.getString();
        break;

      default:
        result = value.toString();
    }

    return result;

  }

  //
  // This method will attempt to put the given StringBuffer into the
  // given variant.  It is really intended to be used strictly by
  // ItemFormatter.TEXT type of parsing, so no other casting is attempted.
  //
  static final Variant stringToVariant(String stringVal, Variant value, int variantType, int scale) {
    switch (variantType)
    {
      case Variant.STRING:  // the most common we hope
        value.setString(stringVal.toString());
        break;

      case Variant.BYTE:
        value.setByte((byte) Integer.parseInt(stringVal));
        break;

      case Variant.SHORT:
        value.setShort((short) Integer.parseInt(stringVal));
        break;

      case Variant.INT:
        value.setInt((int) Integer.parseInt(stringVal));
        break;

      case Variant.LONG:
        value.setLong((long) Long.parseLong(stringVal));
        break;

      case Variant.FLOAT:
        Float f = new Float(stringVal);
        value.setFloat(f.floatValue());
        break;
      case Variant.DOUBLE:
        Double d = new Double(stringVal);
        value.setDouble(d.doubleValue());
        break;

      case Variant.BIGDECIMAL:
        BigDecimal bn = new BigDecimal(stringVal);
        if (scale >= 0)
          bn.setScale(scale);
        value.setBigDecimal(bn);
        DiagnosticJLimo.trace(Trace.FormatStr, "StringToVariant(" + stringVal + ") -> " + bn.toString());

        break;

      case Variant.DATE:
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        //! TODO <rac> Asking for a string back as any kind of date time means the person was really
        //! using the wrong string formatter (string instead of DateTime).  The "correct" solution
        //! might be to generate a new DateTime formatter to parse this string IN THE LOCALE we are
        //! in.  The short cut is to let the default util.Date try to decompose it into a long.  In
        //! any case, this is a questionable path for any client to be taking through this code
        java.util.Date dateObj = new java.util.Date(java.util.Date.parse(stringVal));
        value.setDate(dateObj.getTime());
        break;

      case Variant.TIME:
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        java.sql.Time timeObj = new java.sql.Time(java.util.Date.parse(stringVal));
        value.setTime(timeObj);
        break;

      case Variant.TIMESTAMP:
        //! TODO <rac> Steve's new Variant may have better technique soon for getting long directly
        Timestamp timestampObj = new Timestamp(java.util.Date.parse(stringVal));
        value.setTimestamp(timestampObj);
        break;

      default:
        return null;
    }
    return value;
  }
}
