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
  * <p>Extends the <code>javax.swing.JLabel</code> class. It adds the following properties:</p>
  *
  * <ul>
  * <li><code>dataSet</code> - The <code>DataSet</code> from which the <code>text</code> property of the label is assigned.</li>
  * <li><code>columnName</code> - The <code>Column</code> of the <code>DataSet</code> from which the <code>text</code> property of the label is assigned.</li>
  * <li><code>columnNameIcon</code> - The <code>Column</code> of the <code>DataSet</code> from which the <code>icon</code> property of the label is assigned.</li>
  * <li><code>iconEditable</code> - Whether icons can be loaded from files at runtime by double-clicking the label.</li>
  * <li><code>textWithMnemonic</code> - Allows text and a mnemonic character to be set simultaneously from a single string.</li>
  * </ul>
  *
  * <p><code>columnNameIcon</code> must be of data type
  * <code>Variant.INPUTSTREAM</code> (for example, a .gif
  * or .jpg file) or <code>Variant.OBJECT</code> (for
  * example, a <code>java.awt.Image</code> or
  * <code>javax.swing.Icon</code>).</p>
  *
  * <p>Note that <code>textWithMnemonic</code> sets the
  * display mnemonic only. To activate the mnemonic, use
  * the <code>setLabelFor()</code> method to specify the
  * component that should receive focus when the mnemonic
  * key is pressed.</p>
  *
  * <p><code>JdbLabel</code> binds its
  * <code>alignment</code>, <code>background</code>,
  * <code>foreground</code>, and <code>font</code>
  * properties from those defined on the
  * <code>Column</code> <code>columnName</code> unless
  * these same properties are already set explicitly on
  * <code>JdbLabel</code>.</p>
  *
  * @see DBLabelDataBinder
  */
