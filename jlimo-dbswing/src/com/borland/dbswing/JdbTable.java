/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */


//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dbswing;

import java.beans.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.plaf.UIResource;
import javax.swing.table.*;

import com.borland.dx.dataset.*;
import com.borland.jb.util.ErrorResponse;

/**
 * <p>A data-aware extension of <code>JTable</code>. It
 * adds support for the following features.</p>
 *
 * <ul>
 * <li>Multi-row column headers</li>
 * <li>Hidden columns</li>
 * <li>A customizable row header</li>
 * <li>Additional <code>TableCell</code> editors and renderers</li>
 * <li>Smart default column widths</li>
 * <li>Single-column sorting (when used with a <code>DataSet</code>)</li>
 * <li>Popup context menu </li>
 * <li>Selection within the table remains synchronized with <code>DataSet</code> navigation.</li>
 * <li>Using data from SQL data sources (via DataExpress
 * <code>DataSets</code>)</li>
 * </ul>
 *
 * <p>As with other dbSwing controls, <code>JdbTable</code> allows
 * you to set display properties on the component, but when
 * properties are not set, values are inherited from the
 * <code>Column</code>s of the <code>DataSet</code> to which the
 * table is bound. </p>
 *
 * <a name="working_with_tables"></a>
 * <H3>Working with tables</H3>
 *
 * <p><code>JdbTable</code> enhances <code>JTable</code>, its
 * corresponding Swing component, by adding the ability to easily
 * display and edit data from a DataExpress  <code>DataSet</code>.
 * <code>DataSet</code>s are row-oriented: you are always
 * positioned at one row, you can only edit that row, and your
 * edits must pass validation checks before you can move to a new
 * row. <code>JdbTable</code> is optimized to handle database data,
 * and this specialization brings with it a number of changes from
 *  <code>JTable</code>:</p>
 *
 * <ul>
 *
 * <li><p>You can't multi-select cells from multiple rows in a
 * <code>JdbTable</code>. By default, selection is limited to a
 * single cell and only that cell is highlighted. Optionally, you
 * can select an entire row, but you still edit one cell at a
 * time.</p></li>
 *
 * <li><p><code>JdbTable</code> adds display and edit masks for
 * more powerful display and editing of database data types.</p></li>
 *
 * <li><p>Navigation and editing keystrokes strengthen the notion
 * of entering and exiting edit mode. Edit masks are invoked when
 * editing begins and field-level validation is done when it ends.
 * Row-level validation is done when you move to a new row. This
 * validation detects and rejects data that would violate database
 * constraints as soon as it is entered.</p></li>
 *
 * <li><p>By default, <code>JdbTable</code> displays all the
 * <code>Column</code>s of the <code>DataSet</code> it is bound to
 * in the order they appear in the <code>DataSet</code>. Its
 * <code>hiddenColumns</code> and <code>customizedColumns</code>
 * properties let you hide and reorder <code>Column</code>s. </p></li>
 *
 * <li><p><code>JdbTable</code> provides default cell renderers and
 * editors more geared to database data types.</p></li>
 *
 * <li><p>A popup menu lets you sort the table, insert and delete
 * rows, and perform other operations.</p>
 * </ul>
 *
 * <p>All of this assumes that the <code>JdbTable</code>'s
 * <code>dataSet</code> property is set or its model is connected
 * to a <code>DataSet</code>. It is possible to use a
 * <code>JdbTable</code> without using a <code>DataSet</code>,
 * populating it as you would a <code>JTable</code> and getting
 * some extra behavior that <code>JTable</code> does not provide.
 * However, it is much more common to populate a
 * <code>JdbTable</code> from a <code>DataSet</code>.</p>
 *
 * <p>Like other scrollable Swing and dbSwing components,
 * <code>JdbTable</code> will not be scrollable unless it is placed
 * in the viewport area of a <code><a href="TableScrollPane.html">TableScrollPane</a></code>
 * or <code>JScrollPane</code>. Also, it won't have row and column
 * headers because the headers are actually added to the header
 * area of the scroll pane. dbSwing's <code>TableScrollPane</code>,
 * an extension of <code>JScrollPane</code>, includes options that
 * speed up scrolling, especially in large tables.</p>
 *
 * <a name="selection_validation_posting"></a>
 * <h4>Selection, field validation, and row posting</h4>
 *
 * <p>Each time you end edit mode in a field in a
 * <code>DataSet</code>, JBuilder checks that the new value can be
 * parsed to the expected data type and that it satisfies any
 * validation checks (minimum value, for example) defined for its
 * <code>Column</code>. If the value passes these tests, it can be
 * "put." If it fails, an error message is displayed and the field
 * remains in edit mode.</p>
 *
 * <p><code>JdbTable</code> supports the <code>DataSet</code>'s
 * focus on validating one field value at a time by restricting
 * editing to a single cell and highlighting that cell. When you're
 * done editing a field value, use <kbd>Enter</kbd> or
 * <kbd>Tab</kbd> to invoke end-edit processing.</p>
 *
 * <p>JBuilder also performs row-level validation, checking, for
 * example, that all required columns have non-blank values and
 * that columns for which the <code>DataSet</code> has unique
 * indexes do not contain values that duplicate existing rows.
 * Again, an error message is displayed if these checks fail.</p>
 *
 * <p>If the checks pass, all new and modified field values are
 * saved to the <code>DataSet</code>; the row is "posted."
 * <code>JdbTable</code> supports the <code>DataSet</code>'s
 * row-level validation by refusing to navigate to a new row if
 * changes to the current row can't be posted.</p>
 *
 * <p>To select an entire row in a <code>JdbTable</code>, set its
 * <code>rowSelectionAllowed</code> property to
 * <code>true</code> and its <code>cellSelectionEnabled</code>
 * property to <code>false</code>. If you find that
 * the visual indication that a cell has focus is too subtle, you
 * can use the <code>editableFocusedCellForeground</code> and
 * <code>editableFocusedCellBackground</code> properties to
 * distinguish it.</p>
 *
 * <p>The following list summarizes <code>JdbTable</code>'s
 * navigation and editing keystrokes:</p>
 *
 * <ul>
 *
 * <li>Use <kbd>Tab</kbd>, <kbd>Shift+Tab</kbd>, <kbd>Up</kbd>,
 * <kbd>Down</kbd>, <kbd>Left</kbd>, and <kbd>Right</kbd> to move
 * from one cell to another in the table.</li>
 *
 * <li>Use <kbd>Ctrl+Tab</kbd> or <kbd>Shift+Ctrl+Tab</kbd> to move
 * out of the table to the next or previous component.</li>
 *
 * <li>Press <kbd>Home</kbd> or <kbd>End</kbd> to move to the first
 * or last cells of the row and <kbd>Ctrl+Home</kbd> or
 * <kbd>Ctrl+End</kbd> to move to the top or bottom of the
 * column.</li>
 *
 * <li>Begin typing to append to the value in the current cell, or
 * use <kbd>Ctrl+Enter</kbd> to place a text cursor in the cell. At
 * this point, <kbd>Left</kbd>, <kbd>Right</kbd>, <kbd>Home</kbd>,
 * and <kbd>End</kbd> move within the current cell.</li>
 *
 * <li>Press <kbd>Enter</kbd>, <kbd>Tab</kbd>, or
 * <kbd>Shift+Tab</kbd> to end editing in the cell. If the value is
 * invalid, a message will be displayed on the
 * <code>JdbStatusLabel</code> or in an error dialog, and the cell
 * will remain in edit mode.</li>
 *
 * <li><P>Press <kbd>Escape</kbd> to cancel, leaving the cell's
 * value as it was before you began editing.</li>
 * </ul>
 *
 * <a name="row_operations"></a>
 * <h4>Row operations</h4>
 *
 * <p>To sort a <code>JdbTable</code> by the values in a column,
 * click that column's header. To toggle between ascending and
 * descending order, click the header again. You can also
 * right-click anywhere in the data of a <code>JdbTable</code> to
 * open a popup menu of row operations. In addition to selecting a
 * column to sort by, you can return to the <code>DataSet</code>'s
 * original, unsorted order.</p>
 *
 * <p>JBuilder, like most database servers, appends newly inserted
 * rows at the end of a <code>DataSet</code>. In "&lt;unsorted&gt;"
 * order, a new row will "fly away" to the end of the
 * <code>DataSet</code> when you navigate off it or post it.</p>
 *
 * <p>To insert a new row into a <code>JdbTable</code>, use
 * <kbd>Ctrl+Ins</kbd>. Columns that have default values defined
 * will contain those values and all other columns will be empty.
 * If you navigate off a row that has only blank or default values,
 * it will be discarded. To delete a row, use <kbd>Ctrl+Del</kbd>.
 * You can also use the popup menu to insert and delete rows.
 * Insert and delete operations may be disallowed by the
 * <code>DataSet</code>.</p>
 *
 * <p><a href="JdbNavToolBar.html"><code>JdbNavToolBar</code></a>
 * is very often used with <code>JdbTable</code>.
 * <code>JdbNavToolBar</code> automatically detects and binds to
 * the active component's <code>DataSet</code>. It can be used to
 * navigate, insert and delete rows, and save <code>DataSet</code>
 * changes back to a server, and also provides visual indications
 * of editing status and row position.</p>
 *
 * <a name="customizing_appearance"></a>
 * <h4>Customizing a table's appearance</h4>
 *
 * <p>The default appearance and behavior of a
 * <code>JdbTable</code> is only a starting point.
 * <code>JTable</code> has many properties that customize how it
 * looks and works, and <code>JdbTable</code> adds others. Here are
 * some appearance changes that are easy to make:</p>
 *
 * <p><strong>Column headers:</strong> By default, a
 * <code>JdbTable</code> has a column header with black text on a
 * gray background. Each column's default text is the
 * <code>caption</code> property of the corresponding
 * <code>DataSet</code> <code>Column</code>. To change the text,
 * set the <code>caption</code> property. To display several lines
 * of text in a column header, separate them with newline
 * characters, <kbd>'\n'</kbd>, in the <code>Column</code>'s
 * caption. To hide column headers, set the
 * <code>columnHeaderVisible</code> property to
 * <code>false</code>.</p>
 *
 * <p>You can also change the color or font of the column header or
 * disallow rearrangement of columns at runtime with code like
 * this:</p>
 *
 * <pre>
 * myJdbTable.getTableHeader().setBackground(Color.cyan);
 * myJdbTable.getTableHeader().setReorderingAllowed(false);
 * </pre>
 *
 * <p><strong>Row headers:</strong> By default, the leftmost
 * column of a <code>JdbTable</code> displays row numbers. You can
 * suppress it by setting the <code>rowHeaderVisible</code>
 * property to <code>false</code>.</p>
 *
 * <p><strong>Grid properties:</strong> By default, table cells
 * are separated horizontally and vertically by thin gray lines. To
 * change their color, set the <code>gridColor</code> property. To
 * hide grid lines, set the <code>showHorizontalLines</code>
 * property and/or the <code>showVerticalLines</code> property to
 * <code>false</code>. To alter the margin between cell values and
 * the grid, set the <code>intercellSpacing</code> property. This
 * property requires two integers, the horizontal and vertical
 * margins in pixels.</p>
 *
 * <p><strong>Table cell properties:</strong> To customize cells
 * in the table, you can set the <code>font</code>,
 * <code>foreground</code>, <code>background</code>,
 * <code>selectionForeground</code>,  <code>selectionBackground</code>,
<code>editableFocusedCellForeground</code>
 * and <code>editableFocusedCellBackground</code> properties, among
 * others.</p>
 *
 * <a name="hiding_column_in_table"></a>
 * <h4>Hiding columns in a <code>JdbTable</code></h4>
 *
 * <p>If you don't want to display all of a <code>DataSet</code>'s
 * <code>Column</code>s, use <code>JdbTable</code>'s
 * <code>hiddenColumns</code> property. This property can't be set
 * in JBuilder's UI designer because it takes an array, but the
 * code is easy, for example:</p>
 *
 * <pre>
 * myJdbTable.setHiddenColumns(new int[] { 5, 6, 8 } );
 * </pre>
 *
 * <p>where 5, 6, and 8 are the ordinals (counting from 0) of the
 * data set columns that should not be displayed. Put this code in
 * <code>jbInit()</code>. To reset, pass <code>null</code> instead
 * of an array of ints.</p>
 *
 * <a name="custom_column_in_table"></a>
 * <h4>Customizing columns in a <code>JdbTable</code></h4>
 *
 * <p><code>JdbTable</code>'s <code>customColumns</code> property
 * associates <code>TableColumn</code> objects with columns of a
 * <code>JdbTable</code>. The <code>TableColumns</code> may be
 * customized, or they may be used only to control the order of
 * columns in the <code>JdbTable</code>. </p>
 *
 * <p>In order to describe customized columns, first note the three
 * types of columns: </p>
 *
 * <ul>
 * <li><p>The <code>DataSet</code>'s <code>Column</code> objects.</p>
 *
 * <p>Note the uppercase "C" - <code>Column</code> is the name of a
 * class in the <code>com.borland.dx.dataset</code> package. Many
 * <code>Column</code>     properties affect how their data is
 * displayed, so most customization can be done by setting
 * <code>Column</code> properties.  This customization applies to
 * any component that displays the <code>Column</code>'s data,
 * which is not always what you want. </p></li>
 *
 * <li><p>The columns of the <code>JdbTable</code>.</p>
 *
 * <p>Each <code>DataSet</code> <code>Column</code> - except those
 * that are not visible or are excluded by <code>JdbTable</code>'s
 * <code>hiddenColumns</code> property - is displayed in a column
 * in the <code>JdbTable</code> component.  By default,
 * <code>DataSet</code> <code>Column</code>s are matched in
 * left-to-right order with <code>JdbTable</code> columns.  * </p></li>
 *
 * <li><p>The <code>javax.swing.table.TableColumn</code> objects in
 * a <code>JdbTable</code>'s <code>TableColumnModel</code>.</p>
 *
 * <p>Instead of letting <code>JdbTable</code> create default
 * <code>TableColumn</code> objects, you can instantiate one or
 * more <code>TableColumn</code>s, set their
 * <code>modelIndex</code> properties to associate them with
 * <code>DataSet</code> <code>Column</code>s, and pass them to a
 * <code>JdbTable</code> by setting its <code>customColumns</code>
 * property. (To determine the model index, count only the visible
 * data set <code>Column</code>s, not those that are hidden or not
 * visible, starting from zero.) These <code>TableColumn</code>
 * objects provide another way to customize the table's appearance.
 * This customization only affects the <code>JdbTable</code> that
 * owns the <code>TableColumn</code>s. </p></li>
 *</ul>
 *
 * <p>As part of the call to <code>setCustomColumns</code>, you
 * include an array of numbers that specify the column of the table
 * in which to display each <code>TableColumn</code>. To reorder
 * columns in a <code>JdbTable</code>, you define a
 * <code>TableColumn</code> for each column you want to move and
 * set its value in this array appropriately.  To accept a
 * <code>TableColumn</code>'s default position, use -1.  Instead of
 * reordering columns, or in addition, you can modify a
 * <code>JdbTable</code> by setting properties on your
 * <code>TableColumn</code>s. </p>
 *
 * <p>Properties include:</p>
 *
 * <ul>
 * <li><code>minWidth</code></li>
 * <li><code>maxWidth</code></li>
 * <li><code>resizable</code></li>
 * <li><code>cellEditor</code></li>
 * <li><code>cellRenderer</code></li>
 * </ul>
 *
 * <p>The <code>customColumns</code> property does not require a
 * DataExpress <code>DataSet</code>. You can fill a
 * <code>JdbTable</code> with data from another source by setting
 * its model property to any <code>TableModel</code>, then take
 * advantage of <code>customColumns</code> and other
 * <code>JdbTable</code> features that enhance display and runtime
 * manipulation of data. </p>
 *
 * <p>The <code>customColumns</code> property cannot be set through
 * JBuilder's UI designer but some preparation can be done in the
 * designer.  Use the BeanChooser to select a
 * <code>javax.swing.table.TableColumn</code> object, drop it into
 * a design, and set its <code>modelIndex</code> property.  If this
 * <code>TableColumn</code> is being used only to set the position
 * of a column in a <code>JdbTable</code>, no other properties need
 * be set.  Otherwise, the <code>TableColumn</code> may be
 * customized by setting other properties.  You must code the
 * actual call to <code>setCustomColumns()</code> because it takes
 * a <a href="CustomColumnsDescriptor.html">*<code>CustomColumnsDescriptor</code></a>,
 * which can't be manipulated in the designer.  See the online Help
 * or source code for <code>JdbTable</code>'s
 * <code>setCustomColumns()</code> method for an example.</p>
 *
 * <p><code>TableColumn</code> properties and hiding and reordering
 * <code>JdbTable</code> columns will not be shown in the UI
 * designer. </p>
 *
 * <a name="cell_renderer"></a>
 * <h4>Cell renderers and editors</h4>
 *
 * <p>Swing uses renderers to display data and editors to edit
 * data. Different components use different renderers and editors.
 * <code>JTable</code> defines simple default renderers of type
 * <code>DefaultTableCellRenderer</code> for numeric, boolean,
 * image, icon, and object columns. <code>JdbTable</code> usually
 * replaces <code>JTable</code>'s defaults with renderers and
 * editors from the dbSwing package that perform better and support
 * display and edit masks. However, if you explicitly set a
 * column's renderer or editor, <code>JdbTable</code> will not
 * replace it.</p>
 *
 * <p>The default cell editor for a column with a picklist is a
 * combo box cell editor. The column is displayed as usual. When
 * edited, a cell in the column looks and works very much like a
 * <a href="JdbComboBox.html"><code>JdbComboBox</code></a>; a
 * drop-down list of values to choose from is displayed and the
 * value selected from the list becomes the new field value. To get
 * this behavior, set the picklist property of a
 * <code>Column</code> in a <code>DataSet</code> just as you would
 * when using a <code>JdbComboBox</code> and display the
 * <code>DataSet</code> in a <code>JdbTable</code>.</p>
 *
 * <p>When a column whose data type is
 * <code>Variant.INPUTSTREAM</code> is displayed in
 * a <code>JdbTable</code>, its default cell editor is a <a href="TableImageEditor.html"><code>TableImageEditor</code></a>.
 * As much of the image as will fit is displayed in the cell. If
 * you double-click the cell, a dialog opens that displays the full
 * image, with scrollbars if necessary, and allows you to choose a
 * new image file. <code>TableImageEditor</code> can display GIFs,
 * JPGs, and any BMP file that you can view in Windows Paint. If you
 * display an <code>INPUTSTREAM</code> column that does not contain
 * images in a <code>JdbTable</code>, you should provide a custom
 * cell renderer and editor for it.</p>
 *
 * <p>dbSwing also provides a read-only editor, <a href="TableImageReadOnlyEditor.html"><code>TableImageReadOnlyEditor</code></a>,
 * that also displays images in a dialog but does not provide a
 * file chooser and therefore does not allow the field value to be
 * edited. <code>TableImageReadOnlyEditor</code> is never installed
 * as the default cell editor for a column, but can be used instead
 * of <code>TableImageEditor</code> if desired.</p>
 *
 * <p>To replace the default cell renderer or editor in a column of
 * a <code>JdbTable</code>, set the corresponding
 * <code>Column</code>'s <code>itemPainter</code> or
 * <code>itemEditor</code> property. For example, to display a
 * column of images but prevent changes to the data, set the
 * <code>Column</code>'s <code>itemEditor</code> property to an
 * instance of <code>TableImageReadOnlyEditor</code>. If you're
 * working in the UI designer, you can use the BeanChooser to
 * select <code>TableImageReadOnlyEditor</code> from the
 * <code>com.borland.dbswing</code> package, drop an instance into
 * the component tree, and set the <code>Column</code>'s
 * <code>itemEditor</code> property to it.</p>
 *
 * <p>Before you write a custom cell renderer, consider using a
 * <code>ColumnPaintListener</code> instead. It passes you almost
 * the same parameters as a cell renderer, so
 * you can do almost anything with a <code>ColumnPaintListener</code> that you would do in a cell
 * renderer. A <code>ColumnPaintListener</code> differs from a cell
 * renderer in a few ways:</p>
 *
 * <ul>
 * <li>You get the value to be displayed as a <code>Variant</code>.
 *  In a cell renderer it is a <code>String</code>, formatted
 * according to the display mask.</li>
 *
 * <li>You can modify the value passed to you. This only changes
 * the displayed value, not the stored value.</li>
 *
 * <li>Instead of replacing a default renderer with your own, you
 * write an event handler for a <code>Column</code>'s painting or
 * editing event. All components bound to the column that support
 * the <code>ColumnPaint</code> interface will use the event
 * handler.</li>
 * </ul>
 *
 * <p>If you decide to write a custom cell renderer for
 * <code>JdbTable</code>, you will probably extend
 * <code>DBCellRenderer</code>. It's a public inner class of
 * <code>JdbTable</code>, so the syntax is unusual. Notice the
 * extends clause and the call to <code>super()</code> in
 * the constructor of this example. This custom cell renderer
 * displays negative numbers in red:</p>
 *
 * <pre>
 * import java.awt.*;
 * import javax.swing.*;
 * import com.borland.dbswing.*;
 *
 * public class NegativeNumberRenderer extends JdbTable.DBCellRenderer {
 *
 *    public NegativeNumberRenderer(JdbTable jdbTable) {
 *      jdbTable.super();
 *    }
 *    public Component getTableCellRendererComponent(JTable table, Object value,
 *        boolean isSelected, boolean hasFocus, int row, int column) {
 *      super.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column);
 *      if (value != null) {
 *        try {
 *         // Compare numeric value of string to zero. This assumes that a negative
 *         // value has a leading minus sign, so only handles simple numeric formats
 *         if (Double.parseDouble((String) value) &lt; 0.0d)
 *           super.setForeground(Color.red);
 *        }
 *        // Use the default foreground if we can't get a number from the string
 *        catch (NumberFormatException nfe) {
 * 	   }
 *      }
 *      return this;
 *    }
 * }
 *
 * </pre>
 *
 * <a name="table_columns"></a>
 * <h4><code>TableColumns</code></h4>
 *
 * <p>When you set a <code>JdbTable</code>'s <code>dataSet</code>
 * property, each visible column in the <code>DataSet</code> is
 * displayed in a column in the <code>JdbTable</code>. More
 * specifically, each <code>com.borland.dx.dataset.Column</code>
 * object is associated with a <code>javax.swing.table.TableColumn</code>
 * object. Often you can give a <code>JdbTable</code> the desired
 * appearance and behavior by setting <code>Column</code>
 * properties. For example, <code>JdbTable</code> inherits font,
 * color, alignment, and width settings from the
 * <code>Column</code>s in its <code>DataSet</code>. This is the
 * easy way to customize a <code>JdbTable</code>.</p>
 *
 *  <p>Sometimes, you have to create <code>TableColumn</code>s
 * yourself so you can set their properties. For example, you have
 * to set <code>TableColumn</code> properties to control runtime
 * resizing of an individual column in a <code>JdbTable</code>.
 * More often, you have a choice: instead of instantiating a
 * <code>TableColumn</code> and setting a property on it, you can
 * get the same result or a very similar result another way.</p>
 *
 * <p>Unless there's a compelling reason to use a
 * <code>TableColumn</code>, we recommend the alternative approach
 * in these cases:</p>
 *
 * <ul>
 * <li><code>cellRenderer</code> and <code>cellEditor</code>
 * properties: Use <code>Column</code>'s <code>itemPainter</code>
 * and <code>itemEditor</code> properties to assign cell renderers
 * and editors. You don't have to use a cell renderer at all; you
 * can use a <code>ColumnPaintListener</code> instead.</li>
 *
 * <li><code>headerValue</code> property: Set the
 * <code>Column</code>'s <code>caption</code> instead. For
 * multi-line headers, put newline characters (<kbd>'\n'</kbd>) in
 * the caption.</li>
 *
 * <li>Order of columns in <code>JdbTable</code>: Alter the order
 * of columns in a <code>DataSet</code> by
 * setting the <code>preferredOrdinal</code> property.</li>
 *</ul>
 */
