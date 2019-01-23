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

import com.borland.jb.util.*;

/**
 * <p>BeanInfo for <code>RepeatButton</code>.</p>
 */


public class RepeatButtonBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public RepeatButtonBeanInfo() {
    beanClass = RepeatButton.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}

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
      {"name",                   "name", "", "", null, "false", "true"},
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

      // javax.swing.AbstractButton properties
      {"actionCommand",          "action command string", "", ""},     //RES NORES,BI_AbsButton_actionCmd,NORES,NORES
      {"borderPainted",          "paint border", "", ""},     //RES NORES,BI_borderPainted,NORES,NORES
      {"contentAreaFilled",      "paint content area or leave transparent", "", "", null, "true"},     //RES NORES,BI_AbsButton_contentAreaFilled,NORES,NORES,NORES
      {"disabledIcon",           "icon for disabled state", "", ""},     //RES NORES,BI_AbsButton_disabledIcon,NORES,NORES
      {"disabledSelectedIcon",   "icon for selected, disabled state", "", ""},     //RES NORES,BI_AbsButton_selDisabledIcon,NORES,NORES
      {"focusPainted",           "paint visual focus state", "", ""},     //RES NORES,BI_AbsButton_focusPainted,NORES,NORES
      {"horizontalAlignment",    "horizontal alignment of icon and text", "", "", "com.borland.dbswing.editors.HorizontalAlignmentEditor"},     //RES NORES,BI_AbsButton_horzAlignment,NORES,NORES,NORES
      {"horizontalTextPosition", "horizontal position of text relative to icon", "", "", "com.borland.dbswing.editors.HorizontalAlignmentEditor"},     //RES NORES,BI_AbsButton_horzTextPosition,NORES,NORES,NORES
      {"icon",                   "default icon", "", ""},     //RES NORES,BI_AbsButton_icon,NORES,NORES
//      {"label",                  Res._BI_label, "", "", null, "true", "true"},
      {"margin",                 "space between border and text", "", ""},     //RES NORES,BI_AbsButton_margin,NORES,NORES
      {"mnemonic",               "activating mnemonic key", "", "", "com.borland.jbuilder.cmt.editors.MnemonicEditor", null, null, "int.class"},     //RES NORES,BI_AbsButton_mnemonic,NORES,NORES,NORES,NORES
      {"model",                  "model", "", "", null, "true"},     //RES NORES,BI_model,NORES,NORES,NORES
      {"pressedIcon",            "icon for pressed state", "", ""},     //RES NORES,BI_AbsButton_pressedIcon,NORES,NORES
      {"rolloverEnabled",        "enable visual rollover effects", "", ""},     //RES NORES,BI_AbsButton_rolloverEnabled,NORES,NORES
      {"rolloverIcon",           "icon for rollover state", "", ""},     //RES NORES,BI_AbsButton_rolloverIcon,NORES,NORES
      {"rolloverSelectedIcon",   "icon for selected, rollover state", "", ""},     //RES NORES,BI_AbsButton_rolloverSelIcon,NORES,NORES
      {"selected",               "selected state", "", ""},     //RES NORES,BI_AbsButton_selected,NORES,NORES
      {"selectedIcon",           "icon for selected state", "", ""},     //RES NORES,BI_AbsButton_selectedIcon,NORES,NORES
      {"text",                   "display text", "", ""},     //RES NORES,BI_AbsButton_text,NORES,NORES
//      {"UI",                     Res._BI_UI, "", "", null, "true", "true"},
      {"verticalAlignment",      "vertical alignment of icon and text", "", "", "com.borland.dbswing.editors.VerticalAlignmentEditor"},     //RES NORES,BI_AbsButton_vertAlignment,NORES,NORES,NORES
      {"verticalTextPosition",   "vertical position of text relative to icon", "", "", "com.borland.dbswing.editors.VerticalAlignmentEditor"},     //RES NORES,BI_AbsButton_vertTextPosition,NORES,NORES,NORES

      // javax.swing.JButton properties
      {"defaultCapable",         "can be default button", "", ""},     //RES NORES,BI_AbsButton_defaultCapable,NORES,NORES

      // com.borland.dbswing.RepeatButton properties
      {"defaultButton",          "is default button", "", ""},     //RES NORES,BI_AbsButton_defaultButton,NORES,NORES
      {"initialDelay",           "millisecond delay before initial ActionEvent", "", ""},     //RES NORES,BI_RepeatButton_initDelay,NORES,NORES
      {"repeat",                 "send repeated ActionEvents when held down", "", ""},     //RES NORES,BI_RepeatButton_repeat,NORES,NORES
      {"repeatDelay",            "millisecond interval between repeated ActionEvents", "", ""},     //RES NORES,BI_RepeatButton_repeatDelay,NORES,NORES
      {"textWithMnemonic",       "text with embedded mnemonic character (preceded by '&')", "", ""},     //RES NORES,BI_RepeatButton_textWithMnemonic,NORES,NORES
    };

//  Format:
//   {"EventSetName", "EventListenerClass", "AddMethod", "RemoveMethod", "Expert", "Hidden",}
    eventSetDescriptors = new String[][] {
     {"action", "java.awt.event.ActionListener", "", ""},
   };
  }

}
