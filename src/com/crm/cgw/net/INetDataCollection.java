package com.crm.cgw.net;

import java.util.List;

public interface INetDataCollection extends List<INetData>
{
	public void put(INetData data);

	public INetData get();
}
