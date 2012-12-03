package com.finde.topoos.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

	/**
	 * Devuelve True si la cadena es vacia o null
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean IsStringNullOrEmpty(String value) {
		return value == null || value == "";
	}

	/**
	 * Devuelve el dato string convertido a un objeto Calendar
	 * @param str_date
	 * @return
	 */
	public static Calendar StringToCalendar(String str_date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date;
		try {
			date = sdf.parse(str_date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (java.text.ParseException e) {
		}

		return null;

	}

}
