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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import com.borland.dx.dataset.*;

//Old Javadoc comments start here
// JdbNavToolBar is a JToolBar designed specifically for use with
// dbSwing data-aware components.  By default, a JdbNavToolBar
// contains the following 11 buttons for performing common actions
// such as navigating, editing, refreshing, and saving DataSet data.
//<p>
//<TABLE BORDER>
//<TR><TH>Button </TH><TH>Description                      </TH><TH>DataSet method </TH></TR>
//<TR><TD>First  </TD><TD>navigates to first row of DataSet</TD><TD>first()        </TD></TR>
//<TR><TD>Prior  </TD><TD>navigates to previous DataSet row</TD><TD>prior()        </TD></TR>
//<TR><TD>Next   </TD><TD>navigates to next DataSet row    </TD><TD>next()         </TD></TR>
//<TR><TD>Last   </TD><TD>navigates to last row of DataSet </TD><TD>last()         </TD></TR>
//<TR><TD>Insert </TD><TD>inserts new row at current
//                        DataSet row position             </TD><TD>insertRow(true)</TD></TR>
//<TR><TD>Delete </TD><TD>deletes current DataSet row      </TD><TD>deleteRow()    </TD></TR>
//<TR><TD>Post   </TD><TD>commits changes to current
//                        DataSet row                      </TD><TD>post()         </TD></TR>
//<TR><TD>Cancel </TD><TD>cancels changes to current
//                        DataSet row                      </TD><TD>cancel()       </TD></TR>
//<TR><TD>Ditto  </TD><TD>copies previous row contents to
//                        current, newly inserted row      </TD><TD>dittoRow(false)</TD></TR>
//<TR><TD>Save   </TD><TD>saves changes to DataSet         </TD><TD>saveChanges()  </TD></TR>
//<TR><TD>Refresh</TD><TD>refreshes contents of DataSet    </TD><TD>refresh()      </TD></TR>
//</TABLE>
//<p>
// A single JdbNavToolBar can be used to navigate multiple DataSets
// (though only one at any given time).  By default, buttons on the
// toolbar are enabled/disabled appropriately for the currently
// focused data-aware component (see updateButtonEnabledState() for
// details).  You can explicitly set the state of each button by
// setting its corresponding 'buttonState' property
// (e.g. the <I>buttonStateInsert</I> property for the Insert button).
// Valid button states are:
//<p>
//<UL>
//<LI>AUTO_ENABLED (the default value)
// <p>button is automatically enabled or disabled according to the state
//    of the current DataSet
//<LI>AUTO_HIDDEN
// <p>button is automatically hidden when disabled according to the state
//    of the current DataSet
//<LI>ENABLED
// <p>button is always enabled
//<LI>DISABLED
// <p>button is always disabled
//<LI>HIDDEN
// <p>button is always hidden
//</UL>
//<p>
// JdbNavToolBar determines the DataSet it should navigate
// based upon the data-aware component which most recently had
// focus.  If focus moves from a data-aware component to a
// non-data-aware component, JdbNavToolBar's current DataSet
// will be the DataSet associated with the most recently focused
// data-aware component.
//<p>
// There are three ways to set up a JdbNavToolBar for use in your
// application.  The simplest, and recommended way, is to place the
// JdbNavToolBar as a visual component in a frame.  By default,
// JdbNavToolBar will automatically detect other data-aware components
// in the same root container (e.g. JFrame), and navigate the DataSet
// of the component which currently has focus.  Alternatively, a
// JdbNavToolBar can be explicitly associated with a single DataSet by
// setting its <I>dataSet</I> property.
//<p>
// Normally, JdbNavToolBar is initially disabled until a data-aware
// component gains focus.  However, you can explicitly set the
// initially focused DataSet by setting the <I>focusedDataSet</I>
// property.  In the special case where only a single DataSet is
// auto-detected, that DataSet becomes the initially focused DataSet.
//<p>
// Frequently asked questions:
//<UL>
//<LI>Hiding a button on the toolbar
//<p>
// To hide a button on the toolbar, set its corresponding 'buttonState'
// property (e.g. the <I>buttonStateRefresh</I> property for the Refresh
// button) to JdbNavToolBar.HIDDEN.  Doing so will hide the button
// unconditionally, regardless of the currently focused DataSet.
// To conditionally hide a button, see the "Customizing button state
// for a particular DataSet" discussion below.
//<p>
//<LI>Customizing button state for a particular DataSet
//<p>
// To enable, disable, or hide buttons depending upon the DataSet
// currently associated with the JdbNavToolBar, add a
// java.beans.PropertyChangeListener to JdbNavToolBar.  When the
// <I>focusedDataSet</I> property change event occurs and its new value is
// the desired DataSet, set the buttonState properties
// (e.g. buttonStateSave) on the JdbNavToolBar.  Note, however, that
// once a button's corresponding 'buttonState' property has been
// set, it must be explicitly restored to its default state
// (AUTO_ENABLED) to restore automatic enable/disable behavior.
//<p>
//<LI>Changing the button layout
//<p>
// By default, JdbNavToolBar uses a special FlowLayout with FlowLayout.CENTER
// alignment to layout its toolbar buttons.  You can change the
// FlowLayout alignment by setting the <I>alignment</I> property.  It is
// also possible to change the layout to something other than FlowLayout.
// Note, however, that doing so may limit buttonState functionality.
// For example, not all layout managers support non-visible components.
// In such a layout, a button with a HIDDEN buttonState may be
// invisible but still take up space on the toolbar.  If the button is
// intended to always be hidden, however, it can simply be removed
// from the layout.
//<p>
//<LI>Customizing a button's text, icon, or tooltip
//<p>
// The buttons on the toolbar are obtainable via getter methods
// which allows you to customize their text, icon, or tooltip.
// You can also use theis reference to a button to remove it
// from the toolbar if necessary.  Note that all buttons except
// for the priorButton and nextButton are JButtons.  priorButton
// and nextButton are RepeatButtons to facilitate 'repeat' capability
// when the button is held down by the mouse.  To disable or
// customize the default 'repeat' behavior, cast the
// reference to the priorButton or nextButton to a RepeatButton,
// and set its 'repeat'-related properties.
//<p>
//<LI>Adding custom buttons to the toolbar
//<p>
// You can add your own buttons to the toolbar by using the add()
// method.  Note that JdbNavToolBar will not handle such a button's
// events or alter its state in any way.  See "Customizing button
// state for a particular DataSet" above for info on how to
// customize buttons for individual DataSets.
//<p>
//<LI>Changing the default button behavior
//<p>
// To change the default behavior of a toolbar button, you can
// either extend the JdbNavToolBar class and override the
// actionPerformed() method, or get the public reference to
// the button whose behavior you wish to change, and remove
// JdbNavToolBar as an ActionListener on the button.  You can
// then add your own ActionListener to the button, use
// getFocusedDataSet() to get the current DataSet, and execute
// your custom action on the currently focused DataSet.
//<p>
// Old Javadoc comments end here, new ones follow

 /**
  * <p>A <code>JToolBar</code> designed specifically for
  * use with dbSwing data-aware components.
  * <code>JdbNavToolBar</code> uses <a  href="ToolBarLayout.html"><code>ToolBarLayout</code></a>
  * as its default layout manager, and its orientation
  * is horizontal by default.</p>
  *
  * <p>By default, a <code>JdbNavToolbar</code> contains
  * the following 11 buttons for performing common
  * actions such as navigating, editing, refreshing, and
  * saving <code>DataSet</code> data:</p>
  *
  * <p><strong>JdbNavToolbar default buttons</strong></p>
  *
  * <table cellspacing="2" cellpadding="2" border="2" frame="box">
  * <tr>
  *     <th align="LEFT">Button</th>
  * <th align="LEFT">Description</th>
  * </tr>
  *
  * <tr>
  *     <td>First</td>
      * <td>Navigates to the first row of the <code>DataSet</code>. Calls <code>DataSet.first()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Prior</td>
 *      <td>Navigates to the previous  <code>DataSet</code> row. Calls <code>DataSet.prior()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Next</td>
  *     <td>Navigates to the next <code>DataSet</code> row. Calls <code>DataSet.next()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Last</td>
  *     <td>Navigates to the last row of the <code>DataSet</code>. Calls <code>DataSet.last()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Insert</td>
  *     <td>Inserts a new row at the current <code>DataSet</code> row position. Calls <code>DataSet.insertRow(true)</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Delete</td>
  *     <td>Deletes the current <code>DataSet</code> row. Calls <code>DataSet.deleteRow()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Post</td>
  *     <td>Commits changes to the current <code>DataSet</code> row. Calls <code>DataSet.post()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Cancel</td>
  *     <td>Cancels changes being made to the current <code>DataSet</code> row. Calls <code>DataSet.cancel()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Ditto</td>
  *     <td>Copies the previous row's contents to the current, newly inserted row. Calls <code>DataSet.dittoRow(false)</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Save</td>
  *     <td>Saves all changes to the <code>DataSet</code>. Calls <code>DataSet.saveChanges()</code> method.</td>
  * </tr>
  *
  * <tr>
  *     <td>Refresh</td>
  *     <td>Refreshes the contents of the <code>DataSet</code>. Calls <code>DataSet.refresh()</code> method.</td>
  * </tr>
  * </table>
  *
  * <p><code>JdbNavToolBar</code> determines the
  * <code>DataSet</code> it should navigate based upon
  * the data-aware component that had focus most
  * recently. If focus moves from a data-aware component
  * to a component that is not data aware,
  * <code>JdbNavToolBar</code>'s current
  * <code>DataSet</code> is the <code>DataSet</code>
  * associated with the most recently focused data-aware
  * component.</p>
  *
  * <p>There are three ways to set up a
  * <code>JdbNavToolBar</code> for use in your
  * application:</p>
  *
  * <ul>
  * <li><p>Place the <code>JdbNavToolBar</code> as a
  * visual component in a frame. By default,
  * <code>JdbNavToolBar</code> automatically detects
  * other data-aware components in the same root
  * container (such as <code>JFrame</code>), and
  * navigates the <code>DataSet</code> of the component
  * that currently has focus. This behavior is
  * controlled by the <code>autoDetect</code>
  * property.</p>
  *
  * <p>Note that <code>autoDetect</code> doesn't detect
  * components added after <code>JdbNavToolBar</code>
  * has been realized. If your application adds
  * data-aware components dynamically, and you want
  * <code>JdbNavToolBar</code> to navigate the
  * <code>DataSet</code>s of those newly added
  * components, you must set <code>autoDetect</code> to
  * <code>true</code> after adding a new component to
  * your container.</p></li>
  *
  * <li><p>Explicitly set the <code>dataSet</code>
  * property of <code>JdbNavToolBar</code>, associating
  * it with a single <code>DataSet</code>.</p></li>
  *
  * </ul>
  *
  * <p>Note that setting <code>autoDetect</code> to
  * <code>true</code> clears the
  * <code>dataSet</code> property value. Also, when
  * the <code>dataSet</code> and
  * property is the <code>autoDetect</code> property is set to
  * <code>false</code>.</p>
  *
  * <p>Usually a <code>JdbNavToolBar</code> is initially
  * disabled until a data-aware component gains focus.
  * You can explicitly set the initially focused
  * <code>DataSet</code>, however, by setting the
  * <code>focusedDataSet</code> property. In the special
  * case where only a single <code>DataSet</code> is
  * auto-detected, that <code>DataSet</code> becomes the
  * initially focused <code>DataSet</code>.</p>
  *
  *
  * <a name="hiding_a_button"></a
  * <h3>Hiding a button on the toolbar</h3>
  *
  * <p>To hide a button on the toolbar, set its
  * corresponding <code>buttonState</code> property
  * (for example, the <code>buttonStateRefresh</code>
  * property for the Refresh button) to
  * <code>JdbNavToolBar.HIDDEN</code>. Doing so hides
  * the button unconditionally, regardless of the
  * currently focused <code>DataSet</code>.
  *
  * <a name="customized_button_state"></a>
  * <h3>Customized button state for a particular DataSet</h3>
  *
  * <p>To enable, disable, or hide buttons depending
  * upon the <code>DataSet</code> currently associated
  * with the <code>JdbNavToolBar</code>, add a
  * <code>java.beans.PropertyChangeListener</code> to
  * <code>JdbNavToolBar</code>. When the
  * <code>focusedDataSet</code> property change event
  * occurs and its new value is the desired
  * <code>DataSet</code>, set the
  * <code>buttonState</code> properties (for example,
  * <code>buttonStateSave</code>) on the
  * <code>JdbNavToolBar</code>. Note, however, that once
  * a button's corresponding <code>buttonState</code>
  * property has been set, it must be explicitly
  * restored to its default state (AUTO_ENABLED) to
  * restore automatic enable/disable behavior.</p>
  *
  * <a name="changing_button_layout"></a>
  * <h3>Changing the button layout</h3>
  *
  * <p>By default, <code>JdbNavToolBar</code> uses <a
href="ToolBarLayout.html"><code>ToolBarLayout</code></a>
  * as its default layout manager.
  * <code>ToolBarLayout</code> combines the behaviors of
  * the AWT <code>FlowLayout</code> and the
  * <code>GridLayout</code> layout managers.</p>
  *
  * <p>It is possible to change the layout to something
  * other than <code>ToolBarLayout</code>. Note,
  * however, that doing so may limit
  * <code>buttonState</code> functionality; for example,
  * not all layout managers support components that are
  * not visible. In such a layout, a button with a
  * HIDDEN <code>buttonState</code> may be invisible but
  * still take up space on the toolbar. If the button
  * should always remain hidden, however, you can remove
  * it from the layout.</p>
  *
  * <a name="customizing_button"></a>
  * <h3>Customizing a button's text, icon, or tooltip</h3>
  *
  * <p>The buttons on the toolbar are deliberately
  * declared with public access to allow you to
  * customize their text, icon, or tooltip. You can also
  * use the public reference to a button to remove it
  * from the toolbar if necessary. Note that all buttons
  * except for the <code>priorButton</code> and
  * <code>nextButton</code> are <code>JButtons</code>.
  * The <code>priorButton</code> and <code>nextButton</code>
  *  are <code>RepeatButtons</code> to facilitate repeat
  * capability when the button is held down. To disable
  * or customize the default repeat behavior, cast the
  * public reference to the <code>priorButton</code> or
  * <code>nextButton</code> to a
  * <code>RepeatButton</code>, and set its
  * repeat-related properties.</p>
  *
  * <a name="adding_custom_buttons"></a>
  * <h3>Adding custom buttons to the toolbar</h3>
  *
  * <p>You can add your own buttons to the toolbar by
  * using the <code>add()</code> method. Note that
  * <code>JdbNavToolBar</code> does not handle such a
  * button's events or alter its state in any way. See
  * <a href="#customized_button_state">Customizing
  *  button state for a particular DataSet</A> for
  * information on how to customize buttons for
  * individual <code>DataSet</code>s.</p>
  *
  * <a name="changing_default_button_behavior"></a>
  * <h3>Changing the default button behavior</h3>
  *
  * <p>To change the default behavior of a toolbar
  * button, you can either extend the
  * <code>JdbNavToolBar</code> class and override the
  * <code>actionPerformed()</code> method, or get the
  * public reference to the button whose behavior you
  * wish to change and remove <code>JdbNavToolBar</code>
  * as an <code>ActionListener</code> on the button. You
  * can then add your own <code>ActionListener</code> to
  * the button, use <code>getFocusedDataSet()</code> to
  * get the current <code>DataSet</code>, and execute
  * your custom action on the currently focused
  * <code>DataSet</code>.</p>
  *
  */
