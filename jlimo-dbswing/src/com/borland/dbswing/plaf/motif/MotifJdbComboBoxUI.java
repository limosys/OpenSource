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
package com.borland.dbswing.plaf.motif;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import com.sun.java.swing.plaf.motif.*;

import com.borland.dbswing.*;
import com.borland.dbswing.plaf.basic.*;
import com.borland.dx.dataset.*;

public class MotifJdbComboBoxUI extends MotifComboBoxUI {

  public static ComponentUI createUI(JComponent c) {
    return new MotifJdbComboBoxUI();
  }

  protected ComboPopup createPopup() {
    MotifJdbComboPopup popup = new MotifJdbComboPopup(comboBox);
    popup.getAccessibleContext().setAccessibleParent(comboBox);
    return popup;
  }

  public ComboPopup getPopup() {
    return popup;
  }

  public void setUseLookAheadComboBoxEditor(boolean useLookAheadComboBoxEditor) {
    this.useLookAheadComboBoxEditor = useLookAheadComboBoxEditor;
  }

  public boolean isUseLookAheadComboBoxEditor() {
    return useLookAheadComboBoxEditor;
  }

  protected ComboBoxEditor createEditor() {
    if (useLookAheadComboBoxEditor) {
      return new BasicJdbComboBoxEditor(comboBox);
    }
    return super.createEditor();
  }

  protected boolean isShowTable() {
    return comboBox.getModel() instanceof JdbComboBox.DBComboBoxModel;
  }

  public class MotifJdbComboPopup extends MotifComboBoxUI.MotifComboPopup {
    protected JdbTable table;
    protected Timer autoScrollTimer;
    protected boolean hasEntered = false;
    protected boolean isAutoScrolling = false;
//    protected int scrollDirection = BasicComboPopup.SCROLL_UP;
    protected int scrollDirection = 0;

    public MotifJdbComboPopup(JComboBox cBox) {
      super(cBox);
    }

    public void show() {
      Dimension popupSize = comboBox.getSize();
      int popupWidth = popupSize.width;
      if (dropDownWidth != -1) {
        popupWidth = dropDownWidth;
      }
      else {
        if (isShowTable()) {
          table.createDefaultColumnsFromModel();
          popupWidth = table.getColumnModel().getTotalColumnWidth();
          Insets insets = scroller.getInsets();
          Insets scrollBarInsets = scroller.getVerticalScrollBar().getInsets();
          popupWidth += insets.left + insets.right + scrollBarInsets.left + scrollBarInsets.right + 12;
        }
      }
      popupSize.setSize(popupWidth, getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
      Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height,
                                                 popupSize.width, popupSize.height);
      scroller.setMaximumSize(popupBounds.getSize());
      scroller.setPreferredSize(popupBounds.getSize());
      scroller.setMinimumSize(popupBounds.getSize());
//      if (isShowTable()) {
//        table.invalidate();
//        syncTableSelectionWithComboBoxSelection();
//      }
//      else {
//        list.invalidate();
        syncListSelectionWithComboBoxSelection();
//      }

      setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

      initialIndex = comboBox.getSelectedIndex();
      show(comboBox, popupBounds.x, popupBounds.y);
    }

    void syncListSelectionWithComboBoxSelection() {
      int selectedIndex = comboBox.getSelectedIndex();

      if (selectedIndex == -1) {
        if (isShowTable()) {
          table.getSelectionModel().clearSelection();
        }
        else {
          getJList().clearSelection();
        }
      }
      else {
        if (isShowTable()) {
          table.invalidate();
          table.setRowSelectionInterval(selectedIndex, selectedIndex);
          table.ensureRowIsVisible(selectedIndex);
        }
        else {
          getJList().invalidate();
          getJList().setSelectedIndex(selectedIndex);
          getJList().ensureIndexIsVisible(getJList().getSelectedIndex());
        }
      }
    }

