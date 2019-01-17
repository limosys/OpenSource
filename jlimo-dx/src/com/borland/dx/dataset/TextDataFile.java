//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/TextDataFile.java,v 7.1 2003/01/30 21:58:29 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.dx.text.VariantFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.jb.util.LocaleUtil;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.Trace;
import com.borland.jb.io.*;

import java.io.*;
import java.sql.*;
import java.util.Locale;

/**
 *
 * The <CODE>TextDataFile</CODE> component specifies the  properties of a text file that affect its import and export, such as delimiters, field separators, and so on.  This component is used when:
<UL>
<LI>importing data stored in a text format into a {@link com.borland.dx.dataset.TableDataSet TableDataSet} component
<LI>exporting the data stored in any {@link com.borland.dx.dataset.StorageDataSet StorageDataSet} to a text file
</UL>



<P>When importing data into a <CODE>TableDataSet</CODE>, this component specifies the  properties of the text file that affect its import, such as delimiters, field separators, and so on. To further specify the formatting of the data within each field, set the
{@link com.borland.dx.dataset.Column#setExportDisplayMask exportDisplayMask} property of the {@link com.borland.dx.dataset.Column Column} component. The <CODE>exportDisplayMask</CODE> is used both when importing as well as exporting.



<P>This component is the default for the {@link com.borland.dx.dataset.StorageDataSet#setDataFile dataFile} property of the <CODE>StorageDataSet</CODE> component. By default, exported data is in a text format
as specified by this component's properties. To write the data stored in any <CODE>StorageDataSet</CODE> to a text file, instantiate a <CODE>TextDataFile</CODE> component and call one of the <CODE>TextDataFile.save(...)</CODE> methods.

<P>All properties of this component have default values. To changes these values, call the corresponding accessor methods. In addition, localized properties are stored in its associated {@link com.borland.dx.dataset.DataFileFormat DataFileFormat} object, which is set by this component's <CODE>fileFormat</CODE> property.

<!-- JDS start - remove links to database book -->
<P>For information on saving data stored in a <CODE>TableDataSet</CODE> using this component, see  {@link com.borland.dx.dataset.TableDataSet "Saving data in a TextDataFile to a JDBC data source"} in the About section of the <CODE>TableDataSet</CODE> component, or "Retrieving data from a data source" and "Importing and exporting data from a text file" in the <CITE>Database Application Developer's Guide</CITE>.
<!-- JDS end -->

<BR>
    This implementation of DataFile loads data files that have a
    delimited format.  Data values can be optionally delimited.  If the
    delimiter char property is set to 0, no delimiter will be used.
    Currently, only String data values are delimited.  The separator char
    property value is placed between each field value.
    <p>
    When the save() method is called a schema file (filename.schema) is created
    to describe the DataSet and its columns.  When the the load method is called
    the corresponding filename.schema file is opened and used if it exists.
    If a .schema file does not exist at load time, the DataSet to be loaded
    must have enough Columns that match the number and types of columns of
    data in the file to be loaded.
    <p>
    There is a DataFileFormat property that controls how characters are
    outputed.  Currently only Ascii format is supported.  In this format the
    characters 0-0xff are not encoded except for two exceptions:  1) Consecutive
    backslash chars are escaped by doubling their count.  This is not done
    for single occurance backslash characters unless they preceed a 'u'
    character or a high byte character (0x100-0xff00).  2) the delimiter
    character will be escaped with a single backslash if it occurs within
    a data value.
    <p>
    High byte data values (0x0100-0xff00) are sent out as <backslash>u followed
    by 4 hexidecimal characters ('0'-'9', 'A'-'F').
    <p>
    Note that if a DataSet already has data, TextDataFile will not delete the existing rows.

*/

  public class TextDataFile extends DataFile implements LoadCancel {

/**
 *  Constructs a TextDataFile component.
 */
  public TextDataFile() {
    String usEncoding = "8859_1";  //NORES
    try {
//!     encoding    = System.getProperty("file.encoding");  //NORES
      encoding    = (new OutputStreamWriter(System.out)).getEncoding();  // avoids security problem
    }
    catch (Exception ex) {
      // May be a applet security violation, so default to US encoding.
      //
      DiagnosticJLimo.printStackTrace();
      encoding  = usEncoding;
    }
    if (encoding.equals(usEncoding))
      fileFormat  = DataFileFormat.ASCII;
    else
      fileFormat  = DataFileFormat.ENCODED;
//!   locale      = Locale.getDefault();
//!   localeName  = locale.toString();
    separator   = '\t';
    delimiter   = '"';
    loadOnOpen  = true;
    fileName    = "TextDataFile.txt";
  }


  public final void setFileFormat(int fileFormat) { this.fileFormat = fileFormat; }
  public final int getFileFormat() { return fileFormat; }

  /** Load rows with a RowStatus of RowStatus.INSERTED if true.  Otherwise
      RowStatus.LOADED will be used.  If set to true these rows will be treated
      as inserted when methods like Database.saveChanges() are called.
  */
  public final void setLoadAsInserted(boolean loadAsInserted) { this.loadAsInserted = loadAsInserted; }
  public final boolean isLoadAsInserted() { return loadAsInserted; }

  public final String getDelimiter() {
    return FastStringBuffer.stringFromChar(delimiter);
    //return new String(new char[]{delimiter});
  }
  /** Default value is ".
  */
  public final void setDelimiter(String delimiter) {
   if (delimiter == null || delimiter.length() < 1)
     this.delimiter = 0;
   else
     this.delimiter = FastStringBuffer.charFromString(delimiter); // handles backslash, etc
  }

  public final String getSeparator() {
    return FastStringBuffer.stringFromChar(separator);
    //return new String(new char[]{separator});
  }
  /** Default value is \t.
  */
  public final void setSeparator(String separator) {
    if (separator == null || separator.length() < 1)
      this.separator = 0;
    else
      this.separator = FastStringBuffer.charFromString(separator);  // handles backslash, unicode, etc.
  }

  /** Default value is DataFileFormat.ASCII.
  */
  public final void setEncoding(String encoding) { this.encoding = encoding; }
  public final String getEncoding() { return encoding; }

  /**
   * Get Locale. The 'locale' property allows the user to identify
   * which locale to use in formatting a column
   */
  public final Locale getLocale() {
    return locale;
  }

  /**
   * Set Locale. The 'locale' property allows the user to identify
   * which locale to use in formatting a column
   */
  public final void setLocale(Locale locale) {
    this.locale = locale;
//!   localeName = (locale != null) ? locale.toString() : null;
  }


//!  /* At best, these should be deprecated
//!  public final void setLocale(String localeName) {
//!    setLocaleName(localeName);
//!  }
//!
//!  public final void setLocaleName(String localeName) {
//!    this.localeName = localeName;
//!    setLocale(LocaleUtil.getLocale(localeName));
//!  }
//!  public final String getLocaleName() { return localeName; }
//!  */

  /** File name where the data file is located.
  */
  public void     setFileName(String fileName) { this.fileName = fileName; }
  public String   getFileName() { return fileName; }

  /** If true, then the DataSet is loaded with the contents of the file when
      it is opened.
  */
  public boolean  isLoadOnOpen()  { return loadOnOpen; }
  public void     setLoadOnOpen(boolean loadOnOpen)  { this.loadOnOpen  = loadOnOpen; }

  private final String getAbsolutePath() {
    return new File(fileName).getAbsolutePath();
  }

  /**
   * Loads data from a stream into the DataSet.
   *
   * @param dataSet
   * @throws IOException
   */
  public final void load(DataSet dataSet)
    /*-throws DataSetException-*/
    throws IOException
  {
    load(dataSet, new FileInputStream(getAbsolutePath()), null);
  }

  /**
   * Loads metadata into the DataSet using the schema file specified
   * by the fileName property.
   *
   * @param dataSet
   * @throws IOException
   */
  public final void loadMetaData(DataSet dataSet)
    /*-throws DataSetException-*/
    throws IOException
  {
    loadMetaData(dataSet, null);
  }


  private Column[] loadMetaData(DataSet dataSet, InputStream schemaStream)
    /*-throws DataSetException-*/
    throws IOException
  {
//! Diagnostic.println("loading from "+fileName);
    StorageDataSet  mappingDataSet  = null;
    Column[]        mappingColumns  = null;
    StorageDataSet  storageDataSet  = dataSet.getStorageDataSet();
    try {
      SchemaFile schemaFile = new SchemaFile();
      if (dataSet.getColumnCount() > 0) {
        if (schemaStream != null)
          schemaFile.load(schemaStream, (mappingDataSet = new TableDataSet()));
        else
          schemaFile.load(getAbsolutePath(), (mappingDataSet = new TableDataSet()));
      }
      else {
        if (schemaStream != null)
          schemaFile.load(schemaStream, storageDataSet);
        else
          schemaFile.load(getAbsolutePath(), storageDataSet);
      }

      fileFormat  = schemaFile.fileFormat;
      encoding    = schemaFile.encoding;
      delimiter   = schemaFile.delimiter;
      separator   = schemaFile.separator;
//!     localeName  = schemaFile.locale;
      if (schemaFile.localeName != null)
        setLocale(LocaleUtil.getLocale(schemaFile.localeName));
      if (locale != null && storageDataSet.getLocale() == null) {
        storageDataSet.changeLocale(locale);
      }
    }
    catch (IOException ex) {
      //
      // Can be ok to fail if user adds DataSet columns first.
      //
      //! Diagnostic.printStackTrace(ex);
    }

    if (mappingDataSet == null || mappingDataSet.getColumnCount() < 1){
      mappingColumns  = storageDataSet.getColumns();
    }
    else {
      mappingColumns  = mappingDataSet.cloneColumns();
      int count = mappingColumns.length;
      for(int ordinal = 0; ordinal < count; ++ordinal)
        storageDataSet.addUniqueColumn(mappingColumns[ordinal]);
    }
    return mappingColumns;
  }

  /**
   * Loads data from the input stream into the specified DataSet.
   * The schemaStream parameter can be set to null if columns have already
   * been added to the DataSet.
   *
   * @param dataSet
   * @param stream
   * @param schemaStream
   * @throws IOException
   */
  public final void load(DataSet dataSet, InputStream stream, InputStream schemaStream)
    /*-throws DataSetException-*/
    throws IOException
  {
    SimpleCharInputStream in  = null;
    Column[] mappingColumns = loadMetaData(dataSet, schemaStream);

    if (fileFormat == DataFileFormat.ASCII)
      in = new AsciiInputStream(stream, BufferSize);
    else
      in = new EncodedInputStream(stream, getEncoding());

    this.dataSetStore = dataSet.getStorageDataSet();
    this.dataSet      = dataSet;

    this.tokenizer    = new ImportTokenizer(in, separator, delimiter);

    if (dataSet.getColumnCount() < 1)
      DataSetException.cantImportNullDataSet();
    loadData(dataSet, mappingColumns);
  }

  // LoadCancel implementation.
  //
  public final void cancelLoad() { cancel = true; }

  private final void loadData(DataSet dataSet, Column[] mappingColumns)
    /*-throws DataSetException-*/
    throws IOException
  {
    int       columnIndex      = 0;
    Column    column;
    Exception errorEx     = null;
    int       errorRow    = 0;
    Column    errorColumn = null;
    int       row         = 1;

    if (dataSetStore.getNeedsRestructure())
      dataSetStore.restructure();

    try {
      int       hit;
      Exception formatException = null;
      cancel  = false;

      Variant[] values        = dataSetStore.startLoading(this, loadAsInserted ? RowStatus.INSERTED:RowStatus.LOADED, false);
      Column[]  columns       = new Column[mappingColumns.length];
      int       maxColumns    = columns.length;

      for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
        column  = dataSet.getColumn(mappingColumns[ordinal].getColumnName());
        if (column.dataType == Variant.INPUTSTREAM || !column.isResolvable())
            columns[ordinal]  = null;
        else {
          columns[ordinal]  = column;
          columns[ordinal].getExportFormatter(); // preload export formatters before load
        }
      }

      dataSet.open();

      processRow:
      while (true) {
        if (cancel)
          break;
        try {
          // Process a row;
          //
          while (true) {
            hit     = tokenizer.nextToken();

//! Diagnostic.println("hit "+hit+" "+new String(tokenizer.token, 0, tokenizer.tokenLength));

//!         JOAL: Steve BUG7473 missed the last row, when there is only 1 column in the file:
//!           if (hit == ImportTokenizer.EOF && columnIndex == 0)
//!             break;
            if (hit == ImportTokenizer.EOF && tokenizer.tokenLength == 0)
              break;

            while (columnIndex < maxColumns && columns[columnIndex] == null) {
              ++columnIndex;
            }
            if (columnIndex < maxColumns) {
              column  = columns[columnIndex];
              if (tokenizer.tokenLength < 1 && tokenizer.delimiterHit == false) {
                values[column.ordinal].setUnassignedNull();
              }
              else {
                DiagnosticJLimo.check(column.exportFormatter != null);

                column.exportFormatter.parse(values[column.ordinal], tokenizer.token, 0, tokenizer.tokenLength);
              }
              ++columnIndex;
            }
            if (hit == ImportTokenizer.EOL || hit == ImportTokenizer.EOF) {
              break;
            }
          }
        }
        catch(IOException iox) {
          throw iox;
        }
        catch(Exception ex) {
          if (errorEx == null) {
            errorEx     = ex;
            errorRow    = row;
            errorColumn = columns[columnIndex];
          }
          values[columns[columnIndex].ordinal].setAssignedNull();
          ++columnIndex;
          continue processRow;
        }

        ++row;
//!        /*
//!        if ((row % 1000) == 0) {
//!          Diagnostic.println("loaded "+row+"s");
//!          System.gc();
//!        }
//!        */

        if (columnIndex != maxColumns) {
          for (int index = columnIndex; index < maxColumns; ++index) {
            if (columns[index] != null) {
              column  = columns[index];
//!             Diagnostic.println("index:  "+index+" "+values.length+" "+column.ordinal+" "+column.getColumnName());
              values[column.ordinal].setAssignedNull();
            }
          }
        }
        if (columnIndex > 0) {
          dataSetStore.loadRow();
        }
        if (hit == ImportTokenizer.EOF)
          break;
        columnIndex  = 0;
      }
    }
    finally {
      tokenizer.closeStream();
      dataSetStore.endLoading();
    }
    if (errorEx != null)
      DataSetException.invalidFormat(errorEx, errorRow, errorColumn);
  }

  /**
      Saves dataSet data to the file specified by the fileName property setting.
  */
  public void save(DataSet saveDataSet)
    /*-throws DataSetException-*/
    throws IOException
  {
    save(saveDataSet, new FileOutputStream(getAbsolutePath()), null);
  }

