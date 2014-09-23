package com.crm.ascs.thread;

import java.util.Vector;

import com.crm.ascs.net.Trigger;
import com.crm.ascs.net.TriggerCollection;
import com.crm.ascs.tcp.collector.CollectorServer;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class CollectorThread extends DispatcherThread
{
	public int					listenPort			= 3000;
	public int					maxConnection		= 10;
	public int					socketBufferLength	= 65536;
	public int					maxReadBufferLength	= 65536;
	public int					networkTimeout		= 3000;
	public int					triggerTimeout		= 120000;

	public int					queueLogInterval	= 3000;

	public CollectorServer		collectorServer		= null;

	public TriggerCollection	workQueue			= new TriggerCollection();

	private long				lastTimelog			= System.currentTimeMillis();

	public void addWork(Trigger trigger)
	{
		workQueue.put(trigger);
	}

	public Trigger getWork()
	{
		return (Trigger) workQueue.get();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createIntegerParameter("listenPort", "Listen port."));
		vtReturn.add(ThreadUtil.createIntegerParameter("maxConnection", "Maximum connection the server can handle."));
		vtReturn.add(ThreadUtil.createIntegerParameter("socketBufferLength",
				"Socket buffer size, in KB. Default 1500KB."));
		vtReturn.add(ThreadUtil.createIntegerParameter("maxReadBufferLength",
				"Read buffer size, in KB. Default 1500KB."));
		vtReturn.add(ThreadUtil.createIntegerParameter("networkTimeout",
				"Time out to wait for accept connection or read from client."));
		vtReturn.add(ThreadUtil.createIntegerParameter("triggerTimeout", "Time to live of trigger (seconds)."));
		vtReturn.add(ThreadUtil.createIntegerParameter("queueLogInterval", "Local queue log interval (second). Default 10s."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		listenPort = ThreadUtil.getInt(this, "listenPort", 3000);
		maxConnection = ThreadUtil.getInt(this, "maxConnection", 100);
		socketBufferLength = ThreadUtil.getInt(this, "socketBufferLength", 1500) * 1024;
		maxReadBufferLength = ThreadUtil.getInt(this, "maxReadBufferLength", 1500) * 1024;
		networkTimeout = ThreadUtil.getInt(this, "networkTimeout", 1000);
		triggerTimeout = ThreadUtil.getInt(this, "triggerTimeout", 120) * 1000;
		queueLogInterval = ThreadUtil.getInt(this, "queueLogInterval", 120) * 1000;

		super.fillDispatcherParameter();
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		collectorServer = new CollectorServer(this);

		collectorServer.start();
	}

	@Override
	public void afterProcessSession() throws Exception
	{
		try
		{
			collectorServer.stop();
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
		finally
		{
			collectorServer = null;
			super.afterProcessSession();
		}
	}

	@Override
	public void doProcessSession() throws Exception
	{
		long logInterval = queueLogInterval;
		if (lastTimelog + logInterval <= System.currentTimeMillis())
		{
			debugMonitor("Local queue size: " + workQueue.size());

			lastTimelog = System.currentTimeMillis();
		}
	}
}
