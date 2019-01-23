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
import javax.swing.border.Border;

import com.borland.dbswing.plaf.basic.BasicJdbTableScrollBarUI;
import com.borland.dbswing.plaf.metal.MetalJdbTableScrollBarUI;
import com.borland.dbswing.plaf.motif.MotifJdbTableScrollBarUI;

/**
 * <p>An extension of <code>JScrollPane</code> designed specifically for use with a
 * <code>JTable</code>. It differs visually from a <code>JScrollPane</code> in that
 * its vertical and horizontal scrollbars, if present, extend all the way to the
 * scrollpane's top and left edges, respectively, leaving no space for an upper-right
 * and lower-left corner component.</p>
 *
 * <p>It also adds the following properties to enhance <code>JScrollPane</code>
 * behavior:<strong></strong>
 *
 * <ul>
 * <li><code>paintDuringVerticalDrag</code> - Enables or disables painting of the
 * scrollpane's viewport while the vertical scrollbar's thumb is being dragged. The
 * default value is <code>true</code>. Set <code>paintDuringVerticalDrag</code> to
 * <code>false</code> to disable painting. </li>
 *
 * <li><code>autoVerticalScrollSpeedUp</code> - Dynamically increases the vertical
 * scroll increment the longer a vertical scrollbar arrow button is pressed. Its
 * default value is <code>true</code>. Set <code>autoVerticalScrollSpeedUp</code> to
 * <code>false</code> to always scroll by a constant increment.</li>
 * </ul>
 */
