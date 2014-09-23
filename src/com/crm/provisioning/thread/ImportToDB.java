package com.crm.provisioning.thread;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;

import com.crm.kernel.sql.Database;
import com.crm.thread.DispatcherThread;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class ImportToDB extends DispatcherThread
{
	
	private String folderPath = "";
	public Connection connection = null;
	private PreparedStatement stmtSql = null;
	
	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.addElement(createParameterDefinition("FolderPath", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addAll(super.getParameterDefinition());
		
		return vtReturn;
	}

	// //////////////////////////////////////////////////////
	// Override      
	// //////////////////////////////////////////////////////
	public void fillParameter() throws AppException
	{
		try
		{
//			super.fillParameter();
			// Fill parameter
			folderPath = loadMandatory("FolderPath");
		
		}
		catch (AppException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		try
		{
			connection = Database.getConnection();
			
			stmtSql = connection.prepareStatement("Insert into table_temp where isdn = ?");
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	// //////////////////////////////////////////////////////
	// after process session
	// Author : ThangPV
	// Created Date : 16/09/2004
	// //////////////////////////////////////////////////////
	public void afterProcessSession() throws Exception
	{
		try
		{
			Database.closeObject(stmtSql);
			Database.closeObject(connection);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	public void doProcessSession() throws Exception
	{
		try
		{
			  FileInputStream fstream = new FileInputStream(folderPath);
			  
			  DataInputStream in = new DataInputStream(fstream);

			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  
			  String strLine;
			  
			  int counter = 0;
			  while ((strLine = br.readLine()) != null) 
			  {
				  stmtSql.setString(1, strLine);
				  
				  stmtSql.addBatch();
				  
				  counter ++;
				  
				  while(counter >= 200)
				  {
					  stmtSql.execute();
					  counter = 0;
				  }
			  }
			  if(counter > 0)
				  
				  stmtSql.execute();
			  
			  in.close();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
		}
	}


}
