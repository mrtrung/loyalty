package com.crm.provisioning.thread;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.crm.kernel.sql.Database;
import com.crm.thread.MailThread;
import com.crm.util.StringUtil;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;

public class WeeklyReport extends MailThread
{
	protected String folderPath;
	protected String SQLWeeklyReport = "";
	protected String SQLWeeklyReportUn = "";
	protected String productList = "";
	protected String content = "";

	protected PreparedStatement stmtWeeklyReport = null;
	protected PreparedStatement stmtWeeklyReportUn = null;

	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private Connection connection = null;
	{

	}

	// //////////////////////////////////////////////////////
	// Override
	// //////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.addElement(createParameterDefinition("FolderPath", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("SQLWeeklyReport", "",
				ParameterType.PARAM_TEXTAREA_MAX, "10000", ""));
		vtReturn.addElement(createParameterDefinition("SQLWeeklyReportUn", "",
				ParameterType.PARAM_TEXTAREA_MAX, "10000", ""));
		vtReturn.addElement(createParameterDefinition("ProductList", "",
				ParameterType.PARAM_TEXTAREA_MAX, "100", ""));
		vtReturn.addElement(createParameterDefinition("MailContent", "",
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
			super.fillParameter();
			// Fill parameter
			folderPath = loadMandatory("FolderPath");
			SQLWeeklyReport = loadMandatory("SQLWeeklyReport");
			SQLWeeklyReportUn = loadMandatory("SQLWeeklyReportUn");
			productList = loadMandatory("ProductList");
			content = loadMandatory("MailContent");
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
//			String strSQL = SQLWeeklyReport;
			stmtWeeklyReport = connection.prepareStatement(SQLWeeklyReport);
			stmtWeeklyReportUn = connection.prepareStatement(SQLWeeklyReportUn);

//			SQLWeeklyReport = "";
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
			Database.closeObject(stmtWeeklyReport);
			Database.closeObject(stmtWeeklyReportUn);
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
			Date date = new Date();
			
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

			File file = new File(folderPath);
			if (!file.exists())
			{
				file.mkdirs();
			}

			String WeeklyReportFilePath = "";
			String WeeklyReportUnFilePath = "";

			if (folderPath.endsWith("/"))
			{
				WeeklyReportFilePath = folderPath + "WeeklyReport "+ df.format(date) + ".xls";
				WeeklyReportUnFilePath = folderPath + "WeeklyReportUn "+ df.format(date) + ".xls";
			}
			else
			{
				WeeklyReportFilePath = folderPath + "/WeeklyReport "+ df.format(date) + ".xls";
				WeeklyReportUnFilePath = folderPath + "/WeeklyReportUn "+ df.format(date) + ".xls";
			}
			
			String[] arrProduct = StringUtil.toStringArray(productList, ";");
			String[] arrSubject = StringUtil.toStringArray(getSubject(), ";");
//			String[] arrSender = StringUtil.toStringArray(getSender(), ";");
			for (int i=0; i<arrProduct.length; i++)
			{
				writeExcelFile(WeeklyReportFilePath, "Weekly Report Register",
						"Weekly Report Register", " Point Level, No of Subs, Max Point, Min Point, Average (AONET+CUTOVER)" +
								",Average TOPUP, Average USAGE", stmtWeeklyReport);
				
				writeExcelFile(WeeklyReportUnFilePath, "Weekly Report Unregister",
						"Weekly Report Unregister", " Point Level, No of Subs, Max Point, Min Point, Average (AONET+CUTOVER)" +
								",Average TOPUP, Average USAGE", stmtWeeklyReportUn);
				
				// Send mail
			
				String strFileName = WeeklyReportFilePath + ";" + WeeklyReportUnFilePath;

				sendEmail(arrSubject[i] + " Date: " + df.format(date), content, strFileName);
				
				logMonitor("sendEmail successfully!");
			}
			
			storeConfig();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
		}
	}

	public void writeExcelFile(String outputFile, String sheetName,
			String strHeader, String strSubHeader, PreparedStatement obj)
			throws IOException, WriteException
	{
		try
		{
			File file = new File(outputFile);
			WorkbookSettings wbSettings = new WorkbookSettings();

			wbSettings.setLocale(new Locale("en", "EN"));

			WritableWorkbook workbook = Workbook.createWorkbook(file,
					wbSettings);
			workbook.createSheet(sheetName, 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			createLabel(excelSheet, strHeader, strSubHeader);
			createContent(excelSheet, 5, obj);

			workbook.write();
			workbook.close();
		}
		catch (Exception ex)
		{
			debugMonitor("Loi xay ra khi tao file excel:" + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void createLabel(WritableSheet sheet, String strHeader,
			String strSubHeader) throws WriteException
	{
		// Lets create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format
		times = new WritableCellFormat(times10pt);
		Border border = Border.ALL;
		BorderLineStyle lineStyle = BorderLineStyle.THIN;
		times.setBorder(border, lineStyle);

		// Lets automatically wrap the cells
		times.setWrap(true);

		// Create create a bold font with no underlines
		WritableFont times10ptBoldUnderline = new WritableFont(
				WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		timesBoldUnderline.setBorder(border, lineStyle);

		// Lets automatically wrap the cells
		timesBoldUnderline.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		// Write a few headers
		sheet.mergeCells(0, 0, 2, 0);
		addCaption(sheet, 0, 0, strHeader);
		addCaption(sheet, 0, 2, "Date");
		sheet.mergeCells(1, 2, 2, 2);
		Date date = new Date();
		DateFormat obj = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		addCaption(sheet, 1, 2, obj.format(date));
		String[] array = strSubHeader.split(",");
		for (int i = 0; i < array.length; i++)
		{
			addCaption(sheet, i, 4, array[i]);
		}
	}

	private void createContent(WritableSheet sheet, int StartRow,
			PreparedStatement object) throws WriteException, RowsExceededException
	{
		int row = StartRow;
		ResultSet result = null;
		
		ArrayList<WeeklyData> arrList = new ArrayList<WeeklyData>();
		int[] arrayOfLevel = {500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000};
		int allSubs = 0;
		int maxAmount = 0;
		int minAmount = 0;
		int level	= 0;
		int i =0, k = 0, l = 0; 
		
		try
		{
	        result = object.executeQuery();
	        
			while (result.next())
			{
				WeeklyData weeklyData = new WeeklyData();
				
				weeklyData.setLevel(result.getInt("GroupLevel"));
				weeklyData.setAllSubs(result.getInt("AllSub"));
				weeklyData.setMaxAmount(result.getInt("MaxAmount"));
				weeklyData.setMinAmount(result.getInt("MinAmount"));
				weeklyData.setAction(result.getString("action"));
				weeklyData.setAvgPoint(result.getInt("avgpoint"));
				
				arrList.add(weeklyData);
			}
			while (i < arrList.size())
			{
				WeeklyData obj = arrList.get(k);

				for (int j = 0; j< arrayOfLevel.length; j++)
				{
					if (arrayOfLevel[l] == (obj.getLevel()))
					{
						level = arrayOfLevel[l];
						
						allSubs = obj.getAllSubs();
						
						minAmount = obj.getMinAmount();
						
						maxAmount = obj.getMaxAmount();
						
						break;
					}
					else
					{
						j++;
						l++;
					}
				}
				int avg_aonet = 0,avg_cutover = 0,avg_topup = 0,avg_usage = 0;
				
				int flag = 0;
				while (level == (obj.getLevel())&&(flag==0))
				{
					if (obj.getAction().equals("AONET"))
					{
						avg_aonet = obj.getAvgPoint();
						k++;
					}
					else if (obj.getAction().equals("CUTOVER"))
					{
						avg_cutover = obj.getAvgPoint();
						k++;
					}
					else if (obj.getAction().equals("TOPUP"))
					{
						avg_topup = obj.getAvgPoint();
						k++;
					}
					else if (obj.getAction().equals("USAGE"))
					{
						avg_usage = obj.getAvgPoint();
						k++;
					}
					
					i++;
					if( (i < arrList.size()) && (level == obj.getLevel()))
					{
						try
						{
							obj = arrList.get(i);
							
						}catch(Exception e){
							
							flag = 1;
							break;
						}
					}
					else
						break;
					
				}
				
//				logMonitor("level "+ l+": " + level );
//				logMonitor("allSubs "+ l+": " +allSubs );
//				logMonitor("maxAmount "+ l+": " + maxAmount );
//				logMonitor("minAmount "+ l+": " + minAmount );
//				logMonitor("avg_aonet "+ l+": " + avg_aonet );
//				logMonitor("avg_topup "+ l+": " + avg_topup );
//				logMonitor("avg_usage "+ l+": " + avg_usage );
//				logMonitor("avg_cutover "+l+": " + avg_cutover );
			
				l++;
			
				addLabel(sheet, 0, row, Integer.toString(level));
				addLabel(sheet, 1, row, Integer.toString(allSubs));
				addLabel(sheet, 2, row, Integer.toString(maxAmount));
				addLabel(sheet, 3, row, Integer.toString(minAmount));
				addLabel(sheet, 4, row, Integer.toString(avg_aonet+avg_cutover));
				addLabel(sheet, 5, row, Integer.toString(avg_topup));
				addLabel(sheet, 6, row, Integer.toString(avg_usage));
				
				row++;
			}
		}
		catch (Exception ex)
		{
			debugMonitor("Loi xay ra khi tao noi dung file excel:"
					+ ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{

			try
			{
				result.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException
	{
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		sheet.addCell(label);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException
	{
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}
}
