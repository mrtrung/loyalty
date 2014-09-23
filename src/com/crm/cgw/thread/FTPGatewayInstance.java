/**
 * 
 */
package com.crm.cgw.thread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPFile;

import com.crm.thread.DispatcherInstance;

/**
 * @author hungdt
 *
 */
public class FTPGatewayInstance extends DispatcherInstance
{

	public FTPGatewayInstance() throws Exception
	{
		super();
	}
	
	public FTPGatewayThread getDispatcher()
	{
		return (FTPGatewayThread) dispatcher;
	}
	
	public void doProcessSession()
	{
		try
		{
			String[] cpName = getDispatcher().ftpCPNameDownload.split(getDispatcher().ftpCollumnSeparate);
			String remoteFile = "";
			String localFile = "";
			
			for(String cp : cpName)
			{
				remoteFile = getDispatcher().ftpServFolder + "/" + cp + "/";
				localFile = getDispatcher().ftpClientFolder + "/" + cp + "/";
				FTPFile[] files = getDispatcher().ftp.listFiles(remoteFile);
				logMonitor("Download file from " + cp);
				for(FTPFile file : files)
				{
					if(file.getName().startsWith(getDispatcher().ftpPreFile) && file.getName().contains(getDispatcher().ftpExtFile))
					{
						String remoteFile1 = file.getName();
						File downloadFile = new File(localFile+file.getName());
						OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile));
						getDispatcher().ftp.retrieveFile(remoteFile1, outputStream1);
						getDispatcher().ftp.deleteFile(remoteFile1);
						logMonitor("Download file " + file.getName() + " success.");
						outputStream1.close();
					}
				}
			}
		}
		catch (Exception e)
		{
			logMonitor(e);
		}
		
	}
}
