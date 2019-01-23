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
 * <p>The <code>JdbNavTree</code> is a data-aware extension
 * of the <code>JTree</code> component, allowing it to
 * connect to and navigate a <code>DataSet</code> through
 * the <code>dataSet</code> and <code>columnName</code>
 * properties. When you select an item from the
 * <code>JdbNavTree</code>, it moves the pointer in the
 * <code>DataSet</code> to the corresponding row. Any
 * updates made to the items displayed in the
 *  <code>JdbNavTree</code> are ignored. </p>
 *
 * @see JdbTree
 */
public class JdbNavTree extends JTree
  implements ColumnAware, java.io.Serializable
{

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it an empty array of <code>Object</code>s.
</p>
  */
  public JdbNavTree() {
    super(new Object [] {});
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it an array of <code>Object</code>s.</p>
  *
  * @param value The array of <code>Object</code>s used to create the tree.
  */
  public JdbNavTree(Object [] value) {
    super(value);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it a <code>Vector</code>.</p>
  *
  * @param value The <code>Vector</code> containing the data for the tree.
  */
  public JdbNavTree(Vector value) {
    super(value);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it a <code>HashTable</code>.</p>
  *
  * @param value The <code>HashTable</code> containing the data for the tree.
  */
  public JdbNavTree(Hashtable value) {
    super(value);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it the root node.</p>
  *
  * @param root The node that becomes the root of the tree.
  */
  public JdbNavTree(TreeNode root) {
    super(root);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it the root node and a parameter that specifies whether the tree can have child nodes.</p>
  *
  * @param root The <code>TreeNode</code> that becomes the first or root node of the tree.
  * @param asksAllowsChildren Determines whether the tree can have child nodes. If <code>true</code>, the tree can have child nodes.
  */
  public JdbNavTree(TreeNode root, boolean asksAllowsChildren) {
    super(root, asksAllowsChildren);
    commonInit();
  }

 /**
  * <p>Constructs a <code>JdbNavTree</code> by calling the constructor of its superclass and passing to it a <code>TreeModel</code>.</p>
  *
  * @param treeModel The model containing the data for the tree.
  */
  public JdbNavTree(TreeModel treeModel) {
    super(treeModel);
    commonInit();
  }

  /**
   * <p>Used to initialize <code>JdbNavTree</code> with the
   * same defaults, regardless of the constructor used.
   * Sets its binder to be an instance of
   * <code>DBTreeNavBinder</code>.</p>
   */
  protected void commonInit() {
    navBinder = new DBTreeNavBinder(this);
  }

  //
  // com.borland.dx.dataset.ColumnAware interface implementation
  //
  /**
   * <p>Sets the <code>DataSet</code> from which values are
   * read.</p>
   *
   * @param dataSet The <code>DataSet</code>.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    navBinder.setDataSet(dataSet);
  }

  /**
   * <p>Returns the <code>DataSet</code> from which values
   * are read. </p>
   *
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    return navBinder.getDataSet();
  }

  /**
   * Sets the column name of the <code>DataSet</code> from
   * which values are read.
   *
   * @param columnName The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    navBinder.setColumnName(columnName);
  }

  /**
   * Returns the column name of the <code>DataSet</code>
   * from which values are read.
   *
   * @return The column name.
   * @see #setColumnName
   * @see #getDataSet
   */
  public String getColumnName() {
    return navBinder.getColumnName();
  }


  DBTreeNavBinder getNavBinder() {
    return navBinder;
  }

  /**
   * <p>Sets whether to ignore internal node values during
   * selection changes
   * and when locating nodes by value.</p>
   *
   * @param useLeafNodesOnly If <code>true</code>, ignore internal node values.
   * @see #isUseLeafNodesOnly
   */
  public void setUseLeafNodesOnly(boolean useLeafNodesOnly) {
    navBinder.setUseLeafNodesOnly(useLeafNodesOnly);
  }

  /**
   * <p>Returns whether to ignore internal node values
   * during selection changes
   * and when locating nodes by value.</p>
   *
   * @return If <code>true</code>, ignore internal node values.
   * @see #setUseLeafNodesOnly
   */
  public boolean isUseLeafNodesOnly() {
    return navBinder.isUseLeafNodesOnly();
  }

 /**
  * <p>The <code>DBTreeNavBinder</code> to which methods are delegated.</p>
  */
  protected DBTreeNavBinder navBinder;
}
