//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/StorageDataSetStringBean.java,v 7.1 2003/10/30 03:00:57 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;


public class StorageDataSetStringBean
{
    public static final String[][] strings  = {
//**  {"columns",           Res.bundle.getString(ResIndex.BI_columns), "getColumns", "setColumns"},
       {"masterLink",        Res.bundle.getString(ResIndex.BI_masterLink), "getMasterLink", "setMasterLink"},
      {"dataFile",          Res.bundle.getString(ResIndex.BI_dataFile), "getDataFile", "setDataFile"},
      {"displayErrors",     Res.bundle.getString(ResIndex.BI_displayErrors), "isDisplayErrors", "setDisplayErrors"},
      {"locale",            Res.bundle.getString(ResIndex.BI_locale), "getLocale", "setLocale"},
      {"maxDesignRows",     Res.bundle.getString(ResIndex.BI_maxDesignRows), "getMaxDesignRows", "setMaxDesignRows"},
      {"maxRows",           Res.bundle.getString(ResIndex.BI_maxRows), "getMaxRows", "setMaxRows"},
      {"metaDataUpdate",    Res.bundle.getString(ResIndex.BI_metaDataUpdate), "getMetaDataUpdate", "setMetaDataUpdate", "com.borland.jbuilder.cmt.editors.MetaDataUpdateEditor"}, //! Can't use xxx.class.getName() here - dependency links to CMT are BAD.
//!     {"needsRestructure",  Res.bundle.getString(ResIndex.BI_needsRestructure), "getNeedsRestructure", "setNeedsRestructure"},
      {"readOnly",          Res.bundle.getString(ResIndex.BI_readOnly), "isReadOnly", "setReadOnly"},
      {"editable",          Res.bundle.getString(ResIndex.BI_editable), "isEditable", "setEditable"},
      {"resolvable",        Res.bundle.getString(ResIndex.BI_resolvableDataSet), "isResolvable", "setResolvable"},
      {"provider",          Res.bundle.getString(ResIndex.BI_provider), "getProvider", "setProvider"},
      {"resolver",          Res.bundle.getString(ResIndex.BI_resolver), "getResolver", "setResolver"},
      {"schemaName",        Res.bundle.getString(ResIndex.BI_schemaName), "getSchemaName", "setSchemaName"},
      {"sort",              Res.bundle.getString(ResIndex.BI_sort), "getSort", "setSort"},
      {"store",             Res.bundle.getString(ResIndex.BI_store), "getStore", "setStore"},
      {"tableName",         Res.bundle.getString(ResIndex.BI_tableName), "getTableName", "setTableName"},
      {"resolveOrder",      Res.bundle.getString(ResIndex.BI_tableNameList), "getResolveOrder", "setResolveOrder"},
      {"storeName",         Res.bundle.getString(ResIndex.BI_storeName), "getStoreName", "setStoreName"},
      {"enableInsert",      Res.bundle.getString(ResIndex.BI_enableInsert), "isEnableInsert", "setEnableInsert"},
      {"enableUpdate",      Res.bundle.getString(ResIndex.BI_enableUpdate), "isEnableUpdate", "setEnableUpdate"},
      {"enableDelete",      Res.bundle.getString(ResIndex.BI_enableDelete), "isEnableDelete", "setEnableDelete"},
      {"postUnmodifiedRow", Res.bundle.getString(ResIndex.BI_postUnmodified), "isPostUnmodifiedRow", "setPostUnmodifiedRow"},
      {"maxResolveErrors",  Res.bundle.getString(ResIndex.BI_maxResolveErrors), "getMaxResolveErrors", "setMaxResolveErrors"},
      {"storeClassFactory",             Res.bundle.getString(ResIndex.BI_storeClassFactory), "getStoreClassFactory", "setStoreClassFactory"},
    };
}
