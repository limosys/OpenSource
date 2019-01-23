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

import com.borland.dx.dataset.*;
import com.borland.dx.text.*;

/**
 * <p>Provides a basic implementation of the
 * <code>ColumnAware</code> interface, with support for deferred (lazy) opening
 * of the <code>DataSet</code> until requested by calling <code>lazyOpen()</code>.</p>
 *
 * <p>Data-aware components implementing the <code>ColumnAware</code> interface can
 * delegate their <code>setDataSet()</code> and <code>setColumnName()</code> methods to
 * <code>DBColumnAwareSupport</code>.  Such components should invoke the <code>lazyOpen()</code>
 * method just before any attempt to read data from or write data to the
 * <code>DataSet.</code>  Also, if the component needs to implement the <code>AccessListener</code>
 * or <code>DataChangeListener</code> interface, it should not register as a listener
 * directly, but rather pass a reference to itself to the <code>DBColumnAwareSupport's</code> constructor.  This ensures that the <code>DataSet</code>
 * is properly initialized before the component needs to access it.</p>
 *
 * @see DBListModel
 * @see DBListDataBinder
 * @see DBButtonDataBinder
 * @see DBLabelDataBinder
 * @see DBSliderDataBinder
 * @see DBTreeDataBinder
 * @see DBTreeNavBinder
 */
