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

import com.borland.dx.dataset.*;

/**
 * <p>The dbSwing package's default
 * <code>DataSetException</code> handler. It displays an error dialog
 * <code>(DBExceptionDialog)</code> when a <code>DataSetException</code> is thrown.
 * See <a href="DBExceptionDialog.html"><code>DBExceptionDialog</code></a> for descriptions of properties which can
 * be set to configure the capabilities of the error dialog.</p>
 *
 * <p>If the exception thrown is of type <code>ValidationException</code> (a subclass
 * of <code>DataSetException</code>), the error dialog appears only if there
 * are no <code>StatusEvent</code> listeners on the <code>DataSet.</code> An example of this is the
 * <code>JdbStatusLabel</code> component.  A <code>ValidationException</code> is generated typically
 * by a constraint violation, such as a value entered outside the
 * range of allowable minimum or maximum values.</p>
 *
 * <p>To programmatically suppress this dialog at run time, set the
 * <code>displayErrors</code> property of the <code>DataSet</code> to <code>false</code>.</p>
 *
 *<p>Usage example:</p>
 *
 *<pre>
 * DBExceptionHandler handler = DBException.getInstance();
 *   try {
 *     code which could cause a DataSetException
 *   } catch (DataSetException exception) {
 *     handler.handleException(dataSet, frame, exception);
 * }
 *</pre>
 */
public class DBExceptionHandler implements Designable, java.io.Serializable {

  /**
   * <p>Constructs a <code>DBExceptionHandler</code>.</p>
   */
  public DBExceptionHandler() {
  }

  /**
   * <p>Returns a single instance of <code>DBExceptionHandler</code>.
   * Properties set on the instance returned by this
   * method will be applied to all <code>DBExceptionDialogs</code>
   * subsequently displayed.</p>
   *
   * @return A single instance of <code>DBExceptionHandler.</code>
   */
  public static DBExceptionHandler getInstance() {
    if (handler == null) {
      handler = new DBExceptionHandler();
    }
    return handler;
  }

  /**
   * <p>Sets whether the Next or Previous button appears
   * when a <code>ChainedException</code> occurs. For non-chained exceptions, this property has no effect. This property is <code>true</code> by default.</p>
   *
   * @param displayChains If <code>true</code>, the Next or Previous buttons appear when a <code>ChainedException</code> occurs.
   * @see #isDisplayChainedExceptions
   */
  public void setDisplayChainedExceptions(boolean displayChains) {
    this.displayChains = displayChains;
  }

  /**
   * <p>Returns whether the Next or Previous buttons appear
   * when a <code>ChainedException</code> occurs. For non-chained exceptions,
   * this property has no effect. This property is <code>true</code> by default.</p>
   *
   * @return If <code>true</code>, the Next or Previous buttons appear when a <code>ChainedException</code> occurs.
   * @see #setDisplayChainedExceptions
   */
  public boolean isDisplayChainedExceptions() {
    return displayChains;
  }

  /**
   * <p>Sets whether the Stack Trace toggle button appears
   * when an <code>Exception</code> occurs. When it is selected, an exception message
   * is displayed along with its stack trace.  This property is <code>true</code> by default.</p>
   *
   * @param displayStack If <code>true</code>, the Stack Trace toggle button appears when an <code>Exception</code> occurs.
   * @see #isDisplayStackTraces
   */

  public void setDisplayStackTraces(boolean displayStack) {
    this.displayStack = displayStack;
  }

  /**
   * Returns whether the Stack Trace toggle button appears
   * when an <code>Exception</code> occurs. When selected, an exception message
   * is displayed along with its stack trace.
   *
   * @return If <code>true</code>, the Stack Trace toggle button appears when an <code>Exception</code> occurs.
   * @see #setDisplayStackTraces
   */
  public boolean isDisplayStackTraces() {
    return displayStack;
  }

  /**
   * <p>Sets whether an Exit button is displayed when an <code>Exception</code> occurs.</p>
   *
   * @param allowExit If <code>true</code>, an Exit button is displayed when an <code>Exception</code> occurs.
   * @see #isAllowExit
   */
  public void setAllowExit(boolean allowExit) {
    this.allowExit = allowExit;
  }

