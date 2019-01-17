//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/DatabaseBeanInfo.java,v 7.0 2002/08/08 18:39:50 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.BasicBeanInfo;

/**
 *
*
*The <CODE>DatabaseBeanInfo</CODE> component provides explicit information about the
*{@link com.borland.dx.sql.dataset.Database} <CODE>Database</CODE></A> component to JBuilder
* (and other visual tools that support JavaBeans). It extends the
*{@link com.borland.jb.util.BasicBeanInfo} <CODE>BasicBeanInfo</CODE></A> class.
*
*<!-- JDS start - remove paragraph? -->
*<P>
*The <CODE>DatabaseBeanInfo</CODE> component specifies the properties of the <CODE>Database</CODE> that appear at design time in the Inspector and their access methods. It also identifies the custom property editor used to display and edit selected  property values where applicable.
*<!-- JDS end -->
*
*<P>
*The <CODE>DatabaseBeanInfo</CODE> component does not expose the methods and events of the <CODE>Database</CODE>, but specifies that these be extracted through introspection.
 */
public class DatabaseBeanInfo extends BasicBeanInfo
{
  /**
   * Constructs a DatabaseBeanInfo component.
   */
  public DatabaseBeanInfo() {
    beanClass = Database.class;
    propertyDescriptors = com.borland.dx.sql.dataset.cons.DatabaseStringBean.strings;
  }
}