    protected int getPopupHeightForRowCount(int maxRowCount) {
      int currentSize = comboBox.getModel().getSize();

      if (fixedCellHeight != -1 && currentSize > 0) {
        int rowMargin = 0;
        if (isShowTable()) {
          table.setRowHeight(fixedCellHeight);
          rowMargin = table.getRowMargin();
        }
        else {
          getJList().setFixedCellHeight(fixedCellHeight);
        }

        if (maxRowCount < currentSize) {
          return ((fixedCellHeight + rowMargin) * maxRowCount) + 2;
        }
        else {
          return ((fixedCellHeight + rowMargin) * currentSize) + 2;
        }
      }
      else {
        if (currentSize > 0) {
          int height;
          if (isShowTable()) {
            height = table.getRowHeight() + table.getRowMargin();
          }
          else {
            height = getJList().getCellBounds(0,0).height;
          }

          if (maxRowCount < currentSize) {
            return (height * maxRowCount) + 2;
          }
          else {
            return (height * currentSize) + 2;
          }
        }
        else {
          return 100;
        }
      }
    }


    protected void configureTable() {
      table.setFont(comboBox.getFont());
      table.setForeground(comboBox.getForeground());
      table.setBackground(comboBox.getBackground());
      table.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground")); 
      table.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground")); 
      table.setRowHeaderVisible(false);
      table.setColumnHeaderVisible(false);
      table.setRowSelectionAllowed(true);
      table.setAutoSelection(false);
      table.setBorder(null);
      table.setPopupMenuEnabled(false);
      table.setShowHorizontalLines(false);
//      table.setCellRenderer(comboBox.getRenderer());
      table.setRequestFocusEnabled(false);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      syncListSelectionWithComboBoxSelection();
      JdbComboBox.DBComboBoxModel tableModel = (JdbComboBox.DBComboBoxModel) table.getModel();
      PickListDescriptor pickList = tableModel.getPickListDescriptor();
      DataSet pickListDataSet = pickList.getPickListDataSet();
      String [] displayColumns = pickList.getPickListDisplayColumns();
      boolean [] visibleColumns = new boolean[tableModel.getColumnCount()];
      int visibleColumnCount = 0;
      String columnName;
      for (int colNo = 0; colNo < visibleColumns.length; colNo++) {
        columnName = tableModel.getColumn(colNo).getColumnName();
        for (int i = 0; i < displayColumns.length; i++) {
          if (displayColumns[i].equalsIgnoreCase(columnName)) {
            visibleColumns[colNo] = true;
            visibleColumnCount++;
            break;
          }
        }
      }
      int [] hiddenColumns = new int[visibleColumns.length - visibleColumnCount];
      int hiddenColumnIndex = 0;
      for  (int colNo = 0; colNo < visibleColumns.length; colNo++) {
        if (!visibleColumns[colNo]) {
          hiddenColumns[hiddenColumnIndex++] = colNo;
        }
      }
      table.setHiddenColumns(hiddenColumns);
      installTableListeners();
    }

    protected void installTableListeners() {
      // In case they've already been added.
      table.getSelectionModel().removeListSelectionListener(listSelectionListener);
      table.removeMouseMotionListener(listMouseMotionListener);
      table.removeMouseListener(listMouseListener);

      table.getSelectionModel().addListSelectionListener(listSelectionListener);
      table.addMouseMotionListener(listMouseMotionListener);
      table.addMouseListener(listMouseListener);
    }

    protected void setValueIsAdjusting(boolean valueIsAdjusting) {
      this.valueIsAdjusting = valueIsAdjusting;
    }

    protected boolean isValueIsAdjusting() {
      return valueIsAdjusting;
    }

    protected class JdbSelectionHandler extends BasicComboPopup.ListSelectionHandler {
      boolean lightNav = false;

      public JdbSelectionHandler() {
        Object keyNav = getComboBox().getClientProperty("JComboBox.lightweightKeyboardNavigation");  
        if (keyNav != null) {
//            if (keyNav.equals("Lightweight")) {
//              lightNav = true;
//            }
//            else if (keyNav.equals("Heavyweight")) {
          if (keyNav.equals("Heavyweight")) { 
            lightNav = false;
          }
        }
      }

