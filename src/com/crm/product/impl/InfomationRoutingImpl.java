package com.crm.product.impl;

import com.crm.kernel.message.Constants;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.message.VNMMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.provisioning.util.CommandUtil;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class InfomationRoutingImpl extends VNMOrderRoutingImpl
{

	public void smsParser(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		try
		{
			order.setKeyword(order.getKeyword().toUpperCase());

			// remove twice space
			String smsContent = "";
			String invalidResponseContent = orderRoute.getParameter("ResponseInvalidContent", "");
			String validResponseContent = orderRoute.getParameter("ResponseValidContent", "");

			if (!order.getRequest().equals(""))
			{
				smsContent = order.getRequest().toString();
			}
			else
			{
				smsContent = order.getKeyword();
			}

			smsContent = smsContent.trim();

			while (smsContent.indexOf("  ") >= 0)
			{
				smsContent = smsContent.replaceAll("  ", " ");
			}

			// SMS parser

			// Properties parameters = order.getParameters();
			if (smsContent.length() >= orderRoute.getKeyword().length())
			{
				smsContent = smsContent.substring(
						orderRoute.getKeyword().length()).trim();
			}

			String[] arrParams = StringUtil.toStringArray(smsContent, " ");

			// use default number if value of the parameter is wrong.
			if ((orderRoute.getSmsMaxParams() >= 0)
					&& (arrParams.length > orderRoute.getSmsMaxParams()))
			{
				CommandUtil.sendSMS(instance, order, invalidResponseContent);
				throw new AppException(Constants.ERROR_INVALID_SYNTAX);
			}
			if ((orderRoute.getSmsMinParams() > 0)
					&& (arrParams.length < orderRoute.getSmsMinParams()))
			{
				CommandUtil.sendSMS(instance, order, invalidResponseContent);
				throw new AppException(Constants.ERROR_INVALID_SYNTAX);
			}

			CommandUtil.sendSMS(instance, order, validResponseContent);

			order.setCause(Constants.SUCCESS);
		}
		catch (Exception e)
		{
			throw new AppException(Constants.ERROR_INVALID_SYNTAX);
		}
	}

	public VNMMessage nextCommand(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception
	{
		VNMMessage result = CommandUtil.createVNMMessage(request);
		return (VNMMessage) result;
	}
}
