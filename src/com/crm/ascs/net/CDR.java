package com.crm.ascs.net;


public class CDR
{
	public static CDR createCDRFromFileString(String cdrContent) throws Exception
	{
		if (cdrContent == null) { return null; }

		String[] contentElements = cdrContent.split("\\|");

		CDR cdr = new CDR();
		
		try
		{
			cdr.setStreamNo(contentElements[0]);
			cdr.setTimeStamp(contentElements[2]);
			cdr.setChargeResult(contentElements[2]);
			cdr.setMsIsdn(contentElements[3]);
			cdr.setSpID(contentElements[4]);
			cdr.setServiceID(contentElements[5]);
			cdr.setProductID_telco(contentElements[6]);
			cdr.setChargeMode(contentElements[7]);
			cdr.setBeginTime(contentElements[8]);
			cdr.setEndTime(contentElements[9]);
			cdr.setPayType(contentElements[11]);
			cdr.setCost(contentElements[17]);
			cdr.setB_Isdn(contentElements[25]);
		} catch(Exception ep)
		{
			ep.getStackTrace();
		}
		return cdr;
	}

	public String getStreamNo()
	{
		return streamNo;
	}

	public void setStreamNo(String streamNo)
	{
		this.streamNo = streamNo;
	}

	public String getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public String getChargeResult()
	{
		return chargeResult;
	}

	public void setChargeResult(String chargeResult)
	{
		this.chargeResult = chargeResult;
	}

	public String getMsIsdn()
	{
		return msIsdn;
	}

	public void setMsIsdn(String msIsdn)
	{
		this.msIsdn = msIsdn;
	}

	public String getSpID()
	{
		return spID;
	}

	public void setSpID(String spID)
	{
		this.spID = spID;
	}

	public String getServiceID()
	{
		return serviceID;
	}

	public void setServiceID(String serviceID)
	{
		this.serviceID = serviceID;
	}

	public String getProductID_telco()
	{
		return productID_telco;
	}

	public void setProductID_telco(String productID_telco)
	{
		this.productID_telco = productID_telco;
	}

	public String getChargeMode()
	{
		return chargeMode;
	}

	public void setChargeMode(String chargeMode)
	{
		this.chargeMode = chargeMode;
	}

	public String getBeginTime()
	{
		return beginTime;
	}

	public void setBeginTime(String beginTime)
	{
		this.beginTime = beginTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public String getPayType()
	{
		return payType;
	}

	public void setPayType(String payType)
	{
		this.payType = payType;
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

	public String getVolume()
	{
		return volume;
	}

	public void setVolume(String volume)
	{
		this.volume = volume;
	}

	public String getCost()
	{
		return cost;
	}

	public void setCost(String cost)
	{
		this.cost = cost;
	}

	public String getB_Isdn()
	{
		return b_Isdn;
	}

	public void setB_Isdn(String b_Isdn)
	{
		this.b_Isdn = b_Isdn;
	}

	public long getCDR_ID()
	{
		return CDR_ID;
	}

	public void setCDR_ID(long cDR_ID)
	{
		CDR_ID = cDR_ID;
	}

	private long CDR_ID = 0l;
	private String streamNo = "";
	private String timeStamp = "";
	private String chargeResult = "";
	private String msIsdn = "";
	private String spID = "";
	private String serviceID = "";
	private String productID_telco = "";
	private String chargeMode = "";
	private String beginTime = "";
	private String endTime = "";
	private String payType = "";
	private String duration = "";
	private String volume = "";
	private String cost = "";
	private String b_Isdn = "";
}
