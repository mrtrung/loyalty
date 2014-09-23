package com.crm.product.thread.autorenew;

import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;

public abstract class AbstractAutoRenew
{
	protected String					chanel;
	protected String					deliveryUser;
	protected int						orderTimeOut	= 60000;

	protected String					keyword;
	protected String					serviceAddress;

	protected int						subscriberType;
	protected int						attachQueue		= 0;
	protected AbtractAutoRenewThread	abtractAutoRenewThread;

	public int getAttachQueue()
	{
		return attachQueue;
	}

	public void setAttachQueue(int attachQueue)
	{
		this.attachQueue = attachQueue;
	}

	public int getSubscriberType()
	{
		return subscriberType;
	}

	public void setSubscriberType(int subscriberType)
	{
		this.subscriberType = subscriberType;
	}

	public String getChanel()
	{
		return chanel;
	}

	public void setChanel(String chanel)
	{
		this.chanel = chanel;
	}

	public String getDeliveryUser()
	{
		return deliveryUser;
	}

	public void setDeliveryUser(String deliveryUser)
	{
		this.deliveryUser = deliveryUser;
	}

	public int getOrverTime()
	{
		return orderTimeOut;
	}

	public void setOrderTimeOut(int orderTimeOut)
	{
		this.orderTimeOut = orderTimeOut;
	}

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	public String getServiceAddress()
	{
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress)
	{
		this.serviceAddress = serviceAddress;
	}

	public AbtractAutoRenewThread getAbtractAutoRenewThread()
	{
		return abtractAutoRenewThread;
	}

	public void setAbtractAutoRenewThread(AbtractAutoRenewThread abtractAutoRenewThread)
	{
		this.abtractAutoRenewThread = abtractAutoRenewThread;
	}

	public abstract void processRecord(String isdn, CommandMessage commandMessage) throws Exception;
}
