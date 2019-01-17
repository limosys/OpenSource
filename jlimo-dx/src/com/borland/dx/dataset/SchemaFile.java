//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/SchemaFile.java,v 7.0 2002/08/08 18:39:35 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.io.*;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.lang.Character;

/** This class has methods to load and save .schema files used
    for import/export operations.  A schema file contains metadata
    information about a particular dataset.
*/
class SchemaFile {

  private static final String FIELD     =  "FIELD";                   //NORES
  private static final String FILETYPE  =  "FILETYPE";                //NORES
  private static final String VARYING   =  "VARYING";                 //NORES
  private static final String ENCODING  =  "ENCODING";                //NORES
  private static final String LOCALE    =  "LOCALE";                  //NORES
  private static final String DELIMITER =  "DELIMITER";               //NORES
  private static final String SEPARATOR =  "SEPARATOR";               //NORES
  private static final String FILEFORMAT=  "FILEFORMAT";              //NORES
  private static final String ASCII     =  "Ascii";                   //NORES
  private static final String ENCODED   =  "Encoded";                 //NORES
  private static final String VARIANT   =  "Variant.";                //NORES
  private static final String EQUALS    =  " = ";                     //NORES
  private static final String DELIMITERMASK    =  " .,\t\r\n[]=";     //NORES
    // display masks can contain delimiters, confusing tokenizer, so DELIMITERMASK should contain
    // a list of all the delimiters we use AS delimiters in this class.  They will be mapped to
    // unicode escape sequences when writing the schema file and "normalized" back to their
    // readable form when the schema file is read back in.

  void load(String fileName, StorageDataSet dataSet)
    throws IOException, DataSetException
  {
    load(new FileInputStream(makeSchemaFileName(fileName)), dataSet);
  }

  void load(InputStream stream, StorageDataSet dataSet)
    throws IOException, DataSetException
  {
    try{
      loadSchema(stream, dataSet);
    }
    catch(IOException ioex) {
      throw ioex;
    }
    catch(Exception ex) {
      DataSetException.throwException( DataSetException.INVALID_SCHEMA_FILE,
                                       Res.bundle.getString(ResIndex.InvalidSchemaFile),ex
                                     );
    }
  }

  private void loadSchema(InputStream inputStream, StorageDataSet dataSet)
    throws IOException, DataSetException
  {
    AsciiInputStream        stream;
    String                  line;
    String                  tableName = null;
    StringTokenizer         tokenizer;
    String                  key;
    String                  value;

    stream  = new AsciiInputStream(inputStream, 2048);
    while ((line  = stream.readLine()) != null) {
      if (tableName == null) {
        tokenizer = new StringTokenizer(line.trim(), "[]");  //NORES
        if (tokenizer.hasMoreTokens()) {
          tableName = tokenizer.nextToken();
          if (dataSet.getTableName() == null && tableName != null && tableName.length() > 0)
            dataSet.setTableName(tableName);
          continue;
        }
      }
      tokenizer = new StringTokenizer(line, "=\n\r");        //NORES
      if (tokenizer.hasMoreTokens()) {
        key = tokenizer.nextToken().trim();
        if (tokenizer.hasMoreTokens()) {
          value = tokenizer.nextToken().trim();
          if (key.equals(LOCALE)) {
            localeName    = value;
          }
          else if (key.equals(SEPARATOR)) {
            separator  = parseChar(value);
          }
          else if (key.equals(DELIMITER)) {
            delimiter  = parseChar(value);
          }
          else if (key.equals(ENCODING))
            encoding  = value;
          else if (key.equals(FILEFORMAT)) {
            fileFormat  = value.equals(ASCII)?
                          DataFileFormat.ASCII: DataFileFormat.ENCODED;
          }
          else if (key.startsWith(FIELD)) {
            Column  column  = parseColumn(value);
            if (column != null) {
              dataSet.addUniqueColumn(column);
            }
          }
          else if (key.equals(FILETYPE))
            ; // Not used.
          else{
            DiagnosticJLimo.println("Match not found for SchemaFile key:"+key);
          }
        }
      }
    }
  }

  private final char parseChar(String value) {
    if (value.length() > 1 && value.charAt(1) == 'x')           //NORES
      return (char) (Integer.parseInt(value.substring(2), 16));

    return value.charAt(0);
  }

