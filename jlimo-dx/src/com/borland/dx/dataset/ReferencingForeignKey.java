//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ReferencingForeignKey.java,v 7.1.2.1 2004/10/15 19:54:21 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;
import com.borland.dx.dataset.cons.ForeignKeyAction;

import com.borland.jb.util.ErrorResponse;
import com.borland.jb.util.DiagnosticJLimo;

 /**
  */
public class ReferencingForeignKey extends ForeignKey {

  boolean _savepointed;
  Object  _savepoint;
  DataRow _savepointRow;

  private final void setSavepoint() {
    if (!_savepointed) {
      DiagnosticJLimo.check(_savepoint == null);
      _savepoint = _store.getStoreInternals().setSavepoint(_desc.name);
      _savepointed = true;
    }
  }
  private final void setSavepointRow(ReadRow row) {
    if (!_savepointed) {
      if (_savepointRow == null || !_savepointRow.isCompatibleList(_reference))
        _savepointRow = new DataRow(_reference, _desc.referencedColumns);
      DataRow.copyTo(_desc.referencingColumns, row, _desc.referencedColumns, _savepointRow);
    }
  }

  private final void rollback() {
    if (_savepointed) {
      try {
        _store.getStoreInternals().rollback(_savepoint);
      }
      finally {
        _savepointed = false;
        _savepoint = null;
      }
    }
  }

  public void updating(DataSet dataSet, ReadWriteRow newRow, ReadRow oldRow)
    throws Exception
  {
    open();
    if (hasModifiedKey(newRow, oldRow, _desc.referencingColumns)) {
      int refCount = getReferenceCount(dataSet, oldRow);
      if (refCount == 0)
        return;
      if (refCount == 1 && _reference.dataSetStore == _otherReference) {
        if ( nullValues(oldRow, _desc.referencingColumns))
          return;
      }

/*
      Diagnostic.println("newRow:  "+newRow);
      Diagnostic.println("oldRow:  "+oldRow);
      long saveRow = _reference.getInternalRow();
      _reference.first();
      while(_reference.inBounds()) {
        Diagnostic.println("ref:  "+_reference);
        _reference.next();
      }
*/

      processAction(oldRow);
    }
  }
  public void updateError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response)
    /*-throws DataSetException-*/
  {
    rollback();
  }
  public void updated(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    if (_savepointed) {
      cascade(dataSet, false);
    }
  }


  public void deleting(DataSet dataSet)
    throws Exception
  {
    open();
    long internalRow = dataSet.getInternalRow();
//    if (validSameRow(dataSet))
//        return;
    int refCount = getReferenceCount(dataSet, dataSet);
    if (refCount > 0) {
      if (refCount == 1 && validSameRow(dataSet)) {
        return;
      }
      processAction(dataSet);
    }
  }

  private void processAction(ReadRow row) {
      switch(_desc.deleteAction) {
        case ForeignKeyAction.CASCADE:
        case ForeignKeyAction.SET_DEFAULT:
        case ForeignKeyAction.SET_NULL:
          setSavepointRow(row);
          setSavepoint();
          break;
        default:
          violation();
      }
  }
  public void deleteError(DataSet dataSet, DataSetException ex, ErrorResponse response)
    /*-throws DataSetException-*/
  {
    rollback();
  }

  public void deleted(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    if (_savepointed) {
      cascade(dataSet, true);
    }
  }

  private final void cascade(ReadRow row, boolean delete) {
      boolean complete = false;
      try {
        switch(_desc.deleteAction) {
          case ForeignKeyAction.CASCADE:
            while (_reference.locate(_savepointRow, Locate.FIRST|Locate.FAST)) {
              if (delete) {
                _reference.deleteRow();
              }
              else {
                DataRow.copyTo(_desc.referencingColumns, row, _desc.referencedColumns, _reference);
                _reference.post();
              }
            }
            break;
          case ForeignKeyAction.SET_DEFAULT:
            while (_reference.locate(_savepointRow, Locate.FIRST|Locate.FAST)) {
              for (int index = 0; index < _ordinals.length; ++index) {
                _reference.setDefault(_ordinals[index]);
              }
              _reference.post();
            }
            break;
          case ForeignKeyAction.SET_NULL:
            while (_reference.locate(_savepointRow, Locate.FIRST|Locate.FAST)) {
              for (int index = 0; index < _ordinals.length; ++index) {
                _reference.setAssignedNull(_ordinals[index]);
              }
              _reference.post();
            }
            break;
          default:
            DiagnosticJLimo.fail();
        }
        complete = true;
      }
      finally {
        if (!complete)
          rollback();
        _savepoint = null;
        _savepointed = false;
      }
  }


  final int getReferenceCount(DataSet dataSet, ReadRow newRow) {
    return _reference.dataSetStore.getOtherReferenceCount(_reference, setLocateRow(newRow));
//    return _reference.locate(setLocateRow(newRow), Locate.FIRST);
  }

}
