/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : VasmanService.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Sep 28, 2012 4:00:29 PM 
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Sep 28, 2012      hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.vasman;

import com.elcom.iwebservice.ProvisioningRequest;
import com.elcom.iwebservice.ProvisioningResponse;
import com.elcom.iwebservice.CheckAllVasStatusRequest;
import com.elcom.iwebservice.CheckAllVasStatusResponse;
import com.elcom.iwebservice.CheckVasStatusRequest;
import com.elcom.iwebservice.CheckVasStatusResponse;

/**
 * @author hungdt
 * 
 */
public abstract interface VasmanService {
	public abstract ProvisioningResponse Provisioning(ProvisioningRequest paramProvisioningReq);
	public abstract CheckVasStatusResponse CheckVasStatus(CheckVasStatusRequest paramCheckVasStatusReq);
	public abstract CheckAllVasStatusResponse CheckAllVasStatus(CheckAllVasStatusRequest paramCheckAllVasStatusReq);
}
