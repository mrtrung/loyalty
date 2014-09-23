package com.crm.ascs.test;

public class ChargeSubInstance extends SubscriberTestInstance
{

	public ChargeSubInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ChargeSubThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (ChargeSubThread)super.getDispatcher();
	}
	
	@Override
	protected RTBSMessage processIsdn(String isdn) throws Exception
	{
		RTBSMessage message = new RTBSMessage(isdn, RTBSMessage.ACTION_CHARGE_SUB);
		message.setFaceValue(getDispatcher().chargeValue);
		message.setExpirationOffset(getDispatcher().chargeDay);
		message.setDescription(getDispatcher().chargeComment);

		return message;
	}

}
