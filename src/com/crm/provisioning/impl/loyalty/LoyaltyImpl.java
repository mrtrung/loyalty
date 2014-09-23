package com.crm.provisioning.impl.loyalty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.crm.kernel.message.Constants;
import com.crm.kernel.sql.Database;
import com.crm.product.cache.ProductEntry;
import com.crm.product.cache.ProductFactory;
import com.crm.provisioning.cache.ProvisioningCommand;
import com.crm.provisioning.impl.CommandImpl;
import com.crm.provisioning.message.CommandMessage;
import com.crm.provisioning.message.VNMMessage;
import com.crm.provisioning.thread.CommandInstance;
import com.crm.provisioning.util.CommandUtil;
import com.crm.provisioning.util.ResponseUtil;
import com.crm.subscriber.bean.SubscriberBalance;
import com.crm.subscriber.bean.SubscriberProduct;
import com.crm.subscriber.impl.SubscriberBalanceImpl;
import com.crm.subscriber.impl.SubscriberProductImpl;
import com.fss.util.AppException;

public class LoyaltyImpl extends CommandImpl {

	public static String NOT_REGISTERED = "not-registered";

	public CommandMessage getBalanceAmount(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception {

		SubscriberBalance subscriberBalance = SubscriberBalanceImpl
				.getBalance(request.getIsdn());

		SubscriberProduct subscriberProduct = SubscriberProductImpl
				.getProduct(request.getSubProductId());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {

			Date registerDateE = subscriberProduct.getRegisterDate();
			String registerDate = sdf.format(registerDateE);

			int balanceAmount = subscriberBalance.getBalanceAmount();
			int cumulationAmount = subscriberBalance.getCumulationAmount();

			request.setResponseValue(ResponseUtil.SERVICE_START_DATE,
					registerDate);
			request.setResponseValue(ResponseUtil.SERVICE_BALANCE,
					cumulationAmount);
			request.setResponseValue(ResponseUtil.SERVICE_AMOUNT_REMAIN,
					balanceAmount);

		} catch (Throwable e) {

			throw new AppException(Constants.ERROR_BALANCE_NOT_FOUND);
		}

		return request;
	}

	public CommandMessage validateE(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Throwable {

		try {

			ProductEntry productEntry = ProductFactory.getCache().getProduct(
					request.getProductId());

			SubscriberProduct subscriberProduct = SubscriberProductImpl
					.getProduct(request.getSubProductId());

			SubscriberBalance subscriberBalance = SubscriberBalanceImpl
					.getBalance(request.getIsdn());

			boolean isCheck = (subscriberProduct == null);
			boolean isCheckBalance = (subscriberBalance == null);

			if (isCheck) {
				request.setCause(Constants.ERROR_MEMBER_NOT_FOUND);

				return request;
			}

			if (isCheckBalance) {
				request.setCause(Constants.ERROR_BALANCE_NOT_FOUND);

				return request;
			}

			Date nowDate = new Date();
			Date expireDate = subscriberBalance.getExpirationDate();

			boolean compDate = ((long) expireDate.getTime() < (long) nowDate
					.getTime());

			if (compDate) {
				request.setCause(Constants.ERROR_EXPIRED);
				return request;
			}
			// using for DOI DIEM
			String isCheckMember = productEntry.getParameters().getString(
					request.getActionType() + ".isCheckMember", "true");

			if (isCheckMember == "true") {
				// check invalid number
				String strPoints = request.getParameters().getString(
						"sms.params[" + 0 + "]");
				try {
					Integer.parseInt(strPoints);
				} catch (Throwable e) {
					request.setCause(Constants.ERROR_INVALID_SYNTAX);

					return request;
				}
				// min Points = 50
				int redeemPoints = Integer.parseInt(strPoints);

				int minPoints = productEntry.getParameters().getInteger(
						"minPoints", 0);

				if (redeemPoints < minPoints) {
					request.setCause(Constants.ERROR_MIN_POINT);

					return request;
				}

				int validDays = productEntry.getParameters().getInteger(
						"validDays", 90);

				// is not enough registerDate
				Calendar timeCompare = Calendar.getInstance();

				Date registerDate = subscriberProduct.getRegisterDate();

				double now = timeCompare.getTimeInMillis() / (1000 * 60);
				double registerDay = registerDate.getTime() / (1000 * 60)
						+ validDays * 24 * 60;

				if ((registerDay - now) > 0) {
					request.setCause(Constants.ERROR_INVALID_ACTIVE_DATE);
					return request;
				}
				// set Amount
				int factor = productEntry.getParameters().getInteger("factor",
						10);

				request.getParameters().setDouble("Amount",
						redeemPoints * factor);
				request.getParameters().setString("modifyBalance",
						"PROMOTION_60");

				// check isEnoughPoint to redeem
				double balanceAmount = subscriberBalance.getBalanceAmount();

				if (redeemPoints > (int) balanceAmount)

					request.setCause(Constants.ERROR_NOT_ENOUGH_MONEY);
			}

		} catch (Exception e) {
			throw e;
		}

		return request;
	}

	public VNMMessage redeem(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception {
		VNMMessage result = CommandUtil.createVNMMessage(request);

		Connection connection = Database.getConnection();

		PreparedStatement stmtBalance = null;

		String strPoints = request.getParameters().getString(
				"sms.params[" + 0 + "]");

		ProductEntry productEntry = ProductFactory.getCache().getProduct(
				request.getProductId());

		int factor = productEntry.getParameters().getInteger("factor", 25);

		try {

			int redeemPoints = Integer.parseInt(strPoints);

			result.setResponseValue(ResponseUtil.SERVICE_AMOUNT, redeemPoints);

			result.setResponseValue(ResponseUtil.SERVICE_MONEY_REDEEM, factor
					* redeemPoints);

			String uSQL = "update subscriberbalance set balanceAmount = nvl(balanceAmount, 0) - ? where isdn = ? and nvl(status, 3) = ?";

			stmtBalance = connection.prepareStatement(uSQL);

			stmtBalance.setDouble(1, Integer.parseInt(strPoints));

			stmtBalance.setString(2, result.getIsdn());

			stmtBalance.setInt(3, Constants.DEFAULT_STATUS);

			stmtBalance.execute();

		} catch (Exception error) {

			Database.rollback(connection);

			processError(instance, provisioningCommand, request, error);

		} finally {
			Database.closeObject(stmtBalance);
			Database.closeObject(connection);
		}

		return result;
	}

	public VNMMessage unRedeem(CommandInstance instance,
			ProvisioningCommand provisioningCommand, CommandMessage request)
			throws Exception {
		VNMMessage result = CommandUtil.createVNMMessage(request);

		Connection connection = Database.getConnection();

		PreparedStatement stmtBalance = null;

		String strPoints = request.getParameters().getString(
				"sms.params[" + 0 + "]");

		try {
			int redeemPoints = Integer.parseInt(strPoints);

			result.setResponseValue(ResponseUtil.SERVICE_AMOUNT, redeemPoints);

			String uSQL = "update subscriberbalance set balanceAmount = nvl(balanceAmount, 0) + ? where isdn = ? and nvl(status, 3) = ? ";

			stmtBalance = connection.prepareStatement(uSQL);

			stmtBalance.setDouble(1, Integer.parseInt(strPoints));

			stmtBalance.setString(2, result.getIsdn());

			stmtBalance.setInt(3, Constants.DEFAULT_STATUS);

			stmtBalance.execute();

		} catch (Exception error) {

			Database.rollback(connection);

			processError(instance, provisioningCommand, request, error);

		} finally {

			Database.closeObject(stmtBalance);

			Database.closeObject(connection);
		}

		return result;
	}
}