public class JdbTable extends JTable
  implements NavigationListener, AccessListener, MouseListener, KeyListener, DataChangeListener,
             PropertyChangeListener, ActionListener, DataSetAware, java.io.Serializable, FocusListener, EditListener {

  // Inherited constructors

 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the <code>null</code>
  * constructor of its superclass.</p>
  */
  public JdbTable() {
    super(new AbstractTableModel() {
      public int getColumnCount() { return 1; };
      public int getRowCount() { return 1; };
      public String getColumnName(int col) { return "A"; }; 
      public Object getValueAt(int row, int col) { return ""; };  
    });
  }


 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the constructor of its superclass and passing to it
  * a <code>TableModel</code> as the source of the table's data. It uses a default column model and a default
  * selection model.</p>
  *
  * @param dm The data model for the new table.
  */
  public JdbTable(TableModel dm) {
    super(dm);
  }

 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the constructor of its superclass and passing to it
  * a <code>TableModel</code> and <code>TableColumnModel</code> as the source of the table's
  * data. It uses a default selection model.</p>
  *
  * @param dm The data model for the new table.
  * @param cm The column model for the table.
  */
  public JdbTable(TableModel dm, TableColumnModel cm) {
    super(dm, cm);
  }

 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the constructor of its superclass and passing to it
  * a <code>TableModel</code>, <code>TableColumnModel</code>, and <code>ListSelectionModel</code>.
  * If any of these parameters are <code>null</code>, the constructor initializes the table with a default model.</p>
  *
  * @param dm The table model for the table.
  * @param cm The column model for the table.
  * @param sm The row selection model for the table.
  */
  public JdbTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
    super(dm, cm, sm);
  }

 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the constructor of its superclass that takes
  * integer values for the number of columns and rows in the table.</p>
  *
  * @param numColumns The number of columns in the table. The columns have names of the form &quot;A&quot;, &quot;B&quot;, &quot;C&quot;, and so on.
  * @param numRows The number of rows in the table.
  */
  public JdbTable(int numColumns, int numRows) {
    super(numColumns, numRows);
  }

 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the constructor of its superclass that takes a
  * two-dimensional array of <code>Object</code>s for the table's data and an array of <code>Object</code>s for the
  * table's column names.</p>
  *
  * @param data The data for the new table.
  * @param columnNames The names of the column of the table.
  */
  public JdbTable(Vector data, Vector columnNames) {
    super(data, columnNames);
  }

 /**
  * <p>Constructs a <code>JdbTable</code> component by calling the constructor of its superclass that takes
  * <code>Vector</code>s for the table's data and its column names.</p>
  *
  * @param data The data for the new table.
  * @param columnNames The names of the column of the table.
  */
  public JdbTable(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
  }

  private class InsertRowAction extends AbstractAction {
    private InsertRowAction() {
      super("insert-row");   
    }

    public void actionPerformed(ActionEvent e) {
      if (JdbTable.this.isEditable() &&
          dataSet != null && dataSet.isEnableInsert() &&
          !dataSet.isEditingNewRow() && dataSet.isEditable()) {
        boolean readOnly = false;
        if (dataSet instanceof StorageDataSet) {
          readOnly = ((StorageDataSet) dataSet).isReadOnly();
        }
        if (!readOnly) {
          try {
            dataSet.insertRow(true);
          }
          catch (DataSetException ex) {
            DBExceptionHandler.handleException(dataSet, JdbTable.this, ex);
          }
        }
      }
    }
  }

  private class AppendRowAction extends AbstractAction {
    private AppendRowAction() {
      super("append-row");   
    }

    public void actionPerformed(ActionEvent e) {
      if (JdbTable.this.isEditable() &&
          dataSet != null && dataSet.isEnableInsert() &&
          !dataSet.isEditingNewRow() && dataSet.isEditable()) {
        boolean readOnly = false;
        if (dataSet instanceof StorageDataSet) {
          readOnly = ((StorageDataSet) dataSet).isReadOnly();
        }
        if (!readOnly) {
          try {
            if (dataSet.atLast()) {
              dataSet.insertRow(false);
            }
            else {
              dataSet.next();
            }
          }
          catch (DataSetException ex) {
            DBExceptionHandler.handleException(dataSet, JdbTable.this, ex);
          }
        }
      }
    }
  }

  private class DeleteRowAction extends AbstractAction {
    private DeleteRowAction() {
      super("delete-row");   
    }

    public void actionPerformed(ActionEvent e) {
      if (JdbTable.this.isEditable() &&
          dataSet != null && dataSet.isEnableDelete() && dataSet.isEditable()) {
        boolean readOnly = false;
        if (dataSet instanceof StorageDataSet) {
          readOnly = ((StorageDataSet) dataSet).isReadOnly();
        }
        if (!readOnly) {
          try {
            if (!dataSet.isEmpty()) {
              dataSet.deleteRow();
            }
          }
          catch (DataSetException ex) {
            DBExceptionHandler.handleException(dataSet, JdbTable.this, ex);
          }
        }
      }
    }
  }

  /*
  private class CancelEditAction extends AbstractAction {
    private CancelEditAction() {
      super("cancel-edit"); 
    }

    public void actionPerformed(ActionEvent e) {
      if (isEditing()) {
        removeEditor();
      }
    }
  }
  */

  class StartEditAction extends AbstractAction {
    private EventObject eventObject = new EventObject(this);
    private StartEditAction() {
      super("start-edit"); 
    }

    public void actionPerformed(ActionEvent e) {
      if (!isEditing()) {
        editCellAt(getSelectedRow(), getSelectedColumn(), eventObject);
      }
    }
  }

  /**
   * <p>Overrides <code>JTable</code>'s <code>updateUI()</code> method to make a few
   * platform-independent UI changes. First, the default
   * actions for <KBD>Enter</KBD> and <KBD>Shift+Enter</KBD> are changed to behave like
   * <KBD>Tab</KBD> and <KBD>Shift+Tab</KBD>, respectively.
   * Then, <KBD>Ctrl+Ins</KBD> and <KBD>Ctrl+Del</KBD> are bound to the insert and delete row actions.</p>
   */
  public void updateUI() {
    super.updateUI();

    unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
    unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.SHIFT_MASK));
    unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

    ActionListener tabAction = getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
    registerKeyboardAction(tabAction,
                           KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                           JComponent.WHEN_FOCUSED);
    ActionListener reverseTabAction = getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, ActionEvent.SHIFT_MASK));
    registerKeyboardAction(reverseTabAction,
                           KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.SHIFT_MASK),
                           JComponent.WHEN_FOCUSED);

    registerKeyboardAction(new InsertRowAction(),
                           KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, ActionEvent.CTRL_MASK),
                           JComponent.WHEN_FOCUSED);
    registerKeyboardAction(new AppendRowAction(),
                           KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                           JComponent.WHEN_FOCUSED);
    registerKeyboardAction(new DeleteRowAction(),
                           KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK),
                           JComponent.WHEN_FOCUSED);
    ActionListener beginEditAction = getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
