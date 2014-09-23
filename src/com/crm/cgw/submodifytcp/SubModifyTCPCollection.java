/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.util.ArrayList;

import com.crm.cgw.net.INetData;
import com.crm.cgw.net.INetDataCollection;

/**
 * @author hungdt
 * 
 */
public class SubModifyTCPCollection extends ArrayList<INetData> implements INetDataCollection
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
