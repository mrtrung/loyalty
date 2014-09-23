package com.crm.ascs.newvasconnection;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.crm.ascs.net.Trigger;
import com.crm.ascs.net.TriggerRecharge;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.impl.newvas.VASCommandImpl;
import com.crm.provisioning.thread.CommandInstance;

public class ASCSVasConnectionInstance extends CommandInstance
{

	public ASCSVasConnectionInstance() throws Exception
	{
		super();
	}

	@Override
	public int processMessage(Message message) throws Exception
	{
		Trigger trigger = (Trigger) ((ObjectMessage) message).getObject();
		if (getDebugMode().equals("depend"))
		{
			trigger.setDescription("Success");
			logMonitor(trigger.toLogString());
			Thread.sleep(getDispatcher().simulationTime);
		}
		else
		{

			try
			{
				if (trigger instanceof TriggerRecharge)
				{
					VASCommandImpl vasCommandImpl = new VASCommandImpl();
					vasCommandImpl.checkActivationStatus(this, trigger);
					logMonitor(trigger.toLogString());
				}
				else
				{
					trigger.setDescription("Not instanceof Recharge Trigger");
					logMonitor(trigger.toLogString());
				}
			}
			catch (Exception ex)
			{
				trigger.setDescription(ex.getMessage());
				logMonitor(trigger.toLogString());
				return Constants.BIND_ACTION_ERROR;
			}
		}
		return Constants.BIND_ACTION_SUCCESS;
	}
}
