//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/util/Attic/DataSourceSet.java,v 1.18.2.2 2004/10/15 19:54:12 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sql.DataSource;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnChangeAdapter;
import com.borland.dx.dataset.ColumnChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.PickListDescriptor;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.Variant;
import com.borland.dx.util.cons.DataSourceProps;
import com.borland.jb.util.DiagnosticJLimo;

/**
 *
 * <p>
 * Title:
 * </p>
 * <p>
 * Manager for loading, storing and accessing a working set of JDBC DatSource objects:
 * </p>
 *
 * <p>
 * The working set of DataSources is loaded from a {@link java.util.Properties Properties} <CODE>Object</CODE>. serveral styles of property settings for JDBC
 * DataSource definitions are supported.
 * 
 * When used with other DataSource repositories like JNDI, ODBC, and Oracle's tsnames, the {link DataSourceSet DataSourceSet} can be used to expose just all or
 * part of the DataSources available in those repositories. The DataSourceSet can also be used to associate tool properties with a particular data source such
 * as an url pattern or a boolean that indicates whether JDBC extended properties should be used. Tool properties are properties that are not needed to make a
 * connection when calling {@link javax.sql.DataSource#getConnection DataSource.getConnection} method.
 * </p>
 * <p>
 * The {@link #load(Properties) load(Properties)} and {@link #store(Properties) store(Properties)} provide persistance to and from a Properties objects for the
 * DataSource instances managed by a DataSourceSet. The load and store methods manage the persistance of three categories of properties for a DataSource:
 *
 * <p>
 * DataSource properties are stored and loaded from the set of methods on the DataSource implementation that have public accessor methods that begin with "get"
 * and return a value of int, boolean or String. These accessor methods must not have any arguments and there must also be a setter method that has a single
 * argument with the same type as the return type of the get method.
 * </p>
 *
 * <p>
 * Extended properties are stored and loaded from the Properties object returned from the {@link #getProperties() getProperties()} method. Note these are only
 * processed if the DataSource class exposes such a method.
 * </p>
 *
 * <p>
 * URL properties are stored and loaded from the Properties object returned from the {@link #getUrlProperties() getUrlProperties()} method. Note these are only
 * processed if the DataSource class exposes such a method. The {@link com.borland.javax.sql.JdbcDataSource JdbcDataSource} DataSource implementation uses this
 * Properties objects to process substitution variables in its {@link com.borland.javax.sql.DataSourceProperties#getUrl() JdbcDataSource.getUrl()} property
 * setting.
 * </p>
 *
 * <p>
 * If the DataSource needs to be loaded from an external repository such as JNDI, ODBC, or Oracl tsnames, an additional class that implements
 * {@link DataSourceRepository DataSourceRepository} must be specified when adding the DataSource to the DataSourceSet. See the {@link #addDataSource
 * addDataSource} method.
 * </p>
 *
 * Here is a list of current implementations for DataSourceRepository:
 * 
 * <pre>
 * {@link MemoryDataSourceRepository MemoryDataSourceRepository}
 * {@link OdbcDataSourceRepository OdbcDataSourceRepository}
 *
 * </pre>
 *
 */

public class DataSourceSet {

	public DataSourceSet() {
		// cache of instantiated DataSource instances. May only contain a
		// subset of DataSources in this set. Can also load/store
		// DataStoreInstances of this set into Properties object.
		//
		dataSources = new MemoryDataSourceRepository();
		// Unlike "dataSources", contains properties for "all" DataSources managed
		// by this set.
		//
		dataSourceProps = new Hashtable();

		// Set of repositories used by the DataSources in this set. Keyed by
		// DataSource name.
		//
		repositories = new Hashtable();
		repositories.put(dataSources.getClass().getName(), dataSources);

		validProps = new String[] {
				DataSourceProps.TYPE_NAME,
				DataSourceProps.NAME,
				DataSourceProps.DATASOURCE_CLASS_NAME,
				DataSourceProps.DATASOURCE_PROP,
				DataSourceProps.EXTENDED_PROP,
				DataSourceProps.URL_PROP,
				DataSourceProps.REPOSITORY_CLASS_NAME
		};
	}

