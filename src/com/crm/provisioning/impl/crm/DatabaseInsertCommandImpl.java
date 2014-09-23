package com.crm.provisioning.impl.crm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.impl.CommandImpl;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.subscriber.impl.SubscriberEntryImpl;
import com.crm.util.StringUtil;

public class DatabaseInsertCommandImpl extends CommandImpl
{
	// Map table name
	// private static Map<String, Boolean> mTableName = new HashMap<String,
	// Boolean>();

	// parameters input
	private static final String	KEYWORD		= "KEYWORD";
	private static final String	MDN			= "MDN";
	private static final String	SHORT_CODE	= "SHORTCODE";
	private static final String	DELIVERTIME	= "DELIVERTIME";
	private static final String	INSERTTIME	= "INSERTTIME";
	private static final String	CODE		= "CODE";
	private static final String	STATUS		= "STATUS";
	private static final String	RAWDATA		= "RAWDATA";
	private static final String	NEWDATA		= "NEWDATA";
	private static final String	ID			= "ID";

	public CommandMessage insertDatabase(CommandInstance instance, ProvisioningCommand provisioningCommand, CommandMessage order)
			throws Exception
	{
		ProductRoute orderRoute = ProductFactory.getCache().getProductRoute(order.getRouteId());

		// Check subscriber type
		order.setSubscriberType(SubscriberEntryImpl.getSubscriberType(order
				.getIsdn()));

		// Get Subscriber information
		String strISDN = StringUtil.nvl(order.getIsdn(), "");
		String strShortCode = StringUtil.nvl(orderRoute.getServiceAddress(), "");
		String strKeyWord = StringUtil.nvl(order.getKeyword(), "");
		Date dtRequestDate = order.getRequestTime();

		// Get properties
		String strCode = orderRoute.getParameter("code", "0");
		String strStatus = orderRoute.getParameter("status", "0");
		String strNewData = orderRoute.getParameter("newData", order.getKeyword());
		String strRawData = orderRoute.getParameter("rawData", order.getKeyword());
		String strTableName = orderRoute.getParameter("tableName", "Request");
		String strSequence = orderRoute.getParameter("sequence", strTableName + "_SEQ");

		// Process SQL Command
		String strSQL = orderRoute
				.getParameter(
						"sqlCommand",
						"Insert into ~TABLE_NAME~(id,mdn,newdata,rawdata,shortcode,delivertime,inserttime,code,status) "
								+ " values(?,?,?,?,?,?,?,?,?)");

		String strParams = strSQL.substring(strSQL.indexOf("(", 1) + 1, strSQL.indexOf(")", 1));

		String[] argParams = StringUtil.toStringArray(strParams, ",");

		strSQL = strSQL.replace("~TABLE_NAME~", strTableName);

		// Innit Connection and statement
		Connection connection = null;
		PreparedStatement stmt = null;

		try
		{
			connection = Database.getConnection();
			String strId = Database.getSequenceValue(connection, strSequence);
			// connection.setAutoCommit(false);

			// synchronized (mTableName)
			// {
			// if (mTableName.get(strTableName) == null)
			// createTable(strTableName, connection, instance);
			// }

			stmt = connection.prepareStatement(strSQL);

			int iCount = 1;
			for (String paramIndex : argParams)
			{
				paramIndex = paramIndex.trim().toUpperCase();
				if (paramIndex.equals(KEYWORD))
				{
					stmt.setString(iCount, strKeyWord);
				}
				else if (paramIndex.equals(MDN))
				{
					stmt.setString(iCount, strISDN);
				}
				else if (paramIndex.equals(SHORT_CODE))
				{
					stmt.setString(iCount, strShortCode);
				}
				else if (paramIndex.equals(DELIVERTIME))
				{
					stmt.setTimestamp(iCount,
								new Timestamp(dtRequestDate.getTime()));
				}
				else if (paramIndex.equals(INSERTTIME))
				{
					stmt.setTimestamp(iCount,
								new Timestamp((new Date()).getTime()));
				}
				else if (paramIndex.equals(STATUS))
				{
					stmt.setString(iCount, strStatus);
				}
				else if (paramIndex.equals(CODE))
				{
					stmt.setString(iCount, strCode);
				}
				else if (paramIndex.equals(RAWDATA))
				{
					stmt.setString(iCount, strRawData);
				}
				else if (paramIndex.equals(NEWDATA))
				{
					stmt.setString(iCount, strNewData);
				}
				else if (paramIndex.equals(ID))
				{
					stmt.setString(iCount, strId);
				}
				iCount++;
			}
			stmt.executeUpdate();
			// connection.commit();

		}
		catch (Exception ex)
		{
			// connection.rollback();
			instance.debugMonitor(ex);
			throw ex;
		}
		finally
		{
			Database.closeObject(stmt);
			// connection.setAutoCommit(true);
			Database.closeObject(connection);
		}
		return order;
	}

	protected void createTable(String tableName, Connection connection, CommandInstance instance) throws Exception
	{
		String strSQL = "CREATE TABLE [TableName] ( "
				+ " ID             NUMBER	PRIMARY KEY, "
				+ " MDN            VARCHAR2(15), "
				+ " NEWDATA        NVARCHAR2(100),  "
				+ " RAWDATA        NVARCHAR2(500),  "
				+ " SHORTCODE      NUMBER , "
				+ " DELIVERTIME    DATE , "
				+ " INSERTTIME     DATE ,  "
				+ " CODE           NUMBER , "
				+ " STATUS         NUMBER) "
				+ " TABLESPACE SDP_TBS";
		strSQL = strSQL.replace("[TableName]", tableName);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			// DatabaseMetaData metaData = connection.getMetaData();
			// rs = metaData.getTables(null, null, tableName, null);
			// if (!rs.next())
			// {

			stmt = connection.prepareStatement(strSQL);
			stmt.execute();
			// mTableName.put(tableName, true);
			// }
		}
		catch (Exception ex)
		{
			instance.debugMonitor(ex);
		}
		finally
		{
			Database.closeObject(stmt);
			Database.closeObject(rs);
		}
	}
}
