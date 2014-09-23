package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.crm.kernel.domain.DomainFactory;
import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.product.cache.ProductMessage;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;
import com.fss.util.StringUtil;

public class NotificationThread extends DispatcherThread
{
	private PreparedStatement	stmtSubscription		= null;
	private PreparedStatement	stmtSubscriptionUpdate	= null;
	private Connection _conn = null;
	private String				sqlSubscription			= "";
	private String				channel					= "";
	private String				cause					= "success";
	private String				actionType				= "notify";

	private String				lastRunDate				= "";

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(ThreadUtil.createTextParameter("SQLSubscription", 400, "SQL query to get subscription."));

		vtReturn.addElement(ThreadUtil.createTextParameter("LastRunDate", 400, "SQL query to get subscription."));

		vtReturn.addElement(ThreadUtil.createTextParameter("channel", 400, "Subscription channel \"web\" or \"SMS\"."));

		vtReturn.addElement(ThreadUtil.createTextParameter("cause", 400, "Cause of request"));

		vtReturn.addElement(ThreadUtil.createTextParameter("actionType", 400, "ActionType of request"));

		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	public void fillParameter() throws AppException
	{
		try
		{
			super.fillParameter();

			sqlSubscription = loadMandatory("SQLSubscription");
			setLastRunDate(loadMandatory("LastRunDate"));

			channel = ThreadUtil.getString(this, "channel", false, "web");
			cause = ThreadUtil.getString(this, "cause", false, "success");
			actionType = ThreadUtil.getString(this, "actionType", false, "notify");
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

	// //////////////////////////////////////////////////////
	// after process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		try
		{
			neverExpire = false;
			
			String strSQL = sqlSubscription;
			_conn = Database.getConnection();
			_conn.setAutoCommit(false);

			stmtSubscription = _conn.prepareStatement(strSQL);

			strSQL = "Update SubscriberProduct Set lastRunDate = SysDate Where subProductId = ?";
			stmtSubscriptionUpdate = _conn.prepareStatement(strSQL);
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// after process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void afterProcessSession() throws Exception
	{
		try
		{
			Database.closeObject(stmtSubscription);
			Database.closeObject(stmtSubscriptionUpdate);
			Database.closeObject(_conn);
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

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void doProcessSession() throws Exception
	{
		long counter = 0;

		int batchCounter = 0;

		ResultSet rsQueue = null;

		try
		{
			rsQueue = stmtSubscription.executeQuery();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			while (rsQueue.next() && isAvailable())
			{
				long subProductId = rsQueue.getLong("subProductId");
				String isdn = StringUtil.nvl(rsQueue.getString("isdn"), "");
				long productId = rsQueue.getLong("productid");
				int subscriberType = rsQueue.getInt("subscriberType");

				Date expirationDate = rsQueue.getDate("expirationDate");

				String expireDate = sdf.format(expirationDate);

				ProductEntry product = ProductFactory.getCache().getProduct(rsQueue.getLong("productid"));

				if (product == null)
					throw new Exception("Unknow product ID: " + productId);

				CommandEntry command = ProvisioningFactory.getCache().getCommand(Constants.COMMAND_SEND_SMS);

				CommandMessage request = new CommandMessage();

				request.setSubProductId(subProductId);
				request.setProductId(productId);
				request.setIsdn(isdn);
				request.setSubscriberType(subscriberType);
				request.setActionType(actionType);
				request.setChannel(channel);
				request.setCause(cause);
				request.setServiceAddress(product.getAlias());
				request.setKeyword("NOTIFY_" + product.getAlias());
				request.setStatus(Constants.ORDER_STATUS_PENDING);

				request.setCommandId(command.getCommandId());
				request.setProvisioningType(Constants.PROVISIONING_SMSC);

				request.setResponseValue(ResponseUtil.SERVICE_EXPIRE_DATE, expireDate);

				getMessageContent(request, product);
				
				request.setRequestValue(ResponseUtil.SMS_CMD_CHECK, "false");

				QueueFactory.attachCommandRouting(request);

				stmtSubscriptionUpdate.setLong(1, subProductId);
				stmtSubscriptionUpdate.addBatch();

				debugMonitor("Sent to command route: " + request.getKeyword() + ": " + request.getIsdn() + ","
						+ request.getActionType() + ","
						+ request.getCause() + "," + request.getChannel());
			}

			if (batchCounter >= 0)
			{
				stmtSubscriptionUpdate.executeBatch();
			}

			logMonitor("Total record is browsed: " + counter);

			setLastRunDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

			mprtParam.setProperty("LastRunDate", getLastRunDate());
			storeConfig();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			_conn.commit();
			Database.closeObject(rsQueue);
		}
	}

	public void setLastRunDate(String _lastRunDate)
	{
		this.lastRunDate = _lastRunDate;
	}

	public String getLastRunDate()
	{
		return lastRunDate;
	}

	private void getMessageContent(CommandMessage request, ProductEntry product) throws Exception
	{
		String content = "";

		ProductMessage productMessage = product.getProductMessage(
				actionType, request.getCampaignId(), "vni", request.getChannel(), cause);

		if (productMessage != null)
		{
			content = productMessage.getContent();

			request.setCauseValue(productMessage.getCauseValue());
		}
		else
		{
			content = DomainFactory.getCache().getDomain("RESPONSE_MESSAGE", actionType + "." + cause);

			if (content.equals(""))
			{
				content = DomainFactory.getCache().getDomain("RESPONSE_MESSAGE", cause);
			}
		}

		content = ResponseUtil.formatResponse(null, product, request, request.getActionType(), content);

		request.setRequest(content);
		request.setServiceAddress(product.getServiceAddress().equals("") ? request.getServiceAddress() : product
				.getServiceAddress());
	}
}
