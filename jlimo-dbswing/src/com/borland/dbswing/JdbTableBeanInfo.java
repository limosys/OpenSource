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
 * <p>BeanInfo for <code>JdbTable</code>.</p>
 */


public class JdbTableBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public JdbTableBeanInfo() {
    beanClass = JdbTable.class;
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
      {"preferredSize",          "preferred size", "", "", null, "true", "true"},     //RES NORES,BI_prefSize,NORES,NORES,NORES,NORES
      {"requestFocusEnabled",    "can obtain focus by calling requestFocus()", "", "", null, "true"},     //RES NORES,BI_reqFocusEnabled,NORES,NORES,NORES
      {"toolTipText",            "text to display in tool tip", "", ""},     //RES NORES,BI_toolTipText,NORES,NORES

      // javax.swing.JTable
      {"autoCreateColumnsFromModel", "automatically create table column model", "", "", null, "true"},     //RES NORES,BI_JTable_autoCreateCols,NORES,NORES,NORES
      {"autoResizeMode",         "automatic column resizing policy", "", "", "com.borland.dbswing.editors.AutoResizeModeEditor"},     //RES NORES,BI_JTable_autoResizeMode,NORES,NORES,NORES
      {"cellEditor",             "current table cell editor", "", "", null, "true", "true"},     //RES NORES,BI_JTable_cellEditor,NORES,NORES,NORES,NORES
      {"cellSelectionEnabled",   "allow single cell selection", "", ""},     //RES NORES,BI_JTable_cellSelEnabled,NORES,NORES
      {"columnModel",            "table column model", "", "", null, "true"},     //RES NORES,BI_JTable_columnModel,NORES,NORES,NORES
      {"columnSelectionAllowed", "allow columns to be selected", "", ""},     //RES NORES,BI_JTable_colSelAllowed,NORES,NORES
//      {"editingColumn",          Res._BI_JTable_editingCol, "", "", null, "true", "true"},
//      {"editingRow",             Res._BI_JTable_editingRow, "", "", null, "true", "true"},
      {"gridColor",              "grid line color", "", ""},     //RES NORES,BI_JTable_gridColor,NORES,NORES
      {"intercellSpacing",       "margin between cells", "", ""},     //RES NORES,BI_JTable_intercellSpace,NORES,NORES
      {"model",                  "table model", "", "", null, "true", "true"},     //RES NORES,BI_JTable_model,NORES,NORES,NORES,NORES
//      {"preferredScrollableViewportSize",  Res._BI_JTable_prefViewportSize, "", "", null, "true", "true"},
      {"rowHeight",              "height of each row", "", ""},     //RES NORES,BI_JTable_rowHeight,NORES,NORES
      {"rowMargin",              "margin between rows", "", ""},     //RES NORES,BI_JTable_rowMargin,NORES,NORES
      {"rowSelectionAllowed",    "allow rows to be selected", "", ""},     //RES NORES,BI_JTable_rowSelAllowed,NORES,NORES
      {"selectionBackground",    "selection background color", "", ""},     //RES NORES,BI_JList_selBackground,NORES,NORES
      {"selectionForeground",    "selection foreground color", "", ""},     //RES NORES,BI_JList_selForeground,NORES,NORES
      {"selectionModel",         "table's row selection model", "", "", null, "true"},     //RES NORES,BI_JTable_selectionModel,NORES,NORES,NORES
      {"showHorizontalLines",    "show horizontal grid lines", "", ""},     //RES NORES,BI_JTable_showHorzLines,NORES,NORES
      {"showVerticalLines",      "show vertical grid lines", "", ""},     //RES NORES,BI_JTable_showVertLines,NORES,NORES
      {"tableHeader",            "table column header", "", "", null, "true"},     //RES NORES,BI_JTable_tableHeader,NORES,NORES,NORES
//      {"UI",                     Res._BI_UI, "", "", null, "true", "true"},

      // com.borland.dbswing.JdbTable
      {"columnHeaderVisible",    "display column header", "", ""},     //RES NORES,BI_JdbTable_colHeaderVisible,NORES,NORES
      {"columnSortEnabled",      "allow sort by clicking on column header", "", ""},     //RES NORES,BI_JdbTable_colSortEnabled,NORES,NORES
//      {"customizedColumns",      Res._BI_JdbTable_customCols, "", ""},
      {"customColumns",      "customized columns", "", ""},     //RES NORES,BI_JdbTable_customCols,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
      {"hiddenColumns",          "hidden columns", "", ""},     //RES NORES,BI_JdbTable_hiddenCols,NORES,NORES
      {"popupMenuEnabled",       "enable right-click popup menu", "", ""},     //RES NORES,BI_JdbTable_popmenuEnabled,NORES,NORES
      {"rowHeader",              "table row header", "", "", null, "true"},     //RES NORES,BI_JdbTable_rowHeader,NORES,NORES,NORES
      {"rowHeaderVisible",       "display row header", "", ""},     //RES NORES,BI_JdbTable_rowHeaderVisible,NORES,NORES
      {"smartColumnWidths",      "set default width of columns by data type", "", ""},     //RES NORES,BI_JdbTable_smartColWidths,NORES,NORES
      {"autoSelection",          "automatically set an initially selected row", "", ""},     //RES NORES,BI_JdbTable_autoSelection,NORES,NORES
      {"editable",               "allow edits to the table", "", ""},     //RES NORES,BI_JdbTable_editable,NORES,NORES
      {"editableFocusedCellForeground", "editable, focused cell foregound color", "", ""},     //RES NORES,BI_JdbTable_editFocusCellFore,NORES,NORES
      {"editableFocusedCellBackground", "editable, focused cell background color", "", ""},     //RES NORES,BI_JdbTable_editFocusCellBack,NORES,NORES
    };
  }
}


