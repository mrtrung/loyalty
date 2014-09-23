package com.crm.subscriber.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.thread.AddHocCampData;

public class SubscriberAddHocCampImpl {

	public static ArrayList<AddHocCampData> getAddHocCampPoint(Connection connection, int status) throws Exception
	{
		PreparedStatement stmtPoint = null;
		
		ResultSet rsPoint = null;
		
		ArrayList<AddHocCampData> temm = new ArrayList<AddHocCampData>();
		
		try
		{
			String SQL = "SELECT bl.BalanceAmount, bl.cumulationAmount, ah.addhocpoint, ah.isdn FROM SubscriberBalance bl " +
					"RIGHT OUTER JOIN addhoccamp ah ON  bl.isdn = ah.isdn WHERE bl.status = ? and ah.status = ? ";

			stmtPoint = connection.prepareStatement(SQL);

			stmtPoint.setInt(1, status);
			
			stmtPoint.setInt(2, status);
			
			rsPoint= stmtPoint.executeQuery();
			
			while (rsPoint.next())
			{
				AddHocCampData newObj = new AddHocCampData();
				
				newObj.setIsdn(rsPoint.getString("isdn"));
				newObj.setPoint(rsPoint.getInt("addhocpoint"));
				
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
			Database.closeObject(rsPoint);
			Database.closeObject(stmtPoint); 
		}

		return temm;
	}
	
}