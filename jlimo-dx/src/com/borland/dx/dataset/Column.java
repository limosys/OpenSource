//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Column.java,v 7.7.2.2 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//! NOTE!!!   NOTE!!!  NOTE!!!
//! If you add a property to Column, you MUST update ColumnBeanInfo to reflect it
//! or it will not be seen in the inspector.  Properties which are visible only
//! programmatically do not need to be in ColumnBeanInfo. <ron>

package com.borland.dx.dataset;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Locale;
import java.util.TooManyListenersException;

import com.borland.dx.dataset.cons.ColumnConst;
import com.borland.dx.dataset.cons.OpenBlock;
import com.borland.dx.text.Alignment;
import com.borland.dx.text.BigDecimalFormatter;
import com.borland.dx.text.BinaryFormatter;
import com.borland.dx.text.BooleanFormatter;
import com.borland.dx.text.ByteFormatter;
import com.borland.dx.text.DateFormatter;
import com.borland.dx.text.DoubleFormatter;
import com.borland.dx.text.IntegerFormatter;
import com.borland.dx.text.ItemEditMask;
import com.borland.dx.text.ItemEditMaskStr;
import com.borland.dx.text.LongFormatter;
import com.borland.dx.text.ObjectFormatter;
import com.borland.dx.text.ShortFormatter;
import com.borland.dx.text.StringFormatter;
import com.borland.dx.text.TimeFormatter;
import com.borland.dx.text.TimestampFormatter;
import com.borland.dx.text.VariantFormatStr;
import com.borland.dx.text.VariantFormatter;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.ErrorResponse;
import com.borland.jb.util.TriStateProperty;

//import com.borland.dx.dataset.Variant;
//import com.borland.dx.text.Alignment;
//import com.borland.jb.util.ErrorResponse;
//import com.borland.jb.util.TriStateProperty;

/**
 *
 * The <CODE>Column</CODE> component stores important column-level metadata type properties (such as data type and precision) as well as visual properties such
 * as font and alignment. For <CODE>QueryDataSet</CODE> and <CODE>ProcedureDataSet</CODE> components, a set of <CODE>Column</CODE> components is dynamically
 * created each time the <CODE>StorageDataSet</CODE> is instantiated, mirroring the actual columns in the data source at that time.
 * 
 * <P>
 * Data-aware controls pick up <CODE>Column</CODE> properties when bound to a <CODE>DataSet</CODE>. After that, to affect display, set properties of the
 * <CODE>ColumnView</CODE> or <CODE>DataSetColumnView</CODE> components.
 * 
 * <P>
 * For more information on working with columns, see "Working with columns" in the <CITE>Database Application Developer's Guide</CITE>.
 * 
 * 
 * <A NAME="calccols"></A>
 * <H3>Calculated columns</H3>
 * 
 * <!-- JDS start - remove part about Column Editor -->
 * <P>
 * A <CODE>Column</CODE> may derive its values as a result of a calculated expression. Calculated <CODE>Columns</CODE> can be set using the Column Editor but
 * are not retrieved from its data source. Instead, values for the <CODE>Column</CODE> are calculated for each row of data using a formula defined in the
 * <CODE>StorageDataSet</CODE> object's <CODE>calcFields</CODE> event handler. Calculated columns are read-only; you cannot set individual values of a row for
 * columns that are calculated columns.
 * 
 * <!-- JDS end -->
 * 
 * 
 * <P>
 * Typically, the formula for a calculated field uses expressions involving values from other <CODE>Columns</CODE> in that row to generate a value for each row
 * of the calculated <CODE>Column</CODE>. For example, a table might have non-calculated columns for Quantity and UnitPrice, and a calculated
 * <CODE>Column</CODE> for ExtendedPrice, which is calculated by multiplying the values of Quantity and UnitPrice. Calculated <CODE>Columns</CODE> are also
 * useful for performing lookups in other tables. For example, a part number can be used to retrieve a part description for display in an invoice line item.
 * 
 * <!-- JDS start - change JBuilder to JDataStore -->
 * <P>
 * DataExpress calculated columns are read-only, and while JBuilder automatically recognizes this and attempts to discover other non-updateable columns, some
 * JDBC drivers seem unaware that server-side calculated columns are read-only. <!--The JDBC-ODBC bridge seems particularly affected by this.--> For this
 * reason, you should set column properties to read-only and not resolvable. While this seems redundant, this can prove crucial to your application. <!-- JDS
 * end -->
 * 
 * <!-- JDS start - remove paragraph -->
 * <P>
 * For more information and a tutorial on calculated columns, see "Using calculated columns" in the <CITE>Database Application Developer's Guide</CITE>.
 * 
 * <!-- JDS end -->
 * 
 * 
 * <A NAME="colpersistence"></A>
 * <H3>Column persistence and order</H3>
 * <P>
 * <!--BNDX="columns:setting as persistent;persistent columns:"--> An important <CODE>Column</CODE> property is persistence. Set this property to create a
 * persistent, unchanging set of columns. Setting a <CODE>Column</CODE> to persistent guarantees that each time your application runs, it uses and displays the
 * same <CODE>Column</CODE> components every time and in the same order. In the Column Editor, setting any property sets that <CODE>Column</CODE> to persistent.
 * This is designated in the Component Tree with square brackets ([]) around the <CODE>Column</CODE> name. Programmatically, you set the <CODE>persist</CODE>
 * property.
 * 
 * <P>
 * You can define events at the <CODE>Column</CODE> level, however, you must make a column persistent before you can define events on it.
 * 
 * 
 * <P>
 * A side-effect of setting a <CODE>Column</CODE> as persistent is that persistent columns may not placed in the same order as they are found in the
 * <CODE>DataSet</CODE>.
 * 
 * <P>
 * When a <CODE>StorageDataSet</CODE> is provided data, it:
 * <UL>
 * <LI>Moves persistent columns to the left, as the first columns of the <CODE>DataSet</CODE>. Any non-persistent columns are deleted.
 * <LI>Merges columns from the provided data with persistent columns. If a persistent column has the same name as a provided column it is considered to be the
 * same column. If the data types do not match, the values will be coerced to a valid data type.
 * <LI>Adds the remaining columns that are defined only in the application to the <CODE>DataSet</CODE>, in the order they are defined in the
 * <CODE>DataSet.setColumns()</CODE> method call.
 * <LI>Adds the provided columns in the order specified in the query or procedure.
 * <LI>Attempts to place every column whose <CODE>preferredOrdinal</CODE> property is set to its desired location.
 * </UL>
 * 
 * <!-- JDS start - remove paragraph -->
 * <P>
 * For more information on column persistence, see "Specifying required data in your application" in the <CITE>Database Application Developer's Guide</CITE>.
 * <!-- JDS end -->
 * 
 * For information on how column persistence can help in situations where there is a master-detail relationship and the potential for an empty master
 * <CODE>DataSet</CODE>, see {@link com.borland.dx.dataset.MasterLinkDescriptor Empty master DataSet} in the About section of the
 * <CODE>MasterLinkDescriptor</CODE> class.
 * 
 * 
 * 
 * <A NAME="colconstraints"></A>
 * <H3>Column constraints</H3>
 * <P>
 * <!--BNDX="data constraints:;constraints:data;column constraints"--> Another important <CODE>Column</CODE> level feature is the support of constraints.
 * Supported constraints include:
 * <UL>
 * <LI>precision and scale
 * <LI>minimum and maximum values
 * <LI>default values
 * </UL>
 * 
 * <P>
 * These constraints are not enforced when data is loaded into a <CODE>StorageDataSet</CODE> component since it is assumed that the data satisfied the
 * constraints before being stored in the SQL database. However, you cannot leave a modified field until its value satisfies the constraints set on that
 * <CODE>Column</CODE>. This is done automatically; you do not need special code to handle or enforce these constraints. You can extend constraints by writing a
 * class that implements <CODE>ColumnChangeListener</CODE> or <CODE>EditListener</CODE> if you want additional validation handling.
 * 
 * 
 * <A NAME="javaobject"></A>
 * <H3>Java Object support</H3>
 * <P>
 * <!--BNDX="Java Object:support for;Object type:support for Object"-->
 * 
 * Columns can contain various data types of data including <CODE>Object</CODE>. In general, you should provide custom <CODE>ItemPainter</CODE> and
 * <CODE>ItemEditor</CODE> classes for each Java Object type. Set each Column that contains Object data to its applicable <CODE>ItemPainter</CODE> and
 * <CODE>ItemEditor</CODE> classes.
 * 
 * <P>
 * The default <CODE>ItemPainter</CODE> prints the <CODE>toString()</CODE> value of the object. The default <CODE>ItemEditor</CODE> treats the Object as a
 * String. The result of an edit will therefore be an Object of java.lang.String. If these defaults are acceptable given your Object's data, with the exception
 * that the edited Object should be of a different data type, set a custom <CODE>formatter</CODE> for this Column. This formatter should be able to format the
 * Object into a String and parse a String back into the wanted Object type.
 * 
 * 
 * <A NAME="typecoercions"></A>
 * <H3>Data type conflicts between a column and its data source</H3>
 * <P>
 * <!--BNDX="data type coercions:caution for;type coercions:caution for;coercions:caution for"--> <!--BNDX="data type mappings:overriding;columns:overriding
 * default type mappings"--> <!--BNDX="type mappings:overriding;mapping overrides"--> <!--BNDX="columns:caution for assigning data types;data types:caution for
 * assigning;types:caution for assigning"--> Some columns in a <CODE>DataSet</CODE> are provided, for example, by execution of a query or stored procedure. A
 * provided column's <CODE>dataType</CODE> property is set by mapping the JDBC data type (stored in the <CODE>sqlType</CODE> property of the Column component)
 * to the equivalent <CODE>Variant</CODE> data type (stored in the <CODE>Column's</CODE> <CODE>dataType</CODE> property).
 * 
 * <P>
 * You can override the default data type mapping by setting the <CODE>Column's</CODE> <CODE>dataType</CODE> property. By default, the
 * {@link com.borland.dx.sql.dataset.QueryProvider QueryProvider} and {@link com.borland.dx.sql.dataset.QueryResolver QueryResolver} used by a
 * <CODE>QueryDataSet</CODE> performs automatic type coercions between the various time and numeric data types as data is retrieved from and saved to a
 * JDBC-based data source. However, there are some type coercions which are not automatically handled. This includes coercions to or from <CODE>String</CODE>,
 * <CODE>Object</CODE>, and <CODE>InputStream</CODE> data types. In addition, the automatic coercions may not be acceptable for your application. For example,
 * the coercion from a double to <CODE>BigDecimal</CODE> may incur undesirable precision loss. An application can override the automatic type coercion by wiring
 * the <A
 * {@link com.borland.dx.dataset.CoerceToListener#coerceToColumn(com.borland.dx.dataset.StorageDataSet, com.borland.dx.dataset.Column, com.borland.dx.dataset.Variant, com.borland.dx.dataset.Variant)
 * CoerceToListener.coerceToColumn(...)} and
 * {@link com.borland.dx.dataset.CoerceFromListener#coerceFromColumn(com.borland.dx.dataset.StorageDataSet, com.borland.dx.dataset.Column, com.borland.dx.dataset.Variant, com.borland.dx.dataset.Variant)
 * CoerceToListener.coerceFromColumn(...)} events. This feature allows for greater portability of an application. For example, code such as
 * 
 * <PRE>
customerDS.setDate("HIRE_DATE",...);
 * </PRE>
 * 
 * could run on any server, even one that supports TIMESTAMP but not DATE.
 * 
 * 
 * <P>
 * <STRONG>Warning:</STRONG> Be careful when assigning data types to your columns. Coercions, whether intentional or not, can cause data loss. For example,
 * coercion from a FLOAT data source to an INT column type discards the fractional part of the value. If you save the value back to the server, the INT is
 * converted back to a FLOAT thereby losing the fractional part of the data.
 * 
 * <P>
 * For more information on <CODE>Columns</CODE>, see "Working with columns" in the <CITE>Database Application Developer's Guide</CITE>.
 * 
 */

