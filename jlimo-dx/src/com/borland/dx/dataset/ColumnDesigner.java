//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ColumnDesigner.java,v 7.0 2002/08/08 18:39:18 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
*   This interface is for internal use only !
*/

public interface ColumnDesigner {

  int    getColumnCount();
  Column getColumn(int ordinal) /*-throws DataSetException-*/;
  Column hasColumn(String columnName);
  int    addColumn(Column column) /*-throws DataSetException-*/;
  void   changeColumn(int ordinal, Column column) /*-throws DataSetException-*/;
  void   dropColumn(String columnName) /*-throws DataSetException-*/;

}
