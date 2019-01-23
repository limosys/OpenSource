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
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.UIResource;

import com.borland.dbswing.plaf.metal.MetalJdbComboBoxUI;
import com.borland.dbswing.plaf.motif.MotifJdbComboBoxUI;
import com.borland.dbswing.plaf.windows.WindowsJdbComboBoxUI;
import com.borland.dx.dataset.*;

 /**
  * <p>A data-aware extension of <code>JComboBox</code>
  * with the following additional properties. </p>
  *
  * <ul>
  * <li><code>dataSet</code> - The <code>DataSet</code> to
  * which the combobox's selected value is assigned. </li>
  * <li><code>columnName</code> - The name of the
  * <code>Column</code> of the <code>DataSet</code> to
  *  which the selected value is assigned.</li>
  * <li><code>items</code> - An array of <code>Strings</code>
  * or <code>Objects</code> to display in the drop-down
  * list. </li>
  * <li><code>dropDownWidth</code> - The desired width of the
  * drop-down list. This property has no effect on the
  * width of columns in a multi-column drop-down derived
  * from a picklist. <code>dropDownWidth</code> is mainly
  * useful in conjunction with the items property for
  * displaying a single column drop-down of arbitrary
  * width. To specify the width of an individual column in
  * a picklist drop-down, set the width property of the
  * corresponding picklist. </li>
  * <li><code>fixedCellHeight</code> - The desired fixed
  * height of the items in combobox's drop-down list.</li>
  * </ul>
  *
  * <p>If the <code>pickList</code> property of
  * <code>JdbComboBox</code>'s <code>columnName</code> has
  * been defined, <code>JdbComboBox</code> populates
  * its drop-down list as specified in the picklist,
  * displaying multiple columns if necessary. </p>
  *
  * <p>If both the <code>pickList</code> and
  * <code>items</code> properties are set, the
  * <code>items</code> property takes precedence. </p>
  *
  * <p>When an editable <code>JdbComboBox</code> gets its
  * picklist from its <code>items</code> property, you are
  * allowed to enter values not in the picklist into the
  * column the combobox is bound to. When it gets its
  * picklist from the <code>pickList</code> property of
  * the bound-to column, you must enter a value from the
  * picklist; editing in the selected text area of the
  * combobox is simply a convenient way to choose a value
  * from the list. </p>
  *
  * <p>Ordinarily, the selection in a data-aware
  * <code>JdbComboBox</code> is the current value of the
  * column it is bound to. If this value is not in its
  * list, the message "Unable to locate corresponding
  * value" is displayed when you navigate to a row with an
  * unknown value. This is because the selected text area
  * always displays some value, even when it's empty. </p>
  *
  * <p>The assumption is that you want to correct these
  * unknown values when you encounter them. If you don't,
  * you probably won't want to use a combobox to display
  * a column containing unknown values. </p>
  */
