//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/StorageDataSetBeanInfo.java,v 7.0 2002/08/08 18:39:36 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class StorageDataSetBeanInfo extends BasicBeanInfo
{
  public StorageDataSetBeanInfo() {
    beanClass = StorageDataSet.class;
      propertyDescriptorAttributes =  new Object[][] {{LATE_SETTING,"true"}

      } ;
    propertyDescriptors = com.borland.dx.dataset.cons.StorageDataSetStringBean.strings;
  }

}
