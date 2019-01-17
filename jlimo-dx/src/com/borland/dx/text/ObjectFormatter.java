//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ObjectFormatter.java,v 7.0 2002/08/08 18:40:14 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.dataset.Variant;
import java.io.Serializable;

/**
 * The ObjectFormatter component formats and parses data of type Object
 */
public class ObjectFormatter extends VariantFormatter implements Serializable
{
  /**
   * Creates an ObjectFormatter class.
   */
  public ObjectFormatter() {
    super();
  }

  /**
   * Returns a String representing the given value stored in the Variant. A returned
   * empty string indicates a null or empty input value. null means the formatting failed
   * @param value variant
   * @return
   */
  public final String format(Variant value) {
    Object obj = value.getObject();
    return obj == null ? "" : obj.toString();
  }

  /**
   * Analyzes the given String and produces as output a Variant containing
   *  the approriate value. A null return value results when stringValue is null or empty.
   * @param stringValue String
   * @param value Variant
   */
  public void parse(String stringValue, Variant value) {
    value.setObject(stringValue);
  }

  /**
   * A high-speed parse that parses directly into a character array.
   * @param variant Variant
   * @param value char[]
   * @param offset integer
   * @param len integer
   */
  public void parse(Variant variant, char[] value, int offset, int len) {
    variant.setObject(null);
  }

  /**
   * Returns the Variant type, which is always Variant.OBJECT for ObjectFormatter.
   * @return
   */
  public int getVariantType() { return Variant.OBJECT; }
}
