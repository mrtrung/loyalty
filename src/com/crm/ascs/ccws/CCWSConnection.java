package com.crm.ascs.ccws;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

import com.comverse_in.prepaid.ccws.ArrayOfRechargeHistory;
import com.comverse_in.prepaid.ccws.RechargeHistory;
import com.comverse_in.prepaid.ccws.ServiceLocator;
import com.comverse_in.prepaid.ccws.ServiceSoapStub;
import com.comverse_in.prepaid.ccws.SubscriberCreate;
import com.comverse_in.prepaid.ccws.SubscriberMainBase;
import com.comverse_in.prepaid.ccws.SubscriberModify;
import com.comverse_in.prepaid.ccws.SubscriberPPS;
import com.comverse_in.prepaid.ccws.SubscriberRetrieve;
import com.crm.ascs.net.Trigger;
import com.crm.ascs.net.TriggerActivation;
import com.crm.ascs.net.TriggerRecharge;
import com.crm.ascs.util.PoolableObject;
import com.crm.provisioning.impl.ccws.PasswordCallback;
import com.crm.thread.DispatcherThread;

public class CCWSConnection implements PoolableObject
{
	public ServiceSoapStub		binding		= null;

	private DispatcherThread	dispatcher	= null;
	private String				host		= "";
	private String				username	= "";
	private String				password	= "";
	private long				timeout		= 60000;

	public DispatcherThread getDispatcher()
	{
		return dispatcher;
	}

	public void setDispatcher(DispatcherThread dispatcher)
	{
		this.dispatcher = dispatcher;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public long getTimeout()
	{
		return timeout;
	}

	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}

	public CCWSConnection()
	{
	}

	public boolean open() throws Exception
	{
		if (getHost().equals(""))
		{
			throw new NullPointerException("CCWS URL is not valid");
		}

		try
		{
			try
			{
				java.net.URL endpoint = new java.net.URL(getHost());

				EngineConfiguration configuration = new FileProvider("client-config-ccws.wsdd");
				ServiceLocator locator = new ServiceLocator(configuration);

				binding = (ServiceSoapStub) locator.getServiceSoap(endpoint);
				binding._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
				binding._setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
				binding._setProperty(WSHandlerConstants.USER, username);
				PasswordCallback pwCallback = new PasswordCallback(password);

				binding._setProperty(WSHandlerConstants.PW_CALLBACK_REF, pwCallback);
				binding.setTimeout((int) timeout);
			}
			catch (javax.xml.rpc.ServiceException jre)
			{
				if (jre.getLinkedCause() != null)
				{
					jre.getLinkedCause().printStackTrace();
				}
				throw new Exception("JAX-RPC ServiceException caught: " + jre);
			}

			if (binding == null)
			{
				throw new Exception("Binding is null");
			}
		}
		catch (Exception ex)
		{
			binding = null;

			throw ex;
		}

		return true;
	}

	public boolean close()
	{
		return true;
	}

