package com.crm.exception;

/**
 * 
 * @author Nam <br>
 *         Last Modified Date: 13/07/2012
 */
public class SubscriberNotFoundException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7000846438195265093L;

	public SubscriberNotFoundException()
	{
		super();
	}

	public SubscriberNotFoundException(String message)
	{
		super(message);
	}

	public SubscriberNotFoundException(Throwable cause)
	{
		super(cause);
	}
}