  private final Column parseColumn(String value)
    /*-throws DataSetException-*/
  {
    String  columnName  = FIELD;
    String  dataTypeName;
    int     dataType    = Variant.STRING;
    Column  column;

    StringTokenizer tokenizer = new StringTokenizer(value, ",");
    if (tokenizer.hasMoreTokens()) {
      columnName  = tokenizer.nextToken();
      if (tokenizer.hasMoreTokens()) {
        dataTypeName  = tokenizer.nextToken();
        if (dataTypeName.startsWith(VARIANT)) {
//! Diagnostic.println("parseColumn "+dataTypeName);
          dataType  = Variant.typeOf(dataTypeName.substring(VARIANT.length()));
        }
        column  = new Column();
        column.setColumnName(columnName);
        column.setDataType(dataType);
        if (tokenizer.hasMoreTokens()) {
          column.setPrecision(Integer.parseInt(tokenizer.nextToken()));
          if (tokenizer.hasMoreTokens()) {
            column.setScale(Integer.parseInt(tokenizer.nextToken()));
            if (tokenizer.hasMoreTokens()) {
              FastStringBuffer fsb = new FastStringBuffer(tokenizer.nextToken());
              //! Diagnostic.println("SchemaFile: before normalize, export display mask is:" + fsb.toString()+":");

              fsb = fsb.normalizeDelimiters(DELIMITERMASK);
              if (fsb.length() > 0) { // don't override default export format unless
                //! Diagnostic.println("SchemaFile: export display mask is " + fsb.toString());
                column.setExportDisplayMask(fsb.toString());    // there is an explicit override display mask
              }
            }
          }
        }
        return column;
      }
    }
    return null;
  }

  private static final String formatChar(char ch) {
    if (ch > ' ' && ch <= 0x7F)
      return new Character(ch).toString();
    return "0x"+Integer.toString((ch & 0xFFFF), 16);            //NORES
  }

  private static final String formatColumnDescriptor(Column column, int count) {
    String formatMask = column.getExportDisplayMask();
    return  FIELD + count + EQUALS + column.getColumnName()
          + ","+VARIANT+Variant.typeName(column.getDataType())
          + ","+column.getPrecision() + ","+column.getScale()
          + ","+ ((formatMask != null && formatMask.length() > 0)
                  ? FastStringBuffer.expandDelimiters(formatMask, DELIMITERMASK).toString()
                  : "");   // Note: if no export display mask, we leave a dangling comma followed by nothing
  }

  private static final String makeSchemaFileName(String fileName) {
    int dotOffset = fileName.lastIndexOf('.');
    return (dotOffset > 0 ? fileName.substring(0, dotOffset) : fileName) + ".schema";
  }
  final static void save( String          fileName,
                          StorageDataSet  dataSet,
                          String          encoding,
                          int             fileFormat,
                          Locale          locale,
                          char            delimiter,
                          char            separator
                )
    /*-throws DataSetException-*/
    throws IOException
  {
    save( new FileOutputStream(makeSchemaFileName(fileName)),
          dataSet,
          encoding,
          fileFormat,
          locale,
          delimiter,
          separator
        );
  }

  // Made static for protection against using data members that have the same
  // name as the parameters passed to this method.
  //
  final static void save( OutputStream    outStream,
                          StorageDataSet  dataSet,
                          String          encoding,
                          int             fileFormat,
                          Locale          locale,
                          char            delimiter,
                          char            separator
                )
    /*-throws DataSetException-*/
    throws IOException
  {
    AsciiOutputStream stream;
    stream = new AsciiOutputStream(outStream, 2048);
    stream.writeln("["+(dataSet.getTableName()==null?"":dataSet.getTableName())+"]");
    stream.writeln(FILETYPE+EQUALS+VARYING);
//!   Diagnostic.println("------------------>>>>>>> "+fileFormat);
    stream.writeln(FILEFORMAT+EQUALS+
      ((fileFormat==DataFileFormat.ASCII)?ASCII:ENCODED));
    stream.writeln(ENCODING+EQUALS+encoding);
    if (locale != null)
      stream.writeln(LOCALE+EQUALS+locale.toString());
    stream.writeln(DELIMITER+EQUALS+formatChar(delimiter));
    stream.writeln(SEPARATOR+EQUALS+formatChar(separator));
    int count = dataSet.getColumnCount();
    Column column;
    for (int columnIndex = 0; columnIndex < count; ++columnIndex) {
      column  = dataSet.getColumn(columnIndex);
      if (column.isTextual() && column.isResolvable())
        stream.writeln(formatColumnDescriptor(dataSet.getColumn(columnIndex), columnIndex));
    }
    stream.close();
  }

  String  encoding;
  String  localeName;
  char    delimiter;
  char    separator;
  int     fileFormat;
}

