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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.ColumnPaintListener;
import com.borland.dx.dataset.CustomPaintSite;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.DataSetView;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.NavigationEvent;
import com.borland.dx.dataset.NavigationListener;
import com.borland.dx.dataset.PickListDescriptor;
import com.borland.dx.dataset.ReadRow;
import com.borland.dx.dataset.Variant;

/**
 * <p>A data-aware extension to the <code>JList</code>
 * component. <code>JdbList</code> displays a list of
 * choices from which a user may choose a single value.
 * The selected value is written to a column of a
 * <code>DataSet</code> and retrieved using the standard
 * Swing <code>ListSelectionModel</code> methods. When
 * <code>JdbList</code> is bound to a <code>DataSet</code>
 * and <code>DataSet Column</code>, the currently selected
 * choice always reflects the value of the corresponding
 * column in the current <code>DataSet</code> row.</p>
 *
 * <p>There are several ways to fill the list presented by
 * <code>JdbList</code>:</p>
 *
 * <ul>
 * <li>Set <code>JdbList</code>'s <code>items</code> property with an array of <code>String</code>s or <code>Object</code>s to display a static list of choices.</li>
 * <li>Use the standard <code>JList</code> model methods to populate the list from a <code>Vector</code>, <code>Object</code> array, or any <code>ListModel</code> implementation.</li>
 * <li>Retrieve the list of choices from another <code>DataSet</code> by setting the <code>picklist</code> property on <code>JdbList</code>'s <code>columnName</code> <code>Column</code>. The choices displayed by <code>JdbList</code> are always kept in synchronization with changes in the source <code>DataSet</code>. By specifying the <code>picklist</code> property accordingly, it's possible to display a value from one column but save a value from a different column or even save the values from more than one column.</li>
 * <li>Instantiate a <code>DBListDataBinder</code> and set its properties to connect it with <code>JdbList</code> and to a <code>JListModel</code> and <code>JListSelectionModel</code>.</li>
 * </ul>
 *
 * <p>You should avoid setting both the column's
 * <code>picklist</code> property and
 * <code>JdbList</code>'s <code>items</code> property on
 * the same list. If both properties are set in the UI
 * Designer, the <code>picklist</code> setting takes
 * precedence at runtime. When the list is displayed,
 * however, the most recently set property setting always
 * takes precedence. </p>
 *
 * <p>Note that because a list can display one column only
 * at a time, if more than one column is specified as a
 * display column in the picklist definition, only the
 * first display column is displayed by
 * <code>JdbList</code>. For more information about
 * picklists, see  <code>com.borland.dx.dataset.PickListDescriptor</code> class.</p>
 *
 * <p>Ordinarily, the selection in a data-aware
 * <code>JdbList</code> is the current value of the column
 * it is bound to. If this value is not in its list, a
 * <code>JdbList</code> has no selected value by default,
 * but you can modify this behavior by changing the value
 * of the <code>unknownDataValueMode</code> property.</p>
 *
 * @see JdbNavList
*/
public class JdbList extends JList
  implements ListSelectionListener, AccessListener,
             NavigationListener, DataChangeListener,
             PropertyChangeListener, DBDataBinder,
             ColumnAware, java.io.Serializable, FocusListener
{


/**
 * <p>Constructs a <code>JdbList</code> component by calling the constructor of its superclass.</p>
 */
 public JdbList() {
    super();
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbList</code> component by calling the constructor of its superclass and passing to it a <code>ListModel</code>.</p>
  *
  * @param dataModel The <code>ListModel</code> that contains the data in the list displayed by <code>JdbList</code>.
  *
  */
  public JdbList(ListModel dataModel) {
    super(dataModel);
    commonInit();
  }

  /**
   * <p>Constructs a <code>JdbList</code> component by calling the constructor of its superclass that takes an array of <code>Object</code>s as a parameter.</p>
  *
  * @param listData An array of <code>Object</code>s that makes up the list displayed in the <code>JdbList</code>.
  */
  public JdbList(final Object [] listData) {
    super(listData);
    commonInit();
  }

  /**
   * <p>Constructs a <code>JdbList</code> component by calling the constructor of its superclass that takes a <code>Vector</code> as a parameter.</p>
  *
  * @param listData A <code>Vector</code> that makes up the list displayed in the <code>JdbList</code>.
  */
  public JdbList(final Vector listData) {
    super(listData);
    commonInit();
  }

  /**
   * <p>Used to initialize <code>JdbList</code> with the
   * same defaults, regardless of the constructor used. A
   * newly instantiated <code>JdbList</code> differs from
   * a <code>JList</code> in that it always has
   * SINGLE_SELECTION mode as its
   * <code>selectionMode</code>.</p>
   *
   * <p>Also, the default cell renderer for a
   * <code>JdbList</code> is a
   * <code>JdbList.DBCellRenderer</code>
   * rather than a DefaultListCellRenderer.</p>
   */
  protected void commonInit() {
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setCellRenderer(new DBCellRenderer());
    addPropertyChangeListener(this);
  }

  /**
   * <p>Sets the list of items to select from. Setting
   * this property replaces the existing model from which
   * the list is built.</p>
   *
   * @param items The list of items to select from.
   * @see #getItems
   */
  public synchronized void setItems(String [] items) {
    setItems((Object []) items);
  }


  /**
   * <p>Sets a static array of <code>Objects</code> to
   * display in the list. Setting this property replaces
   * the existing model from which the list is built.</p>
   *
   * @param items The list of items to select from.
   * @see #getItems
   */

  public synchronized void setItems(Object [] items) {
    this.items = items;

    if (items == null || items.length == 0) {
      setListData(new Object [] {});
    }
    else {
    // setting the model has the side effect of resetting the
    // picklist model to null on the model property change
      setListData(items);
    }
  }

  /**
   * <p>Returns the items displayed in the list as an
   * array of <code>Strings</code>.</p>
   *
   * @return The items displayed in the list.
   * @see #setItems
   */
  public synchronized String [] getItems() {
    if (items == null) {
      return new String[0];
    }

    String [] stringifiedItems = new String[getModel().getSize()];

    Object element;
    for (int i = 0; i < stringifiedItems.length; i++) {
      element = getModel().getElementAt(i);
      stringifiedItems[i] = element == null ? "" : element.toString();  
    }
    return stringifiedItems;
  }

  /**
   * <p>Sets a <code>DataSet</code> from which the current
   * list selection is read and to which the current list
   * selection is written.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    if (columnAwareSupport.dataSet != null) {
      columnAwareSupport.dataSet.removeNavigationListener(this);
      getSelectionModel().removeListSelectionListener(this);
      removeFocusListener(this);
    }
    columnAwareSupport.setDataSet(dataSet);
    if (dataSet != null) {
      columnAwareSupport.dataSet.addNavigationListener(this);
      getSelectionModel().addListSelectionListener(this);
      addFocusListener(this);
    }
    bindColumnProperties();
  }

  /**
   * <p>Returns the <code>DataSet</code> from which the current
   * list selection is read and to which the current list
   * selection is written.</p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return columnAwareSupport.dataSet;
  }

  /**
   * <p>Sets the name of a <code>Column</code> in the
   * <code>DataSet</code> from which the current list
   * selection is read and to which the current list
   * selection is written. Setting this property replaces
   * the current <code>ListModel</code> from which the
   * list is built.</p>
   *
   * @param columnName The name of a <code>Column</code> in the <code>DataSet</code>.
   * @see #getColumnName
   */
  public void setColumnName(String columnName) {
    columnAwareSupport.setColumnName(columnName);
    bindColumnProperties();
  }

  /**
   * <p>Returns the name of a <code>Column</code> in the
   * <code>DataSet</code> from which the current list
   * selection is read and to which the current list
   * selection is written. </p>
   *
   * @return columnName The name of a <code>Column</code> in the <code>DataSet</code>.
   * @see #setColumnName
   */
  public String getColumnName() {
    return columnAwareSupport.columnName;
  }

  /**
   * <p>Sets the policy for synchronizing a list selection
   * with its <code>DataSet</code> value when the value
   * can't be found in the list.  </p>
   *
   * @param mode Possible values are:
   * <ul>
   * <li>DEFAULT - No item is selected in the list.</li>
   * <li>DISABLE_COMPONENT - The component is disabled.</li>
   * <li>CLEAR_VALUE - The value in the <code>DataSet</code> is cleared.</li>
   * </ul>
   * @see #getUnknownDataValueMode
   * @see DBListDataBinder
   */
  public void setUnknownDataValueMode(int mode) {
    this.mode = mode;
  }

  /**
   * <p>Returns the policy for synchronizing a list selection
   * with its <code>DataSet</code> value when the value
   * can't be found in the list.  </p>
   *
   * @return Possible values are:
   * <ul>
   * <li>DEFAULT - No item is selected in the list.</li>
   * <li>DISABLE_COMPONENT - The component is disabled.</li>
   * <li>CLEAR_VALUE - The value in the <code>DataSet</code> is cleared.</li>
   * </ul>
   * @see #setUnknownDataValueMode
   * @see DBListDataBinder
   */

  public int getUnknownDataValueMode() {
    return mode;
  }

  //
  // javax.swing.event.ListSelectionListener interface implementation
  //

  /**
   * Called whenever the value of the selection changes.
   * @param e the event that characterizes the change.
   */
  public void valueChanged(ListSelectionEvent e) {
    if (!ignoreValueChange && !e.getValueIsAdjusting()) {
      int index = getSelectedIndex();

      // check that the index is really within the range of the model
      if (index > -1 && index < (getModel().getSize())) {

        columnAwareSupport.lazyOpen();

        if (columnAwareSupport.isValidDataSetState() && columnAwareSupport.getColumn().isEditable()) {
          if (items == null && pickListView != null) {
            try {
              pickListView.goToRow(index);
              ReadRow.copyTo(sourceColumns, pickListView, targetColumns, columnAwareSupport.dataSet);
            }
            catch (DataSetException x) {
              DBExceptionHandler.handleException(pickListView, this, x);
            }
          }
          else {
            columnAwareSupport.setObject(getModel().getElementAt(index));
          }
        }
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
    // to update the selected item in the list.
    updateSelectedListValue();
  }

  // finds the current value and sets it as the current selection
  // in the list.  if we're using a DBListModel, we can use the DataSet
  // lookup to get the row index quickly.  Otherwise, we have to sequentially
  // search the entire model.
  private void updateSelectedListValue() {
    int newIndex = -1;

    if (items == null && pickListView != null) {
      try {
        // make a locateRow to navigate the picklist DataSet
        if (locateRow == null) {
          locateRow = new DataRow(pickListView, sourceColumns);
        }

        ReadRow.copyTo(targetColumns, columnAwareSupport.dataSet, sourceColumns, locateRow);

        if (pickListView.locate(locateRow, Locate.FIRST)) {
          newIndex = pickListView.getRow();
        }
      }
      catch (DataSetException e) {
        DBExceptionHandler.handleException(pickListView, this, e);
      }
    }
    else {
      int lastRow = getModel().getSize();
      Object locateValue = columnAwareSupport.getVariant().getAsObject();
      for (int index = 0; index < lastRow; index++) {
        if (getModel().getElementAt(index).equals(locateValue)) {
          newIndex = index;
          break;
        }
      }
    }

    ignoreValueChange = true;
    if (newIndex == -1) {
      if (mode == CLEAR_VALUE && !columnAwareSupport.isNull()) {
        columnAwareSupport.lazyOpen();
        columnAwareSupport.resetValue();
      }
      else if (mode == DISABLE_COMPONENT) {
        setEnabled(false);
      }
      else {
        getSelectionModel().clearSelection();
      }
    }
    else {
      if (mode == DISABLE_COMPONENT && !isEnabled()) {
        setEnabled(true);
      }
      getSelectionModel().setSelectionInterval(newIndex, newIndex);
      ensureIndexIsVisible(newIndex);
    }
    ignoreValueChange = false;
  }

  //
  // com.borland.dx.dataset.DataChangeListener interface implementation
  //
  public void dataChanged(DataChangeEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // depending upon the event, we might have to update the currently
    // selected list value.
    int affectedRow = event.getRowAffected();
    boolean affectedOurRow = (affectedRow == columnAwareSupport.dataSet.getRow()) ||
      affectedRow == -1;
    if (affectedOurRow) {
      updateSelectedListValue();
    }
  }

  public void postRow(DataChangeEvent event) throws Exception {
    // usually needs to get the pending value from the control and set it,
    // but since lists are (usually) not editable directly, this isn't necessary
  }

  //
  // com.borland.dx.dataset.AccessListener interface implementation
  //
  public void accessChange(AccessEvent event) {
    // our target dataset
    if (event.getSource() == columnAwareSupport.getDataSet()) {
      if (event.getID() == AccessEvent.CLOSE) {
        if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
          dataSetEventsEnabled = false;
          return;
        }
        else {
          getSelectionModel().clearSelection();
        }
        if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
          rebindColumnProperties = true;
        }
      }
      else {
//        if (event.getReason() == AccessEvent.DATA_CHANGE) {
          dataSetEventsEnabled = true;
//        }
        if (event.getReason() == AccessEvent.UNSPECIFIED || rebindColumnProperties || event.getReason() == AccessEvent.DATA_CHANGE) {
          bindColumnProperties();
        }
      }
    }
    else if (event.getSource() == pickListView) {
      if (event.getID() == AccessEvent.CLOSE) {
        // if the structure of the picklist changed, we need to
        // recreate our (cached) DataSetView.
        rebuildPickListView = true;
        // but don't need to rebuild if there's only a property change
        if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
          rebuildPickListView = false;
        }
      }
      else {
        // OPEN event
        if (rebuildPickListView) {
          rebuildPickListView = false;
          setPickListModel(pickListView);
        }
      }
    }
  }

  // calls setPrototypeCellValue appropriately based on Column dataType
  private void setPrototype(int charWidth) {
    StringBuffer sb = new StringBuffer(charWidth);
    for (int index = 0; index < charWidth; index++) {
      sb.append('M');  
    }
    setPrototypeCellValue(sb.toString());
  }

  //
  // javax.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("ancestor")) {  
      bindColumnProperties();
    }
    else if (e.getPropertyName().equals("selectionModel")) {   
      if (e.getOldValue() != null) {
        ((ListSelectionModel) e.getOldValue()).removeListSelectionListener(this);
      }
      if (e.getNewValue() != null) {
        ((ListSelectionModel) e.getNewValue()).addListSelectionListener(this);
      }
    }
    else if (e.getPropertyName().equals("model")) {   
      if (!ignoreModelChange) {
        setPickListModel(null);
      }
    }
  }

  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds font, foreground, and background properties from column
  // if not explicitly set on list
  // also opens picklist dataset and sets it as model
  private void bindColumnProperties() {
    if (isDisplayable()) {
      // ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
      // again on the OPEN access event
      rebindColumnProperties = false;
      // will resync our internal state if the dataSet or column has changed
      columnAwareSupport.lazyOpen();

      // if we have a valid column, merge its properties
      if (columnAwareSupport.isValidDataSetState()) {
        Column column = columnAwareSupport.getColumn();

        if (isDefaultProperty(getBackground())) {
          if (column.getBackground() != null) {
            setBackground(column.getBackground());
          }
        }
        if (isDefaultProperty(getForeground())) {
          if (column.getForeground() != null) {
            setForeground(column.getForeground());
          }
        }
        if (isDefaultProperty(getFont())) {
          if (column.getFont() != null) {
            setFont(column.getFont());
          }
        }

        if (column.getItemPainter() instanceof ListCellRenderer) {
          setCellRenderer((ListCellRenderer) column.getItemPainter());
        }

        if (items == null) {
          PickListDescriptor pickList = columnAwareSupport.getColumn().getPickList();
          if (pickList != null) {
            DataSet pickListDataSet = pickList.getPickListDataSet();
            if (pickListDataSet != null) {
              try {
                if (!pickListDataSet.isOpen()) {
                  pickListDataSet.open();
                }
                setPickListModel(pickListDataSet.cloneDataSetView());
              }
              catch (DataSetException e) {
                DBExceptionHandler.handleException(pickListDataSet, this, e);
                setPickListModel(null);
              }
            }
          }
        }

        if (isEnabled()) {
          setEnabled(column.isEditable());
        }
        updateSelectedListValue();
      }
    }
  }

  // pickListView should be null or already open when passed as a parameter
  private void setPickListModel(DataSetView pickListView) {

    if (this.pickListView != null) {
      this.pickListView.removeAccessListener(this);
      if (this.pickListView != pickListView) {
        try {
          this.pickListView.close();
          locateRow = null;
        }
        catch (DataSetException e) {
          DBExceptionHandler.handleException(this.pickListView, this, e);
        }
      }
      // should be after close so dbListModel gets AccessEvent.CLOSE event
      if (dbListModel != null) {
        dbListModel.setDataSet(null);
        dbListModel.setColumnName(null);
      }
    }

    this.pickListView = pickListView;

    PickListDescriptor pickList;
    if (columnAwareSupport.isValidDataSetState() &&
        (pickList = columnAwareSupport.getColumn().getPickList()) != null && pickListView != null) {

      pickListView.addAccessListener(this);

      if (dbListModel == null) {
        dbListModel = new DBListModel();
      }

      dbListModel.setDataSet(pickListView);

      String [] displayColumns = pickList.getPickListDisplayColumns();
      sourceColumns = pickList.getPickListColumns();
      targetColumns = pickList.getDestinationColumns();

      if (displayColumns.length > 0) {
        dbListModel.setColumnName(displayColumns[0]);
      }
      else if (sourceColumns.length > 0) {
        // if a display column hasn't been set, then try to use the first
        // source column
        dbListModel.setColumnName(sourceColumns[0]);
      }
      // otherwise, the columnName of the model will be null, and
      // no model data will be created

      Column column;
      if (dbListModel.getColumnName() != null &&
          (column = pickListView.hasColumn(dbListModel.getColumnName())) != null) {
        setPrototype(column.getWidth());
      }

      ignoreModelChange = true;
      setModel(dbListModel);
      ignoreModelChange = false;
    }

  }

  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    if (ps == null || ps.width < 5 || ps.height < 5) {
      ps = ps != null ? ps : new Dimension(100, 100);
      ps.width = ps.width > 100 ? ps.width : 100;
      ps.height = ps.height > 100 ? ps.height : 100;
    }
    return ps;
  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(this, columnAwareSupport.dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  public static class DBCellRenderer extends JLabel implements ListCellRenderer, CustomPaintSite, java.io.Serializable {
    private int alignment;
    private Border noFocusBorder;
    private int defaultAlignment;
    private Color defaultForeground;
    private Color defaultBackground;
    private Font defaultFont;
    private Border defaultBorder;

    public DBCellRenderer() {
      super();
      noFocusBorder = new EmptyBorder(1, 1, 1, 1);
      setOpaque(true);
      setBorder(noFocusBorder);
    }

    // implements javax.swing.ListCellRenderer
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                   boolean isSelected, boolean cellHasFocus) {

      setComponentOrientation(list.getComponentOrientation());

      if (isSelected) {
        setDefaultForeground(list.getSelectionForeground());
        setDefaultBackground(list.getSelectionBackground());
      }
      else {
        setDefaultForeground(list.getForeground());
        setDefaultBackground(list.getBackground());
      }
      setDefaultFont(list.getFont());
      setDefaultBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); 

      if (value instanceof Icon) {
        setIcon((Icon)value);
      }
      else {
        if (list.getModel() instanceof DBListModel) {
          DBListModel listModel = (DBListModel) list.getModel();
          Variant variantValue = listModel.getVariantElementAt(index);
          Variant variantCopy;
          if (variantValue != null) {
            variantCopy = (Variant) variantValue.clone();
            setDefaultAlignment(listModel.columnAwareSupport.getColumn().getAlignment());
            setText(listModel.columnAwareSupport.getColumn().format(variantValue));
            ColumnPaintListener columnPaintListener = listModel.columnAwareSupport.getColumn().getColumnPaintListener();
            if (columnPaintListener != null) {
              columnPaintListener.painting(listModel.getDataSet(),
                                           listModel.columnAwareSupport.getColumn(),
                                           index, variantValue, (CustomPaintSite) this);
              if (!variantValue.equals(variantCopy)) {
                setText(listModel.columnAwareSupport.getColumn().format(variantValue));
              }
              if (isSelected) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
              }
            }
          }
        }
        else {
          setText((value == null) ? "" : value.toString());
        }
      }

      setEnabled(list.isEnabled());

      return this;
    }

    public void setDefaultForeground(Color foreground) {
      defaultForeground = foreground;
      setForeground(foreground);
    }

    public void setDefaultBackground(Color background) {
      defaultBackground = background;
      setBackground(background);
    }

    public void setDefaultAlignment(int alignment) {
      defaultAlignment = alignment;
      convertAlignment(alignment);
    }

    private void convertAlignment(int alignment) {
      setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(alignment, true));
      setVerticalAlignment(DBUtilities.convertJBCLToSwingAlignment(alignment, false));
    }

    public void setDefaultFont(Font font) {
      defaultFont = font;
      setFont(font);
    }

    public void setDefaultBorder(Border border) {
      defaultBorder = border;
      setBorder(border);
    }

    // CustomPaintSite implementation

    public void reset() {
      setForeground(defaultForeground);
      setBackground(defaultBackground);
      setFont(defaultFont);
      convertAlignment(defaultAlignment);
      setBorder(defaultBorder);
    }

    // inherited from superclass
