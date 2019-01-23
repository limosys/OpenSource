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
 * <p>The <code>JdbRadioButton</code> component extends the
 * <code>javax.swing.JRadioButton</code> class. It adds
 * the following properties:</p>
 *
 * <ul>
 * <li><code>dataSet</code> - Identifies the
 * <code>DataSet</code> to which the
 * <code>selectedDataValue</code> or
 * <code>unselectedDataValue</code> is assigned.</li>
 *
 * <li><code>columnName</code> - The name of the
 * <code>Column</code> of the <code>DataSet</code> to which
 * the value is assigned.</li>
 *
 * <li><code>selectedDataValue</code> - The value to assign
 * to the <code>Column</code> specified by
 * <code>columnName</code> of the specified
 * <code>DataSet</code> when the button is selected.</li>
 *
 * <li><code>unselectedDataValue</code> - The value to
 * assign to the <code>Column</code> specified by
 * <code>columnName</code> of the specified
 * <code>DataSet</code> when the button is not
 * selected.</li>
 *
 * <li><code>textWithMnemonic</code> - Makes the letter
 * that appears in the text string after an ampersand
 * (&amp;) a mnemonic character or &quot;hot
 *  key.&quot;</li>
 *
 * <li><code>buttonGroup</code> - The
 * <code>ButtonGroup</code> that manages the mutual
 * exclusivity of this button. To specify a
 * <code>buttonGroup</code> value, use
 * <code>setButtonGroup()</code>.</li>
 * </ul>
 *
 * <p><code>JdbRadioButton</code> sets its
 * <code>text</code>, <code>alignment</code>,
 * <code>background</code>, <code>foreground</code>, and
 * <code>font</code> properties using the property settings
 * from the <code>Column</code> specified with the
 * <code>columnName</code> property, if they are defined,
 * unless these properties are set explicitly on the
 * <code>JdbRadioButton</code> itself. The value of the
 * <code>text</code> property is considered to be its
 * default state (not explicitly set) if <code>text</code>
 * is <code>null</code> or an empty string
 * (&quot;&quot;).</p>
 *
 * <p>Like <code>JRadioButton</code>,
 * <code>JdbRadioButton</code> has constructors that set an
 * initial value. This is seldom useful for a data-aware
 * control. To set a <code>JdbRadioButton</code> to a
 * default value for each new row of a
 * <code>DataSet</code>, set the default property of the
 * <code>Column</code> the radio button is bound to.</p>
 *
 * <a name="selected_data_value_properties"></a>
 * <h3>selectedDataValue and unselectedDataValue
 * properties</h3>
 *
 * <p>In the Inspector, at design-time, the
 * <code>selectedDataValue</code> and
 * <code>unselectedDataValue</code> property values are set
 * as <code>String</code>s, regardless of the data type of
 * the column. JBuilder converts the value to the proper
 * data type if needed.</p>
 *
 * <p>At runtime, these properties take on default values
 * if they are not set. This is most useful when the
 * component is bound to a boolean column; then
 * <code>selectedDataValue</code> defaults to
 * <code>true</code> and <code>unselectedDataValue</code>
 * defaults to <code>false</code>. When bound to a numeric
 * column, the defaults are 1 and 0; when bound to a
 * <code>String</code> column, the defaults are
 * <code>true</code> and <code>false</code>.</p>
 *
 * <p>The values of the <code>selectedDataValue</code> and
 * <code>unselectedDataValue</code> properties must be
 * consistent with the display mask of the bound-to column.
 * This usually isn't an issue because this component is
 * seldom bound to real number or date/time columns, which
 * are the ones that benefit most from custom formatting.
 * However, if you do specify a display mask for a column
 * bound to one of these components, it's important to be
 * aware of this rule.</p>
 *
 * <p>Usually there is no need to set the
 * <code>unselectedDataValue</code> property for a
 * <code>JdbRadioButton</code>. If you set the radio
 * button's <code>buttonGroup</code> property, the
 * <code>ButtonGroup</code> ensures that only one button in
 * the set is selected. The unselected button has no need
 * to write to the <code>DataSet</code>. Instead the newly
 * selected button writes its
 * <code>selectedDataValue</code> to the
 * <code>DataSet</code>.</p>
 *
 * <p>For more information about these properties, see the
 * topic called <a  href="DBButtonDataBinder.html#selected_data_value_properti
es">selectedDataValue and unselectedDataValue
 * properties</a> in <code>DBButtonDataBinder</code>. </p>
 *
 * @see DBButtonDataBinder
 */

