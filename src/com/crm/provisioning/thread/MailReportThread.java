package com.crm.provisioning.thread;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.crm.kernel.sql.Database;
import com.crm.thread.MailThread;
import com.crm.thread.util.ThreadUtil;
import com.crm.util.DateUtil;
import com.crm.util.StringUtil;
import com.fss.util.AppException;

public class MailReportThread extends MailThread
{
	private Hashtable<String, Integer>	ascsRechargeTrigger		= new Hashtable<String, Integer>();
	private Hashtable<String, Integer>	ascsActivationTrigger	= new Hashtable<String, Integer>();
	private Hashtable<String, Integer>	rtbsRechargeTrigger		= new Hashtable<String, Integer>();
	private Hashtable<String, Integer>	rtbsActivationTrigger	= new Hashtable<String, Integer>();

	private ArrayList<String>			rechargeCosNameList		= new ArrayList<String>();
	private ArrayList<String>			activationCosNameList	= new ArrayList<String>();

	private String						reportTableName			= "trigger_report";

	private Calendar					lastReportTime			= null;

	private String						excelStoredDirectory	= "report";
	private SimpleDateFormat			dateFileFormat			= new SimpleDateFormat("'report_'yyyyMMdd'.xls'");
	private String						timeToCheckForReport	= "12:00:00";
	private String						mailContent				= "";

	private void validateDirectory() throws Exception
	{
		File directory = new File(excelStoredDirectory);
		if (!directory.exists())
		{
			debugMonitor("Directory " + directory + " does not exist.");
			if (!directory.mkdirs())
			{
				throw new Exception("Can not create directory: " + excelStoredDirectory);
			}
			else
			{
				debugMonitor("Directory \"" + directory + "\" has been created.");
			}
		}
		else
		{
			debugMonitor("Directory \"" + directory + "\" exists.");
		}
	}

