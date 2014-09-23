/**
 * 
 */
package com.crm.cgw.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.crm.kernel.sql.Database;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

/**
 * @author hungdt
 * 
 */
public class ExportCDRThread extends DispatcherThread
{
	private PreparedStatement				_stmtCDR			= null;
	private PreparedStatement				stmtRemoveRequest	= null;
	private PreparedStatement				stmtProductRequest	= null;
	private ResultSet						_rSet			= null;
	private ResultSet						_rSetPro			= null;

	protected String						fileName			= "";
	protected String						backupDir			= "";
	protected String						serverDir			= "";
	protected String						serverIP			= "";
	protected String						serverUsername		= "";
	protected String						serverPassword		= "";
	protected String						_strSQL			= "";
	protected String						type			 = "";
	protected ConcurrentHashMap<Long, String>	chpProduct		= null;
	//protected String						alias_			= "";

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();
		vtReturn.addElement(ThreadUtil.createTextParameter("SQL", 100, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("fileName", 100,""));
		vtReturn.addElement(ThreadUtil.createTextParameter("backupDir", 100,""));
		vtReturn.addElement(ThreadUtil.createTextParameter("fileServerDir",100, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("fileServerIP",100, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("fileServerUsername", 100, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("fileServerPassword", 100, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("mailContent",1000, ""));
		vtReturn.addElement(ThreadUtil.createTextParameter("type",1000, ""));

		vtReturn.addAll(super.getParameterDefinition());

		return vtReturn;
	}

	public void fillParameter() throws AppException
	{
		super.fillParameter();

		_strSQL = ThreadUtil.getString(this, "SQL", false, "");
		fileName = ThreadUtil.getString(this, "fileName", false, "");
		backupDir = ThreadUtil.getString(this, "backupDir", false, "");
		serverDir = ThreadUtil.getString(this, "fileServerDir", false, "");
		serverIP = ThreadUtil.getString(this, "fileServerIP", false, "");
		serverUsername = ThreadUtil.getString(this, "fileServerUsername",
				false, "");
		serverPassword = ThreadUtil.getString(this, "fileServerPassword",
				false, "");
		type = ThreadUtil.getString(this, "type",
				false, "");
	}

	public void beforeProcessSession() throws Exception
	{
		chpProduct = new ConcurrentHashMap<Long, String>();
		try
		{
			
			_stmtCDR = getConnection().prepareStatement(_strSQL);
			String strSQL = "Delete cdr_export where id = ?";
			String sql = "select productid, alias_ from productEntry where productType = ?";
			stmtRemoveRequest = getConnection().prepareStatement(strSQL);

			stmtProductRequest = getConnection().prepareStatement(sql);
			stmtProductRequest.setString(1, type);
			_rSetPro = stmtProductRequest.executeQuery();

			while (_rSetPro.next())
			{
				chpProduct.put(_rSetPro.getLong("productid"),
						_rSetPro.getString("alias_"));
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			super.beforeProcessSession();
		}
	}

	public void afterProcessSession() throws Exception
	{
		try
		{
			Database.closeObject(_stmtCDR);
			Database.closeObject(_rSetPro);
		}
		finally
		{
			super.afterProcessSession();
		}
	}

	public void doProcessSession() throws Exception
	{
		long orderId = 0;
		long cpId = 0;
		try
		{

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

			String exportingFile = serverDir;
			String backupFile = backupDir;
			String strFileName = "";

			Set<Long> keys = chpProduct.keySet();

			Iterator<Long> iterator = keys.iterator();
			
			while (iterator.hasNext())
			{
				cpId = iterator.next();
				_stmtCDR.setLong(1, cpId);
				strFileName = fileName.replaceAll("%t%", sdf.format(cal.getTime())).replaceAll("%n%", chpProduct.get(cpId));
				if (!exportingFile.endsWith("\\") && !exportingFile.endsWith("/"))
					exportingFile = exportingFile + "/";

				exportingFile = exportingFile + strFileName;
				backupFile = backupFile + strFileName;

				_rSet = _stmtCDR.executeQuery();

				StringBuffer strChargeInfo = new StringBuffer();
				while (_rSet.next())
				{
					orderId = _rSet.getLong(1);
					Date reqDate = _rSet.getDate("reqdate");
					
					strChargeInfo.append(_rSet.getLong("id") + ","
							+ _rSet.getString("a_party") + ","
							+ _rSet.getString("b_party") + ","
							+ sdfDate.format(reqDate)
							+ ","
							+ sdfTime.format(reqDate)
							+ "," + _rSet.getString("description") + ","
							+ _rSet.getInt("cont_prov_id") + ","
							+ _rSet.getString("cont_prov_name") + ","
							+ _rSet.getInt("cont_code") + ","
							+ _rSet.getInt("cont_type") + ","
							+ _rSet.getString("curency") + ","
							+ _rSet.getString("amount") + "\n");

					stmtRemoveRequest.setLong(1, _rSet.getLong("id"));
					stmtRemoveRequest.execute();
				}

				if (strChargeInfo.length() > 0)
				{

					File file = new File(exportingFile);
					file.setExecutable(true);
					file.setReadable(true);
					file.setWritable(true);
					BufferedWriter out = new BufferedWriter(
							new FileWriter(file));
					out.write(strChargeInfo.toString());
					out.close();
					// backup file
					File file1 = new File(backupFile);
					BufferedWriter out1 = new BufferedWriter(
							new FileWriter(file1));
					file1.setExecutable(true);
					file1.setReadable(true);
					file1.setWritable(true);
					out1.write(strChargeInfo.toString());
					out1.close();

					logMonitor("CREATE FILE " + strFileName + " DONE.");
				}
				else
				{
					logMonitor("NO DATA EXPORT.");
				}
			}

		}
		catch (Exception e)
		{
			logMonitor(e);
			e.printStackTrace();
		}
		finally
		{
			_rSet.close();
		}
	}

}
