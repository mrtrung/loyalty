package com.crm.ascs.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.crm.kernel.message.Constants;

public abstract class Trigger implements INetData
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public static final String	TYPE_RECHARGE		= "Recharge";
	public static final String	TYPE_ACTIVATION		= "Activation";
	public static final String	SEPARATE_CHAR		= ",";
	public static final String	DATE_FORMAT			= "dd/MM/yy";
	public static final String	FILE_DATE_FORMAT	= "yyyyMMddHHmmss";
	public static final int		STATUS_FAILURE		= 2;
	public static final int		STATUS_PENDING		= 1;
	public static final int		STATUS_APPROVED		= 0;

	private long				triggerId			= Constants.DEFAULT_ID;
	private String				isdn				= "";
	private String				cosName				= "";
	private String				previousState		= "";
	private String				state				= "";
	private double				coreBalance			= 0;
	private Date				activationDate		= null;
	private Date				expireDate			= null;
	private Date				receiveDate			= new Date();

	private long				timeout				= 0;

	private int					status				= STATUS_PENDING;
	private String				description			= "";

	private int					retryCount			= 0;

	public void setTriggerId(long triggerId)
	{
		this.triggerId = triggerId;
	}

	public long getTriggerId()
	{
		return triggerId;
	}

	public String getType()
	{
		return "";
	}

	public String getIsdn()
	{
		return isdn;
	}

	public void setIsdn(String isdn)
	{
		this.isdn = isdn;
	}

	public String getCosName()
	{
		return cosName;
	}

	public void setCosName(String cosName)
	{
		this.cosName = cosName;
	}

	public String getPreviousState()
	{
		return previousState;
	}

	public void setPreviousState(String previousState)
	{
		this.previousState = previousState;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public double getCoreBalance()
	{
		return coreBalance;
	}

	public void setCoreBalance(double coreBalance)
	{
		this.coreBalance = coreBalance;
	}

	public Date getActivationDate()
	{
		return activationDate;
	}

	public void setActivationDate(Date activationDate)
	{
		this.activationDate = activationDate;
	}

	public Date getExpireDate()
	{
		return expireDate;
	}

	public void setExpireDate(Date expireDate)
	{
		this.expireDate = expireDate;
	}

	public void setReceiveDate(Date receiveDate)
	{
		this.receiveDate = receiveDate;
	}

	public Date getReceiveDate()
	{
		return receiveDate;
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
	public byte[] getData()
	{
		return getContent().getBytes();
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public long getTimeout()
	{
		return timeout;
	}

	/**
	 * Remain time to live of trigger = ReceiveDate + timeout - Current
	 * (ReceiveDate != null && timeout > 0) or return 0
	 * 
	 * @return
	 */
	public long getTimeToLive()
	{
		if (receiveDate != null && timeout > 0)
		{
			return receiveDate.getTime() + timeout - (new Date()).getTime();
		}

		return 0;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public int getStatus()
	{
		return status;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public void setRetryCount(int retryCount)
	{
		this.retryCount = retryCount;
	}

	public int getRetryCount()
	{
		return retryCount;
	}
	
	public static Date dateFromString(String date, String pattern) throws ParseException
	{
		if ("".equals(date) || date == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(date);
	}

	public static Date dateFromString(String date) throws ParseException
	{
		if ("".equals(date) || date == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.parse(date);
	}	

	public static Date dateFromFileString(String date) throws ParseException
	{
		if ("".equals(date) || date == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(FILE_DATE_FORMAT);
		return sdf.parse(date);
	}
	
	public static String stringFromDate(Date date, String pattern)
	{
		if (date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String stringFromDate(Date date)
	{
		if (date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}

	public static String fileStringFromDate(Date date)
	{
		if (date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(FILE_DATE_FORMAT);
		return sdf.format(date);
	}

	public static Trigger createTrigger(String content) throws Exception
	{
		Trigger trigger = null;
		
		int startIndex = content.indexOf(TYPE_ACTIVATION);
		if (startIndex < 0)
		{
			startIndex = content.indexOf(TYPE_RECHARGE);
			if (startIndex >= 0)
				trigger = new TriggerRecharge();
		}
		else
		{
			trigger = new TriggerActivation();
		}
		
		if (trigger == null)
			return null;

		if (startIndex > 0)
			content = content.substring(startIndex);
		
		trigger.setReceiveDate(new Date());
		trigger.setContent(content);
		return trigger;
	}

	public static Trigger getFromMQMessage(Message message) throws Exception
	{
		if (message instanceof ObjectMessage)
		{
			Object content = ((ObjectMessage) message).getObject();
			if (content instanceof Trigger)
				return (Trigger) content;
			else
				return null;
		}
		else
		{
			return null;
		}
	}

	public static String toFileLogString(Trigger trigger)
	{
		if (trigger == null)
			return "";
		/**
		 * <code>
		 * Write trigger to file:
		 * RechargeTrigger: triggerContent:receiveDate:rechargeDate:activationDate:previousState:status
		 * ActivationTrigger: triggerContent:receiveDate:activationDate:status
		 * </code>
		 */

		String triggerContent = trigger.getContent();
		if (trigger.getType().equals(Trigger.TYPE_RECHARGE))
		{
			TriggerRecharge recharge = (TriggerRecharge) trigger;
			triggerContent = triggerContent
							+ ":" + Trigger.fileStringFromDate(recharge.getReceiveDate())
							+ ":"
							+ Trigger.fileStringFromDate(recharge.getRechargeDate())
							+ ":"
							+ Trigger.fileStringFromDate(recharge.getActivationDate())
							+ ":" + recharge.getPreviousState()
							+ ":" + recharge.getStatus();
		}
		else if (trigger.getType().equals(Trigger.TYPE_ACTIVATION))
		{
			TriggerActivation activation = (TriggerActivation) trigger;
			triggerContent = triggerContent
							+ ":" + Trigger.fileStringFromDate(activation.getReceiveDate())
							+ ":"
							+ Trigger.fileStringFromDate(activation.getActivationDate())
							+ ":" + activation.getStatus();
		}
		return triggerContent;
	}

	public static Trigger createTriggerFromFileString(String triggerContent) throws Exception
	{
		if (triggerContent == null)
			return null;

		String[] contentElements = triggerContent.split(":");

		if (contentElements[0].equals(""))
			return null;

		Trigger trigger = createTrigger(contentElements[0]);

		if (trigger == null)
			return null;

		try
		{
			trigger.setReceiveDate(dateFromFileString(contentElements[1]));

			if (trigger.getType().equals(Trigger.TYPE_RECHARGE))
			{
				((TriggerRecharge) trigger).setRechargeDate(dateFromFileString(contentElements[2]));
				trigger.setActivationDate(dateFromFileString(contentElements[3]));
				((TriggerRecharge) trigger).setPreviousState(contentElements[4]);
				trigger.setStatus(Integer.parseInt(contentElements[5]));
			}
			else if (trigger.getType().equals(Trigger.TYPE_ACTIVATION))
			{
				trigger.setActivationDate(dateFromFileString(contentElements[2]));
				trigger.setStatus(Integer.parseInt(contentElements[3]));
			}
		}
		catch (Exception e)
		{
			throw e;
		}

		return trigger;
	}

	public String toLogString()
	{
		return "id = " + getTriggerId() + " | " + "type = " + getType() + " | " + " isdn = " + getIsdn() + " | " + " cosName = "
				+ getCosName()
				+ " | " + " state = " + getState() + " | " + "coreBalance = " + getCoreBalance() + " expireDate = "
				+ stringFromDate(getExpireDate()) + " | " + " Description = " + getDescription();
	}
}
