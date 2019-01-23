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
import javax.swing.text.*;

import com.borland.dx.dataset.*;

/**
 * <p>A data-aware extension of the <code>JTextPane</code>
 * component. Typically a JdbTextPane is used to edit
 * multi-line strings of  text data.  A
 * <code>JdbTextPane</code> attached to a
 * <code>DataSet</code> <code>Column</code> of type
 * <code>Object</code> (or one not attached to a
 * <code>DataSet</code> at all) allows its
 * text data to include multiple font and color styles.</p>
 *
 * <p><code>JdbTextPane</code> provides a right-click popup
 * menu for performing simple editing tasks, such as
 * cutting, copying, or pasting clipboard data.  The popup
 * menu also appears when the user presses
 * <KBD>Shift+F10</KBD> while the cursor is in the
 * <code>JdbTextPane</code>. Some of the menu's behavior
 * can be customized using the following property
 * settings.</p>
 *
 * <ul>
 * <li><code>enablePopupMenu</code> - Sets whether or not the popup menu is displayable.</li>
 * <li><code>enableClearAll</code>  - Sets whether the Clear All popup menu command appears.</li>
 * <li><code>enableUndoRedo</code>  - Sets whether the Undo and Redo menu commands appear on the popup menu.</li>
 * <li><code>enableFileLoading</code> - Sets whether the File Open menu command appears on the popup menu.</li>
 * <li><code>enableFileSaving</code> - Sets whether the File Save menu command appears on the popup menu.</li>
 * <li><code>enableColorChange</code> - Sets whether the foreground and background color setting menu commands appear on the popup menu.</li>
 * <li><code>enableFontChange</code> - Sets whether the font style setting menu commands appear on the popup menu.</li>
 * </ul>
 *
 * <p>To make a <code>JdbTextPane</code> data-aware, set
 * its <code>dataSet</code> and <code>columnName</code>
 * properties.  Note that the <code>JdbTextPane</code>'s
 * popup menu is available whether it is attached to a
 * <code>DataSet</code> or not.</p>
 *
 * <p>Data typed into a <code>JdbTextPane</code> is not
 * saved immediately to the <code>DataSet</code>.  Rather,
 * certain conditions/events automatically cause the data
 * to be put into the <code>DataSet</code>'s
 * <code>Column</code>. The following two properties affect
 * this behavior.</p>
 *
 * <ul>
 * <li><code>postOnFocusLost</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the
 * <code>JdbTextPane</code> loses focus.</li>
 * <li><code>postOnRowPosted</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the
 * current <code>DataSet</code> row is posted.</li>
 * </ul>
 *
 * <p>When a <code>JdbTextPane</code> is attached to a
 * <code>DataSet</code>, the following
 * keystrokes perform special tasks when pressed:
 *
 * <p><strong>JdbTextPane keystrokes</strong></p>
 *
 * <table cellspacing="2" cellpadding="2" border="1" frame="box">
 * <tr>
 *    <th align="LEFT">Keystroke(s)</th>
 *    <th align="LEFT">Action</th>
 * </tr>
 *
 * <tr>
 *    <td valign="top"><KBD>Ctrl+Enter</KBD></td>
 *    <td valign="top">Causes the data in the text pane to
 * be written to the <code>DataSet</code> <code>Column</code>.
 * In general, changes to the pane's text are not saved to
 * the <code>DataSet</code> <code>Column</code> until some
 * action that posts the text occurs.</td>
 * </tr>
 *
 * <tr>
 *    <td valign="top"><KBD>Esc</KBD></td>
 *    <td>Makes the data in the field change back to the
 * value in the <code>DataSet</code> <code>Column</code>.
 * Pressing <KBD>Esc</KBD> discards any Undo/Redo
 * information that may have accumulated.</td>
 * </tr>
 *
 * <tr>
 *    <td valign="top"><KBD>Ctrl+PgUp, Ctrl+PgDn</KBD></td>
 *    <td>Moves to the previous and next
 * <code>DataSet</code> row, respectively. If the
 * <code>postOnRowPosted</code> property is
 * <code>true</code>, pressing <KBD>Ctrl+PgUp</KBD> or
 * <KBD>Ctrl+PgDn</KBD> saves all the text in the
 * <code>JdbTextPane</code> to the
 *  <code>DataSet</code>.</td>
 * </tr>
 *
 * <tr>
 *    <td valign="top" ><KBD>Ctrl+Tab, Ctrl+Shift+Tab</KBD></td>
 *    <td valign="top" >Using either of these keystrokes to
 * move to another component saves the current text to the
 * <code>DataSet</code> if the <code>postOnFocusLost</code>
 * property is <code>true</code>. More generally, if the
 * <code>postOnFocusLost</code> property is
 * <code>true</code>, any action that causes the
 * <code>JdbTextPane</code> to lose focus causes the text
 * to be saved in the <code>Column</code>.</td>
 * </tr>
 * </table>
 * @see DBTextDataBinder
 */
