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
 * <p>BeanInfo for <code>DBTextDataBinder</code>.</p>
 */

public class DBTextDataBinderBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public DBTextDataBinderBeanInfo() {
    beanClass = DBTextDataBinder.class;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
      {"columnName",             "DataSet Column name", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_dx_columnName,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
//      {"document",             Res._BI_JText_document, "", "", null, "true", "true"},
      {"enablePopupMenu",        "enable popup menu", "", ""},     //RES NORES,BI_DBTextDB_enableMenu,NORES,NORES
      {"jTextComponent",         "subclass of JTextComponent (e.g., JTextField) to bind to data", "", ""},     //RES NORES,BI_DBTextDB_jTextComp,NORES,NORES
      {"postOnFocusLost",        "save column value on focus lost event", "", ""},     //RES NORES,BI_DBTextDB_postFocus,NORES,NORES
      {"postOnRowPosted",        "save column value on row posted event", "", ""},     //RES NORES,BI_DBTextDB_postRowPost,NORES,NORES
      {"columnNameURL",          "column name for HTML page URLs", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_DBTextDB_colNameURL,NORES,NORES,NORES
      {"enableClearAll",         "display Clear All menu selection", "", ""},     //RES NORES,BI_DBTextDB_enableClearAll,NORES,NORES
      {"enableUndoRedo",         "display Undo/Redo menu selections", "", ""},     //RES NORES,BI_DBTextDB_enableUndoRedo,NORES,NORES
      {"enableColorChange",      "display Foreground and Background color menu selection", "", ""},     //RES NORES,BI_DBTextDB_colorChange,NORES,NORES
      {"enableFontChange",       "display Font menu selection", "", ""},     //RES NORES,BI_DBTextDB_fontChange,NORES,NORES
      {"enableFileLoading",      "display file Open menu selection", "", ""},     //RES NORES,BI_DBTextDB_fileLoad,NORES,NORES
      {"enableFileSaving",       "display file Save menu selection", "", ""},     //RES NORES,BI_DBTextDB_fileSave,NORES,NORES
      {"enableURLLoading",       "display Open URL menu selection", "", ""},     //RES NORES,BI_DBTextDB_enableURLLoad,NORES,NORES
      {"enableURLAutoCache",     "cache URL names and content in DataSet", "", ""},     //RES NORES,BI_DBTextDB_enableCache,NORES,NORES
      {"nextFocusOnEnter",       "focus next component on Enter key", "", ""},     //RES NORES,BI_DBTextDB_nextFocusEnter,NORES,NORES
    };
  }
}
