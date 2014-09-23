package com.crm.product.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.SubscriberOrderImpl;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.fss.util.AppException;

public class NewF5OrderRoutingImpl extends F5OrderRoutingImpl
{
	@Override
	public void smsParser(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		// TODO Auto-generated method stub

		// if (order.getActionType().equals(Constants.ACTION_ADD_MEMBER)
		// || order.getActionType().equals(Constants.ACTION_REGISTER))
		// {
		// ProductEntry product =
		// ProductFactory.getCache().getProduct(orderRoute.getProductId());
		//
		// if (product != null)
		// {
		// String isdnTable = product.getParameter("isdnTable", "NewG5Isdn");
		//
		// SubscriberOrderImpl.validateIsdn(order.getIsdn(), isdnTable);
		// }
		// }
		
		super.smsParser(instance, orderRoute, order);
	}

	@Override
	public CommandMessage checkBalance(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order)
			throws Exception
	{
		try
		{
			if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION)
					|| order.getActionType().equals(Constants.ACTION_TOPUP))
					//|| order.getActionType().equals(Constants.ACTION_REGISTER))
			{
				ProductEntry product = ProductFactory.getCache().getProduct(orderRoute.getProductId());

				if (product == null)
				{
					throw new AppException(Constants.ERROR_PRODUCT_NOT_FOUND);
				}
				Calendar startTime = Calendar.getInstance();

				if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION)
						|| order.getActionType().equals(Constants.ACTION_TOPUP))
				{
					SubscriberProduct subProduct = null;
					if (order.getSubProductId() != Constants.DEFAULT_ID)
					{
						subProduct = SubscriberProductImpl.getProduct(order.getSubProductId());
					}

					if (subProduct == null)
					{
						throw new AppException(Constants.ERROR_SUBSCRIPTION_NOT_FOUND);
					}

					/**
					 * Check topup date = current expirationDate - subscription
					 * time
					 */

					startTime.setTime(subProduct.getExpirationDate());
				}

				startTime.add(Calendar.DAY_OF_MONTH, (-1) * product.getSubscriptionPeriod());

				Date startDate = startTime.getTime();

				String rechargeTable = product.getParameter("rechargeTable", "ASCS.RECHARGE_TRIGGER");
				double rechargeMinAmount = product.getParameters().getDouble("rechargeMinAmount", 30000);

				double rechargeAmount = SubscriberOrderImpl.getChargingMoney(order.getIsdn(), rechargeTable, startDate);

				String strStartDate = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(startDate);
				instance.debugMonitor("Total rechargeAmount from " + strStartDate + " is " + rechargeAmount
						+ ", require " + rechargeMinAmount);
				if (rechargeAmount < rechargeMinAmount)
					throw new AppException(Constants.ERROR_NOT_ENOUGH_MONEY);
			}

			return super.checkBalance(instance, orderRoute, order);
		}
		catch (AppException e)
		{
			order.setCause(e.getMessage());
			order.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return order;
	}

	@Override
	public void validate(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		if (!order.getShipTo().equals("") && order.getActionType().equals(Constants.ACTION_ADD_MEMBER))
		// && !order.getActionType().equals(Constants.ACTION_UNREGISTER)
		// && !order.getActionType().equals(Constants.ACTION_REGISTER)
		// && !order.getActionType().equals(Constants.ACTION_CANCEL)
		// && !order.getActionType().equals(Constants.ACTION_REMOVE_MEMBER))
		{
			if (order.getIsdn().equals(order.getShipTo()))
			{
				throw new AppException(Constants.ERROR_INVALID_DELIVER);
			}
			String[] deliverIsdns = order.getShipTo().split(",");

			for (String deliverIsdn : deliverIsdns)
			{
				if (order.getIsdn().equals(deliverIsdn))
					throw new AppException(Constants.ERROR_INVALID_DELIVER);
			}
		}
	}
}
