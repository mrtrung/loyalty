package com.crm.ascs.test;

public class ChangeSubStateInstance extends SubscriberTestInstance
{

	public ChangeSubStateInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public ChangeSubStateThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (ChangeSubStateThread) super.getDispatcher();
	}

	@Override
	protected RTBSMessage processIsdn(String isdn) throws Exception
	{
		RTBSMessage message = new RTBSMessage(isdn, RTBSMessage.ACTION_CHANGE_STATE_SUB);
		message.setState(getDispatcher().nextState);

		return message;
	}
}
