package com.crm.ascs.thread;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import com.crm.ascs.net.Trigger;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.thread.DispatcherInstance;

public class CollectorInstance extends DispatcherInstance
{

	public CollectorInstance() throws Exception
	{
		super();
	}

	@Override
	public CollectorThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (CollectorThread) super.getDispatcher();
	}

	@Override
	public void doProcessSession() throws Exception
	{
		MQConnection connection = null;
		MessageProducer producer = null;
		try
		{
			connection = getMQConnection();
			Trigger trigger = null;
			do
			{
				trigger = ((CollectorThread) getDispatcher()).getWork();

				if (trigger == null)
					break;

				long startTime = System.currentTimeMillis();

//				try
//				{
//					TriggerImpl.insertTrigger(trigger);
//				}
//				catch (Exception e)
//				{
//					debugMonitor(e);
//				}
				long endTime = System.currentTimeMillis();
				//long costDB = endTime - startTime;
				int triggerTimeout = ((CollectorThread) getDispatcher()).triggerTimeout;
				trigger.setTimeout(triggerTimeout);
				startTime = System.currentTimeMillis();
				Message message = QueueFactory.createObjectMessage(connection.getSession(), trigger);

				if (producer == null)
				{
					producer = QueueFactory.createQueueProducer(connection.getSession(), null,
								triggerTimeout, dispatcher.queuePersistent);
				}

				producer.send(queueWorking, message);
				
				endTime = System.currentTimeMillis();
				long costQueue = endTime - startTime;
				debugMonitor("Sent trigger to queue cost: " + costQueue
						+ "ms, Content: " + trigger.getContent());
			}
			while (isAvailable());
		}
		catch (Exception e)
		{
			if (e instanceof JMSException)
			{
				if (connection != null)
					connection.markError();
			}
			throw e;
		}
		finally
		{
			QueueFactory.closeQueue(producer);
			returnMQConnection(connection);
		}
	}

}
