//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/StringArrayResourceBundle.java,v 7.1 2003/06/19 21:36:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Vector;
import java.text.MessageFormat;

public abstract class StringArrayResourceBundle extends ResourceBundle
{
  protected int end_index;
  protected String[] strings;

  protected Object handleGetObject(String key) throws MissingResourceException
  {
    throw new MissingResourceException( "Keyed resources not supported", //NORES
                                        "StringArrayResourceBundle", key); //NORES
  }

  public String getString( int index )
  {
    return (index < strings.length && strings[index] != null) ?
           strings[index] :
           getClass().getName() + ":" + index;
  }


  public Enumeration getKeys()
  {
    return( new Vector().elements() );
  }

  public final String format(int index, Object[] params) {
    if (params != null) {
      for (int paramIndex = 0; paramIndex < params.length; ++paramIndex)
        params[paramIndex] = safe(params[paramIndex]);
    }
    return MessageFormat.format(getString(index), params);
  }

  public final String format(int index, Object param1) {
    return MessageFormat.format(getString(index), new Object[] { safe(param1) });
  }

  public final String format(int index, Object param1, Object param2) {
    return MessageFormat.format(getString(index), new Object[] { safe(param1), safe(param2) });
  }

  public final String format(int index, Object param1, Object param2,
      Object param3) {
    return MessageFormat.format(getString(index), new Object[] { safe(param1), safe(param2), safe(param3) });
  }

  private Object safe(Object param) {
    // Needed for jdk1.1.8
    //
    if (param == null)
      return "null";		//NORES
    return param;
  }

}

