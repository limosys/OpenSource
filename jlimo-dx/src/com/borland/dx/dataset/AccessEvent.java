//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/AccessEvent.java,v 7.0 2002/08/08 18:39:14 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DispatchableEvent;
import com.borland.jb.util.DiagnosticJLimo;
import java.util.*;

 /**
  * The AccessEvent is the internal event generated when a DataSet is opened,
  * closed, or restructured. The AccessEvent class may be useful for component
  * writers; see the com.borland.dbswing source code for usage examples.
  * Not for general usage.
  * <p>
  * The {@link com.borland.dx.dataset.AccessListener} class responds to the
  * AccessEvent class.
  */
public class AccessEvent extends com.borland.jb.util.DispatchableEvent
{
  /** Event type.  Returned from {@link #getID()} */
  public static final int OPEN  = 1;

  /** Event type. Returned from {@link #getID()}.*/
  public static final int CLOSE = 2;

  /** Reason for an {@link #OPEN} event. Unknown dataSet.open() will cause this.
  */
  public static final int UNSPECIFIED   = 1;

  /** Reason for an {@link #OPEN} event.  Indicates that data has changed, but no
      structural changes were made.  This is used when a DataSet sort property is
      changed or a DataSet.empty() is called.  It is also used when
      {@link com.borland.dx.dataset.DataSet#enableDataSetEvents(boolean)} is called
      with an argument value of <b>true</b>.
  */
  public static final int DATA_CHANGE   = 2;

  /** Reason for an {@link #OPEN} event.  Structural change where a Column was
      added to the DataSet.

  */
  public static final int COLUMN_ADD    = 3;

  /** Reason for an {@link #OPEN} event.  Structural change where a Column was
      dropped from the DataSet.

  */
  public static final int COLUMN_DROP   = 4;

  /** Reason for an {@link #OPEN} event.  Structural change where a Column was
      changed in the DataSet.

  */
  public static final int COLUMN_CHANGE = 5;

  /** Reason for an {@link #OPEN} event.  Structural change where a Column was
      moved in the DataSet.

  */
  public static final int COLUMN_MOVE   = 6;

  /** Reason for {@link #CLOSE} event.  Indicates that DataSet was closed and it is not
      known whether it will be opened again.  dataSet.close() will cause this event.
  */
  public static final int UNKNOWN           = 7;

  /**
      Reason for {@link #CLOSE} event.  Indicates that DataSet was closed and it is expected
      to be reopened in a short time period.  Usually called to perform a sort order
      change or structural property change.
  */
  public static final int STRUCTURE_CHANGE  = 8;

  /** Reason for {@link #CLOSE} event.  This is called when a non-structural property change
      like Column Font or Color is changed.  Also used when DataSet.enableDataSetEvents(false)
      is called.  Note that this event will not be sent to DataSetViews listening to
      their associated StorageDataSets.  In practice, this event only makes
      its way to visual components that listen to Access Events.
  */
  public static final int PROPERTY_CHANGE  = 9;

  /**
   * Creates an internal event from the given source with the specified event type.
   * @param source    The event source.
   * @param id        The event type: An {@link #OPEN} event is 1; a CLOSE event is 2.
   */
  public AccessEvent(Object source, int id) {
    super(source);
    this.id         = id;
    this.reason     = UNSPECIFIED;
    this.oldOrdinal = -1;
    this.newOrdinal = -1;
  }

  /**
   *  Creates an internal event from the given source with the specified event
   *  type and reason.
   *
   * @param source    The event source.
   * @param id        The reason for the event. Reasons for {@link #OPEN} events include:
   *                  <ul>
   *                     <li>UNSPECIFIED    = 1     </li>
   *                     <li>DATA_CHANGE    = 2     </li>
   *                     <li>COLUMN_ADD     = 3     </li>
   *                     <li>COLUMN_DROP    = 4     </li>
   *                     <li>COLUMN_CHANGE  = 5     </li>
   *                     <li>COLUMN_MOVE    = 6     </li>
   *                   </ul>
   *                   <p>
   *                   Reasons for CLOSE events include:
   *                   <ul>
   *                      <li>UNKNOWN           = 7  </li>
   *                      <li>STRUCTURE_CHANGE  = 8  </li>
   *                      <li>PROPERTY_CHANGE   = 9  </li>
   *                   </ul>
   * @param reason     The reason for the event.
   */
  public AccessEvent(Object source, int id, int reason) {
    this(source, id);
    DiagnosticJLimo.check(id == OPEN || id == CLOSE);
    this.reason = reason;
  }

  /**
   * Creates an internal event from the given source with the specified
   * event type and reason. The internal event drops the specified column.
   *
   * @param source    The event source.
   * @param id        The reason for the event. Reasons for {@link #OPEN} events include:
   *                  <ul>
   *                     <li>UNSPECIFIED    = 1     </li>
   *                     <li>DATA_CHANGE    = 2     </li>
   *                     <li>COLUMN_ADD     = 3     </li>
   *                     <li>COLUMN_DROP    = 4     </li>
   *                     <li>COLUMN_CHANGE  = 5     </li>
   *                     <li>COLUMN_MOVE    = 6     </li>
   *                   </ul>
   *                   <p>
   *                   Reasons for CLOSE events include:
   *                   <ul>
   *                      <li>UNKNOWN           = 7  </li>
   *                      <li>STRUCTURE_CHANGE  = 8  </li>
   *                      <li>PROPERTY_CHANGE   = 9  </li>
   *                   </ul>
   * @param reason      The reason for the event.
   * @param dropColumn  The name of the column being dropped.
   */
  public AccessEvent(Object source, int id, int reason, Column dropColumn){
    this(source, id, reason);
    DiagnosticJLimo.check(reason == COLUMN_DROP);
    this.dropColumn  = dropColumn;
  }

