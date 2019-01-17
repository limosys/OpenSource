//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ReferencedForeignKey.java,v 7.5.2.1 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

 /**
  */
public class ReferencedForeignKey extends ForeignKey {

  public void updating(DataSet dataSet, ReadWriteRow newRow, ReadRow oldRow)
    throws Exception
  {
    open();
    checkUpdatedKey(dataSet, newRow, oldRow);
  }
  private final void checkUpdatedKey(DataSet dataSet, ReadRow newRow, ReadRow oldRow) {
    if (hasModifiedKey(newRow, oldRow, _desc.referencingColumns)) {
      if (!hasSingleKey(dataSet, newRow, oldRow) && !nullValues(_locateRow, _desc.referencedColumns)) {
//        Diagnostic.println("oldRow: "+oldRow);
//        Diagnostic.println("newRow: "+newRow);
        violation();
      }
    }
  }


  public void adding(DataSet dataSet, ReadWriteRow newRow)
    throws Exception
  {
    open();
    checkKey(dataSet, newRow);
  }



  final boolean nullLocateRow() {
    String[] columnNames = _desc.referencedColumns;
    for (int index = 0; index < columnNames.length; ++index) {
      if (_locateRow.isNull(columnNames[index]))
        return true;
    }
    return false;
  }

  private final void checkKey(DataSet dataSet, ReadRow newRow) {
///*
      if (    !hasSingleKey(dataSet, newRow)
          &&  !nullLocateRow()
          &&  !validSameRow(newRow)
       )
//*/
/*
      if (    !nullReference(newRow)
          &&  !hasReference(newRow)
          &&  !validSameRow(newRow)
       )
*/
    {
/*
      Diagnostic.println("newRow:  "+newRow);
      long saveRow = _reference.getInternalRow();
      _reference.first();
      while(_reference.inBounds()) {
        Diagnostic.println("ref:  "+_reference);
        _reference.next();
      }
*/
      violation();
    }
  }

//  public void added(DataSet dataSet)
//    /*-throws DataSetException-*/
//  {
//  }

  final boolean hasSingleKey(DataSet dataSet, ReadRow newRow) {
    return _reference.dataSetStore.exists(_reference, setLocateRow(newRow));
//    return _reference.locate(setLocateRow(newRow), Locate.FIRST);
  }

  final boolean hasSingleKey(DataSet dataSet, ReadRow newRow, ReadRow oldRow) {
    setLocateRow(oldRow);
    for (int index = 0; index < _desc.referencingColumns.length; ++index) {
      if (newRow.hasColumn(_desc.referencingColumns[index]) != null)
        _locateRow.setVariant(_desc.referencedColumns[index], newRow.getVariantStorage(_desc.referencingColumns[index]));
    }
    return _reference.dataSetStore.exists(_reference, _locateRow);
//    return _reference.locate(_locateRow, Locate.FIRST);
  }

}
