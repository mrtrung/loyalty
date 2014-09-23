/**
 * 
 */

package com.crm.cgw.submodifytcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.crm.cgw.net.INetConnection;
import com.crm.cgw.net.INetData;
import com.crm.cgw.net.INetHandler;
import com.crm.cgw.net.NetThread;
import com.crm.cgw.thread.SubModifyTCPThread;

/**
 * @author hungdt
 */
public class CharggwConnection extends NetThread implements INetConnection {

	private Socket							socket						= null;
	private SubModifyTCPThread				dispatcher					= null;

	private SubModifyTCPHandlerCollection	collectorHandlerCollection	= null;
	private INetHandler						handler						= null;
	private int								connectionId				= 0;

	public void setConnectionId(int connectionId) {

		this.connectionId = connectionId;
	}

	public int getConnectionId() {

		return connectionId;
	}

	public SubModifyTCPThread getDispatcher() {

		return dispatcher;
	}

	public InetAddress getAddress() {

		if (socket == null)
			return null;
		else
			return socket.getInetAddress();
	}

	public int getPort() {

		if (socket == null)
			return 0;
		else
			return socket.getPort();
	}

	@Override
	public void setHandler(INetHandler handler) {

		this.handler = handler;
	}

	@Override
	public INetHandler getHandler() {

		return handler;
	}

	public void setCollectorHandlerCollection(SubModifyTCPHandlerCollection collectorHandlerCollection) {

		this.collectorHandlerCollection = collectorHandlerCollection;
	}

	public SubModifyTCPHandlerCollection getCollectorHandlerCollection() {

		return collectorHandlerCollection;
	}

	public CharggwConnection(Socket socket, SubModifyTCPThread dispatcher)
			throws IOException {

		this.socket = socket;
		this.dispatcher = dispatcher;
		this.setSleepTime(this.dispatcher.getDelayTime());
	}

	private int readInputStream(byte[] receivedData) throws IOException {

		try {
			return socket.getInputStream().read(receivedData);
		}
		catch (SocketTimeoutException ste) {
			return 0;
		}
		catch (SocketException se) {
			// debugMonitor(se);
			return -1;
		}
	}

	public void send(INetData obj) throws IOException {

		OutputStream os;
		synchronized (this) {
			os = socket.getOutputStream();
		}
		byte[] data = obj.getData();
		try {
			if (null == os)
				throw new SocketException("Connection reset");
			os.write(data);
			os.flush();
			data = null;
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			if (null != data)
				stop();
			data = null;
		}
	}

	public boolean isConnected() {

		if (socket == null)
			return false;
		else
			return socket.isConnected();
	}

	@Override
	public void stop() {

		if (isRunning()) {
			try {
				socket.close();
			}
			catch (Exception e) {
			}
			finally {
				socket = null;
				super.stop(dispatcher.networkTimeout);

			}
		}
	}

	@Override
	public void process() throws Exception {

		byte[] buffer = new byte[0];
		byte[] receivedData = new byte[8];
		// System.out.println("waiting data...");
		int byteCount = readInputStream(receivedData);

		while (byteCount > 0) {
			byte[] newBuffer = new byte[buffer.length + byteCount];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			System.arraycopy(receivedData, 0, newBuffer, buffer.length, byteCount);
			buffer = newBuffer;
			byteCount = readInputStream(receivedData);
		}
		if (0 != buffer.length) {
			try {
				handler.handle(buffer);
			}
			catch (Exception e) {
				throw e;
			}
		}

		if (byteCount < 0) {
			handler.handle(null);
		}
	}

	@Override
	public void debugMonitor(Object message) {

		if (dispatcher != null)
			dispatcher.debugMonitor(message);
	}

}
