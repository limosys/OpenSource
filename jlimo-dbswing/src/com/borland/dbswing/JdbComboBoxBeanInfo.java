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
 * <p>BeanInfo for <code>JdbCheckBox</code>.</p>
 */

public class JdbComboBoxBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public JdbComboBoxBeanInfo() {
    beanClass = JdbComboBox.class;
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

      // javax.swing.JComboxBox properties
      {"actionCommand",          "action command string", "", ""},     //RES NORES,BI_AbsButton_actionCmd,NORES,NORES
      {"editable",               "allow editing", "", ""},     //RES NORES,BI_JText_editable,NORES,NORES
      {"editor",                 "selection editor", "", "", null, "true"},     //RES NORES,BI_JCombo_editor,NORES,NORES,NORES
//      {"keySelectionManager",    Res._BI_JCombo_keySelMgr, "", "", "true", "true"},
      {"lightWeightPopupEnabled", "enable lightweight popup", "", "", null, "true"},     //RES NORES,BI_JCombo_lightPopup,NORES,NORES,NORES
      {"maximumRowCount",        "maximum visible rows", "", ""},     //RES NORES,BI_JCombo_maxRowCount,NORES,NORES
      {"model",                  "model", "", "", null, "true"},     //RES NORES,BI_model,NORES,NORES,NORES
      {"popupVisible",           "popup visible state", "", "", null, "true"},     //RES NORES,BI_JCombo_popupVisible,NORES,NORES,NORES
      {"renderer",               "drop down cell renderer", "", "", null, "true"},     //RES NORES,BI_JCombo_renderer,NORES,NORES,NORES
//      {"UI",                     Res._BI_UI, "", "", null, "true", "true"},

      // com.borland.dbswing.JdbComboBox properties
      {"columnName",             "DataSet Column name", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_dx_columnName,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
      {"dropDownWidth",          "width of drop down in pixels", "", ""},     //RES NORES,BI_JdbCombo_dropWidth,NORES,NORES
      {"fixedCellHeight",        "fixed height of drop down cell in pixels", "", ""},     //RES NORES,BI_JdbCombo_fixHeight,NORES,NORES
//      {"items",                  Res._BI_JdbList_items, "", ""},
      {"items",                  "strings to fill list", "", "", null, "false", "false", "string[]"},     //RES NORES,BI_JdbList_items,NORES,NORES,NORES,NORES,NORES
      {"selectedIndex",          "index of selected item", "", ""},     //RES NORES,BI_JCombo_selectedIndex,NORES,NORES
      {"selectedItem",           "selected item", "", ""},     //RES NORES,BI_JCombo_selectedItem,NORES,NORES
    };
  }
}
