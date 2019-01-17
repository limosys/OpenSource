//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ParameterRow.java,v 7.0 2002/08/08 18:39:30 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.sql.*;
import java.io.*;

/**
 * Useful for setting and getting Query and StoredProcedure parameter values.
 * Allows a column to be included more than once in a data row. Useful for query
 * parameters, to specify the same column multiple times, such as for multiple range comparisons.
 *
 * The <CODE>ParameterRow</CODE> component is useful when working with parameter values for SQL statements of  <CODE>QueryDataSet</CODE> and <CODE>ProcedureDataSet</CODE> components. To use this component in your application
<OL>
<LI>instantiate a  <CODE>ParameterRow</CODE>
<LI>add <CODE>Column</CODE> components
<LI>set any <CODE>Column</CODE> properties you need
<LI>set the <CODE>parameterRow</CODE> property of the <CODE>QueryDataSet</CODE> or <CODE>ProcedureDataSet</CODE> to this <CODE>ParameterRow</CODE>
</OL>

<P>Parameters can also be specified using a "scoped" <A HREF="com.borland.dx.dataset.DataRow.html"><CODE>DataRow</CODE></A> that contains only some of the <CODE>Columns</CODE> in the associated <CODE>DataSet</CODE>. However, the <CODE>Column</CODE> components in the <CODE>DataRow</CODE> map directly to the <CODE>Columns</CODE> in the <CODE>DataSet</CODE>, and therefore do not allow more than once reference in the <CODE>DataRow</CODE>.
The <CODE>ParameterRow</CODE> component allows you to specify the same column multiple times, for example, for range comparisons.


<P>For example, you may want to specify query parameters which involve two or more range comparisons against the same <CODE>Column</CODE>. For the following query statement:
<PRE><CODE>SELECT * FROM employee WHERE emp_no&gt;=:LOW AND emp_no&lt;=:HIGH</CODE></PRE>
your <CODE>ParameterRow</CODE> should have a <CODE>Column</CODE> for each of its parameter names, :LOW and :HIGH. Place values you want the query to use in these <CODE>Columns</CODE> and associate them to the query (set the <CODE>parameterRow</CODE> property of the <CODE>QueryDataSet</CODE> or <CODE>ProcedureDataSet</CODE> to this <CODE>ParameterRow</CODE>. In this way, whenever you execute your query, you can use different values for :LOW and :HIGH without having to write multiple queries for each permutation.

<!-- JDS start - remove paragraph -->
<P>For a tutorial and more information on using parameterized queries in your application, see
<A HREF="../../database/prov_paramquery.html">"Using parameterized queries to obtain data from your database"</A> in the <CITE>Database Application Developer's Guide</CITE>.
<!-- JDS end -->


 */
public class ParameterRow extends ReadWriteRow implements ColumnDesigner, Designable
{
  final void rowEdited() { rowEdited = true; }

  /**
   * Default constructor that creates a ParameterRow component.
   */
  public ParameterRow() {
    columnList      = new ColumnList();
    rowValues       = new RowVariant[0];
  }

  /**
   * Adds the specified Column to the ParameterRow, sets its parameterType as
   * specified, then returns the ordinal position of the newly added Column.
   * This method is a shortcut for setting the parameterType property on a Column
   * and then calling the addColumn(Column) method.
   *
   * @param column            The Column component to add to this ParameterRow.
   * @param parameterType     The usage type of the parameter. Valid values are
   *                          defined in {@link com.borland.dx.dataset.ParameterType} variables.
   * @return                  The ordinal position of the newly added Column.
   */
  public int addColumn(Column column, int parameterType) /*-throws DataSetException-*/ {
    column.setParameterType(parameterType);
    return addColumn(column);
  }

  /**
   *  Adds a Column to the ParameterRow, sets its columnName as specified, and
   *  its dataType to {@link com.borland.dx.dataset.ParameterType#IN}. This
   *  method is useful for parameterized queries and is a short-cut to calling
   *  other addColumn(...) methods then setting the parameterType to
   *  {@link com.borland.dx.dataset.ParameterType#IN}.
   *
   * @param columnName    The Column component (specified by its String name)
   *                      to add to this ParameterRow.
   * @param dataType      The data type of the parameter. Valid values are
   *                      data type constants defined in {@link com.borland.dx.dataset.Variant}
   *                      variables.
   */
  public void addColumn(String columnName, int dataType) /*-throws DataSetException-*/
  {
    addColumn(columnName, dataType, ParameterType.IN);
  }

  /** Adds a Column with given dataType to a Row.
  */
  /**
   *  Adds a Column to the ParameterRow, then sets its columnName, dataType,
   *  and parameterType properties as specified by its parameters. On error,
   *  this method throws a DataSetException.
   *
   * @param columnName      The Column component (specified by its String name)
   *                        to add to this ParameterRow.
   * @param dataType        The data type of the parameter. Valid values are
   *                        data type constants defined in {@link com.borland.dx.dataset.Variant}
   *                        variables.
   * @param parameterType   The usage type of the parameter. Valid values are
   *                        defined in {@link com.borland.dx.dataset.ParameterType} variables.
   */
  public void addColumn(String columnName, int dataType, int parameterType) /*-throws DataSetException-*/
  {
    Column column = new Column(columnName, columnName, dataType);
    column.setParameterType( parameterType );
    addColumn(column);
  }

