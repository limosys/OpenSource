//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ConnectionDescriptor.java,v 7.0 2002/08/08 18:39:49 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import java.util.Properties;

/**The <CODE>ConnectionDescriptor</CODE> object stores properties related to connecting to a SQL database.  Its main properties are:
<UL>
<LI><CODE>connectionURL</CODE> (the Universal Resource Locator of the database)
<LI><CODE>userName</CODE>
<LI><CODE>password</CODE>
<LI><CODE>driver</CODE>
<LI>(extended driver) <CODE>properties</CODE>
</UL>
<P>Both the <CODE>ConnectionDescriptor</CODE> object and the <CODE>Database</CODE> component are required elements when accessing data that is stored on a SQL server.

<P>The information stored in the <CODE>ConnectionDescriptor</CODE>
* can be accessed through the user interface by inspecting the
* <CODE>connection</CODE> property of a {@link com.borland.dx.sql.dataset.Database} Database</A> object.  To work with this object programmatically, you set its properties when instantiating the <CODE>ConnectionDescriptor</CODE>, or by its write accessors.


<!-- JDS start - remove paragraph -->
<P>To set these properties  through the JBuilder UI design tools, select the <CODE>Database</CODE> object, then double click the area to the right of the <CODE>connection</CODE> property in the Inspector. Or, single click it to display the ellipses button and click it to open the custom property editor dialog for this descriptor. The Connection custom property editor also offers these additional ease-of-use features:
<UL>
<LI>A Choose Existing Connection button that displays connection history.
<LI>A Test Connection button that tests the connection property settings.
<LI>Registration of drivers with JDBC.
<LI>Automatic discovery of required extended driver properties.
</UL>

 */
public class ConnectionDescriptor implements Cloneable, java.io.Serializable {
  private String connectionURL;
  private String userName;
  private String password;
  private boolean promptPassword;
  private String driver;
  Properties properties;


  /**
   * Constructs a ConnectionDescriptor with the specified URL to the database
   * @param connectionURL String
   */
  public ConnectionDescriptor(String connectionURL) {
    this(connectionURL, null, null);
    //!System.err.println("connectionDescriptor1: " + this);
  }


  /**
   * Constructs a ConnectionDescriptor using the property values from the
   * ConnectionDescriptor object specified as cDesc.
   * @param cDesc ConnectionDescriptor
   */
  public ConnectionDescriptor(ConnectionDescriptor cDesc) {
    this.connectionURL = cDesc.connectionURL;
    this.userName = cDesc.userName;
    this.password = cDesc.password;
    this.driver = cDesc.driver;
    this.properties = cDesc.properties;
    //!System.err.println("connectionDescriptorC: " + this);

  }

  /**
   * Constructs a ConnectionDescriptor with the specified connection URL to the
   * database and user name.
   * @param connectionURL String
   * @param userName String
   */
  public ConnectionDescriptor(String connectionURL, String userName) {
    this(connectionURL, userName, null);
    //!System.err.println("connectionDescriptor2: " + this);
  }

  /**
   * Constructs a ConnectionDescriptor with the specified connection URL to the
   *  database, user name, and password.
   * @param connectionURL String
   * @param userName String
   * @param password String
   */
  public ConnectionDescriptor(String connectionURL, String userName, String password) {
    //! Ron, if password prompting is not disabled, my test suites will not work.
    //! Shouldn't prompting be disabled by default if the password is specified? -Steve.
    //
    this(connectionURL, userName, password, password == null);
    //!System.err.println("connectionDescriptor3: " + this);
  }

  /**
   * Constructs a ConnectionDescriptor with the specified connection URL to the database, user name,
   *  password, and whether to prompt for the password each time or store the password
   * in the ConnectionDescriptor
   * @param connectionURL String
   * @param userName String
   * @param password String
   * @param promptPassword boolean
   */
  public ConnectionDescriptor(String connectionURL, String userName, String password, boolean promptPassword) {
    this.connectionURL = connectionURL;
    this.userName      = userName;
    this.password      = password;
    this.promptPassword= promptPassword;
    //!System.err.println("connectionDescriptor4: " + this);
  }

  /**
   * Constructs a ConnectionDescriptor with the specified connection URL to the
   *  database, user name, password, whether to prompt for the password each time
   * or store the password in the ConnectionDescriptor, and the driver class to use
   *  when connecting to the Database.
   * @param connectionURL String
   * @param userName String
   * @param password String
   * @param promptPassword boolean
   * @param driver String
   */
  public ConnectionDescriptor(String connectionURL, String userName, String password, boolean promptPassword, String driver) {
    this.connectionURL = connectionURL;
    this.userName      = userName;
    this.password      = password;
    this.promptPassword= promptPassword;
    this.driver        = driver;
    //!System.err.println("connectionDescriptor5: " + this);
  }

