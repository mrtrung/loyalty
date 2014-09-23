/**
 * 
 */
package com.crm.product.impl;

import java.util.Calendar;
import java.util.Date;

import com.comverse_in.prepaid.ccws.SubscriberEntity;
import com.crm.kernel.index.ExecuteImpl;
import com.crm.kernel.message.Constants;
import com.crm.marketing.cache.CampaignEntry;
import com.crm.marketing.cache.CampaignFactory;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductPrice;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.impl.ccws.CCWSConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.provisioning.thread.OrderRoutingThread;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.SubscriberEntryImpl;
import com.crm.subscriber.impl.SubscriberOrderImpl;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.crm.thread.DispatcherInstance;
import com.crm.util.DateUtil;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

/**
 * @author ThangPV
 * 
 */
public class OrderRoutingImpl extends ExecuteImpl
{
	public void validate(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
	}

	public String formatResponse(DispatcherInstance instance,
			ProductEntry product, CommandMessage request, String actionType,
			String template)
	{
		return ResponseUtil.formatResponse(instance, product, request,
				actionType, template);
	}

	public void notifyOwner(DispatcherInstance instance,
			ProductRoute orderRoute, CommandMessage request)
	{
		ResponseUtil.notifyOwner(instance, orderRoute, request);
	}

	public void notifyDeliver(DispatcherInstance instance,
			ProductRoute orderRoute, CommandMessage request)
	{
		ResponseUtil.notifyDeliver(instance, orderRoute, request);
	}

	public void sendAdvertising(DispatcherInstance instance,
			ProductRoute orderRoute, CommandMessage request)
	{
		ResponseUtil.notifyAdvertising(instance, orderRoute, request);
	}

	public boolean processMessage(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		return true;
	}

	public boolean sendResponse(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		return true;
	}

	public void smsParser(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		try
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
		catch (Exception e)
		{
			throw new AppException(Constants.ERROR_INVALID_SYNTAX);
		}
	}

	public void checkDuplicate(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		if (SubscriberOrderImpl.isDuplicatedOrder(order.getIsdn(),
				order.getProductId(), order.getOrderDate(),
				orderRoute.getDuplicateScan()))
		{
			throw new AppException(Constants.ERROR_DUPLICATED);
		}
	}

	public void checkMaxRegister(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		int successOrder = SubscriberOrderImpl.getRegisteredOrder(
				order.getIsdn(), order.getProductId(), order.getOrderDate());

		if (successOrder >= orderRoute.getMaxRegisterDaily())
		{
			throw new AppException(Constants.ERROR_OVER_TRANSACTION_LIMIT);
		}
	}

	public void checkBlacklist(OrderRoutingInstance instance,
			ProductEntry product, CommandMessage order) throws Exception
	{

		SubscriberProduct subscriberProduct = null;

		for (int j = 0; (subscriberProduct == null)
					&& (j < product.getBlacklistProducts().length); j++)
		{
			int successOrder = SubscriberOrderImpl.getRegisteredOrder(
						order.getIsdn(), product.getBlacklistProducts()[j], order.getOrderDate());
			if (successOrder > 0)
			{
				throw new AppException(Constants.ERROR_BLACKLIST_PRODUCT);
			}
			else
			{
				subscriberProduct = SubscriberProductImpl.getActive(
						order.getIsdn(), product.getBlacklistProducts()[j]);
			}
		}

		if (subscriberProduct != null)
		{
			throw new AppException(Constants.ERROR_BLACKLIST_PRODUCT);
		}
	}

	public void checkUpgrade(OrderRoutingInstance instance,
			ProductEntry product, CommandMessage order) throws Exception
	{
		SubscriberProduct subscriberProduct = null;

		if (product.getUpgradeProducts().length > 0)
		{
			for (int j = 0; (subscriberProduct == null)
					&& (j < product.getUpgradeProducts().length); j++)
			{
				long productId = product.getUpgradeProducts()[j];

				subscriberProduct = SubscriberProductImpl.getActive(
						order.getSubscriberId(), productId);

				if (subscriberProduct != null)
				{
					order.setAssociateProductId(subscriberProduct
							.getProductId());
				}
				else
				{
					subscriberProduct = SubscriberProductImpl.getActiveX(
							order.getIsdn(), productId, order.getOrderDate());

					if (subscriberProduct != null)
					{
						order.setAssociateProductId(subscriberProduct
								.getProductId());
					}
				}
			}
		}
	}

