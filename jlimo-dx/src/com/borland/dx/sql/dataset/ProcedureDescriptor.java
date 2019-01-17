//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/ProcedureDescriptor.java,v 7.0 2002/08/08 18:39:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;


import com.borland.dx.dataset.*;

/**
 * The ProcedureDescriptor class stores property settings associated with a ProcedureDataSet.
 *  Its main properties are:<p>
 *

its associated Database component (required) <p>

the stored procedure escape sequence or the SQL statement (required) <p>

whether to execute the stored procedure immediately when the ProcedureDataSet is opened (defaults to true) <p>

how the data should be loaded into the ProcedureDataSet <p>



To work with this component programmatically, set its properties when instantiating
the ProcedureDescriptor object, or individually by its write accessor methods.
For properties that do not have corresponding setter methods, use a constructor
that takes that property as a parameter. <p>

Through the user-interface, the information stored in the ProcedureDescriptor can
be accessed by inspecting the procedure property of a ProcedureDataSet object. This
displays the Procedure property editor. The Procedure property editor also provides additional discovery functionality through the Browse Procedures button. Clicking this button displays a dialog listing available stored procedures in the associated database (as specified by the Database property). When you select a stored procedure from this dialog, its contents are pasted into the Stored Procedure Escape or SQL Statement field to be run against the server database.

When you close the Procedure property editor, as long as the necessary properties have been specified (the stored procedure escape sequence or SQL statement and the Database object), JBuilder runs the stored procedure against the specified Database object.

You can also explicitly test the stored procedure's properties by clicking the Test Procedure button from within the Procedure property editor. Results of "Success" and "Fail" display in the area beneath the Test Procedure button.

Data can be loaded all in one fetch, as needed, asynchronously or one at a time. When working with asynchronous queries, opening the ProcedureDataSet then immediately calling methods such as rowCount() typically returns a row count lower than expected. To avoid this, either set the stored procedure to run synchronously, listen for the LoadingEvent, perform other actions while the ProcedureDataSet completes loading, or listen for updates to the row count. With asynchronous loading, as the stored procedure fetches rows of data, they are appended to the end of the DataSet. If working with a sorted view of the DataSet, the new rows appear in the specified sort order. Also, be careful to not make assumptions about the current row position since rows are inserted into the sorted view as they are fetched, thereby changing row positions automatically
 */
public class ProcedureDescriptor extends QueryDescriptor
{
  public ProcedureDescriptor(Database database, String query) {
    super(database, query);
  }

  public ProcedureDescriptor(Database database, String query, ReadWriteRow parameters, boolean executeOnOpen) {
    super(database, query, parameters, executeOnOpen);
  }

  /** @deprecated.  Use ProcedureDescriptor(Database,String,ReadWriteRow,boolean,int)
   */
  public ProcedureDescriptor(Database database,
                             String query,
                             ReadWriteRow parameters,
                             boolean executeOnOpen,
                             boolean asynchronousExecution) {
    super(database, query, parameters, executeOnOpen, asynchronousExecution ? Load.ASYNCHRONOUS : Load.ALL);
  }

  public ProcedureDescriptor(Database database,
                             String query,
                             ReadWriteRow parameters,
                             boolean executeOnOpen,
                             int loadOption) {
    super(database, query, parameters, executeOnOpen, loadOption);
  }

  private static final long serialVersionUID = 1L;
}

