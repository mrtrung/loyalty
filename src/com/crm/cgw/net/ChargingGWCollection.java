/**
 * 
 */
package com.crm.cgw.net;

import java.util.ArrayList;

/**
 * @author hungdt
 * 
 */
public class ChargingGWCollection extends ArrayList<INetData> implements INetDataCollection
{

	private static final long	serialVersionUID	= 1L;
	private Object				lockedObject		= new Object();

	@Override
	public void put(INetData data)
	{
		synchronized (lockedObject)
		{
			add(data);
		}

	}

	@Override
	public INetData get()
	{
		synchronized (lockedObject)
		{
			if (size() > 0)
				return remove(0);
			else
				return null;
		}
	}

	@Override
	public int size()
	{
		synchronized (lockedObject)
		{
			return super.size();
		}
	}

}
