package com.crm.ascs.test;

import com.crm.ascs.net.TriggerRecharge;

public class RTBSMessage extends TriggerRecharge
{

	/**
	 * 
	 */
	private static final long	serialVersionUID		= 5721283254678353185L;

	public static String		ACTION_CREATE_SUB		= "CREATE SUB";
	public static String		ACTION_DELETE_SUB		= "DELETE SUB";
	public static String		ACTION_CHANGE_STATE_SUB	= "CHANGE STATE SUB";
	public static String		ACTION_CHARGE_SUB		= "CHARGE SUB";

	private String				action					= "";

	public String getAction()
	{
		return action;
	}

	public RTBSMessage(String isdn, String action) throws Exception
	{
		setIsdn(isdn);
		this.action = action;
		if (!action.equals(ACTION_CREATE_SUB)
			&& !action.equals(ACTION_DELETE_SUB)
			&& !action.equals(ACTION_CHANGE_STATE_SUB)
			&& !action.equals(ACTION_CHARGE_SUB))
		{
			throw new Exception("Invalid RTBS Message action.");
		}
	}
	
	@Override
	public String toString()
	{
		return getAction() + ":" + getIsdn();
	}
}
