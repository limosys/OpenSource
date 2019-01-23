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
 * <p>BeanInfo for <code>JdbNavTree</code>.</p>
 */

public class JdbNavTreeBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public JdbNavTreeBeanInfo() {
    beanClass = JdbNavTree.class;
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

      // javax.swing.JTree
      {"cellEditor",             "tree cell editor", "", "", null, "true"},     //RES NORES,BI_JTree_cellEditor,NORES,NORES,NORES
      {"cellRenderer",           "tree cell renderer", "", "", null, "true"},     //RES NORES,BI_JTree_cellRenderer,NORES,NORES,NORES
      {"invokesStopCellEditing", "stops cell editing", "", "", null, "true", "true"},     //RES NORES,BI_JTree_stopCellEdit,NORES,NORES,NORES,NORES
      {"largeModel",             "large tree model", "", "", null, "true", "true"},     //RES NORES,BI_JTree_largeModel,NORES,NORES,NORES,NORES
      {"rootVisible",            "display tree root node", "", ""},     //RES NORES,BI_JTree_rootVisible,NORES,NORES
      {"rowHeight",              "tree row height", "", ""},     //RES NORES,BI_JTree_rowHeight,NORES,NORES
      {"scrollsOnExpand",        "scroll on node expansion", "", ""},     //RES NORES,BI_JTree_scrollExpand,NORES,NORES
      {"selectionModel",         "tree selection model", "", "", null, "true"},     //RES NORES,BI_JTree_selectionModel,NORES,NORES,NORES
      {"selectionPath",          "tree selection path", "", "", null, "true", "true"},     //RES NORES,BI_JTree_selectionPath,NORES,NORES,NORES,NORES
      {"selectionPaths",         "tree selection paths", "", "", null, "true", "true"},     //RES NORES,BI_JTree_selectionPaths,NORES,NORES,NORES,NORES
      {"selectionRows",          "tree selection rows", "", "", null, "true", "true"},     //RES NORES,BI_JTree_selectionRows,NORES,NORES,NORES,NORES
      {"showsRootHandles",       "display root handles", "", ""},     //RES NORES,BI_JTree_showRootHandles,NORES,NORES
      {"visibleRowCount",        "visible rows", "", "", null, "true", "true"},     //RES NORES,BI_JList_visibleRows,NORES,NORES,NORES,NORES

      // com.borland.dbswing.JdbNavTree
      {"columnName",             "DataSet Column name", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_dx_columnName,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
      {"useLeafNodesOnly",       "Ignore non-leaf nodes when searching and updating values", "", "", },     //RES NORES,BI_DBTreeDB_leafNodes,NORES,NORES

    };
  }
}
