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

import javax.swing.table.*;

/**
 * <p>Specifies customized column
 * properties for a <code>JdbTable</code>.  It is simply a container class with two
 * properties, <code>columnPositions</code> and <code>tableColumns</code>. <code>columnProperties</code> is an <code>int</code> array for visual column positions.
 * <code>tableColumns</code> is an array of <code>TableColumns</code>.</p>
 *
 * <p>The size of the <code>columnPositions</code> and <code>tableColumns</code> arrays must be the same.
 * The <code>columnPosition</code> array indicates the <code>TableColumnModel</code> position for the
 * corresponding <code>TableColumn</code> entry specified in the <code>tableColumns</code> array.
 * A <code>columnPosition</code> value of -1 leaves the corresponding <code>tableColumn</code> in
 * its natural position in the <code>TableModel</code>.</p>
 *
 * <p>Each <code>TableColumn</code> in the <code>tableColumns</code> array must specify a valid model index
 * in the <code>TableModel</code> (not <code>DataSet</code>) of the <code>TableColumn</code> with customized
 * properties to set.  Properties other than the default value are assumed
 * to be explicitly set, and will take precedence over corresponding
 * values set on a <code>DataSet</code>'s Column.  </p>
 */
public class CustomColumnsDescriptor implements java.io.Serializable {


/**
 * <p>Constructs a <code>CustomColumnsDescriptor</code>.</p>
 *
 * @param columnPositions The array of visual column positions.
 * @param tableColumns The array of <code>TableColumns</code>.
 */


  public CustomColumnsDescriptor(int [] columnPositions, TableColumn [] tableColumns) {
    if (columnPositions == null) {
      throw new IllegalArgumentException(Res._NullColPos);     
    }
    if (tableColumns == null) {
      throw new IllegalArgumentException(Res._NullTblCol);     
    }
    if (columnPositions.length != tableColumns.length) {
      throw new IllegalArgumentException(Res._ColsNotEqual);     
    }
    this.columnPositions = columnPositions;
    this.tableColumns = tableColumns;
  }

/**
 * <p>Returns the array of visual column positions.  </p>
 *
 * @return The array of visual column positions.
 */

 public int [] getColumnPositions() {
    return columnPositions;
  }
/**
 * <p>Returns the array of <code>TableColumns</code>.</p>
 *
 * @return The array of <code>TableColumns</code>.
 */

  public TableColumn [] getTableColumns() {
    return tableColumns;
  }

  private int [] columnPositions;
  private TableColumn [] tableColumns;
}
