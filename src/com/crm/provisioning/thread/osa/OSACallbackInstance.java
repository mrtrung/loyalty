package com.crm.provisioning.thread.osa;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.OSACallbackMessage;
import com.crm.thread.DispatcherInstance;

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

public class OSACallbackInstance extends DispatcherInstance
{
	// //////////////////////////////////////////////////////
	// Queue variables
	// //////////////////////////////////////////////////////
	public Queue	queueCallback	= null;

	public OSACallbackInstance() throws Exception
	{
		super();
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
			queueCallback = QueueFactory.getQueue(QueueFactory.COMMAND_CALLBACK);
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
		Message message = null;

		/**
		 * Get callbackContent from local queue located in dispatcher.
		 */
		OSACallbackMessage callbackContent = ((OSACallbackThread) dispatcher).detachCallbackMessage();

		if (callbackContent != null)
		{
			//debugMonitor(callbackContent.getIsdn() + ": " + callbackContent.getCause());

			if (getLog().isDebugEnabled())
			{
				getLog().debug(callbackContent);
			}

			int timeout = ((OSACallbackThread) getDispatcher()).resultTimeout;
			
			MQConnection connection = null;
			
			try
			{
				connection = getMQConnection();
				
				/**
				 * Send callbackContent to queue server.
				 */
				message = connection.sendMessage(callbackContent, callbackContent.getSessionId(), queueCallback, timeout, dispatcher.queuePersistent);
				
				debugMonitor("Sent to queue: " + callbackContent.getCause() + " - " + callbackContent.getSessionId());
				
				callbackContent = null;
			}
			finally
			{
				returnMQConnection(connection);
			}
		}

		return message;
	}

	public int processMessage(Message message) throws Exception
	{
		message = null;
		
		return Constants.BIND_ACTION_NONE;
	}
	
	@Override
	public void doProcessSession() throws Exception
	{
		MQConnection connection = null;
		MessageProducer producer = null;

		OSACallbackMessage callbackContent = null;
		try
		{
			connection = getMQConnection();
			do
			{
				callbackContent = ((OSACallbackThread) dispatcher).detachCallbackMessage();

				if (callbackContent == null)
					break;

				try
				{
					Message message = QueueFactory.createObjectMessage(connection.getSession(), callbackContent);
					
					if (producer == null)
					{
						int timeout = ((OSACallbackThread) getDispatcher()).resultTimeout;
						producer = QueueFactory.createQueueProducer(connection.getSession(), queueWorking, timeout, dispatcher.queuePersistent);
					}
					producer.send(message);

					debugMonitor("Sent to queue: " + callbackContent.getCause() + " - " + callbackContent.getSessionId());
					
					callbackContent = null;
				}
				catch (Exception ex)
				{
					throw ex;
				}
			}
			while (isAvailable());
		}
		catch (Exception e)
		{
			if (e instanceof JMSException)
			{
				connection.markError();
			}
			debugMonitor(e);
			throw e;
		}
		finally
		{
			QueueFactory.closeQueue(producer);
			returnMQConnection(connection);
		}
	}

}
