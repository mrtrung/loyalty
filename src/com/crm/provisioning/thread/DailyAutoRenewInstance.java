package com.crm.provisioning.thread;
import java.util.Calendar;

import javax.jms.Message;

import com.comverse_in.prepaid.ccws.BalanceEntity;
import com.comverse_in.prepaid.ccws.BalanceEntityBase;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.impl.ccws.CCWSConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.subscriber.impl.SubscriberRenewDailyImpl;

public class DailyAutoRenewInstance extends ProvisioningInstance 
{
	public DailyAutoRenewInstance() throws Exception
	{
		super();
	}
	
	public int processMessage(Message message) throws Exception
	{
		BalanceEntityBase[] balances = new BalanceEntityBase[1];	
		CommandMessage request = (CommandMessage) QueueFactory
				.getContentMessage(message);
		CCWSConnection connection = (CCWSConnection) this.getProvisioningConnection();
		try
		{
			// Get remain balances
			BalanceEntity balance = new BalanceEntity();
			balance = connection.getBalance(request.getIsdn(), ((DailyAutoRenewThread) dispatcher).get_balanceName());
			// Modify balance
            balance.setBalance(balance.getBalance() + ((DailyAutoRenewThread) dispatcher).get_balanceAmount());
            Calendar newDate = balance.getAccountExpiration();
            newDate.add(Calendar.HOUR_OF_DAY, 24 * ((DailyAutoRenewThread) dispatcher).get_balanceExpiration());
            balance.setAccountExpiration(newDate);
            
            balances[0]=balance;
            
			// Add balance to subscriber.

			connection.setBalance(request.getIsdn(), balances, newDate,((DailyAutoRenewThread) dispatcher).get_mtrComment());
			
			// insert to DailyRenewService 
			int status = Constants.SERVICE_STATUS_DENIED;
			int retryTime = 0;
			int maxRetry = ((DailyAutoRenewThread) dispatcher).get_retryTime();
			while (retryTime < maxRetry  && status != Constants.SERVICE_STATUS_APPROVED)
			{
				
				status = SubscriberRenewDailyImpl.insertRenewChargeReq(request.getIsdn(), request.getProductId(),
														  request.getOrderDate());
				retryTime++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			balances = null;
			request = null;
			this.closeProvisioningConnection(connection);
		}
		return Constants.BIND_ACTION_SUCCESS;
	}
}