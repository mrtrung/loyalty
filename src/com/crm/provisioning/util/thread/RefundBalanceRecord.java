package com.crm.provisioning.util.thread;

import java.io.Serializable;

public class RefundBalanceRecord implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6870067786095144527L;
	private String				isdn				= "";
	private String				balanceName			= "";
	private double				amount				= 0;

	public RefundBalanceRecord(String isdn, String balanceName, double amount)
	{
		setIsdn(isdn);
		setBalanceName(balanceName);
		setAmount(amount);
	}

	public String getIsdn()
	{
		return isdn;
	}

	public void setIsdn(String isdn)
	{
		this.isdn = isdn;
	}

	public void setBalanceName(String balanceName)
	{
		this.balanceName = balanceName;
	}

	public String getBalanceName()
	{
		return balanceName;
	}

	public double getAmount()
	{
		return amount;
	}

	public void setAmount(double amount)
	{
		this.amount = amount;
	}

	@Override
	public String toString()
	{
		return "[RefundRecord: " + isdn + "," + amount + "," + balanceName + "]";
	}
}