public class TableScrollPane extends JScrollPane {

/**
 * <p>Creates a <code>TableScrollPane</code> with no parameters. It contains a
 * vertical or horizontal scrollbar if needed. Calls the constructor of
 * <code>this</code> class that takes a <code>Component</code> and two
 * <code>int</code> values (representing the vertical and horizontal scrollbar
 * policies) as parameters. Passes <code>null</code>, <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>, and
* <code>HORIZONTAL_SCROLLBAR_AS_NEEDED</code> to the other constructor.</p>
 */
  public TableScrollPane() {
    this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

/**
 * <p>Creates a <code>TableScrollPane</code>. Calls the constructor of
 * <code>this</code> class that takes a <code>Component</code> and two
 * <code>int</code> values (representing the vertical and horizontal scrollbar
 * policies) as parameters. Passes <code>null</code>, <code>vsbPolicy</code>, and
 * <code>hsbPolicy</code> to the other constructor.</p>
 *
 * <p>The <code>vsbPolicy</code> and <code>hsbPolicy</code> parameters specify when the scrollbars are displayed.</p>
 * @param vsbPolicy The vertical scrollbar policy. One of: <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>, <code>VERTICAL_SCROLLBAR_NEVER</code>, or
<code>VERTICAL_SCROLLBAR_ALWAYS</code>.
 * @param hsbPolicy The horizontal scrollbar policy. One of: <code>HORIZONTAL_SCROLLBAR_AS_NEEDED</code>, <code>HORIZONTAL_SCROLLBAR_NEVER</code>, or <code>HORIZONTAL_SCROLLBAR_ALWAYS</code>.
 */
  public TableScrollPane(int vsbPolicy, int hsbPolicy) {
    this(null, vsbPolicy, hsbPolicy);
  }

/**
 * <p>Creates a <code>TableScrollPane</code>, with the specified component. It
 * contains a vertical or horizontal scrollbar if needed. Calls the constructor of
 * <code>this</code> class that takes a <code>Component</code> and two
 * <code>int</code> values (representing the vertical and horizontal scrollbar
 * policies) as parameters. Passes the specified <code>Component</code>, along with
 * <code>VERTICAL_SCROLLBAR_AS_NEEDED</code> and <code>HORIZONTAL_SCROLLBAR_AS_NEEDED</code> to the other constructor.</p>
 *
 * @param view The component to display in the scrollpane's viewport.
 */
  public TableScrollPane(Component view) {
    this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

/**
 * <p>Creates a <code>TableScrollPane</code>, with the specified component. </p>
 *
 * <p>The <code>vsbPolicy</code> and <code>hsbPolicy</code> parameters specify when
 * the scrollbars are displayed.</p>
 *
 * @param view The component to display in the scrollpane's viewport.
 * @param vsbPolicy The vertical scrollbar policy. One of: <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>, <code>VERTICAL_SCROLLBAR_NEVER</code>, or
<code>VERTICAL_SCROLLBAR_ALWAYS</code>.
 * @param hsbPolicy The horizontal scrollbar policy. One of: <code>HORIZONTAL_SCROLLBAR_AS_NEEDED</code>, <code>HORIZONTAL_SCROLLBAR_NEVER</code>, or <code>HORIZONTAL_SCROLLBAR_ALWAYS</code>.
 */
  public TableScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    setLayout(new TableScrollPaneLayout());
    setVerticalScrollBarPolicy(vsbPolicy);
    setHorizontalScrollBarPolicy(hsbPolicy);
    setViewport(createViewport());
    setVerticalScrollBar(createVerticalScrollBar());
    setHorizontalScrollBar(createHorizontalScrollBar());
    if (view != null) {
      setViewportView(view);
    }
    JLabel upperLeftCorner = new JLabel();
    upperLeftCorner.setBorder(UIManager.getBorder("TableHeader.cellBorder"));  
    setCorner(JScrollPane.UPPER_LEFT_CORNER, upperLeftCorner);
    updateUI();
  }

  /**
   * <p>Sets whether painting of the scrollpane's viewport will be enabled
   * while the vertical scrollbar's thumb is being dragged.</p>
   *
   * <p>This property is <code>true</code> by default. Set to <code>false</code> to
   * disable painting during scrolling.</p>
   *
   * @param paintVerticalDrag If <strong>true</strong>, the scrollpane's vierport will be enabled while the vertical scrollbar's thumb is being dragged.
   * @see #isPaintDuringVerticalDrag
   */
  public void setPaintDuringVerticalDrag(boolean paintVerticalDrag) {
    this.paintVerticalDrag = paintVerticalDrag;
    if (getVerticalScrollBar() != null &&
        getVerticalScrollBar() instanceof TableScrollBar) {
      ((TableScrollBar) getVerticalScrollBar()).setPaintDuringDrag(paintVerticalDrag);
    }
  }

 /**
   * <p>Returns whether painting of the scrollpane's viewport will be enabled
   * while the vertical scrollbar's thumb is being dragged.</p>
   *
   * @return If <strong>true</strong>, the scrollpane's vierport will be enabled while the vertical scrollbar's thumb is being dragged.
   * @see #setPaintDuringVerticalDrag
   */
  public boolean isPaintDuringVerticalDrag() {
    return paintVerticalDrag;
  }

  /**
    * <p>Sets whether the vertical scroll increment size will dynamically increase the
	* longer a vertical scrollbar arrow button is held down.</p>
    *
    * <p>This property is <code>true</code> by default. Set to <code>false</code> to
	* use a fixed increment size.</p>
	*
	* @param verticalScrollSpeedUp If <code>true</code>, the vertical scroll increment size will dynamically increase the longer a vertical scrollbar arrow button is held down.
    * @see #isAutoVerticalScrollSpeedUp
    */
  public void setAutoVerticalScrollSpeedUp(boolean verticalScrollSpeedUp) {
    this.verticalScrollSpeedUp = verticalScrollSpeedUp;
    if (getVerticalScrollBar() != null &&
        getVerticalScrollBar() instanceof TableScrollBar) {
      ((TableScrollBar) getVerticalScrollBar()).setAutoScrollSpeedUp(verticalScrollSpeedUp);
    }
  }

 /**
    * <p>Returns whether the vertical scroll increment size will dynamically increase the
	* longer a vertical scrollbar arrow button is held down.</p>
    *
	* @return If <code>true</code>, the vertical scroll increment size will dynamically increase the longer a vertical scrollbar arrow button is held down.
    * @see #setAutoVerticalScrollSpeedUp
    */
  public boolean isAutoVerticalScrollSpeedUp() {
    return verticalScrollSpeedUp;
  }

  /**
   * <p>Creates a vertical scroll bar for the table scroll pane.</p>
   */
  public JScrollBar createVerticalScrollBar() {
    return new TableScrollBar(JScrollBar.VERTICAL);
  }

  public class TableScrollBar extends ScrollBar {
    public TableScrollBar(int orientation) {
      super(orientation);
    }

    public void updateUI() {
      String currentLookAndFeel = UIManager.getLookAndFeel().getID();
      if (currentLookAndFeel.equals("Motif")) {  
        setUI(MotifJdbTableScrollBarUI.createUI(this));
      }
      else if (currentLookAndFeel.equals("Metal")) {  
        setUI(MetalJdbTableScrollBarUI.createUI(this));
      }
      else if (currentLookAndFeel.startsWith("Mac")) {  
        // Leave the standard scroll bar alone until PLAF extensions for the
        // Mac have been written
        super.updateUI();
      }
      else if (currentLookAndFeel.startsWith("Borland")) { 
        super.updateUI();
      }
      else {
        setUI(BasicJdbTableScrollBarUI.createUI(this));
      }
    }

    public void setAutoScrollSpeedUp(boolean autoSpeedUp) {
      this.autoSpeedUp = autoSpeedUp;
    }

    public boolean isAutoScrollSpeedUp() {
      return autoSpeedUp;
    }

    public void setPaintDuringDrag(boolean paintDuringDrag) {
      this.paintDuringDrag = paintDuringDrag;
    }

    public boolean isPaintDuringDrag() {
      return paintDuringDrag;
    }

    /** whether to trigger paint events while dragging scrollbar */
    private boolean paintDuringDrag = true;

    /** whether to dynamically increase scroll increment size */
    private boolean autoSpeedUp = true;

  }

  private boolean paintVerticalDrag = true;
  private boolean verticalScrollSpeedUp = true;
}

class TableScrollPaneLayout extends ScrollPaneLayout
{
  /**
   * Adds the specified component to the layout. The layout position is
   * identified using one of:<ul>
   * <li>JScrollPane.VIEWPORT
   * <li>JScrollPane.VERTICAL_SCROLLBAR
   * <li>JScrollPane.HORIZONTAL_SCROLLBAR
   * <li>JScrollPane.ROW_HEADER
   * <li>JScrollPane.COLUMN_HEADER
   * <li>JScrollPane.LOWER_LEFT_CORNER (ignored)
   * <li>JScrollPane.LOWER_RIGHT_CORNER
   * <li>JScrollPane.UPPER_LEFT_CORNER
   * <li>JScrollPane.UPPER_RIGHT_CORNER (ignored)
   * </ul>
   *
   * @param s the component identifier
   * @param comp the the component to be added
   */
  public void addLayoutComponent(String s, Component c)
  {
    if (s.equals(VIEWPORT)) {
      viewport = (JViewport)addSingletonComponent(viewport, c);
    }
    else if (s.equals(VERTICAL_SCROLLBAR)) {
      vsb = (JScrollBar)addSingletonComponent(vsb, c);
    }
    else if (s.equals(HORIZONTAL_SCROLLBAR)) {
      hsb = (JScrollBar)addSingletonComponent(hsb, c);
    }
    else if (s.equals(ROW_HEADER)) {
      rowHead = (JViewport)addSingletonComponent(rowHead, c);
    }
    else if (s.equals(COLUMN_HEADER)) {
      colHead = (JViewport)addSingletonComponent(colHead, c);
    }
    //      else if (s.equals(LOWER_LEFT_CORNER)) {
    //          lowerLeft = addSingletonComponent(lowerLeft, c);
    //      }
    else if (s.equals(LOWER_RIGHT_CORNER)) {
      lowerRight = addSingletonComponent(lowerRight, c);
    }
    else if (s.equals(UPPER_LEFT_CORNER)) {
      upperLeft = addSingletonComponent(upperLeft, c);
    }
    //      else if (s.equals(UPPER_RIGHT_CORNER)) {
    //          upperRight = addSingletonComponent(upperRight, c);
    //      }
    else {
      throw new IllegalArgumentException("invalid layout key " + s);  
    }
  }

