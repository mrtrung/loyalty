/**
 * 
 */
package com.crm.cgw.thread;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.crm.cgw.submodifytcp.ChargingResp;
import com.crm.thread.DispatcherInstance;
import com.crm.util.GeneratorSeq;

/**
 * @author hungdt
 *
 */
public class CGWTestInstance extends DispatcherInstance {

	private Socket	socket	= null;
	
	public CGWTestInstance() throws Exception {
		super();
	}
	
	public CGWTestThread getDispatcher()
	{
		return (CGWTestThread) super.getDispatcher();
	}
	
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();
		socket = new Socket(getDispatcher().host, getDispatcher().port);
		socket.setTcpNoDelay(true);
	}
	
	public void afterProcessSession() throws Exception
	{
		try
		{
			if (socket != null)
				socket.close();
		}
		finally
		{
			super.afterProcessSession();
		}
		
	}
	
	public void doProcessSession() throws Exception
	{
		int totalCount = 0;
		int sessionId = 0;
		byte[] packData = new byte[0];
		for (totalCount = 0; totalCount < getDispatcher().sendTotal && isAvailable(); totalCount++)
		{
			sessionId = GeneratorSeq.getNextSeq();
			if (((getDispatcher().batchSize > 0 && totalCount % getDispatcher().batchSize == 0)
					|| getDispatcher().batchSize == 0)
					&& packData.length > 0)
			{
				socket.getOutputStream().write(packData);
				socket.getOutputStream().flush();

				debugMonitor("Sent: sId=" + sessionId+", " + (new String(packData)));
				BufferedInputStream ibufs = new BufferedInputStream(socket.getInputStream());
				int b = ibufs.read();
				byte[] bytes = new byte[b];
				
				String receiveData = new String(bytes);
				ChargingResp resp = new ChargingResp(receiveData);
				
				debugMonitor("Receive: sId=" + sessionId+", " + resp.toString());
				debugMonitor("Sent " + totalCount + " request.");
				packData = new byte[0];
				
				Thread.sleep(getDispatcher().timeBetweenLoop);
			}
			
			String content = getDispatcher().content;
			if (!content.endsWith(";"))
				content = content + ";";

			byte[] data = content.getBytes();
			byte[] sendData = new byte[data.length + 2];
			sendData[0] = 0;
			sendData[1] = 0x13;
			System.arraycopy(data, 0, sendData, 2, data.length);

			byte[] newByteData = new byte[sendData.length + packData.length];

			System.arraycopy(packData, 0, newByteData, 0, packData.length);
			System.arraycopy(sendData, 0, newByteData, packData.length, sendData.length);

			packData = newByteData;

		}

		if (packData.length > 0)
		{
			socket.getOutputStream().write(packData);
			socket.getOutputStream().flush();

			debugMonitor("Sent: sId=" + sessionId+", " + (new String(packData)));
			BufferedInputStream ibufs = new BufferedInputStream(socket.getInputStream());
			int b = ibufs.read();
			byte[] bytes = new byte[b];
			
			String receiveData = new String(bytes);
			ChargingResp resp = new ChargingResp(receiveData);
			
			debugMonitor("Receive: sId=" + sessionId+", " + resp.toString());
			debugMonitor("Sent " + totalCount + " request.");
			packData = new byte[0];
		}
		
		setRunning(false);
		
	}
	
}