	public void checkSubscriberType(OrderRoutingInstance instance,
			ProductEntry product, CommandMessage order) throws Exception
	{
		if (product.getSubscriberTypes().length > 0)
		{
			for (int j = 0; j < product.getSubscriberTypes().length; j++)
			{
				if (product.getSubscriberTypes()[j] == order
						.getSubscriberType())
				{
					return;
				}
			}

			throw new AppException(Constants.ERROR_DENIED_SUBSCRIBER_TYPE);
		}

		return;
	}

	// //////////////////////////////////////////////////////
	// check class of service and compare with list of denied COS
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public String getSubscriberState(OrderRoutingInstance instance,
			ProductRoute orderRoute, ProductEntry product,
			CommandMessage request) throws Exception
	{
		return "";
	}

	// //////////////////////////////////////////////////////
	// check class of service and compare with list of denied COS
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public String getSubscriberCOS(OrderRoutingInstance instance,
			ProductRoute orderRoute, ProductEntry product,
			CommandMessage request) throws Exception
	{
		CCWSConnection connection = null;
		String cosName = "";
		try
		{
			connection = (CCWSConnection) instance.getProvisioningConnection();
			SubscriberEntity subscriberEntity = connection
					.getSubscriberInfor(request.getIsdn());
			cosName = subscriberEntity.getCOSName();
		}
		catch (Exception e)
		{

		}
		finally
		{
			instance.closeProvisioningConnection(connection);
		}
		return cosName;
	}

