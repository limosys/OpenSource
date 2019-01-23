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

/**
 * <p>The <code>ColumnLayout LayoutManager</code> lays out components vertically in a single column.
 * It is a convenient layout for building columns of display labels with adjacent
 * entry fields.</p>
 *
 * <p>It has the following properties:</p>
 *
 *<ul>
 *<li><code>hgap</code> - Horizontal gap between components</li>
 *<li><code>vgap</code> - Vertical gap between components</li>
 *<li><code>verticalFill</code> - Increases the height of the last component to fill the container</li>
 *<li><code>horizontalFill</code> - Increases the width of all components to fill the container</li>
 *<li><code>horizontalAlignment</code> - Horizontal alignment of each component in the container</li>
 *<li><code>verticalAlignment</code> - Vertical alignment of components in the container</li>
 *</ul>
 */
public class ColumnLayout implements LayoutManager, java.io.Serializable {

  // vertical alignment

  /**
  * <p>Vertical alignment constant. Value is 0.</p>
  */
  public static final int TOP    = 0;

  /**
  * <p>Vertical alignment constant. Value is 1.</p>
  */
  public static final int MIDDLE = 1;

  /**
  * <p>Vertical alignment constant. Value is 2.</p>
  */
  public static final int BOTTOM = 2;


  // horizontal alignment

  /**
  * <p>Horizontal alignment constant. Value is 0.</p>
  */
  public static final int LEFT   = 0;

  /**
  * <p>Horizontal alignment constant. Value is 0.</p>
  */
  public static final int RIGHT  = 2;


  private int verticalAlignment;  // default alignment is TOP
  private int hgap;
  private int vgap;
  private boolean fillWidth;
  private boolean fillHeight;
  private int horizontalAlignment;  // default alignment is LEFT
  private Dimension targetSize = new Dimension();

  /**
   * <p>Constructs a default <code>ColumnLayout</code> with TOP LEFT alignment.
   * Width and height fill are set to <code>false</code>.</p>
   */
  public ColumnLayout() {
    this(TOP, 5, 5, false, false);
  }

  /**
   * <p>Constructs a new <code>ColumnLayout</code>.</p>
   *
   * @param verticalAlignment The alignment value
   * @param hgap The horizontal gap value
   * @param vgap The vertical gap value
   * @param fillWidth Whether to fill width
   * @param fillHeight Whether (last component extends) to fill height
   */
  public ColumnLayout(int verticalAlignment, int hgap, int vgap, boolean fillWidth, boolean fillHeight) {
    this.verticalAlignment = verticalAlignment;
    this.hgap = hgap;
    this.vgap = vgap;
    this.fillWidth = fillWidth;
    this.fillHeight = fillHeight;
  }

  /**
   * <p>Gets the horizontal gap between components.</p>
   *
   * @return The horizontal gap.
   * @see #setHgap
   */
  public int getHgap() {
    return hgap;
  }

  /**
   * <p>Sets the horizontal gap between components.</p>
   *
   * @param hgap The horizontal gap.
   * @see #getHgap
   */
  public void setHgap(int hgap) {
    this.hgap = hgap;
  }

  /**
   * <p>Gets the vertical gap between components.</p>
   *
   * @return The vertical gap.
   * @see #setVgap
   */
  public int getVgap() {
    return vgap;
  }

  /**
   * <p>Sets the vertical gap between components.</p>
   *
   * @param vgap The vertical gap.
   * @see #getVgap
   */
  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  /**
   * <p>Sets whether the last component added to the layout should be
   * extended vertically to fill the height of the container.</p>
   *
   * @param fillHeight <code>True</code> to extend the last component, <code>false</code> not to extend it.
   * @see #getVerticalFill
   */
  public void setVerticalFill(boolean fillHeight) {
    this.fillHeight = fillHeight;
  }

  /**
   * <p>Returns whether the last component added to the layout should be
   * extended vertically to fill the height of the container.</p>
   *
   * @return <code>True</code> if last component extends vertically, <code>false</code> if it does not.
   * @see #setVerticalFill
   */
  public boolean getVerticalFill() {
    return fillHeight;
  }

  /**
   * <p>Sets whether every component added to the layout should be
   * extended horizontally to fill the width of the container.</p>
   *
   * @param fillWidth <code>True</code> to extend the last component, <code>false</code> not to extend it.
   * @see #getHorizontalFill
   */
  public void setHorizontalFill(boolean fillWidth) {
    this.fillWidth = fillWidth;
  }

  /**
   * <p>Returns whether every component added to the layout should be
   * extended horizontally to fill the width of the container.</p>
   *
   * @return <code>True</code> if last component extends horizontally, <code>false</code> if it does not.
   * @see #setHorizontalFill
   */
  public boolean getHorizontalFill() {
    return fillWidth;
  }

