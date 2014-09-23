package com.crm.thread;

import java.util.Date;

import com.crm.provisioning.thread.EMACommandThread;
import com.fss.SMSUtility.BasicInput;

public class EPOSCommandThread extends EMACommandThread
{
	public BasicInput input = null;
	
	public boolean hasError = false;
	
	public Date	lastRun	= new Date();
	
	public void setInput(BasicInput input)
	{
		this.input = input;
	}
	
	public BasicInput getInput()
	{
		return this.input;
	}
	
	public Date getLastRun()
	{
		return lastRun;
	}

	public void setLastRun(Date lastRun)
	{
		this.lastRun = lastRun;
	}
}
