package com.crm.exception;

public class InvalidDateFormatException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8889098062944563757L;
	
	private String invalidString = "";
	private String formatString = "";

	public void setFormatString(String formatString)
	{
		this.formatString = formatString;
	}

	public String getFormatString()
	{
		return formatString;
	}

	public void setInvalidString(String invalidString)
	{
		this.invalidString = invalidString;
	}

	public String getInvalidString()
	{
		return invalidString;
	}

	public InvalidDateFormatException()
	{
		super();
	}
	
	public InvalidDateFormatException(String message)
	{
		super(message);
	}
	
	public InvalidDateFormatException(Throwable cause)
	{
		super(cause);
	}
	
}
