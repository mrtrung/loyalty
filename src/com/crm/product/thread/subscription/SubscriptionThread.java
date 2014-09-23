package com.crm.product.thread.subscription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;
import java.util.Map;

public class SubscriptionThread extends DispatcherThread
{
	protected int								batchSize			= 100;
	protected String							deliveryUser		= "";
	protected int								orderTimeout		= 60000;
	protected int								timeBeetweenLoop	= 100;

	protected Vector							vtSubscriptionClass	= new Vector();
	protected Map<String, AbstractSubscription>	mSubscriptionClass	= new HashMap<String, AbstractSubscription>();

	Connection									connection			= null;
	ResultSet									rs					= null;
	protected PreparedStatement					stmtSelect			= null;
	protected PreparedStatement					stmtUpdate			= null;
	protected PreparedStatement					stmtInsert			= null;

	protected boolean							checkInsertBatch	= false;
	protected boolean							checkUpdateBatch	= false;

	protected String							strSQLSelect;
	protected String							strSQLUpdate		= "Update SubscriberProduct Set lastRunDate = SysDate, subscriptionStatus = 1 Where subProductId = ?";
	protected String							strSQLInsert		= "insert into CommandRequest "
																			+ "		(requestId, userName, createDate, requestDate "
																			+ "		, channel, serviceAddress, isdn, keyword) "
																			+ " values "
																			+ "		(command_seq.nextval, ?, SysDate, SysDate"
																			+ "		, ?, ?, ?, ?) ";

	public SubscriptionThread()
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

		vtReturn.addElement(ThreadUtil.createTextParameter("SQLCommand", 300, ""));

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
			strSQLSelect = ThreadUtil.getString(this, "SQLCommand", true, "");
			timeBeetweenLoop = ThreadUtil.getInt(this, "timeBetweenLoop", 100);
			batchSize = ThreadUtil.getInt(this, "batchSize", 100);

			vtSubscriptionClass = new Vector();
			Object obj = getParameter("SubscriptionConfig");
			if (obj != null && (obj instanceof Vector))
			{
				vtSubscriptionClass = (Vector) ((Vector) obj).clone();
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
		try
		{
			// Init map
			for (int i = 0; i < vtSubscriptionClass.size(); i++)
			{
				Vector vtRow = (Vector) vtSubscriptionClass.elementAt(i);

				String mstrKey = vtRow.elementAt(0).toString();
				String mstrProcessClass = vtRow.elementAt(1).toString();
				String mstrChannel = vtRow.elementAt(2).toString();
				String mstrServiceAddress = vtRow.elementAt(3).toString();
				String mstrKeyword = vtRow.elementAt(4).toString();

				AbstractSubscription abstractSubscription = (AbstractSubscription) Class.forName(mstrProcessClass)
						.newInstance();
				abstractSubscription.setChanel(mstrChannel);
				abstractSubscription.setKeyword(mstrKeyword);
				abstractSubscription.setServiceAddress(mstrServiceAddress);
				abstractSubscription.setOrderTimeOut(orderTimeout);
				abstractSubscription.setDeliveryUser(deliveryUser);
				abstractSubscription.setSubscriptionThread(this);
				mSubscriptionClass.put(mstrKey, abstractSubscription);
			}

			// Init statement
			Long startTime = System.currentTimeMillis();
			logMonitor("Loading records.....");

			connection = Database.getConnection();
			stmtInsert = connection.prepareStatement(strSQLInsert);
			stmtUpdate = connection.prepareStatement(strSQLUpdate);
			stmtSelect = connection.prepareStatement(strSQLSelect);
			rs = stmtSelect.executeQuery();
			rs.setFetchSize(1000);

			Long endTime = System.currentTimeMillis();
			logMonitor("Total times: " + (endTime - startTime) / 1000 + "s");

		}
		catch (Exception ex)
		{
			throw ex;
		}
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
			super.afterProcessSession();
		}
	}

	public void doProcessSession() throws Exception
	{
		try
		{
			int executeBatchSize = 1000;
			int batchCounter = 0;
			int loopCounter = 0;

			CommandMessage commandMessage = null;
			logMonitor("Record are processing.....");
			long startTime = System.currentTimeMillis();
			while (rs.next())
			{
				String mstrSubProductId = rs.getString("subproductid");
				String mstrISDN = rs.getString("isdn");
				String mstrProductId = rs.getString("productid");
				AbstractSubscription abstractSubscription = mSubscriptionClass.get(mstrProductId);

				stmtUpdate.setString(1, mstrSubProductId);
				stmtUpdate.addBatch();
				checkUpdateBatch = true;

				commandMessage = new CommandMessage();
				abstractSubscription.processRecord(mstrISDN, commandMessage);

				batchCounter++;

				if (batchCounter >= executeBatchSize)
				{
					if (checkUpdateBatch)
					{
						stmtUpdate.executeBatch();
						checkUpdateBatch = false;
					}
					if (checkInsertBatch)
					{
						stmtInsert.executeBatch();
						checkInsertBatch = false;
					}

					batchCounter = 0;
				}

				logMonitor(commandMessage.toLogString());

				loopCounter++;
				if (loopCounter >= batchSize)
				{
					loopCounter = 0;
					Thread.sleep(timeBeetweenLoop);
				}
			}
			if (batchCounter > 0)
			{
				if (checkUpdateBatch)
				{
					stmtUpdate.executeBatch();
					checkUpdateBatch = false;
				}
				if (checkInsertBatch)
				{
					stmtInsert.executeBatch();
					checkInsertBatch = false;
				}
			}
			long endTime = System.currentTimeMillis();
			logMonitor("Total times: " + (endTime - startTime) / 1000 + "s");
		}
		catch (Exception e)
		{
			debugMonitor(e.getMessage());
			throw e;
		}
	}

	@Override
	protected void afterSession() throws Exception
	{
		Database.closeObject(stmtInsert);
		Database.closeObject(stmtSelect);
		Database.closeObject(stmtSelect);
		Database.closeObject(rs);
		Database.closeObject(connection);

		super.afterSession();
	}

	public void attachQueue(CommandMessage order) throws Exception
	{
		MQConnection connection = null;
		try
		{
			connection = getMQConnection();
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