	@Override
	public void activate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void passivate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validate()
	{
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * CCWS Methods
	 */

	/**
	 * 
	 */
	public SubscriberRetrieve getSubscriberWithNoHistory(String isdn, int queryLevel) throws RemoteException
	{
		/**
		 * 
		 */
		return binding.retrieveSubscriberWithIdentityNoHistory(isdn, null, queryLevel);
	}

	/**
	 * Get sub info with history.
	 * 
	 * @param isdn
	 * @param queryLevel
	 *            level of info<br />
	 *            1 // SubscriberEntity<br />
	 *            + 4 // SubscriberInfo<br />
	 *            + 128 // MonetaryTransactionRecord<br />
	 *            + 256 // ActivityHistory<br />
	 *            + 512 // RechargeHistory<br />
	 *            + 1024 // PSTransaction<br />
	 *            + 2048 // OSAHistory<br />
	 *            + 32768; // Diameter OCSHistory<br />
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws RemoteException
	 */
	public SubscriberRetrieve getSubscriberWithHistory(String isdn, int queryLevel, Calendar startDate, Calendar endDate)
			throws RemoteException
	{
		return binding.retrieveSubscriberWithIdentityWithHistoryForMultipleIdentities(isdn, null, queryLevel,
				startDate, endDate, false);
	}

	public boolean createSubscriber(String isdn, String cosName, String spName) throws Exception
	{
		SubscriberCreate subCreate = new SubscriberCreate();
		subCreate.setSubscriberID(isdn);

		SubscriberMainBase subMain = new SubscriberMainBase();
		subMain.setCOSName(cosName);
		subCreate.setSubscriber(subMain);
		subCreate.setSPName(spName);

		return binding.createSubscriber(subCreate);
	}

	public boolean deleteSubscriber(String isdn) throws Exception
	{
		return binding.deleteSubscriber(isdn, "");
	}

	public boolean changeSubscriberState(String isdn, String state) throws Exception
	{
		SubscriberModify subModify = new SubscriberModify();
		subModify.setSubscriberID(isdn);

		SubscriberPPS subPPS = new SubscriberPPS();
		subPPS.setCurrentState(state);
		subPPS.setSubscriberDateEnterActive(Calendar.getInstance());

		subModify.setSubscriber(subPPS);

		return binding.modifySubscriber(subModify);
	}

	public boolean topup(String isdn, double value, int days, String comment) throws Exception
	{
		return binding.nonVoucherRecharge(isdn, null, new Double(value), days, comment);
	}

	public int getSubInfo(TriggerActivation trigger) throws Exception
	{
		try
		{
			int queryLevel = 1;
			SubscriberRetrieve subRetrieve = getSubscriberWithNoHistory(trigger.getIsdn(), queryLevel);

			if (subRetrieve != null)
			{
				trigger.setActivationDate(subRetrieve.getSubscriberData().getDateEnterActive().getTime());

				return Trigger.STATUS_APPROVED;
			}

			return Trigger.STATUS_FAILURE;
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public int getRechargeHistory(TriggerRecharge trigger, Calendar startDate, Calendar endDate) throws Exception
	{
		try
		{
			int queryLevel = 1 + 512;
			SubscriberRetrieve subRetrieve = getSubscriberWithHistory(trigger.getIsdn(), queryLevel, startDate, endDate);

			if (subRetrieve != null)
			{
				trigger.setActivationDate(subRetrieve.getSubscriberData().getDateEnterActive().getTime());
				trigger.setPreviousState(subRetrieve.getSubscriberData().getPreviousState());

				ArrayOfRechargeHistory aofRechargeHistories = subRetrieve.getRechargeHistories();
				if (aofRechargeHistories != null)
				{

					RechargeHistory[] rechargeHistories = aofRechargeHistories.getRechargeHistory();
					if (rechargeHistories == null)
						return Trigger.STATUS_FAILURE;

					RechargeHistory rechargeHistory = null;
					
					/**
					 * Get by batch and serial (if exist)
					 */
					for (int i = 0; i < rechargeHistories.length && trigger.getSerial() > 0; i++)
					{
						if (rechargeHistories[i].getSerialNumber() == trigger.getSerial())
						{
							if (trigger.getBatch() > 0 && rechargeHistories[i].getBatchNumber() == trigger.getBatch())
							{
								rechargeHistory = rechargeHistories[i];
								break;
							}
							else if (trigger.getBatch() == 0)
							{
								rechargeHistory = rechargeHistories[i];
								break;
							}
						}
					}

					/**
					 * Get last by faceValue
					 */
					if (rechargeHistory == null && rechargeHistories.length > 0)
					{
						Arrays.sort(rechargeHistories, new RechargeHistoryComparator());
						int i = rechargeHistories.length - 1;
						while (i >= 0)
						{
							if (rechargeHistories[i].getFaceValue() == trigger.getFaceValue())
							{
								rechargeHistory = rechargeHistories[i];
								break;
							}
							i--;
						}
					}

					if (rechargeHistory != null)
					{
						trigger.setRechargeDate(rechargeHistory.getRechargeDate().getTime());
						return Trigger.STATUS_APPROVED;
					}
					else
					{
						return Trigger.STATUS_FAILURE;
					}
				}
			}

			return Trigger.STATUS_FAILURE;
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	private class RechargeHistoryComparator implements Comparator<RechargeHistory>
	{

		@Override
		public int compare(RechargeHistory o1, RechargeHistory o2)
		{
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;

			try
			{
				if (o1.getRechargeDate().before(o2.getRechargeDate()))
					return -1;
				if (o1.getRechargeDate().after(o2.getRechargeDate()))
					return 1;
				return 0;
			}
			catch (Exception e)
			{
				return 0;
			}
		}

	}
}