public class JdbLabel extends JLabel
  implements ColumnAware, java.io.Serializable
{


  /**
   * <p>Constructs a <code>JdbLabel</code> component that
   * displays the specified text and icon in the specified
   * alignment, by calling the constructor of its
   *  superclass that takes all three values as
   * parameters.
   *
   * <p>This constructor is called by all of the other
   * <code>JdbLabel</code> constructors.  This is the only
   * <code>JdbLabel</code> constructor which directly
   * calls a constructor of its superclass.
   *
   * @param text The text string that appears as the label.
   * @param icon The <code>Icon</code> displayed as part of the label.
   * @param horizontalAlignment How the text is aligned horizontally. Choose one of these values: <code>SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT</code>.
   */
  public JdbLabel(String text, Icon icon, int horizontalAlignment) {
    super(DBUtilities.excludeMnemonicSymbol(text), icon, horizontalAlignment);
    if (DBUtilities.containsMnemonic(text)) {
      setTextWithMnemonic(text);
    }
    dataBinder = new DBLabelDataBinder(this);
  }

  /**
   * <p>Constructs a <code>JdbLabel</code> component that
   * displays the specified text using the specified
   * alignment, by calling the constructor of
   * <code>this</code> class that takes a
   * <code>String</code>, an <code>Icon</code>, and an
   * <code>int</code> as its parameters.  It passes the
   * specified text and alignment, along with a default
   * value of <code>null</code> for the icon, to that
   * constructor.</p>
   *
   * @param text The text string that appears as the label.
   * @param horizontalAlignment How the text is aligned horizontally. Choose one of these values: <code>SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT</code>.
   */
  public JdbLabel(String text, int horizontalAlignment) {
    this(text, null, horizontalAlignment);
  }


  /**
   * <p>Constructs a <code>JdbLabel</code> component that
   * displays the specified text by calling the
   * constructor of <code>this</code> class that takes a
   * <code>String</code>, an <code>Icon</code>, and an
   * <code>int</code> as its parameters. It passes the
   * specified text string, along with default values of
   * <code>null</code>, and SwingConstants.LEFT to that
   * constructor.</p>
   *
   * @param text The text string that appears as the label.
   */

  public JdbLabel(String text) {
    this(text, null, LEFT);
  }

  /**
   * <p>Constructs a <code>JdbLabel</code> component that
   * displays the specified text using the specified alignment, by calling the
   * constructor of <code>this</code> class that takes a
   * <code>String</code>, an <code>Icon</code>, and an
   * <code>int</code> as its parameters. It passes the
   * specified text string and alignment, along with default values of
   * <code>null</code> for the icon to that
   * constructor.</p>
   *
   * @param image The <code>Icon</code> displayed as the label.
   * @param horizontalAlignment How the text is aligned horizontally. Choose one of these values: <code>SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT</code>.
   */
  public JdbLabel(Icon image, int horizontalAlignment) {
    this(null, image, horizontalAlignment);
  }

  /**
   * <p>Constructs a <code>JdbLabel</code> component that
   * displays the  specified icon, by calling the
   * constructor of <code>this</code> class that takes a
   * <code>String</code>, an <code>Icon</code>, and an
   * <code>int</code> as its parameters. It passes the
   * specified icon, along with default values of
   * <code>null</code> for the text string and
   * <code>SwingConstants.CENTER</code> for the alignment,
   * to that constructor. </p>
   *
   * @param image The <code>Icon</code> displayed as the label.
   */
  public JdbLabel(Icon image) {
    this(null, image, CENTER);
  }

 /**
  * <p>Constructs a <code>JdbLabel</code> component by
  * calling the constructor of <code>this</code> class
  * which takes a <code>String</code>, an
  * <code>Icon</code>, and an <code>int</code>.  It passes
  * default values of "" (an empty string),
  * <code>null</code>, and
  * <code>SwingConstants.LEFT</code> to that
  * constructor.</p>
  */
  public JdbLabel() {
    this("", null, LEFT);  
  }

  //
  // ColumnAware interface implememtation
  //

  /**
   * <p>Specifies the <code>DataSet</code> from which the data is read and to which the data is written.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which the data is read and to which the data is written.</p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   * @see #getColumnName
   */
  public DataSet getDataSet() {
    return dataBinder.getDataSet();
  }

  /**
   * <p>Stores the name of the <code>DataSet Column</code> from which the <code>JdbLabel</code> displays data.</p>
   *
   * @param columnName The name of the <code>DataSet Column</code>.
   *
   * @see #getColumnName
   * @see #setColumnNameIcon
   */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which the label's text should be read.</p>
   *
   * @see #setColumnName
   * @see #getColumnNameIcon
   */
  public String getColumnName() {
    return dataBinder.getColumnName();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which the label's
   * icon should be read and to which the icon is written.</p>
   *
   * @param columnNameIcon The column name.
   * @see #getColumnNameIcon
   * @see #setColumnName
   */
  public void setColumnNameIcon(String columnNameIcon) {
    dataBinder.setColumnNameIcon(columnNameIcon);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which the label's
   * icon should be read and to which the icon is written.</p>
   *
   * @return The column name.
   * @see #setColumnNameIcon
   * @see #getColumnName
   */
  public String getColumnNameIcon() {
    return dataBinder.getColumnNameIcon();
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
      setDisplayedMnemonic(DBUtilities.extractMnemonicChar(text));
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
   * <p>Sets whether the label's icon can be changed at runtime.
   * If <code>iconEditable</code> is <code>true</code>, double-clicking the label
   * displays a dialog box prompting the user to select
   * an image file from which to set the icon.
   * If a valid image file is selected, the label's icon is
   * updated and saved to the <code>DataSet</code> column.</p>
   *
   * <p>This property is <code>false</code> by default.</p>
   *
   * <p>Note that setting the icon directly using <code>setIcon()</code> will
   * not save the icon to the <code>DataSet</code>.  Use <code>DataSet's</code>
   * <code>setObject()</code> or <code>setInputStream()</code> methods to save an
   * icon programmatically to a <code>DataSet</code> column.</p>
   *
   * @param iconEditable If <code>true</code>, the icon can be edited.
   * @see #isIconEditable
   */
  public void setIconEditable(boolean iconEditable) {
    dataBinder.setIconEditable(iconEditable);
  }

  /**
   * <p>Returns whether the label's icon can be changed at runtime.
   * If <code>iconEditable</code> is <code>true</code>, double-clicking the label
   * displays a dialog box prompting the user to select
   * an image file from which to set the icon.
   * If a valid image file is selected, the label's icon is
   * updated and saved to the <code>DataSet</code> column.</p>
   *
   * @return If <code>true</code>, the icon can be edited.
   * @see #setIconEditable
   */

  public boolean isIconEditable() {
    return dataBinder.isIconEditable();
  }


 /**
  * <p>Returns the <code>DBLabelDataBinder</code> that makes this a data-aware component. </p>
  *
  * @return the <code>DBLabelDataBinder</code> that makes this a data-aware component.
 */
  DBLabelDataBinder getDataBinder() {
    return dataBinder;
  }

  /** holds text property value with embedded mnemonic character */
  private String textWithMnemonic;

 /**
  * <p>Returns the <code>DBLabelDataBinder</code> that makes this a data-aware component. </p>
  */
  protected DBLabelDataBinder dataBinder;

  /** whether to allow icons to be loaded at runtime */
  private boolean iconEditable = false;

}

