package com.crm.ascs.test;

import java.util.Vector;

import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class ChangeSubStateThread extends SubscriberTestThread
{
	public String	nextState	= "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createComboParameter("nextState", "Active,Suspended(S1),Disabled(S2),Retired(S3),Idle,Deleted", "Next subscriber state."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		nextState = ThreadUtil.getString(this, "nextState", false, "");

		super.fillDispatcherParameter();
	}
}
