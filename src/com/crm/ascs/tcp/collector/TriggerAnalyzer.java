package com.crm.ascs.tcp.collector;

import com.crm.ascs.net.INetAnalyzer;
import com.crm.ascs.net.INetDataCollection;
import com.crm.ascs.net.INetHandler;
import com.crm.ascs.net.Trigger;

public class TriggerAnalyzer implements INetAnalyzer
{
	private static final String		SEPARATE_CHARS	= ";";

	private String					lastData		= "";
	private TriggerClientHandler	handler			= null;

	@Override
	public void createObject(Object data, INetDataCollection collection)
	{
		byte[] bytes = (byte[]) data;
		String receiveData = new String(bytes);
		String hexData = ""; // StringUtil.toHexString(bytes, 0, bytes.length);

		debugMonitor("RECEIVE from #" + handler.getHandlerId() + ": " + hexData + "[ASCII:" + receiveData + "]");

		receiveData = lastData + receiveData;

		int endIndex = receiveData.indexOf(SEPARATE_CHARS);
		int startIndex = 0;
		while (endIndex > 0)
		{
			/**
			 * parse then remove 2 first bytes
			 */
			String receive = receiveData.substring(startIndex, endIndex);
			try
			{
				collection.put(Trigger.createTrigger(receive));
			}
			catch (Exception e)
			{
				debugMonitor(e.getMessage() + " - Can not parse trigger: " + receive);
				debugMonitor(e);
			}

			startIndex = endIndex + SEPARATE_CHARS.length();

			endIndex = receiveData.indexOf(SEPARATE_CHARS, startIndex);
		}

		lastData = receiveData.substring(startIndex);
	}

	public void debugMonitor(Object message)
	{
		if (handler != null)
			handler.debugMonitor(message);
	}

	public void setHandler(INetHandler handler)
	{
		this.handler = (TriggerClientHandler) handler;
	}

	public INetHandler getHandler()
	{
		return handler;
	}

	@Override
	public void setHandler(
			com.crm.ascs.collector.TriggerClientHandler triggerClientHandler) {
		// TODO Auto-generated method stub
		
	}
}
