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
package com.borland.dbswing.editors;

import javax.swing.ListSelectionModel;

public class ListSelectionModeEditor extends IntegerTagEditor
{
  public ListSelectionModeEditor() {
    super(new int[] {
            ListSelectionModel.SINGLE_SELECTION,
            ListSelectionModel.SINGLE_INTERVAL_SELECTION,
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, },
          new String[] {
            "SINGLE_SELECTION",            
            "SINGLE_INTERVAL_SELECTION",  
            "MULTIPLE_INTERVAL_SELECTION", },     
          new String[] {
            "ListSelectionModel.SINGLE_SELECTION",   
            "ListSelectionModel.SINGLE_INTERVAL_SELECTION",   
            "ListSelectionModel.MULTIPLE_INTERVAL_SELECTION", });   
  }
}
