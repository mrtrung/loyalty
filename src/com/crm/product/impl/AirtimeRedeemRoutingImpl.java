/**
 * 
 */
package com.crm.product.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductPrice;
import com.crm.product.cache.ProductRoute;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.message.VNMMessage;
import com.crm.provisioning.thread.OrderRoutingInstance;
import com.crm.provisioning.util.CommandUtil;
import com.crm.kernel.sql.Database;

import com.fss.util.AppException;

/**
 * @author ThangPV
 * 
 */
public class AirtimeRedeemRoutingImpl extends OrderRoutingImpl
{
	public static String	INVALID_QUANTITY	= "invalid-quantity";

	public static String	NOT_ENOUGH_MONEY	= "not-enough-money";
	
	public static String	UNRANKED_SUBSCRIBER	= "unranked-subscriber";
	
	public static String	UNREGISTER_SUBSCRIBER	= "unregister-subscriber";

	public CommandMessage routing(OrderRoutingInstance instance, ProductRoute orderRoute, CommandMessage order) throws Exception
	{
		
		Connection connection = null;

		PreparedStatement stmtBalance = null;
		ResultSet rsBalance = null;

		PreparedStatement stmtRank = null;
		ResultSet rsRank = null;

		int quantity = 0;

		// String cause = "";

		try
		{
			parser(instance, orderRoute, order);

			String keyword = order.getKeyword();

			keyword = keyword.substring(orderRoute.getKeyword().length()).trim();

			try
			{
				quantity = Integer.valueOf(keyword);

				order.setQuantity(quantity);
			}
			catch (Exception e)
			{
				throw new AppException(INVALID_QUANTITY);
			}

			if (quantity < 0)
			{
				throw new AppException(INVALID_QUANTITY);
			}

			connection = Database.getConnection();

			String SQL = "Select * From SubscriberBalance Where isdn = ? and balanceType = ? ";

			stmtBalance = connection.prepareStatement(SQL);

			stmtBalance.setString(1, order.getIsdn());
			stmtBalance.setString(2, "LOYALTY");

			rsBalance = stmtBalance.executeQuery();

			if (!rsBalance.next())
			{
				throw new AppException(UNREGISTER_SUBSCRIBER);
			}
			else
			{
				if (rsBalance.getDouble("balanceAmount") < quantity)
				{
					throw new AppException(NOT_ENOUGH_MONEY);
				}
			}
				

			// order.setSubscriberType(rsSubscriber.getInt("subscriberType"));

			// get segment of loyalty rank
			stmtRank = connection.prepareStatement("Select * From RankEntry Where rankId = ? ");
			stmtRank.setLong(1, order.getRankId());

			rsRank = stmtRank.executeQuery();

			if (rsRank.next())
			{
				order.setSegmentId(rsRank.getLong("segmentId"));
			}
			else
			{
				throw new AppException(UNRANKED_SUBSCRIBER);
			}

			ProductEntry product = ProductFactory.getCache().getProduct(order.getProductId());
			
			ProductPrice productPrice = product.getProductPrice(
					order.getChannel(), order.getActionType(), order.getSegmentId(), 0
					, quantity, order.getCycleDate());

			if (productPrice == null)
			{
				throw new AppException("price-not-found");
			}
			else
			{
				order.setOfferPrice(productPrice.getFullOfCharge());
				order.setPrice(productPrice.getFullOfCharge());
				
				order.setAmount(quantity * order.getPrice());
			}
		}
		catch (AppException e)
		{
			order.setStatus(Constants.ORDER_STATUS_DENIED);
			order.setCause(e.getMessage());

		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(rsBalance);
			Database.closeObject(stmtBalance);
			Database.closeObject(rsRank);
			Database.closeObject(stmtRank);
			Database.closeObject(connection);
		}
		return order;
	}
}