public class JdbRadioButton extends JRadioButton
  implements ColumnAware, java.io.Serializable
{

 /**
  * <p>Constructs a <code>JdbRadioButton</code> component
  * by calling the  constructor of <code>this</code> class
  * that takes a <code>String</code>, an <code>Icon</code>,
  * and a <code>boolean</code>. Default values of
  * <code>null</code>, <code>null</code>, and
  * <code>false</code> are passed to the other
  * constructor. The radio button displays no text or icon
  * and is initially selected.</p>
  */
  public JdbRadioButton () {
    this(null, null, false);
  }

 /**
  * <p>Constructs a <code>JdbRadioButton</code> component
  * by calling the  constructor of <code>this</code> class
  * that takes a <code>String</code>, an <code>Icon</code>,
  * and a <code>boolean</code>. The specified icon is
  * passed to the other constructor, along with default
  * values of <code>null</code> for the text string and
  * <code>false</code> for the boolean value. The
  * <code>Icon</code> replaces the radio button image. The
  * radio button is initially unselected.</p>
  *
  * @param icon The image that replaces the radio button image.
  */
  public JdbRadioButton(Icon icon) {
    this(null, icon, false);
  }

 /**
  * <p>Constructs a <code>JdbRadioButton</code> component
  * by calling the  constructor of <code>this</code> class
  * that takes a <code>String</code>, an <code>Icon</code>,
  * and a <code>boolean</code>. The specified icon and
  * boolean value are passed to the other constructor,
  * along with a default value of <code>null</code> for the
  * text string. The <code>Icon</code> replaces the radio
  * button image. The radio button is initially selected if
  * the <code>selected</code> parameter is
  * <code>true</code> and is not initially selected if
  * <code>false</code>.</p>
  *
  * @param icon The image that replaces the radio button
  * image.
  * @param selected If <code>true</code>, the button is
  * selected; otherwise, it is unselected.
  */
  public JdbRadioButton(Icon icon, boolean selected) {
    this(null, icon, selected);
  }

 /**
  * <p>Constructs a <code>JdbRadioButton</code> component
  * by calling the  constructor of <code>this</code> class
  * that takes a <code>String</code>, an <code>Icon</code>,
  * and a <code>boolean</code>. The specified text string,
  * along with default values of <code>null</code> and
  * <code>false</code>, are passed to the other
  * constructor.  The <code>String</code> displays beside
  * the radio button. The radio button is initially
  * unselected.</p>
  *
  * @param text The text string that appears next to and
  * identifies the button.
  */
  public JdbRadioButton (String text) {
    this(text, null, false);
  }

 /**
  * <p>Constructs a <code>JdbRadioButton</code> component
  * by calling the  constructor of <code>this</code> class
  * that takes a <code>String</code>, an <code>Icon</code>,
  * and a <code>boolean</code>. The specified text string
  * and boolean value, along with a default value of
  * <code>null</code> for the icon, are passed to the other
  * constructor. The <code>String</code> displays beside
  * the radio button. The radio button is initially
  * selected if the <code>selected</code> parameter is
  * <code>true</code> and is not initially selected if
  * <code>false</code>.
  *
  * @param text The text string that appears next to and
  * identifies the button.
  * @param selected If <code>true</code>, the button is
  * selected; otherwise, it is unselected.
  */
  public JdbRadioButton (String text, boolean selected) {
    this(text, null, selected);
  }

  /**
   * <p>Constructs a <code>JdbRadioButton</code> component
   * by calling the  constructor of <code>this</code> class
   * that takes a <code>String</code>, an
   * <code>Icon</code>, and a <code>boolean</code>.  The
   * specified text string and icon are passed to the other
   * constructor, along with a default value of
   * <code>false</code>. The <code>Icon</code> replaces the
   * radio button image and the <code>String</code>
   * displays beside it. The radio button is initially
   * unselected.</p>
   *
   * @param text The text string that appears next to and identifies the button.
   * @param icon The image that replaces the radio button.
   */
  public JdbRadioButton(String text, Icon icon) {
    this(text, icon, false);
  }

 /**
  * <p>Constructs a <code>JdbRadioButton</code> component
  * by calling the constructor of its superclass that takes
  * a <code>String</code>, an <code>Icon</code>, and a
  * <code>boolean</code> as parameters. The
  * <code>Icon</code> replaces the radio button image and
  * the <code>String</code> displays beside it. The radio
  * button is initially selected if the
  * <code>selected</code> parameter is <code>true</code>
  * and is not initially selected if
  * <code>false</code>.</p>
  *
  * <p>This constructor is called by all of the other
  * <code>JdbRadioButton</code> constructors.  It is the
  * only one of them that calls a constructor of its
  * superclass directly.</p>
  *
  * @param text The text string that appears next to and
  * identifies the button.
  * @param icon The image that replaces the radio button
  * image.
  * @param selected If <code>true</code>, the button is
  * selected; otherwise, it is unselected.
  */
  public JdbRadioButton (String text, Icon icon, boolean selected) {
    super(DBUtilities.excludeMnemonicSymbol(text), icon, selected);
    if (DBUtilities.containsMnemonic(text)) {
      setTextWithMnemonic(text);
    }
    dataBinder = new DBButtonDataBinder(this);
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
   * <p>Sets the <code>ButtonGroup</code> to which this
   * button belongs.</p>
   *
   * <p>To set a <code>buttonGroup</code> for
   * <code>JdbRadioButton</code>, use the Bean Chooser tool
   * to select the Swing <code>javax.swing.ButtonGroup</code> component and drop it
   * on to the UI designer. Then assign this component as
   * the value of the <code>buttonGroup</code> property.
   * For example:</p>
   *
   * <pre>
   *    jdbRadioButton1.setButtonGroup(buttonGroup1);
   *</pre>
   *
   * @param buttonGroup The <code>buttonGroup</code> for this radio button.
   * <code>JdbRadioButton</code>, use the Bean Chooser tool
   * @see #getButtonGroup
   */
  public void setButtonGroup(ButtonGroup buttonGroup) {
    this.buttonGroup = buttonGroup;
    if (isSelected()) {
      buttonGroup.setSelected(getModel(), true);
    }
    getModel().setGroup(buttonGroup);
  }

  /**
   * <p>Returns the <code>ButtonGroup</code> to which this
   * button belongs.</p>
   *
   * @return The <code>buttonGroup</code> for this radio button.
   * @see #setButtonGroup
   */
  public ButtonGroup getButtonGroup() {
    return buttonGroup;
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

  /** ButtonGroup to which this button belongs */
  private ButtonGroup buttonGroup;

  /** holds text property value with embedded mnemonic character */
  private String textWithMnemonic;

  /** <code>DBButtonDataBinder</code> which makes a data-aware component. */
  protected DBButtonDataBinder dataBinder;

}
