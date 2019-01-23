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

/**
 * <p><code>TextIcon</code> is an opaque icon which simply paints the
 * value of its <code>text</code> property.  It can be used to display
 * a text message wherever an icon is expected. Its size is determined
 * completely by the bounds of its <code>text</code> property value,
 * which in turn depends upon its <code>font</code>.  If no font is
 * available at the time a request for its size is made, a default
 * dialog plain-style, 12 point font is used. </p>
 */

public class TextIcon extends Component implements Icon {

/**
 *
 * <p>Constructs a <code>TextIcon</code> component.</p>
*/

  public TextIcon() {
  }

 /**
  * <p>Constructs a <code>TextIcon</code> component using the specified
  * <code>String</code> as its text.</p>
  *
  * @param text The <code>String</code> to use for the text.
  */
  public TextIcon(String text) {
    setText(text);
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    updateMetrics();
    Font oldFont = g.getFont();
    Color oldColor = g.getColor();

    g.setFont(getFont());

    FontMetrics fm = g.getFontMetrics(getFont());

    int width = getIconWidth();
    int height = getIconHeight();

    g.setColor(c.getBackground());
    g.fillRect(0, 0, width, height);

    if (text != null && text.length() > 0) {
      int xOffset;
      switch (horizontalAlignment) {
      default:
      case SwingConstants.LEFT:
        xOffset = margins.left;
        break;
      case SwingConstants.CENTER:
        xOffset = (width - fm.stringWidth(text)) / 2;
        break;
      case SwingConstants.RIGHT:
        xOffset = width - fm.stringWidth(text) - margins.right;
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

      g.setColor(c.getForeground());
      g.drawString(text, xOffset, yOffset);
    }

    // revert back to old font and color settings
    g.setFont(oldFont);
    g.setColor(oldColor);

  }

  /**
   * <p>Returns the width of the <code>TextIcon</code> component. The
   * width is based on the <code>text</code> and <code>font</code>
   * properties.</p>
   *
   * @return The width of the component.
   */

  public int getIconWidth() {
    updateMetrics();
    return iconWidth;
  }

 /**
  * <p>Returns the height of the <code>TextIcon</code> component. The
  * height is based on the <code>text</code> and <code>font</code>
  * properties.</p>
  *
  * @return The height of the component.
  */

  public int getIconHeight() {
    updateMetrics();
    return iconHeight;
  }


  /**
   * <p>Returns the font used by the <code>TextIcon</code> component.</p>
   *
   * <p>If no font is available at the time a request for its size is
   * made, a default dialog plain-style, 12 point font is used.</p>
   *
   * @return The font used by the component.
   */

  public Font getFont() {
    if (font == null) {
      Font f = super.getFont();
      if (f == null) {
        font = new Font("Dialog", Font.PLAIN, 12); 
      }
      else {
        font = f;
      }
    }
    return font;
  }

  private void updateMetrics() {
    if (update) {
      if (text == null || text.length() == 0) {
        iconWidth = margins.left + margins.right;
        iconHeight = margins.bottom + margins.top;
      }
      else {
        FontMetrics fontMetrics = getFontMetrics(getFont());
        iconWidth = fontMetrics.stringWidth(text) + margins.left + margins.right;
        iconHeight = fontMetrics.getHeight() + margins.bottom + margins.top;
      }
      update = false;
    }
  }


  /**
   * <p>Returns the preferred size of the <code>TextIcon</code>
   * component. The preferred size is based on the <code>text</code>
   * and <code>font</code> properties.</p>
   *
   * @return The preferred size of the component.
   */
  public Dimension getPreferredSize() {
    size.height = getIconHeight();
    size.width = getIconWidth();
    return size;
  }


 /**
  * <p>Returns the actual size of the <code>TextIcon</code> component.
  * The actual size is based on the <code>text</code> and
  * <code>font</code> properties.</p>
  *
  * @return The actual size of the component.
  */
    public Dimension getSize() {
    return getPreferredSize();
  }


 /**
  * <p>Returns the maximum size of the <code>TextIcon</code> component.
  * The maximum size is based on the <code>text</code> and
  * <code>font</code> properties.</p>
  * @return The maximum size of the component.
  */

  public Dimension getMaximumSize() {
    return getPreferredSize();
  }


 /**
  * <p>Returns the minimum size of the <code>TextIcon</code> component.
  * The minimum size is based on the <code>text</code> and
  * <code>font</code> properties.</p>
  *
  * @return The minimum size of the component.
  */
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void paint(Graphics g) {
    paintIcon(this, g, 0, 0);
  }

  // TextIcon properties

  /**
   * <p>Sets the text to be drawn.</p>
   * @param text The text to be drawn.
   * @see #getText
   */
  public void setText(String text) {
    this.text = text;
    update = true;
  }

  /**
   * <p>Returns the text to be drawn.</p>
   * @return The text to be drawn.
   * @see #setText
   */
  public String getText() {
    return text;
  }

  /**
   * <p>Sets the horizontal alignment of the text. One of:</p>
   * <ul>
   * <li><code>SwingConstants.LEFT</code></li>
   * <li><code>SwingConstants.CENTER</code></li>
   * <li><code>SwingConstants.RIGHT</code></li>
   * </ul>
   * @param horizontalAlignment The horizontal alignment.
   * @see #getHorizontalAlignment
   */
  public void setHorizontalAlignment(int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * <p>Returns the horizontal alignment of the text. One of:</p>
   * <ul>
   * <li><code>SwingConstants.LEFT</code></li>
   * <li><code>SwingConstants.CENTER</code></li>
   * <li><code>SwingConstants.RIGHT</code></li>
   * </ul>
   * @return The horizontal alignment.
   * @see #setHorizontalAlignment
   */
  public int getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * <p>Sets the vertical alignment of the text. One of:</p>
   * <ul>
   * <li><code>SwingConstants.TOP</code></li>
   * <li><code>SwingConstants.CENTER</code></li>
   * <li><code>SwingConstants.BOTTOM</code></li>
   * </ul>
   * @param verticalAlignment The vertical alignment.
   * @see #getVerticalAlignment
   */
  public void setVerticalAlignment(int verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
  }

  /**
   * <p>Returns the vertical alignment of the text. One of:</p>
   * <ul>
   * <li><code>SwingConstants.TOP</code></li>
   * <li><code>SwingConstants.CENTER</code></li>
   * <li><code>SwingConstants.BOTTOM</code></li>
   * </ul>
   * @return The vertical alignment.
   * @see #setVerticalAlignment
   */
  public int getVerticalAlignment() {
    return verticalAlignment;
  }

  /**
   * <p>Sets the margin for the text. Note that a reference to the
   * parameter rather than a copy is kept, so changing the object after
   * setting this property may have unexpected results.</p>
   *
   * @param margins The margin for the text.
   * @see #getMargins()
   */
  public void setMargins(Insets margins) {
    this.margins = margins;
  }

  /**
   * <p>Returns the margin for the text.</p>
   *
   * @return The margin for the text.
   * @see #setMargins
   */
  public Insets getMargins() {
    return margins;
  }

  private String text;
  private int horizontalAlignment = SwingConstants.LEFT;
  private int verticalAlignment = SwingConstants.CENTER;
  private Insets margins = new Insets(0, 0, 0, 0);
  private Dimension size = new Dimension();
  private Font font;
  private int iconWidth;
  private int iconHeight;
  private boolean update;
}
