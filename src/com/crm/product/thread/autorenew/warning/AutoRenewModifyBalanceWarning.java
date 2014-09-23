package com.crm.product.thread.autorenew.warning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.crm.kernel.sql.Database;
import com.crm.util.StringUtil;

public class AutoRenewModifyBalanceWarning extends AbtractAutoRenewWarning
{
	@Override
	public void buildContent(String[] argContent) throws Exception
	{
		// INIT CONNECTION
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			connection = Database.getConnection();
			stmt = connection.prepareStatement(sQLWarning);
			rs = stmt.executeQuery();
			int i = 0;
			long total = 0, processed = 0, remain = 0, success = 0, error = 0;

			argContent[0] = argContent[0].replace("~DATE~", StringUtil.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
			argContent[1] = argContent[1].replace("~DATE~", StringUtil.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
			while (rs.next())
			{
				i++;
				if (i == 1)
				{
					total = rs.getLong(1);
				}
				else if (i == 2)
				{
					processed = rs.getLong(1);
				}
				else if (i == 3)
				{
					success = rs.getLong(1);
					error = processed - success;
					if (error < 0)
					{
						error = 0;
					}
				}
				else if (i == 4)
				{
					remain = rs.getLong(1);
				}
			}

			argContent[0] = argContent[0].replace("~TOTAL~", formatNumber(total));
			argContent[1] = argContent[1].replace("~TOTAL~", formatNumber(total));

			argContent[0] = argContent[0].replace("~SUCCESS~", formatNumber(success));
			argContent[1] = argContent[1].replace("~SUCCESS~", formatNumber(success));

			argContent[0] = argContent[0].replace("~ERROR~", formatNumber(error));
			argContent[1] = argContent[1].replace("~ERROR~", formatNumber(error));

			argContent[0] = argContent[0].replace("~REMAIN~", formatNumber(remain));
			argContent[1] = argContent[1].replace("~REMAIN~", formatNumber(remain));
			
			if (getYesterdayTotalRecords() == 0)
			{
				countYesterdayRecord();
			}
			
			argContent[0] = argContent[0].replace("~YESTERDAYTOTAL~", formatNumber(getYesterdayTotalRecords()));
			argContent[1] = argContent[1].replace("~YESTERDAYTOTAL~", formatNumber(getYesterdayTotalRecords()));
			if(remain == 0)
			{
				setYesterdayTotalRecords(total);
			}
		}
		catch (Exception ex)
		{
			logMonitor(ex.getMessage());
		}
		finally
		{
			Database.closeObject(stmt);
			Database.closeObject(rs);
			Database.closeObject(connection);
		}
	}
	public void countYesterdayRecord()
	{
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			connection = Database.getConnection();
			stmt = connection.prepareStatement(" select count(*) as total from subscriberorder " +
											   " where " + 
											   " (productid = 11114 or productid = 12301) and " +
											   " orderdate >= trunc(sysdate -1) and " +
											   " (orderdate <= trunc(sysdate -1) + 18/24) and " +
											   " ordertype in ('register','autorenew') and " +
											   " status = 0 ");
			rs = stmt.executeQuery();
			while (rs.next())
			{
				setYesterdayTotalRecords(rs.getLong("total"));
			}
		}
		catch( Exception e)
		{
			
		}
		finally
		{
			Database.closeObject(rs);
			Database.closeObject(stmt);			
			Database.closeObject(connection);			
		}
	}
	
}
