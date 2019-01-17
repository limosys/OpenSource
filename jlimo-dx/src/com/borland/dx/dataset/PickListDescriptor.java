//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/PickListDescriptor.java,v 7.2 2003/05/20 18:47:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.DiagnosticJLimo;
import java.util.Vector;

/**
 *
 * The <CODE>PickListDescriptor</CODE> describes a pick list relationship between a <CODE>Column</CODE> of one <CODE>DataSet</CODE> (the <CODE>target</CODE> DataSet), and a second <CODE>DataSet</CODE> (the <CODE>source</CODE> DataSet) that provides values from which you can choose.

<!-- JDS start - remove first sentence -->
To access the <CODE>PickListDescriptor</CODE> in the JBuilder Inspector, select the <CODE>pickList</CODE> property of a  <CODE>Column</CODE> component.
<!-- JDS end -->
The following properties are stored in the <CODE>PickListDescriptor</CODE>:
<UL>
<LI><CODE>pickListDataSet</CODE> specifies the <CODE>DataSet</CODE> that contains the items to display in the pick list.
<LI><P><CODE>pickListDisplayColumns</CODE>
specifies the <CODE>Column</CODE> components of the <CODE>DataSet</CODE> to display in the pick list.
<LI><P><CODE>pickListColumns</CODE> specifies the columns of the <CODE>pickListDataSet</CODE> from which values in the selected row are copied to <CODE>destinationColumns</CODE>.
<LI><P><CODE>destinationColumns</CODE>  specifies the <CODE>Column</CODE> components of the target <CODE>DataSet</CODE> that are populated with the values associated with the selected pick list choice.
<LI><P><CODE>enforceIntegrity</CODE> determines whether data integrity rules are enforced on the data added to <CODE>destinationColumns</CODE>.  This property is not currently used.

<LI><P><CODE>lookupDisplayColumn</CODE> specifies which column to display (field display values) when the source data is not open. This property is used when the displayed items list differs from the values stored when an item is selected.

</UL>

<!-- JDS start - remove paragraph -->

<P>A dbSwing <CODE>JdbComboBox</CODE> or a <CODE>JdbTable</CODE> bound to a <CODE>DataSet</CODE> containing a <CODE>Column</CODE> that has its {@link com.borland.dx.dataset.Column#setPickList pickList} property set displays the <CODE>pickListDisplayColumns</CODE>
in a drop-down list when the column is edited.

<!-- JDS end -->

<P>There are three basic ways to configure the <CODE>pickList</CODE> property, the first two are  the more common uses:
<OL>
<LI><STRONG>A simple "fill in".</STRONG>  For visual controls, the pick list provides a selection of values from a <CODE>pickListDataSet</CODE>.  The selected values are filled into the specified columns.

<P>For this configuration, set the <CODE>pickListDataSet</CODE>, <CODE>destinationColumns</CODE>
(columns to copy to), <CODE>pickListColumns</CODE> (columns to copy from the
<CODE>pickListDataSet</CODE>), and <CODE>pickListDisplayColumns</CODE> (columns values to display
from the <CODE>pickListDataSet</CODE> when the <CODE>pickList</CODE> column is being edited).
<LI><P><STRONG>"Fill in" and "lookup" display</STRONG>.  Provides the same functionality as above, except you can specify a
<CODE>lookupDisplayColumn</CODE> from the <CODE>pickListDataSet</CODE>.  This allows you to store
a "code number" code in the column, but display a "code description".

<P>The values from the <CODE>lookupDisplayColumn</CODE> in the <CODE>pickListDataSet</CODE> are
displayed instead of the values stored in the <CODE>pickListColumns</CODE>.  A value
to display is chosen by using the current values in <CODE>destinationColumns</CODE> to
locate the row in the <CODE>pickListDataSet</CODE> that has the same values for its
<CODE>pickListColumns</CODE>.

<P>If a match is found, the <CODE>lookupDisplayColumn</CODE> value for that row in the
<CODE>pickListDataSet</CODE> is displayed.  Note that the <CODE>DataSet.setValue()</CODE> and <CODE>getValue()</CODE>
methods retrieve and set the "real" value stored.  To retrieve the display
value for such a column, use the <CODE>DataSet.getDisplayVariant()</CODE> method.

<LI><P><STRONG>Dynamically created read-only "lookup"</STRONG>.  This configuration is a little more work to setup, but allows an application to show both the code and code description values dynamically.  You can achieve a similar effect with the first configuration above, however, this configuration displays the code description
from the <CODE>pickListDataSet</CODE>.  So if the code description is changed in your
<CODE>pickListDataSet</CODE>, that change will be reflected in your <CODE>DataSet</CODE> since this display value is
always "looked up".  In the first configuration described above, if the code description changes in
the
<CODE>pickListDataSet</CODE>, your <CODE>DataSet</CODE> will still show the old "fill in" values.

<P>The lookup column is a special calculated column that retrieves its display
values
from the <CODE>pickListDataSet</CODE>.  Although the <CODE>pickList</CODE> editor is
activated when edits are attempted on this column, no value can be set on this column.
It always retrieves its values by looking them up in the <CODE>pickListDataSet</CODE>.
The lookup operation uses the same mechanism and properties described in
the second configuration above.  To
create a lookup <CODE>Column</CODE>, you must add a <CODE>Column</CODE> to your <CODE>DataSet</CODE> and set
its <CODE>calcType</CODE> property to <CODE>CalcType.LOOKUP</CODE>.  Then set the <CODE>pickList</CODE> property much like configuration #2.  The only difference is that the lookup column cannot be one of the columns listed in the <CODE>destinationColumns</CODE> property.


</OL>

<!-- JDS start - remove paragraph -->
<P>For a tutorial on adding a pick list in an application, see "Looking up choices with a pick list" in the <CITE>Database Application Developer's Guide</CITE>. For information on removing a pick list, see "Removing a picklist field".

<!-- JDS end -->

*/

