//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ShortFormatter.java,v 7.0 2002/08/08 18:40:14 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.IntegerFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.dataset.Variant;

import java.lang.Character;
import java.io.*;

/**
 *
* A Formatter class for parsing and formatting short data types.
* ShortFormatter is used for short columns, handling values within
*  the range of 32767 to -32768.
 */
public class ShortFormatter extends IntegerFormatter implements Serializable
{
  /**
   * Constructs a ShortFormatter object.
   */
  public ShortFormatter(int type) {
    super(type);
  }

  //! for JB 3.0, catch possible NumberFormatException, and rethrow as InvalidFormatException (dcy BTS 19777)
  /**
   * Analyzes the given String and produces as output a
   * Variant containing the approriate value.
   * @param stringValue String
   * @param value Variant
   * @throws InvalidFormatException
   */
  public final void parse(String stringValue, Variant value) throws InvalidFormatException {
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    int i = 32768;
    try {
      i = Integer.parseInt(stringValue, 10);
    }
    catch (NumberFormatException e) {
      //! confirmed this message concatentation was okay with intl dept (dcy)
      if (!stringValue.equals(e.getMessage())) {
        throw new InvalidFormatException(Res.bundle.getString(ResIndex.JDKErrMsg) + ": " + stringValue + ": " + e.getMessage());  //!NORES
      }
      else {
        throw new InvalidFormatException(stringValue);
      }
    }
    if (i > 32767 || i < -32768) {
      throw new InvalidFormatException(Res.bundle.getString(ResIndex.ShortPrecisionBad));
    }

    setFromInt(value, type, i);
  }

  /**
   * Analyzes the text in a character array and produces as output a Variant containing the parsed value.
   * @param variant Variant
   * @param value char[]
   * @param offset integer
   * @param len integer
   * @throws InvalidFormatException
   */
  public final void parse(Variant variant, char[] value, int offset, int len) throws InvalidFormatException {
    if (value == null || len == 0 || value.length == 0) {
      variant.setUnassignedNull();
      return;
    }
    boolean negative  = (value[offset] == '-');
    int     digit;
    int     result  = 0;
    if (negative) {
      ++offset;
      --len;
    }
    for (int index = offset; len > 0; ++index, --len) {
      digit = value[index];
      if (digit>= '0' && digit <= '9')
        digit = digit - '0';
      else {
        digit = Character.digit((char)digit,10);
        if (digit < 0)
          throw new InvalidFormatException(new String(value, offset, len));
      }
      result = result * 10 + digit;
    }

    int i = negative ? -result : result;
    if (i > 32767 || i < -32768) {
      throw new InvalidFormatException(Res.bundle.getString(ResIndex.ShortPrecisionBad));
    }

    setFromInt(variant, type, i);
  }

}
