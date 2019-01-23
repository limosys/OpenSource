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

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

import com.borland.dbswing.plaf.windows.*;
import com.borland.dbswing.plaf.metal.*;
import com.borland.dbswing.plaf.motif.*;
import com.borland.dx.dataset.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


/**
 * <p>Extends the <code>JComboBox</code> class, allowing
 * it to connect to, and navigate a <code>DataSet</code>
 * through <code>dataSet</code> and
 * <code>columnName</code> properties.</p>
 *
 * <p><code>JdbNavComboBox</code> has the following properties:</p>
 *
 * <ul>
 * <li><code>dataSet</code> - The <code>DataSet</code> navigated via the combo box's selected item.</li>
 * <li><code>columnName</code> - The name of the column of the <code>DataSet</code> searched to locate the combo                          box's selected item.</li>
 * <li><code>dropDownWidth</code> - The desired width of combo box's drop-down list.</li>
 * <li><code>fixedCellHeight</code> - The desired fixed height of items in combo box's drop-down list.</li>
 * </ul>
 *
 * <p>When you select an item from the
 * <code>JdbNavComboBox</code>, it moves the pointer in
 * the <code>DataSet</code> to the corresponding row. Any
 * updates made to the items displayed in the
 * <code>JdbNavComboBox</code> are ignored. </p>
 *
 * <P>Unlike <code>JdbComboBox</code>,
 * <code>JdbNavComboBox</code> does not fill its list from
 * a picklist defined on the column it is bound to. Its
 * list comes directly from the column it is bound to, and
 * always displays just a single column.</p>
 *
 * <p><code>JdbNavComboBox</code> usually works best when
 * its <code>DataSet</code> is filtered and sorted so that
 * values in the <code>Column</code> it is bound to are
 * unique and ordered.</p>

 * <p>If there are duplicate values in a
 * <code>JdbNavComboBox</code>, selection from the
 * drop-down list will navigate the <code>DataSet</code>
 * to the corresponding row. If you type into the selected
 * text area of an editable <code>JdbNavComboBox</code>,
 * you will always navigate to the first matching row.</p>
 *
 * @see JdbComboBox
 */

public class JdbNavComboBox extends JComboBox
  implements AccessListener, ColumnAware, java.io.Serializable, FocusListener
{

 /**
  * <p>Constructs a <code>JdbNavComboBox</code> component by calling the <code>null</code> constructor of its superclass.</p>
  */
  public JdbNavComboBox() {
    super();
    setModel(new DBNavComboBoxModel());
  }


 /**
  * <p>Constructs a <code>JdbNavComboBox</code> component by calling the <code>null</code> constructor of its superclass and sets the <code>columnName</code> and <code>DataSet</code> properties.</p>
  *
  * @param dataSet The <code>DataSet</code> in which navigation occurs.
  * @param columnName The name of the <code>Column</code> of the <code>DataSet</code> in which navigation occurs.
  */
  public JdbNavComboBox(DataSet dataSet, String columnName) {
    super();
    setDataSet(dataSet);
    setColumnName(columnName);
    setModel(new DBNavComboBoxModel(dataSet, columnName));
  }

/**
 * <p>Updates the UI of the combo box.</p>
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
 * <p>Calls the <code>addNotify()</code> method of its
 * superclass and opens the <code>DataSet</code> if the
 * <code>dataSet</code> property is not <code>null</code>.</p>
 */
  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      openDataSet();
    }
  }

/**
 * <p>Sets the index value of the selected item in the combo box.</p>
 *
 * @param selectedIndex The index value.
 * @see #getSelectedIndex
 */
  public void setSelectedIndex(int selectedIndex) {
    if (getModel() instanceof DBNavComboBoxModel) {
      ((DBNavComboBoxModel) getModel()).setSelectedIndex(selectedIndex);
    }
    else {
      super.setSelectedIndex(selectedIndex);
    }
  }

