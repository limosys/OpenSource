//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Resolver.java,v 7.0 2002/08/08 18:39:33 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The Resolver class is a base class for Resolver objects such as SQLResolver.
 * A Resolver should be able to update a snapshot of a data source with changes
 * from another snapshot of a database. The changes might be performed on a row
 * by row basis, as SQLResolver does, or in a batch mode.
 * <p>
 * For an example of a custom resolver that extends this class, see the ResolverBean
 * class in the sample project Providers.jpr, the ntier sample application, or the
 * datasetdata sample application (which contains an RMIResolver).
 * These samples are located in the samples folder of your JBuilder installation.
 * (<font color="red">These samples only run with JBuilder Enterprise.</font>)
 */
public abstract class Resolver implements java.io.Serializable, Designable {

  /**
  *   The resolveData method will resolve the modified data in a DataSet.
  *   The destination of the data, and the method of saving the data
  *   is up to the implementation of this abstract method.
  */
  abstract public void resolveData(DataSet dataSet) /*-throws DataSetException-*/;

  /**
  *   Some implementations of resolveData may optionally resolve the
  *   data asynchronously. A StorageDataSet has to block action such as
  *   editing and providing until the asynchronous save is done.
  *   This method allows an implementation to give an appropriate error
  *   message by raising a DataSetException.
  *   The default action is to do nothing, i.e. no asynchronous resolving.
  */
  public void checkIfBusy(StorageDataSet dataSet) /*-throws DataSetException-*/ {
  }

  /**
  *   since JB2.0
  *   Some implementations of Resolver caches information during resolveData.
  *   This method allows an implementation to release these resources and
  *   references for better garbage collection.
  */
  public void close(StorageDataSet dataSet) /*-throws DataSetException-*/ {
  }

  private   static final long serialVersionUID = 1L;
}
