package com.crm.provisioning.thread;

import java.util.Vector;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class RechargeTriggerThread extends DispatcherThread
{
	public String					filteredProduct	= "";
	public String					orderRouteQueue	= "";
	public String					orderUser		= "";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.addElement(ThreadUtil.createTextParameter("filteredProduct", 400,
				"Subscription all these suspended products (if exist, by alias, separated by comma)."));
		vtReturn.addElement(ThreadUtil.createTextParameter("orderRouteQueue", 400, "order route queue name(jndi)."));
		vtReturn.addElement(ThreadUtil.createTextParameter("orderUser", 400, "order user (default system)."));

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
			filteredProduct = ThreadUtil.getString(this, "filteredProduct", false, "");
			orderRouteQueue = ThreadUtil.getString(this, "orderRouteQueue", false, "queue/OrderRoute");

			orderUser = ThreadUtil.getString(this, "orderUser", false, "system");
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

	public boolean isExcludedProduct(String productAlias)
	{
		if ("".equals(filteredProduct) || filteredProduct == null)
			return false;

		if (filteredProduct.contains(productAlias + ","))
		{
			return false;
		}

		if (filteredProduct.contains("," + productAlias))
		{
			return false;
		}

		if (filteredProduct.equals(productAlias))
		{
			return false;
		}

		return true;
	}

}
