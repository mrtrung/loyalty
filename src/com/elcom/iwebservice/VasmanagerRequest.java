/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : VasmanagerReq.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Date Sep 28, 2012 11:56:12 AM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Sep 28, 2012     hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.iwebservice;

/**
 * @author hungdt
 *
 */
public class VasmanagerRequest {
	private String mdn;
	
	public VasmanagerRequest(){
		this.mdn = "";
	}

	public String getMdn()
	{
		return mdn;
	}

	public void setMdn(String mdn)
	{
		this.mdn = mdn;
	}

	
	
	
}