public class DBColumnAwareSupport
  implements AccessListener, DataChangeListener, ColumnAware, java.io.Serializable
{
  /**
   * <p>Initializes <code>DBColumnAwareSupport</code>.  The single parameter should be the
   * instance of the object delegating <code>ColumnAware</code> support to
   * <code>DBColumnAwareSupport</code>.  If the object needs to implement the
   * <code>AccessListener</code> and/or <code>DataChangeListener</code> interfaces, it should not
   * register itself as a listener on its <code>DataSet</code> directly.  It will
   * be called automatically by <code>DBColumnAwareSupport</code> after the <code>DataSet</code>
   * has been properly initialized.  Note that <code>columnAwareObject</code> can
   * be null for objects which don't implement either interface.</p>
   *
   * @param columnAwareObject The instance of the object delegating <code>ColumnAware</code> support to
   * <code>DBColumnAwareSupport</code>.
   */
  public DBColumnAwareSupport(ColumnAware columnAwareObject) {
    if (columnAwareObject instanceof AccessListener) {
      accessListener = (AccessListener) columnAwareObject;
    }

    if (columnAwareObject instanceof DataChangeListener) {
      dataChangeListener = (DataChangeListener) columnAwareObject;
    }
  }

  /**
   * <p>Sets the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @param dataSet The <code>DataSet.</code>
   * @see #getDataSet
   * @see #setColumnName
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

      // instructs lazyOpen() that it must open the new dataSet
      dataSetChanged = true;

      // if the dataSet has been opened already, then we need to
      // immediately update state when this property changes
      if (liveProperties) {
        lazyOpen();
      }
    }
  }


  /**
   * <p>Returns the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @return dataSet The <code>DataSet.</code>
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @param columnName The column name.
   * @see #getColumnName
   */
  public void setColumnName(String columnName) {
    if (this.columnName == null || !this.columnName.equals(columnName)) {
      this.columnName = columnName;

      // instructs lazyOpen() that it must update columnOrdinal
      columnChanged = true;

      // if the dataSet has been opened already, then we need to
      // immediately update state when this property changes
      if (liveProperties) {
        lazyOpen();
      }
    }
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @return The column name.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnName;
  }

  // Used to only allow the DataSet to be opened after both
  // the DataSet and columnName are set.  Sets liveProperties
  // to true on the first valid open attempt.

 /**
  * Allows the <code>DataSet</code> to be opened only after both the <code>dataSet</code> and <code>columnName</code> values are set. It sets to <code>true</code> on the first valid open attempt.
  */
  protected void lazyOpen() {

    // if we've ever tried opening the dataSet, then
    // properties should be put into their immediate response
    // state
    liveProperties = true;
    if (dataSetChanged || columnChanged) {

      if (dataSetChanged) {

        if (dataSet != null) {
          try {
            if (!dataSet.isOpen()) {
              dataSet.open();
            }
          }
          catch (DataSetException e) {
            columnOrdinal = -1;
            // setting dataSetChanged false will prevent subsequent open
            // attempts which will also fail until the invalid column or
            // dataSet problem is fixed.
            dataSetChanged = false;
            DBExceptionHandler.handleException(dataSet, e);
            return;
          }
        }

      }

      // if the dataSet or columnName changed , our columnOrdinal could change.
      if (dataSetChanged || columnChanged) {
        columnOrdinal = -1;
        if (dataSet != null && columnName != null) {
          try {
            Column column;
            if ((column = dataSet.hasColumn(columnName)) != null) {
              columnOrdinal = dataSet.getColumn(columnName).getOrdinal();
            }
          }
          catch (DataSetException e) {
            // setting columnChanged false will prevent subsequent open
            // attempts which will also fail until the invalid column or
            // dataSet problem is fixed.
            columnChanged = false;
            DBExceptionHandler.handleException(dataSet, e);
            return;
          }
        }
        columnChanged = false;
      }
      dataSetChanged = false;
    }
  }

 /**
  * <p>Returns <code>true</code> if the <code>DataSet</code> is not <code>null,</code> if the <code>DataSet</code> has been opened and the <code>columnOrdinal</code> value is not -1. In other words, if a column has been specified on an open <code>DataSet</code> containing data, <code>valueDataSetState</code> is <code>true</code>.</p>
  *
  * @return <code>True</code> if a column has been specified on an open <code>DataSet</code> containing data.
  */
  public boolean isValidDataSetState() {
    return (dataSet != null && dataSet.isOpen() && columnOrdinal != -1);
  }

  //
  // Some convenience methods
  //

/**
 * <p>Returns the column value as a <code>Variant</code>.</p>
 *
 * @return The column value as a <code>Variant.</code>
 * @see #setVariant
 */
  public Variant getVariant() {
    lazyOpen();
    Variant v = new Variant();
    try {
      dataSet.getDisplayVariant(columnOrdinal, dataSet.getRow(), v);
      return v;
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
    v.setAssignedNull();
    return v;
  }

/**
 * <p>Sets the column value as a <code>Variant</code>.</p>
 *
 * @param value The value of the column to set as a <code>Variant.</code>
 * @see #getVariant
 */
  public void setVariant(Variant value) {
    lazyOpen();
    try {
      dataSet.setDisplayVariant(columnOrdinal, value);
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
  }

/**
 * <p>Sets the column value in the <code>DataSet</code> using the value of the <code>object</code> parameter.</p>
 *
 * @param object The value to set the column value to.
 */
  public void setObject(Object object) {
    lazyOpen();
    try {
      value.setAsObject(object, getColumn().getDataType());
      dataSet.setDisplayVariant(columnOrdinal, value);
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
  }


/**
 * <p>Sets the column value with the specified string by calling the <code>setFromString(java.lang.String,
java.awt.Component)</code> method. </p>
 *
 * <p>Returns <code>True</code> if setting the value was successful. The <code>string</code> parameter contains the text string to which the column value is set.  If the column value is not set successfully, the method returns <code>false</code>.</p>
 *
 * @param string The text string to which the column value is set.
 * @return <code>True</code> if the setting the value was successful, <code>false</code> if it was not.
 */
  public boolean setFromString(String string) {
    return setFromString(string, null);
  }



 /**
  * <p>Sets the column value with the specified string and returns <code>true</code> if the column is successfully set. If the column value is not set successfully, the method returns <code>false</code>. The <code>string</code>  parameter contains the text string to which the column value is set.</p>
  *
  * <p>The <code>focusedComponent</code> is the component that has the current focus. </p>
  *
 * @param string The text string to which the column value is set.
  * @param focusedComponent  The component that has the current focus. </p>
  * @return <code>True</code> if the setting the value was successful, <code>false</code> if it was not.
  */
  public boolean setFromString(String string, Component focusedComponent) {
    lazyOpen();
    try {
      getColumn().getFormatter().parse(string, value);
      dataSet.setDisplayVariant(columnOrdinal, value);
    }
    catch (Exception e) {
      try {
        ValidationException.invalidFormat(e, null, null);
      } catch (Exception ex) {
        DBExceptionHandler.handleException(dataSet, focusedComponent, ex);
        if (focusedComponent != null) {
          focusedComponent.requestFocus();
        }
      }
      return false;
    }
    return true;
  }

 /**
  * <p>Sets the column value with the specified string. The <code>string</code> parameter contains the text string to which the column value is set. The <code>focusedComponent</code> is the component that has the current focus. </p>
 *
 * @param string The text string to which the column value is set.
 * @param focusedComponent  The component that has the current focus. </p>
 * @param columnAwareSupport  The <code>columnAwareSupport</code> object. </p>
 * @throws Exception The exception that was thrown.
 */
  public void setFromString2(String string, Component focusedComponent, DBColumnAwareSupport columnAwareSupport) throws Exception {
    lazyOpen();
    try {
      /*
      String formattedValue = null;
     if (column != null && column.getEditMask() == null &&
         column.getFormatter().getFormatObj() instanceof java.text.DateFormat) {
        oldPattern = column.getFormatter().getPattern();
        int yearMaskIndex = -1;
        if ((yearMaskIndex = DBUtilities.yearMaskPos(oldPattern)) != -1) {
          int yearMaskCount = 1;
          int patternLength = oldPattern.length();
          while ((yearMaskIndex + yearMaskCount) < patternLength && oldPattern.charAt(yearMaskIndex + yearMaskCount) == 'y') {
            yearMaskCount++;
          }
          if (yearMaskCount < 4) {
            widePattern = oldPattern.substring(0, yearMaskIndex) + "yyyy" +     
              ((yearMaskIndex + yearMaskCount < patternLength) ? oldPattern.substring(yearMaskIndex + yearMaskCount) : ""); 
          }
        }
        if (widePattern != null) {
          column.getFormatter().setPattern(widePattern);
        }
      }
      */
      getColumn().getFormatter().parse(string, value);
      dataSet.setDisplayVariant(columnOrdinal, value);
    }
    catch (Exception e) {
      if (focusedComponent != null) {
        focusedComponent.requestFocus();
      }
      ValidationException.invalidFormat(e, null, null);
    }
  }

 /**
  * <p>Returns the ordinal of the column in the <code>DataSet</code>. By default, this value is -1, meaning no column is indicated.</p>
  *
  * @return The ordinal value of the column.
  */

  public int getColumnOrdinal() {
    return columnOrdinal;
  }

/**
 * <p>Returns the column.</p>
 *
 * @return The column.
 */
  public Column getColumn() {
    lazyOpen();
    Column column = null;
    try {
      if (columnOrdinal == -1) {
        return null;
      }
      else {
        column = dataSet.getColumn(columnOrdinal);
      }
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
    return column;
  }
  /*
  // sets as AssignedNull
  public void emptyValue() {
    lazyOpen();
    try {
      dataSet.setAssignedNull(columnName);
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
  }
  */
  // sets as UnassignedNull
 /**
  * Resets the value of the clumn to its previous value.
   */
  public void resetValue() {
    lazyOpen();
    try {
      dataSet.setUnassignedNull(columnName);
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
  }

/**
 * <p>Returns <code>true</code> if the column value is <code>null</code>.</p>
 *
 * @return <code>True</code> if column value is <code>null,</code> <code>false</code> if it is not.
 */
  public boolean isNull() {
    lazyOpen();
    try {
      return dataSet.isNull(columnName);
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
    return true;
  }

 /**
  * <p>Returns the value of the column as a formatted string.</p>
  *
  * @return The formatted string.
  */
  public String getFormattedString() {
    lazyOpen();
    Variant data = getVariant();
    String text = data.toString();
    Column column;
    if ((column = getColumn()) != null) {
      ItemFormatter formatter = column.getFormatter();
      if (formatter != null) {
        try {
          text = formatter.format(data);
        }
        catch (InvalidFormatException e) {
          DBExceptionHandler.handleException(e);
        }
      }
    }
    return text;
  }

  //
  // AccessListener interface implementation
  //

 /**
  * <p>Provides information regarding how a <code>DataSet</code> has been changed. </p>
  *
  * @param event The type of event that changed a data set: opened, closed, or restructured. See      <code>com.borland.dx.dataset.AccessEvent</code> for more information.
  */

  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
      dataSetChanged = true;
      columnChanged = true;
      if (liveProperties) {
        lazyOpen();
      }
    }
    if (accessListener != null) {
      accessListener.accessChange(event);
    }
  }

  //
  // DataChangeListener interface implementation
  //

 /**
  * <p>An event to warn listeners that an arbitrary data change occurred to one or more rows of data. </p>
  *
  * @param event  An object telling what type of change was made, and to which row.
  */
  public void dataChanged(DataChangeEvent event) {
    if (dataChangeListener != null) {
      dataChangeListener.dataChanged(event);
    }
  }

  // handled by data-aware components, which should post or cancel pending edits

 /**
  * <p>An event to warn listeners that a row's data has changed and must be posted. </p>
  *
  * @param event An object telling what type of change was made, and to which row.
  * @throws Exception The exception that was thrown.
  */
  public void postRow(DataChangeEvent event) throws Exception {
    dataChangeListener.postRow(event);
  }

  /** DataChangeListener to register on DataSet */
  private DataChangeListener dataChangeListener;

  /** AccessListener to register on DataSet */
  private AccessListener accessListener;

  /** dataSet to/from which value should be written/read */
  DataSet dataSet;

  /** whether or not only the dataSet has changed */
  boolean dataSetChanged = false;

  /** whether or not only the columnName has changed */
  boolean columnChanged = false;

  /** columnName to/from which value should be written/read */
  String columnName;

  /** ordinal of Column columnName of DataSet dataSet */
  int columnOrdinal = -1;

  /** value returned from DataSet */
  private Variant value = new Variant();

  /** whether or not changes to properties should take effect immediately */
  boolean liveProperties = false;
}
