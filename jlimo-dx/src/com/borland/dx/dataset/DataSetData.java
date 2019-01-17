//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/DataSetData.java,v 7.0 2002/08/08 18:39:23 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.cons.DataBits;

import java.io.*;

/**
 * Contains localization variables to store whether data is stored as 8-bit ASCII
 * characters, and whether conversations from locale-specific Unicode to multibyte
 * character sets need to take place when reading and writing data. Often used by TextDataFile.
 */
public class DataSetData implements java.io.Serializable, LoadCancel {

  // This will make sure nobody can instantiate a DataSetData:
  private DataSetData() {
  }

  /**
   * Populates the DataSetData with data and non-transient data members that
   * specify metadata information and status bits for each row.
   * The metadata information includes the column count, row count,
   * column names, data types, rowId, and whether each Column is hidden.
   * The status bits are used internally.
   * <p>
   * The data is organized in arrays of Column data. For example, if a data column
   * is of type Variant.INTEGER, an int array is used for the values of that Column.
   * <p>
   * Any columns that don't already exist in the DataSet are added.
   * <p>
   * <b>Note:</b> Physical types and properties such as sqlType, tableName, and
   * schemaName are not contained in the DataSetData. These properties are not
   * needed for editing purposes and should be extracted from the DBMS directly if needed.
   *
   * @param dataSet
   * @return
   */
  public static DataSetData extractDataSet(DataSet dataSet) /*-throws DataSetException-*/ {
    DataSetData data = new DataSetData();
    data.saveDataSet(dataSet);
    data.resetTransientMembers();
    return data;
  }

/**
 * Similar to extractDataSet except that it extracts the only the changes to
 * the DataSet (edits, inserts, and deletes) that can then be sent to the server.
 *
 * @param dataSet
 * @return
 */
  public static DataSetData extractDataSetChanges(DataSet dataSet) /*-throws DataSetException-*/ {
    DataSetData data = new DataSetData();
    data.saveDataSetChanges(dataSet);
    data.resetTransientMembers();
    return data;
  }

  public void loadDataSet(DataSet dataSet) /*-throws DataSetException-*/ {
    loadDataSetUp(dataSet);
    resetTransientMembers();
  }

  public final void cancelLoad() {
    cancel = true;
  }


//!  /*
//!      validates rows by calling DataRow.validate() for each row as they are loaded
//!      into a DataSet.  By default, this is set to false.
//!  public final void setValidateRows(boolean validateRows) {
//!    this.validateRows = validateRows;
//!  }
//!
//!  public final boolean getValidateRows() { return validateRows; }
//!  */

  private void saveDataSet(DataSet dataSet) /*-throws DataSetException-*/ {
    dataSet.open();
    this.dataSet  = dataSet;
    this.sds      = dataSet.getStorageDataSet();
    columnCount   = dataSet.getColumnCount();
    DataSetView view = dataSet.cloneDataSetView();
    rowCount      = countRows(view, 0, DataBits.EACH_ROW);
    saveMetaData();
    initNull();
    saveAllRows(view, -1, DataBits.EACH_ROW);
    view.close();
  }

  private void loadDataSetUp(DataSet dataSet) /*-throws DataSetException-*/ {
    this.dataSet = dataSet;
    this.sds     = dataSet.getStorageDataSet();
    loadMetaData();
    initNull();
    loadData();
    dataSet.open();
  }

  private void saveDataSetChanges(DataSet dataSet) /*-throws DataSetException-*/ {
    dataSet.open();
    this.dataSet       = dataSet;
    this.sds           = dataSet.getStorageDataSet();
    columnCount        = dataSet.getColumnCount();
    includeInternalRow = true;
    saveChangesOnly    = true;
    variant1           = new Variant();
    variant2           = new Variant();

    DataSetView insertedDataSet = new DataSetView();
    DataSetView deletedDataSet = new DataSetView();
    DataSetView updatedDataSet = new DataSetView();

    try {
      sds.getInsertedRows(insertedDataSet);
      sds.getDeletedRows(deletedDataSet);
      sds.getUpdatedRows(updatedDataSet);
      rowCount = countRows(insertedDataSet,        0, DataBits.INSERTED_ROWS);
      rowCount = countRows(deletedDataSet,  rowCount, DataBits.DELETED_ROWS);
      rowCount = countRows(updatedDataSet,  rowCount, DataBits.UPDATED_ROWS);
      saveMetaData();
      initNull();
      int count = -1;
      count = saveAllRows(insertedDataSet, count, DataBits.INSERTED_ROWS);
      count = saveAllRows(deletedDataSet,  count, DataBits.DELETED_ROWS);
      count = saveAllRows(updatedDataSet,  count, DataBits.UPDATED_ROWS);
      DiagnosticJLimo.check(++count == rowCount);
    }
    finally {
      insertedDataSet.close();
      deletedDataSet.close();
      updatedDataSet.close();
    }
  }

