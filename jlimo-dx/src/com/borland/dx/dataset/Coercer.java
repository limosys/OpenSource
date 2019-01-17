//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Coercer.java,v 7.0 2002/08/08 18:39:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

//!import java.sql.*;

/**
 * This class is used internally by other com.borland classes.
 * You should never use this class directly.
 * To access the functionality provided by this utility class, see CoerceToListener
 * and CoerceFromListener classes.
 */
public class Coercer {

  /*  Previous JavaDoc comments.

      dataSet is the dataSet being provided or resolved for.

      coerceValues is an array of values with a dimension of dataSet.getColumnCount().
      Columns that need to be coereced should have a variant in their ordinal position (Column.getOrdinal())
      of coerceValues.  If no coercion is needed for a Column, its ordcinal position
      in coerceValues should be null.

      firstOrdinal is the first ordinal that needs coercion.
      lastOrdinal is the last+1 ordinal that needs coercion.
  */

  /**
   *  This constructor is used internally by other com.borland classes.
   *  You should never use this constructor directly.
   *
   * @param dataSet
   * @param coerceValues
   * @param firstOrdinal
   * @param lastOrdinal
   */
  public Coercer(StorageDataSet dataSet, Variant[] coerceValues, int firstOrdinal, int lastOrdinal) {
    this.dataSet      = dataSet;
    this.coerceValues = coerceValues;
    this.firstOrdinal = firstOrdinal;
    this.lastOrdinal  = lastOrdinal;
  }

  /*  Previous JavaDoc comments.
      initialize coercer for coercing an array of values at a time.
      destValues is the array of values that will be passed into coerceToColumn.
  */

  /**
   * This method is used internally by other com.borland classes.
   * You should never use this method directly.
   *
   * @param destValues
   * @return
   */
  public Variant[] init(Variant[] destValues) {
    values  = new Variant[destValues.length];
    System.arraycopy(destValues, 0, values, 0, values.length);
    for (int ordinal = firstOrdinal; ordinal < lastOrdinal; ++ordinal) {
      if (coerceValues[ordinal] != null)
        values[ordinal] =  coerceValues[ordinal];
    }
    return values;
  }

  /**
   *  This method is used internally by other com.borland classes.
   *  You should never use this method directly.
   *
   * @param columns
   * @param destValues
   */
  public void coerceToColumn(Column[] columns, Variant[] destValues)
    /*-throws DataSetException-*/
  {
    Column column;
    for (int ordinal = firstOrdinal; ordinal < lastOrdinal; ++ordinal) {
      if (coerceValues[ordinal] != null) {
        column = columns[ordinal];
        if (column.coerceToListener != null)
          column.coerceToListener.coerceToColumn(dataSet, column, coerceValues[ordinal], destValues[ordinal]);
        else
          destValues[ordinal].setAsVariant(coerceValues[ordinal]);
      }
    }
  }

  /**
      Coerce value to coerceValues[column.getOrdinal()].
      @see #init(Variant[] destValues)
  */
  public Variant coerceFromColumn(Column column, Variant value)
    /*-throws DataSetException-*/
  {
    int ordinal = column.ordinal;
    if (coerceValues[ordinal] != null) {
      if (column.coerceFromListener != null)
        column.coerceFromListener.coerceFromColumn(dataSet, column, value, coerceValues[ordinal]);
      else
        coerceValues[ordinal].setAsVariant(value);
      return coerceValues[ordinal];
    }
    return value;
  }

  private transient StorageDataSet            dataSet;
  private transient Variant[]                 values;
  private transient Variant[]                 coerceValues;
  private transient int                       firstOrdinal;
  private transient int                       lastOrdinal;

}
