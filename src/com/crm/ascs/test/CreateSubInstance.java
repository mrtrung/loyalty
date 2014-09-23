package com.crm.ascs.test;

public class CreateSubInstance extends SubscriberTestInstance
{

	public CreateSubInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public CreateSubThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (CreateSubThread) super.getDispatcher();
	}
	
	@Override
	protected RTBSMessage processIsdn(String isdn) throws Exception
	{
		RTBSMessage message = new RTBSMessage(isdn, RTBSMessage.ACTION_CREATE_SUB);
		message.setCosName(getDispatcher().cosName);

		return message;
	}
}
