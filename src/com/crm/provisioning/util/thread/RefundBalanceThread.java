package com.crm.provisioning.util.thread;

import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.Message;
import javax.jms.MessageProducer;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class RefundBalanceThread extends DispatcherThread
{
	public String										filePath		= "";
	public ConcurrentLinkedQueue<RefundBalanceRecord>	records			= new ConcurrentLinkedQueue<RefundBalanceRecord>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400, "Imported File Path (Each record in 1 line: isdn,amount,balanceName)"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		filePath = ThreadUtil.getString(this, "filePath", false, "");
		// TODO Auto-generated method stub
		super.fillDispatcherParameter();
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		// TODO Auto-generated method stub
		super.beforeProcessSession();

		loadFile();
	}

	public void doProcessSession() throws Exception
	{
		if (records.size() == 0)
			return;

		int count = 0;
		MQConnection connection = null;
		MessageProducer producer = null;
		try
		{
			connection = getMQConnection();

			producer = QueueFactory.createQueueProducer(connection.getSession(), queueWorking, 0, queuePersistent);
			while (records.size() > 0)
			{
				RefundBalanceRecord record = records.poll();

				Message message = QueueFactory.createObjectMessage(connection.getSession(), record);

				producer.send(message);
				count++;
			}
		}
		catch (Exception ex)
		{
			debugMonitor(ex);
		}
		finally
		{
			QueueFactory.closeQueue(producer);
			returnMQConnection(connection);
		}

		debugMonitor("Sent total " + count + " record(s) to queue.");
	}

	private void loadFile()
	{
		records.clear();
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
				String[] recordContents = fileData[i].split(",");
				try
				{
					RefundBalanceRecord record = new RefundBalanceRecord(recordContents[0].trim(),
							recordContents[2].trim(),
							Double.parseDouble(recordContents[1].trim()));
					if (record.getIsdn().equals("") ||
							record.getAmount() == 0 ||
							record.getBalanceName().equals(""))
					{
						debugMonitor("Can not parse record: " + fileData[i]);
					}
					else
					{
						records.add(record);
						debugMonitor("Add records: " + record.toString());
					}
				}
				catch (Exception ex)
				{
					debugMonitor("Can not parse record: " + fileData[i]);
					debugMonitor(ex);
				}
			}

			debugMonitor("Added total " + records.size() + " record(s).");
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