	private void loadDataFromDB() throws Exception
	{
		ascsRechargeTrigger.clear();
		ascsActivationTrigger.clear();
		rtbsRechargeTrigger.clear();
		rtbsActivationTrigger.clear();

		rechargeCosNameList.clear();
		activationCosNameList.clear();

		String sqlASCSRecharge = "SELECT COS, Count(*) TOTAL FROM RECHARGE_TRIGGER "
								+ " WHERE Recharge_date >= TRUNC(?) "
								+ " AND Recharge_date < TRUNC(?) + 1 "
								+ " GROUP BY COS ORDER BY COS";

		String sqlASCSActivation = "SELECT COS, Count(*) TOTAL FROM ACTIVATION_TRIGGER "
								+ " WHERE Activation_date >= TRUNC(?) "
								+ " AND Activation_date < TRUNC(?) + 1 "
								+ " AND Previous_State = 'Idle' "
								+ " GROUP BY COS ORDER BY COS";

		String sqlRTBSReport = "SELECT DATE_VALUE, TYPE, COS_NAME, VALUE_RTBS "
								+ " FROM " + reportTableName + " WHERE DATE_VALUE >= TRUNC(?) "
								+ " AND DATE_VALUE < TRUNC(?) + 1";
		Connection connection = null;
		PreparedStatement stmtASCSRecharge = null;
		PreparedStatement stmtASCSActivation = null;
		PreparedStatement stmtRTBSReport = null;

		try
		{
			connection = Database.getConnection();

			/**
			 * ASCS Recharge
			 */
			stmtASCSRecharge = connection.prepareStatement(sqlASCSRecharge);
			stmtASCSRecharge.setTimestamp(1, DateUtil.getTimestampSQL(lastReportTime.getTime()));
			stmtASCSRecharge.setTimestamp(2, DateUtil.getTimestampSQL(lastReportTime.getTime()));

			ResultSet rsASCSRecharge = stmtASCSRecharge.executeQuery();

			while (rsASCSRecharge.next())
			{
				String cosName = rsASCSRecharge.getString("COS");
				Integer count = rsASCSRecharge.getInt("TOTAL");

				if (!rechargeCosNameList.contains(cosName))
					rechargeCosNameList.add(cosName);

				ascsRechargeTrigger.put(cosName, count);
			}

			debugMonitor("Load ASCS recharge statistic completed.");

			/**
			 * ASCS Activation
			 */
			stmtASCSActivation = connection.prepareStatement(sqlASCSActivation);
			stmtASCSActivation.setTimestamp(1, DateUtil.getTimestampSQL(lastReportTime.getTime()));
			stmtASCSActivation.setTimestamp(2, DateUtil.getTimestampSQL(lastReportTime.getTime()));

			ResultSet rsASCSActivation = stmtASCSActivation.executeQuery();

			while (rsASCSActivation.next())
			{
				String cosName = rsASCSActivation.getString("COS");
				Integer count = rsASCSActivation.getInt("TOTAL");

				if (!activationCosNameList.contains(cosName))
					activationCosNameList.add(cosName);

				ascsActivationTrigger.put(cosName, count);
			}

			debugMonitor("Load ASCS activation statistic completed.");

			/**
			 * RTBS Trigger
			 */
			stmtRTBSReport = connection.prepareStatement(sqlRTBSReport);
			stmtRTBSReport.setTimestamp(1, DateUtil.getTimestampSQL(lastReportTime.getTime()));
			stmtRTBSReport.setTimestamp(2, DateUtil.getTimestampSQL(lastReportTime.getTime()));

			ResultSet rsRTBSReport = stmtRTBSReport.executeQuery();

			while (rsRTBSReport.next())
			{
				String type = rsRTBSReport.getString("TYPE");
				String cosName = rsRTBSReport.getString("COS_NAME");
				Integer count = rsRTBSReport.getInt("VALUE_RTBS");

				if (cosName != null)
				{

					if (type.toUpperCase().equals(Trigger.TYPE_ACTIVATION.toUpperCase()))
					{
						if (!activationCosNameList.contains(cosName))
							activationCosNameList.add(cosName);

						rtbsActivationTrigger.put(cosName, count);
					}
					else if (type.toUpperCase().equals(Trigger.TYPE_RECHARGE.toUpperCase()))
					{
						if (!rechargeCosNameList.contains(cosName))
							rechargeCosNameList.add(cosName);

						rtbsRechargeTrigger.put(cosName, count);
					}
				}
			}

			debugMonitor("Load RTBS trigger statistic completed.");
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtASCSRecharge);
			Database.closeObject(stmtASCSActivation);
			Database.closeObject(stmtRTBSReport);
			Database.closeObject(connection);
		}
	}

	private WritableCellFormat	titleCellFormat;
	private WritableCellFormat	headerCellFormat;
	private WritableCellFormat	textCellFormat;
	private WritableCellFormat	numberCellFormat;
	private WritableCellFormat	mergeCellFormat;
	private WritableCellFormat	summaryCellFormat;

	private int					colType		= 1;
	private int					colACOS		= 2;
	private int					colRCOS		= 3;
	private int					colAValue	= 4;
	private int					colRValue	= 5;
	private int					colDiff		= 6;

	/**
	 * First row is the title, the header row must be started from 1
	 */
	private int					rowHeader	= 1;

	private String getColumnByIndex(int index)
	{
		return String.valueOf((char) (65 + index));
	}

	private int getMinColumn()
	{
		int min = Math.min(colType, colACOS);
		min = Math.min(min, colRCOS);
		min = Math.min(min, colAValue);
		min = Math.min(min, colRValue);
		min = Math.min(min, colDiff);
		return min;
	}

	private int getMaxColumn()
	{
		int max = Math.max(colType, colACOS);
		max = Math.max(max, colRCOS);
		max = Math.max(max, colAValue);
		max = Math.max(max, colRValue);
		max = Math.max(max, colDiff);
		return max;
	}

	private void createExcelFile(File file) throws RowsExceededException, WriteException, IOException
	{
		String sheetName = "Report";

		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file,
				wbSettings);
		workbook.createSheet(sheetName, 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createFormat();
		createHeader(excelSheet);
		createContent(excelSheet);

		workbook.write();
		workbook.close();
	}

	private void createFormat() throws WriteException
	{
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);

		textCellFormat = new WritableCellFormat(times10pt);
		textCellFormat.setWrap(false);
		textCellFormat.setAlignment(Alignment.LEFT);
		textCellFormat.setShrinkToFit(true);
		textCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

		numberCellFormat = new WritableCellFormat(times10pt);
		numberCellFormat.setWrap(false);
		numberCellFormat.setAlignment(Alignment.RIGHT);
		numberCellFormat.setShrinkToFit(true);
		numberCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

		summaryCellFormat = new WritableCellFormat(times10pt);
		summaryCellFormat.setWrap(false);
		summaryCellFormat.setAlignment(Alignment.RIGHT);
		summaryCellFormat.setShrinkToFit(true);
		summaryCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
		summaryCellFormat.setBackground(Colour.ICE_BLUE);

		WritableFont times10ptBold = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);

		headerCellFormat = new WritableCellFormat(times10ptBold);
		headerCellFormat.setWrap(false);
		headerCellFormat.setAlignment(Alignment.CENTRE);
		headerCellFormat.setShrinkToFit(true);
		headerCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
		headerCellFormat.setBackground(Colour.ICE_BLUE);

		mergeCellFormat = new WritableCellFormat(times10pt);
		mergeCellFormat.setWrap(false);
		mergeCellFormat.setAlignment(Alignment.LEFT);
		mergeCellFormat.setShrinkToFit(true);
		mergeCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		mergeCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

		WritableFont times12ptBold = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);
		titleCellFormat = new WritableCellFormat(times12ptBold);
		titleCellFormat.setWrap(false);
		titleCellFormat.setAlignment(Alignment.CENTRE);
		titleCellFormat.setShrinkToFit(true);
		titleCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

	}

	private void createHeader(WritableSheet sheet) throws RowsExceededException, WriteException
	{
		// Write a headers
		// addCaption(sheet, 1, 1, "DATE VALUE");
		addCaption(sheet, colType, rowHeader, "TYPE");
		addCaption(sheet, colACOS, rowHeader, "ASCS_COS");
		addCaption(sheet, colRCOS, rowHeader, "RTBS_COS");
		addCaption(sheet, colAValue, rowHeader, "ASCS_VALUE");
		addCaption(sheet, colRValue, rowHeader, "RTBS_VALUE");
		addCaption(sheet, colDiff, rowHeader, "DIFF");
	}

	private void createContent(WritableSheet sheet) throws WriteException,
			RowsExceededException
	{

		int minCol = getMinColumn();
		int maxCol = getMaxColumn();

		/**
		 * Title
		 */
		sheet.mergeCells(minCol, 0, maxCol, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String title = sdf.format(lastReportTime.getTime());
		title = "ASCS REPORT - " + title;
		addLabel(sheet, minCol, 0, title, titleCellFormat);

		/**
		 * Fill by row
		 */
		int row = rowHeader + 1;
		int start = row;

		/**
		 * Recharge trigger first
		 */
		String[] rechargeCOSs = rechargeCosNameList.toArray(new String[] {});
		Arrays.sort(rechargeCOSs);
		/**
		 * All COS
		 */
		for (int i = 0; i < rechargeCOSs.length; i++)
		{
			Integer ascsValue = ascsRechargeTrigger.get(rechargeCOSs[i]);
			if (ascsValue != null)
			{
				addLabel(sheet, colACOS, row, rechargeCOSs[i]);
				addNumber(sheet, colAValue, row, ascsValue.intValue());
			}
			else
			{
				addLabel(sheet, colACOS, row, "");
				addNumber(sheet, colAValue, row, 0);
			}

			Integer rtbsValue = rtbsRechargeTrigger.get(rechargeCOSs[i]);
			if (rtbsValue != null)
			{
				addLabel(sheet, colRCOS, row, rechargeCOSs[i]);
				addNumber(sheet, colRValue, row, rtbsValue.intValue());
			}
			else
			{
				addLabel(sheet, colRCOS, row, "");
				addNumber(sheet, colRValue, row, 0);
			}

			/**
			 * F3-G3
			 */
			addFormular(sheet, colDiff, row, getColumnByIndex(colAValue) + (row + 1)
					+ "-"
					+ getColumnByIndex(colRValue) + (row + 1), numberCellFormat);

			row++;
		}

		/**
		 * Merge type row
		 */
		sheet.mergeCells(colType, start, colType, row);
		addLabel(sheet, colType, start, Trigger.TYPE_RECHARGE.toUpperCase(), mergeCellFormat);

		/**
		 * Summary row
		 */
		addLabel(sheet, colACOS, row, "", summaryCellFormat);
		addLabel(sheet, colRCOS, row, "", summaryCellFormat);
		if (row == start)
		{
			addLabel(sheet, colAValue, row, "0", summaryCellFormat);
			addLabel(sheet, colRValue, row, "0", summaryCellFormat);
			addLabel(sheet, colDiff, row, "0", summaryCellFormat);
		}
		else
		{
			// SUM(F{start},F{row-1})
			addFormular(sheet, colAValue, row, "SUM("
					+ getColumnByIndex(colAValue)
					+ (start + 1) + ":"
					+ getColumnByIndex(colAValue)
					+ row + ")", summaryCellFormat);
			// SUM(G{start},G{row-1})
			addFormular(sheet, colRValue, row, "SUM("
					+ getColumnByIndex(colRValue)
					+ (start + 1) + ":"
					+ getColumnByIndex(colRValue)
					+ row + ")", summaryCellFormat);

			addFormular(sheet, colDiff, row, getColumnByIndex(colAValue) + (row + 1)
					+ "-"
					+ getColumnByIndex(colRValue) + (row + 1), summaryCellFormat);
		}

		/**
		 * Add row + 1
		 */
		row++;
		start = row;
		/**
		 * Activation trigger
		 */

		String[] activationCOSs = activationCosNameList.toArray(new String[] {});
		Arrays.sort(activationCOSs);
		/**
		 * All COS
		 */
		for (int i = 0; i < activationCOSs.length; i++)
		{
			Integer ascsValue = ascsActivationTrigger.get(activationCOSs[i]);
			if (ascsValue != null)
			{
				addLabel(sheet, colACOS, row, activationCOSs[i]);
				addNumber(sheet, colAValue, row, ascsValue.intValue());
			}
			else
			{
				addLabel(sheet, colACOS, row, "");
				addNumber(sheet, colAValue, row, 0);
			}

			Integer rtbsValue = rtbsActivationTrigger.get(activationCOSs[i]);
			if (rtbsValue != null)
			{
				addLabel(sheet, colRCOS, row, activationCOSs[i]);
				addNumber(sheet, colRValue, row, rtbsValue.intValue());
			}
			else
			{
				addLabel(sheet, colRCOS, row, "");
				addNumber(sheet, colRValue, row, 0);
			}

			/**
			 * F3-G3
			 */
			addFormular(sheet, colDiff, row, getColumnByIndex(colAValue) + (row + 1)
					+ "-"
					+ getColumnByIndex(colRValue) + (row + 1), numberCellFormat);
			row++;
		}

		/**
		 * Merge type row
		 */
		sheet.mergeCells(colType, start, colType, row);
		addLabel(sheet, colType, start, Trigger.TYPE_ACTIVATION.toUpperCase(), mergeCellFormat);

		/**
		 * Summary row
		 */

		addLabel(sheet, colACOS, row, "", summaryCellFormat);
		addLabel(sheet, colRCOS, row, "", summaryCellFormat);

		if (row == start)
		{
			addLabel(sheet, colAValue, row, "0", summaryCellFormat);
			addLabel(sheet, colRValue, row, "0", summaryCellFormat);
			addLabel(sheet, colDiff, row, "0", summaryCellFormat);
		}
		else
		{
			// SUM(F{start},F{row-1})
			addFormular(sheet, colAValue, row, "SUM("
					+ getColumnByIndex(colAValue)
					+ (start + 1) + ":"
					+ getColumnByIndex(colAValue)
					+ row + ")", summaryCellFormat);
			// SUM(G{start},G{row-1})
			addFormular(sheet, colRValue, row, "SUM("
					+ getColumnByIndex(colRValue)
					+ (start + 1) + ":"
					+ getColumnByIndex(colRValue)
					+ row + ")", summaryCellFormat);

			addFormular(sheet, colDiff, row, getColumnByIndex(colAValue) + (row + 1)
					+ "-"
					+ getColumnByIndex(colRValue) + (row + 1), summaryCellFormat);
		}

		/**
		 * Auto resize column
		 */
		for (int i = minCol + 1; i <= maxCol; i++)
		{
			CellView cellView = sheet.getColumnView(i);
			cellView.setAutosize(true);
			sheet.setColumnView(i, cellView);
		}
	}

	private Cell addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException
	{
		Label label;
		label = new Label(column, row, s, headerCellFormat);
		sheet.addCell(label);
		return label;
	}

	private Cell addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException
	{
		jxl.write.Number number;
		number = new jxl.write.Number(column, row, integer, numberCellFormat);
		sheet.addCell(number);
		return number;
	}

	private Cell addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException
	{
		Label label;
		label = new Label(column, row, s, textCellFormat);
		sheet.addCell(label);
		return label;
	}

	private Cell addLabel(WritableSheet sheet, int column, int row, String s, WritableCellFormat format)
			throws WriteException, RowsExceededException
	{
		Label label;
		label = new Label(column, row, s, format);
		sheet.addCell(label);
		return label;
	}

	private Cell addFormular(WritableSheet sheet, int column, int row, String formular, WritableCellFormat format)
			throws WriteException, RowsExceededException
	{
		StringBuffer buf = new StringBuffer();
		buf.append(formular);
		Formula f = new Formula(column, row, buf.toString(), format);
		sheet.addCell(f);

		return f;
	}


	protected void report(String fileName) throws Exception
	{
		File file = new File(fileName);

		debugMonitor("Connecting to DB to get report in "
				+ StringUtil.format(lastReportTime.getTime(), "dd/MM/yyyy")
				+ " ...");
		loadDataFromDB();
		debugMonitor("Get report completed.");

		debugMonitor("Exporting to excel file ...");
		createExcelFile(file);
		debugMonitor("Exported to: " + fileName);

		String strSubject = StringUtil.format(lastReportTime.getTime(), getSubject());
		//String strContent = "Automatic sent by ASCS.";

		debugMonitor("Ready to send email.");
		sendEmail(strSubject, mailContent, fileName);
		debugMonitor("Completed reporting.");
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		super.beforeProcessSession();

		validateDirectory();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Vector getParameterDefinition()
	{
		Vector vtReturn = new Vector();

		vtReturn.add(ThreadUtil.createTextParameter("excelStoredDirectory", 100,
				"Directory to save excel report file, default \"report\""));
		vtReturn.add(ThreadUtil.createTextParameter("excelFileFormat", 100,
				"Excel file name format, base on date format, default 'report_'yyyyMMdd'.xls'"));
		vtReturn.add(ThreadUtil.createTextParameter("lastReportTime", 100,
				"The last report time, format yyyyMMddHHmmss"));
		vtReturn.add(ThreadUtil.createTextParameter("rtbsReportTable", 100,
				"The table of rtbs report"));
		vtReturn.add(ThreadUtil
				.createTextParameter(
						"timeToCheckForReport",
						100,
						"Time to check for report in each day (Because they need time to fill RTBS report table of previous day(HHmmss), default 120000)"));
		vtReturn.add(ThreadUtil
				.createTextParameter(
						"mailContent",
						4000,
						"Email content, limited in 4000 characters."));
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	@Override
	public void fillParameter() throws AppException
	{
		// TODO Auto-generated method stub
		super.fillParameter();
		excelStoredDirectory = ThreadUtil.getString(this, "excelStoredDirectory", false, "report");
		reportTableName = ThreadUtil.getString(this, "rtbsReportTable", false, "trigger_report");
		String excelFileFormat = ThreadUtil.getString(this, "excelFileFormat", false, "'report_'yyyyMMdd'.xls'");

		dateFileFormat = new SimpleDateFormat(excelFileFormat);

		String lastFormatTime = ThreadUtil.getString(this, "lastReportTime", false, "");

		lastReportTime = Calendar.getInstance();
		if ("".equals(lastFormatTime))
		{
			lastReportTime.add(Calendar.DAY_OF_MONTH, -1);
		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try
			{
				Date lastReportDate = sdf.parse(lastFormatTime);

				lastReportTime.setTime(lastReportDate);
				lastReportTime.add(Calendar.DAY_OF_MONTH, 1);
			}
			catch (ParseException e)
			{
				throw new AppException("Last report time parsing error.");
			}
		}

		timeToCheckForReport = ThreadUtil.getString(this, "timeToCheckForReport", false, "12:00:00");
		mailContent = ThreadUtil.getString(this, "mailContent", false, "");
	}

	@Override
	public void doProcessSession() throws Exception
	{
		try
		{
			/**
			 * Need to wait after timeToCheckForReport to get report of previous
			 * day
			 */
			Calendar checkTime = (Calendar) lastReportTime.clone();
			checkTime.add(Calendar.DAY_OF_MONTH, 1);
			String strCheckTime = StringUtil.format(checkTime.getTime(), "yyyyMMdd");
			strCheckTime = strCheckTime + timeToCheckForReport;

			Calendar now = Calendar.getInstance();
			String strNow = StringUtil.format(now.getTime(), "yyyyMMddHHmmss");

			if (strNow.compareTo(strCheckTime) > 0)
			{

				String fileName = dateFileFormat.format(lastReportTime.getTime());

				if (excelStoredDirectory.endsWith("/") || excelStoredDirectory.endsWith("\\"))
					fileName = excelStoredDirectory + fileName;
				else
					fileName = excelStoredDirectory + "/" + fileName;

				File file = new File(fileName);

				if (file.exists())
				{
					debugMonitor("File " + fileName + " exists, this day will be ignored.");
				}
				else
				{
					report(fileName);
				}

				/**
				 * Stored last report time
				 */
				lastReportTime.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
				lastReportTime.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
				lastReportTime.set(Calendar.SECOND, now.get(Calendar.SECOND));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				mprtParam.setProperty("lastReportTime", sdf.format(lastReportTime.getTime()));
				storeConfig();
			}
			else
			{
				debugMonitor("Invalid report time, skipping."
						+ " Next check time after " + strCheckTime + " (yyyyMMddHHmmss)");
			}
		}
		catch (Exception e)
		{
			debugMonitor(e);
		}

	}

	public static void main(String[] args) throws Exception
	{
		MailReportThread tr = new MailReportThread();

		tr.doProcessSession();

		// System.out.println(String.valueOf((char) 90));
	}
}
