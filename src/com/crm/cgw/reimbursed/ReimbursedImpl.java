/**
 * 
 */
package com.crm.cgw.reimbursed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import com.comverse_in.prepaid.ccws.SubscriberEntity;
import com.crm.cgw.ftp.CdrOutput;
import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.impl.CommandImpl;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.util.DateUtil;

/**
 * @author hungdt
 * 
 */
public class ReimbursedImpl extends CommandImpl
{

	public void exportCDR(CommandInstance instance, ProvisioningCommand provisioningCommand, CommandMessage request) throws Exception
	{
		try
		{
			ProductEntry product = ProductFactory.getCache().getProduct(request.getProductId());
			CdrOutput output = new CdrOutput();
			
			output.setSeq_No(request.getOrderId());
			if(request.getCont_code() == Constants.CONTENT_CODE_MOBILE_TERMINATED_SMS || request.getCont_code() == Constants.CONTENT_CODE_CALL_FOWARD)
			{
				output.setA_Party(request.getServiceAddress());
				output.setB_Party(request.getIsdn());
			}
			else
			{
				output.setB_Party(request.getServiceAddress());
				output.setA_Party(request.getIsdn());
			}
			
			output.setDate(request.getOrderDate());
			output.setDescription(request.getDescription());
			output.setCont_Prov_Id(request.getProductId());
			output.setCont_Prov_Name(product.getAlias());
			output.setCont_Code(request.getCont_code());
			output.setCont_Type(request.getCont_type());
			output.setCurrentcy(request.getCurrency());
			output.setAmount(String.valueOf(request.getAmount()));
			
			importCDR(output);
			
		}
		catch (Exception e)
		{
			processError(instance, provisioningCommand, request, e);
		}
		
	}
	
	public static void importCDR(CdrOutput output) throws Exception
	{
		PreparedStatement stmtCdr = null;
		ResultSet rsCdr = null;
		Connection connection = null;
		try
		{
			connection = Database.getConnection();
			String sql = "insert into cdr_export(id, a_party, b_party, reqdate, description, cont_prov_id, cont_prov_name"
					+ ", cont_code, cont_type, curency, amount) values (?,?,?,?,?,?,?,?,?,?,?)";
			stmtCdr = connection.prepareStatement(sql);
			stmtCdr.setLong(1, output.getSeq_No());
			stmtCdr.setString(2, output.getA_Party());
			stmtCdr.setString(3, output.getB_Party());
			stmtCdr.setDate(4, DateUtil.getDateSQL(output.getDate()));
			stmtCdr.setString(5, output.getDescription());
			stmtCdr.setLong(6, output.getCont_Prov_Id());
			stmtCdr.setString(7, output.getCont_Prov_Name());
			stmtCdr.setInt(8, output.getCont_Code());
			stmtCdr.setInt(9, output.getCont_Type());
			stmtCdr.setString(10, output.getCurrentcy());
			stmtCdr.setString(11, output.getAmount());
			
			stmtCdr.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtCdr);
			Database.closeObject(rsCdr);
			Database.closeObject(connection);
		}
	}
}
