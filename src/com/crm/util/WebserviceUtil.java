package com.crm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.crm.exception.InvalidDateFormatException;

public class WebserviceUtil
{
	public static String getSessionId()
	{
		return "" + GeneratorSeq.getNextSeq();
	}
	
	public static String getSessionIdInHex()
	{
		int sequense = GeneratorSeq.getNextSeq();
		String hex = Integer.toHexString(sequense).toUpperCase();
		while (hex.length() < 8)
		{
			hex = "0" + hex;
		}
		hex = "0x" + hex;
		
		return hex;
	}
	


	public static Calendar ddMMyyyyHHmmssToCalendar(String source) throws InvalidDateFormatException
	{
		Date date = null;
		String formatString = "ddMMyyyyHHmmss";
		try
		{
			date = (new SimpleDateFormat(formatString)).parse(source);

		}
		catch (ParseException pe)
		{
			InvalidDateFormatException ex = new InvalidDateFormatException(pe);
			ex.setFormatString(formatString);
			ex.setInvalidString(source);
			throw ex;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal;
	}
}
