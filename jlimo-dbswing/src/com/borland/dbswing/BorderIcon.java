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
import javax.swing.*;
import javax.swing.border.*;

/**
 * <p><code>BorderIcon</code> is a simple utility class for creating an <code>Icon</code> with a
 * <code>Border</code>. It offers a particularly convenient way of dynamically
 * creating simple rollover icons out of <code>ImageIcons</code> without having
 * to create multiple image files.</p>
 *
 * <p>For example, to create a typical rollover icon (an icon with raised
 * edges) out of an <code>ImageIcon</code>, do this:</p>
 
 *<pre>
 *  ImageIcon imageIcon = new ImageIcon("image.gif");
 *  JButton jButton = new JButton(imageIcon);
 *  jButton.setRolloverIcon(new BorderIcon(imageIcon));
 *  jButton.setBorder(null); // allows image to fill entire button
 *</pre>
 *<p><code>BorderIcon</code> uses a raised bevel border by default if a border
 * is not explicitly specified.</p>
 */
public class BorderIcon implements Icon, java.io.Serializable {

/**
 * <p>Creates a <code>BorderIcon</code> with no specified border or
 * icon. Calls the constructor of this class which
 * takes a <code>Border</code> and an <code>Icon</code> as parameters.
 * Instantiates a new border using <code>BorderFactory's
 * createRaisedBevelBorder()</code> method, and passes
 * this new border, along with null for the <code>Icon,</code>
 * to the other constructor. </p>
 */
  public BorderIcon() {
    this(BorderFactory.createRaisedBevelBorder(), null);
  }


/**
 * <p>Creates a <code>BorderIcon</code> with the specified border.
 * Calls the constructor of this class which takes a
 * <code>Border</code> and an <code>Icon</code> as parameters. Passes the specified <code>Border</code> and null  
 * for the <code>Icon</code> to the  other constructor.</p>
 * 
 * @param border The <code>Border</code> that becomes the border for the icon. 
 */
  public BorderIcon(Border border) {
    this(border, null);
  }


/** 
 * <p>Creates a <code>BorderIcon</code> with a specified icon.  
 * Calls the constructor of this class which takes a
 * <code>Border</code> and an <code>Icon</code> as parameters. Instantiates a
 *  new border using <code>BorderFactory's
createRaisedBevelBorder()</code> method, and passes this
 *  new border, along with the specified icon, to
 * the other constructor. </p>
 * 
 * @param icon The <code>Icon</code> that becomes the icon for the <code>BorderIcon.</code>
  */
  public BorderIcon(Icon icon) {
    this(BorderFactory.createRaisedBevelBorder(), icon);
  }


/** 
 * <p>Creates a <code>BorderIcon</code> with a specified border and icon.  </p>
 *
 * @param border The <code>Border</code> that becomes the border for the icon.
 * @param icon The <code>Icon</code> that becomes the icon for the icon.
  */

  public BorderIcon(Border border, Icon icon) {
    this.border = border;
    this.icon = icon;
  }

  /**
   * <p>Sets the border for this icon.  The default border
   * is a raised-bevel border.</p>
   *
   * @param border The border for the icon.
   * @see #getBorder
   */
  public void setBorder(Border border) {
    this.border = border;
  }

  /**
   * <p>Returns the border for this icon.</p>
   *
   * @return The border for the icon.
   * @see #setBorder
   */
  public Border getBorder() {
    return border;
  }

  /**
   * <p>Sets the icon used by this icon.</p>
   *
   * @param icon The icon to use.
   * @see #getIcon
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  /**
   * <p>Returns the icon used by this icon.</p>
   *
   * @return The icon used.
   * @see #setIcon
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * <p>Paints the icon with a border.</p>
   */
  public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
    if (icon != null) {
      icon.paintIcon(c, g, x, y);
      border.paintBorder(c, g, x, y, getIconWidth(), getIconHeight());
    }
  }

  /**
   * <p>Returns width of this icon.</p>
   *
   * @return The width of the icon.
   */
  public int getIconWidth() {
    return (icon == null) ? 0 : icon.getIconWidth();
  }

  /**
   * <p>Returns height of this icon.</p>
   *
   * @return The height of the icon.
   */
  public int getIconHeight() {
    return (icon == null) ? 0 : icon.getIconHeight();
  }

  /** Icon used by this icon */
  private Icon icon;

  /** Border to be drawn around this icon */
  private Border border;
}
