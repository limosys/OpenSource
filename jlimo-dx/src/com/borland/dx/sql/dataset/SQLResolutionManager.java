//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/SQLResolutionManager.java,v 7.0 2002/08/08 18:39:58 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.dx.dataset.*;
import java.sql.SQLException;
import com.borland.jb.util.*;

/**
 *
*The <CODE>SQLResolutionManager</CODE> component performs most of the work for the
*  resolution process. The algorithms for transaction management and the change
* resolution reside in this object. An instance of this class is instantiated with
*  an implementation of the <CODE>DefaultResolver</CODE> and
* <CODE>TransactionSupport</CODE> interfaces.
*
*<P>If a <STRONG>null</STRONG> <CODE>TransactionSupport</CODE> object is passed in,
*  the <CODE>SQLResolutionManager</CODE> takes no action for transaction
* processing (as is applicable to non-transaction-processing data sources).
*This object manages the resolution process as follows:
*<UL>
*<LI>The array of <CODE>DataSet</CODE> components passed into the
*  <CODE>saveChanges()</CODE> method is analyzed and split into a
* list of trees. Each tree encapsulates any one-to-many-to-many
* (and so on) behavior.
*<LI>A non-linked (standalone) <CODE>DataSet</CODE> is represented
* as a tree with no children.
*</UL>
*
*<P>The resolution process is broken into two different algorithms.
*  For stand-alone <CODE>DataSet</CODE> components, all rows in the
* following categories are processed in this order to preserve the integrity
*  of the data:
*<UL>
*<LI><CODE>Deleted</CODE> rows
*<LI><CODE>Modified</CODE> rows
*<LI>Inserted rows
*</UL>
*
*
*<P>For one-to-many <CODE>DataSet</CODE> relationships, all rows in the
* following categories are processed in this order:
*<UL>
*<LI><CODE>Deleted</CODE> rows for the bottom-most children <CODE>DataSet</CODE>
* components
*<LI>parents of the above <CODE>DataSet</CODE> components are recursively processed:
*	<UL>
*	<LI><CODE>Inserted</CODE> rows, starting at the root <CODE>DataSet</CODE>
* and working recursively downward
*	<LI><CODE>Modified</CODE> rows, bottom-up.
*	</UL>
*</UL>
*
*<P>Since changes are not in sequential order, changing link fields in a
* one-to-many relationship can cause data loss. Therefore, this action is disallowed
*  by default.
 */
public class SQLResolutionManager extends ResolutionManager
  implements DefaultResolver, TransactionSupport
{

  /**
   * Constructs a SQLResolutionManager object.
   */
  public SQLResolutionManager() {
    doTransactions  = true;
    super.setTransactionSupport(this);
    super.setDefaultResolver(this);
  }

  public void start()
    /*-throws DataSetException-*/
  {
    if (doTransactions) {
      // Start explicit transaction
      try {
        wasAutoCommit = database.getAutoCommit();
        if (wasAutoCommit)
          database.setAutoCommit(false);
      }
      catch (DataSetException sex){
        DataSetException.resolveFailed(sex);
      }
    }

    //!ChrisO TODO: Research
    //! Note: What do we do here if a transaction is already active? Probably best to
    //!       throw some kind of DataSetException. If the user wants explict control
    //!       over transactions, force them to wire the events.
    //!
    //!       Additional research item:
    //!       If somebody specified an isolation level, that will start a transaction
    //!       also. If so, we need to deal with that here somehow. Research should tie
    //!       into prior research issue.
    //!
  }

  public void commit()
    /*-throws DataSetException-*/
  {
    if (doTransactions) {
      try {
        database.commit();
        if (wasAutoCommit)
          database.setAutoCommit(wasAutoCommit);
      }
      catch (DataSetException sex){
        DataSetException.resolveFailed(sex);
      }
    }
  }

  public void rollback()
    /*-throws DataSetException-*/
  {
    if (doTransactions) {
      try {
        database.rollback();
        if (wasAutoCommit)
          database.setAutoCommit(wasAutoCommit);
      }
      catch (DataSetException sex){
        DataSetException.resolveFailed(sex);
      }
    }
  }

  public Resolver getResolver(DataSet dataSet)
    /*-throws DataSetException-*/
  {
    // Get the assigned Resolver object.
    Resolver  resolver = dataSet.getStorageDataSet().getResolver();
    SQLResolver thisResolver = null;

    if (resolver == null) {
      // No Resolver object assigned to the DataSet, need to allocate a default Resolver.
      thisResolver = new QueryResolver();
      thisResolver.setDatabase(database);
    }
    else {
      // Validate that the resolver is an instance of DatabaseResolver.
      if (! (resolver instanceof SQLResolver))
        DataSetException.notDatabaseResolver();
      thisResolver = (SQLResolver)resolver;
      //! JOAL: Workaround for now:
      if (database != null)
        thisResolver.setDatabase(database);
    }

//! JOAL - DISABLED
//!  Note: We should not be doing this !
//!        This is only here for backwards compability with version 1.0 of standatd & Pro skus.
//!        JOAL TODO: Talk to Ray: Hermes has problems with this (HOW????)
//!  thisResolver.setDatabase(database);

    return thisResolver;
  }
//!/* DISABLED.
//!  public boolean isSingleTransaction() {
//!    return getSingleTransaction();
//!  }
//!  */

  public boolean isDoTransactions() {
    return doTransactions;
  }

  /**
   * Specifies whether transactions are supported or not
   * @param doTransactions boolean
   */
  public void setDoTransactions(boolean doTransactions) {
    this.doTransactions = doTransactions;
  }

  // Properties:

  /**
   * Specifies the Database object that this component is associated with
   * @return database
   */
  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  // Overwrite super class setter to make this the default transactionSupport
  /**
   * Write-only property that overwrites the superclass setter to make this
   * class the default TransactionSupport object.
   * @param transactionSupport TransactionSupport
   */
  public void setTransactionSupport(TransactionSupport transactionSupport) {
    if (transactionSupport == null)
      transactionSupport = this;
    super.setTransactionSupport(transactionSupport);
  }

  /**
      Add extra error context infor to resolveError object.
      @param code is UPDATE_FAILED, INSERT_FAILED or DELETE_FAILED specified in
      the ResolutionException class
      @param dataSet is the base DataSet passed into the ResolutionManager
      @param view is positioned at the row that caused the error.
      @param ResolveError can be used to record additional error information.
      @see ResolveError
  */

  protected final void initError( int             code,
                                  DataSet         dataSet,
                                  DataSet         view,
                                  ResolveError    resolveError
                               )
  {
     String errorContext = "SQLState: ";                //NORES
     DataSetException dataSetException = (DataSetException)resolveError.ex;

     //Get additional error context from the  exception chain
     ExceptionChain exceptionChain = dataSetException.getExceptionChain();
     if ( exceptionChain != null && exceptionChain.hasExceptions() && exceptionChain.getException() instanceof java.sql.SQLException)
     {
        java.sql.SQLException exception = (java.sql.SQLException) exceptionChain.getException();

        resolveError.code    = exception.getErrorCode();
        resolveError.context = errorContext + exception.getSQLState()+ ", " + exception.toString(); //NORES
        resolveError.category = 0;

     }
  }

  private Database    database;
  private boolean     doTransactions;
  private boolean     wasAutoCommit;
}



