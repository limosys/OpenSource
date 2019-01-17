//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/Database.java,v 7.0 2002/08/08 18:39:50 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

//import com.borland.jb.util.Trace;
import com.borland.jb.util.EventMulticaster;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;

//import com.borland.jbcl.control.*;
import com.borland.dx.swing.PasswordDialog;
//import com.borland.dx.sql.metadata.MetaData;


import java.net.URL;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.sql.*;

/**
 *The <CODE>Database</CODE> component is a required element of any application accessing data stored
*on a SQL server. It encapsulates a database connection through JDBC to the SQL server and
*also provides lightweight transaction support.
*
*<P>When used with a <CODE>QueryDataSet</CODE> or <CODE>ProcedureDataSet</CODE> component, data is retrieved from the
*  external database into a local cache (<CODE>DataSet</CODE>) on the user's  system.
*   All row data for a particular <CODE>DataSet</CODE> is cached on the user's system
*   as a single unit. Changes made to the local copy of the
* data in the <CODE>DataSet</CODE> are internally recorded as deletes,
* inserts and updates. When all changes to the <CODE>DataSet</CODE> are
* complete, you then save the changes  back to the original database
* by calling the <CODE>saveChanges()</CODE> method with one or more
* <CODE>DataSet</CODE> components.
*
*<P>You can connect several <CODE>QueryDataSet</CODE> or <CODE>ProcedureDataSet</CODE>
*  components to a <CODE>Database</CODE> component, however, some SQL servers
* allow only one active query at a time on a  connection. Check your server
* documentation to see if this is applicable to the SQL server you are accessing.
*
*<P>
*<A NAME="connectionproperties"></A>
*<H3>Setting connection properties</H3>
*<!--BNDX="SQL connections"-->
*<!--BNDX="databases:connecting to. See connections"-->
*<!--BNDX="SQL connections:setting properties for;connections:setting properties for"-->
*<P>The <CODE>Database</CODE> component has an associated <CODE>connection</CODE> property that stores the  connection properties of <CODE>userName</CODE>, <CODE>password</CODE>, and <CODE>connectionUrl</CODE> of the database.
*These properties are stored in the {@link com.borland.dx.sql.dataset.ConnectionDescriptor}
* <CODE>ConnectionDescriptor</CODE></A> object. When the necessary properties for the connection
* have been supplied, the connection
*can be opened explicitly or automatically. When explicitly connecting, use the
*<CODE>openConnection()</CODE> method. The connection is opened automatically when
*   you explicitly open the <CODE>DataSet</CODE>, or when a UI control requests data
* that is obtained through the database connection.
*
*<!-- JDS start - remove paragraph -->
*<P>The information stored in the <CODE>ConnectionDescriptor</CODE> object is
*  accessible through the user interface by inspecting the <CODE>connection</CODE>
* property of a <CODE>Database</CODE> object. This displays the Connection
*  property editor.
*<!-- JDS end -->
*
*
*<P>If all needed properties have been set, a connection is attempted when any of
*  the following situations occur:
*<UL>
*<!-- JDS start - remove next list item -->
*<LI>the Test Connection button is clicked from the Connection property editor
*<!-- JDS end -->
*<LI>an explicit <CODE>openConnection()</CODE> method call is made
*<LI>a <CODE>QueryDataSet</CODE> that relies on data from this connection requests it
*</UL>
*
*<!-- JDS start - remove property editor stuff -->
*<P>When attempting to make the connection to the <CODE>Database</CODE>,
*  the appropriate driver needed to access the remote server is loaded.
* If the remote server driver information is not available in the system
* registry, you can specify the driver using the <CODE>addDriver()</CODE>
* method, in the <CODE>ConnectionDescriptor</CODE> object, or in the
* Connection property editor.
*<!-- JDS end+-->
*
*
*<P>
*<A NAME="dbappdesign"></A>
*<H3>Application design</H3>
*<!--BNDX="applications:;applications:designing;designing applications"-->
*<!--BNDX="transactions"-->
*<P>It is strongly recommended that you include all DataExpress components
* (database connections, queries, <CODE>DataStores</CODE>, and so on) in a {@link com.borland.dx.dataset.DataModule} <CODE>DataModule</CODE></A>. The <CODE>DataModule</CODE> is a specialized container for data access components and their properties. Consolidation of these components in a single container clarifies an application's design and increases the  reusability of the data access components.
*
*<P>The isolation level, specified by the <CODE>transactionIsolation</CODE> property, is used when saving  data changes back to the external database table.
*
*<P>When you need special transaction logic, use the {@link #saveChanges(com.borland.dx.dataset.DataSet[], boolean, boolean, boolean)}
* <CODE>saveChanges(com.borland.dx.dataset.DataSet[], boolean, boolean, boolean)</CODE></A> method. By setting its final parameter <CODE>resetPendingStatus</CODE> to false, this method offers the flexibility of not resetting the pending resolved status bits through the call to the <CODE>saveChanges</CODE> method. When you want to reset the pending resolved status bit, call the <CODE>resetPpendingStatus</CODE> method. This allows you, for example, to save changes made to several <CODE>DataSets</CODE> in a single transaction, and to rollback all changes while still retaining all the changed data in both <CODE>DataSets</CODE>.
*
*<P>When designing your application that involves prompting for a password,
* set the {@link com.borland.dx.sql.dataset.ConnectionDescriptor}
* <CODE>promptPassword</CODE></A>  property to true, then call the
* <CODE>openConnection()</CODE> method for your database when you want the
*  username/password dialog to appear. If you want the username/password dialog to appear as soon as your application loads, call the <CODE>openConnection()</CODE> method at the end of the main frame's <CODE>jbInit()</CODE> method.
*
*<P>If the user cancels the password dialog, your application can detect
*  a <CODE>DataSetException</CODE> of type {@link com.borland.dx.dataset.DataSetException} CONNECTION_DESCRIPTOR_NOT_SET</A> and take the appropriate action. The application could either terminate or disable data-access functions.
*
*<P>When you no longer need a <CODE>Database</CODE> connection, you should
* explicitly call the <CODE>Database.closeConnection()</CODE> method in your
* application. This ensures that <CODE>Database</CODE> classes which hold
*  references to JDBC connections are automatically closed when the
*  <CODE>Database</CODE> object is garbage collected.
*
*<P>
*<A NAME="debuggingconnections"></A>
*<H3>Debugging JDBC connections</H3>
*<!--BNDX="debugging JDBC connections;connections:debugging;JDBC
* connections:debugging"-->
*<!--BNDX="troubleshooting;troubleshooting:JDBC connections;output"-->
*<!--BNDX="ODBC drivers;debugging"-->
*<P>When debugging JDBC connection issues, you can add the following line of
* code to your application before the line of code that executes your query or
*  stored procedure:
*<PRE><CODE>java.sql.DriverManager.setLogStream(System.out);</CODE></PRE>
*This generates (verbose) output from the JDBC driver that shows what is happening and in
*what sequence. Examining this output may help determine the source of JDBC connection
*related problems in your application.
*
*<P>To turn off the debugging output, use the following code:
*<PRE><CODE>java.sql.DriverManager.setLogStream(null);</CODE></PRE>
*
*<P>If you're connecting to data using ODBC drivers under the MicroSoft Windows operating system, enable ODBC logging through the  Control Panel program. The calls that take place are displayed, enabling you to track what is being sent to the ODBC driver.

*/
/*
  Concurrency issues.
    checkConnectionChange() is called to see if any of the databaseListeners
    object to any connection related change.
*/
public class Database implements java.io.Serializable,   Designable
{
  private static boolean useDesignerConnectionCache = true;     // turn off to disable DesignerConnectionCache

