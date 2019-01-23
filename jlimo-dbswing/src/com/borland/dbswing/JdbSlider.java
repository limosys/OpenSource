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
 * <p>A data-aware subclass of
<code>javax.swing.JSlider</code>.
 * It adds the following properties:</p>
 *
 * <ul>
 * <li><code>dataSet</code> - The <code>DataSet</code> to which the slider value is assigned.</li>
 * <li><code>columnName</code> - The numeric <code>Column</code> of the specified <code>DataSet</code> to which the value is assigned.</li>
 * <li><code>unknownDataValueMode</code> - The policy for synchronizing slider state when an out-of-range <code>DataSet</code> data value is encountered.</li>
 * </ul>
 *
 * <p><code>JdbSlider</code> sets its <code>background</code>,
 * <code>foreground</code>, and <code>font</code>
 * properties from those defined on <code>Column</code>
 * <code>columnName</code>, if one is defined, unless these
 * properties are explicitly set on the
 * <code>JdbSlider</code> itself.</p>
 *
 * <p><code>JdbSlider</code> can be bound to a column with
 * an integral value only (such as int, short, and so on).
 * In some cases it may be feasible to coerce non-integral
 * data from a server table to an integer type, but be sure
 * this doesn't create problems when you save changes back
 * to the server.</p>
 *
 * <p><code>JdbSlider</code> cannot display a
 * <code>null</code> column value differently than a
 * minimum value. You should consider forcing every row in
 * the <code>DataSet</code> to have a value in the slider's
 * <code>Column</code> by setting the <code>Column</code>'s
 * <code>default</code> property. Don't try to use the
 * slider's initial value for this; the slider is set to
 * this value only once, not for every new, empty row.</p>
 *
 * @see DBSliderDataBinder
 */