      public void valueChanged(ListSelectionEvent e) {
        if (isShowTable()) {
          if (!lightNav && !isValueIsAdjusting() && !e.getValueIsAdjusting() &&
              table.getSelectedRow() != getComboBox().getSelectedIndex() &&
              table.getSelectedRow() < getComboBox().getItemCount() &&
              table.getSelectedRow() >= -1) {

            setValueIsAdjusting(true);
            getComboBox().setSelectedIndex(table.getSelectedRow());
            table.ensureRowIsVisible(table.getSelectedRow());
            setValueIsAdjusting(false);
          }
        }
        else {
          super.valueChanged(e);
        }
      }
    }

    protected class JdbMouseHandler extends MouseAdapter {
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent anEvent) {
        if (isShowTable()) {
          getComboBox().setSelectedIndex(table.getSelectedRow());
        }
        else {
          getComboBox().setSelectedIndex(getJList().getSelectedIndex());
        }
        initialIndex = -1;
        hide();
      }
    }

    protected class JdbMouseMotionHandler extends MouseMotionAdapter {
//      public void mouseDragged(MouseEvent anEvent) {
//        mouseMoved(anEvent);
//      }

      public void mouseMoved(MouseEvent anEvent) {
        Point location = anEvent.getPoint();
        Rectangle r = new Rectangle();
        if (isShowTable()) {
          table.computeVisibleRect(r);
        }
        else {
          getJList().computeVisibleRect(r);
        }
        if (r.contains(location)) {
          setValueIsAdjusting(true);
          updateListBoxSelectionForEvent(anEvent, false);
          setValueIsAdjusting(false);
        }
      }
    }

