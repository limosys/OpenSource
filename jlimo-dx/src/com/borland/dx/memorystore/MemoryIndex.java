//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/memorystore/MemoryIndex.java,v 7.1 2003/06/13 00:37:05 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.*;
import com.borland.jb.util.Trace;
import com.borland.jb.util.Hex;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Trace;

// Not public.  Not used outside package.
//
class MemoryIndex extends DataIndex
{
 public MemoryIndex(  SortDescriptor    descriptor,
                      RowFilterListener rowFilterListener,
                      InternalRow       filterRow,
                      MemoryData        data,
                      int               visibleMask,
                      int               invisibleMask,
                      IntColumn         statusColumn
                   )
  {
    super(data, visibleMask, invisibleMask);
    this.memoryData             = data;
    this.descriptor             = descriptor;
    this.statusColumn           = statusColumn;
    this.rowFilterListener      = rowFilterListener;
    this.filterRow              = filterRow;

    if (filterRow != null)
      rowFilterResponse = new RowFilterResponse();

//! Diagnostic..println("new Index statusColumn "+statusColumn.toString());
//! Diagnostic..printStackTrace();
    lastRow   = -1;

    vector        = new int[DataColumn.InitialSize];
    vectorLength  = vector.length;
  }

  public final long internalRow(long longRow) {
    int row = (int) longRow;
    // Protects against multi-threaded issue when switching from
    // one index to another when the new index has less rows.  Would
    // not want to get An array indexing exception.  Just dummy things
    // up.
    //
    if (row > lastRow)
      row  = 0;
    //! Diagnostic..println(row + " v " +vector[row]);
    int internalRow = vector[row];
    // Note that any non zero internal row has a retrievable value.  A zero
    // internalRow could come from an index switch with a filter of less records.  Note
    // that index switches always make sure that their array size is at least
    // as large as the previous index to avoid array indexing exceptions.
    //
    if (internalRow < 1)
      return 1;
    return internalRow;
  }

  final boolean canAdd(int internalRow)
    /*-throws DataSetException-*/
  {
    int status  = statusColumn.vector[internalRow];
//!    Diagnostic.println(this+" canAdd: "+Integer.toHexString(visibleMask)+" "+Integer.toHexString(invisibleMask)+" "+Integer.toHexString(statusColumn.vector[internalRow]));
    if ((status&visibleMask) == 0 || (status&invisibleMask) != 0){
//!      Diagnostic.trace(Trace.DataSetEdit, "cantAdd not visible "+Integer.toString(visibleMask, 16)+" "+Integer.toString(statusColumn.vector[internalRow], 16));
      //! Diagnostic..println(internalRow+" mask "+Integer.toString(visibleMask, 16) + " "+Integer.toString(statusColumn.vector[internalRow], 16));
//!      Diagnostic.println("not adding:  "+Integer.toHexString(status));
      return false;
    }

//!    Diagnostic.println("rowFilterListener:  "+rowFilterListener+" "+filterRow);
    if (filterRow != null ) {
      filterRow.setInternalRow(internalRow);
      rowFilterResponse.ignore();

      try {
        rowFilterListener.filterRow(filterRow, rowFilterResponse);
        return rowFilterResponse.canAdd();
      }
      catch (DataSetException ex) {
//!        Diagnostic.printStackTrace(ex);
        return false;
      }
    }

    return true;
  }

  final void vectorInsert(int pos, int value) {
//! Diagnostic..println("vectorInsert "+pos);
    if (comp > 0 && pos <= lastRow) ++pos;
    DiagnosticJLimo.check(pos >= 0);
    if (++lastRow >= vectorLength) {
      growVector();
    }

    if (pos <= lastRow) {
      if (pos < lastRow)
        System.arraycopy(vector, pos, vector, pos+1, lastRow - pos);
      vector[pos] = value;
      DiagnosticJLimo.trace(Trace.DataSetEdit, "Index.vectorInsert "+pos+" value "+value+" lastRow "+lastRow);
    }
    else
      DiagnosticJLimo.fail();
//! Diagnostic..println(pos + " vectorInsert "+vector[0]+" "+vector[1]+" "+vector[2]+" "+vector[3]);
  }

