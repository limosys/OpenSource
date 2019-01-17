//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/cons/DatabaseStringBean.java,v 7.0 2002/08/08 18:40:00 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.sql.dataset.cons;

public class DatabaseStringBean
{
    public static final String[][] strings = {
      {"connection",                Res.bundle.getString(ResIndex.BI_connection), "getConnection", "setConnection"},
      {"transactionIsolation",      Res.bundle.getString(ResIndex.BI_transactionIsolation), "getTransactionIsolation", "setTransactionIsolation", "com.borland.jbcl.editors.TransactionIsolationEditor"}, // int
      {"useCaseSensitiveId",        Res.bundle.getString(ResIndex.BI_useCaseSensitiveId), "isUseCaseSensitiveId", "setUseCaseSensitiveId"},
      {"useCaseSensitiveQuotedId",  Res.bundle.getString(ResIndex.BI_useCaseSensitiveQuotedId), "isUseCaseSensitiveQuotedId", "setUseCaseSensitiveQuotedId"},
      {"useSchemaName",             Res.bundle.getString(ResIndex.BI_useSchemaName), "isUseSchemaName", "setUseSchemaName"},
      {"useSpacePadding",           Res.bundle.getString(ResIndex.BI_useSpacePadding), "isUseSpacePadding", "setUseSpacePadding"},
      {"useStatementCaching",       Res.bundle.getString(ResIndex.BI_useStatementCaching), "isUseStatementCaching", "setUseStatementCaching"},
      {"useTableName",              Res.bundle.getString(ResIndex.BI_useTableName), "isUseTableName", "setUseTableName"},
      {"useTransactions",           Res.bundle.getString(ResIndex.BI_useTransactions), "isUseTransactions", "setUseTransactions"},
      {"databaseName",            Res.bundle.getString(ResIndex.BI_databaseName), "getDatabaseName", "setDatabaseName"},
//!    {"SQLDialect",                Res.bundle.getString(ResIndex.BI_SQLDialect), "getSQLDialect", "setSQLDialect", "com.borland.jbcl.editors.SQLDialectEditor"},
    };
}
