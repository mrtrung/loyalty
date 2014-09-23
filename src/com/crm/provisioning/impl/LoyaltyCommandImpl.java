package com.crm.provisioning.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.subscriber.bean.SubscriberBalance;
import com.crm.subscriber.impl.SubscriberBalanceImpl;

public class LoyaltyCommandImpl extends CommandImpl {

	@Override
	public CommandMessage register(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception {
		// TODO Auto-generated method stub
		Connection connection = null;
		PreparedStatement stmtBalance = null;

		if (isBalanceExist(request)) {

			super.register(instance, provisioningCommand, request);
			try {
				connection = Database.getConnection();

				String SQL = "UPDATE SubscriberBalance SET status = ? WHERE isdn = ? and Status = ?";

				stmtBalance = connection.prepareStatement(SQL);
				stmtBalance.setInt(1, Constants.DEFAULT_STATUS);
				stmtBalance.setString(2, request.getIsdn());
				stmtBalance.setInt(3, Constants.NOT_REGISTERED);

				stmtBalance.executeUpdate();

				// super.register(instance, provisioningCommand, request);
			} catch (Throwable e) {
				request.setCause(Constants.ERROR_REGISTER_BALANCE);
				return request;
			}
			finally
			{
				Database.closeObject(stmtBalance);
				Database.closeObject(connection);
			}
		} else {
			request.setCause(Constants.ERROR_REGISTER_BALANCE);
			return request;
		}
		return request;

	}

	@Override
	public CommandMessage unregister(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception {
		// TODO Auto-generated method stub
		Connection connection = null;
		PreparedStatement stmtBalance = null;
		
		if (isBalanceExist(request)) {
			super.register(instance, provisioningCommand, request);
			try {
				connection = Database.getConnection();

				String SQL = "UPDATE SubscriberBalance SET status = ? WHERE isdn = ? and Status = ?";

				stmtBalance = connection.prepareStatement(SQL);
				stmtBalance.setInt(1, Constants.NOT_REGISTERED);
				stmtBalance.setString(2, request.getIsdn());
				stmtBalance.setInt(3, Constants.DEFAULT_STATUS);

				stmtBalance.executeUpdate();

				super.unregister(instance, provisioningCommand, request);

			} catch (Throwable e) {
				request.setCause(Constants.ERROR_UNREGISTER_BALANCE);
				return request;
			}
			finally
			{
				Database.closeObject(stmtBalance);
				Database.closeObject(connection);
			}
		} else {
			request.setCause(Constants.ERROR_REGISTER_BALANCE);
			return request;
		}
		return request;

	}

	public boolean isBalanceExist(CommandMessage request) throws Exception {
		SubscriberBalance subscriberBalance = SubscriberBalanceImpl
				.getBalance(request.getIsdn());

		if (subscriberBalance != null)
			return true;
		else
			return false;
	}
}
