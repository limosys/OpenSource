//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/Trace.java,v 7.0 2002/08/08 18:40:53 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//NOTTRANSLATABLE

package com.borland.jb.util;



//!CQ ELIMINATE this class!!! This is bogus, grouping all these tags here in a dependency violating global hack!
//!CQ for class-oriented tracing, just use the pkg.pkg.pkg.Clazz.class instance as a tag
//!CQ I will remove this class soon.
/**
 * This interface is used internally by other <CODE>com.borland</CODE>
 * classes. You should never use this interface directly.
 */
public interface Trace
{
  //!CQ move these strings to dataset somewhere
  //!CQ or make a Trace class in dx.dataset!
  public static final String DataSetSave     = "DataSetSave";
  public static final String DataSetFetch    = "DataSetFetch";
  public static final String DataSetEdit     = "DataSetEdit";
  public static final String Notifications   = "Notifications";
  public static final String Master          = "Master";
  public static final String Detail          = "Detail";
  public static final String QueryParse      = "QueryParse";
  public static final String QueryAnalyze    = "QueryAnalyze";
  public static final String QueryProgress   = "QueryProgress";
  public static final String AccessEvents    = "AccessEvents";
  public static final String Locate          = "Locate";
  public static final String ConnectionDescriptor = "ConnectionDescriptor";
  public static final String SortDescriptor       = "SortDescriptor";
  public static final String MasterLinkDescriptor = "MasterLinkDescriptor";
  public static final String QueryDescriptor      = "QueryDescriptor";
  public static final String MaskableEditor       = "MaskableEditor";
  public static final String FormatStr            = "FormatStr";
  public static final String EditMaskStr          = "EditMaskStr";
  public static final String DataStore            = "DataStore";
  public static final String EditEvents           = "Edit";
  public static final String MetaData             = "MetaData";

  //!CQ directly key off the desired event as a convention
  //!CQ or make a Trace class in jbcl.view!
  public static final String ActionEvents    = "ActionEvents";
  public static final String FocusEvents     = "FocusEvents";
  public static final String KeyEvents       = "KeyEvents";
  public static final String MouseEvents     = "MouseEvents";
  public static final String GridEvents      = "GridEvents";
  public static final String HeaderEvents    = "HeaderEvents";
  public static final String ModelEvents     = "ModelEvents";
  public static final String SelectionEvents = "SelectionEvents";
  public static final String SubfocusEvents  = "SubfocusEvents";
}
