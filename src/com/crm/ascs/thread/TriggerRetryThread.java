package com.crm.ascs.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.jms.Message;
import javax.jms.MessageProducer;

import com.crm.ascs.net.Trigger;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class TriggerRetryThread extends DispatcherThread
{
	private Connection			sqlConnection		= null;
	public String				sqlWhereClause		= "";
	public int					triggerTimeout		= 1200;

	public String				tableName			= "";

	private PreparedStatement	stmtGetTrigger		= null;
	private PreparedStatement	stmtUpdateTrigger	= null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		// vtReturn.add(ThreadUtil.createTextParameter("subscriptionProductsList",
		// 400, "List of products need to subscribe, separate by comma (,)."));
		// vtReturn.add(ThreadUtil.createTextParameter("subscriptionChannel",
		// 400,
		// "Channel of subscription request will send to, \"SMS\" or \"web\"."));
		vtReturn.add(ThreadUtil.createTextParameter("sqlWhereClause", 400, "Where clause of get trigger query."));
		vtReturn.add(ThreadUtil.createTextParameter("tableName", 400, "Table to get trigger for retrying."));
		vtReturn.add(ThreadUtil.createIntegerParameter("triggerTimeout", "Life time of trigger, in second."));
		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		// TODO Auto-generated method stub
		super.fillDispatcherParameter();
		sqlWhereClause = ThreadUtil.getString(this, "sqlWhereClause", false, "");
		tableName = ThreadUtil.getString(this, "tableName", false, "");
		triggerTimeout = ThreadUtil.getInt(this, "triggerTimeout", 1200);
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		try
		{
			sqlConnection = Database.getConnection();
			String sql = "Select * from " + tableName + " where 1=1 ";
			if (!sqlWhereClause.equals(""))
				sql = sql + " and " + sqlWhereClause;
			stmtGetTrigger = sqlConnection.prepareStatement(sql);

			String strSQL = "Update " + tableName
						+ " Set status = " + Trigger.STATUS_PENDING
						+ " Where Id = ?";
			stmtUpdateTrigger = sqlConnection.prepareStatement(strSQL);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	public void afterProcessSession() throws Exception
	{
		try
		{
			Database.closeObject(stmtGetTrigger);
			Database.closeObject(stmtUpdateTrigger);
			Database.closeObject(sqlConnection);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	@Override
	public void doProcessSession() throws Exception
	{
		ResultSet rsQueue = stmtGetTrigger.executeQuery();
		int count = 0;

		MQConnection connection = null;
		MessageProducer producer = null;
		try
		{
			connection = getMQConnection();

			while (rsQueue.next() && isAvailable())
			{
				Trigger trigger = Trigger.createTrigger(rsQueue.getString("CONTENT"));
				trigger.setTriggerId(rsQueue.getLong("ID"));
				trigger.setReceiveDate(rsQueue.getDate("RECEIVE_DATE"));
				trigger.setStatus(rsQueue.getInt("STATUS"));
				trigger.setTimeout(triggerTimeout * 1000);

				try
				{
					if (producer == null)
					{
						producer = QueueFactory.createQueueProducer(connection.getSession(), queueWorking, trigger.getTimeout(),
								queuePersistent);
					}

					Message message = QueueFactory.createObjectMessage(connection.getSession(), trigger);
					producer.send(message);
					debugMonitor("Sent to queue: " + trigger.toLogString());
					// add to batch
					stmtUpdateTrigger.setLong(1, trigger.getTriggerId());
					stmtUpdateTrigger.execute();
					count++;
				}
				catch (Exception e)
				{
					debugMonitor(e);
				}
			}

			logMonitor("Processed " + count + " triggers.");
		}
		finally
		{
			returnMQConnection(connection);
		}
	}
}
