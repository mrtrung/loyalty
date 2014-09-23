package com.crm.provisioning.thread;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.thread.MailThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.DateUtil;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class UpdateDBDaily extends MailThread
{

	int c_Loop = 0;

	protected CallableStatement stmtUpdateInterestrateMonthly = null;
	protected CallableStatement stmtUpdateIONET = null;
	protected CallableStatement stmtUpdateAONET = null;
	protected CallableStatement stmtUpdateUSAGE = null;
	protected CallableStatement stmtUpdateTOPUP = null;

	String strLogInterestrateMonthly = "";
	String strLogIONET = "";
	String strLogAONET = "";
	String strLogUSAGE = "";
	String strLogTOPUP = "";

	int timeWait = 0;
	private String telephones = "";

	private Connection connection = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.addElement(ThreadUtil.createTextParameter("batchSize", 200, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("c_Loop", 3, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("timeWait", 600000, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("telephones", 500, "Mail Telephones."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();
			batchSize = ThreadUtil.getInt(this, "batchSize", 100);
			c_Loop = ThreadUtil.getInt(this, "c_Loop", 3);
			timeWait = ThreadUtil.getInt(this, "timeWait", 30000);
			telephones = ThreadUtil.getString(this, "telephones", false, "");

		} catch (AppException e)
		{
			throw e;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		try
		{
			logMonitor("Starting before ProcessSession");

			connection = Database.getConnection();

			connection.setAutoCommit(false);

			stmtUpdateInterestrateMonthly = connection.prepareCall("{CALL ? := loyalty_package.interest_monthly(?, ?)}");
			stmtUpdateIONET = connection.prepareCall("{CALL ? := loyalty_package.inout_network(?, ?)}");
			stmtUpdateAONET = connection.prepareCall("{CALL ? := loyalty_package.collect_age(?, ?)}");
			stmtUpdateUSAGE = connection.prepareCall("{CALL ? := loyalty_package.collect_usage(?, ?)}");
			stmtUpdateTOPUP = connection.prepareCall("{CALL ? := loyalty_package.collect_topup(?, ?)}");

		} catch (Exception ex)
		{
			logMonitor(ex);
		}
	}

	public void doProcessSession() throws Exception
	{
		try
		{
			long interestRecord = 0;
			long ioRecord = 0;
			long aonetRecord = 0;
			long usageRecord = 0;
			long topupRecord = 0;

			// InterestrateMonthly
			Date toDay = new Date();

			Calendar calChecking = Calendar.getInstance();

			int i = 1;

			boolean flag = false;

			while ((flag == false) && isAvailableDay(calChecking))
			{
				try
				{
					logMonitor("Starting InterestrateMonthly...");
					stmtUpdateInterestrateMonthly.registerOutParameter(1, Types.DOUBLE);
					stmtUpdateInterestrateMonthly.setTimestamp(2, DateUtil.getTimestampSQL(toDay));
					stmtUpdateInterestrateMonthly.setFloat(3, 0.025F);

					stmtUpdateInterestrateMonthly.execute();
					interestRecord = stmtUpdateInterestrateMonthly.getInt(1);

					strLogInterestrateMonthly = "Updated " + interestRecord + " records InterestrateMonthly to SubscriberBalance is successfully!!!";

					logMonitor(strLogInterestrateMonthly);

					if (interestRecord == 0)
					{
						i = 0;
						break;
					}

				} catch (Exception e1)
				{
					logMonitor(e1.getStackTrace());
				}
			}

			// inout network
			while ((flag == false) && isAvailableDay(calChecking))
			{
				try
				{
					logMonitor("Starting inout_network...");
					stmtUpdateIONET.registerOutParameter(1, Types.DOUBLE);
					stmtUpdateIONET.setTimestamp(2, DateUtil.getTimestampSQL(toDay));
					stmtUpdateIONET.setInt(3, 1);

					stmtUpdateIONET.execute();
					ioRecord = stmtUpdateIONET.getInt(1);

					strLogIONET = "Updated " + ioRecord + " records inout_network to SubscriberBalance is successfully!!!";
					logMonitor(strLogIONET);

					i++;

					if (i <= c_Loop)
					{
						if (ioRecord == 0)
						{
							logMonitor("Number of Sleep: " + i);
							Thread.sleep(timeWait);
							continue;
						} else
						{
							flag = false;
							i = 0;
							break;
						}
					} else
					{
						flag = false;
						i = 0;
						break;
					}

				} catch (Exception e2)
				{
					logMonitor(e2.getStackTrace());
				}
			}

			// usage
			while ((flag == false) && isAvailableDay(calChecking))
			{
				try
				{
					logMonitor("Starting collect_usage...");
					stmtUpdateUSAGE.registerOutParameter(1, Types.DOUBLE);
					stmtUpdateUSAGE.setTimestamp(2, DateUtil.getTimestampSQL(toDay));
					stmtUpdateUSAGE.setInt(3, 1000);

					stmtUpdateUSAGE.execute();
					usageRecord = stmtUpdateUSAGE.getInt(1);

					strLogUSAGE = "Updated " + usageRecord + " collect_usage to SubscriberBalance is successfully!!!";
					logMonitor(strLogUSAGE);

					i++;

					if (i <= c_Loop)
					{
						if (usageRecord == 0)
						{
							logMonitor("Number of Sleep: " + i);
							Thread.sleep(timeWait);
							continue;
						} else
						{
							flag = false;
							i = 0;
							break;
						}
					} else
					{
						i = 0;
						flag = true;
						sendAlarm();
						break;
					}
				} catch (Exception e3)
				{
					logMonitor(e3.getStackTrace());
				}
			}

			// age of network
			while ((flag == false) && isAvailableDay(calChecking))
			{
				try
				{
					logMonitor("Starting collect_age...");
					stmtUpdateAONET.registerOutParameter(1, Types.DOUBLE);
					stmtUpdateAONET.setTimestamp(2, DateUtil.getTimestampSQL(toDay));
					stmtUpdateAONET.setInt(3, 20);

					stmtUpdateAONET.execute();
					aonetRecord = stmtUpdateAONET.getInt(1);

					strLogAONET = "Updated " + aonetRecord + " records collect_age to SubscriberBalance is successfully!!!";
					logMonitor(strLogAONET);

					if (aonetRecord == 0)
					{
						i = 0;
						break;
					}
				} catch (Exception e4)
				{
					logMonitor(e4.getStackTrace());
				}
			}

			// topup
			while ((flag == false) && isAvailableDay(calChecking))
			{
				try
				{
					logMonitor("Starting collect_topup...");
					stmtUpdateTOPUP.registerOutParameter(1, Types.DOUBLE);
					stmtUpdateTOPUP.setTimestamp(2, DateUtil.getTimestampSQL(toDay));
					stmtUpdateTOPUP.setInt(3, 1000);

					stmtUpdateTOPUP.execute();
					topupRecord = stmtUpdateTOPUP.getInt(1);

					strLogTOPUP = "Updated " + topupRecord + " records collect_topup to SubscriberBalance is successfully!!!";
					logMonitor(strLogTOPUP);

					i++;

					if (i <= c_Loop)
					{
						if (topupRecord == 0)
						{
							logMonitor("Number of Sleep: " + i);
							Thread.sleep(timeWait);
							continue;
						} else
						{
							flag = true;
							i = 0;
							break;
						}
					} else
					{
						i = 0;
						flag = true;
						sendAlarm();
						break;
					}
				} catch (Exception e5)
				{
					logMonitor(e5.getStackTrace());
				}
			}

			connection.setAutoCommit(true);

			sendAlarm();

		} catch (Exception exc)
		{

			connection.rollback();

			logMonitor("System's rollback");

			sendAlarm();
		} finally
		{
			logMonitor("System's closed in day !");
		}

	}

	public void afterProcessSession() throws Exception
	{
		try
		{
			Database.closeObject(stmtUpdateInterestrateMonthly);
			Database.closeObject(stmtUpdateIONET);
			Database.closeObject(stmtUpdateAONET);
			Database.closeObject(stmtUpdateUSAGE);
			Database.closeObject(stmtUpdateTOPUP);
			Database.closeObject(connection);
		} catch (Exception excep)
		{
			logMonitor(excep + " after ProcessSession");
		} finally
		{
			super.afterProcessSession();
		}
	}

	public CommandMessage pushOrder(String isdn, String serviceAddress, String content) throws Exception
	{
		CommandMessage order = new CommandMessage();

		try
		{
			CommandEntry command = ProvisioningFactory.getCache().getCommand(Constants.COMMAND_SEND_SMS);
			order.setProvisioningType(Constants.PROVISIONING_SMSC);
			order.setCommandId(command.getCommandId());
			order.setServiceAddress(serviceAddress);
			order.setIsdn(isdn);
			order.setRequest(content);
			order.setRequestValue(ResponseUtil.SMS_CMD_CHECK, "false");

		} catch (Exception e)
		{
			throw e;
		}
		return order;
	}

	// check in a day
	public boolean isAvailableDay(Calendar calChecking)
	{
		Calendar cal = Calendar.getInstance();

		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

		calChecking.set(calChecking.get(Calendar.YEAR), calChecking.get(Calendar.MONTH), calChecking.get(Calendar.DAY_OF_MONTH));

		if (cal.get(Calendar.DAY_OF_MONTH) > calChecking.get(Calendar.DAY_OF_MONTH))
		{
			return false;
		} else
			return true;
	}

	public void sendAlarm()
	{
		try
		{
			String strContent = "";

			if (strLogInterestrateMonthly.equals("")) strLogInterestrateMonthly = "Updated InterestrateMonthly to is not successfully!!!";
			if (strLogIONET.equals("")) strLogIONET = "Updated inout_network to is not successfully!!!";
			if (strLogUSAGE.equals("")) strLogUSAGE = "Updated collect_usage to is not successfully!!!";
			if (strLogAONET.equals("")) strLogAONET = "Updated collect_age to is not successfully!!!";
			if (strLogTOPUP.equals("")) strLogTOPUP = "Updated collect_topup is not successfully!!!";

			String[] strTelephoneList = StringUtil.toStringArray(telephones, ";");

			strContent = strLogInterestrateMonthly + " ; " + strLogIONET + " ; " + strLogUSAGE + " ; " + strLogAONET + " ; " + strLogTOPUP;

			try
			{
				for (int j = 0; j < strTelephoneList.length; j++)
				{
					// send SMS
					logMonitor("Starting send SMS...");

					CommandMessage order = null;
					String isdn = "";
					String shotCode = "";
					String content = "";

					isdn = strTelephoneList[j];
					shotCode = "268";
					content = strContent;

					order = pushOrder(isdn, shotCode, content);
					QueueFactory.attachCommandRouting(order);

					debugMonitor("Isdn: " + isdn + ", SC: " + shotCode + ", Content: " + content);

					logMonitor("Phone Alarm was sent successfully");
				}
			} catch (Exception eb)
			{
				logMonitor(eb + " Error send Alarm SMS.");
			}

			/**
			 * Stored last report time
			 */
		} catch (Exception exce)
		{
			logMonitor(exce.getStackTrace());
		}
	}
}
