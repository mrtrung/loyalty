package com.crm.ascs.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.crm.kernel.io.FileUtil;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class WriteFileThread extends DispatcherThread
{
	public String				savedDirectory		= "";
	public String				fileNameFormat		= "";
	public String				tempFileName		= "";

	public int					fileRollingInterval	= 60;
	public int					maxFileRecords		= 1000;

	private long				lastTimeRolling		= System.currentTimeMillis();
	private int					currentFileRecords	= 0;
	private File				tempFile			= null;

	private FileOutputStream	fileOutStream		= null;

	private Object				lockedObject		= new Object();

	private SimpleDateFormat	sdf					= null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("savedDirectory", 400, "Trigger file saved directory."));
		vtReturn.add(ThreadUtil.createTextParameter("fileNameFormat", 400,
				"Trigger file name format (include date format, default: 'trigger_'yyyyMMddHHmmss'.txt')."));
		vtReturn.add(ThreadUtil.createTextParameter("tempFileName", 400, "Trigger temporary file (default: trigger.tmp)"));
		vtReturn.add(ThreadUtil.createIntegerParameter("fileRollingInterval", "File rolling interval (second, default: 60s)"));
		vtReturn.add(ThreadUtil.createIntegerParameter("maxFileRecords", "Max number of records in file (default: 1000)"));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		savedDirectory = ThreadUtil.getString(this, "savedDirectory", false, "/");
		fileNameFormat = ThreadUtil.getString(this, "fileNameFormat", false, "'trigger_'yyyyMMddHHmmss'.txt'");

		sdf = new SimpleDateFormat(fileNameFormat);

		tempFileName = ThreadUtil.getString(this, "tempFileName", false, "trigger.tmp");
		fileRollingInterval = ThreadUtil.getInt(this, "fileRollingInterval", 60);
		maxFileRecords = ThreadUtil.getInt(this, "maxFileRecords", 1000);

		// TODO Auto-generated method stub
		super.fillDispatcherParameter();
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		validateDirectory();
	}

	@Override
	public void afterProcessSession() throws Exception
	{
		try
		{
			synchronized (lockedObject)
			{
				fileOutStream.close();
			}
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	public void validateDirectory() throws Exception
	{
		lastTimeRolling = System.currentTimeMillis();
		debugMonitor("Validating directory and file...");

		if (fileOutStream != null)
		{
			try
			{
				fileOutStream.close();
			}
			catch (Exception e)
			{
			}
		}

		File directory = new File(savedDirectory);
		if (!directory.exists())
		{
			debugMonitor("Directory " + directory + " does not exist.");
			if (!directory.mkdirs())
			{
				throw new Exception("Can not create directory: " + savedDirectory);
			}
			else
			{
				debugMonitor("Directory " + directory + " has been created.");
			}
		}
		else
		{
			debugMonitor("Directory " + directory + " exists.");
		}

		String filePath = "";

		if (savedDirectory.endsWith("/") || savedDirectory.endsWith("\\"))
			filePath = savedDirectory + tempFileName;
		else
			filePath = savedDirectory + "/" + tempFileName;

		tempFile = new File(filePath);
		currentFileRecords = 0;
		if (!tempFile.exists())
		{
			debugMonitor("File " + filePath + " does not exist.");
			try
			{
				tempFile.createNewFile();
				debugMonitor("File " + filePath + " has been created.");
			}
			catch (Exception e)
			{
				throw e;
			}
		}
		else
		{
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new FileReader(tempFile));
				while ((reader.readLine()) != null)
					currentFileRecords++;
			}
			catch (Exception ex)
			{
				debugMonitor(ex);
				currentFileRecords = 0;
			}
			finally
			{
				debugMonitor("File " + filePath + " exists, has " + currentFileRecords + " record(s).");
				if (reader != null)
					reader.close();
			}
		}

		fileOutStream = new FileOutputStream(tempFile);

		debugMonitor("Validating completed.");
	}

	private void checkForRolling()
	{
		if ((System.currentTimeMillis() > lastTimeRolling + fileRollingInterval * 1000 && currentFileRecords > 0)
				|| (currentFileRecords >= maxFileRecords))
		{
			try
			{
				rollingFile();
			}
			catch (Exception e)
			{
				debugMonitor(e);
			}
		}
	}

	private void rollingFile() throws Exception
	{
		try
		{
			if (fileOutStream != null)
			{
				fileOutStream.flush();
				fileOutStream.close();
				fileOutStream = null;
			}

			String destFileName = sdf.format(new Date());

			String directory = (new File(savedDirectory)).getAbsolutePath();
			if (directory.endsWith("\\") || directory.endsWith("/"))
				destFileName = directory + destFileName;
			else
				destFileName = directory + "/" + destFileName;

			File destFile = new File(destFileName);

			if (!FileUtil.renameFile(tempFile.getAbsolutePath(), destFile.getAbsolutePath(), true))
			{
				throw new Exception("Can not rename file " + tempFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
			}
			else
			{
				debugMonitor("File " + tempFile.getAbsolutePath() + " has been renamed to " + destFile.getAbsolutePath());
			}
		}
		finally
		{
			validateDirectory();
		}
	}

	public void writeToFile(String textLine) throws Exception
	{
		synchronized (lockedObject)
		{
			checkForRolling();
			try
			{
				String newLine = "\r\n";
				fileOutStream.write(textLine.getBytes());
				fileOutStream.write(newLine.getBytes());
				fileOutStream.flush();
				currentFileRecords++;
			}
			catch (Exception e)
			{
				debugMonitor(e);
				validateDirectory();
			}
		}
	}

	@Override
	public void doProcessSession() throws Exception
	{
		try
		{
			synchronized (lockedObject)
			{
				checkForRolling();
			}
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
	}
}
