package com.crm.provisioning.thread;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;


public class DailyChargeScandThread extends DispatcherThread
{
		private PreparedStatement _stmtQueue = null;
		private PreparedStatement _stmtUpdate = null;
		private Connection _conn = null;
		private String _sqlCommand = "";
		private int  _maxRetry = 3;
		private int _restTime = 5;
		private int _bacthSize = 300;

		// //////////////////////////////////////////////////////
		// Override
		// //////////////////////////////////////////////////////
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Vector getParameterDefinition()
		{
			Vector vtReturn = new Vector();

			vtReturn.addElement(createParameterDefinition("SQLCommand", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("RetryTime", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("RestTime", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("BatchSize", "",
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
				
				setMaxRetry(loadInteger("RetryTime"));
				setRestTime(loadInteger("RestTime"));
				setBatchSize(loadInteger("BatchSize"));
				
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
				
				strSQL = "update DAILYRENEWSERVICES set status = ? where isdn = ? and productid = ?";
				_stmtUpdate= _conn.prepareStatement(strSQL);
				
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
				Database.closeObject(_stmtUpdate);
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
			CommandMessage order = null;
			MQConnection connection = null;
			int retryTime = 0;
			int batchCounter = 0;
			try
			{
				connection = getMQConnection();
				rsQueue = _stmtQueue.executeQuery();
				debugMonitor("Scanning database queue ... ");

				while (rsQueue.next() && isAvailable())
				{
					order = pushOrder(rsQueue.getString("isdn"),rsQueue.getLong("productid"),"core","DAR",0);
					boolean sendSuccess = false;
					while (retryTime < _maxRetry && !sendSuccess)
					{
						try
						{
							connection.sendMessage(order, QueueFactory.ORDER_REQUEST_QUEUE, 0, queuePersistent);
							sendSuccess = true;
							_stmtUpdate.setInt(1, Constants.ORDER_STATUS_PENDING);
							_stmtUpdate.setString(2, rsQueue.getString("isdn"));
							_stmtUpdate.setLong(3, rsQueue.getLong("productid"));
							_stmtUpdate.addBatch();
							batchCounter++;
						}
						catch (Exception ex)
						{
							retryTime ++;
							returnMQConnection(connection);
							connection = getMQConnection();
						}
					}
					
					logMonitor("Scan: " + order.getIsdn());
					counter++;
					if (batchCounter >=getBatchSize())
					{
						_stmtUpdate.executeBatch();
						batchCounter = 0;
					}
					Thread.sleep(getRestTime());
				}

				if (counter > 0)
				{
					debugMonitor("Total transfer record :" + counter);
				}
				if (batchCounter > 0)
				{
					_stmtUpdate.executeBatch();
					batchCounter = 0;
				}
				storeConfig();
			}
			catch (Exception e)
			{
				throw e;
			}
			finally
			{
				returnMQConnection(connection);
				Database.closeObject(rsQueue);
			}
		}

		public CommandMessage pushOrder(String isdn, long product,
				String channel, String keyword, long id) throws Exception
		{
			CommandMessage order = new CommandMessage();

			try
			{
				order.setChannel(channel);
				order.setUserId(0);
				order.setUserName("system");
				order.setSubProductId(id);
				order.setServiceAddress(String.valueOf(product));
				order.setIsdn(isdn);
				order.setKeyword(keyword);
			}
			catch (Exception e)
			{
				throw e;
			}
			return order;
		}

		public void setSQLCommand(String _sqlCommand)
		{
			this._sqlCommand = _sqlCommand;
		}

		public String getSQLCommand()
		{
			return _sqlCommand;
		}
		
		public void setMaxRetry(int MaxRetry)
		{
			this._maxRetry = MaxRetry;
		}

		public int getMaxRetry()
		{
			return this._maxRetry;
		}
		public int getRestTime() {
			return this._restTime;
		}

		public void setRestTime(int _restTime) {
			this._restTime = _restTime;
		}
		
		public void setBatchSize(int batchSize)
		{
			this._bacthSize = batchSize;
		}
		public int getBatchSize()
		{
			return this._bacthSize;
		}
}
