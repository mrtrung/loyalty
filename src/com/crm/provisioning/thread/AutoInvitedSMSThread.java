package com.crm.provisioning.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.crm.kernel.sql.Database;
import com.crm.provisioning.message.VNMMessage;
import com.crm.provisioning.util.CommandUtil;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.thread.DispatcherInstance;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

//trungnq
public class AutoInvitedSMSThread extends DispatcherThread {
	protected String message = "";
	protected String SQL = "";
	protected long timeDelay = 0;
	protected PreparedStatement stmtStatus = null;
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
			
		} catch (Exception e) {
			throw e;
		}
	}

	public void afterProcessSession() throws Exception {
		try {
			Database.closeObject(stmtStatus);
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
		VNMMessage vnmMessage = new VNMMessage();

		int counter = 0;
		int counter2 = 0;
		String strLog = "";
		String strLog2 = "";

		try {
			rsStatus = stmtStatus.executeQuery();

			while (rsStatus.next()) {
				vnmMessage.setIsdn(rsStatus.getString("isdn"));
				vnmMessage.setServiceAddress("268");
				vnmMessage.setAmount(rsStatus.getInt("cumulationAmount"));
				vnmMessage.setProductId(274);
				vnmMessage.setResponseValue(ResponseUtil.SERVICE_BALANCE,
						(int) vnmMessage.getAmount());

				DispatcherInstance instance = new DispatcherInstance();

				String messages = message
						.replaceAll("~SERVICE_BALANCE~", vnmMessage
								.getResponseValue(ResponseUtil.SERVICE_BALANCE));

				CommandUtil.sendSMS(instance, vnmMessage,
						vnmMessage.getServiceAddress(), vnmMessage.getShipTo(),
						messages);

				strLog = "CumulationPoint: " + (int) vnmMessage.getAmount()
						+ " ISDN: " + vnmMessage.getIsdn() + " Message: "
						+ messages;

				logMonitor(strLog);

				counter++;
				counter2++;

				if (counter >= batchSize) {
					Thread.sleep(timeDelay);
					counter = 0;
				}
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
