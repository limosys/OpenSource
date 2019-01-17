//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/DoubleFormatter.java,v 7.0 2002/08/08 18:40:11 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.dataset.Variant;

import java.lang.Character;
import java.io.*;

// Not public.
//
/**
 *
 *The DoubleFormatter class formats and parses data of type double.
 */
public class DoubleFormatter extends VariantFormatter implements Serializable
{
  /**
   * Constructs a DoubleFormatter object.
   */
  public DoubleFormatter(int type) {
    super();
    this.type = type;
  }

  /**
   * Returns a String representing the double value stored in the Variant.
   * All reasonable attempts are made to "cast" the type found in the object into
   *  the appropriate type specified in the constructor of the implementing classes.
   *  A returned
   * empty string indicates a null or empty input value. null means the formatting failed.
   * @param value Variant
   *
   */
  public final String format(Variant value) {
    return (value == null || value.isNull())
              ? ""
              : new Double(value.getAsDouble()).toString();
  }

  //! TODO <rac> Consider hand coding an override for this one too...
  //! public FastStringBuffer format(Variant value, FastStringBuffer fsb);
  //! for JB 3.0, catch possible NumberFormatException, and rethrow as InvalidFormatException (dcy BTS 19777)
  /**
   * Analyzes the given String and produces as output a
   * Variant containing the approriate double value.
   * @param stringValue String
   * @param value Variant
   * @throws InvalidFormatException
   */
  public final void parse(String stringValue, Variant value) throws InvalidFormatException {
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    try {
      setFromDouble(value, type, Double.valueOf(stringValue).doubleValue());
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

  public int getVariantType() { return type; }

  private int type;
}
