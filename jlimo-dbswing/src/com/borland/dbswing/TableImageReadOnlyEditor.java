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
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * <p>A <code>TableCellEditor</code> for icons in a <code>JTable</code> or <a  href="JdbTable.html"><code>JdbTable</code></a>.</p>
 *
 * <p>Although it is a <code>TableCellEditor,</code> it does not allow an
 * icon to be edited. Instead, it displays an icon in its own window,
 * sized to display the natural size of the image, or as a maximized
 * window with scrollbars, if necessary. To edit an icon, use <a   href="TableImageEditor.html"><code>TableImageEditor</code></a>
 * instead.</p>
 *
 * <p>To close the icon window, click the window's close button, or press
 * the <kbd>[Esc]</kbd>, <kbd>[Enter]</kbd>, or <kbd>[Space]</kbd> key.</p>
 *
 * <p>To use <code>TableImageReadOnlyEditor</code> as the editor for a
 * column in a <code>JdbTable,</code> set it as the
 * <code>itemEditor</code> property of the corresponding <code>Column</code>:</p>
 *
 * <pre>
 * imageColumn.setItemEditor(new TableImageReadOnlyEditor());
 * </pre>
 */
public class TableImageReadOnlyEditor extends JLabel implements TableCellEditor, Runnable, java.io.Serializable
{
  /**
   * <p>Default constructor.</p>
   */
  public TableImageReadOnlyEditor() {
    setHorizontalAlignment(CENTER);
  }

     /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
   */
  public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected,
                                               int row, int column) {

    if (value != null && value instanceof Icon) {
      icon = (Icon) value;
      setIcon(icon);
      if (dialog == null) {
        dialog = new ImageDialog(DBUtilities.getFrame(table));
      }
      dialog.setIcon(icon);
      new Thread(this).start();
    }

    return this;
  }

  /**
   * <p>Returns the <code>value</code> for the editor. The <code>value</code> is
   * required for implementation of the
   * <code>javax.swing.table.TableCellEditor</code> interface.</p>
   *
   * @return The <code>value</code> for the editor.
   */
  public Object getCellEditorValue() {
    return icon;
  }

 /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code>  interface.</p>
   *
   * @param eventObject An <code>EventObject</code> object.
   * @return If <code>true</code>, the cell can be edited.
   */
  public boolean isCellEditable(EventObject eventObject) {
    if ((eventObject instanceof MouseEvent && ((MouseEvent)eventObject).getClickCount() >= 2) ||
        (eventObject != null && eventObject.getSource() instanceof JdbTable.StartEditAction)) {
      return true;
    }
    return false;
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code>  interface.</p>
   */
  public boolean shouldSelectCell(EventObject eventObject) {
    return true;
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code>  interface.</p>
   */
  public boolean stopCellEditing() {
    return true;
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code>  interface.</p>
   */
  public void cancelCellEditing() {
  }

  /**
   * <p>Required for implementation of <code>java.lang.Runnable</code> interface.</p>
   */
  public void run() {
    dialog.show();
    fireEditingCanceled();
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
   * @param l A <code>CellEditorListener</code> object.
   * @see #removeCellEditorListener
   */
  public void addCellEditorListener(CellEditorListener l) {
    listenerList.add(CellEditorListener.class, l);
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
   *
   * @param l A <code>CellEditorListener</code> object.
   * @see #addCellEditorListener
   */
  public void removeCellEditorListener(CellEditorListener l) {
    listenerList.remove(CellEditorListener.class, l);
  }

  private void fireEditingCanceled() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==CellEditorListener.class) {
        ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
      }
    }
  }

  private Icon icon;
  private ImageDialog dialog;
  private EventListenerList listenerList = new EventListenerList();
  private transient ChangeEvent changeEvent = new ChangeEvent(this);
}


class ImageDialog extends JDialog implements KeyListener {
  JLabel label = new JLabel();
  JScrollPane scrollPane = new JScrollPane();
  Icon icon;
  Dimension dimension = new Dimension();
  Frame frame;

  ImageDialog(Frame frame) {
    super(frame, "", true);

    JPanel p =  new JPanel();
    p.setLayout(new BorderLayout());
    p.add(scrollPane, BorderLayout.CENTER);
    scrollPane.getViewport().add(label);
    getContentPane().add(p);
    pack();
    this.frame = frame;
    setLocationRelativeTo(frame);
    addKeyListener(this);
  }

  void setIcon(Icon icon) {
    this.icon = icon;
    label.setIcon(icon);
    if (icon != null) {
      dimension.height = icon.getIconHeight();
      dimension.width = icon.getIconWidth();
      label.setPreferredSize(dimension);
    }
    pack();
    setLocationRelativeTo(frame);
  }

//  Icon getIcon() {
//    return icon;
//  }

  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE ||
        e.getKeyCode() == KeyEvent.VK_ENTER ||
        e.getKeyCode() == KeyEvent.VK_SPACE) {
      dispose();
    }
  }
}


