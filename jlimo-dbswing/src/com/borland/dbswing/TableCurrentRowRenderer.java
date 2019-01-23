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

import com.borland.dx.dataset.*;

/**
  * <p>A <code>TableCellRenderer</code> that
  * paints an icon to indicate a <code>JTable's</code> currently selected row. By default,
  * the icon is a black triangle. You can specify an icon of your choosing with the
  * <code>icon</code> property inherited from <code>JLabel</code>, which
  * <code>TableCurrentRowRenderer</code> extends. <code>TableCurrentRowRenderer</code> can
  * be set as <code>TableRowHeader's</code> renderer to indicate a <code>JTable's</code>
  * current row in the row header area of a <code>JScrollPane</code>.</p>
  *
  * <p><strong>Example:</strong></p>
  * <pre>
  *  JTable jTable = new JTable();
  *  TableRowHeader rowHeader = new TableRowHeader(jTable);
  *  rowHeader.setCellRenderer(new TableCurrentRowRenderer());
  *  JScrollPane jScrollPane = new JScrollPane(jTable);
  *  jScrollPane.setRowHeaderView(rowHeader);
  * </pre>
  */
public class TableCurrentRowRenderer extends JLabel
  implements TableCellRenderer, NavigationListener, Serializable
{
  JTable table;
  DataSet dataSet;
  TableRowHeader header;
  Icon arrowIcon;
  int lastRow = 0;


/**
 * <p>Constructs a <code>TableCurrentRowRenderer</code> and sets the icon to
 * <code>image/CurrentRowArrow.gif</code> .</p>
 */
  public TableCurrentRowRenderer() {
    super();
    setOpaque(true);
    setHorizontalAlignment(SwingConstants.RIGHT);
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));   
    arrowIcon = new ImageIcon(TableCurrentRowRenderer.class.getResource("image/CurrentRowArrow.gif"));  
  }

  /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellRenderer</code>.</p>
 */
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {

    if (table != null) {
      JTableHeader columnHeader = table.getTableHeader();
      if (columnHeader != null) {
        setForeground(columnHeader.getForeground());
        setBackground(columnHeader.getBackground());
        setFont(columnHeader.getFont());
      }
      if (this.table != table) {
        if (this.table != null && dataSet != null) {
          dataSet.removeNavigationListener(this);
        }

        this.table = table;
        if (table instanceof JdbTable) {
          dataSet = ((JdbTable) table).getDataSet();
          if (dataSet != null) {
            dataSet.addNavigationListener(this);
          }
        }
        header = (TableRowHeader) value;
      }
      if (column == TableRowHeader.PREF_SIZE) {
        setIcon(arrowIcon);
        return this;
      }
    }

    if (table != null && table.getSelectedRow() == row) {
      setIcon(arrowIcon);
    }
    else {
      setIcon(null);
    }

    return this;
  }

  // NavigationListener Implementation

  public void navigated(NavigationEvent e) {
    if (header != null && dataSet != null && dataSet.getRow() != lastRow) {
      header.repaintRows(lastRow, lastRow);
      lastRow = dataSet.getRow();
      header.repaintRows(lastRow, lastRow);
    }
  }

}

