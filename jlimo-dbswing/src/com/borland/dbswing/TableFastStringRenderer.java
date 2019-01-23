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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.borland.dx.dataset.*;

 /**
  * <p>An optimized implementation of the
  * <code>javax.swing.table.TableCellRenderer</code> for painting
  * <code>Strings</code>. It is <code>JdbTable's</code> default cell renderer for
  * <code>DataSet</code> data types renderable as <code>Strings</code>.</p>
  *
  * <p><code>TableFastStringRenderer</code> also provides <code>JdbTable's</code>
  * support for the DataExpress <code>ColumnPaintListener</code> event. </p>
  *
  * <p><code>TableFastStringRenderer</code> can be used as a table cell renderer
  * for a <code>JTable</code> or <code>JdbTable</code> when not used with a
  * <code>DataSet</code>.  In such cases, the value to be rendered is converted to
  * a <code>String</code> by calling the <code>toString()</code> method. </p>
  *
  * @see JdbTable
  * @see ColumnPaintListener
  */
public class TableFastStringRenderer extends Component implements TableCellRenderer, CustomPaintSite, java.io.Serializable {
  private String value;
  private int horizontalAlignment = SwingConstants.LEFT;
  private int verticalAlignment = SwingConstants.CENTER;
  private Color foreground;
  private Color background;
  private int alignment;
  private Insets margins;
  private Border border;
  private int x = 0;
  private int y = 0;
  private int width = 0;
  private int height = 0;
  private Border noFocusBorder;
  private int defaultAlignment;
  private Color defaultForeground;
  private Color defaultBackground;
  private Insets defaultMargins;
  private Font defaultFont;


  /**
   * <p>Constructs a <code>TableFastStringRenderer</code> component with no parameters.</p>
   */
  public TableFastStringRenderer() {
    super();
    noFocusBorder = new EmptyBorder(1, 2, 1, 2);
    setDefaultMargins(new Insets(2, 2, 2, 2));
  }

  // implements javax.swing.table.TableCellRenderer
    /**
   * <p>Required for implementation of <code>javax.swing.table.TableCellRenderer</code>.</p>
   */
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    if (defaultForeground == null) {
      setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
    }

    if (defaultBackground == null) {
      setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
    }

    setDefaultFont(table.getFont());

    if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); 
      if (table.isCellEditable(row, column) && table instanceof JdbTable) {
        JdbTable jdbTable = (JdbTable) table;
        if (jdbTable.getEditableFocusedCellForeground() != null) {
          setDefaultForeground(jdbTable.getEditableFocusedCellForeground());
        }
        if (jdbTable.getEditableFocusedCellBackground() != null) {
          setDefaultBackground(jdbTable.getEditableFocusedCellBackground());
        }
      }
    }
    else {
      setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
      setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
//      setDefaultForeground(null);
//      setDefaultBackground(null);
      setBorder(noFocusBorder);
    }

    setValue(value);

    return this;
  }

  /**
   * <p>Sets the default foreground <code>color</code> for the cell being rendered.</p>
   *
   * @param foreground A <code>java.awt.Color</code> object representing the foreground color.
   */
  public void setDefaultForeground(Color foreground) {
    defaultForeground = foreground;
    setForeground(foreground);
  }

  /**
   * <p>Sets the default background <code>color</code> for the cell being rendered.</p>
   *
   * @param background A <code>java.awt.Color</code> object representing the background color.
   */
  public void setDefaultBackground(Color background) {
    defaultBackground = background;
    setBackground(background);
  }

  /**
   * <p>Sets the default <code>alignment</code> setting for the cell being rendered.  </p>
   *
   * @param alignment An <code>int</code> representing the alignment bitmask. One of the com.borland.dx.text.Alignment</code> variables.
   */
  public void setDefaultAlignment(int alignment) {
    defaultAlignment = alignment;
    convertAlignment(alignment);
  }

  private void convertAlignment(int alignment) {
    setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(alignment, true));
    setVerticalAlignment(DBUtilities.convertJBCLToSwingAlignment(alignment, false));
  }

  /**
   * <p>Sets the default <code>font</code> to use for the item being rendered.</p>
   *
   * @param font A <code>java.awt.Font</code> object representing the font to use.
   */
  public void setDefaultFont(Font font) {
    defaultFont = font;
    setFont(font);
  }

  /**
   * <p>Sets the default <code>margins</code> for the cell being rendered. Calls
   * <code>setItemMargins()</code>, passing it <code>margins</code>.</p>
   *
   * @param margins An <code>Insets</code> object representing the margins for this item.
   */
  public void setDefaultMargins(Insets margins) {
    defaultMargins = margins;
    setItemMargins(margins);
  }

  // CustomPaintSite implementation

  public void reset() {
    setForeground(defaultForeground);
    setBackground(defaultBackground);
    setFont(defaultFont);
    convertAlignment(defaultAlignment);
    setItemMargins(defaultMargins);
  }

  /**
   * <p>Sets the <code>alignment</code> setting for the cell  being rendered.  </p>
   *
   * @param alignment An <code>int</code> representing the alignment bitmask. One of the <code>com.borland.dx.text.Alignment.html</code> variables.
  * @see #getAlignment
  */
  public void setAlignment(int alignment) {
    this.alignment = alignment;
    convertAlignment(alignment);
  }

  /**
   * <p>Sets the <code>margins</code> for the item in the cell being rendered. The
   * <code>setItemMargins()</code> method calls <code>setMargins()</code>, passing it
   * <code>margins</code>.</p>
   *
   * @param margins An <code>Insets</code> object representing the margins for this item.
   * @see #getItemMargins
   */
  public void setItemMargins(Insets margins) {
    setMargins(margins);
  }

  /**
   * <p>Returns whether or not the <code>TableFastStringRenderer</code> should erase its
   * background. </p>
   *
   * @return <code>true</code> if transparent, <code>false</code> if not.
   */
  public boolean isTransparent() {
    return false;
  }

  /**
   * <p>Returns the <code>alignment</code> setting for the cell being rendered.  </p>
   *
   * @return An <code>int</code> representing the alignment bitmask. One of the <code>com.borland.dx.text.Alignment.html</code> variables.
  * @see #setAlignment
   */
  public int getAlignment() {
    return alignment;
  }

  /**
   * <p>Returns the <code>margins</code> for the item in the cell being rendered. The
   * <code>setItemMargins()</code> method calls <code>setMargins()</code>, passing it
   * <code>margins</code>.</p>
   *
   * @return An <code>Insets</code> object representing the margins for this item.
  * @see #setItemMargins
   */
  public Insets getItemMargins() {
    return margins;
  }

  /**
   * <p> Returns the hosting-site component representing the
   * <code>TableFastStringRenderer</code>.  This is used for coordinating space
   * calculations, as well as to provide a component for <code>ItemPainter</code>
   * implementations that require one.</p>
   *
   * @return The hosting-site component.
   */
  public Component getSiteComponent() {
    return this;
  }

  /**
   * <p>Sets the bounds for the cell being rendered. </p>
   *
   * @param x The left-topc corner of the cell.
   * @param y The bottom-right cornder of the cell.
   * @param width The cell's width.
   * @param height The cell's height.
   */

  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * <p>Sets the value being rendered.</p>
   *
   * @param value The value being rendered.
   */
  public void setValue(Object value) {
    this.value = (value == null) ? "" : value.toString();   
  }

