/**
 * 
 */
package com.crm.provisioning.thread;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import javax.jms.Message;

import com.crm.product.cache.ProductAction;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductPrice;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.CommandUtil;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.subscriber.bean.SubscriberOrder;
import com.crm.subscriber.impl.SubscriberEntryImpl;
import com.crm.subscriber.impl.SubscriberOrderImpl;

import com.fss.util.AppException;

/**
 * @author ThangPV
 * 
 */
public class OrderRoutingInstance extends ProvisioningInstance
{
	public OrderRoutingInstance() throws Exception
	{
		super();
	}

	public CommandMessage validateOrder(ProductRoute orderRoute, CommandMessage order) throws AppException, Exception
	{
		try
		{
			if ((orderRoute == null) || orderRoute.getExecuteMethod() == null)
			{
				return order;
			}

			Object result = orderRoute.getExecuteMethod().invoke(orderRoute.getExecuteImpl(), this, orderRoute, order);

			if (result instanceof CommandMessage)
			{
				return (CommandMessage) result;
			}
			else
			{
				throw new AppException("order-invalid");
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public int processMessage(Message request) throws Exception
	{
		CommandMessage order = (CommandMessage) QueueFactory.getContentMessage(request);

		// order.setRequestValue("executionStartTime", (new
		// java.util.Date()).getTime());

		ProductRoute orderRoute = null;
		ProductEntry product = null;
		ProductAction action = null;
		CommandEntry command = null;

		Exception error = null;

		try
		{
			if (order.getQuantity() == 0)
			{
				order.setQuantity(1);
			}

			// source address
			String isdn = CommandUtil.addCountryCode(order.getIsdn());
			order.setIsdn(isdn);

			// destination address
			String shippingTo = CommandUtil.addCountryCode(order.getShipTo());
			order.setShipTo(shippingTo);

			// get command
			if (order.getKeyword().equals(""))
			{
				throw new AppException("unknow-keyword");
			}

			// get order routing
			if (order.getRouteId() != Constants.DEFAULT_ID)
			{
				orderRoute = ProductFactory.getCache().getProductRoute(order.getRouteId());
			}
			else
			{
				orderRoute =
						ProductFactory.getCache().getProductRoute(
								order.getChannel(), order.getServiceAddress(), order.getKeyword(), order.getOrderDate());
			}

			if (orderRoute == null)
			{
				throw new AppException(Constants.ERROR_INVALID_SYNTAX);
			}
			else
			{
				order.setProductId(orderRoute.getProductId());
				order.setActionType(orderRoute.getActionType());

				order.setRouteId(orderRoute.getRouteId());

				// check timeout
				if (orderRoute.getStatus() == Constants.SERVICE_STATUS_DENIED)
				{
					throw new AppException(Constants.UPGRADING);
				}
				else if (CommandUtil.isTimeout(order, orderRoute.getTimeout()))
				{
					throw new AppException(Constants.ERROR_TIMEOUT);
				}
			}

			/**
			 * Throws AppException(duplicate request) if the same request is in
			 * processing
			 */
			// ((OrderRoutingThread) dispatcher).checkProcessing(order);
			// checkProcessingPass = true;

			// product
			product = ProductFactory.getCache().getProduct(order.getProductId());

			if (product == null)
			{
				throw new AppException(Constants.ERROR_PRODUCT_NOT_FOUND);
			}
			else if (product.getStatus() == Constants.SERVICE_STATUS_DENIED)
			{
				throw new AppException(Constants.UPGRADING);
			}

			/**
			 * Create order
			 */
			SubscriberOrder subscriberOrder = null;
			if ((orderRoute != null) && orderRoute.isCreateOrder())
			{
				try
				{
					Date startTime = new Date();
					subscriberOrder = SubscriberOrderImpl.createOrder(
							order.getUserId(), order.getUserName(), order.getOrderDate(), order.getActionType()
							, order.getSubscriberId(), order.getIsdn(), order.getSubscriberType()
							, order.getSubProductId(), order.getProductId()
							, order.getPrice(), order.getQuantity(), order.getDiscount(), order.getAmount(), order.getScore()
							, order.getCause(), order.getStatus(), order.getChannel());

					Date endTime = new Date();
					debugMonitor("Create order(" + order.getIsdn() + ") cost time: " + (endTime.getTime() - startTime.getTime())
							+ "ms");
					order.setOrderDate(subscriberOrder.getOrderDate());
					order.setOrderId(subscriberOrder.getOrderId());
				}
				catch (SQLException e)
				{
					order.setStatus(Constants.ORDER_STATUS_DENIED);

					if (e.getMessage().startsWith("ORA-00001"))
					{
						order.setCause(Constants.ERROR_DUPLICATED);
					}
					else
					{
						order.setCause(Constants.ERROR_CREATE_ORDER_FAIL);
					}

					logMonitor(e);
				}
				catch (Exception e)
				{
					order.setStatus(Constants.ORDER_STATUS_DENIED);
					order.setCause(Constants.ERROR_CREATE_ORDER_FAIL);

					logMonitor(e);
				}
			}

			if (order.getStatus() != Constants.ORDER_STATUS_DENIED)
			{
				if (orderRoute.getExecuteMethod() != null)
				{
					order = validateOrder(orderRoute, order);
				}
				else
				{
					// get subscriber type
					order.setSubscriberType(SubscriberEntryImpl.getSubscriberType(order.getIsdn()));
				}

				// get price
				if (!orderRoute.isCheckBalance() && (order.getStatus() != Constants.ORDER_STATUS_DENIED))
				{
					// set default price
					order.setOfferPrice(product.getPrice());

					ProductPrice productPrice =
							product.getProductPrice(
									order.getChannel(), order.getActionType(), order.getSegmentId()
									, order.getAssociateProductId(), order.getQuantity(), order.getOrderDate());

					if (productPrice != null)
					{
						order.setPrice(productPrice.getFullOfCharge());
					}
					else
					{
						order.setPrice(product.getPrice());
					}

					order.setAmount(order.getPrice() * order.getQuantity());
				}

				/**
				 * Update order after validate
				 */
				if (orderRoute.isCreateOrder())
				{
					try
					{
						Date startTime = new Date();

						SubscriberOrderImpl.updateOrder(order.getOrderId(), order.getOrderDate(), order.getActionType(),
								order.getSubscriberId(), order.getIsdn(), order.getSubscriberType(), order.getSubProductId(),
								order.getProductId(), order.getPrice(), order.getQuantity(), order.getDiscount(),
								order.getAmount(), order.getScore(), order.getCause(), order.getStatus(), order.getChannel());

						Date endTime = new Date();
						debugMonitor("Update order(" + order.getIsdn() + ") cost time: "
								+ (endTime.getTime() - startTime.getTime())
								+ "ms");
					}
					catch (Exception e)
					{
						order.setStatus(Constants.ORDER_STATUS_DENIED);
						order.setCause(Constants.ERROR_ORDER_NOT_FOUND);

						logMonitor(e);
					}
				}

				/**
				 * Get command
				 */
				if (order.getStatus() != Constants.ORDER_STATUS_DENIED)
				{
					// get destination queue
					String queueName =
							orderRoute.getParameter(
									order.getActionType(), "destination.queue", "", order.isPrepaid(), orderRoute.getQueueName());

					// get first provisioning command
					action = product.getProductAction(order.getActionType(), order.getSubscriberType());

					if (action != null)
					{
						// first execute command for this order
						order.setCommandId(action.getCommandId());

						command = ProvisioningFactory.getCache().getCommand(order.getCommandId());

						if (command == null)
						{
							throw new AppException(Constants.ERROR_COMMAND_NOT_FOUND);
						}
						else
						{
							order.setProvisioningType(command.getProvisioningType());
						}

					}
					else
					{
						throw new AppException(Constants.ERROR_COMMAND_NOT_FOUND);
					}

					// send provisioning request to command routing queue
					if (queueName.equals(""))
					{
						sendCommandRouting(order);
					}
					else
					{
						String provisioningName =
								orderRoute.getParameter(order.getActionType(), "destination.provisioning", "", order.isPrepaid(),
										"");

						if (!provisioningName.equals(""))
						{
							ProvisioningEntry provisioning = ProvisioningFactory.getCache().getProvisioning(provisioningName);

							order.setProvisioningId(provisioning.getProvisioningId());
						}

						sendMessage(queueName, order, 0);
					}
				}
			}
		}
		catch (AppException e)
		{
			order.setCause(e.getMessage());
			order.setDescription(e.getContext());

			error = e;
		}
		catch (Exception e)
		{
			order.setCause(Constants.ERROR);
			order.setDescription(e.getMessage());

			error = e;
		}
		finally
		{
			// if (checkProcessingPass)
			// ((OrderRoutingThread) dispatcher).removeProcessing(order);
		}

		try
		{
			if (error != null)
			{
				order.setStatus(Constants.ORDER_STATUS_DENIED);

				logMonitor(error);
				logMonitor(order.toOrderString());
			}
			else
			{
				/**
				 * Add log ISDN: PRODUCT_ALIAS - COMMAND_ALIAS<br>
				 * NamTA<br>
				 * 21/08/2012
				 */
				if (product != null & command != null)
					debugMonitor(order.getIsdn() + ": " + product.getAlias() + " - " + command.getAlias());
				debugMonitor(order.toOrderString());
			}

			if ((orderRoute == null) || !orderRoute.isSynchronous()
					|| (order.getStatus() == Constants.ORDER_STATUS_DENIED) || (command == null))
			{
				sendOrderResponse(orderRoute, order);
			}

			if ((error != null) && !(error instanceof AppException))
			{
				throw error;
			}
		}
		catch (Exception e)
		{
			sendInstanceAlarm(error, error.getMessage(), order.getProvisioningId(), "");
			throw e;
		}

		return Constants.BIND_ACTION_SUCCESS;
	}
}