	// //////////////////////////////////////////////////////
	// check class of service and compare with list of denied COS
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void validateState(OrderRoutingInstance instance,
			ProductRoute orderRoute, ProductEntry product,
			CommandMessage request) throws Exception
	{
		try
		{
			String currentState = getSubscriberState(instance, orderRoute,
					product, request);

			if (!currentState.equals("") && product.getAvailStatus().length > 0)
			{
				boolean found = false;

				for (int j = 0; !found && (j < product.getAvailStatus().length); j++)
				{
					String status = product.getAvailStatus()[j];

					found = status.equals(currentState);
				}

				if (!found)
				{
					throw new AppException(Constants.ERROR_DENIED_STATUS);
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// check class of service and compare with list of denied COS
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void validateCOS(OrderRoutingInstance instance,
			ProductRoute orderRoute, ProductEntry product,
			CommandMessage request) throws Exception
	{
		try
		{
			String currentCOS = getSubscriberCOS(instance, orderRoute, product,
					request);

			if (!currentCOS.equals("") && product.getAvailCOS().length > 0)
			{
				boolean found = false;

				for (int j = 0; !found && (j < product.getAvailCOS().length); j++)
				{
					String cos = product.getAvailCOS()[j];

					found = cos.equals(currentCOS);
				}

				if (!found)
				{
					throw new AppException(Constants.ERROR_DENIED_COS);
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// check class of service and compare with list of denied COS
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void validateBalance(OrderRoutingInstance instance,
			ProductRoute orderRoute, ProductEntry product,
			CommandMessage request, String balanceName, double balanceAmount,
			Date accountExpiration) throws Exception
	{
		try
		{
			if (balanceAmount < product.getMinBalance())
			{
				throw new AppException(Constants.ERROR_NOT_ENOUGH_MONEY);
			}
			else if ((product.getMaxBalance() > 0)
					&& (balanceAmount > product.getMaxBalance()))
			{
				throw new AppException(Constants.ERROR_BALANCE_TOO_LARGE);
			}
			else
			{
				Calendar calendar = Calendar.getInstance();

				calendar.setTime(new Date());

				calendar.add(Calendar.DATE, product.getMaxExpirationDays());

				if (calendar.after(accountExpiration))
				{
					throw new AppException(Constants.ERROR_EXPIRE_TOO_LARGE);
				}
			}

			// set default price
			request.setOfferPrice(product.getPrice());

			ProductPrice productPrice = product.getProductPrice(
					request.getChannel(), request.getActionType(),
					request.getSegmentId(), request.getAssociateProductId(),
					request.getQuantity(), request.getOrderDate());

			int quantity = 1;
			double fullOfCharge = product.getPrice();
			double baseOfCharge = product.getPrice();

			if (productPrice != null)
			{
				fullOfCharge = productPrice.getFullOfCharge();
				baseOfCharge = productPrice.getBaseOfCharge();
			}

			if (balanceAmount >= fullOfCharge)
			{
				request.setPrice(fullOfCharge);
				request.setFullOfCharge(true);
			}
			else if (balanceAmount < baseOfCharge)
			{
				throw new AppException(Constants.ERROR_NOT_ENOUGH_MONEY);
			}
			else if (orderRoute.isBaseChargeEnable())
			{
				request.setFullOfCharge(false);
				request.setPrice(baseOfCharge);

				quantity = (int) (balanceAmount / request.getPrice());

				if (quantity == 0)
				{
					throw new AppException(Constants.ERROR_NOT_ENOUGH_MONEY);
				}
			}
			else
			{
				throw new AppException(Constants.ERROR_NOT_ENOUGH_MONEY);
			}

			request.setQuantity(quantity);
			request.setAmount(request.getPrice() * request.getQuantity());
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public CommandMessage checkBalance(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		try
		{
			order.setSubscriberType(SubscriberEntryImpl.getSubscriberType(order
					.getIsdn()));
		}
		catch (Exception e)
		{
			throw e;
		}

		return order;
	}

	/**
	 * Check actionType<br>
	 * Modified by NamTA<br>
	 * Modified Date 25/09/2012<br>
	 * Enable topup
	 * 
	 * @param instance
	 * @param orderRoute
	 * @param product
	 * @param order
	 * @param subscriberProduct
	 * @throws Exception
	 */
	public void checkActionType(OrderRoutingInstance instance,
			ProductRoute orderRoute, ProductEntry product,
			CommandMessage order, SubscriberProduct subscriberProduct)
			throws Exception
	{
		Date now = new Date();

		order.setRequestValue("first-action-type", order.getActionType());
		try
		{
			if (subscriberProduct != null)
			{
				int remainDays = 0;
				if (subscriberProduct.getExpirationDate() != null)
				{
					remainDays = DateUtil.getDateDiff(now,
							subscriberProduct.getExpirationDate());
				}
				if (remainDays < 0)
				{
					remainDays = 0;
				}

				order.setResponseValue("service.activeDays", remainDays);
				if (subscriberProduct.getExpirationDate() != null)
				{
					order.setResponseValue("service.activeDate",
							subscriberProduct.getExpirationDate());
				}
			}

			if (subscriberProduct != null)
			{
				order.setSubProductId(subscriberProduct.getSubProductId());
			}

			String actionType = order.getActionType();

			if (actionType.equals(Constants.ACTION_REGISTER)
					&& (subscriberProduct != null))
			{

				/**
				 * Check if isTopupEnable() and (subscriberproduct.isBarring or
				 * subscriberProduct.expirationDate < sysDate)
				 */
				if (orderRoute.isTopupEnable() &&
						(subscriberProduct.isBarring() || subscriberProduct.getExpirationDate().before(new Date())))
				{
					actionType = Constants.ACTION_TOPUP;
				}
				else
				{
					throw new AppException(Constants.ERROR_REGISTERED);
				}
			}

			if (product.isSubscription())
			{
				if ((subscriberProduct == null) || subscriberProduct.isCancel())
				{
					if (actionType.equals(Constants.ACTION_SUBSCRIPTION))
					{
						throw new AppException(
								Constants.ERROR_SUBSCRIPTION_NOT_FOUND);
					}
					else if (actionType.equals(Constants.ACTION_UNREGISTER))
					{
						throw new AppException(
								Constants.ERROR_UNREGISTERED);
					}
					else if (actionType.equals(Constants.ACTION_CANCEL))
					{
						throw new AppException(
								Constants.ERROR_SUBSCRIPTION_NOT_FOUND);
					}
					else if (actionType.equals(Constants.ACTION_TOPUP))
					{
						actionType = Constants.ACTION_REGISTER;
					}
				}
				else if (subscriberProduct != null)
				{
					if (orderRoute.isTopupEnable()
							&& subscriberProduct.isBarring())
					{
						actionType = Constants.ACTION_TOPUP;
					}
					else if (actionType
								.equals(Constants.ACTION_SUBSCRIPTION))
					{
						if (DateUtil.compareDate(
									subscriberProduct.getExpirationDate(), now) >= 0)
						{
							throw new AppException(
										Constants.ERROR_REGISTERED);
						}
					}
				}
			}

			// get associate product
			if (actionType.equals(Constants.ACTION_REGISTER)
					|| actionType.equals(Constants.ACTION_UPGRADE)
					|| actionType.equals(Constants.ACTION_ADVERTISING))
			{
				checkBlacklist(instance, product, order);

				checkUpgrade(instance, product, order);

				if (order.getAssociateProductId() != Constants.DEFAULT_ID)
				{
					actionType = Constants.ACTION_UPGRADE;
				}
			}

			order.setActionType(actionType);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public void checkPromotion(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		CampaignEntry campaign = null;

		try
		{
			// String campaignCode =
			// SubscriberCampaignImpl.getCampaignCode(order.getProductId());
			//
			// if (!campaignCode.equals(""))
			// {
			ProductEntry product = ProductFactory.getCache().getProduct(orderRoute.getProductId());
			campaign = CampaignFactory.getCache().getCampaign(product.getAlias());

			if (campaign != null)
			{
				order.setCampaignId(campaign.getCampaignId());
				order.setSegmentId(campaign.getSegmentId());
			}
			// }
		}
		catch (Exception e)
		{
			instance.logMonitor(e);

			instance.logMonitor(order);
		}
	}

	public CommandMessage parser(OrderRoutingInstance instance,
			ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		Exception error = null;

		ProductEntry product = null;

		SubscriberProduct subscriberProduct = null;

		try
		{
			// check SMS syntax
			Date startTime = new Date();
			Date endTime = new Date();

			/**
			 * Should check for both SMS & web.
			 */
			smsParser(instance, orderRoute, order);

			// check duplicate request
			if (orderRoute.getDuplicateScan() > 0)
			{
				startTime = new Date();
				checkDuplicate(instance, orderRoute, order);

				endTime = new Date();
				instance.debugMonitor("Check duplicate(" + order.getIsdn() + ") cost time: "
						+ (endTime.getTime() - startTime.getTime()) + "ms");
			}

			if (orderRoute.getMaxRegisterDaily() > 0)
			{
				startTime = new Date();
				checkMaxRegister(instance, orderRoute, order);

				endTime = new Date();
				instance.debugMonitor("Check maxregisterdaily(" + order.getIsdn() + ")  cost time: "
						+ (endTime.getTime() - startTime.getTime()) + "ms");
			}
			// check promotion
			if (orderRoute.isCheckPromotion())
			{
				checkPromotion(instance, orderRoute, order);
			}

			// check product in available list
			product = ProductFactory.getCache()
					.getProduct(order.getProductId());

			// get current subscriber product
			subscriberProduct = SubscriberProductImpl.getProduct(order
					.getSubProductId());
			
			/*if (!product.isSubscription())
			{
				
			}
			else */
				if (order.getSubProductId() == Constants.DEFAULT_ID)
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

			// check action type
			checkActionType(instance, orderRoute, product, order,
					subscriberProduct);

			// validate
			if (orderRoute.isCheckBalance())
			{
				order = checkBalance(instance, orderRoute, order);
			}
			else
			{
				order.setAmount(order.getQuantity() * order.getPrice());
			}

			// Set subscriber type
			if (order.getSubscriberType() == Constants.UNKNOW_SUB_TYPE)
			{
				order.setSubscriberType(SubscriberEntryImpl
						.getSubscriberType(order.getIsdn()));
			}

			/**
			 * Check if sub type is supported or not
			 */
			if (order.getSubscriberType() == Constants.PREPAID_SUB_TYPE)
			{
				String unsupported = orderRoute.getParameter("unsupport.prepaid", "false");
				if (unsupported.trim().toLowerCase().equals("true"))
					throw new AppException(Constants.ERROR_UNSUPPORT_PREPAID);
			}
			else if (order.getSubscriberType() == Constants.POSTPAID_SUB_TYPE)
			{
				String unsupported = orderRoute.getParameter("unsupport.postpaid", "false");
				if (unsupported.trim().toLowerCase().equals("true"))
					throw new AppException(Constants.ERROR_UNSUPPORT_POSTPAID);
			}

			// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDate = new Date();

			if ((order.getStatus() == Constants.ORDER_STATUS_DENIED)
					&& order.getCause()
							.equals(Constants.ERROR_NOT_ENOUGH_MONEY))
			{
				if (order.getActionType().equals(Constants.ACTION_TOPUP))
				{
					if (order.getRequestValue("first-action-type", "").equals(Constants.ACTION_SUBSCRIPTION))
					{
						order.setActionType(Constants.ACTION_SUBSCRIPTION);
					}
				}
				// Duymb add Autorenew for MAXI 24
				if (order.getActionType().equals(Constants.ACTION_AUTORENEW))
				{
					order.setActionType(Constants.ACTION_UNRENEW);
					order.setCause("");
					order.setStatus(Constants.ORDER_STATUS_PENDING);					
				}
				// DuyMB add end.
				if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION))
				{
					if (subscriberProduct
							.getExpirationDate().before(currentDate))
					{
						if (subscriberProduct.getSupplierStatus() == Constants.SUPPLIER_ACTIVE_STATUS)
						{
							order.setActionType(Constants.ACTION_SUPPLIER_DEACTIVE);

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

			if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION)
					|| order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE))
			{
				if (subscriberProduct == null)
					throw new AppException(Constants.ERROR_SUBSCRIPTION_NOT_FOUND);

				boolean subscriptionNeverExpire = product.getParameters().getBoolean("subscription.neverExpire", false);

				/**
				 * <code>
				 * if subscription.neverExpire=true
				 * {
				 * 		case graceDate != null && graceDate < currentDate && actionType is de-active
				 * 			cancel subscription;
				 * 		case graceDate != null && graceDate < currentDate && is barring
				 * 			cancel subscription;
				 * 		case graceDate == null && actionType is de-active
				 * 			cancel subscription;
				 * }
				 * </code>
				 */
				if (!subscriptionNeverExpire && ((subscriberProduct.getGraceDate() == null
						&& order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE))
						|| (subscriberProduct.getGraceDate() != null
								&& subscriberProduct.getGraceDate().before(currentDate)
								&& (order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE)
										|| subscriberProduct.isBarring())
										)))
				{
					order.setActionType(Constants.ACTION_CANCEL);

					order.setCause("");

					order.setStatus(Constants.ORDER_STATUS_PENDING);
				}
				else if (subscriberProduct.isBarring() && !orderRoute.isTopupEnable()
						&& order.getActionType().equals(Constants.ACTION_SUBSCRIPTION))
				{
					order.setCause(Constants.ERROR_REGISTERED);

					order.setStatus(Constants.ORDER_STATUS_DENIED);
				}
			}
			
			if (order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE)
					&& subscriberProduct.isBarring())
			{
				order.setCause(Constants.ERROR_SUBSCRIPTION_NOT_FOUND);
				order.setDescription("Is already suspended.");
				order.setStatus(Constants.ORDER_STATUS_DENIED);
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

		/**
		 * In case of subscription (or de-active) <br/>
		 * Unregister subscription if subscriber is Retired(S3) <br />
		 */
		if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION)
				|| order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE))
		{
			String currentState = "";
			try
			{
				currentState = getSubscriberState(instance, orderRoute, product, order);
			}
			catch (Exception e)
			{

			}

			if (currentState.equals(Constants.BALANCE_STATE_RETIRED_S3))
			{
				order.setActionType(Constants.ACTION_CANCEL);
				order.setDescription(order.getCause());
				order.setCause("");
				/**
				 * unregister for all subtype = prepaid subtype
				 */
				order.setSubscriberType(Constants.PREPAID_SUB_TYPE);
				order.setStatus(Constants.ORDER_STATUS_PENDING);
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

	public void simulation(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order)
			throws InterruptedException, AppException
	{
		long executeTime = ((OrderRoutingThread) instance.getDispatcher()).simulationTime;
		String cause = ((OrderRoutingThread) instance.getDispatcher()).simulationCause;
		instance.debugMonitor("Simulation execute time: " + executeTime + "ms");
		Thread.sleep(executeTime);
		order.setSubscriberType(Constants.PREPAID_SUB_TYPE);
		order.setCause(cause);
	}
}