//    registerKeyboardAction(new StartEditAction(),
    registerKeyboardAction(beginEditAction,
                           KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.CTRL_MASK),
                           JComponent.WHEN_FOCUSED);

    addKeyListener(this);
  }

  //
  // JdbTable properties
  //
  /**
   * <p>Sets the <code>DataSet</code> used to build the
   * <code>TableModel</code> that contains the data for the
   * table. <code>setDataSet()</code> creates an instance
   * of <code>DBTableModel</code> with the given
   * <code>DataSet</code>, then sets it as the table's
   * model by calling <code>setModel()</code>. If the
   * <code>DataSet</code> is <code>null</code>, the
   *  <code>DBTableModel</code> creates an empty table model.</p>
   *
   * @param dataSet The <code>DataSet</code> used to build the <code>TableModel</code> that contains the data for the table.
   * @see #getDataSet
   * @see #setModel
   */
  public void setDataSet(DataSet dataSet) {
    pendingDataSet = dataSet;
    if (addNotifyCalled) {
      bindDataSet();
    }
    else {
      this.dataSet = dataSet;
    }
  }

  /**
   * <p>Returns the <code>DataSet</code> used to build the <code>TableModel</code>.</p>
   *
   * <p>A non-null dataSet implies the table's <code>dataModel</code> is a <code>DBTableModel</code>.
    * This invariant condition is used to avoid "instanceof DBTableModel" calls.</p>
   *
   * @return  The <code>DataSet</code> used to build the <code>TableModel</code>.
   * @see     #setDataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * <p>Sets the table model columns that are hidden from the table's display.</p>
   *
   * <p>Invoking <code>setHiddenColumns()</code> always
   * causes the table's column model to be rebuilt
   * regardless of the value of <code>autoCreateDefaultColumnsFromModel()</code>. Each
   * element of the array is the index of the column in the
   * table model you want to be a hidden column.</p>
   *
   * <p>Hidden columns take precedence over customized columns. </p>
   *
   * <p>The order of the elements in the array is not
   * significant. Invalid model indexes are ignored.</p>
   *
   * <p>If the model used by this table undergoes a
   * structural change (i.e., columns are added or removed)
   * then this property will likely need to be set again
   * with model indices applicable to the new model.
   * Specifying a <code>null</code> value resets (removes)
   * all existing hidden columns, and should be done before
   * the model (<code>DataSet</code>) changes to avoid
   * hiding columns which no longer exist in the new model,
   * or accidentally hiding columns in the new model which
   * shouldn't be hidden.</p>
   *
   * <p>The <code>hiddenColumns</code> array is not cloned. If you make changes to the array after this property has been set,
   * unexpected side effects can occur.</p>
   *
   * @param hiddenColumns Integer array of hidden model indices.
   * @see #getHiddenColumns
   */
  public void setHiddenColumns(int [] hiddenColumns) {
    this.hiddenColumns = hiddenColumns;
    updateColumnModel();
  }

  /**
   * <p>Returns which table model columns are hidden from the table's display.</p>
   * <p>Each element of the array is the index of the column in the
   * table model which should be a hidden column.
   * The array returned is not a clone of the table's hiddenColumn array.
   * Changes to the returned array can cause unexpected side effects.</p>
   *
   * @return  Integer array of hidden model indices.
   * @see #setHiddenColumns
   */
  public int [] getHiddenColumns() {
    return hiddenColumns;
  }

  /**
   * <p>Sets <code>TableColumn</code> properties for a subset of this table's <code>TableColumns</code>.</p>
   *
   * <p>Array order is significant--it determines the visual order
   * of columns. Unspecified table columns obtain default
   * properties from the <code>dataModel</code>  and appear in
   * <code>dataModel</code> order. Hidden columns take precedence
   * over customized columns. Invalid model indexes are ignored.</p>
   *
   * <p>The <code>customColumns</code> array is not cloned. Changes
   * to the array after this property has been set can cause unexpected side effects.</p>
   *
   * @param customColumns Array of customized <code>TableColumns</code>.
   * @see #getCustomizedColumns
   * @deprecated Use <code>setCustomColumns()</code> instead.
   */

  public void setCustomizedColumns(TableColumn [] customColumns) {
    int [] columnOrder = new int[customColumns.length];
    for (int colNo = 0; colNo < customColumns.length; colNo++) {
      columnOrder[colNo] = colNo;
    }
    setCustomColumns(new CustomColumnsDescriptor(columnOrder, customColumns));
  }

  /**
   * <p>Returns an array of this table's customized
   * <code>TableColumns</code>.</p>
   *
   * <p>The array returned is not a clone of the table's <code>customColumn</code> array.
   * Changes to the returned array can cause unexpected side effects.</p>
   *
   * @return Array of customized <code>TableColumns</code>.
   * @see #setCustomizedColumns
   * @deprecated Use <code>getCustomColumns()</code> instead.
   */
  public TableColumn [] getCustomizedColumns() {
    if (getCustomColumns() != null) {
      return getCustomColumns().getTableColumns();
    }
    return null;
  }

  public void setSelectionMode(int selectionMode) {
    if (selectionMode != ListSelectionModel.SINGLE_SELECTION) {
      throw new IllegalArgumentException(Res._UnsupSelMode);     
    }
    super.setSelectionMode(selectionMode);
  }

  /**
   * <p>Sets custom properties for a subset of this table's <code>TableColumns</code>.</p>
   *
   * <p><code>CustomColumnsDescriptor</code> is simply a container class with two
   * properties, an int array for visual column positions
   * <code>(columnPositions)</code> and an array of <code>TableColumns</code> <code>(tableColumns)</code>.</p>
   *
   * <p>The size of the <code>columnPositions</code> and <code>tableColumns</code> arrays must be the same.
   * The value in the <code>columnPosition</code> array indicates the <code>TableColumnModel</code> (visual)
   * position for the corresponding entry in the <code>tableColumns</code> array.
   * A <code>columnPosition</code> value of -1 leaves the corresponding <code>tableColumn</code> in
   * its 'natural' or default order in the <code>TableModel</code>.</p>
   *
   * <p>Each <code>TableColumn</code> in the <code>tableColumns</code> array must specify a valid index
   * in the <code>TableModel</code> (not <code>DataSet</code>) of the <code>TableColumn</code> with customized
   * properties to set.  Properties other than the default value are assumed
   * to be explicitly set, and will take precedence over corresponding
   * values set on a <code>DataSet's Column</code>.</p>
   *
   *<p>The 'identifier' property is used internally
   * by <code>JdbTable</code>.  When using a <code>DataSet</code>, you should not set this property.</p>
   *
   * <p>Setting this property (whether or not using a <code>DataSet</code>) will prevent
   * <code>JdbTable</code> from properly merging new hidden or custom columns with
   * an existing <code>TableColumnModel</code>.</p>
   *
   * <p>Hidden columns take precedence over customized columns, so customized
   * properties on hidden columns are ignored.</p>
   *
   * <p>If the model used by this table undergoes a structural change (i.e.,
   * columns are added or removed) then this property will likely need to
   * be set again with model indices applicable to the new model.
   * Setting a null <code>CustomColumnsDescriptor</code> removes all customized column
   * properties, and should be done before the model <code>(DataSet)</code> changes
   * to avoid setting customized properties on columns which no longer
   * exist in the new model.</p>
   *
   * <p>Example:</p>
   *
   *<pre>
   * TableModel tableModel = new AbstractTableModel() {
   *   public int getColumnCount() {
   *     return 4;
   *   }
   *   public int getRowCount() {
   *     return 1;
   *   }
   *   public Object getValueAt(int row, int col) {
   *     return "";
   *   }
   *   public boolean isCellEditable(int row, int col) {
   *     return true;
   *   }
   *   public String getColumnName(int index) {
   *     if (index == 0) {
   *       return "Name";
   *     }
   *     else if (index == 1) {
   *       return "Address";
   *     }
   *     else if (index == 2) {
   *       return "Phone";
   *     }
   *     else if (index == 3) {
   *       return "email";
   *     }
   *     return null;
   *   }
   * };
   * TableColumn addressColumn = new TableColumn(1);
   * // sets preferred width of Address column in the table model
   * addressColumn.setPreferredWidth(225);
   *
   * TableColumn phoneColumn = new TableColumn(2);
   * // sets multi-line header on Phone column in the table model
   * phoneColumn.setHeaderValue(new String [] { "Phone", "Number"});
   *
   * TableColumn emailColumn = new TableColumn(3);
   * // sets custom cell renderer on email column in the table model
   * emailColumn.setCellEditor(new DefaultCellEditor(new JComboBox(new String { "hello" })));
   *
   * // set table's table model
   * jdbTable1.setModel(tableModel);
   *
   * // make email column appear as third column in table
   * // address column appears in its 'natural' position
   * // make phoneColumn appear as first column in table
   * jdbTable1.setCustomColumns(new CustomColumnsDescriptor(new int [] { 2, -1, 0}, new TableColumn [] { emailColumn, addressColumn, phoneColumn}));
   *</pre>
   *
   * @param customColumnDescriptor The custom properties for a subset of this table's <code>TableColumns</code>.
   * @see #getCustomColumns
   */
  public void setCustomColumns(CustomColumnsDescriptor customColumnDescriptor) {
    this.customColumnDescriptor = customColumnDescriptor;
    applyCustomColumns = true;
    updateColumnModel();
  }

  /**
   * <p>Returns the <code>CustomColumnsDescriptor</code> defining customized <code>TableColumn</code>
   * properties of this table.</p>
   *
   * @return The custom properties for a subset of this table's <code>TableColumns</code>.
   * @see #setCustomColumns
   */
  public CustomColumnsDescriptor getCustomColumns() {
    return customColumnDescriptor;
  }

  /**
   * <p>Makes the specified row visible and throws an
<code>IllegalArgumentException</code> if the row
   * is not in the valid range.</p>
   *
   * @param row The row whose visibility is in question.
   */
  public void ensureRowIsVisible(int row) {
    scrollRectToVisible(getCellRect(row, 0, false));
  }

  /**
   * Sets the table's row header.
   * <p>A table has a TableRowHeader by default, which is
   * customizable through its properties.  To add behavior
   * not already provided by <code>TableRowHeader</code>, make a
   * new class extending it, and set it as the new row header.</p>
   *
   * <p>Setting a <code>null</code> row header has the side effect
   * of hiding the row header. Setting a non-null row
   * header has the side effect of displaying the
   * row header if it was not already visible.
   *
   * @param rowHeader The new <code>rowHeader</code>.
   * @see #getRowHeader
   */
  public void setRowHeader(TableRowHeader rowHeader) {
    if (this.rowHeader != null) {
      this.rowHeader.setModel(null);
      this.rowHeader.setCellRenderer(null);
    }
    this.rowHeader = rowHeader;
    setRowHeaderVisible(rowHeader != null);
  }

  /**
   * <p>Returns the table's row header.</p>
   *
   * @return The current row header.
   * @see    #setRowHeader
   */
  public TableRowHeader getRowHeader() {
    return rowHeader;
  }

  /**
   * <p>Determines whether a row header is visible. If
   * <code>rowHeaderVisible</code> is <code>true</code>, the row
   * header is visible; if it is <code>false</code>, the row header
   * is not visible. The default is <code>true</code>.</p>
   *
   * <p>Using <code>setRowHeader()</code> to set a new non-null row
   * header has the side effect of setting
   * <code>rowHeaderVisible</code> to <code>true</code>.</p>
   *
   * @param  rowHeaderVisible   If <code>true</code>, the row header should be visible.
   * @see #isRowHeaderVisible
   * @see #isRowHeaderVisible
   */
