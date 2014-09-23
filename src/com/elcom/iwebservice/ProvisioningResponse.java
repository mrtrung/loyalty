/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : ProvisioningResp.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 3:59:04 PM 
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
public class ProvisioningResponse extends VasmanagerResponse {
	private int nVasID;

	public ProvisioningResponse() {
		this.nVasID = 0;
	}

	public int getNVasID() {
		return this.nVasID;
	}

	public void setNVasID(int vasID) {
		this.nVasID = vasID;
	}
	
	public String toOrderString()
	{
		StringBuffer result = new StringBuffer();

		result.append(" | isdn = ");
		result.append(getIsdn());
		result.append(" | errorCode = ");
		result.append(getnErrorCode());
		result.append(" | errorCodeDetail = ");
		result.append(getsErrorDetail());
		
		
		return result.toString();
	}
}
