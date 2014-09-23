package com.crm.ascs.thread;

import java.util.Vector;

import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class TriggerQueryThread extends RTBSThread
{
	public int					historyTime		= 1;
	public int					maxRetry		= 3;

	public boolean				useSimulation	= false;
	public long					simulationTime	= 1000;
	public String				simulationCause	= "";

	public String				queueActivation	= null;
	public String				queueRecharge	= null;
	public String				queueFile		= null;

	@Override
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();

			historyTime = ThreadUtil.getInt(this, "historyTime", 1);
			maxRetry = ThreadUtil.getInt(this, "maxRTBSRetry", 3);

			useSimulation = ThreadUtil.getBoolean(this, "simulationMode", false);
			simulationTime = ThreadUtil.getLong(this, "simulationExecuteTime", 1000);
			simulationCause = ThreadUtil.getString(this, "simulationCause", false, "");

			queueActivation = ThreadUtil.getString(this, "activationTriggerQueue", false, "");
			queueRecharge = ThreadUtil.getString(this, "rechargeTriggerQueue", false, "");
			queueFile = ThreadUtil.getString(this, "fileBackupQueue", false, "");
		}
		catch (AppException e)
		{
			logMonitor(e);

			throw e;
		}
		catch (Exception e)
		{
			logMonitor(e);

			throw new AppException(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(ThreadUtil.createIntegerParameter("historyTime",
				"Time range to get recharge history from present (by Hour)."));

		vtReturn.addElement(ThreadUtil.createIntegerParameter("maxRTBSRetry", "Max retrying time to get trigger info from RTBS."));

		vtReturn.add(ThreadUtil.createBooleanParameter("simulationMode", "Use simulation or not"));
		vtReturn.add(ThreadUtil.createLongParameter("simulationExecuteTime", "Simulation time in millisecond."));
		vtReturn.add(ThreadUtil.createTextParameter("simulationCause", 400, "Response cause after using simulation."));
		vtReturn.add(ThreadUtil.createTextParameter("activationTriggerQueue", 100,
				"Send activation trigger to queue (default none)."));
		vtReturn.add(ThreadUtil
				.createTextParameter("rechargeTriggerQueue", 100, "Send recharge trigger to queue (default none)."));
		vtReturn.add(ThreadUtil
				.createTextParameter("fileBackupQueue", 100, "Send trigger to queue (default none) for backup in case of error in inserting to DB."));
		vtReturn.addAll(super.getDispatcherDefinition());
		return vtReturn;
	}
}
