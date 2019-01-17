//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/StoreClassFactory.java,v 7.0 2002/08/08 18:39:37 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
    Classes that implement StoreClassFactory can be used to set the
    StorageDataSet.StoreClassFactory property.  Currently, this property
    is only meaningful when the StorageDataSet.Store propeperty is set to
    a DataStoreConneciton or DataStore component.

    The StoreClassFactory property is useful for enforcing that a DataStore
    table is only accessed by a specific StorageDataSet class or StorageDataSet
    class extension.  This allows an application to enforce that property and
    event settings are always activated when a DataStore table is accessed.

    One useful example of how this mechanism can be used is the specification of
    Java triggers for DataStore tables.  By setting the StoreClassFactory property
    and the EditListener events of a StorageDataSet, Java trigger events can be
    enforced for both DataExpress and SQL access to a DataStore table.

    When the StoreClassFactory property is set, the name of the class that implements
    this interface is recorded in the metadata for the associated table in the
    DataStore.  For DataExpress table opens, this property setting acts as a constraint.
    The DataStore will not allow write access to a table that has a StoreClassFactory
    metadata setting unless the StorageDataSet has its StoreFactory property set to a class
    with the same name.

    When a DataStore JDBC connection opens a table that has a StoreClassFactory
    recorded in its metadata, DataStore will do the following:  1) If the connection
    has never instantiated the StoreClassFactory, it will instantiate the
    StoreClassFactory and register this instance with the connection.  So there
    is one StoreClassFactory instantiation for each connection.  2) it will
    then call the StoreClassFactory's getStorageDataSet() method.  The returned
    StorageDataSet will be used for the query operation.  If the StoreClassFactory
    class cannot be instantiated, then write access will be denied to this table.
*/

public interface StoreClassFactory {
  /**
      Currently, this method is only called when a JDBC connection opens a table
      that has a StoreClassFactory recorded in its metadata.

      @store      Store implementation that storeName is being opened for.  Currently,
                  this is always a DataStoreConneciton used by a JDBC Connection.
                  This can be used to perform transactional operations with the
                  same connection that is attempting to open a table.
      @storeName  storeName of table that needs to be opened.
      @return     a StorageDataSet for the given storeName.
  */
  StorageDataSet getStorageDataSet(Store store, String storeName) /*-throws DataSetException-*/;

//  /**
//   * Called when table is opened.  Can be used to add event listeners on
//   * StorageDataSets opened by JDBC drivers or DataExpress.
//   */
//  void  opening(StorageDataSet table);
//  /**
//   * Called when table is closed.  Can be used to remove event listeners on
//   * StorageDataSets opened by JDBC drivers or DataExpress.
//   */
//  void  closed(StorageDataSet table);
}
