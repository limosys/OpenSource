//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/metadata/DataSetAnalyzer.java,v 7.0 2002/08/08 18:40:06 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Column;
import java.lang.*;
import java.util.*;
import java.sql.*;

abstract public class DataSetAnalyzer
{
  /**
  *  Returns the number of columns in a DataSet.
  */
  public abstract int getColumnCount();

  /**
  *  Returns a Column component for given column index.
  */
  public abstract Column getColumn(int ordinal) throws MetaDataException;

  /**
  *  Call this to release resources correctly.
  */
  public void close() {};
}
