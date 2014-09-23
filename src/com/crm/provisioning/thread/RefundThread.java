package com.crm.provisioning.thread;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;

import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class RefundThread extends DispatcherThread
{
	private class RefundRecord
	{
		private String	isdn		= "";
		private long	productId	= 0;
		private double	amount		= 0;

		public RefundRecord(String isdn, long productId, double amount)
		{
			setIsdn(isdn);
			setProductId(productId);
			setAmount(amount);
		}

		public String getIsdn()
		{
			return isdn;
		}

		public void setIsdn(String isdn)
		{
			this.isdn = isdn;
		}

		public long getProductId()
		{
			return productId;
		}

		public void setProductId(long productId)
		{
			this.productId = productId;
		}

		public double getAmount()
		{
			return amount;
		}

		public void setAmount(double amount)
		{
			this.amount = amount;
		}

		@Override
		public String toString()
		{
			return "[RefundRecord: " + isdn + "," + productId + "," + amount + "]";
		}
	}

	public String				filePath	= "";
	public String 				commandAlias = "";
	private List<RefundRecord>	records		= new ArrayList<RefundThread.RefundRecord>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400, "Imported File Path."));
		vtReturn.add(ThreadUtil.createTextParameter("commandAlias", 400, "Alias of osa credit command."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		filePath = ThreadUtil.getString(this, "filePath", false, "");
		commandAlias = ThreadUtil.getString(this, "commandAlias", false, "");
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

	@Override
	public void doProcessSession() throws Exception
	{
		if (records.size() == 0)
			return;

		int count = 0;
		try
		{
			for (int i = 0; i < records.size(); i++)
			{
				RefundRecord record = records.get(i);
				CommandMessage message = new CommandMessage();
				message.setIsdn(record.getIsdn());
				message.setActionType(Constants.ACTION_ROLLBACK);
				message.setProductId(record.getProductId());
				message.setAmount(record.getAmount());
				message.setPaid(true);
				
				CommandEntry command = ProvisioningFactory.getCache().getCommand(commandAlias);
				if (command == null)
					throw new AppException(Constants.ERROR_COMMAND_NOT_FOUND);
				
				message.setCommandId(command.getCommandId());
				message.setProvisioningType(command.getProvisioningType());
				message.setStatus(Constants.ORDER_STATUS_PENDING);
				
				QueueFactory.attachCommandRouting(message);
				
				debugMonitor("Sent " + record.toString() + " to command route.");
				count++;
			}
		}
		catch (Exception ex)
		{
			debugMonitor(ex);
		}
		
		debugMonitor("Sent total " + records.size() + " record(s) to command route.");
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
					RefundRecord record = new RefundRecord(recordContents[0].trim(),
							Long.parseLong(recordContents[1].trim()),
							Double.parseDouble(recordContents[2].trim()));
					if (record.getIsdn().equals("") ||
							record.getAmount() == 0 ||
							record.getProductId() == Constants.DEFAULT_ID)
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
