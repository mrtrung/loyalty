package com.crm.ascs.test;

import java.util.Vector;

import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class CreateSubThread extends SubscriberTestThread
{
	public String	cosName	= "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("cosName", 400, "New subscriber COS name."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		cosName = ThreadUtil.getString(this, "cosName", false, "");

		super.fillDispatcherParameter();
	}

}
