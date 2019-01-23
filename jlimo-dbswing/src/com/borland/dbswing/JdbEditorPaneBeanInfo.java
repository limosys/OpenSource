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
 * <p>BeanInfo for <code>JdbEditorPane</code>.</p>
 */

public class JdbEditorPaneBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public JdbEditorPaneBeanInfo() {
    beanClass = JdbEditorPane.class;
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

      // javax.swing.text.JTextComponent
      {"caret",                  "caret", "", "", null, "true", "true"},     //RES NORES,BI_JText_caret,NORES,NORES,NORES,NORES
      {"caretColor",             "color of text insertion caret", "", ""},     //RES NORES,BI_JText_caretColor,NORES,NORES
      {"caretPosition",          "position of text insertion caret", "", ""},     //RES NORES,BI_JText_caretPos,NORES,NORES
      {"disabledTextColor",      "color of disabled text", "", ""},     //RES NORES,BI_JText_disabledTxtColor,NORES,NORES
      {"document",               "document model", "", "", null, "true"},     //RES NORES,BI_JText_document,NORES,NORES,NORES
      {"editable",               "allow editing", "", ""},     //RES NORES,BI_JText_editable,NORES,NORES
      {"focusAccelerator",       "mnemonic key", "", ""},     //RES NORES,BI_JText_focusAccelerator,NORES,NORES
      {"highlighter",            "highlighter", "", "", null, "true", "true"},     //RES NORES,BI_JText_highlighter,NORES,NORES,NORES,NORES
      {"keymap",                 "keymap", "", "", null, "true", "true"},     //RES NORES,BI_JText_keymap,NORES,NORES,NORES,NORES
      {"margin",                 "space between border and text", "", ""},     //RES NORES,BI_AbsButton_margin,NORES,NORES
      {"selectedTextColor",      "color of selected text", "", ""},     //RES NORES,BI_JText_selTextColor,NORES,NORES
      {"selectionColor",         "color of selection", "", ""},     //RES NORES,BI_JText_selColor,NORES,NORES
      {"selectionEnd",           "last selected character position", "", ""},     //RES NORES,BI_JText_selEnd,NORES,NORES
      {"selectionStart",         "first selected character position", "", ""},     //RES NORES,BI_JText_selStart,NORES,NORES
      {"text",                   "display text", "", ""},     //RES NORES,BI_AbsButton_text,NORES,NORES
//      {"UI",                     Res._BI_UI, "", "", null, "true", "true"},

      // javax.swing.JEditorPane
      {"contentType",            "MIME content type", "", "", null, "true", "true"},     //RES NORES,BI_JText_contentType,NORES,NORES,NORES,NORES
      {"editorKit",              "editor kit", "", "", null, "true", "true"},     //RES NORES,BI_JText_editorKit,NORES,NORES,NORES,NORES
//      {"page",                   Res._BI_JText_page, "", "", null, null, null, "java.net.URL"},

      // com.borland.dbswing.JdbTextDataBinder
      {"columnName",             "DataSet Column name", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_dx_columnName,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
      {"enablePopupMenu",        "enable popup menu", "", ""},     //RES NORES,BI_DBTextDB_enableMenu,NORES,NORES
//      {"jTextComponent",         Res._BI_JdbText_textComp, "", ""},
      {"postOnFocusLost",        "save column value on focus lost event", "", ""},     //RES NORES,BI_DBTextDB_postFocus,NORES,NORES
      {"postOnRowPosted",        "save column value on row posted event", "", ""},     //RES NORES,BI_DBTextDB_postRowPost,NORES,NORES
      {"enableClearAll",         "display Clear All menu selection", "", ""},     //RES NORES,BI_DBTextDB_enableClearAll,NORES,NORES
      {"enableUndoRedo",         "display Undo/Redo menu selections", "", ""},     //RES NORES,BI_DBTextDB_enableUndoRedo,NORES,NORES
      {"enableFileLoading",      "display file Open menu selection", "", ""},     //RES NORES,BI_DBTextDB_fileLoad,NORES,NORES
      {"enableFileSaving",       "display file Save menu selection", "", ""},     //RES NORES,BI_DBTextDB_fileSave,NORES,NORES
      {"enableColorChange",      "display Foreground and Background color menu selection", "", ""},     //RES NORES,BI_DBTextDB_colorChange,NORES,NORES
      {"enableFontChange",       "display Font menu selection", "", ""},     //RES NORES,BI_DBTextDB_fontChange,NORES,NORES
      {"columnNameURL",          "column name for HTML page URLs", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_DBTextDB_colNameURL,NORES,NORES,NORES
      {"pageURL",                "HTML page (URL)", "", ""},     //RES NORES,BI_JText_page,NORES,NORES
      {"enableURLLoading",       "display Open URL menu selection", "", ""},     //RES NORES,BI_DBTextDB_enableURLLoad,NORES,NORES
      {"enableURLAutoCache",     "cache URL names and content in DataSet", "", ""},     //RES NORES,BI_DBTextDB_enableCache,NORES,NORES

      // com.borland.dbswing.JdbEditorPane

    };
  }
}