// This was in original javadoc description.
//   * @beaninfo
//   *       bound: true
//   * description: Whether or not the row header should be visible
  public void setRowHeaderVisible(boolean rowHeaderVisible) {
    this.rowHeaderVisible = rowHeaderVisible;
    if (isDisplayable()) {
      Container viewport = getParent();
      if (viewport instanceof JViewport) {
        Container parent = viewport.getParent();
        if (parent instanceof JScrollPane) {
          JScrollPane scrollPane = (JScrollPane) parent;
          if (viewport != null && ((JViewport) viewport).getView() == this) {
            // Add row header if visible
            if (rowHeaderVisible) {
              if (rowHeader == null) {
                rowHeader = new TableRowHeader(this);
              }
              scrollPane.setRowHeaderView(rowHeader);
            }
            else {
              scrollPane.setRowHeaderView(null);
            }
          }
        }
      }
    }
    firePropertyChange("rowHeaderVisible", !rowHeaderVisible, rowHeaderVisible);  
  }

  /**
   * <p>Returns <code>true</code> if the row header is visible.</p>
   *
   * @return  If <code>true</code>, the row header is visible.
   * @see #setRowHeaderVisible
   */
  public boolean isRowHeaderVisible() {
    return rowHeaderVisible;
  }


  /**
   * <p>Determines whether the column header displaying the column
   * names is visible. If <code>columnHeaderVisible</code> is
   * <code>true</code>, the column header is visible; if it is
   * <code>false</code>, the column header is not visible.</p>
   *
   * @param  columnHeaderVisible   If <code>true</code>, the column header is visible.
   * @see    #isColumnHeaderVisible
   */
//This was in original Javadoc description
//   * @beaninfo
//   *       bound: true
//   * description: Whether or not the column header should be //visible
  public void setColumnHeaderVisible(boolean columnHeaderVisible) {
    this.columnHeaderVisible = columnHeaderVisible;
    if (isDisplayable()) {
      Container viewport = getParent();
      if (viewport instanceof JViewport) {
        Container parent = viewport.getParent();
        if (parent instanceof JScrollPane) {
          JScrollPane scrollPane = (JScrollPane) parent;
          if (viewport != null && ((JViewport) viewport).getView() == this) {
            if (columnHeaderVisible) {
              scrollPane.setColumnHeaderView(getTableHeader());
            }
            else {
              scrollPane.setColumnHeaderView(null);
            }
          }
        }
      }
    }
    firePropertyChange("columnHeaderVisible", !columnHeaderVisible, columnHeaderVisible);  
  }

  /**
   * <p>Returns <code>true</code> if the column header is visible.</p>
   *
   * @return If <code>true</code>, the column header is visible.
   * @see #setColumnHeaderVisible
   */
  public boolean isColumnHeaderVisible() {
    return columnHeaderVisible;
  }

  /**
   * <p>Determines if the default initial column widths are
   * calculated by the model column's data type. If they are,
   * <code>smartColumnWidths</code> is <code>true</code>. The
   * default is <code>true</code>.</p>
   *
   * <p>When using a <code>DataSet</code> as the table's model, set
   * this property value to <code>true</code> to have default
   * column widths determined by analysis of the corresponding
   * <code>DataSet</code> <code>Column</code>. </p>
   *
   * <p>A customized column's <code>preferredSize</code> property
   * takes precedence over this property. Therefore, to override
   * the smart default column width, define a custom column and set
   * its width. Or you can override the <code>getDefaultTableColumnWidth()</code> method that contains the
   * algorithm for determining the default width of a column.</p>
   *
   * <p>If a <code>DataSet</code> is not used to build the table's model and customized
   * columns are specified but the <code>preferredWidth</code> is left as the default,
   * setting this property to <code>true</code> will automatically resize the column
   * width to be the width of the column's <code>headerValue()</code> (converted to a <code>String</code>),
   * or the default <code>TableColumn</code> width (75), whichever is greater.
   * If the column's <code>headerValue</code>() is an array of strings, the width of the widest
   * string is used.
   *
   * <p><strong>Note:</strong> The value of this property is
   * ignored if a <code>DataSet</code> was not used to build this
   * table's model because data type information is not available.</p>
   *
   * @param  smartColumnWidths   If <code>true</code>, the default column width is determined by column data type.
   * @see #isSmartColumnWidths
   * @see #setCustomColumns
   */
  public void setSmartColumnWidths(boolean smartColumnWidths) {
    this.smartColumnWidths = smartColumnWidths;
  }

  /**
   * <p>Returns <code>true</code> if default column widths are deduced by the model column's data type.</p>
   * <p><strong>Note:</strong> The value of this property is
   * ignored if a <code>DataSet</code> was not used to build this
   * table's model because data type information is not available.</p>
   *
   * @return  If <code>true</code>, the default column width is determined by column data type.
   * @see #setSmartColumnWidths
   */
  public boolean isSmartColumnWidths() {
    return smartColumnWidths;
  }

  /**
   * <p>Sets whether <code>JdbTable</code> automatically updates
   * the selection model whenever the data model changes. Setting
   * this property to <code>false</code>, for example, prevents
   * <code>JdbTable</code> from setting the selection when it is
   * first displayed. The default property value is
   * <code>true</code>.</p>
   *
   * @param autoSelection If <code>true</code>, <code>JdbTable</code> automatically updates the
   * selection model whenever the data model changes.
   * @see #isAutoSelection
   */
  public void setAutoSelection(boolean autoSelection) {
    this.autoSelection = autoSelection;
  }

  /**
   * Returns whether <code>JdbTable</code> will automatically update the selection
   * model whenever the data model changes.
   *
   * @return If <code>true</code>, <code>JdbTable</code> automatically updates the
   * selection model whenever the data model changes.
   * @see #isAutoSelection
   */
  public boolean isAutoSelection() {
    return autoSelection;
  }

  /**
   * <p>Sets whether right-clicking in the table will display
   * a popup menu. The default value is <code>true</code>.</p>
   *
   * @param popupEnabled If <code>true</code>, a popup menu is enabled.
   * @see #isPopupMenuEnabled
   * @see #createPopupMenu
   */
  public void setPopupMenuEnabled(boolean popupEnabled) {
    this.popupEnabled = popupEnabled;
  }

  /**
   * <p>Returns whether right-clicking in the table will display
   * a popup menu. </p>
   *
   * @return If <code>true</code>, a popup menu is enabled.
   * @see #setPopupMenuEnabled
   * @see #createPopupMenu
   */
  public boolean isPopupMenuEnabled() {
    return popupEnabled;
  }

  /**
   * <p>Sets whether clicking in a column of the table header will
   * sort rows by that column.  The default value is <code>true</code>.</p>
   *
   * @param enableSort If <code>true</code>, clicking in a column of the table header will sort rows by that column.
   * @see #isColumnSortEnabled
   */
  public void setColumnSortEnabled(boolean enableSort) {
    this.enableSort = enableSort;
  }

  /**
   * <p>Returns whether clicking in a column of the table header will
   * sort rows by that column.  </p>
   *
   * @return If <code>true</code>, clicking in a column of the table header will sort rows by that column.
   * @see #setColumnSortEnabled
   */
  public boolean isColumnSortEnabled() {
    return enableSort;
  }

  /**
   * <p>Sets whether entries of the table are editable or not.</p>
   *
   * <ul>
   * <li>If <code>false</code>, individual cells will not be editable regardless of
   * whether the <code>TableModel</code> allows them to be edited.</li>
   *<li>If <code>true</code>, a particular cell will be editable only if the <code>TableModel</code>
   * allows the cell to be edited.</li>
   * </ul>
   *
   * @param editable If code>true</code>, a particular cell will be editable only if the <code>TableModel</code>
   * allows the cell to be edited.
   * @see #isEditable
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  /**
   * <p>Returns whether entries of the table may be edited or not.</p>
   *
   * @return If code>true</code>, a particular cell will be editable only if the <code>TableModel</code>
   * allows the cell to be edited.
   * @see #setEditable
   */
  public boolean isEditable() {
    return editable;
  }

  //
  // overridden JTable methods
  //
  /**
   * <p>Adds the table's column header to the enclosing
   * <code>ScrollPane</code>'s <code>ColumnHeaderView</code>. The
   * <code>configureEnclosingScrollPane()</code> method is normally
   * invoked only once, when <code>addNotify()</code> is called.</p>
   *
   */
  protected void configureEnclosingScrollPane() {
    Container viewport = getParent();
    if (viewport instanceof JViewport) {
      Container parent = viewport.getParent();
      if (parent instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) parent;
        // If we've haven't been added as the scrollpane's view, then do nothing
        if (viewport != null && ((JViewport) viewport).getView() == this) {
          // If we're in the main viewport, then add columns and column header
          if (columnHeaderVisible) {
            scrollPane.setColumnHeaderView(getTableHeader());
            getTableHeader().revalidate();
            getTableHeader().repaint();
          }
          else {
            scrollPane.setColumnHeaderView(null);
          }
          // scrollPane.getViewport().setBackingStoreEnabled(true);
          Border border = scrollPane.getBorder();
          if (border == null || border instanceof UIResource) {
            scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));  
          }
          // Add row header if visible
          if (rowHeaderVisible) {
            if (rowHeader == null) {
              rowHeader = new TableRowHeader(this);
            }
            scrollPane.setRowHeaderView(rowHeader);
            rowHeader.revalidate();
            rowHeader.repaint();
          }
          else {
            scrollPane.setRowHeaderView(null);
          }
          scrollPane.revalidate();
          scrollPane.repaint();
        }
      }
    }
  }

  /**
   * <p>Rebuilds the <code>TableColumnModel</code> from the
   * <code>TableModel</code> and overrides <code>JTable</code>'s
   * method of the same name.</p>
   *
   * <p><code>createDefaultColumnsFromModel()</code> is called by
   * <code>JTable</code> whenever the table model changes and
   * <code>AutoCreateColumnsFromModel</code> is <code>true</code>.
   * It is also called by <code>setHiddenColumns()</code> and
   * <code>setCustomizedColumns()</code> regardless of the state of
   * <code>AutoCreateColumnsFromModel</code>.</p>
   *
   *<p> <code>createDefaultColumnsFromModel</code> adds
   * uncustomized columns to the table column model in the same
   * order as they are returned by the model.
   * <code>createDefaultColumnsFromModel</code> normally isn't called
   * until a table has been realized. This improves performance at
   * load time, since the column model only needs to be built once
   * - after multiple properties have been set - rather than every
   * time a property changes.</p>
   */
  public void createDefaultColumnsFromModel() {
    DataSet newDataSet = null;
    boolean keepExistingColumnModel = true;

    // Have to check the dataset here for the case where a DBTableModel
    // is being used as the model and the dataset on the model is changed.
    // In this case, a table 'model' property change won't occur, and we
    // have no other way to know that the dataset has changed
    if (getModel() instanceof DBTableModel) {
      dbTableModel = (DBTableModel) getModel();
      newDataSet = dbTableModel.getDataSet();
    }
    else {
      dbTableModel = null;
    }

    if (newDataSet != oldDataSet) {
      keepExistingColumnModel = false;
    }

    if (oldDataSet != null) {
      oldDataSet.removeNavigationListener(this);
      oldDataSet.removeAccessListener(this);
      oldDataSet.removeDataChangeListener(this);
      if (oldDataSet instanceof StorageDataSet) {
        ((StorageDataSet)oldDataSet).removeEditListener(this);
      }
    }
    removeMouseListener(this);
    removeFocusListener(this);

    if (newDataSet != null) {
      newDataSet.addNavigationListener(this);
      newDataSet.addAccessListener(this);
      newDataSet.addDataChangeListener(this);
      if (newDataSet instanceof StorageDataSet) {
        ((StorageDataSet)newDataSet).addEditListener(this);
      }
      addMouseListener(this);
      addFocusListener(this);
      dataSet = newDataSet;
    }

    if (oldDataSet == null && !(getModel() instanceof DBTableModel)) {
      keepExistingColumnModel = false;
    }

    dataSet = newDataSet;
    oldDataSet = newDataSet;

    // Build maps for hidden and customized columns.  Since the ratio of these
    // kinds of columns to total columns is expected to be small, this is
    // probably better than making the column property values
    // maps themselves (plus the property setter code is more compact).
    // The map index refers to a table model index.
    // A true value implies a hidden or customized column.
    boolean [] hiddenColumnMap = new boolean[getModel().getColumnCount()];
    TableColumn [] customizedColumnMap = new TableColumn[hiddenColumnMap.length];
    if (hiddenColumns != null) {
      for (int columnIndex = 0; columnIndex < hiddenColumns.length; columnIndex++) {
        if (hiddenColumns[columnIndex] < hiddenColumnMap.length) {
          hiddenColumnMap[hiddenColumns[columnIndex]] = true;
        }
      }
    }

    TableColumn [] customColumns = new TableColumn[0];
    if (customColumnDescriptor != null && customizedColumnMap.length > 0) {
      customColumns = customColumnDescriptor.getTableColumns();
      if (customColumns != null) {
        for (int columnIndex = 0; columnIndex < customColumns.length; columnIndex++) {
          if (customColumns[columnIndex].getModelIndex() < customizedColumnMap.length) {
            customizedColumnMap[customColumns[columnIndex].getModelIndex()] = customColumns[columnIndex];
          }
        }
      }
    }

    columnModel.removeColumnModelListener(this);

    // column identifiers are used to identify columns from the
    // old column model which should be carried over into the
    // new column model
    Hashtable oldIdentifiers = null;
    if (keepExistingColumnModel && getModel().getColumnCount() > 0) {
      TableColumnModel columnModel = getColumnModel();
      int oldColumnModelSize = columnModel.getColumnCount();
      oldIdentifiers = new Hashtable();
      TableColumn existingColumn;
      for (int columnNo = 0; columnNo < oldColumnModelSize; columnNo++) {
        existingColumn = columnModel.getColumn(columnNo);
        if (existingColumn.getIdentifier() != null) {
          oldIdentifiers.put(existingColumn.getIdentifier(), existingColumn);
        }
      }
    }

    TableColumn [] newModelOrder;
    Hashtable newTableColumns = new Hashtable();
    int newModelIndex = 0;
    if (hiddenColumns == null || (hiddenColumnMap.length < hiddenColumns.length)) {
      // handles the case where either there are no hidden columns or
      // hidden columns are being defined, but the model hasn't been built yet
      newModelOrder = new TableColumn[hiddenColumnMap.length];
    }
    else {
      newModelOrder = new TableColumn[hiddenColumnMap.length - hiddenColumns.length];
    }

    for (int modelIndex = 0; modelIndex < hiddenColumnMap.length; modelIndex++) {
      if (hiddenColumnMap[modelIndex]) {
        continue;
      }
      TableColumn newColumn = new TableColumn(modelIndex);

      // Do we have a DBTableModel?  If so, assign initial column properties
      // from the DataSet Column (may be overridden later by the customized column).
      if (dbTableModel != null) {
        Column dataSetColumn = dbTableModel.getColumn(modelIndex);
        bindDataSetColumnProperties(newColumn, dataSetColumn);

        if (keepExistingColumnModel && oldIdentifiers != null) {
          // Old dataset and new dataset were the same, but a structurally incompatible query
          // may have been re-executed on the dataset (or more columns may simply have been
          // hidden or customized).  Since there's not enough information
          // in the DataSet AccessEvent to tell us what actually happened, we try to
          // merge the existing column model with the new one.
          TableColumn existingColumn;
          if ((existingColumn = (TableColumn) oldIdentifiers.get(dataSetColumn.getColumnName())) != null) {
            inheritCustomColumnProperties(newColumn, existingColumn);
          }
        }

      }
      else {
        newColumn.setHeaderValue(getModel().getColumnName(modelIndex));
        newColumn.setIdentifier(newColumn.getHeaderValue());
        if (smartColumnWidths) {
          int width = getMaxCaptionWidth(newColumn.getHeaderValue(), null);
          if (width > newColumn.getPreferredWidth()) {
            newColumn.setPreferredWidth(width);
          }
          newColumn.setWidth(newColumn.getPreferredWidth());
        }

        if (keepExistingColumnModel && oldIdentifiers != null) {
          TableColumn existingColumn;
          if ((existingColumn = (TableColumn) oldIdentifiers.get(newColumn.getIdentifier())) != null) {
            inheritCustomColumnProperties(newColumn, existingColumn);
            newColumn.setIdentifier(existingColumn.getIdentifier());
          }
        }
      }

      TableColumn column;
      if (applyCustomColumns &&
          (column = customizedColumnMap[modelIndex]) != null) {
        inheritCustomColumnProperties(newColumn, column);
      }

      // If the header is an array of strings or a string with embedded newlines,
      // use the TableHeaderRenderer multi-line renderer
      if (newColumn.getHeaderValue() instanceof String [] ||
          (newColumn.getHeaderValue() instanceof String &&
           ((String) newColumn.getHeaderValue()).indexOf("\n") != -1)) {  
        newColumn.setHeaderRenderer(new TableHeaderRenderer());
      }
      else {
        // workaround for change in JDK 1.3 which no longer keeps a cell renderer for each column.
        // in order to do different tooltips on each column header, we need to have a different
        // renderer for each column.
        DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                         boolean isSelected, boolean hasFocus, int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                }

                setText((value == null) ? "" : value.toString()); 
                setBorder(UIManager.getBorder("TableHeader.cellBorder")); 
                return this;
            }
        };
        label.setHorizontalAlignment(JLabel.CENTER);
        newColumn.setHeaderRenderer(label);
      }

      // workaround for change in JDK 1.3 which no longer keeps a cell renderer for each column
      JComponent headerRenderer = null;
      if (newColumn.getHeaderRenderer() instanceof JComponent) {
        headerRenderer = (JComponent) newColumn.getHeaderRenderer();
      }
      else {
        if (DBUtilities.is1pt3() && headerRenderer == null && getTableHeader() != null &&
            getTableHeader().getDefaultRenderer() instanceof JComponent) {
          headerRenderer = (JComponent) getTableHeader().getDefaultRenderer();
        }
      }
      if (headerRenderer != null) {
        if (newColumn.getHeaderValue() instanceof String []) {
          headerRenderer.setToolTipText(getConcatenatedString((String []) newColumn.getHeaderValue()));
        }
        else {
          headerRenderer.setToolTipText(newColumn.getHeaderValue().toString().replace('\n', ' '));
        }
      }

      newModelOrder[newModelIndex++] = newColumn;
      newTableColumns.put(newColumn.getIdentifier(), newColumn);
    }
    applyCustomColumns = false;

    TableColumnModel newColumnModel = createDefaultColumnModel();
    newColumnModel.setColumnMargin(getColumnModel().getColumnMargin());
    newColumnModel.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Restore existing column order
    if (keepExistingColumnModel && oldIdentifiers != null && getModel().getColumnCount() > 0) {
      TableColumn newColumn;

      TableColumnModel oldColumnModel = getColumnModel();
      int oldColumnModelSize = oldColumnModel.getColumnCount();
      TableColumn oldColumn;

      for (int columnNo = 0; columnNo < oldColumnModelSize; columnNo++) {
        oldColumn = oldColumnModel.getColumn(columnNo);
        if ((newColumn = (TableColumn) newTableColumns.remove(oldColumn.getIdentifier())) != null) {
          newColumnModel.addColumn(newColumn);
        }
      }
    }

    // add any remaining (new) columns in model order
    if (newTableColumns.size() > 0) {
      TableColumn newColumn;
      int newColumnModelSize = newModelOrder.length;
      boolean skipMove = newColumnModelSize == newTableColumns.size();
      for (int columnNo = 0; columnNo < newColumnModelSize; columnNo++) {
        if ((newColumn = (TableColumn) newTableColumns.remove(newModelOrder[columnNo].getIdentifier())) != null) {
          newColumnModel.addColumn(newColumn);
          if (!skipMove && columnNo < newColumnModel.getColumnCount()) {
            newColumnModel.moveColumn(newColumnModel.getColumnCount() - 1, columnNo);
          }
        }
      }
    }

    // Set visual order of custom columns, if necessary.  Maintain
    // left to right precedence in case of duplicate column positions.
    // Since the number of custom columns is expected to be fairly
    // small, a simple sorting algorithm is used.
    int [] columnPositions = null;
    if (customColumnDescriptor != null &&
        ((columnPositions = customColumnDescriptor.getColumnPositions()) != null) &&
        getModel().getColumnCount() > 0) {

      // build a sorted column position array
      int [] unsortedColumnPositions = new int[columnPositions.length];
      System.arraycopy(columnPositions, 0, unsortedColumnPositions, 0, columnPositions.length);
      int [] sortedColumnPositions = new int[columnPositions.length];
      int [] modelMap = new int[customColumns.length];
      for (int index = 0; index < customColumns.length; index++) {
        modelMap[index] = customColumns[index].getModelIndex();
      }
      int index = 0;
      int minIndex;
      int minPos;
      int columnPos;
      int swapInt;
      while (index < unsortedColumnPositions.length) {
        minPos = unsortedColumnPositions[index];
        minIndex = index;
        for (int columnNo = unsortedColumnPositions.length - 1; columnNo > index; columnNo--) {
          columnPos = unsortedColumnPositions[columnNo];
          if (columnPos <= minPos) {
            minPos = columnPos;
            minIndex = columnNo;
          }
        }

        // sortedColumnPositions contains either a positive column location
        // or a negative number to indicate natural order position or a hidden column
        sortedColumnPositions[index] = minPos;

        // Do this check to fix bug 156814
        int hiddenIndex = customColumns[minIndex].getModelIndex();
        if (hiddenIndex >= hiddenColumnMap.length || hiddenColumnMap[hiddenIndex]) {
          sortedColumnPositions[index] = -1;
        }
        swapInt = unsortedColumnPositions[minIndex];
        unsortedColumnPositions[minIndex] = unsortedColumnPositions[index];
        unsortedColumnPositions[index] = swapInt;

        swapInt = modelMap[minIndex];
        modelMap[minIndex] = modelMap[index];
        modelMap[index] = swapInt;

        index++;
      }


      // move columns
//      for (int columnNo = 0; columnNo < sortedColumnPositions.length; columnNo++) {
      for (int columnNo = sortedColumnPositions.length - 1; columnNo >= 0; columnNo--) {
        // ignore naturally ordered or hidden columns
        if (sortedColumnPositions[columnNo] < 0) {
//          continue;
          break;
        }
        if (sortedColumnPositions[columnNo] >= newColumnModel.getColumnCount()) {
          sortedColumnPositions[columnNo] = newColumnModel.getColumnCount() - 1;
        }

        for (int column = 0; column < newColumnModel.getColumnCount(); column++) {
          if (newColumnModel.getColumn(column).getModelIndex() == modelMap[columnNo]) {
            newColumnModel.moveColumn(column, sortedColumnPositions[columnNo]);
            break;
          }
        }
      }
    }

    columnModel.addColumnModelListener(this);
    setColumnModel(newColumnModel);

