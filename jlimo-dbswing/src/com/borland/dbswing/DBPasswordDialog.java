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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

 /**
  * <p>A simple dialog box that prompts a user for a user name and password. <code>DBPasswordDialog</code> is used by <code>DBPasswordPrompter</code> as its dialog box. Useful features of <code>DBPasswordDialog</code> include: </p>
  *
  * <ul>
  * <li>Characters entered in the password field are replaced by *. </li>
  * <li>The OK button can optionally be disabled until both the user name and password are entered. </li>
  * <li>The default button shifts between the OK and Cancel buttons automatically. </li>
  * <li>There is built-in mnemonic key support. </li>
  * <li>If a user name is provided initially, the password field automatically becomes the first focused     field. </li>
  * <li>The strings of the dialog box are resourced, providing international support. </li>
  * </ul>
  *
  *<p><code>DBPasswordDialog</code> automatically disposes itself if the user closes the window or presses either the OK or Cancel button. Call the <code>isOKPressed()</code> method to determine whether or not the user pressed the OK button to close the dialog. </p>
 *
 * <p>Set the <code>autoDispose</code> property to <code>false</code> to prevent <code>DBPasswordDialog</code> from automatically disposing itself. By default, no actions are attached to the OK or Cancel button. To attach an action to a button, add an <code>ActionListener</code> directly to either the OK or Cancel button via the <code>getOkButton()</code> and <code>getCancelButton()</code> methods. Remember to call the <code>dispose()</code> method to free
<code>DBPasswordDialog's</code> resources if you disable <code>autoDispose.</code> </p>
 */


public class DBPasswordDialog extends JDialog
  implements ActionListener, FocusListener, KeyListener
{
  JPanel panel1 = new JPanel();
  JTextField userNameField = new JTextField();
  JdbLabel userNameLabel = new JdbLabel();
  JdbLabel passwordLabel = new JdbLabel();
  JPasswordField passwordField = new JPasswordField();
  RepeatButton okButton = new RepeatButton();
  RepeatButton cancelButton = new RepeatButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel buttonPanel = new JPanel();
//  JPanel buttonOuterPanel = new JPanel();

  private boolean firstFocus = true;
  private boolean okPressed = false;
  private boolean autoDispose = true;

  private boolean userNameRequired = true;
  private boolean passwordRequired = true;

 /**
  * <p>Creates a modal <code>DBPasswordDialog</code> with the specified title. The frame parameter is not currently used, but is reserved for future use. Calls the constructor of its superclass which takes a <code>Frame,</code> a <code>String,</code> and a <code>boolean,</code> and passes a <code>null</code> cast to a <code>Frame,</code> the specified <code>String,</code> and <code>true</code>. </p>
 *
 * @param frame Reserved for future use.
 * @param title The text string that is displayed on the dialog box title bar.
*/
  public DBPasswordDialog(Frame frame, String title) {
    super((Frame) null, title, true);
    init();
  }

  /**
   *  <p>Creates a modal <code>DBPasswordDialog</code> with a specified frame amd a string that appears
   * as the title of the dialog box. </p>
   *
   * @param title The text string that is displayed on the dialog box title bar.
   * @param frame The specified <code>frame</code>.
   */

  public DBPasswordDialog(String title, Frame frame) {
    super(frame, title, true);
    init();
  }

  /**
   * <p>Creates a modal <code>DBPasswordDialog</code> using the a specified dialog and title. Calls the constructor of its superclass which takes a <code>Dialog,</code> a <code>String,</code> and a <code>boolean</code> as parameters. Passes the specified
<code>Dialog</code> and <code>String,</code> along with a default <code>boolean</code> value of <code>true</code> to the superclass constructor.</p>
  *
  * @param dialog The parent <code>Dialog.</code>
  * @param title The text string that is displayed on the dialog box title bar.
*/
  public DBPasswordDialog(Dialog dialog, String title) {
    super(dialog, title, true);
    init();
  }

  /**
   *  <p>Creates a modal <code>DBPasswordDialog</code> with a title. Calls the constructor of this class that takes a <code>Frame</code> and a <code>String</code> as parameters. Passes a <code>null</code> cast to a <code>Frame,</code> and the specified <code>String,</code> to the other constructor.  </p>
  *
  * @param title The text string that is displayed on the dialog box title box.
*/
  public DBPasswordDialog(String title) {
    this((Frame) null, title);
  }

  /**
   *  <p>Creates a modal <code>DBPasswordDialog</code> with no title. Calls the constructor of its superclass which takes a <code>Frame,</code> a <code>String,</code> and a <code>boolean</code> as parameters. Passes default values of a <code>null</code> cast to a <code>Frame,</code> a <code>null String,</code> and <code>true</code> to the superclass constructor.  </p>
   */

  public DBPasswordDialog() {
    super((Frame) null, null, true);
    init();
  }

  void init() {
    try  {
      jbInit();
      pack();

      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

      userNameField.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
          updateButtons();
        }
        public void insertUpdate(DocumentEvent e) {
          updateButtons();
        }
        public void removeUpdate(DocumentEvent e) {
          updateButtons();
        }
      });

      passwordField.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
          updateButtons();
        }
        public void insertUpdate(DocumentEvent e) {
          updateButtons();
        }
        public void removeUpdate(DocumentEvent e) {
          updateButtons();
        }
      });
    }
    catch(Exception ex) {
      DBExceptionHandler.handleException(ex);
    }
  }

  void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    userNameLabel.setTextWithMnemonic(Res._UserName);     
    userNameLabel.setLabelFor(userNameField);
    userNameField.setColumns(30);
    userNameField.addFocusListener(this);
    userNameField.addKeyListener(this);
    userNameField.requestDefaultFocus();
    passwordLabel.setTextWithMnemonic(Res._Password);     
    passwordLabel.setLabelFor(passwordField);
    passwordField.setColumns(30);
    passwordField.addActionListener(this);
    okButton.setEnabled(false);
    okButton.setRepeat(false);
    okButton.setTextWithMnemonic(Res._OKBtn);     
    okButton.addActionListener(this);
    cancelButton.setTextWithMnemonic(Res._CancelBtn);     
    cancelButton.setRepeat(false);
    cancelButton.setDefaultButton(true);
    cancelButton.addActionListener(this);
    buttonPanel.setLayout(new GridBagLayout());
