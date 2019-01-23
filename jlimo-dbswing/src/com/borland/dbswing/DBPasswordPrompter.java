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

import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;

/**
 * <p>Prompts a user for a password to access a DataExpress <code>Database</code> and opens
the connection to the database. To display <code>DBPasswordPrompter</code>, call its <code>showDialog()</code> method
early in an application before any data-aware components attempt to open the database automatically. </p>
 *
 *  <p>You can provide a default user name or password by setting the <code>userName</code> and <code>password</code> properties. By default, a user is allowed to enter three invalid passwords before <code>DBPasswordPrompter</code> automatically closes. You can change the maximum number of attempts by setting the <code>maxAttempts</code> property. You must also set the <code>database</code> property to reference the
<code>com.borland.dx.sql.dataset.Database</code> database to be accessed, and set the <code>frame</code> property to a parent <code>Frame</code> for the dialog box. </p>
 *
 *  <p>Call <code>DBPasswordPrompter's showDialog()</code> method to prompt a user for a username and password. The <code>showDialog()</code> method returns a <code>true</code> value and opens the database connection if the user entered a valid password. Otherwise, <code>showDialog()</code> returns a value of <code>false</code>, and the database
connection remains unopened. </p>
 *
 */
public class DBPasswordPrompter implements ActionListener, java.io.Serializable {

 /**
  * <p>Constructs a <code>DBPasswordPrompter</code>.  If you use this constructor,
  * you must remember to set the <code>frame</code> and <code>database</code>
  * properties before displaying the password dialog box.</p>
  */
  public DBPasswordPrompter() {
  }
 /**
  * <p>Constructs a <code>DBPasswordPrompter</code> with a specified <code>Frame</code>, a text string that displays on the title bar, and the <code>Database</code> to connect to. </p>
  *
  * @param frame The parent <code>Frame</code> of the dialog box.
  * @param title The text string that appears on the title bar.
  * @param database The <code>Database</code> to connect to.
  */
  public DBPasswordPrompter(Frame frame, String title, Database database) {
    setFrame(frame);
    setTitle(title);
    setDatabase(database);
  }

 /**
  * <p>Sets the parent <code>Frame</code> for the dialog box. You must set the <code>database</code> property before calling <code>showDialog()</code> to display the dialog box. </p>
  *
  * @param frame The parent <code>Frame</code> for the dialog box.
  * @see #getFrame
  */
  public void setFrame(Frame frame) {
    this.frame = frame;
  }

 /**
  * <p>Returns the parent <code>Frame</code> for the dialog box. </p>
  *
  * @return The parent <code>Frame</code> for the dialog box.
  * @see #setFrame
  */
  public Frame getFrame() {
    return frame;
  }

 /**
  * <p>Sets a text string that appears on the title bar. Setting this property is optional. If you don't
set it, the URL of the database displays on the title bar. To disable this automatic display, set the title to
an empty string ("").  </p>
  *
  * @param title The text string that appears on the title bar.
  * @see #getTitle
  */
  public void setTitle(String title) {
    this.title = title;
  }

 /**
  * <p>Returns the text string that appears on the title bar. If this property is not set, the URL of the database is returned.  </p>
  *
  * @return The text string that appears on the title bar.
  * @see #setTitle
  */
  public String getTitle() {
    return title;
  }


 /**
  * <p>Sets a default username.  </p>
  *
  * @param userName The user name.
  * @see #getUserName
  */
  public void setUserName(String userName) {
    this.userName = userName;
  }

 /**
  * <p>Returns a default username.  </p>
  *
  * @return The user name.
  * @see #setUserName
  */
  public String getUserName() {
    return userName;
  }

 /**
  * <p>Sets a default password.  </p>
  *
  * @param password The password.
  * @see #getPassword
  */
  public void setPassword(String password) {
    this.password = password;
  }

 /**
  * <p>Returns a default password.  </p>
  *
  * @return The password.
  * @see #setPassword
  */
  public String getPassword() {
    return password;
  }

 /**
  * <p>Sets the <code>com.borland.dx.sql.dataset.Database</code> the user is trying to access. You must set the <code>database</code> property before calling <code>showDialog()</code> to display the dialog box. </p>
  *
  * @param database The database.
  * @see #getDatabase
  */
  public void setDatabase(Database database) {
    this.database = database;
  }

 /**
  * <p>Returns the <code>com.borland.dx.sql.dataset.Database</code> the user is trying to access. </p>
  *
  * @return The database.
  * @see #setDatabase
  */
  public Database getDatabase() {
    return database;
  }

