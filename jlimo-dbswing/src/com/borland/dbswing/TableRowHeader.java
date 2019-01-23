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
import java.io.Serializable;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.borland.dbswing.plaf.*;
import com.borland.dbswing.plaf.basic.*;

/**
 * <p>Place within a <code>JScrollPane's</code> row header area
 * to display information about the rows of a
 * <code>JTable</code> in the <code>JScrollPane's</code> main
 * viewport. When placed into a <code>JScrollPane's</code> row header view,
 * <code>TableRowHeader</code> gets the <code>JScrollPane's</code> viewport
 * view component and uses that as the value of its <code>table</code>
 * property if it is a <code>JTable</code>. <code>TableRowHeader</code>
 * requires its <code>table</code> property to be set to display properly.</p>
 *
 * <p><code>JdbTable</code> uses this component by default to display its row
 * header.</p>
 *
 * <p><code>TableRowHeader</code> uses its corresponding <code>JTable's</code>
 * <code>TableModel</code> as its own model, unless another <code>TableModel</code>
 * has been explicitly specified. If neither a <code>TableModel</code> nor a
 * <code>JTable</code> have been specified, then an empty
 * <code>DefaultTableModel</code> is used by default.</p>
 *
 * <p><code>TableRowHeader</code> optionally allows vertical resizing of the first
 * visible row's header cell to adjust its corresponding <code>JTable's</code> row
 * height.</p>
 *
 * <p><code>TableRowHeader</code> delegates rendering of each row's header cell to
 * its <code>TableCellRenderer</code>. If a renderer is not explicitly specified, a
 * <code>TableRowNoRenderer</code> is used by default. The <code>value</code>
 * parameter passed to the <code>TableCellRenderer's</code>
 * <code>getTableCellRendererComponent()</code> method is always its instance of
 * <code>TableRowHeader</code>, which the renderer can invalidate to cause a repaint.
 * The renderer can obtain information about its table from either the
 * <code>table</code> or <code>row</code> parameter. The <code>column</code>
 * parameter is passed one of the following values, which a renderer may take
 * advantage of to increase performance:</p>
 *
 *
 * <p><strong>TableRowHeader values passed to renderer</strong></p>
 *
 * <table cellspacing="2" cellpadding="2" border="2" frame="box" rules="all">
 * <tr>
 *    <th align="LEFT">Value</th>
 *    <th align="LEFT">Meaning</th>
 * </tr>
 * <tr>
 *    <td><code>TableRowHeader.PAINT</code></td>
 *    <td>Renderer is about to be asked to paint</td>
 * </tr>
 * <tr>
 *     <td><code>TableRowHeader.PREF_SIZE</code></td>
 *     <td>Renderer is being asked its preferred size</td>
 * </tr>
 * <tr>
 *     <td><code>TableRowHeader.MIN_SIZE</code></td>
 *     <td>Renderer is being asked its minimum size</td>
 * </tr>
 * <tr>
 *     <td><code>TableRowHeader.MAX_SIZE</code></td>
 *     <td>Renderer is being asked its maximum size</td>
 * </tr>
 * </table>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * // creates an empty JTable with 20 rows and 10 columns
 * JTable jTable = new JTable(20, 10); 
 * JScrollPane jScrollPane = new JScrollPane(jTable);
 * jScrollPane.setRowHeaderView(new TableRowHeader());
 * </pre>
  */
