//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ValidationException.java,v 7.3 2003/01/30 21:00:30 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The ValidationException class is a subclass of DataSetException and is
 * used heavily by the dataset package for Column and row-level validation
 * errors that occur when posting changed or new row(s) of data.
 */
public class ValidationException extends DataSetException {

  private final static int BASE = 1000;

  /** Attempting to assign a value to a readonly column.
  */
  public  final static int READ_ONLY_COLUMN       = BASE+1;
  /** Attempting to assign a value to a readonly dataset.
  */
  public final static int READ_ONLY_DATASET      =  BASE+2;

  /**
   *  Master rows that have detail rows linked to them cannot be deleted or have
   *  their linking columns modified.
   */
  public final static int CANNOT_ORPHAN_DETAILS    = BASE+3;

  /** Application defined validation failed throw a ColumnChangeListeners.changing()
      event handler.
  */
  public final static int INVALID_COLUMN_VALUE   = BASE+4;

/** Application defined validation failed through a ColumnChangeListeners.changing()
      event handler.
*/

  public final static int INVALID_ROW_VALUES   = BASE+5;

  /* Application defined validation failed through a EditListner.deleting()
      event handler.
  public final static int INVALID_DELETE        = BASE+6;
  */

  /**  Value entered for column is less than the column min property.
  */
  public final static int LESS_THAN_MIN        = BASE+7;

  /** Value entered for column is greater than the column max property.
  */
  public final static int GREATER_THAN_MAX     = BASE+8;

  /**
   *  String values for this column cannot exceed the precision length set
   *  in the Column.precision property. This property can be implicitly set
   *  when data is retrieved from a provider.
   *
   *  If data is being provided from a JDBC driver, the precision specified
   *  by the JDBC result set will be propagated to the Column.precision property.
   *  This propagation can be overridden by explicitly setting the Column.precision
   *  property.
   */
  public final static int INVALID_PRECISION     = BASE+9;

  /**
   * Cannot ditto into an existing row.
   * By default, you cannot ditto over an existing row; you must insert
   * an empty row and ditto into it.
   */
  public static final int CANNOT_DITTO_EXISTING = BASE+10;

  /** Cannot parse value or generic format error.
  */
  public static final int INVALID_FORMAT = BASE+11;

  /**
   *  Application error caused by an Exception in a application event handler.
   */
  public static final int APPLICATION_ERROR = BASE+12;
  /** No Rows to delete.
  */
  public static final int NO_ROWS_TO_DELETE = BASE+13;
  /** Row insertion not allowed
  */
  public static final int INSERT_NOT_ALLOWED = BASE+14;
  /** Row editing not allowed.
  */
  public static final int UPDATE_NOT_ALLOWED = BASE+15;
  /** Row deleting not allowed.
  */
  public static final int DELETE_NOT_ALLOWED = BASE+16;

  /**
   * The key value is a duplicate.
   */
  public static final int DUPLICATE_KEY               = BASE+17;

  /**
   * Violation of foreign key constraint violation has occurred.
   */
  public static final int FOREIGN_KEY_VIOLATION       = BASE+18;

  /**
   * Violation of foreign key constraint definition error.
   */
  public static final int FOREIGN_KEY_ERROR           = BASE+19;

  /**
   * Could not add constraint because a unique or foreign key constraint already exists with that name
   */
  public static final int CONSTRAINT_NAME_EXISTS           = BASE+20;

  static final void     readOnlyColumn(Column column)
  /*-throws ValidationException-*/
  {
    throwValidationException(READ_ONLY_COLUMN, Res.bundle.getString(ResIndex.ReadOnlyColumn), column);
  }

  static final void     readOnlyDataSet()
  /*-throws ValidationException-*/
  {
    throwValidationException(READ_ONLY_DATASET, Res.bundle.getString(ResIndex.ReadOnlyDataSet), null);
  }

  static final void     insertNotAllowed()
    /*-throws ValidationException-*/
  {
    throwValidationException(INSERT_NOT_ALLOWED, Res.bundle.getString(ResIndex.InsertNotAllowed), null);
  }