/*

Note: This is the original comment for this class.

I choose to use the one from the JBuilder Reference pages that had
correct HTML tags [asc]


The PickListDescriptor describes a pick list relationship between a Column of
one DataSet and a second DataSet that provides values from which you can choose.
To Access the PickListDescriptor in the JBuilder Component Inspector, set the
picList property of a Column component.

The DataSet that contains the items to display in the pick list is specified in
the pickListDataSet property.  The pickListDisplayColumns property describes the
columns of that DataSet that are shown in the pick list.

Any control that follows the JBCL model-view architecture and is bound to a
DataSet Column component that has its PickList property set, uses the
PickListItemEditor as its itemEditor by default.  JBCL model-view controls that
automatically make use of the Column PickList property include:

GridControl
ListControl
FieldControl

Although the pickListDisplayColumns parameter is an array of String names of
Column values to display, the PickListItemEditor can only display a single
Column in the pick list to display multiple columns in a pick list, set the
itemEditor property of the Column to the PopupPickListItemEditor.
PopupPickListItemEditor is a component available on the JBCL tool palette page.

Once a row is selected, the values of the pickListColumns in the pickListDataSet
are copied into the destinationColumns of this DataSet .

In JBuilder 2.0 the lookupDisplayColumn property was added.  This is an optional
property setting that specified the name of the column in the pickListDataSet to
display values from.

There are three basic ways to configure the PickList property (number 1 and 2 will probably
be of most value):

1) Simple "fill in".  pickList editor for visual control that provides selection of values
from a pickListDataSet.  The selected values are filled into one or more column
values.

For this configuration set the pickListDataSet, destinationColumns
(columns to copy to), pickListColumns (columns to copy from the
pickListDataSet), and pickListDisplayColumns (columns values to display from the
pickListDataSet when the pickList Column is being edited)

2) "Fill in" and "lookup" display.  Same as #1, except you can specify a
lookupDisplayColumn from the pickListDataSet.  This allows you to store a "code
number" code in the column, but display a "code description".

The values from the lookupDisplayColumn in the pickListDataSet will be displayed
instead of the value that is stored in the pickList Column.  The value to
display is chosen by using the current values in destinationColumns to locate
the row in the pickListDataSet that has the same values for its pickListColumns.
If a match is found, the lookupDisplayColumn value for that row in the
pickListDataSet is used for display.  Note that the DataSet.set/get value
methods will retrieve and set the "real" value stored.  To retrieve the display
value for such a column, use the DataSet.getDisplayVariant() method.

3) Formal read only "lookup" display column.  This configuration is a little
more work to setup, but allows an application to show both the code and code
description values.  Although you can achieve a similar effect with
configuration #1, this configuration will display the code description from the
pickListDataSet.  So if the code description is changed in your pickListDataSet,
that change will be refleted in your DataSet since this display value is always
"looked up".  In configuration #1, if the code description changes in the
pickListDataSet, your dataSet will still show the old "fill in" values.

The lookup column is a special calc column that retrieves its display values
from the pickListDataSet.  Although the pickList editor will be activated when
edits are attempted on this column, no value can be set on this column.  It
always retrieves its values by looking them up in the pickListDataSet.  The
lookup operation uses the same mechanism and properties described in #2.  To
create a lookup Column, you must add a Column to your DataSet and set its
CalcType property to CalcType.LOOKUP.  Set up the pickList property much like
#2.  The only difference is that the lookup column cannot be one of the columns
listed in the destinationColumns property.

*/

