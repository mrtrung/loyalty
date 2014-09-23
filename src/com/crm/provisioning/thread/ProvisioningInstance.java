package com.crm.provisioning.thread;

import javax.jms.Message;
import javax.jms.Queue;

import com.crm.kernel.index.IndexNode;
import com.crm.kernel.message.AlarmMessage;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.cache.CommandAction;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningConnection;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.subscriber.impl.SubscriberOrderImpl;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.crm.thread.DatasourceInstance;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author: HungPQ
 * @version 1.0
 */

public class ProvisioningInstance extends DatasourceInstance
{
	// //////////////////////////////////////////////////////
	// Queue variables
	// //////////////////////////////////////////////////////
	public Queue	queueCommandRouting		= null;
	public Queue	queueOrderResponse		= null;
	public Queue	queueCommandLog			= null;
	public Queue	queueCommandStatistic	= null;

	public ProvisioningInstance() throws Exception
	{
		super();
	}

	public ProvisioningThread getDispatcher()
	{
		return (ProvisioningThread)dispatcher;
	}

	public String getDebugMode()
	{
		if (((ProvisioningThread) dispatcher).useSimulation)
			return "depend";
		else
			return "false";
	}

	public boolean isDebug()
	{
		String debug = getDebugMode();

		return debug.equals("true") || debug.equals("depend");
	}
	
	public void sendMessage(Queue queue, CommandMessage object, long timeout) throws Exception
	{
		if (object != null)
		{
			sendMessage(queue, object, object.getCorrelationID(), timeout);
		}
	}

	public void sendMessage(String queueName, CommandMessage object, long timeout) throws Exception
	{
		if (object != null)
		{
			sendMessage(queueName, object, object.getCorrelationID(), timeout);
		}
	}

	public ProvisioningConnection getProvisioningConnection() throws Exception
	{
		try
		{
			return ((ProvisioningThread) dispatcher).getProvisioningPool().getConnection();
		}
		catch (Exception e)
		{
			if (((ProvisioningThread) dispatcher).getProvisioningPool().getProvisioningPool().getNumActive() == 0)
			{
				long provisioningId = 0;
				if (((ProvisioningThread) dispatcher).provisioning != null)
					provisioningId = ((ProvisioningThread) dispatcher).provisioning.getProvisioningId();
				sendInstanceAlarm(e, "can-not-get-connection", provisioningId,
						((ProvisioningThread) dispatcher).provisioningClass);
			}
			throw e;
		}
	}

	public void closeProvisioningConnection(ProvisioningConnection connection) throws Exception
	{
		((ProvisioningThread) dispatcher).getProvisioningPool().closeConnection(connection);
	}

