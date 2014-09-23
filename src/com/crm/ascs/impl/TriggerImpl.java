package com.crm.ascs.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.crm.ascs.net.Trigger;
import com.crm.ascs.net.TriggerActivation;
import com.crm.ascs.net.TriggerRecharge;
import com.crm.kernel.sql.Database;
import com.crm.util.DateUtil;

public class TriggerImpl
{
	public static void insertTrigger(Trigger trigger) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();
			insertTrigger(connection, trigger);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(connection);
		}
	}

	public static void insertTriggerRecharge(TriggerRecharge[] triggers) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();
			insertTriggerRecharge(connection, triggers);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(connection);
		}
	}

	public static void insertTriggerRecharge(Connection connection, TriggerRecharge[] triggers) throws Exception
	{
		if (triggers == null)
			return;
		if (triggers.length == 0)
			return;

		PreparedStatement stmtTrigger = null;
		try
		{
			connection.setAutoCommit(false);
			String SQL = "Insert into Recharge_Trigger "
					+ "		(ID, TRIGGER_NAME, MDN, COS, PREVIOUS_STATE, CURRENT_STATE"
					+ "		, FACE_VALUE, EXPIRE_DATE, BATCH_NUMBER, SERIAL_NUMBER, RECHARGE_DATE, ACTIVATION_DATE "
					+ "		, RECEIVE_DATE, LOAD_DATE, CONTENT, STATUS )"
					+ " Values "
					+ "		(trigger_seq.nextVal, ?, ?, ?, ?, ?"
					+ "		, ?, ?, ?, ?, ?, ? "
					+ "		, ?, sysdate, ?, ?) ";
			stmtTrigger = connection.prepareStatement(SQL);

			for (TriggerRecharge trigger : triggers)
			{
				stmtTrigger.setString(1, trigger.getType());
				stmtTrigger.setString(2, trigger.getIsdn());
				stmtTrigger.setString(3, trigger.getCosName());
				stmtTrigger.setString(4, trigger.getPreviousState());
				stmtTrigger.setString(5, trigger.getState());
				stmtTrigger.setDouble(6, trigger.getFaceValue());
				stmtTrigger.setTime(7, DateUtil.getTimeSQL(trigger.getExpireDate()));
				stmtTrigger.setInt(8, trigger.getBatch());
				stmtTrigger.setInt(9, trigger.getSerial());

				stmtTrigger.setTime(10, DateUtil.getTimeSQL(trigger.getRechargeDate()));
				stmtTrigger.setTime(11, DateUtil.getTimeSQL(trigger.getActivationDate()));
				stmtTrigger.setTime(12, DateUtil.getTimeSQL(trigger.getReceiveDate()));
				stmtTrigger.setString(13, trigger.getContent());
				stmtTrigger.setInt(14, trigger.getStatus());

				stmtTrigger.addBatch();
			}

			stmtTrigger.executeBatch();
			connection.commit();
		}
		catch (Exception e)
		{
			connection.rollback();
			throw e;
		}
		finally
		{
			Database.closeObject(stmtTrigger);
		}
	}

	public static void insertTriggerActivation(TriggerActivation[] triggers) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();
			insertTriggerActivation(connection, triggers);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(connection);
		}
	}

	public static void insertTriggerActivation(Connection connection, TriggerActivation[] triggers) throws Exception
	{
		if (triggers == null)
			return;
		if (triggers.length == 0)
			return;

		PreparedStatement stmtTrigger = null;
		try
		{
			String SQL = "Insert into Activation_Trigger "
					+ "		(ID, TRIGGER_NAME, MDN, COS, PREVIOUS_STATE, CURRENT_STATE"
					+ "		, CORE_BALANCE, EXPIRE_DATE, ACTIVATION_DATE, LOAD_DATE, RECEIVE_DATE, CONTENT, STATUS )"
					+ " Values "
					+ "		(trigger_seq.nextVal, ?, ?, ?, ?, ?"
					+ "		, ?, ?, ?, sysdate, ?, ?, ? )";

			connection.setAutoCommit(false);
			stmtTrigger = connection.prepareStatement(SQL);

			for (TriggerActivation trigger : triggers)
			{
				stmtTrigger.setString(1, trigger.getType());
				stmtTrigger.setString(2, trigger.getIsdn());
				stmtTrigger.setString(3, trigger.getCosName());
				stmtTrigger.setString(4, trigger.getPreviousState());
				stmtTrigger.setString(5, trigger.getState());
				stmtTrigger.setDouble(6, trigger.getCoreBalance());
				stmtTrigger.setTime(7, DateUtil.getTimeSQL(trigger.getExpireDate()));
				stmtTrigger.setTime(8, DateUtil.getTimeSQL(trigger.getActivationDate()));
				stmtTrigger.setTime(9, DateUtil.getTimeSQL(trigger.getReceiveDate()));
				stmtTrigger.setString(10, trigger.getContent());
				stmtTrigger.setInt(11, trigger.getStatus());

				stmtTrigger.addBatch();
			}

			stmtTrigger.executeBatch();
			connection.commit();
		}
		catch (Exception e)
		{
			connection.rollback();
			throw e;
		}
		finally
		{
			Database.closeObject(stmtTrigger);
		}
	}

	public static void insertTrigger(Connection connection, Trigger trigger) throws Exception
	{
		if (trigger instanceof TriggerRecharge)
			insertTriggerRecharge(connection, (TriggerRecharge) trigger);
		else if (trigger instanceof TriggerActivation)
			insertTriggerActivation(connection, (TriggerActivation) trigger);

	}

	private static void insertTriggerRecharge(Connection connection, TriggerRecharge trigger) throws Exception
	{
		PreparedStatement stmtTrigger = null;
		try
		{
			connection.setAutoCommit(true);
			long triggerId = Database.getSequence(connection, "trigger_seq");

			String SQL = "Insert into Recharge_Trigger "
					+ "		(ID, TRIGGER_NAME, MDN, COS, PREVIOUS_STATE, CURRENT_STATE"
					+ "		, FACE_VALUE, EXPIRE_DATE, BATCH_NUMBER, SERIAL_NUMBER, RECHARGE_DATE, ACTIVATION_DATE "
					+ "		, RECEIVE_DATE, LOAD_DATE, CONTENT, STATUS )"
					+ " Values "
					+ "		(?, ?, ?, ?, ?, ?"
					+ "		, ?, ?, ?, ?, ?, ? "
					+ "		, ?, sysdate, ?, ?) ";

			stmtTrigger = connection.prepareStatement(SQL);

			stmtTrigger.setLong(1, triggerId);
			stmtTrigger.setString(2, trigger.getType());
			stmtTrigger.setString(3, trigger.getIsdn());
			stmtTrigger.setString(4, trigger.getCosName());
			stmtTrigger.setString(5, trigger.getPreviousState());
			stmtTrigger.setString(6, trigger.getState());
			stmtTrigger.setDouble(7, trigger.getFaceValue());
			stmtTrigger.setTime(8, DateUtil.getTimeSQL(trigger.getExpireDate()));
			stmtTrigger.setInt(9, trigger.getBatch());
			stmtTrigger.setInt(10, trigger.getSerial());

			stmtTrigger.setTime(11, DateUtil.getTimeSQL(trigger.getRechargeDate()));
			stmtTrigger.setTime(12, DateUtil.getTimeSQL(trigger.getActivationDate()));
			stmtTrigger.setTime(13, DateUtil.getTimeSQL(trigger.getReceiveDate()));
			stmtTrigger.setString(14, trigger.getContent());
			stmtTrigger.setInt(15, trigger.getStatus());

			stmtTrigger.execute();

			trigger.setTriggerId(triggerId);

		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtTrigger);
		}

	}

	private static void insertTriggerActivation(Connection connection, TriggerActivation trigger) throws Exception
	{
		PreparedStatement stmtTrigger = null;
		try
		{
			connection.setAutoCommit(true);
			long triggerId = Database.getSequence(connection, "trigger_seq");

			String SQL = "Insert into Activation_Trigger "
					+ "		(ID, TRIGGER_NAME, MDN, COS, PREVIOUS_STATE, CURRENT_STATE"
					+ "		, CORE_BALANCE, EXPIRE_DATE, ACTIVATION_DATE, LOAD_DATE, RECEIVE_DATE, CONTENT, STATUS )"
					+ " Values "
					+ "		(?, ?, ?, ?, ?, ?"
					+ "		, ?, ?, ?, sysdate, ?, ?, ? )";

			stmtTrigger = connection.prepareStatement(SQL);

			stmtTrigger.setLong(1, triggerId);
			stmtTrigger.setString(2, trigger.getType());
			stmtTrigger.setString(3, trigger.getIsdn());
			stmtTrigger.setString(4, trigger.getCosName());
			stmtTrigger.setString(5, trigger.getPreviousState());
			stmtTrigger.setString(6, trigger.getState());
			stmtTrigger.setDouble(7, trigger.getCoreBalance());
			stmtTrigger.setTime(8, DateUtil.getTimeSQL(trigger.getExpireDate()));
			stmtTrigger.setTime(9, DateUtil.getTimeSQL(trigger.getActivationDate()));
			stmtTrigger.setTime(10, DateUtil.getTimeSQL(trigger.getReceiveDate()));
			stmtTrigger.setString(11, trigger.getContent());
			stmtTrigger.setInt(12, trigger.getStatus());

			stmtTrigger.execute();

			trigger.setTriggerId(triggerId);

		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtTrigger);
		}
	}

	public static void updateTrigger(Trigger trigger) throws Exception
	{
		Connection connection = null;

		try
		{
			connection = Database.getConnection();
			if (updateTrigger(connection, trigger) == 0)
				insertTrigger(connection, trigger);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(connection);
		}
	}

	public static int updateTrigger(Connection connection, Trigger trigger) throws Exception
	{
		if (trigger instanceof TriggerRecharge)
			return updateTriggerRecharge(connection, (TriggerRecharge) trigger);
		else if (trigger instanceof TriggerActivation)
			return updateTriggerActivation(connection, (TriggerActivation) trigger);
		else
			return 0;
	}

	private static int updateTriggerActivation(Connection connection, TriggerActivation trigger) throws Exception
	{
		PreparedStatement stmtTrigger = null;
		try
		{

			connection.setAutoCommit(true);
			String SQL = "Update Activation_Trigger "
					+ " Set "
					+ " ACTIVATION_DATE = ? , "
					+ " STATUS = ? , "
					+ " LOAD_DATE = sysdate "
					+ " Where ID = ?";

			stmtTrigger = connection.prepareStatement(SQL);

			stmtTrigger.setTime(1, DateUtil.getTimeSQL(trigger.getActivationDate()));
			stmtTrigger.setInt(2, trigger.getStatus());
			stmtTrigger.setLong(3, trigger.getTriggerId());

			return stmtTrigger.executeUpdate();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtTrigger);
		}
	}

	private static int updateTriggerRecharge(Connection connection, TriggerRecharge trigger) throws Exception
	{
		PreparedStatement stmtTrigger = null;
		try
		{

			connection.setAutoCommit(true);
			String SQL = "Update Recharge_Trigger "
					+ " Set "
					+ " PREVIOUS_STATE = ?, "
					+ " RECHARGE_DATE = ?, "
					+ " ACTIVATION_DATE = ? , "
					+ " STATUS = ? , "
					+ " LOAD_DATE = sysdate "
					+ " Where ID = ?";

			stmtTrigger = connection.prepareStatement(SQL);

			stmtTrigger.setString(1, trigger.getPreviousState());
			stmtTrigger.setTime(2, DateUtil.getTimeSQL(trigger.getRechargeDate()));
			stmtTrigger.setTime(3, DateUtil.getTimeSQL(trigger.getActivationDate()));
			stmtTrigger.setInt(4, trigger.getStatus());
			stmtTrigger.setLong(5, trigger.getTriggerId());

			return stmtTrigger.executeUpdate();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			Database.closeObject(stmtTrigger);
		}
	}
}
