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
 * <p>BeanInfo for <code>JdbNavToolBar</code>.</p>
 */

public class JdbNavToolBarBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public JdbNavToolBarBeanInfo() {
    beanClass = JdbNavToolBar.class;

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

      // javax.swing.JToolBar
      {"borderPainted",           "paint border", "", ""},     //RES NORES,BI_borderPainted,NORES,NORES
      {"floatable",              "toolbar is detachable", "", ""},     //RES NORES,BI_JToolBar_floatable,NORES,NORES
      {"margin",                 "space between border and text", "", ""},     //RES NORES,BI_AbsButton_margin,NORES,NORES
      {"orientation",            "vertical or horizontal toolbar", "", "", "com.borland.dbswing.editors.OrientationEditor"},     //RES NORES,BI_JToolBar_orientation,NORES,NORES,NORES
//      {"UI",                     Res._BI_UI, "", "", null, "true", "true"},

      // com.borland.dbswing.JdbNavToolBar
      {"alignment",              "alignment", "", "", "com.borland.dbswing.editors.FlowLayoutAlignmentEditor", "true"},     //RES NORES,BI_JdbNavTool_alignment,NORES,NORES,NORES,NORES
      {"autoDetect",             "automatically detect DataSets", "", ""},     //RES NORES,BI_JdbStatus_autoDetect,NORES,NORES
      {"buttonStateFirst",       "first button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateFirst,NORES,NORES,NORES
      {"buttonStatePrior",       "prior button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_statePrior,NORES,NORES,NORES
      {"buttonStateNext",        "next button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateNext,NORES,NORES,NORES
      {"buttonStateLast",        "last button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateLast,NORES,NORES,NORES
      {"buttonStateInsert",      "insert button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateInsert,NORES,NORES,NORES
      {"buttonStateDelete",      "delete button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateDelete,NORES,NORES,NORES
      {"buttonStatePost",        "post button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_statePost,NORES,NORES,NORES
      {"buttonStateCancel",      "cancel button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateCancel,NORES,NORES,NORES
      {"buttonStateDitto",       "ditto button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateDitto,NORES,NORES,NORES
      {"buttonStateSave",        "save button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateSave,NORES,NORES,NORES
      {"buttonStateRefresh",     "refresh button state", "", "", "com.borland.dbswing.editors.NavButtonStateEditor"},     //RES NORES,BI_JdbNavTool_stateRefresh,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
//      {"dataSetAwareComponents", Res._BI_JdbStatus_dataSetAwareComps, "", "", null, "true", "true"},
      {"focusedDataSet",         "currently focused DataSet", "", ""},     //RES NORES,BI_JdbStatus_focusedDataSet,NORES,NORES
      {"showRollover",           "display button rollover", "", ""},     //RES NORES,BI_JdbNavTool_showRollover,NORES,NORES
      {"showTooltips",           "display button tooltips", "", ""},     //RES NORES,BI_JdbNavTool_showTooltips,NORES,NORES
    };
  }
}
