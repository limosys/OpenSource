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

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import com.borland.dx.dataset.*;

/**
 * <p><code>JdbTree</code> is a data-aware extension of the
 * <code>JTree</code> component. It displays a tree of choices
 * from which a user may choose a single value. The selected
 * value can be written to a <code>Column</code> of a
 * <code>DataSet</code>, and it can be retrieved using the
 * standard Swing <code>TreeSelectionModel</code> methods. When
 * <code>JdbTree</code> is bound to a <code>DataSet</code> and
 * <code>DataSet</code> <code>Column</code>, the currently
 * selected choice always reflects the value of the corresponding
 * <code>Column</code> in the current <code>DataSet</code>
 * row.</p>
 *
 * <p><code>JdbTree</code> uses a <a href="DBTreeDataBinder.html"><code>DBTreeDataBinder</code></a>
 * to bind a tree to a <code>DataSet</code> and make
 * <code>JdbTree</code> data-aware.</p>
 *
 * <p>To fill the tree presented by <code>JdbTree</code>, choose one of the following.</p>
 *
 * <ul>
 * <li>Set <code>JdbTree</code>'s <code>model</code> property
 * with a <code>TreeModel</code> implementation composed of
 * <code>DefaultMutableTreeNodes</code>. </li>
 *
 * <li>Use the standard <code>JTree</code> constructors to
 * populate the tree from a <code>Vector</code>,
 * <code>Object</code> array, or <code>Hashtable</code>. </li>
 * </ul>
 * <p> By default, <code>JdbTree</code> sets its selection mode
 * to <code>TreeSelectionModel.SINGLE_TREE_SELECTION</code>
 * because only a single tree selection can be copied to its
 * <code>DataSet</code> target. Even if the selection mode is
 * changed to allow the selection of multiple items
 * simultaneously, only the first item of the selection group is
 * copied to the <code>DataSet</code>.</p>
 *
 * @see DBTreeDataBinder
 * @see JdbNavTree
 */
