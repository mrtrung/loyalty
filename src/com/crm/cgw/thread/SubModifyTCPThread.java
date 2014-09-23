/**
 * 
 */
package com.crm.cgw.thread;

import java.util.Vector;

import com.crm.cgw.submodifytcp.Charging;
import com.crm.cgw.submodifytcp.SubModifyTCPCollection;
import com.crm.cgw.submodifytcp.SubModifyTCPServer;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

/**
 * @author hungdt
 *
 */
public class SubModifyTCPThread extends DispatcherThread {
	public int						listenPort		= 3000;
	public int						maxConnection	= 10;
	public int						bufferLength	= 65536;
	public int						networkTimeout	= 3000;
	public int						chargingTimeout	= 120000;
	public String					keywordPrefix = "";

	public SubModifyTCPServer		subModifyServer	= null;

	private long					lastTimelog		= System.currentTimeMillis();
	public SubModifyTCPCollection	workQueue		= new SubModifyTCPCollection();

	public void addWork(Charging req) {
		workQueue.put(req);
	}

	public Charging getWork() {
		if (workQueue.size() == 0)
			return null;
		else
			return (Charging) workQueue.remove(0);
	}

	@Override
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createIntegerParameter("listenPort", "Listen port."));
		vtReturn.add(ThreadUtil.createIntegerParameter("maxConnection", "Maximum connection the server can handle."));
		vtReturn.add(ThreadUtil.createIntegerParameter("bufferLength", "Socket buffer size."));
		vtReturn.add(ThreadUtil.createIntegerParameter("networkTimeout", "Time out to wait for accept connection or read from client."));
		vtReturn.add(ThreadUtil.createIntegerParameter("chargingTimeout", "Time to live of trigger (seconds)."));
		vtReturn.add(ThreadUtil.createIntegerParameter("keywordPrefix", "keywordPrefix"));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException {
		listenPort = ThreadUtil.getInt(this, "listenPort", 3000);
		maxConnection = ThreadUtil.getInt(this, "maxConnection", 100);
		bufferLength = ThreadUtil.getInt(this, "bufferLength", 1500);
		networkTimeout = ThreadUtil.getInt(this, "networkTimeout", 1000);
		chargingTimeout = ThreadUtil.getInt(this, "chargingTimeout", 120) * 1000;
		keywordPrefix = ThreadUtil.getString(this, "keywordPrefix", false, "");
		super.fillDispatcherParameter();
	}

	@Override
	public void beforeProcessSession() throws Exception {
		super.beforeProcessSession();

		subModifyServer = new SubModifyTCPServer(this);

		subModifyServer.start();
	}

	@Override
	public void afterProcessSession() throws Exception {
		try {
			subModifyServer.stop();
		}
		catch (Exception e) {
			debugMonitor(e);
		}
		finally {
			subModifyServer = null;
			super.afterProcessSession();
		}
	}

	@Override
	public void doProcessSession() throws Exception {
		long logInterval = 10000;
		if (lastTimelog + logInterval <= System.currentTimeMillis()) {
			debugMonitor("Local queue size: " + workQueue.size());

			lastTimelog = System.currentTimeMillis();
		}
	}
}
