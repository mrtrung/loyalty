package com.crm.provisioning.impl.crm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.impl.CommandImpl;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class MaxSMSPromotionCommanImpl extends CommandImpl
{
	public CommandMessage updateStatus(CommandInstance instance, ProvisioningCommand provisioningCommand, CommandMessage order)
			throws Exception
	{
		ProductRoute productRoute = ProductFactory.getCache().getProductRoute(order.getRouteId());
		String registerType = productRoute.getParameter("RegisterType", "");
		if (registerType.equals(""))
		{
			String strSQL = "update max_sms_promotion_static_list set status = ?, active_date = sysdate "
					+ " where statis_list_id = ?";
			String strISDN = order.getIsdn();

			Connection connection = null;
			PreparedStatement stmt = null;

			String primaryKey = order.getResponseValue("primaryKey", "");
			try
			{
				connection = Database.getConnection();
				stmt = connection.prepareStatement(strSQL);
				stmt.setInt(1, Constants.STATUS_ACTIVE);
				stmt.setString(2, primaryKey);
				stmt.executeUpdate();
			}
			catch (Exception ex)
			{
				throw new AppException(Constants.ERROR);
			}
			finally
			{
				Database.closeObject(stmt);
				Database.closeObject(connection);
			}
		}
		else if (registerType.equals("Manual"))
		{
			String strSQL = " UPDATE   max_sms_promotion_eligible "
					+ " SET   status = ?, active_date = SYSDATE "
					+ " 	   WHERE       1 = 1 "
					+ " 	           AND isdn = ? "
					+ " 	           AND status = ? "
					+ "                AND statis_list_id = ?";
			String strISDN = order.getIsdn();

			Connection connection = null;
			PreparedStatement stmt = null;

			String primaryKey = order.getResponseValue("primaryKey", "");
			try
			{
				connection = Database.getConnection();
				stmt = connection.prepareStatement(strSQL);
				stmt.setInt(1, Constants.STATUS_ACTIVE);
				stmt.setString(2, strISDN);
				stmt.setInt(3, Constants.STATUS_INACTIVE);
				stmt.setString(4, primaryKey);
				stmt.executeUpdate();
			}
			catch (Exception ex)
			{
				throw new AppException(Constants.ERROR);
			}
			finally
			{
				Database.closeObject(stmt);
				Database.closeObject(connection);
			}
		}
		return order;
	}

	public CommandMessage updateStatusUnregister(CommandInstance instance, ProvisioningCommand provisioningCommand,
			CommandMessage order)
			throws Exception
	{
		String strSQL = " update max_sms_promotion_static_list set status = ?, cancel_date = sysdate "
				+ " where isdn = ? and trunc(active_date) = trunc(sysdate) and status = ? ";

		String strISDN = order.getIsdn();

		Connection connection = null;
		PreparedStatement stmt = null;
		try
		{
			connection = Database.getConnection();
			stmt = connection.prepareStatement(strSQL);
			stmt.setInt(1, Constants.STATUS_CANCEL);
			stmt.setString(2, strISDN);
			stmt.setInt(3, Constants.STATUS_ACTIVE);
			stmt.executeUpdate();
		}
		catch (Exception ex)
		{
			throw new AppException(Constants.ERROR);
		}
		finally
		{
			Database.closeObject(stmt);
			Database.closeObject(connection);
		}
		return order;
	}
}
