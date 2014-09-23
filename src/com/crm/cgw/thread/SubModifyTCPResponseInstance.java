/**
 * 
 */
package com.crm.cgw.thread;

import javax.jms.Message;

import com.crm.cgw.submodifytcp.CharggwConnection;
import com.crm.cgw.submodifytcp.Charging;
import com.crm.cgw.submodifytcp.ChargingResp;
import com.crm.cgw.submodifytcp.SubModifyTCPHandler;
import com.crm.cgw.submodifytcp.SubModifyTCPHandlerCollection;
import com.crm.cgw.submodifytcp.SubModifyTCPServer;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.impl.charging.SubModifyConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherInstance;

/**
 * @author hungdt
 *
 */
public class SubModifyTCPResponseInstance extends DispatcherInstance {

	public SubModifyTCPResponseInstance() throws Exception {
		super();
	}

	
	@Override
	public int processMessage(Message message) throws Exception
	{
		
		CommandMessage request = null;
		long handlerId = 0;
		try {
			request = ChargingResp.getFromMQMessage(message);
			if(request == null)
				return Constants.BIND_ACTION_NONE;
			handlerId = request.getParameters().getLong("subHandlerId", 0);
			ChargingResp resp = new ChargingResp();
			
			resp.setM_iSequence((int) request.getRequestId());
			resp.setM_sMDN(request.getIsdn());
		//	resp.setM_iSequence((int) request.getOrderId());
 			resp.setM_sErrorCode(request.getCause());
			
			SubModifyTCPHandlerCollection collectorHandlers = SubModifyTCPServer.collectorHandlers;
			SubModifyTCPHandler handler = collectorHandlers.getByKey(handlerId);
			CharggwConnection connection = (CharggwConnection) handler.getConnection();
			
			connection.send(resp);
			
			collectorHandlers.remove(handlerId);

			debugMonitor("Response client: "  + resp.getContent());
			
		}
		catch (Exception e) {
			debugMonitor(e);
		}
		return Constants.BIND_ACTION_NONE;
	}
}
