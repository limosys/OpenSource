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
import java.lang.reflect.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;
import com.borland.jb.util.*;

/**
 * <p>Can be used to display an error dialog
 * box when a <code>java.lang.Exception</code> is generated.
 * <code>DBExceptionDialog</code> is used by <code>DBExceptionHandler</code> to display
 * information about <code>DataSetExceptions.</code></p>
 *
 * <p>The dbSwing components automatically display the <code>DBExceptionDialog</code>
 * when a <code>DataSetException</code> occurs within a dbSwing component. If you decide to implement your own exception handling in your application, however, you
 * can use this dialog to display exceptions to the user.
 * Refer to the <code>DataSetException</code> class for more information.</p>
 *
 * <p>A <code>DBExceptionDialog</code> always displays an OK button. The following properties
 * allow you to customize the appearance and functionality of <code>DBExceptionDialog:</code></p>
 *
 *<ul>
 *<li><code>messageIcon</code> - Sets an icon to be displayed next to the dialog box's message area.</li>
 *<li><code>displayChainedExceptions</code> - Sets whether Next and Previous buttons are displayed when a <code>ChainedException</code> occurs. These buttons allow viewing of individual exceptions in the chain.</li>
 *<li><code>displayStackTraces</code> - Sets whether the Stack Trace toggle button is displayed when a <code>DataSetException</code> occurs. When selected, an exception's stack trace is displayed in the dialog box in addition to its message.</li>
 *<li><code>allowExit</code> - Sets whether an Exit button allowing the user to terminate an application is displayed in the dialog box.</li>
 *<li><code>closeDataStoresOnExit</code> - If the <code>closeDataStoresOnExit</code> property is <code>true</code>, any open <code>JDataStores</code> attached to the dialog box's parent frame are automatically closed if the Exit button is pressed.</li>
 *<li><code>closeConnectionsOnExit</code> - If the <code>closeConnectionsOnExit</code> property is <code>true</code>, any open database connections used by the application will automatically be closed if the Exit button is pressed.</li>
 *<li><code>enableSecretDebugKey</code> - Sets whether the <kbd>Ctrl+Alt+Shift+D</kbd> key combination can be used to toggle the display of all of <code>DBExceptionDialog's</code> buttons at runtime, despite other property settings.</li>
 *</ul>
 *
 *<p><code>DBExceptionDialog</code> automatically disposes itself if the user presses the OK or Exit button, or closes the dialog box's window.</p>
 *
 */
