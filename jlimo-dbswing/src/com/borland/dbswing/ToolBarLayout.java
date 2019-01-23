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
 * <p><code>ToolBarLayout</code> is a hybrid layout manager that
 * combines the behaviors of the AWT <code>FlowLayout</code> and
 * <code>GridLayout</code> layout managers. When the managed
 * container is made smaller (either vertically or horizontally)
 * than the total preferred size of all its subcomponents, the
 * manager switches from <code>FlowLayout</code> to
 * <code>GridLayout</code> behavior, reducing the size of each
 * subcomponent proportionally.</p>
 *
 * <p>Unlike <code>GridLayout</code>, <code>ToolBarLayout</code>
 * takes into account the visibility state of each component,
 * removing non-visible components from the layout.</p>
 *
 * <p><code>ToolBarLayout</code> is used by <A HREF="JdbNavToolBar.html"><code>JdbNavToolBar</code></A> as its
 * default layout manager.</p>
 *
 */
public class ToolBarLayout extends FlowLayout {
  int orientation = SwingConstants.HORIZONTAL;

  /**
   * <p>Creates a <code>ToolBarLayout</code> with horizontal orientation.</p>
   */
  public ToolBarLayout() {
    super();
  }


 /**
  * <p>
Constructs a <code>ToolBarLayout</code> component with the specified orientation.</p>
  *
  * @param orientation The orientation. Valid values are HORIZONTAL or VERTICAL.
  */
  public ToolBarLayout(int orientation) {
    super();
    this.orientation = orientation;
  }

 /**
  * <p>Sets the orientation. Possible values are HORIZONTAL and VERTICAL.</p>
  * @param orientation HORIZONTAL or VERTICAL.
  */

  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

  public Dimension preferredLayoutSize(Container target) {
    if (orientation == SwingConstants.HORIZONTAL) {
      Dimension d = super.preferredLayoutSize(target);
      return d;
    }
    else {
      synchronized (target.getTreeLock()) {
        Dimension dim = new Dimension(0, 0);
        int nmembers = target.getComponentCount();

        for (int i = 0 ; i < nmembers; i++) {
          Component m = target.getComponent(i);
          if (m.isVisible()) {
            Dimension d = m.getPreferredSize();
            dim.width = Math.max(dim.width, d.width);
            if (i > 0) {
              dim.height += getVgap();
            }
            dim.height += d.height;
          }
        }
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right + getHgap()*2;
        dim.height += insets.top + insets.bottom + getVgap()*2;
        return dim;
      }
    }
  }

