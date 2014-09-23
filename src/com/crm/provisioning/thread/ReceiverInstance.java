package com.crm.provisioning.thread;


import javax.jms.Message;
import javax.jms.MessageProducer;

import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.impl.smpp.SMPPConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.CommandUtil;
import com.crm.thread.DispatcherInstance;
import com.crm.kernel.queue.QueueFactory;
import com.logica.smpp.pdu.PDU;

import javax.jms.JMSException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: FPT
 * </p>
 * 
 * @author DatPX
 * @version 1.0 Purpose : Compute file : R4
 */

public class ReceiverInstance extends DispatcherInstance
{
	// public Queue queueIncomeMessage = null;

	public ReceiverInstance() throws Exception
	{
		super();
	}

	@Override
	public void initQueue() throws Exception
	{

		super.initQueue();
	}

	private CommandMessage detachMessageFromQueue() throws Exception
	{
		PDU pdu = QueueFactory.detachIncomeSMSQueue();
		if (pdu == null)
			return null;
		
		debugMonitor("Enqueue SMS: " + pdu.debugString());
		CommandMessage message = SMPPConnection.getMessageFromPDU(pdu);
		
		return message;
	}

	@Override
	public void doProcessSession() throws Exception
	{
		MQConnection connection = null;
		MessageProducer producer = null;

		CommandMessage request = null;
		try
		{
			connection = getMQConnection();
			do
			{
				request = detachMessageFromQueue();

				if (request == null)
					break;

				request.setTimeout(((ReceiverThread) getDispatcher()).orderTimeout * 1000);

				try
				{
					Message message = QueueFactory.createObjectMessage(connection.getSession(), request);
					
					if (producer == null)
					{
						producer = QueueFactory.createQueueProducer(connection.getSession(), queueWorking, request.getTimeout(), dispatcher.queuePersistent);
					}
					producer.send(message);

					debugMonitor("Sent to OrderRoute: " + request.getIsdn());
				}
				catch (Exception ex)
				{
					logMonitor("Can not send to route, notify error: " + request.toShortString());
					CommandUtil.sendSMS(this, request, "He thong dang ban, quy khach vui long thu lai sau.");
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
