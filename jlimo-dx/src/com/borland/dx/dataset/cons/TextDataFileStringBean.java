//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/TextDataFileStringBean.java,v 7.0 2002/08/08 18:39:41 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;


public class TextDataFileStringBean
{
    public static final String[][] strings  = {
      {"delimiter",      Res.bundle.getString(ResIndex.BI_delimiter), "getDelimiter", "setDelimiter"},
      {"encoding",       Res.bundle.getString(ResIndex.BI_encoding), "getEncoding", "setEncoding"},
      {"fileFormat",     Res.bundle.getString(ResIndex.BI_fileFormat), "getFileFormat", "setFileFormat", "com.borland.jbcl.editors.DataFileFormatEditor"},
      {"fileName",       Res.bundle.getString(ResIndex.BI_fileName), "getFileName", "setFileName", "com.borland.jbcl.editors.FileNameEditor"},
      {"loadOnOpen",     Res.bundle.getString(ResIndex.BI_loadOnOpen), "isLoadOnOpen", "setLoadOnOpen"},
      {"loadAsInserted", Res.bundle.getString(ResIndex.BI_loadAsInserted), "isLoadAsInserted", "setLoadAsInserted"},
      {"locale",         Res.bundle.getString(ResIndex.BI_locale), "getLocale", "setLocale" },
      {"separator",      Res.bundle.getString(ResIndex.BI_separator), "getSeparator", "setSeparator"},
    };
}
