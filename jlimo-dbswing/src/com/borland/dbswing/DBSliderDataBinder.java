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

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.UIResource;

import com.borland.dx.dataset.*;

/**
 *
 * <p>Maps the state of a <code>JSlider's BoundedRangeModel</code> to a value in a <code>DataSet</code> numeric type <code>Column</code>. <code>JdbSlider</code> automatically uses a <code>DBSliderDataBinder</code>, and therefore it becomes a data-aware component. </p>
 *
 * <p>There are two ways to hook up a <code>JSlider</code> to a <code>DBSliderDataBinder</code>. The easier way is to set the <code>jSlider</code> property to a <code>JSlider</code> or a subclass of <code>JSlider</code>. The second way is to make any component using a <code>BoundedRangeModel</code> data-aware by setting <code>DBSliderDataBinder's boundedRangeModel</code> property to the component's model. Note that when you use this approach, you are responsible for opening the <code>DataSet</code> before using it.  </p>
 *
 * <p>You must also set the <code>DBSliderDataBinder's dataSet</code> and <code>columnName</code> properties to specify the <code>DataSet</code> and <code>Column</code> from which the slider value (its current position) is read and to which the slider value is written.  </p>
 *
 * <p>If you set the <code>jSlider</code> property, <code>DBSliderDataBinder</code> binds the <code>background, foreground</code>, and <code>font</code> properties from those defined on <code>Column columnName</code>, if one exists. This does not happen if these properties are set explicitly on <code>DBSliderDataBinder</code> itself.  </p>
 *
 * <p>Besides writing a value to a <code>DataSet</code> when a slider is adjusted, <code>DBSliderDataBinder</code> also ensures that the position of the slider is consistent with the current value of the <code>DataSet Column</code> to which it is attached. If the value in the <code>DataSet</code> is outside the range of <code>DBSliderDataBinder's</code> bounds,
<code>DBSliderDataBinder</code> moves the slider to whichever value is closest to the <code>DataSet's</code> value. You can set the <code>unknownDataValueMode</code> property, however, to one of the following values to specify different behavior:  </p>
 *
 * <ul>
 * <li>DEFAULT - Moves the slider to the position closest to the <code>DataSet's</code> actual value if the value is
outside the valid range of the slider's model.</li>
 *
 * <li>DISABLE_COMPONENT - Disables the slider if the current <code>DataSet</code> value is outside the valid      range of the slider's model.</li>
 *
 * <li>CLEAR_VALUE - Clears the <code>DataSet</code> value if it doesn't fall within the valid range of the slider's      model. </li>
 * </ul>
 * <p>Example: </p>
 *
 * <pre>
 * JSlider jSlider = new JSlider();
 * DBSliderDataBinder dBSliderDataBinder = new DBSliderDataBinder();
 *
 * // attach the slider to DBSliderDataBinder
 * dBSliderDataBinder.setJSlider(jSlider);
 *
 * // set the target DataSet and Column
 * dBSliderDataBinder.setDataSet(dataSet);
 * dBSliderDataBinder.setColumnName("Quantity")
 * </pre>
 * @see JdbSlider
 */
