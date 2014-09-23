package com.crm.ascs.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class SubscriberTestThread extends DispatcherThread
{
	private String			filePath		= "";
	private List<String>	isdnList		= new ArrayList<String>();
	private int				currentIndex	= 0;
	private Object			lockedObject	= new Object();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400, "Imported File Path."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		filePath = ThreadUtil.getString(this, "filePath", false, "");

		super.fillDispatcherParameter();
	}

	private void loadFile()
	{
		synchronized (lockedObject)
		{
			isdnList.clear();
			currentIndex = 0;
			FileReader fileReader = null;
			try
			{
				StringBuilder strBuilder = new StringBuilder();
				char[] buffer = new char[1024];
				fileReader = new FileReader(filePath);
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
					isdnList.add(fileData[i].trim());
				}

				debugMonitor("Added total " + isdnList.size() + " record(s).");
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
	}

	public String getIsdn()
	{
		synchronized (lockedObject)
		{
			if (currentIndex < isdnList.size())
			{
				return isdnList.get(currentIndex++);
			}
			else
			{
				return "";
			}
		}
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		// TODO Auto-generated method stub
		super.beforeProcessSession();

		loadFile();
	}
}
