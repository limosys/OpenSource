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

import java.beans.*;
import java.util.*;
import javax.swing.*;

/**
 * <p>Provides Swing internationalization support for several locales. It automatically
updates Swing's internal localizable resources appropriately for the current system locale when
instantiated. </p>
 * 
 * <p><code>IntlSwingSupport</code> needs to be instantiated only once in an application, and should be instantiated on application startup, before any Swing components are displayed. To initialize <code>IntlSwingSupport</code> for a locale other than the default locale (in a multilingual application, for example), set <code>IntlSwingSupport's locale</code> property to the target locale. </p>
 * <p>Example: </p>
 *
 * <pre>
 * IntlSwingSupport intlSupport = new IntlSwingSupport(); 
 * 
 * //Show a dialog to demonstrate IntlSwingSupport.
 * //User supplies a localized message and title.
 * //IntlSwingSupport translates text that 
 * //JOptionPane provides, such as button labels.
 * 
 * int response = JOptionPane.showConfirmDialog(frame, localizedMessageString,
 * localizedTitleString, JOptionPane.OK_CANCEL_OPTION); 
 * </pre>
 * 
 * <p>As of JDK 1.2, the only Swing components with visible, translatable text strings were the
JFileChooser, JColorChooser, and JOptionPane. </p>
 * 
 * @see java.util.Locale
 * @see JColorChooser
 * @see JFileChooser
 * @see JOptionPane
 */

public class IntlSwingSupport
  implements PropertyChangeListener, java.io.Serializable
{

 /**
  * <p>Constructs an <code>IntlSwingSuppor</code>t component for the current system locale. Calls the constructor of this class that takes a <code>locale</code> as a parameter, passing it <code>Locale.getDefault()</code>. </p>
  *
  */

  public IntlSwingSupport() {
    this(Locale.getDefault());
  }


 /**
  * <p>Constructs an <code>IntlSwingSuppor</code>t component for the specified locale.</p>
  *
  * @param locale  The locale being supported. 
  */
  public IntlSwingSupport(Locale locale) {
    try {
      res = ResourceBundle.getBundle("com.borland.dbswing.IntlSwingSupportRes");  
    }
    catch (MissingResourceException e) {
      e.printStackTrace();
      //DBExceptionHandler.handleException(e);
      return;
    }
    setLocale(locale);
    UIManager.addPropertyChangeListener(this);
  }

  /**
   * PropertyChangeListener implementation to detect change in look-and-feel
   * and load corresponding localized resources.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("lookAndFeel")) {  
      localizeEnvironment();
    }
  }

  private void localizeEnvironment() {
    findResourceBundles(UIManager.getLookAndFeel().getClass());
  }


 /**
  * <p>Sets the locale being supported. </p>
  * 
  * @param locale  The locale being supported. 
  * @see #getLocale
  */

  public void setLocale(Locale locale) {
    this.locale = locale;
    if (locale != null) {
      if(!java.beans.Beans.isDesignTime()) {
        localizeEnvironment();
      }
    }
  }

 /**
  * <p>Returns the locale being supported. </p>
  * 
  * @return  The locale being supported. 
  * @see #setLocale
  */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Loads look and feel resource bundles from the top of the class
   * heirarchy down.  This ensures that individual look and feels
   * can override their superclass look and feel implementations.
   * This also ensures that only the resources necessary for a
   * particular look and feel are loaded.
   */
  private void findResourceBundles(Class lookAndFeelClass) {
    if (lookAndFeelClass != null &&
        !(lookAndFeelClass.getName().equals("javax.swing.LookAndFeel"))) {  
      findResourceBundles(lookAndFeelClass.getSuperclass());

      // Ignore inability to find resources for unknown L&F (bug #80790)
      try {
        loadResourceBundle(res.getString(lookAndFeelClass.getName()));
      }
      catch (MissingResourceException ex) {
      }
    }
  }

  private void loadResourceBundle(String bundleName) {
    ResourceBundle bundle;
    try {
      bundle = ResourceBundle.getBundle(bundleName, locale);
    }
    catch (MissingResourceException e) {
      e.printStackTrace();
      //DBExceptionHandler.handleException(e);
      return;
    }
    Enumeration enumeration = bundle.getKeys();
    while(enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      UIManager.put(key, bundle.getObject(key));
    }
  }

  private transient ResourceBundle res;
  private Locale locale;

}
