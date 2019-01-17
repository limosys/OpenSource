//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnList.java,v 7.0.2.1 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import java.util.StringTokenizer;



/*  This module contains a high speed database column name lookup mechanism.
    This addresses the age old issue of Database Column identifiers.  Many
    database systems allow for identifing columns efficiently by specifying
    an integer "ordinal" that is bassically an index in to an array of columns.
    Typically there is also another column identification technique available
    that allows for column identification by column "name", where name is a
    String representation of a Column name such as "FristName", "Address", or
    "LastName".  Name identification is easier for database programmers to use
    because it is self describing.  It is also more robust because it can
    survive database restructure operations that change the "ordinal" position
    of a Column.  The draw back to using String identifiers has historically
    been that they provide slower access than ordinals because a column "lookup"
    must be performed to compute the ordinal position of the Column.

    The following is a description of a highly optimized system for column
    name lookups.  In this system column names are cached as immutable java
    String "references".  Once a reference is added to the cache, the
    value that the reference points to can never change.  This is because it
    is immutable and because memory in the java environment is only freed when
    no active objects are referencing it (java uses a garbage collection memory
    management scheme).  In this environment, name lookup
    can be sped up dramatically by caching the reference.  Once a String name
    is cached, future lookups can be performed by very efficient String object
    reference comparisons.  No comparison of the String value is necessary.
    once the string has been added to the cache.
    If a String name is not found in the reference cache, a more expensive
    hash lookup by value is performed then the String name is added to the
    reference cache. The reference cache is least recently "allocated".
    This means that the least recently allocated entry is reused when a new
    item needs to be added. The cache is not stored as an array of entries
    because inline compares of cache values is much faster (String arrays are
    range checked and have the overhead of array subscripting).
*/

public class ColumnList
{
  public ColumnList() {
    cols = new Column[8];
  }

  public ColumnList(int size) {
    cols = new Column[size];
  }

  ColumnList(ColumnList columnList)
    /*-throws DataSetException-*/
  {
    this(null, columnList);
  }

  ColumnList(StorageDataSet dataSet, ColumnList columnList)
    /*-throws DataSetException-*/
  {
    setColumns(dataSet, columnList.copyColumns());
  }

  public ColumnList(StorageDataSet dataSet)
    /*-throws DataSetException-*/
  {
    setColumns(null, dataSet.cloneColumns());
  }


  ColumnList cloneColumnList(ColumnList columnList, Column[] shareColumns)
    /*-throws DataSetException-*/
  {
    ColumnList newList  = new ColumnList(columnList.count);

//    newList.cols     = columnList.cloneColumns();//columnList.columns;
    newList.cols     = shareColumns;

    newList.count   = count;

    return newList;
  }

//!  ColumnList copyColumnList(ColumnList columnList)
//!    /*-throws DataSetException-*/
//!  {
//!    ColumnList newList  = new ColumnList();
//!
//!    newList.columns     = columnList.columns;
//!
//!    return newList;
//!  }


  ColumnList cloneColumnList(ColumnList columnList, String[] columnNames, Column[] shareColumns)
    /*-throws DataSetException-*/
  {
    ColumnList newList = new ColumnList(columnNames.length);

    if (columnNames == null)
      DataSetException.throwEmptyColumnNames();

    Column newScopedColumns[]  = new Column[columnNames.length];

//    newList.cols   = new Column[columnList.cols.length];
    newList.cols   = shareColumns;

    newList.count = columnList.count;

    Column  column;
    Column  cloneColumn;


    for (int index = 0; index < columnNames.length; ++index) {
      column                          = columnList.getColumn(columnNames[index]);
//      cloneColumn                     = (Column)column.clone();
//      newScopedColumns[index]         = cloneColumn;
//      newList.cols[column.ordinal] = cloneColumn;
      newScopedColumns[index]         = shareColumns[column.getOrdinal()];
    }
//!/*
//!    newList.cols = columnList.cloneColumns();//cols;
//!
//!    Diagnostic.check(cols.length == columnList.cols.length);
//!
//!    for (int index = 0; index < columnNames.length; ++index) {
//!//! Diagnostic.println("scoped column "+columnNames[index]);
//!
//!      newScopedColumns[index] = newList.getColumn(columnNames[index]);
//!    }
//!*/

    newList.scopedColumns  = newScopedColumns;

    return newList;

  }

  private final void setColumns(StorageDataSet dataSet, Column[] newColumns)
    /*-throws DataSetException-*/
  {
    cols = new Column[newColumns.length];
    try {
      for (int index = 0; index < newColumns.length; ++index)
        addColumn(dataSet, newColumns[index], true, false);
    }
    finally {
      setOrdinals();
    }
  }

