package com.crm.ascs.net;

import java.io.Serializable;

public interface INetData extends Serializable
{
	public byte[] getData();
	
	public String getContent();

	void setRemoteHost(String remoteHost);

	String getRemoteHost();

	void setRemotePort(int remotePort);

	int getRemotePort();
}
