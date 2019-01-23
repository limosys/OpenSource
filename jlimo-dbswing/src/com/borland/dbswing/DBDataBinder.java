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

/**
 *
 * <p>The <code>DBDataBinder</code> interface contains three constants that are used by
 * most data binder components. The constants indicate modes that
 * establish the policy for what happens when an unknown data value is
 * found in the <code>DataSet</code>. These are the constants and their
 * meanings:</p>
 *
 *<ul>
 *<li>DEFAULT - The data bound component is set to its unselected state, or no item is selected.<br><br></li>
 *<li>DISABLE_COMPONENT - The component attached to the data binder is disabled.<br><br></li>
 *<li>CLEAR_VALUE - The value in the <code>DataSet</code> is cleared.<br><br></li>
 *</UL>
 *
 */
public interface DBDataBinder {

  /** 
   * The value in the <code>DataSet</code> is cleared. Set to 0.
   */
  public static final int DEFAULT = 0;

  /** 
   * The component attached to the data binder is disabled. Set to 1. 
   */
  public static final int DISABLE_COMPONENT = 1;

  /** 
   * The component is set to its unselected state, or no item is selected. Set to 2.
   */
  public static final int CLEAR_VALUE = 2;

}
