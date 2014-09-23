/**
 * 
 */
package com.crm.thread;

import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

/**
 * @author ThangPV
 * 
 */
public class SimulatorThread extends DispatcherThread
{
	protected String	deliveryUser	= "";
	protected String	channel			= "";
	protected String	serviceAddress	= "";
	protected String	isdn			= "";
	protected String	endIsdn			= "";
	protected String	shipTo			= "";
	protected String	keyword			= "";
	protected String	content			= "";

	protected int		batchSize		= 1;
	protected int		timeBetweenLoop	= 1;
	protected int		orderTimeout	= 60000;

	protected long		currentIsdn		= 0;
	protected long		maxIsdn			= 0;

	public SimulatorThread()
	{
		super();
	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(ThreadUtil.createTextParameter("deliveryUser", 30, ""));
		// vtReturn.addElement(ThreadUtil.createTextParameter("channel", 30,
		// ""));
		vtReturn.addElement(ThreadUtil.createComboParameter("channel", "SMS,web", ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("serviceAddress", 30, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("isdn", 30, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("endIsdn", 30,
				"If parameter is set, the simulator send order of subscribers range from isdn to endIsdn."));
		vtReturn.addElement(ThreadUtil.createTextParameter("shipTo", 30, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("keyword", 30, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("batchSize", 30, ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("timeBetweenLoop", ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("orderTimeout", "Time to live of order (s)."));

		vtReturn.addAll(ThreadUtil.createQueueParameter(this));
		vtReturn.addAll(ThreadUtil.createInstanceParameter(this));

		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();

			// //////////////////////////////////////////////////////
			// Fill extent parameter
			// //////////////////////////////////////////////////////

			deliveryUser = ThreadUtil.getString(this, "deliveryUser", false, "");
			channel = ThreadUtil.getString(this, "channel", false, "");
			serviceAddress = ThreadUtil.getString(this, "serviceAddress", false, "");
			isdn = ThreadUtil.getString(this, "isdn", false, "");
			endIsdn = ThreadUtil.getString(this, "endIsdn", false, isdn);
			shipTo = ThreadUtil.getString(this, "shipTo", false, "");
			keyword = ThreadUtil.getString(this, "keyword", false, "");
			batchSize = ThreadUtil.getInt(this, "batchSize", 1);
			timeBetweenLoop = ThreadUtil.getInt(this, "timeBetweenLoop", 1);
			orderTimeout = ThreadUtil.getInt(this, "orderTimeout", 60000);

			currentIsdn = 0;
			try
			{
				maxIsdn = Long.parseLong(endIsdn);
			}
			catch (Exception e)
			{
				maxIsdn = Long.parseLong(isdn);
			}
		}
		catch (AppException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new AppException(e.getMessage());
		}
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public synchronized long getCurrentIsdn() throws Exception
	{
		if (currentIsdn == 0)
		{
			currentIsdn = Long.parseLong(isdn);
		}
		else
		{
			currentIsdn++;
		}

		if (currentIsdn > maxIsdn)
		{
			return 0;
		}

		return currentIsdn;
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void doProcessSession() throws Exception
	{
		try
		{
			if (instanceEnable)
			{
				return;
			}

			long startIsdn = Long.parseLong(isdn);

			if (maxIsdn <= startIsdn)
				maxIsdn = startIsdn;

			int count = 0;
			long currentIsdn = startIsdn;

			logMonitor("Begin to send message.");

			while (currentIsdn <= maxIsdn && isAvailable())
			{
				for (int i = 0; i < batchSize && currentIsdn <= maxIsdn && isAvailable(); i++)
				{
					CommandMessage order = new CommandMessage();

					order.setChannel(channel);

					if (channel.equals(Constants.CHANNEL_SMS))
					{
						order.setProvisioningType("SMSC");
					}

					order.setUserId(0);
					if (deliveryUser.equals(""))
						order.setUserName("system");
					else
						order.setUserName(deliveryUser);

					order.setServiceAddress(serviceAddress);
					order.setIsdn(String.valueOf(currentIsdn));
					order.setShipTo(shipTo);
					order.setTimeout(orderTimeout * 1000);

					order.setKeyword(keyword);

					MQConnection connection = null;
					try
					{
						connection = getMQConnection();
						connection.sendMessage(order, "", 0, queueWorking, orderTimeout * 1000
								, new String[] { "SystemID" }, new Object[] { new String(order.getUserName()) }, queuePersistent);
					}
					finally
					{
						returnMQConnection(connection);
					}

					// if (displayDebug)
					logMonitor(order.toLogString());

					currentIsdn++;
					count++;
				}
				logMonitor("message count: " + count);
				Thread.sleep(timeBetweenLoop);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}
}
