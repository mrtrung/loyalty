package com.crm.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.util.ThreadUtil;
import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;
import com.sun.messaging.jmq.jmsserver.management.mbeans.LogMonitor;

public class SimulatorThreadExt extends DispatcherThread
{

	protected String							deliveryUser	= "";
	protected int								orderTimeout	= 60000;

	protected Vector							vtConfig;
	protected Map<String, SimulatorRouteExt>	map				= new HashMap<String, SimulatorRouteExt>();

	protected int								amount			= 0;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(ThreadUtil.createTextParameter("deliveryUser", 30, ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("orderTimeout", "Time to live of order (s)."));

		Vector vtValue = new Vector();
		vtValue.addElement(createParameterDefinition("channel", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Channel", "0"));
		vtValue.addElement(createParameterDefinition("serviceAddress", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Service Address", "1"));
		vtValue.addElement(createParameterDefinition("fromIsdn", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"from isdn", "2"));
		vtValue.addElement(createParameterDefinition("amoun", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"To ISDN", "3"));
		vtValue.addElement(createParameterDefinition("shipTo", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Ship to", "4"));
		vtValue.addElement(createParameterDefinition("keyword", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Keyword", "5"));
		vtValue.addElement(createParameterDefinition("timeBetweenLoop", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"timeBetweenLoop", "6"));
		vtReturn.addElement(createParameterDefinition("RouteConfig", "", ParameterType.PARAM_TABLE, vtValue,
				"Subscription Config"));

		vtReturn.addAll(ThreadUtil.createQueueParameter(this));
		vtReturn.addAll(ThreadUtil.createLogParameter(this));

		return vtReturn;
	}

	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();

			vtConfig = new Vector();
			Object obj = getParameter("RouteConfig");
			if (obj != null && (obj instanceof Vector))
			{
				vtConfig = (Vector) ((Vector) obj).clone();
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

	@Override
	public void doProcessSession() throws Exception
	{
		try
		{
			Vector vtRow = new Vector();
			int count = 0;
			for (int i = 0; i < vtConfig.size() && miThreadCommand != ThreadConstant.THREAD_STOP; i++)
			{
				vtRow = (Vector) vtConfig.elementAt(i);
				String channel = vtRow.elementAt(0).toString();
				String serviceAddress = vtRow.elementAt(1).toString();
				Long fromIsdn = Long.parseLong(vtRow.elementAt(2).toString());
				String shipTo = vtRow.elementAt(4).toString();
				String keyword = vtRow.elementAt(5).toString();
				Long amoun = Long.parseLong(vtRow.elementAt(3).toString());
				amoun = amoun + fromIsdn;
				Long sleepTime = Long.parseLong(vtRow.elementAt(6).toString());
				for (int j = 0; fromIsdn < amoun && isAvailable(); j++)
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
					order.setIsdn(String.valueOf(fromIsdn));
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
					logMonitor(order.toLogString());
					count++;
					fromIsdn++;
					Thread.sleep(sleepTime);
				}
				logMonitor("message count " + keyword + ": " + count);

			}

		}
		catch (Exception ex)
		{
			debugMonitor(ex);
		}
	}

	public synchronized int counter()
	{
		return amount++;
	}
}