    protected class JdbItemHandler implements ItemListener {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && !isValueIsAdjusting()) {
          setValueIsAdjusting(true);
          syncListSelectionWithComboBoxSelection();
          setValueIsAdjusting(false);
          // not necessary, because done in syncListSelectionWithComboBoxSelection()
//          list.ensureIndexIsVisible(comboBox.getSelectedIndex());
        }
      }
    }

    protected class JdbInvocationMouseHandler extends MouseAdapter {
      public void mousePressed(MouseEvent e) {
        Rectangle r;

        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }

        if (!getComboBox().isEnabled()) {
          return;
        }

        jdbDelegateFocus(e);

        jdbTogglePopup();
      }

      public void mouseReleased(MouseEvent e) {
        Component source = (Component)e.getSource();
        Dimension size = source.getSize();
        Rectangle bounds = new Rectangle(0, 0, size.width - 1, size.height - 1);
        if (!bounds.contains(e.getPoint())) {
          MouseEvent newEvent = convertMouseEvent(e);
          Point location = newEvent.getPoint();
          Rectangle r = new Rectangle();
          if (isShowTable()) {
            table.computeVisibleRect(r);
          }
          else {
            getJList().computeVisibleRect(r);
          }
          if (r.contains(location)) {
            updateListBoxSelectionForEvent(newEvent, false);
            if (isShowTable()) {
              getComboBox().setSelectedIndex(table.getSelectedRow());
            }
            else {
              getComboBox().setSelectedIndex(getJList().getSelectedIndex());
            }
          }
          initialIndex = -1;
          hide();
        }
        hasEntered = false;
        MotifJdbComboPopup.this.stopAutoScrolling();
      }
    }

    protected void jdbDelegateFocus(MouseEvent e) {
      if (getComboBox().isEditable()) {
        getComboBox().getEditor().getEditorComponent().requestFocus();
      }
      else {
        getComboBox().requestFocus();
      }
    }

    protected void jdbTogglePopup() {
      if (isVisible()) {
        hide();
      }
      else {
        show();
      }
    }

    protected class JdbInvocationMouseMotionHandler extends MouseMotionAdapter {
      public void mouseDragged(MouseEvent e) {
        if (isVisible()) {
          MouseEvent newEvent = convertMouseEvent(e);
          Rectangle r = new Rectangle();
          if (isShowTable()) {
            table.computeVisibleRect(r);
          }
          else {
            getJList().computeVisibleRect(r);
          }

          if (newEvent.getPoint().y >= r.y && newEvent.getPoint().y <= r.y + r.height - 1) {
            hasEntered = true;
            if (isAutoScrolling) {
              MotifJdbComboPopup.this.stopAutoScrolling();
            }
            Point location = newEvent.getPoint();
            if (r.contains(location)) {
              setValueIsAdjusting(true);
              updateListBoxSelectionForEvent(newEvent, false);
              setValueIsAdjusting(false);
            }
          }
          else {
            if (hasEntered) {
              int directionToScroll = newEvent.getPoint().y < r.y ? SCROLL_UP : SCROLL_DOWN;
              if (isAutoScrolling && scrollDirection != directionToScroll) {
                MotifJdbComboPopup.this.stopAutoScrolling();
                MotifJdbComboPopup.this.startAutoScrolling(directionToScroll);
              }
              else if (!isAutoScrolling) {
                MotifJdbComboPopup.this.startAutoScrolling(directionToScroll);
              }
            }
            else {
              if (e.getPoint().y < 0) {
                hasEntered = true;
//                MotifJdbComboPopup.this.startAutoScrolling(BasicComboPopup.SCROLL_UP);
                MotifJdbComboPopup.this.startAutoScrolling(0);
              }
            }
          }
        }
      }
    }

    protected MouseEvent convertMouseEvent(MouseEvent e) {
        Point convertedPoint;
        if (isShowTable()) {
          convertedPoint = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), table);
        }
        else {
          convertedPoint = SwingUtilities.convertPoint((Component)e.getSource(), e.getPoint(), list);
        }
        MouseEvent newEvent = new MouseEvent((Component)e.getSource(),
                                              e.getID(),
                                              e.getWhen(),
                                              e.getModifiers(),
                                              convertedPoint.x,
                                              convertedPoint.y,
                                              e.getModifiers(),
                                              e.isPopupTrigger());
        return newEvent;
    }

    public class JdbInvocationKeyHandler extends KeyAdapter {
      boolean lightNav = false;

      public JdbInvocationKeyHandler() {
        Object keyNav = getComboBox().getClientProperty("JComboBox.lightweightKeyboardNavigation"); 
        if (keyNav != null) {
//            if (keyNav.equals("Lightweight")) {
//              lightNav = true;
//            }
//            else if (keyNav.equals("Heavyweight")) {
          if (keyNav.equals("Heavyweight")) { 
            lightNav = false;
          }
        }
      }

      public void keyReleased(KeyEvent e) {
        if (!getComboBox().isEditable()) {
          if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (!isVisible()) {
              show();
            }
          }
          else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (initialIndex != -1) {
              getComboBox().setSelectedIndex(initialIndex);
              syncListSelectionWithComboBoxSelection();
              initialIndex = -1;
            }
            if (isVisible()) {
              hide();
            }
          }
          else {
            if (e.getKeyCode() == KeyEvent.VK_SPACE ||
                e.getKeyCode() == KeyEvent.VK_ENTER) {
              if (isVisible()) {
                if (lightNav) {
                  if (isShowTable()) {
                    getComboBox().setSelectedIndex(table.getSelectedRow());
                  }
                  else {
                    getComboBox().setSelectedIndex(getJList().getSelectedIndex());
                  }
                }
                else {
                  hide();
                }
                initialIndex = -1;
              }
              else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                // Don't toggle if the popup is invisible and
                // the key is an <Enter> (conflicts with default
                // button)
                show();
              }
            }
          }
        }
        else {
          if (e.getKeyCode() == KeyEvent.VK_UP ||
              e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (!isVisible()) {
              show();
            }
          }
        }
      }
    }

    protected MouseListener createMouseListener() {
      return new JdbInvocationMouseHandler();
    }

    protected MouseMotionListener createMouseMotionListener() {
      return new JdbInvocationMouseMotionHandler();
    }

    public KeyListener createKeyListener() {
      return new JdbInvocationKeyHandler();
    }

    protected ListSelectionListener createListSelectionListener() {
      return new JdbSelectionHandler();
    }

    protected MouseListener createListMouseListener() {
      return new JdbMouseHandler();
    }

    public MouseMotionListener createListMouseMotionListener() {
      return new JdbMouseMotionHandler();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
      return new JdbComboPopupPropertyChangeHandler();
    }

    protected ItemListener createItemListener() {
      return new JdbItemHandler();
    }

