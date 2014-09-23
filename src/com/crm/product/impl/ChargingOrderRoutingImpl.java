/**
 * 
 */
package com.crm.product.impl;

import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.SubscriberEntryImpl;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.fss.util.AppException;

/**
 * @author hungdt
 *
 */
public class ChargingOrderRoutingImpl extends VNMOrderRoutingImpl {
	
	
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
			
			
			//check for cgw
			String cgw_action = orderRoute.getParameter("charggw.chargtype", "");
			if(cgw_action.equals(Constants.CGW_ACTION_ONLINE))
			{
				
				if(order.getCgwStatus().equals(Constants.CGW_STATUS_D))
				{
					if(order.getSubscriberType() == 1)
					{
						order.setActionType("online-prepaid-d");
					}
				}
				else if (order.getCgwStatus().equals(Constants.CGW_STATUS_U))
				{
					if(order.getSubscriberType() == 1)
					{
						order.setActionType("online-prepaid-u");
					}
				}
			}
			else if (cgw_action.equals(Constants.CGW_ACTION_OFFLINE))
			{
				if(order.getCgwStatus().equals(Constants.CGW_STATUS_D))
				{
					
				}
				else if (order.getCgwStatus().equals(Constants.CGW_STATUS_U))
				{
					if(order.getSubscriberType() == 1)
					{
						order.setActionType("offline-prepaid-u");
					}
					else if(order.getSubscriberType() == 2)
					{
						order.setActionType("offline-postpaid-u");
					}
				}
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

				/**
				 *
				 */
				if ((subscriberProduct.getGraceDate() == null
						&& order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE))
						|| (subscriberProduct.getGraceDate() != null
								&& subscriberProduct.getGraceDate().before(currentDate)
								&& (order.getActionType().equals(Constants.ACTION_SUPPLIER_DEACTIVE)
										|| subscriberProduct.isBarring())
										))
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
		 * In case of subscription <br/>
		 * Unregister subscription if subscriber is Retired(S3) <br />
		 * Or if subscriber can not validate and current date > grace date
		 */
		if (order.getActionType().equals(Constants.ACTION_SUBSCRIPTION))
		{
			if (subscriberProduct == null)
			{
				order.setStatus(Constants.ORDER_STATUS_DENIED);
				order.setCause(Constants.ERROR_SUBSCRIPTION_NOT_FOUND);
			}

			String currentState = "";
			Date currentDate = new Date();
			try
			{
				currentState = getSubscriberState(instance, orderRoute, product, order);
			}
			catch (Exception e)
			{

			}

			if (currentState.equals(Constants.BALANCE_STATE_RETIRED_S3)
					|| order.getSubscriberType() == Constants.UNKNOW_SUB_TYPE
					|| ((subscriberProduct.getGraceDate() == null
						|| (subscriberProduct.getGraceDate() != null
								&& subscriberProduct.getGraceDate().before(currentDate)))
						&& order.getStatus() == Constants.ORDER_STATUS_DENIED))
			{
				order.setActionType(Constants.ACTION_UNREGISTER);
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
}
