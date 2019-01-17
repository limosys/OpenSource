//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ByteFormatter.java,v 7.0 2002/08/08 18:40:10 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.IntegerFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.dataset.Variant;

import java.lang.Character;
import java.io.*;

/**
 * A formatter class for parsing and formatting byte data values. It is used for
 *  formatting and parsing byte columns in DataSets. The range of a byte is -128 to 127.
 */
public class ByteFormatter extends IntegerFormatter implements Serializable
{
 /**
 * Constructs a ByteFormatter object.
 */
  public ByteFormatter(int type) {
    super(type);
  }

  //! for JB 3.0, catch possible NumberFormatException, and rethrow as InvalidFormatException (dcy BTS 19777)
  /**
   * Analyzes the given String and produces as output a Variant
   * containing the approriate value.
   * @param stringValue String
   * @param value Variant
   * @throws InvalidFormatException
   */
  public final void parse(String stringValue, Variant value) throws InvalidFormatException {
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    int i = 128;
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
    if (i > 127 || i < -128) {
      throw new InvalidFormatException(Res.bundle.getString(ResIndex.BytePrecisionBad));
    }

    setFromInt(value, type, i);
  }

  /**
   * A high-speed parse that parses directly into a character array
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
    if (i > 127 || i < -128) {
      throw new InvalidFormatException(Res.bundle.getString(ResIndex.BytePrecisionBad));
    }

    setFromInt(variant, type, i);
  }

}