   /**
   * <p>Returns whether an Exit button is displayed when an <code>Exception</code> occurs.</p>
   *
   * @return If <code>true</code>, an Exit button is displayed when an <code>Exception</code> occurs.
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

   /**
    * <p>Processes the exception.</p>
    *
    * @param dataSet The <code>DataSet</code> the exception occurs in if the exception is a <code>DataSet</code> exception.
    * @param component The component on which the exception occurred.
    * @param ex The <code>Exception</code> that occurs.
    * @param modal  If <code>true</code>, the <code>DBExceptionDialog</code> is modal. A modal dialog must be dismissed before using the rest of the program.
    */

  public static final void handleException(DataSet dataSet, Component component, Throwable ex, boolean modal) {

    if (DataSetException.getExceptionListeners() != null) {
      DataSetException.getExceptionListeners().dispatch(new ExceptionEvent(dataSet, component, ex));
    }
    else if ( (dataSet == null || MatrixData.displayError(dataSet, ex)) &&
        (errorDialog == null || !errorDialog.isVisible())) {
      if (errorDialog != null) {
        errorDialog.dispose();
      }

      errorDialog = new DBExceptionDialog(component != null ?
          DBUtilities.getFrame(component) : null, Res._Error, ex, modal,     
          component);
      errorDialog.setDisplayChainedExceptions(displayChains);
      errorDialog.setDisplayStackTraces(displayStack);
      errorDialog.setAllowExit(allowExit);
      errorDialog.setCloseDataStoresOnExit(closeDataStores);
      errorDialog.setCloseConnectionsOnExit(closeConnections);
      errorDialog.setEnableSecretDebugKey(enableSecretKey);
      errorDialog.show();
    }
  }

  static DBExceptionDialog errorDialog;

  void showDialog() {
    errorDialog.show();
  }

  /**
   * <p>Calls <code>handleException(com.borland.dx.dataset.DataSet, java.awt.Component,
java.lang.Throwable, boolean)</code> and passes to it the <code>dataSet,component,</code> and <code>ex</code> parameter values. It also passes the value of <code>false</code> for the <code>modal</code> parameter. </p>
   *
   * @param dataSet The <code>DataSet</code> the exception occurs in if the exception is a <code>DataSet</code> exception.
   * @param component The component on which the exception occurred.
   * @param ex The <code>Exception</code> that occured.
   * @see #handleException(DataSet dataSet, Component component, Throwable ex, boolean modal)
   */
  public static final void handleException(DataSet dataSet, Component component, Exception ex) {
    handleException(dataSet, component, ex, true);
//    handleException(dataSet, component, ex, false);
  }


  /**
   *<p>Calls <code>handleException(com.borland.dx.dataset.DataSet, java.awt.Component,
java.lang.Throwable, boolean)</code> and passes to it the <code>dataSet</code> and <code>ex</code> parameter values. It passes a <strong>null</strong> value for the <code>component</code> parameter and passes the value of <code>false</code> for the <code>modal</code> parameter. </p>
   *
   * @param dataSet The <code>DataSet</code> the exception occurs in if the exception is a <code>DataSet</code> exception.
   * @param ex The <code>Exception</code> that occured.
   * @see #handleException(DataSet dataSet, Component component, Throwable ex, boolean modal)
   */
  public static final void handleException(DataSet dataSet, Exception ex) {
    handleException(dataSet, null, ex, true);
//    handleException(dataSet, null, ex, false);
  }

  /**
   * <p>Calls <code>handleException(com.borland.dx.dataset.DataSet, java.awt.Component,
java.lang.Throwable, boolean)</code> and passes to it the <code>ex</code> parameter value. It passes a <strong>null</strong> value for the <code>dataSet</code> and <code>component</code> parameters and passes the value of <code>false</code> for the <code>modal</code> parameter. </p>
   *
   * @param ex The <code>Exception</code> that occured.
   * @see #handleException(DataSet dataSet, Component component, Throwable ex, boolean modal)
   */
  public static final void handleException(Exception ex) {
    handleException(null, null, ex);
  }

  /** singleton instance of DBExceptionHandler */
  private static DBExceptionHandler handler;

  /** whether or not to allow display of chained exceptions */
  private static boolean displayChains = true;

  /** whether or not to allow display of exception details */
  private static boolean displayStack = true;

  /** whether or not to allow Exit button to exit app */
  private static boolean allowExit = false;

  /** whether or not to close DataStores on application exit */
  private static boolean closeDataStores = true;

  /** whether or not to close database connections on application exit */
  private static boolean closeConnections = true;

  /** whether or not to enable Ctrl-Shift-Alt-D to toggle debugging options */
  private static boolean enableSecretKey = true;
}

