//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/SortDescriptor.java,v 7.3.2.1 2004/11/04 23:40:06 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.dx.memorystore.MemoryStore;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.LocaleUtil;
import java.util.Locale;

/**
 * Describes the order of presentation for rows of data that are visible
 * to a DataSet. The DataSet can automatically reposition a new or updated
 * row within the cursor, based on the ordering of data in specified columns.
 *
 * The <CODE>SortDescriptor</CODE> class describes the order by which rows of data that are visible to a <CODE>DataSet</CODE> are accessed and presented. Sorting data is very easy and fast since indexes are built as they are needed.

<P>The <CODE>DataSet</CODE> can automatically reposition a new or updated row within the cursor based on the ordering of data by specified columns. In such instances, a row may "fly-away" to its correct position in the <CODE>DataSet</CODE>.

<P>In an ascending sort, <STRONG>null</STRONG> values appear at the bottom of the sort order.

<P>There are no write-accessors for properties of the <CODE>SortDescriptor</CODE>. To set its properties, use a <CODE>SortDescriptor</CODE> constructor that takes the appropriate property as a parameter.

<!-- JDS start - remove paragraph -->
<P>See "Sorting data" in the <CITE>Database Application Developer's Guide</CITE> for a tutorial using the <CODE>SortDescriptor</CODE>.
<!-- JDS end -->

 */
public class SortDescriptor implements java.io.Serializable {

//!  // Used internally to dummy a null SortDescriptor setting.
//!  // Don't make public.
//!  //
  SortDescriptor() {
    this((String[]) null, false, false);
  }


  /** @since JB3.0.
      @param indexname is a name for this index unique for a certain table
      @param sortKeys is an array of columns to sort on.
      @param descending is an array of booleans that indicate which sortKey columns
             sorted in descending order.  The dimension of sortKeys and descending
             should be equal if they are both non-null.
      @param localeName is the name (Locale.toString()) of a locale to use for ordering.
             It is only respected for DataStore.  MemoryStore will always use the Locale of
             the associated StorageDataSet.
      @param options  See SortDescriptor constants for available options that can be or'd
             together.

  */

  /**
   * Constructs a SortDescriptor, named with the specified indexName, with the
   * specified sort keys, as specified in its parameters, using the specified options.
   *
   * @param indexName     A unique name for this index.
   * @param sortKeys      An array of columns on which to sort the data.
   * @param descending    An array of booleans that indicate which sortKeys
   *                      columns are sorted in descending order. The dimension
   *                      of sortKeys and descending should be equal if they
   *                      are both non-null.
   * @param localeName    The name (Locale.toString()) of a locale to use for ordering.
   *                      It is only respected for DataStore. MemoryStore will
   *                      always use the Locale of the associated StorageDataSet.
   * @param options       {@link com.borland.dx.dataset.Sort} variables.
   *                      Variables can be combined with the <b>or</b> operator.
   */
  public SortDescriptor(String            indexName,
                        String[]          sortKeys,
                        boolean[]         descending,
                        String            localeName,
                        int               options
                       )
  {
    this.indexName        = indexName;
    this.sortKeys         = sortKeys;
    this.localeName       = localeName;
    this.descending       = descending;
    if (sortKeys == null)
      this.sortKeys = new String[0];

    unique                = (options&Sort.UNIQUE) != 0;
    caseInsensitive       = (options&Sort.CASEINSENSITIVE) != 0;
    this.options          = options;
  }

  /**
   *  Constructs a SortDescriptor, named with the specified indexName,
   *  with the specified sort keys, as specified in its parameters.
   *
   * @param indexName         A unique name for this index.
   * @param sortKeys          An array of columns on which to sort the data.
   * @param descending        An array of booleans that indicate which sortKeys
   *                          columns are sorted in descending order. The dimension
   *                          of sortKeys and descending should be equal if they are
   *                          both non-null.
   * @param caseInsensitive   If true, data ordering of String type will not be
   *                          sensitive to the case of the data. If false, the
   *                          data will be ordered with respect to the case of the String.
   * @param unique            A constraint on column values. A row with column value
   *                          for sortKeys that is not unique cannot be added to the DataSet.
   * @param localeName        The name (Locale.toString()) of a locale to use for ordering.
   *                          It is only respected for DataStore. MemoryStore will always
   *                          use the Locale of the associated StorageDataSet.
   */
  public SortDescriptor(String            indexName,
                        String[]          sortKeys,
                        boolean[]         descending,
                        boolean           caseInsensitive,
                        boolean           unique,
                        String            localeName
                       )
  {
    this.indexName        = indexName;
    this.sortKeys         = sortKeys;
    this.caseInsensitive  = caseInsensitive;
    this.descending       = descending;
    this.unique           = unique;
    this.localeName       = localeName;
    if (sortKeys == null)
      this.sortKeys = new String[0];
    if (caseInsensitive)
      options |=  Sort.CASEINSENSITIVE;
    if (unique)
      options |=  Sort.UNIQUE;
  }


