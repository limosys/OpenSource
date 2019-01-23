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
 * <p>Extends the <code>javax.swing.JToggleButton</code>
 * class. It adds the following properties:</p>
 *
 * <ul>
 * <li><code>dataSet</code> - Identifies the <code>DataSet</code> to which the
 * <code>selectedDataValue</code> or  <code>unselectedDataValue</code> is assigned.</li>
 * <li><code>columnName</code> - The name of the column of
 * the <code>DataSet</code> to which the value is assigned.</li>
 * <li><code>selectedDataValue</code> - The value to assign to the column specified by <code>columnName</code> of the
 * specified <code>DataSet</code> when the button is selected.</li>
 * <li><code>unselectedDataValue</code> - The value to assign to the column specified by <code>columnName</code> of the
 * specified <code>DataSet</code> when the button is not selected.</li>
 * <li><code>unknownDataValueMode</code> - Specifies the policy for synchronizing the button state when an unknown
 * <code>DataSet</code> is encountered.</li>
 * <li><code>textWithMnemonic</code> - Makes the letter that  appears in the text string after an ampersand (&amp;) a
 * mnemonic character or hot key.</li>
 * </ul>
 *
 * <p><code>JdbToggleButton</code> sets its
 * <code>text</code>, <code>alignment</code>,
 * <code>background</code>, <code>foreground</code>, and
 * <code>font</code> properties using the property settings
 * from the <code>Column</code> specified with the
 * <code>columnName</code> property, if they are defined,
 * unless these properties are set explicitly on the
 * <code>JdbToggleButton</code> itself. The value of the
 * <code>text</code> property is considered to be its default
 * state (not explicitly set) if <code>text</code> is
 * <code>null</code> or an empty string (&quot;&quot;).</p>
 *
 * <p>Like <code>JTogglebutton</code>, <code>JdbToggleButton</code> has constructors that set an
 * initial value. This is seldom useful for a data-aware
 * control. To set a <code>JdbToggleButton</code> to a
 * default value for each new row of a <code>DataSet</code>,
 * set the default property of the <code>Column</code> the
 * toggle button is bound to.</p>
 *
 * <a name="selected_data_value_properties"></a>
 * <h3>selectedDataValue and unselectedDataValue properties</h3>
 *
 * <p>In the Inspector, at design-time, the <code>selectedDataValue</code> and
 * <code>unselectedDataValue</code> property values are set
 * as <code>String</code>s, regardless of the data type of
 * the column. JBuilder converts the value to the proper data
 * type if needed.</p>
 *
 * <p>At runtime, these properties take on default values if
 * they are not set. This is most useful when the component
 * is bound to a boolean column; then  <code>selectedDataValue</code> defaults to <code>true</code>
 * and <code>unselectedDataValue</code> defaults to
 * <code>false</code>. When bound to a numeric column, the
 * defaults are 1 and 0; when bound to a <code>String</code>
 * column, the defaults are <code>true</code> and
 * <code>false</code>.</p>
 *
 * <p>The values of the <code>selectedDataValue</code> and
 * <code>unselectedDataValue</code> properties must be
 * consistent with the display mask of  the bound-to column.
 * This usually isn't an issue because this component is
 * seldom bound to real number or date/time columns, which
 * are the ones that benefit most from custom formatting.
 * However, if you do specify a display mask for a column
 * bound to one of these components, it's important to be
 * aware of this rule.</p>
 *
 * <p>For example, a boolean column with a
display mask of "Yes;No" bound to a
 * <code>JdbToggleButton</code> with a <code>selectedDataValue</code> of
 * <code>true</code> will fail on open with a <code>com.borland.dx.text.InvalidFormatException</code> with
 * the message "Error in parsing string". Set its
 * <code>selectedDataValue</code> to"Yes" instead.</p>
 *
 * @see DBButtonDataBinder
 */
