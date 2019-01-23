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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.borland.dx.dataset.*;

/**
 * <p><code>DBTreeDataBinder</code> maps a <code>JTree's</code> selected value to a <code>DataSet</code> Column value. More generally,
<code>DBTreeDataBinder</code> can map any selected value obtained from a <code>TreeModel</code> to a <code>DataSet</code> value. In particular, <code>DBTreeDataBinder</code> can be used to make a <code>JTree</code> write its currently selected value to a
<code>DataSet</code>, and to update its currently selected value when the <code>DataSet</code> value changes. </p>
 *
 * <p><code>DBTreeDataBinder</code> is the default binder used by <code>JdbTree</code>. </p>
 *
 * <p>There are two other ways to use a <code>DBTreeDataBinder</code> to make a tree data aware: </p>
 *
 * <ul>
 * <li>Set the <code>jTree</code> property to a <code>JTree</code>, or subclass of <code>JTree</code>.</li>
 * <li>Make any component using a <code>TreeModel</code> and <code>TreeSelectionModel</code> data-aware by setting      <code>DBTreeDataBinder's</code> <code>treeModel</code> and <code>treeSelectionModel</code> properties, or by adding
<code>DBTreeDataBinder</code> as a <code>TreeSelectionListener</code> to the component. If you adopt this approach, you are responsible for opening the <code>DataSet</code>.</li>
 * </ul>
 *
 * <p>In either case, you must also set <code>DBTreeDataBinder's dataSet</code> and <code>columnName</code> properties to
indicate the <code>DataSet</code> and <code>Column</code> to which the tree value is to be written and from which the tree value is to be read. </p>
 *
 * <p>Note that <code>DBTreeDataBinder</code> can read and write values between the tree and the specified <code>DataSet</code> only if the <code>TreeNodes</code> are of type <code>javax.swing.tree.DefaultMutableTreeNode</code>. The value read from the tree is the value returned by the <code>DefaultMutableTreeNode.getUserObject()</code> method.
The object returned by <code>getUserObject()</code> is assumed to be of the same data type as that of <code>Column
columnName</code>. The <code>DataSet</code> value is always written to the <code>TreeModel</code> using the <code>javax.swing.tree.MutableTreeNode.setUserObject()</code> method. The object passed to <code>setUserObject()</code> is of the same data type as that of <code>Column columnName</code>. </p>
 *
 * <p>Besides writing a value to a <code>DataSet</code> when a tree value is selected, <code>DBTreeDataBinder</code> also ensures
that the selected tree value indicates the current value of the <code>DataSet Column</code> to which it is attached.
When attempting to match the <code>DataSet</code> value with a user object in the tree, <code>DBTreeDataBinder</code> searches the tree in depth-first order until it encounters a user object with an <code>equals()</code> method that returns true with the <code>DataSet</code> value passed as the parameter. That user object's tree node then becomes the currently selected value in the tree. You can override the <code>findUserObject()</code> method in a subclass to change the tree traversal algorithm. If the user object is anything other than a <code>String,</code> it
should usually override <code>equals()</code> so that it returns true when the contents of the two user objects
have the same value, even if they are different instances. </p>
 *
 * <p>If the value in the <code>DataSet</code> doesn't match a value in the tree, <code>DBTreeDataBinder,</code> by default, clears the tree selection so no value is selected in the tree. You can, however, set the
<code>unknownDataValueMode</code> property to one of the following values to specify alternative behavior: </p>
 *
 * <ul>
 * <li>DEFAULT - Clears the current tree selection if the <code>DataSet</code> value can't be found in the tree.</li>
 * <li>DISABLE_COMPONENT - Disables the component specified by the <code>jTree</code> property if the <code>DataSet</code> value can't be found in the tree.</li>
 * <li>CLEAR_VALUE - Clears the <code>DataSet</code> value if it doesn't match any value in the tree and clears the current tree selection. </li>
 * </ul>
 * <p><code>DBTreeDataBinder</code> can assign a single value only to a <code>DataSet Column</code> and it always uses the <code>TreeSelectionModel's</code> first selection value (<code>TreeSelectionModel.getSelectionPath()</code>) as the currently selected value, regardless of the selection mode of the model. </p>
 *
 * <p>If you specify a <code>TreeSelection</code> and <code>TreeModel</code> individually instead of setting the <code>jTree</code> property, be sure both models are working on the same model data. </p>
 *
 * <p>Example: </p>
 * <pre>
 * JTree jTree = new JTree();
 * DBTreeDataBinder dBTreeDataBinder = new DBTreeDataBinder();
 *         
 * // Set the tree's TreeModel.
 * // You must build a TreeModel outside of the code show here.
 * // JBuilder has no component that can load a tree from a DataSet.
 * jTree.setModel(treeModel);
 *         
 * // when a value is selected from the tree, it is written
 * // to the "path" column of DataSet dataSet
 * dBTreeDataBinder.setJTree(jTree);
 * dBTreeDataBinder.setDataSet(dataSet);
 * dBTreeDataBinder.setColumnName("path");
 * </pre>
 *
 * <p>dbSwing can synchronize a tree with a <code>DataSet</code> so that navigation in the <code>DataSet</code> changes the tree selection and selection in the tree navigates the <code>DataSet</code> (instead of editing it). To do that, use a <code>DBTreeNavBinder</code> instead of a <code>DBTreeDataBinder</code>. </p>
 * 
 * @see JdbTree
 * @see JdbNavTree
 * @see DBTreeNavBinder
 */
