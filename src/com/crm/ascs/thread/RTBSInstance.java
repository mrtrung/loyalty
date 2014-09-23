package com.crm.ascs.thread;

import com.crm.ascs.ccws.CCWSConnection;
import com.crm.thread.DispatcherInstance;

;

public class RTBSInstance extends DispatcherInstance
{

	public RTBSInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public RTBSThread getDispatcher()
	{
		return (RTBSThread) super.getDispatcher();
	}

	public CCWSConnection getCCWSConnection() throws Exception
	{
		return getDispatcher().getCCWSConnection();
	}

	public void returnCCWSConnection(CCWSConnection connection) throws Exception
	{
		getDispatcher().returnCCWSConnection(connection);
	}
}
