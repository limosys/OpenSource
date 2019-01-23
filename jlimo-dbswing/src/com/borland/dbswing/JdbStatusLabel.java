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

import java.awt.Component;
import javax.swing.*;

import com.borland.dx.dataset.*;
import com.borland.jb.util.EventMulticaster;

/**
 * <p>A visible, non-editable component that displays
 * <code>DataSet</code> status and validation messages,
 * such as &quot;Record 1 of 20&quot; or &quot;Value
 * entered is greater than the maximum allowed&quot;.  A
 * single <code>JdbStatusLabel</code> can be used to
 * display messages from several <code>DataSet</code>s
 * (though only one at any given time).</p>
 *
 * <p>There are three ways to set up a
 * <code>JdbStatusLabel</code> for use in your  application:</p>
 *
 * <ul>
 * <li><p>Place the <code>JdbStatusLabel</code> as a visual
 * component in a frame.  By default,
 * <code>JdbStatusLabel</code> identifies other data-aware
 * components in the same root container (for example,
 * <code>JFrame</code>), and displays messages generated
 * by the <code>DataSet</code> of the component that
 * currently has focus. This behavior is controlled by the
 * <code>autoDetect</code> property.</p>
 *
 * <p>Note that <code>autoDetect</code> doesn't detect
 * components added after <code>JdbStatusLabel</code> has
 * been realized. If your application adds data-aware
 * components dynamically, and you want
 * <code>JdbStatusLabel</code> to display messages from
 * those newly added components, you must set
 * <code>autoDetect</code> to <code>true</code> after
 * adding a new component to your container.</p></li>
 *
 * <li><p>Set the <code>dataSet</code> property to a
 * specific <code>DataSet</code> if you always want
 * <code>JdbStatusLabel</code> to be associated with that
 * <code>DataSet</code> only.</p></li>
 *
 * <p>If <code>autoDetect</code> mode is used and there is
 * only one <code>DataSet</code> detected,
 * <code>JdbStatusLabel</code> automatically attaches
 * itself to that <code>DataSet</code>. Otherwise, to avoid
 * any ambiguity, <code>JdbStatusLabel</code> waits until a
 * component associated with a <code>DataSet</code> is
 * focused, and then attaches itself to that
 * <code>DataSet</code>. In this case, the initial message
 * displayed by <code>JdbStatusLabel</code> can be
 * configured in one of two ways:</p>
 *
 * <ul>
 * <li><p>An initial message may be explicitly specified by
 * setting the <code>text</code> property.  This message
 * will be overwritten by the next <code>DataSet</code>
 *  status message.</p></li>
 *
 * <li><p>The <code>focusedDataSet</code> property can be
 * set to a <code>DataSet</code> whose status should be
 * displayed, provided the <code>text</code> property is
 * blank (&quot;&quot;).  If neither the <code>text</code>
 * property nor the <code>focusedDataSet</code> property
 * are set, initially <code>JdbStatusLabel</code> displays
 * a blank message.</p></li>
 * </ul>
 *
 * <p><code>JdbStatusLabel</code> updates its current
 * <code>DataSet</code> message source when
 * <code>focusGained</code> events occur. Therefore, when
 * focus moves from a data-aware component to a
 * non-data-aware component, the message area is not
 * cleared.</p>
 *
 * <p>The <code>displayMessages</code> property controls
 * whether messages caused by <code>DataSet</code> actions
 * are displayed by <code>JdbStatusLabel</code>. Setting
 * this property to <code>false</code> allows background
 * changes to be made to <code>DataSet</code>s without the
 * corresponding status messages being displayed.</p>
 *
 * <a name="setting_initial_size"></a>
 * <h3>Setting the initial size of JdbStatusLabel</h3>
 *
 * <p>Depending upon the layout you use, you might need to
 * set <code>JdbStatusLabel</code>'s
 * <code>preferredSize</code> property to have it appear
 * correctly when it is initially displayed (especially if
 * it has blank text).</p>
 *
 * <a name="controlling_duration"></a>
 * <h3>Controlling the duration user-defined text remains</h3>
 *
 * <p>You can use the <code>setText()</code> method to
display a message at any time in
 *  <code>JdbStatusLabel</code>'s text area. The message is
 * overwritten, however, the next time a
 * <code>DataSet</code> status event occurs. To prevent the
 * message from being overwritten, set the
 * <code>displayMessages</code> property to
 * <code>false</code> until you want the message to be
 * overwritten.</p>
 *
 * <a name="refreshing_message"></a>
 * <h3>Refreshing the most recently displayed status message</h3>
 *
 * <p>Call <code>repaint()</code> to refresh the most
 *  recently displayed status message.</p>
 *
 * @see DBEventMonitor
 */