	private void verify(Properties props, String prefix) {
		Enumeration keys;
		String key;
		String className;
		String databaseName;

		className = props.getProperty(DataSourceProps.DATASOURCE_CLASS_NAME);
		if (className == null)
			throw new DataSourceException(Res.bundle.format(ResIndex.MustSetProp, DataSourceProps.DATASOURCE_CLASS_NAME));
		keys = props.keys();
		while (keys.hasMoreElements()) {
			key = keys.nextElement().toString();
			validateNewKey(prefix, key);
		}
	}

	private final void ioException(IOException ex) {
		throw new DataSourceException(ex);
	}

	public void store(Properties props) {
		Hashtable table = dataSourceProps;
		Enumeration enum1 = table.keys();
		DataSource dataSource;
		String name;
		int index = 0;
		String prefix;
		DataSourceRepository repository;
		while (enum1.hasMoreElements()) {
			name = enum1.nextElement().toString();
			Properties storeProps = (Properties) dataSourceProps.get(name);
			prefix = DataSourceProps.DATASOURCE_PREFIX + '.' + index + '.';

			props.put(prefix + DataSourceProps.NAME, name);
			String typeName = storeProps.getProperty(DataSourceProps.TYPE_NAME);
			if (typeName != null)
				props.put(prefix + DataSourceProps.TYPE_NAME, typeName);

			props.put(prefix + DataSourceProps.DATASOURCE_CLASS_NAME, storeProps.getProperty(DataSourceProps.REPOSITORY_CLASS_NAME));
			props.put(prefix + DataSourceProps.REPOSITORY_CLASS_NAME, storeProps.getProperty(DataSourceProps.REPOSITORY_CLASS_NAME));

			/*
			 * Steve: should not need to sync. To update, drop and then add data sources.
			 * 
			 * Properties dsProps = new Properties(); //sync up repository.load( dataSource, storeProps ); repository.store(dataSource, dsProps);
			 * DataSourceSet.copyProps(dsProps, props, prefix);
			 */
			DataSourceSet.copyProps(storeProps, props, prefix);

			++index;
		}
	}

	static final void copyProps(Properties source, Properties dest, String prefix) {
		if (source != null) {
			Enumeration enum1 = source.keys();
			String key;
			while (enum1.hasMoreElements()) {
				key = enum1.nextElement().toString();
				/*
				 * Steve: Should not be needed anymore // dest.put(prefix+(prefix.endsWith(".")?"":key.startsWith(".")?"":".")+key, source.getProperty(key)); This is
				 * more readable if its needed... if (!prefix.endsWith(".") && !key.startsWith(".")) key = "."+key;
				 */
				DiagnosticJLimo.check(prefix.endsWith(".") || key.startsWith("."));
				dest.put(prefix + key, source.getProperty(key));
			}
		}
	}

	/**
	 * Load DataSources from a file.
	 * 
	 * @param fileName
	 *          file to load DataSources from.
	 */

	public void load(String fileName) {
		try {
			load(new FileInputStream(fileName));
		} catch (IOException ex) {
			ioException(ex);
		}
	}

	/**
	 * load DataSources from an InputStream.
	 * 
	 * @param in
	 *          InputStream to load DataSources from.
	 */

	public void load(InputStream in) {
		Properties props = new Properties();

		try {
			props.load(in);
		} catch (IOException ex) {
			ioException(ex);
		}
		load(props);
	}

	private final void validateNewKey(String prefix, String key) {
		boolean found = false;
		for (int index = 0; index < validProps.length; ++index) {
			if (key.startsWith(validProps[index]))
				return;
		}
		StringBuffer buf = new StringBuffer();
		for (int index = 0; index < validProps.length; ++index) {
			buf.append(validProps[index]);
			buf.append(' ');
		}
		throw new DataSourceException(Res.bundle.format(ResIndex.InvalidStart, prefix + key, buf.toString()));
	}

