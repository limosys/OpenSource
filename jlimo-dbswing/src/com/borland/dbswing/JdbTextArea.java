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
 * <p>A data-aware extension of the <code>JTextArea</code> component.</p>
 *
 * <p>A <code>JdbTextArea</code> is usually used to edit multi-line
 * strings of plain text data. When the <code>lineWrap</code>
 * property is set to <code>true</code>, text is forced to wrap.
 * When the <code>wrapStyleWord</code> property is set to
 * <code>true</code>, wrapping occurs on word boundaries (white
 * space). Both properties are set to <code>true</code> by default.
 * Set <code>lineWrap</code> to <code>false</code> if you don't
 * want long lines to wrap. Set <code>wrapStyleWord</code> to
 * <code>false</code> if you want wrapping to occur on character
 * boundaries, rather than word boundaries. Note that
 * <code>lineWrap</code> must be set to <code>true</code> for
 * wrapping to occur, regardless of the <code>wrapStyleWord</code>
 * setting. </p>
 *
 * <p><code>JdbTextArea</code> provides a right-click popup menu
 * for performing simple editing tasks, such as cutting, copying,
 * or pasting clipboard data. You can also display the menu by
 * pressing <KBD>Shift+F10</KBD>. Some of the menu's behavior can
 * be customized with the following property settings.</p>
 *
 *
 * <ul>
 * <li><code>enablePopupMenu</code> - Sets whether or not the popup menu is displayable.</li>
 * <li><code>enableClearAll</code>  - Sets whether the Clear All popup menu command appears.</li>
 * <li><code>enableUndoRedo</code>  - Sets whether the Undo and Redo menu command appear on the  popup menu.</li>
 * <li><code>enableFileLoading</code> - Sets whether the File Open menu command appears on the popup menu.</li>
 * <li><code>enableFileSaving</code> - Sets whether the File Save menu command appears on the popup menu.</li>
 * </ul>
 *
 * <p>To make a <code>JdbTextArea</code> data-aware, set its
 * <code>dataSet</code> and <code>columnName</code>
 * properties. Note that the <code>JdbTextArea</code>'s menu is
 * available whether it is attached to a <code>DataSet</code> or
 * not.</p>
  *
 * <p>Data typed into a <code>JdbTextArea</code> is not saved
 * immediately to the <code>DataSet</code>. Rather, certain
 * conditions/events automatically cause the data to be put into
 * the <code>DataSet</code>'s column.  The following two properties
 * affect this bevavior.</p>
 *
 * <ul>
 * <li><code>postOnFocusLost</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the <code>JdbTextArea</code> loses focus.</li>
 * <li><code>postOnRowPosted</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the current <code>DataSet</code> row is posted.</li>
 * </ul>
 *
 * <p>When a <code>JdbTextArea</code> is attached to a
 * <code>DataSet</code>, the following keystrokes perform special
 * tasks when pressed.</p>
 *
 * <p><strong>JdbTextArea keystrokes</strong></p>
 *
 * <table cellspacing="2" cellpadding="2" border="1" frame="box">
 * <tr>
 *     <th align="LEFT">Keystroke(s)</th>
 *     <th align="LEFT">Action</th>
 * </tr>
 *
 * <tr>
 *     <td valign="top"><KBD>Ctrl+Enter</KBD></td>
 *     <td valign="top">Causes the data in the text area to be
 *  written to the <code>DataSet</code> <code>Column</code>. In
 *  general, changes to the text are not saved to the
 *  <code>DataSet</code> <code>Column</code> until some action that
 *  posts the text occurs.</td>
 * </tr>
 * <tr>
 *     <td valign="top"><KBD>Esc</KBD></td>
 *     <td>Makes the data in the field change back to the value in
 *  the <code>DataSet</code> <code>Column</code>. Pressing
 *  <KBD>Esc</KBD> discards any Undo/Redo information that may have
 *  accumulated.</td>
 * </tr>
 * <tr>
 *     <td valign="top" ><KBD>Ctrl+PgUp, Ctrl+PgDn</KBD></td>
 *     <td valign="top" >Moves to the previous and next
 *  <code>DataSet</code> row, respectively.  If the
 *  <code>postOnRowPosted</code> property is <code>true</code>,
 *  pressing the <KBD>Ctrl+PgUp</KBD> or <KBD>Ctrl+PgDn</KBD> key
 *  saves all the text in the <code>JdbTextArea</code> to the
 *  <code>DataSet</code>.</td>
 * </tr>
 * <tr>
 *     <td valign="top" ><KBD>Ctrl+Tab, Ctrl+Shift+Tab</KBD></td>
 *     <td valign="top" >Using either of these keystrokes to move
 *  to another component saves the current text to the
 *  <code>DataSet</code> if the <code>postOnFocusLost</code>
 *  property is <code>true</code>. More generally, if the
 *  <code>postOnFocusLost</code> property is <code>true</code>, any
 *  action that causes the <code>JdbTextArea</code> to lose focus
 *  causes the text to be saved in the column.</td>
 * </tr>
 * </table>
 *
 * @see DBTextDataBinder
 */

