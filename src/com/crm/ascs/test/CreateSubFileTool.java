package com.crm.ascs.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class CreateSubFileTool extends DispatcherThread
{
	private String	filePath	= "";
	private String	fromIsdn	= "";
	private String	toIsdn		= "";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400, "Imported File Path."));

		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillDispatcherParameter() throws AppException
	{
		filePath = ThreadUtil.getString(this, "filePath", false, "");

		super.fillDispatcherParameter();
	}
}
