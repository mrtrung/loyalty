package com.crm.ascs.test;

import javax.jms.Message;

import com.crm.ascs.ccws.CCWSConnection;
import com.crm.ascs.net.Trigger;
import com.crm.ascs.thread.RTBSInstance;
import com.crm.kernel.message.Constants;

public class SubModifyInstance extends RTBSInstance
{

	public SubModifyInstance() throws Exception
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public SubModifyThread getDispatcher()
	{
		// TODO Auto-generated method stub
		return (SubModifyThread) super.getDispatcher();
	}

	@Override
	public int processMessage(Message message) throws Exception
	{
		Trigger trigger = Trigger.getFromMQMessage(message);
		
		if (!(trigger instanceof RTBSMessage))
		{
			throw new Exception("Invalid RTBS Message.");
		}
		
		RTBSMessage msg = (RTBSMessage)trigger;
		
		CCWSConnection connection = null;
		try
		{
			connection = getCCWSConnection();
			
			if (msg.getAction().equals(RTBSMessage.ACTION_CREATE_SUB))
			{
				createSub(connection, msg);
			}
			else if (msg.getAction().equals(RTBSMessage.ACTION_DELETE_SUB))
			{
				deleteSub(connection, msg);
			}
			else if (msg.getAction().equals(RTBSMessage.ACTION_CHANGE_STATE_SUB))
			{
				changeStateSub(connection, msg);
			}
			else if (msg.getAction().equals(RTBSMessage.ACTION_CHARGE_SUB))
			{
				chargeSub(connection, msg);
			}
			else
			{
				debugMonitor("DO NOTHING " + trigger.getIsdn());
			}
		}
		finally
		{
			returnCCWSConnection(connection);
		}
		
		return Constants.BIND_ACTION_NONE;
	}

	private void createSub(CCWSConnection connection, RTBSMessage msg) throws Exception
	{
		String isdn = msg.getIsdn();
		String cosName = msg.getCosName();

		debugMonitor("CREATE " + isdn + " COSNAME " + cosName);
		if (connection.createSubscriber(isdn, cosName, getDispatcher().spName))
		{
			debugMonitor(isdn + " - SUCCESS");
		}
		else
		{
			debugMonitor(isdn + " - FAILURE");
		}
	}

	private void deleteSub(CCWSConnection connection, RTBSMessage msg) throws Exception
	{
		String isdn = msg.getIsdn();
		debugMonitor("DELETE " + isdn);
		if (connection.deleteSubscriber(isdn))
		{
			debugMonitor(isdn + " - SUCCESS");
		}
		else
		{
			debugMonitor(isdn + " - FAILURE");
		}
	}

	private void changeStateSub(CCWSConnection connection, RTBSMessage msg) throws Exception
	{
		String isdn = msg.getIsdn();
		String nextState = msg.getState();
		debugMonitor("CHANGE STATE " + isdn + " TO " + nextState);
		if (connection.changeSubscriberState(isdn, nextState))
		{
			debugMonitor(isdn + " - SUCCESS");
		}
		else
		{
			debugMonitor(isdn + " - FAILURE");
		}
	}

	private void chargeSub(CCWSConnection connection, RTBSMessage msg) throws Exception
	{
		String isdn = msg.getIsdn();
		double value = msg.getFaceValue();
		int offset = msg.getExpirationOffset();
		String comment = msg.getDescription();
		
		debugMonitor("CHARGE " + isdn + " VALUE=" + value + " EXP_OFFSET=" + offset + " COMMENT=" + comment);
		if (connection.topup(isdn, value, offset, comment))
		{
			debugMonitor(isdn + " - SUCCESS");
		}
		else
		{
			debugMonitor(isdn + " - FAILURE");
		}
	}
}