public class JdbSlider extends JSlider
  implements DBDataBinder, ColumnAware, java.io.Serializable
{

 /**
  * <p>Creates a <code>JdbSlider</code> by calling the
  *  constructor of <code>this</code> class which takes
  * four <code>int</code> values as parameters. Passes
  * default values of HORIZONTAL, 0, 100, and 50 to the
  * other constructor.</p>
  */
  public JdbSlider() {
    this(HORIZONTAL, 0, 100, 50);
  }

 /**
  * <p>Creates a <code>JdbSlider</code> and specifies
  * whether the slider is oriented horizontally or
  * vertically. This constructor calls the constructor of
  * <code>this</code> class which takes four
  * <code>int</code> values as parameters.  It passes that
  * constructor the specified <code>orientation</code>,
  * along with default values of 0, 100, and 50.</p>
  *
  * @param orientation Determines whether the slider is
  * oriented horizontally or vertically. Valid values are
  * HORIZONTAL and VERTICAL.
  */
  public JdbSlider(int orientation) {
    this(orientation, 0, 100, 50);
  }

 /**
  * <p>Creates a <code>JdbSlider</code> and specifies the
  * minimum and maximum values supported by the slider.
  * Calls the constructor of <code>this</code> class which
  * takes four <code>int</code> values as parameters.
  * Passes the specified minimum and maximum values, along
  * with default values of HORIZONTAL for the
  * <code>orientation</code> and 50 for the
  * <code>value</code>, to that constructor.</p>
  *
  * @param min The minimum value supported by the slider.
  * @param max The maximum value supported by the slider.
  */
  public JdbSlider(int min, int max) {
    this(HORIZONTAL, min, max, 50);
  }

 /**
  * <p>Creates a <code>JdbSlider</code> and specifies the
  * minimum and maximum values supported by the slider and
  * sets its initial position. Calls the constructor of
  * <code>this</code> class which takes four
  * <code>int</code> values as parameters.  Passes the
  * specified <code>min</code>, <code>max</code>, and
  * <code>value</code>, along with a default
  * <code>orientation</code> of HORIZONTAL to that
  * constructor.</p>
  *
  * @param min The minimum value supported by the slider.
  * @param max The maximum value supported by the slider.
  * @param value Sets the slider's initial position.
  */
  public JdbSlider(int min, int max, int value) {
    this(HORIZONTAL, min, max, value);
  }

 /**
  * <p>Creates a <code>JdbSlider</code> and specifies its
  * orientation, specifies the minimum and maximum values
  * supported by the slider, and sets its initial position,
  * by calling the constructor of its superclass which
  * takes these four parameters.  This constructor is
  * called by most of the other <code>JdbSlider</code>
  * constructors.  </p>
  *
  * @param orientation Determines whether the slider is
  * oriented horizontally or vertically. Valid values are
  * HORIZONTAL and VERTICAL.
  * @param min The minimum value supported by the slider.
  * @param max The maximum value supported by the slider.
  * @param value Sets the slider's initial position.
  */
  public JdbSlider(int orientation, int min, int max, int value) {
    super(orientation, min, max, value);
    dataBinder = new DBSliderDataBinder(this);
  }

 /**
  * <p>Creates a <code>JdbSlider</code> that gets its
  * orientation, minimum and maximum supported values, and
  * its current position from the specified model.  Calls
  * the constructor of its superclass which takes a
  * <code>BoundedRangeModel</code> as a parameter.  This is
  * the only constructor of <code>JdbSlider</code> which
  * does not invoke the constructor of <code>this</code>
  * class that takes four <code>int</code> values as
  * parameters.</p>
  *
  * @param model The <code>BoundedRangeModel</code> that
  * contains the orientation, minimum and maximum supported
  * values, and the current position of the slider.
  */
  public JdbSlider(BoundedRangeModel model) {
    super(model);
    dataBinder = new DBSliderDataBinder(this);
  }

  /**
   * <p>Sets the <code>DataSet</code> from which data
   * values are read and to which data values are
   * written.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which data
   * values are read and to which data values are
   * written.</p>
   *
   * @return dataSet The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataBinder.getDataSet();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code>
   * from which data values are read and to which data
   * values are written.</p>
   *
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code>
   * from which data values are read and to which data
   * values are written.</p>
   *
   * @return The column name.
   * @see #setColumnName
   */
  public String getColumnName() {
    return dataBinder.getColumnName();
  }

  /**
   * Sets the policy for setting slider position when synchronizing a
   * slider with a <code>DataSet</code> value outside its bounded model's range.
   *
   * @param mode The possible values are:
   *
   * <ul>
   * <li>DEFAULT - If the DataSet value is less than the
   * slider's minimum value, the slider is set to the
   * minimum value; if the value is greater than the
   * slider's maximum value, the slider is set to the
   * maximum value. </li>
   * <li>DISABLE_COMPONENT - The component is disabled.</li>
   * <li>CLEAR_VALUE - The value in the DataSet is cleared. </li>
</ul>
   *
   * @see #getUnknownDataValueMode
   */
  public void setUnknownDataValueMode(int mode) {
    dataBinder.setUnknownDataValueMode(mode);
  }

 /**
   * Returns the policy for setting slider position when synchronizing a
   * slider with a <code>DataSet</code> value outside its bounded model's range.
   *
   * @return One of:
   *
   * <ul>
   * <li>DEFAULT - If the DataSet value is less than the
   * slider's minimum value, the slider is set to the
   * minimum value; if the value is greater than the
   * slider's maximum value, the slider is set to the
   * maximum value. </li>
   * <li>DISABLE_COMPONENT - The component is disabled. </li>
   * <li>CLEAR_VALUE - The value in the DataSet is cleared. </li>
</ul>
   *
   * @see #setUnknownDataValueMode
   */
  public int getUnknownDataValueMode() {
    return dataBinder.getUnknownDataValueMode();
  }

 /**
  * <p>Returns the <code>DBSliderDataBinder</code> that makes this a data-aware component. </p>
  *
  * @return the <code>DBSliderDataBinder</code> that makes this a data-aware component.
 */
  DBSliderDataBinder getDataBinder() {
    return dataBinder;
  }

  /** <code>DBSliderDataBinder</code> which makes a data-aware component. */

  protected DBSliderDataBinder dataBinder;

}
