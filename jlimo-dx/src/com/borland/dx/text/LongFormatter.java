//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/LongFormatter.java,v 7.0 2002/08/08 18:40:14 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.jb.util.FastStringBuffer;
import com.borland.dx.dataset.Variant;

import java.lang.Character;
import java.io.*;

/**
 * The LongFormatter class formats and parses the long data type.
 */
public class LongFormatter extends VariantFormatter implements Serializable
{
  /**
   * Constructs a LongFormatter object
   */
  public LongFormatter() {
    super();
  }

  /**
   * Returns a String representing the given long value stored in the Variant.
   * A returned empty string indicates a null or empty input value. null means the formatting failed.
   * @param value Variant
   * @return
   */
  public final String format(Variant value) {
    return (value == null || value.isNull())
              ? ""
              : new Long(value.getLong()).toString();
  }

  //! TODO <rac> Consider hand coding an override for this one too...
  //! public FastStringBuffer format(Variant value, FastStringBuffer fsb);
  //! for JB 3.0, catch possible NumberFormatException, and rethrow as InvalidFormatException (dcy BTS 19777)
  /**
   * Analyzes the given String and produces as output an Object containing the
   * appropriate value. A null return value results when stringValue is null or empty.
   * @param stringValue String
   * @param value Varaint
   * @throws InvalidFormatException
   */
  public final void parse(String stringValue, Variant value) throws InvalidFormatException {
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    try {
      value.setLong(Long.parseLong(stringValue, 10));
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
  }


  /**
   * A high-speed parse that parses directly into a character array.
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
    int     result      = 0;
    long    longResult  = 0;
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
      if (result > 99999999) {
        if (longResult == 0)
          longResult  = result;
        longResult  = longResult * 10 + digit;
      }
      else
        result = result * 10 + digit;
    }

    if (longResult == 0)
      variant.setLong((long)(negative ? -result : result));
    else
      variant.setLong(negative ? -longResult : longResult);
  }

  /**
   * Returns the Variant type, which is always Variant.LONG for LongFormatter.
   * @return integer
   */
  public int getVariantType() { return Variant.LONG; }
}
