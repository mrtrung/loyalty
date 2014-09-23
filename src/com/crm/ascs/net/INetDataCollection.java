package com.crm.ascs.net;


public interface INetDataCollection
{
	public void put(INetData data);

	public INetData get();
	
	public int size();
}
