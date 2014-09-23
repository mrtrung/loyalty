package com.crm.thread;

import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.fss.thread.ThreadConstant;

public class SimulatorInstanceExt extends Thread
{
	SimulatorRouteExt	simulatorRouteExt	= null;

	public SimulatorInstanceExt(SimulatorRouteExt simulatorRouteExt)
	{
		this.simulatorRouteExt = simulatorRouteExt;
	}

	public void run()
	{
		int count = 0;
		int batchCounter = 0;
		MQConnection connection = null;
		try
		{
			connection = simulatorRouteExt.simulatorThreadExt.getMQConnection();
			while (simulatorRouteExt.getCurrentIsdn() > 0
					&& simulatorRouteExt.simulatorThreadExt.miThreadCommand != ThreadConstant.THREAD_STOP)
			{
				batchCounter++;
				CommandMessage order = new CommandMessage();

				order.setChannel(simulatorRouteExt.channel);

				if (simulatorRouteExt.channel.equals(Constants.CHANNEL_SMS))
				{
					order.setProvisioningType("SMSC");
				}

				order.setUserId(0);
				if (simulatorRouteExt.deliveryUser.equals(""))
					order.setUserName("system");
				else
					order.setUserName(simulatorRouteExt.deliveryUser);

				order.setServiceAddress(simulatorRouteExt.serviceAddress);
				order.setIsdn(String.valueOf(simulatorRouteExt.currentIsdn));
				order.setShipTo(simulatorRouteExt.shipTo);
				order.setTimeout(60 * 1000);

				order.setKeyword(simulatorRouteExt.keyword);

				try
				{
					connection.sendMessage(order, "", 0, simulatorRouteExt.simulatorThreadExt.queueWorking, 60 * 1000
								, new String[] { "SystemID" }, new Object[] { new String(order.getUserName()) }, true);
				}
				catch (Exception ex)
				{
					simulatorRouteExt.simulatorThreadExt.debugMonitor(ex);
				}
				// finally
				// {
				// simulatorRouteExt.simulatorThreadExt.returnMQConnection(connection);
				// }

				simulatorRouteExt.simulatorThreadExt.logMonitor(order.toLogString());
				count++;
				simulatorRouteExt.simulatorThreadExt.logMonitor("message count: " + count);
				if (batchCounter >= simulatorRouteExt.batchSize)
				{
					Thread.sleep(simulatorRouteExt.timeBetweenLoop);
					batchCounter = 0;
				}
				simulatorRouteExt.simulatorThreadExt.logMonitor(simulatorRouteExt.simulatorThreadExt.counter());
			}
			simulatorRouteExt.simulatorThreadExt.returnMQConnection(connection);
		}
		catch (Exception ex)
		{
			simulatorRouteExt.simulatorThreadExt.returnMQConnection(connection);
			simulatorRouteExt.simulatorThreadExt.debugMonitor(ex);
		}
	}
}
