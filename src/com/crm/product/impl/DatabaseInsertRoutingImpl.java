package com.crm.product.impl;

import com.crm.kernel.message.Constants;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class DatabaseInsertRoutingImpl extends OrderRoutingImpl
{

	@Override
	public void smsParser(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		try
		{
			String strVerifyKeyword = orderRoute.getParameter("verifyKeyword", "false");
			if (strVerifyKeyword.equals(Constants.VERIFY_KEYWORD))
			{
				// Not prefix
				if (!orderRoute.isWildcard())
				{
					order.setKeyword(order.getKeyword().toUpperCase());

					// remove twice space
					String smsContent = "";

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
						throw new AppException(Constants.ERROR_INVALID_SYNTAX);
					}
					if ((orderRoute.getSmsMinParams() > 0)
							&& (arrParams.length < orderRoute.getSmsMinParams()))
					{
						throw new AppException(Constants.ERROR_INVALID_SYNTAX);
					}

					// update SMS option parameter
					order.getParameters().setString("sms.params.count",
							String.valueOf(arrParams.length));

					for (int j = 0; j < arrParams.length; j++)
					{
						order.getParameters().setString("sms.params[" + j + "]",
								arrParams[j]);
					}
				}
				// Prefix
				else
				{
					order.setKeyword(order.getKeyword().toUpperCase());
					if (!order.getKeyword().substring(0, orderRoute.getKeyword().length()).equals(orderRoute.getKeyword()))
					{
						throw new AppException(Constants.ERROR_INVALID_SYNTAX);
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new AppException(Constants.ERROR_INVALID_SYNTAX);
		}
	}
}
