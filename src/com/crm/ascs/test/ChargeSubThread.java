package com.crm.ascs.test;

import java.util.Vector;

import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class ChargeSubThread extends SubscriberTestThread
{
	public double	chargeValue		= 50000;
	public int		chargeDay		= 30;
	public String	chargeComment	= "test";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createNumericParameter("chargeValue", "Charge money value."));
		vtReturn.add(ThreadUtil.createIntegerParameter("chargeDay", "Charge day (expiration offset)."));
		vtReturn.add(ThreadUtil.createTextParameter("chargeComment", 400, "Charge comment."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		chargeValue = ThreadUtil.getDouble(this, "chargeValue", 50000);
		chargeDay = ThreadUtil.getInt(this, "chargeDay", 30);
		chargeComment = ThreadUtil.getString(this, "chargeComment", false, "ASCS Test");

		super.fillDispatcherParameter();
	}
}