public class PickListDescriptor implements java.io.Serializable {

/**
 * Constructs a PickListDescriptor object with the properties stated in its parameters.
 *
 * @param pickListDataSet         The DataSet object that contains the data for display
 *                                in the pick list.
 * @param pickListColumns         The columns of the pickListDataSet from which values in
 *                                the selected row are copied to the destinationColumns.
 * @param pickListDisplayColumns  The Column components of the DataSet to display in the
 *                                pick list. This property is expressed as an array of String
 *                                names.
 * @param destinationColumns      The Column components that are populated with the values
 *                                associated with the selected pick list choice. This property
 *                                is expressed as an array of String column names.
 * @param lookupDisplayColumn     The name of the Column in the picklistDataSet to display
 *                                the column whose pickList property is being set.
 * @param enforceIntegrity        Whether to enforce data integrity rules (data constraints)
 *                                on the data added to the destinationColumns.
 */
  public PickListDescriptor(  DataSet   pickListDataSet,
                              String[]  pickListColumns,
                              String[]  pickListDisplayColumns,
                              String[]  destinationColumns,
                              String    lookupDisplayColumn,
                              boolean   enforceIntegrity
                           )
  {
//!    Diagnostic.check(pickListColumns != null);  JOAL: Fires during design
    this.pickListDataSet        = pickListDataSet;
    this.pickListColumns        = pickListColumns;
    this.pickListDisplayColumns = pickListDisplayColumns;
    this.destinationColumns     = destinationColumns;
    this.enforceIntegrity       = enforceIntegrity;
    this.lookupDisplayColumn    = lookupDisplayColumn;
  }

  /**
   *  Constructs a PickListDescriptor object with the properties stated in its parameters.
   *
   * @param pickListDataSet           The DataSet object that contains the data for display
   *                                  in the pick list.
   * @param pickListColumns           The columns of the pickListDataSet from which values
   *                                  in the selected row are copied to the destinationColumns.
   * @param pickListDisplayColumns    The Column components of the DataSet to display in the
   *                                  pick list. This property is expressed as an array of
   *                                  String column names. If your pick list contains multiple
   *                                  columns, you must also set the itemEditor property of the
   *                                  Column to an instance of the PopupPickListItemEditor.
   *                                  The default pick list item editor is the PickListItemEditor,
   *                                  which can only display a picklist with a single column.
   * @param destinationColumns        The Column components that are populated with the values
   *                                  associated with the selected pick list choice. This
   *                                  property is expressed as an array of String column names.
   * @param enforceIntegrity          Whether to enforce data integrity rules (data constraints)
   *                                  on the data added to the destinationColumns.
   */
  public PickListDescriptor(  DataSet   pickListDataSet,
                              String[]  pickListColumns,
                              String[]  pickListDisplayColumns,
                              String[]  destinationColumns,
                              boolean   enforceIntegrity
                           )
  {
    this(pickListDataSet, pickListColumns, pickListDisplayColumns, destinationColumns, null, enforceIntegrity);
  }

  /**
      The pickListDataSet used to choose values from.
  */
  public final DataSet getPickListDataSet() { return pickListDataSet; }

  /**
      Columns to Display from the pickListDataSet.
  */
  public final String[] getPickListDisplayColumns() { return pickListDisplayColumns; }

  /**
      Columns to fill in from the pickListDataSet.
  */
  public final String[] getPickListColumns() { return pickListColumns; }

  /**
      Columns to fill in on the destination dataSet.
  */
  public final String[] getDestinationColumns() { return destinationColumns; }

  /**
      @since JB2.0
      Name of column in Picklist DataSet to display for this column by data aware
      controls.
  */
  public final String getLookupDisplayColumn() { return lookupDisplayColumn; }

  public boolean isEnforceIntegrity() { return enforceIntegrity; }

  private String[]        destinationColumns;

  private DataSet         pickListDataSet;
  private String[]        pickListColumns;
  private String[]        pickListDisplayColumns;
  private String          lookupDisplayColumn;
  private boolean         enforceIntegrity;
  private static final long serialVersionUID = 1L;
}
