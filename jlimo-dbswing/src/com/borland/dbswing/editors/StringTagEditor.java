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

//import com.borland.jbcl.util.*;

public class StringTagEditor implements PropertyEditor
{
  String[] resourceStrings;   // strings the user will see in the drop down
  String[] sourceCodeStrings; // strings this will generate in source code

  public StringTagEditor(String[] resourceStrings, String[] sourceCodeStrings) {
    this.resourceStrings = resourceStrings;
    // A null set of sourceCode Strings assumes the resourceStrings are the same
    this.sourceCodeStrings = (sourceCodeStrings != null) ? sourceCodeStrings : resourceStrings;
  }

  // PropertyEditor Implementation

  public void setValue(Object o) {
    value = o;
    fire();
  }

  public Object getValue() {
    return value;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    // Silent nop.
  }

  private String getAsText(boolean forSourceCode) {

    if (value == null || !(value instanceof String))
      return "";  

    // Asking for the value for display purposes just parrots back the given value
    if (!forSourceCode)
      return ((String) value);

    // Getting the string for source code looks up the display value's position
    int iPos;
    for (iPos = 0; iPos < resourceStrings.length; ++iPos)
      if (resourceStrings[iPos].equals((String)value))
        break;
    if (iPos >= resourceStrings.length)
      return "";  
    return sourceCodeStrings[iPos];
  }

  public String getAsText() {
    return getAsText(false);
  }

  public String getJavaInitializationString() {
    return getAsText(true);
  }

  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    int iPos = 0;
    if (text != null) {
      for (; iPos < resourceStrings.length; ++iPos)
        if (text.equals(resourceStrings[iPos]))
          break;
      if (iPos >= resourceStrings.length)
        throw new java.lang.IllegalArgumentException();
      value = text;
      fire();
    }
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
      listener.propertyChange(new PropertyChangeEvent(this, "???", null/*???*/, value));  
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listener = l;
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listener = null;
  }

  private PropertyChangeListener listener;
  private Object value;
}
