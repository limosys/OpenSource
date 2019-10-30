//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/util/Attic/MemoryDataSourceRepository.java,v 1.16.2.1 2004/05/23 21:11:23 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.sql.DataSource;

import com.borland.dx.util.cons.DataSourceProps;

public class MemoryDataSourceRepository extends DataSourceRepository {

	public DataSource getDataSource(String name) {
		return (DataSource) dataSources.get(name);
	}

	public void addDataSource(String name, DataSource dataSource) {
		dataSources.put(name, dataSource);
	}

	public void removeDataSource(String name) {
		dataSources.remove(name);
	}

	public Hashtable getDataSources() {
		return dataSources;
	}

	private void setProp(Method[] methods, String setMeth, String value, Object ds)
			throws java.lang.reflect.InvocationTargetException,
			java.lang.IllegalAccessException {
		// find the method we are looking for
		int i = 0;
		while (i < methods.length && !setMeth.equals(methods[i].getName())) {
			i++;
		}

		if (i == methods.length) { throw new DataSourceException(Res.bundle.format(ResIndex.PropNotFound, setMeth)); }

		// get the parameters types - we are only
		// interested in the first. If there are
		// multiple methods only differing by their
		// parameter types this won't work!!
		Class[] paramTypes = methods[i].getParameterTypes();
		String name = paramTypes[0].getName();

		// convert the input string into the
		// correct type.
		Object[] params = new Object[1];
		if (name.equals("java.lang.String")) { // NORES
			params[0] = value;
		} else if (name.equals("boolean")) {// NORES
			params[0] = Boolean.valueOf(value);
		} else if (name.equals("int")) {// NORES
			params[0] = Integer.valueOf(value);
		} else {
			throw new DataSourceException(Res.bundle.format(ResIndex.PropertyNotHandled, name));
		}
		methods[i].invoke(ds, params);
	}

	private boolean isPropertyParameterType(Class paramType) {
		String name = paramType.getName();
		if (name.equals(String.class.getName()))
			return true;
		if (name.equals("boolean")) // NORES
			return true;
		if (name.equals("int")) // NORES
			return true;
		return false;
	}

	private void setProp(Method[] methods, String methodName, Object value, Object ds)
			throws java.lang.reflect.InvocationTargetException,
			java.lang.IllegalAccessException {
		Class[] paramTypes;
		Class objectClass = value.getClass();
		for (int index = 0; index < methods.length; ++index) {
			if (methods[index].getName().startsWith("set")) { // NORES
				paramTypes = methods[index].getParameterTypes();
				if (paramTypes.length == 1 && methods[index].getName().equals(methodName) && paramTypes[0].equals(objectClass)) {
					Object[] params = new Object[1];
					params[0] = value;
					methods[index].invoke(ds, params);
					return;
				}
			}
		}
		throw new DataSourceException(Res.bundle.format(ResIndex.MethodNotFound, ds, methodName, objectClass.getName()));
	}

	public void load(DataSource dataSource, Properties props) {
		Class dataSourceClass = dataSource.getClass();
		Exception ex = null;
		String setMeth = null;
		String key;
		String propName;
		Properties extendedProps = null;
		Properties urlProps = null;
		try {
			dataSourceClass = dataSource.getClass();
			// get a list of the methods
			Method methods[] = dataSourceClass.getMethods();
			Enumeration<Object> enum1 = props.keys();
			while (enum1.hasMoreElements()) {
				key = enum1.nextElement().toString();
				if (key.startsWith(DataSourceProps.DATASOURCE_PROP)) {
					propName = key.substring(DataSourceProps.DATASOURCE_PROP.length(), key.length());
					setMeth = "set" + propName.substring(0, 1).toUpperCase() + propName.substring(1); // NORES
					setProp(methods, setMeth, props.getProperty(key), dataSource);
				} else if (key.startsWith(DataSourceProps.EXTENDED_PROP)) {
					propName = key.substring(DataSourceProps.EXTENDED_PROP.length(), key.length());
					if (extendedProps == null)
						extendedProps = new Properties();
					extendedProps.setProperty(propName, props.getProperty(key));
				} else if (key.startsWith(DataSourceProps.URL_PROP)) {
					propName = key.substring(DataSourceProps.URL_PROP.length(), key.length());
					if (urlProps == null)
						urlProps = new Properties();
					urlProps.setProperty(propName, props.getProperty(key));
				}
			}
			if (extendedProps != null) {
				setProp(methods, "setProperties", extendedProps, dataSource); // NORES
			}
			if (urlProps != null) {
				setProp(methods, "setUrlProperties", urlProps, dataSource); // NORES
			}
		}
		// catch(ClassNotFoundException ex2) {
		// ex = ex2;
		// }
		catch (IllegalAccessException ex3) {
			ex = ex3;
		} catch (InvocationTargetException ex5) {
			ex = ex5;
		}

		if (ex != null)
			throw new DataSourceException(ex);
	}

