//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/OracleJdbcMetaData.java,v 7.0 2002/08/08 18:40:07 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.*;

import com.borland.dx.dataset.ParameterType;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.Variant;

/**
 *
 */
class OracleJdbcMetaData extends Oracle7MetaData
{
  OracleJdbcMetaData(MetaData metaData) {
    super(metaData);
  }

  public String getProcedureQueryString(String procedureName, boolean forResultSet, boolean[] warnings) {
    return super.getProcedureQueryString(procedureName, forResultSet, warnings);

//!/*
//!//  This code is OK, but it is too early to activate: the drivers simply doesn't give
//!//  reliable metadata information incl: Oracle jdbc, Sybase thin, Datagateway, misc ODBC.
//!//
//!    String  query = "call " + procedureName; //NORES
//!    String  ret   = "";
//!    String  params= "";
//!    boolean errors       = false;
//!    boolean hasResultSet = false;
//!    boolean areTypesOK   = true;
//!    try {
//!      DatabaseMetaData meta = metaData.getJdbcMetaData();
//!      ResultSet columns = meta.getProcedureColumns(null,null,procedureName,"%");
//!
//!      while (columns.next()) {
//!        String param = columns.getString(4);   // "COLUMN_NAME"  //NORES
//!        short  ptype = columns.getShort(5);    // "COLUMN_TYPE"  //NORES
//!        short  type  = columns.getShort(6);    // "DATA_TYPE"    //NORES
//!
//!        if (ptype == ParameterType.RETURN) {
//!          ret = ":" + param + " = ";
//!          if (type == ORACLE_CURSOR)
//!            hasResultSet = true;
//!          else if (!metaData.isSqlTypeSupported(type))
//!            areTypesOK = false;
//!          continue;
//!        }
//!
//!        if (!metaData.isSqlTypeSupported(type))
//!          areTypesOK = false;
//!
//!        if (params.length() > 0)
//!          params += ", ";
//!        params += ":" + param;
//!      }
//!      if (params.length() > 0)
//!        params = "(" + params + ")";
//!
//!      columns.close();
//!    }
//!    catch (Exception ex) {
//!      Diagnostic.printStackTrace(ex);
//!      errors = true;
//!    }
//!    if (warnings != null && warnings.length >= 3) {
//!      warnings[0] = errors;
//!      warnings[1] = forResultSet ? !hasResultSet : hasResultSet;
//!      warnings[2] = !areTypesOK;
//!    }
//!    return "{" + ret + query + params + "}";
//!*/
  }

  static final private int ORACLE_CURSOR = -10;
}
