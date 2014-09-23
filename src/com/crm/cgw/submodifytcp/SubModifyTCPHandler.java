/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.io.IOException;
import java.net.Socket;

import com.crm.cgw.net.INetAnalyzer;
import com.crm.cgw.net.INetConnection;
import com.crm.cgw.net.INetHandler;
import com.crm.cgw.net.NetThread;
import com.crm.cgw.thread.SubModifyTCPThread;

/**
 * @author hungdt
 * 
 */
public class SubModifyTCPHandler extends NetThread implements INetHandler {
	private int								handlerId			= 0;
	private SubModifyTCPCollection			chargingCollection	=new SubModifyTCPCollection();
	private INetAnalyzer					analyzer			= null;
	private CharggwConnection				connection			= null;
	private SubModifyTCPHandlerCollection	handlerCollection	= null;
	private SubModifyTCPThread				dispatcher			= null;

	public INetAnalyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(INetAnalyzer analyzer) {
		this.analyzer = analyzer;
		((SubModifyTCPAnalyzer) this.analyzer).setHandler(this);
	}

	public void setHandlerId(int handlerId) {
		this.handlerId = handlerId;
		if (connection != null)
			connection.setConnectionId(handlerId);
	}

	public int getHandlerId() {
		return handlerId;
	}

	public SubModifyTCPHandler(Socket socket, SubModifyTCPThread dispatcher)
			throws IOException {
		this.dispatcher = dispatcher;
		this.setSleepTime(dispatcher.getDelayTime());
		this.connection = new CharggwConnection(socket, this.dispatcher);
		this.connection.setHandler(this);
		this.connection.setConnectionId(getHandlerId());
		this.setSleepTime(dispatcher.getDelayTime());
	}

	public void setSubmodifyTCPHandlerCollection(SubModifyTCPHandlerCollection handlerCollection) {
		this.handlerCollection = handlerCollection;
	}

	@Override
	public void handle(byte[] data) {
		if (data != null)
			analyzer.createObject(data, chargingCollection);
		else
			stop();

	}

	@Override
	public INetConnection getConnection() {
		return connection;
	}

	@Override
	public void process() throws Exception {
		Charging charging = (Charging) chargingCollection.get();
		while (null != charging) {
			dispatcher.addWork(charging);
			charging = (Charging) chargingCollection.get();
		}
	}

	@Override
	public void debugMonitor(Object message) {
		if (dispatcher != null) {
			dispatcher.debugMonitor(message);
		}
	}

	public void start() {
		try {
			if (connection == null)
				throw new Exception("Need to set connection for handler.");

			debugMonitor("Client #" + getHandlerId() + " ("
					+ connection.getAddress().getHostAddress() + ":"
					+ connection.getPort() + ")" + " connected.");
			connection.start();
			super.start();
		}
		catch (Exception e) {
			debugMonitor(e);
			stop();
		}
	}

	@Override
	public void stop() {
		String strLog = "";
		try {
			if (connection == null)
				throw new Exception("Need to set connection for handler.");
			try {
				if (handlerCollection != null) {
					if (handlerCollection.containsKey(this.getHandlerId()))
						handlerCollection.remove(this);
				}

				strLog =
						"Client #" + getHandlerId() + " ("
								+ connection.getAddress().getHostAddress()
								+ ":" + connection.getPort() + ")"
								+ " was disconnected.";

				connection.stop();
			}
			finally {
				debugMonitor(strLog);
			}
		}
		catch (Exception e) {

		}
		finally {
			super.stop(dispatcher.networkTimeout);
		}
	}

}
