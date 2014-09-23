package com.crm.ascs.thread;

import javax.jms.Message;

import com.crm.ascs.net.Trigger;
import com.crm.kernel.message.Constants;
import com.crm.thread.DispatcherInstance;

public class WriteFileInstance extends DispatcherInstance
{

	public WriteFileInstance() throws Exception
	{
		super();
	}

	@Override
	public WriteFileThread getDispatcher()
	{
		return (WriteFileThread) super.getDispatcher();
	}

	@Override
	public int processMessage(Message message) throws Exception
	{
		Trigger trigger = Trigger.getFromMQMessage(message);
		
		if (trigger != null)
		{
			getDispatcher().writeToFile(Trigger.toFileLogString(trigger));
			debugMonitor("Write to file: " + trigger.getContent());
		}
		
		return Constants.BIND_ACTION_NONE;
	}
}
