//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/SimpleFormatter.java,v 7.0 2002/08/08 18:40:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.dataset.Variant;

import java.text.*;
import java.util.*;
import java.io.*;

/**
* This class is a simple wrapper for the VariantFormatStr formatter/parser class.  It serves as a
* simple implementation if you need a quick and dirty formatter/parser which uses both the default
* locale and the default control pattern for the particular Variant you pass in.  Obviously, performance
* is much enhanced if you hold onto this class rather than create it every time you format/parse.
*/
public class SimpleFormatter extends VariantFormatter implements Serializable
{
  VariantFormatStr fms;    // will instantiate as needed

  /**
   * Constructs a SimpleFormatter object.
   */
  public SimpleFormatter() {
    fms = null;
  }

  /**
   * Constructs a SimpleFormatter object that instantiates a VariantFormatStr object
   * of the specified Variant type.
   * @param variantType integer
   */
  public SimpleFormatter(int variantType) {
    this();
    fms = new VariantFormatStr(null, variantType, null);    // use default string, default locale
  }

  /**
   * Calls the format() method of the VariantFormatStr object,
   * returning the formatted string
   * @param value Variant
   * @return String
   */
  public String format(Variant value) {
    return fms.format(value);
  }

  /**
   * Analyzes the given String and produces as output a Variant containing
   * the approriate value.
   * @param stringValue String
   * @param value Variant
   * @throws InvalidFormatException
   */
  public void parse(String stringValue, Variant value) throws InvalidFormatException {
    fms.parse(stringValue, value);
  }

  /**
   * Analyzes the given String and produces as output a Variant containing the approriate value.
   * @param stringValue String
   * @param value Variant
   * @param variantType integer
   * @throws InvalidFormatException
   */
  public void parse(String stringValue, Variant value, int variantType) throws InvalidFormatException {
    fms.parse(stringValue, value, variantType);
  }

  public int getVariantType() {
    return fms.getVariantType();
  }

  public String getPattern() {
    return fms.getPattern();
  }

  public String setPattern(String pattern) {
    return fms.setPattern(pattern);
  }

  public Object setSpecialObject(int objType, Object obj) {
    return fms.setSpecialObject(objType, obj);
  }

  /**
   * Calls the getSpecialObject() method of the VariantFormatStr object,
   * returning the special object.<p> Some Formatter classes define special objects for their own use. You must know the internal
   *  details of the Format subclass being used to use getSpecialObject().
   * @param objType integer
   *
   */
  public Object getSpecialObject(int objType) {
    return fms.getSpecialObject(objType);
  }

  public Locale getLocale() {
    return fms.getLocale();
  }

  public Format getFormatObj() {
    return fms.getFormatObj();
  }

}
