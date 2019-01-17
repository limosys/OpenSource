//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/Sort.java,v 7.1 2002/09/18 04:19:06 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

public interface Sort {
  /**
      For caseinsensitive ordering.  Applies to all columns of String type
  */
  public static final int CASEINSENSITIVE   = 0x01;
  /**
      Unique constraint on the sortKey values.  A row with column value
      for sortKeys that is not unique, cannot be added to the DataSet.
  */
  public static final int UNIQUE            = 0x02;
  /**
      Not null constraint on the sortKey values.
  */
  public static final int NOT_NULL          = 0x04;
  /**   Combines UNIQUE and NOT_NULL constraints.
        Only one index per StorageDataSet can be created with the PRIMARY
        option enabled.
  */
  public static final int PRIMARY           = 0x08|UNIQUE|NOT_NULL;
  /**
      Used as a ordering tie breaker when there are sortKeys with duplicate
      values or when no sortKeys are specified.  If this option is enabled,
      then the position in which a row is added to the DataSet is used to determine
      the sorted position of a row in a DataSet.
  */
  public static final int SORT_AS_INSERTED          = 0x10;

  /**
   * Matches unique or non-unique when opening an existing sort.
   */
  public static final int DONT_CARE_UNIQUE          = 0x20;
  /**
   * Matches caseinsensitive or non-caseinsensitive when opening an existing sort.
   */
  public static final int DONT_CARE_CASEINSENSITIVE  = 0x40;

  static final int DONT_CARE_MASK     = (DONT_CARE_UNIQUE|DONT_CARE_CASEINSENSITIVE);
}