  /**
   * Constructs a ConnectionDescriptor with the specified connection URL to the
   * database, user name, password, whether to prompt for the password each time
   * or store the password in the ConnectionDescriptor, the driver class to use
   * when connecting to the Database, and the instance of a java.util.Properties
   * that stores extended driver properties to use when connecting. Not all drivers
   *  support connecting to a database using a java.util.Properties object. Check your
   *  driver documentation for more information on whether it supports this feature or
   * not.
   * @param connectionURL String
   * @param userName String
   * @param password String
   * @param promptPassword boolean
   * @param driver String
   * @param properties properties
   */
  public ConnectionDescriptor(String connectionURL,
                              String userName,
                              String password,
                              boolean promptPassword,
                              String driver,
                              Properties properties) {
    this.connectionURL = connectionURL;
    this.userName      = userName;
    this.password      = password;
    this.promptPassword= promptPassword;
    this.driver        = driver;
    this.properties    = (properties == null) ? null : (Properties) properties.clone();
    //!System.err.println("new ConnectionDescriptor() -- long form with properties " + properties);
  }

  public synchronized String getConnectionURL()         {return connectionURL;}
  public synchronized void setConnectionURL(String url) {connectionURL = url;}
  public synchronized String getUserName()              {return userName;}
  public synchronized void setUserName(String userName) {this.userName = userName;};
  public synchronized String getPassword()              {return password;}
  public synchronized void setPassword(String password) {this.password = password;}
  public synchronized boolean isPromptPassword()        {return promptPassword;}
  public synchronized void setPromptPassword(boolean prompt) {promptPassword = prompt;}
  public synchronized String getDriver()                {return driver;}
  public synchronized void setDriver(String driver)     {this.driver = driver;}
  public synchronized Properties getProperties()        {return (properties != null) ? (Properties) properties.clone() : null;}
  public synchronized void setProperties(Properties properties) {this.properties = properties;}

  /**
  * Returns 'true' if and only if all the ConnectionDescriptor parameters have been set
  */
  public synchronized boolean isComplete() {
    return (connectionURL != null && connectionURL.length() > 0 &&
            userName != null && userName.length() > 0 &&
            password != null && password.length() > 0);

  }

  /*
   * A service method to check if 2 connections can be shared at design time.
   * This method is only used by com.borland.sql.DesignerConnectionCache and is
   * thus package local. This replaces equals() from version 1.0 and 1.01.
   */
  boolean canShare(ConnectionDescriptor cDesc) {
    return ((cDesc != null) &&
            (connectionURL != null && connectionURL.equals(cDesc.connectionURL)) &&
            (userName != null && userName.equals(cDesc.userName)) &&
            (password != null && password.equals(cDesc.password)) &&
            (driver != null && driver.equals(cDesc.driver))
           );
  }

  /**
   * A service method to convert a 2D string array into a Properties object.
   * Used by ConnectionDescriptor editor in generating code.
   * Can also be used by developers.
   *
   * @param array a 2D array of strings corresponding to key/value pairs
   *
   * @return a Properties object containing these pairs
   */
  public static Properties arrayToProperties(String[][] array) {
    //!System.err.println("arrayToProperties(" + array + ")");

    Properties properties = new Properties();
    String name;
    String value;
    for (int i = 0; i < array.length; ++i) {
      name = array[i][0];
      value = array[i][1];
      if (name != null && name.length() > 0)
        properties.put(name, (value == null ? "" : value));
    }
    //!System.err.println(" returning " + properties);
    return properties;
  }

  /**
  * Show String form of ConnectionDescriptor.  The password does not show.
  */
  public String toString() {
    String pw = (password == null ? "null" : "*");  //NORES
    return new String( Res.bundle.format( ResIndex.ConnectionDescriptor,
                                   new String[] { ((connectionURL != null) ? connectionURL : ""),
                                                  ((userName != null) ? userName : ""),
                                                    pw,
                                                    String.valueOf(promptPassword),
                                                   ((driver != null) ? driver : ""),
                                                   ((properties != null) ? properties.toString() : "")
                                                }
                                   )
                       );
  }

  private static final long serialVersionUID = 1L;
}

