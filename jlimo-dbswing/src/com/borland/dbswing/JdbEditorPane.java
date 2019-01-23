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

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import com.borland.dx.dataset.*;


 /**
  * <p>A data-aware extension of the
  * <code>JEditorPane</code> component. A 
  * <code>JdbEditorPane</code> is used to display or edit
  * pages of RTF or HTML formatted data. A
  * <code>JdbEditorPane</code> must be attached to a
  * <code>DataSet Column</code> of type
  * <code>Variant.OBJECT</code> (or not attached to a
  * <code>DataSet</code> at all) to allow editing and the
  * display of RTF or HTML formatted data. To facilitate
  * the use of <code>JdbEditorPane</code> as an HTML page
  * viewer, <code>JdbEditorPane</code> has its <code>editable</code>
  * property set to false by default. In addition to the
  * file loading and saving menu commands on its popup
  * menu, a <code>JdbEditorPane</code> can also load its
  * content directly from an HTML page with the Open URL
  * menu command on the popup menu or by using the
  * <code>pageURL</code> property. </p>
  * 
  * <p><code>JdbEditorPane</code> provides a right-click
  * popup menu for performing simple editing tasks, such
  * as cutting, copying, or pasting clipboard data. The
  * popup menu also appears when the user presses 
  * <kbd>Shift+F10</kbd> while the cursor is in the
  * <code>JdbEditorPane</code>. Some of the menu's
  * behavior can be customized using these property settings: </p>
  * 
  * <ul>
  * <li><code>enablePopupMenu</code> - Sets whether or not the popup menu is displayable.</li>
  * <li><code>enableClearAll</code> - Sets whether the Clear All popup menu command appears.</li>
  * <li><code>enableUndoRedo</code> - Sets whether the Undo and Redo menu commands appear on the popup menu.</li>
  * <li><code>enableFileLoading</code> - Sets whether the File Open menu command appears on the popup menu.</li>
  * <li><code>enableFileSaving</code> - Sets whether the File Save menu command appears on the popup menu.</li>
  * <li><code>enableColorChange</code> - Sets whether the foreground and background color setting menu
commands appear on the popup menu.</li>
  * <li><code>enableFontChange</code> - Sets whether the font style setting menu commands appear on the popup menu.</li>
  * <li><code>enableURLLoading</code> - Sets whether the Open URL menu command appears on the popup menu. </li>
  * </ul>
  * 
  * <p>To make a <code>JdbEditorPane</code> data-aware,
  * set its <code>dataSet</code> and
  * <code>columnName</code> properties. Note that the
  * <code>JdbEditorPane</code>'s menu is available whether
  * it is attached to a <code>DataSet</code> or not. When
  * you view HTML while <code>JdbEditorPane</code> is
  * attached to a <code>DataSet</code>, the
  * <code>enableURLAutoCache</code> property controls
  * whether clicking on a link to a HTML page
  * automatically caches the page's URL in the
  * <code>DataSet</code>.   
  * <code>enableURLAutoCache</code>'s default value is
  * <code>true</code>. </p>
  * 
  * <p>Data typed into a <code>JdbEditorPane</code> is not
  * saved immediately to the <code>DataSet</code>. Rather,
  * certain conditions/events automatically cause the data
  * to be put into the <code>DataSet</code>'s
  * <code>Column</code>. These two properties affect this
  * behavior: </p>
  * 
  * <ul>
  * <li><code>postOnFocusLost</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the <code>JdbEditorPane</code> loses focus.</li>
  * <li><code>postOnRowPosted</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the current <code>DataSet</code> row is posted. </li>
  * </ul>
  * 
  * <p>When a <code>JdbEditorPane</code> is attached to a
  * <code>DataSet</code>, the following keystrokes perform
  * special tasks when pressed: </p>
  * 
  * <p><strong><code>JdbEditorPane</code> keystrokes
  * </strong></p>
  * 
  * <table cellspacing="2" cellpadding="2" border="1">
  * <tr>
  *     <TH ALIGN="LEFT">Keystroke(s)</th>
  *     <TH ALIGN="LEFT">Action</th>
  * </tr>
  * <tr>
  *     <td valign="top"><KBD>Ctrl+Enter</KBD></td>
  *     <td valign="top">Causes the data in the editor pane to be written to the <code>DataSet</code> <code>Column</code>. In general, changes to the editor pane's text are not saved to the <code>DataSet</code> <code>Column</code> until some action that posts the text occurs.</td>
  * </tr>
  * <tr>
  *     <td valign="top"><KBD>Esc</KBD></td>
  *     <td>Makes the data in the field change back to the value in the <code>DataSet</code> <code>Column</code>. Pressing Esc discards any Undo/Redo information that may have accumulated.</td>
  * </tr>
  * <tr>
  *     <td valign="top"><KBD>Ctrl+PgUp, Ctrl+PgDn</KBD></td>
  *     <td>Moves to the previous and next <code>DataSet</code> row, respectively. If the <code>postOnRowPosted</code> property is <code>true</code>, pressing <KBD>Ctrl+PgUp</KBD> or <KBD>Ctrl+PgDn</KBD> saves all the text in the <code><code>JdbEditorPane</code></code> to the <code>DataSet</code>.</td>
  * </tr>
  * <tr>
  *     <td valign="top" ><KBD>Ctrl+Tab,  Ctrl+Shift+Tab</KBD></td>
  *     <td valign="top" >Using either of these keystrokes to move to another component saves the current text to the <code>DataSet</code> if the <code>postOnFocusLost</code> property is <code>true</code>. More generally, if the <code>postOnFocusLost</code> property is <code>true</code>, any action that causes the <code>JdbEditorField</code> to lose focus causes the text to be saved in the <code>Column</code>.</td>
  * </tr>
  * </table>
  * 
  * @see DBTextDataBinder
  */
