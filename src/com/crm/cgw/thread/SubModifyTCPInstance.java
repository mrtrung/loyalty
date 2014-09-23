/**
 * 
 */
package com.crm.cgw.thread;

import javax.jms.JMSException;

import com.crm.cgw.submodifytcp.ChangeBalance;
import com.crm.cgw.submodifytcp.ChangeState;
import com.crm.cgw.submodifytcp.Charging;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherInstance;
import com.crm.util.AppProperties;

/**
 * @author hungdt
 *
 */
public class SubModifyTCPInstance extends DispatcherInstance {

	public SubModifyTCPInstance() throws Exception {
		super();
	}
	
	public SubModifyTCPThread getDispatcher()
	{
		return (SubModifyTCPThread) super.getDispatcher();
	}
	
	public void doProcessSession() throws Exception
	{
		MQConnection connection = null;
		int count = 0;
		try
		{
			connection = getMQConnection();
			Charging charging = null;
			do
			{
				charging = ((SubModifyTCPThread) getDispatcher()).getWork();
				if(charging == null)
					break;
				long startTime = System.currentTimeMillis();
				try {
					CommandMessage message = new CommandMessage();
					AppProperties app = new AppProperties();
					if(charging instanceof ChangeBalance)
					{
						app.setString("fromReq", "SUBMODIFYTCP");
						app.setLong("subHandlerId", charging.getCharg_seq());
						
						message.setParameters(app);
						message.setUserName(((ChangeBalance) charging).getAccount());
//						message.setSubmodifyBalance(((ChangeBalance) charging).getBalance());
//						message.setSubmodifyAmount(((ChangeBalance) charging).getAmount());
//						message.setSubmodifyExpireDate(((ChangeBalance) charging).getExpireDate());
						message.setDescription(((ChangeBalance) charging).getComment());
						message.setChannel(Constants.CHANNEL_WEB);
						message.setKeyword(getDispatcher().keywordPrefix + "BALANCE");
						message.setServiceAddress("345");
						message.setTimeout(getDispatcher().chargingTimeout * 1000);
						message.setIsdn(((ChangeBalance) charging).getMdn());
						message.setRequestId(((ChangeBalance) charging).getId());
					}
					else if (charging instanceof ChangeState)
					{
						app.setString("fromReq", "SUBMODIFYTCP");
						app.setLong("subHandlerId", charging.getCharg_seq());
						app.setString("state", ((ChangeState) charging).getState());
						message.setParameters(app);
						
						message.setChannel(Constants.CHANNEL_WEB);
						message.setKeyword(getDispatcher().keywordPrefix + "STATE");
						message.setServiceAddress("345");
						message.setTimeout(getDispatcher().chargingTimeout * 1000);
						message.setDescription(((ChangeState) charging).getComment());
						message.setIsdn(((ChangeState) charging).getMdn());
						message.setRequestId(((ChangeState) charging).getId());
					}
					
					int chargeTimeout = ((SubModifyTCPThread) getDispatcher()).chargingTimeout;
					try {
						connection = getMQConnection();
						connection.sendMessage(message, chargeTimeout, queueWorking, chargeTimeout, getDispatcher().queuePersistent);
						count++;
					} catch (Exception e) {
						debugMonitor(e);
					} finally {
						returnMQConnection(connection);
					}
					
					long endTime = System.currentTimeMillis();
					long costQueue = endTime - startTime;
					debugMonitor("Sent submodifyTCP request to queue cost: "
							+ costQueue + "ms, Content: " + charging.getContent());
				} catch (Exception e) {
					debugMonitor(e);
				}
			
				
			
				
			} while (isAvailable());
		}
		catch (Exception e)
		{
			if (e instanceof JMSException)
			{
				if (connection != null)
					connection.markError();
			}
			throw e;
		}
		finally
		{
			returnMQConnection(connection);
		}
	}

}