  public void layoutContainer(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int hgap = getHgap();
      int vgap = getVgap();

      int maxwidth = target.getWidth() - (insets.left + insets.right + hgap*2);
      int maxheight = target.getHeight() - (insets.top + insets.bottom + vgap*2);

      int nmembers = target.getComponentCount();

      boolean ltr = target.getComponentOrientation().isLeftToRight();

      if (orientation == SwingConstants.HORIZONTAL) {
        int x = 0, y = insets.top + vgap;
        int rowh = 0, start = 0;

        for (int i = 0 ; i < nmembers ; i++) {
          Component m = target.getComponent(i);
          if (m.isVisible()) {
            Dimension d = m.getPreferredSize();
            m.setSize(d.width, d.height);

            if ((x == 0) || ((x + d.width) <= maxwidth)) {
              if (x > 0) {
                x += hgap;
              }
              x += d.width;
              rowh = Math.max(rowh, d.height);
            } else {
              doGridLayout(target);
              return;
            }
          }
        }
        moveComponentsX(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers, ltr);
      }
      else {
        int y = 0, x = insets.left + hgap;
        int colw = 0, start = 0;

        for (int i = 0 ; i < nmembers ; i++) {
          Component m = target.getComponent(i);
          if (m.isVisible()) {
            Dimension d = m.getPreferredSize();
            m.setSize(d.width, d.height);

            if ((y == 0) || ((y + d.height) <= maxheight)) {
              if (y > 0) {
                y += vgap;
              }
              y += d.height;
              colw = Math.max(colw, d.width);
            } else {
              doGridLayout(target);
              return;
            }
          }
        }
        moveComponentsY(target, x, insets.top + vgap, colw, maxheight - y, start, nmembers, ltr);
      }
    }
  }

  private void doGridLayout(Container target) {
    int visibleComponents = 0;
    int ncomponents = target.getComponentCount();
    Insets insets = target.getInsets();
    int hgap = getHgap();
    int vgap = getVgap();

    for (int i = 0; i < ncomponents; i++) {
      if (target.getComponent(i).isVisible()) {
        visibleComponents++;
      }
    }

    if (visibleComponents == 0) {
      return;
    }

    int nrows;
    int ncols;
    if (orientation == SwingConstants.HORIZONTAL) {
      nrows = 1;
      ncols = visibleComponents;
    }
    else {
      nrows = visibleComponents;
      ncols = 1;
    }

    int w = target.getWidth() - (insets.left + insets.right);
    int h = target.getHeight() - (insets.top + insets.bottom);

    w = (w - (ncols - 1) * hgap) / ncols;
    h = (h - (nrows - 1) * vgap) / nrows;

    if (orientation == SwingConstants.HORIZONTAL) {
      int y = insets.top + vgap;
      Component comp;
      for (int c = 0, x = insets.left, i = 0 ; c < ncols ; c++) {
        while (i < ncomponents) {
          comp = target.getComponent(i++);
          if (comp.isVisible()) {
            comp.setBounds(x, y, w, comp.getPreferredSize().height);
            x += w + hgap;
            break;
          }
          else {
            comp.setBounds(-1, -1, 0, 0);
          }
        }
      }
    } else {
      int x = insets.left + hgap;
      Component comp;
      for (int c = 0, y = insets.top, i = 0 ; c < nrows ; c++) {
        while (i < ncomponents) {
          comp = target.getComponent(i++);
          if (comp.isVisible()) {
            comp.setBounds(x, y, comp.getPreferredSize().width, h);
            y += h + vgap;
            break;
          }
          else {
            comp.setBounds(-1, -1, 0, 0);
          }
        }
      }
    }
  }

  private void moveComponentsX(Container target, int x, int y, int width, int height,
                              int rowStart, int rowEnd, boolean ltr) {
    int hgap = getHgap();
    int vgap = getVgap();
    int align = getAlignment();

    synchronized (target.getTreeLock()) {
      switch (align) {
      case LEFT:
        x += ltr ? 0 : width;
        break;
      case CENTER:
        x += width / 2;
        break;
      case RIGHT:
        x += ltr ? width : 0;
        break;
      case LEADING:
        break;
      case TRAILING:
        x += width;
        break;
      }
      for (int i = rowStart ; i < rowEnd ; i++) {
        Component m = target.getComponent(i);
        if (m.isVisible()) {
          if (ltr) {
            m.setLocation(x, y + (height - m.getSize().height) / 2);
          } else {
            m.setLocation(target.getWidth() - x - m.getSize().width, y + (height - m.getSize().height) / 2);
          }
          x += m.getSize().width + hgap;
        }
      }
    }
  }

  private void moveComponentsY(Container target, int x, int y, int width, int height,
                              int rowStart, int rowEnd, boolean ltr) {
    int hgap = getHgap();
    int vgap = getVgap();
    int align = getAlignment();

    // we can ignore bidi issues here because we're only aligning vertically
    synchronized (target.getTreeLock()) {
      switch (align) {
      case LEFT:  // TOP
        y = 0;
        break;
      case CENTER:
        y += height / 2;
        break;
      case RIGHT:  // BOTTOM
        y = height;
        break;
      }
      for (int i = rowStart ; i < rowEnd ; i++) {
        Component m = target.getComponent(i);
        if (m.isVisible()) {
          m.setLocation(x + (width - m.getSize().width) / 2, y);
          y += m.getSize().height + vgap;
        }
      }
    }
  }
}