  public final int addColumn(Column column)
    /*-throws DataSetException-*/
  {
    return addColumn(null, column, false, true);
  }


  final void checkChangeColumn(int oldOrdinal, Column newColumn)
    /*-throws DataSetException-*/
  {
    int hash  = newColumn.hash;

    if (newColumn.getColumnName() == null)
      DataSetException.nullColumnName();

    // Cannot use hasColumn because old and new column may be the
    // same object.
    //
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      if (ordinal != oldOrdinal && cols[ordinal].hash == hash && cols[ordinal].getColumnName().equalsIgnoreCase(newColumn.getColumnName()))
        DataSetException.duplicateColumnName();
    }
  }


  final void changeColumn(StorageDataSet dataSet, int oldOrdinal, Column newColumn)
    /*-throws DataSetException-*/
  {
    newColumn.bindDataSet(dataSet);

    cols[oldOrdinal]  = newColumn;

    setOrdinals();
  }

  public final int addColumn(StorageDataSet dataSet, Column column, boolean isUnique, boolean setOrdinals)
    /*-throws DataSetException-*/
  {
    if (!isUnique) {
      if (column.getColumnName() == null)
        DataSetException.nullColumnName();

      if (findOrdinal(column.getColumnName(), column.hash) != -1) {
        DataSetException.duplicateColumnName();
      }
    }

    // Must make a copy, because the Column is about to be
    // bound by the dataSet (iff dataSet is non-null).
    //
//!   column  = (Column)column.clone();

    column.bindDataSet(dataSet);

    checkForScopedColumns();

    if (cols == null || cols.length == count) {
      Column[] newList = new Column[cols==null?8:cols.length+8];

      System.arraycopy(cols, 0, newList, 0, count);

      cols = newList;
    }


    cols[count]  = column;

    ++count;

    if (setOrdinals)
      setOrdinals();

    return count-1;
  }

/*
  final void addColumns(StorageDataSet dataSet, Column[] newColumns)
  {
    checkForScopedColumns();

    for (int index = 0; index < newColumns.length; ++index) {
      if (newColumns[index].getColumnName() == null)
        DataSetException.nullColumnName();
    }

    for (int index = 0; index < newColumns.length; ++index)
      newColumns[index].bindDataSet(dataSet);

    cols = new Column[newColumns.length];

    System.arraycopy(newColumns, 0, cols, 0, newColumns.length);

    setOrdinals();

  }
*/


  private final void checkList() {
    for (int index = 0; index < count; ++index) {
      for (int index2 = 0; index2 < count; ++index2) {
        DiagnosticJLimo.check(index == index2 || cols[index] != cols[index2]);
        DiagnosticJLimo.check(index == index2 || cols[index].ordinal != cols[index2].ordinal);
      }
    }
  }

  final void setOrdinals()
    /*-throws DataSetException-*/
  {
    clearCache();

    for (int ordinal = 0; ordinal < count; ++ordinal)
      cols[ordinal].ordinal  = ordinal;

    checkForScopedColumns();

    if (check) checkList();
  }

  private void checkForScopedColumns()
    /*-throws DataSetException-*/
  {
    if (scopedColumns != null)
      DataSetException.cannotUpdateScopedDataRow();
  }

  final private void checkOrdinal(int ordinal)
    /*-throws DataSetException-*/
  {
    if (ordinal < 0 || ordinal >= count)
      DataSetException.invalidColumnPosition();
  }

  // WARNING.  If you change column ordering, an update notification
  // must be sent to all observers of a dataSet.  see callers of this
  // method in StorageDataSet.
  //
