/**
 * 
 */
package com.crm.cgw.tariff.cache;

import java.util.Date;

import org.apache.log4j.Logger;
import com.crm.util.DateUtil;

/**
 * @author hungdt
 *
 */
public class TariffFactory {
	private static TariffCache cache = null;
	private static Date cacheDate = null;
	
	public TariffFactory()
	{
		super();
	}
	
	public synchronized static void clear() throws Exception
	{
		if (cache != null)
		{
			cache.clear();
		}
		
		cacheDate	= null;
	}
	
	public synchronized static TariffCache loadCache(Date date) throws Exception
	{
		try
		{
			date = DateUtil.trunc(date);

			log.debug("Caching product information for date: " + date);

			if (cache != null)
			{
				cache.clear();
			}
			else
			{
				cache = new TariffCache();
			}
			cache.loadCache();

			cacheDate = date;

			log.debug("Cached product information for date: " + cacheDate);
		}
		catch (Exception e)
		{
			cache = null;
			cacheDate = null;

			throw e;
		}

		return cache;
	}

	public synchronized static TariffCache getCache(Date date) throws Exception
	{
		boolean reload = true;

		try
		{
			date = DateUtil.trunc(date);

			if (cache == null)
			{
				cache = new TariffCache();
			}
			else if ((cacheDate == null) || !cacheDate.equals(date))
			{
				cache.clear();
			}
			else
			{
				reload = false;
			}

			if (reload)
			{
				log.debug("Caching product information for date: " + date);

				cache.loadCache();

				cacheDate = date;

				log.debug("Cached product information for date: " + cacheDate);
			}
		}
		catch (Exception e)
		{
			cache = null;
			cacheDate = null;

			throw e;
		}

		return cache;
	}

	public static TariffCache getCache() throws Exception
	{
		return getCache(new Date());
	}

	private static Logger	log	= Logger.getLogger(TariffFactory.class);
	
}