  final void growVector() {
    growVector(DataColumn.getNewSize(vectorLength));
  }

  void growVector(int newLength) {
    if (newLength > vectorLength) {
      int oldLength    =  vectorLength;
      int newVector[]  = new int[newLength];
      if (oldLength > 0)
        System.arraycopy(vector, 0, newVector, 0, oldLength);
      vector        = newVector;
      vectorLength  = vector.length;
    }
    else
      DiagnosticJLimo.fail();  // Why are you trying to grow?
  }

   final void vectorDelete(int pos) {
    DiagnosticJLimo.check(pos >= 0);
    if (pos <= lastRow) {
      if (pos < lastRow)
        System.arraycopy(vector, pos+1, vector, pos, lastRow - pos);
      --lastRow;
    }
  }

  public final void loadStore(long longInternalRow)
    /*-throws DataSetException-*/
  {
    int internalRow = (int)longInternalRow;
    if (canAdd(internalRow)) {
      if (++lastRow >= vectorLength)
        growVector();
      vector[lastRow] = internalRow;
    }
  }

  public boolean addStore(long longInternalRow)
    /*-throws DataSetException-*/
  {
    int internalRow = (int)longInternalRow;
    DiagnosticJLimo.trace(Trace.DataSetEdit, "Index.add "+internalRow);
    //! Diagnostic..printStackTrace();
    if (lastRow < 0 || internalRow > vector[lastRow]) {
      loadStore(internalRow);                // Faster.
      return true;
    }

    if (canAdd(internalRow)) {
      int pos = (int)findClosest(internalRow);
      if (pos < 0)
        pos = 0;
      vectorInsert(pos, internalRow);
      return true;
    }
    DiagnosticJLimo.trace(Trace.DataSetEdit, "Index.add - cantAdd!!!");
    return false;
  }

  public void prepareUpdate(long internalRow) { }

  public void updateStore(long longInternalRow)
    /*-throws DataSetException-*/
  {
    int internalRow = (int)longInternalRow;
    deleteStore(internalRow);
    addStore(internalRow);
  }


  public final void deleteStore(long internalRow) {
    //! Diagnostic..printStackTrace();
    int pos = (int)findClosest(internalRow);
    if (comp == 0) vectorDelete(pos);
    DiagnosticJLimo.trace(Trace.DataSetEdit, lastRow+" Index.delete Deleted "+(comp==0));
  }

  public final void delete(long internalRow) {
    deleteStore(internalRow);
  }

   final void printVector() {
    for (int index= 0; index <= lastRow; ++index)
      DiagnosticJLimo.print(vector[index]+" ");
    DiagnosticJLimo.println("");
  }

  final int vectorLength() { return vectorLength; }

  public final long findClosest(long searchRow, long longRow) {
    int row = (int)longRow;
    if (lastRow < 0) {
      internalRow = -1;
      return 0;
    }
    if (row <= lastRow && vector[row] == searchRow) {
      internalRow = searchRow;
      return row;
    }
    int newRow  = (int)findClosest(searchRow);
    if (row <= lastRow && vector[newRow] != searchRow) {
      internalRow = vector[row];
      return row;
    }
    return newRow;
  }

  public final long findClosest(long longSearchRow) {
    // Assume it will be found.
    //
    internalRow = longSearchRow;
    int searchRow = (int)longSearchRow;

    if (lastRow == -1)
      return 0;
    int   high    =   lastRow;
    int   low     =   0;
    int   mid     =   -1;

    // Although locate operations can use searchRow of 0 for setting
    // search values, this function is not used for locating.
    //
    if (searchRow == 0 || searchRow >= statusColumn.lastRow)
      return 0;

    while (true) {
      mid    =   (low + high) / 2;

      compare(searchRow, vector[mid]);
      //! Diagnostic..println(lastRow+" comp "+comp+" high "+high+ " low "+low+" mid "+mid);
      if (comp > 0) {
        if (low >= high) {
          if (check)
            checkIndex(mid);
          internalRow = vector[mid];
          return mid;
        }
        low    =   mid+1;
      }
      else if (comp < 0) {
        if (high <= low) {
          if (check)
            checkIndex(mid);
          internalRow = vector[mid];
          return mid;
        }
        high   =   mid-1;
      }
      else{
        if (check)
          checkIndex(mid);

        return mid;
      }
    }
  }

