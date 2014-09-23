package com.crm.ascs.net;

public class TriggerActivation extends Trigger
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7213174388487435854L;

	@Override
	public String getType()
	{
		return Trigger.TYPE_ACTIVATION;
	}

	@Override
	public void setContent(String triggerContent) throws Exception
	{
		// TODO Auto-generated method stub
		super.setContent(triggerContent);

		/**
		 * Activation trigger: Activation isdn cosname previousState state core
		 * expireDate
		 * 
		 */
		String[] contents = triggerContent.split(SEPARATE_CHAR);

		try
		{
			setIsdn(contents[1].trim());
			setCosName(contents[2].trim());
			setPreviousState(contents[3].trim());
			setState(contents[4].trim());
			setCoreBalance(Double.parseDouble(contents[5].trim()));
			setExpireDate(dateFromString(contents[6].trim()));
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	@Override
	public String toString()
	{
		String returnStr = getType() +  ","
						+ getIsdn() + ","
						+ getCosName() + ","
						+ getPreviousState() + ","
						+ getState() + ","
						+ Math.round(getCoreBalance()) + ","
						+ stringFromDate(getExpireDate());
		
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
