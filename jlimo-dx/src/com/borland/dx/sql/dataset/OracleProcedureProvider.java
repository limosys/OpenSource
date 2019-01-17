//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/OracleProcedureProvider.java,v 7.0 2002/08/08 18:39:52 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

 /**
  * deprecated Use ProcedureProvider
  *The OracleProcedureProvider class provides data to the StorageDataSet by executing the specified stored procedure (Oracle PL-SQL) through JDBC. The OracleProcedureProvider class makes no attempt to make the StorageDataSet updatable or editable; it is the developer's responsibility to ensure this prior to the start of the resolution phase.
  *The procedure used for the ProcedureDataSet must have an OUT parameter of type CURSOR REF as the first parameter in the procedure specification (query string). Typically this will be a PL-SQL stored function with a return type of a CURSOR REF. Oracle JDBC drivers use an extension of jdbc for this data type. JBuilder does not have a jdbc Variant type so you cannot specify this parameter in a parameterRow in JBuilder directly. JBuilder does the modifications that allow the callable statement to retrieve the data for this DataSet
  */
public class OracleProcedureProvider extends ProcedureProvider {
}
