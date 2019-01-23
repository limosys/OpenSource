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
package com.borland.dbswing.plaf.basic;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.plaf.*;

import com.borland.dbswing.*;
import com.borland.dbswing.plaf.*;

/**
 * Basic UI delegate for TableRowHeader row header component.
 * Allows user to vertically drag the bottom of the very first row cell
 * in order to set the Table's row height.
 */
public class BasicJdbTableRowHeaderUI extends JdbTableRowHeaderUI {
  TableRowHeader header;
  JTable table;
  TableModel model;
  CellRendererPane rendererPane;
  MouseInputListener mouseInputListener;
  Rectangle cellRect = new Rectangle();
  int lastResizingMark = 0;
  boolean ignoreSingleRowPaint = false;

  public void installUI(JComponent c) {
    header = (TableRowHeader) c;

    table = header.getTable();
    model = header.getModel();

    rendererPane = new CellRendererPane();
    header.add(rendererPane);

    installDefaults();
    installListeners();

  }

  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(header, "TableHeader.background",   
                                     "TableHeader.foreground", "TableHeader.font");   
  }

  protected void installListeners() {
    mouseInputListener = createMouseInputListener();

    header.addMouseListener(mouseInputListener);
    header.addMouseMotionListener(mouseInputListener);
  }

  protected MouseInputListener createMouseInputListener() {
    return new MouseInputHandler();
  }

  public void uninstallUI(JComponent c) {
    uninstallDefaults();
    uninstallListeners();

    header.remove(rendererPane);

    header = null;
    table = null;
    model = null;
    rendererPane = null;

  }

  protected void uninstallDefaults() {
  }

  protected void uninstallListeners() {
    header.removeMouseListener(mouseInputListener);
    header.removeMouseMotionListener(mouseInputListener);

    mouseInputListener = null;

  }

  public class MouseInputHandler implements MouseInputListener {
    private int mouseStartY;
    private Rectangle triggerRect = new Rectangle();
    private int newRowHeight = 21;  // default row height

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Since only the first row's cell can be resized, ignore
     * any mouse presses not at the bottom of the first cell.
     * Also, ignore all mouse presses if row header resizing
     * is disabled.
     */
    public void mousePressed(MouseEvent e) {
      if (!header.isHeightResizable()) {
        return;
      }

      Point point = e.getPoint();
      if (isWithinTriggerRect(point)) {
        mouseStartY = point.y;
      } else {
        mouseStartY = 32768;  // an arbitrary sentinel value
      }
    }

    public void mouseMoved(MouseEvent e) {
      if (isWithinTriggerRect(e.getPoint())) {
        Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
        if (header.getCursor() != resizeCursor) {
          header.setCursor(resizeCursor);
        }
      }
      else {
        Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        if (header.getCursor() != defaultCursor) {
          header.setCursor(defaultCursor);
        }
      }
    }

    public void mouseDragged(MouseEvent e) {
      if (mouseStartY != 32768) {
        int pendingHeight = table.getRowHeight() + e.getY() - mouseStartY;
        if (pendingHeight > 0) {
          newRowHeight = pendingHeight;
          //    if (header.isResizeTableWhileSizing() && table != null) {
          if (header.isResizeTableWhileSizing()) {
            Rectangle newViewRect = ((JViewport) header.getParent()).getViewRect();
// JDK 1.3
            if (DBUtilities.is1pt3()) {
              newViewRect.y = (newViewRect.y / (table.getRowHeight())) * (newRowHeight);
            }
            else {
// JDK 1.2.2
              newViewRect.y = (newViewRect.y / (table.getRowHeight() + table.getRowMargin())) *
                (newRowHeight + table.getRowMargin());
            }
            table.setRowHeight(newRowHeight);
            table.scrollRectToVisible(newViewRect);
            mouseStartY = newViewRect.y + newRowHeight + table.getRowMargin();
            header.repaint();
//          table.revalidate();
//          table.repaint();
          }
          Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
          if (header.getCursor() != resizeCursor) {
            header.setCursor(resizeCursor);
          }
        }
        else {
          Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
          if (header.getCursor() != defaultCursor) {
            header.setCursor(defaultCursor);
          }
        }
      }
    }

    public void mouseReleased(MouseEvent e) {

      if (!header.isHeightResizable()) {
        return;
      }

      if (mouseStartY != 32768) {
        int pendingHeight = table.getRowHeight() + e.getY() - mouseStartY;

        if (pendingHeight > 0) {
          newRowHeight = pendingHeight;
          Rectangle newViewRect = ((JViewport) header.getParent()).getViewRect();
// JDK 1.3
          if (DBUtilities.is1pt3()) {
            newViewRect.y = (newViewRect.y / (table.getRowHeight())) * (newRowHeight);
          }
          else {
// JDK 1.2.2
            newViewRect.y = (newViewRect.y / (table.getRowHeight() + table.getRowMargin())) *
              (newRowHeight + table.getRowMargin());
          }
          table.setRowHeight(newRowHeight);
          table.scrollRectToVisible(newViewRect);

          header.repaint();
//      if (table != null) {
          table.repaint();
//      }
        }
        Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        if (header.getCursor() != defaultCursor) {
          header.setCursor(defaultCursor);
        }
      }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    // Checks if the bottom border of the first row cell was hit; use a 6-pixel
    // high target area, the same range as BasicTableHeader.
    private boolean isWithinTriggerRect(Point p) {
//        if (table == null) {
//      return false;
//        }

// JDK 1.3
      int singleCellHeight;
      if (DBUtilities.is1pt3()) {
        singleCellHeight = table.getRowHeight();
      }
      else {
// JDK 1.2.2
        singleCellHeight = table.getRowHeight() + table.getRowMargin();
      }
      triggerRect.width = header.getWidth();
//      triggerRect.y = singleCellHeight - 3;
      triggerRect.y = lastResizingMark - 3;
      triggerRect.height = 6;

      if (triggerRect.contains(p)) {
        return true;
      }
      return false;
    }
  }

  public void repaintRows(int firstRow, int lastRow) {
// JDK 1.3
    int singleCellHeight;
    if (DBUtilities.is1pt3()) {
      singleCellHeight = table.getRowHeight();
    }
    else {
// JDK 1.2.2
      singleCellHeight = table.getRowHeight() + table.getRowMargin();
    }
    ignoreSingleRowPaint = true;
    header.repaint(0, 0, firstRow * singleCellHeight, header.getWidth(), (lastRow - firstRow + 1) * singleCellHeight);
  }

  public void paint(Graphics g, JComponent c) {
    if (table == null) {
      return;
    }

    Rectangle clipBounds = g.getClipBounds();

// JDK 1.3
    int singleCellHeight;
    if (DBUtilities.is1pt3()) {
      singleCellHeight = table.getRowHeight();
    }
    else {
// JDK 1.2.2
      singleCellHeight = table.getRowHeight() + table.getRowMargin();
    }
    int firstRow = clipBounds.y / singleCellHeight;
    int lastRow = (clipBounds.y + clipBounds.height) / singleCellHeight;
    lastRow = Math.min(lastRow, table.getRowCount() - 1);
    cellRect.height = singleCellHeight;
    cellRect.width = c.getWidth();
    cellRect.y = clipBounds.y - (clipBounds.y % singleCellHeight);

    Component actualPainter;

    for (int rowNo = firstRow; rowNo <= lastRow; rowNo++) {
      actualPainter = header.getCellRenderer().getTableCellRendererComponent(table, header, false, false, rowNo, header.PAINT);

      rendererPane.add(actualPainter);
      rendererPane.paintComponent(g, actualPainter, header, cellRect.x, cellRect.y,
                                  cellRect.width, cellRect.height, false);
      cellRect.y += cellRect.height;
      if (rowNo == firstRow) {
        if (!ignoreSingleRowPaint) {
          lastResizingMark = cellRect.y;
        }
        ignoreSingleRowPaint = false;
      }
    }

  }

  /**
   * Returns the preferred size of the row header.
   */
  public Dimension getPreferredSize(JComponent c) {
    int height = 0;
    int width = 0;

    if (table != null) {
      height = table.getSize().height;
      width = header.getCellRenderer().getTableCellRendererComponent(table, header, false, false, model.getRowCount(), header.PREF_SIZE).getPreferredSize().width;
    }
    return new Dimension(width, height);
  }

  /**
   * Returns the minimum size of the row header.
   */
  public Dimension getMinimumSize(JComponent c) {
    int height = 0;
    int width = 0;

    if (table != null) {
      height = table.getSize().height;
      width = header.getCellRenderer().getTableCellRendererComponent(table, header, false, false, model.getRowCount(), header.MIN_SIZE).getMinimumSize().width;
    }
    return new Dimension(width, height);
  }

  /**
   * Returns the maximum size of the row header.
   */
  public Dimension getMaximumSize(JComponent c) {
    int height = 0;
    int width = 0;

    if (table != null) {
      height = table.getSize().height;
      width = header.getCellRenderer().getTableCellRendererComponent(table, header, false, false, model.getRowCount(), header.MAX_SIZE).getMaximumSize().width;
    }
    return new Dimension(width, height);
  }

  public static ComponentUI createUI(JComponent c) {
    return new BasicJdbTableRowHeaderUI();
  }

}

