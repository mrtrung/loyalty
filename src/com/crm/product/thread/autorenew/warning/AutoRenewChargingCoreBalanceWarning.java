package com.crm.product.thread.autorenew.warning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.crm.kernel.sql.Database;
import com.crm.util.StringUtil;

public class AutoRenewChargingCoreBalanceWarning extends AbtractAutoRenewWarning
{

	@Override
	public void buildContent(String[] argContent) throws Exception
	{
		// INIT CONNECTION
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			connection = Database.getConnection();
			stmt = connection.prepareStatement(sQLWarning);
			rs = stmt.executeQuery();

			long total = 0, autoRenewSuccess = 0, autoRenewError = 0, unrenewSuccess = 0, unrenewError = 0, remain = 0, overLimit = 0;

			argContent[0] = argContent[0].replace("~DATE~", StringUtil.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
			argContent[1] = argContent[1].replace("~DATE~", StringUtil.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
			while (rs.next())
			{
				String orderType = StringUtil.nvl(rs.getString(1), "").trim();
				String cause = StringUtil.nvl(rs.getString(2), "").trim();
				long value = rs.getLong(3);
				if (orderType.equals("") && cause.equals("not-process"))
				{
					remain = value;
				}
				else if (orderType.equals("unrenew") && cause.equals("success"))
				{
					unrenewSuccess = rs.getLong(3);
				}
				else if (orderType.equals("unrenew") && cause.equals("error"))
				{
					unrenewError = rs.getLong(3);
				}
				else if (orderType.equals("autorenew") && cause.equals("error"))
				{
					autoRenewError = rs.getLong(3);
				}
				else if (orderType.equals("autorenew") && cause.equals("success"))
				{
					autoRenewSuccess = rs.getLong(3);
				}
				else if (orderType.equals("") && cause.equals(""))
				{
					total = rs.getLong(3);
				}
				else if (orderType.equals("autorenew") && cause.equals("over-limit"))
				{
					overLimit = rs.getLong(3);
				}
			}

			argContent[0] = argContent[0].replace("~REMAIN~", formatNumber(remain));
			argContent[1] = argContent[1].replace("~REMAIN~", formatNumber(remain));

			argContent[0] = argContent[0].replace("~UNRENEW SUCCESS~", formatNumber(unrenewSuccess));
			argContent[1] = argContent[1].replace("~UNRENEW SUCCESS~", formatNumber(unrenewSuccess));

			argContent[0] = argContent[0].replace("~AUTO RENEW ERROR~", formatNumber(autoRenewError));
			argContent[1] = argContent[1].replace("~AUTO RENEW ERROR~", formatNumber(autoRenewError));

			argContent[0] = argContent[0].replace("~UNRENEW ERROR~", formatNumber(unrenewError));
			argContent[1] = argContent[1].replace("~UNRENEW ERROR~", formatNumber(unrenewError));

			argContent[0] = argContent[0].replace("~AUTO RENEW SUCCESS~", formatNumber(autoRenewSuccess));
			argContent[1] = argContent[1].replace("~AUTO RENEW SUCCESS~", formatNumber(autoRenewSuccess));

			argContent[0] = argContent[0].replace("~TOTAL~", formatNumber(total));
			argContent[1] = argContent[1].replace("~TOTAL~", formatNumber(total));

			argContent[0] = argContent[0].replace("~OVER LIMIT~", formatNumber(overLimit));
			argContent[1] = argContent[1].replace("~OVER LIMIT~", formatNumber(overLimit));
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			Database.closeObject(stmt);
			Database.closeObject(rs);
			Database.closeObject(connection);
		}
	}

}
