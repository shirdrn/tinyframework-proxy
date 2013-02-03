package org.shirdrn.tinyframework.proxy.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date time related utility methods.
 * 
 * @author Yanjun
 */
public class DatetimeUtils {

	/**
	 * Compute specified {@link Date} using {@link Calendar} class. For example:<br>
	 * if howManyDays=-2, date="2009-06-04 18:21:46", 
	 * then return value is "2009-06-02 18:21:46".
	 * @param howManyDays if howManyDays<0 then Math.abs(howManyDays) days ago.
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getDatetime(int howManyDays, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, howManyDays);
		Date beforeDate = calendar.getTime();
		beforeDate.setHours(date.getHours());
		beforeDate.setMinutes(date.getMinutes());
		beforeDate.setSeconds(date.getSeconds());
		return beforeDate;
	}
	
	/**
	 * Compute specified {@link Date} using {@link Calendar} class. For example:<br>
	 * if howManyDays=-2, current date time is "2012-06-04 18:21:46", 
	 * then return value is "2012-06-02 18:21:46".
	 * @param howManyDays if howManyDays<0 then Math.abs(howManyDays) days ago.
	 * @return
	 */
	public static Date getDatetime(int howManyDays) {
		return getDatetime(howManyDays, new Date());
	}
	
	/**
	 * Compute specified {@link Date} according to date string and date format.<br>
	 * For example:<br>
	 * if dateString="2012/06/04 12:54:31", format='yyyy/MM/dd HH:mm:ss',
	 * then return the format of {@link Date} representation.
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static Date getDatetime(String dateString, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Compute String format of {@link Date} according to using {@link Calendar} class
	 * and a given date time format.
	 * @param howManyDays
	 * @param date
	 * @param format
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String formatDatetime(int howManyDays, Date date, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, howManyDays);
		Date beforeDate = calendar.getTime();
		beforeDate.setHours(date.getHours());
		beforeDate.setMinutes(date.getMinutes());
		beforeDate.setSeconds(date.getSeconds());
		return formatDateTime(beforeDate, format);
	}
	
	/**
	 * Compute String format of {@link Date}.
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDateTime(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		String dateString = df.format(date);
		return dateString;
	}

}