// not necessary
//    protected ListDataListener createListDataListener() {
//      return new JdbDataHandler();
//    }

    protected void updateListBoxSelectionForEvent(MouseEvent anEvent,boolean shouldScroll) {
      Point location = anEvent.getPoint();
      int index = 0;
      if (isShowTable()) {
        if (table == null) {
          return;
        }
        index = table.rowAtPoint(location);
      }
      else {
        if (getJList() == null) {
          return;
        }
        index = getJList().locationToIndex(location);
      }

      if (index == -1) {
        if (location.y < 0) {
          index = 0;
        }
        else {
          index = getComboBox().getModel().getSize() - 1;
        }
      }

      if (isShowTable()) {
        if (table.getSelectedRow() != index) {
          table.setRowSelectionInterval(index, index);
          if (shouldScroll) {
            table.ensureRowIsVisible(index);
          }
        }
        table.repaint();
      }
      else {
        if (getJList().getSelectedIndex() != index) {
          getJList().setSelectedIndex(index);
          if (shouldScroll) {
            getJList().ensureIndexIsVisible(index);
          }
        }
      }
    }

    protected void startAutoScrolling(int direction) {
      if (isAutoScrolling) {
        autoScrollTimer.stop();
      }

      isAutoScrolling = true;

      //    if (direction == BasicComboPopup.SCROLL_UP) {
      //      scrollDirection = BasicComboPopup.SCROLL_UP;
      if (direction == 0) {
        scrollDirection = 0;
        Point convertedPoint;
        int top;
        if (isShowTable()) {
          convertedPoint = SwingUtilities.convertPoint(scroller, new Point(1, 1), table);
          top = table.rowAtPoint(convertedPoint);
          if (top != -1) {
            setValueIsAdjusting(true);
            table.setRowSelectionInterval(top, top);
            setValueIsAdjusting(false);
          }
        }
        else {
          convertedPoint = SwingUtilities.convertPoint(scroller, new Point(1, 1), list);
          top = getJList().locationToIndex(convertedPoint);
          setValueIsAdjusting(true);
          getJList().setSelectedIndex(top);
          setValueIsAdjusting(false);
        }

        AbstractAction timerAction = new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            autoScrollUp();
          }
          public boolean isEnabled() {
            return true;
          }
        };

        autoScrollTimer = new Timer(100, timerAction);
      }
      //    else if (direction == BasicComboPopup.SCROLL_DOWN) {
      //      scrollDirection = BasicComboPopup.SCROLL_DOWN;
      else if (direction == 1) {
        scrollDirection = 1;
        Dimension size = scroller.getSize();
        if (isShowTable()) {
          Point convertedPoint = SwingUtilities.convertPoint(scroller,
                                                             new Point(1, (size.height - 1) - 2),
                                                             list);
          int bottom = table.rowAtPoint(convertedPoint);
          if (bottom != -1) {
            setValueIsAdjusting(true);
            table.setRowSelectionInterval(bottom, bottom);
            setValueIsAdjusting(false);
          }
        }
        else {
          Point convertedPoint = SwingUtilities.convertPoint(scroller,
                                                             new Point(1, (size.height - 1) - 2),
                                                             list);
          int bottom = getJList().locationToIndex(convertedPoint);
          setValueIsAdjusting(true);
          getJList().setSelectedIndex(bottom);
          setValueIsAdjusting(false);
        }

        AbstractAction timerAction = new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            autoScrollDown();
          }
          public boolean isEnabled() {
            return true;
          }
        };

        autoScrollTimer = new Timer(100, timerAction);
      }
      autoScrollTimer.start();
    }

    protected void stopAutoScrolling() {
      isAutoScrolling = false;

      if (autoScrollTimer != null) {
        autoScrollTimer.stop();
        autoScrollTimer = null;
      }
    }

    protected void autoScrollUp() {
      if (isShowTable()) {
        int index = table.getSelectedRow();
        if (index > 0) {
          setValueIsAdjusting(true);
          table.setRowSelectionInterval(index - 1, index -1);
          setValueIsAdjusting(false);
          table.ensureRowIsVisible(index - 1);
        }
      }
      else {
        int index = getJList().getSelectedIndex();
        if (index > 0) {
          setValueIsAdjusting(true);
          getJList().setSelectedIndex(index - 1);
          setValueIsAdjusting(false);
          getJList().ensureIndexIsVisible(index - 1);
        }
      }
    }

    protected void autoScrollDown() {
      if (isShowTable()) {
        int index = table.getSelectedRow();
        int lastItem = getJList().getModel().getSize() - 1;
        if (index < lastItem) {
          setValueIsAdjusting(true);
          table.setRowSelectionInterval(index + 1, index + 1);
          setValueIsAdjusting(false);
          table.ensureRowIsVisible(index + 1);
        }
      }
      else {
        int index = getJList().getSelectedIndex();
        int lastItem = getJList().getModel().getSize() - 1;
        if (index < lastItem) {
          setValueIsAdjusting(true);
          getJList().setSelectedIndex(index + 1);
          setValueIsAdjusting(false);
          getJList().ensureIndexIsVisible(index + 1);
        }
      }
    }

    public JdbTable getTable() {
      return table;
    }

    protected JComboBox getComboBox() {
      return comboBox;
    }

    protected JList getJList() {
      return list;
    }

    protected void replaceScroller(JScrollPane scroller) {
      remove(this.scroller);
      this.scroller = scroller;
      add(scroller);
    }