//    setRowHeaderVisible(true);
//    setColumnHeaderVisible(true);
    configureEnclosingScrollPane();

    // set the initial search column for JdbNavField
//    TableColumn column;
//    if (newColumnModel.getColumnCount() > 0 &&
//        (column = newColumnModel.getColumn(0)) != null && dataSet != null) {
//      dataSet.setLastColumnVisited(column.getIdentifier().toString());
//    }

    if (autoSelection && !rebindColumnProperties && (addNotifyCalled || !keepExistingColumnModel)) {
      ignoreNavigation = true;
      if (getColumnCount() > 0) {
        setColumnSelectionInterval(0, 0);
      }
      if (getRowCount() > 0) {
        setRowSelectionInterval(0, 0);
      }
      ignoreNavigation = false;
      Rectangle rect = getCellRect(getSelectedRow(), getSelectedColumn(), true);
      scrollRectToVisible(rect);
    }

    releaseWrappers();

    if (getSelectedRow() == -1 && getSelectedColumn() != -1) {
      ignoreNavigation = true;
      if (getRowCount() > 0) {
        setRowSelectionInterval(0, 0);
      }
      ignoreNavigation = false;
    }

    if (getSelectedRow() != -1 && getSelectedColumn() == -1) {
      if (getColumnCount() > 0) {
        setColumnSelectionInterval(0, 0);
      }
    }

  }

  private void bindDataSetColumnProperties(TableColumn tableColumn, Column dataSetColumn) {
    tableColumn.setHeaderValue(dataSetColumn.getCaption());
    tableColumn.setIdentifier(dataSetColumn.getColumnName());

    tableColumn.setPreferredWidth(getDefaultTableColumnWidth(dataSetColumn));
    tableColumn.setWidth(tableColumn.getPreferredWidth());

    if (dataSetColumn.getItemEditor() != null &&
        dataSetColumn.getItemEditor() instanceof TableCellEditor) {
      tableColumn.setCellEditor((TableCellEditor) dataSetColumn.getItemEditor());
    }
    else {
      tableColumn.setCellEditor(getDefaultCellEditor(dataSetColumn));
    }
    if (dataSetColumn.getItemPainter() != null &&
        dataSetColumn.getItemPainter() instanceof TableCellRenderer) {
      tableColumn.setCellRenderer((TableCellRenderer) dataSetColumn.getItemPainter());
    }
    else {
      tableColumn.setCellRenderer(getDefaultCellRenderer(dataSetColumn));
    }
  }

  private void inheritCustomColumnProperties(TableColumn targetColumn, TableColumn sourceColumn) {
    if (sourceColumn.getHeaderValue() != null && !rebindColumnProperties) {
      targetColumn.setHeaderValue(sourceColumn.getHeaderValue());
      targetColumn.setPreferredWidth(getMaxCaptionWidth(targetColumn.getHeaderValue(), null));
      targetColumn.setWidth(targetColumn.getPreferredWidth());
    }
    // only set the identifier if a DataSet isn't used to build the model,
    // since when it is, the identifier is assumed to be a DataSet Column's
    // columnName.
    if (dataSet == null && sourceColumn.getIdentifier() != null) {
      targetColumn.setIdentifier(sourceColumn.getIdentifier());
    }
    // 75 is the default column width hard-coded(!) in TableColumn
    if (sourceColumn.getPreferredWidth() != 75) {
      targetColumn.setPreferredWidth(sourceColumn.getPreferredWidth());
      targetColumn.setWidth(sourceColumn.getWidth());
    }
    if (sourceColumn.getMinWidth() != 15 && !rebindColumnProperties) {
      targetColumn.setMinWidth(sourceColumn.getMinWidth());
    }
    if (sourceColumn.getMaxWidth() != Integer.MAX_VALUE && !rebindColumnProperties) {
      targetColumn.setMaxWidth(sourceColumn.getMaxWidth());
    }
    if (!sourceColumn.getResizable()) {
      targetColumn.setResizable(false);
    }
    if (sourceColumn.getCellEditor() != null) {
      targetColumn.setCellEditor(sourceColumn.getCellEditor());
    }
    if (sourceColumn.getCellRenderer() != null) {
      targetColumn.setCellRenderer(sourceColumn.getCellRenderer());
    }
    if (sourceColumn.getHeaderRenderer() != null) {
      targetColumn.setHeaderRenderer(sourceColumn.getHeaderRenderer());
    }
  }

  /**
   * Overrides the default implementation to account for the
   * value of the 'editable' property.
   */
  public boolean isCellEditable(int row, int column) {
    return editable && super.isCellEditable(row, column);
  }

  // give this a name we recognize so it's easier to exclude if unnecessary for
  // deployment, and also so that we can detect (in most cases) if a user has explicitly set
  // a custom renderer for a BLOB field and we don't need to cache the converted
  // inputStream byte arrays
  class TableImageRenderer extends DefaultTableCellRenderer {
    public void TableImageRenderer() {
      setHorizontalAlignment(JLabel.CENTER);
    }
    public void setValue(Object value) {
      setIcon((Icon)value);
    }
  }

  /**
   * <p>Returns the default renderer to use for the specified <code>Column</code>.</p>
   *
   * @param dataSetColumn The specified <code>Column</code>.
   * @return The default renderer.
   */
  protected TableCellRenderer getDefaultCellRenderer(Column dataSetColumn) {
    int type = dataSetColumn.getDataType();

    if (type > Variant.NULL_TYPES && type < Variant.OBJECT && type != Variant.BOOLEAN) {
      if (type == Variant.INPUTSTREAM) {
        // INPUTSTREAMs are assumed to contain image data
        // A custom renderer should be specified on an INPUTSTREAM not containing image data
        return new TableImageRenderer();
      }
      else {
        TableFastStringRenderer renderer = new TableFastStringRenderer();

        if (dataSetColumn.getForeground() != null) {
          renderer.setDefaultForeground(dataSetColumn.getForeground());
        }
        if (dataSetColumn.getBackground() != null) {
          renderer.setDefaultBackground(dataSetColumn.getBackground());
        }
        if (dataSetColumn.getFont() != null) {
          renderer.setDefaultFont(dataSetColumn.getFont());
        }
        renderer.setDefaultAlignment(dataSetColumn.getAlignment());

        return renderer;
      }
    }

    TableCellRenderer renderer = getDefaultRenderer(DBTableModel.getJavaClass(dataSetColumn.getDataType()));
    if (renderer instanceof DefaultTableCellRenderer) {
      DefaultTableCellRenderer cellRenderer = (DefaultTableCellRenderer) renderer;
      if (dataSetColumn.getForeground() != null) {
        cellRenderer.setForeground(dataSetColumn.getForeground());
      }
      if (dataSetColumn.getBackground() != null) {
        cellRenderer.setBackground(dataSetColumn.getBackground());
      }
      if (dataSetColumn.getFont() != null) {
        cellRenderer.setFont(dataSetColumn.getFont());
      }
      cellRenderer.setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(dataSetColumn.getAlignment(), true));
      cellRenderer.setVerticalAlignment(DBUtilities.convertJBCLToSwingAlignment(dataSetColumn.getAlignment(), false));
      return cellRenderer;
    }

    return renderer;
  }

  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
    Object value = null;
    ColumnPaintListener columnPaintListener = null;
    Column dataSetColumn = null;
    Variant variantCopy = null;
    if (dbTableModel != null) {
      try {
        dataSetColumn = dbTableModel.getColumn(convertColumnIndexToModel(column));
        dataSet.getDisplayVariant(dataSetColumn.getOrdinal(), row, (Variant) variantValue);

        if (renderer instanceof TableFastStringRenderer) {
          value = dataSetColumn.format(variantValue);
          variantCopy = (Variant) variantValue.clone();
          columnPaintListener = dataSetColumn.getColumnPaintListener();
        }
        else if (dataSetColumn.getDataType() == Variant.INPUTSTREAM &&
                 renderer instanceof TableImageRenderer) {
          InputStream inputStream = variantValue.getInputStream();
          inputStream.reset();
          if (DBUtilities.isBMPFile(inputStream) ||
              DBUtilities.isGIForJPGFile(inputStream)) {
            if (imageCache == null) {
              imageCache = new LRUCache();
            }
            if ((value = imageCache.get(inputStream)) == null) {
              if (DBUtilities.isGIForJPGFile(inputStream)) {
                byte [] bytes = DBUtilities.getByteArrayFromStream(inputStream);
                if (bytes == null) {
                  value = new TextIcon(Res._UnsupImg);     
                }
                else {
                  value = new ImageIcon(bytes);
                }
              }
              else {
                Image bmpImage = DBUtilities.makeBMPImage(inputStream);
                if (bmpImage != null) {
                  value = new ImageIcon(bmpImage);
                }
                else {
                  value = new TextIcon(Res._UnsupImg);     
                }
              }
              if (value != null) {
                imageCache.put(inputStream, value);
              }
            }
          }
          else {
            if (variantValue.isNull()) {
              value = new TextIcon(" ");  
            }
            else {
              value = new TextIcon(Res._UnsupImg);     
            }
          }
        }
        else {
          value = getValueAt(row, column);
        }
      }
      // catch DataSetException or IOException
      catch (Exception e) {
        DBExceptionHandler.handleException(dataSet, this, e);
      }
    }
    else {
      value = getValueAt(row, column);
    }
    boolean rowIsAnchor = (selectionModel.getAnchorSelectionIndex() == row);
    boolean colIsAnchor =
      (columnModel.getSelectionModel().getAnchorSelectionIndex() == column);
    boolean hasFocus = (rowIsAnchor && colIsAnchor) && hasFocus();
    boolean isSelected = hasFocus || isCellSelected(row, column);

    Component component = renderer.getTableCellRendererComponent(this, value,
                                                                 isSelected, hasFocus,
                                                                 row, column);
    if (dbTableModel != null &&
        columnPaintListener != null && component instanceof CustomPaintSite) {
      Color foreground = component.getForeground();
      Color background = component.getBackground();
      columnPaintListener.painting(dataSet, dataSetColumn, row, variantValue, (CustomPaintSite) component);
      if (variantCopy != null && !variantCopy.equals(variantValue) &&
          component instanceof TableFastStringRenderer) {
        try {
          ((TableFastStringRenderer) component).setValue(dataSetColumn.format(variantValue));
        }
        catch (Exception e) {
          DBExceptionHandler.handleException(dataSet, this, e);
        }
      }
      if (isSelected) {
        component.setForeground(foreground);
        component.setBackground(background);
      }
    }

    return component;
  }


  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {
// disabled in JDK 1.2, 1.2.2, and 1.3 since the keyboard can't be used to navigate popup menus
//    if (e.getKeyCode() == KeyEvent.VK_F10 && e.isShiftDown() &&
//        dataSet != null && popupEnabled && !isEditing()) {
//      Rectangle cellRect = getCellRect(getSelectedRow(), getSelectedColumn(), true);
//      createPopupMenu().show(this, cellRect.x + cellRect.width, cellRect.y);
//    }
  }
  public void keyTyped(KeyEvent e) {
    if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
      e.consume();
      if (isEditing()) {
        removeEditor();
      }
    }
  }

  private class DBTableCellComboBoxWrapper extends DBTableCellEditorWrapper {
    private JdbComboBox comboBox;
    public DBTableCellComboBoxWrapper(JdbComboBox comboBox) {
      super(new DefaultCellEditor(comboBox));
      this.comboBox = comboBox;
    }
    public void release() {
      if (comboBox != null) {
        comboBox.setDataSet(null);
        comboBox = null;
      }
    }
  }

  private class DBTableCellEditorWrapper implements TableCellEditor {
    private TableCellEditor tableCellEditor;
    private Component editorComponent;
    private int lastRow = -1;
    private int row;
    private int column;

    public DBTableCellEditorWrapper(TableCellEditor tableCellEditor) {
      this.tableCellEditor = tableCellEditor;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
      if (tableCellEditor != null) {
        editorComponent = tableCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        this.row = row;
        this.column = column;
        return editorComponent;
      }
      return null;
    }

    public Object getCellEditorValue() {
      if (tableCellEditor != null) {
        return tableCellEditor.getCellEditorValue();
      }
      return null;
    }

    public boolean isCellEditable(EventObject anEvent) {
      if (anEvent instanceof MouseEvent) {
        Point point = ((MouseEvent) anEvent).getPoint();
        if (((MouseEvent) anEvent).getClickCount() > 1) {
          return tableCellEditor.isCellEditable(anEvent);
        }
      }
      else if (anEvent == null || anEvent instanceof KeyEvent) {
        if (tableCellEditor.isCellEditable(anEvent)) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              editorComponent.requestFocus();
            }
          });
          return true;
        }
      }
      return false;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
      return tableCellEditor.shouldSelectCell(anEvent);
    }

    public boolean stopCellEditing() {
      if (!isCellValid(tableCellEditor) || !tableCellEditor.stopCellEditing()) {
        return false;
      }
      // moved to a new row, post DataSet row
      if (lastRow != -1 && lastRow != row && dataSet != null) {
        try {
          dataSet.goToRow(row);
          lastRow = row;
        }
        catch (DataSetException ex) {
          DBExceptionHandler.handleException(dataSet, ex);
          return false;
        }
      }
      return true;
    }

    public void cancelCellEditing() {
      tableCellEditor.cancelCellEditing();
    }

    public void addCellEditorListener(CellEditorListener l) {
      tableCellEditor.addCellEditorListener(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
      tableCellEditor.removeCellEditorListener(l);
    }

  }

  private void savePickListWrapper(DBTableCellComboBoxWrapper wrapper) {
    if (pickListWrappers != null && !pickListWrappers.contains(wrapper)) {
      pickListWrappers.add(wrapper);
    }
  }

  /**
   * This is a hack to ensure that unused picklist comboboxes are correctly
   * released.
   */
  private void releaseWrappers() {
    if (pickListWrappers != null) {
      List cellEditors = new ArrayList();
      for (int i = 0; i < getColumnCount(); i++) {
        TableColumn tableColumn = getColumnModel().getColumn(i);
        TableCellEditor cellEditor = tableColumn.getCellEditor();
        if (cellEditor != null && !cellEditors.contains(cellEditor)) {
          cellEditors.add(cellEditor);
        }
      }
      for (int i = pickListWrappers.size() - 1; i >= 0; i--) {
        DBTableCellComboBoxWrapper wrapper =
            (DBTableCellComboBoxWrapper) pickListWrappers.get(i);
        if (!cellEditors.contains(wrapper)) {
          wrapper.release();
          pickListWrappers.remove(i);
        }
      }
    }
  }

  /**
   * <p>Returns the default editor to use for the specified <code>Column</code>.</p>
   *
   * @param dataSetColumn The specified <code>Column</code>.
   * @return The default editor.
   */
  protected TableCellEditor getDefaultCellEditor(Column dataSetColumn) {
    int type = dataSetColumn.getDataType();

    if (type > Variant.NULL_TYPES && type < Variant.OBJECT && type != Variant.BOOLEAN) {
      // The default editor for Variant.INPUTSTREAM types is the image editor.
      // A custom TableCellEditor should be explicitly assigned to a column
      // with a custom data type.
      if (type == Variant.INPUTSTREAM) {
        return new TableImageEditor();
      }
      else {
        if (dataSetColumn.getPickList() != null) {
          JdbComboBox comboBox = new JdbComboBox();
          comboBox.setDataSet(dataSet);
          comboBox.setColumnName(dataSetColumn.getColumnName());
//          return new DBTableCellEditorWrapper(new DefaultCellEditor(comboBox));
          DBTableCellComboBoxWrapper wrapper =
              new DBTableCellComboBoxWrapper(comboBox);
          savePickListWrapper(wrapper);
          return wrapper;
        }
        else {
          TableMaskCellEditor editor = new TableMaskCellEditor();
          editor.setFormatter(dataSetColumn.getFormatter());
          if (dataSetColumn.getDataType() == Variant.STRING && dataSetColumn.getPrecision() != -1) {
            editor.setMaxLength(dataSetColumn.getPrecision());
          }
          editor.setDefaultForeground(dataSetColumn.getForeground() == null ? getForeground() :
                                      dataSetColumn.getForeground());
          editor.setDefaultBackground(dataSetColumn.getBackground() == null ? getBackground() :
                                      dataSetColumn.getBackground());
          editor.setDefaultFont(dataSetColumn.getFont() == null ? getFont() :
                                dataSetColumn.getFont());
          editor.setDefaultAlignment(dataSetColumn.getAlignment());

          editor.setEditMasker(dataSetColumn.getEditMasker());
          editor.setVariantType(dataSetColumn.getDataType());

          return editor;
        }
      }
    }

    TableCellEditor editor = getDefaultEditor(DBTableModel.getJavaClass(dataSetColumn.getDataType()));
    Component editorComponent;
    if (editor instanceof DefaultCellEditor &&
        (editorComponent = ((DefaultCellEditor) editor).getComponent()) != null) {
      if (editorComponent instanceof JComponent) {
        JComponent cellEditor = (JComponent) editorComponent;
        if (dataSetColumn.getForeground() != null) {
          cellEditor.setForeground(dataSetColumn.getForeground());
        }
        if (dataSetColumn.getBackground() != null) {
          cellEditor.setBackground(dataSetColumn.getBackground());
        }
        if (dataSetColumn.getFont() != null) {
          cellEditor.setFont(dataSetColumn.getFont());
        }
      }
      if (editorComponent instanceof JTextField) {
        JTextField cellEditor = (JTextField) editorComponent;
        cellEditor.setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(dataSetColumn.getAlignment(), true));
      }
      if (editorComponent instanceof JCheckBox) {
        ((DefaultCellEditor) editor).setClickCountToStart(2);
      }
    }

    return new DBTableCellEditorWrapper(editor);
  }

  public Component prepareEditor(TableCellEditor editor, int row, int column) {
    Object value = null;
    ColumnPaintListener columnPaintListener = null;
    Column dataSetColumn = null;
    if (dbTableModel != null) {
      try {
        dataSetColumn = dbTableModel.getColumn(convertColumnIndexToModel(column));
        dataSet.getDisplayVariant(dataSetColumn.getOrdinal(), row, (Variant) variantValue);
//        if (editor instanceof DBCellEditor) {
        if (editor instanceof TableMaskCellEditor) {
          // no explicit edit mask, automatically expand year to 4 digits for a date
          if (dataSetColumn.getEditMask() == null && dataSetColumn.getFormatter().getFormatObj() instanceof java.text.DateFormat) {
            String oldPattern = dataSetColumn.getFormatter().getPattern();
            String widePattern = null;
            int yearMaskIndex = -1;
            if ((yearMaskIndex = DBUtilities.yearMaskPos(oldPattern)) != -1) {
              int yearMaskCount = 1;
              int patternLength = oldPattern.length();
              while ((yearMaskIndex + yearMaskCount) < patternLength && oldPattern.charAt(yearMaskIndex + yearMaskCount) == 'y') { 
                yearMaskCount++;
              }
              if (yearMaskCount < 4) {
                widePattern = oldPattern.substring(0, yearMaskIndex) + "yyyy" +     
                  ((yearMaskIndex + yearMaskCount < patternLength) ? oldPattern.substring(yearMaskIndex + yearMaskCount) : ""); 
              }
            }
            if (widePattern != null) {
              dataSetColumn.getFormatter().setPattern(widePattern);
            }
            value = dataSetColumn.format(variantValue);
            if (widePattern != null) {
              dataSetColumn.getFormatter().setPattern(oldPattern);
            }
          }
          else {
            value = variantValue.getAsObject();
          }
          columnPaintListener = dataSetColumn.getColumnPaintListener();
        }
        else if (dataSetColumn.getDataType() == Variant.INPUTSTREAM &&
                 (editor instanceof TableImageEditor || editor instanceof TableImageReadOnlyEditor)) {
          InputStream inputStream = variantValue.getInputStream();
          inputStream.reset();
          if (DBUtilities.isBMPFile(inputStream) ||
              DBUtilities.isGIForJPGFile(inputStream)) {
            if (imageCache == null) {
              imageCache = new LRUCache();
            }
            if ((value = imageCache.get(inputStream)) == null) {
              if (DBUtilities.isGIForJPGFile(inputStream)) {
                byte [] bytes = DBUtilities.getByteArrayFromStream(inputStream);
                if (bytes == null) {
                  value = new TextIcon(Res._UnsupImg);     
                }
                else {
                  value = new ImageIcon(bytes);
                }
              }
              else {
                Image bmpImage = DBUtilities.makeBMPImage(inputStream);
                if (bmpImage != null) {
                  value = new ImageIcon(bmpImage);
                }
                else {
                  value = new TextIcon(Res._UnsupImg);     
                }
              }
              if (value != null) {
                imageCache.put(inputStream, value);
              }
            }
          }
          else {
            value = new TextIcon(Res._UnsupImg);     
          }
        }
        else {
          value = getValueAt(row, column);
        }
      }
      // catch DataSetException or IOException
      catch (Exception e) {
        DBExceptionHandler.handleException(dataSet, this, e);
      }
    }
    else {
      value = getValueAt(row, column);
    }
    boolean isSelected = isCellSelected(row, column);
    Component comp = editor.getTableCellEditorComponent(this, value, isSelected,
                                                        row, column);
    if (comp instanceof JComponent) {
      ((JComponent)comp).setNextFocusableComponent(this);
    }

    if (columnPaintListener != null && comp instanceof CustomPaintSite) {
      columnPaintListener.editing(dataSet, dataSetColumn, (CustomPaintSite) comp);
    }

    return comp;
  }

  // font can be null to indicate the table's font (as opposed to the
  // font for a particular column
  /**
   * <p>Returns the width of the string size based on the font.
   * Note that the font can be a <code>null</code> value to
   * indicate the table's font, as opposed to the font for a column.</p>
   *
   * @param string The string to find the width of.
   * @param font The font used for the string.
   * @return The string width, as an integer.
   */
  protected int getStringWidth(String string, Font font) {
    Graphics g;
    // use a default width just in case there's a problem finding the correct
    // width for a character in the current font
    int emWidth = 9;
    int width;

    if (font == null) { // try to get table's font
      font = getFont();
    }

    if (font == null || (g = getGraphics()) == null) {
      width = emWidth * string.length();
    }
    else {
      FontMetrics fontMetrics = g.getFontMetrics(font);
      width = fontMetrics.stringWidth(string);
      g.dispose();
    }
    return width;
  }

  /**
   * <p>Returns the maximum caption width based on the font and the
   * column header value. This method is called by
   * <code>createDefaultColumnsFromModel()</code> if the table
   * model is <code>null</code> and <code>smartColumnWidths</code>
   * is <code>true</code>.</p>
   *
   * @param columnHeaderValue The column header value.
   * @param font The font.
   * @return The maximum caption width.
   */
  protected int getMaxCaptionWidth(Object columnHeaderValue, Font font) {
    int maxWidth = 0;
    int width;

    if (columnHeaderValue instanceof String []) {
      String [] strings = (String []) columnHeaderValue;
      for (int x = 0; x < strings.length; x++) {
        width = getStringWidth(strings[x] + "  ", font);   
        if (width > maxWidth) {
          maxWidth = width;
        }
      }
    }
    else if (columnHeaderValue != null) {
      int offset = 0;
      String caption = columnHeaderValue.toString();
      while ((offset = caption.indexOf('\n')) != -1) {
        width = getStringWidth(caption.substring(0, offset+1) + "  ", font);  
        if (width > maxWidth) {
          maxWidth = width;
        }
        caption = caption.substring(offset+1);
      }
      if ((width = getStringWidth(caption + "  ", font)) > maxWidth) {  
        maxWidth = width;
      }
    }
    return maxWidth;
  }

  /**
   * <p>Returns a default pixel width for a
   * <code>TableColumn</code> based on a <code>DataSet</code>
   * <code>Column</code>'s type. This method is called by
   * <code>createDefaultColumnsFromModel()</code> as it attempts to
   * inherit <code>TableColumn</code> properties from
   * <code>DataSet</code> <code>Column</code> properties.</p>
   *
   * @param dataSetColumn The <code>TableColumn</code> based on a <code>DataSet</code> <code>Column</code>'s type.
   * @return The default pixel width.
   */
  protected int getDefaultTableColumnWidth(Column dataSetColumn) {

    PickListDescriptor pickList = dataSetColumn.getPickList();
    if (pickList != null && pickList.getPickListDataSet() != null) {
      String lookupDisplayColumnName = dataSetColumn.getPickList().
          getLookupDisplayColumn();
      if (lookupDisplayColumnName != null) {
        Column lookupDisplayColumn = dataSetColumn.getPickList().
            getPickListDataSet().getColumn(lookupDisplayColumnName);
        if (lookupDisplayColumn != null) {
          return getDefaultTableColumnWidth(lookupDisplayColumn);
        }
      }
    }
    int emWidth = getStringWidth("M", dataSetColumn.getFont()); 

    // if a width has been explictly set on the column, use it
    if (dataSetColumn.getWidth() != 15 || !smartColumnWidths) {
      return dataSetColumn.getWidth() * emWidth;
    }

    // if the font width has been determined from query metadata, set the width
    // to 3/4 of the actual width
    if (dataSetColumn.getDataType() == Variant.STRING && dataSetColumn.getPrecision() != -1) {
      return Math.min((dataSetColumn.getPrecision() * 3) / 4, 40) * emWidth;
    }

    int captionWidth = getMaxCaptionWidth(dataSetColumn.getCaption(), dataSetColumn.getFont());
    // define a default data width, taking into account the current font
    int dataWidth = 10 * emWidth;
    // show the caption if it's wider than the data
    if (captionWidth > dataWidth) {
      return captionWidth;
    }
    // if the default data width is greater than the default JTable width, use it
    if (dataWidth > 75) {
      return dataWidth;
    }
    else {
      // use JTable's default width
      return 75;
    }
  }

 /**
  * <p>When a user tries to save a table cell value, the table asks
  * whether to stop editing the cell.  At that point,
  * <code>JdbTable</code> checks to see if the cell editor value is
  * valid. If not, it throws an exception to display to the user.
  * The reason the cell editor is passed as an argument is because
  * it knows the currently edited value, which may still be incomplete.</p>
  *
  * @param cellEditor The cell editor.
  * @return If <code>true</code>, the cell editor is valid.
  */
  protected boolean isCellValid(TableCellEditor cellEditor) {
    if (dataSet == null) {
      return true;
    }

    if ((cellEditor instanceof TableMaskCellEditor && !((TableMaskCellEditor) cellEditor).isValidValue()) ||
        !dbTableModel.isValidValue(cellEditor.getCellEditorValue(), getEditingRow(), getEditingColumn())) {
      DBExceptionHandler.handleException(dataSet, this,
                                         new ValidationException(ValidationException.INVALID_FORMAT,
                                                                 Res._PostRowFailed,     
                                                                 dbTableModel.getColumn(convertColumnIndexToModel(getEditingColumn()))));
      return false;
    }
    return true;
  }

  /**
   * Returns JTableHeader which can't accept keyboard focus.
   */
  protected JTableHeader createDefaultTableHeader() {
    return new JTableHeader(getColumnModel()) {
      public boolean isFocusTraversable() {
        return false;
      }
      public boolean isRequestFocusEnabled() {
        return false;
      }
    };
  }

  /**
   * <p>Creates the default data model at <code>JdbTable</code>
   * instantiation time and overrides <code>JTable</code>'s
   * implementation to create a <code>DBTableModel</code> instead
   * of a <code>DefaultTableModel</code>.</p>
   */
  protected TableModel createDefaultDataModel() {
    return new DBTableModel();
  }

  /**
   * <p>Creates <code>JdbTable</code>'s default right-click popup menu.</p>
   * @return The popup menu.
   */
  protected JPopupMenu createPopupMenu() {

    JPopupMenu menu = new JPopupMenu();
    menu.setDefaultLightWeightPopupEnabled(false);

    JMenuItem menuItem;

    JMenu sortMenu = new JMenu(Res._SortBy);     

    if (dataSet.isEditing()) {
      sortMenu.setEnabled(false);
    }

    JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(Res._Unsorted);     
    checkItem.addActionListener(this);
    checkItem.setActionCommand("nosort");  
    SortDescriptor sortDescriptor = dataSet.getSort();
    String columnSortKey = "";
    if (sortDescriptor == null) {
      checkItem.setState(true);
    }
    else {
      String [] sortKeys;
      if ((sortKeys = sortDescriptor.getKeys()) != null && sortKeys.length > 0) {
        columnSortKey = sortKeys[0];
      }
    }
    sortMenu.add(checkItem);
    String columnName;
    for (int colNo = 0, maxCols = getColumnModel().getColumnCount(); colNo < maxCols; colNo++) {
      Column dataSetColumn = ((DBTableModel) getModel()).getColumn(convertColumnIndexToModel(colNo));
      if (dataSetColumn == null || !dataSet.isSortable(dataSetColumn)) {
        continue;
      }
      TableColumn column = getColumnModel().getColumn(colNo);
      if (column.getHeaderValue() instanceof String []) {
        checkItem = new JCheckBoxMenuItem(getConcatenatedString((String []) column.getHeaderValue()));
      }
      else {
        checkItem = new JCheckBoxMenuItem(column.getHeaderValue().toString().replace('\n', ' '));
      }
      checkItem.addActionListener(this);
      columnName = getColumnModel().getColumn(colNo).getIdentifier().toString();
      checkItem.setActionCommand(columnName);
      if (columnSortKey.equals(columnName)) {
        checkItem.setState(true);
      }
      else {
        checkItem.setState(false);
      }
      sortMenu.add(checkItem);
    }

    menu.add(sortMenu);

    menu.addSeparator();

    boolean readOnly = false;
    if (dataSet instanceof StorageDataSet) {
      readOnly = ((StorageDataSet) dataSet).isReadOnly();
    }
    menuItem = new JMenuItem(Res._Post);     
    menuItem.setEnabled(dataSet.isEditing());
    menuItem.addActionListener(this);
    menuItem.setActionCommand("post");  
    menu.add(menuItem);

    menuItem = new JMenuItem(Res._Cancel);     
    menuItem.setEnabled(dataSet.isEditing());
    menuItem.addActionListener(this);
    menuItem.setActionCommand("cancel");  
    menu.add(menuItem);

    menuItem = new JMenuItem(Res._Insert);     
    menuItem.setEnabled(dataSet.isEnableInsert() && isEditable() && !dataSet.isEditingNewRow() && dataSet.isEditable() && !readOnly);
    menuItem.addActionListener(this);
    menuItem.setActionCommand("insert");  
    menu.add(menuItem);

    menuItem = new JMenuItem(Res._Delete);     
    try {
      menuItem.setEnabled(dataSet.isEnableDelete() && isEditable() && !dataSet.isEmpty() && dataSet.isEditable() && !readOnly);
      menuItem.addActionListener(this);
      menuItem.setActionCommand("delete");  
      menu.add(menuItem);
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, this, e);
    }

    return menu;

  }

  private String getConcatenatedString(String [] strings) {
    StringBuffer caption = new StringBuffer();
    for (int i = 0; i < strings.length; i++) {
      caption.append(strings[i]).append(" ");
    }
    caption.deleteCharAt(caption.length() - 1);
    return caption.toString();
  }

  /**
   * <p>Sets the foreground color of the currently focused, editable cell.</p>
   *
   * @param focusedCellForeground The foreground color.
   * @see #getEditableFocusedCellForeground
   */
  public void setEditableFocusedCellForeground(Color focusedCellForeground) {
    this.focusedCellForeground = focusedCellForeground;
  }

  /**
   * <p>Returns the foreground color of the currently focused, editable cell.</p>
   *
   * @return The foreground color.
   * @see #setEditableFocusedCellForeground
   */
  public Color getEditableFocusedCellForeground() {
    return focusedCellForeground;
  }

  /**
   * <p>Sets the background color of the currently focused, editable cell.</p>
   *
   * @param focusedCellBackground The background color.
   * @see #getEditableFocusedCellBackground
   */
  public void setEditableFocusedCellBackground(Color focusedCellBackground) {
    this.focusedCellBackground = focusedCellBackground;
  }

  /**
   * <p>Returns the background color of the currently focused, editable cell.</p>
   *
   * @return The background color.
   * @see #setEditableFocusedCellBackground
   */
  public Color getEditableFocusedCellBackground() {
    return focusedCellBackground;
  }

  /**
   * Initializes table properties to their default values.
   */
  protected void initializeLocalVars() {
    setOpaque(true);
    createDefaultRenderers();
    createDefaultEditors();

    setTableHeader(createDefaultTableHeader());

    setShowGrid(true);
    setAutoResizeMode(AUTO_RESIZE_OFF);
    setRowHeight(21);  // will actually get overwritten by more accurate value, if possible, in addNotify()
    setRowMargin(1);
//    setRowSelectionAllowed(false);
//    setColumnSelectionAllowed(false);
//    setCellSelectionEnabled(true);
    setCellSelectionEnabled(true);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setCellEditor(null);
    setEditingColumn(-1);
    setEditingRow(-1);
    setPreferredScrollableViewportSize(new Dimension(450,400));

    // I'm registered to do tool tips so we can draw tips for the renderers
    ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    toolTipManager.registerComponent(this);

    setAutoscrolls(true);

    addPropertyChangeListener(this);
  }

  public boolean isCellSelected(int row, int column) {
    return super.isCellSelected(row, column);
  }

  public void setTableHeader(JTableHeader tableHeader) {
    if (getTableHeader() != null) {
      getTableHeader().removeMouseListener(this);
    }
    if (tableHeader != null) {
      tableHeader.addMouseListener(this);
    }
    super.setTableHeader(tableHeader);
  }

  //
  // javax.beans.PropertyChangeListener
  //
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("columnModel")) {  
      Object columnModel;
      if ((columnModel = e.getOldValue()) != null) {
        ((TableColumnModel) columnModel).getSelectionModel().removeListSelectionListener(this);
      }
      if ((columnModel = e.getNewValue()) != null) {
        ((TableColumnModel) columnModel).getSelectionModel().addListSelectionListener(this);
      }
    }
  }

  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
