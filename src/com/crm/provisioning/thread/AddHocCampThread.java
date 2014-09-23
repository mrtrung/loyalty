package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Vector;


import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.message.VNMMessage;
import com.crm.provisioning.util.CommandUtil;
import com.crm.subscriber.impl.SubscriberAddHocCampImpl;
import com.crm.thread.DispatcherInstance;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class AddHocCampThread extends DispatcherThread {
	public String message = "";
	public String	SQL	  = "";
	public String   uSQL  = "";
	protected PreparedStatement	stmtBalance			= null;
	protected Connection 		connection 			= null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("message", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addElement(createParameterDefinition("batchSize", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addElement(createParameterDefinition("SQL", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addElement(createParameterDefinition("uSQL", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	public void fillParameter() throws AppException {
		// TODO Auto-generated method stub
		
		try 
		{
			super.fillParameter();
			message = ThreadUtil.getString(this, "message", true, "");
			batchSize = ThreadUtil.getInt(this, "batchSize", 200);
			SQL		= ThreadUtil.getString(this, "SQL", true, "");
			uSQL	= ThreadUtil.getString(this, "uSQL", true, "");

		}catch (AppException e)
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
	// Author : TrungNQ
	// Created Date : 13/09/2013
	// //////////////////////////////////////////////////////
	@SuppressWarnings("static-access")
	public void doProcessSession() throws Exception
	{
		Connection connection = Database.getConnection();
		
		VNMMessage vnmMessage = new VNMMessage();
		
		SubscriberAddHocCampImpl subscriberAddHocCampImpl = new SubscriberAddHocCampImpl();
		
		ArrayList<AddHocCampData> arrList = subscriberAddHocCampImpl.getAddHocCampPoint(connection, Constants.DEFAULT_STATUS);
		
		int addHocCounter = 0;
		
//		String strLog = "";
		
		PreparedStatement stmtBalance = null;
		PreparedStatement stmtAddHocCamp = null;
		PreparedStatement stmtISDN = null;

		String rSQL = "UPDATE addhoccamp set status = 2 where nvl(status, 3) <> ? ";
		
		stmtBalance = connection.prepareStatement(SQL);	
		stmtAddHocCamp = connection.prepareStatement(uSQL);
		stmtISDN	= connection.prepareStatement(rSQL);
		
		try 
		{		
			for(int i = 0; i < arrList.size(); i++)
			{
				AddHocCampData obj = arrList.get(i);
				
				vnmMessage.setIsdn(obj.getIsdn());			
				vnmMessage.setAmount(obj.getPoint());
	
				stmtBalance.setDouble(1, vnmMessage.getAmount());
				stmtBalance.setDouble(2,  vnmMessage.getAmount());
				stmtBalance.setString(3,  vnmMessage.getIsdn());
				stmtBalance.setInt(4, Constants.DEFAULT_STATUS);
				stmtBalance.addBatch();
			
				stmtAddHocCamp.setString(1, vnmMessage.getIsdn());				
				stmtAddHocCamp.addBatch();
				
				addHocCounter++;
				
				DispatcherInstance instance = new DispatcherInstance();

				CommandUtil.sendSMS(instance, vnmMessage, vnmMessage.getServiceAddress(),
						vnmMessage.getShipTo(),
						message);

//				strLog = "Point: " + vnmMessage.getAmount() + " ISDN: " + vnmMessage.getIsdn() + " Reason: " + message;
//				logMonitor(strLog);
				
				if (addHocCounter >= batchSize)
				{
					stmtBalance.executeBatch();				
					stmtAddHocCamp.executeBatch();
					
					addHocCounter = 0;
				}
			}
			
			if (addHocCounter > 0)
			{
				stmtBalance.executeBatch();				
				stmtAddHocCamp.executeBatch();	
			}
			
			stmtISDN.setInt(1, Constants.DEFAULT_STATUS_TRUE);				
			stmtISDN.addBatch();
			
			addHocCounter++;

			if (addHocCounter >= batchSize)
			{
				stmtISDN.executeBatch();				
				addHocCounter = 0;
			}
			
			if (addHocCounter > 0)
			{
				stmtISDN.executeBatch();				
			}
			
		} catch (Exception error)
		{
			error.printStackTrace();
			
			Database.rollback(connection);
		} 
		finally
		{
			connection.commit();
			Database.closeObject(stmtAddHocCamp);
			Database.closeObject(stmtBalance);
			Database.closeObject(connection);
		}		
	}
}
