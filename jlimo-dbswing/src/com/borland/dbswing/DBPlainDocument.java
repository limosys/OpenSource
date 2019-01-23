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

import javax.swing.text.*;

 /**
  * <p><code>DBPlainDocument</code> is the default document (model) for text components that are attached to a <code>DataSet</code> column of type <code>STRING</code>. </p>
  *
  * <p><code>DBPlainDocument</code> has a <code>maxLength</code> property that enforces a limitation on the number of characters that can be entered into a field. The default value of <code>maxLength</code> is based upon the <code>Column's</code> precision property value. If precision is -1, input is not limited by <code>maxLength.</code> To set <code>maxLength,</code> use code that looks like this: </p>
  *
  * <pre>
((DBPlainDocument)jdbTextField1.getDocument()).setMaxLength(25);
  * </pre>
  *
  * <p>You might set <code>maxLength</code> for a column that is not provided by the query, or for a <code>TableDataSet.</code> Don't set precision for a <code>Column</code> from a <code>QueryDataSet</code> larger than the value from the server because you
won't be able to save those values back to the server. Also remember that if you set precision on a
<code>Column</code> from a server, the server's value takes precedence over yours unless you turn off
<code>metaDataUpdate.</code> It is for this reason that we suggest setting <code>maxLength</code> instead. </p>
  *
  * <p>To enforce additional input constraints at the model level (for example, to convert input characters to
uppercase), extend <code>DBPlainDocument</code> as necessary and set it as the text component's model. </p>
  */
public class DBPlainDocument extends PlainDocument {


  /**
   * <p>Creates a <code>DBPlainDocument</code> by calling the constructor of its superclass. </p>
   */
  public DBPlainDocument() {
    super();
  }

  /**
   * <p>Sets the maximum number of characters that can be entered into a field. </p>
   *
   * @param maxLength The maximum number of characters that can be entered into a field.
   * @see #getMaxLength
   */
  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  /**
   * <p>Returns the maximum number of characters that can be entered into a field. </p>
   *
   * @return The maximum number of characters that can be entered into a field.
   * @see #setMaxLength
   */
  public int getMaxLength() {
    return maxLength;
  }

  public void insertString(int offset, String string, AttributeSet attr) throws BadLocationException {
    int maxInsertLength = maxLength - getLength();
    if (maxInsertLength > string.length()) {
      super.insertString(offset, string, attr);
    }
    else if (maxInsertLength > 0) {
      super.insertString(offset, string.substring(0, maxInsertLength), attr);
    }
  }
  private int maxLength = Integer.MAX_VALUE;
}