public class DBTreeDataBinder extends DBTreeNavBinder
{

/**
 * <p>Creates a <code>DBTreeDataBinder</code>. Calls the <code>null</code> constructor of its superclass.</p>
*/
  public DBTreeDataBinder() {
    super();
  }

/**
 * <p>Creates a <code>DBTreeDataBinder</code> that makes the specified <code>JTree</code> data-aware. </p>
 *
 * @param jTree The <code>JTree</code> to which this <code>DBTreeDataBinder</code> binds to make it data-aware.
 */
  public DBTreeDataBinder(JTree jTree) {
    super(jTree);
  }

  /**
   * Sets the policy for setting the tree selection when synchronizing a 
   * button with a <code>DataSet</code> value when the value can't be located
   * in the tree.
   * 
   * @param mode The possible values are:
   *
   * <ul>
   * <li>DEFAULT - Clears the current tree selection if the <code>DataSet</code> value can't be found in the tree.</li>
   * <li>DISABLE_COMPONENT -  Disables the component specified by the <code>JTree</code> property if the      <code>DataSet</code> value can't be found in the tree.</li>
   * <li>CLEAR_VALUE - Clears the <code>DataSet</code> value if it doesn't match any value in the tree and clears 
the current tree selection. </li>
</ul>
   *
   * @see #getUnknownDataValueMode
   * @see DBTreeDataBinder
   */

  public void setUnknownDataValueMode(int mode) {
    this.mode = mode;
  }

  /**
   * Returns the policy for setting the tree selection when synchronizing a 
   * button with a <code>DataSet</code> value when the value can't be located
   * in the tree.
   * 
   * @return The possible values are:
   *
   * <ul>
   * <li>DEFAULT - Clears the current tree selection if the <code>DataSet</code> value can't be found in the tree.</li>
   * <li>DISABLE_COMPONENT -  Disables the component specified by the <code>JTree</code> property if the      <code>DataSet</code> value can't be found in the tree.</li>
   * <li>CLEAR_VALUE - Clears the <code>DataSet</code> value if it doesn't match any value in the tree and clears 
the current tree selection. </li>
</ul>
   *
   * @see #setUnknownDataValueMode
   */

  public int getUnknownDataValueMode() {
    return mode;
  }

/**
 * <p>Sets the <code>font, foreground</code>, and <code>background</code> properties from the column properties of the same names if these properties are not explicitly set. </p>
 *
 */
  protected void bindColumnProperties() {
    super.bindColumnProperties();

    if (getJTree() != null && getJTree().isDisplayable()) {
      if (getJTree().isEnabled()) {
        if (columnAwareSupport.isValidDataSetState()) {
          Column column = columnAwareSupport.getColumn();
          if (!column.isEditable()) {
            getJTree().setEnabled(false);
          }
        }
      }
    }
  }

  //
  // javax.swing.event.TreeSelectionListener interface implementation
  //

  /**
   * <p>Called whenever the value of the selection changes.</p>
   *
   * @param e The event that characterizes the change.
   */
  public void valueChanged(TreeSelectionEvent e) {
    if (!ignoreValueChange && e.getNewLeadSelectionPath() != null) {
      TreeNode node = (TreeNode) treeSelectionModel.getSelectionPath().getLastPathComponent();
      if (node instanceof DefaultMutableTreeNode && 
          (!isUseLeafNodesOnly() || node.isLeaf()) &&
          columnAwareSupport.isValidDataSetState()) {
        columnAwareSupport.lazyOpen();
        ignoreValueChange = true;
        columnAwareSupport.setObject(((DefaultMutableTreeNode) node).getUserObject());
        ignoreValueChange = false;
      }
    }
  }
}