  /**
   * <p>Sets the horizontal alignment (either <code>ColumnLayout.LEFT, ColumnLayout.MIDDLE,</code>
   * or <code>ColumnLayout.RIGHT</code>) of components managed by <code>ColumnLayout</code>.</p>
   *
   * @param horizontalAlignment  0 for left alignment, 1 for middle alignment, or 2 for right alignment.
   * @see #getHorizontalAlignment
   */
  public void setHorizontalAlignment(int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * <p>Gets the horizontal alignment (either <code>ColumnLayout.LEFT, ColumnLayout.MIDDLE,</code>
   * or <code>ColumnLayout.RIGHT</code>) of components managed by <code>ColumnLayout</code>.</p>
   *
   * @return 0 for left alignment, 1 for middle alignment, or 2 for right alignment.
   * @see #setHorizontalAlignment
   */
  public int getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * <p>Sets the vertical alignment (either <code>ColumnLayout.TOP, ColumnLayout.MIDDLE,</code>
   * or <code>ColumnLayout.BOTTOM</code>) of components managed by <code>ColumnLayout</code>.</p>
   *
   * @param verticalAlignment  0 for top  alignment, 1 for middle alignment, or 2 for bottom  alignment.
   * @see #getVerticalAlignment
   */
  public void setVerticalAlignment(int verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
  }

  /**
   * <p>Sets the vertical alignment (either <code>ColumnLayout.TOP, ColumnLayout.MIDDLE,</code>
   * or <code>ColumnLayout.BOTTOM</code>) of components managed by ColumnLayout.</p>
   *
   * @return 0 for top  alignment, 1 for middle alignment, or 2 for bottom  alignment.
   * @see #setVerticalAlignment
   */
  public int getVerticalAlignment() {
    return verticalAlignment;
  }

  /**
   * <p>Adds the specified component to the layout. Not used by this class.</p>
   *
   * @param name The name of the component.
   * @param comp The component to be added.
   */
  public void addLayoutComponent(String name, Component comp) {
  }

  /**
   * <p>Removes the specified component from the layout. Not used by this class.</p>
   *
   * @param comp The component to remove.
   * @see java.awt.Container#removeAll
   */
  public void removeLayoutComponent(Component comp) {
  }

  /**
   * <p>Returns the preferred dimensions given the components
   * in the target container.</p>
   *
   * @param target The component to lay out.
   * @return The preferred dimensions given the components
   * in the target container.
   */
  public Dimension preferredLayoutSize(Container target) {
    targetSize.width = 0;
    targetSize.height = 0;

    for (int i = 0 ; i < target.getComponentCount(); i++) {
      Component m = target.getComponent(i);
      if (m.isVisible()) {
        Dimension d = m.getPreferredSize();
        targetSize.width = Math.max(targetSize.width, d.width);
        if (i > 0) {
          targetSize.height += vgap;
        }
        targetSize.height += d.height;
      }
    }
    Insets insets = target.getInsets();
    targetSize.width += insets.left + insets.right + hgap*2;
    targetSize.height += insets.top + insets.bottom + vgap*2;
    return targetSize;
  }

  /**
   * <p>Returns the minimum size needed to layout the target container.</p>
   *
   * @param target The component to lay out.
   * @return The minimum size needed to layout the target container.
   */
  public Dimension minimumLayoutSize(Container target) {
    targetSize.width = 0;
    targetSize.height = 0;

    for (int i = 0 ; i < target.getComponentCount(); i++) {
      Component m = target.getComponent(i);
      if (m.isVisible()) {
        Dimension d = m.getMinimumSize();
        targetSize.width = Math.max(targetSize.width, d.width);
        if (i > 0) {
          targetSize.height += vgap;
        }
        targetSize.height += d.height;
      }
    }
    Insets insets = target.getInsets();
    targetSize.width += insets.left + insets.right + hgap*2;
    targetSize.height += insets.top + insets.bottom + vgap*2;
    return targetSize;
  }

  /**
   * <p>Places the components defined by first to last within the target
   * container using the bounds box defined.</p>
   *
   * @param target The container.
   * @param x The x coordinate of the area.
   * @param y The y coordinate of the area.
   * @param width The width of the area.
   * @param height The height of the area.
   * @param first The first component of the container to place.
   * @param last The last component of the container to place.
   */
  private void layoutComponents(Container target, int x, int y, int width, int height,
                                       int first, int last) {
    Insets insets = target.getInsets();
    if (verticalAlignment == MIDDLE) {
      y += height / 2;
    }
    if (verticalAlignment == BOTTOM) {
      y += height;
    }

    for (int i = first ; i < last; i++) {
      Component m = target.getComponent(i);
        Dimension md = m.getSize();
      if (m.isVisible()) {
        int px;
        if (horizontalAlignment == LEFT) {
          px = x;
        }
        else if (horizontalAlignment == RIGHT) {
          px = width - md.width;
        }
        else {
          px = x + (width-md.width)/2;
        }
        m.setLocation(px, y);
        y += vgap + md.height;
      }
    }
  }

  /**
   * <p>Lays out the container.</p>
   *
   * @param target The container to lay out.
   */
  public void layoutContainer(Container target) {
    Insets insets = target.getInsets();
    int maxheight = target.getSize().height - (insets.top + insets.bottom + vgap*2);
    int maxwidth = target.getSize().width - (insets.left + insets.right + hgap*2);
    int numcomp = target.getComponentCount();
    int x = insets.left + hgap;
    int y = 0;
    int colw = 0, start = 0;

    for (int i = 0 ; i < numcomp ; i++) {
      Component m = target.getComponent(i);
      if (m.isVisible()) {
        Dimension d = m.getPreferredSize();
        // fit last component to remaining height
        if ((fillHeight) && (i == (numcomp-1))) {
          d.height = Math.max((maxheight - y), m.getPreferredSize().height);
        }

        // fit component size to container width
        if (fillWidth) {
          m.setSize(maxwidth, d.height);
          d.width = maxwidth;
        }
        else {
          m.setSize(d.width, d.height);
        }

        if (y + d.height > maxheight) {
          return;
        }
        else {
          if (y > 0) {
            y += vgap;
          }
          y += d.height;
          colw = Math.max(colw, d.width);
        }
      }
    }
    layoutComponents(target, x, insets.top + vgap, colw, maxheight - y, start, numcomp);
  }
}
