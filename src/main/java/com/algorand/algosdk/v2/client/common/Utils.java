package com.algorand.algosdk.v2.client.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

	/**
	 * Parse the date from String. The time-zone should be Z
	 * @param dateString YYYY-MM-DDTHH:MM:SSZ e.g. 2011-12-03T10:15:30Z
	 * @return Date object
	 * @throws ParseException 
	 */
	public static Date parseDate(String dateString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		if (dateString.indexOf('.') > 0) {
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSX");
		}
		if (dateString.indexOf('Z') == -1) {
			throw new RuntimeException("The time should end with the time-zone Z");
		}
		return sdf.parse(dateString.replace("Z", "-0000"));
	}

	/**
	 * Get the date formatted as: 2011-12-03T10:15:30Z
	 * @param date object
	 */
	public static String getDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		sdf.setTimeZone(TimeZone.getTimeZone("Z"));
		return sdf.format(date);
	}
}
