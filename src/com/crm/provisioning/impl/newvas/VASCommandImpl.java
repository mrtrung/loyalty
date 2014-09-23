package com.crm.provisioning.impl.newvas;

import com.crm.ascs.net.Trigger;
import com.crm.provisioning.impl.CommandImpl;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.provisioning.impl.newvas.VASConnection;

public class VASCommandImpl extends CommandImpl
{

	/**
	 * Get provisioning from VASGATE <br>
	 * 
	 * Author: NamTA <br>
	 * Create Date: 08/05/2012
	 * 
	 * @param instance
	 * @param provisioningCommand
	 * @param request
	 * @return
	 * @throws Exception
	 */
	// public CommandMessage getProvisioning(
	// CommandInstance instance, ProvisioningCommand provisioningCommand,
	// CommandMessage request)
	// throws Exception
	// {
	// VASConnection connection = null;
	// ProductRoute productRoute = null;
	// ProductEntry productEntry = null;
	// if (instance.getDebugMode().equals("depend"))
	// {
	// simulation(instance, provisioningCommand, request);
	// }
	// else
	// {
	// try
	// {
	// productEntry =
	// ProductFactory.getCache().getProduct(request.getProductId());
	// productRoute =
	// ProductFactory.getCache().getProductRoute(request.getRouteId());
	// connection = (VASConnection) instance.getProvisioningConnection();
	// connection.provisioning(request);
	//
	// String messageResponseKey = request.getActionType() + "-" +
	// request.getCause();
	// String messageResponseValue =
	// productRoute.getParameter(messageResponseKey, "");
	// if (productRoute.getChannel().equals(Constants.CHANNEL_SMS)
	// && !messageResponseValue.equals(""))
	// {
	// CommandUtil.sendSMS(instance, request, messageResponseValue);
	// }
	// else if (productRoute.getChannel().equals(Constants.CHANNEL_SMS))
	// {
	// messageResponseValue = productEntry.getParameter(request.getCause(),
	// "Xin loi, he thong dang ban. Xin quy khach vui long nhan tin lai sau.");
	// CommandUtil.sendSMS(instance, request, messageResponseValue);
	// }
	// }
	// catch (Exception e)
	// {
	// CommandUtil.sendSMS(instance, request,
	// "Xin loi, he thong dang ban. Xin quy khach vui long nhan tin lai sau.");
	// processError(instance, provisioningCommand, request, e);
	// }
	// finally
	// {
	// instance.closeProvisioningConnection(connection);
	// }
	// }
	// return request;
	// }
	//
	// public CommandMessage getActivationStatus(CommandInstance instance,
	// ProvisioningCommand provisioningCommand,
	// CommandMessage request)
	// throws Exception
	// {
	// VASConnection connection = null;
	// try
	// {
	// connection = (VASConnection) instance.getProvisioningConnection();
	// connection.checkAllStatus(request);
	// }
	// catch (Exception e)
	// {
	// processError(instance, provisioningCommand, request, e);
	// }
	// finally
	// {
	// instance.closeProvisioningConnection(connection);
	// }
	//
	// return request;
	// }

	public Trigger checkActivationStatus(CommandInstance instance, Trigger trigger) throws Exception
	{
		VASConnection connection = null;
		try
		{
			connection = (VASConnection) instance.getProvisioningConnection();
			connection.checkStatus(instance, trigger);
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			instance.closeProvisioningConnection(connection);
		}

		return trigger;
	}

}
