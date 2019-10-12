//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/QueryDescriptor.java,v 7.0 2002/08/08 18:39:54 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.*;

/**
 *
 * <P>
 * The <CODE>QueryDescriptor</CODE> stores properties that set a query statement to run against a SQL database. To access SQL table data using a
 * {@link com.borland.dx.sql.dataset.QueryDataSet} <CODE>QueryDataSet</CODE></A>, the {@link com.borland.dx.sql.dataset.Database} <CODE>Database</CODE></A>
 * component and <CODE>QueryDescriptor</CODE> object are also required. The properties of the <CODE>QueryDescriptor</CODE> object are:
 *
 * <UL>
 * <LI>the <CODE>Database</CODE> object which is associated to a particular <CODE>userName</CODE> and <CODE>connectionURL</CODE> (required)
 * <LI>the query string to execute (required)
 * <LI>whether the query executes automatically when the <CODE>QueryDataSet</CODE> is opened.
 * <LI>whether to resource the query string to a separate <CODE>ResourceBundle</CODE>
 * <LI>how the data is loaded into the <CODE>QueryDataSet</CODE>
 * <LI>the list of query parameters
 * </UL>
 *
 *
 *
 * <P>
 * To work with this component programmatically, you set its properties when instantiating the <CODE>QueryDescriptor</CODE> object, or individually by its write
 * accessor methods. For properties that do not have a corresponding setter method, use a constructor that takes that property as a parameter.
 *
 * <!-- JDS start - remove property editor, user interface stuff -->
 * <P>
 * Through the user-interface, the information stored in the <CODE>QueryDescriptor</CODE> can be accessed by inspecting the <CODE>query</CODE> property of a
 * <CODE>QueryDataSet</CODE> object. This displays the Query property editor. The Query property editor also provides the additional functionality of
 * <UL>
 * <LI>
 * <P>
 * A Browse Tables button which assists in table discovery. Clicking this button displays a dialog listing available tables in the database (as specified by the
 * <CODE>Database</CODE> property). When you select a table from this dialog, its columns are listed as well. Buttons allow you to easily paste the table or
 * column name into the query string to create the query statement to run against the server database.
 *
 * <LI>
 * <P>
 * A SQL Builder button that offers UI assistance with the query statement creation.
 *
 *
 * <LI>
 * <P>
 * A Test Query button which allows you to explicitly test the query properties from within the Query property editor. A "Success" or "Fail" message displays in
 * the gray area beneath this button as applicable.
 *
 * </UL>
 * <!-- JDS end -->
 *
 * <!-- JDS start - remove paragraph -->
 * <P>
 * Query statements in the Query property editor display with any line breaks you've entered. When JBuilder generates source code for your query, it preserves
 * the newlines so that the query looks the same when you redisplay it later in query editor. In source, the newlines are stored as "\n", which are removed when
 * the query is passed to database for execution as some servers do not support this. <!-- JDS end -->
 *
 * <A NAME="loading_data"></A>
 * <H3>Loading data</H3>
 *
 * 
 * 
 * <P>
 * Data can be loaded all in one fetch, as needed, asynchronously or one at a time (as specified by the <CODE>loadOption</CODE> property). When working with
 * asynchronous queries, opening the <CODE>QueryDataSet</CODE> then immediately calling methods such as <CODE>rowCount()</CODE> typically returns a row count
 * lower than expected. To avoid this, either set the query to synchronous, listen for the <CODE>LoadingEvent</CODE>, perform other actions while the
 * <CODE>QueryDataSet</CODE> completes loading, or listen for updates to the row count.
 *
 * <P>
 * As an asynchronous query fetches rows of data, they are appended to the end of the <CODE>DataSet</CODE>. When working with a sorted view of the
 * <CODE>DataSet</CODE>, the new rows appear in the specified sort order. Also, be careful to not make assumptions about the current row position since rows are
 * inserted into the sorted view as they are fetched, thereby changing row positions automatically.
 *
 * <A NAME="executing_queries"></A>
 * <H3>Executing queries</H3>
 *
 *
 * 
 * <P>
 * When executing queries, you have several options from which to choose, depending on your query statement. If your statement returns a <CODE>ResultSet</CODE>,
 * use a <CODE>QueryDataSet</CODE> component with a <CODE>QueryDescriptor</CODE>. For example, the statement
 * 
 * <PRE>
 * <CODE>     select * from customer</CODE>
 * </PRE>
 *
 * If you also require the addition of parameter passing (all named or all unnamed), assign these values to an object derived from <CODE>ReadWriteRow</CODE>
 * (for example, a <CODE>ParameterRow</CODE>) in conjunction with the <CODE>QueryDataSet</CODE>.
 *
 * <P>
 * Do not use a <CODE>QueryDataSet</CODE> if your SQL statement doesn't yield a <CODE>ResultSet</CODE>. Instead, use one of the following:
 * <P>
 * <UL>
 * <LI>If your statement doesn't require parameter passing use the {@link com.borland.dx.sql.dataset.Database} <CODE>Database.executeStatement(...)</CODE></A>
 * method.
 * <LI>
 * <P>
 * To execute a SQL statement with parameters, use the {@link com.borland.dx.sql.dataset.QueryProvider} <CODE>QueryProvider.executeStatement(...)</CODE></A>
 * method.
 * <LI>
 * <P>
 * To execute a statement that has output parameters, use the {@link com.borland.dx.sql.dataset.ProcedureProvider}
 * <CODE>ProcedureProvider.callProcedure(...)</CODE></A> method.
 *
 * </UL>
 *
 *
 * <!-- JDS start - remove section -->
 *
 * <A NAME="resourceableSQL"></A>
 * <H3>Resourceable SQL</H3>
 *
 * <P>
 * Through the Query property editor, the SQL statement entered may be optionally resourced into a separate file. This provides a logical separation between the
 * code which uses the SQL statement and the contents of that statement. This allows a developer to change the SQL statement inside the resourced file without
 * needing to recompile the code which uses the SQL. Once a SQL statement has been resourced, any future changes to it in the JBuilder design tools will modify
 * the resource file, not the source file referring to that SQL string. This property appears only in the Query property editor (as the Resource SQL String
 * checkbox) and has no programmatic counterpart since its selection state merely affects the structure of the code that is automatically generated by the UI
 * designer tools. For more information on this option, see Place SQL text in resource bundle from the
 * {@link com.borland.jbuilder.cmt.editors.QueryDescriptorEditor} Query property editor</A> F1 help topic. <!-- JDS end -->
 *
 *
 *
 *
 * <A NAME="Oracle"></A>
 * <H3>Oracle synonyms</H3>
 *
 * <P>
 * If a query is run against a synonym on an Oracle server, it is dependent on the support of synonyms in the JDBC driver to determine whether the query is
 * updatable
 */
