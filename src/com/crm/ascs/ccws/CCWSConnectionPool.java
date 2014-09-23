package com.crm.ascs.ccws;

import org.apache.commons.pool.impl.GenericObjectPool;

import com.crm.ascs.util.ObjectPool;
import com.crm.thread.DispatcherThread;

public class CCWSConnectionPool extends ObjectPool
{
	private DispatcherThread	dispatcher	= null;
	private String				host		= "";
	private String				username	= "";
	private String				password	= "";
	private int					maxActive	= 10;
	private long				maxWait		= 60000;
	private int					maxIdle		= 10;
	private boolean				lifo		= true;

	public DispatcherThread getDispatcher()
	{
		return dispatcher;
	}

	public void setDispatcher(DispatcherThread dispatcher)
	{
		this.dispatcher = dispatcher;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public int getMaxActive()
	{
		return maxActive;
	}

	public void setMaxActive(int maxActive)
	{
		this.maxActive = maxActive;
	}

	public long getMaxWait()
	{
		return maxWait;
	}

	public void setMaxWait(long maxWait)
	{
		this.maxWait = maxWait;
	}

	public int getMaxIdle()
	{
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle)
	{
		this.maxIdle = maxIdle;
	}

	public boolean isLifo()
	{
		return lifo;
	}

	public void setLifo(boolean lifo)
	{
		this.lifo = lifo;
	}

	public CCWSConnectionPool(DispatcherThread dispatcher, String host, String username, String password)
	{
		this.dispatcher = dispatcher;
		this.host = host;
		this.username = username;
		this.password = password;
	}

	@Override
	public void debugMonitor(Object message)
	{
		if (dispatcher != null)
		{
			dispatcher.debugMonitor(message);
		}
	}

	@Override
	protected GenericObjectPool initPool() throws Exception
	{
		return new GenericObjectPool(this, maxActive, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, maxWait,
				maxIdle, maxIdle, false, false, 0, 0, 0, false, 0, lifo);
	}

	@Override
	protected Object createObject() throws Exception
	{
		CCWSConnection connection = new CCWSConnection();
		try
		{
			connection.setDispatcher(dispatcher);
			connection.setHost(host);
			connection.setUsername(username);
			connection.setPassword(password);
			connection.open();
		}
		catch (Exception e)
		{
			throw e;
		}
		return connection;
	}
	
	public void close()
	{
		try
		{
			destroyPool();
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	public CCWSConnection getConnection() throws Exception
	{
		return (CCWSConnection) getObject();
	}

	public void returnConnection(CCWSConnection connection)
	{
		returnObject(connection);
	}
}
