package com.crm.product.thread.autorenew;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.DateUtil;
import com.crm.util.StringUtil;
import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;

public class DailyRenewScandThread extends AbtractAutoRenewThread
{
	protected int								batchSize			= 100;
	protected String							deliveryUser		= "";
	protected int								orderTimeout		= 60000;
	protected int								timeBeetweenLoop	= 100;
	protected long								countProcess		= 0;

	protected int								conditionQueueSize	= 100;
	protected long								conditionDelay		= 1000;

	protected Vector							vtSubscriptionClass	= new Vector();
	protected Vector							vtSchedule			= new Vector();
	protected String							startTime			= "";
	protected String							endTime				= "";

	protected Map<String, AbstractAutoRenew>	mAutorenewClass		= new HashMap<String, AbstractAutoRenew>();

	Connection									connection			= null;
	ResultSet									rs					= null;
	protected PreparedStatement					stmtSelect			= null;
	protected PreparedStatement					stmtRemove			= null;
	protected PreparedStatement					stmtUpdate			= null;

	protected String							strSQLSelect;
	protected String							strSQLTruncate;
	protected String							strSQLUpdate;
	protected int								fetchSize			= 0;
	protected boolean							bCheckSchedule		= false;

	public DailyRenewScandThread()
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
		vtReturn.addElement(ThreadUtil.createTextParameter("deliveryUser", 30, ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("orderTimeout", "Time to live of order (s)."));
		vtReturn.addElement(ThreadUtil.createTextParameter("timeBetweenLoop", 300, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("batchSize", 300, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("fetchSize", 300, "Fetch size for result set"));
		vtReturn.addElement(ThreadUtil.createTextParameter("SQLSelect", 300, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("SQLStruncate", 300, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("SQLUpdate", 300, ""));

		vtReturn.addElement(ThreadUtil.createIntegerParameter("conditionQueueSize", ""));
		vtReturn.addElement(ThreadUtil.createIntegerParameter("conditionDelay", ""));

		Vector vtValue = new Vector();
		vtValue.addElement(createParameterDefinition("ProductId", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "0"));
		vtValue.addElement(createParameterDefinition("ProcessClass", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "1"));
		vtValue.addElement(createParameterDefinition("Channel", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "2"));
		vtValue.addElement(createParameterDefinition("ServiceAddress", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "3"));
		vtValue.addElement(createParameterDefinition("keyword", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "4"));
		vtReturn.addElement(createParameterDefinition("SubscriptionConfig", "", ParameterType.PARAM_TABLE, vtValue,
				"Subscription Config"));

		Vector vtSchedule = new Vector();
		vtSchedule.addElement(createParameterDefinition("StartTime", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "0"));
		vtSchedule.addElement(createParameterDefinition("EndTime", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "1"));
		vtReturn.addElement(createParameterDefinition("ScheduleConfig", "", ParameterType.PARAM_TABLE, vtSchedule,
				"Schedule config"));

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
			deliveryUser = ThreadUtil.getString(this, "deliveryUser", false, "");
			orderTimeout = ThreadUtil.getInt(this, "orderTimeout", 60000);
			strSQLSelect = ThreadUtil.getString(this, "SQLSelect", true, "");
			strSQLTruncate = ThreadUtil.getString(this, "SQLStruncate", true, "");
			strSQLUpdate = ThreadUtil.getString(this, "SQLUpdate", true, "");
			timeBeetweenLoop = ThreadUtil.getInt(this, "timeBetweenLoop", 100);
			batchSize = ThreadUtil.getInt(this, "batchSize", 100);
			fetchSize = ThreadUtil.getInt(this, "fetchSize", 200);
			conditionQueueSize = ThreadUtil.getInt(this, "conditionQueueSize", 100);
			conditionDelay = ThreadUtil.getInt(this, "conditionDelay", 1000);

			vtSubscriptionClass = new Vector();
			Object obj = getParameter("SubscriptionConfig");
			if (obj != null && (obj instanceof Vector))
			{
				vtSubscriptionClass = (Vector) ((Vector) obj).clone();
			}

			Object objSchedule = getParameter("ScheduleConfig");
			if (objSchedule != null && (objSchedule instanceof Vector))
			{
				vtSchedule = (Vector) ((Vector) objSchedule).clone();
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
	protected void beforeSession() throws Exception
	{
		for (int i = 0; i < vtSchedule.size(); i++)
		{
			Vector vtRow = (Vector) vtSchedule.elementAt(i);
			startTime = vtRow.elementAt(0).toString();
			endTime = vtRow.elementAt(1).toString();
		}

		bCheckSchedule = checkSchedule(startTime, endTime);

		if (bCheckSchedule)
		{

			try
			{
				// INIT ABTRACT CLASS
				for (int i = 0; i < vtSubscriptionClass.size(); i++)
				{
					Vector vtRow = (Vector) vtSubscriptionClass.elementAt(i);

					String mstrKey = vtRow.elementAt(0).toString();
					String mstrProcessClass = vtRow.elementAt(1).toString();
					String mstrChannel = vtRow.elementAt(2).toString();
					String mstrServiceAddress = vtRow.elementAt(3).toString();
					String mstrKeyword = vtRow.elementAt(4).toString();

					AbstractAutoRenew abstractAutoRenew = (AbstractAutoRenew) Class.forName(mstrProcessClass).newInstance();
					abstractAutoRenew.setChanel(mstrChannel);
					abstractAutoRenew.setKeyword(mstrKeyword);
					abstractAutoRenew.setServiceAddress(mstrServiceAddress);
					abstractAutoRenew.setOrderTimeOut(orderTimeout);
					abstractAutoRenew.setDeliveryUser(deliveryUser);
					abstractAutoRenew.setAbtractAutoRenewThread(this);
					mAutorenewClass.put(mstrKey, abstractAutoRenew);
				}

				// TRUNCATE TABLE
				logMonitor("Truncating table.....");
				Long startTruncate = System.currentTimeMillis();
				connection = Database.getConnection();
				stmtRemove = connection.prepareStatement(strSQLTruncate);
				stmtRemove.executeUpdate();
				Long endTruncate = System.currentTimeMillis();
				logMonitor("Total truncate times: " + (endTruncate - startTruncate) / 1000 + "s");

				// LOAD RECORE
				Long startTime = System.currentTimeMillis();
				logMonitor("Records are loading.....");

				stmtSelect = connection.prepareStatement(strSQLSelect);
				rs = stmtSelect.executeQuery();
				rs.setFetchSize(fetchSize);

				Long endTime = System.currentTimeMillis();
				logMonitor("Total load records times: " + (endTime - startTime) / 1000 + "s");

				// INIT UPDATE STATEMENT
				connection.setAutoCommit(false);
				stmtUpdate = connection.prepareStatement(strSQLUpdate);

			}
			catch (Exception ex)
			{
				throw ex;
			}
		}
	}

	@Override
	public void processSession() throws Exception
	{
		if (bCheckSchedule)
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
				super.afterProcessSession();
			}
		}
		else
		{
			logMonitor("Schedule not avaiable. Thread will start from " + startTime + " to " + endTime + ".");
			Thread.sleep(calculateTime(startTime, endTime));
		}
	}

	public void doProcessSession() throws Exception
	{
		try
		{
			int loopCounter = 0;

			CommandMessage commandMessage = null;
			logMonitor("Record are processing.....");
			long startTime = System.currentTimeMillis();
			AbstractAutoRenew abstractAutoRenew = null;

			while (rs.next())
			{
				String mstrISDN = "";
				String mstrProductId = "";
				try
				{
					String mLastRowId = rs.getString("rowid");
					mstrISDN = rs.getString("isdn");
					mstrProductId = rs.getString("productid");

					// UPDATE DESCRIPTION
					stmtUpdate.setString(1, mLastRowId);
					stmtUpdate.executeUpdate();

					// PROCESS RECORD
					abstractAutoRenew = mAutorenewClass.get(mstrProductId);
					commandMessage = new CommandMessage();
					commandMessage.setProductId(Long.parseLong(mstrProductId));
					abstractAutoRenew.processRecord(mstrISDN, commandMessage);

					connection.commit();

					logMonitor("SUCCESS - ISDN: " + mstrISDN + " - PRODUCTID: " + mstrProductId);

					loopCounter++;
					if (loopCounter >= batchSize)
					{
						loopCounter = 0;
						Thread.sleep(timeBeetweenLoop);
					}
				}
				catch (Exception ex)
				{
					connection.rollback();
					logMonitor(ex.getMessage());
				}
			}
			long endTime = System.currentTimeMillis();
			logMonitor("Total process times: " + (endTime - startTime) / 1000 + "s");

		}
		catch (Exception e)
		{
			debugMonitor(e.getMessage());
			throw e;
		}
		finally
		{
			connection.setAutoCommit(true);
		}
	}

	@Override
	protected void afterSession() throws Exception
	{
		Database.closeObject(stmtRemove);
		Database.closeObject(stmtSelect);
		Database.closeObject(stmtUpdate);
		Database.closeObject(rs);

		Database.closeObject(connection);

		super.afterSession();
	}

	@Override
	public void attachQueue(CommandMessage order) throws Exception
	{
		MQConnection connection = null;
		try
		{
			connection = getMQConnection();

			// Check condition queue size
			int queueSize = 0;
			if ((queueSize = connection.getQueueSize(queueWorking)) >= conditionQueueSize)
			{
				logMonitor("Curent queue size: " + queueSize + " more than condition queue size: " + conditionQueueSize);
				while ((queueSize = connection.getQueueSize(queueWorking)) >= conditionQueueSize)
				{
					Thread.sleep(conditionDelay);
				}
			}

			connection.sendMessage(order, "", 0, queueWorking, orderTimeout * 1000
					, new String[] { "SystemID" }, new Object[] { new String(order.getUserName()) }, queuePersistent);
		}
		catch (Exception ex)
		{
			order.setCause(Constants.ERROR);
			order.setDescription(ex.getMessage());
			logMonitor(order.toLogString());
			throw ex;
		}
		finally
		{
			returnMQConnection(connection);
		}
		order.setCause(Constants.SUCCESS);
	}
}