/**
 * <p>Returns the index value of the selected item in the combo box.</p>
 *
 * @return The index value.
 * @see #setSelectedIndex
 */
  public int getSelectedIndex() {
    if (getModel() instanceof DBNavComboBoxModel) {
      return ((DBNavComboBoxModel) getModel()).getSelectedIndex();
    }
    return super.getSelectedIndex();
  }

  /**
   * <p>Sets the <code>DataSet</code> in which the navigation occurs.</p>
   * @param dataSet The <code>DataSet</code> in which the navigation occurs.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    if (this.dataSet != null) {
      this.dataSet.removeAccessListener(this);
      removeFocusListener(this);
    }
    this.dataSet = dataSet;
    if (dataSet != null) {
      dataSet.addAccessListener(this);
      addFocusListener(this);
    }
    if (getModel() instanceof DBNavComboBoxModel) {
      ((DBNavComboBoxModel) getModel()).setDataSet(dataSet);
    }
    openDataSet();
  }

  /**
   * <p>Returns the <code>DataSet</code> in which the navigation occurs.</p>
   * @return The <code>DataSet</code> in which the navigation occurs.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * <p>Sets the name of the <code>Column</code> of the <code>DataSet</code> in which the navigation occurs.</p>
   *
   * @param columnName The <code>Column</code> in which the navigation occurs.
   * @see #getColumnName
   */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
    if (getModel() instanceof DBNavComboBoxModel) {
      ((DBNavComboBoxModel) getModel()).setColumnName(columnName);
    }
    openDataSet();
  }

  /**
   * <p>Returns the name of the <code>Column</code> of the <code>DataSet</code> in which the navigation occurs.</p>
   * @return The <code>Column</code> in which the navigation occurs.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * <p>Sets the desired width of the combobox's drop-down list. If this property is left at its default value (-1), the drop-down list is the same width as the combo box.</p>
   *
   * @param dropDownWidth The desired width of the combobox's drop-down list.
   * @see #getDropDownWidth
   */
  public void setDropDownWidth(int dropDownWidth) {
    int oldWidth = this.dropDownWidth;
    this.dropDownWidth = dropDownWidth;
    firePropertyChange("dropDownWidth", oldWidth, dropDownWidth);   
  }

  /**
   * <p>Returns the width of the combobox's drop-down list. If the value is -1, the drop-down list is the same width as the combo box.</p>
   *
   * @return The width of the combobox's drop-down list.
   * @see #setDropDownWidth
   */
  public int getDropDownWidth() {
    return dropDownWidth;
  }


  /**
   * <p>Sets the fixed height for cells in the combo box's
   * drop-down list. <code>JdbComboBox</code>
   * automatically sets this value appropriately when
   * displaying <code>String</code> data in its drop-down
   * list. To allow non-<code>String</code> data of
   * variable heights in the combo box, keep the default
   * value of -1.</p>
   *
   * @param cellHeight The desired height of the combobox's drop-down list.
   * @see #getFixedCellHeight
   */
  public void setFixedCellHeight(int cellHeight) {
    int oldCellHeight = this.cellHeight;
    this.cellHeight = cellHeight;
    firePropertyChange("fixedCellHeight", oldCellHeight, cellHeight);   
  }

  /**
   * <p>Returns the fixed height for cells in the combo
   * box's drop-down list. </p>
   *
   * @return The height of the combobox's drop-down list.
   * @see #setFixedCellHeight
   */

  public int getFixedCellHeight() {
    return cellHeight;
  }

  // overrides JComboBox's implmentation of same method just to
  // ignore null values when traversing the model, which can occur
  // when using DataSet data in the model.
  public void contentsChanged(ListDataEvent e) {
    // This if for bug 192482. JDK 1.3 called fireActionEvent in setSelectedItem().
    // In JDK 1.4, it is called in contentsChanged(). Since JDK 1.4 no longer
    // runs into the null value in the data, we can just call super.contentsChanged().
    if (DBUtilities.islpt4()) {
      super.contentsChanged(e);
    }
    else {
      ComboBoxModel mod = getModel();
      Object newSelectedItem = mod.getSelectedItem();

      if (selectedItemReminder == null) {
        if (newSelectedItem != null) {
          selectedItemChanged();
        }
      }
      else {
        if (!selectedItemReminder.equals(newSelectedItem)) {
          selectedItemChanged();
        }
      }

      if (!isEditable() && newSelectedItem != null) {
        int i, c;
        boolean shouldResetSelectedItem = true;
        Object o;
        Object selectedItem = mod.getSelectedItem();

        for (i = 0, c = mod.getSize(); i < c; i++) {
          o = mod.getElementAt(i);
          // Test below for "o != null" is what distinguishes this JDK 1.3 code
          if (o != null && o.equals(selectedItem)) {
            shouldResetSelectedItem = false;
            break;
          }
        }

        if (shouldResetSelectedItem) {
          if (mod.getSize() > 0) {
            setSelectedIndex(0);
          }
          else {
            setSelectedItem(null);
          }
        }
      }
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
        }
      }
      else {
        bindColumnProperties();
      }
    }
  }

  // AccessListener Implementation (DataSet)
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
      }
      if (rebindColumnProperties || event.getReason() == AccessEvent.UNSPECIFIED || event.getReason() == AccessEvent.DATA_CHANGE) {
        bindColumnProperties();
      }
    }
    else {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
      }
      if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        rebindColumnProperties = true;
      }
    }
  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(this, dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds font, foreground, and background properties from column
  // if not explicitly set on combo box
  private void bindColumnProperties() {
    Column column;
    if (addNotifyCalled && dataSet != null && columnName != null && (column = dataSet.hasColumn(columnName)) != null) {

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

      // for performance, set a fixed cell height for String columns
      if (cellHeight == -1 && column.getDataType() == Variant.STRING) {
        FontMetrics fontMetrics = getGraphics().getFontMetrics(getFont());
        setFixedCellHeight(fontMetrics.getHeight());
      }
      rebindColumnProperties = false;
    }
  }

  private int dropDownWidth = -1;
  private int cellHeight = -1;
  private DataSet dataSet;
  private String columnName;
  private boolean addNotifyCalled;
  private boolean rebindColumnProperties;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}

