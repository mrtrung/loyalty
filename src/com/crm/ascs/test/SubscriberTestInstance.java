package com.crm.ascs.test;

import javax.jms.Message;
import javax.jms.MessageProducer;

import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.thread.DispatcherInstance;

public class SubscriberTestInstance extends DispatcherInstance
{

	public SubscriberTestInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public SubscriberTestThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (SubscriberTestThread) super.getDispatcher();
	}

	@Override
	public void doProcessSession() throws Exception
	{
		MQConnection connection = null;
		MessageProducer producer = null;
		try
		{
			connection = getMQConnection();

			String isdn = "";
			do
			{
				isdn = getDispatcher().getIsdn();
				if (isdn.equals(""))
					break;


				RTBSMessage msg = processIsdn(isdn);
				try
				{
					if (producer == null)
					{
						producer = QueueFactory.createQueueProducer(connection.getSession(), queueWorking, 0,
								getDispatcher().queuePersistent);
					}
					
					Message message = QueueFactory.createObjectMessage(connection.getSession(), msg);
					
					producer.send(message);
					
					debugMonitor("Sent: " + msg.toString());
				}
				catch (Exception e)
				{
					debugMonitor(e);
				}
			}
			while (!isdn.equals("") && isAvailable());

			setRunning(false);

		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
		finally
		{
			QueueFactory.closeQueue(producer);
			returnMQConnection(connection);
		}
	}

	protected RTBSMessage processIsdn(String isdn) throws Exception
	{
		return null;
	}
}