public class TableRowHeader extends JComponent 
  implements TableModelListener, PropertyChangeListener, Serializable
{

  /** 
   * <p>Column index passed to renderer when about to paint. Value is 0.</p>
   */
  public static final int PAINT = 0;

  /** 
   * <p>Column index passed to renderer when asked its preferred size. Value is 1.</p>
   */
  public static final int PREF_SIZE = 1;

  /** 
   * <p>Column index passed to renderer when asked its minimum size. Value is 2.</p>
   */
  public static final int MIN_SIZE = 2;


  /** 
   * <p>Column index passed to renderer when asked its maximum size. Value is 3.</p>
   */
  public static final int MAX_SIZE = 3;

  /** TableRowHeader's base UI delegate class */
  private static final String uiClassID = "TableRowHeaderUI";  

  /** 
   * <p>Whether the row header can be resized vertically.</p>
   */
  boolean heightResizable = true;

  /** 
   * <p>Whether the table display is updated immediately during vertical cell resizing.</p>
   */
  boolean resizeTableWhileSizing = true;

  /** 
   * <p>The row cell renderer.</p>
   */
  TableCellRenderer renderer;

  /** 
   * <p>The corresponding <code>JTable</code> (should be in <code>JScrollPane's</code> main viewport).  </p> 
   */
  JTable table;

  /** 
   * <p>The model, usually the table's <code>TableModel</code>, but it can be overridden.</p> 
   */
  TableModel model;

 /**
   * <p>Constructs a <code>TableRowHeader</code>. Calls the constructor of
   * <code>this</code> class which takes a <code>JTable</code> as a parameter,
   * passing it <code>null</code>.</p>
   */
  public TableRowHeader() {
    this(null);
  }


/**
 * <p>Constructs a <code>TableRowHeader</code> and specifies the table for which the
 * component displays header information. Calls the constructor of <code>this</code>
 * class which takes a <code>JTable</code> and a <code>TableCellRenderer</code> as
 * parameters, passing it the specified table and <code>null</code>.</p>  
 *
 * @param table The table for which the <code>TableRowHeader</code> is a row header.
 *
 */
  public TableRowHeader(JTable table) {
    this(table, null);
  }

/**
 * <p>Constructs a <code>TableRowHeader</code> and specifies the table for which the
 * component displays header information and the renderer used to render the cells of
 * the header.</p>
 *
 * @param table The table for which the <code>TableRowHeader</code> is a row header.
 * @param renderer The cell renderer that renders the cells of the row header.
 */
  public TableRowHeader(JTable table, TableCellRenderer renderer) {
    setTable(table);
    setCellRenderer(renderer);
    updateUI();
  }

  /**
   * <p>Updates the UI.</p>
   */
  public void updateUI() {
    setUI(BasicJdbTableRowHeaderUI.createUI(this));
  }

  /**
   * <p>Returns the <code>UIClassID</code> of <code>TableRowHeader</code>.</p>
   *
   * @return A <code>String</code> representing the <code>UIClassID</code>.
   */
  public String getUIClassID() {
    return uiClassID;
  }

  /**
   * <p>Overrides <code>JComponent.isFocusTraversable()</code> to ensure that the row header never gets focus.</p>
   */
  public boolean isFocusTraversable() {
    return false;
  }

  /**
   * <p>Overrides <code>JComponent.isFocusEnabled()</code> to ensure that the row header never gets focus.</p>
   */
  public boolean isRequestFocusEnabled() {
    return false;
  }

  /**
   * <p>Sets the <code>JTable</code> for which the row header displays information.
   * The <code>table</code> property must be set for <code>TableRowHeader</code> to
   * display information properly.</p>
   *
   * @param table The <code>JTable</code> for which the row header displays information.
   * @see #getTable
   */
  public void setTable(JTable table) {
    if (this.table != null) {
      this.table.removePropertyChangeListener(this);
    }
    this.table = table;
    if (table != null) {
      table.addPropertyChangeListener(this);
      setModel(table.getModel());
    }
    else {
      setModel(null);
    }
  }

  /**
   * <p>Returns the <code>JTable</code> for which the row header displays information.</p>
   *
   * @return The <code>JTable</code> for which the row header displays information.
   * @see #setTable
   */
  public JTable getTable() {
    return table;
  }

  /**
   * <p>Sets the model used by the row header. Uses the model of the
   * <code>JTable</code> specified by the <code>table</code> property, unless another
   * model has been explicitly specified. If neither a model nor a table is
   * specified, a <code>DefaultTableModel</code> is used.</p>
   *
   * @param model The model used by the row header.
   * @see #getModel
  */
  public void setModel(TableModel model) {
    if (this.model != null) {
      this.model.removeTableModelListener(this);
    }
    this.model = model;
    if(model != null) {
      model.addTableModelListener(this);
    }
  }

  /**
   * <p>Returns the model used by the row header.</p>
   *
   * @return The model used by the row header.
   * @see #setModel
   */
  public TableModel getModel() {
    if (model == null) {
      model = new DefaultTableModel();
    }
    return model;
  }


 /** 
  * <p>Sets the cell renderer that renders each row's header cell.
  * If no renderer is specified, <code>TableRowNoRenderer</code> is
  * used by default, which simply renders the current row number.</p>
  *
  * @param renderer The cell renderer.
  * @see #getCellRenderer
  */
  public void setCellRenderer(TableCellRenderer renderer) {
    this.renderer = renderer;
  }

 /** 
  * <p>Returns the cell renderer that renders each row's header cell.</p>
  *
  * @return The cell renderer.
   * @see #setCellRenderer
  */
  public TableCellRenderer getCellRenderer() {
    if (renderer == null) {
      renderer = new TableRowNoRenderer();
    }
    return renderer;
  }

  /**
   * <p>Determines whether the header can be resized vertically. If
   * <code>heightResizable</code> is <code>true</code>, the header
   * can be resized; otherwise, it is <code>false</code>.</p>
   *
   * @param heightResizable If <code>true</code>, the header can be resized.
   * @see #isHeightResizable
   */
  public void setHeightResizable(boolean heightResizable) {
    this.heightResizable = heightResizable;
  }

  /**
   * <p>Returns whether the header can be resized vertically. </p>
   *
   * @return If <code>true</code>, the header can be resized.
   * @see #setHeightResizable
   */
  public boolean isHeightResizable() {
    return heightResizable;
  }

  /**
   * <p>Determines whether the row heights of the table are updated
   * while the top row header cell is resized. If 
   * <code>true</code>, the row heights of the table are updated
   * while the top row header cell is resized. This property is set
   * to <code>true</code> by default.</p>
   *
   * @param resizeTableWhileSizing Resize Set to <code>true</code>  order to update the row heights of the table while the top row header cell is resized. 
   * @see #isResizeTableWhileSizing
   */
  public void setResizeTableWhileSizing(boolean resizeTableWhileSizing) {
    this.resizeTableWhileSizing = resizeTableWhileSizing;
  }

  /**
   * <p>Returns whether the row heights of the table are updated
   * while the top row header cell is resized. If 
   * <code>true</code>, the row heights of the table are updated
   * while the top row header cell is resized. </p>
   *
   * @return If <code>true</code>, the row heights of the table are updated while the top row header cell is resized. 
   * @see #setResizeTableWhileSizing
   */
  public boolean isResizeTableWhileSizing() {
    return resizeTableWhileSizing;
  }

  /**
   * <p>A utility method used by renderers to request that one or more row header cells be repainted.</p>
   * @param firstRow The first row of the block of rows to be repainted.
   * @param lastRow The last row of the block of rows to be repainted.
   */
  public void repaintRows(int firstRow, int lastRow) {
    ((BasicJdbTableRowHeaderUI) ui).repaintRows(firstRow, lastRow);
  }

  //
  // java.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("model")) {   
      Object model;
      if ((model = e.getNewValue()) != null && model instanceof TableModel) {
        setModel((TableModel) model);
      }
    }
  }

  //
  // javax.swing.event.TableModelListener interface
  //
  public void tableChanged(TableModelEvent e) {
    int firstRow = getModel().getRowCount() - 1;
    if (firstRow < 0) {
      firstRow = 0;
    }
    int lastRow = firstRow + (e.getLastRow() - e.getFirstRow() + 1);
    if (e.getType() == TableModelEvent.INSERT) {
//      revalidate();
      repaintRows(firstRow, lastRow);
    }
    else if (e.getType() == TableModelEvent.DELETE) {
//      revalidate();
      repaintRows(firstRow, lastRow);
    }
  }

}



