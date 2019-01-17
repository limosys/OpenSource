//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ReadWriteRow.java,v 7.3.2.1 2004/10/28 00:11:23 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import java.sql.*;
import java.io.InputStream;
import java.math.*;


// Internal class that is derived from by public classes.

public abstract class ReadWriteRow extends ReadRow
{
  abstract void rowEdited() /*-throws DataSetException-*/;
  abstract void processColumnPost(RowVariant value) /*-throws DataSetException-*/;
  void notifyColumnPost(RowVariant value) /*-throws DataSetException-*/
  {
    DiagnosticJLimo.fail();

  }

  private final RowVariant getSetStorage(String columnName)
    /*-throws DataSetException-*/
  {
    rowEdited();
    return setValues[columnList.getOrdinal(columnName)];
  }

  private final RowVariant getSetStorage(int ordinal)
    /*-throws DataSetException-*/
  {
    rowEdited();
    if (columnList.hasScopedColumns()) {
      return setValues[columnList.getScopedColumns()[ordinal].ordinal];
    }
    else {
//      Diagnostic.check(columnList.getScopedColumns()[ordinal].ordinal == ordinal);
      return setValues[ordinal];
    }
  }

  final void initRowValues(boolean doValidations)
    /*-throws DataSetException-*/
  {
    int     setType;
    Column  column;
    int     count   = columnList.count;

//!   Diagnostic.check(count > 0);

//!   this.notifyFieldPost = notifyFieldPost;

    rowValues           = new RowVariant[count];
    if (doValidations)
      setValues         = new RowVariant[count];
    else
      setValues         = rowValues;

    // The initialization that takes place in the body of this loop is the
    // basic setup for a performant mechanism that deals with the
    // "possible" application requirement of field level validation.  If there
    // is no field level validation, very little overhead will be incurred.
    // The scheme uses two arrays - setValues and rowValues.  One array has the values
    // that are initially set.  If there are some validations required, the set value is passed
    // to the Column for validation (could included application event handlers).  If
    // validation is passed, the set value can be copied to the row value.  In the
    // case that there is no validation, the set value does not have to be copied to
    // the row value because they are the same - note that when there are no validations,
    // the rowValues and setValues array elements are set to the same RowVariant.
    // A RowVariant class was constructed to hold an extra column member.  This simplifies
    // the code for all the setters.
    //
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      column  = columnList.cols[ordinal];
      if (column != null) {
        setType = columnList.getSetType(ordinal);
        rowValues[ordinal] = new RowVariant(setType, column, null, doValidations);
        if (doValidations) {
          if (rowValues[ordinal].column.hasValidations) {
            hasValidations      = true;
            setValues[ordinal]  = new RowVariant(setType, column, rowValues[ordinal], true);
          }
          else {
            setValues[ordinal] = rowValues[ordinal];
          }
        }
      }
      else {
        rowValues[ordinal]  = RowVariant.nullVariant;
        setValues[ordinal]  = RowVariant.nullVariant;
      }
    }
  }


  /** Nulls out all values of the row.  Sets them to unassigned nulls.
  */
  public final void clearValues()
    /*-throws DataSetException-*/
  {
    RowVariant variant;
    rowEdited();
    for (int ordinal = 0; ordinal < rowValues.length; ++ordinal) {
      variant = setValues[ordinal];
      if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations)) {
        variant.setUnassignedNull();
        processColumnPost(variant);
      }
      else
        rowValues[ordinal].setUnassignedNull();
    }
  }

  final void _clearValues() {
    for (int ordinal = 0; ordinal < rowValues.length; ++ordinal) {
      rowValues[ordinal].setUnassignedNull();
    }
  }



  public final void     setByte(String columnName, byte value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setByte(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setShort(String columnName, short value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setShort(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setInt(String columnName, int value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setInt(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setLong(String columnName, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setLong(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations)) {
      processColumnPost(variant);
    }
  }
  public final void     setBoolean(String columnName, boolean value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setBoolean(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setDouble(String columnName, double value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setDouble(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setFloat(String columnName, float value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setFloat(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setString(String columnName, String value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setString(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations)) {
      processColumnPost(variant);
    }
  }
  public final void     setBigDecimal(String columnName, BigDecimal value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setBigDecimal(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setDate(String columnName, java.sql.Date value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setDate(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setDate(String columnName, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setDate(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTime(String columnName, Time value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setTime(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTime(String columnName, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setTime(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTimestamp(String columnName, Timestamp value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setTimestamp(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTimestamp(String columnName, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setTimestamp(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  /** @deprecated  use setInputStream(columnName, value).
  */
  public final void     setBinaryStream(String columnName, InputStream value)
    /*-throws DataSetException-*/
  {
    setInputStream(columnName, value);
  }
  public final void     setInputStream(String columnName, InputStream value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setInputStream(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setByteArray(String columnName, byte[] value, int length)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setByteArray(value, length);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setObject(String columnName, Object value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setObject(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setAssignedNull(String columnName)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setAssignedNull();
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  public final void     setUnassignedNull(String columnName)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setUnassignedNull();
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  public final void     setVariant(String columnName, Variant value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.setVariant(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  final void     copyVariant(String columnName, Variant value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    if (!(variant.column.hasValidations && variant.doValidations) || canCopy(variant)) {
      variant.setVariant(value);
      if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
        processColumnPost(variant);
    }
  }

  public final void setVariant(int ordinal, Variant value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setVariant(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  //OVERRIDDEN BY DataRow
  boolean canCopy(RowVariant value) {
    return value.column.canCopy();
  }

  final void copyVariant(int ordinal, Variant value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    if (canCopy(variant)) {
      variant.setVariant(value);
      if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
        processColumnPost(variant);
    }
  }


  void setVariantNoValidate(int ordinal, Variant value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    if (variant.rowVariant != null)
      variant.rowVariant.setVariant(value);
    else
      variant.setVariant(value);
    if (notifyColumnPost)
      notifyColumnPost(variant);
  }

  public final void     setByte(int ordinal, byte value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setByte(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  public final void     setShort(int ordinal, short value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setShort(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setInt(int ordinal, int value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setInt(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setLong(int ordinal, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setLong(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  public final void     setBoolean(int ordinal, boolean value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setBoolean(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  public final void     setDouble(int ordinal, double value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setDouble(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setFloat(int ordinal, float value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setFloat(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setString(int ordinal, String value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setString(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setBigDecimal(int ordinal, BigDecimal value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setBigDecimal(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setDate(int ordinal, java.sql.Date value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setDate(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setDate(int ordinal, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setDate(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTime(int ordinal, Time value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setTime(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTime(int ordinal, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setTime(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTimestamp(int ordinal, Timestamp value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setTimestamp(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setTimestamp(int ordinal, long value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setTimestamp(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  /** @deprecated  use setInputStream(ordinal, value).
  */
  public final void     setBinaryStream(int ordinal, InputStream value)
    /*-throws DataSetException-*/
  {
    setInputStream(ordinal, value);
  }
  public final void     setInputStream(int ordinal, InputStream value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setInputStream(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setByteArray(int ordinal, byte[] value, int length)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setByteArray(value, length);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void     setObject(int ordinal, Object value)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setObject(value);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  /**
   * Sets a Column to null (as opposed to a value that is simply not assigned).
   *
   * @param ordinal The ordinal position of the Column.
   */
  public final void  setAssignedNull(int ordinal)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setAssignedNull();
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  public final void  setUnassignedNull(int ordinal)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.setUnassignedNull();
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }

  /**
   * Set column to default value for this column.
   * @param columnName
   */
  public final void  setDefault(String columnName)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(columnName);
    variant.column.getDefault(variant);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }
  /**
   * Set column to default value for this column.
   * @param ordinal
   */

  public final void  setDefault(int ordinal)
    /*-throws DataSetException-*/
  {
    RowVariant  variant = getSetStorage(ordinal);
    variant.column.getDefault(variant);
    if (notifyColumnPost || (variant.column.hasValidations && variant.doValidations))
      processColumnPost(variant);
  }


  public void setDefaultValues()
    /*-throws DataSetException-*/
  {
    columnList.setDefaultValues(rowValues);
  }

  abstract int[] getRequiredOrdinals();

  /** Throws an exception if any required columns have not been set to a
      non null value.
  */
  public void requiredColumnsCheck()
    /*-throws ValidationException-*/
  {
    int[] requiredOrdinals = getRequiredOrdinals();
    int ordinal;
    if (requiredOrdinals != null) {
      RowVariant value;
      DiagnosticJLimo.check(rowValues != null);
      for (int index = 0; index < requiredOrdinals.length; ++index) {
        ordinal = requiredOrdinals[index];
        value = rowValues[ordinal];
        DiagnosticJLimo.check(value != null);
        if (value.isNull() && value.column != null)
          ValidationException.invalidRowValues(value.column.getColumnName());
      }
    }
  }
  /** Throws an exception if any required columns have not been set to a
      non null value.
  */
  final void requiredColumnsCheckForUpdate()
    /*-throws ValidationException-*/
  {
    int[] requiredOrdinals = getRequiredOrdinals();
    int ordinal;
    if (requiredOrdinals != null) {
      RowVariant value;
      DiagnosticJLimo.check(rowValues != null);
      for (int index = 0; index < requiredOrdinals.length; ++index) {
        ordinal = requiredOrdinals[index];
        value = rowValues[ordinal];
        DiagnosticJLimo.check(value != null);
        if (value.isAssignedNull() && value.column != null)
          ValidationException.invalidRowValues(null);
      }
    }
  }

  public void requiredColumnsCheck(RowVariant[] values)
    /*-throws ValidationException-*/
  {
    Column column;
    int ordinal;
    int[] requiredOrdinals = getRequiredOrdinals();
    if (requiredOrdinals != null) {
      for (int index = 0; index < requiredOrdinals.length; ++index) {
        ordinal = requiredOrdinals[index];
        if (values[ordinal].isNull()) {
          column = rowValues[ordinal].column;
          if (column != null && !(column.isAutoIncrement() && values[ordinal].isUnassignedNull()))
            ValidationException.missingRequiredValue(column);
        }
      }
    }
  }

  final void copySetValuesTo(ReadWriteRow row) {
    row.setValues = setValues;
  }
/*
  final void validate(DataSet dataSet, RowVariant[] values)
  {
    if (hasValidations) {
      for(int ordinal = 0; ordinal < values.length; ++ordinal) {
        rowValues[ordinal].validateAndSet(dataSet, values[ordinal]);
      }
    }
  }
*/

  private transient RowVariant[]  setValues;
          transient boolean       hasValidations;
          transient boolean       notifyColumnPost;
  private static final long serialVersionUID = 1L;
}