/**
 * <p>Sets the horizontal alignment for the item in the cell.  One of:</p>
 *
 * <ul>
 * <li><code>SwingConstants.LEFT</code></li>
 * <li><code>SwingConstants.CENTER</code></li>
 * <li><code>SwingConstants.RIGHT</code></li>
 * </ul>
 *
 * @param horizontalAlignment The horizontal <code>SwingConstants</code> alignment.
  */
  public void setHorizontalAlignment(int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }
/**
 * <p>Sets the vertical alignment for the item in the cell.  One of:</p>
 *
 * <ul>
 * <li><code>SwingConstants.CENTER</code></li>
 * <li><code>SwingConstants.TOP</code></li>
 * <li><code>SwingConstants.BOTTOM</code></li>
 * </ul>
 * @param verticalAlignment The vertical <code>SwingConstants</code> alignment.
 */
  public void setVerticalAlignment(int verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
  }


  /**
   * <p>Sets the <code>margins</code> for the cell being rendered. Used for backward compatibility.</p>
   *
   * @param margins An <code>Insets</code> object representing the margins for this item.
   */
  public void setMargins(Insets margins) {
    this.margins = margins;
  }

  /**
   * <p>Sets the <code>border</code> setting for the cell being rendered. Used
   * for backward compatibility.</p>
   *
   * @param border A <code>Border </code> object representing the border for this item.
   * @see #getBorder
   */
  public void setBorder(Border border) {
    this.border = border;
  }

  /**
   * <p>Returns the <code>border</code> setting for the cell being rendered. Used
   * for backward compatibility.</p>
   *
   * @return A <code>Border </code> object representing the border for this item.
   * @see #setBorder
   */
  public Border getBorder() {
    return border;
  }

  public void paint(Graphics g) {
    Font oldFont = g.getFont();
    Color oldColor = g.getColor();

    g.setFont(getFont());

    FontMetrics fm = g.getFontMetrics(getFont());

    int xOffset;
    switch (horizontalAlignment) {
    default:
    case SwingConstants.LEFT:
      xOffset = margins.left;
      break;
    case SwingConstants.CENTER:
      xOffset = (width - fm.stringWidth(value)) / 2;
      break;
    case SwingConstants.RIGHT:
      xOffset = width - fm.stringWidth(value) - margins.right;
    }

    int yOffset;
    switch (verticalAlignment) {
    default:
    case SwingConstants.TOP:
      yOffset = margins.top;
      break;
    case SwingConstants.CENTER:
      yOffset = (height - fm.getHeight()) / 2;
      break;
    case SwingConstants.BOTTOM:
      yOffset = height - fm.getHeight() - margins.bottom;
      break;
    }
    yOffset += fm.getLeading() + fm.getAscent();

    g.setColor(getBackground());
    g.fillRect(0, 0, width, height);

    if (value != null) {
      g.setColor(getForeground());
      g.drawString(value, xOffset, yOffset);
    }

    border.paintBorder(this, g, 0, 0, width, height);

    // revert back to old font and color settings
    g.setFont(oldFont);
    g.setColor(oldColor);
  }
}