  /**
     * <p>Lays out the scrollpane. The positioning of components depends on
     * the following constraints:</p>
     * <ul>
     * <li> The row header, if present and visible, gets its preferred
     * height and the viewports width.</li>
     *
     * <li> The column header, if present and visible, gets its preferred
     * width and the viewports height.</li>
     *
     * <li> If a vertical scrollbar is needed, i.e. if the viewport's extent
     * height is smaller than its view height or if the <code>displayPolicy</code>
     * is <code>ALWAYS</code>, it's treated like the row header wrote its dimensions
     * and it is made visible.</li>
     *
     * <li> If a horizontal scrollbar is needed it's treated like the
     * column header (and see the vertical scrollbar item).</li>
     *
     * <li> If the scrollpane has a non-null <code>viewportBorder</code>, then space
     * is allocated for that.</li>
     *
     * <li> The viewport gets the space available after accounting for
     * the previous constraints.</li>
     *
     * <li> The corner components, if provided, are aligned with the
     * ends of the scrollbars and headers. If there's a vertical
     * scrollbar the right corners appear, if there's a horizontal
     * scrollbar the lower corners appear, a row header gets left
     * corners and a column header gets upper corners.</li>
     * </ul>
     *
     * @param parent The <code>Container</code> to lay out.
     */
  public void layoutContainer(Container parent)
  {
    /* Sync the (now obsolete) policy fields with the
     * JScrollPane.
         */
    JScrollPane scrollPane = (JScrollPane)parent;
    vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
    hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

    Rectangle availR = new Rectangle(scrollPane.getSize());

    Insets insets = parent.getInsets();
    availR.x = insets.left;
    availR.y = insets.top;
    availR.width -= insets.left + insets.right;
    availR.height -= insets.top + insets.bottom;


    /*
	 * <p>If there's a visible column header remove the space it
	 * needs from the top of availR.  The column header is treated
     * as if it were fixed height, arbitrary width.</p>
     */
    Rectangle colHeadR = new Rectangle(0, availR.y, 0, 0);

    if ((colHead != null) && (colHead.isVisible())) {
      int colHeadHeight = colHead.getPreferredSize().height;
      colHeadR.height = colHeadHeight;
      availR.y += colHeadHeight;
      availR.height -= colHeadHeight;
    }

    /* If there's a visible row header remove the space it needs
         * from the left of availR.  The row header is treated
         * as if it were fixed width, arbitrary height.
         */

    Rectangle rowHeadR = new Rectangle(availR.x, 0, 0, 0);

    if ((rowHead != null) && (rowHead.isVisible())) {
      int rowHeadWidth = rowHead.getPreferredSize().width;
      rowHeadR.width = rowHeadWidth;
      availR.x += rowHeadWidth;
      availR.width -= rowHeadWidth;
    }

    /* If there's a JScrollPane.viewportBorder, remove the
         * space it occupies for availR.
         */

    Border viewportBorder = scrollPane.getViewportBorder();
    Insets vpbInsets;
    if (viewportBorder != null) {
      vpbInsets = viewportBorder.getBorderInsets(parent);
      availR.x += vpbInsets.left;
      availR.y += vpbInsets.top;
      availR.width -= vpbInsets.left + vpbInsets.right;
      availR.height -= vpbInsets.top + vpbInsets.bottom;
    }
    else {
      vpbInsets = new Insets(0,0,0,0);
    }

    colHeadR.x = availR.x;
    rowHeadR.y = availR.y;

    /* At this point availR is the space available for the viewport
         * and scrollbars, and the rowHeadR colHeadR rectangles are correct
         * except for their width and height respectively.  Once we're
         * through computing the dimensions  of these three parts we can
         * go back and set the dimensions of rowHeadR.width, colHeadR.height,
         * and the bounds for the corners.
         *
         * We'll decide about putting up scrollbars by comparing the
         * viewport views preferred size with the viewports extent
         * size (generally just its size).  Using the preferredSize is
         * reasonable because layout proceeds top down - so we expect
         * the viewport to be layed out next.  And we assume that the
         * viewports layout manager will give the view it's preferred
         * size.  One exception to this is when the view implements
         * Scrollable and Scrollable.getViewTracksViewport{Width,Height}
         * methods return true.  If the view is tracking the viewports
         * width we don't bother with a horizontal scrollbar, similarly
         * if view.getViewTracksViewport(Height) is true we don't bother
         * with a vertical scrollbar.
         */

    Component view = (viewport != null) ? viewport.getView() : null;
    Dimension viewPrefSize =
      (view != null) ? view.getPreferredSize()
      : new Dimension(0,0);

    Dimension extentSize =
      (viewport != null) ? viewport.toViewCoordinates(availR.getSize())
      : new Dimension(0,0);

    boolean viewTracksViewportWidth = false;
    boolean viewTracksViewportHeight = false;
    if (view instanceof Scrollable) {
      Scrollable sv = ((Scrollable)view);
      viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
      viewTracksViewportHeight = sv.getScrollableTracksViewportHeight();
    }

    /* If there's a vertical scrollbar and we need one, allocate
         * space for it (we'll make it visible later). A vertical
         * scrollbar is considered to be fixed width, arbitrary height.
         */

//      Rectangle vsbR = new Rectangle(0, availR.y - vpbInsets.top, 0, 0);
    Rectangle vsbR = new Rectangle(0, colHeadR.y, 0, 0);

    boolean vsbNeeded;
    if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
      vsbNeeded = true;
    }
    else if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
      vsbNeeded = false;
    }
    else {  // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
      vsbNeeded = !viewTracksViewportHeight && (viewPrefSize.height > extentSize.height);
    }


