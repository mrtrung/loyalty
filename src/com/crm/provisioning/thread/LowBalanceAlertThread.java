package com.crm.provisioning.thread;

import java.util.Vector;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class LowBalanceAlertThread extends ProvisioningThread
{
	protected Vector vtAlert = new Vector();

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		Vector vtValue = new Vector();
		vtValue.addElement(createParameterDefinition("ProductId", "",
				ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "0"));

		vtValue.addElement(createParameterDefinition("Limitation", "",
				ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "1"));

		vtValue.addElement(createParameterDefinition("Balance", "",
				ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "2"));

		vtValue.addElement(createParameterDefinition("ServiceAddress", "",
				ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "3"));

		vtValue.addElement(createParameterDefinition("TimePerData", "",
				ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "4"));

		vtValue.addElement(createParameterDefinition("SMSContent", "",
				ParameterType.PARAM_TEXTBOX_MAX, "",
				"Context to mapping class", "5"));

		vtReturn.addElement(createParameterDefinition("AlertConfig", "",
				ParameterType.PARAM_TABLE, vtValue, "Alert Config"));
		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();
			vtAlert = new Vector();
			Object obj = getParameter("AlertConfig");
			if (obj != null && (obj instanceof Vector))
			{
				vtAlert = (Vector) ((Vector) obj).clone();
			}
		}
		catch (AppException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public String getSMSContent(long productid)
	{
		String content = "";
		for (int i = 0; i < vtAlert.size(); i++)
		{
			@SuppressWarnings("rawtypes")
			Vector vt = (Vector) vtAlert.elementAt(i);

			if (Long.parseLong(vt.elementAt(0).toString()) == productid)
			{
				content = vt.elementAt(5).toString();
				break;
			}
		}
		return content;
	}

	public int getDataLimitation(long productid)
	{
		int dataLimitation = 0;
		for (int i = 0; i < vtAlert.size(); i++)
		{
			try
			{
				Vector vt = (Vector) vtAlert.elementAt(i);

				if (Long.parseLong(vt.elementAt(0).toString()) == productid)
				{
					dataLimitation = Integer.parseInt(vt.elementAt(1)
							.toString());
					break;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return dataLimitation;
	}

	public int getTimePerData(long productid)
	{
		int TimePerData = 0;
		for (int i = 0; i < vtAlert.size(); i++)
		{
			Vector vt = (Vector) vtAlert.elementAt(i);

			if (Long.parseLong(vt.elementAt(0).toString()) == productid)
			{
				TimePerData = Integer.parseInt(vt.elementAt(4).toString());
				break;
			}
		}
		return TimePerData;
	}

	public String getBalanceName(long productid)
	{
		String balanceName = "";
		for (int i = 0; i < vtAlert.size(); i++)
		{
			Vector vt = (Vector) vtAlert.elementAt(i);

			if (Long.parseLong(vt.elementAt(0).toString()) == productid)
			{
				balanceName = vt.elementAt(2).toString();
				break;
			}
		}
		return balanceName;
	}

	public String getServiceAddress(long productid)
	{
		String serviceAddress = "";
		for (int i = 0; i < vtAlert.size(); i++)
		{
			Vector vt = (Vector) vtAlert.elementAt(i);

			if (Long.parseLong(vt.elementAt(0).toString()) == productid)
			{
				serviceAddress = vt.elementAt(3).toString();
				break;
			}
		}
		return serviceAddress;
	}
}
