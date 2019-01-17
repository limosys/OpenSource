//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/cons/ColumnStringBean.java,v 7.1 2003/05/08 15:34:16 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.dataset.cons;


public class ColumnStringBean
{
    public static final String[][] strings  = {
      {"agg",               Res.bundle.getString(ResIndex.BI_agg), "getAgg", "setAgg"},
      {"alignment",         Res.bundle.getString(ResIndex.BI_alignment), "getAlignment", "setAlignment", "com.borland.jbcl.editors.AlignmentEditor"},
      {"background",        Res.bundle.getString(ResIndex.BI_background), "getBackground", "setBackground"},
      {"calcType",          Res.bundle.getString(ResIndex.BI_calcType), "getCalcType", "setCalcType", "com.borland.jbcl.editors.CalcTypeEditor"},
      {"caption",           Res.bundle.getString(ResIndex.BI_caption), "getCaption", "setCaption"},
      {"columnName",        Res.bundle.getString(ResIndex.BI_columnName), "getColumnName", "setColumnName"},
      {"currency",          Res.bundle.getString(ResIndex.BI_currency), "isCurrency", "setCurrency"},
      {"dataType",          Res.bundle.getString(ResIndex.BI_dataType), "getDataType", "setDataType", "com.borland.jbcl.editors.DataTypeEditor"},
      {"displayMask",       Res.bundle.getString(ResIndex.BI_displayMask), "getDisplayMask", "setDisplayMask","com.borland.jbuilder.cmt.editors.DisplayMaskEditor"},
      {"editMask",          Res.bundle.getString(ResIndex.BI_editMask), "getEditMask", "setEditMask", "com.borland.jbuilder.cmt.editors.MaskEditor"},
//!   {"editMasker",        Res.bundle.getString(ResIndex.BI_editMasker), "getEditMasker", "setEditMasker"},
      {"exportDisplayMask", Res.bundle.getString(ResIndex.BI_exportDisplayMask), "getExportDisplayMask", "setExportDisplayMask"},
//!   {"exportFormatter",   Res.bundle.getString(ResIndex.BI_exportFormatter), "getExportFormatter", "setExportFormatter"},
//!   {"fixedPrecision",    Res.bundle.getString(ResIndex.BI_fixedPrecision), "isFixedPrecision", "setFixedPrecision"},
      {"font",              Res.bundle.getString(ResIndex.BI_font), "getFont", "setFont"},
      {"foreground",        Res.bundle.getString(ResIndex.BI_foreground), "getForeground", "setForeground"},
//!   {"formatter",         Res.bundle.getString(ResIndex.BI_formatter), "getFormatter", "setFormatter"},
      {"itemEditor",        Res.bundle.getString(ResIndex.BI_itemEditor), "getItemEditor", "setItemEditor"},
      {"itemPainter",       Res.bundle.getString(ResIndex.BI_itemPainter), "getItemPainter", "setItemPainter"},
//!   {"localeName",        Res.bundle.getString(ResIndex.BI_localeName), "getLocaleName", "setLocaleName", "com.borland.jbcl.editors.LocaleNameEditor"},
//!   {"locale",            Res.bundle.getString(ResIndex.BI_locale), "getLocaleName", "setLocale", "com.borland.jbcl.editors.LocaleNameEditor"},
      {"locale",            Res.bundle.getString(ResIndex.BI_locale), "getLocale", "setLocale"},
//!   {"lookup",            Res.bundle.getString(ResIndex.BI_lookup), "getLookup", "setLookup"},
      {"default",           Res.bundle.getString(ResIndex.BI_default), "getDefault", "setDefault"},
      {"max",               Res.bundle.getString(ResIndex.BI_max), "getMax", "setMax"},
      {"min",               Res.bundle.getString(ResIndex.BI_min), "getMin", "setMin"},
//!   {"ordinal",           Res.bundle.getString(ResIndex.BI_ordinal), "getOrdinal", "setOrdinal"},
//!   {"persist",           Res.bundle.getString(ResIndex.BI_persist), "isPersist", "setPersist"},
      {"pickList",          Res.bundle.getString(ResIndex.BI_pickList), "getPickList", "setPickList"},
      {"parameterType",     Res.bundle.getString(ResIndex.BI_parameterType), "getParameterType", "setParameterType", "com.borland.jbcl.editors.ParameterTypeEditor"},
      {"precision",         Res.bundle.getString(ResIndex.BI_precision), "getPrecision", "setPrecision"},
      {"sortPrecision",     Res.bundle.getString(ResIndex.BI_precision), "getSortPrecision", "setSortPrecision"},
      {"preferredOrdinal",  Res.bundle.getString(ResIndex.BI_preferredOrdinal), "getPreferredOrdinal", "setPreferredOrdinal"},
      {"readOnly",          Res.bundle.getString(ResIndex.BI_columnReadOnly), "isReadOnly", "setReadOnly"},
      {"editable",          Res.bundle.getString(ResIndex.BI_columnEditable), "isEditable", "setEditable"},
      {"required",          Res.bundle.getString(ResIndex.BI_required), "isRequired", "setRequired"},
      {"rowId",             Res.bundle.getString(ResIndex.BI_rowId), "isRowId", "setRowId"},
      {"scale",             Res.bundle.getString(ResIndex.BI_scale), "getScale", "setScale"},
      {"schemaName",        Res.bundle.getString(ResIndex.BI_columnSchemaName), "getSchemaName", "setSchemaName"},
      {"searchable",        Res.bundle.getString(ResIndex.BI_searchable), "isSearchable", "setSearchable"},
      {"resolvable",        Res.bundle.getString(ResIndex.BI_resolvable), "isResolvable", "setResolvable"},
//!   {"sortable",          Res.bundle.getString(ResIndex.BI_sortable), "isSortable", "setSortable"},
//!   {"sqlType",           Res.bundle.getString(ResIndex.BI_sqlType), "getSqlType", "setSqlType"},
      {"tableName",         Res.bundle.getString(ResIndex.BI_columnTableName), "getTableName", "setTableName"},
//!   {"tableColumnName",   Res.bundle.getString(ResIndex.BI_columnTableColumnName), "getTableColumnName", "setTableColumnName"},
//!   {"textual",           Res.bundle.getString(ResIndex.BI_textual), "isTextual", "setTextual"},
      {"visible",           Res.bundle.getString(ResIndex.BI_visible), "getVisible", "setVisible", "com.borland.jbcl.editors.TriStateEditor"},
      {"width",             Res.bundle.getString(ResIndex.BI_width), "getWidth", "setWidth"},
      {"autoIncrement",     Res.bundle.getString(ResIndex.BI_autoIncrement), "isAutoIncrement", "setAutoIncrement"},
    };
}