	public void sendOrderResponse(ProductRoute orderRoute, CommandMessage request) throws Exception
	{
		try
		{
			if ((orderRoute != null) && orderRoute.isCreateOrder())
			{
				try
				{
					SubscriberOrderImpl.updateStatus(
							request.getOrderId(), request.getOrderDate()
							, request.getStatus(), request.getCause(), request.getDescription());
				}
				catch (Exception e)
				{
					// send notify message to queue manage order update fail
//					MQConnection connection = null;
//					try
//					{
//						connection = getMQConnection();
//						connection.sendMessage(request, "OrderFail", 0);
//					}
//					finally
//					{
//						returnMQConnection(connection);
//					}
//					
					throw e;
				}
			}
			
			if (request.getSubProductId() != Constants.DEFAULT_ID)
			{
				try
				{
					SubscriberProductImpl.setSubscriptionComplete(request.getSubProductId());
				}
				catch (Exception e)
				{
					debugMonitor(e);
				}
			}

			try
			{
				if (orderRoute != null)
				{
					if (orderRoute.isNotifyOwner())
					{
						orderRoute.getExecuteImpl().notifyOwner(this, orderRoute, request);
					}

					if (orderRoute.isNotifyDeliver() && (request.getStatus() != Constants.ORDER_STATUS_DENIED)
							&& !request.getShipTo().equals("") && !request.getIsdn().equals(request.getShipTo()))
					{
						orderRoute.getExecuteImpl().notifyDeliver(this, orderRoute, request);
					}

					if (orderRoute.isSendAdvertising())
					{
						orderRoute.getExecuteImpl().sendAdvertising(this, orderRoute, request);
					}

					if (orderRoute.isSynchronous() && request.getChannel().equals(Constants.CHANNEL_WEB))
					{
						sendMessage(queueOrderResponse, request, request.getTimeout());
					}
				}
				else if (request.getChannel().equals(Constants.CHANNEL_WEB))
				{
					// String content = CommandUtil.formatContent(this, null,
					// product, request);

					sendMessage(queueOrderResponse, request, request.getTimeout());;
				}
				else if (request.getChannel().equals(Constants.CHANNEL_SMS))
				{
					ResponseUtil.notifyOwner(this, orderRoute, request);
				}
			}
			catch (Exception e)
			{
				logMonitor(e);
			}
			sendCommandStatistic(request);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public CommandMessage getNextCommandMessage(CommandAction commandAction, CommandMessage request) throws Exception
	{
		CommandMessage nextRequest = null;

		if (commandAction.getExecuteMethod() != null)
		{
			nextRequest =
					(CommandMessage) commandAction.getExecuteMethod().invoke(
							commandAction.getExecuteImpl(), commandAction, request);
		}
		else
		{
			nextRequest = request.clone();

			nextRequest.setActionType(commandAction.getNextAction());
			nextRequest.setCommandId(commandAction.getNextCommandId());
		}

		return nextRequest;
	}

	public int sendNextCommand(
			ProductRoute orderRoute, CommandMessage request, CommandEntry command, String actionType, String actionCause)
			throws Exception
	{
		// get next command if available
		int nextCounter = 0;

		try
		{
			if (command == null)
			{
				return nextCounter;
			}

			if (actionType.equals(""))
			{
				actionType = request.getActionType();
			}

			if (actionCause.equals(""))
			{
				actionCause = request.getCause();
			}

			for (IndexNode node : command.getActions().getNodes())
			{
				CommandAction commandAction = (CommandAction) node;

				if (commandAction.equals(
						request.getProductId(), actionType, request.getSubscriberType(), actionCause))
				{
					CommandMessage nextRequest = getNextCommandMessage(commandAction, request);

					if (!nextRequest.getActionType().equals(Constants.ACTION_ROLLBACK))
					{
						nextCounter++;
					}

					CommandEntry nextCommand = ProvisioningFactory.getCache().getCommand(nextRequest.getCommandId());

					nextRequest.setProvisioningType(nextCommand.getProvisioningType());

					sendCommandRouting(nextRequest);
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}

		return nextCounter;
	}

	public void sendCommandRouting(CommandMessage request) throws Exception
	{
		try
		{
			request.setRetryCounter(0);
			
			QueueFactory.attachCommandRouting(request);
			//sendMessage(queueCommandRouting, request, 0);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public void sendCommandLog(CommandMessage request) throws Exception
	{
		try
		{
			/**
			 * Turn off command log.
			 */
			//sendMessage(queueCommandLog, request, 0);
			//QueueFactory.attachCommandLog(request);
		}
		catch (Exception e)
		{
			logMonitor(e);
		}
	}

	public void sendCommandStatistic(CommandMessage request) throws Exception
	{
		try
		{
			//sendMessage(queueCommandStatistic, request, 0);
			QueueFactory.attachStatistic(request);
		}
		catch (Exception e)
		{
			logMonitor(e);
		}
	}

	public void sendInstanceAlarm(Exception e, String cause, long provisioningId, String provisioningClass)
	{
		if (!getDispatcher().alarmEnable)
		{
			return;
		}
		AlarmMessage alarm = new AlarmMessage();
		alarm.setCause(cause);
		alarm.setProvisioningId(provisioningId);
		alarm.setProvisioningClass(provisioningClass);

		StackTraceElement[] stackTraces = e.getStackTrace();
		String content = "";
		for (StackTraceElement stackTrace : stackTraces)
		{
			content += stackTrace.toString() + "\r\n";
		}

		alarm.setDescription(e.getMessage());
		alarm.setContent(content);

		sendInstanceAlarm(alarm);
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void initQueue() throws Exception
	{
		super.initQueue();

		try
		{
			//queueCommandRouting = QueueFactory.getQueue(QueueFactory.COMMAND_ROUTE_QUEUE);
			queueCommandLog = QueueFactory.getQueue(QueueFactory.COMMAND_LOG_QUEUE);
			queueCommandStatistic = QueueFactory.getQueue(QueueFactory.COMMAND_STATISTIC_QUEUE);
			queueOrderResponse = QueueFactory.getQueue(QueueFactory.ORDER_RESPONSE_QUEUE);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public Message detachMessage() throws Exception
	{
		try
		{
			if (getDispatcher().maxLocalQueueSize > 0 && 
					QueueFactory.getCommandRoutingSize() > getDispatcher().maxLocalQueueSize)
			{
				debugMonitor("local queue is over: " + QueueFactory.getCommandRoutingSize());
				
				Thread.sleep(1000);
				
				return null;
			}
			
			return super.detachMessage();
		}
		catch (Exception e)
		{
			// getDispatcher().resetQueueConnection();
			throw e;
		}
	}

}
