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
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;

import com.borland.dx.dataset.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * <p>Navigates a <code>DataSet</code> by synchronizing a <code>JTree</code>'s selected value with a <code>DataSet Column</code> value. <code>DBTreeNavBinder</code> is the default binder used by <code>JdbNavTree</code>. </p>
 *
 * <p>There are two other ways to use a <code>DBTreeNavBinder</code> to navigate a <code>DataSet</code>. The easiest way is to
simply set the <code>jTree</code> property to a <code>JTree</code> (or subclass of <code>JTree</code>). Alternatively, any component using a <code>TreeSelectionModel</code> can be set as <code>DBTreeNavBinder</code>'s <code>treeSelectionModel</code> property (or
<code>DBTreeNavBinder</code> can be added as a  <code>TreeSelectionListener</code> to the component). If you take this approach, however, you are responsible for opening the <code>DataSet</code> from which to read data. </p>
 *
 * <p>In either case, you also need to set <code>DBTreeNavBinder</code>'s <code>dataSet</code> and <code>columnName</code> properties to indicate the <code>DataSet</code> and <code>Column</code> in which the tree will navigate. </p>
 *
 * <p>If you set the <code>JTree</code> property, <code>DBTreeNavBinder</code> will also bind the <code>background, foreground</code>, and
<code>font</code> properties from those defined on <code>Column columnName</code> (if defined), unless already explicitly set on the <code>JTree</code> itself. </p>
 *
 * <p><code>DBTreeNavBinder</code> can only synchronize values between the tree and <code>DataSet</code> if the <code>TreeNodes</code> are of actual type <code>javax.swing.tree.DefaultMutableTreeNode</code>, since the <code>DefaultMutableTreeNode.getUserObject()</code> method is used to read values from the tree. The object returned by <code>getUserObject()</code> is assumed to be of the same data type as that of <code>Column columnName</code>. </p>
 *
 * <p>Unlike <code>DBTreeDataBinder</code>, which writes values into the current row of a <code>DataSet</code>,
<code>DBTreeNavBinder</code> locates and navigates to a row in the <code>DataSet</code> with a <code>Column</code> value matching the currently selected node in the tree. </p>
 *
 * <p>If the selected value in the tree cannot be located in the <code>DataSet</code>, <code>DBTreeNavBinder</code> displays a confirmation dialog explaining that this is the case. If the current value in the <code>DataSet</code> cannot be found in the tree, the tree's selection is cleared. </p>
 *
 * <p>Example: </p>
 * <pre>
 * JTree jTree = new JTree();
 * DBTreeNavBinder DBTreeNavBinder = new DBTreeNavBinder();
 *
 * // set the tree's TreeModel
 * jTree.setModel(treeModel);
  *
 * // when a value is selected from the tree, it will be
 * // written to the "path" column of <code>DataSet</code>  dataSet
 * dbTreeNavBinder.setJTree(jTree);
 * dbTreeNavBinder.setDataSet(dataSet);
 * dbTreeNavBinder.setColumnName("path");
 * </pre>
 * @see JdbTree
 * @see JdbNavTree
 * @see DBTreeDataBinder
 */
