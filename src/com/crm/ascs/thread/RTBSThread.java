package com.crm.ascs.thread;

import java.util.Vector;

import com.crm.ascs.ccws.CCWSConnection;
import com.crm.ascs.ccws.CCWSConnectionPool;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class RTBSThread extends DispatcherThread
{
	public String				host			= "";
	public String				userName		= "";
	public String				password		= "";
	public int					timeout			= 30000;

	public int					maxActive		= 30;
	public long					maxWait			= 30000;
	public int					maxIdle			= 30000;
	public boolean				lifo			= true;
	public String				testCommand		= "";

	public CCWSConnectionPool	connectionPool	= null;

	@Override
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();

			host = ThreadUtil.getString(this, "host", true, "");
			userName = ThreadUtil.getString(this, "userName", true, "");
			password = ThreadUtil.getString(this, "password", true, "");
			timeout = ThreadUtil.getInt(this, "timeout", 30000);

			maxActive = ThreadUtil.getInt(this, "maxActive", 30);
			maxWait = ThreadUtil.getInt(this, "maxWait", 30000);
			maxIdle = ThreadUtil.getInt(this, "maxIdle", 30000);
			lifo = ThreadUtil.getBoolean(this, "lifo", true);

			testCommand = ThreadUtil.getString(this, "testCommand", false, "");
		}
		catch (AppException e)
		{
			logMonitor(e);

			throw e;
		}
		catch (Exception e)
		{
			logMonitor(e);

			throw new AppException(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();
		
		vtReturn.addElement(ThreadUtil.createTextParameter("host", 100, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("userName", 100, ""));
		vtReturn.addElement(ThreadUtil.createPasswordParameter("password", ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("timeout", "wait response in miliseconds"));

		vtReturn.addElement(ThreadUtil.createIntegerParameter("maxActive",
				"maximum number of objects that can be allocated in the pool "));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("maxWait", "maximum amount of time (in milliseconds)"));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("maxIdle",
				"maximum number of objects that can sit idle in the pool"));
		vtReturn.addElement(
				ThreadUtil.createBooleanParameter(
						"lifo", "determines whether or not the pool returns idle objects in last-in-first-out order"));

		vtReturn.addAll(super.getDispatcherDefinition());
		return vtReturn;
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		try
		{
			if (connectionPool != null)
			{
				connectionPool.close();
			}

			if (connectionPool == null)
			{
				connectionPool = new CCWSConnectionPool(this, host, userName, password);
			}

			connectionPool.setMaxWait(timeout);
			connectionPool.setMaxActive(maxActive);
			connectionPool.setMaxIdle(maxIdle);
			connectionPool.setMaxWait(maxWait);
			connectionPool.setLifo(lifo);

			connectionPool.open();
		}
		catch (Exception e)
		{
			throw e;
		}

		CCWSConnection testConnection = null;

		try
		{
			logMonitor("Testing connection ...");

			testConnection = connectionPool.getConnection();

			logMonitor("Testing connection are success");
		}
		catch (Exception e)
		{
			logMonitor("open connection is fail");

			logMonitor(e);
		}
		finally
		{
			connectionPool.returnConnection(testConnection);
		}

		super.beforeProcessSession();
	}

	@Override
	public void afterProcessSession() throws Exception
	{
		try
		{
			if (connectionPool != null)
			{
				connectionPool.close();
			}
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	public CCWSConnection getCCWSConnection() throws Exception
	{
		return connectionPool.getConnection();
	}

	public void returnCCWSConnection(CCWSConnection connection) throws Exception
	{
		connectionPool.returnConnection(connection);
	}
}
