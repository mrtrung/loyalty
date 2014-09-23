package com.crm.subscriber.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.crm.kernel.sql.Database;
import com.crm.subscriber.bean.SubscriberBalance;

public class WeeklyReportImpl {
	
	public static ArrayList<SubscriberBalance> getMaxPointSub(Connection connection, int status) throws Exception
	{
		PreparedStatement stmtMaxPointSub = null;
		
		ResultSet rsMaxPointSub = null;
		
		ArrayList<SubscriberBalance> temm = new ArrayList<SubscriberBalance>();
		
		try
		{
			String SQL = "SELECT * FROM (SELECT cumulationAmount, isdn from SubscriberBalance  order by cumulationAmount desc) WHERE ROWNUM < ? " ;

			stmtMaxPointSub = connection.prepareStatement(SQL);

			stmtMaxPointSub.setInt(1, status);
			
			rsMaxPointSub= stmtMaxPointSub.executeQuery();
			
			while (rsMaxPointSub.next())
			{
				SubscriberBalance newObj = new SubscriberBalance();
				
				newObj.setIsdn(rsMaxPointSub.getString("isdn"));
				newObj.setCumulationAmount(rsMaxPointSub.getInt("cumulationAmount"));
				
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
			Database.closeObject(rsMaxPointSub);
			Database.closeObject(stmtMaxPointSub); 
		}

		return temm;
	}

}