public class JdbTree extends JTree
  implements DBDataBinder, ColumnAware, java.io.Serializable
{
 /**
  * <p>Creates a <code>JdbTree</code> component by calling the
  * constructor of <code>this</code> class that takes an array of
  * <code>Object</code>s and passing it a newly instantiated
  * empty array.</p>
  */
  public JdbTree() {
    this(new Object [] {});
  }

 /**
  * <p>Creates a <code>JdbTree</code> component that is populated
  * with an array of <code>Object</code>s by calling the
  * constructor of its superclass and passing the specified array.</p>
  *
  * @param value The array of <code>Object</code>s that become nodes of the tree.
  */
  public JdbTree(Object [] value) {
    super(value);
    commonInit();
  }

 /**
  * <p>Creates a <code>JdbTree</code> component by calling the
  * constructor of its superclass and passing it the specified
  * <code>Vector</code>.
  *
  * @param value The <code>Vector</code> that contains the values that become the nodes of the tree.
  */
  public JdbTree(Vector value) {
    super(value);
    commonInit();
  }

 /**
  * <p>Creates a <code>JdbTree</code> component by calling the
  * constructor of its superclass, passing it the specified
  * <code>Hashtable</code>.</p>
  *
  * @param value The <code>Hashtable</code> with the values that become the nodes of the tree.
  */
  public JdbTree(Hashtable value) {
    super(value);
    commonInit();
  }

 /**
  * <p>Creates a <code>JdbTree</code> component by calling the
  * constructor of its superclass and passing it the specified
  * <code>TreeNode</code>.</p>
  *
  * @param root The <code>TreeNode</code> that becomes the first or root node of the tree.
  */
  public JdbTree(TreeNode root) {
    this(root, false);
  }

 /**
  * <p>Creates a <code>JdbTree</code> component that is populated
  * with a <code>TreeNode</code>. Calls the constructor of
  * <code>this</code> class that takes a <code>TreeModel</code>
  * as a parameter, passing it a newly instantiated model using
  * the specified root node and <code>asksAllowsChildren</code>
  * value.</p>
  *
  * @param root The <code>TreeNode</code> that becomes the first or root node of the tree.
  * @param asksAllowsChildren Determines whether the tree can have child nodes.
  */
  public JdbTree(TreeNode root, boolean asksAllowsChildren) {
    this(new DefaultTreeModel(root, asksAllowsChildren));
  }

 /**
  * <p>Creates a <code>JdbTree</code> component by calling the
  * constructor of its superclass and passing it the specified
  * <code>TreeModel</code>.</p>
  *
  * @param treeModel The <code>TreeModel</code> that contains the values that become the nodes of the tree.
  */
  public JdbTree(TreeModel treeModel) {
    super(treeModel);
    commonInit();
  }

  /**
   * <p>Used to initialize <code>JdbTree</code> with the same
   * defaults, regardless of the constructor used.</p>
   */
  protected void commonInit() {
    dataBinder = new DBTreeDataBinder(this);
  }

  //
  // com.borland.dx.dataset.ColumnAware interface implementation
  //

  /**
   * <p>Sets the <code>DataSet</code> from which the current list selection is
   * read.</p>
   *
   * <p>Setting this property replaces the current
   * <code>ListModel</code> from which the list is built.</p>

   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which the list selection
   * is read. </p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return dataBinder.getDataSet();
  }

  /**
   * <p>Sets the column name of the <code>DataSet</code> from
   * which values are read.</p>
   *
   * <p>Setting this property replaces the current
   * <code>ListModel</code> from which the list is built.</p>
   *
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }

  /**
   * <p>Returns the column name of the <code>DataSet</code>
   * from which values are read.</p>
   * @see #setColumnName
   * @see #setDataSet
   */
  public String getColumnName() {
    return dataBinder.getColumnName();
  }

  /**
   * <p>Sets the policy for setting tree selection when synchronizing a
   * button with its <code>DataSet</code> value when the value can't be located in the tree. Valid values for the <code>mode</code> parameter are DEFAULT, DISABLE_COMPONENT, and CLEAR_VALUE.</p>
   *
   * @param mode One of DEFAULT, DISABLE_COMPONENT, and CLEAR_VALUE.
   * @see #getUnknownDataValueMode
   */
  public void setUnknownDataValueMode(int mode) {
    dataBinder.setUnknownDataValueMode(mode);
  }

  /**
   * <p>Returns the policy for setting the tree selecction when synchronizing a
   * button with its <code>DataSet</code> value when the value doesn't match either
   * of the <code>selectedDataValue</code> or <code>unselectedDataValue</code>
   * property.</p>
   *
   * @return One of DEFAULT, DISABLE_COMPONENT, and CLEAR_VALUE.
   * @see #setUnknownDataValueMode
   */
  public int getUnknownDataValueMode() {
    return dataBinder.getUnknownDataValueMode();
  }

  /**
   * <p>Sets whether to ignore internal node values during selection changes
   * and when locating nodes by value.</p>
   *
   * @param useLeafNodesOnly If <code>true</code>, internal node values are ignored during selection changes.
   * @see #isUseLeafNodesOnly
   */
  public void setUseLeafNodesOnly(boolean useLeafNodesOnly) {
    dataBinder.setUseLeafNodesOnly(useLeafNodesOnly);
  }

  /**
   * <p>Returns whether to ignore internal node values during selection changes
   * and when locating nodes by value.</p>
   *
   * @return If <code>true</code>, internal node values are ignored during selection changes.
   * @see #setUseLeafNodesOnly
   */
  public boolean isUseLeafNodesOnly() {
    return dataBinder.isUseLeafNodesOnly();
  }

 /**
  * <p>Returns the <code>DBTreeDataBinder</code> that makes this a data-aware component. </p>
  */
  protected DBTreeDataBinder dataBinder;
}
