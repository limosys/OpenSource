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
import javax.swing.*;

import com.borland.dx.dataset.*;

 /**
  * <p>Extends the <code>javax.swing.JCheckBox</code>
  * class. <code>JdbCheckBox</code> adds the following
  * properties: </p>
  *
  * <ul>
  * <li><code>dataSet</code> - Identifies the
  * <code>DataSet</code> to which the
  * <code>selectedDataValue</code> or
  * <code>unselectedDataValue</code> is assigned.</li>
  * <li><code>columnName</code> - The name of the column
  * of the <code>DataSet</code> to which the value is
  * assigned.</li>
  * <li><code>selectedDataValue</code> - The value to
  * assign to the column specified by
  * <code>columnName</code> of the specified
  * <code>DataSet</code> when the check box is
  * selected.</li>
  * <li><code>unselectedDataValue</code> - The value to
  * assign to the column specified by
  * <code>columnName</code> of the specified
  * <code>DataSet</code> when the check box is not 
  * selected.</li>
  * <li><code>unknownDataValueMode</code> - The
  * policy for synchronizing the check box state when an
  * unknown <code>DataSet</code> column value is
  * encountered.</li>
  * <li><code>textWithMnemonic</code> - Makes the letter
  * that appears in the text string after an ampersand (&)
  * a mnemonic character or "hot key." </li>
  * </ul>
  *
  * <p><code>JdbCheckBox</code> sets its <code>text,
  * alignment, background, foreground</code>, and
  * <code>font</code> properties using the property
  * settings from the <code>Column</code> specified with
  * the <code>columnName</code> property, if they are
  * defined, unless these properties are set explicitly on
  * the <code>JdbCheckBox</code> itself. The value of the
  * text property is considered to be its default state
  * (not explicitly set) if text is null or an empty
  * string (""). </p>
  *
  * <p>Like <code>JCheckBox,</code> 
  * <code>JdbCheckBox</code> has constructors that set an
  * initial value. This is seldom useful for a data-aware
  * control. To set a <code>JdbCheckBox</code> to a
  * default value for each new row of a
  * <code>DataSet,</code> set the default property of the
  * <code>Column</code> the check box is bound to. </p>
  *
  *<p><strong>selectedDataValue and unselectedDataValue properties</strong></p>
  *
  * <p>In the Inspector, at design-time, the
  * <code>selectedDataValue</code> and
  * <code>unselectedDataValue</code> property values are
  * set as <code>Strings,</code> regardless of the data
  * type of the <code>Column.</code> JBuilder converts the
  * value to the proper data type if needed. </p>
  *
  * <p>At runtime, these properties take on default values
  * if they are not set. This is most useful when the
  * component is bound to a <code>boolean Column;</code>
  * then <code>selectedDataValue</code> defaults to
  * <code>true</code> and 
  * <code>unselectedDataValue</code> defaults to <code>false</code>.
  * When bound to a numeric <code>Column,</code> the
  * defaults are 1 and 0; when bound to a <code>String
  * Column,</code> the defaults are <code>true</code>
  * and <code>false</code>. </p>
  *
  * <p>For more information about these properties, see
  * <code>selectedDataValue</code> and
  * <code>unselectedDataValue</code>
  * properties in <a  href="DBButtonDataBinder"><code>DBButtonDataBinder</code></a></p>. 
 *
 */
