package com.crm.ascs.net;

import com.crm.ascs.collector.TriggerClientHandler;

public interface INetAnalyzer
{
	public void createObject(Object data, INetDataCollection collection);

	public void setHandler(TriggerClientHandler triggerClientHandler);
}
