package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.crm.kernel.message.AlarmMessage;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.DateUtil;
import com.fss.util.AppException;

public class CommandStatisticThread extends DispatcherThread
{
	public int											updateDatabaseInterval	= 0;
	public String										hostToStatistic			= "";
	public int											warningCount			= 0;
	protected ConcurrentHashMap<Long, ProductStatistic>	chpProductStatistic		= null;
	protected Calendar									startTime				= null;

	@Override
	@SuppressWarnings(value = { "rawtypes", "unchecked" })
	public Vector getDispatcherDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createIntegerParameter("updateInterval", "Update database interval in seconds."));
		vtReturn.add(ThreadUtil.createTextParameter("host", 100, "Host to log statistic."));
		vtReturn.add(ThreadUtil.createIntegerParameter("warningCount", "Total failure command to send alarm."));
		vtReturn.addAll(super.getDispatcherDefinition());

		return vtReturn;
	}

	@Override
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();
			updateDatabaseInterval = ThreadUtil.getInt(this, "updateInterval", 3600);
			hostToStatistic = ThreadUtil.getString(this, "host", false, "");
			warningCount = ThreadUtil.getInt(this, "warningCount", 1000);
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

	@Override
	public void beforeProcessSession() throws Exception
	{
		try
		{
			super.beforeProcessSession();

			startTime = Calendar.getInstance();

			chpProductStatistic = new ConcurrentHashMap<Long, ProductStatistic>();
		}
		catch (Exception e)
		{
			mcnMain.close();
			throw e;
		}
	}

	@Override
	public void afterProcessSession() throws Exception
	{
		try
		{
			updateStatistic();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	@Override
	public void doProcessSession() throws Exception
	{
		while (isAvailable())
		{
			Calendar now = Calendar.getInstance();
			if (now.getTimeInMillis() - startTime.getTimeInMillis() > 1000 * updateDatabaseInterval)
			{
				updateStatistic();
			}
			else
			{
				ThreadUtil.sleep(this);
			}
		}
	}
	
	public void putStatisticMap(long productId, boolean isSuccess) throws Exception
	{
		synchronized (chpProductStatistic)
		{
			ProductStatistic productStatistic = null;

			productStatistic = chpProductStatistic.get(productId);
			
			if (productStatistic == null)
			{
				ProductEntry product = ProductFactory.getCache().getProduct(productId);
				
				if (product == null)
					return;
				productStatistic = new ProductStatistic();
				productStatistic.setAlias(product.getAlias());
				productStatistic.setProductId(productId);
			}
			
			if (isSuccess)
			{
				productStatistic.setSuccess(productStatistic.getSuccess() + 1);
			}
			else
			{
				productStatistic.setFailure(productStatistic.getFailure() + 1);
			}
			
			chpProductStatistic.put(productId, productStatistic);
		}
	}

	public void updateStatistic() throws Exception
	{
		Connection connection = null;
		PreparedStatement stmtStatistic = null;

		int failureCount = 0;
		String failureContent = "";

		try
		{
			connection = Database.getConnection();

			String SQL = "Insert into ProductStatistic " +
					"	(startDate,statisticDate,ServerRun,productId,alias,totalReqFailure,totalReqSuccess,totalReq) " +
					"Values " +
					"	(?, ?, ?, ?, ?, ?, ?, ?) ";

			stmtStatistic = connection.prepareStatement(SQL);

			synchronized (chpProductStatistic)
			{
				Set<Long> keys = chpProductStatistic.keySet();

				Iterator<Long> iterator = keys.iterator();
				Calendar now = Calendar.getInstance();
				while (iterator.hasNext())
				{
					Long productId = iterator.next();
					ProductStatistic productStatistic = chpProductStatistic.get(productId);
					if (productStatistic.getFailure() > 0)
					{
						failureCount += productStatistic.getFailure();
						failureContent += productStatistic.toString() + ";\r\n";
					}
					int total = productStatistic.getFailure() + productStatistic.getSuccess();

					if (total > 0)
					{
						stmtStatistic.setTimestamp(1, DateUtil.getTimestampSQL(productStatistic.getStartTime().getTime()));
						stmtStatistic.setTimestamp(2, DateUtil.getTimestampSQL(now.getTime()));
						stmtStatistic.setString(3, hostToStatistic);
						stmtStatistic.setLong(4, productId);

						stmtStatistic.setString(5, productStatistic.getAlias());
						stmtStatistic.setLong(6, productStatistic.getFailure());
						stmtStatistic.setLong(7, productStatistic.getSuccess());
						stmtStatistic.setLong(8, total);

						try
						{
							stmtStatistic.execute();
							debugMonitor("Update database " + productStatistic.toString());

							ProductEntry product = ProductFactory.getCache().getProduct(productId);

							productStatistic = new ProductStatistic();
							productStatistic.setAlias(product.getAlias());
							productStatistic.setProductId(productId);
							chpProductStatistic.put(productId, productStatistic);
						}
						catch (Exception e)
						{
							debugMonitor(e);
							debugMonitor("Fail to update database " + productStatistic.toString());
						}
					}
				}
			}
			startTime = Calendar.getInstance();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtStatistic);
			Database.closeObject(connection);

			if (failureCount >= warningCount)
			{
				AlarmMessage alarmMessage = new AlarmMessage();
				alarmMessage.setRequestTime(new Date());
				alarmMessage.setContent(failureContent);
				alarmMessage.setCause("failure-command");
				alarmMessage.setDescription("Too many failed request.");
				alarmMessage.setImmediately(true);
				// sendInstanceAlarm(alarmMessage);
				sendAlarmMessage(alarmMessage);
			}
		}
	}
}
