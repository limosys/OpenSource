//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/VariantFormatter.java,v 7.0 2002/08/08 18:40:16 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.ItemFormatStr;
import com.borland.dx.text.ItemFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.FastStringBuffer;

//
import java.text.*;
import java.util.*;


/**
* The VariantFormatter class is the general purpose base class used to
* format and to parse data, time and date data, and strings.
* The most popular implemenation of this interface is ItemFormatStr which
* uses pattern strings based on the JDK Format conventions.  Because
* different data types typically require different kinds of handling,
* there are actually 4 basic parse/format types currently supported.
* They are:
*    Formatter.NUMERIC
*    Formatter.CURRENCY
*    Formatter.DATETIME
*    Formatter.TEXT
*/

public abstract class VariantFormatter extends ItemFormatter
{
// ---------------- Implementations of ItemFormatter -------------------------
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
    return format((Variant) value);
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
    Variant value = new Variant();
    parse(stringValue, value);
    return (Object) value;
  }

// ---------- Extensions unique to VariantFormatter --------------

/**
* Constructs a String representing the given
* value stored in the supplied Variant.  All reasonable attempts will
* be made to "cast" the type found in the variant into the appropriate
* type specified in the constructor of the implementors of this.
*
* @param value Contains the value to be formatted.  It will be cast to
* the appropriate type where possible (though the variant itself will not
* be altered).
*
* @return A String containing the formatted data.  An empty
* string indicates a null or empty input value.  A null return means
* the formatting failed.  Be ready for that null possibility.
*/
  public abstract String format(Variant value);


/**
* Constructs a FastStringBuffer representing the given
* value stored in the supplied Variant.  All reasonable attempts will
* be made to "cast" the type found in the variant into the appropriate
* type specified in the constructor of the implementors of this.
*
* @param value Contains the value to be formatted.  It will be cast to
* the appropriate type where possible (though the variant itself will not
* be altered)
*
* @param buffer Is a FastStringBuffer which will receive the formatted text.
* (Null is not permitted -- this is for speed)
*
* @return The given FastStringBuffer containing the formatted data.  An empty
* string indicates a null or empty input value.  A null return means
* the formatting failed.  Be ready for that null possibility.
*/
  public FastStringBuffer format(Variant value, FastStringBuffer buffer) {
    String s = format(value);
    if (s != null) {
      buffer.append(s);
      return buffer;
    }
    return null;
  }

/**
* Parses (i.e. analyzes) the given String and produces as
* output a Variant containing the appropriate value.
*
* @param stringValue Contains the string to be parsed.  A null or empty
* stringValue will return a Variant.ASSIGNED_NULL variant.
*
* @param value Contains the Variant to be used to receive the result
* data.
*
*/
  public abstract void parse(String stringValue, Variant value) throws InvalidFormatException;


/**
* An alternative form of getValue(), this version allows the caller to specify the type
* of the Variant they want returned.
*
* @param stringValue Contains the string to be parsed.  A null or empty
* stringValue will return a Variant.ASSIGNED_NULL variant.
*
* @param value Contains the Variant to be used to receive the result
* data.
*
* @param variantType Contains the desired Variant.type for the output variant.  If it is zero
* or one of the Variant.IsNull types, this method will instead choose the default variant type
* specified at construction time of the VariantFormatter.
*
*/
  public void parse(String stringValue, Variant value, int variantType) throws InvalidFormatException {
    throw new InvalidFormatException(Res.bundle.getString(ResIndex.ParseNotSupported));
      // If this is not specifically overridden, it is an error.  This is used primarly for editmask
      // formatting.
  }


/**
* An alternate form of parse, this one is for speed.  It parses directly into a character array
*
* @param variant Value to receive parsed value (may not be null).
*
* @param value Char array containing text to parse
*
* @param offset (Zero based) offset into value[] where to find string
*
* @param len Max number of characters in value[] to use in parse
*
*/
  public void parse(Variant variant, char[] value, int offset, int len) throws InvalidFormatException {
    if (len == 0) {
      variant.setUnassignedNull();
      return;
    }
    String s = new String(value, offset, len);
    parse(s, variant);
  }


/**
* Returns the Variant.type being used by this VariantFormatter.  All calls to getValue() will
* produce Variants of this type.
*
* @return One of the types defined in the Variant class (e.g. Variant.String)
*
* <P><B>Note:</B> There is no setVariantType() because there is a special version of getValue()
* which allows the caller to request a particular returned Variant type.
*/
  public abstract int getVariantType();


/**
* Returns the pattern currently being used by this Formatter
* for parsing and formatting.
*
* @return The current pattern being used.
*/
  public String getPattern() { return null; }

/**
* Sets the current pattern to be used for parsing and formatting.
*
* @param pattern Contains the new pattern to use.  If null (or empty), this
* method will choose the "best" default pattern for the current locale. This
*
* @return Returns the previous pattern.
*
* <P><B>Note:</B> The new pattern <B>must</B> be of the same basic type associated
* with this type of formatter.  For example, if you used a Date/Time pattern in the
* constructor, it would be an error to switch to a numeric pattern, since each basic
* pattern type has its own data-dependent Formatters and Parsers.
*/
  public String setPattern(String pattern) { return pattern; }

/**
* Some Formatter implementations define special Objects for their
* use.  This method allows them to be set.  You must know the internal
* details of the Format subclass being used to use this method.
*
* @param objType Identifies the particular Object to set.
* Currently, the ones which can be set are:
*
*    VariantFormatter.FillCharacter  [Character] Text pattern only -- the char to use to fill blank slots
*    VariantFormatter.ReplaceCharacter [Character] Text pattern only -- used to replace FillChar on parse
*
*
* @return The prior value of this Object, which can be useful in restoring it after a temporary switch.
*/
  public Object setSpecialObject(int charType, Object obj) { return null; }

/**
* Returns the value of the named special object.
*
* @param objType Identifies the special object to return.
*
* @return Returns the named special object.
*
* @see VariantFormatter#setSpecialObject
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
* The VariantFormatter interface is itself a layer on the JDK's Format interface.
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

/**
 * Returns the scale being used for numeric formatting
 */
  public int getScale() { return -1; }    // overload this if you want anything else

  public static final void setFromInt(Variant value, int type, int val) {
    switch (type) {
      case Variant.BYTE:   value.setByte((byte)val);  return;
      case Variant.SHORT:  value.setShort((short)val); return;
      default:             value.setInt(val);         return;
    }
  }
  public static final void setFromDouble(Variant value, int type, double val) {
    switch(type) {
      case Variant.FLOAT:     value.setFloat((float)val);   return;
      default:                value.setDouble(val);         return;
    }
  }

}

