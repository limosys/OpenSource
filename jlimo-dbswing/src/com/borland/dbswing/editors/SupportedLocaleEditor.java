/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.dbswing.editors;

import java.beans.*;
import java.text.*;
import java.util.*;

public class SupportedLocaleEditor implements PropertyEditor
{
  static String[] resourceStrings;

  private String[][] locales = {
    {"de",      Res._BI_German                                },       //RES NORES,BI_German
    {"el",      Res._BI_Greek                                 },       //RES NORES,BI_Greek
    {"es",      Res._BI_Spanish                               },       //RES NORES,BI_Spanish
    {"fr",      Res._BI_French                                },       //RES NORES,BI_French
    {"hr",      Res._BI_Croatian                              },       //RES NORES,BI_Croatian
    {"it",      Res._BI_Italian                               },       //RES NORES,BI_Italian
    {"ko",      Res._BI_Korean                                },       //RES NORES,BI_Korean
    {"no",      Res._BI_Norwegian                             },       //RES NORES,BI_Norwegian
    {"ru",      Res._BI_Russian                               },       //RES NORES,BI_Russian
    {"tr",      Res._BI_Turkish                               },       //RES NORES,BI_Turkish
    {"zh_CN",   Res._BI_Chinese_China                         },       //RES NORES,BI_Chinese_China
    {"zh_TW",   Res._BI_Chinese_Taiwan                        },       //RES NORES,BI_Chinese_Taiwan
  };

  public SupportedLocaleEditor() {
    if (resourceStrings == null) {
      resourceStrings = new String[locales.length+1];
      resourceStrings[0] = Res._BI_default;     
      String currentLocaleId = Locale.getDefault().toString();
      for (int i = 0; i < locales.length; i++) {
        if (currentLocaleId.equals(locales[i][0])) {
          resourceStrings[i+1] = getLocaleDisplayName(Locale.getDefault());
        }
        else {
          resourceStrings[i+1] = locales[i][1] + " [" + locales[i][0] + "]";   
        }
      }
    }
  }

  /*
   * JDK has a bug in that a locale with a non-specified
   * country still returns a country in the the display name.
   * E.g. the Locale("de") should only return "German",
   * not "German (Germany)".  The content of the locale
   * data is not well synchronized with the spec.
   */
  private static String getLocaleDisplayName0(Locale loc, Locale locDisp) {
    StringBuffer result = new StringBuffer(loc.getDisplayLanguage(locDisp));

    String country = loc.getCountry().length() != 0 ? loc.getDisplayCountry(locDisp) : "";  
    String variant = loc.getVariant().length() != 0 ? loc.getDisplayVariant(locDisp) : "";  

    if (country.length() != 0 || variant.length() != 0) {
      result.append(" (");   
      result.append(country);
      if (country.length() != 0 && variant.length() != 0) {
        result.append(",");  
      }
      result.append(variant);
      result.append(")");    
    }
    return result.toString();
  }

  private static String getLocaleDisplayName(Locale loc) {
    String result;
    if (loc.getLanguage().equals(Locale.getDefault().getLanguage())) {
      result = getLocaleDisplayName0(loc, loc);
    }
    else {
      result = getLocaleDisplayName0(loc, Locale.ENGLISH);
    }
    return result + " [" + loc.toString() + "]";  
  }


  private static String getConstructorString(Locale loc) {
    return "new java.util.Locale(\"" + loc.getLanguage() + "\", \"" +  
      loc.getCountry() + "\", \"" + loc.getVariant() + "\")";          
  }

  public void setValue(Object o) {
    value = (Locale)o;
    fire();
  }

  public Object getValue() {
    return value;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
  }

  // text to be displayed in inspector.  make sure we display the value corresponding to the
  // bundle which will actually be used at runtime.  But note that since (for now) the
  // bundle list is hard-coded, this won't work as expected if a user puts their own
  // bundle on the classpath.
  public String getAsText() {
    if (value == null) {
      return null;
    }
    return getLocaleDisplayName(value);
  }

  public String getJavaInitializationString() {
    if (value == null) {
      return null;
    }
    return getConstructorString(value);
  }

  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    if (text != null) {
      for (int pos = 0; pos < resourceStrings.length; pos++) {
        if (text.equals(resourceStrings[pos])) {
          if (pos == 0) {
            value = Locale.getDefault();
          }
          else {
            value = getLocale(locales[pos-1][0]);
          }
          fire();
          return;
        }
      }
      throw new java.lang.IllegalArgumentException();
    }
  }

  /**
   * This method will return the locale identified by a single string
   * with "locale_country_variant" as returned by Locale.toString.  This
   * method may ultimately be replaced by a similar service in the java.util.Locale
   * package.
   *
   * @param localeString A single string containing language, country, and variant, separated by '_'
   *        A null or empty string will yield the default locale.  The country and variant are
   *        optional.
   *
   * @return the Locale for the given language, country, and variant
   */
  private Locale getLocale(String localeString) {
    if (localeString == null || localeString.length() == 0)
      return Locale.getDefault();

    String[] strings = new String[3];
    strings[0] = strings[1] = strings[2] = "";  

    StringTokenizer tokenizer = new StringTokenizer(localeString, "_");   
    for (int i = 0; i < strings.length && tokenizer.hasMoreTokens(); ++i) {
      strings[i] = tokenizer.nextToken();
    }

    return new Locale(strings[0], strings[1], strings[2]);

  }

  public String[] getTags() {
    return resourceStrings;
  }

  public java.awt.Component getCustomEditor() {
    return null;
  }

  public boolean supportsCustomEditor() {
    return false;
  }

  private void fire() {
    if (listener != null) {
      listener.propertyChange(new PropertyChangeEvent(this, "LocaleEditor???", null, value));  
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listener = l;
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listener = null;
  }

  private PropertyChangeListener listener;
  private Locale value;

}