  /** @deprecated Use ParameterType values instead.
   */
  public static final int IN = 1;
  /** @deprecated Use ParameterType values instead.
   */
  public static final int OUT = 4;
  /** @deprecated Use ParameterType values instead.
   */
  public static final int IN_OUT = 2;
  /** @deprecated Use ParameterType values instead.
   */
  public static final int RETURN = 5;
  /** @deprecated Use ParameterType values instead.
   */
  public static final int RESULT = 3;

  /**
   * @deprecated Use ParameterType property of Column
   */
  public int getParameterType(int ordinal) {
    try {
      Column column = getColumn(ordinal);
      return column.getParameterType();
    }
    catch (DataSetException ex) {
      return ParameterType.NONE;
    }
  }

  /**
   * @deprecated Use ParameterType property of Column
   */
  public int getParameterType(String columnName) {
    try {
      Column column = getColumn(columnName);
      return column.getParameterType();
    }
    catch (DataSetException ex) {
      return ParameterType.NONE;
    }
  }

  /** set Columns by array.  Used by builder tools.
  */
  public void setColumns(Column[] columns)
    /*-throws DataSetException-*/
  {
    for (int index = 0; index < columns.length; ++index) {
      Column column = columns[index];
      if (null == columnList.hasColumn(column.getColumnName()))
        addColumn(column);
    }
  }

  /** get Columns by array.  Used by builder tools.
  */
  public Column[] getColumns() { return columnList.getColumns(); }

  void processColumnPost(RowVariant value) /*-throws DataSetException-*/ {
    value.validateAndSet(null);
  }

  // --------------------- ColumnDesigner Implementations ---------------------------------------

  public int addColumn(Column column) /*-throws DataSetException-*/ {
    int columnIndex = columnList.addColumn(column);
    RowVariant[] oldValues = rowValues;
    init();
    if (rowEdited) {
      for (int ordinal=0; ordinal<columnIndex; ordinal++)
        setVariant(ordinal, oldValues[ordinal]);
      for (int ordinal=columnIndex+1; ordinal < rowValues.length; ordinal++)
        setVariant(ordinal, oldValues[ordinal-1]);
    }
    return columnIndex;
  }

  private final void init() {
    initRowValues(true);
    setDefaultValues();
    Column[] columns = getColumns();
    Column column;
    java.util.Vector list = null;
    requiredOrdinals = null;
    for (int index = 0; index < columns.length; ++index) {
      column = columns[index];
      if (column.isRequired()) {
        if (list == null)
          list = new java.util.Vector();
        list.add(column);
      }
    }
    if (list != null) {
      requiredOrdinals = new int[list.size()];
      for (int index = 0; index < requiredOrdinals.length; ++index) {
        requiredOrdinals[index] = ((Column)list.elementAt(0)).getOrdinal();
      }
    }
  }

  public void changeColumn(int oldOrdinal, Column newColumn) /*-throws DataSetException-*/ {
    Column oldColumn = columnList.cols[oldOrdinal];
    columnList.checkChangeColumn(oldOrdinal, newColumn);
    columnList.changeColumn(null, oldOrdinal, newColumn);
    RowVariant[] oldValues = rowValues;
    init();
    if (rowEdited) {
      for (int ordinal=0; ordinal<rowValues.length; ordinal++) {
        if (ordinal != oldOrdinal ||
            (oldColumn.getDataType()  == newColumn.getDataType() &&
             oldColumn.getScale()     == newColumn.getScale()    &&
             oldColumn.getPrecision() == newColumn.getPrecision()))
        {
          setVariant(ordinal, oldValues[ordinal]);
        }
      }
    }
  }

  public void dropColumn(String columnName) /*-throws DataSetException-*/ {
    Column dropped = columnList.dropColumn(columnList.getColumn(columnName));
    RowVariant[] oldValues = rowValues;
    init();
    if (rowEdited && dropped != null) {
      int columnIndex = dropped.getOrdinal();
      for (int ordinal=0; ordinal<columnIndex; ordinal++)
        setVariant(ordinal, oldValues[ordinal]);
      for (int ordinal=columnIndex+1; ordinal < rowValues.length; ordinal++)
        setVariant(ordinal, oldValues[ordinal+1]);
    }
  }

  private void writePersistentColumns(ObjectOutputStream stream) throws IOException {
    short persistColumns = (short)getColumnCount();
    stream.writeShort(persistColumns);
    int count = columnList.count;
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      stream.writeObject(columnList.cols[ordinal]);
    }
  }

  private void readPersistentColumns(ObjectInputStream stream) throws ClassNotFoundException, IOException {
    short persistColumns = stream.readShort();
    Column[] columns = new Column[persistColumns];
    for (int i = 0; i < persistColumns; i++) {
      columns[i] = (Column)stream.readObject();
    }
    try {
      setColumns(columns);
    }
    catch (DataSetException ex) {
      throw new IOException(ex.getMessage());
    }
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    writePersistentColumns(stream);
  }

  private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
    stream.defaultReadObject();
    columnList      = new ColumnList();
    rowValues       = new RowVariant[0];
    readPersistentColumns(stream);
  }

  int[] getRequiredOrdinals() {return requiredOrdinals;}

  private int[] requiredOrdinals;
  private boolean rowEdited;
  private static final long serialVersionUID = 1L;
}
