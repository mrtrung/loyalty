package com.crm.ascs.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.crm.ascs.net.CDR;
import com.crm.kernel.sql.Database;

public class CDRImpl
{
	public static void insertCDR(CDR[] cdrs) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();
			insertCDR(connection, cdrs);
			
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			Database.closeObject(connection);
		}
	}

	public static void insertCDR(Connection connection, CDR[] cdrs) throws Exception
	{
		PreparedStatement stmtCDR = null;
		
		try
		{
			int count = 0;

			String SQL = "insert into TELCOCDR(id, streamno, createdate, timestamp, chargeresult, isdn, " +
					"spid, serviceid, productid_telco, productid, chargemode, begintime, endtime, " +
					"subtype, cost, b_isdn, status, telcoID)" +
					" VALUES(CDR_SEQ.nextVal,?,sysdate,?,?,?,?,?,?,1,?,?,?,?,?,?,1,1)";

			stmtCDR = connection.prepareStatement(SQL);

			for (CDR cdr : cdrs)
			{
				stmtCDR.setString(1, cdr.getStreamNo());
				stmtCDR.setString(2, cdr.getTimeStamp());
				stmtCDR.setString(3, cdr.getChargeResult());
				stmtCDR.setString(4, cdr.getMsIsdn());
				stmtCDR.setString(5, cdr.getSpID());
				stmtCDR.setString(6, cdr.getServiceID());
				stmtCDR.setString(7, cdr.getProductID_telco());
				stmtCDR.setString(8, cdr.getChargeMode());
				stmtCDR.setString(9, cdr.getBeginTime());
				stmtCDR.setString(10, cdr.getEndTime());
				stmtCDR.setString(11, cdr.getPayType());
				stmtCDR.setString(12, cdr.getCost());
				stmtCDR.setString(13, cdr.getB_Isdn());
				
				stmtCDR.addBatch();
				count++;

				if (count >= 50)
				{
					stmtCDR.executeBatch();
					count = 0;
				}
			}
			if (count > 0)
			{
				stmtCDR.executeBatch();
			}
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			Database.closeObject(stmtCDR);
		}

	}
}
