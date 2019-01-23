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
 * <p>BeanInfo for <code>JdbStatusLabel</code>.</p>
 */

public class JdbStatusLabelBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public JdbStatusLabelBeanInfo() {
    beanClass = JdbStatusLabel.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}

      // java.awt.Component properties
      {"background",             "background color", "", ""},     //RES NORES,BI_background,NORES,NORES
//      {"bounds",                 Res._BI_bounds, "", ""},
//      {"componentOrientation",         Res._BI_compOrientation, "", "", null, "true", "true"},
//      {"cursor",               Res._BI_cursor, "", "", null, "true", "true"},
//      {"dropTarget",             Res._BI_dropTarget, "", "", null, "true", "true"},
      {"enabled",                "enable or disable component", "", ""},     //RES NORES,BI_enabled,NORES,NORES
      {"font",                   "font used for painting", "", ""},     //RES NORES,BI_font,NORES,NORES
      {"foreground",             "foreground color", "", ""},     //RES NORES,BI_foreground,NORES,NORES
      {"locale",                 "locale", "", "", null, "true"},     //RES NORES,BI_locale,NORES,NORES,NORES
//      {"location",                 Res._BI_location, "", "", null, "true"},
//      {"name",                 Res._BI_name, "", "", null, "true"},
//      {"size",                 Res._BI_size, "", "", null, "true"},
      {"visible",                "visibility state", "", "",},     //RES NORES,BI_visible,NORES,NORES

      // java.awt.Container properties
//      {"layout",                 Res._BI_layout, "", "", null, "true", "true"},

      // javax.swing.JComponent properties
      {"alignmentX",             "preferred horizontal alignment", "", "", null, "true"},     //RES NORES,BI_alignmentX,NORES,NORES,NORES
      {"alignmentY",             "preferred vertical alignment", "", "", null, "true"},     //RES NORES,BI_alignmentY,NORES,NORES,NORES
      {"autoscrolls",            "automatically scrolls contents when dragged", "", "", null, "true"},     //RES NORES,BI_autoscrolls,NORES,NORES,NORES
      {"border",                 "border (insets)", "", ""},     //RES NORES,BI_border,NORES,NORES
      {"debugGraphicsOptions",   "diagnostic options for debugging graphics", "", "", "com.borland.dbswing.editors.DebugGraphicsEditor", "true"},     //RES NORES,BI_debugGraphicsOption,NORES,NORES,NORES,NORES
      {"doubleBuffered",         "use offscreen painting buffer", "", "", null, "true"},     //RES NORES,BI_doubleBuffered,NORES,NORES,NORES
      {"maximumSize",            "maximum size", "", ""},     //RES NORES,BI_maxSize,NORES,NORES
      {"minimumSize",            "minimum size", "", ""},     //RES NORES,BI_minSize,NORES,NORES
      {"nextFocusableComponent", "next component to receive focus", "", ""},     //RES NORES,BI_nextFocusComponent,NORES,NORES
      {"opaque",                 "opaque or transparent", "", "", null, "true"},     //RES NORES,BI_opaque,NORES,NORES,NORES
      {"preferredSize",          "preferred size", "", ""},     //RES NORES,BI_prefSize,NORES,NORES
      {"requestFocusEnabled",    "can obtain focus by calling requestFocus()", "", "", null, "true"},     //RES NORES,BI_reqFocusEnabled,NORES,NORES,NORES
      {"toolTipText",            "text to display in tool tip", "", ""},     //RES NORES,BI_toolTipText,NORES,NORES

      // javax.swing.JLabel properties
      {"disabledIcon",           "icon for disabled state", "", ""},     //RES NORES,BI_AbsButton_disabledIcon,NORES,NORES
      {"displayedMnemonic",      "displayed mnemonic key", "", "", "com.borland.jbuilder.cmt.editors.MnemonicEditor", null, null, "int.class"},     //RES NORES,BI_JLabel_dispMnemonic,NORES,NORES,NORES,NORES
      {"horizontalAlignment",    "horizontal alignment of icon and text", "", "", "com.borland.dbswing.editors.HorizontalAlignmentEditor"},     //RES NORES,BI_AbsButton_horzAlignment,NORES,NORES,NORES
      {"horizontalTextPosition", "horizontal position of text relative to icon", "", "", "com.borland.dbswing.editors.HorizontalAlignmentEditor"},     //RES NORES,BI_AbsButton_horzTextPosition,NORES,NORES,NORES
      {"icon",                   "default icon", "", ""},     //RES NORES,BI_AbsButton_icon,NORES,NORES
      {"iconTextGap",            "pixel gap between icon and text", "", ""},     //RES NORES,BI_JLabel_iconTextGap,NORES,NORES
      {"labelFor",               "component focused on mnemonic keypress", "", ""},     //RES NORES,BI_JLabel_labelFor,NORES,NORES
//      {"UI",                     Res._BI_UI, "", "", null, "true", "true"},
      {"text",                   "label's text", "", ""},     //RES NORES,BI_JLabel_text,NORES,NORES
      {"verticalAlignment",      "vertical alignment of icon and text", "", "", "com.borland.dbswing.editors.VerticalAlignmentEditor"},     //RES NORES,BI_AbsButton_vertAlignment,NORES,NORES,NORES
      {"verticalTextPosition",   "vertical position of text relative to icon", "", "", "com.borland.dbswing.editors.VerticalAlignmentEditor"},     //RES NORES,BI_AbsButton_vertTextPosition,NORES,NORES,NORES

      // com.borland.dbswing.JdbStatusLabel
      {"autoDetect",             "automatically detect DataSets", "", ""},     //RES NORES,BI_JdbStatus_autoDetect,NORES,NORES
      {"dataSet",                "single DataSet to monitor for status events", "", ""},     //RES NORES,BI_JdbStatus_dataSet,NORES,NORES
//      {"dataSetAwareComponents", Res._BI_JdbStatus_dataSetAwareComps, "", "", null, "true", "true"},
      {"displayMessages",        "display DataSet status messages", "", ""},     //RES NORES,BI_JdbStatus_displayMsgs,NORES,NORES
      {"focusedDataSet",         "currently focused DataSet", "", ""},     //RES NORES,BI_JdbStatus_focusedDataSet,NORES,NORES
    };
  }
}
