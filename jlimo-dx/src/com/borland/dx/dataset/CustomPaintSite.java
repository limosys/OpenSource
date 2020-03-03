//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/CustomPaintSite.java,v 7.0 2002/08/08 18:39:19 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.border.Border;

 /**
  * The ItemPaintSite interface is the one that is passed to the Column.CustomPaint
  * event handler. Classes that implement the ItemPaintSite and CustomPaintSite
  * interfaces can provide item painters with information about the host container
  * in which the painting occurs.
  * <p>
  * The CustomPaintSite interface has the methods for retrieving and setting
  * display-related properties, such as the background color, the foreground color,
  * the font, the alignment setting, the margins for the item being painted, and
  * the item's transparent state.
  */
public interface CustomPaintSite
{
  /**
   * The reset method reassigns all set values back to the defaults provided
   * by the original ItemPaintSite.
   */
  public void reset();

  /**
   * The background color for the item being painted.
   * @param color A java.awt.Color object representing the background color.
   */
  public void setBackground(Color color);

  /**
   * The foreground color for the item being painted.
   * @param color A java.awt.Color object representing the foreground color.
   */
  public void setForeground(Color color);

  /**
   * The border for the item being painted.
   * @param border A javax.swing.border.Border object to be used instead of default border.
   */
  public void setBorder(Border border);
  
  /**
   * The font to use for the item being painted.
   * @param font A java.awt.Font object representing the font to use.
   */
  public void setFont(Font font);

  /**
   * The alignment setting for the item being painted.
   * @see com.borland.dx.text.Alignment for alignment settings.
   * @param alignment An int representing the alignment bitmask.
   */
  public void setAlignment(int alignment);

  /**
   * The item margins for the item being painted.
   * @param margins An Insets object representing the margins for this item.
   */
  public void setItemMargins(Insets margins);



  /**
   * The background color for the item being painted.
   * @return A java.awt.Color object representing the background color.
   */
  public Color getBackground();

  /**
   * The foreground color for the item being painted.
   * @return A java.awt.Color object representing the foreground color.
   */
  public Color getForeground();

  /**
   * The border for the item being painted.
   * @return A javax.swing.border.Border object to be used instead of default border.
   */
  public Border getBorder();
  
  /**
   * Whether or not the ItemPainter should erase its background.
   * @return true if transparent, false if not.
   */
  public boolean isTransparent();

  /**
   * The font to use for the item being painted.
   * @return A java.awt.Font object representing the font to use.
   */
  public Font getFont();

  /**
   * The alignment setting for the item being painted.
   * @see com.borland.dx.text.Alignment for alignment settings.
   * @return An int representing the alignment bitmask.
   */
  public int getAlignment();

  /**
   * The item margins for the item being painted.
   * @return An Insets object representing the margins for this item.
   */
  public Insets getItemMargins();

  /**
   * Returns the component representing the ItemPaintSite.  This is used
   * for coordinate space calculations, as well as to provide a component for
   * ItemPainter implementations that require one - like ImageItemPainter, which
   * requires an ImageObserver object.
   * @return The hosting site component.
   */
  public Component getSiteComponent();
}