	private final Object loadClass(String name) {
		try {
			return Class.forName(name).newInstance();
		} catch (Exception ex) {
			DataSourceException.throwExceptionChain(ex);
		}
		return null;
	}

	/**
	 * load DataSources from a Properties object.
	 * 
	 * @param props
	 *          Properties object to load from.
	 */
	public void load(Properties props) {
		Hashtable dataSourcePrefixes = new Hashtable();

		Enumeration enum1 = props.keys();
		String key;
		String prefix;
		int index;
		prefix = "";
		while (enum1.hasMoreElements()) {
			key = enum1.nextElement().toString();
			if (key.startsWith(DataSourceProps.DATASOURCE_PREFIX)) {
				index = key.lastIndexOf(DataSourceProps.EXTENDED_PROP);
				if (index < 0) {
					index = key.lastIndexOf(DataSourceProps.URL_PROP);
					if (index < 0)
						index = key.lastIndexOf(DataSourceProps.DATASOURCE_PROP);
				}

				if (index > 0)
					prefix = key.substring(0, index);
				else if (key.endsWith(DataSourceProps.DATASOURCE_CLASS_NAME))
					prefix = key.substring(0, key.lastIndexOf(DataSourceProps.DATASOURCE_CLASS_NAME));
				else if (key.endsWith(DataSourceProps.REPOSITORY_CLASS_NAME))
					prefix = key.substring(0, key.lastIndexOf(DataSourceProps.REPOSITORY_CLASS_NAME));
				else if (key.endsWith(DataSourceProps.NAME))
					prefix = key.substring(0, key.lastIndexOf(DataSourceProps.NAME));
				else if (key.endsWith(DataSourceProps.TYPE_NAME))
					prefix = key.substring(0, key.lastIndexOf(DataSourceProps.TYPE_NAME));
				else
					prefix = null;
				if (prefix != null) {
					Properties prefixProps = (Properties) dataSourcePrefixes.get(prefix);
					if (prefixProps == null) {
						prefixProps = new Properties();
						dataSourcePrefixes.put(prefix, prefixProps);
					}
					prefixProps.put(key.substring(prefix.length(), key.length()), props.getProperty(key));
				}
			}
		}

		enum1 = dataSourcePrefixes.keys();
		Enumeration propKeys;
		String propKey;
		String name;
		String prefixKey;
		DataSource dataSource;
		while (enum1.hasMoreElements()) {
			prefixKey = enum1.nextElement().toString();
			// Diagnostic.println("key: "+prefixKey);
			props = (Properties) dataSourcePrefixes.get(prefixKey);
			name = getRequiredProperty(props, prefix, DataSourceProps.NAME);
			verify(props, prefix);
			repositories.put(name, getRepository(props));
			dataSourceProps.put(name, props);
		}
	}

	private final DataSourceRepository getRepository(Properties props) {
		String repositoryClassName = props.getProperty(DataSourceProps.REPOSITORY_CLASS_NAME);
		DataSourceRepository repository = null;
		if (repositoryClassName != null) {
			repository = (DataSourceRepository) repositories.get(repositoryClassName);
			if (repository == null) {
				repository = (DataSourceRepository) loadClass(repositoryClassName);
				repositories.put(repositoryClassName, repository);
			}
		}
		if (repository == null)
			return dataSources;
		return repository;
	}

	private final String getRequiredProperty(Properties props, String prefix, String key) {
		String value = props.getProperty(key);
		if (value == null)
			throw new DataSourceException(Res.bundle.format(ResIndex.MustSetProp, prefix + key));
		return value;
	}

	/**
	 * Obtain a Connection for a DataSource for a given name.
	 * 
	 * @param name
	 *          of the DataSource to obtain a Connection Object for.
	 * @return a java.sql.Connection Object
	 * @throws java.sql.SQLException
	 */
	public Connection getConnection(String name)
			throws java.sql.SQLException {
		return getDataSource(name).getConnection();
	}

