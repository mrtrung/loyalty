package test;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.xml.rpc.ServiceException;

import org.csapi.www.cs.schema.TpChargingSessionID;

import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.impl.osa.OSAConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.osa.OSACommandInstance;
import com.crm.util.AppProperties;

public class OSATest
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			OSAConnection connection = new OSAConnection();
			connection.setHost("http://rtbstest.htmobile.com.vn/osa/mobility");
			connection.setTimeout(5000);
			connection.setUserName("nms");
			connection.setPassword("nms");
			AppProperties parameters = new AppProperties();
			parameters.setString("callbackHost", "10.32.62.96");
			parameters.setString("callbackPort", "5000");
			parameters.setString("applicationName", "NMS");
			parameters.setString("serviceDescription", "VASMAN");
			parameters.setString("currency", "VND");

			parameters.setString("merchantAccount", "MCA");
			parameters.setString("merchantId", "4");

			connection.setParameters(parameters);

			try
			{
				connection.openConnection();
				CommandMessage message = new CommandMessage();
				message.setIsdn("84922000514");
				message.setAmount(20000.0);
				String description = "test";

				OSACommandInstance instance = new OSACommandInstance();
				try
				{
					//instance.queueConnection = QueueFactory.createQueueConnection();

					// create a queue session
					//instance.queueSession = instance.queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
					instance.queueCallback = QueueFactory.getQueue("test/CommandCallback");
					TpChargingSessionID chargingSessionId = connection.createChargingSession(message.getIsdn(), description);
					connection.directCredit(instance, message, chargingSessionId, description);
					//connection.directDebit(instance, message, chargingSessionId, description);
				}
				finally
				{
					//instance.queueConnection.close();
				}
			}
			catch (Exception e)
			{
				throw e;
			}
			finally
			{
				connection.closeConnection();
			}
			// Date afterDate = new Date();
			// System.out.println("after date:" + afterDate.getTime() +
			// " millisecond.");
			System.out.println("OK");

		}
		catch (ServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
