package test;

import com.crm.provisioning.impl.smpp.SMPPConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.ProvisioningThread;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.thread.DispatcherThread;
import com.crm.util.AppProperties;

public class SMPPTest
{
	public static void main(String[] params) throws Exception
	{
		SMPPConnection connection = new SMPPConnection();
		connection.setDispatcher(new ProvisioningThread());
		connection.setHost("10.8.13.31");
		connection.setPort(8000);
		connection.setUserName("namta");
		connection.setPassword("namta");
		
		AppProperties parameters = new AppProperties();
		parameters.put("bindOption", "transmitter");
		parameters.put("useConcatenated", true);
		parameters.put("asynchronous", true);
		parameters.put("addressRange", "8000");
		connection.setParameters(parameters);
		
		try
		{
			connection.openConnection();
			
			CommandMessage message = new CommandMessage();
			
			message.setResponseValue(ResponseUtil.SMS_HREF, "http://203.162.70.235/ReportOnline8x/Download?f=kHdShsOn1m6Sgyh81m/uxQ==");
			message.setResponseValue(ResponseUtil.SMS_TEXT, "VIDEO HOT_Kinh hoang tro boc dau xe gion mat tu than:");
			message.setIsdn("84922000512");
			message.setServiceAddress("123");
			
			connection.submitWSPMessage(message);
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			connection.closeConnection();
		}
	}
}