	/**
	 * Obtabin a DataSource instance for a given name
	 * 
	 * @param name
	 *          of the DataSource to obtain a DataSource Object for.
	 * @return DataSource instance for name.
	 */
	public DataSource getDataSource(String name) {
		/*
		 * Steve: syncing should be done when written to, not when read from.
		 * 
		 * DataSource result = setRepository.getDataSource(name); if( result != null ){ // SYNC UP Properties p = ( Properties ) repositoryProps.get( name ); ( (
		 * DataSourceRepository ) repositories.get( name ) ).load( result, p ); } return result;
		 */
		DataSource dataSource = dataSources.getDataSource(name);
		if (dataSource == null) {
			Properties props = getDataSourceProperties(name);
			if (props != null) {
				DataSourceRepository repository = getRepository(props);
				dataSource = (DataSource) loadClass(props.getProperty(DataSourceProps.DATASOURCE_CLASS_NAME));
				repository.load(dataSource, props);
				dataSources.addDataSource(name, dataSource);
				// addDataSource(name, dataSource, repository);
			}
		}
		return dataSource;
	}

	/**
	 * Obtain Properties Object used to load a DataSource with the name.
	 * 
	 * @param name
	 *          of <code>DataSource</code> to obtain Properties for.
	 * @return
	 */
	public Properties getDataSourceProperties(String name) {
		return (Properties) dataSourceProps.get(name);
	}

	/**
	 * Add a DataSource to this set. Note that the DataSourceRepository will default to {@link MemoryDataSourceRepository MemoryDataSourceRepository} if null is
	 * passed in.
	 *
	 * @param name
	 *          unique logical name for the DataSource.
	 * @param dataSource
	 *          DataSource instance to add.
	 * @param repository
	 *          DataSourceRepository implementation to retrieve the DataSource from
	 */
	public void addDataSource(String name, DataSource dataSource, DataSourceRepository repository) {

		dataSources.addDataSource(name, dataSource);

		if (repository == null)
			repository = this.dataSources;
		Properties props = new Properties();
		repository.store(dataSource, props);
		repositories.put(name, repository);

		props.put(DataSourceProps.DATASOURCE_CLASS_NAME, dataSource.getClass().getName());
		props.put(DataSourceProps.REPOSITORY_CLASS_NAME, repository.getClass().getName());

		dataSourceProps.put(name, props);
	}

	/**
	 * Add a DataSource to this set using a Properties Object. Note that if DataSourceRepository is null, it will default to {@link MemoryDataSourceRepository
	 * MemoryDataSourceRepository}
	 *
	 * @param name
	 *          unique logical name for the DataSource.
	 * @param props
	 *          Properties object with DataSource settings.
	 * @param repository
	 *          DataSourceRepository implementation to retrieve the DataSource from
	 */
	public void addDataSource(String name, Properties props, DataSourceRepository repository) {

		if (dataSourceProps.get(name) != null)
			throw new DataSourceException(Res.bundle.format(ResIndex.DataSourceMustBeUnique, name));

		verify(props, "");

		if (repository == null)
			repository = getRepository(props);

		repositories.put(name, repository);

		dataSourceProps.put(name, props);
	}

	/**
	 * Remove a DataSource from the set.
	 * 
	 * @param name
	 */
	public void removeDataSource(String name) {
		dataSources.removeDataSource(name);
		/*
		 * Steve: Must be removed from props as well.
		 */
		dataSourceProps.remove(name);
	}

	public void updateDataSource(String name, Properties props) {
		if (dataSourceProps.get(name) == null)
			throw new DataSourceException(Res.bundle.format(ResIndex.MissingDataSource, name));

		verify(props, "");

		removeDataSource(name);
		addDataSource(name, props, null);
	}

	public Object[] getDataSourceNames() {
		return dataSourceProps.keySet().toArray();
	}

	// These constants are temporary and only used in:
	// getConnectionProperties and:
	// applyChange
	// And referenced only by: dbtools.jconsole.model.DataSourceModel

