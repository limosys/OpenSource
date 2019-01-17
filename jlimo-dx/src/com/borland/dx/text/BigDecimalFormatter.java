//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/BigDecimalFormatter.java,v 7.0 2002/08/08 18:40:10 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.dataset.Variant;
import java.io.*;
import java.util.*;
import java.math.*;

/**
 * The BigDecimalFormatter class formats and parses the java.math.BigDecimal values.
 */
public class BigDecimalFormatter extends VariantFormatter implements Serializable
{
  private int scale = -1;
  /**
   * Constructs a BigDecimalFormatter object.
   */
  public BigDecimalFormatter(int scale) {
    super();
//!    System.out.println("BigDecimal: scale = " + scale);
    this.scale = scale;
//    System.err.println("BigDecimalFormatter(" + this.scale + ")");
  }

  /**
   * Returns a String representing the given BigDecimal value stored in the supplied Variant.
   * A returned
   * empty string indicates a null or empty input value. null means the formatting failed.
   * @param value Variant
   * @return String
   */
  public String format(Variant value) {
//    return new BigDecimal(value.getBigDecimal()).toString(); //! JDK beta 3.2
    String s =  (value == null || value.isNull())
              ? ""
              : (scale >= 0)
                  ? value.getBigDecimal().setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
                  : value.getBigDecimal().toString();
//    System.err.println("format of " + value + " is: " + s);
    return s;
  }

  //! TODO <rac> Consider hand coding an override for this one too...
  //! public FastStringBuffer format(Variant value, FastStringBuffer fsb);
  //! for JB 3.0, catch possible NumberFormatException, and rethrow as InvalidFormatException (dcy BTS 19777)
  /**
   * Analyzes the given String and produces as output a Variant containing
   *  the approriate value.
   * @param stringValue String
   * @param value Variant
   * @throws InvalidFormatException
   */
  public void parse(String stringValue, Variant value) throws InvalidFormatException {
//    value.setBigDecimal(new BigDecimal(stringValue, scale)); //! JDK beta 3.2
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }

    BigDecimal bn;
    try {
      bn = new BigDecimal(stringValue);
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
    if (scale >= 0) {
//      System.err.println("BigDecimalFormatter.parse() overriding scale of " + bn.scale() + " with " + this.scale);
      bn = bn.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
    value.setBigDecimal(bn);
//!    value.setBigDecimal(bn /*new BigDecimal(stringValue)*/);
//!    System.err.println("BigDecimalFormatter.parse(" + stringValue + ") gives: " + value);
  }

//!  /*!
//!  public void parse(Variant variant, char[] value, int offset, int len) {
//!    if (len == 0)
//!      variant.setUnassignedNull();
//!    else
//!      variant.setBigDecimal(new BigDecimal(new String(value, offset, len), scale));
//!  }
//!  */

/**
 * Returns the Variant type, which is always Variant.BIGDECIMAL for BigDecimalFormatter.
 * @return integer
 */
  public int getVariantType() { return Variant.BIGDECIMAL; }

}
