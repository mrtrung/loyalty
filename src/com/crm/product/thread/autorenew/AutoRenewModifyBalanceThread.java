package com.crm.product.thread.autorenew;

import java.util.Vector;

import com.crm.provisioning.thread.ProvisioningThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class AutoRenewModifyBalanceThread extends ProvisioningThread
{
	private int	_retryTime			= 3;
	private int	_balanceExpiration	= 1;

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("BalanceExpiration", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("RetryTime", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
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
			set_retryTime(loadInteger("RetryTime"));
			set_balanceExpiration(loadInteger("BalanceExpiration"));
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

	public int get_retryTime()
	{
		return _retryTime;
	}

	public void set_retryTime(int _retryTime)
	{
		this._retryTime = _retryTime;
	}

	public int get_balanceExpiration()
	{
		return _balanceExpiration;
	}

	public void set_balanceExpiration(int _balanceExpiration)
	{
		this._balanceExpiration = _balanceExpiration;
	}

}
