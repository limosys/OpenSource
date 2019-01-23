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

import java.beans.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;

import com.borland.dx.dataset.*;

/**
 * <p>Synchronizes the text and/or icon of a <code>JLabel</code> or a
 * subclass of <code>JLabel</code> with the values in a <code>DataSet</code>.
 * <code>DBLabelDataBinder</code> is used to make the dbSwing <code>JdbLabel</code> component data-aware.</p>
 *
 * <p>To use <code>DBLabelDataBinder</code>, set the <code>jLabel</code> property to any
 * <code>JLabel</code> or class which extends the <code>JLabel</code> class. <code>DBLabelDataBinder</code>
 * also binds the <code>alignment, background,
 * foreground</code>, and <code>font</code> properties from those defined on
 * Column <code>columnName</code> (if defined), unless these properties are already explicitly
 * set on the the <code>JLabel</code> itself.  The label's
 * <code>horizontalAlignment</code> and <code>verticalAlignment</code> properties
 * are assumed to be in their default state if they are
 * <code>SwingConstants.LEFT</code> and <code>SwingConstants.CENTER</code>, respectively.</p>
 *
 * <p>Set <code>DBLabelDataBinder's dataSet,
 * columnName</code>, and <code>columnNameIcon</code> properties to indicate
 * the <code>DataSet</code> and <code>Columns</code> from which the text and icon are read.  Note that at least one of the <code>columnName</code> and <code>columnNameIcon</code>
 * properties should be set.  The <code>columnNameIcon</code> property should
 * be the name of a <code>DataSet Column</code> of type <code>OBJECT</code> or <code>INPUTSTREAM</code>.</p>
 *
 * <p><code>DBLabelDataBinder</code> ensures that the label's text is always
 * consistent with the current value of the <code>DataSet Column</code> to which it
 * is attached.  Therefore, as the <code>DataSet</code> is navigated and the text of
 * the label is updated, the label's container (depending on its
 * current <code>LayoutManager</code>) may adjust its layout to accomodate the
 * label's new size.</p>
 *
 * <p>If you explicitly set the label's text while it is still attached
 * the <code>DataSet</code>, the text will be overwritten the next time the <code>DataSet</code>
 * value changes.</p>
 *
 * <p>Usage example:</p>
 *
 *<pre>
 * JLabel jLabel = new JLabel();
 * DBLabelDataBinder DBLabelDataBinder = new DBLabelDataBinder();
 *
 * // attach the label to DBLabelDataBinder
 * DBLabelDataBinder.setJLabel(jLabel);
 * // set the target DataSet and Column
 * DBLabelDataBinder.setDataSet(dataSet);
 * DBLabelDataBinder.setColumnName("Luminescence");
 * DBLabelDataBinder.setColumnNameIcon("JPG Image");
 *</pre>
 *
 * @see JdbLabel
 */