public class DBTreeNavBinder
  implements TreeSelectionListener, AccessListener, PropertyChangeListener,
             NavigationListener, DataChangeListener, ColumnAware, TreeModelListener,
             Designable, DBDataBinder, java.io.Serializable, FocusListener
{

/**
 * <p>Constructs a <code>DBTreeNavBinder</code> component. Calls the <code>null</code> constructor of its superclass.</p>
*/
  public DBTreeNavBinder() {
  }

/**
 * <p>Creates a <code>DBTreeNavBinder</code> that navigates the specified <code>JTree</code>. Calls the <code>null</code> constructor of its superclass. </p>
 *
 * @param jTree  The <code>JTree</code> which this <code>DBTreeNavBinder</code> will navigate.
*/
  public DBTreeNavBinder(JTree jTree) {
    setJTree(jTree);
  }

  //
  // DBTreeDataBinder properties
  //

/**
 * <p>Sets the <code>JTree</code> used to navigate the <code>DataSet</code>. </p>
 *
 * <p>See the class description for more information. </p>
 *
 * @param jTree The <code>JTree</code> used to navigate the <code>DataSet</code>.
 * @see #getJTree
 */
  public void setJTree(JTree jTree) {
    if (this.jTree != null && this.jTree != jTree) {
      this.jTree.removePropertyChangeListener(this);
    }
    this.jTree = jTree;
    if (jTree == null) {
      setTreeSelectionModel(null);
      setTreeModel(null);
    }
    else {
      jTree.addPropertyChangeListener(this);
      setTreeSelectionModel(jTree.getSelectionModel());
      setTreeModel(jTree.getModel());
    }
  }

/**
 * <p>Returns the <code>JTree</code> used to navigate the <code>DataSet</code>. </p>
 *
 * <p>See the class description for more information. </p>
 *
 * @return The <code>JTree</code> used to navigate the <code>DataSet</code>.
 * @see #setJTree
 */
  public JTree getJTree() {
    return jTree;
  }

/**
 * <p>Sets the <code>TreeSelectionModel</code> used to determine when navigation in the <code>DataSet</code>
should occur. </p>
 *
 * <p>See the class description for more information. </p>
 *
 * @param treeSelectionModel The <code>TreeSelectionModel</code> used to determine when navigation in the <code>DataSet</code>
should occur.
 * @see #getTreeSelectionModel
 */
  public void setTreeSelectionModel(TreeSelectionModel treeSelectionModel) {

    if (this.treeSelectionModel != null) {
      this.treeSelectionModel.removeTreeSelectionListener(this);
    }

    this.treeSelectionModel = treeSelectionModel;

    if (treeSelectionModel != null) {
      treeSelectionModel.addTreeSelectionListener(this);
    }
    bindColumnProperties();
  }

/**
 * <p>Returns the <code>TreeSelectionModel</code> used to determine when navigation in the <code>DataSet</code>
should occur. </p>
 *
 * <p>See the class description for more information. </p>
 *
 * @return The <code>TreeSelectionModel</code> used to determine when navigation in the <code>DataSet</code>
should occur.
 * @see #setTreeSelectionModel
 */

  public TreeSelectionModel getTreeSelectionModel() {
    return treeSelectionModel;
  }

/**
 * <p>Sets the <code>TreeModel</code> containing data to navigate in the <code>DataSet</code>.</p>
 *
 * @param treeModel The <code>TreeModel</code> containing data to navigate in the <code>DataSet</code>.
 * @see #getTreeModel
 */

  public void setTreeModel(TreeModel treeModel) {
    if (this.treeModel != null) {
      this.treeModel.removeTreeModelListener(this);
    }
    this.treeModel = treeModel;
    if (treeModel != null) {
      treeModel.addTreeModelListener(this);
    }
    bindColumnProperties();
  }

/**
 * <p>Returns the <code>TreeModel</code> containing data to navigate in the <code>DataSet</code>.</p>
 *
 * @return The <code>TreeModel</code> containing data to navigate in the <code>DataSet</code>.
 * @see #setTreeModel
 */
  public TreeModel getTreeModel() {
    return treeModel;
  }

  //
  // ColumnAware interface implememtation
  //

  /**
   * <p>Sets the <code>DataSet</code> to which values are written and from which values are read.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   * @see #setColumnName
   */
  public void setDataSet(DataSet dataSet) {
    if (columnAwareSupport.dataSet != null) {
      columnAwareSupport.dataSet.removeNavigationListener(this);
    }
    columnAwareSupport.setDataSet(dataSet);
    if (dataSet != null) {
      columnAwareSupport.dataSet.addNavigationListener(this);
    }
    bindColumnProperties();
  }

  /**
   * <p>Returns the <code>DataSet</code> to which values are written and from which values are read.</p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   * @see #getColumnName
   */
  public DataSet getDataSet() {
    return columnAwareSupport.dataSet;
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> to which values are written and from which values are read.</p>
   *
   * @param columnName The column name of the <code>DataSet</code>.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    columnAwareSupport.setColumnName(columnName);
    bindColumnProperties();
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code> to which values are written and from which values are read.</p>
   *
   * @return The column name of the <code>DataSet</code>.
   * @see #setColumnName
   * @see #getDataSet
   */
  public String getColumnName() {
    return columnAwareSupport.columnName;
  }

  /**
   * <p>Sets whether to ignore internal node values during selection changes
   * and when locating nodes by value.</p>
   *
   * @param useLeafNodesOnly If <code>true</code>, ingores internal node values.
   * @see #isUseLeafNodesOnly
   */
  public void setUseLeafNodesOnly(boolean useLeafNodesOnly) {
    this.useLeafNodesOnly = useLeafNodesOnly;
  }

  /**
   * <p>Returns whether to ignore internal node values during selection changes
   * and when locating nodes by value.</p>
   *
   * @return If <code>true</code>, ingores internal node values.
   * @see #setUseLeafNodesOnly
   */
  public boolean isUseLeafNodesOnly() {
    return useLeafNodesOnly;
  }

  //
  // com.borland.dx.dataset.NavigationListener interface implementation
  //
  public void navigated(NavigationEvent event) {
    if (!dataSetEventsEnabled) {
      return;
    }
    // navigation means we've moved on to a different row, and we need
    // to update the selected item in the tree.
    updateSelectedTreeValue();
  }

  /**
   * <p>Finds the current value and sets it as the current selection
   * in the tree.</p>
   */
  protected void updateSelectedTreeValue() {
    if (!ignoreValueChange) {
      ignoreValueChange = true;
      if (columnAwareSupport.isValidDataSetState()) {
        TreePath selectionPath = findUserObject((DefaultMutableTreeNode) treeModel.getRoot(),
                                                columnAwareSupport.getVariant().getAsObject());

        if (selectionPath != null) {
          TreeNode node = (TreeNode) selectionPath.getLastPathComponent();
          if (!node.isLeaf() && isUseLeafNodesOnly()) {
            selectionPath = null;
          }
        }
        if (selectionPath == null) {
          if (mode == CLEAR_VALUE && !columnAwareSupport.isNull()) {
            columnAwareSupport.lazyOpen();
            columnAwareSupport.resetValue();
          }
          else if (mode == DISABLE_COMPONENT) {
            if (jTree != null && !columnAwareSupport.getVariant().isUnassignedNull()) {
              jTree.setEnabled(false);
            }
          }
          else {
            treeSelectionModel.clearSelection();
          }
        }
        else {
          if (mode == DISABLE_COMPONENT && jTree != null && !jTree.isEnabled()) {
            jTree.setEnabled(true);
          }
          treeSelectionModel.setSelectionPath(selectionPath);
          if (jTree != null) {
            jTree.scrollPathToVisible(selectionPath);
          }
        }
      }
      else {
        if (treeSelectionModel != null) {
          treeSelectionModel.clearSelection();
        }
      }
      ignoreValueChange = false;
    }
  }

  /**
   * <p>Searches for the user object within the tree rooted at node using
   * a depth-first search algorithm. The <code>equals()</code> method of the
   * node's user object is called to determine whether the two user
   * objects are equal.</p>
   *
   * <p>Returns the <code>TreePath</code> to the specified user object if found,
   * otherwise returns <code>null</code>.  You can override this method to
   * implement your own tree search algorithm.</p>
   *
   * @param node The node where the tree is rooted.
   * @param object The user object to search for.
   * @return The path to the node where the user object is located.
   */
  protected TreePath findUserObject(DefaultMutableTreeNode node, Object object) {
    if (node == null) {
      return null;
    }
    if (node.getUserObject().equals(object)) {
      TreePath path = new TreePath(node.getPath());
      return path;
    }
    Enumeration nodes = node.depthFirstEnumeration();
    DefaultMutableTreeNode compareNode;
    while (nodes.hasMoreElements()) {
      compareNode = (DefaultMutableTreeNode) nodes.nextElement();
      if (compareNode.getUserObject().equals(object)) {
        TreePath path = new TreePath(compareNode.getPath());
        return path;
      }
    }
    return null;
  }

  //
  // overrides superclass javax.swing.event.TreeSelectionListener
  // interface implementation
  //

  /**
   * <p>Called whenever the value of the selection changes.</p>
   * @param e The event that characterizes the change.
   */
  public void valueChanged(TreeSelectionEvent e) {
    if (!ignoreValueChange && e.getNewLeadSelectionPath() != null) {
      TreeNode node = (TreeNode) treeSelectionModel.getSelectionPath().getLastPathComponent();
      if (node instanceof DefaultMutableTreeNode &&
          (!isUseLeafNodesOnly() || node.isLeaf())) {
        columnAwareSupport.lazyOpen();
        if (columnAwareSupport.isValidDataSetState()) {
          try {
            if (locateRow == null) {
              locateRow = new DataRow(columnAwareSupport.dataSet, columnAwareSupport.getColumnName());
              variant = new Variant();
            }
            variant.setAsObject(((DefaultMutableTreeNode) node).getUserObject(), columnAwareSupport.getColumn().getDataType());
//          locateRow.setVariant(columnAwareSupport.columnOrdinal, variant);
            locateRow.setVariant(columnAwareSupport.getColumnName(), variant);
            if (!columnAwareSupport.dataSet.locate(locateRow, Locate.FIRST)) {
              JOptionPane.showMessageDialog(jTree,
                                            Res._NavLocateFailed,     
                                            null,
                                            JOptionPane.INFORMATION_MESSAGE);
            }
          }
          catch (DataSetException ex) {
            DBExceptionHandler.handleException(columnAwareSupport.dataSet, ex);
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
        return;
      }
      else {
        treeSelectionModel.clearSelection();
      }
      if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        rebindColumnProperties = true;
      }
    }
    else {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      if (event.getReason() == AccessEvent.UNSPECIFIED || rebindColumnProperties || event.getReason() == AccessEvent.DATA_CHANGE) {
        bindColumnProperties();
      }
    }
  }

  //
  // java.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("ancestor")) {  
      bindColumnProperties();
    }
    if (e.getPropertyName().equals(JTree.SELECTION_MODEL_PROPERTY)) {
      setTreeSelectionModel((TreeSelectionModel) e.getNewValue());
    }
    else if (e.getPropertyName().equals(JTree.TREE_MODEL_PROPERTY)) {
      setTreeModel((TreeModel) e.getNewValue());
    }
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
      updateSelectedTreeValue();
    }
  }

  public void postRow(DataChangeEvent event) throws Exception {
    // usually needs to get the pending value from the control and set it,
    // this isn't relevant for a nav control, since no value is being set
  }


  // returns true if the property has not been explicitly overridden
  // true if and only if the object is an instance of UIResource
  private boolean isDefaultProperty(Object property) {
    return (property == null) || (property instanceof UIResource);
  }

  // binds font, foreground, and background properties from column
  // if not explicitly set on button

