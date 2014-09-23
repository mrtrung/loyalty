/**
 * 
 */
package com.crm.cgw.thread;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

/**
 * @author hungdt
 * 
 */
public class FTPGatewayThread extends DispatcherThread {
	public int			ftpUse				= 0;
	public String		ftpAddress			= "";
	public int			ftpPort				= 0;
	public String		ftpUser				= "";
	public String		ftpPass				= "";
	public String		ftpServFolder		= "";
	public String		ftpClientFolder		= "";
	public String		ftpCPNameDownload	= "";
	public int			ftpDownloadInterval	= 0;
	public String		ftpPreFile			= "charggw";
	public String		ftpExtFile			= "";
	public String		ftpCollumnSeparate	= ",";
	public FTPClient	ftp					= null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createIntegerParameter("ftpUse", "The billing runing ftp mode, that must put cdr file in remote server"));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpAddress", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpPort", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpUser", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpPass", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpServFolder", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpClientFolder", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpCPNameDownload", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpDownloadInterval", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpPreFile", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpExtFile", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("ftpCollumnSeparate", ""));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillDispatcherParameter() throws AppException {
		ftpUse = ThreadUtil.getInt(this, "ftpUse", 3000);
		ftpAddress = ThreadUtil.getString(this, "ftpAddress", false, "");
		ftpPort = ThreadUtil.getInt(this, "ftpPort", 3000);
		ftpUser = ThreadUtil.getString(this, "ftpUser", false, "");
		ftpPass = ThreadUtil.getString(this, "ftpPass", false, "");
		ftpServFolder = ThreadUtil.getString(this, "ftpServFolder", false, "");
		ftpClientFolder =
				ThreadUtil.getString(this, "ftpClientFolder", false, "");
		ftpCPNameDownload =
				ThreadUtil.getString(this, "ftpCPNameDownload", false, "");
		ftpDownloadInterval =
				ThreadUtil.getInt(this, "ftpDownloadInterval", 3000);
		ftpPreFile = ThreadUtil.getString(this, "ftpPreFile", false, "");
		ftpExtFile = ThreadUtil.getString(this, "ftpExtFile", false, "");
		ftpCollumnSeparate =
				ThreadUtil.getString(this, "ftpCollumnSeparate", false, "");
		super.fillDispatcherParameter();
	}

	public void beforeProcessSession() throws Exception {
		try {
			ftp = new FTPClient();
			ftp.addProtocolCommandListener(new PrintCommandListener());
			int reply;
			ftp.connect(ftpAddress);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
			}
			ftp.login(ftpUser, ftpPass);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
		}
		catch (Exception e) {
			debugMonitor(e);
		}

	}

	public void afterProcessSession() throws Exception {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			}
			catch (IOException f) {
				debugMonitor(f);
			}
		}
	}


}
