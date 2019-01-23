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

import javax.swing.*;

/**
 * <p>BeanInfo for <code>TableScrollPane</code>.</p>
 */


public class TableScrollPaneBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public TableScrollPaneBeanInfo() {
    beanClass = TableScrollPane.class;
     /**
   * Any additional named attributes for the JavaBean.
   *
   * Format:  {{"AttributeName", AttributeSetting}, ...}
   * Example: {{"isContainer", Boolean.TRUE}, {"containerDelegate", "getContentPane"}, ...}
   */
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.TRUE},
      {"containerDelegate", "getViewport"},
    };
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
      //  javax.swing.JScrollPane properties
      {"horizontalScrollBarPolicy","horizontal scrollbar policy", "",""},     //RES NORES,BI_TableScrollPane_horizPolicy,NORES,NORES
      {"verticalScrollBarPolicy",  "vertical scrollbar policy", "",""},     //RES NORES,BI_TableScrollPane_vertPolicy,NORES,NORES
      {"horizontalScrollBar",      "horizontal scrollbar", "",""},     //RES NORES,BI_TableScrollPane_horiz,NORES,NORES
      {"verticalScrollBar",        "vertical scrollbar", "",""},     //RES NORES,BI_TableScrollPane_vert,NORES,NORES
      {"rowHeader",                "scrollpane's row header", "",""},     //RES NORES,BI_TableScrollPane_rowHeader,NORES,NORES
      {"viewport",                 "scrollpane viewport", "","",},     //RES NORES,BI_TableScrollPane_viewport,NORES,NORES
      {"viewportBorder",           "viewport border", "","",},     //RES NORES,BI_TableScrollPane_viewportBorder,NORES,NORES
      {"columnHeader",             "scrollpane's column header", "","",},     //RES NORES,BI_TableScrollPane_columnHeader,NORES,NORES
      // java.awt.Component properties
      {"background",             "background color", "", ""},     //RES NORES,BI_background,NORES,NORES
//      {"bounds",                 Res._BI_bounds, "", ""},
      {"componentOrientation",         "language-sensitive component orientation", "", "", null, "false", "true"},     //RES NORES,BI_compOrientation,NORES,NORES,NORES,NORES
      {"cursor",               "cursor", "", "", null, "false", "true"},     //RES NORES,BI_cursor,NORES,NORES,NORES,NORES
      {"dropTarget",             "drop target", "", "", null, "false", "true"},     //RES NORES,BI_dropTarget,NORES,NORES,NORES,NORES
      {"enabled",                "enable or disable component", "", ""},     //RES NORES,BI_enabled,NORES,NORES
      {"font",                   "font used for painting", "", ""},     //RES NORES,BI_font,NORES,NORES
      {"foreground",             "foreground color", "", ""},     //RES NORES,BI_foreground,NORES,NORES
      {"locale",                 "locale", "", "", null, "false", "true"},     //RES NORES,BI_locale,NORES,NORES,NORES,NORES
//      {"location",                 Res._BI_location, "", "", null, "true"},
      {"name",                   "name", "", "", null, "false","true"},
//      {"size",                 Res._BI_size, "", "", null, "true"},
      {"visible",                "visibility state", "", "", null, "false","true"},     //RES NORES,BI_visible,NORES,NORES,NORES,NORES

      // java.awt.Container properties
//      {"layout",                 Res._BI_layout, "", "",},

      // javax.swing.JComponent properties
      {"alignmentX",             "preferred horizontal alignment", "", "", null, "true"},     //RES NORES,BI_alignmentX,NORES,NORES,NORES
      {"alignmentY",             "preferred vertical alignment", "", "", null, "true"},     //RES NORES,BI_alignmentY,NORES,NORES,NORES
      {"autoscrolls",            "automatically scrolls contents when dragged", "", "", null, "true"},     //RES NORES,BI_autoscrolls,NORES,NORES,NORES
      {"border",                 "border (insets)", "", "",null,  "true"},     //RES NORES,BI_border,NORES,NORES,NORES
      {"debugGraphicsOptions",   "diagnostic options for debugging graphics", "", "", "com.borland.jbuilder.cmt.editors.DebugGraphicsEditor", "true"},     //RES NORES,BI_debugGraphicsOption,NORES,NORES,NORES,NORES
      {"doubleBuffered",         "use offscreen painting buffer", "", "", null, "true"},     //RES NORES,BI_doubleBuffered,NORES,NORES,NORES
      {"maximumSize",            "maximum size", "", ""},     //RES NORES,BI_maxSize,NORES,NORES
      {"minimumSize",            "minimum size", "", ""},     //RES NORES,BI_minSize,NORES,NORES
      {"nextFocusableComponent", "next component to receive focus", "", ""},     //RES NORES,BI_nextFocusComponent,NORES,NORES
      {"opaque",                 "opaque or transparent", "", "", null, "true"},     //RES NORES,BI_opaque,NORES,NORES,NORES
      {"preferredSize",          "preferred size", "", ""},     //RES NORES,BI_prefSize,NORES,NORES
      {"requestFocusEnabled",    "can obtain focus by calling requestFocus()", "", "", null, "true"},     //RES NORES,BI_reqFocusEnabled,NORES,NORES,NORES
      {"toolTipText",            "text to display in tool tip", "", "", null, "true", "true"},     //RES NORES,BI_toolTipText,NORES,NORES,NORES,NORES


    };
     /**
     * Additional attributes for each property described in the
     * propertyDescriptors array.  Entries in the
     * propertyDescriptorAttributes array and the propertyDescriptors
     * array are matched by index position in each array.  Use a null
     * value as a placeholder for property entries without attributes.
     * The propertyDescriptorAttributes array need not be the same size
     * as the propertyDescriptors array.
     *
     * Format:  {{"AttributeName", "AttributeValueAsString"}, ...}
     * Example: {null,
     *           {"enumerationValues", "LEFT, 2, SwingConstants.LEFT, CENTER, 0, SwingConstants.CENTER"},}
     */
     propertyDescriptorAttributes =  new Object[][]  {
       {"enumerationValues", new Object[] {"AS_NEEDED",new Integer(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED",
                             "NEVER", new Integer(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), "JScrollPane.HORIZONTAL_SCROLLBAR_NEVER",
                             "ALWAYS", new Integer(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),"JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS"}},
       {"enumerationValues", new Object[] {"AS_NEEDED",new Integer(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED), "JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED",
                             "NEVER", new Integer(JScrollPane.VERTICAL_SCROLLBAR_NEVER), "JScrollPane.VERTICAL_SCROLLBAR_NEVER",
                             "ALWAYS", new Integer(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS),"JScrollPane.VERTICAL_SCROLLBAR_ALWAYS"}},
     };
  }
}
