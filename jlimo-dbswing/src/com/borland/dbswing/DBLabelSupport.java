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

import java.io.*;

import java.awt.*;
import javax.swing.*;

import com.borland.dx.dataset.*;

//
// Note - In the pubs files, this is marked as an
// internal use only class. Changed javadoc comments to
// internal comments.
// Old Javaodc comment starts here:
// DBLabelSupport is a subclass of DBColumnAwareSupport
// which adds support for a second column on the same
// dataset.
// Created for, and used primarily by DBLabelDataBinder,
// which needs
// column support for both its <I>text</I> and
// <I>icon</I> properties.
//
// @see DBColumnAwareSupport
// @see JdbLabel
//

/**
 * <p>Internal use only class.</p>
 */
public class DBLabelSupport extends DBColumnAwareSupport
  implements java.io.Serializable
{
// Old Javaodc comment starts here:
// @see DBColumnAwareSupport(Object)
/**
 * <p>Internal use only.</p>
 */
  public DBLabelSupport(ColumnAware columnAwareObject) {
    super(columnAwareObject);
  }

// Old Javaodc comment starts here:
// Sets the column name of the DataSet from which the // // label's icon
//  should be read.
//
//  @see getColumnNameIcon
// @see getColumnName
// @see setDataSet
//
/**
 * <p>Internal use only.</p>
 */
  public void setColumnNameIcon(String columnNameIcon) {
    if (this.columnNameIcon == null || !this.columnNameIcon.equals(columnNameIcon)) {
      String oldColumnNameIcon = this.columnNameIcon;
      this.columnNameIcon = columnNameIcon;

      // instructs lazyOpen() that it must update columnOrdinal
      columnChanged = true;

      // if the dataSet has been opened already, then we need to
      // immediately update state when this property changes
      if (liveProperties) {
        lazyOpen();
      }
    }
  }

// Old Javaodc comment starts here:
//  Returns the column name of the DataSet from which
// the label's icon should be read.
//
// @see setColumnNameIcon
// @see setColumnName
//
/**
 * <p>Internal use only.</p>
 */
  public String getColumnNameIcon() {
    return columnNameIcon;
  }

  // Used to avoid opening the DataSet until data is
  // actually requested from the model.  Sets liveProperties
  // to true on the first valid open attempt.
/**
 * <p>Internal use only.</p>
 */
  protected void lazyOpen() {

    // if we've ever tried opening the dataSet, then
    // properties should be put into their immediate response
    // state
    liveProperties = true;
    if (dataSetChanged || columnChanged) {

      if (dataSetChanged) {
        if (dataSet != null) {
          try {
            if (!dataSet.isOpen()) {
              dataSet.open();
            }
          }
          catch (DataSetException e) {
            columnOrdinal = -1;
            columnOrdinalIcon = -1;
            // setting dataSetChanged false will prevent subsequent open
            // attempts which will also fail until the invalid column or
            // dataSet problem is fixed.
            dataSetChanged = false;
            DBExceptionHandler.handleException(dataSet, e);
            return;
          }
        }
      }

      if (dataSetChanged || columnChanged) {
        columnOrdinal = -1;
        columnOrdinalIcon = -1;

        if (dataSet != null) {
          if (columnName != null) {
            try {
              columnOrdinal = dataSet.getColumn(columnName).getOrdinal();
            }
            catch (DataSetException e) {
              // setting columnChanged false will prevent subsequent open
              // attempts which will also fail until the invalid column or
              // dataSet problem is fixed.
              columnChanged = false;
              DBExceptionHandler.handleException(dataSet, e);
              return;
            }
          }

          if (columnNameIcon != null) {
            try {
              columnOrdinalIcon = dataSet.getColumn(columnNameIcon).getOrdinal();
              if (!(dataSet.getColumn(columnNameIcon).getDataType() == Variant.INPUTSTREAM ||
                    dataSet.getColumn(columnNameIcon).getDataType() == Variant.OBJECT)) {
                throw new IllegalArgumentException(Res._InvalidIconColumnType);     
              }
            }
            catch (DataSetException e) {
              // setting dataSetChanged false will prevent subsequent open
              // attempts which will also fail until the invalid column or
              // dataSet problem is fixed.
              dataSetChanged = false;
              DBExceptionHandler.handleException(dataSet, e);
              return;
            }
          }
        }
        columnChanged = false;
      }
      dataSetChanged = false;
    }
  }

/**
 * <p>Internal use only.</p>
 */
  public boolean isValidDataSetState() {
    return (dataSet != null && dataSet.isOpen() && !(columnOrdinal == -1 && columnOrdinalIcon == -1));
  }

  //
  // Some convenience methods
  //

/**
 * <p>Internal use only.</p>
 */
  public void putIcon(InputStream inputStream) {
    try {
      if (isValidDataSetState() && columnOrdinalIcon != -1) {
        dataSet.setInputStream(columnOrdinalIcon,
                               new ByteArrayInputStream(DBUtilities.getByteArrayFromStream(inputStream)));
      }
    }
    catch (DataSetException e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
  }

/**
 * <p>Internal use only.</p>
 */
  public Icon getIcon() {
    try {
      if (isValidDataSetState() && columnOrdinalIcon != -1) {
        Variant v = new Variant();
        dataSet.getVariant(columnNameIcon, v);
        if (v.getType() == Variant.INPUTSTREAM) {
          InputStream inputStream = v.getInputStream();
          inputStream.reset();
          Image bmpImage = DBUtilities.makeBMPImage(inputStream);
          if (bmpImage != null) {
            return new ImageIcon(bmpImage);
          }
          else {
            // if the label was bound to the INPUTSTREAM column, assume it contains
            // either a BMP, GIF, or JPEG image.
//            if (DBUtilities.isGIForJPG(inputStream)) {
              byte [] bytes = DBUtilities.getByteArrayFromStream(inputStream);
              if (bytes == null) {
                return null;
              }
              else {
                return new ImageIcon(bytes);
              }
//            }
          }
        }
        else if (v.getType() == Variant.OBJECT) {
          Object object = v.getObject();
          if (object instanceof Image) {
            return new ImageIcon((Image) object);
          }
          if (object instanceof Icon) {
            return (Icon) object;
          }
        }
      }
    }
    catch (Exception e) {
      DBExceptionHandler.handleException(dataSet, e);
    }
    return null;
  }

// Old Javaodc comment starts here:
// columnName from which icon should be read
/**
 * <p>Internal use only.</p>
 */
  String columnNameIcon;

// Old Javaodc comment starts here:
// ordinal of Column columnNameIcon of DataSet dataSet
//
/**
 * <p>Internal use only.</p>
 */
  int columnOrdinalIcon = -1;

}