public class JdbTextPane extends JTextPane
  implements ColumnAware, java.io.Serializable
{

 /**
  * <p>Constructs a <code>JdbTextPane</code> component by
  * calling the <code>null</code> constructor of its superclass. </p>
  */
  public JdbTextPane() {
    super();
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbTextPane</code> component by
  * calling the constructor of its superclass which takes a
  * <code>StyledDocument</code> as a parameter.</p>
  *
  * @param doc The <code>StyledDocument</code> that displays in the <code>JdbTextPane</code>.
  */
  public JdbTextPane(StyledDocument doc) {
    super(doc);
    commonInit();
  }

  /**
   * <p>Used to initialize <code>JdbTextPane</code> with
   * the same defaults, regardless of the constructor used.
   * A newly instantiated <code>JdbTextPane</code> differs
   * from a <code>JTextPane</code> in that it has a
   * non-blinking cursor.</p>
   */
  protected void commonInit() {
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
   * right-clicks the text pane or presses <KBD>Shift+F10</KBD>.
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
   * right-clicks the text pane or presses <KBD>Shift+F10</KBD>.
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
   * on the text pane. The default value is <code>true</code>.</p>
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
   * on the text pane. </p>
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
   * been added to the default popup menu.  </p>
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
   * <p>Sets whether the Load Text menu command is
   * displayed on the popup menu. This is <code>true</code> by default.</p>
   *
   * @param enableFileLoading If <code>true</code>, the Load Text menu command is displayed.
   * @see #isEnableFileLoading
   * @see #setEnablePopupMenu
   */
  public void setEnableFileLoading(boolean enableFileLoading) {
    dataBinder.setEnableFileLoading(enableFileLoading);
  }

   /**
   * <p>Returns whether the Load Text menu command is
   * displayed on the popup menu. </p>
   *
   * @return If <code>true</code>, the Load Text menu command is displayed.
   * @see #setEnableFileLoading
   * @see #isEnablePopupMenu
   */
  public boolean isEnableFileLoading() {
    return dataBinder.isEnableFileLoading();
  }

   /**
   * <p>Sets whether the Save Text menu command is
   * displayed on the popup menu. This is <code>true</code> by default.</p>
   *
   * @param enableFileSaving If <code>true</code>, the Save Text menu command is displayed.
   * @see #isEnableFileSaving
   * @see #setEnablePopupMenu
   */
  public void setEnableFileSaving(boolean enableFileSaving) {
    dataBinder.setEnableFileSaving(enableFileSaving);
  }

   /**
   * <p>Returns whether the Save Text menu command is
   * displayed on the popup menu. </p>
   *
   * @return If <code>true</code>, the Save Text menu command is displayed.
   * @see #isEnableFileSaving
   * @see #isEnablePopupMenu
   */
  public boolean isEnableFileSaving() {
    return dataBinder.isEnableFileSaving();
  }

  /**
   * <p>Sets whether the foreground and background color
   * menu selections are
   * added to the default popup menu.
   * <code>True</code> by default.</p>
   *
   * @param enableColorChange If <code>true</code>, the foreground and background color menu selections are added to the default popup menu.
   * @see #isEnableColorChange
   */
  public void setEnableColorChange(boolean enableColorChange) {
    dataBinder.setEnableColorChange(enableColorChange);
  }

  /**
   * <p>Returns whether the foreground and background color
   * menu selections are
   * added to the default popup menu.
   *
   * @return If <code>true</code>, the foreground and background color menu selections are added to the default popup menu.
   * @see #setEnableColorChange
   */
  public boolean isEnableColorChange() {
    return dataBinder.isEnableColorChange();
  }

   /**
   * <p>Sets whether the font setting menu command is
   * displayed  on the popup menu.</p>
   *
   * <p>The default value is <code>true</code>.</p>
   *
   * @param enableFontChange If <code>true</code>, the font setting menu command is displayed.
   * @see #isEnableFontChange
   * @see #setEnablePopupMenu
   */
 public void setEnableFontChange(boolean enableFontChange) {
    dataBinder.setEnableFontChange(enableFontChange);
  }

 /**
   * <p>Returns whether the font setting menu command is
   * displayed on the popup menu.</p>
   *
   * <p>The default value is <code>true</code>.</p>
   *
   * @return If <code>true</code>, the font setting menu command is displayed.
   * @see #setEnableFontChange
   * @see #isEnablePopupMenu
   */
public boolean isEnableFontChange() {
    return dataBinder.isEnableFontChange();
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
