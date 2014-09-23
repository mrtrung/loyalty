package com.crm.provisioning.thread;

public class WeeklyData {

	int level = 0;
	int allSubs = 0;
	int maxAmount = 0;
	int minAmount = 0;
	String action = "";
	int avgPoint	= 0;
	
	public int getAvgPoint() {
		return avgPoint;
	}
	public void setAvgPoint(int avgPoint) {
		this.avgPoint = avgPoint;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
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
		
	
}
