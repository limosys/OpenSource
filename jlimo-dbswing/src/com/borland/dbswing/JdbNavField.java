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
import javax.swing.text.*;

import com.borland.dx.dataset.*;

/**
 * <p>A <code>JTextField</code> with built-in row locating
 * functionality when its <code>DataSet</code> property is
 * set. If its <code>columnName</code> property is set, it
 * locates data in that column only. If the
 * <code>columnName</code> property is not set, it locates
 * data in the <code>DataSet</code> column that had focus
 * last in a <code>JdbTable</code>. If no column had focus
 * in a <code>JdbTable</code>, the first column in the
 * <code>DataSet</code> that supports locate operations is
 * chosen. Unlike <code>JdbTextField</code>,
 * <code>JdbNavField</code> never writes to a
 * <code>DataSet</code> column.</p>
 *
 * <p>If the column searched is of type <code>String</code>, the search occurs incrementally as
 * characters are typed. If the search string is all
 * lowercase, then the search is case insensitive.  If the
 * search string is mixed case, then the search is case
 * sensitive.</p>
 *
 * <p>If the column searched is not of type <code>String</code>, the search doesn't occur until the Enter key is pressed.</p>
 *
 * <p>To search for prior and next matches, use the up and
 * down arrow keys, respectively. If a
 * <code>JdbStatusLabel</code> is present, it displays
 * current usage information about
 * the <code>JdbNavField</code>, such as whether or not a
 * matching column value was found.</p>
 *
 * @see JdbTextField
 */