  /** @since JB2.0.
      sortKeys is an array of Column.columnName values.
      caseInsensitive is for caseInsensitive ordering. applies to all columns
      descending is for descending ordering.  applies to all columns.

      localeName is the name (Locale.toString()) of a locale to use for ordering.
      It is only respected for DataStore.  MemoryStore will always use the Locale of
      the associated StorageDataSet.

  */

  /**
   * Constructs a SortDescriptor with properties as specified in its parameters.
   *
   * @param sortKeys          The String array containing the names of the Column
   *                          components by which to sort the data.
   * @param caseInsensitive   Whether the sort considers (<b>false</b>) or ignores (<b>true</b>)
   *                          upper and lower case differences. Valid only for
   *                          String columns. Defaults to <b>false</b> (case sensitive).
   * @param descending        Whether the sort is in ascending (<b>false</b>, the default)
   *                          or descending (<b>true</b>) order.
   * @param localeName        The String name of the locale used for sorting of
   *                          the data in the DataSet.
   */
  public SortDescriptor(String[]          sortKeys,
                        boolean           caseInsensitive,
                        boolean           descending,
                        String            localeName
                       )
  {
    this(null,sortKeys,null,caseInsensitive, false, localeName);
    if (descending) {
      this.descending = makeDescending(sortKeys);
    }
  }

  static final boolean[] makeDescending(String[] sortKeys) {
    boolean[] descending = new boolean[sortKeys.length];
    for (int i=0; i<sortKeys.length; i++)
      descending[i] = true;
    return descending;
  }

  /**
   *  Constructs a SortDescriptor with properties as specified in its parameters.
   * @param sortKeys          The String array containing the names of the Column
   *                          components by which to sort the data.
   * @param caseInsensitive   Whether the sort considers (<b>false</b>) or ignores (<b>true</b>)
   *                          upper and lower case differences. Valid only for
   *                          String columns. Defaults to <b>false</b> (case sensitive).
   * @param descending        Whether the sort is in ascending (<b>false</b>, the default)
   *                          or descending (<b>true</b>) order.
   */
  public SortDescriptor(String[]          sortKeys,
                        boolean           caseInsensitive,
                        boolean           descending)
  {
    this(sortKeys, caseInsensitive, descending, null);
  }

  /**
   *  Constructs a SortDescriptor with the specified sort keys.
   *  Defaults to case sensitive, ascending. Case-sensitivity applies
   *  for all specified String columns. Ascending/descending applies to
   *  all specified columns.
   *
   * @param sortKeys    The String array containing the names of the Column
   *                    components by which to sort the data.
   */
  public SortDescriptor(String[] sortKeys)
  {
    this(sortKeys, false, false);
  }

  /**
   * Constructs a SortDescriptor with the specified sort.
   * Defaults to case-sensitive, ascending.
   *
   * @param indexName   The String name of this index.
   */
  public SortDescriptor(String indexName)
  {
    this(indexName, new String[0], null, false, false, null);
  }


  /**
   * Constructs a SortDescriptor that contains the same values
   * as the specified SortDescriptor.
   *
   * @param desc    The SortDescriptor to clone properties values from.
   */
  public SortDescriptor(SortDescriptor desc) {
    this(desc.indexName, desc.sortKeys, desc.descending, desc.localeName, desc.options);
  }

  /**
   *  Returns the number of Column components involved in the sort.
   * @return    The number of Column components involved in the sort.
   */
  public final int keyCount() {return sortKeys == null ? 0 : sortKeys.length; }

  /**
   *  Returns the String array containing the names of the Column components
   *  by which the data is sorted.
   *
   *  @return The String array containing the names of the Column components
   *  by which the data is sorted.
   */
  public final String[] getKeys() { return sortKeys; }

  /** @deprecated  Use isDescending(int i)
  */
  public final boolean isDescending() {
    if (descending == null || descending.length < 1)
      return false;
    for (int i=0; i< descending.length; i++) {
      if (!descending[i])
        return false;
    }
    return true;
  }

  private final boolean isAscending() {
    if (descending == null || descending.length < 1)
      return true;
    for (int index = 0; index < descending.length; ++index) {
      if (descending[index])
        return false;
    }
    return true;
  }

//!  public final boolean isDescending(int i) { return descending == null ? false : descending[i]; } bug 106233

