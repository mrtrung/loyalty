package com.crm.subscriber.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.crm.kernel.sql.Database;
import com.crm.subscriber.bean.SubscriberProduct;

public class IDDServiceImpl
{
	public static int checkSendNotify(String isdn, double dayExpired,
			long productid, boolean notEnough, double maxExpired,
			double subscriptionPeriod, double maxTopUp) throws Exception
	{
		int existed = 0;

		String strSQLCheck = "select confirmstatus, trunc(confirmdate) as confirmdate, messagetype from NOTIFYIDDBUFFET where isdn = ? and productid = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			Date confirmDate;
			int confirmstatus = 0;
			Date currentDate = new Date();
			double dayExtend;
			int messagetype;

			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQLCheck);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productid);
			stmtProduct.setQueryTimeout(10);
			rsProduct = stmtProduct.executeQuery();
			while (rsProduct.next())
			{
				confirmDate = rsProduct.getDate("confirmdate");
				confirmstatus = rsProduct.getInt("confirmstatus");
				messagetype = rsProduct.getInt("messagetype");
				dayExtend = (double) ((currentDate.getTime() - confirmDate
						.getTime()) / (1000 * 60 * 60 * 24));
				if ((confirmstatus == 1 && messagetype == 4 && dayExtend <= maxTopUp)
						|| (confirmstatus == 1 && messagetype != 4 && (dayExpired < maxExpired && dayExpired >= subscriptionPeriod)))
				{
					existed = 1;
				}
				else if ((dayExpired > maxExpired)
						|| (confirmstatus == 2 && dayExpired >= subscriptionPeriod)
						|| (confirmstatus == 2 && notEnough)
						|| (confirmstatus == 1 && messagetype == 4 && dayExtend > maxTopUp))
				{
					existed = -1;
				}
				else
				{
					existed = 2;
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return existed;
	}

	public static void notifyOverdue(String isdn, int status, int mesageType,
			long productid) throws Exception
	{
		String strSQL = "insert into NOTIFYIDDBUFFET values( ?, sysdate, 1, ?, ?, sysdate, ?, 0)";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productid);
			stmtProduct.setInt(3, status);
			stmtProduct.setInt(4, mesageType);
			stmtProduct.setQueryTimeout(10);
			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static boolean isActive(long productID, String isdn)
			throws Exception
	{
		boolean registered = false;

		String strSQL = "select * from subscriberproduct where isdn = ? and supplierstatus = 1 and productid = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productID);
			stmtProduct.setQueryTimeout(10);

			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				registered = true;
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return registered;
	}

	public static boolean isRegistedBefore(String isdn, long productid)
			throws Exception
	{
		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		int count = 0;
		String strSQL = "Select Count(*) as Total from subscriberproduct "
				+ "where productid = ? and isdn = ? and "
				+ " supplierstatus <> '2'  and properties is null ";
		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setLong(1, productid);
			stmtProduct.setString(2, isdn);
			stmtProduct.setQueryTimeout(10);

			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				count = rsProduct.getInt("Total");
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return (count > 0);
	}

	public static boolean checkMaxRegister(long product, String isdn,
			int maxRegister) throws Exception
	{
		boolean success = false;

		Calendar cal = Calendar.getInstance();
		cal.add(cal.DAY_OF_MONTH, -29);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String strSQL = "select count(*) as total from subscriberproduct where productid = ? and isdn = ? "
				+ "and trunc(createdate) >= to_date( ?, 'SYYYY-MM-DD')";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setLong(1, product);
			stmtProduct.setString(2, isdn);
			stmtProduct.setString(3, sdf.format(cal.getTime()));
			stmtProduct.setQueryTimeout(10);

			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				int total = rsProduct.getInt("total");
				if (total >= maxRegister)
					success = true;
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return success;
	}

	public static SubscriberProduct isConfirmRegister(String isdn, long productID)
			throws Exception
	{
		SubscriberProduct bRegister = null;

		String strSQL = "select * from REGISTERVB where isdn = ? and productid = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productID);
			stmtProduct.setQueryTimeout(10);

			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				bRegister = null;
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return bRegister;
	}

	public static void updateProperties(long subproductid, String des)
			throws Exception
	{
		Connection connection = null;
		PreparedStatement stmtProduct = null;

		String strSQL = "update subscriberproduct set properties = ? where SUBPRODUCTID = ?";

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, des);
			stmtProduct.setLong(2, subproductid);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static void removeConfirm(String isdn, long productID)
			throws Exception
	{
		String strSQL = "delete REGISTERVB where isdn = ? and productid= ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productID);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static boolean isConfirmExtend(String isdn, long productID)
			throws Exception
	{
		boolean bExtend = false;

		String strSQL = "select * from NOTIFYIDDBUFFET where isdn = ? and productid = ?  and (confirmstatus = 0 or confirmstatus = -1)";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productID);
			stmtProduct.setQueryTimeout(10);

			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				bExtend = true;
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return bExtend;
	}

	public static boolean isRenewNow(String isdn, long productId)
			throws Exception
	{
		boolean isRenew = false;

		String strSQL = "select * from NOTIFYIDDBUFFET where isdn = ? and confirmstatus = -1 and productId = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productId);
			stmtProduct.setQueryTimeout(10);

			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				isRenew = true;
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return isRenew;
	}

	public static void pendingNotify(String isdn, int confirmStatus,
			long productid) throws Exception
	{
		String strSQL = "update NOTIFYIDDBUFFET set confirmstatus = ?, confirmdate=sysdate, messagetype=-1, sendflg=0 where isdn = ? and productid = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setInt(1, confirmStatus);
			stmtProduct.setString(2, isdn);
			stmtProduct.setLong(3, productid);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static void updateProperties(String isdn, long productid,
			String properties) throws Exception
	{
		String strSQL = "update subscriberproduct set properties = ? where isdn = ? and PRODUCTID = ? and SUPPLIERSTATUS = 1";

		Connection connection = null;
		PreparedStatement stmtProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, properties);
			stmtProduct.setString(2, isdn);
			stmtProduct.setLong(3, productid);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static void updateMessageNotify(String isdn, int mesageType,
			long productid, int sendFlg) throws Exception
	{
		String strSQL = "update NOTIFYIDDBUFFET set messagetype = ?, confirmdate = sysdate, sendflg = ? where isdn = ? and productid = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setInt(1, mesageType);
			stmtProduct.setInt(2, sendFlg);
			stmtProduct.setString(3, isdn);
			stmtProduct.setLong(4, productid);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static void removeExtendIDDBuffet(String isdn, long productID)
			throws Exception
	{
		String strSQL = "delete NOTIFYIDDBUFFET where isdn = ? and productid = ?";

		Connection connection = null;
		PreparedStatement stmtProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productID);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}
	}

	public static boolean confirmRegister(String isdn, long productID)
			throws Exception
	{
		boolean success = false;

		String strSQL = "insert into REGISTERVB values(?, ?, sysdate)";

		Connection connection = null;
		PreparedStatement stmtProduct = null;

		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setLong(2, productID);
			stmtProduct.setQueryTimeout(10);

			stmtProduct.execute();
			success = true;
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return success;
	}

	public static boolean checkNotifyNotEnough(String isdn) throws Exception
	{
		String strSQL = "select * from NOTIFYIDDBUFFET where isdn = ? and messagetype=4 and sendflg=1";

		Connection connection = null;
		PreparedStatement stmtProduct = null;
		ResultSet rsProduct = null;

		boolean send = false;
		try
		{
			connection = Database.getConnection();

			stmtProduct = connection.prepareStatement(strSQL);
			stmtProduct.setString(1, isdn);
			stmtProduct.setQueryTimeout(10);
			rsProduct = stmtProduct.executeQuery();
			if (rsProduct.next())
			{
				send = true;
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsProduct);
			Database.closeObject(stmtProduct);
			Database.closeObject(connection);
		}

		return send;
	}

	public static void confirmRegister(long userId, String userName,
			long subscriberId, String isdn, int subscriberType, long productId,
			String languageId, String subscriberPendingStatus,
			Object activationDate) {
		// TODO Auto-generated method stub
		
	}

	public static SubscriberProduct registerIDD(long userId, String userName,
			long subscriberId, String isdn, int subscriberType, long productId,
			long campaignId, String languageId, boolean includeCurrentDay,
			boolean b, String subscriberPendingStatus, Object activationDate,
			long subProductId) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void removeConfirm(long subProductId) {
		// TODO Auto-generated method stub
		
	}

	public static SubscriberProduct subscription(long userId, String userName,
			long subProductId, boolean fullOfCharge, int quantity,
			boolean includeCurrentDay) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void barringVBService(long userId, String userName,
			long subProductId) {
		// TODO Auto-generated method stub
		
	}

	public static void confirmRegister(long userId, String userName,
			long subscriberId, String isdn, int subscriberType, long productId,
			String languageId, int subscriberPendingStatus,
			Object activationDate) {
		// TODO Auto-generated method stub
		
	}

	public static SubscriberProduct registerIDD(long userId, String userName,
			long subscriberId, String isdn, int subscriberType, long productId,
			long campaignId, String languageId, boolean includeCurrentDay,
			boolean b, int subscriberPendingStatus, Object activationDate,
			long subProductId) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateIDDStatus(String subscriberNotExtendStatus,
			long subProductId) {
		// TODO Auto-generated method stub
		
	}

	public static int checkSendNotify(String isdn, double dayBetween,
			long productId, boolean notEnough, double maxTopUp) {
		// TODO Auto-generated method stub
		return 0;
	}
}