public class Column implements Cloneable, java.io.Serializable, Designable {

	/**
	 * Constructs a Column component.
	 */

	/*
	 * Doesn't show up in JavaDoc properly!
	 *
	 * <table> <tr><td>Property </td> <td> Value </td></tr> <tr><td>visible </td> <td> TriStateProperty.DEFAULT </td></tr> <tr><td>dataType </td> <td>
	 * Variant.ASSIGNED_NULL </td></tr> <tr><td>searchable </td> <td> true </td></tr> <tr><td>resolvable </td> <td> TriStateProperty.DEFAULT </td></tr>
	 * <tr><td>editable </td> <td> true </td></tr> </table>
	 */
	public Column() {
		// ! this.displayMask = null; // compiler inits to null
		// ! this.editMask = null; // compiler inits to null
		this.localeName = "";
		this.precision = -1; // -1 means "don't care"
		this.sortPrecision = -1; // -1 means "don't care"
		this.scale = -1; // -1 means "don't care"
		// ! this.width = 0; // grid is smart about sizing now.

		this.columnName = "";
		this.ordinal = -1;
		this.preferredOrdinal = -1;
		this.visible = TriStateProperty.DEFAULT;
		this.dataType = Variant.ASSIGNED_NULL;
		setPredicate(ColumnConst.SEARCHABLE | ColumnConst.EDITABLE, true);
		this.resolvable = TriStateProperty.DEFAULT;
		// ! this.collator = null;
	}

	/**
	 * Constructs a Column component with property values similar to those of its null constructor (Column()) and as specified by its parameters.
	 *
	 * @param columnName
	 *          The String name of the Column. This name must be unique across all Column components in the DataSet.
	 * @param caption
	 *          The String label used when displaying the Column in a data-aware control. The default caption is the columnName.
	 * @param dataType
	 *          The data type of the items the Column will contain. Accepted values are defined in {@link com.borland.dx.dataset.Variant}
	 */
	public Column(String columnName, String caption, int dataType)
	/*-throws DataSetException-*/
	{
		this();
		setCaption(caption);
		setColumnName(columnName);
		this.dataType = dataType;
	}

	/**
	 * Specifies the columnName used to identify the column in the source table. Use this property to access the data in the DataExpress API. All column names
	 * used in the DataSet must be unique; these names will be changed to make them unique as necessary.
	 * <p>
	 * For columns provided by a data source, the columnName property is set when the query is run. When a query is executed again, this property is used to
	 * identify columns. If aliases are used, the serverColumnName property is also set.
	 * <p>
	 * If this property specifies a value other than the original column name on the server table, set the serverColumnName property so that changes can be
	 * resolved back to the data source.
	 * <p>
	 * The DataSet must be closed in order to set this property.
	 * <p>
	 * On error, the setColumnName() method throws a DataSetException.
	 *
	 *
	 * @param name
	 * @see #setCaption(String caption)
	 */
	public final void setColumnName(String name)
	/*-throws DataSetException-*/
	{
		// !System.err.println("setColumnName to " + name);
		if (dataSet == null) {
			this.columnName = name;
			hash = hash(name);
		} else {
			// Must come before name is modified for DataStore map to work properly.
			//
			int oldEditBlocked = preparePropertyRestructure();
			boolean complete = false;
			String oldName = this.columnName;
			this.columnName = name;
			hash = hash(name);
			try {
				dataSet.changeColumn(ordinal, this);
				complete = true;
			} finally {
				if (!complete) {
					this.columnName = oldName;
					hash = hash(oldName);
					commitPropertyRestructure(oldEditBlocked);
				}
			}
			dataSet.updateForeignKeyColumnName(oldName, name, true, true);
			commitPropertyRestructure(oldEditBlocked);
		}
	}

	static final int hash(String s) {
		if (s == null)
			return 0;
		int length = s.length();
		if (length == 0)
			return 0;
		return (length << 16) + (((Character.toUpperCase(s.charAt(0)) << 8) + Character.toUpperCase(s.charAt(length - 1))) & 0xFFFF);
	}

	/**
	 * Returns the returns the hash table associated with this Column component.
	 *
	 * @return The hash table associated with this Column component.
	 */
	public final int getHash() {
		return hash;
	}

	/**
	 * Get the column name. For QueryDataSets can be used to find the SQL server's name of a column. Also used by data-aware controls for column binding. Unique
	 * for the StorageDataSet meaning, every column name must be unique.
	 * 
	 * @return The column name.
	 */
	public final String getColumnName() {
		return columnName;
	}

	/**
	 * Set the name of the column as it appeared from the server. If an alias to a column of a table is given in a query string, the alias would be the
	 * ColumnName, and the original name of the column in the table would appear as the ServerColumnName property. Most useful for QueryDataSets. It may be left
	 * null.
	 * 
	 * @param serverColumnName
	 *          The name of the column as it appeared from the server.
	 */
	public final void setServerColumnName(String serverColumnName) {
		this.serverColumnName = serverColumnName;
	}

	/**
	 * Get the name of the column that the server specified. If an alias to a column of a table is given in a query string, the alias would be the
	 * ServerColumnName, and the original name of the column in the table would appear as the ColumnName property. Most useful for QueryDataSets. It may be left
	 * null.
	 * 
	 * @return The name of the column that the server specified.
	 */
	public final String getServerColumnName() {
		return serverColumnName != null ? serverColumnName : columnName;
	}

	/**
	 * Set the Table name that the column belongs to. Most useful for QueryDataSets to flag the Table a Column belongs to. It may be left null.
	 * 
	 * @param tableName
	 *          the Table name that the column belongs to.
	 */
	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Get the Table name that the column belongs to. Most useful for QueryDataSets to flag the Table a Column belongs to.
	 * 
	 * @return the Table name that the column belongs to.
	 */
	public final String getTableName() {
		return tableName;
	}

	/**
	 * Set the Schema name of the table that the column belongs to. Most useful for QueryDataSets to flag the Table a Column belongs to. It may be left null.
	 * 
	 * @param schemaName
	 *          The Schema name of the table that the column belongs to.
	 */
	public final void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * Get the Schema name of the table that the column belongs to. Most useful for QueryDataSets to flag the Table a Column belongs to.
	 * 
	 * @return The Schema name of the table that the column belongs to.
	 */
	public final String getSchemaName() {
		return schemaName;
	}

	/* User should not set this one. */
	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	/**
	 * Gets JDBC SqlType of the Column. Refer to java.sql.Types for more information on the types.
	 *
	 * @return The JDBC SqlType of the Column.
	 */
	public final int getSqlType() {
		return sqlType;
	}

	public void setSearchable(boolean searchable)
	/*-throws DataSetException-*/
	{
		if (searchable != isSearchable()) {
			setPredicate(ColumnConst.SEARCHABLE, searchable);
			updateProperties();
		}
	}

	public boolean isSearchable() {
		return is(ColumnConst.SEARCHABLE);
	}

	/**
	 * If set to <b>false</b>, these columns will not be saved back during a resolve. Useful when saving a dataSet back to a SQL server when the dataSet has calc
	 * or aggregation columns.
	 * 
	 * @param resolvable
	 *          If set to <b>false</b>, these columns will not be saved back during a resolve.
	 */
	public void setResolvable(boolean resolvable) {
		this.resolvable = resolvable ? TriStateProperty.TRUE : TriStateProperty.FALSE;
	}

	public boolean isResolvable() {
		switch (resolvable) {
			case TriStateProperty.TRUE:
				return true;
			case TriStateProperty.FALSE:
				return false;
		}
		return calcType == CalcType.NO_CALC; // !Bug07869: Calc fields are never resolvable (JOAL)
	}

	/**
	 * Calculated is CalcType.CALC if the value of the column is calculated by its dataset calcFields event handler. The dataset's calcFields event handler, which
	 * typically uses expressions involving values from other columns in the record to generate a value for each calculated column. For example, a table might
	 * have non-calculated columns for Quantity and UnitPrice, and a calculated column for ExtendedPrice, which would be calculated by multiplying the values of
	 * the Quantity and UnitPrice columns. Calculated columns are also useful for performing lookups in other tables. For example, a part number can be used to
	 * retrieve a part description for display in an invoice line item. Refer to the variables defined in CalcType for the possible calculation types for a
	 * column.
	 * <p>
	 * This cannot be called on an open DataSet.
	 * 
	 * @param calcType
	 */

