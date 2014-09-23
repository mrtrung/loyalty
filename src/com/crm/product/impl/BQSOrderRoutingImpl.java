package com.crm.product.impl;

import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;

public class BQSOrderRoutingImpl extends OrderRoutingImpl
{
	@Override
	public CommandMessage parser(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{

		CommandMessage result = super.parser(instance, orderRoute, order);
		if (result == null)
			result = order;

		if (!result.getCause().equals("") && !result.getCause().equals("success"))
		{
			orderRoute.getExecuteImpl().notifyOwner(instance, orderRoute, result);
		}

		return result;
	}
}
