package com.crm.thread;

import com.fss.thread.ThreadConstant;

public class SimulatorRouteExt extends Thread
{
	String				deliveryUser;
	String				channel;
	String				serviceAddress;
	String				fromIsdn;
	String				toIsdn;
	String				shipTo;
	String				keyword;
	int					batchSize;
	int					instanceSize	= 1;
	int					timeBetweenLoop;
	long				currentIsdn		= 0;
	SimulatorThreadExt	simulatorThreadExt;

	public String getDeliveryUser()
	{
		return deliveryUser;
	}

	public void setDeliveryUser(String deliveryUser)
	{
		this.deliveryUser = deliveryUser;
	}

	public int getInstanceSize()
	{
		return instanceSize;
	}

	public void setInstanceSize(int instanceSize)
	{
		this.instanceSize = instanceSize;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public String getServiceAddress()
	{
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress)
	{
		this.serviceAddress = serviceAddress;
	}

	public String getFromIsdn()
	{
		return fromIsdn;
	}

	public void setFromIsdn(String fromIsdn)
	{
		this.fromIsdn = fromIsdn;
	}

	public String getToIsdn()
	{
		return toIsdn;
	}

	public void setToIsdn(String toIsdn)
	{
		this.toIsdn = toIsdn;
	}

	public String getShipTo()
	{
		return shipTo;
	}

	public void setShipTo(String shipTo)
	{
		this.shipTo = shipTo;
	}

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	public int getTimeBetweenLoop()
	{
		return timeBetweenLoop;
	}

	public void setTimeBetweenLoop(int timeBetweenLoop)
	{
		this.timeBetweenLoop = timeBetweenLoop;
	}

	public SimulatorThreadExt getSimulatorThreadExt()
	{
		return simulatorThreadExt;
	}

	public void setSimulatorThreadExt(SimulatorThreadExt simulatorThreadExt)
	{
		this.simulatorThreadExt = simulatorThreadExt;
	}

	public void setCurrentIsdn(long currentIsdn)
	{
		this.currentIsdn = currentIsdn;
	}

	public synchronized long getCurrentIsdn() throws Exception
	{
		if (currentIsdn == 0)
		{
			currentIsdn = Long.parseLong(fromIsdn);
		}
		else
		{
			currentIsdn++;
		}

		if (currentIsdn > Long.parseLong(toIsdn))
		{
			return 0;
		}

		return currentIsdn;
	}

	public void run()
	{
		try
		{
			for (int i = 0; i < instanceSize; i++)
			{
				SimulatorInstanceExt instanceExt = new SimulatorInstanceExt(this);
				instanceExt.start();
			}
			while (simulatorThreadExt.miThreadCommand != ThreadConstant.THREAD_STOP)
			{
				try
				{
					Thread.sleep(10000L);
				}
				catch (InterruptedException e)
				{
					simulatorThreadExt.debugMonitor(e);
				}
			}
		}
		catch (Exception ex)
		{
			simulatorThreadExt.debugMonitor(ex);
		}
	}
}
