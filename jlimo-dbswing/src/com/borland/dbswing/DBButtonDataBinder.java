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
import java.awt.event.*;
import java.beans.*;
import java.math.*;
import javax.swing.*;
import javax.swing.plaf.*;

import com.borland.dx.dataset.*;
import com.borland.dx.text.*;


/**
 * <p>Maps the
 * state of a button's <code>ButtonModel</code>
 * to a value in a <code>DataSet Column</code>.
 * Properties on <code>DBButtonDataBinder</code>
 * specify values to be set when a
 * <code>ButtonModel</code> is in a selected or
 * unselected state.
 * <code>DBButtonDataBinder</code> is used to
 * make the <code><a href="JdbToggleButton.html">JdbToggleButton</a></code>,
 * <code><a href="JdbRadioButton.html">JdbRadioButton</a></code>, and
 * <code><a href="JdbCheckBox.html">JdbCheckBox</a></code>
 * components data aware.</p>
 *
 * <p>There are two ways to hook up a button to
 * a <code>DBButtonDataBinder</code>. The
 * simpler way is to set the
 * <code>abstractButton</code> property of
 * <code>DBButtonDataBinder</code> to any button
 * that extends the <code>AbstractButton</code>
 * class, such as <code>JButton</code>,
 * <code>JToggleButton</code>,
 * <code>JRadioButton</code>, or
 * <code>JCheckbox</code>. Or you can make any
 * component using a <code>ButtonModel</code>
 * data aware by setting
 * <code>DBButtonDataBinder's</code>
 * <code>buttonModel</code> property to the
 * component's <code>ButtonModel</code>. Note
 * that when using this second approach,
 * however, you are responsible for opening the
 * <code>DataSet</code> before using it.</p>
 *
 * <p>If you set the <code>abstractButton</code>
 * property, <code>DBButtonDataBinder</code>
 * also binds the <code>text</code>,
 * <code>alignment</code>,
 * <code>background</code>,
 * <code>foreground</code>, and
 * <code>font</code> properties from those
 * defined on <code>Column</code>
 * <code>columnName</code>, if one is specified,
 * unless these same properties are already
 * explicitly set on the
 * <code>AbstractButton</code> itself. The
 * <code>text</code> property is considered to
 * be in its default (not explicitly set) state
 * if it is <code>null</code> or an empty string
 * (&quot;&quot;). The button's
 * <code>horizontalAlignment</code> property is
 * considered to be in its default state if its
 * value is <code>SwingConstants.CENTER</code>
 * for a <code>JButton</code> or
 * <code>JToggleButton</code>, or
 * <code>SwingConstants.LEFT</code> for a
 * <code>JRadioButton</code> or
 * <code>JCheckBox</code>. The button's
 * <code>verticalAlignment</code> property is
 * considered to be in its default state if its
 * value is <code>SwingConstants.CENTER</code>.</p>
 *
 *<p>Set the <code>dataSet</code> and
 * <code>columnName</code> properties to
 * indicate the <code>DataSet</code> and
 * <code>Column</code> which data values are
 * read from and written to.</p>
 *
 * <a name="selected_data_value_properties"></a>
 * <h3>selectedDataValue and
 * unselectedDataValue properties</h3>
 *
 * <p>You must specify the values
 * <code>DBButtonDataBinder</code> should save
 * to the <code>DataSet</code> when the button
 * is in its selected and unselected states. Do
 * this by setting
 * <code>DBButtonDataBinder's</code>
 * <code>selectedDataValue</code> and
 * <code>unselectedDataValue</code> properties,
 * respectively; specify the values as
 * <code>String</code>s.
 * <code>DBButtonDataBinder</code> automatically
 * converts these strings to the proper data
 * type for the <code>DataSet</code>
 * <code>Column</code> you specified. To prevent
 * writing a selected or unselected data value
 * to a <code>DataSet</code>, don't set the
 * property value or set it to
 * <code>null</code>. Note that a
 * <code>null</code> value has a different
 * meaning than an empty string (&quot;&quot;),
 * which actually clears whatever value is in
 * the <code>DataSet</code>'s column.</p>
 *
 * <p>For some button types, such as
 * <code>JToggleButton</code>, it makes sense to
 * set a value for both the
 * <code>selectedDataValue</code> and
 * <code>unselectedDataValue</code> properties.
 * Buttons that work in groups, such as
 * <code>JRadioButton</code>, usually would use
 * just a <code>selectedDataValue</code>,
 * however. For example, if several
 * <code>JRadioButtons</code> are attached to
 * the same <code>DataSet</code>
 * <code>Column</code>, you would probably want
 * to set the <code>selectedDataValue</code>
 * only for each <code>JRadioButton</code>
 * because selecting a <code>JRadioButton</code>
 * deselects all others in the same button
 * group. In this case, if you have also set the
 * <code>unselectedDataValue </code>property for
 * each <code>JRadioButton</code>'s
 * <code>DBButtonDataBinder</code>, the value
 * that is finally written to the
 * <code>DataSet</code> is unpredictable; the
 * actual value would depend on the order in
 * which the buttons are selected and
 * unselected.</p>
 *
 * <p>Besides writing a value to a
 * <code>DataSet</code> when a button is
 * selected, <code>DBButtonDataBinder</code>
 * also ensures that the state of the button is
 * consistent with the current value of the
 * <code>DataSet</code> <code>Column</code> to
 * which it is attached. What happens when the
 * value in the <code>DataSet</code> does not
 * match either of the
 * <code>selectedDataValue</code> or
 * <code>unselectedDataValue</code> properties?
 * In this case, <code>DBButtonDataBinder
 * </code>leaves the button, or more accurately,
 * the button's model, in its current state.
 * You can, however, set the
 * <code>unknownDataValueMode</code> property to
 * one of the following values to specify
 * different behavior:</p>
 *
 * <ul>
 * <li>DEFAULT - Leaves the button's model in
 * its unselected state if the current
 * <code>DataSet</code> value does not match
 * either <code>selectedDataValue</code> or
 *<code>unselectedDataValue</code>.<br><br></li>
 * <li>DISABLE_COMPONENT - Disables the button's
 * model state if the current
 * <code>DataSet</code> value does not match
 * either <code>selectedDataValue</code> or
 * <code>unselectedDataValue</code><br><br></li>
 * <li>CLEAR_VALUE - Clears the
 * <code>DataSet</code> value if it doesn't
 * match either <code>selectedDataValue</code>
 * or <code>unselectedDataValue</code>.
 * <br><br></li>
 * </ul>
 * <p><strong>Example:</strong>
 * <pre>
 * JToggleButton jToggleButton = new JToggleButton("Modulation");
 * DBButtonDataBinder dbButtonDataBinder = new DBButtonDataBinder();
 * &nbsp;
 * // attach the button to DBButtonDataBinder
dbButtonDataBinder.setAbstractbutton(JToggleButton);
 * &nbsp;
 * // set the values to be written to DataSet
dbButtonDataBinder.setSelectedDataValue("Frequency");
 *dbButtonDataBinder.setUnselectedDataValue("Amplitude");
 * &nbsp;
 * // set the target DataSet and Column
 * dbButtonDataBinder.setDataSet(dataSet);
 * dbButtonDataBinder.setColumnName("Band");
 * </PRE>
 */



