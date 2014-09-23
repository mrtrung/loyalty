package com.crm.product.thread.autorenew.warning;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.ResponseUtil;
import com.fss.thread.ThreadConstant;
import com.fss.util.StringUtil;

public abstract class AbtractAutoRenewWarning implements Runnable
{
	// WARNING PARAMS
	protected String					isdns;
	protected String					shortCode;
	protected String					emailContent;
	protected String					smsContent;
	protected String					sQLWarning;
	protected String					subject;
	protected String					toEmails;
	protected String					host;
	protected String					fromEmail;
	protected String					password;
	protected Vector					vtWarning		= new Vector();
	protected String					checkWarning	= "0";
	protected String[]					schedules;
	protected int						oddsTime;

	// GLOBAL PARAMS
	protected AutoRenewWarningThread	warningThread;
	boolean[]							bChecks			= null;
	Calendar[]							calSchedules	= null;
	protected long						yesterdayTotalRecords = 0;

	public long getYesterdayTotalRecords() {
		return yesterdayTotalRecords;
	}

	public void setYesterdayTotalRecords(long yesterdayTotalRecords) {
		this.yesterdayTotalRecords = yesterdayTotalRecords;
	}

	public String getShortCode()
	{
		return shortCode;
	}

	public void setShortCode(String shortCode)
	{
		this.shortCode = shortCode;
	}

	public String getEmailContent()
	{
		return emailContent;
	}

	public void setEmailContent(String emailContent)
	{
		this.emailContent = emailContent;
	}

	public String getSmsContent()
	{
		return smsContent;
	}

	public void setSmsContent(String smsContent)
	{
		this.smsContent = smsContent;
	}

	public String getsQLWarning()
	{
		return sQLWarning;
	}

	public void setsQLWarning(String sQLWarning)
	{
		this.sQLWarning = sQLWarning;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getFromEmail()
	{
		return fromEmail;
	}

	public void setFromEmail(String fromEmail)
	{
		this.fromEmail = fromEmail;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Vector getVtWarning()
	{
		return vtWarning;
	}

	public void setVtWarning(Vector vtWarning)
	{
		this.vtWarning = vtWarning;
	}

	public String getCheckWarning()
	{
		return checkWarning;
	}

	public void setCheckWarning(String checkWarning)
	{
		this.checkWarning = checkWarning;
	}

	public AutoRenewWarningThread getWarningThread()
	{
		return warningThread;
	}

	public void setWarningThread(AutoRenewWarningThread warningThread)
	{
		this.warningThread = warningThread;
	}

	public synchronized void logMonitor(Exception ex)
	{
		warningThread.logMonitor(ex.getMessage());
	}

	public synchronized void logMonitor(String strLog)
	{
		warningThread.logMonitor(strLog);
	}

	public String getIsdns()
	{
		return isdns;
	}

	public void setIsdns(String isdns)
	{
		this.isdns = isdns;
	}

	public String getToEmails()
	{
		return toEmails;
	}

	public void setToEmails(String toEmails)
	{
		this.toEmails = toEmails;
	}

	public String[] getSchedules()
	{
		return schedules;
	}

	public void setSchedules(String[] schedules)
	{
		this.schedules = schedules;
	}

	public boolean[] getbChecks()
	{
		return bChecks;
	}

	public void setbChecks(boolean[] bChecks)
	{
		this.bChecks = bChecks;
	}

	public Calendar[] getCalSchedules()
	{
		return calSchedules;
	}

	public void setCalSchedules(Calendar[] calSchedules)
	{
		this.calSchedules = calSchedules;
	}

	public int getOddsTime()
	{
		return oddsTime;
	}

	public void setOddsTime(int oddsTime)
	{
		this.oddsTime = oddsTime;
	}

	public String formatNumber(long number) throws Exception
	{
		NumberFormat numberFormat = new DecimalFormat("###,###,###");
		return numberFormat.format(number);
	}

	public abstract void buildContent(String[] argContent) throws Exception;

	public void sendEmail(String emailContent) throws Exception
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props, new Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try
		{
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress(fromEmail));
			String[] argToEmail = StringUtil.toStringArray(toEmails, ";");
			InternetAddress[] internetAddresses = new InternetAddress[argToEmail.length];
			for (int i = 0; i < argToEmail.length; i++)
			{
				internetAddresses[i] = new InternetAddress(argToEmail[i]);
			}
			mimeMessage.addRecipients(Message.RecipientType.TO, internetAddresses);
			mimeMessage.setSubject(subject);

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(emailContent, "text/html; charset=utf-8");

			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			mimeMessage.setContent(multipart);

			Transport.send(mimeMessage);
		}
		catch (Exception ex)
		{
			logMonitor(ex);
		}
	}

