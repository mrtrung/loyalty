/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hungdt
 * 
 */
public class SubModifyTCPHandlerCollection extends HashMap<Long, SubModifyTCPHandler> {
	private static final long						serialVersionUID	= 1L;

	//public static Map<Long, SubModifyTCPHandler>	tcpHandler			= new HashMap<Long, SubModifyTCPHandler>();

	public SubModifyTCPHandler put(long key, SubModifyTCPHandler hand) {
		hand.setSubmodifyTCPHandlerCollection(this);
		return super.put(key, hand);
	}

	public SubModifyTCPHandler getByKey(long key) throws Exception {
		SubModifyTCPHandler handler = null;
		try {
			handler = get(key);
		}
		catch (Exception e) {
			throw e;
		}

		return handler;
	}
}