	/**
	 * Specifies the calculation type of the Column. Valid values for the calculation type are defined in com.borland.dx.dataset.CalcType variables. Setting a
	 * column to any calculation type (other than NO_CALC) sets this Column to readOnly.
	 * <p>
	 * The DataSet must be closed in order to set this property.
	 *
	 * @param calcType
	 *          The calculation type of the Column.
	 */
	public final void setCalcType(int calcType)
	/*-throws DataSetException-*/
	{
		int oldEditBlocked = preparePropertyRestructure();
		if ((pickList != null && (pickList.getLookupDisplayColumn() == null || calcType != CalcType.LOOKUP))
				|| !validCalc(calcType))
			DataSetException.setCalculatedFailure();
		Column column = this;

		if (dataSet != null)
			column = changeColumn(dataType, calcType);
		else
			this.calcType = calcType;

		// Note that if this is not done, calc fields will be removed when a query
		// execution drops non persistent columns.
		//
		if (column.calcType != CalcType.NO_CALC)
			column.setPredicate(ColumnConst.PERSIST, true);
		initHasValidations();
		commitPropertyRestructure(oldEditBlocked);
	}

	/**
	 * Returns the calculation type of the Column. Valid values for the calculation type are defined in com.borland.dx.dataset.CalcType variables.
	 *
	 * @return The calculation type of the Column. Valid values for the calculation type are defined in com.borland.dx.dataset.CalcType variables.
	 */
	public final int getCalcType() {
		return calcType;
	}

	public final void setAgg(AggDescriptor aggDescriptor) {
		this.aggDescriptor = aggDescriptor;
	}

	/**
	 * Returns the AggDescriptor object that defines the aggregator properties for a calculated Column whose calcType property is
	 * {@link com.borland.dx.dataset.CalcType#AGGREGATE}.
	 *
	 * @return The AggDescriptor object that defines the aggregator properties for a calculated Column whose calcType property is
	 *         {@link com.borland.dx.dataset.CalcType#AGGREGATE}.
	 */
	public final AggDescriptor getAgg() {
		return aggDescriptor;
	}

	/**
	 * ReadOnly enables or disables modification of a column. If set to <b>false</b>, the default, a column can be modified. To prevent a column from being
	 * modified, set ReadOnly to <b>true</b>.
	 * 
	 * @param readOnly
	 *          Enables or disables modification of a column.
	 */
	public final void setReadOnly(boolean readOnly)
	/*-throws DataSetException-*/
	{
		if (readOnly != this.readOnly) {
			this.readOnly = readOnly;
			initHasValidations();
			updateProperties();
			notifyPropertyChange();
		}
	}

	/**
	 * ReadOnly enables or disables modification of a column. If set to <b>false</b>, the default, a column can be modified. To prevent a column from being
	 * modified, set ReadOnly to <b>true</b>.
	 * 
	 * @return <b>true</b> if the column can be modified, <b>false</b> otherwise.
	 */
	public final boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * ReadOnly enables or disables modification of a Column from a UI control.
	 *
	 * @param editable
	 *          If set to <b>true</b>, the column can be modified from a UI control.
	 */
	public final void setEditable(boolean editable)
	// ! /*-throws DataSetException-*/
	{
		setPredicate(ColumnConst.EDITABLE, editable);
		notifyPropertyChange();
	}

	/**
	 * ReadOnly enables or disables modification of a column. If set to <b>false</b>, the default, a column can be modified. To prevent a column from being
	 * modified, set ReadOnly to <b>true</b>.
	 * 
	 * @return <b>true</b> if the column can be modified, <b>false</b> otherwise.
	 */
	public final boolean isEditable() {
		return is(ColumnConst.EDITABLE);
	}

	public final void setRowId(boolean rowId)
	/*-throws DataSetException-*/
	{
		if (rowId != isRowId()) {
			setPredicate(ColumnConst.ROWID, rowId);
			if (rowId && dataSet != null)
				dataSet.rowIdSet();
			updateProperties();
		}
	}

	public final boolean isRowId() {
		return is(ColumnConst.ROWID);
	}

	final void _setRowId(boolean rowId) {
		setPredicate(ColumnConst.ROWID, rowId);
	}

	final Variant parsePropertyValue(Variant currentValue, String parseString)
	/*-throws DataSetException-*/
	{
		if (parseString == null)
			return null;
		else if (parseString.length() > 0) {
			if (defaultString != null && defaultString == parseString && (defaultString.equalsIgnoreCase("now") || defaultString.equalsIgnoreCase("today"))) { // NORES
				if (dataType == Variant.TIME || dataType == Variant.TIMESTAMP || dataType == Variant.DATE) {
					setPredicate(ColumnConst.NOW_DEFAULT, true);
					return null;
				}
			}
			try {
				// ! JOAL:
				/*
				 * This is the code, that used to be in JB2.01 getExportFormatter(); if (exportFormatter != null) { if (currentValue == null) currentValue = new
				 * Variant(dataType); exportFormatter.parse(parseString, currentValue); return currentValue; }
				 */
				if (currentValue == null)
					currentValue = new Variant();
				currentValue.setFromString(dataType, parseString);
			} catch (Exception ex) {
				DiagnosticJLimo.println("parseString:  " + parseString);
				DataSetException.invalidFormat(ex, columnName);
			}
		}

		return currentValue;
	}

	/**
	 * Returns the default value for this Column in new records as a String.
	 *
	 * @return The default value for this Column in new records as a String.
	 */
	public final String getDefault() {
		return defaultString;
	}

	/**
	 * Stores the default value for this Column in new records as a String.
	 * <p>
	 * The default value must be supplied in a locale-independent format, because the default may be coming from the server as metadata. This format is defined in
	 * the code comment for Variant's setFromString() method. For example, a date must be in the format "yyyy-MM-dd".
	 * <p>
	 * If the defaultString parameter is set to "now" and the dataType for this Column is Date, Time, or Timestamp, the default will be the current time returned
	 * from System.currentTimeMillis(). The setDefaultValue method can be used to set the default as a com.borland.dx.dataset.Variant, which can be useful for
	 * binary data types.
	 * <p>
	 * Default values are automatically filled in when a new row is inserted. If no other updates are made to the row, it is considered untouched and is not
	 * posted.
	 *
	 * @param defaultString
	 *          The default value for this Column in new records as a String.
	 */
	public final void setDefault(String defaultString)
	/*-throws DataSetException-*/
	{
		// ! Diagnostic.println("getDefault value: "+name+" "+defaultValue);

		if (this.defaultString != defaultString && (this.defaultString == null || defaultString == null || !this.defaultString.equals(defaultString))) {
			this.defaultString = defaultString;

			defaultValue = parsePropertyValue(defaultValue, defaultString);
			updateProperties(); // Bug 107147
		}
	}

	/**
	 * Return the default value for fields in new records.
	 *
	 * @return The default value for fields in new records as a {@link com.borland.dx.dataset.Variant} of the same data type as the column.
	 */
	public final Variant getDefaultValue() {
		return defaultValue;
	}

	public final void setDefaultValue(Variant defaultValue)
	/*-throws DataSetException-*/
	{
		this.defaultValue = (Variant) defaultValue.clone();
	}

	/**
	 * Returns the default value set for the Column.
	 * 
	 * @param value
	 *          The default value set for the Column.
	 */
	public final void getDefault(Variant value) {
		if (is(ColumnConst.NOW_DEFAULT)) {
			// I use the constructors for these because they will parse out
			// into appropriate values. ie Timestamp will use div/remainder
			// to init nanos. I believe Time will factor out the Date, etc.
			//
			switch (dataType) {
				case Variant.DATE:
					value.setDate(System.currentTimeMillis());
					break;
				case Variant.TIME:
					value.setTime(System.currentTimeMillis());
					break;
				case Variant.TIMESTAMP:
					value.setTimestamp(System.currentTimeMillis());
					break;
				default:
					DiagnosticJLimo.fail();
			}
		} else if (defaultValue == null) {
			value.setUnassignedNull();
		} else {
			value.setVariant(defaultValue);
		}
	}

	/**
	 * Stores the data type of the Column. Valid values for the data type are listed under com.borland.dx.dataset.Variant variables, except the BTYE_ARRAY
	 * variable. This property is used when saving changes to the local copy of the data in the StorageDataSet back to its original source. You typically do not
	 * change the data type of a column, however, the DataSet must be closed in order to set this property.
	 * <p>
	 * The setDataType() method can also be used to set the data type for a calculated field. It should not be called at run time when the Column already has data
	 * in it. On error, this method throws a DataSetException.
	 * <p>
	 * Warning: Be careful when assigning data types to your columns. Coercions, whether intentional or not, can cause data loss. For example, coercion from a
	 * FLOAT data source to an INT column type discards the fractional part of the value. If you save the value back to the server, the INT is converted back to a
	 * FLOAT thereby losing the fractional part of the data.
	 *
	 * @param dataType
	 *          The data type of the Column.
	 */
	public final void setDataType(int dataType) /*-throws DataSetException-*/ {
		Column column = this;

		// ! NOTE: ALSO SET BY CONSTRUCTOR.

		if (this.dataType != dataType || formatter == null) {

			// Changing DataType on bound Column during runtime throws an exception.
			// Doing this during Design needs to call changeColumn to realloc storage.
			if (dataSet != null) {
				column = changeColumn(dataType, calcType);
			} else
				this.dataType = dataType;

			// Note: because 'this' may no longer belong to the original dataset
			// due to the changeColumn, init a potentially new instance
			//
			column.resetColumn();

			initHasValidations();
			// !/*
			// ! if (alignment == Alignment.UNDEFINED && dataType >= Variant.BYTE && dataType <= Variant.BIGDECIMAL) // numeric range
			// ! column.setAlignment(Alignment.RIGHT | Alignment.MIDDLE);
			// ! else
			// ! column.setAlignment(Alignment.LEFT | Alignment.MIDDLE);
			// !*/
		}
	}

