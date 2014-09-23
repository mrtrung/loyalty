package test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.crm.kernel.sql.Database;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.DateUtil;
import com.crm.util.StringUtil;
import com.fss.util.AppException;


public class CallableTest extends DispatcherThread
{
		Date toDay = new Date();

		CallableStatement clbStmt = null;
		
	    String content = "";
		long interestRecord		 = 0;
		long ioRecord 			=0;
		long aonetRecord		= 0;
		long usageRecord		= 0;
		long topupRecord		= 0;
		int timeWait 			= 1000;
		
		String strLogIONET = "";
		String strLogInterestrateMonthly	= "";
		String strLogAONET 					= "";
		String strLogUSAGE					= "";
		String strLogTOPUP 					= "";
		private String						timeToCheckForReport	= "12:00:00";
		private Calendar					lastReportTime			= null;
		
		Connection connection = null;
		
		protected CallableStatement stmtUpdateInterestrateMonthly = null;
		protected CallableStatement stmtUpdateIONET = null;
		protected CallableStatement stmtUpdateAONET = null;
		protected CallableStatement stmtUpdateUSAGE = null;
		protected CallableStatement stmtUpdateTOPUP = null;
		
		public void fillParameter() throws AppException
		{
			try
			{
				super.fillParameter();
				
				batchSize = ThreadUtil.getInt(this, "batchSize", 100);
				String lastFormatTime = ThreadUtil.getString(this, "lastReportTime", false, "");
				lastReportTime = Calendar.getInstance();
				timeWait = ThreadUtil.getInt(this, "timeWait", 100);

				if ("".equals(lastFormatTime))
				{
					lastReportTime.add(Calendar.DAY_OF_MONTH, -1);
				}
				else
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					try
					{
						Date lastReportDate = sdf.parse(lastFormatTime);

						lastReportTime.setTime(lastReportDate);
						lastReportTime.add(Calendar.DAY_OF_MONTH, 1);
					}
					catch (ParseException e)
					{
						throw new AppException("Last report time parsing error.");
					}
				}
				timeToCheckForReport = ThreadUtil.getString(this, "timeToCheckForReport", false, "070000");

			}
			catch (AppException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Vector getParameterDefinition()
		{
			Vector vtReturn = new Vector();
			vtReturn.addElement(ThreadUtil.createTextParameter("batchSize", 200, ""));
			vtReturn.addElement(ThreadUtil.createTextParameter("timeWait", 30000, ""));
			vtReturn.add(ThreadUtil
					.createTextParameter(
							"timeToCheckForReport",
							100,
							"Time to check for report in each day (Because they need time default 07:00:00)"));
			vtReturn.add(ThreadUtil.createTextParameter("lastReportTime", 100,
					"The last report time, format yyyyMMddHHmmss"));
			vtReturn.addElement(ThreadUtil.createTextParameter("telephones", 500, "Mail Telephones."));
			vtReturn.addAll(super.getParameterDefinition());
			return vtReturn;
		}

		public void beforeProcessSession() throws Exception 
		{
			super.beforeProcessSession();

			try
			{
				connection = Database.getConnection();
				
				stmtUpdateInterestrateMonthly = connection.prepareCall("{CALL ? := loyalty_package.interest_monthly(?, ?)}");
				stmtUpdateIONET = connection.prepareCall("{CALL ? := loyalty_package.inout_network(?, ?)}");
				stmtUpdateUSAGE = connection.prepareCall("{CALL ? := loyalty_package.collect_usage(?, ?)}");
				stmtUpdateAONET = connection.prepareCall("{CALL ? := loyalty_package.collect_age(?, ?)}");
				stmtUpdateTOPUP = connection.prepareCall("{CALL ? := loyalty_package.collect_topup(?, ?)}");
			}
			catch (Exception e)
			{
				throw e;
			}
		}
		
		public void afterProcessSession() throws Exception {
			try
			{
				Database.closeObject(stmtUpdateInterestrateMonthly);
				Database.closeObject(stmtUpdateIONET);
				Database.closeObject(stmtUpdateAONET);
				Database.closeObject(stmtUpdateUSAGE);
				Database.closeObject(stmtUpdateTOPUP);
				Database.closeObject(connection);
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
		
		public void doProcessSession() throws Exception{
			try
			{
				int i = 1;
				
				boolean flag = false;
				
				Calendar calChecking = Calendar.getInstance();

				while((flag == false) && (isAvailableDay(calChecking)))
				{
					try
					{
						logMonitor("Starting InterestrateMonthly...");
						stmtUpdateInterestrateMonthly.registerOutParameter(1,
								Types.DOUBLE);
						stmtUpdateInterestrateMonthly.setTimestamp(2,
								DateUtil.getTimestampSQL(toDay));
						stmtUpdateInterestrateMonthly.setFloat(3, 0.025F);
		
						stmtUpdateInterestrateMonthly.execute();
						interestRecord = stmtUpdateInterestrateMonthly.getInt(1);
		
						strLogInterestrateMonthly = "Updated "
								+ interestRecord
								+ " records InterestrateMonthly to SubscriberBalance is successfully!!!";
						logMonitor(strLogInterestrateMonthly);
						
						i++;
						
						if(i <= 3)
						{
							if(interestRecord == 0)
							{
								logMonitor("Number of Sleep: " + i);
								Thread.sleep(timeWait);
								continue;
							}
							else
							{
								flag = false;
								i = 0;
								break;
							}
						}
						else
						{
							i=0;
							flag = true;
							logMonitor("send Alarm");
							break;
						}
					}catch(Exception e1)
					{
						i++;
						
						logMonitor("Exception!");
						
						if(i <= 3)
						{
							if(interestRecord == 0)
							{
								logMonitor("Number of Sleep: " + i);
								Thread.sleep(timeWait);
								continue;
							}
							else
							{
								flag = false;
								i = 0;
								break;
							}
						}
						else
						{
							i=0;
							flag = true;
							logMonitor("send Alarm");
							throw e1;
						}
					}
				}
				while((flag == false) && (isAvailableDay(calChecking)))
				{
					try
					{
						logMonitor("Starting inout_network...");
						stmtUpdateIONET.registerOutParameter(1, Types.DOUBLE);
						stmtUpdateIONET.setTimestamp(2,
								DateUtil.getTimestampSQL(toDay));
						stmtUpdateIONET.setInt(3, 1);
			
						stmtUpdateIONET.execute();
						ioRecord = stmtUpdateIONET.getInt(1);
			
						strLogIONET = "Updated "
								+ ioRecord
								+ " records inout_network to SubscriberBalance is successfully!!!";
						logMonitor(strLogIONET);
						
					}catch(Exception e3)
					{
						i++;
						logMonitor("Exception!");
						
						if(i <= 3)
						{
							if(ioRecord == 0)
							{
								logMonitor("Number of Sleep: " + i);
								Thread.sleep(timeWait);
								continue;
							}
							else
							{
								flag = false;
								i = 0;
								break;
							}
						}
						else
						{
							i=0;
							flag = true;
							logMonitor("send Alarm");
							throw e3;
						}
					}
				}
				while((flag == false) && (isAvailableDay(calChecking)))
				{
					try
					{
					logMonitor("Starting collect_topup...");
					stmtUpdateTOPUP.registerOutParameter(1, Types.DOUBLE);
					stmtUpdateTOPUP.setTimestamp(2, DateUtil.getTimestampSQL(toDay));
					stmtUpdateTOPUP.setInt(3, 1000);
					
					stmtUpdateTOPUP.execute();
					topupRecord = stmtUpdateTOPUP.getInt(1);
					
					strLogTOPUP= "Updated " +topupRecord+ " collect_topup to SubscriberBalance is successfully!!!";
					logMonitor(strLogTOPUP);
					
					i++;
					
					if(i <= 3)
					{
						if(topupRecord == 0)
						{
							logMonitor("Number of Sleep: " + i);
							Thread.sleep(timeWait);
							continue;
						}
						else
						{
							flag = false;
							i = 0;
							break;
						}
					}
					else
					{
						i=0;
						flag = true;
						logMonitor("send Alarm");
						break;
					}
					
					}catch(Exception e2)
					{
						if(i <= 3)
						{
							if(topupRecord == 0)
							{
								logMonitor("Number of Sleep: " + i);
								Thread.sleep(timeWait);
								continue;
							}
							else
							{
								flag = false;
								i = 0;
								break;
							}
						}
						else
						{
							i=0;
							flag = true;
							logMonitor("send Alarm");
							throw e2;
						}
					}
				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
				logMonitor(e);
				
				if(strLogTOPUP.equals(""))
					strLogTOPUP= "Updated collect_topup to SubscriberBalance is not successfully!!!";
								
				Calendar checkTime = (Calendar) lastReportTime.clone();
				
				checkTime.add(Calendar.DAY_OF_MONTH, 1);
				String strCheckTime = StringUtil.format(checkTime.getTime(), "yyyyMMdd");
				strCheckTime = strCheckTime + timeToCheckForReport;

				Calendar now = Calendar.getInstance();
				String strNow = StringUtil.format(now.getTime(), "yyyyMMddHHmmss");
				
				if(isZeroRecord())
					
					logMonitor("Zero record");
				
				if((strNow.compareTo(strCheckTime) > 0))
				{
					logMonitor(strLogTOPUP);
//					lastReportTime.add(Calendar.DAY_OF_MONTH, 1);
					/**
					 * Stored last report time
					 */
					lastReportTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
					lastReportTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
					lastReportTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
					lastReportTime.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
					lastReportTime.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
					lastReportTime.set(Calendar.SECOND, now.get(Calendar.SECOND));

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					mprtParam.setProperty("lastReportTime", sdf.format(lastReportTime.getTime()));
					storeConfig();
				}
				else
					logMonitor("Invalid report time, skipping."
							+ " Next check time after " + strCheckTime + " (yyyyMMddHHmmss)");
				logMonitor(e.getMessage());
			}
			finally
			{
				logMonitor("finally...");
			}
		}

		//check in a day
		public boolean isAvailableDay(Calendar calChecking)
		{
			  Calendar cal = Calendar.getInstance();
			  
			  cal.set(cal.get(Calendar.YEAR),
					  cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			  
			  calChecking.set(calChecking.get(Calendar.YEAR),
					  calChecking.get(Calendar.MONTH), calChecking.get(Calendar.DAY_OF_MONTH));
			  
			  if(cal.get(Calendar.DAY_OF_MONTH) > calChecking.get(Calendar.DAY_OF_MONTH))
			  {
				  return false;
			  }
			  else
				  return true;
		}
		
		public boolean isZeroRecord()
		{
			if((interestRecord == 0) || (topupRecord == 0) || (ioRecord == 0))
				return true;
			else
				return false;
		}
}
