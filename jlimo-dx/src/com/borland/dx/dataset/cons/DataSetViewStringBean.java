//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/DataSetViewStringBean.java,v 7.1 2003/10/30 03:00:56 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;


public class DataSetViewStringBean
{
    public static final String[][] strings  = {
      {"displayErrors",  Res.bundle.getString(ResIndex.BI_displayErrors), "isDisplayErrors", "setDisplayErrors"},
      {"masterLink",     Res.bundle.getString(ResIndex.BI_masterLink), "getMasterLink", "setMasterLink"},
      {"sort",           Res.bundle.getString(ResIndex.BI_sort), "getSort", "setSort"},
      {"storageDataSet", Res.bundle.getString(ResIndex.BI_storageDataSet), "getStorageDataSet", "setStorageDataSet"},
      {"editable",          Res.bundle.getString(ResIndex.BI_editable), "isEditable", "setEditable"},
      {"enableInsert",      Res.bundle.getString(ResIndex.BI_enableInsert), "isEnableInsert", "setEnableInsert"},
      {"enableUpdate",      Res.bundle.getString(ResIndex.BI_enableUpdate), "isEnableUpdate", "setEnableUpdate"},
      {"enableDelete",      Res.bundle.getString(ResIndex.BI_enableDelete), "isEnableDelete", "setEnableDelete"},
      {"postUnmodifiedRow", Res.bundle.getString(ResIndex.BI_postUnmodified), "isPostUnmodifiedRow", "setPostUnmodifiedRow"},
    };
}
