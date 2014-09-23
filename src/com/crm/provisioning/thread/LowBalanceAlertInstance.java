package com.crm.provisioning.thread;

import javax.jms.Message;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.impl.ccws.CCWSConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.CommandUtil;

public class LowBalanceAlertInstance extends ProvisioningInstance{

	public LowBalanceAlertInstance() throws Exception 
	{
		super();
	}
	
	public int processMessage(Message message) throws Exception
	{
		CommandMessage request = (CommandMessage) QueueFactory.getContentMessage(message);
		CCWSConnection connection = (CCWSConnection) this.getProvisioningConnection();
		double dataLimitation = 20*1024*1024;
		try
		{
			double dataAmount = connection.getBalance(request.getIsdn(), request.getParameters().getString("Balance.name", "GPRS")).getBalance();
			if (dataAmount < dataLimitation)
			{
				//Update send flag
				
				//Send Sms alert
				CommandUtil.sendSMS(this, request, request.getServiceAddress(), 
									request.getShipTo(), createContent(request.getIsdn(), dataAmount, ""));
			}
		}
		catch( Exception e)
		{
			
		}
		finally
		{
			this.closeProvisioningConnection(connection);
		}
		return Constants.BIND_ACTION_SUCCESS;
	}
	private String createContent(String isdn, double amount, String expirationDate)
	{
		String template = "Thue bao <isdn> chi con <amount> MB. Hay dang ky them de tiep tuc su dung dich vu. Xin cam on!";
		
		template = template.replaceAll("<isdn>", isdn);
		template = template.replaceAll("<amount>", String.valueOf(amount));
		
		return template;
	}
}
