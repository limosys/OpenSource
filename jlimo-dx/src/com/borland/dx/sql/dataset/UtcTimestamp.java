package com.borland.dx.sql.dataset;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UtcTimestamp {

	private static SimpleDateFormat utcFormat = initUtcDateFormat();

	private static SimpleDateFormat initUtcDateFormat() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		f.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
		return f;
	}

	public static String formatAsUtc(Timestamp dtm) {
		if (dtm == null) return "";
		synchronized (utcFormat) {
			return utcFormat.format(dtm);
		}
	}

	public static Timestamp parseFromUtc(String sUtcDtm) {
		if (sUtcDtm == null || sUtcDtm.trim().isEmpty()) return null;
		// for proper parsing sUtcDtm must have full 3 digits of milliseconds
		int p = sUtcDtm.lastIndexOf(".");
		if (p >= 0) {
			int msCount = sUtcDtm.length() - p;
			while (msCount <= 3) {
				sUtcDtm += "0";
				msCount++;
			}
		} else {
			sUtcDtm += ".000";
		}
		try {
			synchronized (utcFormat) {
				return new Timestamp(utcFormat.parse(sUtcDtm).getTime());
			}
		} catch (Exception ex) {
			return null;
		}
	}

}
