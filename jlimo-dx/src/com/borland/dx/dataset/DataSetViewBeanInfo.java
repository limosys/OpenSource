//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataSetViewBeanInfo.java,v 7.0 2002/08/08 18:39:23 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class DataSetViewBeanInfo extends BasicBeanInfo
{
  public DataSetViewBeanInfo() {
    beanClass = DataSetView.class;
    propertyDescriptors = com.borland.dx.dataset.cons.DataSetViewStringBean.strings;
  }
}
