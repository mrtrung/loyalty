/**
 * 
 */
package com.crm.product.impl;

import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;

/**
 * @author ThangPV<br>
 */
public class Biz30OrderRoutingImpl extends VNMOrderRoutingImpl
{
	@Override
	public CommandMessage parser(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		// TODO Auto-generated method stub
		order = super.parser(instance, orderRoute, order);
		order.getParameters().setBoolean("includeCurrentDay", true);
		return order;
	}
}
