package com.crm.provisioning.thread;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.cache.MQConnection;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class CancelF5Thread extends DispatcherThread {
	private String filePath = "";
	private String serviceAddress = "";
	private String keyword = "";
	private int timeout = 1200000;

	private List<String> isdnList = new ArrayList<String>();
	private Object lockedObject = new Object();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400,
				"Imported File Path."));
		vtReturn.add(ThreadUtil.createTextParameter("serviceAddress", 400,
				"ServiceAddress."));
		vtReturn.add(ThreadUtil.createTextParameter("keyword", 400, "Keyword."));
		vtReturn.add(ThreadUtil.createIntegerParameter("orderTimeout",
				"Order timeout."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException {
		filePath = ThreadUtil.getString(this, "filePath", false, "");
		serviceAddress = ThreadUtil
				.getString(this, "serviceAddress", false, "");
		keyword = ThreadUtil.getString(this, "keyword", false, "");
		timeout = ThreadUtil.getInt(this, "orderTimeout", 1200) * 1000;

		super.fillDispatcherParameter();
	}

	private void loadFile() {
		synchronized (lockedObject) {
			isdnList.clear();
			FileReader fileReader = null;
			try {
				StringBuilder strBuilder = new StringBuilder();
				char[] buffer = new char[1024];
				fileReader = new FileReader(filePath);
				int count = -1;
				do {
					count = fileReader.read(buffer, 0, buffer.length);
					if (count > 0)
						strBuilder.append(buffer, 0, count);
				} while (count > 0);

				String fileContent = strBuilder.toString();
				String[] fileData = null;
				if (fileContent.contains("\r\n"))
					fileData = fileContent.split("\r\n");
				else if (fileContent.contains("\n"))
					fileData = fileContent.split("\n");
				else
					fileData = fileContent.split("\r");

				for (int i = 0; i < fileData.length; i++) {
					isdnList.add(fileData[i].trim());
				}

				debugMonitor("Added total " + isdnList.size() + " record(s).");
			} catch (Exception e) {
				debugMonitor(e);
			} finally {
				if (fileReader != null)
					try {
						fileReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	@Override
	public void beforeProcessSession() throws Exception {
		// TODO Auto-generated method stub
		super.beforeProcessSession();

		loadFile();
	}

	@Override
	public void doProcessSession() throws Exception {
		MQConnection connection = null;

		MessageProducer producer = null;

		try {
			connection = getMQConnection();

			if (isdnList.size() > 0) {
				for (int i = 0; i < isdnList.size(); i++) {
					CommandMessage order = new CommandMessage();
					order.setIsdn(isdnList.get(i));
					order.setKeyword(keyword);
					order.setServiceAddress(serviceAddress);
					order.setTimeout(timeout);
					order.setChannel(Constants.CHANNEL_WEB);

					if (producer == null)
						producer = QueueFactory.createQueueProducer(
								connection.getSession(), queueWorking, timeout,
								queuePersistent);

					Message msg = QueueFactory.createObjectMessage(
							connection.getSession(), order);

					producer.send(msg);
					debugMonitor("Sent: " + order.toShortString());
				}
			}
		} catch (JMSException e) {
			connection.markError();
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			QueueFactory.closeQueue(producer);
			returnMQConnection(connection);
		}
	}
}
