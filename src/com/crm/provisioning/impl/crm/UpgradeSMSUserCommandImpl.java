package com.crm.provisioning.impl.crm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductCache;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.impl.CommandImpl;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class UpgradeSMSUserCommandImpl extends CommandImpl
{
	public CommandMessage updateStatus(CommandInstance instance, ProvisioningCommand provisioningCommand, CommandMessage order)
			throws Exception
	{
		ProductEntry productEntry = ProductFactory.getCache().getProduct(order.getProductId());
		String strTablename = productEntry.getParameter("TableName", "");

		String strSQL = "update ~TABLENAME~ set status = ?, active_date = to_date(?,'dd/mm/yyyy hh24:mi:ss') where isdn = ?";
		strSQL = strSQL.replace("~TABLENAME~", strTablename);

		Connection connection = null;
		PreparedStatement stmt = null;
		try
		{
			connection = Database.getConnection();
			stmt = connection.prepareStatement(strSQL);
			stmt.setInt(1, Constants.STATUS_ACTIVE);
			stmt.setString(2, StringUtil.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
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
