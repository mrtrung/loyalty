package com.crm.ascs.test;

import java.util.Vector;

import com.crm.thread.DispatcherThread;
import com.fss.util.AppException;
import com.crm.thread.util.ThreadUtil;

public class ASCSTestThread extends DispatcherThread
{
	public String	host			= "";
	public int		port			= 2222;
	public String	content			= "";
	public int		batchSize		= 100;
	public long		timeBetweenLoop	= 100;
	public long		sendTotal		= 10000;

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		// TODO Auto-generated method stub
		super.fillDispatcherParameter();
		host = ThreadUtil.getString(this, "Host", false, "127.0.0.1");
		port = ThreadUtil.getInt(this, "Port", 2222);
		content = ThreadUtil.getString(this, "Content", false, "Trigger content");
		batchSize = ThreadUtil.getInt(this, "batchSize", 100);
		timeBetweenLoop = ThreadUtil.getInt(this, "timeBetweenLoop", 100);
		sendTotal = ThreadUtil.getInt(this, "sendTotal", 10000);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("Content", 1000, ""));
		vtReturn.add(ThreadUtil.createTextParameter("Host", 100, ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("Port", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("batchSize", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("timeBetweenLoop", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("sendTotal", ""));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}
}
