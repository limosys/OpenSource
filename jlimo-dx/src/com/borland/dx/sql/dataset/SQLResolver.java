//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/SQLResolver.java,v 7.0 2002/08/08 18:39:58 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;
import java.util.*;
import java.io.*;

/**
 *   his class allows for alternate implementations of the actual behavior required
 * to save changes made to a QueryDataSet for example, to its database data source. <p>

The SQLResolutionManager requires a Resolver that extends this class.
The saveChanges() method of a QueryDataSet and ProcedureDataSet instantiate
a SQLResolutionManager. The Resolver of a QueryDataSet should therefore be
an instance of a class that extends SQLResolver, such as the QueryResolver.
*/

public abstract class SQLResolver extends ResolutionResolver {

  /**
  *   The Database property defines the target of the data changes.
  */
  public abstract Database getDatabase();
  public abstract void setDatabase(Database database);


  /**
  *   The method closeStatements should close any open statements
  *   cached by a Resolver.
  */
  public void closeResources(StorageDataSet dataSet) /*-throws DataSetException-*/
  {
    closeStatements(dataSet);
  }

  /**
  *   The method closeStatements should close any open statements
  *   cached by a Resolver.
  */
  public abstract void closeStatements(StorageDataSet dataSet) /*-throws DataSetException-*/;

  /**
  *   The implementation of resolveData simply calls the saveChanges
  *   method on the current Database.
  */
  public void resolveData(DataSet dataSet) /*-throws DataSetException-*/ {
    if (getDatabase() != null)
      getDatabase().saveChanges(dataSet);
    else
      DataSetException.noDatabaseOnResolver();
  }

  private transient ResolverListener resolverListener;
  private static final long serialVersionUID = 1L;
}

