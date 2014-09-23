package com.crm.thread;

import java.io.File;
import java.text.NumberFormat;
import java.util.Vector;

import javax.jms.Queue;

import org.apache.log4j.Logger;

import com.crm.kernel.message.AlarmMessage;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.StringPool;
import com.fss.thread.ParameterType;
import com.fss.thread.ParameterUtil;
import com.fss.util.AppException;
import com.fss.util.StringUtil;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author: HungPQ
 * @version 1.0
 */

public class QueueMonitorThread extends DispatcherThread
{
	protected String[]		queueList			= new String[0];
	protected int			maxSize				= 10000;
	protected int			warningSize			= 1000;
	protected String		warningDiskPath		= "";
	protected int			warningDiskPercent	= 10;

	protected long			lastGarbage			= System.currentTimeMillis();

	protected static Logger	log					= Logger.getLogger(QueueMonitorThread.class);

	protected String		alarmMessage		= "";

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();

		// data source connection
		vtReturn.addElement(
				createParameterDefinition("queueList", ""
						, ParameterType.PARAM_TEXTBOX_MAX, "", "Indicates that default max queue size"));

		vtReturn.addElement(
				createParameterDefinition("warningSize", ""
						, ParameterType.PARAM_TEXTBOX_MAX, "", "Warning size"));

		vtReturn.addElement(
				createParameterDefinition("maxSize", ""
						, ParameterType.PARAM_TEXTBOX_MAX, "", "Max size"));
		vtReturn.add(ThreadUtil.createTextParameter("diskPath", 400,
				"Path of warning disk."));
		vtReturn.add(ThreadUtil.createIntegerParameter("warningDiskPercent",
				"Percent of in used disk space need to warning if reach."));

		Vector vtYesNo = new Vector();
		vtYesNo.addElement("Y");
		vtYesNo.addElement("N");

		vtReturn.addElement(
				ParameterUtil.createParameterDefinition("alarmEnable", ""
						, ParameterType.PARAM_COMBOBOX, vtYesNo, "Never expire session", "1"));

		vtReturn.addAll(ThreadUtil.createQueueParameter(this));
		vtReturn.addAll(ThreadUtil.createLogParameter(this));

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

			maxSize = ThreadUtil.getInt(this, "maxSize", 10000);

			warningSize = ThreadUtil.getInt(this, "warningSize", 1000);

			queueList = StringUtil.toStringArray(ThreadUtil.getString(this, "queueList", false, ""), StringPool.SEMICOLON);

			warningDiskPath = ThreadUtil.getString(this, "diskPath", false, "/");

