/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : Constants.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Oct 2, 2012 2:17:35 PM 
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Oct 2, 2012      hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.iwebservice;

/**
 * @author hungdt
 * 
 */
public class ErrorCode {

	public static final String E_INVALID_PARAMETER = "ERR90001";
	public static final String E_INVALID_VAS_ID = "ERR90002";
	public static final String E_INVALID_PRODUCT_ID = "ERR90003";
	public static final String E_INVALID_KEYWORD = "ERR90004";
	public static final String E_INVALID_COMMAND_ID = "ERR90005";
	public static final String E_MISSING_IPC_NODE = "ERR90006";
	public static final String E_OK = "ERR00000"; // Thanh cong
	public static final String E_UNKNOWN = "ERR90007";// : Lenh khong ho tro
	public static final String E_FAILSE = "ERR00001";// : That bai
	public static final String E_NOTCONNECT = "ERR90008";// Mat ket noi toi CCS
	public static final String E_OVERLOADED = "ERR90009";// : Qua tai
	public static final String E_TIMEOUT = "ERR90010";// : Timeout
	public static final String E_INVALID_USERPASS = "ERR90011";// Sai username, password
	public static final String E_INVALID_MSISDN = "ERR90012";//
	public static final String E_INVALID_MSISDN_INVITE = "ERR90013";//
	public static final String E_BUSY = "ERR90014"; // TPS vuot qua gioi han
	public static final String E_NOT_FOUND = "ERR90015"; // Merchant khong ton tai
	public static final String E_IP_REJECT = "ERR90016"; // IP khong duoc phep truy cap
	public static final String E_MAX_VOLUME = "ERR90017"; // Qua so giao dich quy dinh cho phep
	

	
	
}
