package com.crm.provisioning.impl.newvas;

import java.net.URL;

import com.crm.ascs.net.Trigger;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.ProvisioningConnection;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.service.elcom.vasman.CheckAllVasStatusReq;
import com.crm.service.elcom.vasman.CheckAllVasStatusResp;
import com.crm.service.elcom.vasman.ProvisioningReq;
import com.crm.service.elcom.vasman.ProvisioningResp;
import com.crm.service.elcom.vasman.WSVasmanagerLocator;
import com.crm.service.elcom.vasman.WSVasmanagerPortType;

public class VASConnection extends ProvisioningConnection
{
	private String					address		= "/WSVasManager/services/WSVasmanager";

	private WSVasmanagerPortType	serviceSoap	= null;

	public VASConnection()
	{
		super();
	}

	private URL getURL(String host, String port) throws Exception
	{
		address = "http://" + host + ":" + port + address;
		return new URL(address);
	}

	@Override
	public boolean openConnection() throws Exception
	{
		WSVasmanagerLocator serviceLocator = new WSVasmanagerLocator();
		URL url = getURL(getHost(), String.valueOf(getPort()));

		serviceSoap = serviceLocator.getWSVasmanagerHttpPort(url);

		return super.openConnection();
	}

	// public CommandMessage provisioning(CommandMessage request)
	// throws Exception
	// {
	// try
	// {
	// String keyword = StringUtil.nvl(request.getKeyword(), "");
	// String[] argParams = StringUtil.toStringArray(keyword, " ");
	// if (argParams.length < 2)
	// {
	// throw new AppException(Constants.ERROR_INVALID_SYNTAX);
	// }
	// String productName = StringUtil.nvl(argParams[1], "");
	// ProductEntry entry =
	// ProductFactory.getCache().getProduct(request.getProductId());
	// String sku = entry.getParameter(productName, "");
	// if (sku.equals(""))
	// {
	// throw new AppException(Constants.ERROR_INVALID_SYNTAX);
	// }
	// String sourceAddress = StringUtil.nvl(request.getIsdn(), "");
	// int commandId = 1;
	// if (request.getActionType().equals(Constants.ACTION_REGISTER))
	// {
	// commandId = 1;
	// }
	// else if (request.getActionType().equals(Constants.ACTION_UNREGISTER))
	// {
	// commandId = 3;
	// }
	// ActivationStatusSoap res =
	// serviceSoap.provisioning(sourceAddress, sku, commandId);
	// int returnCode = res.getReturnCode();
	// String responseDetail = res.getResponseDetail();
	// request.setCause(res.getResponseCode());
	// }
	// catch (Exception e)
	// {
	// throw e;
	// }
	// return request;
	// }
	//
	// public CommandMessage checkAllStatus(CommandMessage request) throws
	// Exception
	// {
	// try
	// {
	// String isdn = request.getIsdn();
	//
	// ActivationStatusSoap[] activationStatusSoaps =
	// serviceSoap.checkAllStatus(isdn);
	//
	// if (activationStatusSoaps == null)
	// {
	//
	// }
	// else
	// {
	// // SupplierStatus
	// // 1 - active
	// // 3 - deactive
	// // 2 - suspend
	// // vas name = productId
	// // vas id = sku
	// String vasList = "";
	// for (ActivationStatusSoap activationStatusSoap : activationStatusSoaps)
	// {
	// String vasName = activationStatusSoap.getProductId();
	// vasList += vasName + StringPool.COMMA;
	// request.setResponseValue(ResponseUtil.VAS + "." + vasName + ".id",
	// activationStatusSoap.getSku());
	// request.setResponseValue(ResponseUtil.VAS + "." + vasName + ".status",
	// activationStatusSoap.getSupplierStatus());
	// request.setResponseValue(ResponseUtil.VAS + "." + vasName +
	// ".description", "");
	// }
	//
	// request.setResponseValue(ResponseUtil.VAS, vasList);
	// }
	// }
	// catch (Exception e)
	// {
	// throw e;
	// }
	//
	// return request;
	// }

	public Trigger checkStatus(CommandInstance instance, Trigger trigger) throws Exception
	{
		String isdn = trigger.getIsdn();
		CheckAllVasStatusReq request = new CheckAllVasStatusReq();
		request.setMdn(isdn);
		request.setUser(getUserName());
		request.setPass(getPassword());

		CheckAllVasStatusResp response = serviceSoap.checkAllVasStatus(request);
		if (response == null)
		{
			trigger.setDescription("No registration service");
			instance.logMonitor(trigger.toLogString());
		}
		else if (response != null && !response.getErrorDetail().equals("") && response.getNStatus().length == 0)
		{
			trigger.setDescription(response.getErrorDetail());
			instance.logMonitor(trigger.toLogString());
		}
		else
		{
			StringBuilder stringBuilder = new StringBuilder();
			int arryStatus[] = response.getNStatus();
			int arrayVasId[] = response.getNVasID();

			for (int i = 0; i < response.getNStatus().length; i++)
			{
				int status = arryStatus[i];
				int vasId = arrayVasId[i];
				if (status == Constants.SUPPLIER_BARRING_STATUS)
				{
					ProvisioningReq provisioningRequest = new ProvisioningReq();
					provisioningRequest.setMdn(isdn);
					provisioningRequest.setNVasID(String.valueOf(vasId));
					provisioningRequest.setNCmdID(1);
					provisioningRequest.setSDescription("Active service from ASCS System");

					ProvisioningResp provisioningResponse = serviceSoap.provisioning(provisioningRequest);
					stringBuilder
							.append((new StringBuilder("[ProductId: ")).append(provisioningResponse.getNVasID())
									.append(", Curent Status: ").append(status)
									.append(", Reponse Code: ").append(provisioningResponse.getErrorCode())
									.append(", Response Detail: ").append(provisioningResponse.getErrorDetail()).append("] ")
									.toString());
				}
				else
				{
					stringBuilder
							.append((new StringBuilder("[ProductId: ")).append(String.valueOf(vasId))
									.append(", Curent Status: ").append(status)
									.append(", Reponse Code: ")
									.append(", Response Detail: ")
									.append("] ").toString());
				}
			}
			trigger.setDescription(stringBuilder.toString());
		}
		return trigger;
	}
}