  static final void     updateNotAllowed()
    /*-throws ValidationException-*/
  {
    throwValidationException(UPDATE_NOT_ALLOWED, Res.bundle.getString(ResIndex.UpdateNotAllowed), null);
  }

  static final void     deleteNotAllowed()
    /*-throws ValidationException-*/
  {
    throwValidationException(DELETE_NOT_ALLOWED, Res.bundle.getString(ResIndex.DeleteNotAllowed), null);
  }

  static final void     cannotOrphanDetails(String detailName)
    /*-throws ValidationException-*/
  {
    throwValidationException(CANNOT_ORPHAN_DETAILS, Res.bundle.format(ResIndex.CannotOrphanDetails, detailName), null);
  }

  static final void     cannotOrphanDetails(Exception ex)
    /*-throws DataSetException-*/
  {
    throwValidationException(CANNOT_ORPHAN_DETAILS, Res.bundle.getString(ResIndex.CannotOrphanDetails), null, ex);
  }

  static final void     cannotDittoExisting()
    /*-throws ValidationException-*/
  {
    throwValidationException(CANNOT_DITTO_EXISTING, Res.bundle.getString(ResIndex.CannotDittoExisting), null);
  }

  static final void     invalidColumnValue(String message)
    /*-throws ValidationException-*/
  {
    if (message == null)
      message = Res.bundle.getString(ResIndex.InvalidColumnValue);

    throwValidationException(INVALID_COLUMN_VALUE, message, null);
  }
  static final void     invalidRowValues(String message)
    /*-throws ValidationException-*/
  {
    if (message == null)
      message = Res.bundle.getString(ResIndex.InvalidRowValues);
    throwValidationException(INVALID_ROW_VALUES, message, null);
  }

  static final void     missingRequiredValue(Column column)
    /*-throws ValidationException-*/
  {
    String  message = Res.bundle.format(ResIndex.MissingRequiredValue, new String[] {column.getColumnName()});
    throwValidationException(INVALID_ROW_VALUES, message, column);
  }

/*
  static final void     invalidDelete(String message)
  {
    if (message == null)
      message = Res.bundle.getString(ResIndex.InvalidDelete);
    throwValidationException(INVALID_DELETE, message, null);
  }
  */

  static final void throwLessThanMin(Column column, String value,  String minValue)
    /*-throws ValidationException-*/
  {
    throwValidationException(LESS_THAN_MIN, Res.bundle.format(ResIndex.LessThanMin, new String[]{column.getColumnName(), value, minValue}), column);
  }

  static final void throwGreaterThanMax(Column column, String value,  String maxValue)
    /*-throws ValidationException-*/
  {
    throwValidationException(GREATER_THAN_MAX, Res.bundle.format(ResIndex.GreaterThanMax, new String[]{column.getColumnName(), value, maxValue}), column);
  }

  static final void invalidPrecision(Column column)
    /*-throws ValidationException-*/
  {
    throwValidationException(INVALID_PRECISION, Res.bundle.format(ResIndex.InvalidPrecision, new String[]{column.getColumnName(), Integer.toString(column.getPrecision())}), column);
  }
  static final void throwApplicationError(Exception ex)
    /*-throws DataSetException-*/
  {
    throwValidationException(APPLICATION_ERROR, ex.getMessage(), null, ex);
  }
  static final void     noRowsToDelete(DataSet dataSet) /*-throws DataSetException-*/ {
    throwValidationException(NO_ROWS_TO_DELETE, Res.bundle.format(ResIndex.NoRowsToDelete, dataSet.getTableName()), (Column)null);
  }

  static final void     foreignKeyViolation(String name)
    /*-throws ValidationException-*/
  {
    throwValidationException(FOREIGN_KEY_VIOLATION, Res.bundle.format(ResIndex.ForeignKeyViolation, name), null);
  }

  public static final void     foreignKeyViolation(String name, String sql)
    /*-throws ValidationException-*/
  {
    throwValidationException(FOREIGN_KEY_ERROR, Res.bundle.format(ResIndex.ForeignKeyViolationSql, name, sql), null);
  }

  static final void     foreignKeyPrimaryMissing(String name, String referencedTable)
    /*-throws ValidationException-*/
  {
    throwValidationException(FOREIGN_KEY_ERROR, Res.bundle.format(ResIndex.ForeignKeyPrimaryMissing, name, referencedTable), null);
  }

