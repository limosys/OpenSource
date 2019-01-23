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

//NOTTRANSLATABLE

package com.borland.dbswing;

import com.borland.jb.util.BasicBeanInfo;

/**
 * <p>BeanInfo for <code>FontChooser</code>.</p>
 */

public class FontChooserBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public FontChooserBeanInfo() {
    beanClass = FontChooser.class;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
      {"frame",       "parent frame for dialog (required)", "", ""},     //RES NORES,BI_DBPassPrompt_frame,NORES,NORES
//      {"modal",       Res._BI_FontChooser_modal, "", ""},
      {"title",       "dialog title", "", ""},     //RES NORES,BI_DBPassPrompt_title,NORES,NORES
      {"sampleText",  "set font preview text", "", ""},     //RES NORES,BI_FontChooser_sampleText,NORES,NORES
//      {"availableFontNames",  Res._BI_FontChooser_fontNames, "", ""},
      {"availableFontSizes",  "set sizes to display in default font size list", "", ""},     //RES NORES,BI_FontChooser_fontSizes,NORES,NORES
      {"allowAnyFontSize",  "allow user to enter any font size", "", ""},     //RES NORES,BI_FontChooser_anyFontSize,NORES,NORES
//      {"selectedFont",  Res._BI_FontChooser_selFont, "", ""},
    };
  }
}
