/**
 * 
 */
package com.crm.cgw.extdebit;

import com.crm.cgw.net.INetAnalyzer;
import com.crm.cgw.net.INetDataCollection;
import com.crm.cgw.net.INetHandler;
import com.crm.cgw.submodifytcp.Charging;
import com.crm.cgw.submodifytcp.SubModifyTCPHandler;

/**
 * @author hungdt
 *
 */
public class ExtDebitAnalyzer implements INetAnalyzer {

	private static final String	SEPARATE_CHARS	= ";";

	private String				lastData		= "";
	private ExtDebitHandler	handler			= null;

	public void debugMonitor(Object message) {
		if (handler != null)
			handler.debugMonitor(message);
	}

	public void setHandler(INetHandler handler) {
		this.handler = (ExtDebitHandler) handler;
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
				+ "[DEBIT:" + receiveData + "]");
		synchronized (lastData) {
			receiveData = lastData + receiveData;

			int endIndex = receiveData.indexOf(SEPARATE_CHARS);
			int startIndex = 0;
			{
				/**
				 * parse then remove 2 first bytes
				 */
				String receive = receiveData;
				try {
					collection.put(ExtDebit.setContentReq(receive, String.valueOf(handler.getHandlerId())));

				}
				catch (Exception e) {
					debugMonitor(e.getMessage() + " - Can not parse request: "
							+ receive);
					debugMonitor(e);
				}

//				startIndex = endIndex + SEPARATE_CHARS.length();
//
//				endIndex = receiveData.indexOf(SEPARATE_CHARS, startIndex);
			}

			lastData = receiveData;
		}
	}

}
