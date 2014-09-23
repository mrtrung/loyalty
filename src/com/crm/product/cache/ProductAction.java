package com.crm.product.cache;

import com.crm.kernel.index.IndexNode;
import com.crm.util.CompareUtil;

public class ProductAction extends IndexNode
{
	private long	productId		= 0;
	private long	commandId		= 0;
	private int		subscriberType	= 1;
	private String	actionType		= "";

	public ProductAction()
	{
		super();
	}

	public long getProductId()
	{
		return productId;
	}

	public void setProductId(long productId)
	{
		this.productId = productId;
	}

	public long getCommandId()
	{
		return commandId;
	}

	public void setCommandId(long commandId)
	{
		this.commandId = commandId;
	}

	public int getSubscriberType()
	{
		return subscriberType;
	}

	public void setSubscriberType(int subscriberType)
	{
		this.subscriberType = subscriberType;
	}

	public String getActionType()
	{
		return actionType;
	}

	public void setActionType(String actionType)
	{
		this.actionType = actionType;
	}

	@Override
	public int compareTo(IndexNode obj)
	{
		ProductAction lookup = (ProductAction)obj;
		
		int result = CompareUtil.compareString(getActionType(), lookup.getActionType(), false);
		
		if (result == 0)
		{
			result = CompareUtil.compare(getSubscriberType(), lookup.getSubscriberType());
		}
		
		return result;
	}
}
