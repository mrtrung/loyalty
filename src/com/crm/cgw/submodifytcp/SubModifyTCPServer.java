/**
 * 
 */
package com.crm.cgw.submodifytcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.crm.cgw.net.NetThread;
import com.crm.cgw.thread.SubModifyTCPThread;

/**
 * @author hungdt
 * 
 */
public class SubModifyTCPServer extends NetThread {

	protected SubModifyTCPThread			dispatcher			= null;
	protected ServerSocket					server				= null;

	public static SubModifyTCPHandlerCollection	collectorHandlers	= new SubModifyTCPHandlerCollection();
	protected int							currentId			= 1;

	public boolean isClosed() {
		if (server == null)
			return true;
		return server.isClosed();
	}

	public SubModifyTCPServer(SubModifyTCPThread dispatcher) {
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

			while (collectorHandlers.size() > 0) {
				SubModifyTCPHandler handler = collectorHandlers.remove(0);
				try {
					handler.stop();
					handler = null;
				}
				catch (Exception e) {
					debugMonitor(e);
				}
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
				SubModifyTCPHandler handler = new SubModifyTCPHandler(socket, dispatcher);
				handler.setAnalyzer(new SubModifyTCPAnalyzer());
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

}