public class DBSliderDataBinder
  implements ChangeListener, PropertyChangeListener, DBDataBinder,
             AccessListener, DataChangeListener, NavigationListener,
             ColumnAware, Designable, java.io.Serializable, FocusListener
{


  /**
   * <p>Creates a <code>DBSliderDataBinder</code>. Calls the <code>null</code> constructor of its superclass. </p>
   */
  public DBSliderDataBinder() {
  }

  /**
   * <p>Creates a <code>DBSliderDataBinder</code> and specifies the <code>JSlider</code> it makes data aware. Calls the <code>null</code> constructor of its superclass. </p>
   *
   * @param slider The <code>JSlider</code> to which <code>DBSliderDataBinder</code> binds to make it data-aware.
  */

  public DBSliderDataBinder(JSlider slider) {
    setJSlider(slider);
  }

  //
  // DBSliderDataBinder properties
  //


 /**
  * <p>Sets the slider component to which <code>DBSliderDataBinder</code> binds to make it data-aware. </p>
  *
  * @param slider The <code>JSlider</code> that <code>DBSliderDataBinder</code> makes data-aware.
  * @see #getJSlider
*/
  public void setJSlider(JSlider slider) {
    if (this.slider != null && this.slider != slider) {
      this.slider.removePropertyChangeListener(this);
    }
    this.slider = slider;

    if (slider == null) {
      setBoundedRangeModel(null);
    }
    else {
      slider.addPropertyChangeListener(this);
      setBoundedRangeModel(slider.getModel());
    }
  }

 /**
  * <p>Returns the slider component to which <code>DBSliderDataBinder</code> binds to make it data-aware. </p>
  *
  * @return The <code>JSlider</code> that <code>DBSliderDataBinder</code> makes data-aware.
  * @see #setJSlider
*/
  public JSlider getJSlider() {
    return slider;
  }

 /**
  * <p>Sets the model of the component that <code>DBSliderDataBinder</code> makes data-aware. When
you set this property, you are responsible for opening the DataSet before using it. Specifying the model
binds the DBSliderDataBinder to the component the model supplies data values to. </p>
  *
  * @param boundedRangeModel The model of the component that <code>DBSliderDataBinder</code> makes data-aware.
  * @see #getBoundedRangeModel
  */
  public void setBoundedRangeModel(BoundedRangeModel boundedRangeModel) {
    if (this.boundedRangeModel != null) {
      this.boundedRangeModel.removeChangeListener(this);
    }

    this.boundedRangeModel = boundedRangeModel;

    if (boundedRangeModel != null) {
      boundedRangeModel.addChangeListener(this);
    }
  }

 /**
  * <p>Returns the model of the component that <code>DBSliderDataBinder</code> makes data-aware.</p>
  *
  * @return The model of the component that <code>DBSliderDataBinder</code> makes data-aware.
  * @see #setBoundedRangeModel
  */
  public BoundedRangeModel getBoundedRangeModel() {
    return boundedRangeModel;
  }

  /**
   * Sets the policy for setting slider position when synchronizing a
   * slider with a <code>DataSet</code> value outside its bounded model's range.
   *
   * @param mode The possible values are:
   *
   * <ul>
   * <li>DEFAULT - If the DataSet value is less than the slider's minimum value, the slider is set to the     minimum value; if the value is greater than the slider's maximum value, the slider is set to the maximum value. </li>
   * <li>DISABLE_COMPONENT - The component is disabled. </li>
   * <li>CLEAR_VALUE - The value in the DataSet is cleared. </li>
</ul>
   *
   * @see #getUnknownDataValueMode
   */
  public void setUnknownDataValueMode(int mode) {
    this.mode = mode;
  }



 /**
   * Returns the policy for setting slider position when synchronizing a
   * slider with a <code>DataSet</code> value outside its bounded model's range.
   *
   * @return One of:
   *
   * <ul>
   * <li>DEFAULT - If the DataSet value is less than the slider's minimum value, the slider is set to the     minimum value; if the value is greater than the slider's maximum value, the slider is set to the maximum value. </li>
   * <li>DISABLE_COMPONENT - The component is disabled. </li>
   * <li>CLEAR_VALUE - The value in the DataSet is cleared. </li>
</ul>
   *
   * @see #setUnknownDataValueMode
   */
  public int getUnknownDataValueMode() {
    return mode;
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

  //
  // java.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("ancestor")) {  
      bindColumnProperties();
    }
    if (e.getPropertyName().equals("model")) {   
      setBoundedRangeModel((BoundedRangeModel) e.getNewValue());
    }
  }

  //
  // javax.swing.event.ChangeListener interface implemenation
  //

  public void stateChanged(ChangeEvent e) {
    if (!ignoreValueChange && !boundedRangeModel.getValueIsAdjusting()) {
//      columnAwareSupport.lazyOpen();
      if (columnAwareSupport.isValidDataSetState()) {
        columnAwareSupport.setFromString(boundedRangeModel.getValue() + "", slider);  
      }
    }
  }

  // synchronizes the state of the slider model with the current
  // DataSet value

  private void updateSliderState() {

    ignoreValueChange = true;
    if (columnAwareSupport.isValidDataSetState()) {
      int value = columnAwareSupport.getVariant().getAsInt();
      if (value >= boundedRangeModel.getMinimum() &&
          value <= boundedRangeModel.getMaximum()) {
        boundedRangeModel.setValue(value);
      }
      else {
        if (mode == DISABLE_COMPONENT && slider != null && !columnAwareSupport.getVariant().isUnassignedNull()) {
          slider.setEnabled(false);
        }
        else if (mode == CLEAR_VALUE && !columnAwareSupport.isNull()) {
          columnAwareSupport.resetValue();
        }
        else {
          // default, set the slider to the closest value
          boundedRangeModel.setValue(value);
        }
      }
    }
    else {
      if (boundedRangeModel != null) {
        boundedRangeModel.setValue(boundedRangeModel.getValue());
      }
    }
    ignoreValueChange = false;
  }

  //
  // com.borland.dx.dataset.NavigationListener interface implementation
  //

  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // navigation means we've moved on to a different row, and we need
    // to update the slider state to the current value.
    updateSliderState();
  }

  //
  // com.borland.dx.dataset.DataChangeListener interface implementation
  //

  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // depending upon the event, we might have to update the current
    // slider state.
    int affectedRow = event.getRowAffected();
    boolean affectedOurRow = (affectedRow == columnAwareSupport.dataSet.getRow()) ||
      affectedRow == -1;
    if (affectedOurRow) {
      updateSliderState();
    }
  }

  public void postRow(DataChangeEvent event) throws Exception {
    // usually needs to get the pending value from the control and set it,
    // but since sliders don't have a pending state, this isn't necessary
  }

  //
  // Code to bind visual Column properties to slider
  //

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds text, alignment, font, foreground, and background properties from column
  // if not explicitly set on slider
  private void bindColumnProperties() {
    if (oldSlider != null) {
      oldSlider.removeFocusListener(this);
      oldSlider = null;
    }

    if (slider != null && slider.isDisplayable()) {
      // ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
      // again on the OPEN access event
      rebindColumnProperties = false;
      // will resync our internal state if the dataSet or column has changed
      columnAwareSupport.lazyOpen();

      updateSliderState();

      if (columnAwareSupport.isValidDataSetState()) {

        slider.addFocusListener(this);
        oldSlider = slider;

        // if we have a valid column, merge its properties
        Column column = columnAwareSupport.getColumn();
        if (isDefaultProperty(slider.getBackground())) {
          if (column.getBackground() != null) {
            slider.setBackground(column.getBackground());
          }
        }
        if (isDefaultProperty(slider.getForeground())) {
          if (column.getForeground() != null) {
            slider.setForeground(column.getForeground());
          }
        }
        if (isDefaultProperty(slider.getFont())) {
          if (column.getFont() != null) {
            slider.setFont(column.getFont());
          }
        }

          // replace the slider's min value from Column if it's the default value
        if (slider.getMinimum() == 0 && column.getMinValue() != null && column.getMinValue().getType() == Variant.INT) {
          slider.setMinimum(column.getMinValue().getInt());
        }

        // replace the slider's max value from Column if it's the default value
        if (slider.getMaximum() == 0 && column.getMaxValue() != null && column.getMaxValue().getType() == Variant.INT) {
          slider.setMaximum(column.getMaxValue().getInt());
        }

        if (slider.isEnabled() && !column.isEditable()) {
          slider.setEnabled(false);
        }

      }
    }
  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(slider, columnAwareSupport.dataSet);
  }
  public void focusLost(FocusEvent e) {
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
        if (mode == DISABLE_COMPONENT) {
          slider.setEnabled(false);
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

  /** AbstractSlider to be bound to DataSet */
  private JSlider slider;

  private JSlider oldSlider;

  /** BoundedRangeModel to monitor for state changes */
  private BoundedRangeModel boundedRangeModel;

  /** current unknown data value mode */
  private int mode = DEFAULT;

  /** support for ColumnAware implementation */
  DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

  /** flag indicating we should ignore ItemStateChange because we caused it */
  private boolean ignoreValueChange;

  private boolean rebindColumnProperties;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
