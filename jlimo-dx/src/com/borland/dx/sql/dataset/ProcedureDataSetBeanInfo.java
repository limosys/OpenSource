//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ProcedureDataSetBeanInfo.java,v 7.0 2002/08/08 18:39:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.BasicBeanInfo;
import com.borland.dx.dataset.StorageDataSetBeanInfo;

/**
 * The ProcedureDataSetBeanInfo component provides explicit information about the ProcedureDataSet component to JBuilder (and other visual tools that support JavaBeans). It extends the BasicBeanInfo class.

The ProcedureDataSetBeanInfo component specifies the properties of the ProcedureDataSet that appear at design time in the Inspector and their access methods. It also identifies the custom property editor used to display and edit selected property values where applicable.

The ProcedureDataSetBeanInfo component does not expose the methods and events of the ProcedureDataSet, but specifies that these be extracted through introspection.
 */
public class ProcedureDataSetBeanInfo extends StorageDataSetBeanInfo
{
  /**
   * Constructs a ProcedureDataSetBeanInfo component.
   */
  public ProcedureDataSetBeanInfo() {
    beanClass = ProcedureDataSet.class;
    appendPropertyDescriptors(com.borland.dx.sql.dataset.cons.ProcedureDataSetStringBean.strings);
  }
}