//!/*
//!  final void moveColumn(int oldOrdinal, int newOrdinal)
//!    /*-throws DataSetException-*/
//!  {
//!    checkForScopedColumns();
//!
//!
//!    checkOrdinal(oldOrdinal);
//!    checkOrdinal(newOrdinal);
//!
//!    Column[]  newList = new Column[count];
//!    Column    column  = cols[oldOrdinal];
//!
//!    int pos = 0;
//!    for (int ordinal = 0; ordinal < count; ++ordinal) {
//!      if (cols[ordinal] != column) {
//!        if (pos == newOrdinal)
//!          ++pos;
//!        newList[pos++]  = cols[ordinal];
//!        }
//!    }
//!    newList[newOrdinal] = column;
//!
//!    cols = newList;
//!
//!    setOrdinals();
//!  }
//!*/
  final void moveColumn(int oldOrdinal, int newOrdinal)
    /*-throws DataSetException-*/
  {
    checkForScopedColumns();

    checkOrdinal(oldOrdinal);
    checkOrdinal(newOrdinal);

    Column    column  = cols[oldOrdinal];

    int ordinal = oldOrdinal;
    if (newOrdinal < oldOrdinal) {
      for (; ordinal > newOrdinal; --ordinal)
        cols[ordinal]  = cols[ordinal-1];
    }
    else {
      for (; newOrdinal > ordinal; ++ordinal)
        cols[ordinal]  = cols[ordinal+1];
    }
    cols[ordinal]  = column;

    setOrdinals();
  }

  final void setDefaultValues(Variant[] rowValues) {
    for (int index = 0; index < rowValues.length; ++index)
      cols[index].getDefault(rowValues[index]);
  }


  public final int findOrdinal(String columnName)
  {
    return findOrdinal(columnName, Column.hash(columnName));
  }

  final int findOrdinal(String columnName, int hash)
  {
    Column column;
    if (check) checkList();
    Column[]  searchCols;
    int       searchCount;
    String    name;
    Column    searchColumn;
//    int       hash = Column.hash(columnName);

    if (scopedColumns == null) {
      searchCount = this.count;
      searchCols  = cols;
    }
    else {
      searchCount = scopedColumns.length;
      searchCols  = scopedColumns;
    }

    for (int ordinal = 0; ordinal < searchCount; ++ordinal) {
      searchColumn  = searchCols[ordinal];
      // regionMatches is better that equalsIngoreCase() because it is one less method call.
      //
      if (searchColumn.hash == hash) {
        name    = searchColumn.getColumnName();
        if (name.regionMatches(true, 0, columnName, 0, hash >> 16)) {
          // setOrdinals() may not have been called, so unless its scoped,
          // just use ordinal
          //
          if (scopedColumns != null)
            ordinal = searchCols[ordinal].ordinal;
          setOrdinal(columnName, ordinal);
          return ordinal;
        }
      }
    }
    return -1;
  }

  private final void setOrdinal(String columnName, int ordinal) {
    // Least recently allocated.
    //
    if (++slot > 19)
      slot = 0;

    switch (slot) {
      case  0: name0  = columnName; ordinal0  =  ordinal; break;
      case  1: name1  = columnName; ordinal1  =  ordinal; break;
      case  2: name2  = columnName; ordinal2  =  ordinal; break;
      case  3: name3  = columnName; ordinal3  =  ordinal; break;
      case  4: name4  = columnName; ordinal4  =  ordinal; break;
      case  5: name5  = columnName; ordinal5  =  ordinal; break;
      case  6: name6  = columnName; ordinal6  =  ordinal; break;
      case  7: name7  = columnName; ordinal7  =  ordinal; break;
      case  8: name8  = columnName; ordinal8  =  ordinal; break;
      case  9: name9  = columnName; ordinal9  =  ordinal; break;
      case 10: name10 = columnName; ordinal10 =  ordinal; break;
      case 11: name11 = columnName; ordinal11 =  ordinal; break;
      case 12: name12 = columnName; ordinal12 =  ordinal; break;
      case 13: name13 = columnName; ordinal13 =  ordinal; break;
      case 14: name14 = columnName; ordinal14 =  ordinal; break;
      case 15: name15 = columnName; ordinal15 =  ordinal; break;
      case 16: name16 = columnName; ordinal16 =  ordinal; break;
      case 17: name17 = columnName; ordinal17 =  ordinal; break;
      case 18: name18 = columnName; ordinal18 =  ordinal; break;
      case 19: name19 = columnName; ordinal19 =  ordinal; break;
    }
  }

  final void clearCache() {
    name0   = "";
    name1   = "";
    name2   = "";
    name3   = "";
    name4   = "";
    name5   = "";
    name6   = "";
    name7   = "";
    name8   = "";
    name9   = "";
    name10  = "";
    name11  = "";
    name12  = "";
    name13  = "";
    name14  = "";
    name15  = "";
    name16  = "";
    name17  = "";
    name18  = "";
    name19  = "";
    slot    = 19; // force next slot to be 0.
  }

  final int getOrdinal(String columnName)
    /*-throws DataSetException-*/
  {
    // This is faster than using an array.  In Java arrays are
    // range checked and there is overhead for subscripting.
    //
    if (columnName == name0)  return ordinal0;
    if (columnName == name1)  return ordinal1;
    if (columnName == name2)  return ordinal2;
    if (columnName == name3)  return ordinal3;
    if (columnName == name4)  return ordinal4;
    if (columnName == name5)  return ordinal5;
    if (columnName == name6)  return ordinal6;
    if (columnName == name7)  return ordinal7;
    if (columnName == name8)  return ordinal8;
    if (columnName == name9)  return ordinal9;
    if (columnName == name10) return ordinal10;
    if (columnName == name11) return ordinal11;
    if (columnName == name12) return ordinal12;
    if (columnName == name13) return ordinal13;
    if (columnName == name14) return ordinal14;
    if (columnName == name15) return ordinal15;
    if (columnName == name16) return ordinal16;
    if (columnName == name17) return ordinal17;
    if (columnName == name18) return ordinal18;
    if (columnName == name19) return ordinal19;

    int ordinal = findOrdinal(columnName, Column.hash(columnName));

    if (ordinal < 0)
      DataSetException.unknownColumnName(columnName);
    return ordinal;
  }

  final int hasOrdinal(String columnName)
  {
    // This is faster than using an array.  In Java arrays are
    // range checked and there is overhead for subscripting.
    //
    if (columnName == name0)  return ordinal0;
    if (columnName == name1)  return ordinal1;
    if (columnName == name2)  return ordinal2;
    if (columnName == name3)  return ordinal3;
    if (columnName == name4)  return ordinal4;
    if (columnName == name5)  return ordinal5;
    if (columnName == name6)  return ordinal6;
    if (columnName == name7)  return ordinal7;
    if (columnName == name8)  return ordinal8;
    if (columnName == name9)  return ordinal9;
    if (columnName == name10) return ordinal10;
    if (columnName == name11) return ordinal11;
    if (columnName == name12) return ordinal12;
    if (columnName == name13) return ordinal13;
    if (columnName == name14) return ordinal14;
    if (columnName == name15) return ordinal15;
    if (columnName == name16) return ordinal16;
    if (columnName == name17) return ordinal17;
    if (columnName == name18) return ordinal18;
    if (columnName == name19) return ordinal19;

    return findOrdinal(columnName, Column.hash(columnName));
  }


  public final Column getColumn(String columnName)
    /*-throws DataSetException-*/
  {
    return cols[getOrdinal(columnName)];
  }

  synchronized final String getBestLocateColumn(int ordinalHint) {

    if (ordinalHint > -1 && ordinalHint < count && cols[ordinalHint].isTextual())
      return cols[ordinalHint].getColumnName();

    for (int ordinal = 0; ordinal < count; ++ordinal)
      if (cols[ordinal].isTextual())
        return cols[ordinal].getColumnName();
    return null;
  }

  public final Column hasColumn(String columnName) {
    if (columnName == null)
      return null;
    int ordinal = hasOrdinal(columnName);
    if (ordinal < 0)
      return null;
    return cols[ordinal];
  }


  final Column dropColumn(Column column) /*-throws DataSetException-*/ {

    checkForScopedColumns();

    // Make sure its there, throw exception if not.
    //
    column  = getColumn(column.getColumnName());

    column.bindDataSet(null);

    if ((column.ordinal+1) < count)
      System.arraycopy(cols, column.ordinal+1, cols, column.ordinal, count-(column.ordinal+1));

    --count;

    setOrdinals();

    return column;
  }


  final boolean hasRowIds() {
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      if (cols[ordinal].isRowId())
        return true;
    }
    return false;
  }

  final void setAllRowIds(boolean setting)
  {
    for (int ordinal = 0; ordinal < count; ++ordinal)
      cols[ordinal]._setRowId(setting);
  }

  final void initColumns()
    /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      cols[ordinal].initColumn();
    }
  }

  final void initHasValidations()
    /*-throws DataSetException-*/
  {
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      cols[ordinal].initHasValidations();
    }
  }

  // The array is copied, but the elements of this list
  // are copied in.
  //
  private final Column[] copyColumns() {
    Column[] newColumns  = new Column[count];
    System.arraycopy(cols, 0, newColumns, 0, count);
    return newColumns;
  }

  // The array is copied, but the elements of this list
  // are copied in.
  //
  public final Column[] cloneColumns() {
    Column[] newColumns  = new Column[count];
    for (int index = 0; index < count; ++index) {
      newColumns[index] = (Column)cols[index].clone();
    }
    return newColumns;
  }


  final Column[] getColumns() {
    Column[] newColumns  = new Column[count];
    System.arraycopy(cols, 0, newColumns, 0, count);
    return newColumns;
  }

  final int getScopedColumnLength() {
    if (scopedColumns == null)
      return count;
    return scopedColumns.length;
  }
  final Column[] getScopedColumns() {
    if (scopedColumns == null)
      return cols;
    return scopedColumns;
  }

  final Column[] getScopedArray() {
    if (scopedColumns == null)
      return getColumnsArray();
    return scopedColumns;
  }

  final boolean hasScopedColumns() { return scopedColumns != null; }

  final String[] getColumnNames(int columnCount) {
    Column[] cols         = getScopedColumns();
    String[] columnNames  = new String[columnCount];

    for (int ordinal = 0; ordinal < columnCount; ordinal++)
      for (int innerOrdinal = 0; innerOrdinal < count; innerOrdinal++)
        if (cols[innerOrdinal].getOrdinal() == ordinal) {
          columnNames[ordinal] = cols[innerOrdinal].getColumnName();
          break;
        }

    return columnNames;
  }

  final int countCalcColumns(boolean countCalcs, boolean countAggs) {
    int calcCount = 0;

    for (int ordinal = 0; ordinal < count; ++ordinal) {
      switch (cols[ordinal].getCalcType()) {
        case CalcType.AGGREGATE:
          if (countAggs) {
            ++calcCount;
          }
           break;
        case CalcType.CALC:
          if (countCalcs)
            ++calcCount;
          break;
      }
    }
    return calcCount;
  }

  final int countAggCalcColumns() {
    int calcCount = 0;

    for (int ordinal = 0; ordinal < count; ++ordinal) {
      if (cols[ordinal].getCalcType() == CalcType.AGGREGATE) {
        AggDescriptor descriptor = cols[ordinal].getAgg();
        if (descriptor != null) {
          AggOperator aggOperator = descriptor.getAggOperator();
          if (aggOperator == null || aggOperator instanceof CustomAggOperator)
            ++calcCount;
        }
      }
    }
    return calcCount;
  }

  final String[] getCalcColumnNames(boolean getCalcs, boolean getAggs) {

    int calcCount = countCalcColumns(getCalcs, getAggs);

    if (calcCount == 0)
      return null;

    String columnNames[] =  new String[calcCount];

    int index = -1;
    for (int ordinal = 0; ordinal < count; ++ordinal) {
      switch (cols[ordinal].getCalcType()) {
        case CalcType.AGGREGATE:
          if (getAggs)
            columnNames[++index]  = cols[ordinal].getColumnName();
          break;
        case CalcType.CALC:
          if (getCalcs)
            columnNames[++index]  = cols[ordinal].getColumnName();
          break;
      }
    }

    return columnNames;
  }

  int getSetType(int ordinal) {
    if (scopedColumns != null) {
      for (int index = 0; index < scopedColumns.length; ++index) {
        if (scopedColumns[index].ordinal == ordinal)
          return cols[ordinal].getDataType();
      }
      // This prohibits the setting of a Variant that is created with this
      // setType.  Useful to keep un scoped values from being set.  Also useful
      // for locate operations to detect what should never be located on.
      //
      return Variant.UNASSIGNED_NULL;
    }
    return cols[ordinal].getDataType();
  }

  public final Column[] getColumnsArray() {
    // If bound to a DataSet, DataSet should never publicly expose its ColumnList!!!
    //
    if (count != cols.length)
      cols  = copyColumns();
    return cols;
  }



          Column[]      cols;
          int           count;
  private Column[]      scopedColumns;  // Are cols being scoped?  This means
                                        // they are based on a sub columnList.


  private int           slot;

  private String        name0;
  private String        name1;
  private String        name2;
  private String        name3;
  private String        name4;
  private String        name5;
  private String        name6;
  private String        name7;
  private String        name8;
  private String        name9;
  private String        name10;
  private String        name11;
  private String        name12;
  private String        name13;
  private String        name14;
  private String        name15;
  private String        name16;
  private String        name17;
  private String        name18;
  private String        name19;


  private int           ordinal0;
  private int           ordinal1;
  private int           ordinal2;
  private int           ordinal3;
  private int           ordinal4;
  private int           ordinal5;
  private int           ordinal6;
  private int           ordinal7;
  private int           ordinal8;
  private int           ordinal9;
  private int           ordinal10;
  private int           ordinal11;
  private int           ordinal12;
  private int           ordinal13;
  private int           ordinal14;
  private int           ordinal15;
  private int           ordinal16;
  private int           ordinal17;
  private int           ordinal18;
  private int           ordinal19;
  private final static boolean  check = false;

}

