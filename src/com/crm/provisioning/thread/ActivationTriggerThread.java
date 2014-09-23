package com.crm.provisioning.thread;

import java.util.HashMap;
import java.util.Vector;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class ActivationTriggerThread extends DispatcherThread
{
	public String						orderRouteQueue		= "";
	public String						orderUser			= "";

	public HashMap<String, String[]>	registerProducts	= new HashMap<String, String[]>();

	private String						allowedCos			= "";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(ThreadUtil
				.createTextParameter(
						"registerProduct",
						400,
						"For activationTrigger, register all these products (if not exist, format [<alias>:<serviceAddress>:<keyword>], separate products by comma)."));

		vtReturn.addElement(ThreadUtil.createTextParameter("orderRouteQueue", 400, "order route queue name(jndi)."));
		vtReturn.addElement(ThreadUtil.createTextParameter("orderUser", 400, "order user (default system)."));
		vtReturn.addElement(ThreadUtil.createTextParameter("allowedCOS", 800,
				"COS Filter, separated by comma, blank it if you want to allow all."));

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
			orderRouteQueue = ThreadUtil.getString(this, "orderRouteQueue", false, "queue/OrderRoute");

			orderUser = ThreadUtil.getString(this, "orderUser", false, "system");

			allowedCos = ThreadUtil.getString(this, "allowedCOS", false, "");
			allowedCos = allowedCos.replace(" ", "");

			String regProduct = ThreadUtil.getString(this, "registerProduct", false, "");
			registerProducts.clear();
			if (!regProduct.equals(""))
			{
				String[] products = regProduct.split(",");
				for (String product : products)
				{
					String[] productDetails = product.split(":");
					if (productDetails.length != 3)
					{
						throw new Exception("Invalid registerProduct parameter format, see tooltext for more format detail.");
					}
					registerProducts.put(productDetails[0], productDetails);
				}
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

	public String[] getRegisterProducts()
	{
		return registerProducts.keySet().toArray(new String[] {});
	}

	public String getProductServiceAddress(String product)
	{
		String[] productDetail = registerProducts.get(product);
		if (productDetail == null)
			return "";
		else
			return productDetail[1];
	}

	public String getProductKeyword(String product)
	{
		String[] productDetail = registerProducts.get(product);
		if (productDetail == null)
			return "";
		else
			return productDetail[2];
	}

	public boolean isAllowedCos(String cosName)
	{
		if (cosName.trim().equals("") || allowedCos.trim().equals(""))
			return true;

		if (allowedCos.startsWith(cosName + ","))
			return true;

		if (allowedCos.endsWith("," + cosName))
			return true;

		if (allowedCos.contains("," + cosName + ","))
			return true;

		if (allowedCos.trim().equals(cosName.trim()))
			return true;

		return false;
	}
}
