package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.marketing.cache.CampaignEntry;
import com.crm.marketing.cache.CampaignFactory;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.CommandUtil;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.thread.DispatcherThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class PromotionAlertThread extends DispatcherThread
{
	protected PreparedStatement	_stmtQueue			= null;
	protected PreparedStatement	_stmtRemove			= null;
	private Connection _conn = null;
	
	protected String			_sqlCommand			= "";
	protected int				_batchNumber		= 1000;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("SQLCommand", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("BatchNumber", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));

		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}
	
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();

			// Fill parameter
			setSQLCommand(loadMandatory("SQLCommand"));
			setBatchNumber(loadInteger("BatchNumber"));
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
	
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		try
		{
			neverExpire = false;
			
			String strSQL = getSQLCommand();
			_conn = Database.getConnection();
			_stmtQueue = _conn.prepareStatement(strSQL);
			
			strSQL = "Delete send_sms where Id = ?";
			_stmtRemove = _conn.prepareStatement(strSQL);
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
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
		ResultSet rsQueue = null;
		CommandMessage order = null;
		
		String isdn = "";
		String shotCode = "";
		String content = "";

		int counter = 0;
		try
		{
			rsQueue = _stmtQueue.executeQuery();
			while (rsQueue.next() && isAvailable())
			{
				isdn = rsQueue.getString("target_number");
				shotCode = rsQueue.getString("source_number");
				content = rsQueue.getString("content");
				
				debugMonitor("Isdn: " + isdn + ", SC: " + shotCode + ", Content: " + content);
				
				order = pushOrder(isdn, shotCode, content);
				
				QueueFactory.attachCommandRouting(order);
				
				_stmtRemove.setLong(1, rsQueue.getLong("ID"));
				_stmtRemove.addBatch();
				
				counter++;
				
				if (counter == getBatchNumber())
				{
					_stmtRemove.executeBatch();
				}
			}
			
			if (counter > 0)
			{
				_stmtRemove.executeBatch();
			}
		}
		catch (Exception ex)
		{
			logMonitor("Error: " + ex.getMessage());
	
		}
		finally
		{
			rsQueue.close();
			_conn.commit();
		}
	}
	
	public CommandMessage pushOrder(String isdn, String serviceAddress, String content) throws Exception
	{
		CommandMessage order = new CommandMessage();

		try
		{
			CommandEntry command = ProvisioningFactory.getCache().getCommand(Constants.COMMAND_SEND_SMS);
			
			order.setProvisioningType(Constants.PROVISIONING_SMSC);
			order.setCommandId(command.getCommandId());
			order.setServiceAddress(serviceAddress);
			order.setIsdn(isdn);
			
			order.setRequest(content);

			order.setRequestValue(ResponseUtil.SMS_CMD_CHECK, "false");
			
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
	
	public void setBatchNumber(int _batchNumber)
	{
		this._batchNumber = _batchNumber;
	}

	public int getBatchNumber()
	{
		return _batchNumber;
	}
}
