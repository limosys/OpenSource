//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ForeignKeyDescriptor.java,v 7.2.2.1 2005/03/08 23:14:36 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;




 /**
  * Class used to define SQL foreign key constraint
  */
public class ForeignKeyDescriptor implements Cloneable {

  static final ForeignKeyDescriptor[] clone(ForeignKeyDescriptor[] descs) {
    ForeignKeyDescriptor[] newDescs = (ForeignKeyDescriptor[])descs.clone();
    for (int index = 0; index < descs.length; ++index) {
      newDescs[index] = (ForeignKeyDescriptor)descs[index].clone();
    }
    return newDescs;
  }

  public final ForeignKeyDescriptor invert(StorageDataSet other) {
    ForeignKeyDescriptor invert = (ForeignKeyDescriptor)clone();
    invert.referencedColumns    = referencingColumns;
    invert.referencingColumns   = referencedColumns;
    invert.referencedTable       = other;
    invert.referencedTableName   = other.getSchemaStoreName();
    return invert;
  }

  public Object clone() {
    try {
      return super.clone();
    }
    catch (java.lang.CloneNotSupportedException ex) {
      DiagnosticJLimo.printStackTrace(ex);
      return null;
    }
  }

  private final boolean equals(String[] a1, String[] a2) {
    if (a1.length != a2.length)
      return false;
    for (int index = 0; index < a1.length; ++index) {
      if (!a1[index].equals(a2[index]))
        return false;
    }
    return true;
  }

  public final boolean equals(Object other) {
    if (other != this) {
      ForeignKeyDescriptor fk = (ForeignKeyDescriptor)other;
      if (referencedTable != fk.referencedTable) {
        if (referencedTable != null && fk.referencedTable != null)
          if (!referencedTable.getSchemaStoreName().equals(fk.referencedTable.getSchemaStoreName()))
            return false;
        else if (referencedTableName != null && fk.referencedTableName != null) {
          return referencedTableName.equals(fk.referencedTableName);
        }
        else
          return false;
      }
      if (!equals(referencedColumns, fk.referencedColumns))
        return false;
      if (!equals(referencingColumns, fk.referencingColumns))
        return false;
/*
      if (_deleteAction != fk._deleteAction)
        return false;
      if (_updateAction != fk._updateAction)
        return false;
*/
    }
    return true;
  }

  private StorageDataSet initReferenceTable(StorageDataSet referencingTable, Store store) {
    StorageDataSet tempReferencedTable = referencedTable;
    if ((tempReferencedTable == null && referencedTableName != null) || tempReferencedTable.getStore() != store) {
      if (referencedTableName.equals(referencingTable.getSchemaStoreName())) {
        return referencingTable;
      }
      tempReferencedTable = new StorageDataSet();
      tempReferencedTable.setStoreName(referencedTableName);
      tempReferencedTable.setStore(store);
      referencedTable = tempReferencedTable;
    }
    return tempReferencedTable;
  }

  StorageDataSet openReferenceTableData(StorageDataSet referencingTable, Store store) {
    StorageDataSet table = initReferenceTable(referencingTable, store);
    table.initExistingData(false);
    return table;
  }

  final SortDescriptor makeLocateSort(String[] columnNames) {
    return new SortDescriptor(null, columnNames, null, null, Sort.DONT_CARE_UNIQUE|Sort.DONT_CARE_CASEINSENSITIVE);
  }

  DataSet openReferenceTable(StorageDataSet referencingTable, Store store, String[] referencedColumns) {
    StorageDataSet table = initReferenceTable(referencingTable, store);
    DataSet ret = table;
    SortDescriptor sort = makeLocateSort(referencedColumns);
    table.open();
    if (table == referencingTable) {
      DataSetView view = new DataSetView();
      view.setStorageDataSet(table);
      ret = view;
    }
    if (table.indexExists(sort, null)) {
      SortDescriptor currentSort = ret.getSort();
      if (currentSort == null || !currentSort.equals(sort)) {
        ret.close();
        ret.setSort(sort);
      }
    }
    ret.open();
    return ret;
  }

  /**
   * The name of the constraint.
   */
  public String    name;
  /**
   * The name of the table being referenced by the referencing table.
   */
  public String    referencedTableName;
  /**
   * Table being referenced by the referencing table.
   */
  public StorageDataSet    referencedTable;
  /**
   * Array of columns from the referenced table that
   * must match in data type, order and count with the referencing columns property.
   */
  public String[]  referencedColumns;
  /**
   * Array of columns from the referencing table that
   * must match in data type, order and count with the referenced columns property.
   */
  public String[]  referencingColumns;
  /**
   * specifies one of the values in com.borland.dx.dataset.cons.ForeignKeyAction
   * for update operations.
   * @see com.borland.dx.dataset.cons.ForeignKeyAction
   */
  public int       updateAction;
  /**
   * specifies one of the values in com.borland.dx.dataset.cons.ForeignKeyAction
   * for update operations.
   * @see com.borland.dx.dataset.cons.ForeignKeyAction
   */
  public int       deleteAction;
  /**
   * specifies whether there should be an initial check of this foreign key
   * constraint at creation time. If check is false the cocsistency of the data
   * will not be checked.
   */
  public boolean   check;
}
