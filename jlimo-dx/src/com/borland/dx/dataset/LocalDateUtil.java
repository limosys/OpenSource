//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/LocalDateUtil.java,v 7.1.2.2 2004/05/27 18:29:46 jlaurids Exp $
// Copyright (c) 1999-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.dx.dataset.Variant;
import com.borland.jb.util.DiagnosticJLimo;
import com.borland.jb.util.FastStringBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class LocalDateUtil {

  public static long getLocalDateAsLong(Date date, Calendar cal) {
    int year, month, day;
    if (cal == null) {
      year  = date.getYear()+1900;
      month = date.getMonth()+1;
      day   = date.getDate();
    }
    else {
      cal.setTime(date);
      year  = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH)+1;
      day   = cal.get(Calendar.DAY_OF_MONTH);
    }
    long longValue = year;
    longValue *= 16;
    longValue += month;
    longValue *= 32;
    longValue += day;
    return longValue;
  }

  public static long getLocalTimeAsLong(Time time, Calendar cal) {
    int hour, min, secs;
    if (cal == null) {
      hour = time.getHours();
      min  = time.getMinutes();
      secs = time.getSeconds();
    }
    else {
      cal.setTime(time);
      hour = cal.get(Calendar.HOUR_OF_DAY);
      min  = cal.get(Calendar.MINUTE);
      secs = cal.get(Calendar.SECOND);
    }
    long longValue = hour;
    longValue *= 60;
    longValue += min;
    longValue *= 60;
    longValue += secs;
    longValue *= 1000;
    return longValue;
  }

  public static void setAsLocalDate(Variant variant, long longValue, Calendar cal) {
    int day = (int)longValue & 31;
    longValue >>= 5;
    int month = (int)longValue & 15;
    longValue >>= 4;
    int year = (int)longValue;
    if (cal == null)
      variant.setDate(new Date(year-1900, month-1, day));
    else {
      cal.clear();
      cal.set(year,month-1,day);
      variant.setDate(new Date(cal.getTime().getTime()));
    }
  }

  public static void setAsLocalTime(Variant variant, long longValue, Calendar cal) {
    longValue /= 1000;
    int seconds = (int)(longValue % 60);
    longValue /= 60;
    int minutes = (int)(longValue % 60);
    longValue /= 60;
    int hours = (int)longValue;
    if (cal == null)
      variant.setTime(new Time(hours, minutes, seconds));
    else {
      cal.clear();
      cal.set(0,0,0,hours, minutes, seconds);
      variant.setTime(new Time(cal.getTime().getTime()));
    }
  }

  private static final void appendInt(FastStringBuffer buf, int value) {
    if (value < 10)
      buf.append('0');
    buf.append(Integer.toString(value));
  }

  /**
   * Formats a date in the date escape format yyyy-mm-dd.
   *
   * @param  variant a date value
   * @return a String in yyyy-mm-dd format
   */
  public static String getLocalDateAsString(Variant variant) {
    int encodedValue = variant.getAsInt();
    int day = encodedValue & 31;
    encodedValue >>= 5;
    int month = encodedValue & 15;
    encodedValue >>= 4;
    int year = encodedValue;

    FastStringBuffer buf = new FastStringBuffer(10);
//    String monthString, dayString;
    appendInt(buf, year);
    buf.append('-');
    appendInt(buf, month);
    buf.append('-');
    appendInt(buf, day);

    return buf.toString();
  }

  public static String getLocalTimeAsString(Variant variant) {
    int encodedValue = variant.getAsInt();
    encodedValue /= 1000;
    int second = encodedValue % 60;
    encodedValue /= 60;
    int minute = encodedValue % 60;
    encodedValue /= 60;
    int hour = encodedValue;

    FastStringBuffer buf = new FastStringBuffer(8);
    appendInt(buf, hour);
    buf.append(':');
    appendInt(buf, minute);
    buf.append(':');
    appendInt(buf, second);

    return buf.toString();
  }

  /**
   * Converts a string in JDBC date format to a <code>Variant</code> value.
   *
   * @param value date in format "yyyy-mm-dd"
   * @param variant the date is set in this <code>Variant</code> object
   */
  public static void setLocalDateAsLong(Variant variant, String value) {
    int year;
    int month;
    int day;
    int firstDash;
    int secondDash;

    if (value == null) throw new java.lang.IllegalArgumentException();

    firstDash = value.indexOf('-');
    secondDash = value.indexOf('-', firstDash+1);
    if ((firstDash <= 0) || (secondDash <= 0) || (secondDash >= value.length()-1))
      throw new java.lang.IllegalArgumentException();

    year  = Integer.parseInt(value.substring(0, firstDash));
    month = Integer.parseInt(value.substring(firstDash+1, secondDash));
    day   = Integer.parseInt(value.substring(secondDash+1));
    if (month == 0 || month > 12 || day ==0 || day > 31)
      throw new java.lang.IllegalArgumentException();

    int encodedValue = year;
    encodedValue *= 16;
    encodedValue += month;
    encodedValue *= 32;
    encodedValue += day;
    variant.setEncodedDate(encodedValue);
  }

  /**
   * Converts a string in JDBC time format to a <code>Variant</code> value.
   *
   * @param value time in format "hh:mm:ss"
   * @param variant the time is set in this <code>Variant</code> object
   */
  public static void setLocalTimeAsLong(Variant variant, String value) {
    int hour;
    int minute;
    int second;
    int firstColon;
    int secondColon;

    if (value == null) throw new java.lang.IllegalArgumentException();

    firstColon = value.indexOf(':');
    secondColon = value.indexOf(':', firstColon+1);
    if ((firstColon <= 0) || (secondColon <= 0) || (secondColon >= value.length()-1))
      throw new java.lang.IllegalArgumentException();

    hour   = Integer.parseInt(value.substring(0, firstColon));
    minute = Integer.parseInt(value.substring(firstColon+1, secondColon));
    second = Integer.parseInt(value.substring(secondColon+1));

    int encodedValue = hour;
    encodedValue *= 60;
    encodedValue += minute;
    encodedValue *= 60;
    encodedValue += second;
    encodedValue *= 1000;
    if (encodedValue < 0 || encodedValue >= 86400000) {
      encodedValue = encodedValue % 86400000;
      if (encodedValue < 0)
        encodedValue += 86400000;
    }
    variant.setEncodedTime(encodedValue);
  }
}
