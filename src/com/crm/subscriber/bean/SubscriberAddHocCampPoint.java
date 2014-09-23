package com.crm.subscriber.bean;

import java.io.Serializable;

import com.crm.kernel.message.Constants;

public class SubscriberAddHocCampPoint implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long 		addHocCampID  		= Constants.DEFAULT_ID;
	private String		isdn		 		= "";
	private int		addHocCampPoint			= 0;
	private	int		status				= 0;
	
	public long getAddHocCampID() {
		return addHocCampID;
	}
	public void setAddHocCampID(long addHocCampID) {
		this.addHocCampID = addHocCampID;
	}
	public String getIsdn() {
		return isdn;
	}
	public void setIsdn(String isdn) {
		this.isdn = isdn;
	}
	public int getAddHocCampPoint() {
		return addHocCampPoint;
	}
	public void setAddHocCampPoint(int addHocCampPoint) {
		this.addHocCampPoint = addHocCampPoint;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
