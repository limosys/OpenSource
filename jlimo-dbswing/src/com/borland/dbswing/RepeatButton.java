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
import java.beans.*;
import javax.swing.*;

/**
 * <p>A fully-interchangeable subclass of <code>JButton</code>. It adds the following properties.</p>
 * 
 * <ul>
 * <li><code>defaultButton</code> - Makes this button the enclosing
 * window's default button.</li>
 * <li><code>textWithMnemonic</code> - Allows text and a mnemonic
 * character to be set simultaneously from a single string.</li>
 * <li><code>repeat</code> - Allows <code>ActionEvents</code> to be
 * fired repeatedly when the button is held down.</li>
 * <li><code>repeatDelay</code> - The delay in milliseconds between
 * repeated <code>ActionEvents</code>.</li>
 * <li><code>initialDelay</code> - The initial delay in milliseconds
 * before the first repeated <code>ActionEvent</code> occurs.</li>
 * </ul>
 */
public class RepeatButton extends JButton
  implements PropertyChangeListener, FocusListener, ActionListener, java.io.Serializable
{
  /**
   * <p>Constructs a <code>RepeatButton</code> with no specified text
   * or icon. Calls the constructor of <code>this</code> class that
   * takes a <code>String</code> and an <code>Icon</code> as
   * parameters. Passes <code>null</code> and <code>null</code> to
   * the other constructor.</p>
   */
  public RepeatButton() {
    this(null, null);
  }

  /**
   * <p>Constructs a <code>RepeatButton</code> with a specified icon
   * that appears on the button face. Calls the constructor of
   * <code>this</code> class that takes a <code>String</code> and an
   * <code>Icon</code> as parameters. Passes <code>null</code> and
   * <code>icon</code> to the other constructor. </p>
   *
   * @param icon The icon that appears on the button face.
   */
  public RepeatButton(Icon icon) {
    this(null, icon);
  }

  /**
   * <p>Constructs a <code>RepeatButton</code> with a specified text
   * string that appears on the button face. Calls the constructor of
   * <code>this</code> class that takes a <code>String</code> and an
   * <code>Icon</code> as parameters. Passes <code>text</code> and
   * <code>null</code> to the other constructor.</p>
   *
   * @param text The text string that appears on the button.
   */
  public RepeatButton(String text) {
    this(text, null);
  }

  /**
   * <p>Constructs a <code>RepeatButton</code> with a specified text
   * string and icon that appears on the button face. Calls the
   * constructor of its superclass that takes a <code>String</code>
   * and an <code>Icon</code> as parameters.</p>
   *
   * @param text The text string that appears on the button.
   * @param icon The icon that appears on the button face.
   */
  public RepeatButton(String text, Icon icon) {
    super(DBUtilities.excludeMnemonicSymbol(text), icon);
    if (DBUtilities.containsMnemonic(text)) {
      setTextWithMnemonic(text);
    }
  }

  /**
   * <p>Sets whether this button should be its container's default
   * button. Note that there can only be one default button within a
   * window. If more than one button in the same container has this
   * property set to <code>true</code>, the button which was most
   * recently set to be the default button takes precedence. Also,
   * note that default button behavior is look-and-feel dependent.</p>
   *
   * <p>Because default button behavior is implemented by a
   * <code>JRootPane</code> object, this property is useful only when
   * a <code>RepeatButton</code> is placed within a Swing container
   * with a <code>JRootPane</code> (for example, <code>JFrame</code>). </p>
   *
   * <p><code>defaultButton</code> is a bound property, and therefore
   * fires a property change event when modified. </p>
   *
   * <p>Setting this property to <code>true</code> has the side
   * effect of setting the <code>defaultCapable</code> property to
   * <code>true</code> if it is <code>false</code>.</p>
   *
   * <p>Note that the while the setter or write access method for
   * <code>defaultButton</code> is in <code>RepeatButton</code>, the
   * getter or read access method is in its parent, <code>JButton</code>; 
   * <code>defaultButton</code> is both a read and write property.</p>
   *
   * @param defaultButton If <code>true</code>, this button is its container's default button
   * @see javax.swing.JButton#isDefaultButton
   * @see javax.swing.JRootPane#setDefaultButton
   */
  public void setDefaultButton(boolean defaultButton) {
    boolean oldDefaultButton = this.defaultButton;
    if (oldDefaultButton != defaultButton) {
      this.defaultButton = defaultButton;
      makeDefaultButton();
      firePropertyChange("defaultButton", oldDefaultButton, defaultButton);  
    }
  }

  /**
   * <p>Sets this button as the default button when the button's peer 
   * is created.</p>
   */
  public void addNotify() {
    super.addNotify();
    makeDefaultButton();
  }

  /**
   * Sets this button as the enclosing <code>JRootPane's default</code> button.
   * Does nothing if this button is not enclosed within a <code>JRootPane.</code>
   */
  private void makeDefaultButton() {

    JRootPane rootPane = SwingUtilities.getRootPane(this);
    // the very first time we're set as defaultButton,
    // then we have to register ourself as a listener
    if (rootPane != null && defaultButton && !alreadyRootPaneListener) {
      alreadyRootPaneListener = true;
      rootPane.addPropertyChangeListener(this);
    }
    if (alreadyRootPaneListener) {
      if (defaultButton) {
        if (!isDefaultCapable()) {
          setDefaultCapable(true);
        }
        rootPane.setDefaultButton(this);
      }
      else {
        if (rootPane.getDefaultButton() == this) {
          rootPane.setDefaultButton(null);
        }
      }
    }
    if (!defaultButton && alreadyRootPaneListener) {
      // need to remove ourself as focus listener
      if (focusEventSource != null) {
        focusEventSource.removeFocusListener(this);
        focusEventSource = null;
      }
      alreadyRootPaneListener = false;
      rootPane.removePropertyChangeListener(this);
    }
  }

  //
  // FocusListener implementation (used to support defaultButton behavior)
  //

  public void focusGained(FocusEvent e) {}

  // focusLost event on previous defaultButton.
  // If it lost focus but the JRootPane
  // hasn't set a new defaultButton, then
  // we should set ourselves to be the default
  // button if we are supposed to be it.
  public void focusLost(FocusEvent e) {
    makeDefaultButton();
  }

  //
  // PropertyChangeListener implementation (used to support defaultButton behavior)
  //

  // propertyChange should only be registered if we're the default button
  public void propertyChange(PropertyChangeEvent e) {
    String prop = e.getPropertyName();
    if (prop.equals("defaultButton")) {            
      JButton newDefaultButton = (JButton) e.getNewValue();
      JButton oldDefaultButton = (JButton) e.getOldValue();
      if (oldDefaultButton != null) {
        oldDefaultButton.removeFocusListener(this);
      }
      if (newDefaultButton != null) {
        newDefaultButton.addFocusListener(this);
        focusEventSource = newDefaultButton;
      }
    }
  }


 /**
  * <p>Sets the text with an embedded mnemonic character.
  * <code>textWithMnemonic</code> is a convenience
  * property for setting the button's text, which
  * interprets an ampersand character (&) within the
  * text as an instruction to make the character
  * following the ampersand the mnemonic character for
  * the button. To put an ampersand in the text but not
  * make the character following it a hot key, put a back
  * slash before the ampersand. To make the ampersand the
  * hot key, put two consecutive ampersands in the text. </p>
  *
  * <p>This property can be used instead of the usual <code>text</code>
  * property, even if a mnemonic character is not
  * embedded in the text. It is particularly useful for
  * applications that resource strings for 
  * internationalization, because the text and mnemonic
  * can be specified in a single string. </p>
  * 
  * <p>Note that the first occurrence of the mnemonic
  * character is always denoted visibly as the mnemonic
  * key, despite the location of the ampersand within the
  * text. Furthermore, only the first occurrence of an
  * ampersand is removed from the text. </p>
  * 
  * <p>If both the <code>text</code> and <code>textWithMneumonic</code> properties are
  * set, the most recently set property takes
  * precedence. </p>
  * 
  * <p><code>textWithMnemonic</code> is a bound property, and therefore a
  * property change event is fired when its value
  * is modified. </p>
  * 
  * @param text The text to set with an embedded mnemonic character.
  * @see #getTextWithMnemonic
  */
  public void setTextWithMnemonic(String text) {
    String oldText = textWithMnemonic;
    if (oldText != text) {
      textWithMnemonic = text;
      setText(DBUtilities.excludeMnemonicSymbol(text));
      setMnemonic(DBUtilities.extractMnemonicChar(text));
      firePropertyChange("textWithMnemonic", oldText, text);    
    }
  }

  /**
   * <p>Returns the text with the embedded mnemonic character.</p>
   *
  * @return The text with with an embedded mnemonic character.
   * @see #setTextWithMnemonic
   */
  public String getTextWithMnemonic() {
    return textWithMnemonic;
  }

  /**
   * <p>Sets whether <code>ActionEvents</code> are fired
   * repeatedly when the button is held down. This property
   * is <code>false</code> by default. <code>repeat</code>
   * is a bound property, and therefore it fires a property
   * change event when its value is modified.</p>
   *
   * @param repeat If <code>true</code>, <code>ActionEvents</code> are fired repeatedly when the button is held down.
   * @see #isRepeat
   * @see #setRepeatDelay
   */
  public void setRepeat(boolean repeat) {
    boolean oldRepeat = this.repeat;
    if (oldRepeat != repeat) {
      this.repeat = repeat;
      if (repeatTimer != null) {
        repeatTimer.stop();
      }
      firePropertyChange("repeat", oldRepeat, repeat);  
    }
  }

  /**
   * <p>Returns whether <code>ActionEvents</code> are fired
   * repeatedly when the button is held down.</p>
   *
   * @return If <code>true</code>, <code>ActionEvents</code> are fired repeatedly when the button is held down.
   * @see #setRepeat
   * @see #getRepeatDelay
   */
  public boolean isRepeat() {
    return repeat;
  }

  /**
   * <p>Sets the interval, in milliseconds, at which
   * repeated <code>ActionEvents</code> are fired. The
   * default interval is 200 milliseconds.
   * <code>repeatDelay</code> is a bound property, and
   * therefore it fires a property change event when its
   * value is modified.</p>
   *
   * <p>To have <code>repeatDelay</code> take effect, the
   * <code>repeat</code> property value must be
   * <code>true</code>.</p>
   *
   * @param repeatDelay The interval, in milliseconds, at which repeated <code>ActionEvents</code> are fired.
   * @see #getRepeatDelay
   * @see #setRepeat
   */
  public void setRepeatDelay(int repeatDelay) {
    int oldDelay = this.repeatDelay;
    this.repeatDelay = repeatDelay;
    if (repeatTimer != null) {
      repeatTimer.setDelay(repeatDelay);
    }
    firePropertyChange("repeatDelay", oldDelay, repeatDelay);  
  }

  /**
   * <p>Returns the interval, in milliseconds, at which
   * repeated <code>ActionEvents</code> are fired. The
   * default interval is 200 milliseconds.</p>
   *
   * @return The interval, in milliseconds, at which repeated <code>ActionEvents</code> are fired.
   * @see #setRepeatDelay
   * @see #isRepeat
   */
  public int getRepeatDelay() {
    return repeatDelay;
  }

  /**
   * <p>Sets the interval, in milliseconds, after which
   * repeated <code>ActionEvents</code> are fired. The
   * default initial delay is 400 milliseconds.
   * <code>initialDelay</code> is a bound property, and
   * therefore it fires a property change event when its
   * value is modified.</p>
   *
   * <p>To have <code>initialDelay</code> take effect, the
   * <code>repeat</code> property value must be
   * <code>true</code>.
   *
   * @param initialDelay The interval, in milliseconds, after which repeated <code>ActionEvents</code> are fired.
   * @see #getInitialDelay
   * @see #setRepeat
   * @see #setRepeatDelay
   */
  public void setInitialDelay(int initialDelay) {
    int oldDelay = this.initialDelay;
    this.initialDelay = initialDelay;
    if (repeatTimer != null) {
      repeatTimer.setInitialDelay(initialDelay);
    }
    firePropertyChange("initialDelay", oldDelay, initialDelay);  
  }

  /**
   * Returns the interval, in milliseconds, at which repeated
   * ActionEvents are fired.
   *
   * @see setInitialDelay
   */

  /**
   * <p>Returns the interval, in milliseconds, after which
   * repeated <code>ActionEvents</code> are fired. The
   * default initial delay is 400 milliseconds.</p>
   *
   * @return The interval, in milliseconds, after which repeated <code>ActionEvents</code> are fired.
   * @see #setInitialDelay
   * @see #isRepeat
   * @see #getRepeatDelay
   */
  public int getInitialDelay() {
    return initialDelay;
  }

  // intercept mouse events to manage repeat ActionEvent timer
  protected void processMouseEvent(MouseEvent e) {
    if (repeat) {
      if (repeatTimer == null) {
        repeatTimer = new Timer(repeatDelay, this);
        repeatTimer.setInitialDelay(initialDelay);
      }
      int id = e.getID();
      switch(id) {
      case MouseEvent.MOUSE_PRESSED:
        mouseOverButton = true;
        repeatTimer.stop();
        repeatTimer.start();
        break;
      case MouseEvent.MOUSE_RELEASED:
        repeatTimer.stop();
        break;
      case MouseEvent.MOUSE_ENTERED:
        mouseOverButton = true;
        break;
      case MouseEvent.MOUSE_EXITED:
        mouseOverButton = false;
        break;
      }
    }
    super.processMouseEvent(e);
  }

  // Implementation of ActionListener interface
  // Invoked by ActionEvent delay timer.  Propogates
  // ActionEvents to this button's ActionEvent listeners.
  public void actionPerformed(ActionEvent e) {
    if (mouseOverButton) {
      fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                          getActionCommand()));
    }
  }

  /** whether or not this button should be the default button for its container */
  private boolean defaultButton;

  /** holds text property value with embedded mnemonic character */
  private String textWithMnemonic;

  /** whether or not we've already added ourself as a rootpane property change listener */
  private boolean alreadyRootPaneListener = false;

  /** holds the button of which we are a FocusListener */
  private JButton focusEventSource = null;

  /** whether or not repeated ActionEvents should be fired when button is held down */
  private boolean repeat = true;

  /** approx. delay in milliseconds between repeated ActionEvents */
  private int repeatDelay = 200;

  /** approx. initial delay in milliseconds before repeated ActionEvents */
  private int initialDelay = 400;

  /** Timer used to fire repeated ActionEvents */
  private Timer repeatTimer;

  /** whether or not the mouse is within the RepeatButton */
  private boolean mouseOverButton = true;
}