public class QueryDescriptor implements java.io.Serializable {
	/**
	*/
	public QueryDescriptor(Database database, String query) {
		this(database, query, true);
	}

	/**
	 * Constructs a QueryDescriptor object with the following properties:
	 * 
	 * @param database
	 *          Database
	 * @param query
	 *          String
	 * @param executeOnOpen
	 *          boolean
	 */
	public QueryDescriptor(Database database, String query, boolean executeOnOpen) {
		this(database, query, null, executeOnOpen, Load.ALL);
	}

	/**
	 * Constructs a QueryDescriptor object with the following properties:
	 * 
	 * @param database
	 *          DataBase
	 * @param query
	 *          String
	 * @param parameters
	 *          ReadWriteRow
	 * @param executeOnOpen
	 *          boolean
	 */
	public QueryDescriptor(Database database, String query, ReadWriteRow parameters, boolean executeOnOpen) {
		this(database, query, parameters, executeOnOpen, Load.ALL);
	}

	/**
	 * Constructs a QueryDescriptor object with the following properties:
	 * 
	 * @param database
	 *          Database
	 * @param query
	 *          String
	 * @param parameters
	 *          ReadWriteRow
	 */
	public QueryDescriptor(Database database, String query, ReadWriteRow parameters) {
		this(database, query, parameters, true, Load.ALL);
	}

	/**
	 * @deprecated. Use QueryDescriptor(Database,String,ReadWriteRow,boolean,int)
	 */
	public QueryDescriptor(Database database, String query, ReadWriteRow parameters, boolean executeOnOpen, boolean asynchronousExecution) {
		this(database, query, parameters, executeOnOpen, asynchronousExecution ? Load.ASYNCHRONOUS : Load.ALL);
	}