public class JdbStatusLabel
     extends JLabel
  implements NavigationListener, StatusListener, AccessListener,
             DataChangeListener, java.io.Serializable
{

/**
 * <p>Constructs a <code>JdbStatusLabel</code> component by
 * calling the <code>null</code> constructor of its
 * superclass.</p>
 */
  public JdbStatusLabel() {
    super();
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    setOpaque(true);
  }

  public void addNotify() {
    super.addNotify();
    if (getText().equals("")) {  
      updateCurrentMessage(StatusLabelEvent.INIT);
    }
    if (autoDetect) {
      DataSetAware [] awares = DBUtilities.findDataAwareComponents(this);
      if (awares.length > 0) {
        setFocusedDataSet(awares[0].getDataSet());
      }
    }
  }

  /**
   * <p>Sets whether <code>JdbStatusLabel</code>
   * automatically attaches itself to <code>DataSet</code>s
   * within its enclosing highest-level container (usually
   * a <code>JFrame</code>). The default value is
   * <code>true</code>, but the value changes to
   * <code>false</code> if you set the <code>dataSet</code> property.  </p>
   *
   * <p>Auto-detection occurs when the
   * <code>JdbStatusLabel</code> is realized.</p>
   *
   * <p>Setting <code>autoDetect</code> to <code>true</code>, clears
   * the <code>dataSet</code> property value.</p>
   *
   * @param autoDetect If <code>true</code>, <code>JdbStatusLabel</code>
   * automatically attaches itself to <code>DataSet</code>s
   * within its enclosing highest-level container.
   * @see #isAutoDetect
   */
  public void setAutoDetect(boolean autoDetect) {
    boolean oldAutoDetect = this.autoDetect;
    firePropertyChange("autoDetect", oldAutoDetect, autoDetect); 
  }

  /**
   * <p>Returns whether  <code>JdbStatusLabel</code> automatically attaches
   * itself to <code>DataSet</code>s within its enclosing
   * highest-level container (usually a
   * <code>JFrame</code>). </p>
   *
   * @return  If <code>true</code>, <code>JdbStatusLabel</code>
   * automatically attaches itself to <code>DataSet</code>s
   * within its enclosing highest-level container.

   * @see #setAutoDetect
   */
  public boolean isAutoDetect() {
    return autoDetect;
  }

  /**
   * <p>Sets the list of data-aware components to which
   * <code>JdbStatusLabel</code> listens for
   * <code>DataSet</code> status messages. Setting this
   * property to a non-null value disables the
   * <code>autoDetect</code> property, and specifying a
   * <code>null</code> value automatically enables the
   * <code>autoDetect</code> property. </p>
   *
   * <p>When both the <code>dataSet</code> and
   * <code>dataSetAwareComponents</code> properties are
   * set, the one set most recently is the one that takes
   * precedence and the setting of the other one is
   * cleared.</p>
   *
   * @param dataAwareComponents The list of data-aware components to which
   * <code>JdbStatusLabel</code> listens for  <code>DataSet</code> status messages.
   * @see #getDataSetAwareComponents
   * @see #setAutoDetect
   * @see #getDataSet
   */
  public void setDataSetAwareComponents(Component [] dataAwareComponents) {
    userSetDataAwareComponents = dataAwareComponents;
    autoDetect = (dataAwareComponents == null);
    dataSet = null;
  }

  /**
   * <p>This method is deprecated and always returns <code>null</code>.</p>
   *
   * @return null
   * @deprecated
   */
  public Component [] getDataSetAwareComponents() {
    return userSetDataAwareComponents;
  }

  /**
   * <p>Sets the <code>DataSet</code> that is the source of
   * the status messages displayed by this component.
   * Setting this property to a non-null value disables the
   * <code>autoDetect</code> property, and specifying a
   * <code>null</code> value automatically enables the
   * <code>autoDetect</code> property. </p>
   *
   * <p>When both the <code>dataSet</code> and
   * <code>dataSetAwareComponents</code> properties are
   * set, the one set most recently is the one that takes
   * precedence and the setting of the other one is cleared.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   * @see #setAutoDetect
   */
  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
    autoDetect = (dataSet == null);
    userSetDataAwareComponents = null;
    updateCurrentDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> that is the source of
   * the status messages displayed by this component.</p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * <p>Sets the <code>DataSet</code> from
   * which the current status text is obtained. This
   * property can also be used to specify which
   * <code>DataSet</code>'s status should be displayed
   * initially. To do so, set this property to that
   * <code>DataSet</code>, and leave the <code>text</code>
   * property blank (&quot;&quot;).</p>
   *
   * <p>Messages from the specified <code>DataSet</code>
   * continue to be displayed until another data-aware
   * component receives focus, at which time messages from
   * its <code>DataSet</code> begin displaying.</p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param dataSet The <code>DataSet</code>from which the current status text is obtained.
   * @see #getFocusedDataSet
   */

  public void setFocusedDataSet(DataSet dataSet) {
    updateCurrentDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which the current status text is obtained.</p>
   *
   * @return The <code>DataSet</code>from which the current status text is obtained.
   * @see #setFocusedDataSet
   */

  public DataSet getFocusedDataSet() {
    return currentDataSet;
  }

  /**
   * <p>Sets whether messages are displayed.
   * This property can be set to <code>false</code> to hide
   * messages from the user. This is most useful when you
   * want to temporarily hide messages from the user, or
   * want to display a message of your own without having
   * it overwritten by <code>DataSet</code> status
   * messages. Set <code>displayMessages</code> back to
   * <code>true</code> when you no longer need to hide the
   * status messages.</p>
   *
   * @param displayMessages If <code>false</code>, messages  are hidden from the user.
   * @see #isDisplayMessages
   */
  public void setDisplayMessages(boolean displayMessages) {
    this.displayMessages = displayMessages;
  }

  /**
   * <p>Returns whether or not messages are displayed.</p>
   *
   * @return If <code>false</code>, messages  are hidden from the user.
   * @see #setDisplayMessages
   */
  public boolean isDisplayMessages() {
    return displayMessages;
  }

  /**
   * <p>Adds a listener for <code>StatusLabelEvent</code> dispatches.</p>
   * @param listener The listener.
   * @see #removeStatusLabelListener
   */
  public final void addStatusLabelListener(StatusLabelListener listener) {
    statusListeners = EventMulticaster.add(statusListeners, listener);
  }

  /**
   * <p>Removes a listener for <code>StatusLabelEvent</code> dispatches.</p>
   * @param listener The listener.
   * @see #addStatusLabelListener
   */
  public final void removeStatusLabelListener(StatusLabelListener listener) {
    statusListeners = EventMulticaster.remove(statusListeners, listener);
  }


  void updateCurrentDataSet(DataSet dataSet) {
    if (dataSet != currentDataSet) {
      // Remove ourself as a listener from the previous dataSet;
      if (currentDataSet != null) {
        currentDataSet.removeStatusListener(this);
        currentDataSet.removeAccessListener(this);
        currentDataSet.removeNavigationListener(this);
        currentDataSet.removeDataChangeListener(this);
      }
      if (dataSet != null) {
        // Add ourself as a listener to this event source's DataSet
        dataSet.addStatusListener(this);
        dataSet.addAccessListener(this);
        dataSet.addNavigationListener(this);
        dataSet.addDataChangeListener(this);
      }
      currentDataSet = dataSet;
      updateCurrentMessage(StatusLabelEvent.DATASET_CHANGED);
    }
  }

  //
  // StatusListener implementation
  //
  public void statusMessage(StatusEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
//    currentMessage = event.getMessage();
//    repaint();
    if (displayMessages) {
      int code = StatusLabelEvent.STATUS_EVENT;
      Object message = event;
      if (event.getCode() == StatusEvent.EXCEPTION) {
        code = StatusLabelEvent.EXCEPTION;
        message = event.getException();
      }
      if (fireStatusLabelEvent(code, message)) {
        setText(event.getMessage());
      }
    }
  }

  boolean fireStatusLabelEvent(int code, Object message) {
    boolean messageAccepted = true;
    if (statusListeners != null) {
      messageAccepted = statusListeners.vetoableDispatch(new StatusLabelEvent(this, code, message));
    }
    return messageAccepted;
  }

  //
  // DataChangeListener implementation
  //
  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    if (event.getID() == DataChangeEvent.ROW_CHANGED) {
      if (displayMessages && fireStatusLabelEvent(StatusLabelEvent.DATASET_ROW_CHANGED, " ")) {  
        DBUtilities.invokeOnSwingThread(new Runnable() {
          public void run() {
            setText(" "); 
          }
        });
      }
    }
  }

  public void postRow(DataChangeEvent event) {
  }

  //
  // AccessListener implementation
  //
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      updateCurrentMessage(StatusLabelEvent.DATASET_OPENED);
    }
    else {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
        return;
      }
      if (displayMessages && fireStatusLabelEvent(StatusLabelEvent.DATASET_CLOSED, " ")) {  
        setText(" ");   
      }
    }
  }

  //
  // NavigationListener implementation
  //
  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    updateCurrentMessage(StatusLabelEvent.DATASET_NAVIGATED);
  }

  private void updateCurrentMessage(int statusLabelEvent) {
    if (currentDataSet != null && currentDataSet.isOpen()) {
      try {
        if (currentDataSet.getRowCount() > 0) {
          currentMessage = java.text.MessageFormat.format(Res._RecordId,     
	             new String[] {Integer.toString(currentDataSet.getRow() + 1),
		                   Integer.toString(currentDataSet.getRowCount())});
        }
        else {
          currentMessage = " "; 
        }
      }
      catch (Exception e) {
        DBExceptionHandler.handleException(currentDataSet, e);
      }
    }
    else {
      currentMessage = " ";   
    }
    if (displayMessages && fireStatusLabelEvent(statusLabelEvent, currentMessage)) {  
      setText(currentMessage);
    }
  }

  /** <p>Array of components to which this component has registered for.</p>
      Focus events */
  private Component [] dataAwareComponents;

  /** <p>User-specified array of data-aware components with which to
      register for Focus events.</p> */
  private Component [] userSetDataAwareComponents;

  /** <p>Single <code>dataSet</code> to which <code>JdbStatusLabel</code> listens for status messages.</p> */
  private DataSet dataSet;

  /** <p>Whether or not to automatically detect and register self on
      <code>DataSets</code> in the same container.</p> */
  private boolean autoDetect = true;

  /** <p><code>DataSet</code> whose current status is displayed.</p> */
  private DataSet currentDataSet = null;

  /** <p>Current status message.</p> */
  private String currentMessage = " ";   

  /** <p>Whether or not <code>DataSet</code> status messages should be displayed.</p> */
  private boolean displayMessages = true;

  private transient EventMulticaster      statusListeners;

  private boolean registeredWithRuntime = false;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
