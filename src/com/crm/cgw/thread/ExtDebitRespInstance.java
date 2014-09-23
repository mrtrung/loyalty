/**
 * 
 */
package com.crm.cgw.thread;

import javax.jms.Message;

import com.crm.cgw.extdebit.ExtDebitConnection;
import com.crm.cgw.extdebit.ExtDebitHandler;
import com.crm.cgw.extdebit.ExtDebitHandlerCollection;
import com.crm.cgw.extdebit.ExtDebitResp;
import com.crm.cgw.extdebit.ExtDebitServer;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherInstance;

/**
 * @author hungdt
 * 
 */
public class ExtDebitRespInstance extends DispatcherInstance {

	public ExtDebitRespInstance() throws Exception {
		super();
	}

	public int processMessage(Message message) throws Exception {

		CommandMessage request = null;
		long handlerId = 0;

		try {
			request = ExtDebitResp.getFromMQMessage(message);
			if (request == null)
				return Constants.BIND_ACTION_NONE;

			handlerId = request.getParameters().getLong("handlerId", 0);

			ExtDebitResp resp = new ExtDebitResp();

			resp.setM_iSequence((int) request.getOrderId());
			resp.setM_sMDN(request.getIsdn());
			resp.setM_iSequence((int) request.getOrderId());
			resp.setM_sErrorCode(request.getCause());

			ExtDebitHandlerCollection collectorHandlers = ExtDebitServer.collectorHandlers;

			ExtDebitHandler handler = collectorHandlers.getByKey(handlerId);

			ExtDebitConnection connection = (ExtDebitConnection) handler
					.getConnection();

			connection.sendClient(resp);

			collectorHandlers.remove(handlerId);

			debugMonitor("Response client: " + resp.getContent());
		} catch (Exception e) {
			throw e;
		}

		return Constants.BIND_ACTION_NONE;
	}

	public static int HexaToInteger(String value) {
		String hex = value.split("x")[1];
		int i = Integer.valueOf(hex, 16).intValue();
		return i;
	}
}