	// !RC Note to Steve: I found that changeColumn is replacing the old column
	// !RC with the clone, which is deadly for the DMdesigner, which expects
	// !RC its column instance variable to remain the same after this call.
	//
	private final Column changeColumn(int dataType, int calcType)
	/*-throws DataSetException-*/
	{
		if (dataSet.isOpen() && !java.beans.Beans.isDesignTime())
			DataSetException.cannotChangeColumnDataType();
		this.dataType = dataType;
		this.calcType = calcType;
		initHasValidations();
		dataSet.changeColumn(getOrdinal(), this);
		// ChangeColumn changes the column instance!!
		try {
			return dataSet.getColumn(getColumnName());
		} catch (Exception ex) {
			DiagnosticJLimo.printStackTrace(ex);
		}
		return this;
	}

	/**
	 * Stores the data type of the Column. Valid values for the data type are listed under com.borland.dx.dataset.Variant variables, except the BTYE_ARRAY
	 * variable. This property is used when saving changes to the local copy of the data in the StorageDataSet back to its original source. You typically do not
	 * change the data type of a column, however, the DataSet must be closed in order to set this property.
	 * <p>
	 * The setDataType() method can also be used to set the data type for a calculated field. It should not be called at run time when the Column already has data
	 * in it. On error, this method throws a DataSetException.
	 * <p>
	 * Warning: Be careful when assigning data types to your columns. Coercions, whether intentional or not, can cause data loss. For example, coercion from a
	 * FLOAT data source to an INT column type discards the fractional part of the value. If you save the value back to the server, the INT is converted back to a
	 * FLOAT thereby losing the fractional part of the data.
	 *
	 * @return One of the data types supported by dx.dataset.Variant
	 */
	public final int getDataType() {
		return dataType;
	}

	final Column getDisplayColumn() {
		Column result = this;
		if (pickList != null) {
			String lookupDisplayColumn = pickList.getLookupDisplayColumn();
			DataSet pickDataSet = pickList.getPickListDataSet();
			Column lookupColumn;
			if (lookupDisplayColumn != null && pickDataSet != null) {
				try {
					if (pickDataSet.getStorageDataSet() != dataSet)
						pickDataSet.open();
					result = pickDataSet.hasColumn(lookupDisplayColumn);
					if (result == null)
						result = this;
				} catch (DataSetException ex) {
					DiagnosticJLimo.printStackTrace(ex);
				}
			}
		}
		return result;
	}

	final int getDisplayType() {
		return getDisplayColumn().dataType;
	}

	final void initLookup(StorageDataSet dataSet)
	/*-throws DataSetException-*/
	{
		if (pickList != null) {
			if (pickList.getLookupDisplayColumn() == null)
				lookup = null;
			else
				lookup = new Lookup(dataSet, this, pickList);
		}
	}

	// ! /* NOT IMPLEMENTED YET.
	// ! * If non null, Indicates that this column gets is values from a column in a lookup table.
	// ! * If set, the column is read only.
	// ! * @see LookupDescriptor.
	// ! public final void setLookup(LookupDescriptor lookupDescriptor) {
	// ! this.lookupDescriptor = lookupDescriptor;
	// ! }
	// ! public final LookupDescriptor getLookup() { return lookupDescriptor; }
	// ! */

	/**
	 * Specifies the PickListDescriptor for the Column which describes the relationship between the Column and a second, separate "pick list" or "lookup" DataSet.
	 * <p>
	 * The DataSet must be closed in order to set this property.
	 * <p>
	 * The dbSwing JdbComboBox and JdbTable components will display pick list data when bound to a Column with its pickList property set. The JBCL GridControl,
	 * ListControl, and FieldControl controls can also display pick list data but are limited to displaying only the first display column defined by the
	 * PickListDescriptor.
	 *
	 * @param pickList
	 *          The PickListDescriptor for the Column which describes the relationship between the Column and a second, separate "pick list" or "lookup" DataSet.
	 */
	public final void setPickList(PickListDescriptor pickList) /*-throws DataSetException-*/ {
		int oldEditBlocked = preparePropertyRestructure();
		this.pickList = pickList;
		// !Fix bug 13537.
		// !
		this.lookup = null;
		if (dataSet != null) {
			dataSet.getStorageDataSet().setProviderPropertyChanged(true);
		}
		commitPropertyRestructure(oldEditBlocked);
		if (!is(ColumnConst.ALIGNMENT_SET))
			alignment = 0;
		if (!is(ColumnConst.CAPTION_SET))
			caption = null;
	}

	/**
	 *
	 * @return The PickListDescriptor for the Column which describes the relationship between the Column and a second, separate "pick list" or "lookup" DataSet.
	 */
	public final PickListDescriptor getPickList() {
		return pickList;
	}

	// !rac TODO. Designer must take setPersist into account when persisting a dataset.

	/**
	 * Stores whether the Column is persisted in a DataSet when the application is run.
	 * <p>
	 * Any column for which a property has been explicitly set in the JBuilder Component Inspector is automatically set (internally) to persistent. This is
	 * indicated with square brackets ([]) around the Column component's name in the Component Tree.
	 * <p>
	 * By default, the columns that display in a data-aware control are determined at run-time based on the Columns that appear in the DataSet. If your
	 * application depends on particular columns displaying in the control, set this property programmatically to <b>true</b> before the data is provided. If the
	 * source column of the persistent column changes or is deleted, the column is left empty. No Exception is thrown.
	 * <p>
	 * Calling StorageDataSet.setColumns(...) sets the persist property to true for all columns passed in.
	 *
	 * @param persist
	 *          If <b>true</b> then the Column is persisted in a DataSet when the application is run.
	 */
	public final void setPersist(boolean persist) {
		setPredicate(ColumnConst.PERSIST, persist);
	}

	/**
	 * Gets the status on whether the Column is persisted in a DataSet when the application is run.
	 *
	 * @return <b>true> if the Column is persisted in a DataSet w hen the application is run.
	 */
	public final boolean isPersist() {
		return is(ColumnConst.PERSIST);
	}

	/**
	 * Specifies whether the Column is visible or hidden. If not set, default logic is used to determine whether to display the Column. For example, the linking
	 * column(s) of the detail DataSet in a master-detail relationship are hidden by default. Accepted values for visible are listed in
	 * {@link com.borland.jb.util.TriStateProperty}.
	 *
	 * @param visible
	 */
	public final void setVisible(int visible)
	/*-throws DataSetException-*/
	{
		this.visible = visible;
		notifyPropertyChange();
	}

	/**
	 * Gets the status on whether the Column is visible or hidden.
	 * 
	 * @return The status on whether the Column is visible or hidden.
	 */
	public final int getVisible() {
		return visible;
	}

	/*
	 * Do not put in BeanInfo!!!! Used internally to advise that a column should not be shown by default.
	 */

	/**
	 * This method is used internally by other com.borland classes. Do not use this method directly.
	 *
	 * @param hidden
	 */
	public final void setHidden(boolean hidden) {
		if (hidden != isHidden()) {
			setPredicate(ColumnConst.HIDDEN, hidden);
			updateProperties(); // Bug 107147
		}
	}

	public final boolean isHidden() {
		return is(ColumnConst.HIDDEN);
	}

	final void resetColumn()
	/*-throws DataSetException-*/
	{
		// !RC TODO This now needs to be set BEFORE any formatStr stuff
		// ! setCurrency(false);
		predicates &= ~(ColumnConst.NOW_DEFAULT);
		if (!is(ColumnConst.FORMATTER_SET))
			formatter = null;
		if (!is(ColumnConst.EDITMASKER_SET))
			editMasker = null;
		if (!is(ColumnConst.ALIGNMENT_SET))
			alignment = 0;
	}

	final void initColumn()
	/*-throws DataSetException-*/
	{
		if (formatter == null) {

			formatter = nullFormatter;

			// Must check for null so a setDefaultValue() call not nullified.
			//
			if (defaultString != null)
				setDefault(defaultString);
			if (minString != null)
				setMin(minString);
			if (maxString != null)
				setMax(maxString);

			if (dataType == Variant.STRING) {
				Locale locale = getLocale();
				if (locale != null) {
					collator = (RuleBasedCollator) Collator.getInstance(locale);
					collator.setStrength(Collator.TERTIARY);
					collator.setDecomposition(Collator.FULL_DECOMPOSITION);
				} else
					collator = null;
			}

			//
			// Will get an EditMask interface if and only if there is an EditMask and the type-dependent
			// constructor above didn't choose one for us.
			//
			if (editMasker == null) {
				if (editMask != null && editMask.length() != 0)
					setEditMask(editMask);
			}
		}
	}

	public final VariantFormatter createDefaultFormatter(int type, Locale locale) {
		switch (type) {
			case Variant.DATE:
			case Variant.TIME:
			case Variant.TIMESTAMP:
			case Variant.BIGDECIMAL:
			case Variant.FLOAT:
			case Variant.DOUBLE:
				return new VariantFormatStr(null, type, locale, getScale(), getPrecision(), isCurrency());
			case Variant.STRING:
				return new StringFormatter();
			case Variant.INT:
				return new IntegerFormatter(type);
			case Variant.SHORT:
				return new ShortFormatter(type);
			case Variant.BYTE:
				return new ByteFormatter(type);
			case Variant.LONG:
				return new LongFormatter();
			case Variant.BOOLEAN:
				return new BooleanFormatter();
			case Variant.INPUTSTREAM:
				return new BinaryFormatter();
			case Variant.OBJECT:
				return new ObjectFormatter();
			case Variant.BYTE_ARRAY:
			case Variant.ASSIGNED_NULL:
				break;
			default:
				DiagnosticJLimo.println("type " + type);
				DiagnosticJLimo.fail();
		}
		return null;
	}

	private final VariantFormatter createExportFormatter(int dataType) {
		// For compatibility with TextDataFile .txt files, these formatters must
		// be used. Developer can always override with explicit setting of a formatter.
		//
		switch (dataType) {
			case Variant.DATE:
				return new DateFormatter();
			case Variant.TIME:
				return new TimeFormatter();
			case Variant.TIMESTAMP:
				return new TimestampFormatter();
			case Variant.BIGDECIMAL:
				return new BigDecimalFormatter(this.getScale());
			case Variant.FLOAT:
			case Variant.DOUBLE:
				return new DoubleFormatter(dataType);
			case Variant.STRING:
				return new StringFormatter();
			case Variant.INT:
				return new IntegerFormatter(dataType);
			case Variant.SHORT:
				return new ShortFormatter(dataType);
			case Variant.BYTE:
				return new ByteFormatter(dataType);
			case Variant.LONG:
				return new LongFormatter();
			case Variant.BOOLEAN:
				return new BooleanFormatter();
			case Variant.INPUTSTREAM:
				return new BinaryFormatter();
			case Variant.OBJECT:
				return new ObjectFormatter();
			default:
				DiagnosticJLimo.println("type " + dataType);
				DiagnosticJLimo.fail();
		}
		return null;
	}

