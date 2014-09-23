package com.crm.cgw.net;

public interface INetConnection
{	
	public void setHandler(INetHandler handler);
	public INetHandler getHandler();
}
