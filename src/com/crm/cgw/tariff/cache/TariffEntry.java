/**
 * 
 */
package com.crm.cgw.tariff.cache;

import java.util.Date;

import com.crm.kernel.index.IndexNode;

/**
 * @author hungdt
 * 
 */
public class TariffEntry extends IndexNode {
	private long	tariff_id		= 0;
	private String	a_number		= "";
	private String	b_number		= "";
	private int		amount			= 0;
	private boolean	is_Use			= false;
	private int		valid_Period	= 0;
	private String	currency		= "";
	private Date	update_Time		= null;
	private String	description		= "";
	private int		cont_Code		= 0;
	private int		cont_Type		= 0;
	private int		charge_Act		= 0;

	public TariffEntry(long tariff_id, String a_number) {
		super(a_number);
		setTariff_id(tariff_id);
	}

	public long getTariff_id() {
		return tariff_id;
	}

	public void setTariff_id(long tariff_id) {
		this.tariff_id = tariff_id;
	}

	public String getA_number() {
		return a_number;
	}

	public void setA_number(String a_number) {
		this.a_number = a_number;
	}

	public String getB_number() {
		return b_number;
	}

	public void setB_number(String b_number) {
		this.b_number = b_number;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isIs_Use() {
		return is_Use;
	}

	public void setIs_Use(boolean is_Use) {
		this.is_Use = is_Use;
	}

	public int getValid_Period() {
		return valid_Period;
	}

	public void setValid_Period(int valid_Period) {
		this.valid_Period = valid_Period;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getUpdate_Time() {
		return update_Time;
	}

	public void setUpdate_Time(Date update_Time) {
		this.update_Time = update_Time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getCharge_Act() {
		return charge_Act;
	}

	public void setCharge_Act(int charge_Act) {
		this.charge_Act = charge_Act;
	}

}