	/**
	 * @deprecated Use DataSet.isSortable().
	 */
	public final boolean isSortable() {
		return dataType != Variant.INPUTSTREAM
				&& dataType != Variant.OBJECT;
	}

	/**
	 * Can the value be displayed textually.
	 */
	/**
	 * Stores whether the Column contains data that can be represented by a String. For example, numeric, date, and string data return <b>true</b>. Data stored as
	 * binary data return <b>false</b>.
	 *
	 * @return <b>true</b> If the Column contains data that can be represented by a String. Data stored as binary data return <b>false</b>.
	 */
	public final boolean isTextual() {
		switch (dataType) {
			case Variant.INPUTSTREAM:
			case Variant.OBJECT:
				return false;
			default:
				break; // to make compiler happy
		}
		return true;
	}

	/**
	 * Specifies whether values in the Column may be left blank at the time of posting the row of data to the DataSet. A DataSetException is thrown if the
	 * setRequired() method is called on a Column of an open DataSet.
	 *
	 * @param required
	 *          If <b>true</b> then the values in the Column may be left blank at the time of posting the row of data to the DataSet.
	 */
	public final void setRequired(boolean required)
	// ! /*-throws DataSetException-*/
	{
		if (required != isRequired()) {
			_setRequired(required);
			updateProperties();
		}
	}

	final void _setRequired(boolean required) {
		setPredicate(ColumnConst.REQUIRED, required);
	}

	/**
	 * Specifies whether values in the Column may be left blank at the time of posting the row of data to the DataSet. A DataSetException is thrown if the
	 * setRequired() method is called on a Column of an open DataSet.
	 *
	 * @return <b>true</b> if the values in the Column may be left blank at the time of posting the row of data to the DataSet.
	 */
	public final boolean isRequired() {
		if (is(ColumnConst.REQUIRED | ColumnConst.PRIMARY_KEY))
			return !isAutoIncrement();
		return false;
	}

	public final void setAutoIncrement(boolean autoIncrement) {
		if (autoIncrement != isAutoIncrement()) {
			setPredicate(ColumnConst.AUTOINCREMENT, autoIncrement);
			if (autoIncrement)
				setPredicate(ColumnConst.CLUSTERED, false);
			if (dataSet != null) {
				int oldEditBlocked = preparePropertyRestructure();
				boolean complete = false;
				try {
					dataSet.changeColumn(ordinal, this);
					complete = true;
				} finally {
					if (!complete)
						setPredicate(ColumnConst.AUTOINCREMENT, !autoIncrement);
					commitPropertyRestructure(oldEditBlocked);
				}
			}
		}
	}

	final void setPrimaryKey(boolean primaryKey) {
		setPredicate(ColumnConst.PRIMARY_KEY, primaryKey);
	}

	public final boolean isPrimaryKey() {
		return is(ColumnConst.PRIMARY_KEY);
	}

	public final boolean isAutoIncrement() {
		return (predicates & (ColumnConst.AUTOINCREMENT | ColumnConst.CLUSTERED)) == ColumnConst.AUTOINCREMENT;
	}

	public final boolean isClusteredOrAutoIncrement() {
		return is(ColumnConst.CLUSTERED | ColumnConst.AUTOINCREMENT);
	}

	final void setClustered(boolean set) {
		setPredicate(ColumnConst.CLUSTERED | ColumnConst.AUTOINCREMENT, set);
	}

	/** The 'minValue' property specifies the minimum allowable value in this column (inclusive) */
	public final String getMin() {
		return minString;
	}

	/**
	 * @updated JB2.0 to use Column.ExportFormatter() to format values. Display formats do not always allow for complete expression of a value.
	 */
	public final void setMin(String minString)
	/*-throws DataSetException-*/
	{
		this.minString = minString;

		minValue = parsePropertyValue(minValue, minString);
		initHasValidations();
	}

	/**
	 * The 'maxValue' property specifies the maximum allowable value in this column (inclusive)
	 * 
	 * @updated JB2.0 to use Column.ExportFormatter() to format values. Display formats do not always allow for complete expression of a value.
	 */
	public final String getMax() {
		return maxString;
	}

	public final void setMax(String maxString)
	/*-throws DataSetException-*/
	{

		this.maxString = maxString;

		maxValue = parsePropertyValue(maxValue, maxString);
		initHasValidations();
	}

	/** The 'minValue' property specifies the minimum allowable value in this column (inclusive) */
	public final Variant getMinValue() {
		return minValue;
	}

	public final void setMinValue(Variant minValue)
	// ! /*-throws DataSetException-*/
	{
		this.minValue = (Variant) minValue.clone();
		initHasValidations();
	}

	/** The 'maxValue' property specifies the maximum allowable value in this column (inclusive) */
	public final Variant getMaxValue() {
		return maxValue;
	}

	public final void setMaxValue(Variant maxValue)
	// ! /*-throws DataSetException-*/
	{
		this.maxValue = (Variant) maxValue.clone();
		initHasValidations();
	}

	/** The 'currency' property identifies which BigDecimal fields are to be treated as currency */
	public final void setCurrency(boolean currency)
	/*-throws DataSetException-*/
	{
		setPredicate(ColumnConst.CURRENCY, currency);
		notifyPropertyChange();
	}

	public final boolean isCurrency() {
		return is(ColumnConst.CURRENCY);
	}

	// The following set of getter and setter methods are the Column-persistable view settings
	//

	/**
	 * Optional ItemEditor that can be used by controls that support ViewManager properties (ie FieldControl, ListControl, and GridControl). The ItemEditor for
	 * this column will be passed a ColumnVariant ast the "data" object. The ColumnVariant class extends the Variant class with getColumn() and getDataSet()
	 * methods. These can be used to provide more context about Column (ie PickListDescriptor) and DataSet properties.
	 */
	public final void setItemEditor(Object itemEditor) {
		this.itemEditor = itemEditor;
	}

	/**
	 * Returns the optional Swing CellEditor for use by a visual component bound to the Column when editing.
	 * <p>
	 * For dbSwing components, itemEditor will be used if its type matches the type of editor used by the component. For example, if itemEditor is of type
	 * TableCellEditor, then it will be used when editing the Column's data in a JdbTable. Note that it is possible to use the same itemEditor with different
	 * types of Swing components simultaneously by making the itemEditor implement multiple CellEditor interfaces.
	 * <p>
	 * (For JBCL components, itemEditor will be used by controls that support ViewManager properties, for example, the FieldControl, ListControl, and GridControl.
	 * The ItemEditor for this Column is passed a ColumnVariant as the "data" object. The ColumnVariant class extends the Variant class with the getColumn() and
	 * getDataSet() methods. These can be used to provide more context about the Column, for example, its pickListDescriptor and dataSet properties.)
	 *
	 * @return The optional Swing CellEditor for use by a visual component bound to the Column when editing.
	 */
	public final Object getItemEditor() {
		return itemEditor;
	}

	/**
	 * Optional ItemPainter that can be used by controls that support ViewManager properties (ie FieldControl, ListControl, and GridControl). The ItemPainter for
	 * this column will be passed a Variant as the "data" object. The ColumnVariant class extends the Variant class with getColumn() and getDataSet() methods.
	 * These can be used to provide more context about Column (ie PickListDescriptor) and DataSet properties.
	 */
	public final void setItemPainter(Object itemPainter) /*-throws DataSetException-*/ {
		this.itemPainter = itemPainter;
		notifyPropertyChange();
	}

	/**
	 * Returns the optional Swing CellRenderer or JBCL ItemPainter for use by a visual component bound to the Column when painting.
	 *
	 * @return The optional Swing CellRenderer or JBCL ItemPainter for use by a visual component bound to the Column when painting.
	 */
	public final Object getItemPainter() {
		return itemPainter;
	}

	// The following set of getter and setter methods are the Column-persistable view settings
	//

	/**
	 * Get Font. Used by data-aware controls.
	 */
	public final Font getFont() {
		return font;
	}

	/**
	 * Set Font. Used by data-aware controls.
	 */
	public final void setFont(Font font) /*-throws DataSetException-*/ {
		this.font = font;
		notifyPropertyChange();
	}

	/**
	 * Get Alignment.
	 * 
	 * @see com.borland.dx.text.Alignment for alignment possibilities. Used by data-aware controls.
	 */
	public final int getAlignment() {
		if (alignment == 0) {
			int displayType = getDisplayType();
			// ! int displayType = dataType;
			if (displayType >= Variant.BYTE && displayType <= Variant.BIGDECIMAL) // numeric range
				alignment = Alignment.RIGHT | Alignment.MIDDLE;
			else if (displayType == Variant.BOOLEAN) // checkbox
				alignment = Alignment.CENTER | Alignment.MIDDLE;
			else if (displayType == Variant.INPUTSTREAM) // images
				alignment = Alignment.HSTRETCH | Alignment.VSTRETCH;
			else
				alignment = Alignment.LEFT | Alignment.MIDDLE;
		}
		return alignment;
	}

	/**
	 * Set Alignment. Used by data-aware controls.
	 * 
	 * @see com.borland.dx.text.Alignment for alignment possibilities.
	 */
	public final void setAlignment(int alignment) /*-throws DataSetException-*/ {
		if ((alignment & Alignment.ALL) != alignment)
			invalidArgument();
		this.alignment = alignment;
		setPredicate(ColumnConst.ALIGNMENT_SET, (alignment != Alignment.UNDEFINED));
		notifyPropertyChange();
	}

	/**
	 * Get Background. Used by data-aware controls.
	 * 
	 * @see java.awt.Color for color possibilities.
	 */
	public final Color getBackground() {
		return background;
	}

	/**
	 * Set Background. Used by data-aware controls.
	 * 
	 * @see java.awt.Color for color possibilities.
	 */
	public final void setBackground(Color background) /*-throws DataSetException-*/ {
		this.background = background;
		notifyPropertyChange();
	}

