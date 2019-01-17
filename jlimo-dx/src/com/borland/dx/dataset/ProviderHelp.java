//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ProviderHelp.java,v 7.0 2002/08/08 18:39:31 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * This class collects utility functions that operate on the StorageDataSet
 * and DataSet classes that are helpful for provider/resolver implementations.
 * These methods are implemented inside StorageDataSet, but are not functions typical
 * for DataSet usage.
 */
public class ProviderHelp {

  /**
   * Causes a DataSetException to be thrown if the DataSet is open.
   * @param dataSet
   */
  public static final void failIfOpen(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    dataSet.failIfOpen();
  }

//!/*
//!  /* @since JB2.0
//!      Creates an ordinal mapping from the DataSet's Columns to the provided columns array.
//!      If columnMap is null, an int[] will be allocated.
//!
//!  public static final int[] createColumnMap(StorageDataSet dataSet, Column[] columns, int[] columnMap)
//!    /*-throws DataSetException-*/
//!  {
//!    return dataSet.createColumnMap(columns, columnMap);
//!  }
//!*/

  /**
   *  Used by the ResolutionManager to place a StorageDataSet into a "resolving" mode.
   *  This mode prohibits any other resolution or providing to occur. After the resolution
   *  phase is complete, the endResolution() method should be called. If the postEdits parameter
   *  is <b>true</b>, the StorageDataSet will post an unposted row for itself and any DataSetViews that
   *  may be associated with the StorageDataSet.
   *
   * @param dataSet
   * @param postEdits
   */
  public static final void startResolution(StorageDataSet dataSet, boolean postEdits)
    /*-throws DataSetException-*/
  {
    dataSet.startResolution(postEdits);
  }

  /**
   * Used by the ResolutionManager to signal that StorageDataSet is no longer in a
   * "resolving" mode. Once the startResolution() method is called, any other
   * providing or resolving operation is prohibited until this method is called.
   *
   * @param dataSet
   */
  public static final void endResolution(StorageDataSet dataSet) {
    dataSet.endResolution();
  }

  /**
   * Marks a row as pending resolution. Used by the ResolutionManager when
   * saving changes from a DataSet to a remote data provider that supports
   * transactions, for example, JDBC connections.
   *
   * @param dataSet
   * @param on
   */
  public static final void markPendingStatus(DataSet dataSet, boolean on)
    /*-throws DataSetException-*/
  {
    dataSet.markPendingStatus(on);
  }

  /**
   * @deprecated      Use initData(com.borland.dx.dataset.StorageDataSet, com.borland.dx.dataset.Column[], boolean, boolean)
   * @param dataSet
   * @param columns
   * @param updateColumns
   * @param keepExistingColumns
   * @param emptyRows
   * @return
   */
  public static final int[] initData( StorageDataSet  dataSet,
                                      Column[]        columns,
                                      boolean         updateColumns,
                                      boolean         keepExistingColumns,
                                      boolean         emptyRows
                                    )
    /*-throws DataSetException-*/
  {
    return dataSet.initData(columns, updateColumns, keepExistingColumns);
  }

  /**
   *  Used by providers, initData() initializes the data storage of a DataSet
   *  for a new set of columns and returns an ordinal map of the passed in columns
   *  to the corresponding columns in the DataSet. This method differs from the
   *  StorageDataSet.setColumns() method in that it preserves persistent columns.
   *  Note that several column properties in the columns array will be merged
   *  in with existing columns in the StorageDataSet columns that have the same name.
   *
   * @param dataSet               The StorageDataSet whose data storage is to be initialized.
   *
   * @param columns               The array of columns that represents the added columns.
   *
   * @param updateColumns         Boolean that determines whether columns will be merged into
   *                              existing persistent columns. If true, the columns will not be
   *                              added. Instead the returned column map is computed with the
   *                              current columns. If both updateColumns and keepExistingColumns
   *                              parameters are true, non-persistent columns will also be retained.
   *
   * @param keepExistingColumns   If true, new columns will be merged into existing columns.
   *                              If false all non-persistent columns are removed before merging
   *                              in the persistent columns.
   * @return
   */
  public static final int[] initData( StorageDataSet  dataSet,
                                      Column[]        columns,
                                      boolean         updateColumns,
                                      boolean         keepExistingColumns
                                    )
    /*-throws DataSetException-*/
  {
    return dataSet.initData(columns, updateColumns, keepExistingColumns);
  }

