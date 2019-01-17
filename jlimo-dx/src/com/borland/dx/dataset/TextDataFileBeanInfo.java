//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/TextDataFileBeanInfo.java,v 7.0 2002/08/08 18:39:38 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class TextDataFileBeanInfo extends BasicBeanInfo
{
  public TextDataFileBeanInfo() {
    beanClass = TextDataFile.class;
    propertyDescriptors = com.borland.dx.dataset.cons.TextDataFileStringBean.strings;
  }
}