 /**
  * <p>Sets the maximum number of attempts the user can try to enter a valid password before the
dialog box closes. The default value is three. </p>
  *
  * @param maxAttempts The maximum number of attempts the user can try to enter a valid password before the
dialog box closes.
  * @see #getMaxAttempts
  */
  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

 /**
  * <p>Returns the maximum number of attempts the user can try to enter a valid password before the
dialog box closes.  </p>
  *
  * @return The maximum number of attempts the user can try to enter a valid password before the
dialog box closes.
  * @see #setMaxAttempts
  */
  public int getMaxAttempts() {
    return maxAttempts;
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
   * <p>Displays the password prompt dialog.
   * Call <code>showDialog()</code> and check its return value to
   * determine whether the user entered a valid password.</p>
   *
   * @see #showDialog()
   */
  public void show() {
    showDialog();
  }

  /**
   * <p>Displays the password prompt dialog.  Before calling this method,
   *  set both the <code>frame</code> and <code>database</code> properties,
   * otherwise an <code>IllegalStateException</code> is thrown. <code>showDialog()</code> connects with the database and returns <code>true</code> if a valid
   * password is entered within the number of attempts specified by <code>maxAttempts</code>.</p>
   *
   * @return If <code>true</code>, a valid password was entered
   */
  public boolean showDialog() {
    if (database == null) {
      throw new IllegalStateException(Res._PassPrmptNoDatabase);     
    }

    attempts = 0;

    if (database != null) {
      descriptor = database.getConnection();
      if (userName == null) {
        userName = descriptor.getUserName();
      }
      promptPasswordState = descriptor.isPromptPassword();
      // temporarily turn off promptPassword so that if
      // the password challenge fails, we can display
      // ourself instead of the built-in prompt dialog.
      if (promptPasswordState) {
        descriptor.setPromptPassword(false);
      }
      if (title == null) {
        title = descriptor.getConnectionURL();
      }
    }
    dialog = new DBPasswordDialog(frame, title);
    dialog.getOkButton().addActionListener(this);
    dialog.getCancelButton().addActionListener(this);
    dialog.setUserName(userName);
    dialog.setUserNameRequired(userNameRequired);
    dialog.setPassword(password);
    dialog.setPasswordRequired(passwordRequired);
    dialog.setAutoDispose(false);
    dialog.setLocationRelativeTo(frame);

    dialog.show();

    dialog.getOkButton().removeActionListener(this);
    dialog.getCancelButton().removeActionListener(this);
    dialog.dispose();

    descriptor.setPromptPassword(promptPasswordState);

    return validPassword && attempts < maxAttempts;
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == dialog.getOkButton()) {
      attempts++;
      try {
        userName = dialog.getUserName();
        password = dialog.getPassword();

        descriptor.setUserName(userName);
        descriptor.setPassword(password);

        database.setConnection(descriptor);
        database.openConnection();

        if (!database.isOpen()) {
          return;
        }
      }
      catch (DataSetException ex) {

        // keep the dialog up until a valid password
        // is entered, the max. number of attempts
        // is exceeded, or the user cancels.
        if (attempts >= maxAttempts) {
          dialog.setVisible(false);
        }
        return;
      }
      validPassword = true;
    }
    // got here either because the user pressed the
    // cancel button, or the username and password
    // were valid.  In either case, we want to
    // hide the dialog.
    dialog.setVisible(false);
  }

  /** The dialog's parent frame. */
  private Frame frame;

  /** The title of the dialog box. */
  private String title;

  /** The default username. */
  private String userName;

  /** The default password. */
  private String password;

  /** Contains the com.borland.dx.sql.Database to connect to. */
  private Database database;

  /** Contains the com.borland.dx.sql.dataset.ConnectionDescriptor of the Database. */
  private ConnectionDescriptor descriptor;

  /** Whether or not a validPassword was entered. */
  private boolean validPassword = false;

  /** The DBPasswordDialog displayed to enter password. */
  private DBPasswordDialog dialog;

  /** Holds original state of ConnectionDescriptor.setPromptPassword. */
  private boolean promptPasswordState;

  /** The maximum attempts allowed to enter a valid password before the dialog closes. */
  private int maxAttempts = 3;

  /** Holds the number of attempts made to enter a valid password. */
  private int attempts = 0;

  private boolean userNameRequired = true;
  private boolean passwordRequired = true;
}
