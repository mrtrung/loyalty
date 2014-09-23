/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  Vietnamobile. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : VasmanConnection.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : hungdt
 * SYSTEM NAME            : NEW VASMAN
 * MODULE NAME            : Webservice New Vasman
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Oct 2, 2012 11:24:31 AM 
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	            Author	      Version        Description
 * -----------------------------------------------------------------------------------
 * Oct 2, 2012      hungdt        1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.elcom.iwebservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.crm.kernel.sql.Database;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.elcom.bean.ActivationLog;

/**
 * @author hungdt
 * 
 */
public class VasmanConnection extends SubscriberProductImpl {


	public static List<SubscriberProduct> checkAllVasStatus(String isdn) throws Exception {
		List<SubscriberProduct> lst = new ArrayList<SubscriberProduct>();
		PreparedStatement stmtCheck = null;
		ResultSet rsStatus = null;
		SubscriberProduct result = null;
		Connection connection = null;
		try {
			connection = Database.getConnection();
			String sql = "Select * from SubscriberProduct where isdn = ?  and "
					+ CONDITION_ACTIVE + " and "+CONDITION_BARRING+" order by registerDate desc";
			stmtCheck = connection.prepareStatement(sql);
			stmtCheck.setString(1, isdn);
			rsStatus = stmtCheck.executeQuery();
			while (rsStatus.next()) {
				result = getProduct(rsStatus);
				lst.add(result);
			}
		} catch (Exception e) {
			throw e;
		}
		finally
		{
			Database.closeObject(stmtCheck);
			Database.closeObject(rsStatus);
			Database.closeObject(connection);
		}
		return lst;
	}

	public static void addLog(ActivationLog log)
			throws Exception {

		PreparedStatement stmtLog = null;
		Connection connection = null;
		try {
			
			connection = Database.getConnection();
			String sql = "Insert into Activation (requestId, groupId, companyId, userId, userName, createdate, modifieddate, processdate,"
					+ " requestDate, responseDate, channel, serviceAddress, sourceAddress, destAddress, provisioningType, provisioningId, productId, "
					+ "commandId, keyword, request, response, responseCode, description, status) "
					+ "Values "
					+ "(ACTIVATE_SEQ.nextval,?,?,?,?,sysdate,sysdate,sysdate,sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			stmtLog = connection.prepareStatement(sql);

			stmtLog.setInt(1, 0);
			stmtLog.setInt(2, 0);
			stmtLog.setLong(3, log.getUserId());
			stmtLog.setString(4, log.getUsername());
			stmtLog.setString(5, log.getChannel());
			stmtLog.setString(6, log.getServiceAddress());
			stmtLog.setString(7, log.getSourceAddress());
			stmtLog.setString(8, log.getDestinationAddress());
			stmtLog.setString(9, log.getProvisioningType());
			stmtLog.setString(10, log.getPromotionId());
			stmtLog.setString(11, log.getProductId());
			stmtLog.setString(12, log.getCommandId());
			stmtLog.setString(13, log.getKeyword());
			stmtLog.setString(14, "");
			stmtLog.setString(15, "");
			stmtLog.setString(16, log.getResponseCode());
			stmtLog.setString(17, "");
			stmtLog.setString(18, "");

			stmtLog.execute();

		} catch (Exception e) {
			throw e;

		}
		finally
		{
			Database.closeObject(stmtLog);
			Database.closeObject(connection);
		}

	}
}
