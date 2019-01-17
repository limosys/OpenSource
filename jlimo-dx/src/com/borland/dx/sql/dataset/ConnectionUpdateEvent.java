//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ConnectionUpdateEvent.java,v 7.0 2002/08/08 18:39:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.ExceptionDispatch;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.*;

/**
 * This class is used to inform the ConnectionUpdateListener before and after closing
 *  a database connection or when changing the attributes of the JDBC connection.
 */
public class ConnectionUpdateEvent extends EventObject
  implements ExceptionDispatch
{
 /**
 * Constructs a ConnectionUpdateEvent object.
 */  public ConnectionUpdateEvent(Object source) {
    super(source);
  }

  public void exceptionDispatch(EventListener listener) throws Exception
  {
    switch (id) {
      case CHANGED:
        ((ConnectionUpdateListener)listener).connectionChanged(this);
        break;
      case CLOSED:
        ((ConnectionUpdateListener)listener).connectionClosed(this);
        break;
      case CAN_CLOSE:
        ((ConnectionUpdateListener)listener).canChangeConnection(this);
        break;
      case OPENING:
        ((ConnectionUpdateListener)listener).connectionOpening(this);
        break;
    }
  }

  final void setProperties(int id) {
    this.id = id;
  }

  private int id;

  /** Changing a database connection.
  */
  public final static int CHANGED    = 1;
  /** Closing a database connection.
  */
  public final static int CLOSED     = 2;
  /** Asking to close a database connection.
  */
  public final static int CAN_CLOSE  = 3;
  /** @since JB3.0
      Connection is being opened.  This can be wired to prompt user name and
      password from the user.
  */
  public final static int OPENING  = 4;
}
