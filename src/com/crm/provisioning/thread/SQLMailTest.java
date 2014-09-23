package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import com.crm.kernel.sql.Database;
import com.crm.thread.DispatcherThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class SQLMailTest extends DispatcherThread {
	
	protected String SQL = "";

	Connection connection = null;
	PreparedStatement stmtSQL = null;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("SQL", "",
				ParameterType.PARAM_TEXTAREA_MAX, "10000", ""));
		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}
	@Override
	public void fillParameter() throws AppException {
		
		try
		{
			super.fillParameter();
			SQL = loadMandatory("SQL");
			
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
	@Override
	public void beforeProcessSession() throws Exception {

		super.beforeProcessSession();

		try
		{
			connection =  Database.getConnection();
			stmtSQL = connection.prepareStatement(SQL);
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void doProcessSession() throws Exception
	{
		ResultSet result = null;
		result = stmtSQL.executeQuery(SQL);
		
		ArrayList<WeeklyData> arrList = new ArrayList<WeeklyData>();
		int[] arrayOfLevel = {500, 1000, 2000, 3000, 4000, 12000};
		int allSubs = 0;
		int maxAmount = 0;
		int minAmount = 0;
		int level	= 0;
		int i =0, k = 0, l = 0; 
		
//		int s = (1<2?2:3);
		while(result.next())
		{
			WeeklyData weeklyData = new WeeklyData();
			
			weeklyData.setLevel(result.getInt("GroupLevel"));
			weeklyData.setAllSubs(result.getInt("AllSub"));
			weeklyData.setMaxAmount(result.getInt("MaxAmount"));
			weeklyData.setMinAmount(result.getInt("MinAmount"));
			weeklyData.setAction(result.getString("action"));
			weeklyData.setAvgPoint(result.getInt("avgpoint"));
			
			arrList.add(weeklyData);
		}
		
		while (k < arrList.size())
		{
			WeeklyData obj = arrList.get(k);

			for (int j = 0; j< arrayOfLevel.length; j++)
			{
				if (arrayOfLevel[l] == (obj.getLevel()))
				{
					level = arrayOfLevel[l];
					
					allSubs = obj.getAllSubs();
					
					minAmount = obj.getMinAmount();
					
					maxAmount = obj.getMaxAmount();
					
					break;
				}
				else
				{
					j++;
					l++;
				}	
			}
			int avg_aonet = 0,avg_cutover = 0,avg_topup = 0,avg_usage = 0;
			
//			level = arrayOfLevel[l];
			int flag = 0;
			while (level == (obj.getLevel())&&(flag==0))
			{
				if (obj.getAction().equals("AONET"))
				{
					avg_aonet = obj.getAvgPoint();
					k++;
				}
				else if (obj.getAction().equals("CUTOVER"))
				{
					avg_cutover = obj.getAvgPoint();
					k++;
				}
				else if (obj.getAction().equals("TOPUP"))
				{
					avg_topup = obj.getAvgPoint();
					k++;
				}
				else if (obj.getAction().equals("USAGE"))
				{
					avg_usage = obj.getAvgPoint();
					k++;
				}
				
				i++;
				if( (i < arrList.size()) && (level == obj.getLevel()))
				{
					try
					{
						obj = arrList.get(i);
						
					}catch(Exception e){
						
						flag = 1;
						break;
					}
				}
				else
					break;
				
			}
			
			logMonitor("level "+ l+": " + level );
			logMonitor("allSubs "+ l+": " +allSubs );
			logMonitor("maxAmount "+ l+": " + maxAmount );
			logMonitor("minAmount "+ l+": " + minAmount );
			logMonitor("avg_aonet "+ l+": " + avg_aonet );
			logMonitor("avg_topup "+ l+": " + avg_topup );
			logMonitor("avg_usage "+ l+": " + avg_usage );
			logMonitor("avg_cutover "+l+": " + avg_cutover );
		
			l++;
		}
	}

	@Override
	public void afterProcessSession() throws Exception {
		try
		{
			Database.closeObject(stmtSQL);
			Database.closeObject(connection);
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
}
