//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/MetaDataException.java,v 7.0 2002/08/08 18:40:07 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.SQLException;
import com.borland.dx.dataset.DataSetException;
import com.borland.jb.util.ExceptionChain;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.DiagnosticJLimo;


/**
This is used heavily by the metadata package.
*/

public class MetaDataException extends DataSetException {
  private static final int BASE = 1000*7;

  /** Column type not supported by database
  */
  public static final int UNSUPPORTED_COLUMNTYPE                    = BASE+1;

  /** Object must be either a int[] or Object[] or java.util.Enumeration
  */
  public static final int BAD_FIELDLIST                             = BASE+2;

  /** Table creation not implemented for current database dialect
  */
  public static final int NOT_IMPLEMENTED                           = BASE+3;


  static final void throwUnsupportedColumnType(int dataType, String productName)
        throws MetaDataException
  {
    throwException( UNSUPPORTED_COLUMNTYPE,
                    Res.bundle.format( ResIndex.InvalidColumnType,  new String[] { Variant.typeName(dataType), productName })
                  );
  }

  static final void throwBadFieldlist() throws MetaDataException {
    throwException( BAD_FIELDLIST, Res.bundle.getString(ResIndex.BadFieldlist) );
  }
  static final void throwNotImplemented() throws MetaDataException {
    throwException( NOT_IMPLEMENTED, Res.bundle.getString(ResIndex.NotImplemented) );
  }
  static final void rethrowSQLException(SQLException ex) throws MetaDataException {
    throwException(SQL_ERROR, ex.getMessage(), ex);
  }

  static final void rethrowDataSetException(DataSetException ex) throws MetaDataException {
    throwException(ex.getErrorCode(), ex.getMessage(), ex.getExceptionChain());
  }

  private static final void throwException(int errorCode, String message, Exception ex)
    throws MetaDataException
  {
    throw new MetaDataException(errorCode, message, ex);
  }

  static final void throwException(int errorCode, String message, ExceptionChain chain)
    throws MetaDataException
  {
    throw new MetaDataException(errorCode, message, chain);
  }

  private static final void throwException(int errorCode, String message)
    throws MetaDataException
  {
    throw new MetaDataException(errorCode, message);
  }

  public MetaDataException(int errorCode, String message) {
    super(errorCode,message);
  }

  public MetaDataException(int errorCode, String message, Exception ex) {
    super(errorCode,message, ex);
  }

  public MetaDataException(int errorCode, String message, ExceptionChain chain) {
    super(errorCode, message, chain);
  }
}