  /**
   *  Creates an internal event from the given source with the specified
   *  event type and reason. The internal event restructures the table.
   *
   * @param source      The event source.
   * @param id          The event type: An OPEN event is a {@link #CLOSE} event is 2.
    * @param reason      The reason for the event. Reasons for {@link #OPEN} events include:
   *                    <ul>
   *                       <li>UNSPECIFIED    = 1 </li>
   *                       <li>DATA_CHANGE    = 2 </li>
   *                       <li>COLUMN_ADD     = 3 </li>
   *                       <li>COLUMN_DROP    = 4 </li>
   *                       <li>COLUMN_CHANGE  = 5 </li>
   *                       <li>COLUMN_MOVE    = 6 </li>
   *                     </ul>
   *                     <p>
   *                     Reasons for CLOSE events include:
   *                     <ul>
   *                        <li>UNKNOWN           = 7 </li>
   *                        <li>STRUCTURE_CHANGE  = 8 </li>
   *                        <li>PROPERTY_CHANGE   = 9 </li>
   *                      </ul>
   * @param oldOrdinal    The ordinal number of the column to replace.
   * @param newOrdinal    The ordinal number of the column to replace oldOrdinal with.
   */
  public AccessEvent(Object source, int id, int reason, int oldOrdinal, int newOrdinal) {
    this(source, id, reason);
    DiagnosticJLimo.check(reason == COLUMN_MOVE);
    this.oldOrdinal = oldOrdinal;
    this.newOrdinal = newOrdinal;
  }

  /**
   *  Creates an internal event from the given source with the specified event type and reason.
   *  The internal event restructures the table.
   *
   * @param source      The event source.
   * @param id          The event type: An OPEN event is 1; a {@link #CLOSE} event is 2.
   * @param reason      The reason for the event. Reasons for {@link #OPEN} events include:
   *                    <ul>
   *                       <li>UNSPECIFIED    = 1 </li>
   *                       <li>DATA_CHANGE    = 2 </li>
   *                       <li>COLUMN_ADD     = 3 </li>
   *                       <li>COLUMN_DROP    = 4 </li>
   *                       <li>COLUMN_CHANGE  = 5 </li>
   *                       <li>COLUMN_MOVE    = 6 </li>
   *                     </ul>
   *                     <p>
   *                     Reasons for CLOSE events include:
   *                     <ul>
   *                        <li>UNKNOWN           = 7 </li>
   *                        <li>STRUCTURE_CHANGE  = 8 </li>
   *                        <li>PROPERTY_CHANGE   = 9 </li>
   *                      </ul>
   * @param oldColumn   The name of the column to replace.
   * @param newColumn   The name of the column to replace oldColumn with.
   */
  public AccessEvent(Object source, int id, int reason, Column oldColumn, Column newColumn) {
    this(source, id, reason);
    DiagnosticJLimo.check(reason == COLUMN_CHANGE);
    this.oldColumn  = oldColumn;
    this.newColumn  = newColumn;
  }

  /**
   * Creates an internal event from the given source for the specified event.
   *
   * @param source    The event source.
   * @param event     The type of event that changed a DataSet.
   */
  public AccessEvent(Object source, AccessEvent event) {
   this(source, event.id, event.reason);

   this.oldOrdinal  = event.oldOrdinal;
   this.newOrdinal  = event.newOrdinal;

   this.dropColumn  = event.dropColumn;

   this.oldColumn   = event.oldColumn;
   this.newColumn   = event.newColumn;
  }

  public void dispatch(EventListener listener) {
    ((AccessListener)listener).accessChange(this);
  }

  /**
   * The event type.
   * Valid return values for this method are defined as variables in this class.
   *
   * @return  The event type.
   */
  public final int getID() {
    return id;
  }

  /**
   * Read-only property that returns the reason for the event.
   * Valid values are defined in the variables section of this class.
   *
   * @return  The reason for the event.
   */
  public final int getReason() {
    return reason;
  }

  /** Get old ordinal postion from a column move.
   *  @return The old ordinal postion from a column move.
  */
  public int getOldOrdinal() { return oldOrdinal; }
  /**
   * Get new ordinal postion from a column move.
   * @return The new ordinal postion from a column move.
  */
  public int getNewOrdinal() { return newOrdinal; }

  /**
   *  Get old Column from a COLUMN_CHANGE.
   *  @return The old Column from a COLUMN_CHANGE.
  */
  public Column getOldColumn() { return oldColumn; }
  /**
   * Get new Column from a COLUMN_CHANGE.
   * @return The new Column from a COLUMN_CHANGE.
  */
  public Column getNewColumn() { return newColumn; }
  /**
   * Get dropped Column from a COLUMN_DROP.
   * @return The dropped Column from a COLUMN_DROP.
  */
  public Column getDropColumn() { return dropColumn; }

  private int id;
  private int reason;

  private int oldOrdinal;
  private int newOrdinal;

  private Column dropColumn;

  private Column oldColumn;
  private Column newColumn;
}
