package com.crm.ascs.udp.collector;

import java.net.DatagramPacket;

import com.crm.ascs.net.INetAnalyzer;
import com.crm.ascs.net.INetDataCollection;
import com.crm.ascs.net.NetThread;
import com.crm.ascs.net.Trigger;
import com.crm.ascs.tcp.collector.TriggerClientHandler;
import com.crm.ascs.thread.CollectorThread;
import com.crm.util.StringUtil;

public class CollectorAnalyzer extends NetThread implements INetAnalyzer
{
	private static final String	SEPARATE_CHARS	= ";";

	protected CollectorThread	dispatcher		= null;
	protected String			lastData		= "";

	public CollectorAnalyzer(CollectorThread dispatcher)
	{
		this.dispatcher = dispatcher;
	}

	@Override
	public void createObject(Object data, INetDataCollection collection)
	{
		DatagramPacket packet = (DatagramPacket) data;
		int length = packet.getLength();
		byte[] bytes = packet.getData();
		String receiveData = new String(bytes, 0, length);
		String hexData = StringUtil.toHexString(bytes, 0, length);

		debugMonitor("RECEIVE from " + packet.getAddress().getHostAddress() + ": " + hexData + "[ASCII:" + receiveData + "]");
		synchronized (lastData)
		{
			lastData += receiveData;
		}
	}

	@Override
	public void process() throws Exception
	{
		synchronized (lastData)
		{
			String receiveData = lastData;
			int endIndex = receiveData.indexOf(SEPARATE_CHARS);
			int startIndex = 0;
			while (endIndex > 0)
			{
				String receive = receiveData.substring(startIndex, endIndex);
				try
				{
					dispatcher.addWork(Trigger.createTrigger(receive));
				}
				catch (Exception e)
				{
				}

				startIndex = endIndex + SEPARATE_CHARS.length();

				endIndex = receiveData.indexOf(SEPARATE_CHARS, startIndex);
			}

			lastData = receiveData.substring(startIndex);
		}
	}

	@Override
	public void debugMonitor(Object message)
	{
		if (dispatcher != null)
			dispatcher.debugMonitor(message);
	}

	@Override
	public void setHandler(
			com.crm.ascs.collector.TriggerClientHandler triggerClientHandler) {
		// TODO Auto-generated method stub
		
	}

}
