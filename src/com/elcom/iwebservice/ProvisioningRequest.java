/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : ProvisioningReq.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 3:58:36 PM 
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Sep 28, 2012      hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.iwebservice;

import java.text.ParseException;

/**
 * @author hungdt
 * 
 */
public class ProvisioningRequest extends VasmanagerRequest {
	public static final int CMD_ID_NONE = 0;
	public static final int CMD_ID_ACTIVATE = 1;
	public static final int CMD_ID_SUSPEND = 2;
	public static final int CMD_ID_DEACTIVATE = 3;
	private String user;
	private String pass;
	private String nVasID = "";
	private int nCmdID = 0;
	private String sDescription = "";

	public ProvisioningRequest() {
		this.nCmdID = 0;
		this.nVasID = "";
		this.sDescription = "";
	}

	public String getNVasID() {
		return this.nVasID;
	}

	public void setNVasID(String vasID) {
		this.nVasID = vasID;
	}

	public int getNCmdID() {
		return this.nCmdID;
	}

	public void setNCmdID(int cmdID) {
		this.nCmdID = cmdID;
	}

	public String getSDescription() {
		return this.sDescription;
	}

	public void setSDescription(String description) {
		this.sDescription = description;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String toOrderString()
	{
		StringBuffer result = new StringBuffer();

		result.append(" | nVASID = ");
		result.append(getNVasID());
		result.append(" | nCmdID = ");
		result.append(getNCmdID());
		result.append(" | isdn = ");
		result.append(getMdn());
		result.append(" | user = ");
		result.append(getUser());
		result.append(" | pass = ");
		result.append(getPass());
		result.append(" | description = ");
		result.append(getSDescription());
		
		return result.toString();
	}
}
