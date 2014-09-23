/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.net.DatagramPacket;

import com.crm.cgw.net.INetDataCollection;
import com.crm.cgw.net.INetHandler;
import com.crm.cgw.net.INetAnalyzer;
import com.crm.cgw.thread.SubModifyTCPThread;
import com.crm.util.StringUtil;

/**
 * @author hungdt
 * 
 */
public class SubModifyTCPAnalyzer implements INetAnalyzer {
	private static final String	SEPARATE_CHARS	= ";";

	private String				lastData		= "";
	private SubModifyTCPHandler	handler			= null;

	public void debugMonitor(Object message) {
		if (handler != null)
			handler.debugMonitor(message);
	}

	public void setHandler(INetHandler handler) {
		this.handler = (SubModifyTCPHandler) handler;
	}

	public INetHandler getHandler() {
		return handler;
	}

	@Override
	public void createObject(Object data, INetDataCollection collection) {
		byte[] bytes = (byte[]) data;
		String receiveData = new String(bytes);
		String hexData = ""; // StringUtil.toHexString(bytes, 0, bytes.length);

		debugMonitor("RECEIVE from #" + handler.getHandlerId() + ": " + hexData
				+ "[ASCII:" + receiveData + "]");
		synchronized (lastData) {
			receiveData = lastData + receiveData;
			{
				/**
				 * parse then remove 2 first bytes
				 */
				String receive = receiveData;
				try {
					collection.put(Charging.createCharging(receive, handler.getHandlerId()));

				}
				catch (Exception e) {
					debugMonitor(e.getMessage() + " - Can not parse request: "
							+ receive);
					debugMonitor(e);
				}

			}

			lastData = receiveData;
		}

	}
}
