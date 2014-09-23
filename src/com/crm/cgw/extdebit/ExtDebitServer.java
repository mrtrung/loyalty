/**
 * 
 */
package com.crm.cgw.extdebit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.crm.cgw.net.NetThread;
import com.crm.cgw.submodifytcp.SubModifyTCPAnalyzer;
import com.crm.cgw.submodifytcp.SubModifyTCPHandler;
import com.crm.cgw.submodifytcp.SubModifyTCPHandlerCollection;
import com.crm.cgw.thread.ExtDebitThread;
import com.crm.cgw.thread.SubModifyTCPThread;

/**
 * @author hungdt
 * 
 */
public class ExtDebitServer extends NetThread {

	protected ExtDebitThread			dispatcher			= null;
	protected ServerSocket				server				= null;

	public static ExtDebitHandlerCollection	collectorHandlers	= new ExtDebitHandlerCollection();
	protected int						currentId			= 1;

	@Override
	public void process() throws Exception {
		try {
			if (!isRunning()) {
				stop();
				return;
			}

			if (collectorHandlers.size() >= dispatcher.maxConnection) {
				closeSocket();
			}
			else {
				
				openSocket();
				Socket socket = server.accept();
				socket.setSoTimeout(dispatcher.networkTimeout);
				socket.setReceiveBufferSize(dispatcher.bufferLength);
				ExtDebitHandler handler = new ExtDebitHandler(socket, dispatcher);
				handler.setAnalyzer(new ExtDebitAnalyzer());
				int curId = currentId++;
				handler.setHandlerId(curId);
				collectorHandlers.put((long) curId, handler);
				
				handler.start();
			}
		}
		catch (SocketTimeoutException ste) {
		}
		catch (SocketException se) {
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if ((server != null) && server.isClosed()) {
				try {
					server.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void debugMonitor(Object message) {
		if (dispatcher != null)
			dispatcher.debugMonitor(message);
	}

	public boolean isClosed() {
		if (server == null)
			return true;
		return server.isClosed();
	}

	public ExtDebitServer(ExtDebitThread dispatcher) {
		this.dispatcher = dispatcher;
		setSleepTime(dispatcher.getDelayTime());
	}

	private void openSocket() throws IOException {
		if (server != null)
			return;
		server = new ServerSocket();// (dispatcher.listenPort,
		// dispatcher.maxConnection);

		server.setReceiveBufferSize(dispatcher.bufferLength);
		InetSocketAddress address =
				new InetSocketAddress(dispatcher.listenPort);
		server.bind(address, dispatcher.maxConnection);
		server.setSoTimeout(dispatcher.networkTimeout);
	}

	private void closeSocket() throws IOException {
		try {
			if (server == null)
				return;
			server.close();
		}
		finally {
			server = null;
		}
	}

	@Override
	public void start() {
		if (isRunning())
			destroy();
		
		super.start();
	}

	@Override
	public void stop() {
		destroy();
	}

	@Override
	public void destroy() {
		if (!isRunning()) { return; }

		try {

			if(collectorHandlers.size() > 0)
			{
				Set mapSet = (Set) collectorHandlers.entrySet();

				 Iterator mapIterator = mapSet.iterator();
				
				 while (mapIterator.hasNext()) {
					 Map.Entry mapEntry = (Map.Entry) mapIterator.next();
					 ExtDebitHandler handler = (ExtDebitHandler) mapEntry.getValue();
					 try {
							handler.stop();
							handler = null;
						}
						catch (Exception e) {
							debugMonitor(e);
						}
				 }
				 
				 collectorHandlers.clear();
			}
			
			try {
				closeSocket();
			}
			catch (IOException e) {
				debugMonitor(e);
			}
		}
		catch (Exception e) {
			debugMonitor(e);
		}
		finally {
			super.destroy();
		}
	}

}