  public Database() {
    classLoader = this.getClass().getClassLoader();
  }

  public void setDatabaseName(String name) {
    this.databaseName  = name;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  //!  //$$$ testing only <rac> TODO -- remove when array editor is working and generating right code
//!/*
//!  private String[] testArray;
//!  public void setTestArray(String[] array) {
//!    testArray = array;
//!  }
//!  public String[] getTestArray() {
//!    return testArray;
//!  }
//!*/
  /**
  * The ConnectionDescriptor contains all the information needed by the Database to establish a
  * JDBC connection (which includes a URL, a userName, and a password).  You can "get" or "set"
  * this property at anytime, but the new Connection Descriptor will not be applied until the
  * next time the database is explicitly opened.
  */
  public ConnectionDescriptor getConnection() {
//!    Diagnostic.println("Database.getConnection() on " + this + " -->" + this.connectionDescriptor);
//!System.err.println("Database.getConnection() on " + this + " -->" + this.connectionDescriptor);
    return this.connectionDescriptor;
  }

  public void setConnection(ConnectionDescriptor connectionDescriptor)
    /*-throws DataSetException-*/
  {
    //!System.err.println("Database.setConnection(" + connectionDescriptor + ") on " + this);
    closeConnection();
    this.connectionDescriptor = connectionDescriptor;
    connectionChanged(false);
//!    Diagnostic.println("Database.setConnection(" + connectionDescriptor + ") on " + this);
  }

  public final java.sql.Connection getJdbcConnection()
    /*-throws DataSetException-*/
  {
    openConnection();
    return connection;
  }

  /**
      Current state of autoCommit will be cached by Database component.
      Any changes to the Connection autoCommit mode should be made through
      the DataBase component setAutoCommit method so that its cached state
      can be updated.
  */
  public final void setJdbcConnection(Connection connection)
    /*-throws DataSetException-*/
  {
    connectionProvided  = (connection != null);
    this.connection = connection;
    try {
      this.autoCommit = connection.getAutoCommit();
    }
    catch(SQLException ex) {
      DataSetException.SQLException(ex);
    }
  }


  private final void connectionChanged(boolean closed) {
    try {
      if (closed) {
        //!Diagnostic.println(" zapping connection to null");
        if (runtimeMeta != null)
          runtimeMeta.connectionClosed();
        connection = null;
      }
      if (databaseListeners != null) {
        int id = closed ? ConnectionUpdateEvent.CLOSED : ConnectionUpdateEvent.CHANGED;
        connectionUpdateEvent.setProperties(id);
        databaseListeners.exceptionDispatch(connectionUpdateEvent);
      }
    }
    catch (Exception ex) {
      DiagnosticJLimo.printStackTrace(ex);
    }
  }

  private final void checkConnectionChange()
    /*-throws DataSetException-*/
  {
    try {
      if (databaseListeners != null) {
        connectionUpdateEvent.setProperties(ConnectionUpdateEvent.CAN_CLOSE);
        databaseListeners.exceptionDispatch(connectionUpdateEvent);
      }
    }
    catch (DataSetException ex) {
      throw ex;
    }
    catch (Exception ex) {
      DataSetException.connectionNotClosed(ex);
    }
  }

  private final void opening()
    /*-throws DataSetException-*/
  {
    try {
      if (databaseListeners != null) {
        connectionUpdateEvent.setProperties(ConnectionUpdateEvent.OPENING);
        databaseListeners.exceptionDispatch(connectionUpdateEvent);
      }
    }
    catch (DataSetException ex) {
      throw ex;
    }
    catch(Exception ex) {
      DataSetException.throwExceptionChain(ex);
    }
  }

  /**
   * A little helper for adding a driver to the "jdbc.drivers" System class
   * property.  Note, jdbc-odbc driver is currently always added to the
   * property setting.
   */
  public final void addDriver(String driver) {
    try {
      addDriver(driver,false);
    }
    catch (DataSetException ex) {
    }
  }
  /**
   * A little helper for adding drivers to the "jdbc.drivers" System class
   * property.  Note, jdbc-odbc driver is currently always added to the
   * property setting.
   * Multiple drivers can be specified in the driver parameter by separating
   * each driver with a ';' character.
   */

  public static void addDrivers(String driver) /*-throws DataSetException-*/ {
    boolean multiple = (driver.indexOf(";") > 0);
    StringTokenizer tokenizer = new StringTokenizer(driver, ";");
    while (tokenizer.hasMoreTokens()) {
      String s = tokenizer.nextToken();
      //!System.err.println(" autoregistering: " + s);
      addDriver(s,multiple);
    }
  }

  public static void addDriver(String driver, boolean multiple) /*-throws DataSetException-*/ {
    //!System.err.println("addDriver(" + driver + ")");

    // Applets will throw a security exception here -- swallow that silently
    try {
      Properties  properties  = System.getProperties();
      String drivers = properties.getProperty("jdbc.drivers");  //NORES
      if (drivers == null)
        drivers = "";
      else
        drivers = drivers + ":";
      if (drivers.indexOf(driver) < 0) {
      //!System.err.println("  *** adding JDBC driver: " + driver);
        properties.put("jdbc.drivers", drivers + driver);  //NORES
      // Try to load the driver now as well as registering it.
      // This seems to be necessary if the driver is registered AFTER
      // the JDBC connection manager has opened its properties file
      }
    }
    catch (Throwable ex) {
      DiagnosticJLimo.printStackTrace(ex);
    }

    // Asking the driver's class to load will invoke their static code
    // which is required to call DriverManager.registerDriver()
    try {
       //! We used just to call Class.forName
       //! Class.forName(driver);


//     Now use the classLoader used to load the Database class.
//     Call newInstance() on the new class to force it to load and
//     execute the static code needed to register the driver with the JDBC
//     DriverManager.  First check if a driver is already loaded.
//     This code is neccessary for IE and AppClassLoader in JDK1.2

      Class driverClass = classLoader == null ? Class.forName(driver) : classLoader.loadClass(driver);
      Enumeration drivers = DriverManager.getDrivers();
      boolean found = false;
      while (drivers.hasMoreElements()) {
        Object driverInstance = drivers.nextElement();
        if (driverClass.isInstance(driverInstance)) {
          found = true;
          break;
        }
      }
      if (!found)
        driverClass.newInstance();
      DiagnosticJLimo.println("Successfully loaded JDBC driver class: " + driver);
    }
    catch (Throwable ex) {
      DiagnosticJLimo.printStackTrace(ex);

      if (!multiple) {
        if (java.beans.Beans.isDesignTime())
          DataSetException.driverNotLoadedInDesign(driver);
        else
          DataSetException.driverNotLoadedAtRuntime(driver);
      }
    }
  }

  synchronized final void sharedCloseConnection()
    /*-throws DataSetException-*/
  {
    checkConnectionChange();
    connectionChanged(true);
  }

  public synchronized final void closeConnection()
    /*-throws DataSetException-*/
  {
    //!Diagnostic.println("closeConnection()");

    checkConnectionChange();

    if (connection != null) {

      boolean reallyClose = true;     // this may be changed if using designer cache below

      // During design time, we maintain a useCounted cache of connections.
      // This is to overcome Java's seemingly random finalize's which allow
      // us to exhaust a connection limit toggling back and forth to design.
      //if (java.beans.Beans.isDesignTime() && connectionUseCount > 0) {
      if (useDesignerConnectionCache && java.beans.Beans.isDesignTime()) {
        //!Diagnostic.println(" releasing connection: " + connection);
        reallyClose = DesignerConnectionCache.releaseConnection(connection);
      }

      // I hate this, but it gets around a problem with visigenics odbc driver.
      // If you open connection, createPreparedStatement (without executing it)
      // then close you get an "Invalid transaction state" error.  By setting
      // connection to AutoCommit, problem goes away.  I even tried committing
      // transactions
      //

      //!RC Added try/catch.  When driver crashes, this will throw exception
      //!RC but we need to keep churning to get the close accomplished
      try {
        if (getMetaData().supportsTransactions())
          setAutoCommit(true);
      }
      catch (Throwable ex){
        // nothing we can do at this point
//!        com.borland.jbcl.model.DataSetModel.handleException(ex);
      }

      try {
        if (reallyClose) {
          //!Diagnostic.println("*** closing jdbc connection now: " + connection);
          connection.close();
        }
      }
      catch (SQLException ex){
        DataSetException.SQLException(ex);
      }
//!RC Added finally wrapper around the shutdown so it happens whether
//!RC or not we get an exception closing
      finally {
        connectionChanged(true);
      }
    }
  }

  public synchronized final void openConnection()
    /*-throws DataSetException-*/
  {

    //!Diagnostic.println("openConnection()");

    if (connection == null) {

      if (databaseName != null && databaseName.length() > 0) {
        connection = Naming.getConnection(databaseName);
        if (connection != null) return;
      }

      // This is the default mode of a JDBC driver.
      //
      autoCommit  = true;
      boolean designTime  = java.beans.Beans.isDesignTime();

      if (useDesignerConnectionCache && designTime) {
        //!Diagnostic.println("  looking in cache");
        connection  = DesignerConnectionCache.shareConnection(getConnection());
        if (connection != null) {
          //!Diagnostic.println(" sharing connection already cached: " + connection);
          return;
        }
        //else Diagnostic.println(" connection not in the cache");
      }

     opening();

     // Attempt to connect to a driver.  Each one
     // of the registered drivers will be loaded until
     // one is found that can process this URL
     //
      ConnectionDescriptor cDesc = getConnection();
      if (cDesc == null)
        DataSetException.connectionDescriptorNotSet();

      // Pre-register any driver(s) (optionally) declared by the user
      String driver = cDesc.getDriver();
      if (driver != null && driver.length() > 0)
        addDrivers(driver);

      //! to help detect sample files which don't register drivers
      //!else {
      //!  System.err.println("no driver specified!!!!!");
      //!  Diagnostic.printStackTrace();
      //!}

      //!RC We are experiencing deadlocks in design mode
      //! Don't prompt in design mode if we have a password already
//!/*
//!      String password = cDesc.getPassword();
//!      boolean havePassword = (password != null && password.length() > 0);
//!      boolean needPrompt = designTime
//!                             ? (cDesc.isPromptPassword() &&
//!                                 (cDesc.getPassword() == null ||
//!                                  cDesc.getPassword().length() == 0))
//!                             : cDesc.isPromptPassword();
//!*/
      //!RC Cannot prompt for password in design -- deadlock issues arise with modal dialog
      //! Do not prompt for a password when in design
      //!      boolean needPrompt = cDesc.isPromptPassword() && !designTime;

      //! modal dialog in addNotify() deadlock was fixed in JDK 1.2.2, so there's no
      //! reason for us not to prompt in design mode. -dcy 7/15/99
      String password = cDesc.getPassword();
      boolean havePassword = (password != null && password.length() > 0);
      boolean needPrompt = designTime ? (cDesc.isPromptPassword() && !havePassword) :
                                        cDesc.isPromptPassword();

      //! it's okay to pass a null as frame and/or title with Swing. -dcy
      PasswordDialog passwordDialog = null;
      if (needPrompt) {
        passwordDialog = new PasswordDialog(cDesc.getConnectionURL());
        passwordDialog.setLocationRelativeTo(null);
      }
      // We hang in a loop allowing the user multiple attempts at getting the password right
      // (But we prompt only if the connectionDescriptor told us to).
      while (true) {
        if (needPrompt) {
//! for PrimeTime, made default password dialog dbSwing's dialog. -dcy 7/15/99
//!          if (!UserNamePasswordDialog.invoke(null, null, this))
//!            DataSetException.connectionDescriptorNotSet();
//!          cDesc = getConnection();    // get what the user specified in the dialog
          passwordDialog.show();
          if (!passwordDialog.okPressed) {
            DataSetException.connectionDescriptorNotSet();
          }
          cDesc.setUserName(passwordDialog.userNameField.getText());
          cDesc.setPassword(new String(passwordDialog.passwordField.getPassword()));
        }

        //!Diagnostic.println("user "+user);
        //!Diagnostic.println("password "+password);
//!        String url = cDesc.getConnectionURL();
//!        Diagnostic.println("url \""+url+"\"");
        try {
          //!Diagnostic.println(" trying connection...");
          if (cDesc.getProperties() == null || cDesc.getProperties().size() == 0)
            connection = DriverManager.getConnection(cDesc.getConnectionURL(), cDesc.getUserName(), cDesc.getPassword());

          // If we have extended properties, merge username and password into the properties
          // and use the alternate form to establish the connection
          else {
            Properties properties = (Properties) cDesc.getProperties().clone();
            //properties.put("username", (cDesc.getUserName() == null) ? "" : cDesc.getUserName()); //NORES
            //properties.put("password", (cDesc.getPassword() == null) ? "" : cDesc.getPassword()); //NORES
            //!RC 1/13/98 Smarter logic no longer overwrites non-blank values with blanks
            putExtendedProperty(properties, "user",     cDesc.getUserName()); //NORES
            putExtendedProperty(properties, "username", cDesc.getUserName()); //NORES
            putExtendedProperty(properties, "password", cDesc.getPassword()); //NORES
            //!Diagnostic.println("database connecting with properties: " + properties);
            connection = DriverManager.getConnection(cDesc.getConnectionURL(), properties);
          }
          connectionProvided  = false;

          break;
        }
        catch (SQLException ex) {
          //!System.err.println("SQLexception: " + ex);
          DataSetException ex2 = null;
          Exception ex3 = ex;
          if (ex.getMessage().equals("No suitable driver")) { //NORES
            //!TODO: Change this for next rev 1.2
            if (designTime)
              ex3 = ex2 = DataSetException.mkUrlNotFoundInDesign(cDesc.getConnectionURL(), ex);
            else
              ex3 = ex2 = DataSetException.mkUrlNotFound(cDesc.getConnectionURL(), ex);
          }

          if (needPrompt) {
//! in PrimeTime, changed to use dx.swing's exception handler instead.  For efficiency,
//! PasswordDialog also includes a method which displays the exception message in
//! a dialog.  Also, in JDK 1.2.2, it is okay to display the dialog in design time as well.
//!            com.borland.jbcl.model.DataSetModel.handleException(null, null, ex3, !(java.beans.Beans.isDesignTime()));
//!              // ask for modal dialog when NOT design time, so user has to put away exception
//!              // dialog before being reprompted
              // ask for modal dialog when NOT design time, so user has to put away exception
              // dialog before being reprompted
            passwordDialog.showExDialog(ex.getMessage());
            continue;
          }
          if (ex2 == null)
            DataSetException.SQLException(ex);
          else
            throw ex2;
        }
      }

     // If we were unable to connect, an exception
     // would have been thrown.  So, if we get here,
     // we are successfully connected to the URL

     // Check for, and display and warnings generated
     // by the connect.

     try {
       checkForWarning(connection.getWarnings());
     }
     catch (SQLException ex){
       DataSetException.SQLException(ex);
     }
//!Diagnostic.println("Opening connection autocommit "+connection.getAutoCommit());
//!Diagnostic.println("Opening connection autoclose "+connection.getAutoClose());
//!Diagnostic.println(" "+getMetaData().supportsOpenStatementsAcrossCommit());
//!Diagnostic.println("setting isolation level start");
     if (setIsolationLevel)
       setTransactionIsolation(transactionIsolation);

     if (useDesignerConnectionCache && designTime) {
       DesignerConnectionCache.addConnection(getConnection(), connection);
       //!Diagnostic.println(" created new entry in connection cache for: " + connection);
     }

     //connection.setAutoCommit(true);
//!Diagnostic.println("Opening after isolation setting autocommit "+connection.getAutoCommit());
//!Diagnostic.println("setting isolation level end");

     // Get the DatabaseMetaData object and display
     // some information about the connection

     //DatabaseMetaData dma = connection.getMetaData ();

     //!Diagnostic.println("\nConnected to InterClient Beta (version number not available)");
     //!Diagnostic.println("\nConnected to " + dma.getURL());
     //!Diagnostic.println("Driver       "   + dma.getDriverName());
     //!Diagnostic.println("Version      "   + dma.getDriverVersion());
     //!Diagnostic.println("");
    }
  }
  //!RC 1/9/98
  // Puts the given extended property into the list, but only
  // if that property is not already defined and nonnull.
  // Used for username and password
  private void putExtendedProperty(Properties properties, String key, String value) {
    //!System.err.println("putExtendedProperty(" + key + ", " + value + ")");
    if (value == null || value.length() == 0)
      return;

    if (properties.containsKey(key)) {
      Object o = properties.get(key);
      //!System.err.println(" list already contains key " + key + " associated with value " + o);
      if (o != null && o instanceof String && ((String)o).length() > 0) {
        //!System.err.println(" so not changing it");
        return;
      }
    }
    //!System.err.println(" putting prop " + key + " with value " + value);
    properties.put(key, value);
  }


  private final boolean checkForWarning(SQLWarning warning)
    /*-throws DataSetException-*/
  {

    // Note that there could be multiple warnings chained together
    //

    if (warning != null) {
//!      try {
        DiagnosticJLimo.println("\n *** Warning ***\n");
        while (warning != null) {
          DiagnosticJLimo.println("SQLState: " + warning.getSQLState());
          DiagnosticJLimo.println("Message:  " + warning.getMessage());
          DiagnosticJLimo.println("Vendor:   " + warning.getErrorCode());
          DiagnosticJLimo.println("");
          warning = warning.getNextWarning();
        }
//!      }
//!      catch (SQLException ex){
//!        DataSetException.SQLException(ex);
//!      }
      return true;
    }
    return false;
  }



//!  /**
//!   * @ deprecated Use java.sql.Connection.TRANSACTION_NONE
//!   */
//!  public static final int NoTransactionSupport  = Connection.TRANSACTION_NONE;
//!
//!  /**
//!   * @ deprecated Use java.sql.Connection.TRANSACTION_READ_UNCOMMITTED
//!   */
//!//!  public static final int ReadDirty  = Connection.TRANSACTION_READ_UNCOMMITTED;
//!
//!  /**
//!   * @ deprecated Use java.sql.Connection.TRANSACTION_READ_COMMITTED
//!   */
//!//!  public static final int ReadCommitted = Connection.TRANSACTION_READ_COMMITTED;
//!
//!  /**
//!   * @ deprecated Use java.sql.Connection.TRANSACTION_REPEATABLE_READ
//!   */
//!//!  public static final int ReadRepeatable = Connection.TRANSACTION_REPEATABLE_READ;
//!
//!  /**
//!   * @ deprecated Use java.sql.Connection.TRANSACTION_SERIALIZABLE
//!   */
//!  public static final int ReadSerializable = Connection.TRANSACTION_SERIALIZABLE;

  /**
   * You can call this method to try to change the transaction
   * isolation level on a newly opened connection.
   *
   * <P><B>Note:</B> setTransactionIsolation cannot be called while
   * in the middle of a transaction.
   *
   * @param level one of the TRANSACTION_* isolation values with the
   * exception of TRANSACTION_NONE; some databases may not support
   * other values. If a database doesn't support the requested isolation
   * level, we will keep trying with higher (more restricted) isolation
   * levels until either one is found that is supported, or an exception
   * will be thrown (DataSetException.TransactionIsolationLevelNotSupported).
   *
   * @see DatabaseMetaData#supportsTransactionIsolationLevel
   */
  public synchronized final void setTransactionIsolation(int level)
      /*-throws DataSetException-*/
  {

    try {
      if (connection == null) {
        setIsolationLevel = true;
        transactionIsolation  = level;
      }
      else
      if (transactionIsolation != level) {

        checkConnectionChange();

        // Does this driver support transactions?
        if (getMetaData().supportsTransactions()){
          // Does this driver support this specific isol. level
          if (getMetaData().supportsTransactionIsolationLevel(level)) {
            connection.setTransactionIsolation(level);
            transactionIsolation  = level;
            setIsolationLevel = false;
            connectionChanged(false);
          }
          else {
            // Requested isolation level not supported, step up to
            // next isolation level.
            switch (level) {
              case Connection.TRANSACTION_READ_UNCOMMITTED:
                level = Connection.TRANSACTION_READ_COMMITTED;
                break;
              case Connection.TRANSACTION_READ_COMMITTED:
                level = Connection.TRANSACTION_REPEATABLE_READ;
                break;
              case Connection.TRANSACTION_REPEATABLE_READ:
                level = Connection.TRANSACTION_SERIALIZABLE;
                break;
              case Connection.TRANSACTION_SERIALIZABLE:
                DataSetException.transactionIsolationLevelNotSupported();
  //!            break;
              case Connection.TRANSACTION_NONE:
                transactionIsolation = Connection.TRANSACTION_NONE;
                return;
            }
            // Recurse trying to find an acceptable isolation level.
            setTransactionIsolation(level);
          }
        }
        else {
          transactionIsolation = Connection.TRANSACTION_NONE;
          setIsolationLevel = false;
        }
      }
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  /**
   * Get this Connection's current transaction isolation mode
   *
   * @return the current mode value
   */
  public final int getTransactionIsolation() /*-throws DataSetException-*/ {
    if (connection != null) {
      try {
        DiagnosticJLimo.check(transactionIsolation == connection.getTransactionIsolation());
        return connection.getTransactionIsolation();
      }
      catch (SQLException ex){
        DataSetException.SQLException(ex);
      }
    }
    return transactionIsolation;       //!RC was -1 on 5/13/97 -- confused VCD;
  }

  public final int getMaxStatements() /*-throws DataSetException-*/ {
    if (connection == null)
      return 0;
    else
      return getRuntimeMetaData().getIntValue(RuntimeMetaData.INT_MAXSTATEMENTS);
  }

  public synchronized final Statement createStatement()
    /*-throws DataSetException-*/
  {
    openConnection();
    try {
      return connection.createStatement();
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
    return null;
  }

  public synchronized final PreparedStatement createPreparedStatement(String query)
   /*-throws DataSetException-*/
  {
    openConnection();
    try {
      return connection.prepareStatement(query);
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
    return null;
  }

  public synchronized final CallableStatement createCallableStatement(String query)
    /*-throws DataSetException-*/
  {
    openConnection();
    try {
      return connection.prepareCall(query);
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
    return null;
  }

  public synchronized final DatabaseMetaData getMetaData() /*-throws DataSetException-*/ {
    return getRuntimeMetaData().getJdbcMetaData();
  }

  public synchronized final void setRuntimeMetaData(RuntimeMetaData runtimeMeta) {
    this.runtimeMeta = runtimeMeta;
  }

  public synchronized final RuntimeMetaData getRuntimeMetaData() {
    if (runtimeMeta == null)
      runtimeMeta = new RuntimeMetaData(this);
    return runtimeMeta;
  }

  /** Sets Connection's autoCommit state to enable if it differs from the
      cached state of this Database component.
  */
  public final void setAutoCommit(boolean enable)
    /*-throws DataSetException-*/
  {
    openConnection();
    try {
      connection.setAutoCommit(enable);
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
    this.autoCommit = enable;
  }

  /** returns cached state of Connections autocommit state.
  */
  public final boolean getAutoCommit()
    /*-throws DataSetException-*/
  {
    openConnection();
    return autoCommit;
  }


  /** @deprecated
      Use setAutoCommit();
  */
  public void start()
    /*-throws DataSetException-*/
  {
      setAutoCommit(false);

    //!TODO: Research
    //! Note: What do we do here if a transaction is already active? Probably best to
    //!       throw some kind of DataSetException. If the user wants explict control
    //!       over transactions, force them to wire the events.
    //!
    //!       Additional research item:
    //!       If somebody specified an isolation level, that will start a transaction
    //!       also. If so, we need to deal with that here somehow. Research should tie
    //!       into prior research issue.
    //!
  }

  // implementation of method in TransactionSupport interface
  public void commit()
    /*-throws DataSetException-*/
  {
    try {
      if (connection != null)
        connection.commit();
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  // implementation of method in TransactionSupport interface
  public void rollback()
    /*-throws DataSetException-*/
  {
    try {
      if (connection != null)
        connection.rollback();
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  private final ResolutionManager createResolutionManager(boolean doTransactions, boolean postEdits, boolean resetPendingStatus) {
    SQLResolutionManager resolutionManager = new SQLResolutionManager();
    resolutionManager.setDatabase(this);
    resolutionManager.setDoTransactions(doTransactions);
    resolutionManager.setPostEdits(postEdits);
    resolutionManager.setResetPendingStatus(resetPendingStatus);
    return resolutionManager;
  }

  /** Saves dataSet changes to the database.
  */
  public final void saveChanges(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    createResolutionManager(isUseTransactions(),true,true).saveChanges(dataSet);
  }

  /** Saves one or more dataSet changes to the database.
  */
  public final void saveChanges(DataSet[] dataSets)
    /*-throws DataSetException-*/
  {
    saveChanges(dataSets,isUseTransactions());
  }

  /** Saves one or more dataSet changes to the database.
  */
  public final void saveChanges(DataSet[] dataSets, boolean doTransactions)
    /*-throws DataSetException-*/
  {
    createResolutionManager(doTransactions,true,true).saveChanges(dataSets);
  }

  /**
      @since JB2.0
      Saves one or more dataSet changes to the database.
      @param dataSets            Array of dataSets to save changes for.
      @param doTransactions      true:  All changes will be in 1 transaction. (default)
                                 false: No transactions calls will be made.
      @param postEdits           true:  All edits are posted before changes are saved. (default)
                                 false: No edits are saved.
      @param resetPendingStatus: true:  Status bits are reset after changes are saved. (default)
                                 false: Status bits are left in pending state.
  */
  public final void saveChanges(DataSet[] dataSets, boolean doTransactions, boolean postEdits, boolean resetPendingStatus)
    /*-throws DataSetException-*/
  {
    createResolutionManager(doTransactions,postEdits,resetPendingStatus).saveChanges(dataSets);
  }

  /**
      @since JB2.0
      Resets the status bits after 1 or more saveChanges calls.
      @param markResolved   true:  if changes completed succesfully.
                            false: if changes were rolled back.
  */
  public void resetPendingStatus(DataSet[] dataSets, boolean markResolved)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < dataSets.length; ++index)
      dataSets[index].resetPendingStatus(markResolved);
  }

//!ChrisO todo: remove this code if decision is that there is no "override" for updatemode.
//!  /** Saves a dataSet to the  with a specific updateMode.
//!  */
//!  public final void saveChanges(DataSet dataSet, int updateMode)
//!    /*-throws DataSetException-*/
//!  {
//!    createResolutionManager(updateMode).saveChanges(dataSet);
//!  }
//
//
//!  /** Saves one or more dataSets to the database with a specific updateMode.
//!  */
//!  public final void saveChanges(DataSet[] dataSets, int updateMode)
//!    /*-throws DataSetException-*/
//!  {
//!    createResolutionManager(updateMode).saveChanges(dataSets);
//!  }

  // This constructs a StorageDataSet from scratch and fills from JDBC resultSet
  public StorageDataSet resultSetToDataSet(ResultSet result)
    /*-throws DataSetException-*/
  {
    return resultSetToDataSet(new TableDataSet(), result);
  }

  public StorageDataSet resultSetToDataSet(StorageDataSet dataSet, ResultSet result)
    /*-throws DataSetException-*/
  {
    new QueryProvider().resultSetToDataSet(this, dataSet,  result);
    return dataSet;
  }

  public int executeStatement(String statementString) /*-throws DataSetException-*/ {
    Statement statement = null;
    try {
      Connection connection = getJdbcConnection();
      statement = connection.createStatement();
      statement.executeUpdate(statementString);
      int updateCount = statement.getUpdateCount();
      statement.close();
      return updateCount;
    }
    catch (SQLException ex) {
      if (statement !=  null) {
        try {
            statement.close();
        }
        catch(SQLException ex2) {
          DataSetException.SQLException(ex2);
        }
      }
      DataSetException.SQLException(ex);
      return -1;
    }
  }

  protected void finalize() throws Throwable {
    if (!connectionProvided)
      closeConnection();
  }

//       This property controls if metadata inquiries should be schemaName qualified in
//       all circumstances i.e. use the username if a schemaName wasn't specified.
//!RC JOAL -- I changed the spelling to be Java compliant
  public boolean isUseSchemaName() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_SCHEMANAME); }
  public void setUseSchemaName(boolean useSchemaName) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_SCHEMANAME,useSchemaName); }


//       This property controls if the table name should be prepended to quoted fieldnames
//       in generated queries. The reason for doing so is to trick certain query parsers
//       (BDE local SQL) to recognize them as fields.
//
  public boolean isUseTableName() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_TABLENAME); }
  public void setUseTableName(boolean useTableName) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_TABLENAME,useTableName); }


//       This property controls if a CHAR field should be space padded or not.
//       The reason for doing so is to get around certain database driver bugs.
//       (Oracle type 4 driver)
//
  public boolean isUseSpacePadding() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_SPACEPADDING); }
  public void setUseSpacePadding(boolean useSpacePadding) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_SPACEPADDING,useSpacePadding); }


