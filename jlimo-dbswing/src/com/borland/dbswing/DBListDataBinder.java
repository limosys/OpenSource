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

import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

import com.borland.dx.dataset.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * <p>Maps a <code>JList's</code> selected value to a <code>DataSet Column</code>
 * value.  More generally, <code>DBListDataBinder</code> can map any
 * selected value obtained from a <code>ListModel</code> to a <code>DataSet</code> value.
 * In particular, <code>DBListDataBinder</code> can be used to make a <code>JList</code> write
 * its currently selected value to a <code>DataSet,</code> and to update its
 * currently selected value when the <code>DataSet</code> value changes.</p>
 *
 *<p>There are two ways to use a <code>DBListDataBinder</code> to make a list
 * data-aware.  The easier way is to set the <code>jList</code>
 * property to a <code>JList</code> (or subclass of <code>JList</code>).  You can also make any
 * component using a <code>ListModel</code> and <code>ListSelectionModel</code>
 * data-aware by setting <code>DBListDataBinder's  listModel</code>
 * and <code>listSelectionModel</code> properties (or by adding
 * <code>DBListDataBinder</code> as a <code>ListSelectionListener</code> to the component).
 * If you take the second approach, however, you are
 * responsible for opening the <code>DataSet.</code></p>
 *
 *<p>In either case, you must set <code>DBListDataBinder's</code>
 * <code>dataSet</code> and <code>columnName</code> properties to indicate the
 * <code>DataSet</code> and <code>Column</code> from which the list value is to be read and to which it is to be written.
 * If you set the <code>jList</code> property, <code>DBListDataBinder</code> will also
 * bind the <code>background</code>, <code>foreground</code>, and <code>font</code>
 * properties from those defined on Column <code>columnName</code> (if
 * defined), unless already explicitly set on the <code>JList</code> itself.</p>
 *
 *<p>In addition to writing a value to a <code>DataSet</code> when a list value is selected, <code>DBListDataBinder</code> ensures
that the selected list value indicates the current value of the <code>DataSet Column</code> to which it is attached. When the list is using a <code>DBListModel</code> as its data model, <code>DBListDataBinder</code> uses <code>DataSet</code>
methods to quickly find the matching value. Otherwise, the list's data model is searched sequentially
until the first match is found. When the value in the <code>DataSet</code> does not match a value in the list,
<code>DBListDataBinder</code> clears, by default, the list selection so no value is selected in the list. You can, however, set the <code>unknownDataValueMode</code> property to specify alternative behavior. </p>
 *
 *<p><code>DBListDataBinder</code> can only assign a single value to a <code>DataSet
 * Column,</code> and always uses the <code>ListSelectionModel's</code> first selected
 * value (<code>ListSelectionModel.getMinSelectionIndex()</code>) as the currently selected value.</p>
 *
 *<p>Finally, note that there is (always) an implicit assumption that
 * the <code>ListSelectionModel's</code> indices are bound by the size of the
 * <code>ListModel,</code> even though neither model has a reference to the other.
 * If you specify a <code>ListSelectionModel</code> and <code>ListModel</code> individually
 * instead of setting the <code>jList</code> property, be sure both models
 * are working on the same model data.</p>
 *
 * <p>Usage example:</p>
 *<pre>
 * JList jList = new JList(new String [] { "First", "Second", "Third" });
 * DBListDataBinder DBListDataBinder = new DBListDataBinder();
 *
 * // when a value is selected from the list, it will be written
 * // to the "base" column of DataSet dataSet
 * DBListDataBinder.setJList(jList);
 * DBListDataBinder.setDataSet(dataSet);
 * DBListDataBinder.setColumnName("base");
 *</pre>
 *
 * @see JdbList
 */
