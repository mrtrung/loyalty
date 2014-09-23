package com.crm.provisioning.thread;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.comverse_in.prepaid.ccws.SubscriberEntity;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.*;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.impl.ccws.CCWSConnection;
import com.crm.provisioning.message.VNMMessage;
import com.fss.util.AppException;

public class SetBalanceThread extends DispatcherThread
{
	private class SetBalanceRecord
	{
		private String	isdn		= "";
		private long	productId	= 0;

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

		public SetBalanceRecord(String isdn, long productId)
		{
			setIsdn(isdn);
			setProductId(productId);
		}

		@Override
		public String toString()
		{
			return "[SetBalanceRecord: " + isdn + "," + productId + "]";
		}
	}

	private String					filePath	= "";

	private List<SetBalanceRecord>	records		= new ArrayList<SetBalanceRecord>();

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		// TODO Auto-generated method stub
		filePath = ThreadUtil.getString(this, "filePath", false, "");
		super.fillDispatcherParameter();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400, "Imported File Path."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
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
				SetBalanceRecord record = records.get(i);
				
				try
				{
					VNMMessage message = new VNMMessage();
					message.setIsdn(record.getIsdn());
					message.setProductId(record.getProductId());
					message.setChannel(Constants.CHANNEL_WEB);
					message.setActionType(Constants.ACTION_REGISTER);

					ProductEntry product = ProductFactory.getCache().getProduct(message.getProductId());

					ProductEntry useProduct = ProductFactory.getCache().getProduct("SET_" + product.getAlias());

					if (useProduct == null)
					{
						throw new AppException(Constants.ERROR_PRODUCT_NOT_FOUND + "-SET_" + product.getAlias());
					}
					message.setProductId(useProduct.getProductId());

					CommandEntry command = ProvisioningFactory.getCache().getCommand("CCWS.MODIFY_BALANCE");

					if (command == null)
						throw new AppException(Constants.ERROR_COMMAND_NOT_FOUND);

					message.setCommandId(command.getCommandId());
					message.setProvisioningType(command.getProvisioningType());
					message.setStatus(Constants.ORDER_STATUS_PENDING);
					QueueFactory.attachCommandRouting(message);

					debugMonitor("Sent " + message.getIsdn() + "-" + useProduct.getAlias() + " to command route.");
					count++;
				}
				catch (Exception e)
				{
					debugMonitor(e);
				}
			}
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}
		finally
		{
		}
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
					SetBalanceRecord record = new SetBalanceRecord(recordContents[0].trim(),
							Long.parseLong(recordContents[1].trim()));
					if (record.getIsdn().equals("") ||
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