public class JdbToggleButton extends JToggleButton
  implements DBDataBinder, ColumnAware, java.io.Serializable
{

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component by
  * calling the constructor of <code>this</code> class that
  * takes a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code>. Passes default values of
  * <code>null</code>, <code>null</code>, and
  * <code>false</code> to that constructor. The button has no
  * text or icon and is initially unselected.</p>
  */
  public JdbToggleButton () {
    this(null, null, false);
  }

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component by
  * calling the constructor of <code>this</code> class which
  * takes a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code> value as parameters. Passes the
  * specified icon, along with default values of
  * <code>null</code> for the <code>String</code>, and
  * <code>false</code> for the <code>boolean</code>, to that
  * constructor.</p>
  *
  * @param icon The image to display on the button.
  */
  public JdbToggleButton(Icon icon) {
    this(null, icon, false);
  }

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component by
  * calling the constructor of <code>this</code> class that
  * takes a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code> as parameters. Passes the specified
  * icon and <code>boolean</code> value, along with a
  * <code>null</code> <code>String</code>, to that
  * constructor.</p>
  *
  * @param icon The image to display on the button.
  * @param selected f <code>true</code>, the button is initially selected. If <code>false</code>, the button is not initially selected.
  */
  public JdbToggleButton(Icon icon, boolean selected) {
    this(null, icon, selected);
  }

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component by
  * calling the constructor of <code>this</code> class that
  * takes a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code>. Passes the specified text string,
  * along with default values of <code>null</code> and
  * <code>false</code> to that constructor.</p>
  *
  * @param text The text value to display on the button.
  */
  public JdbToggleButton (String text) {
    this(text, null, false);
  }

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component by
  * calling the constructor of <code>this</code> class that
  * takes a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code> as parameters. Passes the specified
  * text string and <code>boolean</code> value, along with a
  * default value of <code>null</code> for the
  * <code>Icon</code>, to that constructor.</p>
  *
  * @param text The text value to display on the button.
  * @param selected If <code>true</code>, the button is initially selected. If <code>false</code>, the button is not initially selected.
  */
  public JdbToggleButton (String text, boolean selected) {
    this(text, null, selected);
  }

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component by
  * calling the constructor of <code>this</code> class that
  * takes a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code> as parameters. Passes the specified
  * text string and icon, along with a default
  * <code>boolean</code> value of <code>false</code>, to that
  * constructor.</p>
  *
  * @param text The text value to display on the button.
  * @param icon The image to display on the button.
  */
  public JdbToggleButton(String text, Icon icon) {
    this(text, icon, false);
  }

 /**
  * <p>Constructs a <code>JdbToggleButton</code> component
  * by calling the constructor of its superclass and passing
  * the specified <code>String</code>, <code>Icon</code>,
  * and <code>boolean</code> value. This constructor is
  * called by all of <code>JdbToggleButton</code>'s other
  * constructors, and is the only one of them which calls a
  * constructor of its superclass directly.</p>
  *
  * @param text The text value to display on the button.
  * @param icon The image to display on the button.
  * @param selected If <code>true</code>, the button is initially selected. If <code>false</code>, the button is not initially selected.
  */
  public JdbToggleButton(String text, Icon icon, boolean selected) {
    super(DBUtilities.excludeMnemonicSymbol(text), icon, selected);
    if (DBUtilities.containsMnemonic(text)) {
      setTextWithMnemonic(text);
    }
    dataBinder = new DBButtonDataBinder(this);
  }

  /**
   * <p>Sets the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @return dataSet The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataBinder.getDataSet();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which data values are read and to which data values are written.</p>
   *
   * @return The column name.
   * @see #setColumnName
   */
  public String getColumnName() {
    return dataBinder.getColumnName();
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
    dataBinder.setSelectedDataValue(selectedValue);
  }

  /**
   * <p>Returns the value written to the <code>DataSet</code> when the
   * button is selected <code>(button.isSelected() == true)</code>.</p>
   *
   * @return The value written.
   * @see #setSelectedDataValue
   */
  public String getSelectedDataValue() {
    return dataBinder.getSelectedDataValue();
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
    dataBinder.setUnselectedDataValue(unselectedValue);
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
    return dataBinder.getUnselectedDataValue();
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
    dataBinder.setUnknownDataValueMode(mode);
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
    return dataBinder.getUnknownDataValueMode();
  }

 /**
  * <p>Sets the text with an embedded mnemonic character.
  * <code>textWithMnemonic</code> is a convenience
  * property for setting the button's text, which
  * interprets an ampersand character (&) within the
  * text as an instruction to make the character
  * following the ampersand the mnemonic character for
  * the button. To put an ampersand in the text but not
  * make the character following it a hot key, put a back
  * slash before the ampersand. To make the ampersand the
  * hot key, put two consecutive ampersands in the text. </p>
  *
  * <p>This property can be used instead of the usual <code>text</code>
  * property, even if a mnemonic character is not
  * embedded in the text. It is particularly useful for
  * applications that resource strings for
  * internationalization, because the text and mnemonic
  * can be specified in a single string. </p>
  *
  * <p>Note that the first occurrence of the mnemonic
  * character is always denoted visibly as the mnemonic
  * key, despite the location of the ampersand within the
  * text. Furthermore, only the first occurrence of an
  * ampersand is removed from the text. </p>
  *
  * <p>If both the <code>text</code> and <code>textWithMneumonic</code> properties are
  * set, the most recently set property takes
  * precedence. </p>
  *
  * <p><code>textWithMnemonic</code> is a bound property, and therefore a
  * property change event is fired when its value
  * is modified. </p>
  *
  * @param text The text to set with an embedded mnemonic character.
  * @see #getTextWithMnemonic
  */
  public void setTextWithMnemonic(String text) {
    String oldText = textWithMnemonic;
    if (oldText != text) {
      textWithMnemonic = text;
      setText(DBUtilities.excludeMnemonicSymbol(text));
      setMnemonic(DBUtilities.extractMnemonicChar(text));
      firePropertyChange("textWithMnemonic", oldText, text);    
    }
  }

  /**
   * <p>Returns the text with the embedded mnemonic character.</p>
   *
  * @return The text with with an embedded mnemonic character.
   * @see #setTextWithMnemonic
   */
  public String getTextWithMnemonic() {
    return textWithMnemonic;
  }

 /**
  * <p>Returns the <code>DBButtonDataBinder</code> that makes this a data-aware component. </p>
  *
  * @return the <code>DBButtonDataBinder</code> that makes this a data-aware component.
 */
  DBButtonDataBinder getDataBinder() {
    return dataBinder;
  }

  /** holds text property value with embedded mnemonic character */
  private String textWithMnemonic;

 /**
  * <p>Returns the <code>DBButtonDataBinder</code> that makes this a data-aware component. </p>
  */
  protected DBButtonDataBinder dataBinder;

}
