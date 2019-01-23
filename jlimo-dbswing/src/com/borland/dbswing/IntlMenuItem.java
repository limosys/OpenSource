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
package com.borland.dbswing;

import javax.swing.*;

 /**
  * An extension of <code>JMenuItem</code>. It adds the 
  * <code>textWithMnemonic</code> property for
  * internationalization support. The <code>textWithMnemonic</code>
  * property accepts a text string containing an
  * ampersand (&) preceding the character to be used as
  * the menu item's mnemonic character. This makes
  * internationalization easier because the text and
  * mnemonic can be translated as a single string. A
  * translator given a list of strings to translate need
  * not match single mnemonic characters in isolation
  * with their corresponding menu names. 
  *  
  * @see JMenuItem
  * @see IntlRadioButtonMenuItem
  * @see IntlMenu
  */
public class IntlMenuItem extends JMenuItem {

 /**
  * <p>Creates an <code>IntlMenuItem</code> with no
  * specified text or icon. Calls the constructor of this
  * class that takes a <code>String</code> and an <code>Icon</code> as parameters.
  * Passes a <code>null</code> and a <code>null</code> cast to an <code>Icon</code> to the other
  * constructor.  </p>
  */
  public IntlMenuItem() {
    this(null, (Icon) null);
    setRequestFocusEnabled(false);
  }

 /**
  * <p>Creates an <code>IntlMenuItem</code> that takes an icon. 
  * Calls the constructor of this
  * class that takes a <code>String</code> and an <code>Icon</code> as parameters.
  * Passes a <code>null</code> and the specified <code>Icon</code> to the other
  * constructor.  </p>
  *
  * @param icon The icon that appears on the menu item. 
  */
  public IntlMenuItem(Icon icon) {
    this(null, icon);
    setRequestFocusEnabled(false);
  }

 /**
  * <p>Creates an <code>IntlMenuItem</code> that 
  * contains an a text string. Calls the constructor of this
  * class that takes a <code>String</code> and an <code>Icon</code> as parameters.
  * Passes the specified <code>String</code> and a <code>null</code> cast to an <code>Icon</code> to the other
  * constructor.  </p>
  *
  * @param s The text string that appears on the menu item. 
  */
  public IntlMenuItem(String s) {
    this(s, (Icon) null);
  }

 /**
  * <p>Creates an <code>IntlMenuItem</code> that 
  * contains an a text string and an icon. Calls the constructor of its superclass 
  * that takes a <code>String</code> and an <code>Icon</code> as parameters, passing the specified values.
  *
  * @param s The text string that appears on the menu item. 
  * @param icon The icon that appears on the menu item. 
  */
  public IntlMenuItem(String s, Icon icon) {
    super(DBUtilities.excludeMnemonicSymbol(s), icon);
    if (DBUtilities.containsMnemonic(s)) {
      setTextWithMnemonic(s);
    }
  }


 /**
  * <p>Creates an <code>IntlMenuItem</code> that 
  * contains an a text string and a mnemonic character.
  * Calls the constructor of its superclass that takes a
  * <code>String</code> and an <code>int</code>, passing the specified values. </p>
  *
  * @param text The text string that appears on the menu item. 
  * @param mnemonic The character in the string that is the mnemonic character. 
  */
  public IntlMenuItem(String text, int mnemonic) {
    super(text, mnemonic);
  }


 /**
  * <p>Sets the text with an embedded mnemonic character.
  * <code>textWithMnemonic</code> is a convenience
  * property for setting the button's text, which
  * interprets an ampersand character (&) within the
  * text as an instruction to make the character
  * following the ampersand the mnemonic character for
  * the button. To put an ampersand in the text but not
  * make the character following it a hot key, put a back
  * slash before the ampersand. To make the ampersand the
  * hot key, put two consecutive ampersands in the text. </p>
  *
  * <p>This property can be used instead of the usual <code>text</code>
  * property, even if a mnemonic character is not
  * embedded in the text. It is particularly useful for
  * applications that resource strings for 
  * internationalization, because the text and mnemonic
  * can be specified in a single string. </p>
  * 
  * <p>Note that the first occurrence of the mnemonic
  * character is always denoted visibly as the mnemonic
  * key, despite the location of the ampersand within the
  * text. Furthermore, only the first occurrence of an
  * ampersand is removed from the text. </p>
  * 
  * <p>If both the <code>text</code> and <code>textWithMneumonic</code> properties are
  * set, the most recently set property takes
  * precedence. </p>
  * 
  * <p><code>textWithMnemonic</code> is a bound property, and therefore a
  * property change event is fired when its value
  * is modified. </p>
  * 
  * @param text The text to set with an embedded mnemonic character.
  * @see #getTextWithMnemonic
  */
  public void setTextWithMnemonic(String text) {
    String oldText = textWithMnemonic;
    if (oldText != text) {
      textWithMnemonic = text;
      setText(DBUtilities.excludeMnemonicSymbol(text));
      setMnemonic(DBUtilities.extractMnemonicChar(text));
      firePropertyChange("textWithMnemonic", oldText, text);    
    }
  }

  /**
   * <p>Returns the text with the embedded mnemonic character.</p>
   *
  * @return The text with with an embedded mnemonic character.
   * @see #setTextWithMnemonic
   */
  public String getTextWithMnemonic() {
    return textWithMnemonic;
  }

  /** holds text property value with embedded mnemonic character */
  private String textWithMnemonic;
}