  private final void checkIndex(int row) {
    if (check) {
      int saveComp  = comp;
      if (row > 0) {
        compare(vector[row], vector[row-1]);
        DiagnosticJLimo.check(comp > 0);
      }
      if ( row < lastRow) {
        compare(vector[row], vector[row+1]);
        DiagnosticJLimo.check(comp < 0);
      }
      comp  = saveComp;
    }
  }

  public final void loadSearchValues(Column[] locateColumns, RowVariant[] values)
    /*-throws DataSetException-*/
  {
    int       ordinal;
    DiagnosticJLimo.check(vector != null);
    for (int columnIndex = 0; columnIndex < locateColumnCount; ++columnIndex) {
      ordinal = locateColumns[columnIndex].getOrdinal();
      memoryData.dataColumns[ordinal].setVariant(0, values[ordinal]);
    }
  }

  public long locate(long startRow, int locateOptions)
    /*-throws DataSetException-*/
  {
    // Protects against multi-threaded issue when switching from
    // one index to another when the new index has less rows.  Would
    // not want to get An array indexing exception.
    //
    if (startRow > lastRow)
      return -1;

    if ((locateOptions&(Locate.PRIOR|Locate.LAST)) != 0)
      return locateBackwards((int)startRow);
    else
      return locateForwards((int)startRow);
  }

   final int locateForwards(int startRow) {
    int count = (int)lastRow() + 1;
    for (int row = startRow; row < count; ++row) {
      if (compareRow(row)) {
        return row;
      }
    }
    return -1;
  }

   final int locateBackwards(int startRow) {
//! Diagnostic..println("starting on "+startRow);
    for (int row = startRow; row > -1; --row) {
      if (compareRow(row))
        return row;
    }
    return -1;
  }

   final boolean compareRow(int row) {
    for (int columnIndex = 0; columnIndex < locateColumnCount; ++columnIndex) {
      if (locatePartialIndex == columnIndex) {
        if (!memoryData.dataColumns[locateColumns[columnIndex].getOrdinal()].partialCompare(vector[row], 0, locateCaseInsensitive))
          return false;
      }
      else if (locateCaseInsensitive) {
        if (memoryData.dataColumns[locateColumns[columnIndex].getOrdinal()].compareIgnoreCase(vector[row], 0) != 0)
          return false;
      }
      else {
//d.verbose  = true;
        if (memoryData.dataColumns[locateColumns[columnIndex].getOrdinal()].compare(vector[row], 0) != 0) {
//d.verbose  = false;
          return false;
        }
//d.verbose  = false;
      }
    }
    return true;
  }

  public long lastRow() { return lastRow; }


  public boolean markForUpdate(RowVariant[] values)
    /*-throws DataSetException-*/
  {
    //!SteveS TODO.  Could optimize updates with this.
    return true;
  }

   void compare(int index1, int index2) {
    comp =  index1 - index2;
    //! Diagnostic..println("compare "+index1+ " "+index2+" comp "+ comp + " "+index1+" "+vector[index2]);
  }

	public void note(int note) {
		/*-throws DataSetException-*/
		DiagnosticJLimo.fail();
	}

	int[] vector;
	IntColumn statusColumn;
	int comp;

	int lastRow;
	int vectorLength;

//!  private   DataColumn[]          dataColumns;
	MemoryData memoryData;
	private InternalRow filterRow;
	private RowFilterResponse rowFilterResponse;

  private final static boolean check = false;
}