public class JdbTextArea extends JTextArea
  implements ColumnAware, java.io.Serializable
{

  /**
    * <p>Constructs a <code>JdbTextArea</code> component by calling
   * the constructor of <code>this</code> class that takes a
   * <code>Document</code>, a <code>String</code>, and two
   * <code>int</code> values as parameters. Passes default values
   * of <code>null</code>, <code>null</code>, 0, and 0 to the other
   * constructor.</p>
   */
  public JdbTextArea() {
    this(null, null, 0, 0);
  }

  /**
   * <p>Constructs a <code>JdbTextArea</code> component by calling
   * the constructor of <code>this</code> class that takes a
   * <code>Document</code>, a <code>String</code>, and two
   * <code>int</code> values as parameters. Passes the specified
   * text string, along with default values of <code>null</code>,
   * 0, and 0, to the other constructor.</p>
   *
   * @param text The text you want the text area to initially display.
   */
  public JdbTextArea(String text) {
    this(null, text, 0, 0);
  }

  /**
   * <p>Constructs a <code>JdbTextArea</code> component by calling
   * the constructor of <code>this</code> class that takes a
   * <code>Document</code>, a <code>String</code>, and two
   * <code>int</code> values as parameters. Passes the specified
   * <code>int</code> values for number of rows and columns, along
   * with default <code>null</code> values for the
   * <code>Document</code> and <code>String</code>, to the other
   * constructor.</p>
   *
   * <p>Note that the rows and column parameters are not related to
   * the rows and columns of a <code>DataSet</code>, and are only a
   * request for the specified height and width. The layout
   * determines how big the <code>JdbTextArea</code> actually
   * is.</p>
   *
   * @param rows The number of rows of text you want the text area to be able to display.
   * @param columns The number of text character columns you want the text area to be able to display.
   */
  public JdbTextArea(int rows, int columns) {
    this(null, null, rows, columns);
  }

  /**
   * <p>Constructs a <code>JdbTextArea</code> component by calling
   * the constructor of <code>this</code> class that takes a
   * <code>Document</code>, a <code>String</code>, and two
   * <code>int</code> values as parameters. Passes the specified
   * text string, specified number of rows and columns, and a
   * default value of <code>null</code> for the
   * <code>Document</code> to the other constructor.</p>
   *
   * <p>Note that the rows and column parameters are not related to
   * the rows and columns of a <code>DataSet</code>, and are only a
   * request for the specified height and width. The layout
   * determines how big the <code>JdbTextArea</code> actually
   * is.</p>
   *
   * @param text The text you want the text area to initially display.
   * @param rows The number of rows of text you want the text area to be able to display.
   * @param columns The number of text character columns you want the text area to be able to display.
   */
  public JdbTextArea(String text, int rows, int columns) {
    this(null, text, rows, columns);
  }



  /**
   * <p>Constructs a <code>JdbTextArea</code> component by calling
   * the constructor of <code>this</code> class that takes a
   * <code>Document</code>, a <code>String</code>, and two
   * <code>int</code> values as parameters. Passes the specified
   * <code>Document</code>, along with default values of null, 0,
   * and 0, to the other constructor.</p>
   *
   * @param doc The text storage model.
   */
  public JdbTextArea(Document doc) {
    this(doc, null, 0, 0);
  }


 /**
  * <p>Constructs a <code>JdbTextArea</code> component by calling
  * the constructor of its superclass that takes a
  * <code>Document</code>, the text string to initially display,
  * and the number of rows and columns as parameters. This
  * constructor is called by all of the other
  * <code>JdbTextArea</code> constructors, and is the only one of
  * them that calls a constructor of its superclass directly.</p>
  *
  * <p>Note that the rows and column parameters are not related to
  * the rows and columns of a <code>DataSet</code>, and are only a
  * request for the specified height and width. The layout
  * determines how big the <code>JdbTextArea</code> actually
  * is.</p>
  *
  * @param doc The text storage model.
  * @param text The text you want the text area to initially display.
   * @param rows The number of rows of text you want the text area to be able to display.
   * @param columns The number of text character columns you want the text area to be able to display.
   */
  public JdbTextArea(Document doc, String text, int rows, int columns) {
    super(doc, text, rows, columns);
    commonInit();
  }

  /**
   * <p>Used to initialize <code>JdbTextArea</code> with the same
   * defaults, regardless of the constructor used.</p>
   *
   * <p>A newly instantiated <code>JdbTextArea</code> differs from
   * a <code>JTextArea</code> in that it has a non-blinking cursor,
   * slightly larger left and right margins, and has the
   * <code>lineWrap</code> and <code>wrapStyleWord</code>
   * properties set to <code>true</code> by default.</p>
   */
  protected void commonInit() {
    Insets margin = getMargin();
    setMargin(new Insets(margin.top, margin.left + 2, margin.bottom, margin.right + 2));
    setLineWrap(true);
    setWrapStyleWord(true);
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
   * right-clicks the text area or presses <KBD>Shift+F10</KBD>.
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
   * right-clicks the text area or presses <KBD>Shift+F10</KBD>.
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
   * on the text area. The default value is <code>true</code>.</p>
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
   * on the text area. </p>
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
