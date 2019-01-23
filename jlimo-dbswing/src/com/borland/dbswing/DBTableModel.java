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

import java.math.*;
import java.sql.*;

import javax.swing.table.*;

import com.borland.dx.dataset.*;

/**
 * <p>An implementation of the Swing <code>TableModel</code>. It obtains its model data from a DataExpress <code>DataSet</code>. <code>DBTableModel</code> is the default <code>TableModel</code> for a <code>JdbTable</code> that has a non-null <code>DataSet</code>. </p>
 *
 * <p><code>DBTableModel</code> ignores DataSet <code>Columns</code> that aren't visible in any column operations in the <code>TableModel</code>. </p>
 *
 * <p>Example: </p>
 *
 * <pre>
 * JTable jTable = new JTable();
 * DBTableModel DBTableModel = new DBTableModel();
 * TableDataSet tableDataSet = new TableDataSet();
 * DBTableModel.setDataSet(tableDataSet);
 * jTable.setModel(DBTableModel);
 * </pre>
 * @see JdbTable
 */
public class DBTableModel extends AbstractTableModel
  implements DataChangeListener, AccessListener, DataSetAware, Designable
{

  /**
   * <p>Constructs a <code>DBTableModel</code> with a <code>null DataSet</code>. Calls the constructor of this class that takes a <code>DataSet</code> as a parameter and passes it <code>null</code>. </p>
   */
  public DBTableModel() {
    this(null);
  }

  /**
   * <p>Constructs a <code>DBTableModel</code> using the specified <code>DataSet</code>.  Calls the <code>null</code> constructor of its superclass. </p>
   *
   * @param dataSet The <code>DataSet</code> from which the <code>DBTableModel</code> obtains its data.
    */
  public DBTableModel(DataSet dataSet) {
    setDataSet(dataSet);
  }

  /**
   * <p>Sets the <code>DataSet</code> used to build this table model.</p>
   *
   * @param dataSet The <code>DataSet</code> used to build this table model.
   * @see #getDataSet
   */

  public void setDataSet(DataSet dataSet) {
    if (this.dataSet != dataSet) {
      if (this.dataSet != null) {
        this.dataSet.removeAccessListener(this);
        this.dataSet.removeDataChangeListener(this);
      }

      this.dataSet = dataSet;
      if (dataSet != null) {
        dataSet.addAccessListener(this);
        dataSet.addDataChangeListener(this);
      }
      updateModel();
      fireTableStructureChanged();
      fireTableDataChanged();
    }
  }

  /**
   * <p>Returns the <code>DataSet</code> used to build this table model.</p>
   *
   * @return The <code>DataSet</code> used to build this table model.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  // updates the model when the DataSet has changed
  private void updateModel() {
    if (isValidDataSetState()) {
      try {
        // totalColumns is the total number of columns,
        // but doesn't take into account non-visible columns.
        // columnCount reflects the number of columns in
        // the model.
        // We assume that the number of invisible columns
        // will be small relative to the total number of
        // columns, so we allocate array caches to the
        // size of total number of columns.
        int totalColumns = dataSet.getColumnCount();
        captionNames = new String[totalColumns];
        columnClasses = new Class[totalColumns];
        lookupTypes = new int[totalColumns];
        isEditable = new boolean[totalColumns];
        columnMap = new int[totalColumns];
        columnCount = 0;
        Column column;
        PickListDescriptor pickListDescriptor;
        for (int colNo = 0; colNo < totalColumns; colNo++) {
          column = dataSet.getColumn(colNo);
          // skip non-visible columns
          if (dataSet.columnIsVisible(column.getColumnName())) {
            captionNames[columnCount] = column.getCaption();
            isEditable[columnCount] = column.isEditable() && !column.isReadOnly() && dataSet.isEditable();
            lookupTypes[columnCount] = -1;

            if ((pickListDescriptor = column.getPickList()) == null) {
              columnClasses[columnCount] = getJavaClass(column.getDataType());
            }
            else {
              DataSet pickListDataSet;
              String lookupColumnName;
              Column lookupColumn;
              if ((pickListDataSet = pickListDescriptor.getPickListDataSet()) != null) {
                if ((lookupColumnName = pickListDescriptor.getLookupDisplayColumn()) == null) {
                  columnClasses[columnCount] = getJavaClass(column.getDataType());
                }
                else if ((lookupColumn = pickListDataSet.hasColumn(lookupColumnName)) != null) {
                  columnClasses[columnCount] = getJavaClass(lookupColumn.getDataType());
                  lookupTypes[columnCount] = lookupColumn.getDataType();
                }
                else {
                  // ignore the column if there's something wrong with the picklist definition
                  continue;
                }
              }
              else {
                // ignore the column if there's something wrong with the picklist definition
                continue;
              }
            }
            columnMap[columnCount] = colNo;
            columnCount++;
          }
        }
      }
      catch (DataSetException e) {
        handleException(e);
        return;
      }
    }
  }

  // returns the Java Class for the specified variant datatype
  static Class getJavaClass(int variantType) {
    switch (variantType) {
      case Variant.UNASSIGNED_NULL:
      case Variant.ASSIGNED_NULL:
        return null;
      case Variant.BYTE:
        return Byte.class;
      case Variant.SHORT:
        return Short.class;
      case Variant.INT:
        return Integer.class;
      case Variant.LONG:
        return Long.class;
      case Variant.FLOAT:
        return Float.class;
      case Variant.DOUBLE:
        return Double.class;
      case Variant.BIGDECIMAL:
        return BigDecimal.class;
      case Variant.BOOLEAN:
        return Boolean.class;
      case Variant.INPUTSTREAM:
        return Object.class;
      case Variant.DATE:
        return java.sql.Date.class;
      case Variant.TIME:
        return Time.class;
      case Variant.TIMESTAMP:
        return Timestamp.class;
      case Variant.STRING:
        return String.class;
      case Variant.OBJECT:
        return Object.class;
      case Variant.BYTE_ARRAY:
        return Object.class;
    }
    return null;
  }

 /**
  * <p>Determines whether the <code>DataSet</code> is in a valid state. The <code>DataSet</code>  is valid (<code>isValidDataSetState()</code> returns <code>true</code>) if:</p>
  *
  * <ul>
  * <li>The value of <code>dataSetChanged</code> is <code>false</code>, </li>
  * <li>There is a specified <code>dataSet</code> value, and </li>
  * <li>The specified <code>DataSet</code> is open. </li>
  * </ul>
  *
  * @return If <code>true</code>, the <code>DataSet</code> is valid.
  */

  protected boolean isValidDataSetState() {
    return dataSet != null && dataSet.isOpen();
  }

  // TableModel interface implementation

  /**
   * <p>Returns the number of rows in the model, which is
   * equal to the number of rows in the <code>DataSet</code>.</p>
   *
   * @return The number of rows in the model (<code>DataSet</code>).
   */
  public int getRowCount() {
    if (isValidDataSetState()) {
      try {
        return dataSet.getRowCount();
      }
      catch (Exception ex) {
        handleException(ex);
      }
    }
    // there was a problem getting the rowCount, return 0;
    return 0;
  }

  /**
   * <p>Returns the number of columns in the model, which is
   * equal to the number of visible <code>Columns</code> in the <code>DataSet</code>.</p>
   *
   * @return The number of columns in the model.
   */
  public int getColumnCount() {
    if (isValidDataSetState()) {
      return columnCount;
    }
    return 0;
  }

  /**
   * <p>Returns the name of column <code>columnIndex</code> in the model.</p>
   *
   * <p>Note that there may not be a 1-1 correspondence between model
   * column indices and <code>DataSet</code> column ordinals because some <code>DataSet
   * Columns</code> may not be visible.</p>
   *
   * @param columnIndex The name of column <code>columnIndex</code>.
   * @return The name of column <code>columnIndex</code> in the model.
   */
  public String getColumnName(int columnIndex) {
    if (isValidDataSetState() && columnIndex >= 0 && columnIndex < captionNames.length) {
      return captionNames[columnIndex];
    }
    return "";   
  }

  /**
   * <p>Returns the class of column <code>columnIndex</code> in the model (<code>DataSet</code>).</p>
   *
   * <p>Note that there may not be a 1-1 correspondence between model
   * column indices and <code>DataSet</code> column ordinals because some <code>DataSet
   * Columns</code> may not be visible.</p>
   *
   * <p>When a picklist with a lookup is defined for a <code>Column</code>, the column
   * class returned is that of the lookup column rather than the actual <code>Column</code>.</p>
   *
   * @param columnIndex The class of column <code>columnIndex</code>.
   * @return The class of column <code>columnIndex</code> in the model (<code>DataSet</code>).
   */
  public Class getColumnClass(int columnIndex) {
    if (isValidDataSetState()) {
      return columnClasses[columnIndex];
    }
    return null;
  }

  /**
   * <p>Returns <code>true</code> if the specified cell is editable.</p>
   *
   * <p>A cell is editable if and only if the <code>dataSet</code> is non-null and
   * open, and the corresponding <code>DataSet Column</code> is editable and not
   * read-only.</p>
   *
  * @param rowIndex The row of the specified cell.
  * @param columnIndex The column of the specified cell.
   * @return If <code>true</code>, the specified cell is editable.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (isValidDataSetState()) {
      return isEditable[columnIndex];
    }
    return false;
  }

  /**
   * <p>Returns the value at the row and column.  Does not cause the
   * current <code>DataSet</code> row to change.</p>
   *
   * <p>If the <code>Column</code> has a <code>pickList</code> property indicating to lookup the
   * value in a different table, the lookup value will be returned instead.</p>
   *
  * @param row The row of the specified cell.
  * @param columnIndex The column of the specified cell.
  * @return The value at the row and column.
  * @see #setValueAt
  */
  public Object getValueAt(int row, int columnIndex) {
    if (isValidDataSetState()) {
      try {
        dataSet.getDisplayVariant(columnMap[columnIndex], row, value);
        return value.getAsObject();
      }
      catch (Exception ex) {
        handleException(ex);
      }
    }
    return null;
  }

  /**
   * <p>Puts <code>aValue</code> into the <code>DataSet</code> associated with this <code>TableModel</code> at the specified row and column index.
   * Also moves the current <code>DataSet</code> row to <code>rowIndex</code> and the current column to <code>columnIndex</code>.</p>
   *
   * <p>Note that the <code>TableModel</code> interface does not allow an exception to be
   * thrown if a value cannot be posted due to formatting errors or
   * constraints on the underlying <code>DataSet</code>. When such an event occurs,
   * <code>setValueAt()</code> displays a dialog explaining the error condition,
   * and does not save the value to the <code>DataSet</code>.</p>
   *
   * @param aValue The value to insert into the <code>DataSet</code>.
   * @param rowIndex The <code>rowIndex</code> to move the current <code>DataSet</code> row to.
   * @param columnIndex The <code>columnIndex</code> to move the current <code>DataSet</code> column to.
  * @see #getValueAt
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (isValidDataSetState()) {
      try {
        // try to validate the value
        if (aValue != null) {
          checkValue(aValue, rowIndex, columnIndex);
        }
        else {
          value.setAssignedNull();
        }
        // Try to navigate to target row.  This action may be denied by vetoable
        // events on the DataSet, in which case we display a dialog indicating so, and discard
        // the pending value to be saved.
        if (rowIndex != dataSet.getRow()) {
          boolean moved = true;
          try {
            moved = dataSet.goToRow(rowIndex);
          }
          catch (Exception e) {
            moved = false;
          }
          finally {
            if (!moved) {
              throw new Exception(java.text.MessageFormat.format(Res._NavPostFailed,     
	                                            new Object[] {Integer.toString(dataSet.getRow())}));
            }
          }

        }
        dataSet.setDisplayVariant(columnMap[columnIndex], value);
      }
      catch (Exception ex) {
        handleException(ex);
      }
    }
    return;
  }


 /**
  * <p>Checks whether the given value satisfies the column formatter for the specified cell (dataset row and column). That is to say, it checks whether the value is valid for the corresponding type, or whether the value can be converted successfully to the data type of the target
column. It also has the side effect of actually saving the value if the value can be converted to the proper
type, or throwing an exception if not. </p>
  *
  * @param aValue The value to check.
  * @param rowIndex The row of the specified cell.
  * @param columnIndex The column of the specified cell.
  * @throws Exception The exception that was thrown.
  * @see #isValidValue
  */
  protected void checkValue(Object aValue, int rowIndex, int columnIndex) throws Exception {
    Column column = dataSet.getColumn(columnMap[columnIndex]);
    if (aValue instanceof String) {
      try {
        int lookupType;
        if ((lookupType = lookupTypes[columnIndex]) == -1) {
          value.setFromString(column.getDataType(), (String) aValue);
        }
        else {
          value.setFromString(lookupType, (String) aValue);
        }
      }
      catch (Exception e) {
        ValidationException.invalidFormat(e, column.getColumnName(), null);
      }
    }
    else if (aValue instanceof Variant) {
      value.setVariant((Variant) aValue);
    }
    else {
      value.setAsObject(aValue, column.getDataType());
    }
  }

  // returns true if the specified value is valid in the dataset at the given row and column

 /**
  * <p> Another entry point for <code>checkValue()</code> to see if a value can be converted to the proper dataset column type. The <code>checkValue()</code> method is called at two different times, once before a user tries to end edit on a cell (the user won't be allowed to leave the cell until the value is valid), and once before a value is actually written into a dataset via <code>DBTableModel</code>. </p>
  *
  * <p> Note that the first case only applies if an edit mask is defined for the column, but the second case happens all the time, including when an application tries to write to a dataset using the <code>DBTableModel</code> directly. </p>
  *
  * @param aValue The value to check.
  * @param rowIndex The row of the specified cell.
  * @param columnIndex The column of the specified cell.
  * @return If <code>true</code>, the value is valid.
  * @see #checkValue
  */
  public boolean isValidValue(Object aValue, int rowIndex, int columnIndex) {
    if (!isValidDataSetState()) {
      return false;
    }
    try {
      checkValue(aValue, rowIndex, columnIndex);
    }
    catch (Exception e) {
      return false;
    }
    return true;
  }

  private void handleException(Exception ex) {
    DBExceptionHandler.getInstance().handleException(dataSet, ex);
  }

  //
  // DataChangeListener interface implemenation
  //
  public void dataChanged(DataChangeEvent e) {
    if (!dataSetEventsEnabled) {
      return;
    }
    if (e.multiRowChange()) {
      // multiple rows added, deleted, or updated.
      // Rebuild entire table since we don't know the precise range
      fireTableDataChanged();
    }
    else {
      int row = e.getRowAffected();
      switch(e.getID()) {
        case DataChangeEvent.ROW_ADDED:
          fireTableRowsInserted(row, row);
          break;
        case DataChangeEvent.ROW_DELETED:
          fireTableRowsDeleted(row, row);
          break;
        case DataChangeEvent.ROW_CHANGED:
          fireTableRowsUpdated(row, getRowCount());
          break;
        case DataChangeEvent.ROW_CHANGE_POSTED:
          // single posted row change could cause row fly-away.  Since we don't know
          // where the row came from, we have no choice but to rebuild the entire range.
        case DataChangeEvent.DATA_CHANGED:
          // should never occur since DATA_CHANGED implies a multi-row change!
        default:
          fireTableDataChanged();
      }
    }
  }

  // DataSet event indicating it's time to post our current value.
  // Not relevant to DBTableModel, because it can never have any unposted field values.
  public void postRow(DataChangeEvent e) throws Exception {
  }

  // AccessListener interface implementation (com.borland.dx.dataset.AccessListener)

  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.CLOSE) {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
      }

      if (event.getReason() == AccessEvent.UNKNOWN) {
        // DataSet was closed and we don't know if it will be opened
        // again soon.  Empty the model.
        fireTableStructureChanged();
        fireTableDataChanged();
      }
      else if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        // The DataSet will be opened again soon, so don't do anything yet
        propertyChange = true;
      }
    }
    else {
      // AccessEvent.OPEN

      dataSetEventsEnabled = true;
      switch (event.getReason()) {
        case AccessEvent.UNSPECIFIED:
          // general table open, rows and structure may have changed, so build the model.
          updateModel();
          fireTableStructureChanged();
          if (!propertyChange) {
            fireTableDataChanged();
          }
          propertyChange = false;
          break;
        case AccessEvent.COLUMN_ADD:
        case AccessEvent.COLUMN_DROP:
        case AccessEvent.COLUMN_CHANGE:
        case AccessEvent.COLUMN_MOVE:
          // general structure change, rebuild the model
//          if (dataSetChanged) {
            updateModel();
            fireTableStructureChanged();
//          }
          break;
        case AccessEvent.DATA_CHANGE:
          dataSetEventsEnabled = true;
          // column was sorted or dataset was emptied, but structure has not changed.
          // reload data rows
          fireTableDataChanged();
          break;
      }
    }
  }

  /**
   * <p>Returns the <code>DataSet Column</code> for the specified <code>columnIndex</code>.</p>
   *
   * @param columnIndex The location of the column in the <code>DataSet</code>.
   * @return The <code>DataSet Column</code> for the specified <code>columnIndex</code>.
   */
  public Column getColumn(int columnIndex) {
    try {
      if (isValidDataSetState()) {
        return dataSet.getColumn(columnMap[columnIndex]);
      }
    }
    catch (DataSetException e) {
      throw new IllegalArgumentException();
    }
    return null;
  }

  /** current DataSet */
  DataSet dataSet;

  /** Single instance used everywhere to pass data to/from the DataSet */
  private Variant value = new Variant();

  /** number of columns in the model */
  private int columnCount;

  /** name of column in the model */
  private String [] captionNames;

  /** class of column in the model */
  private Class [] columnClasses;

  /** lookup column type */
  private int [] lookupTypes;

  /** whether or not a column in the model is editable */
  private boolean [] isEditable;

  /** map from model indexes to DataSet (visible) column ordinals */
  private int [] columnMap;

  /** indicates the reopen was due to a property change */
  private boolean propertyChange;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
