/**
 * 
 */
package com.crm.cgw.tariff.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.crm.kernel.index.BinaryIndex;
import com.crm.kernel.index.IndexNode;
import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.fss.util.AppException;

/**
 * @author hungdt
 * 
 */
public class TariffCache {
	private BinaryIndex	tariff		= new BinaryIndex();

	private Date		cacheDate	= null;

	public synchronized void loadCache() throws Exception {
		Connection connection = null;

		try {
			

			log.debug("Caching charggw tariff plan ...");

			connection = Database.getConnection();
			loadTariff(connection);
			setCacheDate(new Date());
			log.debug("Merchant is cached");
		} catch (Exception e) {
			throw e;
		} finally {
			Database.closeObject(connection);
		}
	}

	public void clear() {
		tariff.clear();
	}

	protected void loadTariff(Connection connection) throws Exception {
		PreparedStatement stmtConfig = null;
		ResultSet rsConfig = null;
		try 
		{
			String sql = "select * from charggw_tariff_plan order by tariff_plan_id desc";
			stmtConfig  = connection.prepareStatement(sql);
			rsConfig = stmtConfig.executeQuery();
			while(rsConfig.next())
			{
				log.debug(String.format("Caching tariff_plan rule for %s ...", rsConfig.getString("a_number")));
				TariffEntry tariffE = new TariffEntry(rsConfig.getLong("tariff_plan_id"), rsConfig.getString("a_number"));
				tariffE.setTariff_id(rsConfig.getLong("tariff_plan_id"));
				tariffE.setA_number(rsConfig.getString("a_number"));
				tariffE.setB_number(rsConfig.getString("b_number"));
				tariffE.setAmount(rsConfig.getInt("amount"));
				tariffE.setIs_Use(rsConfig.getBoolean("is_use"));
				tariffE.setValid_Period(rsConfig.getInt("valid_period"));
				tariffE.setCurrency(rsConfig.getString("currency"));
				tariffE.setUpdate_Time(rsConfig.getDate("updateTime"));
				tariffE.setDescription(rsConfig.getString("description"));
				tariffE.setCharge_Act(rsConfig.getInt("charge_act"));
				
				log.debug(String.format("Tariff_plan rule %s is cached", rsConfig.getString("a_number")));
				
			}
		}
		catch (Exception e) 
		{
			throw e;
		}
		finally {
			Database.closeObject(rsConfig);
			Database.closeObject(stmtConfig);
		}

	}

	public BinaryIndex getTariff() {
		return tariff;
	}

	public void setTariff(BinaryIndex tariff) {
		this.tariff = tariff;
	}

	public Date getCacheDate() {
		return cacheDate;
	}

	public void setCacheDate(Date cacheDate) {
		this.cacheDate = cacheDate;
	}
	
	public TariffEntry getTariff(String a_number) throws Exception
	{
		if(a_number.equals(""))
		{
			return null;
		}
		
		IndexNode result = tariff.getByKey(a_number);
		
		if(result == null)
		{
			throw new AppException(Constants.ERROR_TARIFF_NOT_FOUND);
		}
		
		return (TariffEntry) result;
	}
	
	public TariffEntry getTariff(long tariff_plan_id) throws Exception
	{
		if(tariff_plan_id == Constants.DEFAULT_ID)
		{
			return null;
		}
		
		IndexNode result = tariff.getById(tariff_plan_id);
		
		if(result == null)
		{
			throw new AppException(Constants.ERROR_TARIFF_NOT_FOUND);
		}
		
		return (TariffEntry) result;
	}
	
	private static Logger log = Logger.getLogger(TariffCache.class);
}
