//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/cons/QueryResolverStringBean.java,v 7.0 2002/08/08 18:40:02 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.sql.dataset.cons;

public class QueryResolverStringBean
{
    public static final String[][] strings = {
      {"database",    Res.bundle.getString(ResIndex.BI_database), "getDatabase", "setDatabase"},
      {"updateMode",  Res.bundle.getString(ResIndex.BI_updateMode), "getUpdateMode", "setUpdateMode", "com.borland.jbcl.editors.UpdateModeEditor"},
      {"resolverQueryTimeout",  Res.bundle.getString(ResIndex.BI_resolverQueryTimeout), "getResolverQueryTimeout", "setResolverQueryTimeout"},
    };
}
