package com.crm.product.thread.autorenew;

import java.util.Calendar;

import javax.jms.Message;

import com.comverse_in.prepaid.ccws.BalanceEntity;
import com.comverse_in.prepaid.ccws.BalanceEntityBase;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductRoute;
import com.crm.provisioning.impl.ccws.CCWSConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.ProvisioningInstance;
import com.crm.subscriber.impl.SubscriberRenewDailyImpl;
import com.fss.util.AppException;

public class AutoRenewModifyBalanceInstance extends ProvisioningInstance
{

	public AutoRenewModifyBalanceInstance() throws Exception
	{
		super();
	}

	public int processMessage(Message message) throws Exception
	{
		CommandMessage order = (CommandMessage) QueueFactory.getContentMessage(message);
		if (getDebugMode().equals("depend"))
		{
			// insert to DailyRenewService
			int status = Constants.SERVICE_STATUS_DENIED;
			int retryTime = 0;
			int maxRetry = ((AutoRenewModifyBalanceThread) dispatcher).get_retryTime();

			long startTime = System.currentTimeMillis();
			while (retryTime < maxRetry && status != Constants.SERVICE_STATUS_APPROVED)
			{

				status = SubscriberRenewDailyImpl.insertRenewChargeReq(order.getIsdn(), order.getProductId(),
							order.getOrderDate());
				retryTime++;
			}
			order.setCause(Constants.SUCCESS);
			order.setSubscriberType(1);
			Thread.sleep(getDispatcher().simulationTime);
			return Constants.BIND_ACTION_SUCCESS;
		}

		ProductRoute orderRoute = null;
		BalanceEntityBase[] balances = null;
		CCWSConnection connection = null;
		BalanceEntity balance = null;
		try
		{
			long startModifyBalanceTime = System.currentTimeMillis();
			orderRoute = ProductFactory.getCache().getProductRoute(
								order.getChannel(), order.getServiceAddress(), order.getKeyword(), order.getOrderDate());
			if (orderRoute == null)
			{
				throw new AppException(Constants.ERROR_INVALID_SYNTAX);
			}
			else
			{
				order.setActionType(orderRoute.getActionType());
				order.setRouteId(orderRoute.getRouteId());
			}

			// Get parameters
			String balanceName = orderRoute.getParameter("BalanceName", "");
			long amount = orderRoute.getParameters().getLong("Amount", 0);
			int day = orderRoute.getParameters().getInteger("Days", 0);
			String comment = orderRoute.getParameter("Comment", "Auto Renew Modify Balance");
			balances = new BalanceEntityBase[1];
			connection = (CCWSConnection) this.getProvisioningConnection();
			balance = new BalanceEntity();
			balance = connection.getBalance(order.getIsdn(), balanceName);
			balance.setBalance(balance.getBalance() + amount);
			Calendar newDate = balance.getAccountExpiration();
			newDate.add(Calendar.HOUR_OF_DAY, 24 * day);
			balance.setAccountExpiration(newDate);
			balances[0] = balance;

			connection.setBalance(order.getIsdn(), balances, newDate, comment);

			long endModifyBalanceTime = System.currentTimeMillis();
			long totalModifyTime = endModifyBalanceTime - startModifyBalanceTime;

			// insert to DailyRenewService
			int status = Constants.SERVICE_STATUS_DENIED;
			int retryTime = 0;
			int maxRetry = ((AutoRenewModifyBalanceThread) dispatcher).get_retryTime();

			long startTime = System.currentTimeMillis();
			while (retryTime < maxRetry && status != Constants.SERVICE_STATUS_APPROVED)
			{

				status = SubscriberRenewDailyImpl.insertRenewChargeReq(order.getIsdn(), order.getProductId(),
							order.getOrderDate());
				retryTime++;
			}
			order.setCause(Constants.SUCCESS);
			order.setSubscriberType(1);

			long endTime = System.currentTimeMillis();

			logMonitor("SUCCESS - ISDN: " + order.getIsdn() + " - PRODUCTID: " + order.getProductId() + " - AMOUNT: "
						+ amount
						+ " - INSERT TIME: " + (endTime - startTime) + " - MODIFY BALANCE TIME: " + totalModifyTime);
		}
		catch (Exception ex)
		{
			logMonitor("FAIL - ISDN: " + order.getIsdn() + " - PRODUCTID: " + order.getProductId() + " - DESCRIPTION: "
						+ ex.getMessage());
			throw ex;
		}
		finally
		{
			this.closeProvisioningConnection(connection);
		}
		return Constants.BIND_ACTION_SUCCESS;
	}
}