  private void resetTransientMembers() {
    dataSet      = null;
    sds          = null;
    loadVariants = null;
    columnMap    = null;
    oldDataRow   = null;
    variant1     = null;
    variant2     = null;
    dataTypes    = null;
  }

  private void saveMetaData() /*-throws DataSetException-*/ {
    int allocColumns = columnCount;
    if (includeInternalRow)
      allocColumns++;
    names     = new String[allocColumns];
    types     = new byte[allocColumns];
    data      = new Object[allocColumns];
    dataTypes = new byte[allocColumns];
    precision = new short[allocColumns];
    scale     = new short[allocColumns];
    for (int i=0; i<columnCount; ++i) {
      Column column  = dataSet.getColumn(i);
      names[i]       = column.getColumnName();
      int dataType   = column.getDataType();
      dataTypes[i]   = (byte)dataType;
      precision[i]   = (short)column.getPrecision();
      scale[i]       = (short)column.getScale();
      data[i]        = newDataColumn(dataType);
      if (column.isHidden())
        dataType |= DataBits.HIDDEN_BIT;
      if (column.isRowId())
        dataType |= DataBits.ROWID_BIT;
      if (!column.isResolvable())
        dataType |= DataBits.NOT_RESOLVABLE_BIT;
      types[i] = (byte)dataType;
    }
    if (includeInternalRow) {
      names[columnCount]     = uniqueColumnName(DataBits.INTERNALROW);
      types[columnCount]     = Variant.LONG + DataBits.NOT_RESOLVABLE_BIT;
      data[columnCount]      = newDataColumn(Variant.LONG);
      precision[columnCount] = -1;
      scale[columnCount]     =  0;
    }
    nulls      = new byte[((allocColumns*2*rowCount)+6)/8];
    statusBits = new byte[rowCount];
    version    = 1;
  }

  private void loadMetaData() /*-throws DataSetException-*/ {
    columnCount = names.length;
    Column[] columns = new Column[columnCount];
    dataTypes = new byte[columnCount];
    for (int i=0; i<columnCount; ++i) {
      int info      = types[i];
      int dataType  = info & DataBits.DATATYPE_MASK;
      dataTypes[i]  = (byte)dataType;
      Column column = new Column(names[i], names[i], dataType);
      column.setHidden((info & DataBits.HIDDEN_BIT) != 0);
      column.setRowId((info & DataBits.ROWID_BIT) != 0);
      column.setResolvable((info & DataBits.NOT_RESOLVABLE_BIT) == 0);
      column.setPrecision(precision[i]);
      column.setScale(scale[i]);
      columns[i] = column;
    }
    columnMap = sds.initData(columns, !compatibleColumns(columns), false);
//!   columnMap = sds.createColumnMap(columns, null);
  }

