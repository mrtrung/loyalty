/**
 * 
 */
package com.crm.cgw.extdebit;

import java.util.HashMap;

import com.crm.cgw.submodifytcp.SubModifyTCPHandler;

/**
 * @author hungdt
 *
 */
public class ExtDebitHandlerCollection extends HashMap<Long, ExtDebitHandler> {
	private static final long						serialVersionUID	= 1L;

	//public static Map<Long, SubModifyTCPHandler>	tcpHandler			= new HashMap<Long, SubModifyTCPHandler>();

	public ExtDebitHandler put(long key, ExtDebitHandler hand) {
		hand.setHandlerCollection(this);
		return super.put(key, hand);
	}

	public ExtDebitHandler getByKey(long key) throws Exception {
		ExtDebitHandler handler = null;
		try {
			handler = get(key);
		}
		catch (Exception e) {
			throw e;
		}

		return handler;
	}
}