public class DBExceptionDialog extends JDialog
  implements ActionListener, KeyListener
{


  /**
  * <p>Constructs a <code>DBExceptionDialog</code> object with parameters. </p>
  *
  * @param frame The parent <code>Frame. </code>
  * @param title The text string that is displayed on the dialog box title bar.
  * @param ex The <code>Exception</code> that occurred.
  */
  public DBExceptionDialog(Frame frame, String title, Throwable ex) {
    this(frame, title, ex, false);
  }


  /**
  * <p>Constructs a <code>DBExceptionDialog</code> object with parameters. </p>
  *
  * @param frame The parent <code>Frame. </code>
  * @param title The text string that is displayed on the dialog box title bar.
  * @param ex The <code>Exception</code> that occurred.
  * @param c The <code>tComponent</code> for which to display the DBExceptionDialog.
  */
  public DBExceptionDialog(Frame frame, String title, Throwable ex, Component c) {
    this(frame, title, ex, false);
    this.returnFocusComponent = c;
  }


  /**
  * <p>Constructs a <code>DBExceptionDialog</code> object with parameters. </p>
  *
  * @param frame The parent <code>Frame. </code>
  * @param title The text string that is displayed on the dialog box title bar.
  * @param ex The <code>Exception</code> that occurred.
  * @param modal Whether the DBExceptionDialog is modal or not. A modal dialog must be dismissed before using the rest of the program.
  * @param c The <code>tComponent</code> for which to display the DBExceptionDialog.
  */
  public DBExceptionDialog(Frame frame, String title, Throwable ex, boolean modal, Component c) {
    this(frame, title, ex, modal);
    this.returnFocusComponent = c;
  }

  /**
  * <p>Constructs a <code>DBExceptionDialog</code> object with parameters. </p>
  *
  * @param frame The parent <code>Frame. </code>
  * @param title The text string that is displayed on the dialog box title bar.
  * @param exception The <code>Exception</code> that occurred.
  * @param modal Whether the DBExceptionDialog is modal or not. A modal dialog must be dismissed before using the rest of the program.
  */
  public DBExceptionDialog(Frame frame, String title, Throwable exception, boolean modal) {
    super(frame, title, modal);

    makeExceptionList(exception);

    this.frame = frame;
  }

/**
 * <p>Calls <code>addNotify()</code> of the superclass and tweaks the layout of the dialog.</p>
 */
  public void addNotify() {
    super.addNotify();
    if (UIManager.getLookAndFeel().getID().equals("Motif")) {  
      // (admittedly hacky) workaround for very strange button layout sizing only on Solaris
      okButton.setBorder(stackButton.getBorder());
      prevButton.setBorder(stackButton.getBorder());
      nextButton.setBorder(stackButton.getBorder());
      exitButton.setBorder(stackButton.getBorder());
    }
  }

  /**
   * <p>Displays the <code>DBExceptionDialog</code> dialog box.</p>
   */
  public void show() {

    Throwable exception = (Throwable) exceptionVector.elementAt(0);
    String messageString = exception.getMessage();
    // Many java exceptions like NullPointerException have no message.
    //
    if (messageString == null || messageString.length() < 1) {
      messageString = exception.getClass().getName();
    }

    messageArea.setRows(widestMessageLength / messageAreaWidth + 2);
    messageArea.setColumns(messageAreaWidth);
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);
    messageArea.setText(messageString);
    messageArea.setEditable(false);
    messageArea.getCaret().setSelectionVisible(false);
//    messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    messageArea.setBackground(UIManager.getColor("OptionPane.background")); 
    messageArea.setForeground(UIManager.getColor("OptionPane.foreground")); 
    messageArea.setFont(this.getFont());
    messageArea.setEnablePopupMenu(false);
    pane = new JScrollPane(messageArea);
    pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    getContentPane().add(pane, BorderLayout.CENTER);
    if (messageIcon != null) {
      iconLabel.setIcon(messageIcon);
    }
    iconLabel.setVerticalAlignment(SwingConstants.TOP);
    iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    getContentPane().add(iconLabel, BorderLayout.WEST);

    okButton = new RepeatButton(Res._OKBtn);     
    okButton.setRepeat(false);
    okButton.addActionListener(this);
    okButton.setDefaultButton(true);
    buttonPanel.add(okButton);

    prevButton = new RepeatButton(Res._PrevBtn);     
    prevButton.setRepeat(false);
    prevButton.setToolTipText(Res._PrevTip);     
    prevButton.addActionListener(this);

    nextButton = new RepeatButton(Res._NextBtn);     
    nextButton.setRepeat(false);
    nextButton.setToolTipText(Res._NextTip);     
    nextButton.addActionListener(this);

    stackButton = new JdbToggleButton(Res._StackBtn);     
    stackButton.setToolTipText(Res._StackTip);     
    stackButton.addActionListener(this);

    exitButton = new RepeatButton(Res._ExitBtn);     
    exitButton.setRepeat(false);
    exitButton.setToolTipText(Res._ExitTip);     
    exitButton.addActionListener(this);

    if (enableSecretKey) {
      addKeyListener(this);
    }
    else {
      removeKeyListener(this);
    }

    updateButtons();

    buttonGridLayout.setRows(1);
    buttonGridLayout.setHgap(5);
    buttonPanel.setLayout(buttonGridLayout);
    buttonOuterPanel.add(buttonPanel);
    getContentPane().add(buttonOuterPanel, BorderLayout.SOUTH);
    setBackground(UIManager.getColor("OptionPane.background")); 
    setForeground(UIManager.getColor("OptionPane.foreground")); 

    buttonPanel.invalidate();
    pack();
    initialPackedSize = getSize();
    setLocationRelativeTo(frame);

    showCount++;

    super.show();
  }

  void updateButtons() {

    // there are at most five buttons (OK, Next, Prev, Details, Exit), and
    // OK is always present
    int buttonCount = 1;

    if (prevButton.getParent() == buttonPanel) {
      buttonPanel.remove(prevButton);
    }
    if (nextButton.getParent() == buttonPanel) {
      buttonPanel.remove(nextButton);
    }
    if (stackButton.getParent() == buttonPanel) {
      buttonPanel.remove(stackButton);
    }
    if (exitButton.getParent() == buttonPanel) {
      buttonPanel.remove(exitButton);
    }

    Throwable exception = (Throwable) exceptionVector.elementAt(exceptionPosition);
    if (displayChains && exception instanceof ChainedException &&
        ((ChainedException) exception).getExceptionChain() != null) {
      prevButton.setEnabled(exceptionPosition > 0);
      buttonPanel.add(prevButton);
      buttonCount++;

      nextButton.setEnabled(exceptionPosition < (exceptionVector.size()-1));
      buttonPanel.add(nextButton);
      buttonCount++;
    }

    if (enableStackTrace) {
      buttonPanel.add(stackButton);
      buttonCount++;
    }

    if (allowExit) {
      buttonPanel.add(exitButton);
      buttonCount++;
    }

    buttonGridLayout.setColumns(buttonCount);
    buttonPanel.invalidate();
    pack();
  }

  //
  // java.awt.event.ActionListener interface implementation
  //
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == exitButton) {
      if (closeDataStores || closeConnections) {
        Method isOpenMethod;
        boolean isOpen;
        Method closeMethod;
        DataSetAware dataSetAware;
        DataSet dataSet;
        Frame [] frames = Frame.getFrames();
        for (int i = 0; i < frames.length; i++) {
          DataSetAware [] dataSetAwares = DBUtilities.findDataAwareChildren(frames[i]);
          for (int j = 0; j < dataSetAwares.length; j++) {
            dataSetAware = dataSetAwares[j];
            close(dataSetAware);
          }
          Window [] windows = frames[i].getOwnedWindows();
          for (int j = 0; j < windows.length; j++) {
            dataSetAwares = DBUtilities.findDataAwareChildren(windows[j]);
            for (int k = 0; k < dataSetAwares.length; k++) {
              dataSetAware = dataSetAwares[k];
              close(dataSetAware);
            }
          }
        }
      }
      dispose();
      System.exit(0);
    }
    else if (e.getSource() == okButton) {
      showCount--;
      dispose();
      if (returnFocusComponent != null) {
        returnFocusComponent.requestFocus();
        returnFocusComponent = null;
      }
    }
    else if (e.getSource() == nextButton) {
      if (exceptionPosition < (exceptionVector.size()-1)) {
        displayException(++exceptionPosition);
      }
    }
    else if (e.getSource() == prevButton) {
      if (exceptionPosition > 0 ) {
        displayException(--exceptionPosition);
      }
    }
    else if (e.getSource() == stackButton) {
      displayException(exceptionPosition);
      if (!dialogExpanded) {
        dialogExpanded = true;
        pack();
      }
    }
  }

  private void close(DataSetAware dataSetAware) {
    Method isOpenMethod;
    boolean isOpen;
    Method closeMethod;
    DataSet dataSet = dataSetAware.getDataSet();
    if (dataSet != null) {
      try {
        if (closeDataStores &&
            dataSet.getStorageDataSet()instanceof StorageDataSet) {
          Object store = ((StorageDataSet) dataSet.getStorageDataSet()).
              getStore();
          if (store != null &&
              store.getClass().getName().equals(
              "com.borland.datastore.DataStore")) { 
            isOpenMethod = store.getClass().getMethod("isOpen", null); 
            isOpen = ((Boolean) isOpenMethod.invoke(store,
                null)).booleanValue();
            if (isOpen) {
              closeMethod = store.getClass().getMethod("close", null); 
              closeMethod.invoke(store, null);
            }
          }
        }
        if (closeConnections && dataSet != null &&
            dataSet.getStorageDataSet()instanceof QueryDataSet) {
          Database database = (((QueryDataSet) dataSet.getStorageDataSet())).
              getDatabase();
          if (database != null && database.isOpen()) {
            database.closeConnection();
          }
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  //
  // java.awt.event.KeyListener interface implementation
  //
  public void keyTyped(KeyEvent e) {}
  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_D &&
        e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
      if (!hiddenToggleState) {
        hiddenChains = isDisplayChainedExceptions();
        hiddenStack = isDisplayStackTraces();
        hiddenExit = isAllowExit();
        setDisplayChainedExceptions(true);
        setDisplayStackTraces(true);
        setAllowExit(true);
      }
      else {
        setDisplayChainedExceptions(hiddenChains);
        setDisplayStackTraces(hiddenStack);
        setAllowExit(hiddenExit);
      }
      hiddenToggleState = !hiddenToggleState;
      updateButtons();
    }
  }


  private void displayException(int pos) {
    if (displayChains) {
      prevButton.setEnabled(pos > 0);
      nextButton.setEnabled(pos < (exceptionVector.size()-1));
    }

    Throwable ex  = (Throwable) exceptionVector.elementAt(pos);
    if (stackButton.isSelected()) {
      ByteArrayOutputStream byteStream  = new ByteArrayOutputStream(2048);
      PrintStream           printStream = new PrintStream(byteStream);
      ex.printStackTrace(printStream);   
      printStream.flush();
      messageArea.setLineWrap(false);
      messageArea.getCaret().setSelectionVisible(true);
      messageArea.setText(byteStream.toString());
      if (getSize().equals(initialPackedSize)) {
        messageArea.setRows(10);
      }
      else {
        dialogExpanded = true;
      }
      messageArea.setBackground(UIManager.getColor("TextArea.background")); 
      messageArea.setForeground(UIManager.getColor("TextArea.foreground")); 
      messageArea.setCaretPosition(0);
      messageArea.setEnablePopupMenu(true);
//      messageArea.setBorder(BorderFactory.createLoweredBevelBorder());
      pane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                                                        BorderFactory.createLoweredBevelBorder()));
      iconLabel.setIcon(null);
    }
    else {
      String messageString  = ex.getMessage();
      // Many java exceptions like NullPointerException have no message.
      //
      if (messageString == null || messageString.length() < 1) {
        messageString = ex.getClass().getName();
      }
      messageArea.setRows(widestMessageLength / messageAreaWidth + 2);
      messageArea.setColumns(messageAreaWidth);
      messageArea.setLineWrap(true);
      messageArea.getCaret().setSelectionVisible(false);
      messageArea.setText(messageString);
      messageArea.setBackground(UIManager.getColor("OptionPane.background")); 
      messageArea.setForeground(UIManager.getColor("OptionPane.foreground")); 
      if (messageIcon != null) {
        iconLabel.setIcon(messageIcon);
      }
//      messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      messageArea.setEnablePopupMenu(false);
    }
  }

  private void makeExceptionList(Throwable ex) {
    exceptionVector.addElement(ex);
    int length = widestMessageLength;
    String msg = ex.getMessage();
    if (msg == null || msg.length() == 0) {
      msg = ex.getClass().getName();
    }
    if ((length = msg.length()) > widestMessageLength) {
      widestMessageLength = length;
    }

    if (ex instanceof ChainedException) {
      ExceptionChain chain = ((ChainedException)ex).getExceptionChain();
      while (chain != null) {
        ex = chain.getException();
        if (isDisplayChainedExceptions()) {
          msg = ex.getMessage();
          if (msg == null || msg.length() == 0) {
            msg = ex.getClass().getName();
          }
          if ((length = msg.length()) > widestMessageLength) {
            widestMessageLength = length;
          }
        }
        exceptionVector.addElement(ex);
        chain = chain.getNext();
      }
    }
    exceptionPosition = 0;
  }

  /**
   * <p>Returns the number of times the dialog box has been called with the <code>show()</code> method.</p>
   *
   * @return The number of times the dialog box has been called.
   * @see #show
   */
  public static int getShowCount() {
    return showCount;
  }

  /**
   * <p>Sets the icon displayed next to the dialog box's message.
   * By default, a <code>JOptionPane's</code> information icon is used.
   * If <code>messageIcon</code> is set to <strong>null,</strong> no icon is displayed.</p>
   *
   * @param messageIcon The icon displayed next to the dialog box's message.
   * @see #getMessageIcon
   */
  public void setMessageIcon(Icon messageIcon) {
    this.messageIcon = messageIcon;
  }

  /**
   * <p>Returns the icon displayed next to the dialog box's message.
   * By default, a <code>JOptionPane's</code> information icon is used.</p>
   *
   * @return The icon displayed next to the dialog box's message. If <strong>null,</strong> no icon is displayed.
   * @see #setMessageIcon
   */
  public Icon getMessageIcon() {
    return messageIcon;
  }

  /**
   * <p>Sets whether the Next or Previous button appears
   * when a <code>ChainedException</code> occurs. For non-chained exceptions, this property has no effect. This property is <code>true</code> by default.</p>
   *
   * @param displayChains If <code>true</code>, the Next or Previous button appears when a <code>ChainedException</code> occurs.
   * @see #isDisplayChainedExceptions
   */
  public void setDisplayChainedExceptions(boolean displayChains) {
    this.displayChains = displayChains;
  }

  /**
   * <p>Returns whether the Next or Previous button appears
   * when a <code>ChainedException</code> occurs. For non-chained exceptions, this property has no effect.</p>
   *
   * @return If <code>true</code>, the Next or Previous button appears when a <code>ChainedException</code> occurs.
   * @see #setDisplayChainedExceptions
   */
  public boolean isDisplayChainedExceptions() {
    return displayChains;
  }

  /**
   * <p>Sets whether the Stack Trace toggle button will appear
   * when an <code>Exception</code> occurs. When selected, an exception message
   * is displayed along with its stack trace. This property is <code>true</code> by default.</p>
   *
   * @param enableStackTrace If <code>true</code>, the Stack Trace toggle button will appear
   * when an <code>Exception</code> occurs.
   * @see #isDisplayStackTraces
   */
  public void setDisplayStackTraces(boolean enableStackTrace) {
    this.enableStackTrace = enableStackTrace;
  }

  /**
   * <p>Returns whether the Stack Trace toggle button will appear
   * when an Exception occurs. When selected, an exception message
   * is displayed along with its stack trace.</p>
   *
   * @return If <code>true</code>, the Stack Trace toggle button will appear
   * when an <code>Exception</code> occurs.
   * @see #setDisplayStackTraces
   */
  public boolean isDisplayStackTraces() {
    return enableStackTrace;
  }

  /**
   * <p>Sets whether an Exit button is displayed when an <code>Exception</code> occurs.</p>
   *
   * @param allowExit If <code>true</code>, and Exit button is displayed when an <code>Exception</code> occurs.
   * @see #isAllowExit
   */
  public void setAllowExit(boolean allowExit) {
    this.allowExit = allowExit;
  }

  /**
   * <p>Returns whether an Exit button is displayed when an <code>Exception</code> occurs.</p>
   *
   * @return If <code>true</code>, and Exit button is displayed when an <code>Exception</code> occurs.
   * @see #setAllowExit
   */
  public boolean isAllowExit() {
    return allowExit;
  }

  /**
   * <p>Sets whether <code>JDataStores</code> should automatically be detected and
   * closed when the Exit button is used to terminate an application.
   * This property is <code>true</code> by default.</p>
   *
   * @param closeDataStores If <code>true</code>, <code>JDataStores</code>are automatically detected and closed when the Exit button is used to terminate an application.
   * @see #isCloseDataStoresOnExit
   */
  public void setCloseDataStoresOnExit(boolean closeDataStores) {
    this.closeDataStores = closeDataStores;
  }

  /**
   * <p>Returns whether <code>JDataStores</code> should automatically be detected and
   * closed when the Exit button is used to terminate an application.</p>
   *
   * @return If <code>true</code>, <code>JDataStores</code> are automatically detected and closed when the Exit button is used to terminate an application.
   * @see #setCloseDataStoresOnExit
   */
  public boolean isCloseDataStoresOnExit() {
    return closeDataStores;
  }

  /**
   * <p>Sets whether any open database connections should be closed when
   * the Exit button is used to terminate an application.
   * This property is set to <code>true</code> by default.</p>
   *
   * @param closeConnections If <code>true</code>, open database connections are closed when the Exit button is used to terminate an application.
   * @see #isCloseConnectionsOnExit
   */
  public void setCloseConnectionsOnExit(boolean closeConnections) {
    this.closeConnections = closeConnections;
  }

  /**
   * <p>Returns whether any open database connections should be closed when
   * the Exit button is used to terminate an application.</p>
   *
   * @return If <code>true</code>, open database connections are closed when the Exit button is used to terminate an application.
   * @see #setCloseConnectionsOnExit
   */
  public boolean isCloseConnectionsOnExit() {
    return closeConnections;
  }

  /**
   * <p>Sets whether the <kbd>Ctrl+Alt+Shift+D</kbd> key combination can be used
   * to unconditionally force the display of all buttons when the dialog box is visible.
   * This property is <code>true</code> by default.</p>
   *
   * @param enableSecretKey If <code>true</code>, the <kbd>Ctrl+Alt+Shift+D</kbd> key combination is used
   * to unconditionally force the display of all buttons when the dialog box is visible.
   * @see #isEnableSecretDebugKey
   */
  public void setEnableSecretDebugKey(boolean enableSecretKey) {
    this.enableSecretKey = enableSecretKey;
  }

  /**
   * <p>Returns whether the <kbd>Ctrl+Alt+Shift+D</kbd> key combination can be used
   * to unconditionally force the display of all buttons when
   * the dialog box is visible.</p>
   *
   * @return If <code>true</code>, the <kbd>Ctrl+Alt+Shift+D</kbd> key combination can be used
   * to unconditionally force the display of all buttons when the dialog box is visible.
   * @see #setEnableSecretDebugKey
   */
  public boolean isEnableSecretDebugKey() {
    return enableSecretKey;

  }

  private transient Vector exceptionVector = new Vector();

  private int             exceptionPosition;

  private static int      showCount;
  private transient Component       returnFocusComponent;

  /** whether or not to allow display of chained exceptions */
  private boolean displayChains = true;

  /** whether or not to allow display of exception stack traces */
  private boolean enableStackTrace = true;

  /** whether or not to allow Exit button to exit app */
  private boolean allowExit = true;

  /** whether or not to close DataStores on application exit */
  private boolean closeDataStores = true;

  /** whether or not to close database connections on application exit */
  private boolean closeConnections = true;

  /** whether or not to enable Ctrl-Shift-Alt-D to toggle debugging options */
  private static boolean enableSecretKey = true;

  private boolean hiddenToggleState = false;
  private boolean hiddenChains;
  private boolean hiddenStack;
  private boolean hiddenExit;

  private RepeatButton okButton;
  private RepeatButton nextButton;
  private RepeatButton prevButton;
  private JdbToggleButton stackButton;
  private RepeatButton exitButton;

  private JdbTextArea messageArea = new JdbTextArea() {
    // only allow the messageArea to gain focus when viewing the stack trace
    public boolean isFocusTraversable() {
      if (isEnablePopupMenu()) {
        return true;
      }
      return false;
    }
  };
  private int messageAreaWidth = 50;
  private int widestMessageLength = messageAreaWidth;
  private JPanel buttonOuterPanel = new JPanel();
  private GridLayout buttonGridLayout = new GridLayout();
  private JPanel buttonPanel = new JPanel();
  private JLabel iconLabel = new JLabel();
  private Frame frame;
  private Icon messageIcon = UIManager.getIcon("OptionPane.informationIcon");  
  private boolean dialogExpanded = false;
  private Dimension initialPackedSize;
  private JScrollPane pane;
}