			warningDiskPercent = ThreadUtil.getInt(this, "warningDiskPercent", 10);
		}
		catch (AppException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new AppException(e.getMessage());
		}

	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// Modified by NamTA
	// Modified Date 27/08/2012
	// //////////////////////////////////////////////////////
	protected String queueWarning(Queue checkQueue, String name, int size) throws Exception
	{
		if (size > warningSize)
		{
			String queueWarning = "";

			if (size >= maxSize)
			{
				queueWarning = "FATAL: queue " + name + " is reach to limitation (" + size + "/" + maxSize + ")\r\n";
			}
			else
			{
				queueWarning = "WARNING: queue " + name + " may be reach to limitation (" + size + "/" + maxSize + ")\r\n";
			}

			logMonitor(queueWarning);
			// alarmMessage += warningMessage;

			return queueWarning;
		}
		return "";
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// Modified by NamTA
	// Modified Date 27/08/2012
	// //////////////////////////////////////////////////////
	protected boolean memoryLogging() throws Exception
	{
		StringBuilder sb = new StringBuilder();

		Runtime runtime = Runtime.getRuntime();

		/**
		 * Removed garbage function, changed to use in JVM options.
		 */
		// garbage
		// if ((lastGarbage + 10 * 60 * 1000) < System.currentTimeMillis())
		// {
		// log.info(sb.toString());
		// logMonitor(sb.toString());
		//
		// sb.append("Cleaning memory ...\n");
		//
		// runtime.gc();
		//
		// lastGarbage = System.currentTimeMillis();
		//
		// log.info("Cleaned memory ...");
		// logMonitor("Cleaned memory ...");
		// }

		// write log
		NumberFormat format = NumberFormat.getInstance();

		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		File file = new File(warningDiskPath);
		long totalDiskSize = file.getTotalSpace();
		long usableDiskSize = file.getUsableSpace();
		long usedDiskSize = totalDiskSize - usableDiskSize;
		long percentUsedDisk = 100 * (usedDiskSize) / totalDiskSize;

		sb = new StringBuilder();
		sb.append("Memory information:\r\n");
		sb.append("\t :: Free memory            : " + format.format(freeMemory / 1024 / 1024) + " MB\r\n");
		sb.append("\t :: Allocated memory       : " + format.format(allocatedMemory / 1024 / 1024) + " MB\r\n");
		sb.append("\t :: Max memory             : " + format.format(maxMemory / 1024 / 1024) + " MB\r\n");
		sb.append("\t :: Total free memory      : " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024 / 1024)
				+ " MB\r\n");
		sb.append("\t :: Total free memory      : "
				+ format.format((100 * (freeMemory + (maxMemory - allocatedMemory)) / maxMemory))
				+ " (%)\r\n");
		sb.append("\t :: Disk in used           : " + format.format(usedDiskSize / 1024 / 1024) + "/"
				+ format.format(totalDiskSize / 1024 / 1024) + " MB, Used "
				+ format.format(percentUsedDisk) + " (%) (" + warningDiskPath + ")\r\n");
		sb.append("\t :: Total running thread   : " + Thread.activeCount() + "\r\n"); // DuyMB

		alarmMessage += sb.toString();
		boolean needAlarm = false;
		if (percentUsedDisk >= warningDiskPercent)
		{
			needAlarm = true;
		}

		return needAlarm;
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// Modified by NamTA
	// Modified Date 27/08/2012
	// //////////////////////////////////////////////////////
	public void doProcessSession() throws Exception
	{
		alarmMessage = "";
		boolean needAlarm = false;
		needAlarm = memoryLogging();

		String queueWarningMessage = "";

		alarmMessage += "Local Income SMS queue: " + QueueFactory.getIncomeSMSQueueSize() + "\r\n";
		alarmMessage += "Local CommandRoute queue: " + QueueFactory.getCommandRoutingSize() + "\r\n";
		alarmMessage += "Local CommandLog queue: \t" + QueueFactory.getCommandLogQueueSize() + "\r\n";
		alarmMessage += "Local CommandStatistic queue: \t" + QueueFactory.getStatisticQueueSize() + "\r\n";

		if (queueDispatcherEnable)
		{
			for (int j = 0; j < queueList.length; j++)
			{
				if (!queueList[j].equals(""))
				{
					Queue checkQueue = QueueFactory.getQueue(queueList[j]);

					int size = 0;

					MQConnection connection = null;
					try
					{
						connection = getMQConnection();
						size = connection.getQueueSize(checkQueue);
						// size = QueueFactory.getQueueSize(queueSession,
						// checkQueue);
						alarmMessage += "Total command request for " + queueList[j] + " : " + size + "\r\n";
					}
					catch (Exception e)
					{
						alarmMessage += "Total command request for " + queueList[j] + " : Can not count, browser is closed.\r\n";
					}
					finally
					{
						returnMQConnection(connection);
					}

					queueWarningMessage += queueWarning(checkQueue, queueList[j], size);
				}
			}
		}

		if (needAlarm)
		{
			alarmMessage += "WARNING: Disk space is running low";
		}
		if (!queueWarningMessage.equals(""))
		{
			needAlarm = true;
			alarmMessage += queueWarningMessage;
		}

		log.info(alarmMessage);

		logMonitor(alarmMessage);

		if (needAlarm)
		{
			AlarmMessage message = new AlarmMessage();
			message.setContent(alarmMessage);
			message.setDescription("System resource is running low.");
			message.setCause("system-resouce");
			message.setImmediately(true);
			sendAlarmMessage(message);
		}
	}
}
