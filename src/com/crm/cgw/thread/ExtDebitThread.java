/**
 * 
 */
package com.crm.cgw.thread;

import java.util.Vector;

import com.crm.cgw.extdebit.ExtDebit;
import com.crm.cgw.extdebit.ExtDebitCollection;
import com.crm.cgw.extdebit.ExtDebitServer;
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
public class ExtDebitThread extends DispatcherThread {
	public int					listenPort		= 3000;
	public int					maxConnection	= 10;
	public int					bufferLength	= 65536;
	public int					networkTimeout	= 3000;
	public int					debitTimeout	= 120000;
	public String				keywordPrefix	= "";

	public ExtDebitServer		extDebitServer	= null;

	private long				lastTimelog		= System.currentTimeMillis();
	public ExtDebitCollection	workQueue		= new ExtDebitCollection();

	public void addWork(ExtDebit req) {
		workQueue.put(req);
	}

	public ExtDebit getWork() {
		if (workQueue.size() == 0)
			return null;
		else
			return (ExtDebit) workQueue.remove(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createIntegerParameter("listenPort", "Listen port."));
		vtReturn.add(ThreadUtil.createIntegerParameter("maxConnection", "Maximum connection the server can handle."));
		vtReturn.add(ThreadUtil.createIntegerParameter("bufferLength", "Socket buffer size."));
		vtReturn.add(ThreadUtil.createIntegerParameter("networkTimeout", "Time out to wait for accept connection or read from client."));
		vtReturn.add(ThreadUtil.createIntegerParameter("debitTimeout", "Time to live of trigger (seconds)."));
		vtReturn.add(ThreadUtil.createIntegerParameter("keywordPrefix", ""));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException {
		listenPort = ThreadUtil.getInt(this, "listenPort", 3000);
		maxConnection = ThreadUtil.getInt(this, "maxConnection", 100);
		bufferLength = ThreadUtil.getInt(this, "bufferLength", 1500);
		networkTimeout = ThreadUtil.getInt(this, "networkTimeout", 1000);
		debitTimeout = ThreadUtil.getInt(this, "debitTimeout", 120) * 1000;
		keywordPrefix = ThreadUtil.getString(this, "keywordPrefix", false, "");
		super.fillDispatcherParameter();
	}

	@Override
	public void beforeProcessSession() throws Exception {
		super.beforeProcessSession();

		extDebitServer = new ExtDebitServer(this);

		extDebitServer.start();
	}

	@Override
	public void afterProcessSession() throws Exception {
		try {
			extDebitServer.stop();
		}
		catch (Exception e) {
			debugMonitor(e);
		}
		finally {
			extDebitServer = null;
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
