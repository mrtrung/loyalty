/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : ActivationProduct.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Oct 2, 2012 3:05:57 PM 
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Oct 2, 2012      hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.fss.util.StringUtil;

/**
 * @author hungdt
 *
 */
public class ActivationLog {
	public long DEFAUL_ID = 0L;

	  private String _sender = "";
	  private String _username = "";
	  private long _userId = 0L;

	  private long _request_date = System.currentTimeMillis();
	  private long _response_date = System.currentTimeMillis();
	  private String _provisioning_type = "";
	  private String _provisioning_id = "";
	  private String _product_id = "";
	  private String _promotion_id = "";
	  private String _root_command_id = "";
	  private String _command_id = "";
	  private String _channel = "SMS";
	  private String _service_address = "";
	  private String _source_address = "";
	  private String _dest_address = "";
	  private String _keyword = "";

	  private long _request_id = 0L;

	  private Properties _parameters = new Properties();
	  private String _command_request = "";
	  private String _command_response = "";
	  private String _response_code = "";

	  private boolean _isDone = false;
	  private long _process_time = 0L;

	  public ActivationLog clone()
	  {
	    ActivationLog result = new ActivationLog();

	    result.setRequestId(this._request_id);
	    result.setSender(this._sender);
	    result.setUserId(this._userId);
	    result.setUsername(this._username);
	    result.setRequestDate(this._request_date);
	    result.setResponseDate(this._response_date);
	    result.setProvisioningType(this._provisioning_type);
	    result.setProvisioningId(this._provisioning_id);
	    result.setProductId(this._product_id);
	    result.setPromotionId(this._promotion_id);
	    result.setRootCommandId(this._command_id);
	    result.setCommandId(this._command_id);
	    result.setChannel(this._channel);
	    result.setServiceAddress(this._service_address);
	    result.setSourceAddress(this._source_address);
	    result.setDestinationAddress(this._dest_address);

	    result.setParameters((Properties)this._parameters.clone());

	    result.setProcessTime(0L);

	    return result;
	  }

	  public synchronized void sign()
	  {
	    this._isDone = true;

	    notifyAll();
	  }

	  public synchronized void waitSign(long timeout) throws Exception
	  {
	    try
	    {
	      wait(timeout);
	    }
	    catch (InterruptedException e)
	    {
	      throw new TimeoutException("");
	    }

	    if (!this._isDone)
	    {
	      throw new TimeoutException("Transaction timeout");
	    }
	  }

	  public String toString()
	  {
	    String result = "sender: " + this._sender + " | " + 
	      "userName: " + this._username + " | " + 
	      "requestDate: " + this._request_date + " | " + 
	      "responseDate: " + this._response_date + " | " + 
	      "provisioningType: " + this._provisioning_type + " | " + 
	      "provisioningId: " + this._provisioning_id + " | " + 
	      "productId: " + this._product_id + " | " + 
	      "promotionId: " + this._promotion_id + " | " + 
	      "rootCommandId: " + this._root_command_id + " | " + 
	      "commandId: " + this._command_id + " | " + 
	      "channel: " + this._channel + " | " + 
	      "serviceAddress: " + this._service_address + " | " + 
	      "sourceAddress: " + this._source_address + " | " + 
	      "keyword: " + this._keyword + " | " + 
	      "parameters: " + this._parameters.toString() + " | " + 
	      "request: " + this._command_request + " | " + 
	      "response: " + this._command_response + " | " + 
	      "responseCode: " + this._response_code;

	    return result;
	  }

	  public String toShortString()
	  {
	    String result = "user: " + this._username + " | " + 
	      "requestDate: " + this._request_date + " | " + 
	      "productId: " + this._product_id + " | " + 
	      "commandId: " + this._command_id + " | " + 
	      "channel: " + this._channel + " | " + 
	      "sourceAddress: " + this._source_address + " | " + 
	      "parameters: " + this._parameters.toString() + " | " + 
	      "request: " + this._command_request + " | " + 
	      "response: " + this._command_response + " | " + 
	      "responseCode: " + this._response_code;

	    return result;
	  }

	  public void setProvisioningType(String _provisioning_type)
	  {
	    this._provisioning_type = _provisioning_type;
	  }

	  public String getProvisioningType()
	  {
	    return this._provisioning_type;
	  }

	  public void setProvisioningId(String _provisioning_id)
	  {
	    this._provisioning_id = _provisioning_id;
	  }

	  public String getProvisioningId()
	  {
	    return this._provisioning_id;
	  }

	  public void setCommandId(String _command_id)
	  {
	    this._command_id = _command_id;
	  }

	  public String getCommandId()
	  {
	    return this._command_id;
	  }

	  public void setParameters(Properties _parameters)
	  {
	    this._parameters = _parameters;
	  }

	  public Properties getParameters()
	  {
	    return this._parameters;
	  }

	  public void setParameter(String name, String value)
	  {
	    getParameters().setProperty(name, value);
	  }

