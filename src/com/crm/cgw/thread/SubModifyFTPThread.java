/**
 * 
 */
package com.crm.cgw.thread;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.crm.cgw.ftp.CdrInput;
import com.crm.cgw.util.FileUtils;
import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

/**
 * @author hungdt
 * 
 */
public class SubModifyFTPThread extends DispatcherThread
{
	public String			cdrFolder			= "";
	public String			cdrBackupFolder	= "";
	public String			cdrCPNameDownload	= "";
	public int			loadInterval		= 0;
	public String			cdrPreFile		= "charggw";
	public String			cdrExtFile		= "";
	public String			cdrCollumnSeparate	= ",";
	public String			keywordPrefix		= "";
	public int			requestTimeout		= 0;
	

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createIntegerParameter("cdrFolder", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("cdrBackupFolder", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("cdrCPNameDownload",
				""));
		vtReturn.add(ThreadUtil.createIntegerParameter("loadInterval", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("cdrPreFile", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("cdrExtFile", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("cdrCollumnSeparate",
				""));
		vtReturn.add(ThreadUtil.createIntegerParameter("keywordPrefix", ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("requestTimeout", ""));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillDispatcherParameter() throws AppException
	{
		cdrFolder = ThreadUtil.getString(this, "cdrFolder", false, "");
		cdrBackupFolder = ThreadUtil.getString(this, "cdrBackupFolder",
				false, "");
		cdrCPNameDownload = ThreadUtil.getString(this, "cdrCPNameDownload",
				false, "");
		loadInterval = ThreadUtil.getInt(this, "loadInterval", 3000);
		cdrPreFile = ThreadUtil.getString(this, "cdrPreFile", false, "");
		cdrExtFile = ThreadUtil.getString(this, "cdrExtFile", false, "");
		cdrCollumnSeparate = ThreadUtil.getString(this, "cdrCollumnSeparate",
				false, "");
		keywordPrefix = ThreadUtil
				.getString(this, "keywordPrefix", false, "");
		requestTimeout = ThreadUtil.getInt(this, "requestTimeout", 3000);
		super.fillDispatcherParameter();
	}

	

}
