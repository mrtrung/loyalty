package com.crm.ascs.thread;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.crm.ascs.impl.TriggerImpl;
import com.crm.ascs.net.Trigger;
import com.crm.ascs.net.TriggerActivation;
import com.crm.ascs.net.TriggerRecharge;
import com.crm.kernel.io.FileUtil;
import com.crm.kernel.io.WildcardFilter;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class ImportTriggerThread extends DispatcherThread
{
	public String					importingDirectory	= "";
	public String					backupDirectory		= "";
	public String					wildcard			= "";
	private List<TriggerActivation>	triggerActivations	= new ArrayList<TriggerActivation>();
	private List<TriggerRecharge>	triggerRecharges	= new ArrayList<TriggerRecharge>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("importingDirectory", 400, "Importing directory path."));
		vtReturn.add(ThreadUtil.createTextParameter("backupDirectory", 400, "Backup directory path."));
		vtReturn.add(ThreadUtil.createTextParameter("wildcard", 400, "Wildcard for importing files."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		importingDirectory = ThreadUtil.getString(this, "importingDirectory", false, "/");
		backupDirectory = ThreadUtil.getString(this, "backupDirectory", false, "/");
		wildcard = ThreadUtil.getString(this, "wildcard", false, "*.txt");
		// TODO Auto-generated method stub
		super.fillDispatcherParameter();
	}

	private String validateBackupDirectory() throws Exception
	{
		File directory = new File(backupDirectory);
		if (!directory.exists())
		{
			debugMonitor("Directory " + backupDirectory + " does not exist.");
			if (!directory.mkdirs())
			{
				throw new Exception("Can not create directory: " + backupDirectory);
			}
			else
			{
				debugMonitor("Directory " + backupDirectory + " has been created.");
			}
		}

		return backupDirectory;
	}

	private void loadFile(File file)
	{
		triggerActivations.clear();
		triggerRecharges.clear();
		FileReader fileReader = null;
		try
		{
			StringBuilder strBuilder = new StringBuilder();
			char[] buffer = new char[1024];
			debugMonitor("Reading file " + file.getName());
			fileReader = new FileReader(file);
			int count = -1;
			do
			{
				count = fileReader.read(buffer, 0, buffer.length);
				if (count > 0)
					strBuilder.append(buffer, 0, count);
			}
			while (count > 0);

			String fileContent = strBuilder.toString();
			String[] fileData = null;
			if (fileContent.contains("\r\n"))
				fileData = fileContent.split("\r\n");
			else if (fileContent.contains("\n"))
				fileData = fileContent.split("\n");
			else
				fileData = fileContent.split("\r");

			for (int i = 0; i < fileData.length; i++)
			{
				try
				{
					Trigger trigger = Trigger.createTriggerFromFileString(fileData[i]);
					if (trigger != null)
					{
						if (trigger.getType() == Trigger.TYPE_ACTIVATION)
							triggerActivations.add((TriggerActivation) trigger);
						else if (trigger.getType() == Trigger.TYPE_RECHARGE)
							triggerRecharges.add((TriggerRecharge) trigger);
						debugMonitor("Add records: " + trigger.toString());
					}
					else
					{
						throw new Exception("Parse to null trigger.");
					}
				}
				catch (Exception ex)
				{
					debugMonitor("Can not parse trigger: " + fileData[i]);
					debugMonitor(ex);
				}
			}

			debugMonitor("Added total " + (triggerActivations.size() + triggerRecharges.size()) + " record(s).");
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
		finally
		{
			if (fileReader != null)
				try
				{
					fileReader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}

	@Override
	public void doProcessSession() throws Exception
	{
		File importingDir = new File(importingDirectory);

		debugMonitor("Checking importing directory " + importingDirectory + " ...");

		if (!importingDir.exists())
		{
			throw new Exception("Can not find importing directory: " + importingDirectory);
		}

		debugMonitor("Searching by wildcard " + wildcard + " ...");
		File[] files = importingDir.listFiles(new WildcardFilter(wildcard));

		if (files == null || files.length == 0)
		{
			debugMonitor("Searched 0 file.");
		}

		for (File file : files)
		{
			try
			{
				loadFile(file);

				String backupFileName = file.getName();

				boolean hasError = false;
				try
				{
					TriggerImpl.insertTriggerRecharge(triggerRecharges.toArray(new TriggerRecharge[] {}));
				}
				catch (Exception e)
				{
					debugMonitor(e);
					backupFileName = backupFileName + ".recharge";
					hasError = true;
				}

				try
				{
					TriggerImpl.insertTriggerActivation(triggerActivations.toArray(new TriggerActivation[] {}));
				}
				catch (Exception e)
				{
					debugMonitor(e);
					backupFileName = backupFileName + ".activation";
					hasError = true;
				}

				if (hasError)
					backupFileName = backupFileName + ".error";

				String backupDir = validateBackupDirectory();
				if (backupDir.endsWith("/") || backupDir.endsWith("\\"))
					backupFileName = backupDir + backupFileName;
				else
					backupFileName = backupDir + "/" + backupFileName;

				File backupFile = new File(backupFileName);
				if (FileUtil.renameFile(file.getAbsolutePath(), backupFile.getAbsolutePath(), true))
				{
					debugMonitor("Backup file " + file.getName() + " to " + backupFile.getPath());
				}
				else
				{
					debugMonitor("Can not backup file " + file.getName() + " to " + backupFile.getPath());
				}

			}
			catch (Exception e)
			{
				debugMonitor(e);
			}
		}
	}
}