/**
 * <p>Sets the <code>font, foreground</code>, and <code>background</code> properties from the column properties of the same names if these properties are not explicitly set. </p>
 */

  protected void bindColumnProperties() {
    if (oldJTree != null) {
      oldJTree.removeFocusListener(this);
      oldJTree = null;
    }

    if (jTree != null && jTree.isDisplayable()) {
      // ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
      // again on the OPEN access event
      rebindColumnProperties = false;
      // will resync our internal state if the dataSet or column has changed
      columnAwareSupport.lazyOpen();

      updateSelectedTreeValue();

      if (columnAwareSupport.isValidDataSetState()) {

        jTree.addFocusListener(this);
        oldJTree = jTree;

        Column column = columnAwareSupport.getColumn();

        if (isDefaultProperty(jTree.getBackground())) {
          if (column.getBackground() != null) {
            jTree.setBackground(column.getBackground());
          }
        }
        if (isDefaultProperty(jTree.getForeground())) {
          if (column.getForeground() != null) {
            jTree.setForeground(column.getForeground());
          }
        }
        if (isDefaultProperty(jTree.getFont())) {
          if (column.getFont() != null) {
            jTree.setFont(column.getFont());
          }
        }

        if (jTree.getCellEditor() == null && column.getItemEditor() instanceof TreeCellEditor) {
          jTree.setCellEditor((TreeCellEditor) column.getItemEditor());
        }

        if (jTree.getCellRenderer() == null && column.getItemPainter() instanceof TreeCellRenderer) {
          jTree.setCellRenderer((TreeCellRenderer) column.getItemPainter());
        }
      }
    }
  }

  public void treeNodesChanged(TreeModelEvent e) {
    if (jTree != null && e.getTreePath().equals(jTree.getSelectionPath().getParentPath())) {
      int targetIndex = ((TreeNode) jTree.getSelectionPath().getParentPath().getLastPathComponent()).getIndex((TreeNode) jTree.getSelectionPath().getLastPathComponent());
      int [] childIndices = e.getChildIndices();
      for (int i = 0; i < childIndices.length; i++) {
        if (childIndices[i] == targetIndex) {
          valueChanged(new TreeSelectionEvent(this, jTree.getSelectionPath(), false, jTree.getSelectionPath(), jTree.getSelectionPath()));
          break;
        }
      }
    }
  }
  public void treeNodesInserted(TreeModelEvent e) {}
  public void treeNodesRemoved(TreeModelEvent e) {}
  public void treeStructureChanged(TreeModelEvent e) {}

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(jTree, columnAwareSupport.dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  /** JTree to which we are attached */
  private JTree jTree;

  private JTree oldJTree;

  /** DataRow used to lookup selected values in DataSet */
  private DataRow locateRow;

  /** Variant used to hold lookup values */
  private Variant variant;

  /**
   * <p>The <code>treeSelectionModel</code> containing current tree selection.</p>
   */
  protected TreeSelectionModel treeSelectionModel;

  /**
   * <p>The <code>treeModel</code> of the tree from which to read data.</p>
   */
  protected TreeModel treeModel;

  /** support for ColumnAware implementation */
  DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

  /**
   * <p>Flag indicating that <code>ListSelectionEvent</code> should be ignored. </p>
   */
  protected boolean ignoreValueChange;

  /**
   * <p>The current setting of unknown data value mode.</p>
   */
  protected int mode = DEFAULT;

  private boolean rebindColumnProperties;

  /**
   * Determines whether or not to ignore internal nodes for searches and selection changes.
   */
  private boolean useLeafNodesOnly;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;
}
