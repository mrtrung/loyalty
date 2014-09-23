package com.crm.subscriber.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.util.DateUtil;

public class SubscriberRenewDailyImpl
{
	public static int insertRenewChargeReq(String isdn, long productId, Date orderDate) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();

			return insertRenewChargeReq(connection, isdn, productId, orderDate);
		}
		finally
		{
			Database.closeObject(connection);
		}
	}

	public static int insertRenewChargeReq(Connection connection, String isdn,
											long productId, Date orderDate) throws SQLException
	{
		int result = Constants.SERVICE_STATUS_APPROVED;
		String insertSQL = "INSERT INTO DAILYRENEWSERVICES (ISDN,PRODUCTID,PROCESSDATE,STATUS ) VALUES (?,?,?,?)";

		PreparedStatement stmt = connection.prepareStatement(insertSQL);

		stmt.setString(1, isdn);
		stmt.setLong(2, productId);
		stmt.setTimestamp(3, DateUtil.getTimestampSQL(orderDate));
		stmt.setInt(4, 0);

		try
		{
			stmt.execute();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			result = Constants.SERVICE_STATUS_DENIED;
		}
		finally
		{
			Database.closeObject(stmt);
		}
		return result;
	}

	public static int disableRenew(String isdn, long productId, String orderTypeReg, String orderTypeRenew) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();

			return disableRenew(connection, isdn, productId, orderTypeReg, orderTypeRenew);
		}
		finally
		{
			Database.closeObject(connection);
		}
	}

	public static int disableRenew(Connection connection, String isdn, long productId, String OrderTypeReg, String OrderTypeRenew)
			throws SQLException
	{
		int result = Constants.SERVICE_STATUS_APPROVED;
		String insertSQL = " UPDATE SUBSCRIBERORDER "
				+
							" SET DESCRIPTION = 'disableRenew'"
				+
							" WHERE ISDN = ? and PRODUCTID = ? and STATUS = ? and ORDERDATE >= trunc(sysdate) and OrderType in (?,?) and description is null";

		PreparedStatement stmt = connection.prepareStatement(insertSQL);

		stmt.setString(1, isdn);
		stmt.setLong(2, productId);
		stmt.setInt(3, Constants.SERVICE_STATUS_APPROVED);
		stmt.setString(4, OrderTypeReg);
		stmt.setString(5, OrderTypeRenew);
		try
		{
			if (stmt.executeUpdate() == 0)
			{
				result = Constants.SERVICE_STATUS_DENIED;
			}
			// stmt.executeUpdate();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			result = Constants.SERVICE_STATUS_DENIED;
		}
		finally
		{
			Database.closeObject(stmt);
		}
		return result;
	}
}
