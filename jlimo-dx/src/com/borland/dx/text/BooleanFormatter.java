//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/BooleanFormatter.java,v 7.0 2002/08/08 18:40:10 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.DiagnosticJLimo;
import java.lang.Character;
import java.io.*;

// Not public.
//
/**
 * The BooleanFormatter class formats and parses boolean data.
 */
public class BooleanFormatter extends VariantFormatter implements Serializable
{
  /**
   * Constructs a BooleanFormatter object.
   */
  public BooleanFormatter() {
    super();
  }

  /**
   * Returns a String representing the given boolean value stored in the supplied Variant. A returned
   * empty string indicates a null or empty input value. null means the formatting failed.
   * @param value Varaint
   *
   */
  public final String format(Variant value) {
    if (value == null || value.isNull())
      return "";

    if (value.getBoolean())
      return Res.bundle.getString(ResIndex.True);
    else
      return Res.bundle.getString(ResIndex.False);
  }

  /**
   * Analyzes the given String and produces as output a Variant containing the
   *  appropriate value. A null return value results when stringValue is null or empty.
   * @param stringValue String
   * @param value Variant
   */
  public final void parse(String stringValue, Variant value) {
    if (stringValue == null || (stringValue=stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    value.setBoolean(stringValue.equalsIgnoreCase(Res.bundle.getString(ResIndex.True)));
  }

  /**
   * Returns the Variant type, which is always Variant.BOOLEAN for
   *  BooleanFormatter classes.
   *
   */
  public int getVariantType() { return Variant.BOOLEAN; }
}