	  public String getParameter(String name, String nullValue)
	  {
	    try
	    {
	      Properties parameters = getParameters();

	      return StringUtil.nvl(parameters.getProperty(name), nullValue);
	    }
	    catch (Exception e) {
	    }
	    return nullValue;
	  }

	  public long getSubProductId()
	  {
	    String subProductId = getParameter("subProductId", "-1");

	    return Long.valueOf(subProductId).longValue();
	  }

	  public boolean isRegistered()
	  {
	    return getSubProductId() != -1L;
	  }

	  public Date getRegisterDate()
	  {
	    long registerDate = Long.valueOf(getParameter("registerDate", "0")).longValue();

	    if (registerDate == 0L)
	    {
	      registerDate = System.currentTimeMillis();
	    }

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(registerDate);

	    return calendar.getTime();
	  }

	  public Date getGraceDate()
	  {
	    long graceDate = Long.valueOf(getParameter("graceDate", "0")).longValue();

	    if (graceDate == 0L)
	    {
	      graceDate = System.currentTimeMillis();
	    }

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(graceDate);

	    return calendar.getTime();
	  }

	  public Date getExpirationDate()
	  {
	    long expirationDate = Long.valueOf(getParameter("expirationDate", "0")).longValue();

	    if (expirationDate == 0L)
	    {
	      expirationDate = System.currentTimeMillis();
	    }

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(expirationDate);

	    return calendar.getTime();
	  }

	  public String getActionType()
	  {
	    return getParameter("actionType", "");
	  }

	  public void setActionType(String actionType)
	  {
	    getParameters().setProperty("actionType", actionType);
	  }

	  public int getOrderStatus()
	  {
	    return Integer.valueOf(getParameter("orderStatus", "-1")).intValue();
	  }

	  public void setOrderStatus(int status)
	  {
	    getParameters().setProperty("orderStatus", String.valueOf(status));
	  }

	  public void setCommandResponse(String _command_response)
	  {
	    this._command_response = _command_response;
	  }

	  public String getCommandResponse()
	  {
	    return this._command_response;
	  }

	  public void setProductId(String _product_id)
	  {
	    this._product_id = _product_id;
	  }

	  public String getProductId()
	  {
	    return this._product_id;
	  }

	  public void setSourceAddress(String _source_address)
	  {
	    this._source_address = _source_address;
	  }

	  public String getSourceAddress()
	  {
	    return this._source_address;
	  }

	  public void setResponseCode(String _response_code)
	  {
	    this._response_code = _response_code;
	  }

	  public String getResponseCode()
	  {
	    return this._response_code;
	  }

	  public void setRequestDate(long _request_date)
	  {
	    this._request_date = _request_date;
	  }

	  public long getRequestDate()
	  {
	    return this._request_date;
	  }

	  public void setResponseDate(long _response_date)
	  {
	    this._response_date = _response_date;
	  }

	  public long getResponseDate()
	  {
	    return this._response_date;
	  }

	  public void setRequestId(long _request_id)
	  {
	    this._request_id = _request_id;
	  }

	  public long getRequestId()
	  {
	    return this._request_id;
	  }

	  public void setProcessTime(long _process_time)
	  {
	    this._process_time = _process_time;
	  }

	  public long getProcessTime()
	  {
	    return this._process_time;
	  }

	  public void setChannel(String _channel)
	  {
	    this._channel = _channel;
	  }

	  public String getChannel()
	  {
	    return this._channel;
	  }

	  public void setServiceAddress(String _service_number)
	  {
	    this._service_address = _service_number;
	  }

	  public String getServiceAddress()
	  {
	    return this._service_address;
	  }

	  public void setDestinationAddress(String _receiver)
	  {
	    this._dest_address = _receiver;
	  }

	  public String getDestinationAddress()
	  {
	    return this._dest_address;
	  }

	  public void setRootCommandId(String _root_command_id)
	  {
	    this._root_command_id = _root_command_id;
	  }

	  public String getRootCommandId()
	  {
	    return this._root_command_id;
	  }

	  public void setKeyword(String _keyword)
	  {
	    this._keyword = _keyword;
	  }

	  public String getKeyword()
	  {
	    return this._keyword;
	  }

	  public void setCommandRequest(String _command_request)
	  {
	    this._command_request = _command_request;
	  }

	  public String getCommandRequest()
	  {
	    return this._command_request;
	  }

	  public void setSender(String _sender)
	  {
	    this._sender = _sender;
	  }

	  public String getSender()
	  {
	    return this._sender;
	  }

	  public void setUsername(String _username)
	  {
	    this._username = _username;
	  }

	  public String getUsername()
	  {
	    return this._username;
	  }

	  public void setUserId(long _userId)
	  {
	    this._userId = _userId;
	  }

	  public long getUserId()
	  {
	    return this._userId;
	  }

	  public void setPromotionId(String _promotion_id)
	  {
	    this._promotion_id = _promotion_id;
	  }

	  public String getPromotionId()
	  {
	    return this._promotion_id;
	  }
}
