//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnBeanInfo.java,v 7.0 2002/08/08 18:39:18 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class ColumnBeanInfo extends BasicBeanInfo
{
  public ColumnBeanInfo() {
    beanClass = Column.class;
    propertyDescriptors = com.borland.dx.dataset.cons.ColumnStringBean.strings;
  }
}
