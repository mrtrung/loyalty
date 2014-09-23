package com.crm.product.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class UpgradeSMSUserImpl extends VNMOrderRoutingImpl
{
	@Override
	public void checkPromotion(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		ProductEntry productEntry = ProductFactory.getCache().getProduct(order.getProductId());
		String strTablename = productEntry.getParameter("TableName", "");

		String strSQL =
				" select 'exist' "
						+ " from ~TABLENAME~ "
						+ " where  1 = 1 "
						+ "        and isdn = ?";
		strSQL = strSQL.replace("~TABLENAME~", strTablename);

		String strISDN = StringUtil.nvl(order.getIsdn(), "");
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			connection = Database.getConnection();
			stmt = connection.prepareStatement(strSQL);
			stmt.setString(1, strISDN);
			// stmt.setInt(2, Constants.STATUS_INACTIVE);
			rs = stmt.executeQuery();
			if (rs.next())
				return;
			else
				throw new AppException(Constants.ERROR_INVALID_PROMOTION);
		}
		finally
		{
			Database.closeObject(stmt);
			Database.closeObject(rs);
			Database.closeObject(connection);
		}
	}
}