//       This property controls if the jdbc statements should be reused.
//       The reason for doing so is to get around certain database driver bugs.
//       (Ingress with ?? driver)
//
  public boolean isUseStatementCaching() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_STATEMENTCACHING); }
  public void setUseStatementCaching(boolean useStatementCaching) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_STATEMENTCACHING,useStatementCaching); }


//       This property controls if Strings should be passed using the JDBC
//       setObject call instead of setString.
//
  public boolean isUseSetObjectForStrings() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_SETOBJECTFORSTRINGS); }
  public void setUseSetObjectForStrings(boolean useSetObjectForStrings) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_SETOBJECTFORSTRINGS,useSetObjectForStrings); }


//       This property controls if Binary Streams should be passed using the
//       JDBC setObject call instead of setString.
//
  public boolean isUseSetObjectForStreams() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_SETOBJECTFORSTREAMS); }
  public void setUseSetObjectForStreams(boolean useSetObjectForStreams) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_SETOBJECTFORSTREAMS,useSetObjectForStreams); }

  /**
       This property controls if saveChanges should use transactions.
       By default it will use transactions if the associated JDBC driver
       returns true for its implementation of DatabaseMetaData.supportsTransactions()
  */


  public boolean isUseTransactions() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_TRANSACTIONS); }
  public void setUseTransactions(boolean useTransactions) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_TRANSACTIONS,useTransactions); }

  /**
  */

  public boolean isReuseSaveStatements() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.REUSE_SAVE_STATEMENTS); }
  public void setReuseSaveStatements(boolean reuseSaveStatements) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.REUSE_SAVE_STATEMENTS,reuseSaveStatements); }