class DBNavComboBoxModel extends DBListModel
  implements ComboBoxModel, NavigationListener
{
  public DBNavComboBoxModel() {
    super();
  }

  public DBNavComboBoxModel(DataSet dataSet, String columnName) {
    super(dataSet, columnName);
  }

  public void setDataSet(DataSet dataSet) {
    if (columnAwareSupport.dataSet != null) {
      columnAwareSupport.dataSet.removeNavigationListener(this);
    }
    super.setDataSet(dataSet);
    if (dataSet != null) {
      dataSet.addNavigationListener(this);
    }
  }

  public void setSelectedItem(Object selectedItem) {
    if (!ignoreValueChange) {
      this.selectedItem = selectedItem;
      selectedIndex = -1;
      columnAwareSupport.lazyOpen();
      if (selectedItem != null && columnAwareSupport.isValidDataSetState()) {
        Variant selectedVariantItem = null;
        if (selectedItem instanceof Variant) {
          selectedVariantItem = (Variant) selectedItem;
        }
        else {
          Variant variant = new Variant();
          try {
            variant.setAsObject(selectedItem, columnAwareSupport.getColumn().getDataType());
          }
          catch (Exception e) {
            try {
              ValidationException.invalidFormat(e, columnAwareSupport.columnName, Res._InvalidSelectedItem);     
            }
            catch (ValidationException ex) {
              DBExceptionHandler.handleException(columnAwareSupport.dataSet, ex);
              return;
            }
          }
          selectedVariantItem = variant;
        }
        try {
          if (locateRow == null) {
            locateRow = new DataRow(columnAwareSupport.dataSet, getColumnName());
          }
          locateRow.setVariant(getColumnName(), selectedVariantItem);
          ignoreNavigation = true;
          if (!columnAwareSupport.dataSet.locate(locateRow, Locate.FIRST)) {
            ignoreNavigation = false;
            selectedIndex = -1;
            JOptionPane.showMessageDialog(null,
                                          Res._NavLocateFailed,     
                                          null,
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
          ignoreNavigation = false;
          selectedIndex = columnAwareSupport.dataSet.getRow();
        }
        catch (DataSetException e) {
          DBExceptionHandler.handleException(columnAwareSupport.dataSet, e);
        }
      }
      ignoreValueChange = true;
      fireContentsChanged(this, -1, -1);
      ignoreValueChange = false;
    }
  }

  public Object getSelectedItem() {
    return selectedItem;
  }

  public void setSelectedIndex(int selectedIndex) {
    if (!ignoreValueChange) {
      selectedItem = null;
      this.selectedIndex = selectedIndex;
      columnAwareSupport.lazyOpen();
      if (columnAwareSupport.isValidDataSetState()) {
        if (selectedIndex != columnAwareSupport.dataSet.getRow()) { // Avoid unnecessary navigation
          try {
            ignoreNavigation = true;
            if (!columnAwareSupport.dataSet.goToRow(selectedIndex)) {
              ignoreNavigation = false;
              this.selectedIndex = columnAwareSupport.dataSet.row();
              this.selectedItem = getElementAt(this.selectedIndex);
              JOptionPane.showMessageDialog(null,
                                            Res._NavMoveFailed, 
                                            null,
                                            JOptionPane.INFORMATION_MESSAGE);
              return;
            }
          }
          catch (DataSetException e) {
            this.selectedIndex = columnAwareSupport.dataSet.row();
            this.selectedItem = getElementAt(this.selectedIndex);
            DBExceptionHandler.handleException(columnAwareSupport.dataSet, e);
            return;
          }
          finally {
            ignoreNavigation = false;
          }
        }
        selectedItem = getElementAt(selectedIndex);
      }
      ignoreValueChange = true;
      fireContentsChanged(this, -1, -1);
      ignoreValueChange = false;
    }
  }

  public int getSelectedIndex() {
    return selectedIndex;
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
    if (!ignoreNavigation) {
//      ignoreValueChange = true;
      setSelectedIndex(columnAwareSupport.dataSet.getRow());
//      updateSelectedValue();
//      ignoreValueChange = false;
    }
  }

  protected void updateSelectedValue() {
    Variant value = new Variant();
    columnAwareSupport.dataSet.getVariant(getColumnName(), value);
    setSelectedItem(value.getAsObject());
  }

  // override (replace) superclass' method to resync selected item on sort
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      columnAwareSupport.lazyOpen();
      if (columnAwareSupport.isValidDataSetState()) {
        Object oldSelectedItem = selectedItem;
        int oldIndex = selectedIndex;
        setSelectedItem(null);
        fireContentsChanged(this, 0, getSize() - 1);
        if (oldSelectedItem != null && oldIndex == -1) {  // happens if selectedItem was set before dataSet opened
          setSelectedItem(oldSelectedItem);
        } else if (oldSelectedItem == null && oldIndex != -1) {  // happens if selectedIndex was set before dataSet opened
          setSelectedIndex(oldIndex);
        } else { // sync with dataSet
          setSelectedIndex(columnAwareSupport.dataSet.getRow());
        }
      }
    }
    else {  // CLOSE
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }
      else {
        // skip removal if we know we're going to open again right away
        setSelectedItem(null);   // should set selectedItem to null and selectedIndex to -1 (init state)
        fireContentsChanged(this, 0, getSize() - 1);
      }
    }
  }

  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    int currentRow = columnAwareSupport.dataSet.getRow();
    int affectedRow = event.getRowAffected();

    ignoreValueChange = true;
    if (event.getID() == DataChangeEvent.ROW_ADDED &&
        currentRow == affectedRow) {
      setSelectedItem(null);
    }
    else {
      super.dataChanged(event);
    }
    ignoreValueChange = false;
    /*
    if (event.getID() == DataChangeEvent.DATA_CHANGED) {  // more than one row changed
      setSelectedItem(null);   // should set selectedItem to null and selectedIndex to -1 (init state)
      fireContentsChanged(this, 0, getSize() - 1);
      setSelectedIndex(currentRow);  // update our current row (selection)
    }
    else {
      int affectedRow = event.getRowAffected();
      fireContentsChanged(this, affectedRow, affectedRow);
      if (currentRow != affectedRow) {
        setSelectedIndex(currentRow);  // update our current row (selection)
      }
    }
    */
  }

  /** flag indicating we should ignore change because we caused it */
  private boolean ignoreValueChange;

  /** flag indicating we should ignore navigation event because we caused it */
  private boolean ignoreNavigation;

  private int selectedIndex = -1;
  private Object selectedItem;
  private DataRow locateRow;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}


