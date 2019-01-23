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
package com.borland.dbswing.plaf.motif;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.sun.java.swing.plaf.motif.*;

import com.borland.dbswing.*;
import com.borland.dbswing.plaf.*;

/**
 * Motif-style UI delegate for TableScrollBar component with special
 * enhancements:
 * - optionally doesn't set the value of the scrollbar while the scrollbar's
 *   thumb is being dragged
 * - aligns viewport on scrollbar unit increment boundary
 * - gradually increases the scroll increment size (Fibonacci sequence)
 *   when the mouse is held down on the thumb track or scroll arrow
 */
public class MotifJdbTableScrollBarUI extends MotifScrollBarUI {

  public static ComponentUI createUI(JComponent c)    {
    return new MotifJdbTableScrollBarUI();
  }

  public void installUI(JComponent c) {
    super.installUI(c);
    tableScrollBar = (TableScrollPane.TableScrollBar) c;
  }

  /**
   * Returns a custom TrackListener.
   */
  protected TrackListener createTrackListener() {
    return new TableTrackListener();
  }

  /**
   * Returns a custom ScrollListener.
   */
  protected ScrollListener createScrollListener() {
    return new TableScrollListener();
  }

  /**
   * Returns a custom ArrowButtonListener.
   */
  protected ArrowButtonListener createArrowButtonListener(){
    return new TableArrowButtonListener();
  }

  // necessary to allow extended inner class to access protected variables
  protected boolean isDragging() {
    return isDragging;
  }

  // necessary to allow extended inner class to access protected variables
  protected JScrollBar getScrollBar() {
    return scrollbar;
  }

  // necessary to allow extended inner class to access protected method
  protected void setThumbBounds(int x, int y, int width, int height) {
    super.setThumbBounds(x, y, width, height);
  }

  // necessary to allow extended inner class to access protected method
  protected Rectangle getThumbBounds() {
    return thumbRect;
  }

  // necessary to allow extended inner class to access protected variables
  protected JButton getDecrButton() {
    return decrButton;
  }

  // necessary to allow extended inner class to access protected variables
  protected JButton getIncrButton() {
    return incrButton;
  }

  // necessary to allow extended inner class to access protected method
  protected Rectangle getTrackBounds() {
    return trackRect;
  }

  protected void scrollByUnit(int direction) {
    synchronized (scrollbar) {
      int delta;
      if (direction > 0) {
        delta = scrollbar.getUnitIncrement(direction);
      }
      else {
        delta = -scrollbar.getUnitIncrement(direction);
      }
      int newValue = delta + scrollbar.getValue();
      newValue -= newValue % delta;
      scrollbar.setValue(newValue); 
    }
  }

  protected void scrollByBlock(int direction) {
//    super.scrollByBlock(direction);
    synchronized (scrollbar) {
      int oldValue = scrollbar.getValue();
      int blockIncrement = scrollbar.getBlockIncrement(direction);
      int delta = blockIncrement * ((direction > 0) ? +1 : -1);

      int newValue = oldValue + delta;
      newValue -= newValue % scrollbar.getUnitIncrement(direction);
      scrollbar.setValue(newValue);
      trackHighlight = direction > 0 ? INCREASE_HIGHLIGHT : DECREASE_HIGHLIGHT;
      Rectangle dirtyRect = getTrackBounds();
      scrollbar.repaint(dirtyRect.x, dirtyRect.y, dirtyRect.width, dirtyRect.height);
    }
  }

  protected TrackListener getTrackListener() {
    return trackListener;
  }

  protected class TableTrackListener extends TrackListener {
    int lastDraggedY;

    public int getCurrentMouseY() {
      return currentMouseY;
    }

    public int getCurrentMouseX() {
      return currentMouseX;
    }

