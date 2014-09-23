/**
 * 
 */
package com.crm.product.impl;

import com.crm.kernel.message.Constants;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.subscriber.impl.SubscriberCampaignImpl;

import com.fss.util.AppException;

/**
 * @author ThangPV
 * 
 */
public class HelpOrderRoutingImpl extends OrderRoutingImpl
{
	public CommandMessage parser(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		Exception error = null;

		try
		{
			// check SMS syntax
			if (order.getChannel().equals("SMS"))
			{
				smsParser(instance, orderRoute, order);
			}

			if (order.getStatus() != Constants.ORDER_STATUS_DENIED)
			{
				order.setCause(orderRoute.getKeyword());
				
				order.setStatus(Constants.ORDER_STATUS_APPROVED);

				SubscriberCampaignImpl.createCampaignEvent(
						order.getUserId(), order.getUserName(), order.getOrderId(), order.getOrderDate()
						, order.getSubscriberId(), order.getSubProductId(), order.getIsdn(), order.getSubscriberType()
						, order.getProductId(), order.getServiceAddress(), order.getKeyword()
						, order.getCampaignId(), order.getSegmentId(), null, Constants.DEFAULT_ID
						, "", Constants.ORDER_STATUS_PENDING);
			}
		}
		catch (Exception e)
		{
			error = e;
		}

		if (error != null)
		{
			order.setStatus(Constants.ORDER_STATUS_DENIED);

			if (error instanceof AppException)
			{
				order.setCause(error.getMessage());
			}
			else
			{
				order.setDescription(error.getMessage());
			}
		}

		if ((error != null) && !(error instanceof AppException))
		{
			throw error;
		}

		return order;
	}
}