	private static final int TYPE_FILE = 1;
	private static final int TYPE_REQUIRED = 0x4000;
	private static final int TYPE_SELDOM = 0x8000;

	public static final String NAME = "NAME"; // NORES
	public static final String DESCRIPTION = "DESCRIPTION"; // NORES
	public static final String SERVER_NAME = "SERVER_NAME"; // NORES
	public static final String PROTOCOL = "PROTOCOL"; // NORES
	public static final String SERVER_PORT = "SERVER_PORT"; // NORES
	public static final String DATABASE_NAME = "DATABASE_NAME"; // NORES
	private static final String CREATE = "CREATE"; // NORES
	private static final String USERNAME = "USERNAME"; // NORES
	public static final String PASSWORD = "PASSWORD"; // NORES
	public static final String READONLYTX = "READONLYTX"; // NORES

	private static final String NAME_CAPTION = "Name";
	private static final String DESCRIPTION_CAPTION = "Description";
	private static final String SERVER_NAME_CAPTION = "Server Name";
	public static final String PROTOCOL_CAPTION = "Protocol";
	private static final String SERVER_PORT_CAPTION = "Port";
	private static final String DATABASE_NAME_CAPTION = "Database Filename";
	private static final String CREATE_CAPTION = "Create";
	private static final String USERNAME_CAPTION = "Username";
	private static final String PASSWORD_CAPTION = "Password";
	private static final String READONLYTX_CAPTION = "Readonly Transactions";

	private static final String DESCRIPTION_PROPERTY = "datasource.prop.Description"; // NORES
	private static final String SERVER_NAME_PROPERTY = "datasource.prop.ServerName"; // NORES
	private static final String PROTOCOL_PROPERTY = "datasource.prop.NetworkProtocol";// NORES
	private static final String SERVER_PORT_PROPERTY = "datasource.prop.PortNumber"; // NORES
	private static final String DATABASE_NAME_PROPERTY = "datasource.prop.DatabaseName"; // NORES
	private static final String CREATE_PROPERTY = "datasource.prop.Create"; // NORES
	private static final String USERNAME_PROPERTY = "datasource.prop.User"; // NORES
	private static final String PASSWORD_PROPERTY = "datasource.prop.Password"; // NORES
	private static final String READONLYTX_PROPERTY = "extended.prop.readOnlyTx"; // NORES

	private static final int NAME_INDEX = 0;
	private static final int DESCRIPTION_INDEX = 1;
	private static final int SERVER_NAME_INDEX = 2;
	private static final int PROTOCOL_INDEX = 3;
	private static final int SERVER_PORT_INDEX = 4;
	private static final int DATABASE_NAME_INDEX = 5;
	private static final int CREATE_INDEX = 6;
	private static final int USERNAME_INDEX = 7;
	private static final int PASSWORD_INDEX = 8;
	private static final int READONLYTX_INDEX = 9;

	private static final String TCP = "tcp"; // NORES
	private static final String LOCALHOST = "localhost"; // NORES
	private static final String DEFAULT_PORT = "2508"; // NORES

	private DataSet protocolChoices;

	private DataSet getProtocolChoices() {
		if (protocolChoices == null) {
			StorageDataSet dataSet = new StorageDataSet();
			Column protocol = new Column(PROTOCOL, PROTOCOL_CAPTION, Variant.STRING);
			Column name = new Column(NAME, NAME_CAPTION, Variant.STRING);
			dataSet.setColumns(new Column[] { protocol, name });
			dataSet.open();
			dataSet.insertRow(false);
			dataSet.setString(0, TCP);
			dataSet.setString(1, "Remote");
			dataSet.insertRow(false);
			dataSet.setString(0, "");
			dataSet.setString(1, "Local");
			dataSet.post();
			protocolChoices = dataSet;
		}
		return protocolChoices;
	}

