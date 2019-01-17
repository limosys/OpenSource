//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ProcedureResolverBeanInfo.java,v 7.0 2002/08/08 18:39:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.BasicBeanInfo;

/**
 * The <CODE>ProcedureResolverBeanInfo</CODE> component provides explicit information about the
{@link com.borland.dx.sql.dataset.ProcedureResolver}
<CODE>ProcedureResolver</CODE></A> component to JBuilder
 (and other visual tools that support JavaBeans). It extends the
{@link com.borland.jb.util.BasicBeanInfo} <CODE>BasicBeanInfo</CODE></A> class.

<P>
The <CODE>ProcedureResolverBeanInfo</CODE> component specifies the properties of the <CODE>ProcedureResolver</CODE> that appear at design time in the Inspector and their access methods. It also identifies the custom property editor used to display and edit selected  property values where applicable.

<P>
The <CODE>ProcedureResolverBeanInfo</CODE> component does not expose the methods and events of the <CODE>ProcedureResolver</CODE>, but specifies that these be extracted through introspection.

 */
public class ProcedureResolverBeanInfo extends BasicBeanInfo
{
  /**
   * Constructs a ProcedureResolverBeanInfo component.
   */
  public ProcedureResolverBeanInfo() {
    beanClass = ProcedureResolver.class;
    propertyDescriptors = com.borland.dx.sql.dataset.cons.ProcedureResolverStringBean.strings;
  }
}
