package com.crm.ascs.test;

public class DeleteSubInstance extends SubscriberTestInstance
{

	public DeleteSubInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public SubscriberTestThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return super.getDispatcher();
	}
	
	@Override
	protected RTBSMessage processIsdn(String isdn) throws Exception
	{
		return new RTBSMessage(isdn, RTBSMessage.ACTION_DELETE_SUB);
	}
}
