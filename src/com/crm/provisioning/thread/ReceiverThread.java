package com.crm.provisioning.thread;

import java.util.Vector;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class ReceiverThread extends DispatcherThread
{
	public int	orderTimeout	= 0;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createIntegerParameter("orderTimeout", "Time to live of order (s)."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		orderTimeout = ThreadUtil.getInt(this, "orderTimeout", 0);
		super.fillDispatcherParameter();
	}
}
