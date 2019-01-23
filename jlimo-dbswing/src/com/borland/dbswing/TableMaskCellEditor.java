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
// Copyright (c) 1996 - 2004 Borland Software Corporation.  All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dbswing;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;

import com.borland.dx.dataset.*;
import com.borland.dx.text.*;

/**
 * <p>Internal use only class.</p>
 */

public class TableMaskCellEditor extends JTextField implements TableCellEditor, DocumentListener, ActionListener, CustomPaintSite, java.io.Serializable {

/**
 * <p>Internal use only.</p>
 */
  protected EventListenerList listenerList = new EventListenerList();
//  private Object value;
/**
 * <p>Internal use only.</p>
 */
  protected VariantFormatter formatter;

/**
 * <p>Internal use only.</p>
 */
  protected boolean touched;

/**
 * <p>Internal use only.</p>
 */
  protected int columnIndex;

/**
 * <p>Internal use only.</p>
 */
  protected Variant variantValue;

/**
 * <p>Internal use only.</p>
 */
  protected transient ChangeEvent changeEvent = new ChangeEvent(this);

/**
 * <p>Internal use only.</p>
 */
  protected boolean ignoreModelChange;

/**
 * <p>Internal use only.</p>
 */
  protected int alignment;

/**
 * <p>Internal use only.</p>
 */
  protected int defaultAlignment;

/**
 * <p>Internal use only.</p>
 */
  protected Color defaultForeground;

/**
 * <p>Internal use only.</p>
 */
  protected Color defaultBackground;

/**
 * <p>Internal use only.</p>
 */
  protected Insets defaultMargins = new Insets(0, 0, 0, 0);

/**
 * <p>Internal use only.</p>
 */
  protected Font defaultFont;

/**
 * <p>Internal use only.</p>
 */
  protected ItemEditMask ems;

/**
 * <p>Internal use only.</p>
 */
  protected ItemEditMaskState state;

/**
 * <p>Internal use only.</p>
 */
  protected Variant maskVariant;

/**
 * <p>Internal use only.</p>
 */
  protected int type;

/**
 * <p>Internal use only.</p>
 */
  protected JTable table;

/**
 * <p>Internal use only.</p>
 */
  protected DBTableModel dbTableModel;

/**
 * <p>Internal use only.</p>
 */
  protected ActionEvent tabEvent;

/**
 * <p>Internal use only.</p>
 */
  protected boolean ignoreEndEdit = false;

/**
 * <p>Internal use only.</p>
 */
  protected boolean allowCellSelection = true;

/**
 * <p>Internal use only.</p>
 */
  protected boolean acceptKeyReleasedEvent;

/**
 * <p>Internal use only.</p>
 */
  public TableMaskCellEditor() {
    super();
    setMargin(defaultMargins);
    getDocument().addDocumentListener(this);
    addActionListener(this);
    variantValue = new Variant();
  }