    public void mouseDragged(MouseEvent e) {
      if(!getScrollBar().isEnabled() || !isDragging()) {
        return;
      }

      BoundedRangeModel model = getScrollBar().getModel();
      Rectangle thumbR = getThumbBounds();
      float trackLength;
      int thumbMin, thumbMax, thumbPos;

      if (getScrollBar().getOrientation() == JScrollBar.VERTICAL) {
        thumbMin = getDecrButton().getY() + getDecrButton().getHeight();
        thumbMax = getIncrButton().getY() - getThumbBounds().height;
        thumbPos = Math.min(thumbMax, Math.max(thumbMin, (e.getY() - offset)));
        setThumbBounds(thumbR.x, thumbPos, thumbR.width, thumbR.height);
        trackLength = getTrackBounds().height;
      }
      else {
        thumbMin = getDecrButton().getX() + getDecrButton().getWidth();
        thumbMax = getIncrButton().getX() - getThumbBounds().width;
        thumbPos = Math.min(thumbMax, Math.max(thumbMin, (e.getX() - offset)));
        setThumbBounds(thumbPos, thumbR.y, thumbR.width, thumbR.height);
        trackLength = getTrackBounds().width;
      }

      if (thumbPos == thumbMax) {
        if (tableScrollBar.isPaintDuringDrag()) {
          getScrollBar().setValue(model.getMaximum() - model.getExtent());
        }
        else {
          lastDraggedY = model.getMaximum() - model.getExtent();
        }
      }
      else {
        float valueMax = model.getMaximum() - model.getExtent();
        float valueRange = valueMax - model.getMinimum();
        float thumbValue = thumbPos - thumbMin;
        float thumbRange = thumbMax - thumbMin;
        lastDraggedY = (int)(0.5 + ((thumbValue / thumbRange) * valueRange));

        if (tableScrollBar.isPaintDuringDrag()) {
          getScrollBar().setValue(lastDraggedY + model.getMinimum());
        }
        else {
          // ensure the top row is not partially hidden
          lastDraggedY -= lastDraggedY % getScrollBar().getUnitIncrement(1);
        }
      }
    }

    public void mouseReleased(MouseEvent e) {
      if(!getScrollBar().isEnabled()) {
        return;
      }

      if (isDragging() && !tableScrollBar.isPaintDuringDrag()) {
        getScrollBar().setValue(lastDraggedY);
      }

      speedUpCounter1 = 1;
      speedUpCounter2 = 1;

      super.mouseReleased(e);
    }
  }

  protected class TableScrollListener extends ScrollListener {
    protected int direction = +1;
    protected boolean useBlockIncrement;

    public TableScrollListener()        {
      this(1, false);
    }

    public TableScrollListener(int dir, boolean block)  {
      direction = dir;
      useBlockIncrement = block;
    }
        
    public void setDirection(int direction) { this.direction = direction; }
    public int getDirection() { return direction; }

    public void setScrollByBlock(boolean block) { this.useBlockIncrement = block; }
    public boolean isScrollByBlock() { return useBlockIncrement; }
                                        
    public void actionPerformed(ActionEvent e) {
      if (tableScrollBar.isAutoScrollSpeedUp()) {
        int sum = speedUpCounter1 + speedUpCounter2;
        speedUpCounter1 = speedUpCounter2;
        speedUpCounter2 = sum;
      }

      for (int i = 0; i < speedUpCounter1; i++) {
        if (isScrollByBlock())  {
          scrollByBlock(direction);             
          // Stop scrolling if the thumb catches up with the mouse
          if(getScrollBar().getOrientation() == JScrollBar.VERTICAL)    {
            if(direction > 0)   {
              if(getThumbBounds().y + getThumbBounds().height 
                 >= ((TableTrackListener) getTrackListener()).getCurrentMouseY()) {
                ((Timer)e.getSource()).stop();
                break;
              }
            } else if(getThumbBounds().y <= ((TableTrackListener) getTrackListener()).getCurrentMouseY()) {
              ((Timer)e.getSource()).stop();
              break;
            }
          } else {
            if(direction > 0)   {
              if(getThumbBounds().x + getThumbBounds().width 
                 >= ((TableTrackListener) getTrackListener()).getCurrentMouseX()) {
                ((Timer)e.getSource()).stop();
                break;
              }
            } else if(getThumbBounds().x <= ((TableTrackListener) getTrackListener()).getCurrentMouseX()) {
              ((Timer)e.getSource()).stop();
              break;
            }
          }
        } else {
          scrollByUnit(direction);
        }

        if(direction > 0 
           && getScrollBar().getValue()+getScrollBar().getVisibleAmount() 
           >= getScrollBar().getMaximum()) {
          ((Timer)e.getSource()).stop();
          break;
        }
        else if(direction < 0 
                && getScrollBar().getValue() <= getScrollBar().getMinimum()) {
          ((Timer)e.getSource()).stop();
          break;
        }
      }
    }
  }

  protected class TableArrowButtonListener extends ArrowButtonListener {
    public void mouseReleased(MouseEvent e) {
      speedUpCounter1 = 1;
      speedUpCounter2 = 1;
      super.mouseReleased(e);
    }
  }

  private int speedUpCounter1 = 1;
  private int speedUpCounter2 = 1;
  private TableScrollPane.TableScrollBar tableScrollBar;
  private TableScrollPane tableScrollPane;
}

