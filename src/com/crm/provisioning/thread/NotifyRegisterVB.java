package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class NotifyRegisterVB extends DispatcherThread
{
	protected PreparedStatement _stmtQueue = null;
	protected PreparedStatement _stmtRemove = null;
	private Connection _conn = null;
	
	protected String _sqlCommand = null;
	protected String _orderQueueName = "";
	protected String _contentVB600 = "";
	protected String _contentVB220 = "";
	protected int _ExpiredTime = 10;
	{

	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("SQLCommand", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("SMSContentVB600", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("SMSContentVB220", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("ExpiredTime", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("OrderQueueName", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		
		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	public void fillParameter() throws AppException
	{
		try
		{
			_sqlCommand = loadMandatory("SQLCommand");
			_contentVB220 = loadMandatory("SMSContentVB220");
			_contentVB600 = loadMandatory("SMSContentVB600");
			_ExpiredTime = Integer.valueOf(loadMandatory("ExpiredTime"));
			_orderQueueName = loadMandatory("OrderQueueName");

			super.fillParameter();
		}
		catch (AppException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		try
		{
			String strSQL = getSQLCommand();
			_conn = Database.getConnection();
			_stmtQueue = _conn.prepareStatement(strSQL);

			strSQL = "delete REGISTERVB Where isdn = ? and productid = ?";
			_stmtRemove = _conn.prepareStatement(strSQL);

		}
		catch (Exception e)
		{
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// after process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void afterProcessSession() throws Exception
	{
		try
		{
			Database.closeObject(_stmtQueue);
			Database.closeObject(_stmtRemove);
			Database.closeObject(_conn);
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

	public void doProcessSession() throws Exception
	{
		int batchCount = 0;
		MQConnection connection = null;
		try
		{
			connection = getMQConnection();
			// Luu y truong hop connection bi dut ket noi

			ResultSet result = _stmtQueue.executeQuery();

			Date currentDate = new Date();

			String isdn = "";
			String content = "";
			String serviceAddress = "";
			int productid;
			
			while (result.next())
			{
				Timestamp dRegisterDate = result.getTimestamp("orderdate");
				if (currentDate.getTime() - dRegisterDate.getTime() > _ExpiredTime * 60 * 1000)
				{

					isdn = result.getString("isdn");
					productid = result.getInt("productid");

					ProductEntry product = ProductFactory.getCache()
							.getProduct(productid);

					if ("IDDBUFFET".equals(product.getAlias().toUpperCase()))
					{
						serviceAddress = "1720";
						content = _contentVB600;
					}
					else if ("VB220".equals(product.getAlias().toUpperCase()))
					{
						serviceAddress = "1721";
						content = _contentVB220;
					}

					CommandMessage order = pushOrder(isdn, serviceAddress,
							content);
					connection.sendMessage(order, _orderQueueName, 0, queuePersistent);

					_stmtRemove.setString(1, isdn);
					_stmtRemove.setLong(2, productid);
					_stmtRemove.addBatch();
					batchCount++;
					
					logMonitor(isdn + " - remove confirm register " + product.getAlias());
				}
			}
			result.close();
			if (batchCount > 0)
			{
				_stmtRemove.executeBatch();
			}
		}
		catch (Exception ex)
		{
			_logger.error("NotifyRegisterVB: " + ex.getMessage());
			logMonitor(ex.getMessage());
		}
		finally
		{
			returnMQConnection(connection);
			_conn.commit();
		}
	}

	public CommandMessage pushOrder(String isdn, String serviceAddress,
			String request) throws Exception
	{
		CommandMessage order = new CommandMessage();

		try
		{
			order.setChannel("SMS");
			order.setUserId(0);
			order.setUserName("system");

			order.setServiceAddress(serviceAddress);
			order.setIsdn(isdn);
			order.setRequest(request);
		}
		catch (Exception e)
		{
			throw e;
		}
		return order;
	}

	private static Logger _logger = Logger.getLogger(NotifyRegisterVB.class);

	public void setSQLCommand(String _sqlCommand)
	{
		this._sqlCommand = _sqlCommand;
	}

	public String getSQLCommand()
	{
		return _sqlCommand;
	}
}
