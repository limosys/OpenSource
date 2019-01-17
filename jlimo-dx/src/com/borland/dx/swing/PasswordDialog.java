//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/swing/PasswordDialog.java,v 7.0 2002/08/08 18:40:09 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.borland.jb.util.DiagnosticJLimo;

/**
 * PasswordDialog is used internally by DataExpress as the default
 * dialog box for prompting for a user name and password when
 * ConnectionDescriptor's 'promptPassword' property is true.
 *<P>
 * Both the user name and password fields are optional.  Characters
 * entered in the password field are replaced by '*'.  Pressing
 * [Enter] in the password field will automatically click the 'OK'
 * button if both the password and user name fields are non-blank.
 */
public class PasswordDialog extends JDialog
  implements ActionListener, KeyListener
{
  JPanel panel1 = new JPanel();
  public JTextField userNameField = new JTextField();
  JLabel userNameLabel = new JLabel();
  JLabel passwordLabel = new JLabel();
  public JPasswordField passwordField = new JPasswordField();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel buttonInnerPanel = new JPanel();
  JPanel buttonOuterPanel = new JPanel();
  GridLayout gridLayout1 = new GridLayout();

  public boolean okPressed;
  /**
   * Creates a modal PasswordDialog with a title.
   */
  public PasswordDialog(String title) {
    //! parent is null because we are called from within Database.java,
    //! and there is no way to determine a frame
    super((Frame) null, title, true);
    init();
  }

  void init() {
    try  {
      jbInit();
      pack();

      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    catch(Exception ex) {
      DiagnosticJLimo.printStackTrace(ex);
    }
  }

  void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    userNameLabel.setText(Res.bundle.getString(ResIndex.UserName));
    userNameLabel.setLabelFor(userNameField);
    userNameField.setColumns(30);
    userNameField.addKeyListener(this);
    userNameField.setNextFocusableComponent(passwordField);
    passwordLabel.setText(Res.bundle.getString(ResIndex.Password));
    passwordLabel.setLabelFor(passwordField);
    passwordField.setColumns(30);
    passwordField.addActionListener(this);
    passwordField.setNextFocusableComponent(okButton);
    okButton.setText(Res.bundle.getString(ResIndex.OKBtn));
    okButton.requestDefaultFocus();
    okButton.addActionListener(this);
    okButton.setNextFocusableComponent(cancelButton);
    cancelButton.setText(Res.bundle.getString(ResIndex.CancelBtn));
    cancelButton.addActionListener(this);
    cancelButton.setNextFocusableComponent(userNameField);
    buttonInnerPanel.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(20);
    gridLayout1.setVgap(10);
    getContentPane().add(panel1);
    panel1.add(userNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 5, 5), 0, 0));
    panel1.add(userNameField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 10), 0, 0));
    panel1.add(passwordLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 5, 5), 0, 0));
    panel1.add(passwordField, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0));
    panel1.add(buttonOuterPanel, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 10, 0), 0, 0));
    buttonOuterPanel.add(buttonInnerPanel, null);
    buttonInnerPanel.add(okButton, null);
    buttonInnerPanel.add(cancelButton, null);
  }

  //
  // java.awt.event.ActionListener interface implementation
  //
  /**
   * Implements actions for buttons on the dialog box.
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == userNameField) {
      FocusManager.getCurrentManager().focusNextComponent(userNameField);
    } else if (e.getSource() == passwordField) {
      if (userNameField.getText().length() > 0) {
        okButton.doClick();
      }
      else {
        FocusManager.getCurrentManager().focusNextComponent(passwordField);
      }
    } else if (e.getSource() == okButton || e.getSource() == cancelButton) {
      okPressed = e.getSource() == okButton;
      dispose();
    }
  }

  //
  // java.awt.event.KeyListener interface implementation
  //
  public void keyTyped(KeyEvent e) {}
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      FocusManager.getCurrentManager().focusNextComponent(userNameField);
      e.consume();
    }
  }
  public void keyReleased(KeyEvent e) {}

  public void showExDialog(String msg) {
    JOptionPane.showMessageDialog(null,
                                  msg,
                                  getTitle(),
                                  JOptionPane.ERROR_MESSAGE);
  }
}

