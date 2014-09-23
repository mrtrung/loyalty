/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : CheckVasStatusReq.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 3:54:23 PM 
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
import java.text.SimpleDateFormat;

/**
 * @author hungdt
 * 
 */
public class CheckVasStatusRequest extends VasmanagerRequest {
	private int nVasID = 0;
	private String user;
	private String pass;

	public int getNVasID() {
		return this.nVasID;
	}

	public void setNVasID(int vasID) {
		this.nVasID = vasID;
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
		result.append(" | isdn = ");
		result.append(getMdn());
		result.append(" | userMame = ");
		result.append(getUser());
		result.append(" | password = ");
		result.append(getPass());
		
		return result.toString();
	}
}
