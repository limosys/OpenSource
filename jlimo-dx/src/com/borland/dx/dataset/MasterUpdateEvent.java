//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MasterUpdateEvent.java,v 7.0 2002/08/08 18:39:29 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.*;

public class MasterUpdateEvent extends EventObject
  implements ExceptionDispatch
{
  public MasterUpdateEvent(Object source) {
    super(source);
  }

  public void exceptionDispatch(EventListener listener)
    throws Exception
  {
    switch(id) {
      case CHANGING:
        ((MasterUpdateListener)listener).masterChanging(this);
        break;
      case DELETING:
        ((MasterUpdateListener)listener).masterDeleting(this);
        break;
      case CAN_CHANGE:
        ((MasterUpdateListener)listener).masterCanChange(this);
        break;
      default:
        DiagnosticJLimo.fail();
        break;
    }
  }

  final void setProperties(DataSet master, int id, ReadRow changingRow) {
    this.master       = master;
    this.id           = id;
    this.changingRow  = changingRow;
    this.column       = null;
  }

  final void setProperties(DataSet master, int id, Column column) {
    this.master       = master;
    this.id           = id;
    this.changingRow  = null;
    this.column       = column;
  }

  final void setProperties(DataSet master, int id) {
    this.master       = master;
    this.id           = id;
    this.changingRow  = null;
    this.column       = null;
  }

  /** Returns the kind of Master update event.
  */
  public final int getID() { return id;}

  /** Returns the row that the current row in the master DataSet is about to
      be changed to.  Set to null if id != CHANGING.
  */
  public final ReadRow getChangingRow() { return changingRow; }

  /** Returns the Column being set if id == CAN_CHANGE.
  */
  public final Column getColumn() { return column; }

  /** Returns the master DataSet initiating this event.
  */
  public final DataSet getMaster() { return master; }

  private int           id;
  private ReadRow       changingRow;
  private Column        column;
  private DataSet       master;

  /** Deleting a master update event.
  */
  public final static int DELETING   = 1;
  /** Changing a master update event.
  */
  public final static int CHANGING   = 2;
  /** Asking to change master link columns.
  */
  public final static int CAN_CHANGE = 3;
}