//    public class PropertyChangeHandler implements PropertyChangeListener {
    public class JdbComboPopupPropertyChangeHandler extends BasicComboPopup.PropertyChangeHandler {
      public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if (propertyName.equals("model")) {  
          if (((ComboBoxModel) e.getNewValue()) instanceof JdbComboBox.DBComboBoxModel) {
            if (table == null) {
              table = new JdbTable();
              table.setEditable(false);
            }
            table.setModel((TableModel) getComboBox().getModel());
            configureTable();
            replaceScroller(new TableScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
          }
        }
        super.propertyChange(e);
      }
    }

  }

  public PropertyChangeListener createPropertyChangeListener() {
    return new JdbPropertyChangeHandler();
  }

  public class JdbPropertyChangeHandler extends BasicComboBoxUI.PropertyChangeHandler {
    public void propertyChange(PropertyChangeEvent e) {
      if (e.getPropertyName().equals("fixedCellHeight")) {   
        fixedCellHeight = ((Integer) e.getNewValue()).intValue();
      }
      else if (e.getPropertyName().equals("dropDownWidth")) {   
        dropDownWidth = ((Integer) e.getNewValue()).intValue();
      }
      else {
        super.propertyChange(e);
      }
    }
  }

  protected Dimension getDisplaySize() {
    Dimension size;
    if (fixedCellHeight == -1 || dropDownWidth == -1) {
      size = super.getDisplaySize();
    }
    else {
      size = new Dimension();
    }
    if (fixedCellHeight != -1) {
      size.height = fixedCellHeight;
    }
    if (dropDownWidth != -1) {
      size.width = dropDownWidth;
    }
    return size;
  }

  private boolean useLookAheadComboBoxEditor = true;
  private int fixedCellHeight = -1;
  private int dropDownWidth = -1;
  private int initialIndex = -1;
}