public class DBLabelDataBinder
  implements PropertyChangeListener, AccessListener,
             DataChangeListener, NavigationListener,
             ColumnAware, Designable, MouseListener,
             java.io.Serializable, FocusListener
{

  /**
   * <p>Constructs a <code>DBLabelDataBinder</code>. Calls the <code>null</code> constructor of its superclass.</p>
   */
  public DBLabelDataBinder() {
  }

  /**
   * <p>Constructs a <code>DBLabelDataBinder</code> that makes the specified <code>JLabel</code> component data aware. Calls the <code>null</code> constructor of its superclass. </p>
   *
   * @param label The <code>JLabel</code> that provides the label.
   */
  public DBLabelDataBinder(JLabel label) {
    setJLabel(label);
  }

  //
  // DBLabelDataBinder properties
  //



  /**
   * <p>Sets the <code>JLabel</code> that <code>DBLabelDataBinder</code> makes data-aware. <code>DBLabelDataBinder</code> also binds the <code>alignment, background, foreground</code>, and <code>font</code> properties from those defined on <code>Column columnName</code> (if defined), unless these properties are explicitly set on <code>JLabel</code> itself. </p>
  *
  * @param label The <code>JLabel</code> <code>DBLabelDataBinder</code> makes data-aware.
  * @see #getJLabel
*/
  public void setJLabel(JLabel label) {
    if (this.label != null && this.label != label) {
      this.label.removePropertyChangeListener(this);
      this.label.removeMouseListener(this);
    }
    this.label = label;

    if (label != null) {
      label.addPropertyChangeListener(this);
    }
    bindColumnProperties();
  }

  /**
   * <p>Returns the <code>JLabel</code> that <code>DBLabelDataBinder</code> makes data-aware. </p>
  *
  * @return The <code>JLabel</code> <code>DBLabelDataBinder</code> makes data-aware.
  * @see #setJLabel
*/
  public JLabel getJLabel() {
    return label;
  }

  //
  // ColumnAware interface implememtation
  //

  /**
   * <p>Sets the <code>DataSet</code> from which the label's data is be read.</p>
   *
   * @param dataSet The <code>DataSet</code> from which the label's data is read.
   * @see #getDataSet
   * @see #setColumnName
   * @see #setColumnNameIcon
   */
  public void setDataSet(DataSet dataSet) {
    if (labelSupport.dataSet != null) {
      labelSupport.dataSet.removeNavigationListener(this);
    }
    labelSupport.setDataSet(dataSet);
    if (dataSet != null) {
      labelSupport.dataSet.addNavigationListener(this);
    }
    bindColumnProperties();
  }

  /**
   * <p>Returns the <code>DataSet</code> from which the label's data is written.</p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   * @see #getColumnName
   * @see #getColumnNameIcon
   */

  public DataSet getDataSet() {
    return labelSupport.dataSet;
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which the label's text is read.</p>
   *
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setColumnNameIcon
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    labelSupport.setColumnName(columnName);
    bindColumnProperties();
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> from which the label's
   * text is read.</p>
   *
   * @return The column name.
   * @see #setColumnName
   * @see #getColumnNameIcon
   * @see #getDataSet
   */
  public String getColumnName() {
    return labelSupport.columnName;
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from which the label's
   * icon is read.</p>
   *
   * @param columnNameIcon The column name of the <code>DataSet</code> from which the label's icon is read.
   * @see #getColumnNameIcon
   * @see #setColumnName
   * @see #setDataSet
   */
  public void setColumnNameIcon(String columnNameIcon) {
    labelSupport.setColumnNameIcon(columnNameIcon);
    bindColumnProperties();
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> to which the label's
   * icon is written.</p>
   *
   * @return The column name of the <code>DataSet</code> to which the label's icon is written.
   * @see #setColumnNameIcon
   * @see #getColumnName
   * @see #getDataSet
   */
  public String getColumnNameIcon() {
    return labelSupport.columnNameIcon;
  }

  //
  // java.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("ancestor")) {  
      bindColumnProperties();
    }
  }

  /**
   * <p>Writes the image (assumed to be) contained in the specified <code>inputStream</code>
   * to the <code>columnOrdinalIcon</code> column.</p>
   *
   * @param inputStream The <code>inputStream</code> to write the image to.
   */
  public void putIcon(InputStream inputStream) {
    try {
      if (canPutIcon()) {
        labelSupport.putIcon(inputStream);
      }
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(labelSupport.dataSet, label, e);
    }
  }

  private boolean canPutIcon() throws DataSetException {
    return labelSupport.isValidDataSetState() && labelSupport.columnOrdinalIcon != -1 &&
      labelSupport.dataSet.getColumn(labelSupport.columnNameIcon).isEditable();
  }

  // synchronizes the text of the label with the current
  // DataSet value
  private void updateLabelDisplay() {
    if (labelSupport.isValidDataSetState()) {
      if (labelSupport.columnOrdinal != -1) {
        label.setText(labelSupport.getFormattedString());
      }
      if (labelSupport.columnOrdinalIcon != -1) {
        label.setIcon(labelSupport.getIcon());
      }
    }
  }

  //
  // com.borland.dx.dataset.NavigationListener interface implementation
  //

  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // navigation means we've moved on to a different row, and we need
    // to update the label's text to the current value.
    updateLabelDisplay();
  }

  //
  // com.borland.dx.dataset.DataChangeListener interface implementation
  //

  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // depending upon the event, we might have to update the current
    // label.
    int affectedRow = event.getRowAffected();
    boolean affectedOurRow = (affectedRow == labelSupport.dataSet.getRow()) ||
      affectedRow == -1;
    if (affectedOurRow) {
      updateLabelDisplay();
    }
  }

  public void postRow(DataChangeEvent event) throws Exception {
    // usually needs to get the pending value from the control and set it,
    // but since label's can't be edited, this isn't necessary
  }

  //
  // Code to bind visual Column properties to label
  //

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds alignment, font, foreground, and background properties from column
  // if not explicitly set on label
  private void bindColumnProperties() {
    if (oldLabel != null) {
      oldLabel.removeFocusListener(this);
      oldLabel = null;
    }

    if (label != null && label.isDisplayable()) {
      // ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
      // again on the OPEN access event
      rebindColumnProperties = false;
      // will resync our internal state if the dataSet or column has changed
      labelSupport.lazyOpen();

      updateLabelDisplay();

      if (labelSupport.isValidDataSetState()) {

        label.addFocusListener(this);
        oldLabel = label;

        if (labelSupport.columnOrdinal != -1) {
          Column column = labelSupport.getColumn();
          if (label.getHorizontalAlignment() == SwingConstants.LEFT) {
            label.setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(column.getAlignment(), true));
          }
          if (label.getVerticalAlignment() == SwingConstants.CENTER) {
            label.setVerticalAlignment(DBUtilities.convertJBCLToSwingAlignment(column.getAlignment(), false));
          }
          if (isDefaultProperty(label.getBackground())) {
            if (column.getBackground() != null) {
              label.setBackground(column.getBackground());
            }
          }
          if (isDefaultProperty(label.getForeground())) {
            if (column.getForeground() != null) {
              label.setForeground(column.getForeground());
            }
          }
          if (isDefaultProperty(label.getFont())) {
            if (column.getFont() != null) {
              label.setFont(column.getFont());
            }
          }
        }
      }
    }
  }

  //
  // com.borland.dx.dataset.AccessListener interface implementation
  //

  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.CLOSE) {
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        dataSetEventsEnabled = false;
      }
      else {
        updateLabelDisplay();
      }
      if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        rebindColumnProperties = true;
      }
    }
    else {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      if (event.getReason() == AccessEvent.UNSPECIFIED || rebindColumnProperties ||
          event.getReason() == AccessEvent.DATA_CHANGE) {
        bindColumnProperties();
      }
    }
  }

  /**
   * <p>Sets whether the label's icon can be changed at runtime.
   * If <code>iconEditable</code> is <code>true</code>, double-clicking the label
   * displays a dialog box prompting the user to select
   * an image file from which to set the icon.
   * If a valid image file is selected, the label's icon is
   * updated and saved to the <code>DataSet</code> column.</p>
   *
   * <p>This property is <code>false</code> by default.</p>
   *
   * <p>Note that setting the icon directly using <code>setIcon()</code> will
   * not save the icon to the <code>DataSet</code>.  Use <code>DataSet's</code>
   * <code>setObject()</code> or <code>setInputStream()</code> methods to save an
   * icon programmatically to a <code>DataSet</code> column.</p>
   *
   * @param iconEditable If <code>true</code>, the icon can be edited.
   * @see #isIconEditable
   */
  public void setIconEditable(boolean iconEditable) {
    if (this.iconEditable != iconEditable) {
      this.iconEditable = iconEditable;
      if (iconEditable) {
        if (label != null) {
          label.addMouseListener(this);
        }
      }
      else {
        if (label != null) {
          label.removeMouseListener(this);
        }
      }
    }
  }

  /**
   * <p>Returns whether the label's icon can be changed at runtime.
   * If <code>iconEditable</code> is <code>true</code>, double-clicking the label
   * displays a dialog box prompting the user to select
   * an image file from which to set the icon.
   * If a valid image file is selected, the label's icon is
   * updated and saved to the <code>DataSet</code> column.</p>
   *
   * @return If <code>true</code>, the icon can be edited.
   * @see #setIconEditable
   */

  public boolean isIconEditable() {
    return iconEditable;
  }

  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mouseClicked(MouseEvent e) {
    try {
      if (e.getClickCount() == 2 && iconEditable) {
        if (fileChooser == null) {
          Frame frame = DBUtilities.getFrame(label);
          frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          fileChooser = new JFileChooser();
          fileChooser.setAccessory(new ImagePreviewer(fileChooser, frame));
          fileChooser.setMultiSelectionEnabled(false);
          fileChooser.resetChoosableFileFilters();
          javax.swing.filechooser.FileFilter fileFilter = (javax.swing.filechooser.FileFilter) new ImageFileFilter();
          fileChooser.addChoosableFileFilter(fileFilter);
          // workaround for Java bug id: 4163841
          // to remove the 'all files' filter, I had to call setFileFilter and then remove the
          // 'all files' filter (dcy).
          fileChooser.setFileFilter(fileFilter);
          fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
          fileChooser.setApproveButtonText(Res._LoadImageFile);     
          frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        if (fileChooser.showOpenDialog(label) == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          if (file != null && file.exists()) {
            Frame frame = DBUtilities.getFrame(label);
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            InputStream inputStream = null;
            try {
              inputStream = new BufferedInputStream(new FileInputStream(file));
              //          inputStream.mark(Integer.MAX_VALUE);
              boolean bmpFile = DBUtilities.isBMPFile(inputStream);
              if (bmpFile || DBUtilities.isGIForJPGFile(inputStream)) {
                if (canPutIcon()) {
                  labelSupport.putIcon(inputStream);
                }
                else {
                  if (bmpFile) {
                    Image bmpImage = DBUtilities.makeBMPImage(inputStream);
                    if (bmpImage != null) {
                      label.setIcon(new ImageIcon(bmpImage));
                    }
                    else {
                      JOptionPane.showMessageDialog(label,
                                                    Res._InvalidImageFile,     
                                                    null,
                                                    JOptionPane.INFORMATION_MESSAGE);
                    }
                  }
                  else {
                    label.setIcon(new ImageIcon(file.getCanonicalPath()));
                  }
                }
              }
              else {
                JOptionPane.showMessageDialog(label,
                                              Res._InvalidImageFile,     
                                              null,
                                              JOptionPane.INFORMATION_MESSAGE);
              }
            }
            catch (Exception ex) {
              //IOException or FileNotFoundException
              DBExceptionHandler.handleException(labelSupport.dataSet, label, ex);
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
          else {
            JOptionPane.showMessageDialog(label,
                                          Res._FileNotExist,     
                                          "",  
                                          JOptionPane.ERROR_MESSAGE);
            label.repaint();
          }
        }
      }
    }
    catch (Exception ex) {
      DBExceptionHandler.handleException(labelSupport.dataSet, label, ex);
    }
  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(label, labelSupport.dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  /** JLabel to be bound to DataSet */
  private JLabel label;

  /** previously bound JLabel */
  private JLabel oldLabel;

  /** support for ColumnAware implementation */
  private DBLabelSupport labelSupport = new DBLabelSupport(this);

  /** whether to allow icons to be loaded at runtime */
  private boolean iconEditable = false;

  /** file chooser used to load icon file */
  private JFileChooser fileChooser;

  private boolean rebindColumnProperties;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
