package com.crm.product.thread.subscription;

import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;

public class MaxSMSPromotionSubscription extends AbstractSubscription
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

		subscriptionThread.attachQueue(order);

		// subscriptionThread.attachQueue(order);

		// subscriptionThread.stmtInsert.setString(1, deliveryUser);
		// subscriptionThread.stmtInsert.setString(2, chanel);
		// subscriptionThread.stmtInsert.setString(3, serviceAddress);
		// subscriptionThread.stmtInsert.setString(4, isdn);
		// subscriptionThread.stmtInsert.setString(5, keyword);
		// subscriptionThread.stmtInsert.addBatch();
		//
		// subscriptionThread.checkInsertBatch = true;
	}
}