	public void sendSMS(String smsContent) throws Exception
	{
		CommandMessage commandMessage = new CommandMessage();
		commandMessage.setServiceAddress(shortCode);
		commandMessage.setProvisioningType(Constants.PROVISIONING_SMSC);

		CommandEntry command = ProvisioningFactory.getCache().getCommand(Constants.COMMAND_SEND_SMS);
		commandMessage.setCommandId(command.getCommandId());

		commandMessage.setRequest(smsContent);
		commandMessage.setRequestValue(ResponseUtil.SMS_CMD_CHECK, "false");
		commandMessage.setTimeout(120000);

		String[] argISDN = StringUtil.toStringArray(isdns, ";");
		for (String isdn : argISDN)
		{
			commandMessage.setIsdn(isdn);
			QueueFactory.attachCommandRouting(commandMessage);
		}
	}

	public void initParams()
	{
		bChecks = new boolean[schedules.length];
		calSchedules = new Calendar[schedules.length];
		for (int i = 0; i < schedules.length; i++)
		{
			bChecks[i] = false;

			String[] argTime = StringUtil.toStringArray(schedules[i], ":");
			String strHour = argTime[0];
			String strMinutes = argTime[1];
			String strSecond = argTime[2];

			calSchedules[i] = Calendar.getInstance();
			calSchedules[i].set(Calendar.HOUR_OF_DAY, Integer.parseInt(strHour));
			calSchedules[i].set(Calendar.MINUTE, Integer.parseInt(strMinutes));
			calSchedules[i].set(Calendar.SECOND, Integer.parseInt(strSecond));
		}
	}

	public boolean checkSchedule()
	{
		Calendar now = Calendar.getInstance();

		// Thoi diem hien tai
		long lNow = now.getTime().getTime();
		// Thoi diem dau
		long lTest0 = calSchedules[0].getTime().getTime();
		// Thoi diem cuoi
		long lTest3 = calSchedules[schedules.length - 1].getTime().getTime();

		if (lNow < lTest0 || lNow > lTest3 + oddsTime * 60000)
		{
			return false;
		}
		else
		{
			for (int i = 0; i < schedules.length; i++)
			{
				if (i == schedules.length - 1)
				{
					long lTest2 = calSchedules[i].getTime().getTime();

					if (lNow >= lTest2 && lNow < lTest2 + oddsTime * 60000 && bChecks[i] == false)
					{
						bChecks[i] = true;
						for (int j = 0; j < bChecks.length; j++)
						{
							if (j != i)
							{
								bChecks[j] = false;
							}
						}
						return true;
					}
				}
				else
				{
					// Thoi diem hai dau mut
					long lTest1 = calSchedules[i].getTime().getTime();
					long lTest2 = calSchedules[i + 1].getTime().getTime();

					if (lNow >= lTest1 && lNow < lTest2 && lNow < lNow + oddsTime * 60000 && bChecks[i] == false)
					{
						bChecks[i] = true;
						for (int j = 0; j < bChecks.length; j++)
						{
							if (j != i)
							{
								bChecks[j] = false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public void warning() throws Exception
	{
		if (checkWarning.equals("1") && checkSchedule())
		{
			String strEmailContent = this.emailContent;
			String strSMSContent = this.smsContent;
			String[] argContent = new String[2];
			argContent[0] = strEmailContent;
			argContent[1] = strSMSContent;

			buildContent(argContent);
			sendEmail(argContent[0]);
			sendSMS(argContent[1]);

			logMonitor("Send email warning to " + toEmails);
			logMonitor("Send SMS warning to " + isdns);
		}
	}

	@Override
	public void run()
	{
		try
		{
			while (warningThread.runElementThread)
			{
				warning();
				Thread.sleep(1000L);
			}
		}
		catch (Exception ex)
		{
			logMonitor(ex);
		}

	}
}