public class JdbEditorPane extends JEditorPane
  implements ColumnAware, java.io.Serializable
{

 /**
  * <p>Constructs a <code>JdbEditorPane</code>, calling
  * the constructor of its superclass, and sets the
  * <code>editable</code> property to <code>false</code>
  * so that its contents cannot be edited.</p>
  */
  public JdbEditorPane() {
    super();
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbEditorPane</code> by calling
  * the constructor of its superclass that takes a URL as
  * a parameter.  It displays the contents of the
  * specified URL and sets the <code>editable</code>
  * property to <code>false</code> so that its contents
  * cannot be edited.</p>
  * 
  * @param initialPage The initial page that <code>JdbEditorPane</code> displays.
  * @throws IOException The exception that was thrown.
  */
  public JdbEditorPane(URL initialPage) throws IOException {
    super(initialPage);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbEditorPane</code>, by calling
  * the constructor of its superclass which takes a string
  * containing an URL specification, and sets the
  * <code>editable</code> property to <code>false</code>
  * so that its contents cannot be edited.</p>
  * 
  * @param url The URL for the page you want displayed in the <code>JdbEditorPane</code>.
  * @throws IOException The exception that was thrown.
  */
  public JdbEditorPane(String url) throws IOException {
    super(url);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbEditorPane</code> by calling
  * the constructor of its superclass that takes two
  * <code>String</code>s as parameters. It displays the
  * specified text string and sets the
  * <code>editable</code> property to <code>false</code>
  * so that its contents cannot be edited.</p>
  * 
  * @param type A string indicating the MIME content type of the specified text parameter. The content type (e.g. "text/plain") determines which <code>EditorKit</code> is used by <code>JdbEditorPane</code> to process data.
  * @param text The text to be displayed initially.
  */
  public JdbEditorPane(String type, String text) {
    super(type, text);
    commonInit();
  }

 /**
  * <p>Used to initialize <code>JdbEditorPane</code> with
  * the same defaults, regardless of the constructor
  * used.</p>
  * 
  * <p>A newly instantiated <code>JdbEditorPane</code>
  * differs from a <code>JEditorPane</code> in that it is
  * non-editable by default, has a non-blinking cursor,
  * and its cursor has the same foreground color as a
  * <code>JTextPane</code>.</p>
  * 
  */
  protected void commonInit() {
    setEditable(false);
    setCaretColor(UIManager.getColor("TextPane.caretForeground"));  
    dataBinder = new DBTextDataBinder(this);  
  }

  /**
   * <p>Sets the <code>DataSet</code> fromo which values are written and to which values are read.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
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
   * <p>Sets the column name of the
   * <code>DataSet</code> from which values are read and
   * to which values are written. Must be of type
   * <code>Object</code> to save RTF or HTML data.</p>
   * 
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }

  /**
   * <p>Returns the column name of the
   * <code>DataSet</code> from which values are read and
   * to which values are written.</p>
   * 
   * @return The column name.
   * @see #setColumnName
   * @see #getDataSet
   */
  public String getColumnName() {
    return dataBinder.getColumnName();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> 
   * to which URLs are written. Must be a column of type
   * <code>String</code>.</p>
   * 
   * @param columnName The column name.
   * @see #getColumnNameURL
   * @see #setDataSet
   */
  public void setColumnNameURL(String columnName) {
    dataBinder.setColumnNameURL(columnName);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> 
   * from which URLs are read. </p>
   * 
   * @return The column name.
   * @see #setColumnNameURL
   * @see #getDataSet
   */
  public String getColumnNameURL() {
    return dataBinder.getColumnNameURL();
  }

  /**
   * <p>Sets whether a popup menu is enabled on the
   * <code>JdbEditorPane</code>. If
   * <code>enablePopupMenu</code> is
   * <code>true</code>, a menu pops up when the user
   * right-clicks the <code>JdbEditorPane</code> or
   * presses <KBD>Shift+F10</KBD>.</p>
   * 
   * <p>This is <code>true</code> by default.</p>
   *
   * @param popupEnabled If <code>true</code>, a menu pops up when the user right-clicks the <code>JdbEditorPane</code> or presses <KBD>Shift+F10</KBD>.
   * @see #isEnablePopupMenu
   */
  public void setEnablePopupMenu(boolean popupEnabled) {
    dataBinder.setEnablePopupMenu(popupEnabled);
  }

  /**
   * <p>Returns if a popup menu is enabled on the
   * <code>JdbEditorPane</code>. </p>
   * 
   * @return If <code>true</code>, a menu pops up when the user right-clicks the <code>JdbEditorPane</code> or presses <KBD>Shift+F10</KBD>.
   * @see #setEnablePopupMenu
   */
  public boolean isEnablePopupMenu() {
    return dataBinder.isEnablePopupMenu();
  }

  /**
   * <p>Sets whether the current text is entered in the
   * <code>DataSet</code> <code>Column</code> indicated by
   * <code>columnName</code> when focus is lost on the
   * text area. The default value is
   * <code>true</code>.</p>
   * 
   * @param postOnFocusLost If <code>true</code>, the current text is entered in the <code>DataSet</code>.
   * @see #isPostOnFocusLost
   */
  public void setPostOnFocusLost(boolean postOnFocusLost) {
    dataBinder.setPostOnFocusLost(postOnFocusLost);
  }

  /**
   * <p>Returns whether the current text should be put in
   * the <code>DataSet</code>'s column when focus is lost 
   * on the text area.</p>
   *
   * @return If <code>true</code>, the current text is entered in the <code>DataSet</code>.
   * @see #setPostOnFocusLost
   */
  public boolean isPostOnFocusLost() {
    return dataBinder.isPostOnFocusLost();
  }

  /**
   * <p>Sets whether the current text should be put in the
   * <code>DataSet</code> <code>Column</code> indicated by
   * the <code>columnName</code> property when the current
   * row is posted. This occurs, for example, if the user
   * presses a row navigation key while the text component
   * has current focus. The default value is
   * <code>true</code>.</p>
   * 
   * @param postOnRowPosted If <code>true</code>, the current text should be put in the <code>DataSet</code> <code>Column</code>.
   * @see #isPostOnRowPosted
   */
  public void setPostOnRowPosted(boolean postOnRowPosted) {
    dataBinder.setPostOnRowPosted(postOnRowPosted);
  }

  /**
   * <p>Returns whether the current text should be put in the   
   * <code>DataSet</code> <code>Column</code> indicated by
   * the <code>columnName</code> property when the current
   * row is posted. This occurs, for example, if the user
   * presses a row navigation key while the text component
   * has current focus. The default value is 
   * <code>true</code>.</p>
   * @return If <code>true</code>, the current text should be put in the <code>DataSet</code> <code>Column</code>.
   * @see #setPostOnRowPosted
   */
  public boolean isPostOnRowPosted() {
    return dataBinder.isPostOnRowPosted();
  }

  /**
   * <p>Sets whether the Clear All command is displayed in
   * the popup menu. The default value is 
   * <code>true</code>.</p>
   *
   * @param enableClearAll If <code>true</code>, the Clear All menu selection is displayed.
   * @see #isEnableClearAll
   * @see #setEnablePopupMenu
   */
  public void setEnableClearAll(boolean enableClearAll) {
    dataBinder.setEnableClearAll(enableClearAll);
  }

  /**
   * <p>Returns whether the Clear All popup menu command is displayed.</p>
   *
   * @return If <code>true</code>, the Clear All menu selection is displayed.
   * @see #setEnableClearAll
   * @see #isEnablePopupMenu
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
   * <p>Sets whether the File Open popup menu command is
   * displayed on the popup menu. The default value is 
   * <code>true</code>.</p>
   * 
   * @param enableFileLoading If <code>true</code>, the File Open menu selection is displayed.
   * @see #isEnableFileLoading
   * @see #setEnablePopupMenu
   */
  public void setEnableFileLoading(boolean enableFileLoading) {
    dataBinder.setEnableFileLoading(enableFileLoading);
  }

  /**
   * <p>Returns whether the File Open popup menu command
   * is displayed on the popup menu.</p>
   * 
   * @return If <code>true</code>, the File Open menu selection is displayed.
   * @see #setEnableFileLoading
   * @see #isEnablePopupMenu
   */
  public boolean isEnableFileLoading() {
    return dataBinder.isEnableFileLoading();
  }

  /**
   * <p>Sets whether the File Save menu option is 
   * displayed on the popup menu. The default value is 
   * <code>true</code>.</p>
   *
   * @param enableFileSaving If <code>true</code>, the  Save File menu selection is displayed.
   * @see #isEnableFileSaving
   * @see #setEnablePopupMenu
   */
  public void setEnableFileSaving(boolean enableFileSaving) {
    dataBinder.setEnableFileSaving(enableFileSaving);
  }

  /**
   * <p>Returns whether the File Save menu option is 
   * displayed on the popup menu. </p>
   *
   * @return If <code>true</code>, the  Save File menu selection is displayed.
   * @see #setEnableFileSaving
   * @see #isEnablePopupMenu
   */
  public boolean isEnableFileSaving() {
    return dataBinder.isEnableFileSaving();
  }

  /**
   * <p>Sets whether the foreground and background color
   * setting menu commands are displayed on the popup
   * menu. This is <code>true</code> by default.</p>
   * 
   * <p>The document being edited must be of type RTF or
   * HTML for this option to display.</p>
   * 
   * @param enableColorChange If <code>true</code>, the foreground and background color setting menu options are displayed.
   * @see #isEnableColorChange
   * @see #setEnablePopupMenu
   */
  public void setEnableColorChange(boolean enableColorChange) {
    dataBinder.setEnableColorChange(enableColorChange);
  }

  /**
   * <p>Returns whether the foreground and background 
   * color setting menu commands are displayed on the 
   * popup menu.</p>
   * 
   * @return If <code>true</code>, the foreground and background color setting menu options are displayed.
   * @see #isEnableColorChange
   * @see #isEnablePopupMenu
   */
  public boolean isEnableColorChange() {
    return dataBinder.isEnableColorChange();
  }

  /**
   * <p>Sets whether the font setting menu command is
   * displayed  on the popup menu.</p>
   * 
   * <p>The document being edited must be of type RTF or
   * HTML for this option to display.</p>
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

  /**
   * <p>Sets whether the Open URL menu command is
   * displayed on the popup menu.</p>
   * 
   * <p>The default value is <code>true</code>.</p>
   * 
   * @param enableURLLoading If <code>true</code>, the Open URL menu command is displayed.
   * @see #isEnableURLLoading
   * @see #setEnablePopupMenu
   */
  public void setEnableURLLoading(boolean enableURLLoading) {
    dataBinder.setEnableURLLoading(enableURLLoading);
  }

  /**
   * <p>Returns whether the Open URL menu command is
   * displayed on the popup menu.</p>
   * 
   * @return If <code>true</code>, the Open URL menu command is displayed.
   * @see #setEnableURLLoading
   * @see #isEnablePopupMenu
   */

  public boolean isEnableURLLoading() {
    return dataBinder.isEnableURLLoading();
  }

  /**
   * <p>Sets whether HTML pages fetched using hyperlinks
   * are automatically inserted as new rows if they don't
   * already exist in the <code>DataSet</code> (for
   * example, if the URL  in the
   * <code>columnNameURL</code> column can't be
   * found).</p> 
   * 
   * <p>This property is ignored unless the data binder is
   * bound to a <code>JdbEditorPane</code>, the
   * <code>columnNameURL</code> property is set, and the
   * associated <code>DataSet</code> allows new rows to be
   * inserted. The default value is <code>true</code>.</p>
   * 
   * @param enableURLCache If <code>true</code>, HTML pages are automatically inserted as new rows.
   * @see #isEnableURLAutoCache
   */
  public void setEnableURLAutoCache(boolean enableURLCache) {
    dataBinder.setEnableURLAutoCache(enableURLCache);
  }

  /**
   * <p>Returns whether HTML pages fetched using hyperlinks
   * are automatically inserted as new rows if they don't
   * already exist in the <code>DataSet</code> (for
   * example, if the URL  in the
   * <code>columnNameURL</code> column can't be
   * found).</p> 
   * 
   * @return If <code>true</code>, HTML pages are automatically inserted as new rows.
   * @see #setEnableURLAutoCache
   */
  public boolean isEnableURLAutoCache() {
    return dataBinder.isEnableURLAutoCache();
  }

  /**
   * <p>Sets the <code>JdbEditorPane</code>'s URL page as
   * a <code>String</code>. <code>setPageURL()</code>
   * takes a <code>String</code> that represents a URL and
   * loads the HTML page it names into the
   *  <code>JdbEditorPane</code>.</p>
   *
   * @param urlString The <code>String</code> to set the URL page to.
   * @see #getPageURL
   */
  public void setPageURL(String urlString) {
    try {
      if (urlString != null) {
        super.setPage(urlString);
      }
    } catch (Exception e) {
      DBExceptionHandler.handleException(e);
    }
  }

  /**
   * <p>Returns the URL of the currently loaded HTML page as a <code>String</code>, or <code>null</code> if the control is empty or was not loaded by means of a URL.</p>
   *
   * @return The URL of the currently loaded HTML page as a <code>String</code>.
   * @see #setPageURL
   */
  public String getPageURL() {
    URL page;
    if ((page = super.getPage()) != null) {
      return page.toString();
    }
    return null;
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

  /** <p><code>DBTextDataBinder</code> which makes a data-aware component.</p> */
  protected DBTextDataBinder dataBinder;
}
