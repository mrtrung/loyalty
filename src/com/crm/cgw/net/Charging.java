/**
 * 
 */
package com.crm.cgw.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hungdt
 * 
 */
public abstract class Charging implements INetData
{
	private static final long	serialVersionUID	= 1L;
	private long				sessionId			= 0;
	private String				msg				= "";
	private String				username			= "";
	private String				password			= "";
	private long				transactionId		= 0;
	private Date				transactionDate	= null;
	private long				cpId				= 0;
	private String				cpName			= "";
	private String				aNumber			= "";
	private String				bNumber			= "";
	private Date				submitTime		= null;
	private Date				sendTime			= null;
	private String				serviceState		= "";
	private String				contentCode		= "";
	private String				contentType		= "";
	private String				description		= "";
	private String				commandCode		= "";
	private String				remoteHost		= "0.0.0.0";
	private int				remotePort		= 0;

	public static final String	SEPARATE_CHAR		= ",";
	public static final String	DATE_FORMAT		= "dd/MM/yy";

	public long getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(long sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public long getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(long transactionId)
	{
		this.transactionId = transactionId;
	}

	public Date getTransactionDate()
	{
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate)
	{
		this.transactionDate = transactionDate;
	}

	public long getCpId()
	{
		return cpId;
	}

	public void setCpId(long cpId)
	{
		this.cpId = cpId;
	}

	public String getCpName()
	{
		return cpName;
	}

	public void setCpName(String cpName)
	{
		this.cpName = cpName;
	}

	public String getaNumber()
	{
		return aNumber;
	}

	public void setaNumber(String aNumber)
	{
		this.aNumber = aNumber;
	}

	public String getbNumber()
	{
		return bNumber;
	}

	public void setbNumber(String bNumber)
	{
		this.bNumber = bNumber;
	}

	public Date getSubmitTime()
	{
		return submitTime;
	}

	public void setSubmitTime(Date submitTime)
	{
		this.submitTime = submitTime;
	}

	public Date getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(Date sendTime)
	{
		this.sendTime = sendTime;
	}

	public String getServiceState()
	{
		return serviceState;
	}

	public void setServiceState(String serviceState)
	{
		this.serviceState = serviceState;
	}

	public String getContentCode()
	{
		return contentCode;
	}

	public void setContentCode(String contentCode)
	{
		this.contentCode = contentCode;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCommandCode()
	{
		return commandCode;
	}

	public void setCommandCode(String commandCode)
	{
		this.commandCode = commandCode;
	}

	public void setContent(String triggerContent) throws Exception
	{
	}

	@Override
	public String getContent()
	{
		return toString();
	}

	@Override
	public byte [] getData()
	{
		return getContent().getBytes();
	}

//	@Override
//	public void setRemoteHost(String remoteHost)
//	{
//		this.remoteHost = remoteHost;
//	}
//
//	@Override
//	public String getRemoteHost()
//	{
//		return remoteHost;
//	}
//
//	@Override
//	public void setRemotePort(int remotePort)
//	{
//		this.remotePort = remotePort;
//	}
//
//	@Override
//	public int getRemotePort()
//	{
//		return remotePort;
//	}

	public static Date dateFromString(String date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.parse(date);
	}

	public String toLogString()
	{
		return "SessionId = " + getSessionId() + " | " + " TransactionId = "
				+ getTransactionId() + " | " + " TransactionDate = "
				+ getTransactionDate() + " | " + " Msg = " + getMsg()
				+ " | " + " ANumber = " + getaNumber() + " | "
				+ " BNumber = " + getbNumber() + " | " + "CpId = "
				+ getCpId() + " userName = " + getUsername() + " | "
				+ " Description = " + getDescription();
	}

}
