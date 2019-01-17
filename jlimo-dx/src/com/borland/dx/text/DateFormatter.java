//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/DateFormatter.java,v 7.0.2.1 2004/05/23 21:11:15 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.dataset.Variant;

import java.sql.*;
import java.io.*;

/**
 * The DateFormatter class uses a Variant of type Variant.DATE,
 * which stores data in a java.sql.Date object.
 */
public class DateFormatter extends VariantFormatter implements Serializable
{
  // create a static formatter for parsing GMT dates (used by parse() below)
  private static java.text.DateFormat gmtFormatter;

  /**
   * Constructs a DateFormatter object.
   */
  public DateFormatter() {
    super();
  }

  /**
   *  Returns a String representing the given date stored in the Variant. A returned
   *  empty string indicates a null or empty input value.
   * @param value Variant
   */
  public final String format(Variant value) {
    //!String s = value.getDate().toString();
    //!Note: toString and valueOf use JDBC date escape syntax so must agree with each other
    //!System.out.println("DateFormatter: formatted " + s + " from " + value.getDate().getTime());
    //!return s;
//!JOAL    return (value == null || value.isNull())
//!JOAL          ? ""
//!JOAL          : (new java.util.Date((value).getDate().getTime())).toGMTString();
    //!return value.toString();

    //! JOAL: Use the SQL standard for DATEs:
    return (value == null || value.isNull() ? "" : value.toString());
  }

  //! TODO <rac> Consider hand coding an override for this one too...
  //! public FastStringBuffer format(Variant value, FastStringBuffer fsb);

  /**
   * Analyzes the given String and produces as output a
   * Variant containing the appropriate value.
   * @param stringValue String
   * @param value Variant
   */
  public final void parse(String stringValue, Variant value) {
//!JOAL    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
//!JOAL      value.setUnassignedNull();
//!JOAL      return;
//!JOAL    }
//!JOAL    Date date = new java.sql.Date(70, 0, 1);
//!JOAL    try {
//!   Fix for BTS 81394:
//!   Use the SimpleDateFormat() formatter instead of the (deprecated) java.util.Date.parse()
//!   method which doesn't properly handle dates with years < 100.  Note that SimpleDateFormat's
//!   parser can throw a ParseException, which we rethrow as an IllegalArgumentException() (as
//!   java.util.Date.parse() does) to avoid having to change the (exception) signature of this method.
//!      date.setTime(new java.util.Date(stringValue).getTime());
//!      // Note: we use util date since it has a smarter constructor and will parse GMT time
//!JOAL      if (gmtFormatter == null) {
//!JOAL        gmtFormatter = new java.text.SimpleDateFormat("d MMM yyyy HH:mm:ss z", java.util.Locale.US);
//        gmtFormatter = new java.text.SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US);
//!JOAL        gmtFormatter.setLenient(false);
//!JOAL      }
//!JOAL      date.setTime(gmtFormatter.parse(stringValue).getTime());
//!JOAL    }
//!    catch (RuntimeException ex) {
//!      System.out.println("Format error "+ stringValue);
//!      throw ex;
//!    }
//!JOAL    catch (java.text.ParseException ex) {
//!JOAL      throw new IllegalArgumentException("Format error:  "+stringValue);
//!JOAL    }
//!JOAL    value.setDate(date);

//!    System.out.println("DateFormatter: parsing " + stringValue);
//!    java.sql.Date d = java.sql.Date.valueOf(stringValue);  // uses JDBC date escape form
//!    value.setDate(d);

    //!JOAL Use the SQL standard for DATEs:
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
    }
    else {
      try {
        value.setFromString(Variant.DATE, stringValue);
      }
      catch (Exception ex) {
        // Backwards compatible parsing:
         if (gmtFormatter == null) {
           gmtFormatter = new java.text.SimpleDateFormat("d MMM yyyy HH:mm:ss z", java.util.Locale.US);
//         gmtFormatter = new java.text.SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US);
           gmtFormatter.setLenient(false);
         }
         try {
           value.setDate(gmtFormatter.parse(stringValue).getTime());
         }
         catch (Exception ex2) {
           throw new IllegalArgumentException(Res.bundle.format(ResIndex.FormatError, stringValue));
         }
      }
    }
  }

  /*!
  public void parse(Variant variant, char[] value, int offset, int len) {
    if (len == 0)
      variant.setUnassignedNull();
    else {
      Date date = null;
      //! TODO. When sun surfaces a java.sql.Date(string) constructor,
      //! stop making two allocations.
      //!
      //! TODO.  How should time zone be handled. - local for display, but
      //! perhaps GMT for export/import?
      //!
      date = new java.sql.Date(70, 1, 1);

      try {
//!  System.out.println("DateFormatter: parsing " + new String(value, offset, len));
        date.setTime(new java.sql.Date(new String(value, offset, len)).getTime());
      }
      catch (RuntimeException ex) {
        System.out.println("DateFormatter: parse exception on "+new String(value, offset, len));
  variant.setUnassignedNull();
        throw ex;
      }
      variant.setDate(date);
    }
  }
  */

  /**
   * Returns the Variant type, which is always Variant.DATE for DateFormatter classes.
   * @return integer
   */
  public int getVariantType() { return Variant.DATE; }

}
