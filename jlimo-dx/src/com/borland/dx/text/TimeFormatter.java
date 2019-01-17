//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/TimeFormatter.java,v 7.0 2002/08/08 18:40:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.dataset.Variant;

import java.sql.*;
import java.io.*;

/**
 * The TimeFormatter class formats and parses time data values.
 */
public class TimeFormatter extends VariantFormatter implements Serializable
{
  /**
   * Constructs a TimeFormatter object.
   */
  public TimeFormatter() {
    super();
  }

  /**
   * Returns a String representing the given timestamp stored in the Variant. A returned
   *  empty string indicates a null or empty input value. null means the formatting failed
   * @param value Variant
   *
   */
  public final String format(Variant value) {
    return (value == null || value.isNull())
              ? ""
              : value.getTime().toString();
    // Note: toString and valueOf use JDBC date escape syntax so must agree with each other
  }

  /**
   * Analyzes the given String and produces as output a Variant containing the approriate value.
   * @param stringValue String
   * @param value Variant
   */
  public final void parse(String stringValue, Variant value) {
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    java.sql.Time t = java.sql.Time.valueOf(stringValue);  // uses JDBC date escape form
    value.setTime(t);
  }

  /**
   * A high-speed parse that parses directly into a character array.
   * @param variant Varaint
   * @param value char[]
   * @param offset integer
   * @param len integer
   */
  public void parse(Variant variant, char[] value, int offset, int len) {
    if (len == 0)
      variant.setUnassignedNull();
    else
      variant.setTime(java.sql.Time.valueOf(new String(value, offset, len)));
  }

  /**
   *
   *Returns the type of the Variant, which is always Variant.TIME for TimeFormatter
   */
  public int getVariantType() { return Variant.TIME; }
}