   /**
    * Reflects whether this StorageDataSet has received some property change
    * which could affect the column structure or set of row data, for example,
    * a change in QueryDescriptor or TextDataFile. Used by Provider classes.
    * The JdbcProvider uses this to decide if it needs to discard cached information
    * about the JDBC data source.
    *
    * @see #setProviderPropertyChanged(StorageDataSet, boolean)
    * @param dataSet
    * @return
    */
  public static final boolean isProviderPropertyChanged(StorageDataSet dataSet) {
    return dataSet.isProviderPropertyChanged();
  }

  /**
   * Used by Provider classes. This property reflects whether this StorageDataSet
   * has received some property change which could affect the column structure or
   * set of row data, for example, a change in QueryDescriptor or TextDataFile.
   *
   * @see #isProviderPropertyChanged(com.borland.dx.dataset.StorageDataSet)
   * @param dataSet
   * @param propertiesChanged
   */
  public static final void setProviderPropertyChanged(StorageDataSet dataSet, boolean propertiesChanged) {
    dataSet.setProviderPropertyChanged(propertiesChanged);
  }

  /**
   *  A provider can call this method to determine if it should make copies of data
   *  from columns of type {@link com.borland.dx.dataset.Variant#INPUTSTREAM}.
   *  The method will return <b>false</b> if the storage is going to copy the stream,
   *  thus there is no need for the provider to copy the data. The method returns
   *  <b>true</b> if the storage is just storing a reference to the InputStream. In this case,
   *  the provider might need to make a memory copy of the data, if the stream has a
   *  limited lifetime or is not resetable.
   *
   * @param dataSet
   * @return
   */
  public static final boolean isCopyProviderStreams(StorageDataSet dataSet) {
    return dataSet.getMatrixData().copyStreams();
  }

  /**
   *  Normally set by Providers to indicate that there is insufficient metadata
   *  to post any changes back to its original source.
   *
   * @param dataSet
   * @param hasRowIds
   */
  public static final void setMetaDataMissing(StorageDataSet dataSet, boolean hasRowIds)
  {
    dataSet.setMetaDataMissing(hasRowIds);
  }

  /**
   *  Returns an int, which can be saved and used by a provider as a flag
   *  that a column restructure has occurred. This number is incremented each
   *  time a column structure changes. Therefore, if a resolver initially calls
   *  this method and stores the return value, any change in its return value on a
   *  subsequent call indicates that the resolver must discard any cached information
   *  about the DataSet.
   *
   * @param dataSet
   * @return          An int, which can be saved and used by a provider as a flag
   *                  that a column restructure has occurred. This number is incremented each
   *                  time a column structure changes. Therefore, if a resolver initially calls
   *                  this method and stores the return value, any change in its return value on a
   *                  subsequent call indicates that the resolver must discard any cached information
   *                  about the DataSet.
   */
  public static final int getStructureAge(StorageDataSet dataSet) {
    return dataSet.getStructureAge();
  }

  /**
   * Returns a StorageDataSet that contains the metadata for a given
   * StorageDataSet or DataSetView.
   *
   * @param dataSet
   * @return        A StorageDataSet that contains the metadata for a given
   *                StorageDataSet or DataSetView.
   */
  public static final StorageDataSet getResolverDataSet(DataSet dataSet) {
    if (dataSet.resolverStorageDataSet != null)
      return dataSet.resolverStorageDataSet;
    return dataSet.getStorageDataSet();
  }
}