  /**
   * <p>Internal use only.</p>
   */
  protected Document createDefaultModel() {
    return new DBPlainDocument();
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setMaxLength(int maxLength) {
    if (getDocument() instanceof DBPlainDocument) {
      ((DBPlainDocument) getDocument()).setMaxLength(maxLength);
    }
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setFormatter(VariantFormatter formatter) {
    this.formatter = formatter;
  }

  /**
   * <p>Internal use only.</p>
   */
  public Object getCellEditorValue() {
    if (ems != null) {
      return maskVariant;
    }
    else {
      return variantValue;
    }
  }

  /**
   * <p>Internal use only.</p>
   */
  public boolean isCellEditable(EventObject anEvent) {
    if (anEvent instanceof MouseEvent) {
      if (((MouseEvent) anEvent).getClickCount() > 1) {
        return true;
      }
    }
    else if (anEvent == null || anEvent instanceof KeyEvent) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          requestFocus();
          setCaretPosition(getDocument().getLength());
        }
      });
      return true;
    }
    return false;
  }

  /**
   * <p>Internal use only.</p>
   */
  public boolean shouldSelectCell(EventObject anEvent) {
    return allowCellSelection;
  }

  /**
   * <p>Internal use only.</p>
   */
  public boolean stopCellEditing() {
    if (!touched) {
      table.removeEditor();
      return true;
    }
    if (isValidValue()) {
      if (!ignoreEndEdit) {
        ignoreEndEdit = true;
        fireEditingStopped();
        ignoreEndEdit = false;
      }
      return true;
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        requestFocus();
      }
    });
    allowCellSelection = false;
    return false;
  }

  /**
   * <p>Internal use only.</p>
   */
  public void cancelCellEditing() {
    fireEditingCancelled();
  }

  /**
   * <p>Internal use only.</p>
   */
  public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected,
                                               int row, int column) {
    this.table = table;
    if (table.getModel() instanceof DBTableModel) {
      dbTableModel = (DBTableModel) table.getModel();
    }
    columnIndex = column;
    touched = false;
    acceptKeyReleasedEvent = true;
    setValue(value);

    return this;
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setValue(Object value) {
    ignoreModelChange = true;
//    this.value = value;
    if (ems != null) {
      if (value != null) {
        variantValue.setFromString(type, value.toString());
        state = ems.prepare(variantValue);
      }
      else {
        state = ems.prepare(null);
      }

      ems.move(state, KeyEvent.VK_END);
      updateDisplay();
    }
    else {
      if (value != null) {
        setText(value.toString());
      }
      else {
        setText("");   
      }
    }
    ignoreModelChange = false;
  }

  //
  // javax.swing.event.DocumentListener
  //
  /**
   * <p>Internal use only.</p>
   */
  public void insertUpdate(DocumentEvent e) {
    textModified(e);
  }

  /**
   * <p>Internal use only.</p>
   */
  public void removeUpdate(DocumentEvent e) {
    textModified(e);
  }

  /**
   * <p>Internal use only.</p>
   */
  public void changedUpdate(DocumentEvent e) {
    textModified(e);
  }

  private void textModified(DocumentEvent e) {
    if (!ignoreModelChange && !touched && dbTableModel != null) {
      try {
        // touch the field so we're guaranteed to get a postRow notification later
        dbTableModel.getDataSet().startEdit(dbTableModel.getColumn(table.convertColumnIndexToModel(columnIndex)));
      }
      catch (DataSetException ex) {
        DBExceptionHandler.handleException(dbTableModel.getDataSet(), ex);
      }
      touched = true;
    }
  }

  /**
   * <p>Internal use only.</p>
   */
  public boolean isValidValue() {
    if (ems != null) {
      if (maskVariant == null) {
        maskVariant = new Variant();
      }
      try {
        if (!ems.isComplete(state)) {
          throw new InvalidFormatException(Res._DataEntryIncomplete);     
        }
        ems.getFinalValue(state, maskVariant);
      }
      catch (Exception ex) {
        if (ex instanceof InvalidFormatException) {
          state.cursorPos = ((InvalidFormatException)ex).getErrorOffset();                // error adjusts cursor
        }
        updateSelection();                                   // and shows it to user
        try {
          ValidationException.invalidFormat(ex, null, ex.getMessage());
        }
        catch(ValidationException ex2) {
          DBExceptionHandler.handleException(dbTableModel.getDataSet(), ex2);
        }
        return false;
      }
    }
    else {
      if (formatter != null) {
        try {
          String oldPattern = null;
          String widePattern = null;
          try {
            /*
            if (formatter.getFormatObj() instanceof java.text.DateFormat) {
              oldPattern = formatter.getPattern();
              int yearMaskIndex = -1;
              if ((yearMaskIndex = DBUtilities.yearMaskPos(oldPattern)) != -1) {
                int yearMaskCount = 1;
                int patternLength = oldPattern.length();
                while ((yearMaskIndex + yearMaskCount) < patternLength && oldPattern.charAt(yearMaskIndex + yearMaskCount) == 'y') {
                  yearMaskCount++;
                }
                if (yearMaskCount < 4) {
                  widePattern = oldPattern.substring(0, yearMaskIndex) + "yyyy" +     
                    ((yearMaskIndex + yearMaskCount < patternLength) ? oldPattern.substring(yearMaskIndex + yearMaskCount) : ""); 
                }
              }
              if (widePattern != null) {
                formatter.setPattern(widePattern);
              }
            }
            */
            formatter.parse(getText(), variantValue);
          }
          catch (Exception e) {
            ValidationException.invalidFormat(e, null, e.getMessage());
          }
          finally {
            if (widePattern != null) {
              formatter.setPattern(oldPattern);
            }
          }
        }
        catch (Exception ex) {
          try {
            ValidationException.invalidFormat(ex, null, ex.getMessage());
          }
          catch (ValidationException ex2) {
            DBExceptionHandler.handleException(dbTableModel.getDataSet(), this, ex2);
          }
          return false;
        }
      }
      else {
        variantValue.setString(getText());
      }
    }
    return true;
  }

  // implements java.awt.event.ActionListener
/**
 * <p>Internal use only.</p>
 */
  public void actionPerformed(ActionEvent e) {
    if (isValidValue()) {
      fireEditingStopped();
    }
  }


  // implements javax.swing.CellEditor
/**
 * <p>Internal use only.</p>
 */
  public void addCellEditorListener(CellEditorListener l) {
    listenerList.add(CellEditorListener.class, l);
  }

  // implements javax.swing.CellEditor
/**
 * <p>Internal use only.</p>
 */
  public void removeCellEditorListener(CellEditorListener l) {
    listenerList.remove(CellEditorListener.class, l);
  }

/**
 * <p>Internal use only.</p>
 */
  protected void fireEditingStopped() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==CellEditorListener.class) {
        ((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
      }
    }
  }