  private final boolean compatibleColumns(Column[] columns)
    /*-throws DataSetException-*/
  {
    if (sds.getColumnCount() < columns.length)
      return false;
    Column dsColumn;
    Column column;
    for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
      column    = columns[ordinal];
      dsColumn  = sds.hasColumn(column.getColumnName());
      if (dsColumn == null)
        return false;
      if (dsColumn.dataType != column.dataType)
        return false;
      if (dsColumn.isRowId() != column.isRowId())
        return false;
      if (dsColumn.isResolvable() != column.isResolvable())
        return false;
      if (dsColumn.getPrecision() != column.getPrecision())
        return false;
      if (dsColumn.getScale() != column.getScale())
        return false;
    }
    return true;
  }

  private String uniqueColumnName(String columnName) {
    int len = columnName.length();
    int count = 0;
    while (dataSet.hasColumn(columnName) != null) {
      columnName = columnName.substring(0,len) + Integer.toString(++count);
    }
    return columnName;
  }

  private Object newDataColumn(int dataType) /*-throws DataSetException-*/ {
    switch (dataType) {
      case Variant.BYTE:           return new byte[rowCount];
      case Variant.SHORT:          return new short[rowCount];
      case Variant.INT:            return new int[rowCount];
      case Variant.LONG:           return new long[rowCount];
      case Variant.FLOAT:          return new float[rowCount];
      case Variant.DOUBLE:         return new double[rowCount];
      case Variant.BIGDECIMAL:     return new java.math.BigDecimal[rowCount];
      case Variant.BOOLEAN:        return new boolean[rowCount];
      case Variant.DATE:
      case Variant.TIME:
      case Variant.TIMESTAMP:      return new long[rowCount];
      case Variant.STRING:         return new String[rowCount];
      case Variant.INPUTSTREAM:
      case Variant.BYTE_ARRAY:
      case Variant.OBJECT:         return new Object[rowCount];
      default:                     return null;
    }
  }

  private void loadData() /*-throws DataSetException-*/ {
    cancel       = false;
    loadVariants = sds.startLoading(this, RowStatus.LOADED, false, false, validateRows);

    try {
      for (int row=0; !cancel && row<rowCount; ++row) {
        loadRowData(row);
        status = statusBits[row];
        sds.loadRow(status);
      }
    }
    finally {
      sds.endLoading();
    }
  }

  private int countRows(DataSet dataSet, int startRow, int stepKind) /*-throws DataSetException-*/ {
    int row     = startRow;
    specialStep = stepKind;
    if (!nextRow(dataSet,true))
      return row;
    do {
      if ((status & RowStatus.UPDATED) != 0)
        row++;
      row++;
    } while (nextRow(dataSet,false));
    return row;
  }

  private int saveAllRows(DataSet dataSet, int startRow, int stepKind) /*-throws DataSetException-*/ {
    int row     = startRow;
    specialStep = stepKind;
    oldDataRow  = new DataRow(dataSet);

    if (!nextRow(dataSet,true))
      return row;
    do {
      row = saveRow(row, dataSet);
      if (stepKind != DataBits.EACH_ROW)
        dataSet.markPendingStatus(true);
    } while (nextRow(dataSet,false));
    return row;
  }

  private boolean nextRow(DataSet dataSet, boolean first) /*-throws DataSetException-*/ {
    if (first) {
      if (dataSet.isEmpty())
        return false;
      dataSet.first();
    }
    else {
      if (!dataSet.next())
        return false;
    }
    status = dataSet.getStatus();
    if (specialStep == DataBits.EACH_ROW)
      return true;

    switch (specialStep) {
      case DataBits.UPDATED_ROWS:
        while ((status & RowStatus.DELETED) != 0 || (status & RowStatus.INSERTED) != 0) {
          if (!dataSet.next())
            return false;
          status = dataSet.getStatus();
        }
        break;

      case DataBits.DELETED_ROWS:
        while ((status & RowStatus.INSERTED) != 0) {
          if (!dataSet.next())
            return false;
          status  = dataSet.getStatus();
          status &= ~RowStatus.UPDATED;
        }
        break;

      case DataBits.INSERTED_ROWS:
        while ((status & RowStatus.DELETED) != 0) {
          if (!dataSet.next())
            return false;
          status  = dataSet.getStatus();
          status &= ~RowStatus.UPDATED;
        }
        break;
    }
    return true;
  }

  private int saveRow(int row, DataSet dataSet) /*-throws DataSetException-*/ {
    if ((status & RowStatus.UPDATED) == 0) {
      statusBits[++row] = (byte)status;
      saveRowData(row,dataSet,null);
      if (includeInternalRow)
        saveInternalRow(row,dataSet);
    }
    else {
      sds.getOriginalRow(dataSet, oldDataRow);
      statusBits[++row] = RowStatus.ORIGINAL;
      saveRowData(row,oldDataRow,null);
      if (includeInternalRow)
        saveInternalRow(row,dataSet);
      statusBits[++row] = (byte)status;
      saveRowData(row,dataSet,oldDataRow);
      if (includeInternalRow)
        saveInternalRow(row,dataSet);
    }
    return row;
  }

  private void saveRowData(int row, ReadRow rowData, ReadRow original) /*-throws DataSetException-*/ {
    for (int ordinal=0; ordinal<columnCount; ++ordinal) {
      Object dataRow = data[ordinal];
      if (saveNull(ordinal,rowData,original))
        continue;
      switch (dataTypes[ordinal]) {
        case Variant.BYTE:
        {
          byte[] column = (byte[])dataRow;
          column[row] = rowData.getByte(ordinal);
          break;
        }
        case Variant.SHORT:
        {
          short[] column = (short[])dataRow;
          column[row] = rowData.getShort(ordinal);
          break;
        }
        case Variant.INT:
        {
          int[] column  = (int[])dataRow;
          column[row] = rowData.getInt(ordinal);
          break;
        }
        case Variant.LONG:
        {
          long[] column = (long[])dataRow;
          column[row] = rowData.getLong(ordinal);
          break;
        }
        case Variant.FLOAT:
        {
          float[] column = (float[])dataRow;
          column[row] = rowData.getFloat(ordinal);
          break;
        }
        case Variant.DOUBLE:
        {
          double[] column = (double[])dataRow;
          column[row] = rowData.getDouble(ordinal);
          break;
        }
        case Variant.BIGDECIMAL:
        {
          java.math.BigDecimal[] column = (java.math.BigDecimal[])dataRow;
          column[row] = rowData.getBigDecimal(ordinal);
          break;
        }
        case Variant.BOOLEAN:
        {
          boolean[] column = (boolean[])dataRow;
          column[row] = rowData.getBoolean(ordinal);
          break;
        }
        case Variant.DATE:
        {
          long[] column = (long[])dataRow;
          column[row] = rowData.getDate(ordinal).getTime();
          break;
        }
        case Variant.TIME:
        {
          long[] column = (long[])dataRow;
          column[row] = rowData.getTime(ordinal).getTime();
          break;
        }
        case Variant.TIMESTAMP:
        {
          long[] column = (long[])dataRow;
          column[row] = rowData.getVariantStorage(ordinal).getAsLong();
          break;
        }
        case Variant.STRING:
        {
          String[] column = (String[])dataRow;
          column[row] = rowData.getString(ordinal);
          break;
        }
        case Variant.INPUTSTREAM:
        {
          Object[] column = (Object[])dataRow;
          InputStream stream = rowData.getInputStream(ordinal);
          byte[] byteArray = null;
          try {
            stream.reset();
            byteArray = InputStreamToByteArray.getBytes(stream);
          }
          catch(IOException ex) {
            // Oh well, lets see what is still available.
          }
          column[row] = byteArray;
          break;
        }
        case Variant.BYTE_ARRAY:
        {
          Object[] column = (Object[])dataRow;
          column[row] = rowData.getByteArray(ordinal);;
          break;
        }
        case Variant.OBJECT:
        {
          Object[] column = (Object[])dataRow;
          column[row] = rowData.getObject(ordinal);
          break;
        }
      }
    }
  }

  private void saveInternalRow(int row, DataSet dataSet) /*-throws DataSetException-*/ {
    incNull();
    long   internalRow = dataSet.getInternalRow();
    long[] column = (long[])data[columnCount];
    column[row]   = internalRow;
  }

  private void loadRowData(int row) /*-throws DataSetException-*/ {
    for (int i=0; i<columnCount; ++i) {
      Object  dataRow = data[i];
      int     ordinal = columnMap[i];
      Variant variant = loadVariants[ordinal];
      int     nullVal = getNull();
      if (nullVal != 0) {
        if (nullVal == DataBits.UNASSIGNED_NULL)
          variant.setUnassignedNull();
        else if (nullVal == DataBits.ASSIGNED_NULL)
          variant.setAssignedNull();
        continue;
      }
      switch (dataTypes[i]) {
        case Variant.BYTE:
        {
          byte[] column = (byte[])dataRow;
          variant.setByte(column[row]);
          break;
        }
        case Variant.SHORT:
        {
          short[] column = (short[])dataRow;
          variant.setShort(column[row]);
          break;
        }
        case Variant.INT:
        {
          int[] column  = (int[])dataRow;
          variant.setInt(column[row]);
          break;
        }
        case Variant.LONG:
        {
          long[] column = (long[])dataRow;
          variant.setLong(column[row]);
          break;
        }
        case Variant.FLOAT:
        {
          float[] column = (float[])dataRow;
          variant.setFloat(column[row]);
          break;
        }
        case Variant.DOUBLE:
        {
          double[] column = (double[])dataRow;
          variant.setDouble(column[row]);
          break;
        }
        case Variant.BIGDECIMAL:
        {
          java.math.BigDecimal[] column = (java.math.BigDecimal[])dataRow;
          variant.setBigDecimal(column[row]);
          break;
        }
        case Variant.BOOLEAN:
        {
          boolean[] column = (boolean[])dataRow;
          variant.setBoolean(column[row]);
          break;
        }
        case Variant.DATE:
        {
          long[] column = (long[])dataRow;
          variant.setDate(column[row]);
          break;
        }
        case Variant.TIME:
        {
          long[] column = (long[])dataRow;
          variant.setTime(column[row]);
          break;
        }
        case Variant.TIMESTAMP:
        {
          long[] column = (long[])dataRow;
          variant.setTimestamp(column[row]);
          break;
        }
        case Variant.STRING:
        {
          String[] column = (String[])dataRow;
          variant.setString(column[row]);
          break;
        }
        case Variant.INPUTSTREAM:
        {
          Object[] column = (Object[])dataRow;
          byte[] byteArray = (byte[])column[row];
          variant.setInputStream(new InputStreamToByteArray(byteArray));
          break;
        }
        case Variant.BYTE_ARRAY:
        {
          Object[] column = (Object[])dataRow;
          byte[] byteArray = (byte[])column[row];
          variant.setByteArray(byteArray,byteArray.length);
          break;
        }
        case Variant.OBJECT:
        {
          Object[] column = (Object[])dataRow;
          variant.setObject(column[row]);
          break;
        }
      }
    }
  }

  private void initNull() {
    nullIndex  =  0;
    nullOffset = -2;
  }

  private void incNull() {
    nullOffset += 2;
    if (nullOffset >= 8) {
      nullOffset = 0;
      nullIndex++;
    }
  }

  private boolean saveNull(int ordinal, ReadRow dataRow, ReadRow original) /*-throws DataSetException-*/ {
    incNull();
    boolean ignoreData = false;
    int val = 0;
    if (dataRow.isNull(ordinal)) {
      ignoreData = true;
      val = dataRow.isAssignedNull(ordinal) ? DataBits.ASSIGNED_NULL : DataBits.UNASSIGNED_NULL;
      nulls[nullIndex] |= (byte)(val << nullOffset);
    }
    else if (saveChangesOnly && original != null) {
      dataRow.getVariant(ordinal,variant1);
      original.getVariant(ordinal,variant2);
      if (variant1.equals(variant2)) {
        ignoreData = true;
        nulls[nullIndex] |= (byte)(DataBits.UNCHANGED_NULL << nullOffset);
      }
    }
    return ignoreData;
  }

  private int getNull() {
    incNull();
    return (nulls[nullIndex] >> nullOffset) & DataBits.NULL_MASK;
  }

  // version 2 for Time values always in UTC.
  //
  private static final long serialVersionUID = 2L;

  // DataSetData Format:
  private           int            version;              // The version number of this DataSetData
  private           String[]       names;                // The column names
  private           byte[]         types;                // The column types + rowid, hidden, internalrow
  private           int            rowCount;             // The number of rows in the data.
  private           byte[]         statusBits;           // The status bits (1 byte per row).
  private           byte[]         nulls;                // The null bits (2 bit per data element)
  private           short[]        precision;            // The precision of each column
  private           short[]        scale;                // The scale of each type
  private           Object[]       data;                 // The data columns

  // Dirty global variables:
  private transient DataSet        dataSet;              // The dataSet being saved or loaded
  private transient StorageDataSet sds;
  private transient Variant[]      loadVariants;         // Used for loading only
  private transient int[]          columnMap;            // Used for loading only
  private transient boolean        cancel;               // Used for loading only (LoadCancel)
  private transient boolean        includeInternalRow;   // Used for saving only
  private transient boolean        saveChangesOnly;      // Used for saving only
  private transient int            specialStep;          // Used for saving only
  private transient DataRow        oldDataRow;           // Used for saving only
  private transient int            status;               // Used for saving only: status of current row.
  private transient Variant        variant1;             // Used for saving deltas only (in saveNull)
  private transient Variant        variant2;             // Used for saving deltas only (in saveNull)
  private transient int            columnCount;          // Number of data columns not including: metaData, statusBits, nullArray
  private transient byte[]         dataTypes;            // The JB dataTypes of the columns
  private transient int            nullIndex;            // Running index into nullArray
  private transient int            nullOffset;           // Running offset into each byte of a null value (2 bits for each data value).
  private transient boolean        validateRows;

}



