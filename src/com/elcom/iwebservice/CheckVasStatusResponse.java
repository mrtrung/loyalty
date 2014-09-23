/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : CheckVasStatusResp.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 3:54:52 PM 
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
public class CheckVasStatusResponse extends VasmanagerResponse {
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_CANCEL = 3;
	public static final int STATUS_UNKNOWN = 0;
	private int nVasID = 0;
	private int nStatus = 0;

	public long getNVasID() {
		return this.nVasID;
	}

	public void setNVasID(int vasID) {
		this.nVasID = vasID;
	}

	public int getNStatus() {
		return this.nStatus;
	}

	public void setNStatus(int status) {
		this.nStatus = status;
	}
	
	public String toOrderString()
	{
		StringBuffer result = new StringBuffer();

		result.append(" | nVASID = ");
		result.append(getNVasID());
		result.append(" | nStatus = ");
		result.append(getNStatus());
		result.append(" | isdn = ");
		result.append(getIsdn());
		result.append(" | errorCode = ");
		result.append(getnErrorCode());
		result.append(" | errorCodeDetail = ");
		result.append(getsErrorDetail());
		
		
		return result.toString();
	}
}
