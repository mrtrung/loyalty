package com.crm.product.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;

import com.fss.util.AppException;

public class MaxSMSPromotionLoadBatchThread extends DispatcherThread
{
	protected String	channel			= "";
	protected String	serviceAddress	= "";
	protected String	shipTo			= "";
	protected String	keyword			= "";
	protected String	deliveryUser	= "";
	protected int		orderTimeout	= 60000;
	int					batchSize		= 100;
	int					miDelayLop		= 0;

	String				sqlSelect;
	Connection			connection		= null;
	PreparedStatement	stmtSelect		= null;
	ResultSet			rsSelect		= null;
	Vector				vtData			= new Vector();

	String				strSQLUpdate;
	PreparedStatement	stmtUpdate		= null;

	Vector				vtSQLConfig		= new Vector();

	public MaxSMSPromotionLoadBatchThread()
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
		vtReturn.addElement(ThreadUtil.createTextParameter("sqlSelect", 500, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("sqlUpdate", 500, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("deliveryUser", 30, ""));
		vtReturn.addElement(ThreadUtil.createComboParameter("channel", "SMS,web", ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("serviceAddress", 30, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("keyword", 30, ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("orderTimeout", "Time to live of order (s)."));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("batchSize", ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("loopTimeDelay", ""));
		vtReturn.addAll(ThreadUtil.createQueueParameter(this));

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
			sqlSelect = ThreadUtil.getString(this, "sqlSelect", true, "");
			strSQLUpdate = ThreadUtil.getString(this, "sqlUpdate", true, "");
			deliveryUser = ThreadUtil.getString(this, "deliveryUser", false, "");
			channel = ThreadUtil.getString(this, "channel", false, "");
			serviceAddress = ThreadUtil.getString(this, "serviceAddress", false, "");
			keyword = ThreadUtil.getString(this, "keyword", false, "");
			orderTimeout = ThreadUtil.getInt(this, "orderTimeout", 60000);
			batchSize = ThreadUtil.getInt(this, "batchSize", 100);
			miDelayLop = ThreadUtil.getInt(this, "loopTimeDelay", 100);
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
	protected void beforeSession() throws Exception
	{
		long startTime = System.currentTimeMillis();

		logMonitor("Loading record...........");
		connection = Database.getConnection();
		stmtSelect = connection.prepareStatement(sqlSelect);
		stmtSelect.setInt(1, Constants.STATUS_INACTIVE);
		rsSelect = stmtSelect.executeQuery();
		rsSelect.setFetchSize(10000);
		vtData = com.fss.sql.Database.convertToVector(rsSelect);

		stmtUpdate = connection.prepareStatement(strSQLUpdate);
		super.beforeSession();

		long endTime = System.currentTimeMillis();
		long totalTime = (endTime - startTime) / 1000;
		logMonitor("Load record time: " + totalTime + "s");
	}

	@Override
	public void processSession() throws Exception
	{
		try
		{
			beforeProcessSession();

			doProcessSession();
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			afterProcessSession();
		}
	}

	public void doProcessSession() throws Exception
	{
		int executeBatchSize = 500;
		int batchCounter = 0;
		try
		{
			// ///////////////////////////////////////////
			logMonitor("Updating record...............");
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < vtData.size(); i++)
			{
				batchCounter++;
				try
				{
					Vector vtRow = (Vector) vtData.elementAt(i);
					String isdn = vtRow.elementAt(1).toString();
					String id = vtRow.elementAt(0).toString();
					stmtUpdate.setInt(1, Constants.STATUS_PROCESSING);
					stmtUpdate.setString(2, id);
					stmtUpdate.addBatch();
					if (batchCounter == executeBatchSize)
					{
						stmtUpdate.executeBatch();
						batchCounter = 0;
					}
				}
				catch (Exception ex)
				{
					debugMonitor(ex);
				}
			}
			if (batchCounter > 0)
			{
				stmtUpdate.executeBatch();
			}
			long endTime = System.currentTimeMillis();
			long totalTime = (endTime - startTime) / 1000;
			logMonitor("Update record time: " + totalTime + "s");

			Vector vtRow = new Vector();
			int loopCounter = 0;

			logMonitor("Records are processing...............");
			for (int i = 0; i < vtData.size(); i++)
			{
				loopCounter++;
				vtRow = (Vector) vtData.elementAt(i);
				String id = vtRow.elementAt(0).toString();
				String isdn = vtRow.elementAt(1).toString();

				CommandMessage order = new CommandMessage();
				order.setResponseValue("primaryKey", id);
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
				order.setIsdn(isdn);
				order.setTimeout(orderTimeout);
				order.setKeyword(keyword);

				MQConnection connection = null;
				try
				{
					connection = getMQConnection();
					connection.sendMessage(order, "", 0, queueWorking, orderTimeout * 1000
								, new String[] { "SystemID" }, new Object[] { new String(order.getUserName()) }, queuePersistent);
				}
				catch (Exception ex)
				{
					debugMonitor(ex);
				}
				finally
				{
					returnMQConnection(connection);
				}

				logMonitor(order.toLogString());
				if (loopCounter == batchSize)
				{
					loopCounter = 0;
					Thread.sleep(miDelayLop);
				}

			}

		}
		catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	protected void afterSession() throws Exception
	{
		Database.closeObject(stmtSelect);
		Database.closeObject(stmtUpdate);
		Database.closeObject(rsSelect);
		Database.closeObject(connection);
		vtData.clear();
		super.afterSession();
	}
}
