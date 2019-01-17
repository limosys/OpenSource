//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryDataSetBeanInfo.java,v 7.0 2002/08/08 18:39:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.BasicBeanInfo;

/**
 * The <CODE>QueryDataSetBeanInfo</CODE> component provides explicit
 * information about the
{@link com.borland.dx.sql.dataset.QueryDataSet}
<CODE>QueryDataSet</CODE></A> component to JBuilder
 (and other visual tools that support JavaBeans). It extends the
{@link com.borland.jb.util.BasicBeanInfo} <CODE>BasicBeanInfo</CODE></A> class.

<P>
The <CODE>QueryDataSetBeanInfo</CODE> component specifies the properties
* of the <CODE>QueryDataSet</CODE> that appear at design time in the Inspector
* and their access methods. It also identifies the custom property editor used
*  to display and edit selected  property values where applicable.

<P>
The <CODE>QueryDataSetBeanInfo</CODE> component does not expose the methods
* and events of the <CODE>QueryDataSet</CODE>, but specifies that these be
*  extracted through introspection.

 */
public class QueryDataSetBeanInfo extends com.borland.dx.dataset.StorageDataSetBeanInfo
{
  /**
   * Constructs a QueryDataSetBeanInfo component.
   */public QueryDataSetBeanInfo() {
    super();
    beanClass = QueryDataSet.class;
    appendPropertyDescriptors(com.borland.dx.sql.dataset.cons.QueryDataSetStringBean.strings);
    // This is why certain properties are not visible in the designers:
    //   columns:  We only want programmatic access
  }
}
