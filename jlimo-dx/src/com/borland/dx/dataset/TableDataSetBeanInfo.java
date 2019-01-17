//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/TableDataSetBeanInfo.java,v 7.0 2002/08/08 18:39:37 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class TableDataSetBeanInfo extends StorageDataSetBeanInfo
{
  public TableDataSetBeanInfo() {
    super();
    beanClass = TableDataSet.class;
  }
}
