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
import java.io.Serializable;
import javax.swing.*;
import javax.swing.table.*;


// Javadoc comments aready in file. Did not replace, but used JBDoc comments instead.
// NW 2/02
// * TableRowNoRenderer is a TableCellRenderer which simply renders the current
// * row number.  It is TableRowHeader's default renderer used to display
// * row numbers in the row header area of a JScrollPane containing a JTable.
// *<P>
// * Useful Properties:
// *<UL>
// *<LI>rowNoOffset, 1 by default
// *<LI>alignment, SwingConstants.CENTER by default
// *</UL>
// *<P>
// * Usage example:
// *<code>
// *  JTable jTable = new JTable();
// *  TableRowHeader rowHeader = new TableRowHeader(jTable);
// *  rowHeader.setCellRenderer(new TableRowNoRenderer());
// *  JScrollPane jScrollPane = new JScrollPane(jTable);
// *  jScrollPane.setRowHeaderView(rowHeader);
// *</code>
 

/**
 * <p>A <code>TableCellRenderer</code> that renders the current row number. It is <a
 * href="TableRowHeader.html"><code>TableRowHeader's</code></a> default renderer used
 * to display row numbers in the row header area of a <code>JScrollPane</code>
 * containing a <code>JTable</code>.</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * JTable jTable = new JTable();
 * TableRowHeader rowheader = new TableRowHeader(jTable);
 * // TableRowNoRenderer is used by default if no other cell
 * // renderer is specified by using rowheader.setCellRenderer()
 * </pre>
 */
public class TableRowNoRenderer extends JLabel
  implements TableCellRenderer, Serializable
{
  int rowNoOffset = 1;
  TableRowHeader rowHeader;

/**
 * <p>Constructs a <code>TableRowNoRenderer</code>.</p>
 */
  public TableRowNoRenderer() {
    super();
    setOpaque(true);
    setHorizontalAlignment(SwingConstants.CENTER);
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));   
  }

    /**  
   * <p>Required for implementation of <code>javax.swing.table.TableCellRenderer</code>.</p>
 */
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {

    if (table != null) {
      JTableHeader header = table.getTableHeader();
      if (header != null) {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
      }
      if (value instanceof TableRowHeader) {
        rowHeader = (TableRowHeader) value;
      }
      if (column == TableRowHeader.PREF_SIZE) {
        int charWidth = new Integer(table.getRowCount()).toString().length() + 1;
        StringBuffer sb = new StringBuffer(charWidth);
        for (int index = 0; index < charWidth; index++) {
          sb.append('8');  
        }
        setText(sb.toString());
        return this;
      }
    }

    setText(" " + (row + rowNoOffset) + " ");   

    return this;
  }


/**
 * <p>Determines the number that begins the numbering of the rows. The default value is
 * 1, so the first row number to appear in the row header is 1. If you set
 * <code>rowNoOffset</code> to 0, for example, the first row header number displays as 0.</p>
 *
 * @param rowNoOffset The number that begins the numbering of the rows.
 * @see #getRowNoOffset
 */
  public void setRowNoOffset(int rowNoOffset) {
    this.rowNoOffset = rowNoOffset;
    rowHeader.repaint();
  }

/**
 * <p>Returns the number that begins the numbering of the rows. </p>
 *
 * @return The number that begins the numbering of the rows.
 * @see #setRowNoOffset
 */
  public int getRowNoOffset() {
    return rowNoOffset;
  }
}

