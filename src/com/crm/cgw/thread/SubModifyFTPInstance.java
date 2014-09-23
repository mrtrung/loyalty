/**
 * 
 */
package com.crm.cgw.thread;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.crm.cgw.ftp.CdrInput;

import com.crm.cgw.util.FileUtils;
import com.crm.kernel.message.Constants;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherInstance;
import com.crm.util.AppProperties;

/**
 * @author hungdt
 * 
 */
public class SubModifyFTPInstance extends DispatcherInstance
{
	protected List<CdrInput>	records	= null;
	protected SimpleDateFormat	sdf		= new SimpleDateFormat(
											"yyyyMMddhhmmss");
	protected Calendar			startTime	= null;

	public SubModifyFTPInstance() throws Exception
	{
		super();
	}

	public SubModifyFTPThread getDispatcher()
	{
		return (SubModifyFTPThread) dispatcher;

	}

	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();
		records = new ArrayList<CdrInput>();
		startTime = Calendar.getInstance();
		try
		{
			String [] cpName = getDispatcher().cdrCPNameDownload
					.split(getDispatcher().cdrCollumnSeparate);
			for (String cp : cpName)
			{
				File currentFile = new File(getDispatcher().cdrFolder + cp);
				File toFile = new File(getDispatcher().cdrBackupFolder + cp);
				// backup cdr
				FileUtils.copy(currentFile, toFile);
				// load content to list
				LoadFile(cp);
			}

			if (records.size() > 0)
			{
				SendOrder();
			}
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}

	}

	public void doProcessSession() throws Exception
	{
		Calendar now = Calendar.getInstance();
		if (now.getTimeInMillis() - startTime.getTimeInMillis() > 1000 * getDispatcher().loadInterval)
		{
			records = new ArrayList<CdrInput>();
			String [] cpName = getDispatcher().cdrCPNameDownload
					.split(getDispatcher().cdrCollumnSeparate);
			for (String cp : cpName)
			{
				File currentFile = new File(getDispatcher().cdrFolder + cp);
				File toFile = new File(getDispatcher().cdrBackupFolder + cp);
				// backup cdr
				FileUtils.copy(currentFile, toFile);
				// load content to list
				LoadFile(cp);
			}

			if (records.size() > 0)
			{
				SendOrder();
			}
		}
	}

	public void SendOrder() throws Exception
	{
		int count = 0;

		for (int i = 0; i < records.size(); i++)
		{

			CdrInput input = records.get(i);

			CommandMessage message = new CommandMessage();
			message.setIsdn(input.getBNumber());
			message.setServiceAddress(input.getANumber());
			message.setCont_type(Integer.parseInt(input.getContentType()));
			message.setChannel(Constants.CHANNEL_WEB);
			message.setKeyword(getDispatcher().keywordPrefix + input.getANumber());
			message.setUserName("system");
			message.setTimeout(getDispatcher().requestTimeout * 1000);
			message.setCgwStatus(input.getStatus());

			MQConnection connection = null;
			try
			{
				connection = getMQConnection();
				connection.sendMessage(message, message.getTimeout(),
						queueWorking, message.getTimeout(),
						getDispatcher().queuePersistent);

				count++;
			}
			catch (Exception e)
			{
				debugMonitor(e);
			}
			finally
			{
				returnMQConnection(connection);
			}

		}

		debugMonitor(count + " records send to order.");
	}

	public void LoadFile(String cp)
	{
		FileReader fileReader = null;

		try
		{
			StringBuilder strBuilder = new StringBuilder();
			char [] buffer = new char[1024];

			File cdrFile = new File(getDispatcher().cdrFolder + cp);
			File [] files = cdrFile.listFiles();
			for (File file : files)
			{
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
				String [] fileData = null;
				if (fileContent.contains("\r\n"))
					fileData = fileContent.split("\r\n");
				else if (fileContent.contains("\n"))
					fileData = fileContent.split("\n");
				else
					fileData = fileContent.split("\r");

				CdrInput record = null;
				for (int i = 0; i < fileData.length; i++)
				{
					String [] recordContents = fileData[i].split("\t");
					try
					{
						record = new CdrInput();
						record.setANumber(recordContents[0].trim());
						record.setBNumber(recordContents[1].trim());
						record.setFromDatetime(sdf
								.parse(recordContents[2].trim()));
						record.setStatus(recordContents[3].trim());
						record.setToDatetime(sdf.parse(recordContents[4]
								.trim()));
						record.setCpId(Integer.parseInt(recordContents[5]
								.trim()));
						record.setContentType(recordContents[6].trim());
						record.setServiceName(recordContents[7].trim());
						records.add(record);
						debugMonitor("Add records: " + record.toString());
					}
					catch (Exception e)
					{
						debugMonitor("Can not parse record: "
								+ fileData[i]);
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
			startTime = Calendar.getInstance();
			debugMonitor("Added total " + records.size() + " record(s).");
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
	}

}
