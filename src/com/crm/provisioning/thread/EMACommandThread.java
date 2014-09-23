package com.crm.provisioning.thread;

import java.util.Vector;

import com.crm.thread.util.ThreadUtil;
import com.crm.util.AppProperties;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class EMACommandThread extends CommandThread
{
	public long		enquireInterval		= 10;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addAll(ThreadUtil.createProvisioningParameter(this));
		vtReturn.add(ThreadUtil.createBooleanParameter("simulationMode", "Use simulation or not"));
		vtReturn.add(ThreadUtil.createLongParameter("simulationExecuteTime", "Simulation time in millisecond."));
		vtReturn.add(ThreadUtil.createTextParameter("simulationCause", 400, "Response cause after using simulation."));
		vtReturn.addAll(ThreadUtil.createQueueParameter(this));
		vtReturn.addAll(ThreadUtil.createInstanceParameter(this));
		vtReturn.addAll(ThreadUtil.createLogParameter(this));
		vtReturn.addElement(createParameterDefinition("enquireInterval"
				, "", ParameterType.PARAM_TEXTBOX_MASK, "999999", ""));
		
		return vtReturn;
	}
	
	public void fillDispatcherParameter() throws AppException
	{
		super.fillDispatcherParameter();
		enquireInterval = ThreadUtil.getLong(this, "enquireInterval", 3000);
	}
	
	public void initProvisioningParameters() throws Exception
	{
		try
		{
			super.initProvisioningParameters();

			AppProperties parameters = new AppProperties();

			parameters.setLong("enquireInterval", enquireInterval);

			provisioningPool.setParameters(parameters);
		}
		catch (Exception e)
		{
			throw e;
		}
	}
}
