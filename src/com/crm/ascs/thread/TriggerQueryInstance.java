package com.crm.ascs.thread;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSession;

import org.apache.axis.AxisFault;

import com.crm.ascs.ccws.CCWSConnection;
import com.crm.ascs.impl.TriggerImpl;
import com.crm.ascs.net.Trigger;
import com.crm.ascs.net.TriggerActivation;
import com.crm.ascs.net.TriggerRecharge;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.util.GeneratorSeq;

public class TriggerQueryInstance extends RTBSInstance
{
	String logDateFormat = "dd/MM/yyyy HH:mm:ss";

	public TriggerQueryInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public TriggerQueryThread getDispatcher()
	{
		return (TriggerQueryThread) super.getDispatcher();
	}

	@Override
	public int processMessage(Message message) throws Exception
	{
		try
		{
			Trigger trigger = Trigger.getFromMQMessage(message);

			if (trigger == null)
				return Constants.BIND_ACTION_NONE;

			CCWSConnection connection = null;

			long startTime = System.currentTimeMillis();
			long endTime = System.currentTimeMillis();
			long updateCost = 0;
			long sendQueueCost = 0;

			try
			{
				if (getDispatcher().useSimulation)
				{
					trigger.setActivationDate(trigger.getReceiveDate());
					if (trigger.getPreviousState().equals(""))
						trigger.setPreviousState("Idle");
					trigger.setStatus(Trigger.STATUS_APPROVED);
					long time = getDispatcher().simulationTime;
					Thread.sleep(time);
					debugMonitor("Simulation sleep execute time: " + time + " ms.");
					if (trigger instanceof TriggerRecharge)
					{
						((TriggerRecharge) trigger).setRechargeDate(trigger.getReceiveDate());
					}

				}
				else
				{
					try
					{
						connection = getCCWSConnection();
						int sessionId = 0;
						try
						{
							sessionId = GeneratorSeq.getNextSeq();
						}
						catch (Exception e)
						{

						}
						
						int status = Trigger.STATUS_FAILURE;
						if (trigger.getType().equals(Trigger.TYPE_ACTIVATION))
						{
							String strRequest = "com.comverse_in.prepaid.ccws.ServiceSoapStub.retrieveSubscriberWithIdentityNoHistory("
									+ trigger.getIsdn() + ")";
							debugMonitor("SEND: ID="
									+ sessionId
									+ "; REQ=" + strRequest);
							status = connection.getSubInfo((TriggerActivation) trigger);
							debugMonitor("RECEIVE: ID="
									+ sessionId
									+ "; RESP=ActivationDate(" 
									+ Trigger.stringFromDate(trigger.getActivationDate()) + ")");
							
						}
						else if (trigger.getType().equals(Trigger.TYPE_RECHARGE))
						{
							Calendar startDate = Calendar.getInstance();
							startDate.setTime(trigger.getReceiveDate());
							startDate.add(Calendar.HOUR_OF_DAY, (-1) * getDispatcher().historyTime);
							Calendar endDate = Calendar.getInstance();

							String strRequest = "com.comverse_in.prepaid.ccws.ServiceSoapStub.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities("
									+ trigger.getIsdn()
									+ "," + Trigger.stringFromDate(startDate.getTime(), logDateFormat)
									+ "," + Trigger.stringFromDate(endDate.getTime(), logDateFormat)
									+ ")";
							debugMonitor("SEND: ID="
									+ sessionId
									+ "; REQ=" + strRequest);
							status = connection.getRechargeHistory((TriggerRecharge) trigger, startDate, endDate);
							debugMonitor("RECEIVE: ID="
									+ sessionId
									+ "; RESP=ActivationDate("
									+ Trigger.stringFromDate(trigger.getActivationDate())
									+ ")"
									+ ",PreviousState("
									+ trigger.getPreviousState()
									+ ")"
									+ ",RechargeDate("
									+ Trigger.stringFromDate(((TriggerRecharge) trigger).getRechargeDate(), logDateFormat)
									+ ")");
						}
						trigger.setStatus(status);
					}
					catch (Exception e)
					{
						trigger.setStatus(Trigger.STATUS_FAILURE);
						if (e instanceof IOException || e instanceof AxisFault)
						{
							/**
							 * Push back to retry then break to not update to
							 * DB.
							 */
							if (trigger.getRetryCount() < getDispatcher().maxRetry)
							{
								startTime = System.currentTimeMillis();
								trigger.setRetryCount(trigger.getRetryCount() + 1);
								sendTriggerToQueue(trigger, queueWorking);

								endTime = System.currentTimeMillis();
								sendQueueCost = endTime - startTime;
								debugMonitor("Send back to queue for retry cost " +
										sendQueueCost + "ms.");
								throw e;
							}
						}
						debugMonitor(e);
					}
					finally
					{
						returnCCWSConnection(connection);
					}
				}

				/**
				 * Update to DB
				 */
				startTime = System.currentTimeMillis();
				boolean isRetryTrigger = false;
				if (trigger.getTriggerId() != Constants.DEFAULT_ID)
				{
					isRetryTrigger = true;
				}
				try
				{
					if (isRetryTrigger)
					{
						TriggerImpl.updateTrigger(trigger);
					}
					else
					{
						TriggerImpl.insertTrigger(trigger);
					}

					endTime = System.currentTimeMillis();
					updateCost = endTime - startTime;

					debugMonitor("Update trigger to DB cost " + updateCost + "ms.");
				}
				catch (SQLException e)
				{
					debugMonitor(e);

					if (trigger.getTriggerId() == Constants.DEFAULT_ID)
					{
						try
						{
							startTime = System.currentTimeMillis();
							sendTriggerToQueue(trigger, getDispatcher().queueFile);
							endTime = System.currentTimeMillis();
							sendQueueCost = endTime - startTime;
							debugMonitor("Send to write to file cost " + sendQueueCost + "ms.");
						}
						catch (Exception ex)
						{
							debugMonitor(ex);
							debugMonitor("Can not send to write to file: " + trigger.toString());
						}
					}
				}

				startTime = System.currentTimeMillis();

				/**
				 * Send trigger to specific queue.
				 */
				if (!isRetryTrigger
						&& ((!getDispatcher().queueActivation.equals("") && trigger.getType().equals(Trigger.TYPE_ACTIVATION))
								|| (!getDispatcher().queueRecharge.equals("") && trigger.getType().equals(Trigger.TYPE_RECHARGE))))
				{
					try
					{
						String[] extQueueNames = new String[0];
						if (trigger.getType().equals(Trigger.TYPE_ACTIVATION))
							extQueueNames = getDispatcher().queueActivation.split(",");
						else if (trigger.getType().equals(Trigger.TYPE_RECHARGE))
							extQueueNames = getDispatcher().queueRecharge.split(",");
						for (int i = 0; i < extQueueNames.length; i++)
						{
							if (extQueueNames[i].trim().equals(""))
							{
								continue;
							}
							sendTriggerToQueue(trigger, extQueueNames[i]);
						}
					}
					catch (Exception e)
					{
						debugMonitor(e);
					}
				}

				endTime = System.currentTimeMillis();
				sendQueueCost = endTime - startTime;

				debugMonitor("Send next queue cost " + sendQueueCost + "ms.");

			}
			finally
			{
				debugMonitor(trigger.toLogString());
			}
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}

		return Constants.BIND_ACTION_NONE;
	}

	public void sendTriggerToQueue(Trigger trigger, Queue queue) throws Exception
	{
		QueueSession session = null;
		MQConnection connection = null;
		try
		{
			connection = getMQConnection();
			session = connection.createSession();
		}
		finally
		{
			returnMQConnection(connection);
		}

		Message message = QueueFactory.createObjectMessage(session, trigger);

		QueueFactory.sendMessage(session, queue, message, trigger.getTimeToLive(), getDispatcher().queuePersistent);
	}

	public void sendTriggerToQueue(Trigger trigger, String queueName) throws Exception
	{
		if (queueName.equals(""))
			return;
		Queue queue = QueueFactory.getQueue(queueName);
		sendTriggerToQueue(trigger, queue);
	}
}