public class JdbComboBox extends JComboBox
  implements NavigationListener, AccessListener, ItemListener, PropertyChangeListener,
             DataChangeListener, ColumnAware, java.io.Serializable, FocusListener
{

 /**
  * <p>Constructs a <code>JdbComboBox</code> component by
  * calling the constructor of its superclass and makes
  * itself a listener for property change events. </p>
  */
  public JdbComboBox() {
    super();
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbComboBox</code> component by
  * calling the constructor of its superclass, passing a
  * <code>Vector</code> for the <code>items</code>
  * parameter, and makes itself a listener for property
  * change events. </p>
  *
  * @param items The <code>Vector</code> that becomes the
  * list of the combobox.
  */
  public JdbComboBox(Vector items) {
    super(items);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbComboBox</code> component by
  * calling the constructor of its superclass, passing an
  * array of <code>Objects</code> for the
  * <code>items</code> parameter, and makes itself a
  * listener for property change events.</p>
  *
  * @param items The array of <code>Objects</code> that becomes the list of the combobox.
  */
  public JdbComboBox(final Object items[]) {
    super(items);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbComboBox</code> component by
  * calling the constructor of its superclass, passing a
  * <code>ComboBoxModel</code> as its model, and makes
  *  itself a listener for property change events. </p>
  *
  * @param model The <code>ComboBoxModel</code> that contains the data for the combobox.
  */
  public JdbComboBox(ComboBoxModel model) {
    super(model);
    commonInit();
  }

 /**
  * <p>Calls the <code>addPropertyChangeListener()</code>
  *  method and passes it <code>this</code>.</p>
  */
  protected void commonInit() {
    addPropertyChangeListener(this);
  }


 /**
  * <p>Updates the UI of the combobox. </p>
  */
  public void updateUI() {
    String currentLookAndFeel = UIManager.getLookAndFeel().getID();
    if (currentLookAndFeel.equals("Motif")) {  
      setUI(MotifJdbComboBoxUI.createUI(this));
    }
    else if (currentLookAndFeel.equals("Metal")) {  
      setUI(MetalJdbComboBoxUI.createUI(this));
    }
    else {
      setUI(WindowsJdbComboBoxUI.createUI(this));
    }
  }

 /**
  * <p>Sets an array of <code>Strings</code> to fill the
  * combobox drop-down list. When an item is selected, it
  * is written to the <code>DataSet</code> specified
  * by the <code>dataSet</code> property.  The
  * <code>items</code> property takes precedence over a
  * <code>pickList</code> defined on by the
  * <code>dataSet</code> property.</p>
  *
  * @param items An array of <code>Strings</code> to fill the drop-down list.
  * @see #getItems
  * @see #setItems
  */
  public synchronized void setItems(final String[] items) {
    setItems((Object []) items);
  }

 /**
  * <p>Sets an array of <code>Objects</code> to fill the
  * combobox drop-down list. When an item is selected, it
  * is written to the <code>DataSet</code> specified
  * by the <code>dataSet</code> property.  The
  * <code>items</code> property takes precedence over a
  * <code>pickList</code> defined by the
  * <code>dataSet</code> property.</p>
  *
  * <p>When using this property to fill the drop-down
  * list, be sure the corresponding <code>DataSet</code>
  * column is of a suitable type.</p>
  *
  * <p>Note that <code>JComboBox</code>'s default renderer
  * simply does a <code>toString()</code> method call on
  * all objects other than <code>Icons</code>.  Remember
  * to specify a custom combobox renderer if your object
  * has more sophisticated rendering needs.</p>
  *
  * @param items An array of <code>Objects</code> to fill the drop-down list.
  * @see #getItems
  * @see #setItems
  */
  public synchronized void setItems(final Object[] items) {
    this.items = items;
    if (items == null || items.length == 0) {
      setModel(new DefaultComboBoxModel());
    }
    else {
    // setting the model has the side effect of resetting the
    // picklist model to null on the model property change
      setModel(new DefaultComboBoxModel(items));
    }
    setSelectedItem((items == null || items.length == 0) ? null : items[0]);
  }

  /**
   * <p>Returns, as an array of <code>Strings</code>, the objects set via the <code>setItems</code> method.
  *
  * @return The array of <code>Strings</code> in the  drop-down list.
  * @see #setItems
  */
  public synchronized String[] getItems() {
    if (items == null) {
      return new String[0];
    }

    String [] stringifiedItems = new String[getModel().getSize()];

    Object element;
    for (int i = 0; i < stringifiedItems.length; i++) {
      element = getModel().getElementAt(i);
      stringifiedItems[i] = element == null ? "" : element.toString();  
    }
    return stringifiedItems;
  }

 /**
  * <p>Sets the index of the selected item in the combobox drop-down list. </p>
  *
  * @param selectedIndex The index of the selected item.
  * @see #getSelectedIndex
  */
  public void setSelectedIndex(int selectedIndex) {
    if (getModel() instanceof DBComboBoxModel) {
      ((DBComboBoxModel) getModel()).setSelectedIndex(selectedIndex);
    }
    else {
      super.setSelectedIndex(selectedIndex);
    }
  }

 /**
  * <p>Returns the index of the selected item in the combobox drop-down list. </p>
  *
  * @return The index of the selected item.
  * @see #setSelectedIndex
  */
  public int getSelectedIndex() {
    if (getModel() instanceof DBComboBoxModel) {
      return ((DBComboBoxModel) getModel()).getSelectedIndex();
    }
    return super.getSelectedIndex();
  }

  /**
   * <p>Sets the <code>DataSet</code> from which data values are read and to which data values are written. </p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    if (this.dataSet != null) {
      this.dataSet.removeAccessListener(this);
      this.dataSet.removeDataChangeListener(this);
      this.dataSet.removeNavigationListener(this);
      removeItemListener(this);
      removeFocusListener(this);
    }
    this.dataSet = dataSet;
    if (dataSet != null) {
      dataSet.addAccessListener(this);
      dataSet.addDataChangeListener(this);
      dataSet.addNavigationListener(this);
      addItemListener(this);
      addFocusListener(this);
    }
    if (getModel() instanceof DBComboBoxModel) {
      ((DBComboBoxModel) getModel()).setDataSet(dataSet);
    }
    openDataSet();
  }

  /**
   * <p>Returns the <code>DataSet</code> from which data
   * values are read and to which data values are
   * written. </p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * <p>Sets the column in the selected
   * <code>DataSet</code> from which data values are read
   * and to which data values are written. </p>
   *
   * @param columnName The column in the <code>DataSet</code>.
   * @see #getColumnName
   */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
    openDataSet();
  }

  /**
   * <p>Returns the column in a <code>DataSet</code> from
   * which data values are read and to which data values
   * are written. </p>
   *
   * @return The column in the <code>DataSet</code>.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * <p>Sets the desired width in pixels of the combobox
   * drop-down list. If this property is
   * left at its default value (-1), the drop-down list is
   * the same width as the combobox.</p>
   *
   * @param dropDownWidth The width in pixels of the drop-down list.
   * @see #getDropDownWidth
   */
  public void setDropDownWidth(int dropDownWidth) {
    int oldWidth = this.dropDownWidth;
    this.dropDownWidth = dropDownWidth;
    firePropertyChange("dropDownWidth", oldWidth, dropDownWidth);   
  }

  /**
   * <p>Returns the width in pixels of the combobox
   * drop-down list. If this property is
   * left as its default value (-1), the drop-down list is
   * the same width as the combobox.</p>
   *
   * @return The width in pixels of the drop-down list.
   * @see #setDropDownWidth
   */
  public int getDropDownWidth() {
    return dropDownWidth;
  }

  /**
   * <p>Sets the fixed height, in pixels, for cells in the
   * combobox drop-down list. <code>JdbComboBox</code>
   * automatically sets this value appropriately when
   * displaying <code>String</code> data in its drop-down
   * list. To allow non-<code>String</code> data of
   * variable heights in the combobox, keep the default
   * value of -1. </p>
   *
   * @param cellHeight The fixed height for cells in the drop-down list.
   * @see #getFixedCellHeight
   */
  public void setFixedCellHeight(int cellHeight) {
    int oldCellHeight = this.cellHeight;
    this.cellHeight = cellHeight;
    firePropertyChange("fixedCellHeight", oldCellHeight, cellHeight);   
  }

  /**
   * <p>Returns the fixed height for cells in the combobox
   * drop-down list.</p>
   *
   * @return The height of cells in the drop-down list.
   * @see #setFixedCellHeight
   */
  public int getFixedCellHeight() {
    return cellHeight;
  }

  //
  // ItemListener Implementation
  //
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (getModel() instanceof DBComboBoxModel) {
        updateSelectedItem();
      }
      else {
        Column column;
        if (dataSet != null && dataSet.isOpen() && (column = dataSet.hasColumn(columnName)) != null) {
          Object item = getSelectedItem();
          Variant value = new Variant();
          try {
            if (item != null) {
              if (item instanceof String) {
                column.getFormatter().parse((String) item, value);
              }
              else {
                value.setAsObject(item, column.getDataType());
              }
            }
            else {
              value.setAssignedNull();
            }
            if (!ignoreValueChange) {
              dataSet.setDisplayVariant(column.getOrdinal(), value);
            }
          }
          catch (Exception ex) {
            DBExceptionHandler.handleException(dataSet, this, ex);
          }
        }
      }
    }
  }


  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(this, dataSet);
  }

  public void focusLost(FocusEvent e) {
  }
  // overrides JComboBox's implmentation of same method just to
  // ignore null values when traversing the model, which can occur
  // when using DataSet data in the model.

  /**
   * Overrides the <code>JComboBox</code> implmentation of
   * same method just to ignore null values when
   * traversing the model, which can occur when using
   * <code>DataSet</code> data in the model.
   */
  public void contentsChanged(ListDataEvent e) {
    // This if for bug 202972. JDK 1.3 called fireActionEvent in setSelectedItem().
    // In JDK 1.4, it is called in contentsChanged(). Since JDK 1.4 no longer
    // runs into the null value in the data, we can just call super.contentsChanged().
    if (DBUtilities.islpt4()) {
      super.contentsChanged(e);
    }
    else {
      ComboBoxModel mod = getModel();
      Object newSelectedItem = mod.getSelectedItem();

      if (selectedItemReminder == null) {
        if (newSelectedItem != null)
          selectedItemChanged();
      }
      else {
        if (!selectedItemReminder.equals(newSelectedItem))
          selectedItemChanged();
      }

      if (!isEditable() && newSelectedItem != null) {
        int i, c;
        boolean shouldResetSelectedItem = true;
        Object o;
        Object selectedItem = mod.getSelectedItem();

        for (i = 0, c = mod.getSize(); i < c; i++) {
          o = mod.getElementAt(i);
//        if (o.equals(selectedItem) ) {
          if (o != null && o.equals(selectedItem)) {
            shouldResetSelectedItem = false;
            break;
          }
        }

        if (shouldResetSelectedItem) {
          if (mod.getSize() > 0)
            setSelectedIndex(0);
          else
            setSelectedItem(null);
        }
      }
    }
  }

  // overrides default addNotify() implementation to open dataset before component is made visible
  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      openDataSet();
    }
  }

  private void openDataSet() {
    if (addNotifyCalled && dataSet != null && columnName != null) {
      if (!dataSet.isOpen()) {
        try {
          dataSet.open();
        }
        catch (DataSetException ex) {
          DBExceptionHandler.handleException(dataSet, this, ex);
          return;
        }
      }
      else {
        bindColumnProperties();
      }
    }
  }

  //
  // AccessListener Implementation (DataSet)
  //
  public void accessChange(AccessEvent event) {
    // our target dataset
    if (event.getSource() == dataSet) {
      if (event.getID() == AccessEvent.CLOSE) {
        if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
          dataSetEventsEnabled = false;
          return;
        }
        if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
          rebindColumnProperties = true;
        }
      }
      else {
//        if (event.getReason() == AccessEvent.DATA_CHANGE) {
          dataSetEventsEnabled = true;
//        }
        if (event.getReason() == AccessEvent.UNSPECIFIED || rebindColumnProperties || event.getReason() == AccessEvent.DATA_CHANGE) {
          bindColumnProperties();
        }
      }
    }
    else if (event.getSource() == pickListView) {
      if (event.getID() == AccessEvent.CLOSE) {
        // if the structure of the picklist changed, we need to
        // recreate our (cached) DataSetView.
        rebuildPickListView = true;
        // but don't need to rebuild if there's only a property change
        if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
          rebuildPickListView = false;
        }
        if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
          pickListDataSetEventsEnabled = false;
          return;
        }
      }
      else {
        // OPEN event
//        if (event.getReason() == AccessEvent.DATA_CHANGE) {
          pickListDataSetEventsEnabled = true;
//        }
        if (rebuildPickListView) {
          rebuildPickListView = false;
          ignoreValueChange = true;
          setPickListModel(pickListView);
          ignoreValueChange = false;
        }
      }
    }
  }

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds font, foreground, and background properties from column
  // if not explicitly set on combobox
  // also opens picklist dataset and sets it as model
  private void bindColumnProperties() {
    if (isDisplayable()) {
      // if we have a valid column, merge its properties
      if (dataSet != null && dataSet.isOpen() && (column = dataSet.hasColumn(columnName)) != null) {
        if (isDefaultProperty(getBackground())) {
          if (column.getBackground() != null) {
            setBackground(column.getBackground());
          }
        }
        if (isDefaultProperty(getForeground())) {
          if (column.getForeground() != null) {
            setForeground(column.getForeground());
          }
        }
        if (isDefaultProperty(getFont())) {
          if (column.getFont() != null) {
            setFont(column.getFont());
          }
        }

        PickListDescriptor pickList = column.getPickList();
        if (items == null && pickList != null) {
          DataSet pickListDataSet = pickList.getPickListDataSet();
          if (pickListDataSet != null) {
            try {
              if (!pickListDataSet.isOpen()) {
                pickListDataSet.open();
              }
              ignoreValueChange = true;
              setPickListModel(pickListDataSet.cloneDataSetView());
              ignoreValueChange = false;
            }
            catch (DataSetException e) {
              DBExceptionHandler.handleException(pickListDataSet, this, e);
              setPickListModel(null);
            }
          }
        }
        else {
          // for performance, set a fixed cell height for String columns
          if (cellHeight == -1 && column.getDataType() == Variant.STRING) {
            FontMetrics fontMetrics = getGraphics().getFontMetrics(getFont());
            setFixedCellHeight(fontMetrics.getHeight());
          }
        }

        if (isEnabled() && !column.isEditable()) {
          setEnabled(false);
        }
        rebindColumnProperties = false;
      }
      updateSelectedItem();
    }
  }

  // pickListView should be null or already open when passed as a parameter
  private void setPickListModel(DataSetView pickListView) {

    if (this.pickListView != null) {
      this.pickListView.removeAccessListener(this);
      if (this.pickListView != pickListView) {
        try {
          this.pickListView.close();
        }
        catch (DataSetException e) {
          DBExceptionHandler.handleException(this.pickListView, this, e);
        }
      }
      // should be after close so DBComboBoxModel gets AccessEvent.CLOSE event
      if (dbComboBoxModel != null) {
        dbComboBoxModel.setDataSet(null);
        dbComboBoxModel.setPickListDescriptor(null);
        pickListLocateRow = null;
        selectionLocateRow = null;
      }
    }

    this.pickListView = pickListView;

    Column pickListColumn;
    PickListDescriptor pickList;
    if (dataSet != null && dataSet.isOpen() && (pickListColumn = dataSet.hasColumn(columnName)) != null &&
        pickListColumn != null && (pickList = pickListColumn.getPickList()) != null && pickListView != null) {

      pickListView.addAccessListener(this);

//      if (dbComboBoxModel == null) {
        dbComboBoxModel = new DBComboBoxModel();
//      }

      dbComboBoxModel.setDataSet(pickListView);
      dbComboBoxModel.setPickListDescriptor(pickList);

      String [] displayColumns = pickList.getPickListDisplayColumns();
      sourceColumns = pickList.getPickListColumns();
      targetColumns = pickList.getDestinationColumns();
      String lookupColumnName = pickList.getLookupDisplayColumn();

      Column lookupColumn = null;
      if (lookupColumnName != null && (lookupColumn = pickListView.hasColumn(lookupColumnName)) != null) {
        int modelIndex = -1;
        for (int colNo = 0, maxCols = dbComboBoxModel.getColumnCount(); colNo < maxCols; colNo++) {
          if (dbComboBoxModel.getColumn(colNo).getColumnName().equalsIgnoreCase(lookupColumnName)) {
            modelIndex = colNo;
            break;
          }
        }
        dbComboBoxModel.setModelColumnIndex(modelIndex);
      }
//      else {
//        dbComboBoxModel.setModelColumnIndex(-1);
//      }
      else if (sourceColumns.length > 0 && (column = pickListView.hasColumn(sourceColumns[0])) != null) {
        // if a display column hasn't been set, then try to use the first
        // source column
        dbComboBoxModel.setModelColumnIndex(column.getOrdinal());
      }

      if (lookupColumn == null) {
        lookupColumn = column;
      }

      Graphics g;
      if (cellHeight == -1 && lookupColumn.getDataType() == Variant.STRING && (g = getGraphics()) != null) {
        FontMetrics fontMetrics = g.getFontMetrics(getFont());
        setFixedCellHeight(fontMetrics.getHeight());
      }

      ignoreModelChange = true;
      setModel(dbComboBoxModel);
      ignoreModelChange = false;
    }

  }

  //
  // NavigationListener Implementation (DataSet)
  //
  public void navigated(NavigationEvent e) {
    if (!dataSetEventsEnabled) {
      return;
    }
    updateSelectedItem();
  }

   // updates combobox's selected item to represent current dataset column value
 /**
  * Updates the combobox's selected item to represent current dataset column value.
  */
  protected void updateSelectedItem() {
    int newIndex = -1;

    ignoreValueChange = true;
    if (getModel() instanceof DBComboBoxModel) {
      try {
        // make a locateRow to navigate the picklist DataSet
       if (pickListLocateRow == null) {
          pickListLocateRow = new DataRow(pickListView, sourceColumns);
       }

        ReadRow.copyTo(targetColumns, dataSet, sourceColumns, pickListLocateRow);

        if (pickListView.locate(pickListLocateRow, Locate.FIRST)) {
          newIndex = pickListView.getRow();
        }
      }
      catch (DataSetException ex) {
        DBExceptionHandler.handleException(pickListView, ex);
      }
    }
    else {
      int lastRow = getModel().getSize();
      Variant value = new Variant();
      try {
        dataSet.getVariant(columnName, dataSet.getRow(), value);
      }
      catch (DataSetException ex) {
        DBExceptionHandler.handleException(pickListView, ex);
      }
      Object locateValue = value.getAsObject();
      for (int index = 0; index < lastRow; index++) {
        if (getModel().getElementAt(index).equals(locateValue)) {
          newIndex = index;
          break;
        }
      }
    }

    ignoreValueChange = true;
    if (newIndex == -1) {
      Column column = dataSet.hasColumn(columnName);
      if (column != null) {
        Variant value = new Variant();
        try {
          dataSet.getDisplayVariant(column.getOrdinal(), dataSet.getRow(), value);
        }
        catch (DataSetException ex) {
          DBExceptionHandler.handleException(dataSet, ex);
        }
        setSelectedItem(value.getAsObject());
      }
      else {
        setSelectedItem(null);
      }
    }
    else {
      setSelectedIndex(newIndex);
    }
    ignoreValueChange = false;
  }

  //
  // DataChangeListener interface implementation
  //
  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    int currentRow = dataSet.getRow();
    int affectedRow = event.getRowAffected();

    if (currentRow == affectedRow) {
      updateSelectedItem();
    }
  }

  public void postRow(DataChangeEvent event) throws Exception {
  }

  //
  // javax.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("model")) {   
      if (!ignoreModelChange && e.getOldValue() != null && e.getOldValue() instanceof DBComboBoxModel) {
        setPickListModel(null);
      }
    }
  }

  public class DBComboBoxModel extends DBTableModel
    implements ComboBoxModel {

    public DBComboBoxModel() {
      super();
    }

    public DBComboBoxModel(DataSet dataSet) {
      super(dataSet);
    }

    public void setSelectedItem(Object selectedItem) {
      this.selectedItem = selectedItem;
      selectedIndex = -1;
      if (selectedItem != null && isValidDataSetState()) {
        Variant selectedVariantItem = null;
        if (selectedItem instanceof Variant) {
          selectedVariantItem = (Variant) selectedItem;
        }
        else {
          Variant variant = new Variant();
          try {
            if (selectedItem instanceof String) {
              variant.setFromString(getColumn(modelColumnIndex).getDataType(), (String) selectedItem);
            }
            else {
              variant.setAsObject(selectedItem, getColumn(modelColumnIndex).getDataType());
            }
          }
          catch (Exception e) {
            try {
              ValidationException.invalidFormat(e, getColumn(modelColumnIndex).getColumnName(), Res._InvalidSelectedItem);     
            }
            catch (ValidationException ex) {
              DBExceptionHandler.handleException(this.dataSet, ex);
              return;
            }
          }
          selectedVariantItem = variant;
        }
        try {
          if (selectionLocateRow == null) {
            selectionLocateRow = new DataRow(this.dataSet, getColumn(modelColumnIndex).getColumnName());
          }
          selectionLocateRow.setVariant(getColumn(modelColumnIndex).getColumnName(), selectedVariantItem);
          if (!this.dataSet.locate(selectionLocateRow, Locate.FIRST)) {
            selectedIndex = -1;
            JOptionPane.showMessageDialog(null,
                                          Res._NavLocateFailed,     
                                          null,
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
          selectedIndex = this.dataSet.getRow();
          if (!ignoreValueChange) {
            ReadRow.copyTo(sourceColumns, pickListView, targetColumns, JdbComboBox.this.dataSet);
          }
        }
        catch (DataSetException e) {
          DBExceptionHandler.handleException(this.dataSet, e);
        }
      }
      fireContentsChanged(this, -1, -1);
    }

    public Object getSelectedItem() {
      return selectedItem;
    }

    public void setSelectedIndex(int selectedIndex) {
      selectedItem = null;
      this.selectedIndex = selectedIndex;
      if (isValidDataSetState()) {
        if (selectedIndex == -1) {
          return;
        }
        try {
          if (this.dataSet.goToRow(selectedIndex)) {
            if (!ignoreValueChange) {
              selectedItem = getElementAt(selectedIndex);
              ReadRow.copyTo(sourceColumns, pickListView, targetColumns, JdbComboBox.this.dataSet);
            }
          }
          else {
            this.selectedIndex = -1;
            JOptionPane.showMessageDialog(null,
                                          Res._PickListGoToFailed,     
                                          null,
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
        }
        catch (DataSetException e) {
          this.selectedIndex = -1;
          selectedItem = null;
          DBExceptionHandler.handleException(this.dataSet, e);
          return;
        }
        selectedItem = getElementAt(selectedIndex);
      }
      fireContentsChanged(this, -1, -1);
    }

    public int getSelectedIndex() {
      return selectedIndex;
    }

    public int getSize() {
      return getRowCount();
    }

    public Object getElementAt(int index) {
      if (modelColumnIndex == -1) {
        Variant value = new Variant();
        try {
          JdbComboBox.this.dataSet.getDisplayVariant(column.getOrdinal(), JdbComboBox.this.dataSet.getRow(), value);
          return value.getAsObject();
        }
        catch (DataSetException ex) {
          DBExceptionHandler.handleException(JdbComboBox.this.dataSet, ex);
        }
        return null;
      }
      else {
        return getValueAt(index, modelColumnIndex);
      }
    }

    public void setModelColumnIndex(int modelColumnIndex) {
      this.modelColumnIndex = modelColumnIndex;
    }

    public int getModelColumnIndex() {
      return modelColumnIndex;
    }

    public Column getColumn(int columnIndex) {
      if (columnIndex == -1) {
        return column;
      }
      else {
        return super.getColumn(columnIndex);
      }
    }

    public void setPickListDescriptor(PickListDescriptor pickListDescriptor) {
      this.pickListDescriptor = pickListDescriptor;
    }

    public PickListDescriptor getPickListDescriptor() {
      return pickListDescriptor;
    }

    // extend superclass' method to resync selected item on sort
    public void accessChange(AccessEvent event) {
      super.accessChange(event);
      if (event.getID() == AccessEvent.OPEN) {
//        if (event.getReason() == AccessEvent.DATA_CHANGE) {
          pickListDataSetEventsEnabled = true;
//        }
        Object oldSelectedItem = selectedItem;
        int oldIndex = selectedIndex;
        setSelectedItem(null);
        fireContentsChanged(this, 0, getSize() - 1);
        if (oldSelectedItem != null && oldIndex == -1) {  // happens if selectedItem was set before dataSet opened
          setSelectedItem(oldSelectedItem);
        } else if (oldSelectedItem == null && oldIndex != -1) {  // happens if selectedIndex was set before dataSet opened
          setSelectedIndex(oldIndex);
        } else { // sync with dataSet
//          setSelectedIndex(this.dataSet.getRow());
        }
      }
      else {  // CLOSE
        if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
          pickListDataSetEventsEnabled = false;
          return;
        }
        // skip removal if we know we're going to open again right away
        if (event.getReason() != AccessEvent.PROPERTY_CHANGE) {
          setSelectedItem(null);   // should set selectedItem to null and selectedIndex to -1 (init state)
          fireContentsChanged(this, 0, getSize() - 1);
        }
      }
    }

    public void dataChanged(DataChangeEvent e) {
      if (!pickListDataSetEventsEnabled) {
        return;
      }
      ignoreValueChange = true;
      super.dataChanged(e);
      if (e.multiRowChange()) {
        fireContentsChanged(this, 0, getSize() - 1);
      }
      else {
        int row = e.getRowAffected();
        switch(e.getID()) {
        case DataChangeEvent.ROW_ADDED:
          fireIntervalAdded(this, row, row);
          break;
        case DataChangeEvent.ROW_DELETED:
          fireIntervalRemoved(this, row, row);
          break;
        case DataChangeEvent.ROW_CHANGED:
          fireContentsChanged(this, row, row);
          break;
        case DataChangeEvent.ROW_CHANGE_POSTED:
        case DataChangeEvent.DATA_CHANGED:
        default:
          fireContentsChanged(this, 0, getSize() - 1);
        }
      }
      ignoreValueChange = false;
    }

    // ListModel implementation taken from javax.swing.AbstractListModel
    protected EventListenerList listenerList = new EventListenerList();

    public void addListDataListener(ListDataListener l) {
      listenerList.add(ListDataListener.class, l);
    }
    public void removeListDataListener(ListDataListener l) {
      listenerList.remove(ListDataListener.class, l);
    }

    protected void fireContentsChanged(Object source, int index0, int index1)
    {
      Object[] listeners = listenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ListDataListener.class) {
          if (e == null) {
            e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
          }
          ((ListDataListener)listeners[i+1]).contentsChanged(e);
        }
      }
    }

    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
      Object[] listeners = listenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ListDataListener.class) {
          if (e == null) {
            e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
          }
          ((ListDataListener)listeners[i+1]).intervalAdded(e);
        }
      }
    }

    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
      Object[] listeners = listenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ListDataListener.class) {
          if (e == null) {
            e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
          }
          ((ListDataListener)listeners[i+1]).intervalRemoved(e);
        }
      }
    }

    private Object selectedItem;
    private int selectedIndex = -1;
    private int modelColumnIndex = -1;
    private PickListDescriptor pickListDescriptor;
  }

  private DataSet dataSet;
  private Column column;
  private String columnName;

  private Object [] items;

  private DBComboBoxModel dbComboBoxModel;
//  private TableCellRenderer renderer = new DefaultTableCellRenderer();

  private boolean ignoreModelChange;

  private boolean addNotifyCalled;
  private int dropDownWidth = -1;
  private int cellHeight = -1;
  private boolean rebindColumnProperties;

  private DataSetView pickListView;
  private String [] sourceColumns;
  private String [] targetColumns;
  private boolean rebuildPickListView = true;

  private DataRow pickListLocateRow;
  private DataRow selectionLocateRow;

  private boolean ignoreValueChange;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
  private boolean pickListDataSetEventsEnabled = true;

}

