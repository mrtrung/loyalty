/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.crm.subscriber.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.crm.kernel.sql.Database;
import com.crm.subscriber.bean.SubscriberBalance;
import com.crm.subscriber.bean.SubscriberProduct;

import com.fss.util.AppException;

/**
 * The implementation of the subscriber entry local service.
 * 
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link com.sdp.subscriber.service.SubscriberEntryLocalService} interface.
 * </p>
 * 
 * <p>
 * Never reference this interface directly. Always use
 * {@link com.sdp.subscriber.service.SubscriberEntryLocalServiceUtil} to access
 * the subscriber entry local service.
 * </p>
 * 
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 * 
 * @author Phan Viet Thang
 * @see com.sdp.subscriber.service.base.SubscriberEntryLocalServiceBaseImpl
 * @see com.sdp.subscriber.service.SubscriberEntryLocalServiceUtil
 */
public class SubscriberBalanceImpl
{
	public static boolean withdraw(
			long userId, String userName, long subscriberId, String isdn, String balanceType, double amount)
			throws Exception
	{
		Connection connection = Database.getConnection();
		connection.setAutoCommit(false);

		PreparedStatement stmtBalance = null;

		try
		{
			String SQL = "Update SubscriberBalance "
					+ "Set userId = ?, userName = ?, balanceAmount = nvl(balanceAmount, 0) - ? "
					+ "Where subscriberId = ? and nvl(balanceAmount, 0) >= ? ";

			stmtBalance = connection.prepareStatement(SQL);

			stmtBalance.setLong(1, userId);
			stmtBalance.setString(2, userName);
			stmtBalance.setDouble(3, amount);
			stmtBalance.setString(4, isdn);
//			stmtBalance.setString(5, balanceType);
			stmtBalance.setDouble(5, amount);

			stmtBalance.execute();

			if (stmtBalance.getUpdateCount() == 0)
			{
				throw new AppException("not-enough-money");
			}

			connection.commit();
		}
		catch (Exception e)
		{
			Database.rollback(connection);

			throw e;
		}
		finally
		{
			Database.closeObject(stmtBalance);
			Database.closeObject(connection);
		}

		return true;
	}

	//edit by trungnq
	public static SubscriberBalance getBalance(ResultSet rsBalance)
			throws Exception
	{
		SubscriberBalance result = new SubscriberBalance();

		try
		{
			result.setBalanceId(rsBalance.getLong("balanceId"));
			result.setUserId(rsBalance.getLong("userId"));
			result.setUserName(Database.getString(rsBalance, "userName"));
//			result.setCreateDate(rsBalance.getTimestamp("createDate"));
//			result.setModifiedDate(rsBalance.getTimestamp("modifiedDate"));
			result.setSubscriberId(rsBalance.getLong("subscriberId"));
			result.setIsdn(Database.getString(rsBalance, "isdn"));
			//result.setBalanceType(rsBalance.getString("balanceType"));
			result.setBalanceAmount(rsBalance.getInt("balanceAmount"));
			result.setStartDate(rsBalance.getTimestamp("startDate"));
			result.setExpirationDate(rsBalance.getTimestamp("expirationDate"));
			result.setStatus(rsBalance.getInt("status"));
			result.setDescription(rsBalance.getString("description"));
			result.setCumulationAmount(rsBalance.getInt("cumulationAmount"));
		}
		catch (Exception e)
		{
			throw e;
		}

		return result;
	}
	
	public static SubscriberBalance getBalance(Connection connection, String isdn) throws Exception
	{
		PreparedStatement stmtBalance = null;
		ResultSet rsBalance = null;

		SubscriberBalance result = null;

		try
		{
			String SQL = "Select * From SubscriberBalance Where isdn = ?";

			stmtBalance = connection.prepareStatement(SQL);
			stmtBalance.setString(1, isdn);

			rsBalance= stmtBalance.executeQuery();

			if (rsBalance.next())
			{
				result = getBalance(rsBalance);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsBalance);
			Database.closeObject(stmtBalance);
		}

		return result;
	}
	
	public static SubscriberBalance getBalance(String isdn)
			throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();

			return getBalance(connection, isdn);
		}
		finally
		{
			Database.closeObject(connection);
		}
	}
	public static boolean adjustment(
			long userId, String userName, long subscriberId, String isdn, String balanceType, double amount)
			throws Exception
	{
		return withdraw(userId, userName, subscriberId, isdn, balanceType, - amount);
	}
}