  /**
   *  Determines whether or not the values in column i are in ascending
   *  or descending order.
   *
   * @param     i
   * @return    <b>true</b> if key i is descending.
   */
  public final boolean isDescending(int i) { return descending == null || i >= descending.length ? false : descending[i]; }

  /**
   * Read-only property that returns an array that has the descending value
   * for each key. This property applies to all applicable columns specified
   * in the keys property. A value of null means that all keys are ascending.
   *
   * @return  The array that has the descending value for each key.
   */
  public final boolean[] getDescending() { return descending; }


  /**
   * Read-only property that returns whether the sort considers (false) or
   * ignores (true) upper and lower case differences. Valid only for String
   * columns. This property applies to all applicable String columns specified in
   * the keys property.
   *
   * @return <b>true</b> if the sort considers  uppoer and lower case differences,
   *         <b>false</b> otherwise.
   */
  public final boolean isCaseInsensitive() { return caseInsensitive; }

  /**
   * @return  The String name of locale that this SortDescriptor was created with.
   *          If no localeName was specified, <b>null</b> is returned.
   *          This property is ignored for MemoryStore.
   *          MemoryStore always uses the locale of the StorageDataSet.
   *          DataStore (which maintains persistent indexes) respects this setting.
   *          If this property is <b>null</b>, DataStore will behave like MemoryStore and
   *          use the Locale of the StorageDataSet.
   */
  public final String getLocaleName() { return localeName; }

// must be immutable  public final void setUnique(boolean unique) { this.unique = unique; }
  /**
   *  This a constraint on column values. A row with a column value for sortKeys
   *  that is not unique cannot be added to the DataSet.
   *
   * @return
   */
  public final boolean isUnique() { return unique; }

//! must be immutable  public final void setIndexName(String name) { this.indexName = name; }
  /**
   * Returns the name of an index that maintains this sorting.
   * MemoryStore will ignore this property.
   *
   * @return    The name of the index that maintains this sorting.
   *           MemoryStore will ignore this property.
   */
  public final String getIndexName() { return this.indexName; }

  /* Do not make public.  It really should not be set while a dataSet
     is open.
  */
//! final void setRowFilterListener() { this.filter = filter; }

/**
 * Determines whether the SortDescriptor contains the same property
 * values as the specified SortDescriptor.
 *
 * @param descriptor    The SortDescriptor to compare property values against.
 * @return
 */
  public final boolean equals(SortDescriptor descriptor) {
    return equals(descriptor, null);
  }

  final boolean nameEquals(SortDescriptor desc) {
    if (indexName != null && desc.indexName != null) {
      if (indexName.length() > 0 && desc.indexName.length() > 0)
        return MatrixData.identifierEquals(indexName,desc.indexName);
    }
    return false;
  }


  private final boolean descendingEquals(SortDescriptor desc) {
    if (isDescending() && desc.isDescending())
      return true;
    if (isAscending() && desc.isAscending())
      return true;
    if ((descending == null) != (desc.descending == null))
      return false;

    if (descending != null && descending.length == desc.descending.length) {
      for (int index = 0; index < descending.length; ++index) {
        if (descending[index] != desc.descending[index])
          return false;
      }
    }
    return true;
  }

  private final boolean is(int option) {
    return (options & option) != 0;
  }

  private final boolean optionsEqual(SortDescriptor desc) {
    int options1 = options;
    int options2 = desc.options;
    boolean unique1 = unique;
    boolean unique2 = desc.unique;
    boolean case1   = caseInsensitive;
    boolean case2   = desc.caseInsensitive;
    if ((options2&Sort.DONT_CARE_UNIQUE) != 0) {
      options1 &= ~Sort.PRIMARY;
      options2 &= ~Sort.PRIMARY;
      unique1 = false;
      unique2 = false;
    }
    if ((options2&Sort.DONT_CARE_CASEINSENSITIVE) != 0) {
      options1 &= ~Sort.CASEINSENSITIVE;
      options2 &= ~Sort.CASEINSENSITIVE;
      case1 = false;
      case2 = false;
    }
    options1 &= ~Sort.DONT_CARE_MASK;
    options2 &= ~Sort.DONT_CARE_MASK;
    return (unique1 == unique2 && case1 == case2 && options1 == options2);
  }

  private final boolean equalsDefaultLocale(String name) {
    if (locale == null)
      locale  = Locale.getDefault();
    if (!locale.toString().equals(name))
      return false;
    return true;
  }

  /** @updated JB3.0
      Can be equal in two ways:

      1) Both descriptors have a non-null indexName property that are equal.
         If IndexNams are equal, no further tests for equality are made.
      2) If IndexName does not match, all of the other properties of
         SortDescriptor are compared for equality.

  */

