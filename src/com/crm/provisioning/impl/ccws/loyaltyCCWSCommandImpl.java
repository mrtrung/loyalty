package com.crm.provisioning.impl.ccws;

import java.util.Calendar;
import java.util.Date;

import com.comverse_in.prepaid.ccws.BalanceEntity;
import com.comverse_in.prepaid.ccws.BalanceEntityBase;
import com.comverse_in.prepaid.ccws.SubscriberEntity;
import com.crm.kernel.message.Constants;
import com.crm.marketing.cache.CampaignEntry;
import com.crm.marketing.cache.CampaignFactory;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.message.VNMMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.provisioning.util.CommandUtil;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.crm.util.StringPool;
import com.crm.util.StringUtil;

public class loyaltyCCWSCommandImpl extends CCWSCommandImpl{
	
	public VNMMessage modifyBalance(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception
	{
		CCWSConnection connection = null;

		BalanceEntityBase[] balances = null;

		String isdn = CommandUtil.addCountryCode(request.getIsdn());

		VNMMessage result = CommandUtil.createVNMMessage(request);
		
		ProductEntry productEntry = ProductFactory.getCache().getProduct(result.getProductId());

		String balanceModify = productEntry.getParameters().getString("modifyBalance", "PROMOTION_60");
		
		if (instance.getDebugMode().equals("depend"))
		{
			simulation(instance, provisioningCommand, result);
		}
		else
		{
			try
			{
				request.setCause("success");
				
				ProductEntry product = ProductFactory.getCache().getProduct(
						result.getProductId());

				SubscriberEntity subscriber = getSubscriber(instance, result);
				
				//PROMOTION_60,Core,PROMOTION
				String[] addBalances = StringUtil.toStringArray(
						result.getParameters().getString("balanceModify", "PROMOTION_60"), StringPool.COMMA);
				
				double balanceAmount =  result.getSubscriberRetrieve().getSubscriberData().getBalances().getBalance()[14].getAvailableBalance();
				
				Calendar expiredDateE = result.getSubscriberRetrieve().getSubscriberData().getBalances().getBalance()[14].getAccountExpiration();

				Date dateExpiredDate = expiredDateE.getTime();
				
				result.getParameters().setDouble("balanceAmount", balanceAmount);
				result.getParameters().setDate("expiredDate", dateExpiredDate, "dd/MM/yyyy HH:mm:ss");
				
				Date now = new Date();
				
				Calendar activeDate = Calendar.getInstance();

				balances = new BalanceEntityBase[addBalances.length];

				result.setRequestValue("balances.premodified.activeDate",
						subscriber.getDateEnterActive().getTime());
				
				for (int j = 0; j < addBalances.length; j++)
				{
					String balanceName = balanceModify;

					String prefix = "balance." + result.getActionType() + "."
							+ balanceName;

					// get current balance
					BalanceEntity balance = CCWSConnection.getBalance(
							subscriber, balanceName);

					// save original balance status before add
					result.setRequestValue("balances." + balanceName
							+ ".premodified.expirationDate", balance
							.getAccountExpiration().getTime());
					result.setRequestValue("balances." + balanceName
							+ ".premodified.amount", balance.getBalance());

					// calculate expiration date for this balance
					Calendar maxExpiredDate = Calendar.getInstance();
					maxExpiredDate.setTime(now);
					maxExpiredDate.set(Calendar.HOUR_OF_DAY, 23);
					maxExpiredDate.set(Calendar.MINUTE, 59);
					maxExpiredDate.set(Calendar.SECOND, 59);
					
					int maxDays = result.getParameters().getInteger(
							prefix + ".maxDays");
					maxExpiredDate.add(Calendar.DATE, maxDays);
					Calendar expiredDate = balance.getAccountExpiration();
					
					if (product.isSubscription())
					{
						SubscriberProduct subProduct = SubscriberProductImpl.getUnterminated(
								result.getIsdn(), request.getProductId());
						if (subProduct != null)
						{
							expiredDate.setTime(subProduct.getExpirationDate());
						}
					}
					
					int expireTime = product.getParameters().getInteger(
							prefix + ".days");
					
					if (result.getCampaignId() != Constants.DEFAULT_ID)
					{
						CampaignEntry campaign = CampaignFactory.getCache()
								.getCampaign(result.getCampaignId());

						if ((campaign != null))
						{
							expireTime = campaign.getSchedulePeriod();
						}
					}
					
					String setExpFlg = product.getParameters().getString(
							prefix + ".resetexpiredate", "false");

					if (setExpFlg.equals("true"))
					{
						expireTime--;
						expiredDate.setTime(now);
					}
					
					boolean truncExpire = Boolean.parseBoolean(product.getParameter("TruncExpireDate", "true"));
					if (truncExpire)
					{
						int expireHour = product.getParameters().getInteger(
								prefix + ".expire.hour", 23);
						int expireMinute = product.getParameters().getInteger(
								prefix + ".expire.minute", 59);
						int expireSecond = product.getParameters().getInteger(
								prefix + ".expire.second", 59);
	
						expiredDate.set(Calendar.HOUR_OF_DAY, expireHour);
						expiredDate.set(Calendar.MINUTE, expireMinute);
						expiredDate.set(Calendar.SECOND, expireSecond);
					}

					if (expireTime > 0)
					{
						expiredDate.add(Calendar.DATE, expireTime);
					}

					if ((maxDays > 0) && expiredDate.after(maxExpiredDate))
					{
						expiredDate = maxExpiredDate;
					}

					if ((maxDays > 0) && expiredDate.after(activeDate))
					{
						activeDate = expiredDate;
					}
					
					int amount = result.getParameters().getInt("Amount");
					
					balances[j] = new BalanceEntityBase();

					balances[j].setBalanceName(balanceName);

					// Need to check accumulate or reset account DuyMB.
					String accumulateFlg = productEntry.getParameters().getString(
							prefix + ".accumulate", "false");
					if (accumulateFlg.equals("true"))
					{
						balances[j].setBalance(balance.getBalance() + amount);
					}
					else
					{
						balances[j].setBalance(amount);
					}
					
					balances[j].setAccountExpiration(expiredDate);

					// Add response value for writing log file
					result.setResponseValue(balances[j].getBalanceName()
							+ ".amount",
							StringUtil.format(balances[j].getBalance(), "#"));

					result.setResponseValue(balances[j].getBalanceName()
							+ ".expireDate", StringUtil.format(balances[j]
							.getAccountExpiration().getTime(),
							Constants.DATE_FORMAT));
				}

				// String comment = product.getIndexKey();
				String comment = product.getParameter(
						"mtrComment." + product.getAlias() + "."
								+ request.getActionType(), "Comment MTR"
								+ product.getAlias());

				// modify balances
				if (balances.length > 0)
				{
					connection = (CCWSConnection) instance
							.getProvisioningConnection();
					
					BalanceEntityBase[] balancesModify = new BalanceEntityBase[balances.length + 1];
					//Sync expire Core with expire GPRS of some COS
					String[] syncCOSList = product.getParameter("SyncCOSList", "").split(",");
					
					if (syncCOSList.length > 0)
					{
						for (int j=0; j<balances.length; j++)
						{	
							balancesModify[j] = balances[j];
							
							BalanceEntity core = CCWSConnection.getBalance(subscriber,
									CCWSConnection.CORE_BALANCE);
							
							if (balances[j].getBalanceName().equals("GPRS")
									&& balances[j].getAccountExpiration().after(core.getAccountExpiration()))
							{
								for (int i = 0; i < syncCOSList.length; i++)
								{
									if (subscriber.getCOSName().trim()
											.equalsIgnoreCase(syncCOSList[i].trim()))
									{
										balancesModify[balances.length] = new BalanceEntityBase();
										balancesModify[balances.length].setBalanceName("Core");
										balancesModify[balances.length].setBalance(core.getBalance() - result.getPrice());
										balancesModify[balances.length].setAccountExpiration(balances[j].getAccountExpiration());
										
										String strLog = "Acount Expiration: " + balances[j].getAccountExpiration() + "Balance Name: " + balances[j].getBalanceName() + "Amount: "+ balances[j].getBalance();
										
										instance.debugMonitor(strLog);
									}
								}
							}
						}
					}

					long sessionId = setRequest(instance, result,
							"com.comverse_in.prepaid.ccws.ServiceSoapStub.modifySubscriber:"
									+ getLogBalances(balancesModify, isdn));
					connection.setBalance(isdn, balancesModify, activeDate, comment);
					setResponse(instance, result, Constants.SUCCESS, sessionId);
				}
			}
			catch (Exception error)
			{
				processError(instance, provisioningCommand, result, error);
			}
			finally
			{
				instance.closeProvisioningConnection(connection);
			}
		}

		return result;
	}	
	
	//rollback
	public VNMMessage unModifyBalance(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request) throws Exception
	
	{
		CCWSConnection connection = null;

		BalanceEntityBase[] balances = null;

		String isdn = CommandUtil.addCountryCode(request.getIsdn());

		VNMMessage result = CommandUtil.createVNMMessage(request);
		
		ProductEntry productEntry = ProductFactory.getCache().getProduct(result.getProductId());

		String balanceModify = productEntry.getParameters().getString("modifyBalance", "PROMOTION");

		if (instance.getDebugMode().equals("depend"))
		{
			simulation(instance, provisioningCommand, result);
		}
		else
		{
			try
			{
				request.setCause("error");
				
				ProductEntry product = ProductFactory.getCache().getProduct(
						result.getProductId());

				SubscriberEntity subscriber = getSubscriber(instance, result);
				
				//PROMOTION_60,Core,PROMOTION
				String[] addBalances = StringUtil.toStringArray(
						result.getParameters().getString("balanceModify", "PROMOTION_60"), StringPool.COMMA);

				Date now = new Date();

				balances = new BalanceEntityBase[addBalances.length];

				Calendar activeDate = Calendar.getInstance();

				result.setRequestValue("balances.premodified.activeDate",
						subscriber.getDateEnterActive().getTime());
				
				for (int j = 0; j < addBalances.length; j++)
				{
					String balanceName = balanceModify;

					String prefix = "balance." + result.getActionType() + "."
							+ balanceName;

					// get current balance
					BalanceEntity balance = CCWSConnection.getBalance(
							subscriber, balanceName);

					// save original balance status before add
					result.setRequestValue("balances." + balanceName
							+ ".premodified.expirationDate", balance
							.getAccountExpiration().getTime());
					result.setRequestValue("balances." + balanceName
							+ ".premodified.amount", balance.getBalance());

					// calculate expiration date for this balance
					Calendar maxExpiredDate = Calendar.getInstance();

					maxExpiredDate.setTime(now);
					
					maxExpiredDate.set(Calendar.HOUR_OF_DAY, 23);
					maxExpiredDate.set(Calendar.MINUTE, 59);
					maxExpiredDate.set(Calendar.SECOND, 59);

					int maxDays = result.getParameters().getInteger(
							prefix + ".maxDays");

					maxExpiredDate.add(Calendar.DATE, maxDays);
					
					int balanceAmount = (int) result.getParameters().getDouble("balanceAmount", 0);

					Date expiredDateE = result.getParameters().getDate("expiredDate", "dd/MM/yyyy HH:mm:ss");
					Calendar expiredDate = Calendar.getInstance();
					expiredDate.setTime(expiredDateE);
					
					boolean truncExpire = Boolean.parseBoolean(product.getParameter("TruncExpireDate", "true"));
					if (truncExpire)
					{
						int expireHour = product.getParameters().getInteger(
								prefix + ".expire.hour", 23);
						int expireMinute = product.getParameters().getInteger(
								prefix + ".expire.minute", 59);
						int expireSecond = product.getParameters().getInteger(
								prefix + ".expire.second", 59);
	
						expiredDate.set(Calendar.HOUR_OF_DAY, expireHour);
						expiredDate.set(Calendar.MINUTE, expireMinute);
						expiredDate.set(Calendar.SECOND, expireSecond);
					}

					balances[j] = new BalanceEntityBase();

					balances[j].setBalanceName(balanceName);

					balances[j].setBalance(balanceAmount);
					
					balances[j].setAccountExpiration(expiredDate);

					// Add response value for writing log file
					result.setResponseValue(balances[j].getBalanceName()
							+ ".amount",
							StringUtil.format(balances[j].getBalance(), "#"));

					result.setResponseValue(balances[j].getBalanceName()
							+ ".expireDate", StringUtil.format(balances[j]
							.getAccountExpiration().getTime(),
							Constants.DATE_FORMAT));
				}

				// String comment = product.getIndexKey();
				String comment = product.getParameter(
						"mtrComment." + product.getAlias() + "."
								+ request.getActionType(), "Comment MTR"
								+ product.getAlias());

				// modify balances
				if (balances.length > 0)
				{
					connection = (CCWSConnection) instance
							.getProvisioningConnection();
					
					BalanceEntityBase[] balancesModify = new BalanceEntityBase[balances.length + 1];
					//Sync expire Core with expire GPRS of some COS
					String[] syncCOSList = product.getParameter("SyncCOSList", "").split(",");
					
					if (syncCOSList.length > 0)
					{
						for (int j=0; j<balances.length; j++)
						{	
							balancesModify[j] = balances[j];
							
							BalanceEntity core = CCWSConnection.getBalance(subscriber,
									CCWSConnection.CORE_BALANCE);
							
							if (balances[j].getBalanceName().equals("GPRS")
									&& balances[j].getAccountExpiration().after(core.getAccountExpiration()))
							{
								for (int i = 0; i < syncCOSList.length; i++)
								{
									if (subscriber.getCOSName().trim()
											.equalsIgnoreCase(syncCOSList[i].trim()))
									{
										balancesModify[balances.length] = new BalanceEntityBase();
										balancesModify[balances.length].setBalanceName("Core");
										balancesModify[balances.length].setBalance(core.getBalance() - result.getPrice());
										
										balancesModify[balances.length].setAccountExpiration(balances[j].getAccountExpiration());
										
										String strLog = "Acount Expiration: " + balances[j].getAccountExpiration() + "Balance Name: " + balances[j].getBalanceName() + "Amount: "+ balances[j].getBalance();
										
										instance.debugMonitor(strLog);
									}
								}
							}
						}
					}

					long sessionId = setRequest(instance, result,
							"com.comverse_in.prepaid.ccws.ServiceSoapStub.modifySubscriber:"
									+ getLogBalances(balancesModify, isdn));
					connection.setBalance(isdn, balancesModify, activeDate, comment);
					setResponse(instance, result, Constants.ERROR, sessionId);
				}
			}
			catch (Exception error)
			{
				processError(instance, provisioningCommand, result, error);
			}
			finally
			{
				instance.closeProvisioningConnection(connection);
			}
		}

		return result;
	
	}

}
