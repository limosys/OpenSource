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
import javax.swing.*;
import javax.swing.text.*;
import com.borland.dx.dataset.*;

 /**
  * <p>A data-aware extension of the <code>JTextField</code> component.</p>
  *
  * <p>Although data in a <code>JdbTextField</code> is
  * always edited as a <code>String</code>, a
  * <code>JdbTextField</code> can be used to display and
  * edit data from all <code>DataSet</code> data types
  * except for BLOB/INPUTSTREAM-like types.</p>
  *
  * <p><code>JdbTextField</code> also provides a
  * right-click/<kbd>Shift+F10</kbd> menu for performing
  * simple editing tasks, such as cutting, copying, or
  * pasting clipboard data.  Some of the menu's behavior
  * can be customized via property settings:</p>
  *
  * <ul>
  * <li><code>enablePopupMenu</code> - Sets whether or not
  * the popup menu is displayable.</li>
  *
  * <li><code>enableClearAll</code>  - Sets whether the
  * Clear All popup menu selection appears.</li>
  *
  * <li><code>enableUndoRedo</code>  - Sets whether the
  * Undo and Redo popup menu selections appear.</li>
  * </ul>
  *
  * <p>To make a <code>JdbTextField</code> data-aware, set
  * its <code>dataSet</code> and <code>columnName</code>
  * properties.  Note that the <code>JdbTextField</code>'s
  * menu is available regardless of whether it is attached
  * to a <code>DataSet</code> or not.</p>
  *
  * <p>Data typed into a <code>JdbTextField</code> is not
  * saved immediately to the <code>DataSet</code>.  Rather,
  * certain conditions or events automatically cause the
  * data to be put into the <code>DataSet</code>'s
  * <code>Column</code>. The following two properties which
  * influence this behavior are.</p>
  *
  * <ul>
  * <li><code>postOnFocusLost</code> - If <code>true</code>, data is saved to the
  * <code>DataSet</code> whenever the  <code>JdbTextField</code> loses focus.</li>
  *
  * <li><code>postOnRowPosted</code> - If  <code>true</code>, data is saved to the
  * <code>DataSet</code> whenever the current <code>DataSet</code> row is posted.</li>
  * </ul>
  *
  * <p>When a <code>JdbTextField</code> is attached to a
  * <code>DataSet</code>, the following keystrokes perform
  * special tasks when pressed.</p>
  *
  * <p><strong>JdbTextField keystrokes</strong>
  *
  * <table cellspacing="2" cellpadding="2" border="1" frame="box">
  * <tr>
  *    <th align="LEFT">Keystroke(s)</th>
  *    <th align="LEFT">Action</th>
  * </tr>
  * <tr>
  *    <td valign="top" ><KBD>Enter</KBD></td>
  *    <td valign="top" >Causes the data in the field to be written to the <code>DataSet</code> <code>Column</code>. In
  * general, changes to the field's text are not saved to
  * the <code>DataSet</code> <code>Column</code> until some
  * action that posts the field occurs.</td>
  * </tr>
  * <tr>
  *    <td valign="top" ><KBD>Esc</KBD></td>
  *    <td valign="top" >Makes the data in the field change  back to the value in the <code>DataSet</code>
  * <code>Column</code>. Pressing <KBD>Esc</KBD> discards
  * any Undo/Redo information that has been accumulated.</td>
  * </tr>
  * <tr>
  *    <td valign="top" ><KBD>PgUp, PgDn</KBD></td>
  *    <td valign="top" >Moves to the previous and next  <code>DataSet</code> row, respectively. If the
  * <code>postOnRowPosted</code> property is
  * <code>true</code>, pressing the <KBD>PgUp</KBD> or
  * <KBD>PgDn</KBD> key saves all the text in the
  * <code>JdbTextField</code> to the <code>DataSet</code>.</td>
  * </tr>
  * <tr>
  *    <td valign="top" ><KBD>Tab, Shift+Tab</KBD></td>
  *    <td valign="top" >Using either of these keystrokes to move to another component saves the current text to the
  * <code>DataSet</code> if the <code>postOnFocusLost</code> property is <code>true</code>.
  * More generally, if the <code>postOnFocusLost</code>
  * property is <code>true</code>, any action that causes
  * the <code>JdbTextField</code> to lose focus causes the
  * text to be saved in the <code>Column</code>.</td>
  * </tr>
  * </table>
  *
  * @see DBTextDataBinder
  */
