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
 * <p>BeanInfo for <code>DBButtonDataBinder</code>.</p>
 */

public class DBButtonDataBinderBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public DBButtonDataBinderBeanInfo() {
    beanClass = DBButtonDataBinder.class;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
      {"abstractButton",    "subclass of AbstractButton (e.g. JRadioButton) to bind to data", "", ""},     //RES NORES,BI_DBButtonDB_absButton,NORES,NORES
//      {"buttonModel",    Res._BI_DBButtonDB_buttonModel, "", "", null, "true", "true"},
      {"columnName",             "DataSet Column name", "", "", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"},     //RES NORES,BI_dx_columnName,NORES,NORES,NORES
      {"dataSet",                "DataSet data source", "", ""},     //RES NORES,BI_dx_dataSet,NORES,NORES
      {"selectedDataValue",      "value written to DataSet in selected state", "", ""},     //RES NORES,BI_DBButtonDB_selectedVal,NORES,NORES
      {"unknownDataValueMode",   "policy for indicating inconsistent values", "", "", "com.borland.dbswing.editors.UnknownDataModeEditor"},     //RES NORES,BI_DBButtonDB_unkMode,NORES,NORES,NORES
      {"unselectedDataValue",    "value written to DataSet in unselected state", "", ""},     //RES NORES,BI_DBButtonDB_unselectedVal,NORES,NORES
    };
  }
}
