/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.dbswing;

import javax.swing.*;

import com.borland.dx.dataset.*;

/**
 * <p>An implementation of the Swing <code>ListModel</code>.  It
 * provides data from a DataExpress <code>DataSet</code> and <code>Column</code>.  A
 * <code>DBListModel</code> updates itself automatically to changes in its
 * corresponding <code>DataSet</code>, such as row filtering, row sorting,
 * addition and removal of rows, and so on. Also, if the <code>columnName</code> property value
 * refers to an invisible <code>DataSet Column</code> or if the value of either the <code>columnName</code>
 * or <code>dataSet</code> property is invalid, <code>DBListModel</code> behaves as if the <code>DataSet</code> is empty.</p>
 *
 *<p><code>DBListModel</code> is used by <code>JdbList</code> as its default model. Note that
 * dbSwing components automatically open or close their attached
 *<code>DataSets</code> as necessary.  If you are using a <code>DBListModel</code>
 * independently of any dbSwing components, you are responsibile
 * for explicitly opening or closing the <code>DataSet</code> before any
 * requests are made for data from the model.  </p>
 *
 *<p>Usage example:</p>
 *
 *<pre>
 * // Shows the correct way to assign a DBListModel to a
 * // non-dbSwing component.
 * JList jList = new JList();
 * DBListModel dBListModel = new DBListModel();
 * dBListModel.setDataSet(dataSet);
 * dBListModel.setColumnName("last name");
 *   try {
 *     dataSet.open();
 *   } catch (DataSetException e) {
 *     // unable to open the dataset
 *     e.printStackTrace();
 *   }
 * jList.setModel(dBListModel);
 *</pre>
 *
 *<p>If you wish to make a <code>JList</code> data-aware, consider the use of the <a href="DBListDataBinder.html"><code>DBListDataBinder</code></a> as an alternative
to specifying <code>DBListModel</code> as its model directly. <code>DBListDataBinder</code> automatically opens and closes the <code>DataSet</code> for you. While a <code>DBListModel</code> fills a list from a <code>DataSet</code> and keeps the list
up-to-date, a <code>DBListDataBinder</code> synchronizes a list with navigation in a <code>DataSet</code> and writes list selections to the <code>DataSet.</code> The two are complementary; you'll often want to use both if you have a custom list component in a data-oriented application. </p>
 *
 * @see DBListDataBinder
 * @see DBTableModel
 */
public class DBListModel extends AbstractListModel
  implements AccessListener, DataChangeListener, ColumnAware, Designable
{


 /**
  * <p>Creates a <code>DBListModel</code> by calling the <code>null</code> constructor of its superclass.</p>
  */
  public DBListModel() {
    super();
  }

  /**
   * <p>Creates a <code>DBListModel</code> with a specified <code>DataSet</code> and column name. Calls the <code>null</code> constructor of its
superclass. </p>
   *
   * @param dataSet The <code>DataSet</code> from which the model obtains its data.
   * @param columnName The column name of the <code>DataSet</code> from which the model obtains its data.
   */
  public DBListModel(DataSet dataSet, String columnName) {
    super();
    setDataSet(dataSet);
    setColumnName(columnName);
  }

  /**
   * <p>Sets the <code>DataSet</code> from which this model obtains its data.</p>
   *
   * @param dataSet The <code>DataSet</code> from which this model obtains its data.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    columnAwareSupport.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which this model obtains its data.</p>
   *
   * @return The <code>DataSet</code> from which this model obtains its data.
   * @see #setDataSet
   * @see #getColumnName
   */
  public DataSet getDataSet() {
    return columnAwareSupport.getDataSet();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which this model
   * obtains its data.</p>
   *
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    columnAwareSupport.setColumnName(columnName);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which this model
   * obtains its data.</p>
   *
   * @return The column name of the <code>DataSet</code> from which this model obtains its data.
   * @see #setColumnName
   * @see #getDataSet
   */
  public String getColumnName() {
    return columnAwareSupport.getColumnName();
  }

  //
  // ListModel interface implementation
  //
  /**
   * <p>Returns the length of the list.</p>
   *
   * @return The length of the list.
   */
  public int getSize() {
    if (columnAwareSupport.dataSet != null && columnAwareSupport.dataSet.isOpen()) {
      columnAwareSupport.lazyOpen();
    }
    if (columnAwareSupport.isValidDataSetState()) {
      try {
        return columnAwareSupport.dataSet.getRowCount();
      }
      catch (DataSetException e) {
        DBExceptionHandler.handleException(columnAwareSupport.dataSet, e);
      }
    }
    return 0;
  }

  /**
   * <p>Returns the value at the specified index.</p>
   *
   * @param index The specified index.
   * @return The value at the specified index.
   */
  public Object getElementAt(int index) {
    Variant value = getVariantElementAt(index);
    if (value == null) {
      return null;
    }
    else {
      return value.getAsObject();
    }
  }

  Variant getVariantElementAt(int index) {
    if (columnAwareSupport.dataSet != null && columnAwareSupport.dataSet.isOpen()) {
      columnAwareSupport.lazyOpen();
    }
    if (columnAwareSupport.isValidDataSetState()) {
      try {
        columnAwareSupport.dataSet.getDisplayVariant(columnAwareSupport.columnOrdinal, index, value);
        return value;
      }
      catch (DataSetException e) {
        DBExceptionHandler.handleException(columnAwareSupport.dataSet, e);
      }
    }
    return null;
  }

  //
  // AccessListener interface implementation
  //
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      columnAwareSupport.lazyOpen();
      if (columnAwareSupport.isValidDataSetState()) {
        fireContentsChanged(this, 0, getSize());
      }
    }
    else {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }
      // dataSet closed, should close down model and fire contents removed event.
      // Because dataSet is no longer open, model will return empty contents
      // until the dataSet is reopened.
      fireIntervalRemoved(this, 0, getSize());
    }
  }

  //
  // DataChangeListener interface implementation
  //
  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    if (event.multiRowChange()) {
      // more than one data row changed.  Since we
      // don't know exactly which rows have changed,
      // fire contents changed event covering all rows
      fireContentsChanged(this, 0, getSize() - 1);
      return;
    }
    int affectedRow = event.getRowAffected();
    switch (event.getID()) {
      // row was added
      case DataChangeEvent.ROW_ADDED:
        fireIntervalAdded(this, affectedRow, affectedRow);
        break;
      // row was removed
      case DataChangeEvent.ROW_DELETED:
        fireIntervalRemoved(this, affectedRow, affectedRow);
        break;
      // cell was changed or row was changed and posted
      case DataChangeEvent.ROW_CHANGED:
      case DataChangeEvent.ROW_CHANGE_POSTED:
        fireContentsChanged(this, affectedRow, affectedRow);
        break;
      // multiple rows changed
      case DataChangeEvent.DATA_CHANGED:
      // for robustness, fire contents changed event covering
      // all rows if we don't recognize the event
      default:
        fireContentsChanged(this, 0, getSize() - 1);
        break;
    }
  }

  // handled by data-aware components, which should post or cancel pending edits
  public void postRow(DataChangeEvent event) throws Exception {
  }

  /** value returned from DataSet */
  private Variant value = new Variant();

  /** ColumnAware support */
  DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
