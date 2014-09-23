package com.crm.ascs.net;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TriggerCollection implements INetDataCollection
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private ConcurrentLinkedQueue<INetData> triggers = new ConcurrentLinkedQueue<INetData>();

	@Override
	public void put(INetData data)
	{
		triggers.offer(data);
	}

	@Override
	public INetData get()
	{
		return triggers.poll();
	}

	@Override
	public int size()
	{
		return triggers.size();
	}
}