public class DBListDataBinder
  implements ListSelectionListener, AccessListener, PropertyChangeListener,
             NavigationListener, DataChangeListener, ColumnAware, DBDataBinder,
             Designable, java.io.Serializable, FocusListener
{

  /**
   * <p>Constructs a <code>DBListDataBinder</code>. Calls the <code>null</code> constructor of its superclass.</p>
   */
  public DBListDataBinder() {
  }

  /**
   * <p>Creates a <code>DBListDataBinder</code> with a specified list. Calls the <code>null</code> constructor of its superclass. </p>
   *
   * @param jList The <code>JList</code> that appears in the list.
*/
  public DBListDataBinder(JList jList) {
    setJList(jList);
  }

  //
  // DBListDataBinder properties
  //

/**
 * <p>Sets the <code>JList</code> the component is attached to. By setting the <code>JList</code> property, you make
the specified <code>JList</code> data-aware as <code>DBListDataBinder</code> maps the <code>JList's</code> selected value to a
DataSet Column value. </p>
 *
 * <p>If you set the <code>jList</code> property, <code>DBListDataBinder</code> also binds the <code>background, foreground,</code> and
<code>font</code> properties from those defined on <code>Column columName</code> (if defined), unless already explicitly set on the <code>JList</code> itself. </p>
 *
 * @param jList The <code>JList</code> the component is attached to.
  * @see #getJList
*/
  public void setJList(JList jList) {
    if (this.jList != null && this.jList != jList) {
      this.jList.removePropertyChangeListener(this);
    }
    this.jList = jList;
    if (jList == null) {
      setListSelectionModel(null);
      setListModel(null);
    }
    else {
      jList.addPropertyChangeListener(this);
      setListSelectionModel(jList.getSelectionModel());
      setListModel(jList.getModel());
    }
  }

/**
 * <p>Returns the <code>JList</code> the component is attached to.</p>
 *
 * @return The <code>JList</code> the component is attached to.
  * @see #setJList
*/
  public JList getJList() {
    return jList;
  }


/**
 * <p>Sets the <code>ListSelectionModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value. </p>
 *
 * <p><code>DBListDataBinder</code> assigns a single value only to a <code>DataSet Column</code>, and always uses the <code>ListSelectionModel's</code> first selected value (<code>ListSelectionModel.getMinSelectionIndex()</code>) as the currently selected value. </p>
 *
 * <p>Note that there is always an implicit assumption that the <code>ListSelectionModel's</code> indices are bound by the size of the <code>ListModel,</code> even though neither model has a reference to the other. If you specify a <code>ListSelectionModel</code> and <code>ListModel</code> individually instead of setting the <code>JList</code> property, be sure
both models are working on the same model data. </p>
 *
 * @param listSelectionModel The <code>ListSelectionModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value.
 * @see #getListSelectionModel
 */
  public void setListSelectionModel(ListSelectionModel listSelectionModel) {

    if (this.listSelectionModel != null) {
      this.listSelectionModel.removeListSelectionListener(this);
    }

    this.listSelectionModel = listSelectionModel;

    if (listSelectionModel != null) {
      listSelectionModel.addListSelectionListener(this);
    }
    bindColumnProperties();
  }


/**
 * <p>Returns the <code>ListSelectionModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value. </p>
 *
 * @return The <code>ListSelectionModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value.
 * @see #setListSelectionModel
 */
  public ListSelectionModel getListSelectionModel() {
    return listSelectionModel;
  }


/**
 * <p>Sets the <code>ListModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value. </p>
 *
 * @param listModel The <code>ListModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value.
 * @see #getListModel
 */

  public void setListModel(ListModel listModel) {

    this.listModel = listModel;

    dbListModel = null;
    if (listModel instanceof DBListModel) {
      dbListModel = (DBListModel) listModel;
    }
    // discard our cache
    dataSetView = null;
    locateRow = null;

    bindColumnProperties();

  }

/**
 * <p>Returns the <code>ListModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value. </p>
 *
 * @return The <code>ListModel</code> from which <code>DBListDataBinder</code> maps a selected value to a <code>DataSet</code> value.
 * @see #setListModel
 */

  public ListModel getListModel() {
    return listModel;
  }

  //
  // ColumnAware interface implememtation
  //

  /**
   * <p>Sets the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @param dataSet The <code>DataSet</code> from which values are read and to which values are written.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    if (columnAwareSupport.dataSet != null) {
      columnAwareSupport.dataSet.removeNavigationListener(this);
    }
    columnAwareSupport.setDataSet(dataSet);
    if (dataSet != null) {
      columnAwareSupport.dataSet.addNavigationListener(this);
    }
    bindColumnProperties();
  }


  /**
   * <p>Returns the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @return The <code>DataSet</code> from which values are read and to which values are written.
   * @see #setDataSet
   * @see #getColumnName
   */
  public DataSet getDataSet() {
    return columnAwareSupport.dataSet;
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @param columnName The <code>DataSet</code> from which values are read and to which values are written.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    columnAwareSupport.setColumnName(columnName);
    bindColumnProperties();
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @return The <code>DataSet</code> from which values are read and to which values are written.
   * @see #setColumnName
   * @see #getDataSet
   */

  public String getColumnName() {
    return columnAwareSupport.columnName;
  }

  /**
   * <p>Sets the policy for setting the list selection when synchronizing a button with its <code>dataSet</code> value when the value can't be found in the list.</p>
   *
   * @param mode The policy for setting the list selection when synchronizing a button with its <code>dataSet</code> value when the value can't be found in the list. Possible values are:</p>
   * <ul>
   * <li>DEFAULT - Clear the current list selection if the <code>DataSet</code> value cannot be found in the list. </li>
   * <li>DISABLE_COMPONENT - Disable the component specified by the <code>JList</code> property if the <code>DataSet</code> value can not be found in the list. </li>
   * <li>CLEAR_VALUE - Clears the <code>DataSet</code> value if it does not match any value in the list, and clears the current list selection. </li>
   *</ul>
   * @see #getUnknownDataValueMode
   */
  public void setUnknownDataValueMode(int mode) {
    this.mode = mode;
  }


  /**
   * <p>Returns the policy for setting the list selection when synchronizing a button with its <code>dataSet</code> value when the value can't be found in the list.</p>
   *
   * @return The policy for setting the list selection when synchronizing a button with its <code>dataSet</code> value when the value can't be found in the list. Possible values are:</p>
   * <ul>
   * <li>DEFAULT - Clear the current list selection if the <code>DataSet</code> value cannot be found in the list. </li>
   * <li>DISABLE_COMPONENT - Disable the component specified by the <code>JList</code> property if the <code>DataSet</code> value can not be found in the list. </li>
   * <li>CLEAR_VALUE - Clears the <code>DataSet</code> value if it does not match any value in the list, and clears the current list selection. </li>
   *</ul>
   * @see #setUnknownDataValueMode
   */
  public int getUnknownDataValueMode() {
    return mode;
  }

  //
  // javax.swing.event.ListSelectionListener interface implementation
  //

  /**
   * <p>Called when the value of the selection changes.</p>
   * @param e The event that characterizes the change.
   */
  public void valueChanged(ListSelectionEvent e) {
    if (!ignoreValueChange && !e.getValueIsAdjusting()) {
      int index = listSelectionModel.getMinSelectionIndex();
      if (index == -1) {
        index = e.getFirstIndex();
      }

      columnAwareSupport.lazyOpen();

      if (dbListModel != null) {
        columnAwareSupport.setVariant(dbListModel.getVariantElementAt(index));
      }
      else {
        columnAwareSupport.setObject(listModel.getElementAt(index));
      }
    }
  }

  //
  // com.borland.dx.dataset.NavigationListener interface implementation
  //
  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // navigation means we've moved on to a different row, and we need
    // to update the selected item in the list.
    updateSelectedListValue();
  }

  // finds the current value and sets it as the current selection
  // in the list.  if we're using a dbListModel, we can use the DataSet
  // lookup to get the row index quickly.  Otherwise, we have to sequentially
  // search the entire model.
  private void updateSelectedListValue() {
    int newIndex = -1;

    if (!columnAwareSupport.isValidDataSetState()) {
      ignoreValueChange = true;
      if (listSelectionModel != null) {
        listSelectionModel.clearSelection();
      }
      ignoreValueChange = false;
      return;
    }

    if (dbListModel != null) {
      try {
        // make a DataSetView so we can navigate independently of dataSet
        if (dataSetView == null) {
          dataSetView = dbListModel.getDataSet().cloneDataSetView();
        }
        if (locateRow == null) {
          locateRow = new DataRow(dataSetView, dbListModel.getColumnName());
        }
        locateRow.setVariant(dbListModel.getColumnName(), columnAwareSupport.getVariant());
        if (dataSetView.locate(locateRow, Locate.FIRST)) {
          newIndex = dataSetView.getRow();
        }
      }
      catch (DataSetException e) {
        DBExceptionHandler.handleException(dataSetView, e);
      }
    }
    else {
      if (listModel != null) {
        int lastRow = listModel.getSize();
        Object locateValue = columnAwareSupport.getVariant().getAsObject();
        for (int index = 0; index < lastRow; index++) {
          if (listModel.getElementAt(index).equals(locateValue)) {
            newIndex = index;
            break;
          }
        }
      }
    }

    ignoreValueChange = true;
    if (newIndex == -1) {
      if (mode == CLEAR_VALUE && !columnAwareSupport.isNull()) {
        columnAwareSupport.lazyOpen();
        columnAwareSupport.resetValue();
      }
      else if (mode == DISABLE_COMPONENT) {
        if (jList != null && !columnAwareSupport.getVariant().isUnassignedNull()) {
          jList.setEnabled(false);
        }
      }
      else {
        listSelectionModel.clearSelection();
      }
    }
    else {
      if (mode == DISABLE_COMPONENT && jList != null && !jList.isEnabled()) {
        jList.setEnabled(true);
      }
      listSelectionModel.setSelectionInterval(newIndex, newIndex);
      if (jList != null) {
        jList.ensureIndexIsVisible(newIndex);
      }
    }

    ignoreValueChange = false;
  }

  //
  // com.borland.dx.dataset.DataChangeListener interface implementation
  //
  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // depending upon the event, we might have to update the currently
    // selected list value.
    int affectedRow = event.getRowAffected();
    boolean affectedOurRow = (affectedRow == columnAwareSupport.dataSet.getRow()) ||
      affectedRow == -1;
    if (affectedOurRow) {
      updateSelectedListValue();
    }
  }

  public void postRow(DataChangeEvent event) throws Exception {
    // usually needs to get the pending value from the control and set it,
    // but since lists are (usually) not editable directly, this isn't necessary
  }

  //
  // com.borland.dx.dataset.AccessListener interface implementation
  //
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.CLOSE) {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }
      else {
        listSelectionModel.clearSelection();
      }

      if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        rebindColumnProperties = true;
      }
    }
    else {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      if (event.getReason() == AccessEvent.UNSPECIFIED || rebindColumnProperties || event.getReason() == AccessEvent.DATA_CHANGE) {
        bindColumnProperties();
      }
    }
  }

  //
  // java.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("ancestor")) {  
      bindColumnProperties();
    }
    if (e.getPropertyName().equals("selectionModel")) {   
      setListSelectionModel((ListSelectionModel) e.getNewValue());
    }
    else if (e.getPropertyName().equals("model")) {   
      setListModel((ListModel) e.getNewValue());
    }
  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(jList, columnAwareSupport.dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds font, foreground, and background properties from column
  // if not explicitly set on list
  private void bindColumnProperties() {
    if (oldJList != null) {
      oldJList.removeFocusListener(this);
      oldJList = null;
    }

    if (jList != null && jList.isDisplayable()) {
      // ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
      // again on the OPEN access event
      rebindColumnProperties = false;
      // will resync our internal state if the dataSet or column has changed
      columnAwareSupport.lazyOpen();

      updateSelectedListValue();

      if (columnAwareSupport.isValidDataSetState()) {

        jList.addFocusListener(this);
        oldJList = jList;

        Column column = columnAwareSupport.getColumn();

        if (isDefaultProperty(jList.getBackground())) {
          if (column.getBackground() != null) {
            jList.setBackground(column.getBackground());
          }
        }
        if (isDefaultProperty(jList.getForeground())) {
          if (column.getForeground() != null) {
            jList.setForeground(column.getForeground());
          }
        }
        if (isDefaultProperty(jList.getFont())) {
          if (column.getFont() != null) {
            jList.setFont(column.getFont());
          }
        }
        if (jList.isEnabled() && !column.isEditable()) {
          jList.setEnabled(false);
        }
      }
    }
  }

  /** JList to which we are attached */
  private JList jList;

  private JList oldJList;

  /** ListSelectionModel of list from which to read data */
  private ListSelectionModel listSelectionModel;

  /** ListModel containing current list selection */
  private ListModel listModel;

  /** dbListModel containing current list selection */
  private DBListModel dbListModel;

  /** current unknown data value mode */
  private int mode = DEFAULT;

  /** support for ColumnAware implementation */
  private DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

  /** flag indicating we should ignore ListSelectionEvent because we caused it */
  private boolean ignoreValueChange;

  /** cached DataSetView */
  private DataSetView dataSetView;

  /** cached DataRow */
  private DataRow locateRow;

  private boolean rebindColumnProperties;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