//  /**
//  */
//
//  public boolean isUseClearParameters() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_CLEAR_PARAMETERS); }
//  public void setUseClearParameters(boolean useClearParameters) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_CLEAR_PARAMETERS,useClearParameters); }

  //!  boolean isUseQuotesForIndexInfo() { return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_QUOTES_FOR_INDEX_INFO); }
//!  void setUseQuotesForIndexInfo(boolean useTransactions) { getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_QUOTES_FOR_INDEX_INFO,useTransactions); }


  /**
  * Tells whether the database connection is open
  */
  public boolean isOpen() {
    return (connection != null);
  }

  /**
    Specify SQL dialect that your server is based on.  This is not required,
    but may be useful.  See SQLDialect.
  */
  public final void setSQLDialect(int dialect) {
    getRuntimeMetaData().setIntValue(RuntimeMetaData.INT_SQLDIALECT,dialect);
  }

  public final int getSQLDialect() {
    return getRuntimeMetaData().getIntValue(RuntimeMetaData.INT_SQLDIALECT);
  }

  public final void addConnectionUpdateListener(ConnectionUpdateListener listener) {
    databaseListeners = EventMulticaster.add(databaseListeners, listener);
    if (connectionUpdateEvent == null)
      connectionUpdateEvent = new ConnectionUpdateEvent(this);
  }

  public final void removeConnectionUpdateListener(ConnectionUpdateListener listener) {
    databaseListeners = EventMulticaster.remove(databaseListeners, listener);
    if (databaseListeners == null)
      connectionUpdateEvent = null;
  }

  public char getIdentifierQuoteChar() {
    return (char)getRuntimeMetaData().getIntValue(RuntimeMetaData.INT_QUOTECHAR);
  }
  public void setIdentifierQuoteChar(char quoteChar) {
    getRuntimeMetaData().setIntValue(RuntimeMetaData.INT_QUOTECHAR, (int)quoteChar);
  }

  public boolean storesUpperCaseIdentifiers()
  {
    return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_UPPERCASEIDENTIFIERS);
  }
  public boolean storesLowerCaseIdentifiers()
  {
    return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_LOWERCASEIDENTIFIERS);
  }
  boolean storesLowerCaseQuotedIdentifiers()
  {
    return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_LOWERCASEQUOTEDIDS);
  }

  /**
    @since JB2.01
    Controls whether an unquoted identifier in SQL is treated case sensitive by
    the database. This would be true for Sybase and false for Oracle.
    The default value is based on a series of calls to the JDBC driver, which
    may not return correct values.
  */
  public boolean isUseCaseSensitiveId() {
    return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_CASESENSITIVEID);
  }
  public void setUseCaseSensitiveId(boolean caseSensitive) {
    getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_CASESENSITIVEID, caseSensitive);
  }

  /**
    @since JB2.01
    Controls whether an QUOTED identifier in SQL is treated case sensitive by
    the database. This would be true both Sybase and Oracle.
    The default value is based on a series of calls to the JDBC driver, which
    may not return correct values.
  */
  public boolean isUseCaseSensitiveQuotedId() {
    return getRuntimeMetaData().getBooleanValue(RuntimeMetaData.USE_CASESENSITIVEQUOTEDID);
  }
  public void setUseCaseSensitiveQuotedId(boolean caseSensitive) {
    getRuntimeMetaData().setBooleanValue(RuntimeMetaData.USE_CASESENSITIVEQUOTEDID, caseSensitive);
  }

  /**
  * @deprecated Use the default constructor and setJdbcConnection()
  */
  public Database(java.sql.Connection connection) {
    connectionProvided  = (connection != null);
    this.connection = connection;
  }

  /**
   * @deprecated Use getJdbcConnection()
   */
  public final java.sql.Connection jdbcConnection()
    /*-throws DataSetException-*/
  {
    return getJdbcConnection();
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    if (connectionProvided)
      stream.writeObject(connection);
    getRuntimeMetaData().writeMetaInfo(stream);  // Ugly, but we dont have to make MetaData Serializable
  }

  private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
    stream.defaultReadObject();
    if (connectionProvided)
      connection = (Connection)stream.readObject();
    getRuntimeMetaData().readMetaInfo(stream);   // Ugly, but we dont have to make MetaData Serializable
  }

  /**
   * Internal utility function which returns an identifier based on the catalogName,
   *  schemaName, and tableName passed to it
   * @param catalogName String
   * @param schemaName String
   * @param tableName String
   *
   */
  public String makeTableIdentifier(String catalogName, String schemaName, String tableName) {
    char quoteCharacter = getIdentifierQuoteChar();
    if (quoteCharacter != '\0' && !isUseTableName())
      tableName = quoteCharacter + tableName + quoteCharacter;
    if (schemaName != null && schemaName.length() > 0) {
      if (quoteCharacter != '\0')
        tableName = quoteCharacter + schemaName + quoteCharacter + "." + tableName;
      else
        tableName = schemaName + "." + tableName;
    }
    return tableName;
  }

          transient java.sql.Connection   connection;
  private           boolean               setIsolationLevel;
  private           int                   transactionIsolation;
  private transient EventMulticaster      databaseListeners;
  private transient ConnectionUpdateEvent connectionUpdateEvent;
  private           ConnectionDescriptor  connectionDescriptor;
  private           boolean               connectionProvided;

  private transient int                   connectionUseCount; // used when sharing connections during design
  private transient RuntimeMetaData       runtimeMeta;
  private boolean   autoCommit;
  private static    ClassLoader           classLoader;
  private           String                databaseName;
  private static    final long            serialVersionUID = 1L;
}

/*
    Method put in own class to eliminate j2ee.jar file dependency when
    Database class loaded in case conneciton not established with a
    DataSource databaseName.k
 */
class Naming {

  static final Connection getConnection(String databaseName) {
        try {
          javax.naming.Context ctx = new javax.naming.InitialContext();
          DataSource source = (DataSource)ctx.lookup(databaseName);
          return source.getConnection();
        }
        catch(Exception ex) {
          DataSetException.throwExceptionChain(ex);
        }
    return null;
  }
}


