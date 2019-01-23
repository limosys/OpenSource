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

public class IntegerTagEditor implements PropertyEditor
{
  int[] values;               // the array of values (null counts from 0:n)
  String[] resourceStrings;   // strings the user will see in the drop down
  String[] sourceCodeStrings; // strings this will generate in source code

  public IntegerTagEditor(int[] values, String[] resourceStrings, String[] sourceCodeStrings) {
    // A null list of integer values assumes an incrementing enumeration from 0
    if (values == null) {
      values = new int[resourceStrings.length];
      for (int i = 0; i < values.length; ++i)
        values[i] = i;
    }
    this.values = values;
    this.resourceStrings = resourceStrings;
    // A null set of sourceCode Strings assumes the resourceStrings are the same
    this.sourceCodeStrings = (sourceCodeStrings != null) ? sourceCodeStrings : resourceStrings;
  }

  /**
   * This version of IntegerTagEditor will sort by resourceStrings
   */
  public IntegerTagEditor(int[] values, String[] resourceStrings, String[] sourceCodeStrings, boolean sort) {
    this(values, resourceStrings, sourceCodeStrings);
    if (sort) {
      for (int i = 0; i < resourceStrings.length; ++i) {
        for (int j = i+1; j < resourceStrings.length; ++j) {
          int compare = resourceStrings[i].compareTo(resourceStrings[j]);
          if (compare > 0) {
            String s = resourceStrings[i];
            resourceStrings[i] = resourceStrings[j];
            resourceStrings[j] = s;

            s = sourceCodeStrings[i];
            sourceCodeStrings[i] = sourceCodeStrings[j];
            sourceCodeStrings[j] = s;

            int iVal = values[i];
            values[i] = values[j];
            values[j] = iVal;
          }
        }
      }
    }
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
    int iVal = (value == null || !(value instanceof Integer))
                  ? values[0]
                  : ((Integer)value).intValue();
    int iPos;
    for (iPos = 0; iPos < values.length; ++iPos)
      if (values[iPos] == iVal)
        break;
    if (iPos >= values.length)
      return "";
    return (forSourceCode) ? sourceCodeStrings[iPos] : resourceStrings[iPos];
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
      value = new Integer(values[iPos]);
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
    if (listener != null)
      listener.propertyChange(new PropertyChangeEvent(this, "???", null/*???*/, value));  
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