//      Graphics g;
      // try to set the default row height based on the actual font height if the
      // row height has not been explicitly set
//      if (getRowHeight() == 16 && (g = getGraphics())!= null) {
//        Font f = getFont();
//        FontMetrics fm = g.getFontMetrics(f);
//        setRowHeight(fm.getHeight() + 5);
//      }
      if (pendingDataSet != null) {
        bindDataSet();
      }
      else {   // using a custom model
        if (getModel() != null) {
          createDefaultColumnsFromModel();
        }
      }
    }
  }

  private void bindDataSet() {
    if (dbTableModel == null) {
      dbTableModel = new DBTableModel();
    }
    if (pendingDataSet != null) {
      try {
        pendingDataSet.open();
      }
      catch (DataSetException ex) {
        DBExceptionHandler.handleException(pendingDataSet, this, ex);
        dbTableModel.setDataSet(null);
        setModel(dbTableModel);
        return;
      }
    }
    dbTableModel.setDataSet(pendingDataSet);
    if (getModel() != dbTableModel) {
      setModel(dbTableModel);
    }
    if (pendingDataSet != null) {
      int rowNo = pendingDataSet.getRow();
      if (rowNo > 0) {
        setRowSelectionInterval(rowNo, rowNo);
      }
    }
  }

  public void removeNotify() {
    super.removeNotify();
    removeHeaders();
//    addNotifyCalled = false;
  }

  private void removeHeaders() {
    if (isDisplayable()) {
      Container viewport = getParent();
      if (viewport instanceof JViewport) {
        Container parent = viewport.getParent();
        if (parent instanceof JScrollPane) {
          JScrollPane scrollPane = (JScrollPane) parent;
          if (scrollPane.getColumnHeader().getView() instanceof JTableHeader) {
            scrollPane.setColumnHeaderView(null);
          }
          if (scrollPane.getRowHeader().getView() instanceof TableRowHeader) {
            scrollPane.setRowHeaderView(null);
          }
        }
      }
    }
  }

  public void setVisible(boolean visible) {
    super.setVisible(visible);
    setRowHeaderVisible(visible);
    setColumnHeaderVisible(visible);
  }

  // ensures we wait until addNotify has been called before trying to
  // build the column model.  Used by properties to avoid premature
  // opening of the dataset.
  private void updateColumnModel() {
    if (addNotifyCalled) {
      createDefaultColumnsFromModel();
    }
  }

  // ListSelectionListener Implementation
  // augments superclass' implementation
  public void valueChanged(ListSelectionEvent e) {
    super.valueChanged(e);
    if (e.getSource() == getColumnModel().getSelectionModel()) {
      int tableColumn = getSelectedColumn();
      if (!e.getValueIsAdjusting() && dataSet != null &&
          tableColumn > -1 && tableColumn < getColumnModel().getColumnCount()) {
        int modelColumn = convertColumnIndexToModel(tableColumn);
        TableModel tableModel = getModel();

        if (modelColumn >= 0 && modelColumn < tableModel.getColumnCount() &&
            dataSet != null && tableModel instanceof DBTableModel) {
          Column column = ((DBTableModel) tableModel).getColumn(modelColumn);
          dataSet.setLastColumnVisited(column.getColumnName());
        }

      }
    }
    else {
      int selectedRow = getSelectedRow();
      if (selectedRow >= getRowCount()) { // This test is a fix for 149157.  Charles
        selectedRow = getRowCount() - 1;
      }
      if (!ignoreNavigation && !e.getValueIsAdjusting() && dataSet != null && dataSet.isOpen() && selectedRow != dataSet.getRow()) {
        // If a row is being edited, then we first have to see if we can post the
        // row before navigating to a different row.  Note that this may cause the row to move
        // to a different row position.
        if (dataSet.isEditing()) {
          try {
            dataSet.post();
          }
          catch (DataSetException ex) {
            ignoreNavigation = true;
            // dataSet.getRow() should still be a valid row, so no need to check for valid range here
            setRowSelectionInterval(dataSet.getRow(), dataSet.getRow());
            ignoreNavigation = false;
            DBExceptionHandler.handleException(dataSet, ex);
            return;
          }
        }
        if (selectedRow >= getRowCount()) { // This test is a fix for 170839.  Charles
          // The dataSet.post(), above, may have caused the row count to decrement.
          selectedRow = getRowCount() - 1;
        }
        if (dataSet.getRow() != selectedRow) {
          ignoreNavigation = true;
          try {
            dataSet.goToRow(selectedRow);
            if (selectedRow >= 0) {
              setRowSelectionInterval(selectedRow, selectedRow);
            }
          }
          catch (DataSetException ex) {
            // dataSet.getRow() should still be a valid row, so no need to check for valid range here
            setRowSelectionInterval(dataSet.getRow(), dataSet.getRow());
            DBExceptionHandler.handleException(dataSet, ex);
          }
          finally {
            ignoreNavigation = false;
          }
        }
      }
    }
  }

  public void tableChanged(TableModelEvent e) {
    boolean insOrDel = e.getType() == TableModelEvent.INSERT ||
        e.getType() == TableModelEvent.DELETE;
    if (insOrDel) {
      ignoreNavigation = true;
    }
    try {
      super.tableChanged(e);
      if (insOrDel) {
        if (getRowCount() > e.getLastRow()) {
          setRowSelectionInterval(e.getLastRow(), e.getLastRow());
        }
      }
    }
    finally {
      if (insOrDel) {
        ignoreNavigation = false;
      }
    }
  }


  // NavigationListener Implementation (DataSet)

  public void navigated(NavigationEvent e) {
    if (!dataSetEventsEnabled) {
      return;
    }
    int row = dataSet.getRow();
    if (!ignoreNavigation) {
      ignoreNavigation = true;
//      Rectangle rect = getCellRect(getSelectedRow(), getSelectedColumn(), true);
//      repaint(rect.x, rect.y, rect.width, rect.height);
      if (getRowCount() > row) {
        setRowSelectionInterval(row, row);
      }
      Rectangle rect = getCellRect(getSelectedRow(), getSelectedColumn(), true);
//      repaint(rect.x, rect.y, rect.width, rect.height);
      scrollRectToVisible(rect);
      repaint();
      ignoreNavigation = false;
    }

    if (getRowHeader() != null) {
      getRowHeader().revalidate();
      getRowHeader().repaint();
    }

  }

  // AccessListener Implementation (DataSet)

  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.OPEN) {
//      if (event.getReason() == AccessEvent.DATA_CHANGE) {
        dataSetEventsEnabled = true;
//      }
      if (autoSelection && !rebindColumnProperties) {
        int rowNo = dataSet.getRow();
        if (rowNo >= 0 && rowNo < dataSet.getRowCount()) { 
          setRowSelectionInterval(rowNo, rowNo);
        }
        else {
          if ( getRowCount() > 0 ) {
            setRowSelectionInterval(0, 0);
          }
        }
        if (getColumnCount() > 0) {
          setColumnSelectionInterval(0, 0);
        }
      }
      if (rebindColumnProperties) {
        createDefaultColumnsFromModel();
        rebindColumnProperties = false;
      }
      repaint();
      if (getRowHeader() != null) {
        getRowHeader().revalidate();
        getRowHeader().repaint();
      }
    }
    else {
//      getSelectionModel().clearSelection();
//      getColumnModel().getSelectionModel().clearSelection();
      if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
        rebindColumnProperties = true;
      }
      if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
        rebindColumnProperties = true;
