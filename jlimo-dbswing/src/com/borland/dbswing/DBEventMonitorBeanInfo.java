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
 * <p>BeanInfo for <code>DBEventMonitor</code>.</p>
 */

public class DBEventMonitorBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public DBEventMonitorBeanInfo() {
    beanClass = DBEventMonitor.class;
    propertyDescriptors = new String[][] {
//  Format:
//    {"PropertyName", "Short Description", "ReadMethod", "WriteMethod", "PropertyEditor", "Expert", "Hidden"}
//      {"dataSets",         Res._BI_JdbEventMon_dataSets, "", "", null, "true", "true"},
      {"dataAwareComponentContainer",  "Container of DataSets to monitor for events", "", ""},     //RES NORES,BI_DBEventMon_dataSetContainer,NORES,NORES
      {"enableAccessListener",  "enable monitoring of Access events", "", ""},     //RES NORES,BI_DBEventMon_enableAccess,NORES,NORES
      {"enableCalcAggFieldsListener",  "enable monitoring of CalcAggFields events", "", ""},     //RES NORES,BI_DBEventMon_enableCalcAggFields,NORES,NORES
      {"enableCalcFieldsListener",  "enable monitoring of CalcFields events", "", ""},     //RES NORES,BI_DBEventMon_enableCalcFields,NORES,NORES
      {"enableColumnChangeListener",  "enable monitoring of ColumnChange events", "", ""},     //RES NORES,BI_DBEventMon_enableColumnChange,NORES,NORES
      {"enableColumnPaintListener",  "enable monitoring of ColumnPaint events", "", "", null, "true"},     //RES NORES,BI_DBEventMon_enableColumnPaint,NORES,NORES,NORES
      {"enableDataChangeListener",  "enable monitoring of DataChange events", "", ""},     //RES NORES,BI_DBEventMon_enableDataChange,NORES,NORES
      {"enableEditListener",  "enable monitoring of Edit events", "", ""},     //RES NORES,BI_DBEventMon_enableEdit,NORES,NORES
      {"enableLoadListener",  "enable monitoring of Load events", "", ""},     //RES NORES,BI_DBEventMon_enableLoad,NORES,NORES
      {"enableNavigationListener",  "enable monitoring of Navigation events", "", ""},     //RES NORES,BI_DBEventMon_enableNavigation,NORES,NORES
      {"enableOpenListener",  "enable monitoring of Open events", "", ""},     //RES NORES,BI_DBEventMon_enableOpen,NORES,NORES
      {"enableResolverListener",  "enable monitoring of Resolver events", "", ""},     //RES NORES,BI_DBEventMon_enableResolver,NORES,NORES
      {"enableRowFilterListener",  "enable monitoring of RowFilter events", "", ""},     //RES NORES,BI_DBEventMon_enableRowFilter,NORES,NORES
      {"enableStatusListener",  "enable monitoring of Status events", "", ""},     //RES NORES,BI_DBEventMon_enableStatus,NORES,NORES
//      {"dataSetAwareComponents",         Res._BI_DBEventMon_dataSetAwareComps, "", "", null, "true"},
//      {"printStream",      Res._BI_DBEventMon_printStream, "", "", null, "true", "true"},
    };
  }
}
