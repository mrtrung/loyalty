package com.crm.provisioning.thread;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Vector;

import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.util.DateUtil;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;


public class DailyRenewScandThread extends DispatcherThread
{
		private PreparedStatement _stmtQueue = null;		
		private PreparedStatement _stmtRemove = null;
		private Connection conection = null;

		private String _sqlCommand = "";
		private String _shortCode = "";
		private int _restTime = 5;

		// //////////////////////////////////////////////////////
		// Override
		// //////////////////////////////////////////////////////
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Vector getParameterDefinition()
		{
			Vector vtReturn = new Vector();

			vtReturn.addElement(createParameterDefinition("SQLCommand", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("ShortCode", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("RestTime", "",
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
				
				setShortCode(loadMandatory("ShortCode"));
				setRestTime(loadInteger("RestTime"));
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
			conection = Database.getConnection(); 
			try
			{
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 18);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				
				String strSQL = getSQLCommand();
				
				_stmtQueue = conection.prepareStatement(strSQL);
				_stmtQueue.setTimestamp(1,DateUtil.getTimestampSQL(today.getTime()));
				
				_stmtRemove = conection.prepareStatement("Delete DAILYRENEWSERVICES where status = 1");
				_stmtRemove.execute();
				conection.commit();
				Thread.sleep(200);
				
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
				Database.closeObject(conection);
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
			try
			{
				connection = getMQConnection();
				rsQueue = _stmtQueue.executeQuery();
				debugMonitor("Scanning database queue ... ");

				while (rsQueue.next() && isAvailable())
				{
					order = pushOrder(rsQueue.getString("isdn"),rsQueue.getLong("productid"),"Core","DAR",0);
					connection.sendMessage(order, QueueFactory.DAILY_RENEW, 0, queuePersistent);
					
					logMonitor("Scan: " + order.getIsdn());
					counter++;
					Thread.sleep(getRestTime());
				}

				if (counter > 0)
				{
					debugMonitor("Total transfer record :" + counter);
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

				order.setProductId(product);
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
		
		public void setShortCode(String _shortCode)
		{
			this._shortCode = _shortCode;
		}

		public String getShortCode()
		{
			return _shortCode;
		}
		public int getRestTime() {
			return _restTime;
		}

		public void setRestTime(int _restTime) {
			this._restTime = _restTime;
		}
		
}
