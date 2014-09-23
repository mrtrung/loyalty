package com.crm.ascs.test;

import java.util.Vector;

import com.crm.ascs.thread.RTBSThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class SubModifyThread extends RTBSThread
{
	public String	spName	= "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("spName", 400, "SPName."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		spName = ThreadUtil.getString(this, "spName", false, "");

		super.fillDispatcherParameter();
	}
}
