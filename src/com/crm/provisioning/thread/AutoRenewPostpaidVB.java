package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.fss.sql.Database;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;
import com.fss.util.StringUtil;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author: HungPQ
 * @version 1.0
 */

public class AutoRenewPostpaidVB extends DispatcherThread {
	private PreparedStatement _stmtQueue = null;
	private PreparedStatement _stmtSubscribers = null;
	private PreparedStatement _stmtOverdue = null;
	private PreparedStatement _stmtInsert = null;
	private Connection _conn = null;

	protected String _sqlCommand = "";
	protected String _sqlOverdue = "";
	protected String _timeRun = "";
	protected String _content = "";

	protected String _productVB600 = "";
	protected String _productVB220 = "";

	private static Logger _logger = Logger.getLogger(SubscriptionThread.class);

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("SQLCommand", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("SQLOverdue", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("TimeRun", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("SMSNotify", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("VB600Alias", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));
		vtReturn.addElement(createParameterDefinition("VB220Alias", "",
				ParameterType.PARAM_TEXTBOX_MAX, "100"));

		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	public void fillParameter() throws AppException {
		try {
			super.fillParameter();

			setSQLCommand(loadMandatory("SQLCommand"));
			setTimeRun(loadMandatory("TimeRun"));
			_sqlOverdue = loadMandatory("SQLOverdue");
			_content = loadMandatory("SMSNotify");
			setProductVB600(loadMandatory("VB600Alias"));
			setProductVB220(loadMandatory("VB220Alias"));
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isAutoLoop() {
		return false;
	}

	// //////////////////////////////////////////////////////
	// after process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void beforeProcessSession() throws Exception {
		super.beforeProcessSession();

		try {
			String strSQL = getSQLCommand();
			_conn = com.crm.kernel.sql.Database.getConnection();
			_stmtQueue = _conn.prepareStatement(strSQL);

			_stmtOverdue = _conn.prepareStatement(_sqlOverdue);

			strSQL = "insert into NOTIFYIDDBUFFET values(?, sysdate, 2, ?, 0, sysdate, 3, 1)";
			_stmtInsert = _conn.prepareStatement(strSQL);

			strSQL = "Update SubscriberProduct Set lastRunDate = SysDate Where subProductId = ?";
			_stmtSubscribers = _conn.prepareStatement(strSQL);
		} catch (Exception e) {
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// after process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void afterProcessSession() throws Exception {
		try {
			Database.closeObject(_stmtQueue);
			Database.closeObject(_stmtInsert);
			Database.closeObject(_stmtOverdue);
			Database.closeObject(_stmtSubscribers);
			Database.closeObject(_conn);
		} catch (Exception e) {
			throw e;
		} finally {
			super.afterProcessSession();
		}
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void doProcessSession() throws Exception {
		long counter = 0;

		int batchSize = 5000;
		int batchCounter = 0;

		ResultSet rsQueue = null;

		MQConnection connection = null;
		try {
			connection = getMQConnection();

			Date cur = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar timeRun = Calendar.getInstance();
			timeRun.setTime(sdf.parse(getTimeRun()));

			if (!cur.before(timeRun.getTime())) {
				String isdn = "";
				String expirationdate = "";
				long productID;
				String serviceAddress = "";

				ProductEntry productVB600 = ProductFactory.getCache()
						.getProduct(getProductVB600());
				ProductEntry productVB220 = ProductFactory.getCache()
						.getProduct(getProductVB220());

				rsQueue = _stmtOverdue.executeQuery();
				while (rsQueue.next() && isAvailable()) {
					isdn = rsQueue.getString("isdn");
					expirationdate = new SimpleDateFormat("dd/MM/yyyy")
							.format(rsQueue.getDate("expirationdate"));
					productID = rsQueue.getLong("productid");

					_stmtInsert.setString(1, isdn);
					_stmtInsert.setLong(2, productID);
					_stmtInsert.addBatch();

					if (productID == productVB600.getProductId()) {
						serviceAddress = "1720";
						_content = _content.replaceAll("~PRODUCT~", "VB600");
					} else if (productID == productVB220.getProductId()) {
						serviceAddress = "1721";
						_content = _content.replaceAll("~PRODUCT~", "VB220");
					}

					_content = _content.replaceAll("~SERVICE_EXPIRATION_DATE~",
							expirationdate);
					_content = _content.replaceAll("~SERVICE_ADDRESS~",
							serviceAddress);

					CommandMessage order = pushSMS(isdn, serviceAddress,
							_content);

					connection
							.sendMessage(order, queueName, 0, queuePersistent);

					logMonitor("" + isdn + ": Expirationdate is: "
							+ rsQueue.getDate("expirationdate"));

					batchCounter++;
				}

				logMonitor("Total record is overdue: " + batchCounter);

				if (batchCounter > 0) {
					_stmtInsert.executeBatch();
					batchCounter = 0;
				}

				ProductEntry product = null;

				rsQueue = _stmtQueue.executeQuery();
				while (rsQueue.next() && isAvailable()) {
					String productId = StringUtil.nvl(
							rsQueue.getString("productId"), "");
					String sourceAddress = StringUtil.nvl(
							rsQueue.getString("isdn"), "");
					int subsriberType = rsQueue.getInt("subscriberType");

					if (subsriberType == Constants.POSTPAID_SUB_TYPE) {
						try {
							product = ProductFactory.getCache().getProduct(
									rsQueue.getLong("productid"));

							if (product.getProductId() == productVB600
									.getProductId()) {
								serviceAddress = "1720";
							} else if (product.getProductId() == productVB220
									.getProductId()) {
								serviceAddress = "1721";
							}

							CommandMessage order = pushOrder(
									rsQueue.getString("isdn"), serviceAddress,
									product.getAlias());

							connection.sendMessage(order,
									QueueFactory.ORDER_REQUEST_QUEUE, 0,
									queuePersistent);

							batchCounter++;

							if (_logger.isDebugEnabled()) {
								_logger.debug("add auto renew request for msisdn = "
										+ sourceAddress);
							}

							if (batchCounter >= batchSize) {
								_stmtSubscribers.executeBatch();

								if (_logger.isDebugEnabled()) {
									_logger.debug("executed batch with "
											+ batchCounter + " records");
								}

								batchCounter = 0;
							}

							counter++;

							logMonitor("isdn: " + rsQueue.getString("isdn")
									+ ", Product: " + product.getAlias());
						} catch (Exception e) {
							logMonitor(sourceAddress + ": " + productId + "("
									+ e.getMessage() + ")");
						}
					}
				}

				if (batchCounter >= 0) {
					_stmtSubscribers.executeBatch();
				}

				logMonitor("Total record is browsed: " + counter);

				timeRun.add(Calendar.DATE, 1);
				setTimeRun(sdf.format(timeRun.getTime()));
				mprtParam.setProperty("TimeRun", getTimeRun());
			} else {
				logMonitor("Not time to run...");
			}

			storeConfig();
		} catch (Exception e) {
			logMonitor(e.getMessage());
		} finally {
			returnMQConnection(connection);
			_conn.commit();
			Database.closeObject(rsQueue);
		}
	}

	public CommandMessage pushOrder(String isdn, String serviceAddress,
			String product) throws Exception {
		CommandMessage order = new CommandMessage();

		try {
			order.setChannel("web");
			order.setUserId(0);
			order.setUserName("system");

			order.setServiceAddress(serviceAddress);
			order.setIsdn(isdn);
			order.setKeyword("RENEWPOSTPAID_" + product);
		} catch (Exception e) {
			throw e;
		}
		return order;
	}

	public CommandMessage pushSMS(String isdn, String serviceAddress,
			String request) throws Exception {
		CommandMessage order = new CommandMessage();

		try {
			order.setChannel("SMS");
			order.setUserId(0);
			order.setUserName("system");

			order.setServiceAddress(serviceAddress);
			order.setIsdn(isdn);
			order.setRequest(request);
		} catch (Exception e) {
			throw e;
		}
		return order;
	}

	public void setSQLCommand(String _sqlCommand) {
		this._sqlCommand = _sqlCommand;
	}

	public String getSQLCommand() {
		return _sqlCommand;
	}

	public String getTimeRun() {
		return _timeRun;
	}

	public void setTimeRun(String _timeRun) {
		this._timeRun = _timeRun;
	}

	public String getProductVB600() {
		return _productVB600;
	}

	public void setProductVB600(String _productVB600) {
		this._productVB600 = _productVB600;
	}

	public String getProductVB220() {
		return _productVB220;
	}

	public void setProductVB220(String _productVB220) {
		this._productVB220 = _productVB220;
	}
}
