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
 * <p>BeanInfo for <code>DBTreeDataBinder</code>.</p>
 */

public class DBTreeDataBinderBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public DBTreeDataBinderBeanInfo() {
    beanClass = DBTreeDataBinder.class;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
      {"columnName",             "DataSet Column name", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_dx_columnName,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
      {"jTree",                  "JTree or subclass of JTree to bind to data", "", ""},     //RES NORES,BI_DBTreeDB_jTree,NORES,NORES
//      {"treeModel",              Res._BI_DBTreeDB_treeModel, "", "", null, "true", "true"},
//      {"treeSelectionModel",     Res._BI_DBTreeDB_treeSelectionModel, "", "", null, "true", "true"},
      {"unknownDataValueMode",   "policy for indicating inconsistent values", "", "", "com.borland.dbswing.editors.UnknownDataModeEditor"},     //RES NORES,BI_DBButtonDB_unkMode,NORES,NORES,NORES
      {"useLeafNodesOnly",       "Ignore non-leaf nodes when searching and updating values", "", "", },     //RES NORES,BI_DBTreeDB_leafNodes,NORES,NORES
    };
  }
}