public class DBButtonDataBinder
  implements ActionListener, ItemListener, PropertyChangeListener,
             AccessListener, DataChangeListener, NavigationListener,
             ColumnAware, Designable, DBDataBinder, java.io.Serializable, FocusListener
{

/**
 * <p>Constructs a <code>DBButtonDataBinder</code>. Calls the <code>null</code> constructor of its superclass. </p>
*/
  public DBButtonDataBinder() {
  }


/**
 * <p>Constructs a <code>DBButtonDataBinder</code> that makes the specified button data aware. Calls the <code>null</code>
 * constructor of its superclass. </p>
 *
 * @param button  The button <code>DBButtonDataBinder</code> makes data aware.
 */

  public DBButtonDataBinder(AbstractButton button) {
    setAbstractButton(button);
  }

  //
  // DBButtonDataBinder properties
  //

 /**
  * <p>Sets the button that this <code>DBButtonDataBinder</code> makes data aware.</p>
 *
 * @param button  The button <code>DBButtonDataBinder</code> makes data aware.
 * @see #getAbstractButton
 */

  public void setAbstractButton(AbstractButton button) {
    if (this.button != null && this.button != button) {
      this.button.removePropertyChangeListener(this);
    }
    this.button = button;

    if (button == null) {
      setButtonModel(null);
    }
    else {
      button.addPropertyChangeListener(this);
      setButtonModel(button.getModel());
      if (button instanceof JButton || button instanceof JToggleButton) {
        defaultHorizontalAlignment = SwingConstants.CENTER;
      }
    }
  }

 /**
  * <p>Gets the button that this <code>DBButtonDataBinder</code> makes data aware.</p>
 *
 * @return  The button <code>DBButtonDataBinder</code> makes data aware.
 * @see #setAbstractButton
 */

  public AbstractButton getAbstractButton() {
    return button;
  }

/**
 * <p>Sets the <code>buttonModel</code> of the button <code>DBButtonDataBinder</code> makes data aware. By
 * specifying a <code>buttonModel</code> of a button as the value of the <code>DBButtonDataBinder buttonModel</code>, the
button becomes data aware. You must, however,
 * open the <code>DataSet</code> before using the button component.</p>
 *
 * @param buttonModel The <code>buttonModel</code> of the button <code>DBButtonDataBinder</code> makes data aware.
 * @see #getButtonModel
*/

  public void setButtonModel(ButtonModel buttonModel) {
    if (this.buttonModel != null) {
      this.buttonModel.removeActionListener(this);
      this.buttonModel.removeItemListener(this);
    }

    this.buttonModel = buttonModel;

    if (buttonModel != null) {
      buttonModel.addActionListener(this);
      buttonModel.addItemListener(this);
    }
    bindColumnProperties();
  }

/**
 * <p>Sets the <code>buttonModel</code> of the button <code>DBButtonDataBinder</code> makes data aware. </p>
 *
 * @return The <code>buttonModel</code> of the button <code>DBButtonDataBinder</code> makes data aware.
 * @see #setButtonModel
 */
  public ButtonModel getButtonModel() {
    return buttonModel;
  }

  /**
   * <p>Sets the value written to the <code>DataSet</code> when the
   * button is selected <code>(button.isSelected() == true)</code>.
   * Setting this value to <code>null</code> writes nothing
   * to the <code>DataSet</code>.  Setting this value to an
   * empty string ("") clears the value in the <code>DataSet.</code></p>
   *
   * @param selectedValue The value to be written to the <code>DataSet.</code>
   * @see #getSelectedDataValue
   */
  public void setSelectedDataValue(String selectedValue) {
    this.selectedValue = selectedValue;
  }

  /**
   * <p>Returns the value written to the <code>DataSet</code> when the
   * button is selected <code>(button.isSelected() == true)</code>.</p>
   *
   * @return The value written.
   * @see #setSelectedDataValue
   */
  public String getSelectedDataValue() {
    return selectedValue;
  }

  /**
   * <p>Sets the value to be written to the <code>DataSet</code> when the
   * button is unselected <code>(button.isSelected() == false)</code>.
   * Setting this value to <code>null</code> writes nothing
   * to the <code>DataSet</code>.  Setting this value to an
   * empty string ("") clears the value in the <code>DataSet.</code></p>
   *
   * @param unselectedValue The value to be written to the <code>DataSet</code> when the
   * button is unselected.
   * @see #getUnselectedDataValue
   */
  public void setUnselectedDataValue(String unselectedValue) {
    this.unselectedValue = unselectedValue;
  }

  /**
   * <p>Returns the value written to the <code>DataSet</code> when the
   * button is unselected <code>(button.isSelected() == false)</code>.</p>
   *
   * @return The value written to the <code>DataSet</code> when the
   * button is unselected.
   * @see #setUnselectedDataValue
   */

  public String getUnselectedDataValue() {
    return unselectedValue;
  }

  /**
   * <p>Sets the policy for setting button state when synchronizing a
   * button with its <code>DataSet</code> value when the value doesn't match either
   * of the <code>selectedDataValue</code> or <code>unselectedDataValue</code>
   * property values. Valid values for the <code>mode</code> parameter are DEFAULT, DISABLE_COMPONENT, and CLEAR_VALUE.</p>
   *
   * @param mode One of DEFAULT, DISABLE_COMPONENT, and CLEAR_VALUE.
   * @see #getUnknownDataValueMode
   */
  public void setUnknownDataValueMode(int mode) {
    this.mode = mode;
  }

  /**
   * <p>Returns the policy for setting button state when synchronizing a
   * button with its <code>DataSet</code> value when the value doesn't match either
   * of the <code>selectedDataValue</code> or <code>unselectedDataValue</code>
   * property.</p>
   *
   * @return One of DEFAULT, DISABLE_COMPONENT, and CLEAR_VALUE.
   * @see #setUnknownDataValueMode
   */
  public int getUnknownDataValueMode() {
    return mode;
  }

  //
  // ColumnAware interface implememtation
  //

  /**
   * <p>Sets the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @param dataSet The <code>DataSet</code>.
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
   * <p>Returns the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @return dataSet The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return columnAwareSupport.dataSet;
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    columnAwareSupport.setColumnName(columnName);
    bindColumnProperties();
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @return The column name.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnAwareSupport.columnName;
  }

  //
  // java.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("ancestor")) { 
      bindColumnProperties();
    }
    if (e.getPropertyName().equals(AbstractButton.MODEL_CHANGED_PROPERTY)) {
      setButtonModel((ButtonModel) e.getNewValue());
    }
  }

  //
  // java.awt.event.ActionListener interface implementation
  //

  public void actionPerformed(ActionEvent e) {
    // all buttons should fire an ActionEvent when the button is 'pressed'

    // only set the selected value if we're selected.
    // the unselected value is set on the item state change event.
    columnAwareSupport.lazyOpen();
    if (columnAwareSupport.isValidDataSetState() && columnAwareSupport.getColumn().isEditable()) {
      if (buttonModel.isSelected()) {
        if (selectedValue != null) {
//          Variant value = new Variant();
//          value.setFromString(columnAwareSupport.getColumn().getDataType(), selectedValue);
//          columnAwareSupport.setVariant(value);
          columnAwareSupport.setFromString(selectedValue, button);
        }
        else if (unselectedValue == null) {
          // if both the unselected and selected values are null, then try to set the value
          // appropriately for the bound column, usually expected to be a BOOLEAN type
          Variant value = columnAwareSupport.getVariant();
          switch (columnAwareSupport.getColumn().getDataType()) {
            case Variant.BOOLEAN:    value.setBoolean(true);                   break;
            case Variant.STRING:     value.setString(String.valueOf(true));    break;
            case Variant.BYTE:       value.setByte((byte) 1);                  break;
            case Variant.SHORT:      value.setShort((short) 1);                break;
            case Variant.INT:        value.setInt(1);                          break;
            case Variant.LONG:       value.setLong(1);                         break;
            case Variant.FLOAT:      value.setFloat(1);                        break;
            case Variant.DOUBLE:     value.setDouble(1);                       break;
            case Variant.BIGDECIMAL: value.setBigDecimal(new BigDecimal("1")); break;
            case Variant.TIME:       value.setTime(1);                         break;
            case Variant.DATE:       value.setDate(1);                         break;
            case Variant.TIMESTAMP:  value.setTimestamp(1);                    break;
            default:                 value.setUnassignedNull();
          }
          if (!value.isUnassignedNull()) {
            columnAwareSupport.setVariant(value);
          }
        }
      }
      else {
        if (unselectedValue != null) {
//          Variant value = new Variant();
//          value.setFromString(columnAwareSupport.getColumn().getDataType(), unselectedValue);
//          columnAwareSupport.setVariant(value);
          columnAwareSupport.setFromString(unselectedValue, button);
        }
        else if (selectedValue == null) {
          // if both the unselected and selected values are null, then try to set the value
          // appropriately for the bound column, usually expected to be a BOOLEAN type
          Variant value = columnAwareSupport.getVariant();
          switch (columnAwareSupport.getColumn().getDataType()) {
            case Variant.BOOLEAN:    value.setBoolean(false);                  break;
            case Variant.STRING:     value.setString(String.valueOf(false));   break;
            case Variant.BYTE:       value.setByte((byte) 0);                  break;
            case Variant.SHORT:      value.setShort((short) 0);                break;
            case Variant.INT:        value.setInt(0);                          break;
            case Variant.LONG:       value.setLong(0);                         break;
            case Variant.FLOAT:      value.setFloat(0);                        break;
            case Variant.DOUBLE:     value.setDouble(0);                       break;
            case Variant.BIGDECIMAL: value.setBigDecimal(new BigDecimal("0")); break;
            case Variant.TIME:       value.setTime(0);                         break;
            case Variant.DATE:       value.setDate(0);                         break;
            case Variant.TIMESTAMP:  value.setTimestamp(0);                    break;
            default:                 value.setUnassignedNull();
          }
          if (!value.isUnassignedNull()) {
            columnAwareSupport.setVariant(value);
          }
        }
      }
    }
  }

  //
  // java.awt.event.ItemListener interface implemenation
  //

  public void itemStateChanged(ItemEvent e) {
    if (!ignoreValueChange) {
      columnAwareSupport.lazyOpen();
      // only need to pay attention to DESELECTED event in case we
      // are a JRadioButton in a group that gets automatically deselected.
      if (e.getStateChange() == ItemEvent.DESELECTED) {
        if (columnAwareSupport.isValidDataSetState() && columnAwareSupport.getColumn().isEditable()) {
          if (unselectedValue != null) {
//            Variant value = new Variant();
//            value.setFromString(columnAwareSupport.getColumn().getDataType(), unselectedValue);
//            columnAwareSupport.setVariant(value);
            columnAwareSupport.setFromString(unselectedValue, button);
          }
          else if (selectedValue == null) {
            // if both the unselected and selected values are null, then try to set the value
            // appropriately for the bound column, usually expected to be a BOOLEAN type
            Variant value = columnAwareSupport.getVariant();
            switch (columnAwareSupport.getColumn().getDataType()) {
            case Variant.BOOLEAN:    value.setBoolean(false);                  break;
            case Variant.STRING:     value.setString(String.valueOf(false));   break;
            case Variant.BYTE:       value.setByte((byte) 0);                  break;
            case Variant.SHORT:      value.setShort((short) 0);                break;
            case Variant.INT:        value.setInt(0);                          break;
            case Variant.LONG:       value.setLong(0);                         break;
            case Variant.FLOAT:      value.setFloat(0);                        break;
            case Variant.DOUBLE:     value.setDouble(0);                       break;
            case Variant.BIGDECIMAL: value.setBigDecimal(new BigDecimal("0")); break;
            case Variant.TIME:       value.setTime(0);                         break;
            case Variant.DATE:       value.setDate(0);                         break;
            case Variant.TIMESTAMP:  value.setTimestamp(0);                    break;
            default:                 value.setUnassignedNull();
            }
            if (!value.isUnassignedNull()) {
              columnAwareSupport.setVariant(value);
            }
          }
        }
      }
    }
  }

  // synchronizes the state of the button model with the current
  // DataSet value

/**
 * Updates button's state to current value.
 */

  protected void updateButtonState() {

    ignoreValueChange = true;
    if (columnAwareSupport.isValidDataSetState()) {
      Variant columnValue = columnAwareSupport.getVariant();
      if (selectedValue == null && unselectedValue == null) {
        buttonModel.setSelected(columnValue.getAsBoolean());
      }
      else {
        if (compareVariant == null || compareVariant.getType() != columnAwareSupport.getColumn().getDataType()) {
          compareVariant = new Variant();
        }
        if (selectedValue != null) {
          try {
            columnAwareSupport.getColumn().getFormatter().parse(selectedValue, compareVariant);
//            compareVariant.setFromString(columnAwareSupport.getColumn().getDataType(), selectedValue);
            if (columnValue.equals(compareVariant)) {
              if (mode == DISABLE_COMPONENT && !buttonModel.isEnabled()) {
                buttonModel.setEnabled(true);
              }
              buttonModel.setSelected(true);
              return;
            }
          }
          catch (InvalidFormatException e) {
            DBExceptionHandler.handleException(columnAwareSupport.dataSet, button, e);
          }
        }
        if (unselectedValue != null) {
          try {
            columnAwareSupport.getColumn().getFormatter().parse(unselectedValue, compareVariant);
//            compareVariant.setFromString(columnAwareSupport.getColumn().getDataType(), unselectedValue);
            if (columnValue.equals(compareVariant)) {
              if (mode == DISABLE_COMPONENT && !buttonModel.isEnabled()) {
                buttonModel.setEnabled(true);
              }
              buttonModel.setSelected(false);
              return;
            }
          }
          catch (InvalidFormatException e) {
            DBExceptionHandler.handleException(columnAwareSupport.dataSet, button, e);
          }
        }
        // default mode, set the button to its unselected state
        buttonModel.setSelected(false);
        // current column value does not match either the selected or unselected data value
        if (mode == DISABLE_COMPONENT) {
          // only disable the component if it has an assigned value
          if (columnValue.isUnassignedNull()) {
            buttonModel.setEnabled(true);
          }
          else {
            buttonModel.setEnabled(false);
          }
        }
        else if (mode == CLEAR_VALUE && !columnAwareSupport.isNull()) {
          columnAwareSupport.resetValue();
        }
      }
    }
    else {
      if (buttonModel != null) {
        buttonModel.setEnabled(true);
        buttonModel.setSelected(false);
      }
    }
    ignoreValueChange = false;
  }

  //
  // com.borland.dx.dataset.NavigationListener interface implementation
  //

  /**
   * <p>This is an event to notify listeners that the current row has changed, that a user has moved from one row to another. </p>
   * @param event  The event that called the listener, indicating in this case that the current row has been changed.
  */
  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // navigation means we've moved on to a different row, and we need
    // to update the button state to the current value.

    updateButtonState();
  }

  //
  // com.borland.dx.dataset.DataChangeListener interface implementation
  //

 /**
  * <p>An event to warn listeners that an arbitrary data change occurred to one or more rows of data. </p>
  *
  * @param event  An object telling what type of change was made, and to which row.
  */
  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // depending upon the event, we might have to update the current
    // button state.
    int affectedRow = event.getRowAffected();
    boolean affectedOurRow = (affectedRow == columnAwareSupport.dataSet.getRow()) ||
      affectedRow == -1;
    if (affectedOurRow) {
      updateButtonState();
    }
  }

 /**
  * <p>An event to warn listeners that a row's data has changed and must be posted. </p>
  *
  * @param event An object telling what type of change was made, and to which row.
  * @throws Exception The exception that was thrown.
  */
  public void postRow(DataChangeEvent event) throws Exception {
    // usually needs to get the pending value from the control and set it,
    // but since buttons don't have a pending state, this isn't necessary
  }

  //
  // Code to bind visual Column properties to button
  //

  // returns whether or not the text should be overriden by the column's caption.
  // true if and only if text is null or the empty string ("").
  private boolean useCaptionFromColumn() {
    return (button.getText() == null) || (button.getText().length() == 0);
  }

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds text, alignment, font, foreground, and background properties from column
  // if not explicitly set on button
  private void bindColumnProperties() {
    if (oldButton != null) {
      oldButton.removeFocusListener(this);
      oldButton = null;
    }

    if (button != null && button.isDisplayable()) {
      // ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
      // again on the OPEN access event
      rebindColumnProperties = false;
      // will resync our internal state if the dataSet or column has changed
      columnAwareSupport.lazyOpen();

      updateButtonState();

      if (columnAwareSupport.isValidDataSetState()) {

        button.addFocusListener(this);
        oldButton = button;

        Column column = columnAwareSupport.getColumn();

        if (useCaptionFromColumn()) {
          button.setText(column.getCaption());
        }
        if (button.getHorizontalAlignment() == defaultHorizontalAlignment) {
          button.setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(column.getAlignment(), true));
        }
        if (button.getVerticalAlignment() == SwingConstants.CENTER) {
          button.setVerticalAlignment(DBUtilities.convertJBCLToSwingAlignment(column.getAlignment(), false));
        }
        if (isDefaultProperty(button.getBackground())) {
          if (column.getBackground() != null) {
            button.setBackground(column.getBackground());
          }
        }
        if (isDefaultProperty(button.getForeground())) {
          if (column.getForeground() != null) {
            button.setForeground(column.getForeground());
          }
        }
        if (isDefaultProperty(button.getFont())) {
          if (column.getFont() != null) {
            button.setFont(column.getFont());
          }
        }

        if (buttonModel.isEnabled() && !column.isEditable()) {
          buttonModel.setEnabled(false);
        }
      }
    }
  }

  //
  // com.borland.dx.dataset.AccessListener interface implementation
  //

 /**
  * <p>Provides information regarding how a <code>DataSet</code> has been changed. </p>
  *
  * @param event The type of event that changed a data set: opened, closed, or restructured. See      <code>com.borland.dx.dataset.AccessEvent</code> for more information.
  */

  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.CLOSE) {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }
      else {
        if (mode == DISABLE_COMPONENT) {
          buttonModel.setEnabled(false);
        }
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

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(button, columnAwareSupport.dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  /** AbstractButton to be bound to DataSet */
  private AbstractButton button;

  private AbstractButton oldButton;

  /** ButtonModel to monitor for state changes */
  private ButtonModel buttonModel;

  /** data value for 'selected' state */
  private String selectedValue;

  /** data value for 'unselected' state */
  private String unselectedValue;

  /** current unknown data value mode */
  private int mode = DEFAULT;

  /** support for ColumnAware implementation */
  private DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

  /** flag indicating we should ignore ItemStateChange because we caused it */
  private boolean ignoreValueChange;

  /** button's default horizontal alignment */
  private int defaultHorizontalAlignment = SwingConstants.LEFT;

  /** variant used to compare selected and unselected values against column value */
  private Variant compareVariant;

  private boolean rebindColumnProperties;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