//        dataSetEventsEnabled = false;
      }
    }
  }

  //
  // DataChangeListener interface implemenation
  //
  public void dataChanged(final DataChangeEvent e) {
    if (dataSetEventsEnabled) {
     if (isEditing()) {
       int editingRow = getEditingRow();
       int affectedRow = e.getRowAffected();
       if (affectedRow == editingRow &&
           e.getID() == DataChangeEvent.ROW_DELETED) {
         removeEditor();
       }
     }
     else if (e.getID() == DataChangeEvent.ROW_ADDED) {
       final int rowAffected = e.getRowAffected();
       DBUtilities.invokeOnSwingThread(new Runnable() {
         public void run() {
           ensureRowIsVisible(rowAffected);
         }
       });
      }
    }
  }

  // posts any pending edits, happens when user presses toolbar post button,
  // or navigates to a different row
  public void postRow(DataChangeEvent e) throws Exception {
    if (cellEditor instanceof TableMaskCellEditor) {
      if (!((TableMaskCellEditor) cellEditor).isValidValue()) {
        try {
          throw new ValidationException(ValidationException.INVALID_FORMAT, Res._PostRowFailed, dbTableModel.getColumn(convertColumnIndexToModel(getEditingColumn())));     
        }
        catch (ValidationException ex) {
          //        DBExceptionHandler.handleException(dataSet, this, ex);
          throw ex;
        }
      }
//      else {
//        cellEditor.stopCellEditing();
//      }
    }
    if (cellEditor != null) {
      cellEditor.stopCellEditing();
    }
  }

  //
  // java.awt.event.MouseListener
  //
  public void mouseClicked(MouseEvent e) {
    int tableColumn;
    if (e.getSource() == getTableHeader() && enableSort &&
        (tableColumn = getTableHeader().columnAtPoint(e.getPoint())) != -1) {
      int modelColumn = convertColumnIndexToModel(tableColumn);
      TableModel tableModel = getModel();
      if (modelColumn >= 0 && modelColumn < tableModel.getColumnCount() &&
          dataSet != null && tableModel instanceof DBTableModel) {
        Frame frame = DBUtilities.getFrame(this);
        try {
          Column column = ((DBTableModel) tableModel).getColumn(modelColumn);
          if (dataSet.isSortable(column) && !isEditing() && !dataSet.isEditing()) {
            boolean resetAutoSelection = autoSelection;
            autoSelection = false;
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            dataSet.toggleViewOrder(column.getColumnName());
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            autoSelection = resetAutoSelection;
          }
        }
        catch (Exception x) {
          DBExceptionHandler.handleException(dataSet, this, x);
          frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      }
    }
  }
  public void mousePressed(MouseEvent e) {
    if (e.getSource() == this && e.isPopupTrigger() &&
        getModel() instanceof DBTableModel && popupEnabled && !isEditing()) {
      createPopupMenu().show(this, e.getX(), e.getY());
    }
  }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {
    if (e.getSource() == this && e.isPopupTrigger() &&
        getModel() instanceof DBTableModel && popupEnabled && !isEditing()) {
      createPopupMenu().show(this, e.getX(), e.getY());
    }
  }

  //
  // java.awt.ActionListener
  // for processing Menu events
  public void actionPerformed(ActionEvent e) {
    JMenuItem menuItem = (JMenuItem) e.getSource();
    String action = menuItem.getActionCommand();
    try {
      if (action.equals("post")) {  
        dataSet.post();
      }
      else if (action.equals("cancel")) {  
        dataSet.cancel();
      }
      else if (action.equals("insert")) {  
        if (isEditable()) {
          dataSet.insertRow(true);
        }
      }
      else if (action.equals("delete")) {  
        if (isEditable()) {
          dataSet.deleteRow();
        }
      }
      else if (action.equals("nosort")) {  
        dataSet.setSort(null);
      }
      else { // a column to sort
        boolean resetAutoSelection = autoSelection;
        autoSelection = false;
        dataSet.toggleViewOrder(action);
        autoSelection = resetAutoSelection;
      }
    }
    catch (DataSetException ex) {
      DBExceptionHandler.handleException(dataSet, this, ex);
    }
  }

  public void focusGained(FocusEvent e) {
    DBUtilities.updateCurrentDataSet(this, dataSet);
  }
  public void focusLost(FocusEvent e) {
  }

  /** DataSet used to build TableModel */
  private DataSet dataSet;

  private boolean addNotifyCalled;

  /** JdbRowHeader used to display table's row header */
  private TableRowHeader rowHeader;

  /** Whether or not to display the row header */
  private boolean rowHeaderVisible              = true;

  /** Whether or not to display the column header */
  private boolean columnHeaderVisible           = true;

  /** Whether or not to size column widths by column data type */
  private boolean smartColumnWidths             = true;

  /** Ordered array of dataModel indices specifying hidden columns */
  private int [] hiddenColumns;

  /** Ordered array of TableColumns with customized properties */
//  private TableColumn [] customColumns;

  /** Redundant reference to super's columnModel of type JdbTableColumnModel to avoid casting */
//  protected JdbTableColumnModel dbColumnModel;

  /** Redundant reference to super's tableModel of type DBTableModel to avoid casting */
  private DBTableModel dbTableModel;

  private boolean ignoreNavigation;

  private boolean autoSelection = true;

  private boolean rebindColumnProperties;


  /** whether or not to display the popup menu */
  private boolean popupEnabled = true;

  /** whether or not to allow header click sorting */
  private boolean enableSort = true;

  private Variant variantValue = new Variant();

  /** whether or not the table is editable */
  private boolean editable = true;

  /** cache for images */
  private LRUCache imageCache;

  /** editable, focused cell foreground */
  private Color focusedCellForeground;

  /** editable, focused cell background */
  private Color focusedCellBackground;

  private CustomColumnsDescriptor customColumnDescriptor;

  private boolean applyCustomColumns;

  private DataSet oldDataSet;
  private DataSet pendingDataSet;

  // for DataSet.enableDataSetEvents() support
  private boolean dataSetEventsEnabled = true;

  private List pickListWrappers = new ArrayList();
  public void deleteError(DataSet dataSet, DataSetException ex,
      ErrorResponse response) {
  }

  public void updateError(DataSet dataSet, ReadWriteRow row,
      DataSetException ex, ErrorResponse response) {
  }

  public void addError(DataSet dataSet, ReadWriteRow row, DataSetException ex,
      ErrorResponse response) {
  }

  public void editError(DataSet dataSet, Column column, Variant value,
      DataSetException ex, ErrorResponse response) {
  }

  public void inserted(DataSet dataSet) {
  }

  public void inserting(DataSet dataSet) throws Exception {
  }

  public void modifying(DataSet dataSet) throws Exception {
  }

  public void deleted(DataSet dataSet) {
  }

  public void deleting(DataSet dataSet) throws Exception {
    if (isEditing()) {
      if (getEditingRow() == dataSet.getRow()) {
        removeEditor();
      }
    }
  }

  public void added(DataSet dataSet) {
  }

  public void adding(DataSet dataSet, ReadWriteRow newRow) throws Exception {
  }

  public void updated(DataSet dataSet) {
  }

  public void updating(DataSet dataSet, ReadWriteRow newRow,
      ReadRow oldRow) throws Exception {
  }

  public void canceling(DataSet dataSet) throws Exception {
  }

}
