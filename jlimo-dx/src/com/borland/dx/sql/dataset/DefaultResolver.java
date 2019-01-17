//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/DefaultResolver.java,v 7.0 2002/08/08 18:39:51 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;

/**
 * The DefaultResolver interface collects behavior for supplying a Resolver
 * object to the ResolutionManager. Objects implementing this interface are responsible
 * for specifying an initialized Resolver object to the ResolutionManager.
 *Whenever the ResolutionManager needs a Resolver object, it invokes the getResolver()
 * method and passes to it the current DataSet being resolved. An implementation of this object can either return an instance of a specific type of Resolver, or can extract the resolver property (if one exists) from the DataSet passed in. If no Resolver property is set for the DataSet, it is this object's responsibility to return an instance to a Resolver for the ResolutionManager to use.
 *The Database component implements this class and uses the QueryResolver as its
 * default Resolver object.
 */
public interface DefaultResolver{
  /**
  * Implementations of this method are responsible for doing any initialization required
  * by the Resolver object. For example, objects derived from DatabaseResolver require that
  * their init method is invoked with the associated Database as a parameter before any
  * resolving is performed.
  */
  public Resolver getResolver(DataSet dataSet) /*-throws DataSetException-*/;
}

