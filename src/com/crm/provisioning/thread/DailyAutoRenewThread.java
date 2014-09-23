package com.crm.provisioning.thread;
import java.util.Vector;
import com.fss.thread.ParameterType;
import com.fss.util.AppException;


public class DailyAutoRenewThread extends ProvisioningThread
{
		private String _balanceName = "1DAY_VOICE";
		private int _balanceAmount = 21600;
		private int _retryTime = 3;
		private int _balanceExpiration = 1;
		private String _mtrComment =""; 

		// //////////////////////////////////////////////////////
		// Override
		// //////////////////////////////////////////////////////
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Vector getParameterDefinition()
		{
			Vector vtReturn = new Vector();

			vtReturn.addElement(createParameterDefinition("BalanceAmount", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("BalanceExpiration", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));	
			vtReturn.addElement(createParameterDefinition("RetryTime", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("BalanceName", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));
			vtReturn.addElement(createParameterDefinition("MtrComment", "",
					ParameterType.PARAM_TEXTBOX_MAX, "100"));			

			vtReturn.addAll(super.getParameterDefinition());

			return vtReturn;
		}

		// //////////////////////////////////////////////////////
		// Override
		// //////////////////////////////////////////////////////
		public void fillParameter() throws AppException
		{
			try
			{
				super.fillParameter();

				set_balanceName(loadMandatory("BalanceName"));
				set_retryTime(loadInteger("RetryTime"));
				set_blanceAmout(loadInteger("BalanceAmount"));
				set_balanceExpiration(loadInteger("BalanceExpiration"));
				set_mtrComment(loadMandatory("MtrComment"));
			}
			catch (AppException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}		
		public String get_balanceName() {
			return _balanceName;
		}

		public void set_balanceName(String _balanceName) {
			this._balanceName = _balanceName;
		}

		public int get_balanceAmount() {
			return _balanceAmount;
		}

		public void set_blanceAmout(int _balanceAmount) {
			this._balanceAmount = _balanceAmount;
		}

		public int get_retryTime() {
			return _retryTime;
		}

		public void set_retryTime(int _retryTime) {
			this._retryTime = _retryTime;
		}

		public int get_balanceExpiration() {
			return _balanceExpiration;
		}

		public void set_balanceExpiration(int _balanceExpiration) {
			this._balanceExpiration = _balanceExpiration;
		}
		public String get_mtrComment() {
			return _mtrComment;
		}

		public void set_mtrComment(String _mtrComment) {
			this._mtrComment = _mtrComment;
		}		
}

