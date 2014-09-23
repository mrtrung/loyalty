/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.crm.cgw.net.INetData;

/**
 * @author hungdt
 * 
 */
public class Charging implements INetData {
	private static final long	serialVersionUID	= 1L;
	private long				id					= 0;
	private String				account				= "";
	private String				mdn					= "";
	private String				comment				= "";
	private String				charggw_type		= "";
	private long				timeout				= 0;
	private long				charg_seq			= 0;
	private double				balanceAvaible		= 0;

	private Date				receiveDate			= new Date();

	public static final String	SEPARATE_CHAR		= ",";
	public static final String	DATE_FORMAT			= "dd/MM/yy HH:mm:ss";
	public static final String	TYPE_CHANGE_BALANCE	= "BALANCE";
	public static final String	TYPE_CHANGE_STATE	= "STATE";

	public static final String	accoutStartwith		= "ACCOUNT";

	public static Date dateFromString(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.parse(date);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCharggw_type() {
		return charggw_type;
	}

	public void setCharggw_type(String charggw_type) {
		this.charggw_type = charggw_type;
	}

	public long getCharg_seq() {
		return charg_seq;
	}

	public void setCharg_seq(long charg_seq) {
		this.charg_seq = charg_seq;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public double getBalanceAvaible() {
		return balanceAvaible;
	}

	public void setBalanceAvaible(double balanceAvaible) {
		this.balanceAvaible = balanceAvaible;
	}

	@Override
	public String getContent() {
		return toString();
	}

	@Override
	public byte[] getData() {
		return getContent().getBytes();
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public long getTimeToLive() {
		if (receiveDate != null && timeout > 0) { return receiveDate.getTime()
				+ timeout - (new Date()).getTime(); }

		return 0;
	}

	public String toLogString() {
		// return "SessionId = " + getSessionId() + " | " +
		// " TransactionId = "
		// + getTransactionId() + " | " + " TransactionDate = "
		// + getTransactionDate() + " | " + " Msg = " + getMsg()
		// + " | " + " ANumber = " + getaNumber() + " | "
		// + " BNumber = " + getbNumber() + " | " + "CpId = "
		// + getCpId() + " userName = " + getUsername() + " | "
		// + " Description = " + getDescription();ds
		return "";
	}

	public void setContent(String strContent) throws Exception {
	}

	public static Charging createCharging(String strContent, long handlerId) throws Exception {

		Charging charging = null;
		if (strContent.indexOf(TYPE_CHANGE_BALANCE) > 0) {
			charging = new ChangeBalance();
		}
		else if (strContent.indexOf(TYPE_CHANGE_STATE) > 0) {
			charging = new ChangeState();
		}
		charging.setContent(strContent);
		charging.setCharg_seq(handlerId);
		return charging;
	}

	public static Charging getFromMQMessage(Message message) throws Exception {
		if (message instanceof ObjectMessage) {
			Object content = ((ObjectMessage) message).getObject();
			if (content instanceof Charging)
				return (Charging) content;
			else
				return null;
		}
		else {
			return null;
		}
	}
}