	private final void copyProps(DataSource dataSource, Method getterMethod, Properties props, String prefix) {
		Properties getterProps = null;
		try {
			getterProps = (Properties) getterMethod.invoke(dataSource, null);
		} catch (Exception ex) {
			DataSourceException.throwExceptionChain(ex);
		}
		DataSourceSet.copyProps(getterProps, props, prefix);
	}

	public void store(DataSource dataSource, Properties props) {
		Hashtable dataSourceMethods;
		Method[] methods;
		Class[] parameterTypes;
		Class returnType;
		Class dataSourceClass = dataSource.getClass();
		DataSource defaultDataSource;
		try {
			defaultDataSource = (DataSource) dataSourceClass.newInstance();
		} catch (Exception ex) {
			defaultDataSource = null;
		}
		methods = dataSource.getClass().getMethods();
		Method method;
		dataSourceMethods = new Hashtable();
		String name;
		for (int methodIndex = 0; methodIndex < methods.length; ++methodIndex) {
			method = methods[methodIndex];
			name = method.getName();
			if (name.startsWith("get") || name.startsWith("is")) {
				returnType = method.getReturnType();
				parameterTypes = method.getParameterTypes();
				if (parameterTypes.length == 0) {
					if (isPropertyParameterType(returnType))
						dataSourceMethods.put(name, method);
					else if (returnType.equals(java.util.Properties.class)) {
						if (name.equals("getProperties")) { // NORES
							copyProps(dataSource, method, props, DataSourceProps.EXTENDED_PROP);
						} else if (name.equals("getUrlProperties")) { // NORES
							copyProps(dataSource, method, props, DataSourceProps.URL_PROP);
						}
					}
				}
			}
		}
		Method getterMethod;
		String getterName;
		String baseName;
		Object value;
		Object defaultValue;
		Object[] emptyParam = new Object[0];
		for (int methodIndex = 0; methodIndex < methods.length; ++methodIndex) {
			method = methods[methodIndex];
			name = method.getName();
			if (name.startsWith("set")) { // NORES
				parameterTypes = method.getParameterTypes();
				if (parameterTypes.length == 1 && isPropertyParameterType(parameterTypes[0])) {
					baseName = name.substring(3, name.length());
					getterMethod = (Method) dataSourceMethods.get("get" + baseName); // NORES
					if (getterMethod == null) getterMethod = (Method) dataSourceMethods.get("is" + baseName); // NORES
					if (getterMethod != null && parameterTypes[0].equals(getterMethod.getReturnType())) {
						value = null;
						defaultValue = null;
						try {
							value = getterMethod.invoke(dataSource, emptyParam);
							if (defaultDataSource != null)
								defaultValue = getterMethod.invoke(defaultDataSource, emptyParam);
						} catch (Exception ex) {
							value = null;
							// DataSourceException.throwExceptionChain(ex);
						}
						if (value != null && (defaultValue == null || !value.equals(defaultValue)))
							props.put(DataSourceProps.DATASOURCE_PROP + baseName, value.toString());
					}
				}
			}
		}
	}

	Hashtable dataSources = new Hashtable();
}
