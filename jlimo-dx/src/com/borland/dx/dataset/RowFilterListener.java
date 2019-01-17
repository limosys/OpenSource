//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/RowFilterListener.java,v 7.0 2002/08/08 18:39:34 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;


import java.util.*;

/**
 * This interface is used as a notification when a row is being added or updated.
 * The RowFilterListener only controls which rows are displayed in the current
 * view of a DataSet based on current filter criteria. The RowFilterListener does
 * not delete rows from a DataSet or block any data values from being entered.
 * If a newly inserted row contains a value that does not meet the filter
 * criteria, it is stored in the DataSet, but does not show in the current view.
 * If you need to prevent rows that do not meet the filter criteria from being
 * stored in a DataSet, use the EditListener.adding() and updating() events.
 */
public interface RowFilterListener extends EventListener {

    /**
     * This method is called for each row as a data set is opened, and whenever
     * a new or modified row is posted. The filterRow() method decides if the
     * current row of the data set should be included in the view. To include
     * it, call RowFilterResponse.add(). To exclude it, call response.ignore(),
     * which is the default behavior. A filterRow() method that never calls
     * RowFilterResponse.add() produces an empty DataSetView.
     *
     * @param row         The current row of the data set.
     * @param response    Whether or not the row should be included in the current, filtered, view.
     */
    public void filterRow(ReadRow row, RowFilterResponse response) /*-throws DataSetException-*/;
}
