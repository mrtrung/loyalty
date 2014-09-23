package com.crm.product.thread.autorenew.warning;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import com.crm.util.StringUtil;
import com.fss.thread.ManageableThread;
import com.fss.thread.ParameterType;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;

public class AutoRenewWarningThread extends ManageableThread
{
	protected Vector									vtWarning			= new Vector();
	protected HashMap<String, AbtractAutoRenewWarning>	mAbtractWarning		= new HashMap<String, AbtractAutoRenewWarning>();
	protected boolean									runElementThread	= false;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		Vector vtWarning = new Vector();
		vtWarning.addElement(createParameterDefinition("Check Warning", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "0"));
		vtWarning.addElement(createParameterDefinition("SQLCommand", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"SQL Command", "1"));
		vtWarning.addElement(createParameterDefinition("List ISDN", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"List Warning", "2"));
		vtWarning.addElement(createParameterDefinition("Short Code", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"ShortCode Warning", "3"));
		vtWarning.addElement(createParameterDefinition("SMS Content", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "4"));
		vtWarning.addElement(createParameterDefinition("From Email", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "5"));
		vtWarning.addElement(createParameterDefinition("Password", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "6"));
		vtWarning.addElement(createParameterDefinition("To Email", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "7"));
		vtWarning.addElement(createParameterDefinition("Subject", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "8"));
		vtWarning.addElement(createParameterDefinition("Email Content", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"SQL Command", "9"));
		vtWarning.addElement(createParameterDefinition("Host", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "10"));
		vtWarning.addElement(createParameterDefinition("Schedule", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "11"));
		vtWarning.addElement(createParameterDefinition("Product ID", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "12"));
		vtWarning.addElement(createParameterDefinition("Process Class", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "13"));
		vtWarning.addElement(createParameterDefinition("Odds Time", "", ParameterType.PARAM_TEXTBOX_MAX, "",
				"", "14"));
		vtReturn.addElement(createParameterDefinition("Warning Config", "", ParameterType.PARAM_TABLE, vtWarning,
				"Warning Config"));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillParameter() throws AppException
	{
		super.fillParameter();

		Object objWarning = getParameter("Warning Config");
		if (objWarning != null && (objWarning instanceof Vector))
		{
			vtWarning = (Vector) ((Vector) objWarning).clone();
		}
	}

	@Override
	protected void beforeSession() throws Exception
	{
		for (int i = 0; i < vtWarning.size(); i++)
		{

			Vector vtRow = (Vector) vtWarning.elementAt(i);
			AbtractAutoRenewWarning abtractAutoRenewWarning = (AbtractAutoRenewWarning) Class.forName(vtRow.elementAt(13)
					.toString()).newInstance();

			abtractAutoRenewWarning.setCheckWarning(vtRow.elementAt(0).toString());
			abtractAutoRenewWarning.setsQLWarning(vtRow.elementAt(1).toString());
			abtractAutoRenewWarning.setIsdns(vtRow.elementAt(2).toString());
			abtractAutoRenewWarning.setShortCode(vtRow.elementAt(3).toString());
			abtractAutoRenewWarning.setSmsContent(vtRow.elementAt(4).toString());
			abtractAutoRenewWarning.setFromEmail(vtRow.elementAt(5).toString());
			abtractAutoRenewWarning.setPassword(vtRow.elementAt(6).toString());
			abtractAutoRenewWarning.setToEmails(vtRow.elementAt(7).toString());
			abtractAutoRenewWarning.setSubject(vtRow.elementAt(8).toString());
			abtractAutoRenewWarning.setEmailContent(vtRow.elementAt(9).toString());
			abtractAutoRenewWarning.setHost(vtRow.elementAt(10).toString());
			abtractAutoRenewWarning.setSchedules(StringUtil.toStringArray(vtRow.elementAt(11).toString(), ";"));
			abtractAutoRenewWarning.setOddsTime(Integer.parseInt(StringUtil.nvl(vtRow.elementAt(14).toString(), "1")));
			abtractAutoRenewWarning.setWarningThread(this);
			abtractAutoRenewWarning.initParams();
			mAbtractWarning.put(vtRow.elementAt(12).toString(), abtractAutoRenewWarning);
		}
		runElementThread = true;
	}

	@Override
	public void processSession() throws Exception
	{
		for (Entry<String, AbtractAutoRenewWarning> entry : mAbtractWarning.entrySet())
		{
			new Thread(entry.getValue()).start();
		}

		boolean bCheckRestart = false;

		while (miThreadCommand != ThreadConstant.THREAD_STOP)
		{
			Calendar calRestart = Calendar.getInstance();
			calRestart.set(Calendar.HOUR_OF_DAY, 0);
			calRestart.set(Calendar.MINUTE, 0);
			calRestart.set(Calendar.SECOND, 0);
			calRestart.set(Calendar.MILLISECOND, 0);
			long lRestart = calRestart.getTime().getTime();

			Calendar now = Calendar.getInstance();
			long lTimeNow = now.getTime().getTime();

			if (lTimeNow >= lRestart && lTimeNow < lRestart + 180000 && !bCheckRestart)
			{
				bCheckRestart = true;
				for (Entry<String, AbtractAutoRenewWarning> entry : mAbtractWarning.entrySet())
				{
					entry.getValue().initParams();
				}
			}
			else if (lTimeNow > lRestart + 180000)
			{
				bCheckRestart = false;
			}

			Thread.sleep(1000L);
		}
	}

	@Override
	protected void afterSession() throws Exception
	{
		runElementThread = false;
		super.afterSession();
	}
}
