//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/JDataStoreMetaData.java,v 7.0 2002/08/08 18:40:06 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import java.sql.*;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ParameterType;
import com.borland.dx.dataset.DataSetException;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.Variant;

/**
 *
 */
class JDataStoreMetaData extends MetaDataImplementor
{
  JDataStoreMetaData(MetaData metaData) {
    super(metaData);
  }

}

