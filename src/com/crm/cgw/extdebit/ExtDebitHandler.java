/**
 * 
 */
package com.crm.cgw.extdebit;

import java.io.IOException;
import java.net.Socket;

import com.crm.cgw.net.INetAnalyzer;
import com.crm.cgw.net.INetConnection;
import com.crm.cgw.net.INetHandler;
import com.crm.cgw.net.NetThread;
import com.crm.cgw.thread.ExtDebitThread;

/**
 * @author hungdt
 * 
 */
public class ExtDebitHandler extends NetThread implements INetHandler {

	private int							handlerId			= 0;
	private ExtDebitCollection			extDebitCollection	=
																	new ExtDebitCollection();
	private INetAnalyzer				analyzer			= null;
	private ExtDebitConnection			connection			= null;
	private ExtDebitHandlerCollection	handlerCollection	= null;
	private ExtDebitThread				dispatcher			= null;

	public int getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(int handlerId) {
		this.handlerId = handlerId;
		if (connection != null)
			connection.setConnectionId(handlerId);
	}
	
	

	public ExtDebitHandlerCollection getHandlerCollection() {
		return handlerCollection;
	}

	public void setHandlerCollection(ExtDebitHandlerCollection handlerCollection) {
		this.handlerCollection = handlerCollection;
	}

	@Override
	public INetAnalyzer getAnalyzer() {
		return analyzer;
	}

	@Override
	public void setAnalyzer(INetAnalyzer analyzer) {
		this.analyzer = analyzer;
		((ExtDebitAnalyzer) this.analyzer).setHandler(this);

	}

	@Override
	public void handle(byte[] data) {
		if (data != null)
			analyzer.createObject(data, extDebitCollection);
		else
			stop();

	}

	@Override
	public INetConnection getConnection() {
		return connection;
	}

	@Override
	public void process() throws Exception {
		ExtDebit ext = (ExtDebit) extDebitCollection.get();
		while (null != ext) {
			dispatcher.addWork(ext);
			ext = (ExtDebit) extDebitCollection.get();
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

	public ExtDebitHandler(Socket socket, ExtDebitThread dispatcher)
			throws IOException {
		this.dispatcher = dispatcher;
		this.setSleepTime(dispatcher.getDelayTime());
		this.connection = new ExtDebitConnection(socket, this.dispatcher);
		this.connection.setHandler(this);
		this.connection.setConnectionId(getHandlerId());
		this.setSleepTime(dispatcher.getDelayTime());
	}
}
