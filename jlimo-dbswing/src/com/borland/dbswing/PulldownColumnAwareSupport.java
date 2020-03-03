package com.borland.dbswing;

import java.awt.Component;

import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.ValidationException;
import com.borland.dx.dataset.Variant;

public class PulldownColumnAwareSupport extends DBColumnAwareSupport {

  private Variant value;

  public PulldownColumnAwareSupport(ColumnAware columnAwareObject) {
    super(columnAwareObject);
    value = new Variant();
  }

  @Override
	public String getFormattedString() {
    if (isLookupDefined() && (!isSuggestModeOK() || suggestModeSaveCode)) {
      lazyOpen();
      return getFormattedStringThroughLookup(getVariant());
    } else {
      return super.getFormattedString();
    }
  }

  @Override
	public void setFromString2(String text, Component focusedComponent, DBColumnAwareSupport columnAwareSupport) throws
      Exception {
    if (isLookupDefined() && (!isSuggestModeOK() || suggestModeSaveCode) ) {
      lazyOpen();
      setFromStringThroughLookup(text, focusedComponent);
    } else {
      super.setFromString2(text, focusedComponent, columnAwareSupport);
    }
  }

  @Override
	public boolean setFromString(String text, Component focusedComponent) {
    if (isLookupDefined() && (!isSuggestModeOK() || suggestModeSaveCode)) {
      lazyOpen();
      try {
        setFromStringThroughLookup(text, focusedComponent);
      } catch (Exception ex) {
      	ex.printStackTrace();
        try {
          ValidationException.invalidFormat(ex, null, null);
        } catch (Exception ex1) {
        	ex1.printStackTrace();
          DBExceptionHandler.handleException(dataSet, focusedComponent, ex1);
          if (focusedComponent != null) {
            focusedComponent.requestFocus();
          }
        }
        return false;
      }
      return true;
    } else {
      return super.setFromString(text, focusedComponent);
    }
  }

  private String getFormattedStringThroughLookup(Variant variant) {
    String text = "";
    if (variant.isNull()) return text;
    DataRow dr = new DataRow(lkupDataSet, lkupColVal);
    dr.setVariant(lkupColVal, variant);
    if (lkupDataSet.locate(dr, Locate.FIRST)) {
      text = lkupDataSet.getString(lkupColDisp);
    } else if (suggestMode && suggestModeSaveCode) {
      return variant.getString();
    }
    return text;
  }

  public Variant getValueFromStringThroughLookup(String text) {
    if (text == null || text.equals("")) {
      lkupDataSet.getVariant(lkupColVal, value);
      value.setAssignedNull();
    } else {
      DataRow dr = new DataRow(lkupDataSet, lkupColDisp);
      dr.setString(lkupColDisp, text);
      if (lkupDataSet.locate(dr, Locate.FIRST)) {
        lkupDataSet.getVariant(lkupColVal, value);
      } else if (suggestMode && suggestModeSaveCode) {
        lkupDataSet.getVariant(lkupColVal, value);
        value.setString(text);
      } else {
        lkupDataSet.getVariant(lkupColVal, value);
        value.setAssignedNull();
      }
    }
    return value;
  }



  private void setFromStringThroughLookup(String text, Component focusedComponent) {
    if (text==null || text.equals("")) {
      lkupDataSet.getVariant(lkupColVal, value);
      value.setAssignedNull();
      dataSet.setDisplayVariant(columnOrdinal, value);
    } else {
      try {
        DataRow dr = new DataRow(lkupDataSet, lkupColDisp);
        dr.setString(lkupColDisp, text);
        if (lkupDataSet.locate(dr, Locate.FIRST + Locate.CASE_INSENSITIVE)) {
          lkupDataSet.getVariant(lkupColVal, value);
          dataSet.setDisplayVariant(columnOrdinal, value);
        } else if (suggestMode && suggestModeSaveCode) {
          lkupDataSet.getVariant(lkupColVal, value);
          value.setString(text);
          dataSet.setDisplayVariant(columnOrdinal, value);
        } else {
          lkupDataSet.getVariant(lkupColVal, value);
          value.setAssignedNull();
          dataSet.setDisplayVariant(columnOrdinal, value);
        }
      } catch (Exception ex) {
      	if (!neverShowPulldown)  ex.printStackTrace();
        if (focusedComponent != null) {
          focusedComponent.requestFocus();
        }
        ValidationException.invalidFormat(ex, null, null);
      }
    }
  }

  DataSet lkupDataSet;
  String lkupColVal;
  String lkupColDisp;
  boolean suggestMode = false;
  boolean suggestModeSaveCode = false;
  boolean neverShowPulldown = false;

  public void setNeverShowPulldown(boolean neverShowPulldownList) {
  	neverShowPulldown = neverShowPulldownList;
  }

  public boolean isNeverShowPulldown() {
    return neverShowPulldown;
  }
  
  public void setLookupDataSet(DataSet dataSet) {
    lkupDataSet = dataSet;
  }

  public DataSet getLookupDataSet() {
    return lkupDataSet;
  }

  public void setLookupValueColumn(String columnName) {
    lkupColVal = columnName;
  }

  public String getLookupValueColumn() {
    return lkupColVal;
  }

  public void setLookupDisplayColumn(String columnName) {
    lkupColDisp = columnName;
  }

  public String getLookupDisplayColumn() {
    return lkupColDisp;
  }

  public boolean isLookupDefined() {
    return lkupDataSet != null
        && lkupDataSet.isOpen()
        && lkupColVal != null
        && lkupDataSet.hasColumn(lkupColVal) != null
        && lkupColDisp != null
        && lkupDataSet.hasColumn(lkupColDisp) != null;
  }

  public boolean isSuggestMode() {
    return suggestMode;
  }

  public void setSuggestMode(boolean isSuggestMode) {
    this.suggestMode = isSuggestMode;
  }

  public boolean isSuggestModeSaveCode() {
    return suggestModeSaveCode;
  }

  public void setSuggestModeSaveCode(boolean isSuggestMode) {
    this.suggestModeSaveCode = isSuggestMode;
  }
  public boolean isSuggestModeOK() {
    return suggestMode;
  }

}
