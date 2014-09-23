/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crm.kernel.message.Constants;
import com.crm.util.AppProperties;

/**
 * @author hungdt
 *
 */
public class ChangeBalance extends Charging {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				balance				= "";

	private String				expireDate			= "";
	
	private String				amount				= "";
	
	private List<String> 		lstBalance 	 = new ArrayList<String>();
	
	private AppProperties		parameters			= new AppProperties();

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	
	public List<String> getLstBalance() {
		return lstBalance;
	}

	public void setLstBalance(List<String> lstBalance) {
		this.lstBalance = lstBalance;
	}
	
	public AppProperties getParameters()
	{
		return parameters;
	}

	public void setParameters(AppProperties parameters)
	{
		this.parameters = parameters;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setContent(String strContent) throws Exception {
		super.setContent(strContent);
		String[] content = strContent.split(SEPARATE_CHAR);
		try {
			setId(Integer.parseInt(content[0].trim()));
			setAccount(content[1].trim().split("=")[1]);
			setMdn(content[2].trim().split("=")[1]);
			setCharggw_type(TYPE_CHANGE_BALANCE);
			List<String> _temp = new ArrayList<String>();
			String _balance = "";
			String _amount = "";
			String _expiredate = "";
			for(int i=3; i< content.length-1; i = i+2)
			{
				String _nameBalance = content[i].split("=")[0].substring(8);
				String _tempAmout = content[i].split("=")[1];
				String _tempExpiredate = content[i+1].split("=")[1];
				_balance += "," + _nameBalance;
				_amount += "," +_tempAmout;
				_expiredate += "," + _tempExpiredate;
				_temp.add(content[i] +","+ content[i+1]);
			}
			
			setBalance(_balance.trim().substring(1));
			setAmount(_amount.trim().substring(1));
			setExpireDate(_expiredate.trim().substring(1));
			setLstBalance(_temp);
			setComment(content[content.length-1].trim().split("=")[1]);
		}
		catch (Exception e) {
			throw e;
		}
	}
	
}
