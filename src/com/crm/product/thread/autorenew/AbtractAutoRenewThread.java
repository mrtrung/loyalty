package com.crm.product.thread.autorenew;

import java.util.Calendar;
import java.util.Date;

import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.util.StringUtil;
import com.fss.util.DateUtil;

public abstract class AbtractAutoRenewThread extends DispatcherThread
{
	public abstract void attachQueue(CommandMessage order) throws Exception;

	public boolean checkSchedule(String startTime, String endTime) throws Exception
	{
		String mstrToday = StringUtil.format(new Date(), "dd/MM/yyyy");

		String startProcess = mstrToday + " " + startTime;
		Date dtStartProcess = com.fss.util.DateUtil.toDate(startProcess, "dd/MM/yyyy HH:mm:ss");

		String endProcess = mstrToday + " " + endTime;
		Date dtEndProcess = com.fss.util.DateUtil.toDate(endProcess, "dd/MM/yyyy HH:mm:ss");

		Date dtCurentDate = new Date();
		if (dtCurentDate.after(dtStartProcess) && dtCurentDate.before(dtEndProcess))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public long calculateTime(String startTime, String endTime)
	{
		// INIT START TIME
		String[] argStartTime = StringUtil.toStringArray(startTime, ":");
		String strHourStartTime = argStartTime[0].trim();
		String strMinutesStartTime = argStartTime[1].trim();
		String strSecondsStartTime = argStartTime[2].trim();
		Calendar calStartTime = Calendar.getInstance();
		calStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strHourStartTime));
		calStartTime.set(Calendar.MINUTE, Integer.parseInt(strMinutesStartTime));
		calStartTime.set(Calendar.SECOND, Integer.parseInt(strSecondsStartTime));
		Date dtStartTime = calStartTime.getTime();
		long lStartTime = dtStartTime.getTime();

		// INIT END TIME
		String[] argEndTime = StringUtil.toStringArray(endTime, ":");
		String strHourEndTime = argEndTime[0].trim();
		String strMinutesEndTime = argEndTime[1].trim();
		String strSecondsEndTime = argEndTime[2].trim();
		Calendar calEndTime = Calendar.getInstance();
		calEndTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strHourEndTime));
		calEndTime.set(Calendar.MINUTE, Integer.parseInt(strMinutesEndTime));
		calEndTime.set(Calendar.SECOND, Integer.parseInt(strSecondsEndTime));
		Date dtEndTime = calEndTime.getTime();
		long lEndTime = dtEndTime.getTime();

		// GET NOW
		Date now = new Date();
		long lTimeNow = now.getTime();

		long lReturn = 0;
		if (lTimeNow < lStartTime)
		{
			lReturn = lStartTime - lTimeNow;
		}
		else if (lTimeNow > lEndTime)
		{
			calStartTime.add(Calendar.DAY_OF_MONTH, 1);
			dtStartTime = calStartTime.getTime();
			lStartTime = dtStartTime.getTime();
			lReturn = lStartTime - lTimeNow;
		}
		return lReturn;
	}
}