//    buttonInnerPanel.setLayout(gridLayout1);
//    gridLayout1.setColumns(2);
//    gridLayout1.setHgap(20);
//    gridLayout1.setVgap(10);
    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(panel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 8, 8, 8), 0, 0));
    panel1.add(userNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    panel1.add(userNameField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 0), 0, 0));
    panel1.add(passwordLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 0, 0, 0), 0, 0));
    panel1.add(passwordField, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 4, 0, 0), 0, 0));
    panel1.add(buttonPanel, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 0, 0, 0), 0, 0));

    buttonPanel.add(new JPanel(), new GridBagConstraints(
        0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    GridBagConstraints firstButton = new GridBagConstraints(
        1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    GridBagConstraints secondButton = new GridBagConstraints(
        2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 8, 0, 0), 0, 0);
    buttonPanel.add(okButton, Platform.isMacLAF() ? secondButton : firstButton);
    buttonPanel.add(cancelButton, Platform.isMacLAF() ? firstButton : secondButton);
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton.doClick();
      }
    };
    getRootPane().registerKeyboardAction(actionListener,
                                         KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                         JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  //
  // java.awt.event.ActionListener interface implementation
  //
  /**
   * <p>Implements actions for buttons on the dialog box.</p>
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == userNameField || e.getSource() == passwordField) {
      if (okButton.isEnabled()) {
        okButton.doClick();
      }
      else {
        cancelButton.doClick();
      }
    }
    else if (e.getSource() == okButton || e.getSource() == cancelButton) {
      okPressed = e.getSource() == okButton;
      if (autoDispose) {
        dispose();
      }
    }
  }

  //
  // java.awt.event.KeyListener interface implementation
  //
  public void keyTyped(KeyEvent e) {}
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER && passwordRequired) {
      javax.swing.FocusManager.getCurrentManager().focusNextComponent(userNameField);
      e.consume();
    }
  }
  public void keyReleased(KeyEvent e) {}

  //
  // java.awt.event.FocusListener interface implementation
  //

  /**
   * <p>Used to move the initial focus to the password entry field if
   * the  <code>userName</code> entry field is already filled.</p>
   */
  public void focusGained(FocusEvent e) {
    if (firstFocus && userNameField.getText().length() > 0) {
      firstFocus = false;
      passwordField.requestFocus();
    }
  }

  /**
   * <p>Present as a side-effect of the FocusListener implementation.
   * Does nothing.</p>
   */
  public void focusLost(FocusEvent e) {
  }

  void updateButtons() {
    boolean okState = (!userNameRequired || userNameField.getText().length() > 0) &&
                      (!passwordRequired || passwordField.getPassword().length > 0);
    if (userNameRequired || passwordRequired) {
      if (okState != okButton.isEnabled()) {
        okButton.setEnabled(okState);
      }
    }
    else {
      okButton.setEnabled(true);
    }
    if (okState) {
      cancelButton.setDefaultButton(false);
      okButton.setDefaultButton(true);
    }
    else {
      okButton.setDefaultButton(false);
      cancelButton.setDefaultButton(true);
    }
  }



  /**
   * <p>Sets the user name.</p>
   *
   * @param userName The user name.
   * @see #getUserName
   * @see #setUserNameRequired
   */

  public void setUserName(String userName) {
    userNameField.setText(userName);
    updateButtons();
  }

  /**
   * <p>Returns the user name. </p>
   *
   * @return The user name.
   * @see #setUserName
   * @see #isUserNameRequired
   */
  public String getUserName() {
    return userNameField.getText();
  }

  /**
   * <p>Sets the password.</p>
   *
   * @param password The password.
   * @see #getPassword
   * @see #setPasswordRequired
   */

  public void setPassword(String password) {
    passwordField.setText(password);
    updateButtons();
  }

  /**
   * <p>Returns the password.</p>
   *
   * @return The password.
   * @see #setPassword
   * @see #isPasswordRequired
   */
  public String getPassword() {
    return new String(passwordField.getPassword());
  }

  /**
   * <p>Sets whether the dialog box should automatically
   * dispose itself when the user closes its window or
   * presses the OK or Cancel button.</p>
   *
   * @param autoDispose If <code>true</code>, the dialog box will automatically dispose itself.
   * @see #isAutoDispose
   */
  public void setAutoDispose(boolean autoDispose) {
    this.autoDispose = autoDispose;
  }

  /**
   * <p>Returns whether the dialog box should automatically
   * dispose itself when the user closes its window or
   * presses the OK or Cancel button.</p>
   *
   * @return If <code>true</code>, the dialog box will automatically dispose itself.
   * @see #setAutoDispose
   */
  public boolean isAutoDispose() {
    return autoDispose;
  }

  /**
   * <p>Sets whether or not a user name must be entered
   * to close the dialog with the OK button.</p>
   *
   * @param userNameRequired If <code>true</code>, a user name must be entered in order
   * to close the dialog with the OK button.
   * @see #isUserNameRequired
   * @see #getUserName
   */
  public void setUserNameRequired(boolean userNameRequired) {
    this.userNameRequired = userNameRequired;
    updateButtons();
  }

  /**
   * <p>Returns whether or not a user name must be entered
   * to close the dialog with the OK button.</p>
   *
   * @return If <code>true</code>, a user name must be entered in order
   * to close the dialog with the OK button.
   * @see #setUserNameRequired
   * @see #getUserName
   */
  public boolean isUserNameRequired() {
    return userNameRequired;
  }

  /**
   * <p>Sets whether or not a password must be entered
   * to close the dialog with the OK button.</p>
   *
   * @param passwordRequired If <code>true</code>, a password must be entered in order
   * to close the dialog with the OK button.
   * @see #isPasswordRequired
   * @see #setPassword
   */
  public void setPasswordRequired(boolean passwordRequired) {
    this.passwordRequired = passwordRequired;
    updateButtons();
  }

  /**
   * <p>Returns whether or not a password must be entered
   * to close the dialog with the OK button.</p>
   *
   * @return If <code>true</code>, a password must be entered in order
   * to close the dialog with the OK button.
   * @see #setPasswordRequired
   * @see #getPassword
   */
  public boolean isPasswordRequired() {
    return passwordRequired;
  }

  /**
   * <p>Returns the OK button used by the dialog box.</p>
   *
   * @return The OK button.
   * @see #getCancelButton
   */
  public JButton getOkButton() {
    return okButton;
  }

  /**
   * <p>Returns the Cancel button used by the dialog box.</p>
   *
   * @return The Cancel button.
   * @see #getOkButton
   */
  public JButton getCancelButton() {
    return cancelButton;
  }

  /**
   * <p>Returns <code>true</code> if the user pressed the OK button to close
   * the dialog box.</p>
   *
   * @return If <code>true</code>, the user pressed the OK button.
   */
  public boolean isOKPressed() {
    return okPressed;
  }

}

