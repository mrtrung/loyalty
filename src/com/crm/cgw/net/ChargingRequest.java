/**
 * 
 */
package com.crm.cgw.net;

/**
 * @author hungdt
 *
 */
public class ChargingRequest extends Charging
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7213174388487435854L;
	
	public void setContent(String chargingContent) throws Exception
	{
		super.setContent(chargingContent);
		
		String[] contents = chargingContent.split(SEPARATE_CHAR);
		
		
	}
}