public class JdbTextField extends JTextField
  implements ColumnAware, java.io.Serializable
{

/**
 * <p>Constructs a <code>JdbTextField</code> component by
 * calling the constructor of <code>this</code> class that
 * takes a <code>Document</code>, a <code>String</code>,
 * and an <code>int</code> as parameters.  Passes default
 * values of <code>null</code>, <code>null</code>, and 0 to
 * that constructor.</p>
 */
  public JdbTextField() {
    this(null, null, 0);
  }

/**
 * <p>Constructs a <code>JdbTextField</code> component by
 * calling the constructor of <code>this</code> class that
 * takes a <code>Document</code>, a <code>String</code>,
 * and an <code>int</code> as parameters. Passes the
 * specified text string, along with default values of
 * <code>null</code> for the <code>Document</code> and 0
 * for the <code>columns</code> parameter, to the other
 * constructor.</p>
 *
 * @param text The text you want to appear initially in the text field.
 */
  public JdbTextField(String text) {
    this(null, text, 0);
  }

/**
 * <p>Constructs a <code>JdbTextField</code> component by
 * calling the constructor of <code>this</code> class that
 * takes a <code>Document</code>, a <code>String</code>,
 * and an <code>int</code> as parameters. Passes the
 * specified number of columns, along with default
 * <code>null</code> values for the <code>Document</code>
 * and <code>String</code> to the other constructor.</p>
 *
 * <p>Note that the <code>columns</code> parameter is not
 * related to the rows and columns of a
 * <code>DataSet</code>, and is only a request for the
 * desired width of the control. Depending on the font you
 * use, you may see many more characters in the control
 * than the value of <code>columns</code> leads you to
 * expect. Text in a <code>JdbTextField</code> can scroll,
 * so you can type in more characters than are visible.</p>
 *
 * <p>Not all layouts will respect this requested width.
 * <code>FlowLayout</code> will, for example, but
 * <code>BorderLayout</code> won't. <code>XYLayout</code>
 * will as long as you don't resize the control.</p>
 *
 * @param columns The number of visible character columns wide you want the text field to be.
 */
  public JdbTextField(int columns) {
    this(null, null, columns);
  }

/**
 * <p>Constructs a <code>JdbTextField</code> component by
 * calling the constructor of <code>this</code> class that
 * takes a <code>Document</code>, a <code>String</code>,
 * and an <code>int</code> as parameters. Passes the
 * specified text string and number of columns, along with
 * a default <code>null</code> <code>Document</code>, to
 * the other constructor.</p>
 *
 * <p>Note that the <code>columns</code> parameter is not
 * related to the rows and columns of a
 * <code>DataSet</code>, and is only a request for the
 * desired width of the control. Depending on the font you
 * use, you may see many more characters in the control
 * than the value of <code>columns</code> leads you to
 * expect. Text in a <code>JdbTextField</code> can scroll,
 * so you can type in more characters than are visible.</p>
 *
 * <p>Not all layouts will respect this requested width.
 * <code>FlowLayout</code> will, for example, but
 * <code>BorderLayout</code> won't. <code>XYLayout</code>
 * will as long as you don't resize the control.</p>
 *
 * @param text The text you want to appear initially in the text field.
 * @param columns The number of visible character columns wide you want the text field to be.
 */
  public JdbTextField(String text, int columns) {
    this(null, text, columns);
  }

/**
 * <p>Constructs a <code>JdbTextField</code> component that
 * uses the specified <code>Document</code>, text string,
 * and number of columns. Calls the constructor of its
 * superclass that takes these three parameters. This
 * constructor is called by all of the other
 * <code>JdbTextField</code> constructors, and is the only
 * one of them that calls a constructor of its superclass
 * directly.</p>
 *
 * <p>Note that the <code>columns</code> parameter is not
 * related to the rows and columns of a
 * <code>DataSet</code>, and is only a request for the
 * desired width of the control. Depending on the font you
 * use, you may see many more characters in the control
 * than the value of <code>columns</code> leads you to
 * expect. Text in a <code>JdbTextField</code> can scroll,
 * so you can type in more characters than are visible.</p>
 *
 * <p>Not all layouts will respect this requested width.
 * <code>FlowLayout</code> will, for example, but
 * <code>BorderLayout</code> won't. <code>XYLayout</code>
 * will as long as you don't resize the control.</p>
 *
 * @param doc The text storage model to use.
 * @param text The text you want to appear initially in the text field.
 * @param columns The number of visible character columns wide you want the text field to be.
 */
  public JdbTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    commonInit();
  }

  /**
   * <p>Used to initialize <code>JdbTextField</code> with
   * the same defaults, regardless of the constructor used.</p>
   *
   * <p>A newly instantiated <code>JdbTextField</code>
   * differs from a <code>JTextField</code> in that it has
   * a non-blinking cursor, and slightly larger left and right margins.</p>
   */
  protected void commonInit() {
    Insets margin = getMargin();
    setMargin(new Insets(margin.top, margin.left + 2, margin.bottom, margin.right + 2));
    dataBinder = new DBTextDataBinder(this);
  }

  /**
   * <p>Sets the <code>DataSet</code> from which values are
   * read.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which values
   * are read. </p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataBinder.getDataSet();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from
   * which values are read.</p>
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
   * from which values are read.</p>
   *
   * @return The column name.
   * @see #setColumnName
   * @see #getDataSet
   */
  public String getColumnName() {
    return dataBinder.getColumnName();
  }

  /**
   * <p>Sets whether a popup menu appears when the user
   * right-clicks the text field or presses <KBD>Shift+F10</KBD>.
   *  The default value is <code>true</code>.</p>
   *
   * @param popupEnabled If <code>true</code>, a popup menu is enabled.
   * @see #isEnablePopupMenu
   */
  public void setEnablePopupMenu(boolean popupEnabled) {
    dataBinder.setEnablePopupMenu(popupEnabled);
  }

  /**
   * <p>Return whether a popup menu appears when the user
   * right-clicks the text field or presses <KBD>Shift+F10</KBD>. </p>
   *
   * @return If <code>true</code>, a popup menu is enabled.
   * @see #setEnablePopupMenu
   */
  public boolean isEnablePopupMenu() {
    return dataBinder.isEnablePopupMenu();
  }

  /**
   * <p>Sets whether the current text is entered in the
   * <code>DataSet</code>'s <code>Column</code> when focus is lost
   * on the text field. The default value is <code>true</code>.</p>
   *
   * @param postOnFocusLost If <code>true</code>, the current text is entered in the <code>DataSet</code>'s <code>Column</code>.
   * @see #isPostOnFocusLost
   */
  public void setPostOnFocusLost(boolean postOnFocusLost) {
    dataBinder.setPostOnFocusLost(postOnFocusLost);
  }

  /**
   * <p>Returns whether the current text is entered in the
   * <code>DataSet</code>'s <code>Column</code> when focus is lost
   * on the text field. </p>
   *
   * @return If <code>true</code>, the current text is entered in the <code>DataSet</code>'s <code>Column</code>.
   * @see #setPostOnFocusLost
   */
  public boolean isPostOnFocusLost() {
    return dataBinder.isPostOnFocusLost();
  }

  /**
   * <p>Sets whether the current text should be put in the
   * <code>DataSet's</code> column when the current row is
   * posted. This occurs, for example, if the user presses
   * a row navigation key while the text component has
   * current focus. The default value is
   * <code>true</code>.</p>
   *
   * @param postOnRowPosted If <code>true</code>, the current text should be put in the <code>DataSet's</code> column.
   * @see #isPostOnRowPosted
   */
  public void setPostOnRowPosted(boolean postOnRowPosted) {
    dataBinder.setPostOnRowPosted(postOnRowPosted);
  }

  /**
   * <p>Returns whether the current text should be put in the
   * <code>DataSet's</code> column when the current row is
   * posted.</p>
   *
   * @return If <code>true</code>, the current text should be put in the <code>DataSet's</code> column.
   * @see #setPostOnRowPosted
   */
  public boolean isPostOnRowPosted() {
    return dataBinder.isPostOnRowPosted();
  }

  /**
   * <p>Sets whether the Clear All menu selection is
   * added to the default popup menu.
   * <code>True</code> by default.</p>
   *
   * @param enableClearAll If <code>true</code>, Clear All is added to the default popup menu.
   * @see #isEnableClearAll
   */
  public void setEnableClearAll(boolean enableClearAll) {
    dataBinder.setEnableClearAll(enableClearAll);
  }

  /**
   * <p>Returns whether the Clear All menu selection has
   * been added to the default popup menu.
   *
   * @return If <code>true</code>, Clear All is added to the default popup menu.
   * @see #setEnableClearAll
   */
  public boolean isEnableClearAll() {
    return dataBinder.isEnableClearAll();
  }

  /**
   * <p>Sets whether the Undo and Redo popu menu commands are displayed on the popup menu. The default value is
   * <code>true</code>.</p>
   *
   * @param enableUndoRedo If <code>true</code>, the Undo/Redo menu selection is displayed.
   * @see #isEnableUndoRedo
   * @see #setEnablePopupMenu
   */
  public void setEnableUndoRedo(boolean enableUndoRedo) {
    dataBinder.setEnableUndoRedo(enableUndoRedo);
  }

  /**
   * <p>Returns whether the Undo and Redo popu menu commands are displayed on the popup menu.</p>
   *
   * @return If <code>true</code>, the Undo/Redo menu selection is displayed.
   * @see #setEnableUndoRedo
   * @see #isEnablePopupMenu
   */
  public boolean isEnableUndoRedo() {
    return dataBinder.isEnableUndoRedo();
  }

  /**
   * <p>Sets whether pressing <kbd>Enter</kbd>
   * automatically moves focus to the next focusable field.
   * The default value is <code>true</code>.</p>
   *
   * @param nextFocusOnEnter If <code>true</code>, pressing <kbd>Enter</kbd> automatically moves focus to the next focusable field.
   * @see #isNextFocusOnEnter
   */
  public void setNextFocusOnEnter(boolean nextFocusOnEnter) {
    dataBinder.setNextFocusOnEnter(nextFocusOnEnter);
  }

  /**
   * <p>Returns whether pressing <kbd>Enter</kbd>
   * automatically moves focus to the next focusable field.</p>
   *
   * @return If <code>true</code>, pressing <kbd>Enter</kbd> automatically moves focus to the next focusable field.
   * @see #isNextFocusOnEnter
   */
  public boolean isNextFocusOnEnter() {
    return dataBinder.isNextFocusOnEnter();
  }

  // used by DBTextDataBinder actions to get the dataBinder
 /**
  * <p>Returns the <code>DBTextDataBinder</code> that makes this a data-aware component. </p>
  *
  * @return the <code>DBTextDataBinder</code> that makes this a data-aware component.
 */
  DBTextDataBinder getDataBinder() {
    return dataBinder;
  }

 /**
  * <p>Returns the <code>DBTextDataBinder</code> that makes this a data-aware component. </p>
  */
  protected DBTextDataBinder dataBinder;

}
