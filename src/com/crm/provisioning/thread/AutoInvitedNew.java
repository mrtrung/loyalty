package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.kernel.sql.Database;
import com.crm.provisioning.cache.CommandEntry;
import com.crm.provisioning.cache.ProvisioningFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.StringUtil;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

//trungnq
public class AutoInvitedNew extends DispatcherThread {
	protected String message = "";
	protected String SQL = "";
	protected String sqlUpdate = "Update inviteSMS set status = 1 where isdn = ? ";
	protected long timeDelay = 0;
	protected PreparedStatement stmtStatus = null;
	protected PreparedStatement stmtUpdate = null;
	protected Connection connection = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("message", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addElement(createParameterDefinition("SQL", "",
				ParameterType.PARAM_TEXTBOX_MAX, "1000", ""));
		vtReturn.addElement(createParameterDefinition("batchSize", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addElement(createParameterDefinition("timeDelay", "",
				ParameterType.PARAM_TEXTBOX_MAX, "256", ""));
		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	public void fillParameter() throws AppException {
		// TODO Auto-generated method stub

		try {
			super.fillParameter();
			message = ThreadUtil.getString(this, "message", true, "");
			SQL = ThreadUtil.getString(this, "SQL", true, "");
			batchSize = ThreadUtil.getInt(this, "batchSize", 200);
			timeDelay = ThreadUtil.getInt(this, "timeDelay", 1000);

		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void beforeProcessSession() throws Exception {
		super.beforeProcessSession();

		try {
			String strSQL = SQL;

			connection = Database.getConnection();

			stmtStatus = connection.prepareStatement(strSQL);

			stmtUpdate = connection.prepareStatement(sqlUpdate);

		} catch (Exception e) {
			throw e;
		}
	}

	public void afterProcessSession() throws Exception {
		try {
			Database.closeObject(stmtStatus);
			Database.closeObject(stmtUpdate);
			Database.closeObject(connection);
		} catch (Exception e) {
			throw e;
		} finally {
			super.afterProcessSession();
		}
	}

	// //////////////////////////////////////////////////////
	// process session
	// Author : TrungNQ
	// Created Date : 20/09/2013
	// //////////////////////////////////////////////////////
	public void doProcessSession() throws Exception {
		ResultSet rsStatus = null;
		CommandMessage vnmMessage = new CommandMessage();

		int counter = 0;
		int counter2 = 0;
		String strLog = "";
		String strLog2 = "";

		try {
			rsStatus = stmtStatus.executeQuery();

			while (rsStatus.next()) {
				vnmMessage.setIsdn(rsStatus.getString("isdn"));
				vnmMessage.setServiceAddress("268");
				// vnmMessage.setAmount(rsStatus.getInt("cumulationAmount"));
				// vnmMessage.setProductId(274);
				// vnmMessage.setResponseValue(ResponseUtil.SERVICE_BALANCE,
				// (int) vnmMessage.getAmount());

				String content = message
						.replaceAll("~SERVICE_BALANCE~", StringUtil
								.valueOf(rsStatus.getInt("cumulationAmount")));

				CommandEntry command = ProvisioningFactory.getCache()
						.getCommand(Constants.COMMAND_SEND_SMS);

				vnmMessage.setProvisioningType(Constants.PROVISIONING_SMSC);
				vnmMessage.setCommandId(command.getCommandId());
				vnmMessage.setRequest(content);
				vnmMessage.setRequestValue(ResponseUtil.SMS_CMD_CHECK, "false");

				QueueFactory.attachCommandRouting(vnmMessage);

				strLog = "CumulationPoint: "
						+ rsStatus.getInt("cumulationAmount") + " ISDN: "
						+ vnmMessage.getIsdn() + " Message: " + content;

				stmtUpdate.setString(1, vnmMessage.getIsdn());
				stmtUpdate.addBatch();

				logMonitor(strLog);

				counter++;
				counter2++;

				if (counter >= batchSize) {
					stmtUpdate.executeBatch();
					Thread.sleep(timeDelay);
					counter = 0;
				}
			}
			if (counter > 0) {
				stmtUpdate.executeBatch();
			}
			strLog2 = "Tong so ban ghi la:" + counter2;
			logMonitor(strLog2);

		} catch (Exception error) {
			error.printStackTrace();
		} finally {
			Database.closeObject(rsStatus);
		}
	}
}
