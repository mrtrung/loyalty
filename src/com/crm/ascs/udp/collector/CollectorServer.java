package com.crm.ascs.udp.collector;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.crm.ascs.net.NetThread;
import com.crm.ascs.thread.CollectorThread;

public class CollectorServer extends NetThread
{
	private CollectorThread		dispatcher		= null;
	private DatagramSocket		serverSocket	= null;
	private DatagramPacket		packet			= null;
	private CollectorAnalyzer	analyzer		= null;
	private boolean				isPrepareToStop	= false;

	public CollectorServer(CollectorThread dispatcher) throws SocketException
	{
		this.dispatcher = dispatcher;
		analyzer = new CollectorAnalyzer(dispatcher);
	}

	private void openSocket() throws SocketException
	{
		if (serverSocket == null)
		{
			serverSocket = new DatagramSocket(dispatcher.listenPort);
			byte[] data = new byte[dispatcher.maxReadBufferLength];
			packet = new DatagramPacket(data, 0, data.length);
		}
	}

	private void closeSocket()
	{
		try
		{
			serverSocket.close();
		}
		finally
		{
			serverSocket = null;
		}
	}

	@Override
	public void start()
	{
		if (isRunning())
			destroy();
		try
		{
			openSocket();
			analyzer.start();

			super.start();
		}
		catch (Exception ex)
		{
			debugMonitor(ex);
		}
	}

	@Override
	public void stop()
	{
		isPrepareToStop = true;
		destroy();
	}

	@Override
	public void destroy()
	{
		if (!isRunning())
		{
			return;
		}

		try
		{
			analyzer.stop();
		}
		catch (Exception e)
		{

		}

		try
		{
			closeSocket();
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
		finally
		{
			super.destroy();
		}
	}

	@Override
	public void process() throws Exception
	{
		// TODO Auto-generated method stub
		if (!isRunning())
		{
			stop();
			return;
		}

		try
		{
			while (isRunning())
			{
				serverSocket.receive(packet);
				analyzer.createObject(packet, null);
				
				byte[] data = new byte[dispatcher.maxReadBufferLength];
				packet = new DatagramPacket(data, 0, data.length);
			}
		}
		catch (SocketException se)
		{
			if (!isPrepareToStop)
			{
				throw se;
			}
		}
		catch (SocketTimeoutException ste)
		{
			// TODO: Do nothing
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	public void debugMonitor(Object message)
	{
		if (dispatcher != null)
			dispatcher.debugMonitor(message);
	}
}