	/**
	 * Get Foreground. Used by data-aware controls.
	 * 
	 * @see java.awt.Color for color possibilities.
	 */
	public final Color getForeground() {
		return foreground;
	}

	/**
	 * Set Foreground. Used by data-aware controls.
	 * 
	 * @see java.awt.Color for color possibilities.
	 */
	public final void setForeground(Color foreground) /*-throws DataSetException-*/ {
		this.foreground = foreground;
		notifyPropertyChange();
	}

	/**
	 * Get caption (display label) used by data-aware controls. Name is not guaranteed to be unique for a StorageDataSet.
	 */
	public final String getCaption() {
		if (caption == null) {
			Column displayColumn = getDisplayColumn();
			if (displayColumn == this)
				caption = columnName;
			else
				caption = displayColumn.getCaption();
		}
		return caption;
	}

	/**
	 * Set caption (diplay label) used by data-aware controls. Name is not guaranteed to be unique for a StorageDataSet.
	 */
	public final void setCaption(String caption) /*-throws DataSetException-*/ {
		this.caption = caption;
		setPredicate(ColumnConst.CAPTION_SET, (caption != null));
		if (dataSet != null)
			notifyPropertyChange();
	}

	/**
	 * Get display mask.
	 */
	public final String getDisplayMask() {
		return displayMask;
	}

	/**
	 * Set display mask. A null or empty string select a reasonable default based on the locale.
	 */
	public final void setDisplayMask(String displayMask)
	/*-throws DataSetException-*/
	{
		this.displayMask = displayMask;
		setFormatter(new VariantFormatStr(displayMask, dataType, getLocale(), getScale(), getPrecision(), isCurrency()));
		setPredicate(ColumnConst.FORMATTER_SET, false);
		notifyPropertyChange();
	}

	/**
	 * Sets the Column to its default formatter. This is different from calling setDisplayMask(null), which explicitly asks to select a preferred formatter based
	 * on locale.
	 */
	public final void setDisplayMask()
	/*-throws DataSetException-*/
	{
		displayMask = null;
		notifyPropertyChange();
	}

	/**
	 * Get import/export display mask.
	 */
	public final String getExportDisplayMask() {
		return exportDisplayMask;
	}

	/**
	 * Set import/export display mask. A null or empty string select a reasonable default based on the locale.
	 */
	public final void setExportDisplayMask(String exportDisplayMask) {
		this.exportDisplayMask = exportDisplayMask;
		setExportFormatter(new VariantFormatStr(exportDisplayMask, dataType, getLocale(), getScale(), getPrecision(), isCurrency()));
	}

	/**
	 * Sets the Column to its default formatter for import/export. This is different from setExportDisplayMask(null), which explicitly asks to select a preferred
	 * formatter based on locale.
	 */
	public final void setExportDisplayMask()
	/*-throws DataSetException-*/
	{
		exportDisplayMask = null;
	}

	/**
	 * Get formatter
	 */
	public final VariantFormatter getFormatter() {
		if (formatter == nullFormatter) {
			int displayType = getDisplayType();
			if (displayMask != null && displayMask.length() != 0)
				formatter = new VariantFormatStr(displayMask, displayType, getLocale(), getScale(), getPrecision(), isCurrency());
			else {
				formatter = createDefaultFormatter(displayType, getLocale());
			}
		}
		return formatter;
	}

	/**
	 * Set formatter
	 */
	public final void setFormatter(VariantFormatter formatter)
	/*-throws DataSetException-*/
	{
		this.formatter = formatter;
		setPredicate(ColumnConst.FORMATTER_SET, formatter != null);
		notifyPropertyChange();
	}

	/**
	 * Get import/export formatter
	 */
	public final VariantFormatter getExportFormatter() {
		if (exportFormatter == null) {
			// Do not call setExportFormat because it will set the EXPORT_FORMATTER_SET
			// predicate.
			//
			exportFormatter = createExportFormatter(dataType);
		}
		return exportFormatter;
	}

	/**
	 * Set formatter
	 */
	public final void setExportFormatter(VariantFormatter exportFormatter) {
		setPredicate(ColumnConst.EXPORT_FORMATTER_SET, true);
		this.exportFormatter = exportFormatter;
		// ! Diagnostic.println(" SetExportFormatter: Column " + columnName + " = " + exportFormatter.getClass().getName());
	}

	/**
	 * Get edit mask.
	 */
	public final String getEditMask() {
		return editMask;
	}

	/**
	 * Set edit mask. A null or empty string select a reasonable default based on the locale.
	 */
	public void setEditMask(String editMask)
	/*-throws DataSetException-*/
	{
		this.editMask = editMask;
		// setEditMasker(new ItemEditMaskStr(editMask, formatter, dataType, locale));
		setEditMasker(new ItemEditMaskStr(editMask,
				new VariantFormatStr(editMask, dataType, getLocale(), getScale(), getPrecision(), isCurrency()),
				// formatter,
				dataType,
				locale));
		setPredicate(ColumnConst.EDITMASKER_SET, false);
	}

	/**
	 * Sets the Column to its default editMasker. This is different from calling setEditMask(null), which explicitly asks to select a preferred editMasker based
	 * on locale.
	 */
	public final void setEditMask()
	/*-throws DataSetException-*/
	{
		editMask = null;
		notifyPropertyChange();
	}

	/**
	 * Get editMasker interface.
	 */
	public final ItemEditMask getEditMasker() {
		return editMasker;
	}

	/**
	 * Set editmask interface.
	 */
	public void setEditMasker(ItemEditMask editMasker)
	/*-throws DataSetException-*/
	{
		this.editMasker = editMasker;
		setPredicate(ColumnConst.EDITMASKER_SET, editMasker != null);
		notifyPropertyChange();
	}

	// ! /*
	// ! Get Locale. The 'locale' property allows the user to identify
	// ! which locale to use in formatting a column
	// ! public final String getLocaleName() { return localeName; }
	// ! */
	// ! /*
	// ! * Set Locale by name. The 'locale' property allows the user to identify
	// ! * which locale to use in formatting a column
	// ! public final void setLocaleName(String localeName)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! this.localeName = localeName;
	// ! Locale l = LocaleUtil.getLocale(localeName);
	// ! setLocale(l);
	// ! }
	// ! */
	// !
	// ! /*
	// ! * An alternate way to ask the Column's locale to follow the default
	// ! public final void setLocaleName()
	// ! /*-throws DataSetException-*/
	// ! {
	// ! setLocaleName(null);
	// ! }
	// ! */

	/**
	 * Get Locale. The 'locale' property allows the user to identify which locale to use in formatting a column
	 */
	public final Locale getLocale() {
		if (locale != null)
			return locale;
		if (dataSet != null)
			return dataSet.getLocale();
		return null;
	}

	/**
	 * Set Locale. The 'locale' property allows the user to identify which locale to use in formatting a column
	 */
	public final void setLocale(Locale locale)
	/*-throws DataSetException-*/
	{
		if (this.locale != locale) {
			this.locale = locale;
			localeName = (locale != null) ? locale.toString() : null;
			updateProperties();
		}
		// notifyPropertyChange();
	}

	// ! NOTE! NOTE! NOTE!
	// ! This String version of setLocale MUST appear after the other one above it,
	// ! or the BeanInfo introspection logic will throw an exception, complaining
	// ! that the getter and setter are of different types.
	// ! /**
	// ! * String based version for setting a Locale. The string
	// ! * is "country_language_variant"
	// ! public final void setLocale(String localeName)
	// ! /*-throws DataSetException-*/
	// ! {
	// ! setLocaleName(localeName);
	// ! }
	// ! */

	// ! Java doesn't use the return to disambiguate duplicate methods -- how to we make a String getter? RC
	// ! /**
	// ! * String based getter for Locale. Returns the name of the Locale
	// ! */
	// ! public final String getLocale() {
	// ! return localeName;
	// ! }

	/**
	 * Get Precision. Used by data-aware controls.
	 */
	public final int getPrecision() {
		return precision;
	}

	public final boolean isPrecisionSet() {
		return is(ColumnConst.PRECISION_SET);
	}

	/**
	 * Set Precision. Used by data-aware controls.
	 */
	public final void setPrecision(int precision)
	/*-throws DataSetException-*/
	{
		// Since this is a constraint for Strings, consider it
		// a structuralChange.
		//
		// -1 means "don't care"
		if (precision < -1)
			invalidArgument();
		this.precision = precision;
		setPredicate(ColumnConst.PRECISION_SET, true);
		initHasValidations();
	}

	/**
	 * Returns the precision used for the Column in data-aware controls.
	 * 
	 * @return The precision used for the Column in data-aware controls.
	 */
	public final int getSortPrecision() {
		return sortPrecision;
	}

	/**
	 * Set Sort precision. Only used for non english locales that use Collation keys for sorting.
	 */
	public final void setSortPrecision(int sortPrecision)
	// ! /*-throws DataSetException-*/
	{
		// -1 means "don't care"
		if (sortPrecision < -1)
			invalidArgument();
		this.sortPrecision = sortPrecision;
	}

	/**
	 * Get Scale. Used by data-aware controls.
	 */
	public final int getScale() {
		return scale;
	}

	public final boolean isScaleSet() {
		return is(ColumnConst.SCALE_SET);
	}

	/**
	 * Set Scale. Used by data-aware controls.
	 */
	public final void setScale(int scale)
	/*-throws DataSetException-*/
	{
		if (this.scale != scale) {
			this.scale = scale;
			// !RC TODO: test
			// -1 is legal to mean "don't care"
			if (scale < -1)
				invalidArgument();
			if (dataSet != null)
				notifyPropertyChange();
		}
	}

	/**
	 * Get Width (number of characters) Used by data-aware controls.
	 */
	public int getWidth() {
		return width > 0 ? width : precision > 0 ? Math.min(precision, 15) : 15;
	}

	/**
	 * Set Width (number of characters) Used by data-aware controls.
	 */
	public void setWidth(int width) /*-throws DataSetException-*/ {
		if (width < 0)
			invalidArgument();
		this.width = width;
		notifyPropertyChange();
	}

	/**
	 * @return The maximal "inline" length, in bytes, for Strings and InputStreams.
	 */
	public final int getMaxInline() {
		return maxInline;
	}

