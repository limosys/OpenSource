//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/LocaleUtil.java,v 7.0 2002/08/08 18:40:52 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.lang.*;
import java.util.*;
import java.text.*;

/**
 * Returns the locale identified by a single string with "locale_country_variant"
 * as returned by <code>Locale.toString()</code>.
 */
public class LocaleUtil {

  /**
   * Returns the locale identified by a single string with
   * "locale_country_variant" as returned by <code>Locale.toString</code>.
   * This method may ultimately be replaced by a similar service in the
   * <code>java.util.Locale</code> package.
   * @param localeString A single string containing language, country,
   *                      and variant, separated by '_'. A null or empty string yields the
   *                      default locale. The country and variant are optional.
   *
   * @return the Locale for the given language, country, and variant
   */
  public static Locale getLocale(String localeString) {
    if (localeString == null || localeString.length() == 0)
      return Locale.getDefault();

    String[] strings = new String[3];
    strings[0] = strings[1] = strings[2] = "";

    StringTokenizer tokenizer = new StringTokenizer(localeString, "_");
    for (int i = 0; i < strings.length && tokenizer.hasMoreTokens(); ++i) {
      strings[i] = tokenizer.nextToken();
      //System.err.println("boolean.decomposePattern[" + i + "] is " + strings[i]);
    }

    return new Locale(strings[0], strings[1], strings[2]);

  }
}
