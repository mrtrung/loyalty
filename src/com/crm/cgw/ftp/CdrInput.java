/**
 * 
 */
package com.crm.cgw.ftp;

import java.util.Date;

/**
 * @author hungdt
 *
 */
public class CdrInput {
	private String	ANumber			= "";
	private String	BNumber			= "";
	private Date	fromDatetime	= null;
	private String	status			= "";
	private Date	toDatetime		= null;
	private int		cpId			= 0;
	private String	contentType		= "";
	private String	serviceName		= "";

	public String getANumber() {
		return ANumber;
	}

	public void setANumber(String aNumber) {
		ANumber = aNumber;
	}

	public String getBNumber() {
		return BNumber;
	}

	public void setBNumber(String bNumber) {
		BNumber = bNumber;
	}

	public Date getFromDatetime() {
		return fromDatetime;
	}

	public void setFromDatetime(Date fromDatetime) {
		this.fromDatetime = fromDatetime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getToDatetime() {
		return toDatetime;
	}

	public void setToDatetime(Date toDatetime) {
		this.toDatetime = toDatetime;
	}

	public int getCpId() {
		return cpId;
	}

	public void setCpId(int cpId) {
		this.cpId = cpId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
