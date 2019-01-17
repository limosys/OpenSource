//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/SystemResourceBundle.java,v 7.1 2002/10/07 21:24:02 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import java.util.*;
import java.lang.reflect.*;
import java.io.InputStream;

/**
  * Loads a ResourceBundle using the System classloader.
  * Necessary for applets that load a ResourceBundle that belongs to the
  * system (e.g. java.text.resources.LocaleElements).
  */

class SystemResourceBundle {
  private static String localeElementsBundleName;
  /**
    * Load the named resource bundle for the given locale.
    */
  static ResourceBundle getBundle(String name, Locale locale)
                          throws MissingResourceException
  {
    return ResourceLoader.getBundle(null, name, locale);
  }
  /*
   * Load the named resource bundle for the default locale.
   */
  static ResourceBundle getBundle(String name) throws MissingResourceException
  {
    return getBundle(name, Locale.getDefault());
  }

  static ResourceBundle getLocaleElementsBundle(Locale locale)
                          throws MissingResourceException
  {
//      ResourceBundle resource = ResourceBundle.load(new TextLoader(),
//        "LocaleElements", locale);

//! JDK beta 3.2
//      ResourceBundle resource =
//        ResourceBundle.getBundle("java.text.resources.LocaleElements", locale,
//                                         null);//DateFormat.class.getClassLoader());

    if (localeElementsBundleName == null) {
      String version = System.getProperty("java.version"); //NORES
      boolean afterJDK1_4 = version.charAt(0) != '1' || version.charAt(1) != '.' || version.charAt(3) != '.' || version.charAt(2) >= '4'; //NORES
      localeElementsBundleName = afterJDK1_4 ? "sun.text.resources.LocaleElements" : "java.text.resources.LocaleElements"; //NORES
    }
    return getBundle(localeElementsBundleName, locale);
  }
}


/**
  * Provides functionality missing in java.util.ResourceBundle.
  * This class provides an equivalent to java.util.ResourceBundle.getBundle()
  * that allows a specified classloader to be used.  java.util.ResourceBundle()
  * always uses the class loader of its caller.
  */
class ResourceLoader {

  // Technique to find obfuscated class name
  private static final Class realClass = new ResourceLoader().getClass();

  static ResourceBundle getBundle(ClassLoader loader, String name, Locale locale)
                          throws MissingResourceException
  {
    try {
      if (loader==null) {
        // Trying to load a system class...
        //!RC 3/29/99 Cannot use .class on package scope name since obfuscated
        //if (ResourceLoader.class.getClassLoader()==null) {
        if (ResourceLoader.realClass.getClassLoader()==null) {
          // ...from a local application.
          return java.util.ResourceBundle.getBundle(name, locale);
        }
        else {
          // ...from an applet.
          // We need to call ResourceBundle.getBundle() from the context
          // of a local class.
          Class clazz = Class.forName("java.util.ResourceBundle");
          Method getBundleMethod = clazz.getMethod("getBundle",
                                      new Class[]{String.class, Locale.class});
          return (ResourceBundle)getBundleMethod.invoke(null, new Object[]{name, locale});
        }
      }
      else {
        // Trying to load a classloader resource...
        if (ResourceLoader.class.getClassLoader()==null) {
          // ...from a local application
          Class clazz = Class.forName("java.util.ResourceBundle");
          Method getBundleMethod = clazz.getMethod("getBundle",
                                      new Class[]{String.class, Locale.class});
          return (ResourceBundle)getBundleMethod.invoke(null, new Object[]{name, locale});
        }
        else {
          // ...from an applet
          Class clazz = loader.loadClass("java.util.ResourceBundle");
          Method getBundleMethod = clazz.getMethod("getBundle",
                                      new Class[]{String.class, Locale.class});
          return (ResourceBundle)getBundleMethod.invoke(null, new Object[]{name, locale});
        }
      }

    } catch (Exception e) {
      throw new MissingResourceException("can't find resource for ", //NORES
                                            name, "");
    }

  }
  static ResourceBundle getBundle(ClassLoader loader, String name) throws MissingResourceException
  {
    return getBundle(loader, name, Locale.getDefault());
  }
}

