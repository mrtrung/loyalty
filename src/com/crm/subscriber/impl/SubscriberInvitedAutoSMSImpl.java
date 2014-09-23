package com.crm.subscriber.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.subscriber.bean.SubscriberBalance;

public class SubscriberInvitedAutoSMSImpl{
	
	public static ArrayList<SubscriberBalance> getUnregisterBalance(Connection connection, int status) throws Exception
	{
		PreparedStatement stmtStatus = null;
		
		ResultSet rsStatus = null;
		
		ArrayList<SubscriberBalance> temm = new ArrayList<SubscriberBalance>();
		
		try
		{
			String SQL = "select bal.isdn, bal.cumulationAmount from " +
					"(select * from (select * from subscriberBalance " +
					"where status = ? and activationdate > '01-JAN-2006' " +
					"order by nvl(cumulationamount, 0) desc)" +
					"where rownum < 200001) bal";
			
			stmtStatus = connection.prepareStatement(SQL);
			
			stmtStatus.setInt(1, Constants.NOT_REGISTERED);
			
			rsStatus = stmtStatus.executeQuery();
			
			while(rsStatus.next())
			{
				SubscriberBalance newObj = new SubscriberBalance();
				
				newObj.setIsdn(rsStatus.getString("isdn"));
				newObj.setCumulationAmount(rsStatus.getInt("cumulationAmount"));
				
				temm.add(newObj);
			}
		}
		catch (Exception error)
		{
			error.printStackTrace();
			
			Database.rollback(connection);
		}
		finally
		{
			Database.closeObject(rsStatus);
			Database.closeObject(stmtStatus); 
		}

		return temm;
	}

}
