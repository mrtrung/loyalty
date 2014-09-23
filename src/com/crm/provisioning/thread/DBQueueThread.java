package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.jms.Queue;

import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author: HungPQ
 * @version 1.0
 */

public class DBQueueThread extends DispatcherThread
{
	private PreparedStatement	_stmtQueue		= null;
	private PreparedStatement	_stmtRemove		= null;
	private Connection _conn = null;

	private String				_sqlCommand		= "";
	private int					_minFreeSize	= 15000;

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("SQLCommand", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));

		vtReturn.addElement(createParameterDefinition("MinFreeSize", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));

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
			super.fillParameter();

			setSQLCommand(loadMandatory("SQLCommand"));

			try
			{
				setMinFreeSize(Integer.parseInt(loadMandatory("MinFreeSize")));
			}
			catch (Exception e)
			{
				setMinFreeSize(15000);
			}
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
	// after process session
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

			strSQL = "Delete CommandRequest Where requestId = ?";
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

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void doProcessSession() throws Exception
	{
		long counter = 0;
		ResultSet rsQueue = null;

		try
		{
			Queue checkQueue = QueueFactory.getQueue(queueName);

			rsQueue = _stmtQueue.executeQuery();
			debugMonitor("Scanning database queue ... ");

			MQConnection connection = null;

			try
			{
				connection = getMQConnection();
				while (rsQueue.next() && isAvailable())
				{
					boolean bypass = false;

					int sizeOrder = connection.getQueueSize(checkQueue);

					if (sizeOrder >= _minFreeSize)
					{
						bypass = true;
						debugMonitor("Too many order in queue: " + sizeOrder);
					}

					if (!bypass)
					{
						CommandMessage order = pushOrder(rsQueue.getString("isdn"),
								rsQueue.getString("serviceaddress"), rsQueue.getString("channel"),
								rsQueue.getString("keyword"));

						connection.sendMessage(order, QueueFactory.ORDER_REQUEST_QUEUE, 0, queuePersistent);

						_stmtRemove.setLong(1, rsQueue.getLong("requestId"));
						_stmtRemove.execute();
						_conn.commit();
						logMonitor(order.toString());

						counter++;
					}
					else
					{
						break;
					}
				}
			}
			finally
			{
				returnMQConnection(connection);
			}

			if (counter > 0)
			{
				debugMonitor("Total transfer record :" + counter);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			_conn.commit();
			Database.closeObject(rsQueue);
		}
	}

	public CommandMessage pushOrder(String isdn, String product,
			String channel, String keyword) throws Exception
	{
		CommandMessage order = new CommandMessage();

		try
		{
			order.setChannel(channel);
			order.setUserId(0);
			order.setUserName("system");

			order.setServiceAddress(product);
			order.setIsdn(isdn);
			order.setKeyword(keyword);
		}
		catch (Exception e)
		{
			throw e;
		}
		return order;
	}

	public void setMinFreeSize(int _minFreeSize)
	{
		this._minFreeSize = _minFreeSize;
	}

	public int getMinFreeSize()
	{
		return _minFreeSize;
	}

	public void setSQLCommand(String _sqlCommand)
	{
		this._sqlCommand = _sqlCommand;
	}

	public String getSQLCommand()
	{
		return _sqlCommand;
	}
}
