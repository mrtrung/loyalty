package com.crm.product.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.fss.util.AppException;

public class MaxSMSPromotionRoutingImpl extends VNMOrderRoutingImpl
{
	@Override
	public void checkPromotion(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		if (order.getActionType().equals(Constants.ACTION_REGISTER))
		{
			String strSQL_Select = "select statis_list_id, status "
					+ " from max_sms_promotion_eligible "
					+ " where 1 = 1 "
					+ "       and isdn = ? "
					+ "       and status = ? ";

			String strISDN = order.getIsdn();

			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try
			{
				connection = Database.getConnection();
				stmt = connection.prepareStatement(strSQL_Select);
				stmt.setString(1, strISDN);
				stmt.setInt(2, Constants.STATUS_INACTIVE);
				rs = stmt.executeQuery();
				if (!rs.next())
				{
					throw new AppException(Constants.ERROR_INVALID_PROMOTION);
				}
				else
				{
					int iStatus = rs.getInt(2);
					if (iStatus == Constants.STATUS_ACTIVE || iStatus == Constants.STATUS_CANCEL)
					{
						throw new AppException(Constants.ERROR_REGISTERED);
					}
					String primaryKey = rs.getString(1);
					order.setResponseValue("primaryKey", primaryKey);
				}
			}
			finally
			{
				Database.closeObject(stmt);
				Database.closeObject(rs);
				Database.closeObject(connection);
			}
		}
		else if (order.getActionType().equals(Constants.ACTION_UNREGISTER))
		{
			String strSQL_Select = "select to_char(expirationdate,'dd/mm/yyyy hh24:mi:ss')"
					+ "             from subscriberproduct "
					+ "             where 1 = 1 "
					+ "                   and sysdate < expirationdate "
					+ "                   and productid = ? and isdn = ?";

			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try
			{
				connection = Database.getConnection();
				stmt = connection.prepareStatement(strSQL_Select);
				stmt.setLong(1, order.getProductId());
				stmt.setString(2, order.getIsdn());
				rs = stmt.executeQuery();
				if (!rs.next())
				{
					throw new AppException(Constants.ERROR_INVALID_PROMOTION);
				}
			}
			finally
			{
				Database.closeObject(stmt);
				Database.closeObject(rs);
				Database.closeObject(connection);
			}
		}
	}
}
