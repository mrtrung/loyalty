/**
 * 
 */
package com.crm.cgw.thread;

import javax.jms.JMSException;

import com.crm.cgw.extdebit.ExtDebit;
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
public class ExtDebitInstance extends DispatcherInstance {

	public ExtDebitInstance() throws Exception {
		super();

	}

	public ExtDebitThread getDispatcher() {
		return (ExtDebitThread) super.getDispatcher();

	}

	@Override
	public void doProcessSession() throws Exception {
		MQConnection connection = null;
		int count = 0;
		try {
			connection = getMQConnection();
			ExtDebit extDebit = null;
			do {
				extDebit = getDispatcher().getWork();

				if (extDebit == null)
					break;

				long startTime = System.currentTimeMillis();

				try {
					CommandMessage message = new CommandMessage();
					message.setIsdn(extDebit.getBNumber().split("=")[1]);
					message.setServiceAddress(extDebit.getANumber().split("=")[1]);
					message.setCont_type(Integer.parseInt(extDebit.getContType().split("=")[1]));
					message.setChannel(Constants.CHANNEL_WEB);
					message.setKeyword(getDispatcher().keywordPrefix+ extDebit.getANumber().split("=")[1]);
					message.setUserName("system");
					message.setTimeout(getDispatcher().debitTimeout * 1000);
					message.setCgwStatus(extDebit.getServiceState().split("=")[1]);
					
					message.setOrderId(Long.parseLong(extDebit.getsTransId().split("=")[1]));
					AppProperties app = new AppProperties();
					app.setString("handlerId", extDebit.getExtDebit_seq());
					//app.setString("sessionId", );
					app.setString("fromReq", "EXTDEBIT");
					message.setParameters(app);
					int chargeTimeout = ((ExtDebitThread) getDispatcher()).debitTimeout;

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
					debugMonitor("Sent extdebit request to queue cost: "
							+ costQueue + "ms, Content: " + extDebit.getContent());
				} catch (Exception e) {
					debugMonitor(e);
				}
				
			} while (isAvailable());
		} catch (Exception e) {
			if (e instanceof JMSException) {
				if (connection != null)
					connection.markError();
			}
			throw e;
		} finally {
			returnMQConnection(connection);
		}
	}

}