/**
 * Saves the DataSet data and metadata as specified.
 *
 * @param saveDataSet       The DataSet containing the data to save.
 * @param stream            The stream that will contain the actual data from the DataSet.
 * @param schemaStream      The stream that will contain the DataSet's metadata. If
 *                          this parameter is null, the fileName property is used to
 *                          save the metadata.
 * @throws IOException
 */
  public void save(DataSet saveDataSet, OutputStream stream, OutputStream schemaStream)
    /*-throws DataSetException-*/
    throws IOException
  {
    saveDataSet.open();
    saveDataSet.post();
    dataSet       = saveDataSet.cloneDataSetView();
    dataSet.open();
    try {
      DiagnosticJLimo.check(dataSet.getColumnList() != null);
      dataSet.first();

      SimpleCharOutputStream  out;

      if (fileFormat == DataFileFormat.ASCII)
        out       = new AsciiOutputStream(stream, BufferSize);
      else
        out       = new EncodedOutputStream(stream, getEncoding(), BufferSize);

      this.separator    = separator;

      dataSetStore      = this.dataSet.getStorageDataSet();
      dataRow           = new DataRow(dataSet);
      DiagnosticJLimo.check(dataSet != null);
      DiagnosticJLimo.check(dataSet.getColumnList() != null);
      columns           = dataSet.columnList.getColumns();
      stringColumn      = new boolean[columns.length];
      for (int index = 0; index < columns.length; ++index) {
        if (columns[index].dataType == Variant.INPUTSTREAM || !columns[index].isResolvable())
            columns[index]  = null;
        else {
          if (columns[index].dataType == Variant.STRING)
            stringColumn[index] = true;
          columns[index].getExportFormatter();    // create the export/formatter once before row scan
        }
      }

      synchronized(dataSetStore) {
        if (schemaStream == null)
          new SchemaFile().save(getAbsolutePath(), dataSetStore, encoding, fileFormat, locale, delimiter, separator);
        else
          new SchemaFile().save(schemaStream, dataSetStore, encoding, fileFormat, locale, delimiter, separator);
  //! Diagnostic.println("saving to "+fileName);
        saveData(out);
      }
    }
    finally {
      dataSet.close();
    }
  }

  private final void saveData(SimpleCharOutputStream out)
    throws IOException, DataSetException
  {
    try {
      dataSet.first();
//      int         rowCount      = dataSet.getRowCount();
      int         columnCount   = columns.length;
      int         columnIndex;
      int         row;
      int         stringPos;
      int         stringLen;
      String      string;
      Column      column;
      Variant[]   values        = dataRow.getRowValues(dataSet.columnList);
      Variant     value;

      for (row = 0; dataSet.inBounds(); ++row) {
        dataSetStore.getRowData(dataSet, row, dataRow);
        for (columnIndex = 0; ;) {
          column  = columns[columnIndex];
          if (column != null && !values[column.ordinal].isNull()) {
            //!Potential future optimization to use char[]
            //
            //!if (columns[columnIndex].formatter.format(values[column.ordinal], charBuffer))
            //!  Diagnostic.fail();
            //!else {
            if (columns[columnIndex].exportFormatter == null) {
              DiagnosticJLimo.println("Null export formatter");
              DiagnosticJLimo.fail();
            }

            value = values[column.ordinal];
            if (!value.isNull()) {
              string = columns[columnIndex].exportFormatter.format(value);
              if (delimiter != 0 && stringColumn[columnIndex])
                out.writeDelimited(string, delimiter);
              else
                out.write(string);
            }
          }
          if (++columnIndex < columnCount) {
            if (column != null)
              out.write(separator);
          }
          else
            break;
        }
        out.writeln();
        dataSet.next();
      }
    }
    catch (ValidationException ex) {
      DiagnosticJLimo.fail();
    }

    out.close();
  }


  private           boolean                 loadAsInserted;
  private           int                     fileFormat;
  private transient DataSet                 dataSet;
  private transient StorageDataSet          dataSetStore;
  private transient DataRow                 dataRow;

  private           char                    separator;
  private           char                    delimiter;
  private           String                  encoding;
  private           Locale                  locale;
//! private transient String                  localeName;
  private transient Column[]                columns;
  private transient boolean[]               stringColumn;
  private transient ImportTokenizer         tokenizer;

  private           boolean                 loadOnOpen;
  private           String                  fileName;
  private transient boolean                 cancel;
  private static    int                     BufferSize  = (16*1024);
  private static    final long              serialVersionUID = 1L;
}
