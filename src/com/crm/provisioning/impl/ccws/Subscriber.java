package com.crm.provisioning.impl.ccws;

import org.apache.axis.AxisFault;
import com.comverse_in.prepaid.ccws.*;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Subscriber extends com.comverse_in.prepaid.ccws.SubscriberRetrieve
{
	public Subscriber()
	{
		super();
	}

	// Tai khaon chinh name =""
	public BalanceEntity getBalance(String balanceName)
	{
		BalanceEntity[] data = this.getSubscriberData().getBalances().getBalance();
		for (int i = 0; i <= data.length - 1; i++)
		{
			if (data[i].getBalanceName().equals(balanceName))
				return data[i];
		}
		return null;
	}

	public SubscriberEntity getSubscriberInfor()
	{
		return this.getSubscriberData();
	}

}
