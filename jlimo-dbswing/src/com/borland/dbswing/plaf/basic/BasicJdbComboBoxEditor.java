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
package com.borland.dbswing.plaf.basic;

import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

public class BasicJdbComboBoxEditor implements ComboBoxEditor,FocusListener,Serializable {
  protected ComboBoxTextField editor;
  protected JComboBox comboBox;

  public BasicJdbComboBoxEditor(JComboBox comboBox) {
    this(comboBox, null);
  }

  public BasicJdbComboBoxEditor(JComboBox comboBox, Border border) {
    this.comboBox = comboBox;
    editor = new ComboBoxTextField("", 9, comboBox);
    editor.addFocusListener(this);
    if (border != null) {
      editor.setBorder(border);
    }
  }

  public Component getEditorComponent() {
    return editor;
  }

  public void setItem(Object anObject) {
    if ( anObject != null )
      editor.setText(anObject.toString());
    else
      editor.setText("");   
  }

  public Object getItem() {
    return editor.getText();
  }

  public int getIndex() {
    return editor.getIndex();
  }

  public void selectAll() {
    editor.selectAll();
    editor.requestFocus();
  }

  public void focusGained(FocusEvent e) {}
  public void focusLost(FocusEvent e) {
    if ( !e.isTemporary() ) {
      editor.postActionEvent();
    }
  }

  public void addActionListener(ActionListener l) {
    editor.addActionListener(l);
  }

  public void removeActionListener(ActionListener l) {
    editor.removeActionListener(l);
  }

  class ComboBoxTextField extends JTextField {
    private BasicComboBoxDocument document;

    public ComboBoxTextField(String value,int n, JComboBox comboBox) {
      super(value,n);
      super.setBorder(null);
      setDocument(document = new BasicComboBoxDocument(comboBox, this));
    }

    public int getIndex() {
      return document.getIndex();
    }

//    public void setBorder(Border b) {}
  }

  class BasicComboBoxDocument extends PlainDocument implements PropertyChangeListener {
    protected JComboBox comboBox;
    protected ComboBoxModel comboBoxModel;
    protected int lastIndex = 0;
    protected JTextField textField;

    public BasicComboBoxDocument(JComboBox comboBox, JTextField textField) {
      this.comboBox = comboBox;
      comboBox.addPropertyChangeListener(this);
      this.textField = textField;
      comboBoxModel = comboBox.getModel();
    }

    public void propertyChange(PropertyChangeEvent e) {
      if (e.getPropertyName().equals("model")) { 
        comboBoxModel = (ComboBoxModel) e.getNewValue();
      }
    }

    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {


      if (str == null) {
        return;
      }

      super.insertString(offs, str, a);

      String searchText = getText(0, getLength());

      int size = comboBoxModel.getSize();
      String value;
      for (int i = 0; i < size; i++) {
        value = comboBoxModel.getElementAt(i).toString();
        if (value.toUpperCase().startsWith(searchText.toUpperCase())) {
          remove(0, getLength());
          super.insertString(0, value, null);
          textField.setCaretPosition(searchText.length());
          textField.moveCaretPosition(getLength());
          lastIndex = i;
          return;
        }
      }
    }

    public int getIndex() {
      return lastIndex;
    }
  }
}

