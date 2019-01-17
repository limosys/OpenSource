//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ParameterType.java,v 7.0 2002/08/08 18:39:31 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * This class defines constants that are used by Column objects in a ParameterRow.
 * These constants indicate how the parameters are to be handled by the database
 * server when the associated QueryDataSet or ProcedureDataSet is executed.
 */
public class ParameterType {

/**
 *  Indicates that this parameter type is unknown
 */
  public static final int NONE = 0;

/**
 * Indicates that this parameter is used only for input and will not be modified.
 */
  public static final int IN = 1;

/**
 * Indicates that this parameter is used only for output
 */
  public static final int OUT = 4;

/**
 * Indicates that this parameter is used both for input and for output
 */
  public static final int IN_OUT = 2;

/**
 *   Indicates that this parameter is the returned value from a procedure
 */
  public static final int RETURN = 5;

/**
 *  Indicates that this parameter is a resultset
 */
  public static final int RESULT = 3;

}
