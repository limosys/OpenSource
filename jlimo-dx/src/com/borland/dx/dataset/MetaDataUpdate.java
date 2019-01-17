//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/MetaDataUpdate.java,v 7.0 2002/08/08 18:39:29 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The MetaDataUpdate interface defines constants used with the metaDataUpdate
 * property of the QueryDataSet and ProcedureDataSet components.
 * Its constants specify whether metadata discovery should be performed (or not)
 * when executing a query or stored procedure against a SQL server database.
 * From the JBuilder UI Designer, select the metaDataUpdate property of the
 * StorageDataSet component and select which constant to use.
 * <p>
 * If the driver used to connect to a SQL database does not support the metaData
 * functions used by this package, you may get an error message indicating that
 * edits to the data cannot be saved because none of its updateable columns have
 * a table name. In such cases, try setting the metaDataUpdate property of the
 * QueryDataSet or ProcedureDataSet to MetaDataUpdate.NONE. This bypasses the
 * step of automatic querying for metadata information, however, in order for the
 * data to be updateable, this information must be provided by the application.
 * For Column components, set the rowID, precision, scale, and searchable properties.
 * For QueryDataSet and ProcedureDataSet components, set the tableName and schemaName properties.
*/

public interface MetaDataUpdate
{

/**
 * This constant is used to specify that the open of the DataSet is not
 * to override the above stated properties. You must specify the values
 * for these properties for any persistent Column components in the StorageDataSet.
 */
  public static final int NONE       =  0;

/**
 * Specifies that the tableName and schemaName properties of the StorageDataSet
 * and any persistent columns should be set when opening the DataSet.
 * The default resolver (Queryresolver), needs this information to make
 * update queries. This constant has no effect for ProcedureDataSet components.
 */
  public static final int TABLENAME  =  1;

/**
 *  Specifies that a query should be analyzed for updateability. If set, the
 *  query string may be automatically changed to include columns that can be
 *  used to identify a row in a table. The rowID property is set or reset on
 *  all columns, overriding the settings in any persistent columns.
 *  The default resolver (QueryResolver) needs this information to make update queries. This constant has no effect for ProcedureDataSet components.
 */
  public static final int ROWID      =  2;

/**
 * Specifies that the precision property of persistent columns
 * should be overridden by the value detected in the driver's metadata.
 */
  public static final int PRECISION  =  4;

/**
 *  Specifies that the scale property of persistent columns should be overridden
 *  by the value detected in the driver's metadata.
 */
  public static final int SCALE      =  8;

/**
 * Specifies that the searchable property of persistent columns should be
 * overridden by the value detected in the driver's metadata.
 */
  public static final int SEARCHABLE = 16;

/**
 * This constant is the default and specifies that the open of the DataSet
 * will automatically override the following settings:
 * <p>
 * <ul>
 *    <li>The tableName and schemaName properties of the StorageDataSet.</ul>
 *    <li>The rowId, precision, scale, and searchable properties for the Column component.</li>
 * </ul>
 */
  public static final int ALL        = 31;
}