public class JdbNavField extends JTextField
  implements ColumnAware, FocusListener
{


 /**
  * <p>Constructs a <code>JdbNavField</code>, calling the <code>null</code> constructor of its superclass.</p>
  */
  public JdbNavField() {
    super();
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavField</code> that initially displays the specified text, calling the constructor of its superclass that takes a <code>String</code>.</p>
  * @param text The text that initially displays in the field.
  */
  public JdbNavField(String text) {
    super(text);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavField</code> that displays
  * the specified number of character columns, calling the
  * constructor of its superclass that takes an
  * <code>int</code>.</p>
  *
  * <p>Note that the <code>columns</code> parameter is not
  * related to the rows and columns of a
  * <code>DataSet</code>, and is only a request for the
  * desired width of the control. Depending on the font
  * you use, you may see many more characters in the
  * control than the value of <code>columns</code> leads
  * you to expect. </p>
  *
  * <p>Not all layouts will respect this requested width.
  * <code>FlowLayout</code> will, for example, but
  * <code>BorderLayout</code> won't. <code>XYLayout</code>
  * will as long as you don't resize the control.</p>
  *
  * @param columns The number of visible character columns the field can display; independent of the width of the field.
  */
  public JdbNavField(int columns) {
    super(columns);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavField</code> of the specified width that initially displays the specified text, calling the constructor of its superclass that takes a <code>String</code> and an <code>int</code>.</p>
  *
  * <p>Note that the <code>columns</code> parameter is not related to the rows and columns of a <code>DataSet</code>, and is only a request for the desired width of the control. Depending on the font you use, you may see many more characters in the control than the value of <code>columns</code> leads you to expect. </p>
  *
  * <p>Not all layouts will respect this requested width. <code>FlowLayout</code> will, for example, but <code>BorderLayout</code> won't. <code>XYLayout</code> will as long as you don't resize the control.</p>
  *
  * @param text The text that initially displays in the field.
  * @param columns The number of visible character columns the field can display; independent of the width of the field.
  */
  public JdbNavField(String text, int columns) {
    super(text, columns);
    commonInit();
  }

/**
  * <p>Constructs a <code>JdbNavField</code> of the
  * specified width that uses a storage text model and
  * that initially displays the specified text, calling
  * the constructor of its superclass that takes a
  * <code>Document</code>, a <code>String</code>, and an
  * <code>int</code>.</p>
  *
  * <P>Note that the <code>columns</code> parameter is not
  * related to the rows and columns of a
  * <code>DataSet</code>, and is only a request for the
  * desired width of the control. Depending on the font
  * you use, you may see many more characters in the
  * control than the value of <code>columns</code> leads
  * you to expect. </p>
  *
  * <P>Not all layouts will respect this requested width.
  * <code>FlowLayout</code> will, for example, but
  * <code>BorderLayout</code> won't. <code>XYLayout</code>
  * will as long as you don't resize the control.</p>
  *
  * @param doc The storage text model.
  * @param text The text that initially displays in the field.
  * @param columns The number of visible character columns the field can display; independent of the width of the field.
  */
  public JdbNavField(Document doc, String text, int columns) {
    super(doc, text, columns);
    commonInit();
  }

/**
  * <p>Calls the <code>addNotify()</code> method of the superclass and opens the <code>DataSet</code>.
  */
  public void addNotify() {
    super.addNotify();
    openDataSet();
  }


  /**
   * <p>Used to initialize <code>JdbNavField</code> with
   * the same defaults, regardless of the constructor
   * used. A newly instantiated <code>JdbNavField</code>
   * differs from a <code>JTextField</code> in that it has
   * a non-blinking cursor, and slightly larger left and
   * right margins.</p>
   */
  protected void commonInit() {
    getCaret().setBlinkRate(0);
    Insets margin = getMargin();
    setMargin(new Insets(margin.top, margin.left + 2, margin.bottom, margin.right + 2));
    mapNavFieldActions();
    enableEvents(AWTEvent.INPUT_METHOD_EVENT_MASK);
  }

  //
  // JdbNavField properties
  //

  /**
   * <p>Sets the <code>DataSet</code> in which the navigation occurs.</p>
   * @param dataSet The <code>DataSet</code> in which the navigation occurs.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    if (this.dataSet != null) {
      removeFocusListener(this);
    }
    this.dataSet = dataSet;
    if (dataSet != null) {
      addFocusListener(this);
    }
  }

  /**
   * <p>Returns the <code>DataSet</code> in which the navigation occurs.</p>
   * @return The <code>DataSet</code> in which the navigation occurs.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * <p>Sets the name of the <code>Column</code> of the <code>DataSet</code> in which the navigation occurs.</p>
   *
   * @param columnName The <code>Column</code> in which the navigation occurs.
   * @see #getColumnName
   */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  /**
   * <p>Returns the name of the <code>Column</code> of the <code>DataSet</code> in which the navigation occurs.</p>
   * @return The <code>Column</code> in which the navigation occurs.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * <p>Returns a new <code>JdbNavFieldDocument</code>,
   * which is an inner class, defined in
   * <code>JdbNavField</code>, that extends
   * <code>javax.swing.text.PlainDocument</code>.</p>
   */
  protected Document createDefaultModel() {
    return new JdbNavFieldDocument();
  }


  /**
   * <p>Returns an array of <code>Action</code> objects.
   * If you want to extend <code>JdbNavField</code>, call
   * <code>getActions()</code> to get the array of
   * <code>Action</code> objects for
   * <code>JdbNavField</code> and combine it with your own
   * array of <code>Actions</code> that specify the new
   * actions you want to give your extended
   * <code>JdbNavField</code>.</p>
   */
  public Action [] getActions() {
    return TextAction.augmentList(super.getActions(), navFieldActions);
  }

  private void mapNavFieldActions() {
    Keymap parentKeymap = getKeymap();
    Keymap navFieldKeymap = JTextComponent.addKeymap("JdbNavField Keymap", parentKeymap);  
    JTextComponent.loadKeymap(navFieldKeymap, keyBindings, getActions());
    setKeymap(navFieldKeymap);
  }

  /**
   * <p>Sets whether the search is case sensitive.</p>
   *
   * @param caseSensitive If <code>true</code>, the search is case sensitive.
   * @see #isCaseSensitive
   */
  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  /**
   * <p>Returns whether the search is case sensitive.</p>
   *
   * @return caseSensitive If <code>true</code>, the search is case sensitive.
   * @see #setCaseSensitive
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  private void openDataSet() {
    try {
      if (dataSet != null && !dataSet.isOpen()) {
        dataSet.open();
      }
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, this, e);
    }
  }

  private void locateNext() {
    locateOptions = Locate.NEXT;
    locateFirst = false;
    doInteractiveLocate();
  }

  private void locatePrev() {
    locateOptions = Locate.PRIOR;
    locateFirst = false;
    doInteractiveLocate();
  }

  private void locateEnter() {
    enterPressed = true;
    doInteractiveLocate();
    enterPressed = false;
  }

  private void doInteractiveLocate() {

    Document model = getDocument();
    if (model instanceof JdbNavFieldDocument) {
      String searchText = null;
      if (getSelectionEnd() == getDocument().getLength() &&
          getSelectionStart() < getSelectionEnd()) {
        try {
          searchText = getText(0, getSelectionStart());
        }
        catch (BadLocationException e) {
          DBExceptionHandler.handleException(dataSet, this, e);
        }
      }
      else {
        searchText = getText();
      }
//      ((JdbNavFieldDocument) model).doInteractiveLocate(searchText, false);
      setText(searchText);
    }

  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(this, dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  class JdbNavFieldDocument extends PlainDocument {
    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {

      if (str == null || dataSet == null) {
        return;
      }

      super.insertString(offs, str, a);
      if((a != null) && (a.getAttribute(StyleConstants.ComposedTextAttribute) != null)) {
        return ;
      }

      if (locateFirst) {
        locateOptions = Locate.FIRST;
      }
      locateFirst = true;

//      final String searchText = getText(0, getLength());
      String searchText = getText(0, getLength());
      if (!caseSensitive) {
        if (searchText.toLowerCase().equals(searchText)) {
          locateOptions |= Locate.CASE_INSENSITIVE;
        }
      }

      try {
        dataSet.interactiveLocate(searchText,
                                  columnName,
                                  locateOptions,
                                  enterPressed);
        Column column;
        String currentColumnName = columnName == null ? dataSet.getLastColumnVisited() : columnName;
        if ((column = dataSet.hasColumn(currentColumnName)) != null) {
          if (column.getDataType() == Variant.STRING) {
            String value = dataSet.getString(currentColumnName);
            if (value.regionMatches((locateOptions & Locate.CASE_INSENSITIVE) == Locate.CASE_INSENSITIVE ? true : false,
                                    0, searchText, 0, searchText.length())) {
              super.insertString(getLength(), value.substring(getLength()), null);

              justLocated = true ;
              selectStart = searchText.length() ;
              selectEnd = getLength() ;
              setCaretPosition(selectStart);
              moveCaretPosition(selectEnd);

            }
          }
        }
      }
      catch (Exception e) {
        DBExceptionHandler.handleException(dataSet, e);
      }
    }

  }

  protected void processInputMethodEvent(InputMethodEvent e) {
    super.processInputMethodEvent(e);
    if(justLocated) {
      justLocated = false ;
      setCaretPosition(selectStart);
      moveCaretPosition(selectEnd);
    }
  }

  // actions added by JdbNavField

 /**
  * <p>Name of action which invokes Locate for the subsequent row containing <code>JdbNavField</code>'s current text.</p>
  */
  public static final String locateNextAction = "locate-next";  

 /**
  * <p>Name of action which invokes Locate for the previous row containing <code>JdbNavField</code>'s current text.</p>
  */
  public static final String locatePreviousAction = "locate-previous";  

 /**
  * <p>Name of action which invokes Locate on  <code>JdbNavField</code>'s current text.</p>
  */
  public static final String locateEnterAction = "locate-enter";   

  private static final JTextComponent.KeyBinding [] keyBindings = {
    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), locateNextAction),
    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), locatePreviousAction),
    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), locateEnterAction),
  };

  private static final Action [] navFieldActions = {
    new LocateNextAction(),
    new LocatePreviousAction(),
    new LocateEnterAction(),
  };

  // action implementation
  static class LocateNextAction extends TextAction {
    LocateNextAction() {
      super(locateNextAction);
    }

    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target instanceof JdbNavField) {
        ((JdbNavField) target).locateNext();
      }
    }
  }

  static class LocatePreviousAction extends TextAction {
    LocatePreviousAction() {
      super(locatePreviousAction);
    }

    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target instanceof JdbNavField) {
        ((JdbNavField) target).locatePrev();
      }
    }
  }

  static class LocateEnterAction extends TextAction {
    LocateEnterAction() {
      super(locateEnterAction);
    }

    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target instanceof JdbNavField) {
        ((JdbNavField) target).locateEnter();
      }
    }
  }

  private DataSet dataSet;
  private String columnName;
  private boolean caseSensitive;

  private boolean locateFirst = true;
  private boolean justLocated = false;
  private int selectStart ;
  private int selectEnd ;

  private int locateOptions;
  private boolean enterPressed;

}
