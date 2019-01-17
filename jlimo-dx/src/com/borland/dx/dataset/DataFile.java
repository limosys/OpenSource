//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataFile.java,v 7.0 2002/08/08 18:39:21 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * This class collects the basic behavior of all file-based data sources: loading
 * data from and writing data to a file. These operations are often referred to as
 * importing and exporting.
 * <p>
 * Extend this base class when creating new classes to define a custom file
 * format that you want to import data from, or export data to.
 * <p>
 * The {@link com.borland.dx.dataset.TextDataFile} component extends this class.
 * It provides the ability to read data from a text file into the
 * {@link com.borland.dx.dataset.TableDataSet} component, and to save data stored
 * in any StorageDataSet class object to a text file. Its properties specify how the
 * data is organized in the text file.
 */
public abstract class DataFile implements java.io.Serializable, Designable {
  /** Implementor does not need to synchronize on dataSet if the
      asychrnonous startLoading/loadRow/endLoading methods of dataSet are used.
      if asyncrhonous dataSet load methods are not used, should synchronize
      on StorageDataSet parameter. ie
          synchronize(dataSet) {
            //
            // DataFile data load implementation ...
            //
          }

  */

  /**
   * Loads data into the DataSet. Implementations of this method do not need to
   * synchronize on the dataSet parameter if the asynchronous StorageDataSet methods
   * of startLoading(), loadRow(), and endLoading() are called.
   *
   * @param dataSet
   * @throws Exception
   */
  public abstract void load(DataSet dataSet)  throws Exception;

  /** Implementor should synchronize on the dataSet.getDataSet().
      ie
          synchronize(dataSet.getDataSet()) {
            //
            // DataFile data save implementation ...
            //
          }
  */

  /**
   * Saves the data in the DataSet.
   *
   * @param dataSet
   * @throws Exception
   */
  public abstract void save(DataSet dataSet)  throws Exception;

  /**
   * Implementor should load information and determine the columns of the DataFile.
   */
  public abstract void loadMetaData(DataSet dataSet) throws Exception;

  /**
   *  If <b>true</b>, the StorageDataSet will automatically load from the
   *  DataFile when the StorageDataSet is opened.
   *
   *  @return <b>true</b> if the StorageDataSet will automatically load from the
   *          DataFile when the StorageDataSet is opened.
   */
  public abstract boolean isLoadOnOpen();

  private static final long serialVersionUID = 1L;
}