public class JdbNavToolBar
  extends JToolBar
  implements AccessListener, NavigationListener, StatusListener, ActionListener,
             java.io.Serializable, MouseListener
{


 /**
  * <p>Constructs a <code>JdbNavToolBar</code> by calling the <code>null</code> constructor of its superclass, adding the following buttons: FIRST, PRIOR, NEXT, LAST, INSERT, DELETE, POST, CANCEL, DITTO, SAVE, and REFRESH, and setting <code>showRollover</code> and <code>showTooltips</code> to <code>true</code>.</p>
  */
  public JdbNavToolBar() {
    super();
    setLayout(new ToolBarLayout(HORIZONTAL));
    initButtons();
    // Always disable button rollover for Mac L&F, since its normal button border is
    // unsuitable for the toolbar.
    setShowRollover(!"Mac".equals(UIManager.getLookAndFeel().getID()));  
    setShowTooltips(true);
  }

  /**
   * Initializes the buttons on the toolbar.  Sets JdbNavToolBar as each
   * button's ActionListener.
   */
  private void initButtons() {
    //    addComponentListener(this);

    ImageIcon icon;

    firstButton = initButton("first.gif");  

    priorButton = new RepeatButton(new ImageIcon(JdbNavToolBar.class.getResource("image/prior.gif")));  
    priorButton.addActionListener(this);
    priorButton.setMargin(new Insets(0, 0, 0, 0));
    add(priorButton);

    nextButton = new RepeatButton(new ImageIcon(JdbNavToolBar.class.getResource("image/next.gif")));  
    nextButton.addActionListener(this);
    nextButton.setMargin(new Insets(0, 0, 0, 0));
    add(nextButton);

    lastButton = initButton("last.gif");  

    insertButton = initButton("insert.gif");  

    deleteButton = initButton("delete.gif");  

    postButton = initButton("post.gif");  

    cancelButton = initButton("cancel.gif");  

    dittoButton = initButton("ditto.gif");  

    saveButton = initButton("save.gif");  

    refreshButton = initButton("refresh.gif");  

  }

  private JButton initButton(String imageName) {
    JButton jButton = new JButton(new ImageIcon(JdbNavToolBar.class.getResource("image/" + imageName)));  
//    JButton jButton = new JButton(new ImageIcon(JdbNavToolBar.class.getResource(imageName)));  
    jButton.addActionListener(this);
    jButton.setMargin(new Insets(0, 0, 0, 0));
    add(jButton);
    return jButton;
  }

/**
  * <p>Calls the <code>addNotify()</code> method of the superclass.
  */
  public void addNotify() {
    super.addNotify();
    if (autoDetect) {
      DataSetAware [] awares = DBUtilities.findDataAwareComponents(this);
      if (awares.length > 0) {
        setFocusedDataSet(awares[0].getDataSet());
      }
    }
    updateButtonEnabledState();
    revalidateButtons();

    // the Metal L&F provides built-in support for rollover, but for other L&Fs
    // we have to manage the rollover border ourselves.
    normalButtonBorder = UIManager.getBorder("Button.border");   
    // Based on normalButtonBorder.getBorderInsets() to fix bug 149818
    emptyButtonBorder = new EmptyBorder(normalButtonBorder.getBorderInsets(firstButton));
    ui = getUI();
    if (showRollover && !(ui instanceof MetalToolBarUI) && firstButton.getBorder() != null) {
      firstButton.setBorder(emptyButtonBorder);
      priorButton.setBorder(emptyButtonBorder);
      nextButton.setBorder(emptyButtonBorder);
      lastButton.setBorder(emptyButtonBorder);
      insertButton.setBorder(emptyButtonBorder);
      deleteButton.setBorder(emptyButtonBorder);
      postButton.setBorder(emptyButtonBorder);
      cancelButton.setBorder(emptyButtonBorder);
      dittoButton.setBorder(emptyButtonBorder);
      saveButton.setBorder(emptyButtonBorder);
      refreshButton.setBorder(emptyButtonBorder);
    }
  }

/**
 * <p>Returns the orientation of the <code>JdbNavToolBar</code>. </p>
 *
 * @return HORIZONTAL if the toolbar is horizontally oriented and VERTICAL if it is vertically oriented.
 * @see #setOrientation
 */
  public int getOrientation() {
    return orientation;
  }

/**
 * <p>Sets the orientation of the <code>JdbNavToolBar</code>. Possible values are HORIZONTAL or VERTICAL. </p>
 *
 * @param orientation Set to HORIZONTAL if the toolbar is to be horizontally oriented or to VERTICAL if it is to be vertically oriented.
 * @see #getOrientation
 */
  public void setOrientation(int orientation) {
    if (!((orientation == HORIZONTAL) || (orientation == VERTICAL))) {
      throw new IllegalArgumentException( "orientation must be one of: VERTICAL, HORIZONTAL" ); 
    }

    if ( this.orientation != orientation )
      {
        int old = this.orientation;

        LayoutManager layoutManager = getLayout();

        if (layoutManager instanceof ToolBarLayout) {
          ((ToolBarLayout) layoutManager).setOrientation(orientation);
        }
        else {
          if ( orientation == VERTICAL ) {
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
          }
          else {
            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
          }
        }

        this.orientation = orientation;
        firePropertyChange("orientation", old, orientation);  
        revalidate();
      }

  }

  public void mouseClicked(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {
    JButton button = (JButton) e.getSource();
    if (showRollover && button.getBorder() != null) {
      if (button.isEnabled()) {
        if (!(ui instanceof MetalToolBarUI)) {
          button.setBorder(normalButtonBorder);
        }
      }
      else {
        button.setBorder(emptyButtonBorder);
      }
    }
  }

  public void mouseEntered(MouseEvent e) {
    JButton button = (JButton) e.getSource();
    if (showRollover) {
      if (button.isEnabled() && button.getBorder() != null) {
        button.setBorder(normalButtonBorder);
      }
    }
  }

  public void mouseExited(MouseEvent e) {
    JButton button = (JButton) e.getSource();
    if (showRollover && button.getBorder() != null) {
      button.setBorder(emptyButtonBorder);
    }
  }


  /**
   * Updates each button's enabled state based on the state of the
   * currently focused DataSet.
   */
  private void updateButtonEnabledState() {
    try {
      if (currentDataSet != null && currentDataSet.isOpen()) {
        boolean readOnly = false;
        if (currentDataSet instanceof StorageDataSet) {
          readOnly = ((StorageDataSet) currentDataSet).isReadOnly();
        }
        if (!(buttonStateFirst == ENABLED || buttonStateFirst == DISABLED)) {
          firstButton.setEnabled(!(currentDataSet.atFirst() || currentDataSet.isEmpty()));
        }
        if (!(buttonStatePrior == ENABLED || buttonStatePrior == DISABLED)) {
          priorButton.setEnabled(!(currentDataSet.atFirst() || currentDataSet.isEmpty()));
        }

        if (!(buttonStateNext == ENABLED || buttonStateNext == DISABLED)) {
          nextButton.setEnabled(!currentDataSet.atLast() || (currentDataSet.isEnableInsert() && currentDataSet.isEditable() && !readOnly));
        }
        if (!(buttonStateLast == ENABLED || buttonStateLast == DISABLED)) {
          lastButton.setEnabled(!(currentDataSet.atLast() || currentDataSet.isEmpty()));
        }

        if (!(buttonStateInsert == ENABLED || buttonStateInsert == DISABLED)) {
          insertButton.setEnabled(currentDataSet.isEnableInsert() && !currentDataSet.isEditingNewRow() && currentDataSet.isEditable() && !readOnly);
        }

        if (!(buttonStateDelete == ENABLED || buttonStateDelete == DISABLED)) {
          deleteButton.setEnabled(currentDataSet.isEnableDelete() && !currentDataSet.isEmpty() && currentDataSet.isEditable() && !readOnly);
        }

        if (!(buttonStatePost == ENABLED || buttonStatePost == DISABLED)) {
          postButton.setEnabled(currentDataSet.isEditing());
        }
        if (!(buttonStateCancel == ENABLED || buttonStateCancel == DISABLED)) {
          cancelButton.setEnabled(currentDataSet.isEditing());
        }

        if (!(buttonStateDitto == ENABLED || buttonStateDitto == DISABLED)) {
          dittoButton.setEnabled(currentDataSet.isEditingNewRow() || !currentDataSet.isEditing());
        }

        if (!(buttonStateSave == ENABLED || buttonStateSave == DISABLED)) {
          saveButton.setEnabled(currentDataSet.saveChangesSupported() && currentDataSet.isEditable() && !readOnly);
        }
        if (!(buttonStateRefresh == ENABLED || buttonStateRefresh == DISABLED)) {
          refreshButton.setEnabled(currentDataSet.refreshSupported());
        }
      }
      else {
        firstButton.setEnabled(false);
        priorButton.setEnabled(false);
        nextButton.setEnabled(false);
        lastButton.setEnabled(false);
        insertButton.setEnabled(false);
        deleteButton.setEnabled(false);
        postButton.setEnabled(false);
        cancelButton.setEnabled(false);
        dittoButton.setEnabled(false);
        saveButton.setEnabled(false);
        refreshButton.setEnabled(false);
      }
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
  }

  /**
   * Updates each button according to its state (AUTO_ENABLED, AUTO_HIDDEN,
   * ENABLED, DISABLED, HIDDEN).
   */
  private void revalidateButtons() {
    updateButton(firstButton, buttonStateFirst);
    updateButton(priorButton, buttonStatePrior);
    updateButton(nextButton, buttonStateNext);
    updateButton(lastButton, buttonStateLast);
    updateButton(insertButton, buttonStateInsert);
    updateButton(deleteButton, buttonStateDelete);
    updateButton(postButton, buttonStatePost);
    updateButton(cancelButton, buttonStateCancel);
    updateButton(dittoButton, buttonStateDitto);
    updateButton(saveButton, buttonStateSave);
    updateButton(refreshButton, buttonStateRefresh);
    revalidate();
    repaint();
  }

  private void updateButton(JButton button, int state) {
    switch (state) {
    case AUTO_HIDDEN:
      // Button is only visible if enabled.  If disabled, button is hidden
      if (button.isEnabled()) {
        if (!button.isVisible()) {
          button.setVisible(true);
        }
      }
      else {
        if (button.isVisible()) {
          button.setVisible(false);
        }
      }
      break;
    case ENABLED:
    case DISABLED:
      // Button should be visible and enabled or disabled
      if (!button.isVisible()) {
        button.setVisible(true);
      }
      button.setEnabled(state == ENABLED);
      break;
    case HIDDEN:
      // Button should be hidden (may be enabled or disabled)
      if (button.isVisible()) {
        button.setVisible(false);
      }
      break;
    case AUTO_ENABLED:
    default:
      // Button is always visible, but may be enabled or disabled
      if (!button.isVisible()) {
        button.setVisible(true);
      }
      break;
    }
  }

  /**
   * <p>Sets whether <code>JdbNavToolBar</code>
   * automatically attaches itself to <code>DataSet</code>s
   * within its enclosing highest-level container (usually
   * a <code>JFrame</code>). Auto-detection occurs when the
   * <code>JdbNavToolBar</code> is realized.
   * Setting <code>autoDetect</code> to <code>true</code>,
   * clears the <code>dataSet</code> property value.</p>
   *
   * @param autoDetect If <code>true</code>, the <code>dataSet</code> property value is cleared.
   * @see #isAutoDetect
   */
  public void setAutoDetect(boolean autoDetect) {
    boolean oldAutoDetect = this.autoDetect;
    if (autoDetect) {
      dataSet = null;
    }
    firePropertyChange("autoDetect", oldAutoDetect, autoDetect); 
  }

  /**
   * <p>Returns whether <code>JdbNavToolBar</code>
   * automatically attaches itself to <code>DataSet</code>s
   * within its enclosing highest-level container (usually
   * a <code>JFrame</code>).</p>
   *
   * @return If <code>true</code>, the <code>dataSet</code> property value is cleared.
   * @see #setAutoDetect
   */
  public boolean isAutoDetect() {
    return autoDetect;
  }

  /**
   * <p>Sets the list of data-aware components to which
   * <code>JdbNavToolBar</code> attaches itself. Setting
   * this property to a non-null value disables the
   * <code>autoDetect</code> property, and conversely,
   * specifying a <code>null</code> value automatically
   * enables the <code>autoDetect</code> property. When
   * both the <code>dataSet</code> and
   * <code>dataSetAwareComponents</code> properties are
   * set, the one set most recently is the one that takes
   * precedence and the setting of the other one is
   * cleared. </p>
   *
   * @param dataAwareComponents THe array of data-aware components.
   * @see #getDataSetAwareComponents
   * @see #setAutoDetect
   */

  public void setDataSetAwareComponents(Component[] dataAwareComponents) {
    userSetDataAwareComponents = dataAwareComponents;
    autoDetect = (dataAwareComponents == null);
    dataSet = null;
  }

  /**
   * <p>Returns the list of data-aware components to which
   * <code>JdbNavToolBar</code> attaches itself. </p>
   *
   * @return The array of data-aware components.
   * @see #setDataSetAwareComponents
   * @see #isAutoDetect
   */
  public Component [] getDataSetAwareComponents() {
    return userSetDataAwareComponents;
  }

  /**
   * <p>Sets the <code>DataSet</code> whose
   * data is navigated by this component. Setting this
   * property to a non-null value disables the
   * <code>autoDetect</code> property, and conversely,
   * specifying a <code>null</code> value automatically
   * enables the <code>autoDetect</code> property. When
   * both the <code>dataSet</code> and
   * <code>dataSetAwareComponents</code> properties are
   * set, the one set most recently is the one that takes
   * precedence and the setting of the other one is
   * cleared. </p>
   *
   * @param dataSet The <code>DataSet</code> whose data is navigated by this component.
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
   * <p>Returns the <code>DataSet</code> whose
   * data is navigated by this component.  </p>
   *
   * @return The <code>DataSet</code> whose data is navigated by this component.
   * @see #setDataSet
   * @see #isAutoDetect
   */
  public DataSet getDataSet() {
    return dataSet;
  }



  /**
   * <p>Sets the current <code>DataSet</code> attached to
   * <code>JdbNavToolBar</code>. This property can be used
   * to specify the initial <code>DataSet</code> to which
   * <code>JdbNavToolBar</code> should be attached.</p>
   *
   * <p>This is a bound property and fires a property
   * change event.</p>
   *
   * @param dataSet The <code>DataSet</code> attached to
   * <code>JdbNavToolBar</code>.
   * @see #getFocusedDataSet
   */
  public void setFocusedDataSet(DataSet dataSet) {
    updateCurrentDataSet(dataSet);
  }

  /**
   * <p>Returns the current <code>DataSet</code> attached
   * to <code>JdbNavToolBar</code>. </p>
   *
   * @return The <code>DataSet</code> attached to
   * <code>JdbNavToolBar</code>.
   * @see #setFocusedDataSet
   */
  public DataSet getFocusedDataSet() {
    return currentDataSet;
  }

  /**
   * <p>Determines whether a rollover icon is displayed
   * when the mouse is moved over one of the toolbar
   * buttons. The default value is <code>true</code>.</p>
   *
   * <p>Note that rollover appearance is look-and-feel
   * dependent. Specifically, <code>JdbNavToolBar</code>
   * provides rollover icons for all look-and-feels other
   * than Metal, which has its own built-in support for
   * button rollover.</p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param showRollover If <code>true</code>, a rollover icon is displayed.
   * @see #isShowRollover
   */
  public void setShowRollover(boolean showRollover) {
    // Always disable button rollover for Mac L&F, since its normal button border is
    // unsuitable for the toolbar.
    if ("Mac".equals(UIManager.getLookAndFeel().getID())) {  
      showRollover = false;
    }

    this.showRollover = showRollover;
    // The Metal L&F has built-in support for button rollover,
    // so use it if available
    ComponentUI ui = getUI();
    if (ui instanceof MetalToolBarUI) {
      ((MetalToolBarUI) ui).setRolloverBorders(showRollover);
    }
    if (showRollover) {
      firstButton.addMouseListener(this);
      priorButton.addMouseListener(this);
      nextButton.addMouseListener(this);
      lastButton.addMouseListener(this);
      insertButton.addMouseListener(this);
      deleteButton.addMouseListener(this);
      postButton.addMouseListener(this);
      cancelButton.addMouseListener(this);
      dittoButton.addMouseListener(this);
      saveButton.addMouseListener(this);
      refreshButton.addMouseListener(this);
    }
    else {
      firstButton.removeMouseListener(this);
      priorButton.removeMouseListener(this);
      nextButton.removeMouseListener(this);
      lastButton.removeMouseListener(this);
      insertButton.removeMouseListener(this);
      deleteButton.removeMouseListener(this);
      postButton.removeMouseListener(this);
      cancelButton.removeMouseListener(this);
      dittoButton.removeMouseListener(this);
      saveButton.removeMouseListener(this);
      refreshButton.removeMouseListener(this);
    }
  }

  /**
   * <p>Returns whether a rollover icon is displayed
   * when the mouse is moved over one of the toolbar
   * buttons.
   *
   * @return If <code>true</code>, a rollover icon is displayed.
   * @see #setShowRollover
   */
  public boolean isShowRollover() {
    return showRollover;
  }

  /**
   * <p>Determines whether button tooltips are displayed.
   * The default value is <code>true</code>.</p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param showTooltips If <code>true</code>, tooltips are displayed.
   * @see #isShowTooltips
   */
  public void setShowTooltips(boolean showTooltips) {
    this.showTooltips = showTooltips;
    if (showTooltips) {
      firstButton.setToolTipText(Res._First);     
      priorButton.setToolTipText(Res._Prior);     
      nextButton.setToolTipText(Res._Next);     
      lastButton.setToolTipText(Res._Last);     
      insertButton.setToolTipText(Res._Insert);     
      deleteButton.setToolTipText(Res._Delete);     
      postButton.setToolTipText(Res._Post);     
      cancelButton.setToolTipText(Res._Cancel);     
      dittoButton.setToolTipText(Res._Ditto);     
      saveButton.setToolTipText(Res._Save);     
      refreshButton.setToolTipText(Res._Refresh);     
    }
    else {
      firstButton.setToolTipText(null);
      priorButton.setToolTipText(null);
      nextButton.setToolTipText(null);
      lastButton.setToolTipText(null);
      insertButton.setToolTipText(null);
      deleteButton.setToolTipText(null);
      postButton.setToolTipText(null);
      cancelButton.setToolTipText(null);
      dittoButton.setToolTipText(null);
      saveButton.setToolTipText(null);
      refreshButton.setToolTipText(null);
    }
  }

  /**
   * Returns whether or not button tooltips are displayed.
   *
   * @return If <code>true</code>, tooltips are displayed.
   * @see #isShowTooltips
   */
  public boolean isShowTooltips() {
    return showTooltips;
  }

  /**
   * <p>Sets the (<code>FlowLayout</code>) alignment for
   * this <code>JdbNavToolBar</code>. The default
   * layout/alignment for a <code>JdbNavToolBar</code> is
   * <code>FlowLayout</code> with CENTER alignment. If the
   * layout has been explicitly changed to be some layout
   * other than <code>FlowLayout</code>, however, the value
   * returned here is meaningless and always 0 and setting
   * this property has no effect.</p>
   *
   * @param alignment The desired <code>FlowLayout</code> aligment setting.
   * @see #getAlignment
   */
  public void setAlignment(int alignment) {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout) {
      ((FlowLayout)layout).setAlignment(alignment);
    }
  }

  /**
   * <p>Returns the (<code>FlowLayout</code>) alignment for
   * this <code>JdbNavToolBar</code>. </p>
   *
   * @return The <code>FlowLayout</code> aligment setting.
   * @see #setAlignment
   */
  public int getAlignment() {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout) {
      return ((FlowLayout)layout).getAlignment();
    }
    return 0;
  }

  /**
   * <p>Sets the state of the First button,
   * which navigates the focused <code>DataSet</code> to
   * its first row.</p>
   *
   * <p> <code>buttonStateFirst</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateFirst The state of the First button.
   * @see #getButtonStateFirst
   * @see #setButtonStateLast
   */
  public void setButtonStateFirst(int buttonStateFirst) {
    this.buttonStateFirst = buttonStateFirst;
    updateButton(firstButton, buttonStateFirst);
    revalidate();
  }

  /**
   * <p>Returns the state of the First button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateFirst
   * @see #getButtonStateLast
   */
  public int getButtonStateFirst() {
    return buttonStateFirst;
  }

  /**
   * <p>Sets the state of the Prior button,
   * which navigates the focused <code>DataSet</code> to
   * its previous row.</p>
   *
   * <p> <code>buttonStatePrior</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStatePrior The state of the Prior button.
   * @see #getButtonStatePrior
   * @see #setButtonStateNext
   */
  public void setButtonStatePrior(int buttonStatePrior) {
    this.buttonStatePrior = buttonStatePrior;
    updateButton(priorButton, buttonStatePrior);
    revalidate();
  }

  /**
   * <p>Returns the state of the Prior button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStatePrior
   * @see #getButtonStateNext
   */
  public int getButtonStatePrior() {
    return buttonStatePrior;
  }

  /**
   * <p>Sets the state of the Next button,
   * which navigates the focused <code>DataSet</code> to
   * its next row.</p>
   *
   * <p> <code>buttonStateNext</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateNext The state of the Next button.
   * @see #getButtonStateNext
   * @see #setButtonStatePrior
   */
  public void setButtonStateNext(int buttonStateNext) {
    this.buttonStateNext = buttonStateNext;
    updateButton(nextButton, buttonStateNext);
    revalidate();
  }

  /**
   * <p>Returns the state of the Next button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateNext
   * @see #getButtonStatePrior
   */
  public int getButtonStateNext() {
    return buttonStateNext;
  }

  /**
   * <p>Sets the state of the Last button,
   * which navigates the focused <code>DataSet</code> to
   * its last row.</p>
   *
   * <p> <code>buttonStateLast</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateLast The state of the Last button.
   * @see #getButtonStateLast
   * @see #setButtonStateFirst
   */
  public void setButtonStateLast(int buttonStateLast) {
    this.buttonStateLast = buttonStateLast;
    updateButton(lastButton, buttonStateLast);
    revalidate();
  }

  /**
   * <p>Returns the state of the Last button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateLast
   * @see #getButtonStateFirst
   */
  public int getButtonStateLast() {
    return buttonStateLast;
  }

  /**
   * <p>Sets the state of the Insert button,
   * which inserts a row into the focused <code>DataSet</code>.</p>
   *
   * <p><code>buttonStateInsert</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateInsert The state of the Insert  button.
   * @see #getButtonStateInsert
   * @see #setButtonStateDelete
   */
  public void setButtonStateInsert(int buttonStateInsert) {
    this.buttonStateInsert = buttonStateInsert;
    updateButton(insertButton, buttonStateInsert);
    revalidate();
  }

  /**
   * <p>Returns the state of the Insert button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateInsert
   * @see #getButtonStateDelete
   */
  public int getButtonStateInsert() {
    return buttonStateInsert;
  }

  /**
   * <p>Sets the state of the Delete button,
   * which deletes a row from the focused <code>DataSet</code>.</p>
   *
   * <p><code>buttonStateDelete</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateDelete The state of the Delete  button.
   * @see #getButtonStateDelete
   * @see #setButtonStateInsert
   */
  public void setButtonStateDelete(int buttonStateDelete) {
    this.buttonStateDelete = buttonStateDelete;
    updateButton(deleteButton, buttonStateDelete);
    revalidate();
  }

  /**
   * <p>Returns the state of the Delete button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateDelete
   * @see #getButtonStateInsert
   */
  public int getButtonStateDelete() {
    return buttonStateDelete;
  }

  /**
   * <p>Sets the state of the Post button,
   * which posts a row to the focused <code>DataSet</code>.</p>
   *
   * <p><code>buttonStatePost</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStatePost The state of the Post button.
   * @see #getButtonStatePost
   * @see #setButtonStateCancel
   */
  public void setButtonStatePost(int buttonStatePost) {
    this.buttonStatePost = buttonStatePost;
    updateButton(postButton, buttonStatePost);
    revalidate();
  }

  /**
   * <p>Returns the state of the Post button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStatePost
   * @see #getButtonStateCancel
   */
  public int getButtonStatePost() {
    return buttonStatePost;
  }

  /**
   * <p>Sets the state of the Cancel button,
   * which cancels the row posted to the focused <code>DataSet</code>.</p>
   *
   * <p><code>buttonStateCancel</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateCancel The state of the Cancel button.
   * @see #getButtonStateCancel
   * @see #setButtonStatePost
   */
  public void setButtonStateCancel(int buttonStateCancel) {
    this.buttonStateCancel = buttonStateCancel;
    updateButton(cancelButton, buttonStateCancel);
    revalidate();
  }

  /**
   * <p>Returns the state of the Cancel button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateCancel
   * @see #getButtonStatePost
   */
  public int getButtonStateCancel() {
    return buttonStateCancel;
  }

  /**
   * <p>Sets the state of the Ditto button.</p>
   *
   * <p><code>buttonStateDitto</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateDitto The state of the Ditto button.
   * @see #getButtonStateDitto
   */
  public void setButtonStateDitto(int buttonStateDitto) {
    this.buttonStateDitto = buttonStateDitto;
    updateButton(dittoButton, buttonStateDitto);
    revalidate();
  }

  /**
   * <p>Returns the state of the Ditto button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateDitto
   */
  public int getButtonStateDitto() {
    return buttonStateDitto;
  }

  /**
   * <p>Sets the state of the Save button,
   * which saves changes to the focused
   * <code>DataSet</code>.</p>
   *
   * <p><code>buttonStateSave</code> must be one of these
   * values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateSave The state of the Save button.
   * @see #getButtonStateSave
   */
  public void setButtonStateSave(int buttonStateSave) {
    this.buttonStateSave = buttonStateSave;
    updateButton(saveButton, buttonStateSave);
    revalidate();
  }

 /**
   * <p>Returns the state of the Save button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateSave
   */
  public int getButtonStateSave() {
    return buttonStateSave;
  }


  /**
   * <p>Sets the state of the Refresh button,
   * which refreshes the focused
   * <code>DataSet</code>.</p>
   *
   * <p><code>buttonStateRefresh</code> must be one of
   * these values:  </p>
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * <p>AUTO_ENABLED is the default state. </p>
   *
   * <p>This is not a bound property and does not fire a
   * property change event.</p>
   *
   * @param buttonStateRefresh The state of the Refresh  button.
   * @see #getButtonStateRefresh
   */
  public void setButtonStateRefresh(int buttonStateRefresh) {
    this.buttonStateRefresh = buttonStateRefresh;
    updateButton(refreshButton, buttonStateRefresh);
    revalidate();
  }

 /**
   * <p>Returns the state of the Refresh button.</p>
   *
   * @return One of the following values is returned:
   *
   * <ul>
   * <li>AUTO_ENABLED (the default value) - The button is automatically enabled or disabled according to the state of the current <code>DataSet</code>.</li>
   * <li>AUTO_HIDDEN - The button is automatically hidden when disabled, and made visible when enabled, according to the state of the current <code>DataSet</code>. </li>
   * <li>ENABLED - The button is always enabled.</li>
   * <li>DISABLED - The button is always disabled. </li>
   * <li>HIDDEN - The button is always hidden. </li>
   * </ul>
   *
   * @see #setButtonStateRefresh
   */
  public int getButtonStateRefresh() {
    return buttonStateRefresh;
  }



  // adds ourself as a listener to the newly focused DataSet,
  // fires a property change event indicating the new focused DataSet,
  // and updates the state of buttons on the toolbar
  void updateCurrentDataSet(DataSet dataSet) {
    DataSet oldDataSet = currentDataSet;
    if (dataSet != currentDataSet) {
      // Remove ourself as a listener from the previous dataSet;
      if (currentDataSet != null) {
        currentDataSet.removeStatusListener(this);
        currentDataSet.removeAccessListener(this);
        currentDataSet.removeNavigationListener(this);
      }
      if (dataSet != null) {
        // Add ourself as a listener to this event source's DataSet
        dataSet.addStatusListener(this);
        dataSet.addAccessListener(this);
        dataSet.addNavigationListener(this);
      }
      currentDataSet = dataSet;
      // fire a property change notification to allow user to customize
      // individual button state.
      firePropertyChange("focusedDataSet", oldDataSet, currentDataSet); 
      // invokeLater() to fix 149590
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          // set the button state according to the currentDataSet
          updateButtonEnabledState();
          // after the user has had a chance to set the visibility of each
          // button, revalidate the buttons.
          revalidateButtons();
        }
      });
    }
  }

  // AccessListener implementation

  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) { //&& event.getReason() == AccessEvent.DATA_CHANGE) {
      dataSetEventsEnabled = true;
    }
    if (event.getID() == AccessEvent.CLOSE && event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
      dataSetEventsEnabled = false;
      return;
    }
    updateButtonEnabledState();
    revalidateButtons();
  }

  // StatusListener implementation

  public void statusMessage(StatusEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    switch (event.getCode()) {
    case (StatusEvent.EDIT_STARTED):
    case (StatusEvent.EDIT_CANCELED):
    case (StatusEvent.LOCATE_MATCH_FOUND):
    case (StatusEvent.LOCATE_MATCH_NOT_FOUND):
    case (StatusEvent.DATA_CHANGE):
      updateButtonEnabledState();
      revalidateButtons();
      break;
    }
  }

  // NavigationListener implementation

  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    updateButtonEnabledState();
    revalidateButtons();
  }

  // ActionListener implementation

  public void actionPerformed(ActionEvent e) {
    if (currentDataSet != null) {
      try {
        JButton button = (JButton) e.getSource();
        if (button == firstButton) {
          currentDataSet.first();
        }
        else if (button == nextButton) {
          if (currentDataSet.atLast() || (currentDataSet.isEditable() && currentDataSet.isEmpty())) {
            currentDataSet.insertRow(false);
          }
          else {
            currentDataSet.next();
          }
        }
        else if (button == priorButton) {
          currentDataSet.prior();
        }
        else if (button == lastButton) {
          currentDataSet.last();
        }
        else if (button == insertButton) {
          currentDataSet.insertRow(true);
        }
        else if (button == deleteButton) {
          currentDataSet.deleteRow();
        }
        else if (button == postButton) {
          currentDataSet.post();
        }
        else if (button == dittoButton) {
          currentDataSet.dittoRow(false, true);
        }
        else if (button == cancelButton) {
          if (currentDataSet.isEditing()) {
            currentDataSet.cancel();
          }
        }
        else if (button == saveButton) {
          currentDataSet.saveChanges();
        }
        else if (button == refreshButton) {
          currentDataSet.refresh();
        }
      }
      catch (DataSetException ex) {
        DBExceptionHandler.handleException(dataSet, ex);
      }
    }
  }

  /**
   * <p>Returns a reference to First toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */

  public JButton getFirstButton() {
    return firstButton;
  }

  /**
   * <p>Returns a reference to Prior toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getPriorButton() {
    return priorButton;
  }

  /**
   * <p>Returns a reference to Next toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getNextButton() {
    return nextButton;
  }

  /**
   * <p>Returns a reference to Last toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */

  public JButton getLastButton() {
    return lastButton;
  }

  /**
   * <p>Returns a reference to Insert toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getInsertButton() {
    return insertButton;
  }

  /**
   * <p>Returns a reference to Delete toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getDeleteButton() {
    return deleteButton;
  }

  /**
   * <p>Returns a reference to Post toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getPostButton() {
    return postButton;
  }

  /**
   * <p>Returns a reference to Cancel toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getCancelButton() {
    return cancelButton;
  }

  /**
   * <p>Returns a reference to Ditto toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getDittoButton() {
    return dittoButton;
  }

  /**
   * <p>Returns a reference to Save toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getSaveButton() {
    return saveButton;
  }

  /**
   * <p>Returns a reference to Refresh toolbar button (<code>JButton</code>).</p>
   *
   * @return The reference to the button.
   */
  public JButton getRefreshButton() {
    return refreshButton;
  }

  /** reference to "First" toolbar button (JButton) */
  private JButton firstButton;

  /** reference to "Prior" toolbar button (RepeatButton) */
  private JButton priorButton;

  /** reference to "Next" toolbar button (RepeatButton) */
  private JButton nextButton;

  /** reference to "Last" toolbar button (JButton) */
  private JButton lastButton;

  /** reference to "Insert" toolbar button (JButton) */
  private JButton insertButton;

  /** reference to "Delete" toolbar button (JButton) */
  private JButton deleteButton;

  /** reference to "Post" toolbar button (JButton) */
  private JButton postButton;

  /** reference to "Cancel" toolbar button (JButton) */
  private JButton cancelButton;

  /** reference to "Ditto" toolbar button (JButton) */
  private JButton dittoButton;

  /** reference to "Save" toolbar button (JButton) */
  private JButton saveButton;

  /** reference to "Refresh" toolbar button (JButton) */
  private JButton refreshButton;


 /**
  * <p>A <code>buttonState</code> property constant specifying that the button should be visible and automatically enabled/disabled. Set to 0.</p>
  */
  public static final int AUTO_ENABLED = 0;

 /**
  * <p>A <code>buttonState</code> property constant specifying that the button should be automatically hidden when it is disabled, and made visible when it is enabled.  Enabling and disabling happens automatically, according to the state of the current <code>DataSet</code>. Set to 1.</p>
 */
  public static final int AUTO_HIDDEN = 1;

 /**
  * <p>A <code>buttonState</code> property constant specifying that the button should always be enabled. Set to 2.</p>
  */
  public static final int ENABLED = 2;

 /**
  * <p>A <code>buttonState</code> property constant specifying that the button should always be disabled. Set to 3.</p>
  */
  public static final int DISABLED = 3;

 /**
  * <p>A <code>buttonState</code> property constant specifying that the button should always be disabled. Set to 4.</p>
  */
  public static final int HIDDEN = 4;

  /** user-specified array of data-aware components with which to
      register for Focus events */
  private Component [] userSetDataAwareComponents;

  /** whether or not to automatically detect and register self on
      DataSets in the same container */
  private boolean autoDetect = true;

  /** single DataSet to which to be explicitly attached */
  private DataSet dataSet;

  /** DataSet whose current status is displayed */
  private DataSet currentDataSet = null;

  /** whether or not to display button rollover */
  private boolean showRollover = true;

  /** whether or not to display button tooltips */
  private boolean showTooltips = true;

  /** state of First button */
  private int buttonStateFirst = AUTO_ENABLED;

  /** state of Prior button */
  private int buttonStatePrior = AUTO_ENABLED;

  /** state of Next button */
  private int buttonStateNext = AUTO_ENABLED;

  /** state of Last button */
  private int buttonStateLast = AUTO_ENABLED;

  /** state of Insert button */
  private int buttonStateInsert = AUTO_ENABLED;

  /** state of Delete button */
  private int buttonStateDelete = AUTO_ENABLED;

  /** state of Post button */
  private int buttonStatePost = AUTO_ENABLED;

  /** state of Cancel button */
  private int buttonStateCancel = AUTO_ENABLED;

  /** state of Ditto button */
  private int buttonStateDitto = AUTO_ENABLED;

  /** state of Save button */
  private int buttonStateSave = AUTO_ENABLED;

  /** state of Refresh button */
  private int buttonStateRefresh = AUTO_ENABLED;

  /** border used as non-rollover button border */
  private Border emptyButtonBorder;

  /** border used as rollover button border */
  private Border normalButtonBorder;

  private int orientation = HORIZONTAL;

  private boolean registeredWithRuntime = false;

  private ComponentUI ui;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
