/**
 * 
 */
package com.crm.cgw.extdebit;

import com.crm.cgw.net.INetData;
import com.crm.cgw.submodifytcp.Charging;

/**
 * @author hungdt
 * 
 */
public class ExtDebit implements INetData {
	String	sTransId		= "";
	String	TransDateTime	= "";	// La thoi gian gui ban tin
	String	ANumber			= "";	// So dich vu: 19001570
	String	BNumber			= "";	// So thue bao gui tin nhan: for
									// example: 8492352722
	String	SubmitTime		= "";	// Thoi gian thue bao guitin, khuong
									// dang: yyyyMMddHHmmss
	String	ServiceState	= "";	// Trang thai cua ban tin: D ==> Ban tin
	// thanh cong, U ==> Ban tin khong thanh
	// cong
	String	SentTime		= "";	// Thoi gian he thong dich vu nhan duoc
	// tin
	String	CPId			= "";	// Content Provider ID
	String	CPName			= "";	// Content Provider Name
	String	ContCode		= "";
	String	ContType		= "";
	String	Desc			= "";	// Description
	 String				extDebit_seq			= "";
	
	private String		sessionId = "";
	public static final String	SEPARATE_CHAR		= "&";
	public static final String	DATE_FORMAT			= "dd/MM/yy HH:mm:ss";
	public String getsTransId() {
		return sTransId;
	}

	public void setsTransId(String sTransId) {
		this.sTransId = sTransId;
	}

	public String getTransDateTime() {
		return TransDateTime;
	}

	public void setTransDateTime(String transDateTime) {
		TransDateTime = transDateTime;
	}

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

	public String getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(String submitTime) {
		SubmitTime = submitTime;
	}

	public String getServiceState() {
		return ServiceState;
	}

	public void setServiceState(String serviceState) {
		ServiceState = serviceState;
	}

	public String getSentTime() {
		return SentTime;
	}

	public void setSentTime(String sentTime) {
		SentTime = sentTime;
	}

	public String getCPId() {
		return CPId;
	}

	public void setCPId(String cPId) {
		CPId = cPId;
	}

	public String getCPName() {
		return CPName;
	}

	public void setCPName(String cPName) {
		CPName = cPName;
	}

	public String getContCode() {
		return ContCode;
	}

	public void setContCode(String contCode) {
		ContCode = contCode;
	}

	public String getContType() {
		return ContType;
	}

	public void setContType(String contType) {
		ContType = contType;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}
	

	public String getExtDebit_seq() {
		return extDebit_seq;
	}

	public void setExtDebit_seq(String extDebit_seq) {
		this.extDebit_seq = extDebit_seq;
	}
	
	

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public byte[] getData() {
		return getContent().getBytes();
	}

	@Override
	public String getContent() {
		return toString();
	}
	
	public void setContent(String strContent) throws Exception {
		
	}
	
	public static ExtDebit setContentReq(String strContent, String handlerId) throws Exception {
		ExtDebit ext = new ExtDebit();
		String[] content = strContent.split(SEPARATE_CHAR);
		ext.setExtDebit_seq(handlerId);
		ext.setSessionId(content[0]);
		ext.setsTransId(content[1]);
		ext.setTransDateTime(content[2]);
		ext.setCPId(content[3]);
		ext.setCPName(content[4]);
		ext.setANumber(content[5]);
		ext.setBNumber(content[6]);
		ext.setSubmitTime(content[7]);
		ext.setServiceState(content[8]);
		ext.setSentTime(content[9]);
		ext.setContCode(content[10]);
		ext.setContType(content[11]);
		ext.setDesc(content[12]);
		
		return ext;
	}
	
	

}