//    public void setForeground(Color foreground) {}

    // inherited from superclass
//    public void setBackground(Color background) {}

    public void setAlignment(int alignment) {
      this.alignment = alignment;
      convertAlignment(alignment);
    }

    public void setItemMargins(Insets margins) {
      setBorder(new EmptyBorder(margins));
    }

    public boolean isTransparent() {
      return false;
    }

    public int getAlignment() {
      return alignment;
    }

    public Insets getItemMargins() {
      return getBorder().getBorderInsets(this);
    }

    public Component getSiteComponent() {
      return this;
    }

		@Override
		public void setBorderOvrrd(Border border) {}

		@Override
		public Border getBorderOvrrd() {
			return null;
		}

		@Override
		public void addColorStripe(Color color, int width, boolean isLeftAligned) {}

  }

  /** ListModel used when reading list from picklist DataSet */
  private DBListModel dbListModel;

  /** cached DataSetView */
  private DataSetView pickListView;

  /** picklist source columns */
  private String [] sourceColumns;

  /** picklist destination columns */
  private String [] targetColumns;

  /** DBColumnAwareSupport which helps manage our dataset and columnName */
  private DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

  /** current unknown data value mode */
  private int mode = DEFAULT;

  /** flag indicating we should ignore ListSelectionEvent because we caused it */
  private boolean ignoreValueChange;

  private boolean ignoreModelChange;

  /** cached DataRow */
  private DataRow locateRow;

  private Object [] items;

  private boolean rebindColumnProperties;
  private boolean rebuildPickListView = true;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
