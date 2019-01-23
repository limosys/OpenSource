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
// Copyright (c) 1996 - 2004 Borland Software Corporation.  All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE
package com.borland.dbswing;

import com.borland.jb.util.BasicBeanInfo;

/**
 * <p> BeanInfo for <code>ColumnLayout</code>.</p>
 */

public class ColumnLayoutBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public ColumnLayoutBeanInfo() {
    beanClass = ColumnLayout.class;
     propertyDescriptorAttributes =  new Object[][]  {
       {"enumerationValues", new Object[] {
                             "TOP", new Integer(ColumnLayout.TOP),"ColumnLayout.TOP",
                             "MIDDLE", new Integer(ColumnLayout.MIDDLE), "ColumnLayout.MIDDLE",
                             "BOTTOM", new Integer(ColumnLayout.BOTTOM), "ColumnLayout.BOTTOM"},},
       {"enumerationValues", new Object[] {
                             "LEFT", new Integer(ColumnLayout.LEFT),"ColumnLayout.LEFT",
                             "MIDDLE", new Integer(ColumnLayout.MIDDLE), "ColumnLayout.MIDDLE",
                             "RIGHT", new Integer(ColumnLayout.RIGHT), "ColumnLayout.RIGHT"},},
       };
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
      {"horizontalAlignment",  "horizontal alignment of components in container", "", "",},     //RES NORES,BI_halign,NORES,NORES
      {"verticalAlignment",    "vertical alignment of components in container", "", "",},     //RES NORES,BI_valign,NORES,NORES
      {"hgap",                 "horizontal gap between components", "", "",},     //RES NORES,BI_hgap,NORES,NORES
      {"vgap",                 "vertical gap between components", "", "",},     //RES NORES,BI_vgap,NORES,NORES
      {"horizontalFill",       "increase height of last component to fill container", "", "",},     //RES NORES,BI_hfill,NORES,NORES
      {"verticalFill",         "increase width of components to fill container", "", "",},     //RES NORES,BI_vfill,NORES,NORES

    };
  }
}