	/**
	 * Set MaxInline.
	 */
	public final void setMaxInline(int maxInline)
	/*-throws DataSetException-*/
	{
		// Since this is a constraint for Strings, consider it
		// a structuralChange.
		//
		// -1 means "don't care"
		if (maxInline < -1)
			invalidArgument();
		this.maxInline = maxInline;
		initHasValidations();
	}

	/**
	 *
	 * @return The name of the Java class.
	 */
	public final String getJavaClass() {
		return javaClass == null ? null : javaClass.getName();
	}

	/**
	 * Set JavaClass
	 */
	public final void setJavaClass(String className)
	/*-throws DataSetException-*/
	{
		// Since this is a constraint for Strings, consider it
		// a structuralChange.
		//
		if (className != null) {
			try {
				javaClass = Class.forName(className);
			} catch (ClassNotFoundException ex) {
				DataSetException.throwExceptionChain(ex);
			}
		} else {
			javaClass = null;
		}
		initHasValidations();
	}

	/**
	 * Has fixed precision.
	 */
	public boolean isFixedPrecision() {
		return is(ColumnConst.FIXED_PRECISION);
	}

	/**
	 * Set fixed precision.
	 */
	public void setFixedPrecision(boolean fixedPrecision)
	/*-throws DataSetException-*/
	{
		if (fixedPrecision != isFixedPrecision()) {
			setPredicate(ColumnConst.FIXED_PRECISION, fixedPrecision);
			updateProperties();
		}
	}

	/**
	 * Clones the Column and returns it in Object
	 * 
	 * @return Cloned column.
	 */
	public Object clone() {
		return cloneColumn();
	}

	/**
	 * Clones the Column and returns it in Object
	 * 
	 * @return Cloned column.
	 */
	public Column cloneColumn() {
		try {
			Column copy = (Column) super.clone();
			copy.dataSet = null;
			return copy;
		} catch (java.lang.CloneNotSupportedException ex) {
			DiagnosticJLimo.printStackTrace(ex);
			return null;
		}
	}

	/**
	 * Creates and returns a complete copy of the Column, without unbinding the StorageDataSet.
	 * 
	 * @return A complete copy of the Column.
	 */
	public Column copy() {
		try {
			Column copy = (Column) super.clone();
			return copy;
		} catch (java.lang.CloneNotSupportedException ex) {
			DiagnosticJLimo.printStackTrace(ex);
			return null;
		}
	}

	// Do not make public. Only called by columnList when the column
	// is added and when the column is dropped from a StorageDataSet.
	//
	final synchronized void bindDataSet(StorageDataSet dataSet)
	/*-throws DataSetException-*/
	{
		// !System.err.println("bindDataSet: binding DS: " + dataSet);
		// !System.err.println(" on column: " + this);
		if (dataSet != null && this.dataSet != null && this.dataSet != dataSet)
			DataSetException.columnAlreadyBound(this.dataSet, this);

		if (this.dataSet != dataSet) {
			this.dataSet = dataSet;
		}
		resetColumn();
	}

	/*
	 * Call this for column settings that should not be performed while a DataSet is open. If the Column is not bound (dataSet not set), this is a nop. The
	 * DataSet does not need to know that something structurally related changed. Note that a side affect of dataSet.columnStructuralChange is that it will throw
	 * an exception if the DataSet is open. A structural change is often something that will invalidate the state in a DataRow or DataSetView. This includes
	 * required, readOnly, min/max etc checks.
	 */
	private final int preparePropertyRestructure()
	/*-throws DataSetException-*/
	{
		if (dataSet != null)
			return dataSet.prepareColumnPropertyRestructure();
		return OpenBlock.NotOpen;
	}

	private final void commitPropertyRestructure(int oldEditBlocked)
	/*-throws DataSetException-*/
	{
		if (dataSet != null)
			dataSet.commitPropertyRestructure(oldEditBlocked);
	}

	private final void notifyPropertyChange()
	/*-throws DataSetException-*/
	{
		if (dataSet != null) {
			resetColumn();
			initColumn();
			dataSet.notifyPropertyChange();
		}
	}

	private final void updateProperties()
	/*-throws DataSetException-*/
	{
		// commitPropertyRestructure(preparePropertyRestructure());
		if (dataSet != null)
			dataSet.updateProperties();
	}

	/**
	 * @return The ordinal position of the Column component within the DataSet.
	 */
	public int getOrdinal() {
		return ordinal;
	}

	// !RC TODO Steve, property inspectors cannot deal with getters that return a different value than the
	// !RC setter, so setOrdinal/getOrdinal don't play well with other properties. I added get/Set preferredOrdinal
	// !RC so we could have a regular property for this.

	/**
	 * @return The preferred ordinal position of the Column in the DataSet.
	 */
	public int getPreferredOrdinal() {
		return preferredOrdinal;
	}

	/**
	 * This cannot be called on an open DataSet.
	 */

	public void setPreferredOrdinal(int preferredOrdinal)
	/*-throws DataSetException-*/
	{
		int oldEditBlocked = preparePropertyRestructure();
		this.preferredOrdinal = preferredOrdinal;
		if (dataSet != null)
			dataSet.processPreferredOrdinals();
		commitPropertyRestructure(oldEditBlocked);
	}

	/**
	 * Read-only property that returns whether the Column has specified validation criteria in the form of:
	 *
	 * - readOnly - a minimum value - a maximum value - for a String column, precision greater than 0 - a registered ColumnChangeListener
	 *
	 * @return If any of the above apply, this method returns <b>true</b>. Also, calculated and aggregate columns return <b>true</b>. Otherwise this method
	 *         returns <b>false</b>.
	 *
	 */
	public boolean hasValidations() {
		return hasValidations;
	}

	final void initHasValidations() {
		if (changeListener != null
				|| readOnly
				|| minValue != null
				|| maxValue != null
				|| calcType != CalcType.NO_CALC
				|| (dataType == Variant.STRING && precision > 0)
				|| (dataSet != null && dataSet.changeListener != null)
				|| (javaClass != null && dataType == Variant.OBJECT)) {
			hasValidations = true;
		} else
			hasValidations = false;
	}

	/**
	 *
	 * @return The type of parameter in a {@link com.borland.dx.dataset.ParameterRow}.
	 */
	public int getParameterType() {
		// ! Diagnostic.println("column.getParameterType() returning " + parameterType);
		return parameterType;
	}

	public void setParameterType(int parameterType) {
		// ! Diagnostic.println("column.setParameterType(" + parameterType + ")");
		this.parameterType = parameterType;
	}

	/**
	 * Validates the specified value against the Column component's validation criteria (minimum value, maximum value, and so on). On error, this method throws a
	 * ValidationException or DataSetException as applicable.
	 *
	 * @param editDataSet
	 *          The editDataSet.
	 * @param value
	 *          The specified value.
	 */
	public void validate(DataSet editDataSet, Variant value) /*-throws DataSetException-*/ {
		if (editDataSet == null || editDataSet.getStorageDataSet().editListeners == null)
			performValidation(editDataSet, value);
		else {
			while (true) {
				try {
					performValidation(editDataSet, value);
				} catch (DataSetException ex) {
					ErrorResponse response = editDataSet.getStorageDataSet()
							.processEditError(editDataSet, this, value, ex);
					if (response.isRetry())
						continue;
					else if (response.isAbort())
						throw ex;
					else
						return;
				}
				return;
			}
		}
	}

	final boolean inValidateOrChanged() {
		return is(ColumnConst.IN_CHANGED | ColumnConst.IN_VALIDATE);
	}

	final void changed(DataSet editDataSet, Variant value) /*-throws DataSetException-*/ {
		StorageDataSet editStorageDataSet = editDataSet == null ? null : editDataSet.getStorageDataSet();
		if (changeListener != null || (editStorageDataSet != null && editStorageDataSet.changeListener != null)) {
			if (!is(ColumnConst.IN_CHANGED | ColumnConst.IN_VALIDATE)) {
				try {
					setPredicate(ColumnConst.IN_CHANGED, true);
					if (changeListener != null)
						changeListener.changed(editDataSet, this, value);
					if (editStorageDataSet != null && editStorageDataSet.changeListener != null)
						editStorageDataSet.changeListener.changed(editDataSet, this, value);
				} finally {
					setPredicate(ColumnConst.IN_CHANGED, false);
				}
			}
		}
	}

	/**
	 * Uses formatter property to produce string representation. If there is no formatter for this column, value.toString() is returned.
	 */
	/**
	 * Uses the formatter property to produce a String representation of the Variant. If there is no formatter for this Column, value.toString() is returned.
	 * 
	 * @param value
	 *          The Variant value to return formatted as a String.
	 * @return A string representation of the Variant or value.toString() if the formatter is not available for this Column.
	 */
	public final String format(Variant value) {
		getFormatter();
		if (formatter != null)
			return formatter.format(value);

		return value.toString();
	}

	private void performValidation(DataSet editDataSet, Variant value)
	/*-throws DataSetException-*/
	{
		// Note that I have placed the user validations first. This is because
		// the user can change the contents of value. Column level constraints are
		// applied after the user gets a crack at it.
		//
		StorageDataSet editStorageDataSet = editDataSet == null ? null : editDataSet.getStorageDataSet();
		if (changeListener != null || (editStorageDataSet != null && editStorageDataSet.changeListener != null)) {
			if (!is(ColumnConst.IN_VALIDATE)) {
				setPredicate(ColumnConst.IN_VALIDATE, true);
				try {
					if (changeListener != null)
						changeListener.validate(editDataSet, this, value);
					if (editStorageDataSet != null && editStorageDataSet.changeListener != null)
						editStorageDataSet.changeListener.validate(editDataSet, this, value);
					setPredicate(ColumnConst.IN_VALIDATE, false);
				} catch (Exception ex) {
					setPredicate(ColumnConst.IN_VALIDATE, false);
					// Diagnostic.printStackTrace(ex);
					ValidationException.invalidColumnValue(ex.getMessage());
				}
			}
		}

		startEdit(false);

		if (dataType == Variant.STRING && collator != null && getLocale() != null) {

			if (minValue != null && collator.compare(minValue.getString(), value.getString()) > 0)
				ValidationException.throwLessThanMin(this, format(value), format(minValue));

			if (maxValue != null && collator.compare(maxValue.getString(), value.getString()) < 0)
				ValidationException.throwGreaterThanMax(this, format(value), format(maxValue));
		} else {
			if (minValue != null && minValue.compareTo(value) > 0)
				ValidationException.throwLessThanMin(this, format(value), format(minValue));

			if (maxValue != null && maxValue.compareTo(value) < 0)
				ValidationException.throwGreaterThanMax(this, format(value), format(maxValue));
		}

		if (javaClass != null && value.getObject() != null && value.getObject().getClass() != javaClass) {
			DataSetException.invalidClass(javaClass);
		}

		if (dataType == Variant.STRING && precision > 0) {
			String string = value.getString();
			if (string != null && string.length() > precision)
				ValidationException.invalidPrecision(this);
		}
	}

