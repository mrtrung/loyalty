/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : CheckAllVasStatusResp.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 3:52:30 PM 
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Sep 28, 2012      hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.iwebservice;

/**
 * @author hungdt
 * 
 */
public class CheckAllVasStatusResponse extends VasmanagerResponse {
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_CANCEL = 2;
	public static final int STATUS_UNKNOWN = 0;
	private int[] nVasID;
	private int[] nStatus;

	public CheckAllVasStatusResponse() {
		this.nVasID = new int[0];
		this.nStatus = new int[0];
	}

	public int[] getNVasID() {
		return this.nVasID;
	}

	public void setNVasID(int[] vasID) {
		this.nVasID = vasID;
	}

	public int[] getNStatus() {
		return this.nStatus;
	}

	public void setNStatus(int[] status) {
		this.nStatus = status;
	}
	
	
	public String toOrderString()
	{
		StringBuffer result = new StringBuffer();
		result.append(" | nVASID = ");
		
		for(int i : getNVasID())
		{
			result.append(i + ",");
		}
		result.append(" | nStatus = ");
		for(int i : getNStatus())
		{
			result.append(i + ",");
		}
		result.append(" | isdn = ");
		result.append(getIsdn());
		result.append(" | errorCode = ");
		result.append(getnErrorCode());
		result.append(" | errorCodeDetail = ");
		result.append(getsErrorDetail());
		
		
		return result.toString();
	}
}