	// Currently: only JDataStore properties are handled !!!!!!
	// To be implemented correctly later by Steve (or Joal)
	// Create a dataSet, which has all possible settings for this connection.
	// Perhaps this method should be in metadata.cache ...
	public DataSet getConnectionProperties(String dsName) {
		Properties props = getDataSourceProperties(dsName);
		StorageDataSet dataSet = new StorageDataSet();
		Column name = new Column(NAME, NAME_CAPTION, Variant.STRING);
		Column description = new Column(DESCRIPTION, DESCRIPTION_CAPTION, Variant.STRING);
		Column serverName = new Column(SERVER_NAME, SERVER_NAME_CAPTION, Variant.STRING);
		Column protocol = new Column(PROTOCOL, PROTOCOL_CAPTION, Variant.STRING);
		Column port = new Column(SERVER_PORT, SERVER_PORT_CAPTION, Variant.INT);
		Column databaseName = new Column(DATABASE_NAME, DATABASE_NAME_CAPTION, Variant.STRING);
		Column create = new Column(CREATE, CREATE_CAPTION, Variant.BOOLEAN);
		Column userName = new Column(USERNAME, USERNAME_CAPTION, Variant.STRING);
		Column password = new Column(PASSWORD, PASSWORD_CAPTION, Variant.STRING);
		Column readonlyTx = new Column(READONLYTX, READONLYTX_CAPTION, Variant.BOOLEAN);
		protocol.setPickList(new PickListDescriptor(getProtocolChoices(), new String[] { PROTOCOL }, new String[] { NAME }, new String[] { PROTOCOL }, NAME,
				false));
		password.setItemEditor(new PasswordEditor());
		dataSet.setColumns(new Column[] { name, description, serverName, protocol, port, databaseName, create, userName, password, readonlyTx });
		dataSet.open();
		dataSet.insertRow(false);
		dataSet.setString(NAME_INDEX, dsName);
		dataSet.setString(DESCRIPTION_INDEX, props.getProperty(DESCRIPTION_PROPERTY));
		dataSet.setString(SERVER_NAME_INDEX, props.getProperty(SERVER_NAME_PROPERTY));
		dataSet.setString(PROTOCOL_INDEX, props.getProperty(PROTOCOL_PROPERTY));
		parseInt(dataSet, SERVER_PORT_INDEX, props.getProperty(SERVER_PORT_PROPERTY));
		dataSet.setString(DATABASE_NAME_INDEX, props.getProperty(DATABASE_NAME_PROPERTY));
		parseBoolean(dataSet, CREATE_INDEX, props.getProperty(CREATE_PROPERTY));
		dataSet.setString(USERNAME_INDEX, props.getProperty(USERNAME_PROPERTY));
		dataSet.setString(PASSWORD_INDEX, props.getProperty(PASSWORD_PROPERTY));
		parseBoolean(dataSet, READONLYTX_INDEX, props.getProperty(READONLYTX_PROPERTY));
		dataSet.post();
		dataSet.close();
		try {
			dataSet.addColumnChangeListener(protocolChangeListener);
		} catch (TooManyListenersException ex) {
			// will not happen !
		}
		dataSet.open();
		return dataSet;
	}

	static class PasswordEditor extends javax.swing.DefaultCellEditor {
		PasswordEditor() {
			super(new javax.swing.JPasswordField());
		}
	}

	private void parseBoolean(DataSet dataSet, int ordinal, String string_value) {
		if (string_value == null || string_value.length() == 0)
			dataSet.setBoolean(ordinal, false); // Temporary: works for JDataStore; depends on the default settings...
		// dataSet.setAssignedNull(ordinal);
		else
			dataSet.setBoolean(ordinal, string_value.equalsIgnoreCase(Boolean.TRUE.toString()));
	}

	private void parseInt(DataSet dataSet, int ordinal, String string_value) {
		if (string_value == null || string_value.length() == 0)
			dataSet.setAssignedNull(ordinal);
		else {
			try {
				dataSet.setInt(ordinal, Integer.parseInt(string_value));
			} catch (Exception ex) {
				dataSet.setAssignedNull(ordinal);
			}
		}
	}