	/**
	 *
	 * @param database
	 *          DataBase
	 * @param query
	 *          String
	 * @param parameters
	 *          ReadWriteRow
	 * @param executeOnOpen
	 *          boolean
	 * @param loadOption
	 *          integer
	 */

	public QueryDescriptor(	Database database,
													String query,
													ReadWriteRow parameters,
													boolean executeOnOpen,
													int loadOption) {
		DiagnosticJLimo.trace(Trace.QueryDescriptor, "QueryDescriptor(" + database + ", " + query + ", " + parameters + ")");
		this.database = database;
		// !ktien: bug 13468, stop stripping the line-ends.
		// ! Callers that need to strip the line-ends will need to
		// ! call getStrippedQueryString().
		// setQueryText(query);
		this.query = query; // store the query as-is.
		strippedQuery = stripQueryText(query); // store stripped form
		paramRow = parameters;
		this.executeOnOpen = executeOnOpen;
		this.loadOption = loadOption;
	}

	public Database getDatabase() {
		return database;
	}

	/**
	 * LimoSys feature to replace bad database connection (e.g. after computer in sleep mode)
	 */
	public void switchConnection(Database db) {
		this.database = db;
	}

	// !ktien: this method used to return the query with
	// line-ends stripped. It now returns them as-is.
	// To get the stripped version, call getStrippedQueryString().
	public String getQueryString() {
		return query;
	}

	String getStrippedQueryString() {
		return strippedQuery;
	}

	void setParameterRow(ReadWriteRow paramRow) {
		this.paramRow = paramRow;
	}

	/**
	 * The ParameterRow property of the QueryDescriptor is mutable. A call to QueryDataSet.changeParameters will change the QueryDescriptor's ParameterRow
	 * property.
	 */
	public ReadWriteRow getParameterRow() {
		return paramRow;
	}

	// ! /**
	// ! * This method strips out characters which would choke a server (such as \r\n)
	// ! * !ktien: Changed from setQueryText(). This now *returns* the stripped
	// ! * ! text, instead of setting the query field of this
	// ! * ! QueryDescriptor object.
	// ! */
	// ! private void setQueryText(String s) {
	static String stripQueryText(String s) {
		// ! System.err.println("Setting query text to:\r\n" + s);
		char c;
		if (s == null)
			// query = null;
			return null;
		else {
			FastStringBuffer fsb = new FastStringBuffer(s);
			for (int i = 0; i < fsb.length(); ++i) {
				c = fsb.charAt(i);
				if (c == '\r' || c == '\n') {
					fsb.setCharAt(i, ' ');
					continue;
				}
				if (c == '\\') {
					if (fsb.length() <= (i + 1)) {
						fsb.removeCharsAt(i, 1);
						continue;
					}
					if (fsb.charAt(i + 1) == 'r' || fsb.charAt(i + 1) == 'n') { // NORES
						fsb.removeCharsAt(i, 2);
						fsb.insert(i, " ");
					}
				}
			}
			// ! query = fsb.toString();
			return fsb.toString();
			// ! Diagnostic.println("QueryDataSet using \"" + query + "\"");
		}
		// ! System.err.println("Query text follows:\r\n" + query);
	}

	public void setExecuteOnOpen(boolean executeOnOpen) {
		this.executeOnOpen = executeOnOpen;
	}

	public boolean isExecuteOnOpen() {
		return executeOnOpen;
	}

	/**
	 * @deprecated. Use getLoadOption
	 */
	public final boolean isAsynchronousExecution() {
		return (loadOption == Load.ASYNCHRONOUS);
	}

	/**
	 * @deprecated. Use setLoadOption
	 */
	public final void setAsynchronousExecution(boolean async) {
		loadOption = async ? Load.ASYNCHRONOUS : Load.ALL;
	}

	public final int getLoadOption() {
		return loadOption;
	}

	public final void setLoadOption(int loadOption) {
		this.loadOption = loadOption;
	}

	public String toString() {
		return "QueryDescriptor: \"" + query + "\" using database " + database + " and parameters " + paramRow // NORES
				+ ", executeOnOpen = " + executeOnOpen + ", loadOption = " + loadOption; // NORES
	}

	private Database database;
	private String query;
	private String strippedQuery;
	private ReadWriteRow paramRow;
	private boolean executeOnOpen;
	private int loadOption;
	private static final long serialVersionUID = 1L;
}
