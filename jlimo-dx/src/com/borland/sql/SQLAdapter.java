//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/sql/SQLAdapter.java,v 7.0 2002/08/08 18:41:14 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.sql;

//import com.borland.jdbc.*;

import java.sql.SQLException;

/**
 * The SQLAdapter interface can be implemented by any JDBC class
 * which can be adapted for improved performance.
 * A JDBC class which implements this interface is said to be adaptable.
 * An adaptation modifies the default behavior for a JDBC object
 * by compromising standard JDBC behavior in favor of performance.
 * <p>
 * Certain JDBC driver classes may choose to implement this interface
 * for enhanced performance with the JBuilder dataset components.
 * Currently, JBuilder only looks for ResultSet adaptations.
 * Future versions of JBuilder may be able to use this for other
 * java.sql package objects.
 * <p>
 * Here's an example adaptation of a result set object which instructs
 * the driver to right trim CHAR string instances, and to avoid
 * constructing multiple java.sql.Date instances by reusing the
 * first Date container fetched on each subsequent fetch.
 * <pre>
 * Statement s = c.createStatement ();
 * ResultSet rs = s.executeQuery ("select NAME, BIRTHDAY from BIRTHDAYS");
 *
 * SQLAdapter rsSQLAdapter = (SQLAdapter) rs;
 * rsSQLAdapter.adapt (SQLAdapter.RIGHT_TRIM_STRINGS, null);
 * rsSQLAdapter.adapt (SQLAdapter.SINGLE_INSTANCE_TIME, null);
 *
 * while (rs.next ()) {
 *   rs.getString ("NAME");    // no need to call trim()
 *   rs.getDate ("BIRTHDAY");  // The first Date object constructed is reused
 *                             // and modified throughout the iterations.
 * }
 * </pre>
 * <p>
 * Additional modifiers will be added in future releases.
 *
 * @since 0.92
 * @author Steve Shaughnessy
 **/
public interface SQLAdapter
{

  /**
   * This modifier instructs an adaptable JDBC driver to create String instances with
   * white space already trimmed from the end of the String
   * on calls to ResultSet.getString ().
   **/
  public final static int RIGHT_TRIM_STRINGS = 1;

  /**
   * This modifier instructs an adaptable JDBC driver to reuse a single instance
   * of a Java Time/Date/Timestamp object on calls to
   * ResultSet.getDate(), ResultSet.getTime() and
   * ResultSet.getTimestamp ().
   **/
  public final static int SINGLE_INSTANCE_TIME = 2;

  /**
      Instructs driver to ensure streams returned will reposition to 0 position
      on a binary field value when the InputStream.reset() method is called.  Also
      expects that there is a separate instance for each binary value returned.
      If driver uses ByteArrayInputStream, then it can return true.  Return true
      if you support this.  extraInfo is always null.
  */
  public final int  RESETABLE_STREAM      = 3;

  /**
   * Adapt the JDBC object which implements this interface
   * to the modification described by one of the above modifiers.
   *
   * @param modifier is either RIGHT_TRIM_STRINGS, SINGLE_INSTANCE_TIME
   * @param extraInfo Any extra information that needs to be specified along with the modifier.
   * @return true if the modifier is supported, false otherwise.
   * @exception SQLException If driver is unable to adapt to the modification..
   **/
  public boolean adapt(int modifier, Object extraInfo) throws SQLException;

  /**
   * Revert back to the default JDBC behavior for
   * the object previously adapted for the
   * modification described by the given modifier.
   *
   * @param modifier is either RIGHT_TRIM_STRINGS, SINGLE_INSTANCE_TIME
   * @exception SQLException If driver is unable to revert the modification back
   * to the standard JDBC behavior.
   **/
  public void revert(int modifier) throws SQLException;

}


