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

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.UIResource;

import com.borland.dx.dataset.*;

 /**
  * <p>A data-aware extension of the
  * <code>JList</code> component that connects to and
  * navigates a <code>DataSet</code> through the
  * <code>dataSet</code> and <code>columnName</code>
  * properties. When you select an item from the
  * <code>JdbNavList</code>, it makes the current row
  * in the <code>DataSet</code> the first one whose
  * column value matches the selected item. Any
  * updates made to the items displayed in the
  * <code>JdbNavList</code> are ignored. </p>
  *
  * <p>Unlike <code>JdbList</code>,
  * <code>JdbNavList</code> does not use a picklist
  * defined in its column to fill its list.
  * <code>JdbNavList</code>'s list items always come
  * from the <code>DataSet Column</code> it is bound
  * to. For this reason, <code>JdbNavList</code>
  * usually works best when the values in that column
  * are unique or the <code>DataSet</code> is sorted
  * on that column.</p>
  *
  * <p>If rows are inserted, deleted, or modified in
  * the <code>DataSet</code>, the
  * <code>JdbNavList</code>'s list is updated to
  * reflect the changes.</p>
  *
  * <p>If there are duplicate values in a
  * <code>JdbNavList</code>, selection from the list
  * will correctly find the row which corresponds to
  * the specific instance of the duplicate value which
  * is currently selected.  </p>
  *
  * @see JdbList
  */
public class JdbNavList extends JList
  implements NavigationListener, AccessListener, ListSelectionListener,
             ColumnAware, java.io.Serializable
{

 /**
  * <p>Constructs a <code>JdbNavList</code> component by calling the <code>null</code> constructor of its superclass.</p>
  */
  public JdbNavList() {
    super();
    setModel(new DBListNavModel());
  }

 /**
  * <p>Constructs a <code>JdbNavList</code> component by calling the <code>null</code> constructor of its superclass and then setting the <code>dataSet</code> and <code>columnName</code> properties of <code>JdbNavList</code>.</p>
  *
  * @param dataSet The <code>DataSet</code> that <code>JdbNavList</code> navigates.
  * @param columnName The name of the column within the specified <code>DataSet</code> that <code>JdbNavList</code> navigates.
  */
  public JdbNavList(DataSet dataSet, String columnName) {
    super();
    setDataSet(dataSet);
    setColumnName(columnName);
    setModel(new DBListNavModel(dataSet, columnName));
  }

  /**
   * <p>Sets the <code>DataSet</code> in which the navigation occurs.</p>
   * @param dataSet The <code>DataSet</code> in which the navigation occurs.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    if (this.dataSet != null) {
      this.dataSet.removeNavigationListener(this);
      this.dataSet.removeAccessListener(this);
      getSelectionModel().removeListSelectionListener(this);
    }
    this.dataSet = dataSet;
    if (dataSet != null) {
      dataSet.addNavigationListener(this);
      dataSet.addAccessListener(this);
      getSelectionModel().addListSelectionListener(this);
      if (getModel() instanceof DBListNavModel) {
        ((DBListNavModel) getModel()).setDataSet(dataSet);
      }
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
   * <p>Sets the name of the <code>Column</code> with whose values the <code>JdbNavList</code> is synchronized.</p>
   *
   * @param columnName The <code>Column</code> in which the navigation occurs.
   * @see #getColumnName
   */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
    if (getModel() instanceof DBListNavModel) {
      ((DBListNavModel) getModel()).setColumnName(columnName);
    }
    openDataSet();
  }

  /**
   * <p>Returns the name of the <code>Column</code> with whose values the <code>JdbNavList</code> is synchronized.</p>
   *
   * @return The <code>Column</code> in which the navigation occurs.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnName;
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

  /**
   * <p>Calls the <code>addNotify()</code> method of its superclass and opens the <code>DataSet</code> if the <code>dataSet</code> property is not <code>null</code>.</p>
  */
  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      openDataSet();
    }
  }

  //
  // ListSelectionListener Implementation
  //
  public void valueChanged(ListSelectionEvent e) {
    if (!ignoreNavigation && !e.getValueIsAdjusting()) {
      int index = getSelectedIndex();
      try {
        if (dataSet != null && index > -1 && index < dataSet.getRowCount() &&
            dataSet.getRow() != index) {
          dataSet.goToRow(index);
        }
      }
      catch (DataSetException ex) {
        DBExceptionHandler.handleException(dataSet, this, ex);
      }
    }
  }

  //
  // NavigationListener Implementation (DataSet)
  //
  public void navigated(NavigationEvent e) {
    if (!dataSetEventsEnabled) {
      return;
    }

    int row = dataSet.getRow();
    if (getSelectedIndex() != row) {
      ignoreNavigation = true;
      setSelectedIndex(row);
      ignoreNavigation = false;
    }
    ensureIndexIsVisible(row);
  }

  //
  // AccessListener Implementation (DataSet)
  //
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      if (rebindColumnProperties || event.getReason() == AccessEvent.UNSPECIFIED || event.getReason() == AccessEvent.DATA_CHANGE) {
        bindColumnProperties();

        setSelectedIndex(dataSet.getRow());
      }
    }
    else {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }
      getSelectionModel().clearSelection();
      if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        rebindColumnProperties = true;
      }
    }
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

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      setPrototype(column.getWidth());
      rebindColumnProperties = false;
    }
  }

  // calls setPrototypeCellValue appropriately based on Column dataType
  private void setPrototype(int charWidth) {
    StringBuffer sb = new StringBuffer(charWidth);
    for (int index = 0; index < charWidth; index++) {
      sb.append('M');  
    }
    setPrototypeCellValue(sb.toString());
  }

  public void setSelectionMode(int selectionMode) {
    if (selectionMode != ListSelectionModel.SINGLE_SELECTION) {
      throw new IllegalArgumentException(Res._UnsupSelMode);     
    }
    super.setSelectionMode(selectionMode);
  }

  /**
 * <p>Returns the preferred size of the <code>JdbNavList</code>. </p>
 *
 * @return The preferred size.
 */
  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    if (ps == null || ps.width < 5 || ps.height < 5) {
      ps = ps != null ? ps : new Dimension(100, 100);
      ps.width = ps.width > 100 ? ps.width : 100;
      ps.height = ps.height > 100 ? ps.height : 100;
    }
    return ps;
  }

  private DataSet dataSet;
  private String columnName;
  private boolean addNotifyCalled;
  private boolean ignoreNavigation;
  private boolean rebindColumnProperties;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}

class DBListNavModel extends DBListModel
{
  public DBListNavModel() {
    super();
  }

  public DBListNavModel(DataSet dataSet, String columnName) {
    super(dataSet, columnName);
  }

  // override (replace) superclass' method to resync selected item on sort
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
      }
      columnAwareSupport.lazyOpen();
      if (columnAwareSupport.isValidDataSetState()) {
        fireContentsChanged(this, 0, getSize() - 1);
      }
    }
    else {  // CLOSE
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }

      // skip removal if we know we're going to open again right away
      if (event.getReason() != AccessEvent.STRUCTURE_CHANGE) {
        fireContentsChanged(this, 0, getSize() - 1);
      }
    }
  }

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