  public static final void     foreignKeyColumnMismatch(String name)
    /*-throws ValidationException-*/
  {
    throwValidationException(FOREIGN_KEY_ERROR, Res.bundle.format(ResIndex.ForeignKeyColumnMismatch, name), null);
  }

  static final void     tableMissing(String name)
    /*-throws ValidationException-*/
  {
    throwValidationException(FOREIGN_KEY_ERROR, Res.bundle.format(ResIndex.TableMissing, name), null);
  }

  static final void     constraintMissing(String name)
    /*-throws ValidationException-*/
  {
    throwValidationException(FOREIGN_KEY_ERROR, Res.bundle.format(ResIndex.ConstraintMissing, name), null);
  }

  public static final void constraintNameUsed(String name)
    /*-throws ValidationException-*/
  {
    throwValidationException(CONSTRAINT_NAME_EXISTS, Res.bundle.format(ResIndex.ConstraintNameUsed, name), null);
  }

  /**
   * Duplicates found
   * @param dataSet
   * @param descriptor
   */
  public static final void     duplicateKey(StorageDataSet dataSet, SortDescriptor descriptor) /*-throws DataSetException-*/ { duplicateKey(dataSet, descriptor.getIndexName()); }

  public static final void     duplicateKey(StorageDataSet dataSet, String name) /*-throws DataSetException-*/ { duplicateKey(dataSet.getStoreName() == null ? dataSet.getTableName() : dataSet.getSchemaStoreName(), name);}

  public static final void     duplicateKey(String tableName, String indexName) /*-throws DataSetException-*/ { throwValidationException(DUPLICATE_KEY, Res.bundle.format(ResIndex.DuplicateKey, tableName, indexName!=null ? indexName : Res.getString(ResIndex.SortIndex)), (Column)null);}
/**
 * Constructs a ValidationException object that contains the error code,
 * the error that occurred, and the Column in which it occurred.
 *
 * @param errorCode   One of the ValidationException variables.
 * @param error       The message describing the error that occurred.
 * @param column      The column containing the error that occurred.
 */
  public ValidationException(int errorCode, String error, Column column) {
    super(errorCode, error);
    this.column  = column;
  }

  /**
   *  Constructs a ValidationException object that contains the error code,
   *  the error that occurred, the Column in which it occurred, and the exception.
   *
   * @param errorCode   One of the ValidationException variables.
   * @param error       The message describing the error that occurred.
   * @param column      The column containing the error that occurred.
   * @param ex          The exception that occurred.
   */
  public ValidationException(int errorCode, String error, Column column, Exception ex) {
    super(errorCode, error, ex);
    this.column  = column;
  }

  static final void throwValidationException( int       errorCode,
                                              String    error,
                                              Column    column
                                            )
  /*-throws ValidationException-*/
  {
    throw new ValidationException(errorCode, error, column);
  }

  static final void throwValidationException( int       errorCode,
                                              String    error,
                                              Column    column,
                                              Exception ex
                                            )
    /*-throws DataSetException-*/
  {
    if (ex instanceof DataSetException)
      throw (DataSetException)ex;
    throw new ValidationException(errorCode, error, column, ex);
  }

  /**
   * Cannot parse value or generic format error. If message is <b>null</b>, throws a
   * ValidationException of {@link #INVALID_FORMAT}. If message is not <b>null</b>, returns the
   * offending column for this error, or returns <b>null<b> if it is a row level error.
   *
   * @param ex
   * @param columnName
   * @param message
   */
  public static final void invalidFormat(Exception ex, String columnName, String message)
    /*-throws ValidationException-*/
  {
    if (message == null)
      message = Res.bundle.getString(ResIndex.InvalidCharacters);
    throw new ValidationException(INVALID_FORMAT, message, null, ex);
  }


  /**
   * The offending Column for this error, or <b>null</b> if it is a
   * row level error.
   *
   * @return    The offending Column for this error, or <b>null</b> if it is a
   *            row level error.
   */
  public final Column getErrorColumn() {
    return column;
  }

  private Column  column;
}