public class JdbCheckBox extends JCheckBox
  implements DBDataBinder, ColumnAware, java.io.Serializable
{

/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of this class which takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters.</p>
 * 
 * <p>This constructor passes default values of
 * <code>null, null</code>, and <code>false</code> to
 * the other <code>JdbCheckBox</code> constructor. That
 * constructor then calls the constructor of its
 * superclass that also takes a <code>String</code>, an
 * <code>Icon</code>, and a <code>boolean</code> as
 * parameters. </p>
 *
 * <p>The check box resulting from this constructor has no text or icon and is initially unselected. </p>
*/
  public JdbCheckBox () {
    this(null, null, false);
  }

/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of this class which takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters. </p>
 * 
 * <p>This constructor passes the specified icon, along
 * with default values of <code>null</code> for the
 * <code>String</code> and <code>false</code> for the
 * boolean, to the other <code>JdbCheckBox</code> 
 * constructor. That constructor then calls the
 * constructor of its superclass that also takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters. </p>
 * 
 * <p>The resulting check box has the icon displayed beside it. The check box is initially unselected. </p>
 *
 * @param icon The icon that displays next to the check box. 
*/
  public JdbCheckBox(Icon icon) {
    this(null, icon, false);
  }

/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of this class which takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters.</p>
 * 
 * <p>This constructor passes the specified icon and
 * <code>boolean</code>, along with a default value of
 * <code>null</code> for the <code>String</code>, to the
 * other <code>JdbCheckBox</code> constructor. That
 * constructor then calls the constructor of its
 * superclass that also takes a <code>String</code>, an
 * <code>Icon</code>, and a <code>boolean</code> as parameters. </p>
 *
 * <p>The resulting check box has the icon displayed
 * beside it. The check box is initially selected if the
 * selected parameter is <code>true</code> and is not initially
 * selected if <code>false</code>. </p>
 *
 * @param icon The icon that displays next to the check box. 
 * @param selected If <code>true</code>, the check box is checked; if <code>false</code>, the check box is not checked. 
 *
 */
  public JdbCheckBox(Icon icon, boolean selected) {
    this(null, icon, selected);
  }


/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of this clas which takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters. </p>
 * 
 * <p>This constructor passes the specified text string,
 * along with default values of <code>null</code>, and
 * <code>false</code> to the other 
 * <code>JdbCheckBox</code> constructor.  That constructor
 * then calls the constructor of its superclass that also
 * takes a <code>String</code>, an
 * <code>Icon</code>, and a <code>boolean</code> as
 * parameters. </p>
 *
 * <p>The resulting check box has the specified text
 * string displayed beside it. The check box is initially
 * unselected. </p>
 * 
 * @param text The text string that displays next to the check box that identifies it. 
*/
   
  public JdbCheckBox(String text) {
    this(text, null, false);
  }

/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of this class which takes a
 * a <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters.  </p>
 * 
 * <p>This constructor passes the specified text string
 * and selected parameter value, along with a default
 * value of <code>null</code> for the <code>Icon</code>
 * parameter, to the other <code>JdbCheckBox</code>
 * constructor.  That constructor then calls the
 * constructor of its superclass that also takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters. </p>
 *
 * <p>The resulting check box has the specified text
 * string displayed beside it. The check box is initially
 * selected if the selected parameter is <code>true</code> and is not
 * initially selected if <code>false</code>. </p>
 *
 * @param text  The text string that displays next to the check box that identifies it. 
 * @param selected If <code>true</code>, the check box is checked; if <code>false</code>, the check box is not checked. 
*/
  public JdbCheckBox (String text, boolean selected) {
    this(text, null, selected);
  }

/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of this class which takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters. </p>
 * 
 * <p>This constructor passes the specified text string
 * and icon, along with a default boolean value of
 * <code>false</code>, to the other 
 * <code>JdbCheckBox</code> constructor.  That constructor
 * then calls the constructor of its superclass that also
 * takes a <code>String</code>, an <code>Icon</code>, and
 * a <code>boolean</code> as parameters. </p>
 *
 * <p>The resulting check box has the specified
 * <code>String</code> and <code>Icon</code> displayed
 * beside it. The check box is initially unselected. </p>
 *
 * @param text The text string that displays next to the check box that identifies it. 
 * @param icon The icon that displays next to the check box. 
*/
  public JdbCheckBox(String text, Icon icon) {
    this(text, icon, false);
  }

/**
 * <p>Constructs a <code>JdbCheckBox</code> component by
 * calling the constructor of its superclass that takes a
 * <code>String</code>, an <code>Icon</code>, and a
 * <code>boolean</code> as parameters. The
 * <code>String</code> and <code>Icon</code> display
 * beside the check box. The check box is initially
 * selected if the selected parameter is
 * <code>true</code> and is not initially selected if
 * <code>false</code>. </p>
 * 
 * <p>This constructor is called by all of the other
 * <code>JdbCheckBox</code> constructors, and is the only
 * one of them that directly invokes a constructor of its
 * superclass. </p>
 * 
 * @param text The text string that displays next to the check box that identifies it. 
 * @param icon The icon that displays next to the check box. 
 * @param selected If <code>true</code>, the check box is checked; if <code>false</code>, the check box is not checked. 
 */
  public JdbCheckBox (String text, Icon icon, boolean selected) {
    super(DBUtilities.excludeMnemonicSymbol(text), icon, selected);
    if (DBUtilities.containsMnemonic(text)) {
      setTextWithMnemonic(text);
    }
    dataBinder = new DBButtonDataBinder(this);
  }

  /**
   * <p>Sets the <code>DataSet</code> to which values are  written and from which values are read.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which values are read and to which values are written.</p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataBinder.getDataSet();
  }

 /**
  * <p>Specifies a <code>Column</code> name in the
  * <code>DataSet</code> to display data from and write
  * data to. Usually the data type for the value in the
  * <code>Column</code> linked to a 
  * <code>JdbCheckBox</code> is <code>boolean</code>.
  * Regardless of the type, the
  * <code>selectedDataValue</code> and
  * <code>unselectedDataValue</code> values are specified
  * as <code>Strings</code> and JBuilder converts them to
  * the appropriate data type. </p>
  *
  * @param columnName The <code>Column</code> name in the <code>DataSet</code> to display data from and write data to. 
  * @see #getColumnName
  * @see #setSelectedDataValue
  * @see #setUnselectedDataValue
  */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }


  /**
   * <p>Returns the <code>Column</code> name in the
   *  <code>DataSet</code> to display data from and write
   *  data to. </p>
   *
   * @return The <code>Column</code> name in the <code>DataSet</code> to display data from and write data to. 
   * @see #setColumnName
   * @see #getSelectedDataValue
   * @see #getUnselectedDataValue
   */

  public String getColumnName() {
    return dataBinder.getColumnName();
  }

  /**
   * <p>Sets the value to be written to the
   * <code>DataSet</code> when the
   * button is 'selected' <code>(button.isSelected() == true)</code>.
   * Setting this value to <code>null</code> results in nothing being
   * written to the <code>DataSet</code>.  Setting this value to an
   * empty string ("") results in the <code>DataSet</code> value being
   * cleared.</p>
   *
   * <p>Note that regardless of the data type of the
   * <code>Column</code>, the property value is set as a
   * <code>String</code>. JBuilder converts it to the
   * proper data type if needed. </p>
   *
   * @param selectedValue The value to be written to the <code>DataSet</code> when the butotn is selected.
   * @see #getSelectedDataValue
   */
  public void setSelectedDataValue(String selectedValue) {
    dataBinder.setSelectedDataValue(selectedValue);
  }

  /**
   * <p>Returns the value written to the <code>DataSet</code> when the
   * button is selected <code>(button.isSelected() == true)</code>.</p>
   *
   * @return The value to be written to the <code>DataSet</code> when the butotn is selected.
   * @see #setSelectedDataValue
   */
  public String getSelectedDataValue() {
    return dataBinder.getSelectedDataValue();
  }

  /**
   * <p>Sets the value to be written to the <code>DataSet</code> when the 
   * button is unselected <code>(button.isSelected() == false)</code>. 
   * Setting this value to <code>null</code> results in nothing being
   * written to the <code>DataSet</code>.  Setting this value to an
   * empty string ("") results in the <code>DataSet</code> value being 
   * cleared.</p>
   *
   * <p>Note that regardless of the data type of the
   * column, the property value is set as a
   * <code>String</code>. JBuilder converts it to the
   * proper data type if needed. </p>
   *
   * @param unselectedValue The value to be written to the <code>DataSet</code> when the button is unselected.
   * @see #getUnselectedDataValue
   */
  public void setUnselectedDataValue(String unselectedValue) {
    dataBinder.setUnselectedDataValue(unselectedValue);
  }

  /**
   * <p>Returns the value written to the <code>DataSet</code> when the 
   * button is unselected. </p>
   *
   * @return The value to be written to the <code>DataSet</code> when the button is unselected.
   * @see #setUnselectedDataValue
   */
  public String getUnselectedDataValue() {
    return dataBinder.getUnselectedDataValue();
  }


  /**
   * <p>Sets the policy for setting check box state when
   * synchronizing the check box with its
   * <code>DataSet</code> value when the value doesn't
   * match either of the <code>selectedDataValue</code> or
   * <code>unselectedDataValue</code> property values. </p>

   * @param mode Possible values are:
   * <ul>
   * <li>DEFAULT - The check box remains in its unselected state.</li>
   * <li>DISABLE_COMPONENT - The check box is disabled.</li>
   * <li>CLEAR_VALUE - The value in the DataSet is cleared.</li>
   * </ul>
   * @see #getUnknownDataValueMode
   * @see DBButtonDataBinder 
   */
  public void setUnknownDataValueMode(int mode) {
    dataBinder.setUnknownDataValueMode(mode);
  }

  /**
   * <p>Returns the policy for setting check box state when
   * synchronizing the check box with its
   * <code>DataSet</code> value when the value doesn't
   * match either of the <code>selectedDataValue</code> or
   * <code>unselectedDataValue</code> property values. </p>

   * @return Possible values are:
   * <ul>
   * <li>DEFAULT - The check box remains in its unselected state.</li>
   * <li>DISABLE_COMPONENT - The check box is disabled.</li>
   * <li>CLEAR_VALUE - The value in the DataSet is cleared.</li>
   * </ul>
   * @see #setUnknownDataValueMode
   * @see DBButtonDataBinder 
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

  /** <code>DBButtonDataBinder</code> which makes a data-aware component. */
  protected DBButtonDataBinder dataBinder;

}
