//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/Alignment.java,v 7.0 2002/08/08 18:40:09 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;



/**
 * The <CODE>Alignment</CODE> component provides general-purpose two-dimensional
 *  alignment constants for aligning an object within a rectangular container.
 *  The constants are divided into the following groups. You can select none or one
 *  (the maximum) per group:
*
*<P>Horizontal alignment:
*<UL>
*<LI>LEFT
*<LI>CENTER
*<LI>RIGHT
*<LI>HSTRETCH
*<LI>HORIZONTAL
*</UL>
*<P>Vertical alignment:
*<UL>
*<LI>TOP
*<LI>MIDDLE
*<LI>BOTTOM
*<LI>VSTRETCH
*<LI>VERTICAL
*</UL>
*

 */
public interface Alignment
{
  // Uninitialized state.
  //
  /**
   * No alignment constant is defined.
   */
  public static final int UNDEFINED  = 0x00;

  // Horizontal alignment
  /**
   * A horizontal alignment constant. Aligns horizontally along the
   * left edge of the container
   */
  public static final int LEFT       = 0x01;
  /**
   * A horizontal alignment constant. Centers horizontally within the container object.
   */
  public static final int CENTER     = 0x02;
  /**
   * A horizontal alignment constant.
   *  Aligns horizontally along the right edge of the container
   */ public static final int RIGHT      = 0x03;
  /**
   *A horizontal alignment constant. Aligns horizontally between the
   *  left and right edges of the container, stretching as necessary.
   */public static final int HSTRETCH   = 0x04;
  /**
    * Used to programmatically filter out the set of vertical alignment bits,
    * leaving only the horizontal alignment
   */ public static final int HORIZONTAL = 0x0F;

  // Vertical alignment
  /**
    *A vertical alignment constant. Aligns along the top of the container.
   */public static final int TOP      = 0x10;
   /**
    * A vertical alignment constant. Aligns in the vertical middle of the container.
   */public static final int MIDDLE   = 0x20;
   /**
     * A vertical alignment constant. Aligns vertically along the bottom of the container
   */ public static final int BOTTOM   = 0x30;
   /**
     * A vertical alignment constant. Aligns vertically between the
     * top and bottom of the container, stretching if necessary.
   */ public static final int VSTRETCH = 0x40;
   /**
     * Used to programmatically filter out the set of horizontal
     * alignment bits, leaving only the vertical alignment.
   */ public static final int VERTICAL = 0xF0;

  /**
   * All Alignment constants
   */
  public static final int ALL = LEFT|CENTER|RIGHT|HSTRETCH|TOP|MIDDLE|BOTTOM|VSTRETCH|VERTICAL|HORIZONTAL;

}
