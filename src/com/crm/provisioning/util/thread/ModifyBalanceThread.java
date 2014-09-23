package com.crm.provisioning.util.thread;

import java.util.Vector;

import com.crm.provisioning.thread.ProvisioningThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class ModifyBalanceThread extends ProvisioningThread
{
	public String	ccwsComment	= "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("ccwsComment", 400,
				"CCWS comment"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		ccwsComment = ThreadUtil.getString(this, "ccwsComment", false, "");
		// TODO Auto-generated method stub
		super.fillDispatcherParameter();
	}
}
