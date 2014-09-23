/**
 * 
 */
package com.crm.cgw.ftp;

import java.util.Date;

/**
 * @author hungdt
 *
 */
public class CdrOutput {
	/**
	 * =============================================
	 * |Content Code	|	Description				|
	   |100				|	Mobile Originated Voice	|
	   |102				|	Mobile Originated SMS	|
	   |103				|	Mobile Terminated SMS	|
	   |104				|	Call Forward/Divert		|
	   |120				|	MMS Originated			|
	   |121				|	MMS Terminated			|
	   |122				|	Data					|
	   |125				|	MMS Forwarded			|
	   =============================================
	 */
	private long		seq_No			= 0;
	private String	a_Party			= "";
	private String	b_Party			= "";
	private Date	date			= null;
	private String	time			= "";
	private String	description		= "";
	private long		cont_Prov_Id	= 0;
	private String	cont_Prov_Name	= "";
	private int		cont_Code		= 0;
	private int		cont_Type		= 0;
	private String	currentcy		= "";
	private String	amount			= "";

	public long getSeq_No() {
		return seq_No;
	}

	public void setSeq_No(long seq_No) {
		this.seq_No = seq_No;
	}

	public String getA_Party() {
		return a_Party;
	}

	public void setA_Party(String a_Party) {
		this.a_Party = a_Party;
	}

	public String getB_Party() {
		return b_Party;
	}

	public void setB_Party(String b_Party) {
		this.b_Party = b_Party;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCont_Prov_Id() {
		return cont_Prov_Id;
	}

	public void setCont_Prov_Id(long cont_Prov_Id) {
		this.cont_Prov_Id = cont_Prov_Id;
	}

	public String getCont_Prov_Name() {
		return cont_Prov_Name;
	}

	public void setCont_Prov_Name(String cont_Prov_Name) {
		this.cont_Prov_Name = cont_Prov_Name;
	}

	public int getCont_Code() {
		return cont_Code;
	}

	public void setCont_Code(int cont_Code) {
		this.cont_Code = cont_Code;
	}

	public int getCont_Type() {
		return cont_Type;
	}

	public void setCont_Type(int cont_Type) {
		this.cont_Type = cont_Type;
	}

	public String getCurrentcy() {
		return currentcy;
	}

	public void setCurrentcy(String currentcy) {
		this.currentcy = currentcy;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
