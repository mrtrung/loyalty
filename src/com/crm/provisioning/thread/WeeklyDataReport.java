package com.crm.provisioning.thread;

public class WeeklyDataReport {

	String level = "";
	int allSubs = 0;
	int maxAmount = 0;
	int minAmount = 0;
	String action = "";
	long avg_aonet	= 0;
	long avg_cutover = 0;
	long avg_topup	= 0;
	long avg_usage	= 0;
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getAllSubs() {
		return allSubs;
	}
	public void setAllSubs(int allSubs) {
		this.allSubs = allSubs;
	}
	public int getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
	}
	public int getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getAvg_aonet() {
		return avg_aonet;
	}
	public void setAvg_aonet(long avg_aonet) {
		this.avg_aonet = avg_aonet;
	}
	public long getAvg_cutover() {
		return avg_cutover;
	}
	public void setAvg_cutover(long avg_cutover) {
		this.avg_cutover = avg_cutover;
	}
	public long getAvg_topup() {
		return avg_topup;
	}
	public void setAvg_topup(long avg_topup) {
		this.avg_topup = avg_topup;
	}
	public long getAvg_usage() {
		return avg_usage;
	}
	public void setAvg_usage(long avg_usage) {
		this.avg_usage = avg_usage;
	}
	
	
}
