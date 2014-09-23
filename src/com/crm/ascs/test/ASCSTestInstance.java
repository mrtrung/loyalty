package com.crm.ascs.test;

import java.net.InetAddress;
import java.net.Socket;

import com.crm.thread.DispatcherInstance;
public class ASCSTestInstance extends DispatcherInstance
{
	private Socket	socket	= null;

	@Override
	public ASCSTestThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (ASCSTestThread) super.getDispatcher();
	}

	public ASCSTestInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		// TODO Auto-generated method stub
		super.beforeProcessSession();

		socket = new Socket(InetAddress.getByName(getDispatcher().host), getDispatcher().port);
		socket.setTcpNoDelay(true);
	}

	@Override
	public void afterProcessSession() throws Exception
	{
		try
		{
			if (socket != null)
				socket.close();
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	@Override
	public void doProcessSession() throws Exception
	{
		int totalCount = 0;
		byte[] packData = new byte[0];
		for (totalCount = 0; totalCount < getDispatcher().sendTotal && isAvailable(); totalCount++)
		{
			if (((getDispatcher().batchSize > 0 && totalCount % getDispatcher().batchSize == 0)
					|| getDispatcher().batchSize == 0)
					&& packData.length > 0)
			{
				socket.getOutputStream().write(packData);
				socket.getOutputStream().flush();

				debugMonitor("Sent: " + (new String(packData)));
				debugMonitor("Sent " + totalCount + " triggers.");
				packData = new byte[0];
				
				Thread.sleep(getDispatcher().timeBetweenLoop);
			}
			
			String content = getDispatcher().content;
			if (!content.endsWith(";"))
				content = content + ";";

			byte[] data = content.getBytes();
			byte[] sendData = new byte[data.length + 2];
			sendData[0] = 0;
			sendData[1] = 0x13;
			System.arraycopy(data, 0, sendData, 2, data.length);

			byte[] newByteData = new byte[sendData.length + packData.length];

			System.arraycopy(packData, 0, newByteData, 0, packData.length);
			System.arraycopy(sendData, 0, newByteData, packData.length, sendData.length);

			packData = newByteData;

		}

		if (packData.length > 0)
		{
			socket.getOutputStream().write(packData);
			socket.getOutputStream().flush();

			debugMonitor("Sent: " + (new String(packData)));
			debugMonitor("Sent " + totalCount + " triggers.");
			packData = new byte[0];
		}
		
		setRunning(false);
	}

}
