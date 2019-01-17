//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/RowVariant.java,v 7.0.2.1 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * This component is used internally by other com.borland classes.
 * You should never use this class directly.
 */
public class RowVariant extends Variant {
  static final RowVariant nullVariant = new RowVariant(UNASSIGNED_NULL);
  public RowVariant(
                        int         dataType,
                        Column      column,
                        RowVariant  rowVariant,
                        boolean     doValidations
                   )
    /*-throws DataSetException-*/
  {
    super(dataType);
    this.column         = column;
    this.doValidations  = doValidations;
    this.rowVariant = rowVariant;
  }
  public static final RowVariant getNullVariant() { return nullVariant; }
  public RowVariant(int dataType) {
    super(dataType);
  }
  public RowVariant() {
    super();
  }
  final void validateAndSet(DataSet dataSet) /*-throws DataSetException-*/ {
    column.validate(dataSet, this);
    if (rowVariant != null)
      rowVariant.setVariant(this);
    column.changed(dataSet, this);
  }

/*
  final void validateAndSet(DataSet dataSet, RowVariant value) {
    column.validate(dataSet, value);
    if (value.rowVariant != null)
      value.rowVariant.setVariant(value);
    column.changed(dataSet, value);
  }
*/

  void validate(DataSet dataSet) /*-throws DataSetException-*/ {
    column.validate(dataSet, this);
  }

  public final boolean isSet() {
    return set;
  }

          Column      column;
          RowVariant  rowVariant;
          boolean     doValidations;
          boolean     set;
  public  boolean     changed;
}


