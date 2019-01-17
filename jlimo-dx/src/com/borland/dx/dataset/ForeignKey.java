//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ForeignKey.java,v 7.2.2.2 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;

 /**
  */
class ForeignKey extends EditAdapter {

  private boolean                 open;
          Store                   _store;
          DataSet                 _reference;
          StorageDataSet          _otherReference;
          ForeignKeyDescriptor    _desc;
          DataRow                 _locateRow;
          int[]                   _ordinals;


  void setForeignKeyDescriptor(ForeignKeyDescriptor desc) {
    _desc = desc;
  }

  void setOtherReference(StorageDataSet otherReference) {
    _otherReference = otherReference;
  }

  public void setStore(Store store) {
    _store = store;
  }

  void setReferencing(StorageDataSet referencing) {
  }

  private final void copyTo(String[] referencingColumns, ReadRow source, String[] referencedColumns, ReadWriteRow dest) {
    int ordinal;
    for (int index = 0; index < referencingColumns.length; ++index) {
      ordinal = source.columnList.hasOrdinal(referencingColumns[index]);
      if (ordinal < 0)
        dest.setUnassignedNull(referencedColumns[index]);
      else
        dest.copyVariant(referencedColumns[index], source.getVariantStorage(referencingColumns[index]));
    }
  }

  final ReadRow setLocateRow(ReadRow source) {
    if (!_locateRow.isCompatibleList(_reference))
      allocateLocateRow();
    copyTo(_desc.referencingColumns, source, _desc.referencedColumns, _locateRow);
//    DataRow.copyTo(_desc.referencingColumns, source, _desc.referencedColumns, _locateRow);
    return _locateRow;
  }

  final void violation() {
    ValidationException.foreignKeyViolation(_desc.name);
  }

  final boolean hasModifiedKey(ReadRow newRow, ReadRow oldRow, String[] cols) {
    for (int index = 0; index < cols.length; ++index) {
      if (newRow.hasColumn(cols[index]) != null) {
//      if (newRow.hasColumn(referencingColumns[index]) != null && !newRow.isUnassignedNull(referencingColumns[index])) {
        try {
          if (newRow.isModified(cols[index], oldRow, cols[index]))
            return true;
        }
        catch(Exception ex) {
          DiagnosticJLimo.printStackTrace(ex);
        }
      }
    }
    return false;
  }
/*
  final boolean hasUpdatedKey(ReadRow newRow, ReadRow oldRow) {
    for (int index = 0; index < _desc.referencedColumns.length; ++index) {
      if (newRow.isModified(_desc.referencedColumns[index], oldRow, _desc.referencingColumns[index])) {
        return hasReference(oldRow);
      }
    }
    return false;
  }
*/

  final boolean validSameRow(ReadRow newRow) {
    if (_reference.dataSetStore == _otherReference) {
      Variant value1;
      Variant value2;
      for (int index = 0; index < _desc.referencedColumns.length; ++index) {
        value1 = newRow.getVariantStorage(_desc.referencedColumns[index]);
        value2 = newRow.getVariantStorage(_desc.referencingColumns[index]);
        if (!value1.equals(value2))
          return false;
      }
      return true;
    }
    else
      return false;
  }

  final boolean nullValues(ReadRow row, String[] cols) {
    int index;
    int count = 0;
    for (index = 0; index < cols.length; ++index) {
      if (!row.isNull(cols[index])) {
        ++count;
      }
    }
    return count != cols.length;
  }

  private final void allocateLocateRow() {
    _locateRow = new DataRow(_reference, _desc.referencedColumns);
  }

  final void open() {
    if (!open || !_reference.isOpen()) {
      _reference = _desc.openReferenceTable(_otherReference, _store, _desc.referencedColumns);
      allocateLocateRow();
      _ordinals = new int[_desc.referencedColumns.length];
      for (int index = 0; index < _ordinals.length; ++index)
        _ordinals[index] = _reference.getColumn(_desc.referencedColumns[index]).getOrdinal();
      open = true;
    }
  }

  void close() {
    if (open) {
      _reference.close();
      open = false;
    }
  }


}