    if ((vsb != null) && vsbNeeded) {
      int vsbWidth = vsb.getPreferredSize().width;
      availR.width -= vsbWidth;
      vsbR.x = availR.x + availR.width + vpbInsets.right;
      vsbR.width = vsbWidth;
    }

    /* If there's a horizontal scrollbar and we need one, allocate
         * space for it (we'll make it visible later). A horizontal
         * scrollbar is considered to be fixed height, arbitrary width.
         */

//      Rectangle hsbR = new Rectangle(availR.x - vpbInsets.left, 0, 0, 0);
    Rectangle hsbR = new Rectangle(rowHeadR.x, 0, 0, 0);
    boolean hsbNeeded;
    if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
      hsbNeeded = true;
    }
    else if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
      hsbNeeded = false;
    }
    else {  // hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED
      hsbNeeded = !viewTracksViewportWidth && (viewPrefSize.width > extentSize.width);
    }

    if ((hsb != null) && hsbNeeded) {
      int hsbHeight = hsb.getPreferredSize().height;
      availR.height -= hsbHeight;
      hsbR.y = availR.y + availR.height + vpbInsets.bottom;
      hsbR.height = hsbHeight;

      /* If we added the horizontal scrollbar then we've implicitly
       * reduced  the vertical space available to the viewport.
       * As a consequence we may have to add the vertical scrollbar,
       * if that hasn't been done so already.  Ofcourse we
       * don't bother with any of this if the vsbPolicy is NEVER.
       */
      if ((vsb != null) && !vsbNeeded && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
        extentSize = viewport.toViewCoordinates(availR.getSize());
        vsbNeeded = viewPrefSize.height > extentSize.height;

        if (vsbNeeded) {
          int vsbWidth = vsb.getPreferredSize().width;
          availR.width -= vsbWidth;
          vsbR.x = availR.x + availR.width + vpbInsets.right;
          vsbR.width = vsbWidth;
        }
      }
    }

    /* We now have the final size of the viewport: availR.
         * Now fixup the header and scrollbar widths/heights.
         */

    vsbR.height = availR.height + vpbInsets.top + vpbInsets.bottom + colHeadR.height;
    hsbR.width = availR.width + vpbInsets.left + vpbInsets.right + rowHeadR.width;
    rowHeadR.height = availR.height;
    colHeadR.width = availR.width;

    /* Set the bounds of all nine components.  The scrollbars
         * are made invisible if they're not needed.
         */

    if (viewport != null) {
      viewport.setBounds(availR);
    }

    if (rowHead != null) {
      rowHead.setBounds(rowHeadR);
    }

    if (colHead != null) {
      colHead.setBounds(colHeadR);
    }

    if (vsb != null) {
      if (vsbNeeded) {
        vsb.setVisible(true);
        vsb.setBounds(vsbR);
      }
      else {
        vsb.setVisible(false);
      }
    }

    if (hsb != null) {
      if (hsbNeeded) {
        hsb.setVisible(true);
        hsb.setBounds(hsbR);
      }
      else {
        hsb.setVisible(false);
      }
    }

    if (lowerLeft != null) {
      //          lowerLeft.setBounds(rowHeadR.x, hsbR.y, rowHeadR.width, hsbR.height);
      lowerLeft.setBounds(0, 0, 0, 0);
    }

    if (lowerRight != null) {
      lowerRight.setBounds(vsbR.x, hsbR.y, vsbR.width, hsbR.height);
    }

    if (upperLeft != null) {
      upperLeft.setBounds(rowHeadR.x, colHeadR.y, rowHeadR.width, colHeadR.height);
    }

    if (upperRight != null) {
      //          upperRight.setBounds(vsbR.x, colHeadR.y, vsbR.width, colHeadR.height);
      upperRight.setBounds(0, 0, 0, 0);
    }
  }

}