	private ColumnChangeListener protocolChangeListener = new ColumnChangeAdapter() {
		public void changed(DataSet dataSet, Column column, Variant value) {
			if (column.getOrdinal() == PROTOCOL_INDEX) {
				if (value.getString() == null || value.getString().length() == 0) {
					dataSet.setAssignedNull(SERVER_NAME_INDEX);
					dataSet.setAssignedNull(SERVER_PORT_INDEX);
					dataSet.getColumn(SERVER_NAME_INDEX).setEditable(false);
					dataSet.getColumn(SERVER_PORT_INDEX).setEditable(false);
				} else if (value.getString().equals(TCP)) {
					dataSet.getColumn(SERVER_NAME_INDEX).setEditable(true);
					dataSet.getColumn(SERVER_PORT_INDEX).setEditable(true);
					if (dataSet.isNull(SERVER_NAME_INDEX))
						dataSet.setString(SERVER_NAME_INDEX, LOCALHOST);
					if (dataSet.isNull(SERVER_PORT_INDEX))
						dataSet.setInt(SERVER_PORT_INDEX, Integer.parseInt(DEFAULT_PORT));
				}
			}
			if (column.getOrdinal() != 0)
				applyChange(dataSet.getString(0), dataSet);
		}
	};

	public String getUserName(String name) {
		Properties props = getDataSourceProperties(name);
		return props.getProperty(USERNAME_PROPERTY);
	}

	public String getPassword(String name) {
		Properties props = getDataSourceProperties(name);
		return props.getProperty(PASSWORD_PROPERTY);

	}

	// To be implemented correctly later by Steve (or Joal)
	// Create a dataSet, which has all possible settings for this connection.
	// Perhaps this method should be in metadata.cache ...
	public void applyChange(String name, DataSet dataSet) {
		Properties props = getDataSourceProperties(name);
		setProperty(props, DESCRIPTION_PROPERTY, dataSet, DESCRIPTION_INDEX);
		setProperty(props, SERVER_NAME_PROPERTY, dataSet, SERVER_NAME_INDEX);
		setProperty(props, PROTOCOL_PROPERTY, dataSet, PROTOCOL_INDEX);
		setProperty(props, SERVER_PORT_PROPERTY, dataSet, SERVER_PORT_INDEX);
		setProperty(props, DATABASE_NAME_PROPERTY, dataSet, DATABASE_NAME_INDEX);
		setProperty(props, USERNAME_PROPERTY, dataSet, USERNAME_INDEX);
		setProperty(props, PASSWORD_PROPERTY, dataSet, PASSWORD_INDEX);
		updateDataSource(name, props);
	}

	private void setProperty(Properties props, String key, DataSet dataSet, int columnIndex) {
		if (dataSet.isNull(columnIndex))
			props.remove(key);
		else {
			switch (dataSet.getColumn(columnIndex).getDataType()) {
				case Variant.STRING:
					props.put(key, dataSet.getString(columnIndex));
					break;
				case Variant.INT:
					props.put(key, String.valueOf(dataSet.getInt(columnIndex)));
					break;
				case Variant.BOOLEAN:
					props.put(key, String.valueOf(dataSet.getBoolean(columnIndex)));
					break;
				default:
					DiagnosticJLimo.fail();
			}
		}
	}

	/**
	 * Obtain a Hastable for all DataSources in this set. Note that currently this is not a "cloned" copy, and should not be edited.
	 * 
	 * @return Hashtable of all DataSources in the set.
	 */
	public Hashtable getDataSources() {
		return dataSources.getDataSources();
	}

	/**
	 *
	 * @return a hashtable of all known DataSourceRepositories used by this set.
	 */
	public Hashtable getDataSourceRepositories() {
		return repositories;
	}

	private MemoryDataSourceRepository dataSources;
	private Hashtable dataSourceProps;
	private Hashtable repositories;
	private String[] validProps;
}
