package com.crm.product.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.IDDServiceImpl;
import com.crm.subscriber.impl.SubscriberEntryImpl;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.fss.util.AppException;

public class SMSRoutingImpl extends OrderRoutingImpl
{
	public CommandMessage register(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		String responseCode = "";
		try
		{
			boolean confirmRegiter = IDDServiceImpl.isConfirmRegister(
					request.getIsdn(), request.getProductId()) != null;
			if (!confirmRegiter)
			{
				responseCode = "confirm.not-existed";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	public CommandMessage cancelConfirm(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		String responseCode = "";
		try
		{
			boolean confirmRegiter = IDDServiceImpl.isConfirmRegister(
					request.getIsdn(), request.getProductId()) != null;
			if (confirmRegiter)
			{
				IDDServiceImpl.removeConfirm(request.getIsdn(),
						request.getProductId());
				request.setCause("cancelConfirm.success");
				request.setStatus(Constants.ORDER_STATUS_DENIED);
			}
			else
			{
				responseCode = "confirm.not-existed";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);
			}
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	public CommandMessage unregister(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		SubscriberProduct subProduct = SubscriberProductImpl.getActive(
				request.getIsdn(), request.getProductId());

		String responseCode = "";
		try
		{
			if (!IDDServiceImpl.isActive(request.getProductId(),
					request.getIsdn()))
			{
				responseCode = "not-yet-register";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}

			Date registerDate = subProduct.getRegisterDate();

			String date = new SimpleDateFormat("yyyy-MM-dd")
					.format(registerDate);
			String currentDate = new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date());

			if (date.equals(currentDate) && request.isPostpaid())
			{
				responseCode = "unregister.postpaid";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		request.setResponse(responseCode);
		return request;
	}

	public CommandMessage confirmRegisterService(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		ProductEntry product = ProductFactory.getCache().getProduct(
				request.getProductId());

		String responseCode = "";

		try
		{
			String otherProductName = product.getParameter("OtherProduct", "");
			ProductEntry otherProduct = ProductFactory.getCache().getProduct(
					otherProductName);

			if (IDDServiceImpl.isActive(otherProduct.getProductId(),
					request.getIsdn()))
			{
				responseCode = "registered." + otherProduct.getAlias();
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}

			if (IDDServiceImpl.isActive(request.getProductId(),
					request.getIsdn()))
			{
				responseCode = "registered."
						+ (request.getSubscriberType() == Constants.PREPAID_SUB_TYPE ? "prepaid"
								: "postpaid");
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}
			else if (IDDServiceImpl.isRegistedBefore(request.getIsdn(),
					request.getProductId()))
			{
				responseCode = "registered.in-past";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}

			boolean confirmRegiter = IDDServiceImpl.isConfirmRegister(
					request.getIsdn(), request.getProductId()) != null;
			if (confirmRegiter)
			{
				responseCode = "register.not-confirm";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}

			request.setResponse(responseCode);
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	public CommandMessage renewService(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		ProductEntry product = ProductFactory.getCache().getProduct(
				request.getProductId());

		String responseCode = "";
		try
		{
			String otherProductName = product.getParameter("OtherProduct", "");
			ProductEntry otherProduct = ProductFactory.getCache().getProduct(
					otherProductName);

			if (IDDServiceImpl.isActive(otherProduct.getProductId(),
					request.getIsdn()))
			{
				responseCode = "registered." + otherProduct.getAlias();
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}
			
			if (IDDServiceImpl.isActive(request.getProductId(),
					request.getIsdn()))
			{
				responseCode = "registered."
						+ (request.getSubscriberType() == Constants.PREPAID_SUB_TYPE ? "prepaid"
								: "postpaid");
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}

			if (request.isPostpaid())
			{
				int maxRegister = Integer.parseInt(product.getParameter(
						"MaxRegister", "1"));

				if (IDDServiceImpl.checkMaxRegister(request.getProductId(),
						request.getIsdn(), maxRegister))
				{
					responseCode = "register.max";
					request.setResponseValue(ResponseUtil.SERVICE_AMOUNT,
							Integer.valueOf(maxRegister));
					
					request.setResponse(responseCode);
					request.setCause(responseCode);
					request.setStatus(Constants.ORDER_STATUS_DENIED);

					return request;
				}
			}

			if (!IDDServiceImpl.isRegistedBefore(request.getIsdn(),
					request.getProductId()))
			{
				responseCode = "giahan.is-first-time";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}

			request.getParameters().setProperty("PropertiesRenew", "true");
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	public CommandMessage confirmExtend(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		String responseCode = "";
		if (!IDDServiceImpl.isActive(request.getProductId(), request.getIsdn()))
		{
			responseCode = "not-existed";
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);

			return request;
		}

		if (!IDDServiceImpl.isConfirmExtend(request.getIsdn(),
				request.getProductId()))
		{
			responseCode = "not-confirm-extend";
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);

			return request;
		}

		try
		{
			boolean renewNow = IDDServiceImpl.isRenewNow(request.getIsdn(),
					request.getProductId());
			if (renewNow)
			{
				IDDServiceImpl.pendingNotify(request.getIsdn(), 1,
						request.getProductId());
			}
			else
			{
				IDDServiceImpl.pendingNotify(request.getIsdn(), 1,
						request.getProductId());
				
				responseCode = "cancel-confirm-extend";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);
			}
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	public CommandMessage cancelExtendIDD(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		String responseCode = "";
		if (!IDDServiceImpl.isActive(request.getProductId(), request.getIsdn()))
		{
			responseCode = "not-existed";
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);

			return request;
		}

		if (!IDDServiceImpl.isConfirmExtend(request.getIsdn(),
				request.getProductId()))
		{
			responseCode = "not-confirm-extend";
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);

			return request;
		}

		try
		{
			IDDServiceImpl.pendingNotify(request.getIsdn(), 2,
					request.getProductId());

			responseCode = "cancelExtend." + Constants.SUCCESS; 
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	public CommandMessage getIDDInstruction(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		ProductEntry product = ProductFactory.getCache().getProduct(
				request.getProductId());

		request.setResponse("get-intruction." + product.getAlias());
		request.setCause("get-intruction." + product.getAlias());
		request.setStatus(Constants.ORDER_STATUS_DENIED);

		return request;
	}

	public CommandMessage getIDDDestination(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		ProductEntry product = ProductFactory.getCache().getProduct(
				request.getProductId());

		request.setResponse("get-destination." + product.getAlias());
		request.setCause("get-destination." + product.getAlias());
		request.setStatus(Constants.ORDER_STATUS_DENIED);

		return request;
	}

	public CommandMessage searchVB220(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		request = parser(instance, orderRoute, request);

		SubscriberProduct subProduct = SubscriberProductImpl.getActive(
				request.getIsdn(), request.getProductId());

		String responseCode = "";
		try
		{
			if (IDDServiceImpl.isActive(request.getProductId(),
					request.getIsdn()))
			{
				if (request.isPostpaid())
				{
					if (request.getKeyword().toUpperCase().equals("TH VB220"))
					{
						responseCode = "postpaid-get-expired";
						request.setResponse(responseCode);
						request.setCause(responseCode);
						request.setResponseValue(
								ResponseUtil.SERVICE_EXPIRE_DATE,
								new SimpleDateFormat("yyyy/MM/dd")
										.format(subProduct.getExpirationDate()));
						request.setStatus(Constants.ORDER_STATUS_DENIED);

						return request;
					}
				}
				else
				{
					responseCode = "VB220.prepaid";
					request.setResponse(responseCode);
					request.setCause(responseCode);
					request.setStatus(Constants.ORDER_STATUS_DENIED);

					return request;
				}
			}
			else
			{
				responseCode = "VB220.not-registered";
				request.setResponse(responseCode);
				request.setCause(responseCode);
				request.setStatus(Constants.ORDER_STATUS_DENIED);

				return request;
			}
		}
		catch (Exception e)
		{
			responseCode = Constants.ERROR;
			request.setResponse(responseCode);
			request.setCause(responseCode);
			request.setStatus(Constants.ORDER_STATUS_DENIED);
		}

		return request;
	}

	@Override
	public CommandMessage parser(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		Exception error = null;

		ProductEntry product = null;

		SubscriberProduct subscriberProduct = null;

		try
		{
			// check SMS syntax
			if (order.getChannel().equals("SMS"))
			{
				smsParser(instance, orderRoute, order);
			}

			// check duplicate request
			if (orderRoute.getDuplicateScan() > 0)
			{
				checkDuplicate(instance, orderRoute, order);
			}

			// check product in available list
			product = ProductFactory.getCache()
					.getProduct(order.getProductId());

			// get current subscriber product
			if (!product.isSubscription())
			{

			}
			else if (order.getSubProductId() == Constants.DEFAULT_ID)
			{
				/**
				 * Edited: replaced getActive by getUnterminated (for barring
				 * subscription case)
				 */
				subscriberProduct = SubscriberProductImpl.getUnterminated(
						order.getIsdn(), order.getProductId());
			}
			else
			{
				subscriberProduct = SubscriberProductImpl.getProduct(order
						.getSubProductId());
			}

			if (order.getSubscriberType() == Constants.UNKNOW_SUB_TYPE)
			{
				order.setSubscriberType(SubscriberEntryImpl
						.getSubscriberType(order.getIsdn()));
			}

			order.setAmount(order.getQuantity() * order.getPrice());
			
			// check action type
			checkActionType(instance, orderRoute, product, order,
					subscriberProduct);

			if ((order.getStatus() == Constants.ORDER_STATUS_DENIED)
					&& order.getCause()
							.equals(Constants.ERROR_NOT_ENOUGH_MONEY))
			{
				if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION))
				{
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

					String expirationDate = dateFormat.format(subscriberProduct
							.getExpirationDate());
					String currentDate = dateFormat.format(new Date());

					if (expirationDate.compareTo(currentDate) < 0)
					{
						if (subscriberProduct.getSupplierStatus() == Constants.SUPPLIER_ACTIVE_STATUS)
						{
							order.setActionType(Constants.ACTION_SUPPLIER_DEACTIVE);

							order.setCause("");

							order.setStatus(Constants.ORDER_STATUS_PENDING);
						}
					}
					else
					{
						String graceDate = dateFormat.format(subscriberProduct
								.getGraceDate());

						if (graceDate.compareTo(currentDate) < 0)
						{
							order.setActionType(Constants.ACTION_CANCEL);

							order.setCause("");

							order.setStatus(Constants.ORDER_STATUS_PENDING);
						}
					}
				}
			}
			else
			{
				checkSubscriberType(instance, product, order);
			}

			if (order.getStatus() != Constants.ORDER_STATUS_DENIED)
			{
				validate(instance, orderRoute, order);
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

	public CommandMessage rejectInvalidTime(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		order.setStatus(Constants.ORDER_STATUS_DENIED);
		order.setCause(Constants.ERROR_OUT_OF_TIME);

		return order;
	}
}
