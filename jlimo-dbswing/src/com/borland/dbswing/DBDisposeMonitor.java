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
import java.lang.reflect.*;

import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;

/**
 * <p><code>DBDisposeMonitor</code> is a utility component which helps ensure that <code>DataSet</code>
 * resources used by dbSwing data-aware controls are released when the
 * Window containing the controls (usually a <code>Frame</code>) is disposed.
 * In particular, when the Window <code>DBDisposeMonitor</code> is monitoring is
 * disposed (or, optionally, closed), <code>DBDisposeMonitor</code> will search for
 * all data-aware components in the Window and set their <code>DataSet</code>
 * property to <code>null.</code></p>
 *
 * <p><code>DBDisposeMonitor</code> is also a useful monitoring tool when developing
 * applications which use DataExpress <code>JDataStore</code> components.  A
 * <code>DBDisposeMonitor</code> which has been assigned a container will
 * automatically close any <code>JDataStores</code> it detects when the container is
 * disposed.  You can alter this behavior by setting the
 * <code>closeDataStores</code> property, which is <code>true</code> by default.
 * Similarly, <code>DBDisposeMonitor</code> will close any open database
 * connections unless you disable this default behavior by setting
 * its <code>closeConnections</code> property to  <code>false</code>.</p>
 *
 * <p>There are two ways to set up a <code>DBDisposeMonitor</code> for use in an
 * application.  The simplest way is to set the
 * <code>dataAwareComponentContainer</code> property to the container
 * (usually a <code>JFrame</code>, but could also be a <code>JPanel</code>) to monitor.
 * Alternatively, you can explicitly specify an array of data-aware
 * components to monitor via the <code>dataSetAwareComponents</code>
 * property.</p>
 *
 * <p>The <code>executeOnWindowClosing</code> property determines whether
 * <code>DBDisposeMonitor</code> will execute on <code>Window.CLOSING</code> events as
 * well as <code>Window.CLOSE</code> (dispose) events. <code>executeOnWindowClosing</code>
 * is <code>true</code> by default.</p>
 *
 */