	/*
	 * DO NOT MAKE PUBLIC! MUST GO THROUGH DataSet to accurately detect if column can be edited.
	 */

	final void startEdit(boolean checkEditable) /*-throws ValidationException-*/
	{
		if (readOnly || (calcType != CalcType.NO_CALC && calcType != CalcType.LOOKUP) || (checkEditable && !is(ColumnConst.EDITABLE)))
			ValidationException.readOnlyColumn(this);
	}

	final boolean canCopy() {
		return !readOnly && calcType == CalcType.NO_CALC;
	}

	final boolean isLookupOrAggregate() {
		return calcType == CalcType.LOOKUP || calcType == CalcType.AGGREGATE;
	}

	public void addColumnChangeListener(ColumnChangeListener listener)
			throws TooManyListenersException, DataSetException {
		if (listener == null)
			invalidArgument();

		if (changeListener != null)
			throw new TooManyListenersException();

		changeListener = listener;
		initHasValidations();
	}

	public synchronized void removeColumnChangeListener(ColumnChangeListener listener) {
		if (changeListener == listener) {
			changeListener = null;
			initHasValidations();
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * This is the listener to use for checks on field values that should be done before a user leaves the field. One example of when this might be useful is to
	 * check that a part number is in stock before the rest of the line item information is entered. Writing a listener for a StorageDataSet enables it to be
	 * called for all columns.
	 *
	 * @see com.borland.dx.dataset.ColumnChangeListener
	 * @return
	 */
	public final ColumnChangeListener getColumnChangeListener() {
		return changeListener;
	}

	/**
	 * @since JB2.01
	 * @see CoerceToListener
	 */
	public void addCoerceToListener(CoerceToListener listener)
			throws TooManyListenersException, DataSetException {
		if (listener == null)
			invalidArgument();

		if (coerceToListener != null)
			throw new TooManyListenersException();

		coerceToListener = listener;
	}

	/**
	 * @since JB2.01
	 * @see CoerceFromListener
	 */
	public synchronized void removeCoerceToListener(CoerceToListener listener) {
		if (coerceToListener == listener) {
			coerceToListener = null;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * @since JB2.01
	 */
	public void addCoerceFromListener(CoerceFromListener listener)
			throws TooManyListenersException, DataSetException {
		if (listener == null)
			invalidArgument();

		if (coerceFromListener != null)
			throw new TooManyListenersException();

		coerceFromListener = listener;
	}

	/**
	 * @since JB2.01
	 */
	public synchronized void removeCoerceFromListener(CoerceFromListener listener) {
		if (coerceFromListener == listener) {
			coerceFromListener = null;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * @since JB2.0
	 */
	public void addColumnPaintListener(ColumnPaintListener listener)
			throws TooManyListenersException, DataSetException {
		if (listener == null)
			invalidArgument();

		if (paintListener != null)
			throw new TooManyListenersException();

		paintListener = listener;
	}

	public synchronized void removeColumnPaintListener(ColumnPaintListener listener) {
		if (paintListener == listener)
			paintListener = null;
		else
			throw new IllegalArgumentException();
	}

	public final ColumnPaintListener getColumnPaintListener() {
		return paintListener;
	}

	// !TOSTEVE
	// !I added this for debugging -- it is really useful now

	/**
	 * @return The String representation of the Column.
	 */
	public String toString() {
		return "Column [" + columnName + "] is type " + Variant.typeName(dataType) // NORES
				+ ", sqlType " + sqlType + ", ordinal " + ordinal + " in dataset " + dataSet // NORES
				+ "\r\n" + super.toString(); // NORES
	}

	/*
	 * This method attempts to reconcile the differences between the 'this' Column and the given one. The 'this' column is the one where the reconciliations will
	 * occur. The 'column' parameter may be discarded after use.
	 *
	 * @param column A Column object whose properties we want to take on
	 */
	// WARNING: should not be used for public use. Dangerous because it can
	// change the dataType.
	//
	final void reconcile(Column column, int metaDataUpdate, boolean coerceDataType) /*-throws DataSetException-*/ {
		// if (dataSet != null && dataSet.isOpen())
		// DataSetException.cannotChangeColumnDataType();

		if (sqlType == 0)
			setSqlType(column.sqlType);

		if (!coerceDataType || this.dataType == Variant.ASSIGNED_NULL)
			this.dataType = column.dataType;

		// Reconcile: Precision
		if ((metaDataUpdate & MetaDataUpdate.PRECISION) != 0 || this.precision == -1)
			this.precision = column.precision;

		// Reconcile: Scale
		if ((metaDataUpdate & MetaDataUpdate.SCALE) != 0 || this.scale == -1)
			this.scale = column.scale;

		// Reconcile: Searchable
		if ((metaDataUpdate & MetaDataUpdate.SEARCHABLE) != 0) {
			setPredicate(ColumnConst.SEARCHABLE, column.is(ColumnConst.SEARCHABLE));
		}

		// Reconcile: RowId
		if ((metaDataUpdate & MetaDataUpdate.ROWID) != 0)
			setPredicate(ColumnConst.ROWID, column.is(ColumnConst.ROWID));

		// Reconcile: TableName, SchemaName, ServerColumnName
		if ((metaDataUpdate & MetaDataUpdate.TABLENAME) != 0) {
			this.tableName = column.tableName;
			this.schemaName = column.schemaName;
			this.serverColumnName = column.serverColumnName;
		}

		if (preferredOrdinal < 0)
			preferredOrdinal = column.preferredOrdinal;

		setPredicate(ColumnConst.NOW_DEFAULT, false);
		if (!is(ColumnConst.FORMATTER_SET)) {
			if (column.is(ColumnConst.FORMATTER_SET)) {
				formatter = column.getFormatter();
				setPredicate(ColumnConst.FORMATTER_SET, true);
			} else
				formatter = null;
		}
		if (!is(ColumnConst.EDITMASKER_SET)) {
			if (column.is(ColumnConst.EDITMASKER_SET)) {
				editMasker = column.editMasker;
				setPredicate(ColumnConst.EDITMASKER_SET, true);
			} else
				editMasker = null;
		}
		if (!is(ColumnConst.ALIGNMENT_SET)) {
			if (column.is(ColumnConst.ALIGNMENT_SET)) {
				alignment = column.alignment;
				setPredicate(ColumnConst.ALIGNMENT_SET, true);
			} else
				alignment = 0;
		}
		initHasValidations();
	}

	private void invalidArgument() {
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the StorageDataSet associated with this Column component.
	 * 
	 * @return The StorageDataSet associated with this Column component.
	 */
	public StorageDataSet getDataSet() {
		return dataSet;
	}

	private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		try {
			setLocale(locale); // Sets: localeName
			resetColumn(); // Sets: minValue,maxValue,defaultValue,NOW_DEFAULT,formatter,editMasker,collator
		} catch (DataSetException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	final boolean is(int predicate) {
		return (predicates & predicate) != 0;
	}

	final void setPredicate(int predicate, boolean set) {
		if (set)
			predicates |= predicate;
		else
			predicates &= ~predicate;
	}

	private static final boolean validCalc(int calcType) {
		return calcType >= CalcType.NO_CALC && calcType <= CalcType.LOOKUP;
	}

	final boolean canDitto() {
		return !isReadOnly() && isEditable() && getCalcType() == CalcType.NO_CALC;
	}

	//
	// !private member data
	//

	// !Is this one of the columns that uniquely id a row?
	//
	// ! transient VariantFormatter formatter;

	// !used as optimizaition to quickly find out the column for a column.
	//
	// int column;

	// !position in columns array. NOTE: must have column and position.
	// !with persistent columns of a QueryDataSet, there may be columns that
	// !have not columns of data because the Persistent columns are out of
	// !sync with query property or database itself.
	//
	// int position;

	transient int ordinal;
	int preferredOrdinal;
	int dataType;
	private transient StorageDataSet dataSet;

	// !disallows updates.
	//
	boolean readOnly;

	private String serverColumnName;
	private String tableName;
	private String schemaName;
	// This is the columnName before toLowerCase operated on it.
	//
	private String columnName;

	private String defaultString;
	private String minString;
	private String maxString;

	private transient Variant defaultValue;
	private transient Variant minValue;
	private transient Variant maxValue;

	private int sqlType;
	private int visible; // Should associated column of data be hidden
	private int calcType;
	private PickListDescriptor pickList;
	// ! private LookupDescriptor lookupDescriptor;
	private Object itemEditor;
	private Object itemPainter;

	private Font font;
	private int alignment;
	private Color background;
	private Color foreground;
	private String caption;
	private String displayMask;
	private String exportDisplayMask;
	transient VariantFormatter formatter;
	transient VariantFormatter exportFormatter;
	private String editMask;
	private transient ItemEditMask editMasker;
	private transient String localeName;
	Locale locale;
	private int precision;
	private int sortPrecision;
	private int scale;
	private int width;
	private int maxInline;
	private Class javaClass;
	int resolvable;
	private AggDescriptor aggDescriptor;
	transient ColumnChangeListener changeListener;
	transient CoerceToListener coerceToListener;
	transient CoerceFromListener coerceFromListener;
	transient ColumnPaintListener paintListener;
	private int parameterType;
	private transient RuleBasedCollator collator;
	transient Lookup lookup;
	transient boolean hasValidations;

	// 2 because has field was added.
	//
	private static final long serialVersionUID = 2L;
	int hash;

	private int predicates;

	private final static VariantFormatter nullFormatter = new StringFormatter();
}
