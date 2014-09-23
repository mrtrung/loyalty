package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.crm.kernel.sql.Database;

public class ResultSetArrTest {

	@SuppressWarnings("unused")
	private static String level;
	private static int allSubs;
	private static int maxAmount;
	private static int minAmount;
	private static int avg_aonet;
	private static int avg_topup;
	private static int avg_usage;
	private static int avg_cutover;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String args[]) throws Exception
	{
		String sql = "Select * from subscriberBalance";
		ArrayList al = new ArrayList();  
		ArrayList list = new ArrayList(); 

		Connection connection = null;
		PreparedStatement stmtSQL = null;
		
		connection =  Database.getConnection();
		stmtSQL = connection.prepareStatement(sql);
		
		ResultSet result = null;
		result = stmtSQL.executeQuery();

		while(result.next())
			
		{
			al.add(1, result.getString("GroupLevel"));
			al.add(2, result.getString("AllSub"));
			al.add(3, result.getString("MaxAmount"));
			al.add(4, result.getString("MinAmount"));
			al.add(5, result.getString("action"));
			al.add(6, result.getString("avgamount"));
			list.add(al);
		}
		for(int i=1; i <= list.size(); i++)
		{
			for(int j=0; j< al.size(); j++)
			{
				level 	= (String) al.get(1);
				allSubs = (Integer) al.get(2);
				maxAmount = (Integer) al.get(3);
				minAmount = (Integer) al.get(4);
				
				if(al.get(5).equals("AONET"))
					avg_aonet = (Integer) al.get(6);
				if(al.get(5).equals("TOPUP"))
					avg_topup = (Integer) al.get(6);
				if(al.get(5).equals("USAGE"))
					avg_usage = (Integer) al.get(6);
				if(al.get(5).equals("CUTOVER"))
					avg_cutover = (Integer) al.get(6);
			}
		}
		
	}

}