/**
 * <p>Internal use only.</p>
 */
  protected void fireEditingCancelled() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==CellEditorListener.class) {
        ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
      }
    }
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setDefaultForeground(Color foreground) {
    defaultForeground = foreground;
    setForeground(foreground);
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setDefaultBackground(Color background) {
    defaultBackground = background;
    setBackground(background);
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setDefaultAlignment(int alignment) {
    defaultAlignment = alignment;
    setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(alignment, true));
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setDefaultFont(Font font) {
    defaultFont = font;
    setFont(font);
  }

  // CustomPaintSite implementation
  /**
   * <p>Internal use only.</p>
   */

  public void reset() {
    setForeground(defaultForeground);
    setBackground(defaultBackground);
    setFont(defaultFont);
    setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(defaultAlignment, true));
    setMargin(defaultMargins);
  }

  // inherited from superclass
  //    public void setForeground(Color foreground) {}

  // inherited from superclass
  //    public void setBackground(Color background) {}

  /**
   * <p>Internal use only.</p>
   */
  public void setAlignment(int alignment) {
    this.alignment = alignment;
    setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(alignment, true));
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setItemMargins(Insets margins) {
    setMargin(margins);
  }

  /**
   * <p>Internal use only.</p>
   */
  public boolean isTransparent() {
    return false;
  }

  /**
   * <p>Internal use only.</p>
   */
  public int getAlignment() {
    return alignment;
  }

  /**
   * <p>Internal use only.</p>
   */
  public Insets getItemMargins() {
    return getMargin();
  }

  /**
   * <p>Internal use only.</p>
   */
  public Component getSiteComponent() {
    return this;
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setEditMasker(ItemEditMask ems) {
    this.ems = ems;
  }

  /**
   * <p>Internal use only.</p>
   */
  public void setVariantType(int type) {
    this.type = type;
  }

/**
 * <p>Internal use only.</p>
 */
  protected void processKeyEvent(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_TAB) {
      if (!isValidValue()) {
        e.consume();
        return;
      }
      if (tabEvent == null) {
        tabEvent = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, null);
      }
      if (e.isShiftDown()) {
        table.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, ActionEvent.SHIFT_MASK)).actionPerformed(tabEvent);
      }
      else {
        table.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0)).actionPerformed(tabEvent);
      }
    }
    else if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_TYPED && !e.isControlDown()) {
      if (isValidValue()) {
        fireEditingStopped();
        e.consume();
        return;
      }
    }
    else {
      if (ems != null) {
        handleKeyEvent(e);
      }
    }
    super.processKeyEvent(e);
  }

  private void handleKeyEvent(KeyEvent e) {
    int id      = e.getID();
    int keyCode = e.getKeyCode();

    if (!acceptKeyReleasedEvent && id != KeyEvent.KEY_PRESSED && id != KeyEvent.KEY_TYPED) {
      return;
    }

    switch (keyCode) {
    case KeyEvent.VK_HOME:
    case KeyEvent.VK_END:
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_RIGHT:
      ems.move(state, keyCode);
      updateSelection();
      break;
    case KeyEvent.VK_BACK_SPACE:
      if (ems.move(state, KeyEvent.VK_LEFT)) {
        updateSelection();
        deleteSelection(false);
        updateDisplay();
      }
      break;
    case KeyEvent.VK_DELETE:
      deleteSelection(false);
      ems.move(state, KeyEvent.VK_RIGHT);
      updateDisplay();
      break;
    case KeyEvent.VK_TAB:
    case KeyEvent.VK_ENTER:
      if (id != KeyEvent.KEY_TYPED) {
        return;
      }
      break;
    case KeyEvent.VK_ESCAPE:
      cancelCellEditing();
      break;
    default:
      if ((e.isControlDown() && keyCode == KeyEvent.VK_V) ||
          (e.isShiftDown() && keyCode == KeyEvent.VK_INSERT)) {
        Clipboard clipboard = getToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        if (content != null) {
          try {
            String data = (String) (content.getTransferData(DataFlavor.stringFlavor));
            if (data.length() > 0) {
              deleteSelection(true);
            }
            for (int i = 0, end = data.length(); i < end; i++) {
              ems.insert(state, data.charAt(i));
            }
            updateDisplay();
          }
          catch (Exception ex) {
            getToolkit().beep();
          }
        }
        e.consume();
        return;
      }
      char ch = e.getKeyChar();
      if (!acceptKeyReleasedEvent && id != KeyEvent.KEY_TYPED || ch >= 0 && ch < ' ') {
        if (ch == '\b') {
          e.consume();
        }
        return;
      }
      deleteSelection(true);
      ems.insert(state, ch);
      updateDisplay();
      break;
    }
    if (acceptKeyReleasedEvent) {
      acceptKeyReleasedEvent = false;
    }
    e.consume();
  }

  private void updateSelection() {

    if (state != null) {
      select(state.cursorPos, state.cursorPos + 1);
    }
  }

  private void updateDisplay() {
    if (state != null) {
      setText(state.displayString.toString());
      updateSelection();
    }
  }

  private void deleteSelection(boolean preserveAtCursor) {
    int selStart = getSelectionStart();
    int selEnd = getSelectionEnd();
    int nChars = selEnd - selStart;
    if (nChars > 1 || !preserveAtCursor) {
      ems.delete(state, selStart, nChars);
    }
  }

/**
 * <p>Internal use only.</p>
 */
  protected void processMouseEvent(MouseEvent e) {
    if (ems != null && e.getID() == MouseEvent.MOUSE_CLICKED) {
      handleMouseClicked(e);
    }
    else {
      super.processMouseEvent(e);
    }
  }

  private void handleMouseClicked(MouseEvent e) {
    state.cursorPos = viewToModel(e.getPoint());
    ems.move(state, MouseEvent.MOUSE_CLICKED);
    updateSelection();
  }

}
