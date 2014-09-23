package com.crm.product.thread.autorenew;

import com.crm.kernel.message.Constants;
import com.crm.provisioning.message.CommandMessage;

public class MaxSMSAutoRenew extends AbstractAutoRenew
{

	@Override
	public void processRecord(String isdn, CommandMessage order) throws Exception
	{
		order.setChannel(chanel);

		if (chanel.equals(Constants.CHANNEL_SMS))
		{
			order.setProvisioningType("SMSC");
		}

		order.setUserId(0);
		if (deliveryUser.equals(""))
			order.setUserName("system");
		else
			order.setUserName(deliveryUser);

		order.setServiceAddress(serviceAddress);
		order.setIsdn(isdn);
		order.setTimeout(orderTimeOut);
		order.setKeyword(keyword);

		abtractAutoRenewThread.attachQueue(order);
	}

}