public class DBDisposeMonitor
  implements WindowListener, ComponentListener, Designable, java.io.Serializable
{

/**
 * <p>Constructs a <code>DBDisposeMonitor</code> component.</p>
 */
  public DBDisposeMonitor() {
  }

  /**
   * <p>Sets the container of data-aware components to be deregistered
   * as <code>DataSet</code> listeners when <code>DBDisposeMonitor</code> detects that a window is
   * being disposed or closed.  </p>
   *
   * <p>If set, this property overrides the
   * <code>dataSetAwareComponents</code>  property.</p>
   *
   * @param container The container of data-aware components.
   * @see #getDataAwareComponentContainer
   */
  public void setDataAwareComponentContainer(Container container) {
    if (this.container != container && this.container != null) {
      this.container.removeComponentListener(this);
    }
    this.container = container;
    registeredAsWindowListener = false;
    if (container != null) {
      container.addComponentListener(this);
    }
  }

  /**
   * <p>Returns the container of data-aware components to be deregistered as <code>DataSet</code> listeners when <code>DBDisposeMonitor</code> detects that a window is
   * being disposed or closed.  </p>
   *
   * @return The container of data-aware components.
   * @see #setDataAwareComponentContainer
   */
  public Container getDataAwareComponentContainer() {
    return container;
  }

  /**
   * <p>Sets the array of data-aware components which should be deregistered
   * as <code>DataSet</code> listeners when <code>DBDisposeMonitor</code> detects a window is
   * being disposed or closed.  </p>
   *
   * <p>The <code>dataAwareComponentContainer</code>
   * property has precedence over this property.</p>
   *
   * @param dataAwareComponents The array of data-aware components.
   * @see #getDataSetAwareComponents
   */
  public void setDataSetAwareComponents(DataSetAware [] dataAwareComponents) {
    if (this.dataAwareComponents != dataAwareComponents && this.dataAwareComponents != null) {
      for (int i = 0; i < this.dataAwareComponents.length; i++) {
        if (this.dataAwareComponents[i] instanceof Component) {
          ((Component) this.dataAwareComponents[i]).removeComponentListener(this);
        }
      }
    }
    this.dataAwareComponents = dataAwareComponents;
    registeredAsWindowListener = false;
    for (int i = 0; i < dataAwareComponents.length; i++) {
      if (dataAwareComponents[i] instanceof Component) {
        ((Component) dataAwareComponents[i]).addComponentListener(this);
      }
    }
  }

  /**
   * <p>Returns the array of data-aware components which should be deregistered
   * as <code>DataSet</code> listeners when DBDisposeMonitor detects a window is
   * being disposed or closed.</p>
   *
   * @return The array of data-aware components.
   * @see #setDataSetAwareComponents
   */
  public DataSetAware [] getDataSetAwareComponents() {
    return dataAwareComponents;
  }

  /**
   * <p>Sets whether <code>DBDisposeMonitor</code> automatically detects and
   * closes any <code>JDataStore</code> components when its container is disposed.
   * This property is <code>true</code> by default.</p>
   *
   * @param closeDataStores <code>True</code> to automatically detct and close <code>JDataStore</code> components; <code>false</code> not to.
   * @see #isCloseDataStores
   */
  public void setCloseDataStores(boolean closeDataStores) {
    this.closeDataStores = closeDataStores;
  }

  /**
   * <p>Returns whether <code>DBDisposeMonitor</code> will automatically detect and
   * close any <code>JDataStore</code> components when its container is disposed.
   * This property is <code>true</code> by default.</p>
   *
   * @return <code>True</code> if  <code>JDataStore</code> components are automatically detected and closed; <code>false</code> if not.
   * @see #setCloseDataStores
   */
  public boolean isCloseDataStores() {
    return closeDataStores;
  }

  /**
   * <p>Sets whether <code>DBDisposeMonitor</code> should automatically close any
   * open database connections it finds when its container is disposed.
   * This property is <code>true</code> by default.</p>
   *
   * @param closeConnections <code>True</code> to automatically detct and close any open database connections;  <code>false</code> not to.
   * @see #isCloseConnections
   */
  public void setCloseConnections(boolean closeConnections) {
    this.closeConnections = closeConnections;
  }

  /**
   * <p>Returns whether <code>DBDisposeMonitor</code> should automatically close any
   * open database connections it finds when its container is disposed.
   * This property is <code>true</code> by default.</p>
   *
   * @return <code>True</code> if any open database connections are automatically detected and closed; <code>false</code> if not.
   * @see #setCloseConnections
   */
  public boolean isCloseConnections() {
    return closeConnections;
  }

  /**
   * <p>Sets whether <code>DataSet</code> clean-up should occur on <code>Window.CLOSING</code>
   * events as well as <code>Window.CLOSE</code> events. This property is
   * <code>true</code> by default.</p>
   *
   * @param doOnClosing <code>True</code> if <code>DataSet</code> clean-up should occur on both <code>Window.CLOSING</code> and
   * <code>Window.CLOSE</code> events; <code>false</code> if not.
   */
  public void setExecuteOnWindowClosing(boolean doOnClosing) {
    this.doOnClosing = doOnClosing;
  }

  /**
   * <p>Returns whether <code>DataSet</code> clean-up should occur on <code>Window.CLOSING</code>
   * events as well as <code>Window.CLOSE</code> events.  This property is
   * <code>true</code> by default.</p>
   *
   * @return <code>True</code> if <code>DataSet</code> clean-up should occur on both <code>Window.CLOSING</code> and
   * <code>Window.CLOSE</code> events; <code>false</code> if not.
   */
  public boolean isExecuteOnWindowClosing() {
    return doOnClosing;
  }

  //
  // ComponentListener interface implementation
  //

  public void componentMoved(ComponentEvent e) {}
  public void componentHidden(ComponentEvent e) {}

  public void componentResized(ComponentEvent e) {
    componentShown(e);
  }
  public void componentShown(ComponentEvent e) {
    if (!registeredAsWindowListener) {
      // register ourselves as WindowListener on the window
      Component c = (Component) e.getSource();
      while (c != null &&!(c instanceof Window)) {
        c = c.getParent();
      }
      if (c != null) {
        ((Window) c).addWindowListener(this);
        registeredAsWindowListener = true;
      }
    }
  }


  public void windowOpened(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowActivated(WindowEvent e) {}
  public void windowDeactivated(WindowEvent e) {}

  public void windowClosing(WindowEvent e) {
    if (doOnClosing) {
      doDisposal();
    }
  }

  public void windowClosed(WindowEvent e) {
    doDisposal();
  }

  void doDisposal() {
    if (alreadyCleanedUp) {
      return;
    }

    if (container != null) {
      dataAwareComponents = DBUtilities.findDataAwareChildren(container);
    }

    Method isOpenMethod;
    boolean isOpen;
    Method closeMethod;
    DataSet dataSet = null;
    StorageDataSet storageDataSet = null;
    try {
      for (int index = 0; index < dataAwareComponents.length; index++) {
        if (dataAwareComponents[index] != null) {
          dataSet = dataAwareComponents[index].getDataSet();
          if (dataSet != null) {
            storageDataSet = dataSet.getStorageDataSet();
          }
          // close open datastores
          if (closeDataStores && storageDataSet instanceof StorageDataSet) {
            Object store = storageDataSet.getStore();
            if (store != null && store.getClass().getName().equals("com.borland.datastore.DataStore")) {  
              isOpenMethod = store.getClass().getMethod("isOpen", null);  
              isOpen = ((Boolean) isOpenMethod.invoke(store, null)).booleanValue();
              if (isOpen) {
                closeMethod = store.getClass().getMethod("close", null);   
                closeMethod.invoke(store, null);
              }
            }
          }
          // close open database connections
          if (closeConnections && storageDataSet instanceof QueryDataSet) {
            Database database = ((QueryDataSet) storageDataSet).getDatabase();
            if (database != null && database.isOpen()) {
              database.closeConnection();
            }
          }
          // unbind datasets and databinders
          dataAwareComponents[index].setDataSet(null);
          if (dataAwareComponents[index] instanceof DBTextDataBinder) {
            ((DBTextDataBinder) dataAwareComponents[index]).setJTextComponent(null);
          }
          else if (dataAwareComponents[index] instanceof DBButtonDataBinder) {
            ((DBButtonDataBinder) dataAwareComponents[index]).setAbstractButton(null);
          }
          else if (dataAwareComponents[index] instanceof DBLabelDataBinder) {
            ((DBLabelDataBinder) dataAwareComponents[index]).setJLabel(null);
          }
          else if (dataAwareComponents[index] instanceof DBListDataBinder) {
            ((DBListDataBinder) dataAwareComponents[index]).setJList(null);
          }
          else if (dataAwareComponents[index] instanceof DBSliderDataBinder) {
            ((DBSliderDataBinder) dataAwareComponents[index]).setJSlider(null);
          }
          else if (dataAwareComponents[index] instanceof DBTreeNavBinder) {
            ((DBTreeNavBinder) dataAwareComponents[index]).setJTree(null);
          }
        }
      }
    }
    catch (Exception e) {
      DBExceptionHandler.handleException(dataSet, e);
    }

    alreadyCleanedUp = true;
  }

  /** Container of data-aware components to monitor */
  private Container container;

  /** Whether or not we've already registered ourself as a window listener */
  private boolean registeredAsWindowListener;

  /** array of components which DBDisposeMonitor should deregister */
  private DataSetAware [] dataAwareComponents;

  /** whether or not to close DataStores on WINDOW_DISPOSE events */
  private boolean closeDataStores = true;

  /** whether or not to close database connections on WINDOW_DISPOSE events */
  private boolean closeConnections = true;

  /** whether or not to execute on WINDOW_CLOSING instead of WINDOW_CLOSED */
  private boolean doOnClosing = true;

  private boolean alreadyCleanedUp = false;
}
