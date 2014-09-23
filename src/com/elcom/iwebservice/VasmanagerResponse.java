/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : VasmanagerResp.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 11:58:55 AM 
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
public class VasmanagerResponse {
	private String isdn;
	private String nErrorCode;
	private String sErrorDetail;

	public VasmanagerResponse() {
		this.isdn = "";
		this.nErrorCode = "";
		this.sErrorDetail = "";
	}

	public String getIsdn() {
		return isdn;
	}

	public void setIsdn(String isdn) {
		this.isdn = isdn;
	}

	public String getnErrorCode() {
		return nErrorCode;
	}

	public void setnErrorCode(String nErrorCode) {
		this.nErrorCode = nErrorCode;
	}

	public String getsErrorDetail() {
		return sErrorDetail;
	}

	public void setsErrorDetail(String sErrorDetail) {
		this.sErrorDetail = sErrorDetail;
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
