package com.crm.provisioning.util.thread;

import java.util.Calendar;

import javax.jms.Message;

import com.comverse_in.prepaid.ccws.BalanceEntity;
import com.comverse_in.prepaid.ccws.BalanceEntityBase;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.impl.ccws.CCWSConnection;
import com.crm.provisioning.thread.ProvisioningInstance;

public class ModifyBalanceInstance extends ProvisioningInstance
{
	@Override
	public ModifyBalanceThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (ModifyBalanceThread)super.getDispatcher();
	}

	public ModifyBalanceInstance() throws Exception
	{
		super();
	}

	@Override
	public int processMessage(Message message) throws Exception
	{
		RefundBalanceRecord record = (RefundBalanceRecord) QueueFactory.getContentMessage(message);

		if (record != null)
		{
			BalanceEntityBase balance = new BalanceEntityBase();

			Calendar expirationDate = Calendar.getInstance();
			expirationDate.add(Calendar.DAY_OF_MONTH, 1);

			balance.setBalance(record.getAmount());
			balance.setBalanceName(record.getBalanceName());
			balance.setAccountExpiration(expirationDate);
			
			String ccwsComment = getDispatcher().ccwsComment;

			CCWSConnection connection = null;
			try
			{
				connection = (CCWSConnection) getProvisioningConnection();
				
				BalanceEntity currentBalance = connection.getBalance(record.getIsdn(), balance.getBalanceName());
				
				double addedBalance = balance.getBalance();
				
				if (currentBalance.getBalance() > 0)
				{
					balance.setBalance(addedBalance + currentBalance.getBalance());
				}
				
				connection.setBalance(record.getIsdn(), 
						new BalanceEntityBase[] { balance }, 
						Calendar.getInstance(), 
						ccwsComment);
				debugMonitor("Add(" + record.getIsdn() + "," + balance.getBalanceName() + "): "
						+ currentBalance.getBalance() + " + "
						+ addedBalance
						+ " = " + balance.getBalance()
						+ " | success");

			}
			catch (Exception e)
			{
				debugMonitor("Add(" + record.getIsdn() + "," + balance.getBalanceName() + "): " + balance.getBalance()
						+ " | failed");
				debugMonitor(e);
			}
			finally
			{
				closeProvisioningConnection(connection);
			}

		}

		return Constants.BIND_ACTION_NONE;
	}
}
