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

/**
 * <p>Can be used in place of a
 * <code>TableColumn's</code> default header renderer to display multi-line
 * column headers. Note that because this renderer maintains state information,
 * a single instance cannot be reused as the renderer for multiple
 * <code>TableColumn</code> objects. This renderer also assumes that once its
 * value has ben set, it is unlikely to change and tries to take advantage of
 * this assumption to achieve better performance.</p>
 *
 * <p>Because <code>TableColumn.setHeaderValue()</code> takes an
 * <code>Object</code> as its parameter, you have the option of passing a
 * <code>String</code> containing newline characters (<KBD>'\n'</KBD>) to it,
 * or an array of <code>String</code> objects. Because passing an array is more
 * efficient, consider this option if possible.
 * <code>TableHeaderRenderer</code> is used internally by 
 * <a  href="JdbTable.html"><code>JdbTable</code></a> whenever there is a
 * header which consists of either an array of <code>Strings</code> or a
 * <code>String</code> containing newline characters.</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * // specify JdbHeader as the first column's renderer
 * jTable.getColumnModel().getColumn(0).setHeaderRenderer(new TableHeaderRenderer());
 *
 * // specify a multi-line column header
 * jTable.getColumnModel().getColumn(0).setHeaderValue("Line 1\nLine2");
 * jTable.getColumnModel().getColumn(1).setHeaderValue(new String[]{&quot;Line1&quot;, &quot;Line2&quot;});
* </pre>
*/

//
// From previous JBDoc comment. did not want to lose. NW 2/02
//
// * A possible future enhancement would be to allow an icon to be displayed as
// well.
//
//
// * Special Features:
// * supports multiple lines
// * Properties:
// * none
// * *foreground inherited from current JTableHeader
// * *background inherited from current JTableHeader
// * *font inherited from current JTableHeader
// 
// * Usage example:
// *<code>
// *   // specify TableHeaderRenderer as the first column's renderer
// *   jTable.getColumnModel().getColumn(0).setHeaderRenderer(new
// TableHeaderRenderer());
// *   // specify a multi-line column header
// *   jTable.getColumnModel().getColumn(0).setHeaderValue("Line 1\nLine2");
// *   jTable.getColumnModel().getColumn(1).setHeaderValue(new String [] {
// "Line 1, "Line2" });
// *</code>

public class TableHeaderRenderer implements TableCellRenderer, Serializable {
  private transient boolean alreadyInitialized = false;
  transient Object previousValue;
  transient JComponent renderer;

  
    /**  
   * <p>Required for implementation of <code>javax.swing.table.TableCellRenderer</code>.</p>
   */
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {

    if (alreadyInitialized && previousValue.equals(value)) {
      return renderer;
    }

    if (value == null) {
      if (renderer == null || renderer instanceof JList) {
        renderer = new JLabel();
      }
      else {
        ((JLabel) renderer).setText("");        
      }
    }
    else if (value instanceof String []) {
      if (renderer == null || renderer instanceof JLabel) {
        renderer = new JList((String []) value);
      }
      else {
        ((JList) renderer).setListData((String []) value);
      }
      ((JList) renderer).setVisibleRowCount(((String []) value).length);
    }
    else {
      // Do a toString() on the value and parse it for multiple lines.
      String text = value.toString();

      if (text.indexOf('\n') != -1) {
        // We got multiple lines.
        // There will likely only be a few, short lines, so we'll be lazy here
        // and parse the string twice.  As long as the value doesn't change
        // the next time this renderer is called (an unlikely situation), then
        // the cached value will be used and this won't adversely affect
        // performance.
        String line = text;
        int lines = 0;
        int offset = 0;

        while ((offset = line.indexOf('\n')) != -1) {
          lines++;
          line = line.substring(offset+1);
        }
        String [] textLines = new String[lines+1];
        line = text;
        lines = 0;
        while ((offset = line.indexOf('\n')) != -1) {
          textLines[lines++] = line.substring(0, offset);
          line = line.substring(offset+1);
        }
        textLines[lines] = line;

        if (renderer == null || renderer instanceof JLabel) {
          renderer = new JList(textLines);
        }
        else {
          ((JList) renderer).setListData(textLines);
        }
        ((JList) renderer).setVisibleRowCount(textLines.length);
      }
      else {
        // We got a single-line.
        if (renderer == null || renderer instanceof JList) {
          renderer = new JLabel(text);
        }
        else {
          ((JLabel) renderer).setText(text);
        }
      }

    }

    if (table != null) {
      JTableHeader header = table.getTableHeader();
      if (header != null) {
        renderer.setForeground(header.getForeground());
        renderer.setBackground(header.getBackground());
        renderer.setFont(header.getFont());
      }
    }

    renderer.setBorder(UIManager.getBorder("TableHeader.cellBorder"));   
    renderer.setOpaque(true);

    previousValue = value;
    alreadyInitialized = true;

    return renderer;
  }
}

