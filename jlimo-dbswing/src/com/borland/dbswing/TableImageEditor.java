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
import java.io.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * <p>The default <code>TableCellEditor</code> for icons in a <a  href="JdbTable.html"><code>JdbTable</code></a>.</p>
 *
 * <p><code>TableImageEditor</code> displays a <code>JFileChooser</code> dialog with
 * a preview window showing the current cell value or the currently selected file.
 * To display the current cell value without providing the ability to change
 * its value, explicitly set the column's cell editor to  <a href="TableImageReadOnlyEditor.html"><code>TableImageReadOnlyEditor</code></a>.</p>
 *
 */
public class TableImageEditor extends JLabel implements TableCellEditor, Runnable, java.io.Serializable
{
  /**
   * <p>Default constructor.</p>
   */
  public TableImageEditor() {
    setHorizontalAlignment(CENTER);
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
   */
  public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected,
                                               int row, int column) {

    Icon icon = null;
    if (value != null && value instanceof Icon) {
      icon = (Icon) value;
      setIcon(icon);
    }

    if (dialog == null) {
      frame = DBUtilities.getFrame(table);
      dialog = new JFileChooser();
      dialog.setAccessory(imagePreviewer = new ImagePreviewer(dialog, frame));
      dialog.setApproveButtonText(Res._OKBtn);     
      dialog.setDialogTitle(Res._LoadImageFile);     
      dialog.setFileFilter(new ImageFileFilter());
    }
    dialog.setSelectedFile(null);

    imagePreviewer.setIcon(icon);

    new Thread(this).start();

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
    if (imagePreviewer != null) {
      return imagePreviewer.getInputStream();
    }
    return null;
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
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
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
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
    if (dialog.showDialog(frame, null) == JFileChooser.APPROVE_OPTION) {
      dialog.setPreferredSize(dialog.getSize());
      fireEditingStopped();
    }
    else {
      dialog.setPreferredSize(dialog.getSize());
      fireEditingCanceled();
    }
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellEditor</code> interface.</p>
   *
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

  private void fireEditingStopped() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==CellEditorListener.class) {
        ((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
      }
    }
  }

  private void fireEditingCanceled() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==CellEditorListener.class) {
        ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
      }
    }
  }

  private Frame frame;
  private JFileChooser dialog;
  private EventListenerList listenerList = new EventListenerList();
  private transient ChangeEvent changeEvent = new ChangeEvent(this);
  private ImagePreviewer imagePreviewer;
  private static IntlSwingSupport intlSwingSupport = new IntlSwingSupport();
}

class ImagePreviewer extends JPanel implements PropertyChangeListener {
  File file;
  Frame frame;
  JScrollPane scrollPane = new JScrollPane();
  JLabel label = new JLabel();
  Icon icon;

  ImagePreviewer(JFileChooser fc, Frame frame) {
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    scrollPane.getViewport().add(label);
//    setPreferredSize(new Dimension(200, 200));
    fc.addPropertyChangeListener(this);
    label.setHorizontalAlignment(JLabel.CENTER);
    this.frame = frame;
  }

  void setIcon(Icon icon) {
    this.icon = icon;
    label.setIcon(icon);
  }

  Icon getIcon() {
    return icon;
  }

  InputStream getInputStream() {
    if (file != null && file.exists()) {
      frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      InputStream inputStream = null;
      try {
        inputStream = new ByteArrayInputStream(DBUtilities.getByteArrayFromStream(new BufferedInputStream(new FileInputStream(file))));
        return inputStream;
      }
      catch (Exception ex) {
        //IOException or FileNotFoundException
        DBExceptionHandler.handleException(ex);
      }
      finally {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (inputStream != null) {
          try {
            inputStream.close();
          }
          catch (IOException ex) {
          }
        }
      }
    }
    return null;
  }

  public void propertyChange(PropertyChangeEvent e) {
    String propertyName = e.getPropertyName();
    if (propertyName == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
      file = (File) e.getNewValue();
      if (file != null && file.exists()) {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        InputStream inputStream = null;
        try {
          if ( !file.isDirectory() ) {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            if (DBUtilities.isBMPFile(inputStream)) {
              Image bmpImage = DBUtilities.makeBMPImage(inputStream);
              label.setIcon(bmpImage != null ? new ImageIcon(bmpImage) : null);
            }
            else if (DBUtilities.isGIForJPGFile(inputStream)) {
              label.setIcon(new ImageIcon(file.getCanonicalPath()));
            }
            else {
              label.setIcon(null);
            }
          }
          else {
            label.setIcon(null);
          }
        }
        catch (Exception ex) {
          //IOException or FileNotFoundException
          DBExceptionHandler.handleException(ex);
        }
        finally {
          frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          if (inputStream != null) {
            try {
              inputStream.close();
            }
            catch (IOException ex) {
            }
          }
        }
      }
    }
  }
}
