package com.crm.ascs.net;

import java.util.Date;

public class TriggerRecharge extends Trigger
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1164074977172637505L;

	private double				faceValue			= 0;
	private int					expirationOffset	= 0;
	private int					batch				= 0;
	private int					serial				= 0;
	private Date				rechargeDate		= null;

	public double getFaceValue()
	{
		return faceValue;
	}

	public void setFaceValue(double faceValue)
	{
		this.faceValue = faceValue;
	}

	public int getExpirationOffset()
	{
		return expirationOffset;
	}

	public void setExpirationOffset(int expirationOffset)
	{
		this.expirationOffset = expirationOffset;
	}

	public int getBatch()
	{
		return batch;
	}

	public void setBatch(int batch)
	{
		this.batch = batch;
	}

	public int getSerial()
	{
		return serial;
	}

	public void setSerial(int serial)
	{
		this.serial = serial;
	}
	
	public Date getRechargeDate()
	{
		return rechargeDate;
	}
	
	public void setRechargeDate(Date rechargeDate)
	{
		this.rechargeDate = rechargeDate;
	}

	@Override
	public String getType()
	{
		return Trigger.TYPE_RECHARGE;
	}

	@Override
	public void setContent(String triggerContent) throws Exception
	{
		super.setContent(triggerContent);

		/**
		 * Recharge trigger: Recharge isdn cosname state coreAfterRecharge
		 * faceValue expirationOffset expireDate batch serial
		 * 
		 */
		String[] contents = triggerContent.split(SEPARATE_CHAR);

		try
		{
			setIsdn(contents[1].trim());
			setCosName(contents[2].trim());
			setState(contents[3].trim());
			setCoreBalance(Double.parseDouble(contents[4].trim()));
			setFaceValue(Double.parseDouble(contents[5].trim()));
			setExpirationOffset(Integer.parseInt(contents[6].trim()));
			setExpireDate(dateFromString(contents[7].trim()));

			try
			{
				setBatch(Integer.parseInt(contents[8].trim()));
			}
			catch (Exception e)
			{
			}

			try
			{
				setSerial(Integer.parseInt(contents[9].trim()));
			}
			catch (Exception e)
			{
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	public String toString()
	{
		String returnStr = getType() + ","
						+ getIsdn() + ","
						+ getCosName() + ","
						+ getState() + ","
						+ Math.round(getCoreBalance()) + ","
						+ Math.round(getFaceValue()) + ","
						+ getExpirationOffset() + ","
						+ stringFromDate(getExpireDate()) + ","
						+ (getBatch() > 0 ? getBatch() : "[]") + ","
						+ (getSerial() > 0 ? getSerial() : "[]");

		return returnStr;
	}

	@Override
	public void setRemoteHost(String remoteHost) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRemotePort(int remotePort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}
}