  /**
   * Checks whether this SortDescriptor contains the same values as the
   * descriptor specified in the parameter of this method. If locale is not
   * specified, the default locale is used. This method returns <b>true</b>
   * if the SortDescriptors are the same, false otherwise. The SortDescriptors
   * can be equal in two ways:
   * <p>
   * <ul>
   *  <li>When both descriptors have a non-null indexName property that are equal.
   *      If indexNames are equal, no further tests for equality are made.</li>
   *  <li>If indexName does not match, all of the other properties of the
   *      SortDescriptor are compared for equality.</li>
   * </ul>
   *
   * @param descriptor
   * @param locale
   * @return
   */
  public final boolean equals(SortDescriptor descriptor, Locale locale) {

    if (nameEquals(descriptor))
      return true;
    if (   descendingEquals(descriptor)
        && keyCount() == descriptor.keyCount()
        && optionsEqual(descriptor)
        && (sortKeys == null) == (descriptor.sortKeys == null))
    {
      if (sortKeys == null)
        return true;

      if (!equalsIgnoreCase(sortKeys, descriptor.sortKeys))
        return false;

      if ((localeName == null || localeName.length() == 0) && (descriptor.localeName == null || descriptor.localeName.length() == 0))
        ;
      else if (localeName != descriptor.localeName) {

        if (localeName == null) {
          if (!equalsDefaultLocale(descriptor.localeName))
            return false;
        }
        else if (descriptor.localeName == null) {
          if (!descriptor.equalsDefaultLocale(localeName))
            return false;
        }
        else if (!localeName.equals(descriptor.localeName))
          return false;
      }

      return true;
    }

    return false;
  }

  static final boolean equalsIgnoreCase(String[] names1, String[] names2) {
    if (names1.length == names2.length) {
      for (int index = 0; index < names1.length; ++index) {
        String name1 = names1[index];
        String name2 = names2[index];
        if (!name1.equalsIgnoreCase(name2)) {
          return false;
        }
      }
    }
    return true;
  }

  /** Returns the String representation of the values stored in the SortDescriptor.
   * @return    The String representation of the values stored in the SortDescriptor.
   */
  public String toString() {
    FastStringBuffer fsb = new FastStringBuffer();

    if (sortKeys == null)
      return "";

    for (int i = 0; i < sortKeys.length; ++i) {
      if (i > 0) {
        fsb.append('|');
      }
      fsb.append(sortKeys[i]);
      if (descending != null && descending.length > i && descending[i])
        fsb.append(":desc");
    }
    toString(fsb, caseInsensitive);
    toString(fsb, unique);
    fsb.append(',');
    fsb.append(localeName);
    fsb.append(',');
    fsb.append(indexName);
    fsb.append(Integer.toHexString(options));
    return fsb.toString();
  }

  private final void toString(FastStringBuffer fsb, boolean val) {
    fsb.append(',');
    fsb.append(val?Boolean.TRUE.toString():Boolean.FALSE.toString());
  }

  /**
   * Returns the locale that this SortDescriptor was created with.
   *
   * @return  The locale that this SortDescriptor was created with.
   */
  public final Locale getLocale() {
    if (locale == null && localeName != null)
      locale = LocaleUtil.getLocale(localeName);
    return locale;
  }

  /**
   * Returns <b>true</b> if Sort.SORT_AS_INSERTED option has been set.
   *
   * @return <b>true</b> if Sort.SORT_AS_INSERTED option has been set.
  */
  public final boolean isSortAsInserted() {
    return (options & Sort.SORT_AS_INSERTED) != 0;
  }

  /**
   * Returns <b>true</b> if Sort.PRIMARY option has been set.
   * @return true if Sort.PRIMARY option has been set.
  */
  public final boolean isPrimary() {
    return (options & Sort.PRIMARY) == Sort.PRIMARY;
  }

  /**
   * Returns The Sort options.
      @return The Sort options.
  */
  public final int getOptions() {
    return options;
  }

  final void check()
    /*-throws DataSetException-*/
  {
  //! Could be supported, but currently just doesn't work.  -Steve.
  //!
    if (isSortAsInserted()) {
      if (descending != null && descending.length > 0 && descending[descending.length-1])
        DataSetException.invalidSortAsInserted();
    }
  }


          String              indexName;
  private boolean             unique;
  private boolean[]           descending;
  private boolean             caseInsensitive;
  private String[]            sortKeys;
  private String              localeName;
  private transient Locale    locale;
  private int                 options;
  private static final long serialVersionUID = 3L;
}
