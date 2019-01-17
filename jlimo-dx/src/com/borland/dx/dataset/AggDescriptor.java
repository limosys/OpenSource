//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/AggDescriptor.java,v 7.0 2002/08/08 18:39:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;
import com.borland.jb.util.DiagnosticJLimo;

/**
 * Used to specify the grouping, target field to aggregate on, and the
 * aggregation operation for a column with a calcType of
 * {@link com.borland.dx.dataset.CalcType#AGGREGATE}.
 */
public class AggDescriptor implements java.io.Serializable {

/**
 * Constructs an AggDescriptor object with the specified parameters.
 *
 * @param groupColumnNames    An array of Column names that define the groups
 *                            of rows to aggregate on.
 * @param aggColumnName       The name of the Column to perform the aggregation on.
 * @param aggOperator         The aggregation operation to perform.
 *                            See the aggOperator property for valid values.
 */
  public AggDescriptor( String[]      groupColumnNames,
                        String        aggColumnName,
                        AggOperator   aggOperator
                      )
  {
    this.groupColumnNames = groupColumnNames;
    this.aggColumnName    = aggColumnName;
    this.aggOperator      = aggOperator;
  }

  /**
   * @return   The array of Column names that define the groups of rows to aggregate on.
   */
  public String[] getGroupColumnNames() { return groupColumnNames; }

  /**
   * @return  The aggregation operation to perform. Valid values are:
   * <ul>
   *  <li>{@link com.borland.dx.dataset.SumAggOperator}</li>
   *  <li>{@link com.borland.dx.dataset.MinAggOperator}</li>
   *  <li>{@link com.borland.dx.dataset.CountAggOperator}>/li>
   *  <li>Any custom aggregation defined by extending the
   *      {@link com.borland.dx.dataset.CustomAggOperator} class.</li>
   * </ul>
   */
  public AggOperator  getAggOperator() { return aggOperator; }

  /**
   * @return  The Column (by name) to perform the aggregation on.
   */
  public String   getAggColumnName() { return aggColumnName; }

  boolean groupEquals(String[] columnNames) {

    if (columnNames == groupColumnNames)
      return true;

    if (columnNames == null || groupColumnNames == null)
      return false;

//    Diagnostic.check(groupColumnNames.length > 0);

    if (columnNames.length != groupColumnNames.length)
      return false;
    for (int index = 0; index < groupColumnNames.length; ++index)
      if (!groupColumnNames[index].equalsIgnoreCase(columnNames[index]))
        return false;
    return true;
  }

  private String[]      groupColumnNames;
  private String        aggColumnName;
  private AggOperator   aggOperator;
  private static final long serialVersionUID = 1L;
}
