//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnVariant.java,v 7.0 2002/08/08 18:39:19 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

  /**
      Extends Variant class adding two methods to retrieve the DataSet and Column
      associated with this value.  Visual components that receive values from
      a DataSet will receive them as ColumnVariant.  These components can test
      the Object value received to collect more contextual information about the
      value.  For example a JBCL ItemEditor implementaion could make the following
      test:

      if (value instanceof ColumnVariant)

      to  determine if value received from a DataSet model implementation is a
      DataSet.  If so, any parsing errors could be sent to DataSet StatusListeners
      such as a StatusBar control by calling DataSet.statusMessage();
  */
public class ColumnVariant extends Variant {

/**
 * Constructs a ColumnVariant object with the specified parameters.
 * @param column    The Column component that contains the Variant.
 * @param dataSet   The DataSet that contains the Variant.
 */
  public ColumnVariant(Column column, DataSet dataSet) {
    super(column.getDisplayType());
    this.column     = column;
    this.dataSet    = dataSet;
  }

  /*
   * @return  The Column component that contains the Variant.
   */
  public Column getColumn() { return column; }

  /**
   * @return  Read-only property that returns the DataSet that contains the Variant.
   */
  public DataSet getDataSet() { return dataSet; }

  private Column    column;
  private DataSet   dataSet;
}
