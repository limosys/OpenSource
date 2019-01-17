//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataChangeEvent.java,v 7.1 2003/06/13 00:37:26 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DispatchableEvent;

import java.util.*;

 /**
  * The DataChangeEvent is the internal event generated when the data in a
  * DataSet is changed. It is passed to DataSet components and listeners
  * of the DataSet. The event ID (see the ID property) indicates the type
  * of data update. Other members provide additional information on the change
  * of the data.
  * <p>
  * The dbSwing components contain some examples for using the DataChangeEvent class.
  * <p>
  * The {@link com.borland.dx.dataset.DataChangeEvent} class may be useful for component writers, however,
  * is not recommended for general usage. The DataChangeListener
  * responds to the DataChangeEvent class.
  */
public class DataChangeEvent extends com.borland.jb.util.DispatchableEvent
  implements com.borland.jb.util.ExceptionDispatch
{
  /** Row added.  getRowAffected() will return the new position.
  */
  public static final int ROW_ADDED         = 1;
  /** Row Deleted.  getRowAffected() will return the new position.
  */
  public static final int ROW_DELETED       = 2;
  /** Only a cell changed, row did not post.  getRowAffected() will return the new position.
  */
  public static final int ROW_CHANGED       = 3;
  /** Row changed and posted.  getRowAffected() will return the new position.
  */
  public static final int ROW_CHANGE_POSTED = 4;
  /** More than one row of data has changed.
  */
  public static final int DATA_CHANGED      = 5;
  /** Notification to listeners that a row is posting.
      This allows a listener to post unposted field values just
      before the row is going to be posted.
  */
  public static final int POST_ROW          = 6;

  /**
   * Data in more than one row is affected.  Useful for repaint strategies.
   */
  public final boolean multiRowChange() { return affectedRow == -1; }

/**
 *  Constructs a DataChangeEvent object.
 *
 * @param source
 * @param id
 */
  public DataChangeEvent(Object source, int id) {
    this(source, id, -1);
  }

  /**
   * Constructs a DataChangeEvent object.
   *
   * @param source
   * @param id
   * @param affectedRow
   */
  public DataChangeEvent(Object source, int id, long affectedRow) {
    super(source);
    this.id         = id;
    this.affectedRow  = affectedRow;
  }

  /**
   * This method is used internally by other com.borland classes.
   * You should never use this method directly.
   *
   * @param listener
   */
  public void dispatch(EventListener listener) {
    ((DataChangeListener)listener).dataChanged(this);
  }

  public void exceptionDispatch(EventListener listener)
    throws Exception
  {
    ((DataChangeListener)listener).postRow(this);
  }

  /**
   * If multiRowChange is false, this returns the row affected.  Otherwise
   *  -1 is returned.
   */
  public final int getRowAffected() { return (int)affectedRow; }
  public final long getLongRowAffected() { return affectedRow; }

  /**
   * @return   The type of data change. Return values for this property
   *           are constants defined in this class.
   */
  public final int    getID() { return id;}

  /**
   * @return    The concatenation of super.toString and the value of the ID property.
   */
  public String toString() {
    return super.toString()+" "+id;
  }
  private int     id;
  private long    affectedRow;
}
